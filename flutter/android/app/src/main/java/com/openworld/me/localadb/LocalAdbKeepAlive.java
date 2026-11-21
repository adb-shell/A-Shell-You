package com.openworld.me.localadb;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;

import java.util.Map;

/** Main-looper owned, at most three idempotent battery-exemption attempts per verified connection. */
final class LocalAdbKeepAlive {
    private final PowerManager power;
    private final String packageName;
    private final LocalAdbCommands commands;
    private final Runnable changed;
    private boolean completed, busy;
    private int attempts;
    private long retryAt;
    private String error = "";

    LocalAdbKeepAlive(Context context, LocalAdbCommands commands, Runnable changed) {
        power = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        packageName = context.getPackageName();
        this.commands = commands;
        this.changed = changed;
    }

    void update(boolean ready) {
        if (!ready) {
            // Do not let an old in-flight callback grant a fresh set of retries to a new session.
            if (!busy) { completed = false; attempts = 0; retryAt = 0; }
            return;
        }
        // A cancelled old command must finish releasing its private connection first.
        // The manager's existing health loop can then handle the new connection.
        if (completed || busy || attempts >= 3 || SystemClock.elapsedRealtime() < retryAt) return;
        if (Boolean.TRUE.equals(isExempt())) { completed = true; error = ""; return; }
        attempts++;
        error = "";
        busy = true;
        String argument = "'+" + packageName.replace("'", "'\\''") + "'";
        commands.submit("adb-keep-alive", "",
                "dumpsys deviceidle whitelist " + argument, new LocalAdbManager.Reply() {
                    @Override public void success(Object value) {
                        boolean commandOk = value instanceof Map
                                && Integer.valueOf(0).equals(((Map<?, ?>) value).get("exitCode"));
                        finish(commandOk ? "系统未确认电池优化豁免，请检查系统电池设置。"
                                : "保活命令未成功执行，请检查系统电池优化设置。");
                    }
                    @Override public void failure(String code, String message) {
                        finish(message);
                    }
                });
    }

    private void finish(String failure) {
        busy = false;
        completed = Boolean.TRUE.equals(isExempt());
        if (!completed && attempts < 3) {
            retryAt = SystemClock.elapsedRealtime() + (attempts == 1 ? 5000 : 30000);
            error = "暂未取得电池优化豁免，将自动重试。";
        } else error = completed ? "" : failure;
        changed.run();
    }

    private Boolean isExempt() {
        if (Build.VERSION.SDK_INT < 23 || power == null) return null;
        try { return power.isIgnoringBatteryOptimizations(packageName); }
        catch (RuntimeException ignored) { return null; }
    }

    void addStatus(Map<String, Object> status) {
        Boolean exempt = isExempt();
        status.put("keepAliveSupported", Build.VERSION.SDK_INT >= 30);
        status.put("keepAliveExempt", exempt);
        status.put("keepAliveBusy", busy);
        status.put("keepAliveError", Boolean.TRUE.equals(exempt) ? "" : error);
    }
}
