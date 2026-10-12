package me.ashell.localadb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/** A dedicated local channel; no entry from remote commands, input masks or product services. */
public final class LocalAdbPlugin implements FlutterPlugin, ActivityAware, MethodChannel.MethodCallHandler {
    private final Handler main = new Handler(Looper.getMainLooper());
    private MethodChannel channel;
    private Activity activity;
    private Context context;
    private LocalAdbClient client;
    private ExecutorService worker;
    private Operation current;
    private boolean attached;

    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        context = binding.getApplicationContext();
        client = new LocalAdbClient(context);
        worker = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "ashell-local-adb"));
        channel = new MethodChannel(binding.getBinaryMessenger(), "me.ashell/local_adb/v1");
        channel.setMethodCallHandler(this);
        attached = true;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (!attached) {
            result.error("unavailable", "ADB 模块已关闭。", null);
            return;
        }
        if ("status".equals(call.method)) {
            Map<String, Object> status = new HashMap<>();
            status.put("supported", Build.VERSION.SDK_INT >= 30);
            status.put("connected", client.isConnected());
            status.put("busy", current != null);
            result.success(status);
            return;
        }
        if ("cancel".equals(call.method)) {
            cancelCurrent("cancelled");
            result.success(null);
            return;
        }
        if ("openSettings".equals(call.method)) {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                if (activity != null) activity.startActivity(intent);
                else context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                result.success(null);
            } catch (Exception e) {
                result.error("settings", "无法打开开发者选项，请在系统设置中手动打开无线调试。", null);
            }
            return;
        }
        if (Build.VERSION.SDK_INT < 30) {
            result.error("unsupported", "配对码无线调试需要 Android 11 或以上版本。", null);
            return;
        }
        if (current != null) {
            result.error("busy", "请等待当前操作结束，或先取消当前操作。", null);
            return;
        }
        switch (call.method) {
            case "pair": {
                Integer port = port(call, result);
                Object rawCode = call.argument("code");
                if (port == null) return;
                if (!(rawCode instanceof String) || !((String) rawCode).matches("[0-9]{6}")) {
                    result.error("invalid", "请输入系统显示的 6 位配对码。", null);
                    return;
                }
                String code = (String) rawCode;
                run("pair", result, operation -> {
                    client.pair(port, code, operation.cancelled);
                    return null;
                });
                break;
            }
            case "connect": {
                Integer port = port(call, result);
                if (port != null) run("connect", result, operation -> client.connect(port, operation.cancelled));
                break;
            }
            case "shell": {
                Object value = call.argument("command");
                if (!(value instanceof String) || ((String) value).trim().isEmpty()
                        || ((String) value).length() > 4096 || ((String) value).indexOf('\0') >= 0) {
                    result.error("invalid", "请输入 1–4096 字符的 shell 命令。", null);
                    return;
                }
                String command = (String) value;
                run("shell", result, operation -> client.runShell(command, operation.cancelled));
                break;
            }
            case "disconnect":
                run("disconnect", result, operation -> {
                    client.disconnect();
                    return null;
                });
                break;
            default:
                result.notImplemented();
        }
    }

    private Integer port(MethodCall call, MethodChannel.Result result) {
        Object value = call.argument("port");
        if (!(value instanceof Integer) || (Integer) value < 1 || (Integer) value > 65535) {
            result.error("invalid", "端口必须为 1–65535。", null);
            return null;
        }
        return (Integer) value;
    }

    private void run(String kind, MethodChannel.Result result, Work work) {
        Operation operation = new Operation(kind, result);
        current = operation;
        operation.timeout = () -> {
            if (current == operation) cancelCurrent("timeout");
        };
        main.postDelayed(operation.timeout, 20000);
        worker.execute(() -> {
            Object response = null;
            boolean failed = false;
            operation.thread = Thread.currentThread();
            try {
                if (operation.cancelled.get()) throw new InterruptedException();
                response = work.run(operation);
            } catch (Exception | LinkageError e) {
                // Never send keys, pairing codes, command contents or exception payloads to logs.
                failed = true;
                client.disconnect();
            } finally {
                operation.thread = null;
                if (operation.cancelled.get()) client.disconnect();
                Thread.interrupted();
            }
            Object value = response;
            boolean error = failed;
            main.post(() -> finish(operation, value, error));
        });
    }

    private void finish(Operation operation, Object response, boolean failed) {
        main.removeCallbacks(operation.timeout);
        if (current != operation) return;
        current = null;
        if (!attached) return;
        if (operation.cancelled.get()) {
            boolean timeout = "timeout".equals(operation.cancelReason);
            operation.result.error(operation.cancelReason,
                    timeout ? "操作超时，已断开本地 ADB。请检查端口后重试。" : "操作已取消，本地 ADB 已断开。", null);
        } else if (failed) {
            String message;
            switch (operation.kind) {
                case "pair":
                    message = "配对失败。请保持系统配对码窗口打开，检查配对端口和配对码，然后重试。";
                    break;
                case "connect":
                    message = "连接失败。请确认已配对、无线调试已开启，并填写无线调试首页的连接端口。";
                    break;
                default:
                    message = "ADB 操作失败，连接已断开。请重新连接后重试。";
            }
            operation.result.error("adb_failed", message, null);
        } else {
            operation.result.success(response);
        }
    }

    private void cancelCurrent(String reason) {
        Operation operation = current;
        if (operation == null) return;
        operation.cancelReason = reason;
        operation.cancelled.set(true);
        client.abort();
        Thread thread = operation.thread;
        if (thread != null) thread.interrupt();
        // Also covers cancellation after the worker finished but before its main-thread callback.
        if (!worker.isShutdown()) worker.execute(client::disconnect);
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        attached = false;
        cancelCurrent("cancelled");
        if (current != null) main.removeCallbacks(current.timeout);
        channel.setMethodCallHandler(null);
        client.abort();
        worker.execute(client::disconnect);
        worker.shutdown();
        activity = null;
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) { activity = binding.getActivity(); }
    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) { onAttachedToActivity(binding); }
    @Override
    public void onDetachedFromActivityForConfigChanges() { activity = null; }
    @Override
    public void onDetachedFromActivity() { activity = null; }

    private interface Work { Object run(Operation operation) throws Exception; }

    private static final class Operation {
        final String kind;
        final MethodChannel.Result result;
        final AtomicBoolean cancelled = new AtomicBoolean();
        volatile Thread thread;
        String cancelReason = "cancelled";
        Runnable timeout;

        Operation(String kind, MethodChannel.Result result) {
            this.kind = kind;
            this.result = result;
        }
    }
}
