package com.openworld.me.localadb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.SystemClock;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** Process-owned Binder capability, independently recoverable from a still-running shell server. */
final class PrivilegedBridge {
    private static PrivilegedBridge instance;
    static synchronized PrivilegedBridge get(Context context) {
        if (instance == null) instance = new PrivilegedBridge(context.getApplicationContext());
        return instance;
    }

    static final class Binding {
        final IBinder service;
        final int uid;
        final String generation;
        IBinder.DeathRecipient death;
        Binding(IBinder service, int uid, String generation) {
            this.service = service; this.uid = uid; this.generation = generation;
        }
    }

    private final Context context;
    private final SharedPreferences records;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ThreadPoolExecutor verifier = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), task -> { Thread t = new Thread(task, "openworld-privileged-verify"); t.setDaemon(true); return t; });
    private final ScheduledExecutorService deadlines = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread t = new Thread(task, "openworld-privileged-deadline"); t.setDaemon(true); return t;
    });
    private volatile Binding binding;
    private Check checking;
    private volatile boolean allowed, starting;
    private volatile String generation, error = "";
    private volatile Runnable listener = () -> { };

    private PrivilegedBridge(Context context) {
        this.context = context;
        records = context.getSharedPreferences("openworld_privileged_v1", Context.MODE_PRIVATE);
        allowed = records.getBoolean("enabled", false);
        generation = records.getString("generation", "");
    }

    void listen(Runnable listener) { this.listener = listener; changed(); }
    private void changed() { main.post(() -> listener.run()); }

    Binding current() {
        Binding value = binding;
        return allowed && value != null && generation.equals(value.generation)
                && value.service.isBinderAlive() ? value : null;
    }

    boolean ready() { return current() != null; }

    synchronized void addStatus(Map<String, Object> state) {
        Binding value = current();
        state.put("connected", value != null);
        state.put("activationSupported", true);
        state.put("executionBackend", "embedded");
        state.put("activationState", value != null ? "ready" : starting ? "starting"
                : allowed ? "activation_required" : "inactive");
        state.put("serviceUid", value == null ? null : value.uid);
        state.put("activationError", error);
    }

    synchronized boolean accept(IBinder service, String candidateGeneration, int sourceUid) {
        if ((sourceUid != 2000 && sourceUid != 0) || service == null || !allowed
                || !generation.equals(candidateGeneration)) return false;
        Binding value = current();
        if (value != null) return value.service.equals(service);
        if (checking != null) return checking.service.equals(service);
        if (!starting && !error.isEmpty()) return false;
        Check check = new Check(service, candidateGeneration, sourceUid);
        checking = check;
        starting = true;
        changed();
        check.timeout = deadlines.schedule(() -> failed(check, "特权服务验证超时，请重新激活。"), 8, TimeUnit.SECONDS);
        try { verifier.execute(() -> verify(check)); }
        catch (java.util.concurrent.RejectedExecutionException busy) { failed(check, "特权服务验证忙，请稍后重试。"); }
        return true;
    }

    private void verify(Check check) {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        Binding verified = null;
        boolean installed = false;
        try {
            data.writeInterfaceToken(PrivilegedProtocol.SERVICE);
            if (!check.service.transact(PrivilegedProtocol.INFO, data, reply, 0)) throw new IOException();
            reply.readException();
            int version = reply.readInt(), uid = reply.readInt(), owner = reply.readInt();
            String packageName = reply.readString(), apk = reply.readString(), actualGeneration = reply.readString();
            if (version != PrivilegedProtocol.VERSION || uid != check.sourceUid
                    || owner != Process.myUid() || !context.getPackageName().equals(packageName)
                    || !context.getApplicationInfo().sourceDir.equals(apk)
                    || !check.generation.equals(actualGeneration)) throw new SecurityException();
            if (check.cancelled.get()) return;
            check.stream = PrivilegedProtocol.open(check.service, check.lease, "shell,v2,raw:id -u");
            if (check.cancelled.get()) { check.stream.close(); return; }
            Map<String, Object> probe = AdbShell.execute(check.stream, check.cancelled);
            if (!Integer.valueOf(0).equals(probe.get("exitCode"))
                    || !Integer.toString(uid).equals(String.valueOf(probe.get("output")).trim())) throw new IOException();
            verified = new Binding(check.service, uid, check.generation);
            Binding candidate = verified;
            verified.death = () -> lost(candidate);
            check.service.linkToDeath(verified.death, 0);
            synchronized (this) {
                if (checking != check || check.cancelled.get() || !allowed || !generation.equals(check.generation)) return;
                if (!check.service.isBinderAlive()) throw new IOException("Service died during verification");
                binding = verified; checking = null; starting = false; error = "";
                installed = true;
                notifyAll();
            }
            changed();
        } catch (Exception | LinkageError failure) { failed(check, "内置特权服务身份或 Shell 验证失败，请重新激活。"); }
        finally {
            if (!installed && verified != null) unlink(verified);
            check.timeout.cancel(false);
            if (check.stream != null) try { check.stream.close(); } catch (IOException ignored) { }
            reply.recycle(); data.recycle();
        }
    }

    private synchronized void failed(Check check, String message) {
        check.cancelled.set(true);
        if (check.stream != null) try { check.stream.close(); } catch (IOException ignored) { }
        if (checking != check) return;
        checking = null; starting = false; error = message;
        PrivilegedProtocol.stop(check.service);
        notifyAll(); changed();
    }

    private synchronized void lost(Binding expected) {
        if (binding != expected) return;
        binding = null; starting = false;
        error = "特权服务已停止，需要重新激活；已有配对记录仍可复用。";
        notifyAll(); changed();
    }

    private synchronized String begin(AtomicBoolean cancelled) throws IOException {
        if (cancelled.get() || Thread.currentThread().isInterrupted())
            throw new java.io.InterruptedIOException("Activation cancelled");
        disable();
        String next = UUID.randomUUID().toString();
        if (!records.edit().putBoolean("enabled", true).putString("generation", next).commit())
            throw new IOException("Activation record unavailable");
        generation = next; allowed = true; starting = true; error = "";
        if (cancelled.get() || Thread.currentThread().isInterrupted()) {
            disable();
            throw new java.io.InterruptedIOException("Activation cancelled");
        }
        changed();
        return next;
    }

    synchronized void block() {
        allowed = false; starting = false;
        Binding old = binding; binding = null;
        Check check = checking; checking = null;
        if (check != null) {
            check.cancelled.set(true);
            check.timeout.cancel(false);
            if (check.stream != null) try { check.stream.close(); } catch (IOException ignored) { }
            PrivilegedProtocol.stop(check.service);
        }
        if (old != null) {
            unlink(old);
            PrivilegedProtocol.stop(old.service);
        }
        notifyAll(); changed();
    }

    synchronized void disable() throws IOException {
        block();
        generation = "";
        if (!records.edit().putBoolean("enabled", false).remove("generation").commit()) {
            error = "停用记录保存失败，请保持应用打开并重试停用。";
            throw new IOException("Revocation record unavailable");
        }
        error = "";
    }

    void activate(LocalAdbClient bootstrap, AtomicBoolean cancelled, long deadline) throws Exception {
        String activation = begin(cancelled);
        boolean success = false;
        try {
            String apk = context.getApplicationInfo().sourceDir;
            String command = "trap '' HUP; CLASSPATH=" + quote(apk)
                    + " /system/bin/app_process /system/bin --nice-name=openworld_privileged_" + Process.myUid()
                    + " com.openworld.me.localadb.OpenWorldPrivilegedService " + quote(context.getPackageName())
                    + " " + Process.myUid() + " " + quote(apk) + " " + quote(activation)
                    + " </dev/null >/dev/null 2>&1 &";
            Map<String, Object> result = bootstrap.runShell(command, cancelled);
            if (!Integer.valueOf(0).equals(result.get("exitCode"))) throw new IOException("Starter failed");
            synchronized (this) {
                while (!cancelled.get() && !Thread.currentThread().isInterrupted() && allowed
                        && generation.equals(activation) && !ready()) {
                    long remaining = deadline - SystemClock.elapsedRealtime();
                    if (remaining <= 0 || (!starting && !error.isEmpty())) break;
                    wait(Math.min(remaining, 100));
                }
                success = !cancelled.get() && !Thread.currentThread().isInterrupted()
                        && generation.equals(activation) && ready();
            }
            if (!success) throw new IOException("内置服务未激活。请保持开发者选项和 USB 调试开启后重试；部分系统会终止 Shell 服务。");
        } finally {
            synchronized (this) {
                if (!success && generation.equals(activation)) {
                    String detail = error;
                    disable();
                    error = detail.isEmpty() ? "内置服务未激活，请重试。" : detail;
                    changed();
                }
            }
        }
    }

    private static String quote(String value) { return "'" + value.replace("'", "'\\''") + "'"; }

    private static void unlink(Binding value) {
        if (value.death != null) try { value.service.unlinkToDeath(value.death, 0); }
        catch (RuntimeException ignored) { /* Death may race the registration/teardown. */ }
    }

    private static final class Check {
        final IBinder service, lease = new Binder();
        final String generation;
        final int sourceUid;
        final AtomicBoolean cancelled = new AtomicBoolean();
        volatile AdbChannel stream;
        java.util.concurrent.ScheduledFuture<?> timeout;
        Check(IBinder service, String generation, int sourceUid) {
            this.service = service; this.generation = generation; this.sourceUid = sourceUid;
        }
    }
}
