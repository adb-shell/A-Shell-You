package com.openworld.me.localadb;

import android.content.AttributionSource;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Shell-only system bridge. Adapted from Shizuku BinderSender / IContentProviderUtils
 * at b844bc491f1790c72328e1a8e5b2349f8978f0ea (Apache-2.0).
 * Uses reflection instead of packaging hidden-framework stubs; only our own provider is addressed.
 */
final class PrivilegedSystem {
    private final Object activityManager;
    private final Class<?> managerInterface;
    private final Object packageManager;
    private final Method getApplicationInfo;
    private final String packageName, authority;
    private final int ownerUid;
    private final String apk;
    private IBinder observer;

    PrivilegedSystem(String packageName, int ownerUid, String apk) throws Exception {
        this.packageName = packageName; this.authority = packageName + PrivilegedProtocol.PROVIDER_SUFFIX;
        this.ownerUid = ownerUid; this.apk = apk;
        // Use the shell's framework Binder APIs directly. ActivityThread.systemMain() would
        // initialize system-server HWUI/font state in this standalone VM on some ROMs.
        IBinder packageBinder = (IBinder) Class.forName("android.os.ServiceManager")
                .getMethod("getService", String.class).invoke(null, "package");
        packageManager = Class.forName("android.content.pm.IPackageManager$Stub")
                .getMethod("asInterface", IBinder.class).invoke(null, packageBinder);
        getApplicationInfo = Class.forName("android.content.pm.IPackageManager")
                .getMethod("getApplicationInfo", String.class,
                        Build.VERSION.SDK_INT >= 33 ? long.class : int.class, int.class);
        Class<?> manager = Class.forName("android.app.ActivityManager");
        activityManager = manager.getDeclaredMethod("getService").invoke(null);
        managerInterface = Class.forName("android.app.IActivityManager");
        verifyInstallation();
    }

    ApplicationInfo application() throws Exception {
        // Keep boxed flag types explicit: a numeric conditional promotes Integer to Long.
        if (Build.VERSION.SDK_INT >= 33)
            return (ApplicationInfo) getApplicationInfo.invoke(packageManager, packageName, 0L, ownerUid / 100000);
        return (ApplicationInfo) getApplicationInfo.invoke(packageManager, packageName, 0, ownerUid / 100000);
    }

    void verifyInstallation() throws Exception {
        ApplicationInfo info = application();
        if (info == null || ownerUid % 100000 < 10000 || ownerUid % 100000 >= 20000
                || info.uid != ownerUid || !apk.equals(info.sourceDir))
            throw new SecurityException("Application installation changed");
    }

    boolean isRunning() {
        try {
            if ((application().flags & ApplicationInfo.FLAG_STOPPED) != 0) return false;
            Object list = managerInterface.getMethod("getRunningAppProcesses").invoke(activityManager);
            if (!(list instanceof java.util.List)) return false;
            for (Object process : (java.util.List<?>) list) {
                if (process.getClass().getField("uid").getInt(process) == ownerUid) return true;
            }
        } catch (Exception ignored) { }
        return false;
    }

    Bundle send(IBinder service, String generation) throws Exception {
        verifyInstallation();
        // Match upstream's reference-counted null-token workaround for BinderProxy identity.
        IBinder token = null;
        boolean acquired = false;
        try {
            Object holder = managerInterface.getMethod("getContentProviderExternal", String.class,
                    int.class, IBinder.class, String.class).invoke(activityManager, authority, ownerUid / 100000, token,
                    "openworld-privileged");
            if (holder == null) throw new IllegalStateException("Provider unavailable");
            acquired = true;
            Object provider = holder.getClass().getField("provider").get(holder);
            if (provider == null) throw new IllegalStateException("Provider unavailable");
            Bundle extras = new Bundle();
            extras.putBinder("service", service);
            extras.putString("generation", generation);
            Class<?> api = Class.forName("android.content.IContentProvider");
            if (Build.VERSION.SDK_INT >= 31) {
                AttributionSource attribution = new AttributionSource.Builder(Process.myUid())
                        .setPackageName(Process.myUid() == 0 ? null : "com.android.shell").build();
                return (Bundle) api.getMethod("call", AttributionSource.class, String.class,
                        String.class, String.class, Bundle.class).invoke(provider,
                        attribution, authority, "attach", null, extras);
            }
            return (Bundle) api.getMethod("call", String.class, String.class, String.class,
                    String.class, String.class, Bundle.class).invoke(provider,
                    Process.myUid() == 0 ? null : "com.android.shell", null, authority, "attach", null, extras);
        } finally {
            if (acquired) {
                try {
                    managerInterface.getMethod("removeContentProviderExternalAsUser", String.class, IBinder.class, int.class)
                            .invoke(activityManager, authority, token, ownerUid / 100000);
                } catch (Exception ignored) { }
            }
        }
    }

    void observe(Runnable active) throws Exception {
        Class<?> api = Class.forName("android.app.IUidObserver");
        Class<?> stub = Class.forName("android.app.IUidObserver$Stub");
        int activeCode = transaction(stub, "onUidActive");
        int cachedCode = transaction(stub, "onUidCachedChanged");
        observer = new Binder() {
            @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                    throws android.os.RemoteException {
                if (code == INTERFACE_TRANSACTION) {
                    if (reply != null) reply.writeString("android.app.IUidObserver");
                    return true;
                }
                if (code == activeCode || code == cachedCode) {
                    data.enforceInterface("android.app.IUidObserver");
                    int uid = data.readInt();
                    boolean foreground = code == activeCode || data.readInt() == 0;
                    if (uid == ownerUid && foreground) active.run();
                    return true;
                }
                return super.onTransact(code, data, reply, flags);
            }
        };
        Object callback = Proxy.newProxyInstance(api.getClassLoader(), new Class<?>[]{api},
                (proxy, method, args) -> "asBinder".equals(method.getName()) ? observer : null);
        managerInterface.getMethod("registerUidObserver", api, int.class, int.class, String.class)
                .invoke(activityManager, callback, 8 | 16, -1, null); // ACTIVE | CACHED
    }

    private static int transaction(Class<?> stub, String method) throws Exception {
        Field field = stub.getDeclaredField("TRANSACTION_" + method);
        field.setAccessible(true);
        return field.getInt(null);
    }
}
