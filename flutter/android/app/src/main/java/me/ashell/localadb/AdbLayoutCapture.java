package me.ashell.localadb;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Display;
import android.view.accessibility.AccessibilityEvent;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

/** Shell-only node renderer. Never starts, disables or reconfigures the product AccessibilityService. */
public final class AdbLayoutCapture {
    private static volatile long heartbeat = SystemClock.elapsedRealtime();
    private AdbLayoutCapture() { }

    public static void main(String[] args) {
        // Keep framework/OEM System.out diagnostics out of the binary frame channel.
        DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out)));
        System.setOut(System.err);
        Thread lease = new Thread(() -> {
            try {
                while (SystemClock.elapsedRealtime() - heartbeat < 10000) Thread.sleep(500);
            } catch (InterruptedException ignored) { }
            System.exit(0);
        }, "ashell-layout-lease");
        lease.setDaemon(true);
        lease.start();
        try {
            if (android.os.Process.myUid() != 2000 && android.os.Process.myUid() != 0)
                throw new SecurityException();
            if (System.in.read() != 1) return;
            int quality = args.length == 1 ? Integer.parseInt(args[0]) : AdbCaptureProfile.JPEG_QUALITY;
            final int jpegQuality = Math.max(50, Math.min(90, quality));
            Thread input = new Thread(() -> {
                try { while (System.in.read() == 1) heartbeat = SystemClock.elapsedRealtime(); }
                catch (Exception ignored) { }
                System.exit(0); // Binder death unregisters this helper even during a stuck node call.
            }, "ashell-layout-input");
            input.setDaemon(true);
            input.start();
            if (Looper.myLooper() == null) Looper.prepareMainLooper();
            // Keep the framework context/configuration initialization on the main thread.
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("systemMain").invoke(null);
            Context context = (Context) activityThread.getMethod("getSystemContext").invoke(thread);
            new Thread(() -> capture(context, jpegQuality, output), "ashell-layout-render").start();
            // Context-based UiAutomation and framework display/configuration callbacks use this
            // looper. Node queries and JPEG encoding must not occupy it.
            Looper.loop();
        } catch (Throwable error) {
            failure(output, 1, error);
        } finally {
            System.exit(0);
        }
    }

    private static void capture(Context context, int quality, DataOutputStream output) {
        UiAutomation automation = null;
        int stage = 2;
        try {
            Class<?> connection = Class.forName("android.app.IUiAutomationConnection");
            Object binder = Class.forName("android.app.UiAutomationConnection").getConstructor().newInstance();
            try {
                // The shell constructor works on API 30+ and pins queries to display 0,
                // matching ADB video, screenshots and touch coordinates.
                automation = UiAutomation.class.getConstructor(Looper.class, connection)
                        .newInstance(Looper.getMainLooper(), binder);
            } catch (NoSuchMethodException unavailable) {
                automation = UiAutomation.class.getConstructor(Context.class, connection)
                        .newInstance(context, binder);
            }
            UiAutomation.class.getMethod("connect", int.class).invoke(automation,
                    UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);
            AccessibilityServiceInfo service = automation.getServiceInfo();
            if (service == null) throw new IllegalStateException();
            service.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            service.packageNames = null;
            service.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                    | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
                    | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            automation.setServiceInfo(service);

            stage = 4;
            // API 34+ exposes UiAutomation.clearCache(). API 33 requires the connection
            // ID; API 30-32 has a process-local cache. Resolve signatures for OEM backports.
            Method clearCache;
            Object cacheOwner;
            Object[] cacheArgs = new Object[0];
            try {
                clearCache = UiAutomation.class.getMethod("clearCache");
                cacheOwner = automation;
            } catch (NoSuchMethodException oldApi) {
                Class<?> client = Class.forName("android.view.accessibility.AccessibilityInteractionClient");
                cacheOwner = client.getMethod("getInstance").invoke(null);
                try { clearCache = client.getMethod("clearCache"); }
                catch (NoSuchMethodException perConnection) {
                    clearCache = client.getMethod("clearCache", int.class);
                    cacheArgs = new Object[]{UiAutomation.class.getMethod("getConnectionId").invoke(automation)};
                }
            }
            stage = 3;
            Class<?> displays = Class.forName("android.hardware.display.DisplayManagerGlobal");
            Object displayManager = displays.getMethod("getInstance").invoke(null);
            Method getDisplayInfo = displays.getMethod("getDisplayInfo", int.class);
            while (true) {
                stage = 3;
                long started = SystemClock.elapsedRealtime();
                Object info = getDisplayInfo.invoke(displayManager, Display.DEFAULT_DISPLAY);
                if (info == null) throw new IllegalStateException();
                int width = info.getClass().getField("logicalWidth").getInt(info);
                int height = info.getClass().getField("logicalHeight").getInt(info);
                AdbCaptureProfile profile = new AdbCaptureProfile(width, height);
                Bitmap bitmap = Bitmap.createBitmap(profile.width, profile.height, Bitmap.Config.ARGB_8888);
                try {
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.BLACK);
                    canvas.scale((float) profile.width / width, (float) profile.height / height);
                    stage = 4;
                    clearCache.invoke(cacheOwner, cacheArgs);
                    new AdbLayoutRenderer(width, height, profile.width).draw(automation, canvas);
                    stage = 5;
                    ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality, jpeg) || jpeg.size() > 2 * 1024 * 1024)
                        throw new IllegalStateException();
                    synchronized (output) {
                        output.writeInt(0x4c415931); // LAY1, big-endian, bounded by app receiver
                        output.writeInt(width); output.writeInt(height);
                        output.writeInt(profile.width); output.writeInt(profile.height);
                        output.writeInt(jpeg.size()); jpeg.writeTo(output); output.flush();
                    }
                } finally { bitmap.recycle(); }
                Thread.sleep(Math.max(1, AdbCaptureProfile.FRAME_INTERVAL_MS - (SystemClock.elapsedRealtime() - started)));
            }
        } catch (Throwable error) {
            failure(output, stage, error);
        } finally {
            if (automation != null) {
                try { UiAutomation.class.getMethod("disconnect").invoke(automation); } catch (Throwable ignored) { }
            }
            System.exit(0);
        }
    }

    private static void failure(DataOutputStream output, int stage, Throwable error) {
        // Return actual stage/API/exception type, never node text or raw exception messages.
        for (int i = 0; i < 8 && error.getCause() != null && error.getCause() != error; i++) error = error.getCause();
        String type = error.getClass().getSimpleName();
        if (type.length() > 80) type = type.substring(0, 80);
        try {
            synchronized (output) {
                output.writeInt(0x4c415945); // LAYE
                output.writeInt(stage); output.writeInt(Build.VERSION.SDK_INT);
                output.writeUTF(type); output.flush();
            }
        } catch (Exception closed) { }
    }
}
