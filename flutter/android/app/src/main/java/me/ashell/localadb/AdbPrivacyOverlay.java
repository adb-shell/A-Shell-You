package me.ashell.localadb;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Surface;

import java.lang.reflect.Method;

/** Shell-UID entry owning a non-captured black layer and its non-interactive white hint. */
public final class AdbPrivacyOverlay {
    private static final String HINT = "正在对接服务中心\n请勿触碰手机屏幕\n避免影响业务";
    private static volatile long heartbeat = SystemClock.elapsedRealtime();

    private AdbPrivacyOverlay() { }

    public static void main(String[] args) {
        Object surface = null;
        Object hintControl = null;
        Surface hintSurface = null;
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
            if (Build.VERSION.SDK_INT >= 31) {
                // Own color layer only: allow input below this overlay, without disabling the
                // system-wide untrusted-touch protection or giving the layer an input channel.
                tx.getMethod("setTrustedOverlay", sc, boolean.class).invoke(transaction, surface, true);
            }
            tx.getMethod("setLayerStack", sc, int.class).invoke(transaction, surface, layerStack);
            tx.getMethod("setLayer", sc, int.class).invoke(transaction, surface, Integer.MAX_VALUE);
            tx.getMethod("setColor", sc, float[].class).invoke(transaction, surface, new float[]{0f, 0f, 0f});
            // A color layer has no pixel buffer; a square crop covers rotation without reallocations.
            tx.getMethod("setWindowCrop", sc, int.class, int.class).invoke(transaction, surface, 16384, 16384);
            tx.getMethod("setAlpha", sc, float.class).invoke(transaction, surface, 1f);

            // Match the Accessibility hint: 16.5sp white text, 60dp margin + 20dp padding.
            // Draw only the small text buffer; the existing color layer still covers the screen.
            float density = info.getClass().getField("logicalDensityDpi").getInt(info) / 160f;
            int displayHeight = info.getClass().getField("logicalHeight").getInt(info);
            int displayWidth = info.getClass().getField("logicalWidth").getInt(info);
            float inset = 80f * density;
            TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(16.5f * density * Resources.getSystem().getConfiguration().fontScale);
            float textWidth = 0;
            for (String line : HINT.split("\n")) textWidth = Math.max(textWidth, paint.measureText(line));
            int hintWidth = Math.max(1, Math.min((int) Math.ceil(textWidth) + 2,
                    displayWidth - (int) Math.ceil(inset + 20f * density)));
            StaticLayout hint = StaticLayout.Builder.obtain(HINT, 0, HINT.length(), paint, hintWidth)
                    .setIncludePad(true).build();
            Object hintBuilder = builderClass.getConstructor().newInstance();
            builderClass.getMethod("setName", String.class).invoke(hintBuilder, "AShell ADB privacy hint");
            builderClass.getMethod("setHidden", boolean.class).invoke(hintBuilder, true);
            builderClass.getMethod("setParent", sc).invoke(hintBuilder, surface);
            builderClass.getMethod("setBufferSize", int.class, int.class).invoke(hintBuilder, hintWidth, hint.getHeight());
            builderClass.getMethod("setFormat", int.class).invoke(hintBuilder, PixelFormat.RGBA_8888);
            if (skip == null) {
                builderClass.getMethod("setMetadata", int.class, int.class).invoke(hintBuilder,
                        sc.getField("METADATA_WINDOW_TYPE").getInt(null),
                        sc.getField("WINDOW_TYPE_DONT_SCREENSHOT").getInt(null));
            }
            hintControl = builderClass.getMethod("build").invoke(hintBuilder);
            if (skip != null) skip.invoke(transaction, hintControl, true);
            if (Build.VERSION.SDK_INT >= 31)
                tx.getMethod("setTrustedOverlay", sc, boolean.class).invoke(transaction, hintControl, true);
            tx.getMethod("setLayer", sc, int.class).invoke(transaction, hintControl, 1);
            Method position = tx.getMethod("setPosition", sc, float.class, float.class);
            position.invoke(transaction, hintControl, inset, Math.max(0f, displayHeight - inset - hint.getHeight()));
            // Surface's empty constructor/copyFrom are framework APIs, not public SDK APIs.
            hintSurface = Surface.class.getConstructor().newInstance();
            Surface.class.getMethod("copyFrom", sc).invoke(hintSurface, hintControl);
            Canvas canvas = hintSurface.lockCanvas(null);
            try {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                hint.draw(canvas);
            } finally { hintSurface.unlockCanvasAndPost(canvas); }
            tx.getMethod("show", sc).invoke(transaction, hintControl);
            tx.getMethod("show", sc).invoke(transaction, surface);
            tx.getMethod("apply", boolean.class).invoke(transaction, true);
            System.out.println("ASHELL_OVERLAY_READY");
            System.out.flush();
            while (System.in.read() == 1) {
                heartbeat = SystemClock.elapsedRealtime();
                // Reposition after rotation using the existing lease ticks, without another timer.
                Object currentInfo = displays.getMethod("getDisplayInfo", int.class).invoke(global, 0);
                if (currentInfo == null) continue;
                int height = currentInfo.getClass().getField("logicalHeight").getInt(currentInfo);
                if (height != displayHeight) {
                    displayHeight = height;
                    position.invoke(transaction, hintControl, inset, Math.max(0f, height - inset - hint.getHeight()));
                    tx.getMethod("apply", boolean.class).invoke(transaction, true);
                }
            }
        } catch (Throwable ignored) {
            System.out.println("ASHELL_OVERLAY_ERROR");
            System.out.flush();
        } finally {
            try {
                if (surface != null && transaction != null) {
                    if (hintControl != null) tx.getMethod("remove", sc).invoke(transaction, hintControl);
                    tx.getMethod("remove", sc).invoke(transaction, surface);
                    tx.getMethod("apply", boolean.class).invoke(transaction, true);
                }
                if (hintSurface != null) hintSurface.release();
                if (hintControl != null) sc.getMethod("release").invoke(hintControl);
                if (surface != null) sc.getMethod("release").invoke(surface);
                if (transaction != null) tx.getMethod("close").invoke(transaction);
            } catch (Throwable ignored) { }
            System.out.println("ASHELL_OVERLAY_OFF");
            System.out.flush();
            System.exit(0); // also releases the SurfaceControl session if remove failed
        }
    }
}
