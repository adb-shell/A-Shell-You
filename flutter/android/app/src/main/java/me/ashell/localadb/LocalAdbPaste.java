package me.ashell.localadb;

import android.os.Handler;
import android.os.Looper;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.muntashirakon.adb.AdbStream;

/** Explicit Unicode paste over the existing scrcpy control socket, never a shell script. */
final class LocalAdbPaste {
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ArrayDeque<Request> pending = new ArrayDeque<>();
    private long sequence;

    synchronized void submit(String owner, String id, byte[] text, LocalAdbManager.Reply reply) {
        if (pending.size() >= 4) {
            reply.failure("busy", "ADB 输入队列已满，请稍后重试。"); return;
        }
        Request request = new Request(++sequence, owner, id, text, reply);
        request.timeout = () -> finish(request, "timeout", "ADB 未确认粘贴请求，请检查手机输入框后再操作。");
        pending.add(request);
        main.postDelayed(request.timeout, 10000);
    }

    void writeNext(AdbStream stream) throws IOException {
        byte[] packet;
        synchronized (this) {
            Request request = pending.peek();
            if (request == null || request.sent) return;
            request.sent = true;
            // scrcpy 5.0.1 SET_CLIPBOARD: type, sequence, paste, UTF-8 byte length, text.
            packet = ByteBuffer.allocate(14 + request.text.length).put((byte) 9)
                    .putLong(request.sequence).put((byte) 1).putInt(request.text.length).put(request.text).array();
            request.text = null;
        }
        // libadb 3.1.1 does not wait for READY between fragments of one large write.
        // Individual <=4 KiB writes also work with daemons negotiating the minimum size.
        for (int offset = 0; offset < packet.length; offset += 4096) {
            stream.write(packet, offset, Math.min(4096, packet.length - offset));
            stream.flush();
        }
    }

    void readAcks(AdbStream stream, AtomicBoolean cancelled) throws IOException {
        byte[] packet = new byte[9];
        while (!cancelled.get()) {
            int offset = 0;
            while (offset < packet.length) {
                if (cancelled.get()) return;
                int count = stream.read(packet, offset, packet.length - offset);
                if (count <= 0) throw new EOFException();
                offset += count;
            }
            // Autosync is disabled; only ACK_CLIPBOARD is requested on this socket.
            if (packet[0] != 1) throw new IOException("Unexpected ADB input reply");
            long id = ByteBuffer.wrap(packet, 1, 8).getLong();
            synchronized (this) {
                Request request = pending.peek();
                if (request != null && request.sent && request.sequence == id) finish(request, "", "");
            }
        }
    }

    synchronized void cancel(String owner, String id) {
        Iterator<Request> requests = pending.iterator();
        while (requests.hasNext()) {
            Request request = requests.next();
            if (request.owner.equals(owner) && (id == null || request.id.equals(id))) {
                requests.remove();
                complete(request, "cancelled", "输入请求已取消；已发送到手机的内容无法撤回。");
            }
        }
    }

    synchronized void stop() {
        Request request;
        while ((request = pending.poll()) != null) complete(request, "disconnected", "ADB 输入连接已断开，请重新操作。");
    }

    private synchronized void finish(Request request, String code, String message) {
        if (pending.remove(request)) complete(request, code, message);
    }

    private void complete(Request request, String code, String message) {
        main.removeCallbacks(request.timeout);
        request.text = null;
        // ACK confirms handling of the control message, not insertion by the focused app.
        main.post(() -> {
            if (code.isEmpty()) request.reply.success(null);
            else request.reply.failure(code, message);
        });
    }

    private static final class Request {
        final long sequence;
        final String owner, id;
        final LocalAdbManager.Reply reply;
        byte[] text;
        boolean sent;
        Runnable timeout;
        Request(long sequence, String owner, String id, byte[] text, LocalAdbManager.Reply reply) {
            this.sequence = sequence; this.owner = owner; this.id = id; this.text = text; this.reply = reply;
        }
    }
}
