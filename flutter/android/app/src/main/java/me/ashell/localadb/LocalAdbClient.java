package me.ashell.localadb;

import android.content.Context;
import android.os.Build;

import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbConnection;
import io.github.muntashirakon.adb.AdbStream;
import io.github.muntashirakon.adb.PairingConnectionCtx;

/** Worker-thread owner of a local ADB connection. No product JNI, services or peer protocol. */
final class LocalAdbClient {
    private final Context context;
    private volatile AdbLocalSocket socket;
    private volatile AdbConnection connection;
    private volatile boolean shellVerified;

    LocalAdbClient(Context context) {
        this.context = context.getApplicationContext();
    }

    boolean isConnected() {
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
                    code.getBytes(StandardCharsets.UTF_8), identity.privateKey, identity.certificate, "AShell local ADB");
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

    String connect(int port, AtomicBoolean cancelled) throws Exception {
        disconnect();
        check(cancelled);
        AdbIdentity identity = AdbIdentity.load(context);
        AdbLocalSocket transport = newTransport(port, cancelled);
        try {
            AdbConnection value = new AdbConnection.Builder("127.0.0.1", transport.port())
                    .setApi(Build.VERSION.SDK_INT).setPrivateKey(identity.privateKey)
                    .setCertificate(identity.certificate).setDeviceName("AShell local ADB").build();
            connection = value;
            check(cancelled);
            if (!value.connect(8, TimeUnit.SECONDS, true)) throw new IllegalStateException("Connection timed out");
            Map<String, Object> probe = AdbShell.execute(value, "id", cancelled);
            String output = (String) probe.get("output");
            if (output == null || !(output.contains("uid=2000(") || output.contains("uid=0("))
                    || !Integer.valueOf(0).equals(probe.get("exitCode"))) {
                throw new IllegalStateException("Shell unavailable");
            }
            check(cancelled);
            shellVerified = true;
            return output.trim();
        } catch (Exception | LinkageError e) {
            disconnect();
            throw e;
        }
    }

    Map<String, Object> runShell(String command, AtomicBoolean cancelled) throws Exception {
        if (!isConnected()) throw new IllegalStateException("Not connected");
        check(cancelled);
        AdbConnection value = connection;
        if (value == null) throw new IllegalStateException("Not connected");
        Map<String, Object> result = AdbShell.execute(value, command, cancelled);
        check(cancelled);
        return result;
    }

    // Used only by the mirror's private LocalAdbClient, never by the shared shell client.
    AdbStream openMirrorStream(String destination) throws Exception {
        AdbConnection value = connection;
        if (!isConnected() || value == null) throw new IllegalStateException("ADB disconnected");
        return value.open(destination);
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
            try { value.close(); } catch (Exception ignored) { }
        }
    }

    private static void check(AtomicBoolean cancelled) throws InterruptedIOException {
        if (cancelled.get() || Thread.currentThread().isInterrupted()) throw new InterruptedIOException("Cancelled");
    }
}
