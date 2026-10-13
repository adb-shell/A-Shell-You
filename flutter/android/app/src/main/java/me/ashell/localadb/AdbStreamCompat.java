package me.ashell.localadb;

import android.os.SystemClock;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbConnection;
import io.github.muntashirakon.adb.AdbProtocol;
import io.github.muntashirakon.adb.AdbStream;

/**
 * Compatibility boundary for the pinned libadb 3.1.1 (c849886e).
 * Its open() sends OPEN before an unconditional wait(): an early OKAY is lost.
 * Use the same library stream/reader, but hold the stream monitor before sending,
 * check readiness in a bounded loop, and allocate the destination by UTF-8 bytes.
 * Member names below are pinned in local-adb.pro; never silently fall back to open().
 */
final class AdbStreamCompat {
    private static final class Access {
        static final Field NEXT = field(AdbConnection.class, "mLastLocalId");
        static final Field STREAMS = field(AdbConnection.class, "mOpenedStreams");
        static final Field READY = field(AdbStream.class, "mWriteReady");
        static final Constructor<AdbStream> CREATE;
        static final Method SEND;
        static {
            try {
                CREATE = AdbStream.class.getDeclaredConstructor(AdbConnection.class, int.class);
                CREATE.setAccessible(true);
                SEND = AdbConnection.class.getDeclaredMethod("sendPacket", byte[].class);
                SEND.setAccessible(true);
            } catch (ReflectiveOperationException error) { throw new ExceptionInInitializerError(error); }
        }
        private static Field field(Class<?> type, String name) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (ReflectiveOperationException error) { throw new ExceptionInInitializerError(error); }
        }
    }

    @SuppressWarnings("unchecked")
    static AdbStream open(AdbConnection connection, String destination) throws Exception {
        byte[] name = destination.getBytes(StandardCharsets.UTF_8);
        if (name.length == 0 || name.length > 65535 || destination.indexOf('\0') >= 0)
            throw new IOException("Invalid ADB destination");
        final AdbStream stream;
        final Map<Integer, AdbStream> streams;
        final int id;
        synchronized (connection) {
            if (!connection.isConnected() || !connection.isConnectionEstablished())
                throw new IOException("ADB disconnected");
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException();
            id = Access.NEXT.getInt(connection) + 1;
            Access.NEXT.setInt(connection, id);
            stream = Access.CREATE.newInstance(connection, id);
            streams = (Map<Integer, AdbStream>) Access.STREAMS.get(connection);
            streams.put(id, stream);
        }
        boolean opened = false;
        try {
            AtomicBoolean ready = (AtomicBoolean) Access.READY.get(stream);
            byte[] packet = AdbProtocol.generateMessage(AdbProtocol.A_OPEN, id, 0, Arrays.copyOf(name, name.length + 1));
            long deadline = SystemClock.elapsedRealtime() + 8000;
            synchronized (stream) {
                Access.SEND.invoke(connection, (Object) packet);
                while (!stream.isClosed() && !ready.get()) {
                    if (!connection.isConnectionEstablished()) throw new IOException("ADB disconnected");
                    long remaining = deadline - SystemClock.elapsedRealtime();
                    if (remaining <= 0) throw new IOException("ADB stream open timed out");
                    stream.wait(Math.min(remaining, 200));
                }
                if (stream.isClosed()) throw new IOException("ADB stream rejected");
            }
            opened = true;
            return stream;
        } finally {
            if (!opened) {
                streams.remove(id);
                try { stream.close(); } catch (IOException ignored) { }
            }
        }
    }
}
