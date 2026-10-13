package me.ashell.localadb;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/** Shell-only node renderer. Never starts, disables or reconfigures the product AccessibilityService. */
public final class AdbLayoutCapture {
    private static volatile long heartbeat = SystemClock.elapsedRealtime();
    private AdbLayoutCapture() { }

    public static void main(String[] args) {
        UiAutomation automation = null;
        int stage = 1;
        DataOutputStream output = new DataOutputStream(System.out);
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
            if (Looper.myLooper() == null) Looper.prepareMainLooper();
            // app_process has no ActivityThread/application context. Framework
            // display and automation APIs on newer/OEM Android require one.
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("systemMain").invoke(null);
            Context systemContext = (Context) activityThread.getMethod("getSystemContext").invoke(thread);
            callbacks.start();
            Class<?> connection = Class.forName("android.app.IUiAutomationConnection");
            Object binder = Class.forName("android.app.UiAutomationConnection").getConstructor().newInstance();
            try {
                automation = UiAutomation.class.getConstructor(Looper.class, connection)
                        .newInstance(callbacks.getLooper(), binder);
            } catch (NoSuchMethodException unavailable) {
                automation = UiAutomation.class.getConstructor(Context.class, connection)
                        .newInstance(systemContext, binder);
            }
            stage = 2;
            UiAutomation.class.getMethod("connect", int.class).invoke(automation,
                    UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES);
            AccessibilityServiceInfo service = automation.getServiceInfo();
            if (service == null) throw new IllegalStateException();
            service.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                    | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
                    | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            automation.setServiceInfo(service);
            Method clearCache = null;
            try { clearCache = UiAutomation.class.getMethod("clearCache"); } catch (NoSuchMethodException ignored) { }
            WindowManager windowsManager = (WindowManager) systemContext.getSystemService(Context.WINDOW_SERVICE);
            while (true) {
                stage = 3;
                long started = SystemClock.elapsedRealtime();
                Point size = new Point();
                windowsManager.getDefaultDisplay().getRealSize(size);
                int width = size.x, height = size.y;
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
                    stage = 4;
                    if (clearCache != null) {
                        try { clearCache.invoke(automation); } catch (Exception ignored) { }
                    }
                    int[] remaining = {4096};
                    List<AccessibilityWindowInfo> windows;
                    try { windows = automation.getWindows(); }
                    catch (RuntimeException unavailable) { windows = Collections.emptyList(); }
                    boolean found = false;
                    try {
                        for (int i = windows.size() - 1; i >= 0; i--) {
                            AccessibilityWindowInfo window = windows.get(i);
                            if (android.os.Build.VERSION.SDK_INT >= 30 && window.getDisplayId() != 0) continue;
                            try {
                                AccessibilityNodeInfo root = window.getRoot();
                                if (root != null) { draw(root, canvas, paint, remaining, 0); found = true; }
                            } catch (RuntimeException ignored) { } // window may disappear between frames
                        }
                    } finally { for (AccessibilityWindowInfo window : windows) window.recycle(); }
                    if (!found) {
                        AccessibilityNodeInfo root = automation.getRootInActiveWindow();
                        if (root == null) root = automation.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                        if (root == null) root = automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
                        if (root != null) draw(root, canvas, paint, remaining, 0);
                        else {
                            paint.setColor(Color.WHITE); paint.setStyle(Paint.Style.FILL);
                            canvas.drawText("当前界面未提供布局节点", 16, paint.getTextSize() * 2, paint);
                        }
                    }
                    stage = 5;
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
            // Bounded stage code, never node text/identifiers/raw exceptions.
            try { output.writeInt(0x4c415945); output.writeInt(stage); output.flush(); } catch (Exception closed) { }
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
            // Shell introspection also renders bounded nodes marked hidden by an
            // overlay. Do not inherit Accessibility's visible-only restriction.
            if (!bounds.isEmpty()) {
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
        } catch (RuntimeException ignored) {
            // A stale node must not terminate the entire device-owned source.
        } finally { node.recycle(); }
    }
}
