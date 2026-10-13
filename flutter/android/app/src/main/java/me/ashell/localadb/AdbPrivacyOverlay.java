package me.ashell.localadb;

import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;

import java.lang.reflect.Method;

/** Standalone shell-UID entry in our APK. It owns only one non-captured black color layer. */
public final class AdbPrivacyOverlay {
    private static volatile long heartbeat = SystemClock.elapsedRealtime();

    private AdbPrivacyOverlay() { }

    public static void main(String[] args) {
        Object surface = null;
        Object transaction = null;
        Class<?> sc = null;
        Class<?> tx = null;
        Thread lease = new Thread(() -> {
            try {
                while (SystemClock.elapsedRealtime() - heartbeat < 10000) Thread.sleep(500);
            } catch (InterruptedException ignored) { return; }
            // Binder death removes the layer even if stdin or SurfaceFlinger calls stop responding.
            System.exit(0);
        }, "ashell-overlay-lease");
        lease.setDaemon(true);
        lease.start();
        try {
            if (android.os.Process.myUid() != 2000 && android.os.Process.myUid() != 0) return;
            if (Build.VERSION.SDK_INT < 30 || System.in.read() != 1) return;
            Looper.prepareMainLooper();
            sc = Class.forName("android.view.SurfaceControl");
            Class<?> builderClass = Class.forName("android.view.SurfaceControl$Builder");
            tx = Class.forName("android.view.SurfaceControl$Transaction");
            Object builder = builderClass.getConstructor().newInstance();
            builderClass.getMethod("setName", String.class).invoke(builder, "AShell ADB privacy overlay");
            builderClass.getMethod("setHidden", boolean.class).invoke(builder, true);
            builderClass.getMethod("setColorLayer").invoke(builder);
            Method skip = null;
            if (Build.VERSION.SDK_INT >= 31) {
                // Resolve before building/showing: never substitute FLAG_SECURE (which captures black).
                skip = tx.getMethod("setSkipScreenshot", sc, boolean.class);
            } else {
                // Android 11 predecessor of SKIP_SCREENSHOT, read platform constants rather than magic flags.
                int key = sc.getField("METADATA_WINDOW_TYPE").getInt(null);
                int type = sc.getField("WINDOW_TYPE_DONT_SCREENSHOT").getInt(null);
                builderClass.getMethod("setMetadata", int.class, int.class).invoke(builder, key, type);
            }
            Class<?> displays = Class.forName("android.hardware.display.DisplayManagerGlobal");
            Object global = displays.getMethod("getInstance").invoke(null);
            Object info = displays.getMethod("getDisplayInfo", int.class).invoke(global, 0);
            if (info == null) throw new IllegalStateException("No default display");
            int layerStack = info.getClass().getField("layerStack").getInt(info);
            surface = builderClass.getMethod("build").invoke(builder);
            transaction = tx.getConstructor().newInstance();
            if (skip != null) skip.invoke(transaction, surface, true);
            tx.getMethod("setLayerStack", sc, int.class).invoke(transaction, surface, layerStack);
            tx.getMethod("setLayer", sc, int.class).invoke(transaction, surface, Integer.MAX_VALUE);
            tx.getMethod("setColor", sc, float[].class).invoke(transaction, surface, new float[]{0f, 0f, 0f});
            // A color layer has no pixel buffer; a square crop covers rotation without reallocations.
            tx.getMethod("setWindowCrop", sc, int.class, int.class).invoke(transaction, surface, 16384, 16384);
            tx.getMethod("setAlpha", sc, float.class).invoke(transaction, surface, 1f);
            tx.getMethod("show", sc).invoke(transaction, surface);
            tx.getMethod("apply", boolean.class).invoke(transaction, true);
            System.out.println("ASHELL_OVERLAY_READY");
            System.out.flush();
            while (System.in.read() == 1) heartbeat = SystemClock.elapsedRealtime();
        } catch (Throwable ignored) {
            System.out.println("ASHELL_OVERLAY_ERROR");
            System.out.flush();
        } finally {
            try {
                if (surface != null && transaction != null) {
                    tx.getMethod("remove", sc).invoke(transaction, surface);
                    tx.getMethod("apply", boolean.class).invoke(transaction, true);
                }
                if (surface != null) sc.getMethod("release").invoke(surface);
                if (transaction != null) tx.getMethod("close").invoke(transaction);
            } catch (Throwable ignored) { }
            System.out.println("ASHELL_OVERLAY_OFF");
            System.out.flush();
            System.exit(0); // also releases the SurfaceControl session if remove failed
        }
    }
}
