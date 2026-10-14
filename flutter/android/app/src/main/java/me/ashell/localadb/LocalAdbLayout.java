package me.ashell.localadb;

import android.content.Context;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import io.github.muntashirakon.adb.AdbStream;

/** Persistent node renderer over a private shell-v2 stream; no dumps on shared storage. */
final class LocalAdbLayout implements AutoCloseable {
    private final AdbStream stream;
    private final LocalAdbClient client;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final Thread heartbeat;
    private final DataInputStream input;
    private boolean receivedFrame;

    LocalAdbLayout(Context context, LocalAdbClient client, AtomicBoolean cancelled) throws Exception {
        this.client = client;
        String apk = context.getApplicationInfo().sourceDir.replace("'", "'\\''");
        int quality = AdbCaptureProfile.current(context).encoding()[2];
        stream = client.openMirrorStream("shell,v2,raw:CLASSPATH='" + apk
                + "' exec app_process / me.ashell.localadb.AdbLayoutCapture " + quality);
        input = new DataInputStream(new Stdout(stream, cancelled));
        heartbeat = new Thread(() -> {
            byte[] tick = {0, 1, 0, 0, 0, 1};
            try {
                while (!closed.get() && !cancelled.get()) {
                    stream.write(tick, 0, tick.length); stream.flush(); Thread.sleep(1000);
                }
            } catch (Exception ignored) { }
            client.abort(); // EOF/lease expiry removes the shell UiAutomation registration.
        }, "ashell-layout-heartbeat");
        heartbeat.setDaemon(true);
        heartbeat.start();
    }

    LocalAdbScreenshot.Frame read() throws IOException {
        try {
            int magic = input.readInt();
            if (magic == 0x4c415945) throw new Failure(input.readInt(), input.readInt(), input.readUTF());
            if (magic != 0x4c415931) throw new Failure("ADB 穿透数据格式异常，请确认已安装最新 Android 客户端。");
            int sw = input.readInt(), sh = input.readInt(), w = input.readInt(), h = input.readInt(), size = input.readInt();
            if (sw <= 0 || sh <= 0 || sw > 16384 || sh > 16384 || w <= 0 || h <= 0 || w > 1280 || h > 1280
                    || size <= 0 || size > 2 * 1024 * 1024) throw new Failure("ADB 穿透返回了无效的画面尺寸或长度。");
            byte[] jpeg = new byte[size]; input.readFully(jpeg);
            receivedFrame = true;
            return new LocalAdbScreenshot.Frame(w, h, sw, sh, jpeg);
        } catch (Failure failure) {
            throw failure;
        } catch (IOException closed) {
            throw new Failure(receivedFrame ? "ADB 穿透数据流已中断，请确认无线调试连接后重试。"
                    : "ADB 穿透进程在首帧前结束，请重新开启穿透；仍失败时请提供 Android 版本及错误提示。");
        }
    }

    static final class Failure extends IOException {
        Failure(String message) { super(message); }
        Failure(int stage, int api, String type) {
            super((stage == 1 ? "ADB 穿透系统环境初始化失败。"
                    : stage == 2 ? "ADB 穿透节点服务连接失败。"
                    : stage == 3 ? "ADB 穿透无法读取屏幕尺寸。"
                    : stage == 4 ? "ADB 穿透无法读取当前界面节点。"
                    : "ADB 穿透画面生成失败。") + "（API " + api + " / " + safeType(type) + "）");
        }
        private static String safeType(String type) {
            return type.length() <= 80 && type.matches("[A-Za-z0-9_$]+") ? type : "FrameworkError";
        }
    }

    @Override public void close() {
        closed.set(true); heartbeat.interrupt(); client.abort();
        try { stream.close(); } catch (IOException ignored) { }
    }

    private static final class Stdout extends InputStream {
        private final AdbStream stream;
        private final AtomicBoolean cancelled;
        private final byte[] header = new byte[5], discard = new byte[4096];
        private int remaining;
        Stdout(AdbStream stream, AtomicBoolean cancelled) { this.stream = stream; this.cancelled = cancelled; }
        private void fully(byte[] bytes) throws IOException {
            int offset = 0;
            while (offset < bytes.length) {
                if (cancelled.get()) throw new EOFException();
                int count = stream.read(bytes, offset, bytes.length - offset);
                if (count <= 0) throw new EOFException();
                offset += count;
            }
        }
        @Override public int read() throws IOException {
            byte[] b = new byte[1]; return read(b, 0, 1) < 0 ? -1 : b[0] & 255;
        }
        @Override public int read(byte[] bytes, int offset, int length) throws IOException {
            if (length == 0) return 0;
            while (remaining == 0) {
                fully(header);
                int channel = header[0] & 255;
                int size = ByteBuffer.wrap(header, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                if (size < 0 || size > 1048576 || channel < 1 || channel > 3)
                    throw new Failure("ADB 穿透 Shell 数据包无效。");
                if (channel == 3) {
                    if (size != 1) throw new Failure("ADB 穿透 Shell 退出包无效。");
                    byte[] exit = new byte[1]; fully(exit);
                    throw new Failure("ADB 穿透进程已退出（exit " + (exit[0] & 255) + "）。");
                }
                if (channel == 1) { remaining = size; continue; }
                // Diagnostics are not frames. Drain in bounded chunks; a long-running
                // helper must not stop solely because accumulated OEM logs exceed 64 KiB.
                while (size > 0) {
                    if (cancelled.get()) throw new EOFException();
                    int count = stream.read(discard, 0, Math.min(discard.length, size));
                    if (count <= 0) throw new EOFException();
                    size -= count;
                }
            }
            if (cancelled.get()) throw new EOFException();
            int count = stream.read(bytes, offset, Math.min(remaining, length));
            if (count <= 0) throw new EOFException();
            remaining -= count; return count;
        }
    }
}
