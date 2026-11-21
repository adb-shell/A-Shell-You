package com.openworld.me.localadb;

import android.content.Context;
import android.os.Build;
import android.os.Binder;
import android.os.IBinder;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbConnection;
import io.github.muntashirakon.adb.PairingConnectionCtx;

/** Worker-owned channel lease; normal calls use verified Binder, explicit activation uses adbd. */
final class LocalAdbClient {
    static final class ConnectionFailure extends IOException {
        ConnectionFailure(boolean handshakeComplete) {
            super(handshakeComplete
                    ? "ADB 连接握手成功，但 Shell 通道或 UID 验证未完成，请重新连接。"
                    : "无线调试连接握手未完成，请确认连接端口有效，且系统仍保留本应用配对记录。");
        }
    }
    private static final ScheduledExecutorService DEADLINES = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "openworld-adb-connect-deadline");
        thread.setDaemon(true);
        return thread;
    });
    private final Context context;
    private final boolean boundedClose;
    private volatile AdbLocalSocket socket;
    private volatile AdbConnection connection;
    private volatile boolean shellVerified;
    private volatile PrivilegedBridge.Binding binding;
    private final IBinder lease = new Binder();
    private final java.util.Set<AdbChannel> channels = new java.util.HashSet<>();
    private long epoch;

    LocalAdbClient(Context context) {
        this(context, true);
    }

    LocalAdbClient(Context context, boolean boundedClose) {
        this.context = context.getApplicationContext();
        this.boundedClose = boundedClose;
    }

    boolean isConnected() {
        PrivilegedBridge.Binding attached = binding;
        if (attached != null) return attached == PrivilegedBridge.get(context).current();
        AdbConnection value = connection;
        AdbLocalSocket transport = socket;
        return shellVerified && value != null && transport != null && !transport.isClosed()
                && value.isConnected() && value.isConnectionEstablished();
    }

    void pair(int port, String code, AtomicBoolean cancelled) throws Exception {
        disconnect();
        check(cancelled);
        AdbIdentity identity = AdbIdentity.load(context);
        AdbLocalSocket transport = newTransport(port, cancelled);
        PairingConnectionCtx pairing = null;
        try {
            pairing = new PairingConnectionCtx("127.0.0.1", transport.port(),
                    code.getBytes(StandardCharsets.UTF_8), identity.privateKey, identity.certificate, "OpenWorld local ADB");
            check(cancelled);
            pairing.start();
            check(cancelled);
        } finally {
            transport.close();
            // Upstream close may encounter partially initialized streams after a failed handshake.
            if (pairing != null) {
                try { pairing.close(); } catch (Exception ignored) { }
            }
            socket = null;
        }
    }

    String connect(AtomicBoolean cancelled) throws Exception {
        // Wireless ports are activation hints only. Every normal execution lane
        // binds to the already verified service, even when Wi-Fi/adbd are completely unavailable.
        disconnect();
        check(cancelled);
        PrivilegedBridge.Binding value = PrivilegedBridge.get(context).current();
        if (value == null) throw new IOException("ADB 能力尚未激活或已失效，请先激活内置服务。");
        synchronized (channels) {
            check(cancelled);
            binding = value;
        }
        return "uid=" + value.uid;
    }

    /** Only an explicit manager activation can open an adbd transport. */
    String connectAdb(int port, AtomicBoolean cancelled, long timeoutMillis) throws Exception {
        disconnect();
        check(cancelled);
        // Bound TLS AND the first shell OPEN/read. libadb's OPEN wait has no deadline.
        // Synchronize completion with interruption so a late timer cannot cancel the next job.
        Object lease = new Object();
        boolean[] finished = {false};
        AtomicBoolean expired = new AtomicBoolean();
        Thread caller = Thread.currentThread();
        ScheduledFuture<?> deadline = DEADLINES.schedule(() -> {
            synchronized (lease) {
                if (finished[0]) return;
                expired.set(true);
                abort();
                caller.interrupt();
            }
        }, Math.max(1, timeoutMillis), TimeUnit.MILLISECONDS);
        boolean handshakeComplete = false;
        try {
            AdbIdentity identity = AdbIdentity.load(context);
            AdbLocalSocket transport = newTransport(port, cancelled);
            AdbConnection value = new AdbConnection.Builder("127.0.0.1", transport.port())
                    .setApi(Build.VERSION.SDK_INT).setPrivateKey(identity.privateKey)
                    .setCertificate(identity.certificate).setDeviceName("OpenWorld local ADB").build();
            connection = value;
            check(cancelled);
            if (!value.connect(8, TimeUnit.SECONDS, true)) throw new IllegalStateException("Connection timed out");
            handshakeComplete = true;
            Map<String, Object> probe = AdbShell.execute(value, "id -u", cancelled);
            String output = (String) probe.get("output");
            if (output == null || !("2000".equals(output.trim()) || "0".equals(output.trim()))
                    || !Integer.valueOf(0).equals(probe.get("exitCode"))) {
                throw new IOException("Shell UID verification failed");
            }
            check(cancelled);
            synchronized (lease) {
                if (expired.get() || cancelled.get() || transport.isClosed() || !value.isConnectionEstablished())
                    throw new IOException("ADB verification expired");
                shellVerified = true;
                finished[0] = true;
            }
            return "uid=" + output.trim();
        } catch (Exception | LinkageError e) {
            disconnect();
            if (!cancelled.get() && !(e instanceof LinkageError)) throw new ConnectionFailure(handshakeComplete);
            throw e;
        } finally {
            synchronized (lease) { finished[0] = true; }
            deadline.cancel(false);
            if (expired.get() && !cancelled.get()) Thread.interrupted();
        }
    }

    Map<String, Object> runShell(String command, AtomicBoolean cancelled) throws Exception {
        if (!isConnected()) throw new IllegalStateException("Not connected");
        check(cancelled);
        if (binding != null) return AdbShell.execute(openMirrorStream("shell,v2,raw:" + command), cancelled);
        AdbConnection value = connection;
        if (value == null) throw new IllegalStateException("Not connected");
        Map<String, Object> result = AdbShell.execute(value, command, cancelled);
        check(cancelled);
        return result;
    }

    // Each execution lane owns its own client and descriptors; no shared data stream.
    AdbChannel openMirrorStream(String destination) throws Exception {
        PrivilegedBridge.Binding attached;
        long expected;
        synchronized (channels) { attached = binding; expected = epoch; }
        if (attached != null) {
            if (attached != PrivilegedBridge.get(context).current()) throw new IOException("Service stopped");
            AdbChannel stream = PrivilegedProtocol.open(attached.service, lease, destination);
            synchronized (channels) {
                if (epoch != expected || binding != attached) { stream.close(); throw new IOException("Cancelled"); }
                channels.add(stream);
                stream.onClose(() -> { synchronized (channels) { channels.remove(stream); } });
            }
            return stream;
        }
        AdbConnection value = connection;
        if (!isConnected() || value == null) throw new IllegalStateException("ADB disconnected");
        return new AdbChannel(AdbStreamCompat.open(value, destination));
    }

    private AdbLocalSocket newTransport(int port, AtomicBoolean cancelled) throws Exception {
        check(cancelled);
        AdbLocalSocket transport = new AdbLocalSocket();
        socket = transport;
        try {
            check(cancelled);
            transport.start(port);
            check(cancelled);
            return transport;
        } catch (Exception e) {
            transport.close();
            socket = null;
            throw e;
        }
    }

    /** Nonblocking cancellation, callable from the main thread while a worker is in native I/O. */
    void abort() {
        AdbChannel[] current;
        synchronized (channels) {
            epoch++;
            binding = null;
            current = channels.toArray(new AdbChannel[0]);
            channels.clear();
        }
        for (AdbChannel stream : current) try { stream.close(); } catch (IOException ignored) { }
        shellVerified = false;
        AdbLocalSocket value = socket;
        if (value != null) value.close();
    }

    /** Worker-only: upstream close joins its reader thread. */
    void disconnect() {
        abort();
        AdbConnection value = connection;
        connection = null;
        socket = null;
        if (value != null) {
            if (!boundedClose) {
                try { value.close(); } catch (Exception ignored) { }
                return;
            }
            // No ADB lane may pin its state machine on libadb's unbounded reader join.
            Thread cleanup = new Thread(() -> {
                try { value.close(); } catch (Exception ignored) { }
            }, "openworld-adb-private-close");
            cleanup.setDaemon(true);
            cleanup.start();
            try { cleanup.join(1000); }
            catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            if (cleanup.isAlive()) cleanup.interrupt();
        }
    }

    private static void check(AtomicBoolean cancelled) throws InterruptedIOException {
        if (cancelled.get() || Thread.currentThread().isInterrupted()) throw new InterruptedIOException("Cancelled");
    }
}
