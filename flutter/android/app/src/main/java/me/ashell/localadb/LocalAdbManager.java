package me.ashell.localadb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Android process owns the identity/connection. PC sessions own requests, never the identity. */
public final class LocalAdbManager {
    interface Reply {
        void success(Object value);
        void failure(String code, String message);
    }

    private static LocalAdbManager instance;
    static synchronized LocalAdbManager get(Context context) {
        if (instance == null) instance = new LocalAdbManager(context.getApplicationContext());
        return instance;
    }

    private final Context context;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final LocalAdbClient client;
    private final SharedPreferences records;
    private final ExecutorService worker = Executors.newSingleThreadExecutor(
            task -> new Thread(task, "ashell-local-adb"));
    private final ArrayDeque<Operation> queue = new ArrayDeque<>();
    private Operation current;
    private long revision;

    private LocalAdbManager(Context context) {
        this.context = context;
        client = new LocalAdbClient(context);
        records = context.getSharedPreferences("local_adb_record_v1", Context.MODE_PRIVATE);
        RemoteAdbNative.attach(this);
    }

    Map<String, Object> status() {
        Map<String, Object> value = new HashMap<>();
        value.put("supported", Build.VERSION.SDK_INT >= 30);
        value.put("connected", client.isConnected());
        value.put("paired", Build.VERSION.SDK_INT >= 30
                && records.getBoolean("paired", false) && AdbIdentity.exists(context));
        value.put("pairedAt", records.getLong("pairedAt", 0));
        value.put("connectPort", records.getInt("connectPort", 0));
        value.put("busy", current != null);
        value.put("queued", queue.size());
        value.put("operation", current == null ? "" : current.kind);
        value.put("revision", revision);
        return value;
    }

    void execute(String owner, String kind, Map<String, Object> args, Reply reply) {
        if ("status".equals(kind)) { reply.success(status()); return; }
        if ("cancel".equals(kind)) {
            cancelOwner(owner);
            reply.success(null);
            return;
        }
        if (Build.VERSION.SDK_INT < 30) {
            reply.failure("unsupported", "配对码无线调试需要 Android 11 或以上版本。");
            return;
        }
        if (!("pair".equals(kind) || "connect".equals(kind) || "shell".equals(kind)
                || "disconnect".equals(kind) || "revoke".equals(kind))) {
            reply.failure("invalid", "未知 ADB 操作。"); return;
        }
        int port = 0;
        Object rawPort = args.get("port");
        if (rawPort != null) {
            if (!(rawPort instanceof Number) || ((Number) rawPort).doubleValue() != ((Number) rawPort).intValue()) {
                reply.failure("invalid", "端口必须是整数。"); return;
            }
            port = ((Number) rawPort).intValue();
        }
        if (("pair".equals(kind) && (port < 1 || port > 65535))
                || ("connect".equals(kind) && (port < 0 || port > 65535))) {
            reply.failure("invalid", "端口必须为 1–65535；连接端口留空可自动发现。"); return;
        }
        String text = "";
        if ("pair".equals(kind)) {
            Object code = args.get("code");
            if (!(code instanceof String) || !((String) code).matches("[0-9]{6}")) {
                reply.failure("invalid", "请输入 6 位配对码。"); return;
            }
            text = (String) code;
        } else if ("shell".equals(kind)) {
            Object command = args.get("command");
            if (!(command instanceof String) || ((String) command).trim().isEmpty()
                    || ((String) command).length() > 4096 || ((String) command).indexOf('\0') >= 0) {
                reply.failure("invalid", "请输入 1–4096 字符且不含 NUL 的 shell 命令。"); return;
            }
            text = (String) command;
        }
        if ("revoke".equals(kind)) {
            // Global device action: cancel all queued work before invalidating the shared identity.
            while (!queue.isEmpty()) {
                Operation pending = queue.remove();
                pending.text = "";
                pending.reply.failure("cancelled", "ADB 身份正在撤销，排队请求已取消。");
            }
            if (current != null) cancelCurrent("cancelled");
        } else if (queue.size() >= 7) {
            reply.failure("busy", "Android ADB 队列已满，请稍后重试。"); return;
        }
        queue.add(new Operation(owner, kind, port, text, reply));
        revision++;
        startNext();
    }

    private void startNext() {
        if (current != null || queue.isEmpty()) return;
        Operation operation = queue.remove();
        current = operation;
        operation.timeout = () -> { if (current == operation) cancelCurrent("timeout"); };
        main.postDelayed(operation.timeout, 20000);
        worker.execute(() -> {
            Object value = null;
            boolean failed = false;
            operation.thread = Thread.currentThread();
            try {
                if (operation.cancelled.get()) throw new InterruptedException();
                switch (operation.kind) {
                    case "pair":
                        client.pair(operation.port, operation.text, operation.cancelled);
                        if (!operation.cancelled.get()) records.edit().putBoolean("paired", true)
                                .putLong("pairedAt", System.currentTimeMillis()).apply();
                        break;
                    case "connect": {
                        int port = operation.port;
                        if (port == 0) {
                            try { port = AdbPortDiscovery.find(context, operation.cancelled); }
                            catch (InterruptedException e) { throw e; }
                            catch (Exception ignored) { }
                            if (port == 0) port = records.getInt("connectPort", 0);
                        }
                        if (port == 0) throw new IllegalStateException("Port unavailable");
                        value = client.connect(port, operation.cancelled);
                        if (!operation.cancelled.get()) records.edit().putInt("connectPort", port)
                                .putBoolean("paired", true).apply();
                        break;
                    }
                    case "shell": value = client.runShell(operation.text, operation.cancelled); break;
                    case "disconnect": client.disconnect(); break;
                    case "revoke":
                        client.disconnect();
                        if (!records.edit().clear().commit()) throw new IllegalStateException("Record removal failed");
                        AdbIdentity.revoke(context);
                        break;
                    default: throw new IllegalStateException();
                }
            } catch (Exception | LinkageError ignored) {
                failed = true;
                client.disconnect();
            } finally {
                operation.text = "";
                operation.thread = null;
                if (operation.cancelled.get()) client.disconnect();
                Thread.interrupted();
            }
            Object result = value;
            boolean error = failed;
            main.post(() -> {
                main.removeCallbacks(operation.timeout);
                if (current != operation) return;
                current = null;
                revision++;
                if (operation.cancelled.get()) operation.reply.failure(operation.cancelReason,
                        "timeout".equals(operation.cancelReason) ? "ADB 操作超时，连接已断开。" : "ADB 操作已取消。");
                else if (error) operation.reply.failure("adb_failed", failureMessage(operation.kind));
                else operation.reply.success(result);
                startNext();
            });
        });
    }

    private String failureMessage(String kind) {
        if ("pair".equals(kind)) return "配对失败，请保持系统配对窗口打开并核对配对端口与配对码。";
        if ("connect".equals(kind)) return "连接或 shell 验证失败，请填写无线调试首页的连接端口；若已在系统忘记配对，请重新配对。";
        if ("revoke".equals(kind)) return "撤销未完整完成；ADB 已断开，请在系统无线调试中忘记对应配对后重试。";
        return "ADB 操作失败，连接已断开，请重新连接。";
    }

    private void cancelCurrent(String reason) {
        Operation operation = current;
        if (operation == null || !operation.cancelled.compareAndSet(false, true)) return;
        operation.cancelReason = reason;
        client.abort();
        Thread thread = operation.thread;
        if (thread != null) thread.interrupt();
        // Also close the reader if cancellation arrives after worker completion but before
        // its main-looper result. This is queued before startNext can submit another job.
        worker.execute(client::disconnect);
    }

    void cancelOwner(String owner) {
        Iterator<Operation> pending = queue.iterator();
        while (pending.hasNext()) {
            Operation operation = pending.next();
            if (operation.owner.equals(owner)) {
                pending.remove();
                operation.text = "";
                operation.reply.failure("cancelled", "请求已取消。");
            }
        }
        if (current != null && current.owner.equals(owner)) cancelCurrent("cancelled");
        revision++;
    }

    // Called from the isolated JNI bridge, on arbitrary native threads. No work runs there.
    public void dispatchRemote(String owner, String token, String payload) {
        main.post(() -> {
            Reply reply = new Reply() {
                private void finish(boolean ok, Object value, String code, String message) {
                    try {
                        JSONObject out = new JSONObject().put("ok", ok).put("state", new JSONObject(status()));
                        if (ok) out.put("result", value instanceof Map ? new JSONObject((Map<?, ?>) value)
                                : value == null ? JSONObject.NULL : value);
                        else out.put("error", code).put("message", message);
                        RemoteAdbNative.complete(token, out.toString());
                    } catch (Exception | LinkageError ignored) { }
                }
                @Override public void success(Object value) { finish(true, value, "", ""); }
                @Override public void failure(String code, String message) { finish(false, null, code, message); }
            };
            try {
                JSONObject request = new JSONObject(payload);
                Map<String, Object> args = new HashMap<>();
                for (String key : new String[]{"port", "code", "command"}) {
                    if (request.has(key) && !request.isNull(key)) args.put(key, request.get(key));
                }
                execute("remote:" + owner, request.getString("method"), args, reply);
            } catch (Exception ignored) { reply.failure("invalid", "无效 ADB 请求。"); }
        });
    }

    public void disconnectRemote(String owner) {
        main.post(() -> cancelOwner("remote:" + owner));
    }

    private static final class Operation {
        final String owner;
        final String kind;
        final int port;
        volatile String text;
        final Reply reply;
        final AtomicBoolean cancelled = new AtomicBoolean();
        volatile Thread thread;
        String cancelReason = "cancelled";
        Runnable timeout;
        Operation(String owner, String kind, int port, String text, Reply reply) {
            this.owner = owner; this.kind = kind; this.port = port; this.text = text; this.reply = reply;
        }
    }
}
