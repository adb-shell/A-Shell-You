package me.ashell.localadb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbStream;

/** Bounded, binary shell-v2 screenshots. No Accessibility, MediaProjection or disk files. */
final class LocalAdbScreenshot {
    private static final int MAX_PNG = 16 * 1024 * 1024;
    private LocalAdbScreenshot() { }

    static final class Frame {
        final int width, height, sourceWidth, sourceHeight;
        final byte[] jpeg;
        Frame(int width, int height, int sourceWidth, int sourceHeight, byte[] jpeg) {
            this.width = width; this.height = height; this.jpeg = jpeg;
            this.sourceWidth = sourceWidth; this.sourceHeight = sourceHeight;
        }
    }

    static Frame capture(LocalAdbClient client, AtomicBoolean cancelled) throws Exception {
        ByteArrayOutputStream png = new ByteArrayOutputStream();
        try (AdbStream stream = client.openMirrorStream("shell,v2,raw:screencap -p")) {
            byte[] header = new byte[5];
            byte[] buffer = new byte[32768];
            int stderr = 0;
            while (true) {
                readFully(stream, header, header.length, cancelled);
                int channel = header[0] & 255;
                int size = ByteBuffer.wrap(header, 1, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
                if (size < 0 || size > MAX_PNG || channel < 1 || channel > 3) throw new IOException("Invalid screenshot packet");
                if (channel == 3) {
                    if (size != 1) throw new IOException("Invalid screenshot exit");
                    readFully(stream, buffer, 1, cancelled);
                    if (buffer[0] != 0) throw new IOException("System screenshot failed");
                    break;
                }
                if (channel == 1 && size > MAX_PNG - png.size()) throw new IOException("Screenshot too large");
                if (channel == 2) {
                    stderr += size;
                    if (stderr > 65536) throw new IOException("Screenshot stderr too large");
                }
                while (size > 0) {
                    int count = Math.min(size, buffer.length);
                    readFully(stream, buffer, count, cancelled);
                    if (channel == 1) png.write(buffer, 0, count);
                    size -= count;
                }
            }
        }
        if (cancelled.get()) throw new IOException("Stopped");
        byte[] bytes = png.toByteArray();
        if (bytes.length < 24 || bytes[0] != (byte) 137 || bytes[1] != 'P' || bytes[2] != 'N' || bytes[3] != 'G')
            throw new IOException("Expected PNG screenshot");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (options.outWidth <= 0 || options.outHeight <= 0 || options.outWidth > 8192 || options.outHeight > 8192
                || (long) options.outWidth * options.outHeight > 32L * 1024 * 1024) throw new IOException("Invalid screenshot dimensions");
        AdbCaptureProfile profile = new AdbCaptureProfile(options.outWidth, options.outHeight);
        options.inSampleSize = 1;
        // Decode near the transmission size, avoiding a full-resolution intermediate.
        while (options.outWidth / (options.inSampleSize * 2) >= profile.width
                && options.outHeight / (options.inSampleSize * 2) >= profile.height) options.inSampleSize *= 2;
        options.inJustDecodeBounds = false;
        options.inScaled = false;
        Bitmap source = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (source == null) throw new IOException("Screenshot decode failed");
        Bitmap resized = source;
        try {
            int width = profile.width;
            int height = profile.height;
            if (width != source.getWidth() || height != source.getHeight()) resized = Bitmap.createScaledBitmap(source, width, height, true);
            if (cancelled.get()) throw new IOException("Stopped");
            ByteArrayOutputStream jpeg = new ByteArrayOutputStream();
            if (!resized.compress(Bitmap.CompressFormat.JPEG, profile.encoding()[2], jpeg) || jpeg.size() > 2 * 1024 * 1024)
                throw new IOException("Screenshot encode failed");
            return new Frame(width, height, profile.sourceWidth, profile.sourceHeight, jpeg.toByteArray());
        } finally {
            if (resized != source) resized.recycle();
            source.recycle();
        }
    }

    private static void readFully(AdbStream stream, byte[] buffer, int size, AtomicBoolean cancelled) throws IOException {
        int offset = 0;
        while (offset < size) {
            if (cancelled.get() || Thread.currentThread().isInterrupted()) throw new IOException("Stopped");
            int count = stream.read(buffer, offset, size - offset);
            if (count <= 0) throw new EOFException();
            offset += count;
        }
    }
}
