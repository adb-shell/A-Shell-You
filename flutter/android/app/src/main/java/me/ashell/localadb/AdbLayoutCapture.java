package me.ashell.localadb;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.List;

/** Shell-only node renderer. Never starts, disables or reconfigures the product AccessibilityService. */
public final class AdbLayoutCapture {
    private static volatile long heartbeat = SystemClock.elapsedRealtime();
    private AdbLayoutCapture() { }

    public static void main(String[] args) {
        UiAutomation automation = null;
        HandlerThread callbacks = new HandlerThread("ashell-layout-callbacks");
        Thread lease = new Thread(() -> {
            try {
                while (SystemClock.elapsedRealtime() - heartbeat < 10000) Thread.sleep(500);
            } catch (InterruptedException ignored) { }
            System.exit(0);
        }, "ashell-layout-lease");
        lease.setDaemon(true);
        lease.start();
        try {
            if (android.os.Process.myUid() != 2000 && android.os.Process.myUid() != 0) return;
            if (System.in.read() != 1) return;
            int quality = args.length == 1 ? Integer.parseInt(args[0]) : AdbCaptureProfile.JPEG_QUALITY;
            quality = Math.max(50, Math.min(90, quality));
            Thread input = new Thread(() -> {
                try { while (System.in.read() == 1) heartbeat = SystemClock.elapsedRealtime(); }
                catch (Exception ignored) { }
                System.exit(0); // Binder death unregisters this UiAutomation, even during a stuck node call.
            }, "ashell-layout-input");
            input.setDaemon(true);
            input.start();
            Looper.prepareMainLooper();
            callbacks.start();
            Class<?> connection = Class.forName("android.app.IUiAutomationConnection");
            Object binder = Class.forName("android.app.UiAutomationConnection").getConstructor().newInstance();
            automation = UiAutomation.class.getConstructor(Looper.class, connection)
                    .newInstance(callbacks.getLooper(), binder);
            UiAutomation.class.getMethod("connect", int.class).invoke(automation,
                    UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);
            AccessibilityServiceInfo service = automation.getServiceInfo();
            service.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                    | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            automation.setServiceInfo(service);
            Method clearCache = null;
            try { clearCache = UiAutomation.class.getMethod("clearCache"); } catch (NoSuchMethodException ignored) { }
            Class<?> displays = Class.forName("android.hardware.display.DisplayManagerGlobal");
            Object global = displays.getMethod("getInstance").invoke(null);
            DataOutputStream output = new DataOutputStream(System.out);
            while (true) {
                long started = SystemClock.elapsedRealtime();
                Object info = displays.getMethod("getDisplayInfo", int.class).invoke(global, 0);
                int width = info.getClass().getField("logicalWidth").getInt(info);
                int height = info.getClass().getField("logicalHeight").getInt(info);
                if (width <= 0 || height <= 0 || width > 16384 || height > 16384) throw new IllegalStateException();
                AdbCaptureProfile profile = new AdbCaptureProfile(width, height);
                Bitmap bitmap = Bitmap.createBitmap(profile.width, profile.height, Bitmap.Config.ARGB_8888);
                try {
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(Color.BLACK);
                    canvas.scale((float) profile.width / width, (float) profile.height / height);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStrokeWidth((float) width / profile.width);
                    paint.setTextSize(Math.max(24f, 11f * width / profile.width));
                    if (clearCache != null) clearCache.invoke(automation);
                    int[] remaining = {4096};
                    List<AccessibilityWindowInfo> windows = automation.getWindows();
                    boolean found = false;
                    try {
                        for (int i = windows.size() - 1; i >= 0; i--) {
                            AccessibilityWindowInfo window = windows.get(i);
                            if (android.os.Build.VERSION.SDK_INT >= 30 && window.getDisplayId() != 0) continue;
                            AccessibilityNodeInfo root = window.getRoot();
                            if (root != null) { draw(root, canvas, paint, remaining, 0); found = true; }
                        }
                    } finally { for (AccessibilityWindowInfo window : windows) window.recycle(); }
                    if (!found) {
                        AccessibilityNodeInfo root = automation.getRootInActiveWindow();
                        if (root != null) draw(root, canvas, paint, remaining, 0);
                        else {
                            paint.setColor(Color.WHITE); paint.setStyle(Paint.Style.FILL);
                            canvas.drawText("当前界面未提供布局节点", 16, paint.getTextSize() * 2, paint);
                        }
                    }
                    ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, quality, jpeg) || jpeg.size() > 2 * 1024 * 1024)
                        throw new IllegalStateException();
                    output.writeInt(0x4c415931); // LAY1, big-endian, bounded by app receiver
                    output.writeInt(width); output.writeInt(height);
                    output.writeInt(profile.width); output.writeInt(profile.height);
                    output.writeInt(jpeg.size()); jpeg.writeTo(output); output.flush();
                } finally { bitmap.recycle(); }
                Thread.sleep(Math.max(1, AdbCaptureProfile.FRAME_INTERVAL_MS - (SystemClock.elapsedRealtime() - started)));
            }
        } catch (Throwable ignored) {
            // No node text, identifiers or raw exceptions in diagnostic output.
        } finally {
            if (automation != null) {
                try { UiAutomation.class.getMethod("disconnect").invoke(automation); } catch (Throwable ignored) { }
            }
            callbacks.quitSafely();
            System.exit(0);
        }
    }

    @SuppressWarnings("deprecation")
    private static void draw(AccessibilityNodeInfo node, Canvas canvas, Paint paint, int[] remaining, int depth) {
        try {
            if (--remaining[0] < 0 || depth > 64) return;
            Rect bounds = new Rect(); node.getBoundsInScreen(bounds);
            if (node.isVisibleToUser() && !bounds.isEmpty()) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(node.isClickable() ? Color.GREEN : Color.LTGRAY);
                canvas.drawRect(bounds, paint);
                CharSequence text = node.isPassword() ? "••••" : node.getText();
                if (text == null && !node.isPassword()) text = node.getContentDescription();
                if (text != null && text.length() > 0) {
                    String label = text.subSequence(0, Math.min(160, text.length())).toString().replace('\n', ' ');
                    canvas.save(); canvas.clipRect(bounds);
                    paint.setStyle(Paint.Style.FILL); paint.setColor(Color.WHITE);
                    canvas.drawText(label, bounds.left + 2, bounds.top + paint.getTextSize(), paint);
                    canvas.restore();
                }
            }
            for (int i = 0; i < node.getChildCount() && remaining[0] > 0; i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) draw(child, canvas, paint, remaining, depth + 1);
            }
        } finally { node.recycle(); }
    }
}
