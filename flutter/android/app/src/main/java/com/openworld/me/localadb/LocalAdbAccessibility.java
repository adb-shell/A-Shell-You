package com.openworld.me.localadb;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.os.UserManager;
import android.provider.Settings;

import com.openworld.me.nZW99cdXQ0COhB2o;

import java.util.HashMap;
import java.util.Map;

/** Only this installed application's service. All state is owned by the main looper. */
final class LocalAdbAccessibility {
    private static final String PAUSED = "pausedComponent";
    private final Context context;
    private final Handler main;
    private final LocalAdbCommands commands;
    private final Runnable changed;
    private final SharedPreferences records;
    private ComponentName component;
    private String unavailable = "";
    private String lastError = "";
    private Pending pending;

    LocalAdbAccessibility(Context context, Handler main, LocalAdbCommands commands, Runnable changed) {
        this.context = context;
        this.main = main;
        this.commands = commands;
        this.changed = changed;
        // Independent of pairing records: revoking ADB must not restore Accessibility.
        records = context.getSharedPreferences("local_adb_accessibility_v1", Context.MODE_PRIVATE);
        try {
            if (Build.VERSION.SDK_INT < 30) throw new IllegalStateException("需要 Android 11 或以上版本。");
            UserManager users = (UserManager) context.getSystemService(Context.USER_SERVICE);
            if (users == null || users.isManagedProfile()
                    || Build.VERSION.SDK_INT >= 33 && users.isProfile()) {
                throw new IllegalStateException("当前资料共享父用户的无障碍配置，请在系统设置中管理。");
            }
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
            if (info.services != null) for (ServiceInfo service : info.services) {
                if (!context.getPackageName().equals(service.packageName)
                        || !Manifest.permission.BIND_ACCESSIBILITY_SERVICE.equals(service.permission)
                        || service.metaData == null
                        || !service.metaData.containsKey(AccessibilityService.SERVICE_META_DATA)) continue;
                if (component != null) throw new IllegalStateException("本应用声明了多个无障碍服务，不能唯一确定目标。");
                component = new ComponentName(service.packageName, service.name);
            }
            if (component == null) throw new IllegalStateException("未找到本应用注册的无障碍服务。");
        } catch (Exception error) {
            component = null;
            unavailable = error instanceof IllegalStateException ? error.getMessage() : "无法读取本应用无障碍服务信息。";
        }
    }

    static boolean handles(String method) {
        return "accessibilityEnable".equals(method) || "accessibilityDisable".equals(method)
                || "accessibilityPause".equals(method) || "accessibilityResume".equals(method);
    }

    private Boolean enabled() {
        if (component == null) return null;
        try {
            String setting = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (setting != null) for (String item : setting.split(":")) {
                if (component.equals(ComponentName.unflattenFromString(item))) return true;
            }
            return false;
        } catch (RuntimeException error) { return null; }
    }

    private boolean running() {
        // Read existing lifecycle evidence; never invoke or change the original service's controls.
        AccessibilityService service = nZW99cdXQ0COhB2o.Companion.getCtx();
        return service != null && component != null
                && component.equals(new ComponentName(service, service.getClass()));
    }

    private boolean hasPauseRecord() {
        return component != null && component.flattenToString().equals(records.getString(PAUSED, ""));
    }

    void addStatus(Map<String, Object> value) {
        Boolean enabled = enabled();
        value.put("accessibilityControlSupported", component != null);
        value.put("accessibilityComponent", component == null ? "" : component.flattenToString());
        value.put("accessibilityEnabled", enabled);
        value.put("accessibilityRunning", component == null ? null : running());
        value.put("accessibilityPaused", hasPauseRecord() && Boolean.FALSE.equals(enabled));
        value.put("accessibilityBusy", pending != null);
        value.put("accessibilityError", component == null ? unavailable : lastError);
    }

    void execute(String owner, String requestId, int port, String method, LocalAdbManager.Reply reply) {
        if (component == null) { reply.failure("unsupported", unavailable); return; }
        if (pending != null) { reply.failure("busy", "本应用无障碍操作正在执行，请稍后重试。"); return; }
        Boolean enabled = enabled();
        if (enabled == null) { reply.failure("unavailable", "无法读取本应用无障碍配置，未进行修改。"); return; }
        if ("accessibilityResume".equals(method) && !hasPauseRecord()) {
            reply.failure("not_paused", "本应用没有可恢复的暂停记录。"); return;
        }
        if ("accessibilityPause".equals(method) && !enabled) {
            Map<String, Object> result = new HashMap<>();
            result.put("message", hasPauseRecord() ? "本应用无障碍已暂停。" : "本应用无障碍原本未启用，无需暂停。");
            reply.success(result);
            return;
        }
        if ("accessibilityPause".equals(method)
                && !records.edit().putString(PAUSED, component.flattenToString()).commit()) {
            reply.failure("storage", "无法保存暂停记录，未修改无障碍配置。"); return;
        }
        Pending job = new Pending(owner, requestId, method, reply);
        pending = job;
        lastError = "";
        changed.run();
        // A separate command socket; media, touch, identity and voice are not stopped.
        commands.submit(owner, requestId, port, command(job.enable), new LocalAdbManager.Reply() {
            @Override public void success(Object value) {
                if (pending != job) return;
                if (job.cancelled) { finish(job, "cancelled", "操作已取消，请以当前无障碍状态为准。"); return; }
                Map<?, ?> result = value instanceof Map ? (Map<?, ?>) value : new HashMap<>();
                Object exit = result.get("exitCode");
                if (!(exit instanceof Number) || ((Number) exit).intValue() != 0 || Boolean.TRUE.equals(result.get("truncated"))) {
                    String output = String.valueOf(result.get("output"));
                    if (output.length() > 1000) output = output.substring(0, 1000);
                    finish(job, "settings_failed", "修改本应用无障碍配置失败（退出码 " + exit + "）：" + output);
                    return;
                }
                job.deadline = SystemClock.elapsedRealtime() + 5000;
                job.verify = () -> verify(job);
                verify(job);
            }
            @Override public void failure(String code, String message) {
                if (pending == job) finish(job, code, message);
            }
        });
    }

    private void verify(Pending job) {
        if (pending != job) return;
        Boolean enabled = enabled();
        boolean running = running();
        // An explicit enable/disable/resume supersedes pause as soon as the setting
        // takes effect, even if the OS subsequently takes too long to bind/unbind.
        if (!"accessibilityPause".equals(job.method) && enabled != null
                && enabled == job.enable && hasPauseRecord()
                && !records.edit().remove(PAUSED).commit()) {
            finish(job, "storage", "无障碍配置已改变，但暂停记录清理失败，请刷新状态后重试。"); return;
        }
        if (enabled != null && enabled == job.enable && running == job.enable) {
            finish(job, null, "accessibilityPause".equals(job.method) ? "本应用无障碍已暂停，可点击恢复。"
                    : job.enable ? "本应用无障碍已启用，服务已连接。" : "本应用无障碍已关闭。");
        } else if (SystemClock.elapsedRealtime() >= job.deadline) {
            finish(job, "not_applied", enabled == null ? "无法确认系统无障碍配置，请刷新状态。"
                    : enabled != job.enable ? "系统未保持请求的无障碍配置，请检查系统限制或其他设置操作。"
                    : job.enable ? "配置已启用，但无障碍服务尚未连接，请检查系统限制或应用服务错误。"
                    : "配置已关闭，但无障碍服务尚未退出，请刷新状态确认。");
        } else {
            changed.run();
            main.postDelayed(job.verify, 150);
        }
    }

    private void finish(Pending job, String error, String message) {
        if (job.verify != null) main.removeCallbacks(job.verify);
        // A lost/cancelled reply does not prove that the system write was rolled back.
        // Keep the pause record; paused is only reported if the real setting is disabled.
        pending = null;
        lastError = error == null ? "" : message;
        changed.run();
        if (error != null) job.reply.failure(error, message);
        else {
            Map<String, Object> result = new HashMap<>();
            result.put("message", message);
            job.reply.success(result);
        }
    }

    void cancel(String owner, String requestId) {
        Pending job = pending;
        if (job == null || owner != null && !owner.equals(job.owner)
                || requestId != null && !requestId.equals(job.requestId)) return;
        job.cancelled = true;
        // Running/queued commands are cancelled by LocalAdbCommands. Hold the lock until
        // its callback, so a late system write cannot race with another permissions action.
        if (job.verify != null) finish(job, "cancelled", "操作已取消，请以当前无障碍状态为准。");
    }

    private String command(boolean enable) {
        // Android assigns UID = userId * 100000 + appId. Do not target foreground user or user 0.
        String settings = "settings --user " + (Process.myUid() / 100000);
        String full = quote(component.flattenToString());
        String shortName = quote(component.flattenToShortString());
        // Read at execution time, remove only this component (both Android spellings),
        // preserve all other entries/order, then optionally append our full component.
        // A concurrent external settings writer is detected before put; never replay a saved list.
        return "set -f\nold=$(" + settings + " get secure enabled_accessibility_services) || exit 1\n"
                + "case \"$old\" in null) old='' ;; esac\nnew=''\nIFS=:\n"
                + "for item in $old; do\ncase \"$item\" in " + full + "|" + shortName + "|'') continue ;; esac\n"
                + "new=${new:+$new:}$item\ndone\nunset IFS\n"
                + (enable ? "new=${new:+$new:}" + full + "\n" : "")
                + "check=$(" + settings + " get secure enabled_accessibility_services) || exit 1\n"
                + "case \"$check\" in null) check='' ;; esac\n"
                + "[ \"$check\" = \"$old\" ] || { echo 'Accessibility settings changed; retry'; exit 75; }\n"
                + "[ \"$new\" = \"$old\" ] || " + settings + " put secure enabled_accessibility_services \"$new\"\n";
    }

    private static String quote(String value) { return "'" + value.replace("'", "'\\''") + "'"; }

    private static final class Pending {
        final String owner, requestId, method;
        final boolean enable;
        final LocalAdbManager.Reply reply;
        boolean cancelled;
        long deadline;
        Runnable verify;
        Pending(String owner, String requestId, String method, LocalAdbManager.Reply reply) {
            this.owner = owner; this.requestId = requestId; this.method = method; this.reply = reply;
            enable = "accessibilityEnable".equals(method) || "accessibilityResume".equals(method);
        }
    }
}
