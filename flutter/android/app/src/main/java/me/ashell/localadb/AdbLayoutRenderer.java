package me.ashell.localadb;

import android.app.UiAutomation;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Display;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Draws real accessibility nodes into the ADB bitmap, without using the product frame pipeline. */
final class AdbLayoutRenderer {
    private final Paint outline = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint text = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Rect screen;
    private final float textSize;
    private int remaining = 4096;
    private int drawn;

    AdbLayoutRenderer(int width, int height, int encodedWidth) {
        screen = new Rect(0, 0, width, height);
        float pixel = (float) width / encodedWidth;
        outline.setStyle(Paint.Style.STROKE);
        outline.setStrokeWidth(pixel);
        text.setColor(Color.WHITE);
        textSize = Math.max(24f, 11f * pixel);
    }

    @SuppressWarnings("deprecation")
    void draw(UiAutomation automation, Canvas canvas) {
        List<AccessibilityNodeInfo> roots = new ArrayList<>();
        Set<Integer> windowIds = new HashSet<>();
        List<AccessibilityWindowInfo> windows;
        RuntimeException queryError = null;
        boolean queried = false;
        try { windows = automation.getWindows(); }
        catch (RuntimeException unavailable) { windows = Collections.emptyList(); queryError = unavailable; }
        try {
            // Window enumeration and the active root are independent queries. A status-bar
            // root must not prevent reading the app when its window list is still updating.
            for (int i = windows.size() - 1; i >= 0; i--) {
                AccessibilityWindowInfo window = windows.get(i);
                if (window.getDisplayId() != Display.DEFAULT_DISPLAY) continue;
                try {
                    // API 33+ can fetch a bounded batch of descendants for the traversal.
                    AccessibilityNodeInfo root = Build.VERSION.SDK_INT >= 33
                            ? window.getRoot(AccessibilityNodeInfo.FLAG_PREFETCH_DESCENDANTS_BREADTH_FIRST) : window.getRoot();
                    addRoot(roots, windowIds, root); queried = true;
                } catch (RuntimeException staleWindow) { queryError = staleWindow; }
            }
            boolean activeRootFound = false;
            try {
                AccessibilityNodeInfo root = automation.getRootInActiveWindow();
                activeRootFound = root != null;
                addRoot(roots, windowIds, root); queried = true;
            }
            catch (RuntimeException unavailable) { queryError = unavailable; }
            if (!activeRootFound) {
                boolean focusFound = false;
                try {
                    AccessibilityNodeInfo focus = automation.findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                    focusFound = focus != null;
                    addRoot(roots, windowIds, focus); queried = true;
                }
                catch (RuntimeException unavailable) { queryError = unavailable; }
                if (!focusFound) {
                    try { addRoot(roots, windowIds, automation.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)); queried = true; }
                    catch (RuntimeException unavailable) { queryError = unavailable; }
                }
            }
            if (!queried && queryError != null) throw queryError;
            for (AccessibilityNodeInfo root : roots) drawNode(root, canvas, 0);
        } finally {
            for (AccessibilityNodeInfo root : roots) root.recycle();
            for (AccessibilityWindowInfo window : windows) window.recycle();
        }
        if (drawn == 0) {
            drawText(canvas, screen, "当前界面未提供可绘制的布局节点\n请切换到系统设置等标准界面确认");
        }
    }

    @SuppressWarnings("deprecation")
    private void addRoot(List<AccessibilityNodeInfo> roots, Set<Integer> ids, AccessibilityNodeInfo root) {
        if (root == null) return;
        AccessibilityWindowInfo window = null;
        boolean keep = false;
        try {
            window = root.getWindow();
            if (window != null && window.getDisplayId() != Display.DEFAULT_DISPLAY) return;
            // Window ID is stable across the list/root queries within this frame.
            int id = root.getWindowId();
            if (id < 0 ? !roots.contains(root) : ids.add(id)) { roots.add(root); keep = true; }
        } finally {
            if (window != null) window.recycle();
            if (!keep) root.recycle();
        }
    }

    @SuppressWarnings("deprecation")
    private void drawNode(AccessibilityNodeInfo node, Canvas canvas, int depth) {
        if (remaining-- <= 0 || depth > 64) return;
        try {
            Rect bounds = new Rect();
            node.getBoundsInScreen(bounds);
            // Like the original penetration renderer, use screen coordinates. Do not
            // reject a real bounded node merely because an overlay marks it invisible.
            if (bounds.intersect(screen)) {
                outline.setColor(node.isClickable() ? Color.GREEN : Color.LTGRAY);
                canvas.drawRect(bounds, outline);
                drawn++;
                CharSequence label = node.isPassword() ? "••••" : node.getText();
                if (!node.isPassword() && TextUtils.isEmpty(label)) label = node.getContentDescription();
                if (!node.isPassword() && TextUtils.isEmpty(label)) label = node.getHintText();
                if (!TextUtils.isEmpty(label)) drawText(canvas, bounds, label);
            }
        } catch (RuntimeException staleNode) { }
        int children;
        try { children = node.getChildCount(); }
        catch (RuntimeException staleNode) { return; }
        for (int i = 0; i < children && remaining > 0; i++) {
            AccessibilityNodeInfo child = null;
            try {
                child = Build.VERSION.SDK_INT >= 33
                        ? node.getChild(i, AccessibilityNodeInfo.FLAG_PREFETCH_DESCENDANTS_BREADTH_FIRST) : node.getChild(i);
                if (child != null) drawNode(child, canvas, depth + 1);
            } catch (RuntimeException staleChild) {
                // One disappearing child must not discard its still-valid siblings.
            } finally { if (child != null) child.recycle(); }
        }
    }

    private void drawText(Canvas canvas, Rect bounds, CharSequence value) {
        if (bounds.width() <= 2 || bounds.height() <= 2) return;
        text.setTextSize(textSize);
        Paint.FontMetrics metrics = text.getFontMetrics();
        float available = bounds.height() - 2f;
        float lineHeight = metrics.descent - metrics.ascent;
        if (lineHeight > available) text.setTextSize(textSize * available / lineHeight);
        metrics = text.getFontMetrics();
        int lines = Math.max(1, Math.min(8, (int) (available / (metrics.descent - metrics.ascent))));
        // Convert spans to plain text; preserve Chinese, RTL and explicit line breaks.
        int end = Math.min(1024, value.length());
        if (end < value.length() && end > 0 && Character.isHighSurrogate(value.charAt(end - 1))) end--;
        String label = value.subSequence(0, end).toString();
        StaticLayout layout = StaticLayout.Builder.obtain(label, 0, label.length(), text, bounds.width() - 2)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL).setIncludePad(false)
                .setMaxLines(lines).setEllipsize(TextUtils.TruncateAt.END).build();
        int saved = canvas.save();
        try {
            canvas.clipRect(bounds);
            canvas.translate(bounds.left + 1, bounds.top + 1);
            layout.draw(canvas);
        } finally { canvas.restoreToCount(saved); }
    }
}
