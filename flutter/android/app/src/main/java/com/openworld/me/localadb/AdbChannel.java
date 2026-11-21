package com.openworld.me.localadb;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.system.Os;
import android.system.OsConstants;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbStream;

/** One cancellable byte stream. ADB is bootstrap-only; normal traffic uses Binder-owned FDs. */
final class AdbChannel implements Closeable {
    private final AdbStream adb;
    private final ParcelFileDescriptor descriptor;
    private final IBinder remote;
    private final FileInputStream input;
    private final FileOutputStream output;
    private final AtomicBoolean closed = new AtomicBoolean();
    private volatile Runnable onClose = () -> { };

    void onClose(Runnable callback) {
        onClose = callback;
        if (closed.get()) callback.run();
    }

    AdbChannel(AdbStream stream) {
        adb = stream; descriptor = null; remote = null; input = null; output = null;
    }

    AdbChannel(ParcelFileDescriptor descriptor, IBinder remote) {
        this.adb = null; this.descriptor = descriptor; this.remote = remote;
        input = new FileInputStream(descriptor.getFileDescriptor());
        output = new FileOutputStream(descriptor.getFileDescriptor());
    }

    int read(byte[] bytes, int offset, int length) throws IOException {
        if (closed.get()) throw new IOException("Channel closed");
        return adb != null ? adb.read(bytes, offset, length) : input.read(bytes, offset, length);
    }

    void write(byte[] bytes, int offset, int length) throws IOException {
        if (closed.get()) throw new IOException("Channel closed");
        if (adb != null) adb.write(bytes, offset, length);
        else output.write(bytes, offset, length);
    }

    void flush() throws IOException {
        if (adb != null) adb.flush();
        else output.flush();
    }

    @Override public void close() throws IOException {
        if (!closed.compareAndSet(false, true)) return;
        onClose.run();
        if (adb != null) { adb.close(); return; }
        // shutdown, not close alone, wakes reads/writes blocked on another thread's FD.
        try { Os.shutdown(descriptor.getFileDescriptor(), OsConstants.SHUT_RDWR); }
        catch (Exception ignored) { }
        try { descriptor.close(); }
        finally {
            Parcel data = Parcel.obtain();
            try {
                data.writeInterfaceToken(PrivilegedProtocol.CHANNEL);
                remote.transact(PrivilegedProtocol.CLOSE, data, null, IBinder.FLAG_ONEWAY);
            } catch (Exception ignored) { }
            finally { data.recycle(); }
        }
    }
}
