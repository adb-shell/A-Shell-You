package com.openworld.me.localadb;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;

import java.io.IOException;

/** Private in-device protocol; never exposed as a peer port or third-party Shizuku API. */
final class PrivilegedProtocol {
    static final int VERSION = 1;
    static final String SERVICE = "com.openworld.localadb.PrivilegedService.v1";
    static final String CHANNEL = "com.openworld.localadb.PrivilegedChannel.v1";
    static final String APP_LEASE = "com.openworld.localadb.PrivilegedAppLease.v1";
    static final int CHECK_LEASE = IBinder.FIRST_CALL_TRANSACTION;
    static final int INFO = IBinder.FIRST_CALL_TRANSACTION;
    static final int OPEN = INFO + 1;
    static final int STOP = INFO + 2;
    static final int CLOSE = IBinder.FIRST_CALL_TRANSACTION;
    static final String PROVIDER_SUFFIX = ".openworld.privileged";

    static boolean checkLease(IBinder lease, String generation) throws Exception {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(APP_LEASE);
            data.writeString(generation);
            if (!lease.transact(CHECK_LEASE, data, reply, 0)) throw new IOException("Lease unavailable");
            reply.readException();
            return reply.readInt() != 0;
        } finally { reply.recycle(); data.recycle(); }
    }

    static AdbChannel open(IBinder service, IBinder lease, String destination) throws IOException {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        ParcelFileDescriptor fd = null;
        try {
            data.writeInterfaceToken(SERVICE);
            data.writeString(destination);
            data.writeStrongBinder(lease);
            if (!service.transact(OPEN, data, reply, 0)) throw new IOException("Unsupported service");
            reply.readException();
            IBinder channel = reply.readStrongBinder();
            if (reply.readInt() != 0) fd = ParcelFileDescriptor.CREATOR.createFromParcel(reply);
            if (channel == null || fd == null) throw new IOException("Missing channel");
            AdbChannel result = new AdbChannel(fd, channel);
            fd = null;
            return result;
        } catch (Exception error) {
            throw new IOException("Privileged channel unavailable", error);
        } finally {
            if (fd != null) try { fd.close(); } catch (IOException ignored) { }
            reply.recycle(); data.recycle();
        }
    }

    static void stop(IBinder service) {
        if (service == null) return;
        Parcel data = Parcel.obtain();
        try {
            data.writeInterfaceToken(SERVICE);
            service.transact(STOP, data, null, IBinder.FLAG_ONEWAY);
        } catch (Exception ignored) { }
        finally { data.recycle(); }
    }
}
