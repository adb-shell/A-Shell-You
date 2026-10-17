package me.ashell.localadb;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.WindowManager;

/** Read-only counterpart of MainService's width / 350 integer scaling. */
final class AdbCaptureProfile {
    static final int MAX_EDGE = 1280;
    private static final int MEDIA_MAX_EDGE = 960;
    static final int JPEG_QUALITY = 75;
    static final int FRAME_INTERVAL_MS = 500;
    final int sourceWidth, sourceHeight, width, height;

    AdbCaptureProfile(int sourceWidth, int sourceHeight) {
        this(sourceWidth, sourceHeight, MAX_EDGE);
    }

    private AdbCaptureProfile(int sourceWidth, int sourceHeight, int maxEdge) {
        if (sourceWidth <= 0 || sourceHeight <= 0 || sourceWidth > 16384 || sourceHeight > 16384)
            throw new IllegalArgumentException("Invalid display size");
        this.sourceWidth = sourceWidth;
        this.sourceHeight = sourceHeight;
        int divisor = Math.max(1, sourceWidth / 350);
        double scale = Math.min(1.0 / divisor, (double) maxEdge / Math.max(sourceWidth, sourceHeight));
        width = Math.max(1, (int) (sourceWidth * scale));
        height = Math.max(1, (int) (sourceHeight * scale));
    }

    @SuppressWarnings("deprecation")
    static AdbCaptureProfile current(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 30) {
            android.graphics.Rect bounds = wm.getMaximumWindowMetrics().getBounds();
            return new AdbCaptureProfile(bounds.width(), bounds.height());
        }
        Point size = new Point();
        wm.getDefaultDisplay().getRealSize(size);
        return new AdbCaptureProfile(size.x, size.y);
    }

    int maxSize() { return Math.max(8, Math.max(width, height) & ~7); }

    // Video/screenshots use the ordinary width divisor with a lower tall-screen ceiling.
    // Physical touch geometry, layout rendering and the privacy overlay keep their profile.
    AdbCaptureProfile media() { return new AdbCaptureProfile(sourceWidth, sourceHeight, MEDIA_MAX_EDGE); }

    long screenshotIntervalMs(int jpegBytes) {
        int[] settings = encoding();
        int fps = Math.max(1, Math.min(12, settings[1]));
        int budget = Math.max(200000, Math.min(1000000, settings[0]));
        // Responsive small frames; slow down large screenshots to preserve bandwidth.
        // Bound deliberate sleep so a large frame cannot trip the no-frame watchdog.
        long bandwidthInterval = (jpegBytes * 8000L + budget - 1) / budget;
        return Math.max((1000L + fps - 1) / fps, Math.min(2000L, bandwidthInterval));
    }
    private static native int[] quality(int width, int height);

    int[] encoding() {
        // Read the ordinary stream's selected quality/FPS without changing its QoS state.
        try {
            int[] value = quality(width, height);
            if (value != null && value.length == 3) return value;
        } catch (LinkageError ignored) { }
        return new int[]{Math.max(200000, Math.min(1000000, width * height * 2)), 15, JPEG_QUALITY};
    }
}
