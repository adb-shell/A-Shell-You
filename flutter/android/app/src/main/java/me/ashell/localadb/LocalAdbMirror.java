package me.ashell.localadb;

import android.content.Context;
import android.os.SystemClock;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbStream;

/** One device-owned scrcpy server, isolated from shared shell and product capture services. */
final class LocalAdbMirror {
    interface ShellReady { boolean get(); }
    private static final String ASSET = "adb-mirror/scrcpy-server-v5.0.1";
    private static final String HASH = "764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536";
    private static final int MAX_PACKET = 2 * 1024 * 1024;
    private final Context context;
    private final ShellReady shellReady;
    private final ScheduledExecutorService guard = Executors.newSingleThreadScheduledExecutor(
            task -> new Thread(task, "ashell-adb-video-guard"));
    private Run active;
    private long generation;
    private boolean nativeReady;

    private static native void register(LocalAdbManager manager);
    private static native void state(long generation, String phase, String error);
    private static native void frame(long generation, int width, int height,
                                     boolean config, boolean keyframe, byte[] data);

    LocalAdbMirror(Context context, ShellReady shellReady) {
        this.context = context.getApplicationContext();
        this.shellReady = shellReady;
    }

    synchronized void attach(LocalAdbManager manager) {
        try {
            System.loadLibrary("ashell");
            register(manager);
            nativeReady = true;
        } catch (LinkageError ignored) { }
    }

    synchronized void unavailable() {
        if (active == null) publish("error", "请先完成 ADB 连接并验证 Shell，等待当前 ADB 操作结束。");
    }

    synchronized void start(int port) {
        if (active != null || !nativeReady) return;
        if (!shellReady.get() || port <= 0 || port > 65535) { unavailable(); return; }
        generation++;
        Run run = new Run(generation, port);
        active = run;
        publish("starting", "");
        run.watchdog = guard.scheduleWithFixedDelay(this::checkHealth, 1, 1, TimeUnit.SECONDS);
        run.thread = new Thread(() -> capture(run), "ashell-adb-video");
        run.thread.start();
    }

    synchronized void stop(String reason) {
        Run run = active;
        if (run == null) { publish("idle", reason); return; }
        if (!run.cancelled.compareAndSet(false, true)) return;
        run.failure = reason;
        publish("stopping", reason);
        // Abort only this private ADB transport; its close unblocks every pending stream.
        run.client.abort();
        if (run.thread != null) run.thread.interrupt();
    }

    private synchronized void checkHealth() {
        if (active == null || active.cancelled.get()) return;
        if (!shellReady.get()) stop("ADB Shell 已断开，投屏已停止。");
        else if (SystemClock.elapsedRealtime() - active.lastPacket > 20000)
            stop("ADB 投屏启动或画面接收超时，请重试。");
    }

    private void publish(String phase, String error) {
        if (nativeReady) state(generation, phase, error);
    }

    private void capture(Run run) {
        try {
            run.client.connect(run.port, run.cancelled);
            String scid = String.format(Locale.US, "%08x", new SecureRandom().nextInt() & 0x7fffffff);
            // A fixed module-owned path bounds leftovers even if upload is interrupted.
            String path = "/data/local/tmp/ashell-scrcpy-5.0.1.jar";
            push(context, run.client, run.cancelled, path);
            // All arguments are fixed or generated locally. No peer-controlled shell interpolation.
            String command = "CLASSPATH=" + path + " app_process / com.genymobile.scrcpy.Server 5.0.1"
                    + " scid=" + scid + " tunnel_forward=true audio=false control=false"
                    + " video_codec=h264 max_size=1280 max_fps=20 video_bit_rate=2000000"
                    + " video_codec_options=i-frame-interval=1,repeat-previous-frame-after:long=100000"
                    + " send_device_meta=false send_dummy_byte=false power_on=false"
                    + " cleanup=true log_level=error";
            try (AdbStream process = run.client.openMirrorStream("shell,v2,raw:sh")) {
                byte[] script = ("trap 'rm -f " + path + "' EXIT; " + command + "\n").getBytes(StandardCharsets.US_ASCII);
                byte[] input = ByteBuffer.allocate(script.length + 10).order(ByteOrder.LITTLE_ENDIAN)
                        .put((byte) 0).putInt(script.length).put(script).put((byte) 4).putInt(0).array();
                process.write(input, 0, input.length);
                process.flush();
                Thread output = new Thread(() -> drainProcess(run, process), "ashell-scrcpy-output");
                output.start();
                AdbStream video = null;
                long deadline = SystemClock.elapsedRealtime() + 10000;
                while (!run.cancelled.get() && SystemClock.elapsedRealtime() < deadline) {
                    try { video = run.client.openMirrorStream("localabstract:scrcpy_" + scid); break; }
                    catch (IOException e) { Thread.sleep(150); }
                }
                if (video == null) throw new IOException("Server socket unavailable");
                try (AdbStream stream = video) { readVideo(run, stream); }
                finally { output.interrupt(); }
            }
        } catch (Exception | LinkageError e) {
            if (!run.cancelled.get()) run.failure = "ADB 投屏失败，请确认无线调试仍开启并重新连接。设备编码器可能不支持当前采集。";
        } finally {
            if (run.watchdog != null) run.watchdog.cancel(false);
            // Clear interruption before joining the private ADB reader during close.
            Thread.interrupted();
            run.client.disconnect();
            synchronized (this) {
                if (active == run) {
                    active = null;
                    publish(run.failure.isEmpty() ? "idle" : "error", run.failure);
                }
            }
        }
    }

    private void drainProcess(Run run, AdbStream stream) {
        try {
            byte[] header = new byte[5];
            byte[] discard = new byte[4096];
            while (!run.cancelled.get()) {
                readFully(run, stream, header);
                int channel = header[0] & 255;
                int size = ByteBuffer.wrap(header, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                if (size < 0 || size > 65536 || channel < 1 || channel > 3) throw new IOException();
                while (size > 0) {
                    int count = stream.read(discard, 0, Math.min(size, discard.length));
                    if (count <= 0) throw new EOFException();
                    size -= count;
                }
                if (channel == 3) break;
            }
        } catch (Exception ignored) { }
        synchronized (this) {
            if (active == run && !run.cancelled.get()) stop("scrcpy 服务端已退出，请重新开启投屏。");
        }
    }

    private void readVideo(Run run, AdbStream stream) throws Exception {
        byte[] codec = new byte[4];
        readFully(run, stream, codec);
        if (ByteBuffer.wrap(codec).getInt() != 0x68323634) throw new IOException("Expected H264");
        byte[] header = new byte[12];
        int width = 0, height = 0;
        boolean streaming = false;
        while (!run.cancelled.get()) {
            readFully(run, stream, header);
            ByteBuffer meta = ByteBuffer.wrap(header); // scrcpy 5.0.1, big-endian
            long flags = meta.getLong();
            int size = meta.getInt();
            if ((flags & Long.MIN_VALUE) != 0) {
                width = ByteBuffer.wrap(header, 4, 4).getInt();
                height = size;
                if (width <= 0 || height <= 0 || width > 1280 || height > 1280) throw new IOException("Invalid size");
                continue;
            }
            if (width == 0 || size <= 0 || size > MAX_PACKET) throw new IOException("Invalid packet");
            byte[] data = new byte[size];
            readFully(run, stream, data);
            run.lastPacket = SystemClock.elapsedRealtime();
            boolean config = (flags & (1L << 62)) != 0;
            boolean key = (flags & (1L << 61)) != 0;
            synchronized (this) {
                if (active != run || run.cancelled.get()) return;
                if (!streaming && !config) { publish("streaming", ""); streaming = true; }
                frame(run.id, width, height, config, key, data);
            }
        }
    }

    static void push(Context context, LocalAdbClient client, AtomicBoolean cancelled, String path) throws Exception {
        // Verify the packaged binary before sending it to the shell-owned temp directory.
        MessageDigest hash = MessageDigest.getInstance("SHA-256");
        try (InputStream in = context.getAssets().open(ASSET)) {
            byte[] chunk = new byte[8192];
            int count;
            while ((count = in.read(chunk)) != -1) hash.update(chunk, 0, count);
        }
        StringBuilder hex = new StringBuilder();
        for (byte b : hash.digest()) hex.append(String.format(Locale.US, "%02x", b & 255));
        if (!HASH.contentEquals(hex)) throw new IOException("Server checksum mismatch");
        try (AdbStream sync = client.openMirrorStream("sync:"); InputStream in = context.getAssets().open(ASSET)) {
            byte[] destination = (path + ",33152").getBytes(StandardCharsets.US_ASCII); // regular file 0600
            writeSync(sync, "SEND", destination, destination.length);
            byte[] chunk = new byte[32768];
            int count;
            while ((count = in.read(chunk)) != -1) {
                if (cancelled.get()) throw new IOException("Stopped");
                writeSync(sync, "DATA", chunk, count);
            }
            byte[] done = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                    .put("DONE".getBytes(StandardCharsets.US_ASCII)).putInt((int) (System.currentTimeMillis() / 1000)).array();
            sync.write(done, 0, done.length);
            sync.flush();
            byte[] reply = new byte[8];
            int offset = 0;
            while (offset < reply.length) {
                if (cancelled.get()) throw new IOException("Stopped");
                int countRead = sync.read(reply, offset, reply.length - offset);
                if (countRead <= 0) throw new EOFException();
                offset += countRead;
            }
            if (reply[0] != 'O' || reply[1] != 'K' || reply[2] != 'A' || reply[3] != 'Y') throw new IOException("Push failed");
        }
    }

    private static void writeSync(AdbStream stream, String id, byte[] data, int size) throws IOException {
        byte[] packet = ByteBuffer.allocate(size + 8).order(ByteOrder.LITTLE_ENDIAN)
                .put(id.getBytes(StandardCharsets.US_ASCII)).putInt(size).put(data, 0, size).array();
        stream.write(packet, 0, packet.length);
        stream.flush();
    }

    private static void readFully(Run run, AdbStream stream, byte[] bytes) throws IOException {
        int offset = 0;
        while (offset < bytes.length) {
            if (run.cancelled.get() || Thread.currentThread().isInterrupted()) throw new IOException("Stopped");
            int count = stream.read(bytes, offset, bytes.length - offset);
            if (count <= 0) throw new EOFException();
            offset += count;
        }
    }

    private final class Run {
        final long id;
        final int port;
        final LocalAdbClient client = new LocalAdbClient(context);
        final AtomicBoolean cancelled = new AtomicBoolean();
        volatile long lastPacket = SystemClock.elapsedRealtime();
        volatile String failure = "";
        Thread thread;
        ScheduledFuture<?> watchdog;
        Run(long id, int port) { this.id = id; this.port = port; }
    }
}
