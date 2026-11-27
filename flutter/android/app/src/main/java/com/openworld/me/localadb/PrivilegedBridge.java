package com.openworld.me.localadb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;

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
    static final class ActivationFailure extends IOException {
        final String code;
        ActivationFailure(String code, String message) { super(message); this.code = code; }
    }
    private static final long RECOVERY_WINDOW_MS = 20000;
    private static final long VERIFY_TIMEOUT_MS = 8000;
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
    // Dies with the app process, not with a Flutter page or a remote-control session.
    private final IBinder appLease = new Binder() {
        @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws android.os.RemoteException {
            if (code == INTERFACE_TRANSACTION) {
                if (reply != null) reply.writeString(PrivilegedProtocol.APP_LEASE);
                return true;
            }
            data.enforceInterface(PrivilegedProtocol.APP_LEASE);
            int caller = Binder.getCallingUid();
            if (caller != 2000 && caller != 0) throw new SecurityException("Lease caller denied");
            if (code != PrivilegedProtocol.CHECK_LEASE) return false;
            String expectedGeneration = data.readString();
            reply.writeNoException();
            // Pure memory check: even a failed one-way STOP is followed by explicit revocation
            // on the next maintenance tick; keeping the app process alive is not authorization.
            reply.writeInt(allowed && generation.equals(expectedGeneration) && ready() ? 1 : 0);
            return true;
        }
    };
    private final int bootCount;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ThreadPoolExecutor verifier = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), task -> { Thread t = new Thread(task, "openworld-privileged-verify"); t.setDaemon(true); return t; });
    private final ScheduledExecutorService deadlines = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread t = new Thread(task, "openworld-privileged-deadline"); t.setDaemon(true); return t;
    });
    private volatile Binding binding;
    private Check checking;
    private IBinder pendingService;
    private volatile boolean allowed, starting;
    private boolean restartRequired, identityRejected, handoffReceived;
    private long recoveryUntil, retryAfter;
    private int verificationFailures;
    private volatile String generation, error = "";
    private String errorCode = "";
    private volatile Runnable listener = () -> { };

    private PrivilegedBridge(Context context) {
        this.context = context;
        records = context.getSharedPreferences("openworld_privileged_v1", Context.MODE_PRIVATE);
        bootCount = readBootCount(context);
        allowed = records.getBoolean("enabled", false);
        generation = records.getString("generation", "");
        int activatedBoot = records.getInt("bootCount", -1);
        if (allowed) {
            restartRequired = activatedBoot >= 0 && bootCount >= 0 && activatedBoot != bootCount;
            if (restartRequired) {
                errorCode = "device_rebooted";
                error = "手机已重启，ADB 服务已结束。请开启无线调试后点击“激活 ADB”；系统仍保留配对时无需重新配对。";
            }
            else recoveryUntil = SystemClock.elapsedRealtime() + RECOVERY_WINDOW_MS;
        }
    }

    private static int readBootCount(Context context) {
        try { return Settings.Global.getInt(context.getContentResolver(), Settings.Global.BOOT_COUNT, -1); }
        catch (RuntimeException unavailable) { return -1; }
    }

    void listen(Runnable listener) { this.listener = listener; changed(); }
    private void changed() { main.post(() -> listener.run()); }

    Binding current() {
        Binding value = binding;
        return allowed && value != null && generation.equals(value.generation)
                && value.service.isBinderAlive() ? value : null;
    }

    boolean ready() { return current() != null; }

    synchronized boolean reconnecting() {
        return allowed && !starting && !ready() && !restartRequired && !identityRejected
                && SystemClock.elapsedRealtime() < recoveryUntil;
    }

    synchronized boolean awaitRecovery(AtomicBoolean cancelled, long deadline) throws InterruptedException {
        while (!ready() && reconnecting() && !cancelled.get()) {
            long remaining = Math.min(deadline, recoveryUntil) - SystemClock.elapsedRealtime();
            if (remaining <= 0) break;
            wait(Math.min(remaining, 100));
        }
        if (cancelled.get() || Thread.currentThread().isInterrupted()) throw new InterruptedException();
        return ready();
    }

    synchronized void addStatus(Map<String, Object> state) {
        Binding value = current();
        state.put("connected", value != null);
        state.put("activationSupported", true);
        state.put("executionBackend", "embedded");
        state.put("activationState", value != null ? "ready" : starting ? "starting" : reconnecting() ? "reconnecting"
                : allowed ? "activation_required" : "inactive");
        state.put("recovering", reconnecting());
        state.put("serviceUid", value == null ? null : value.uid);
        state.put("activationError", error);
        state.put("activationErrorCode", errorCode);
    }

    synchronized Bundle accept(IBinder service, String candidateGeneration, int sourceUid) {
        if ((sourceUid != 2000 && sourceUid != 0) || service == null || !allowed
                || restartRequired || identityRejected || !generation.equals(candidateGeneration)) return handoff(false, false);
        Binding value = current();
        if (value != null) return handoff(value.service.equals(service), value.service.equals(service));
        if (checking != null) return handoff(checking.service.equals(service), false);
        if (pendingService != null && pendingService.isBinderAlive() && !pendingService.equals(service))
            return handoff(false, false);
        pendingService = service;
        handoffReceived = true;
        long now = SystemClock.elapsedRealtime();
        // A temporary verification failure is not revocation. Keep the server alive and retry
        // at a bounded rate, including after the initial visible recovery window has elapsed.
        if (now < retryAfter) return handoff(true, false);
        if (!starting) recoveryUntil = Math.max(recoveryUntil, now + VERIFY_TIMEOUT_MS);
        Check check = new Check(service, candidateGeneration, sourceUid);
        checking = check;
        changed();
        check.timeout = deadlines.schedule(() -> failed(check, "内置服务验证暂时超时，正在等待恢复。", false),
                VERIFY_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        try { verifier.execute(() -> verify(check)); }
        catch (java.util.concurrent.RejectedExecutionException busy) { failed(check, "内置服务验证暂忙，正在等待恢复。", false); }
        return handoff(true, false);
    }

    private Bundle handoff(boolean accepted, boolean verified) {
        Bundle reply = new Bundle();
        reply.putBoolean("accepted", accepted);
        reply.putBoolean("ready", accepted && verified);
        if (accepted && verified) reply.putBinder("lease", appLease);
        return reply;
    }

    private void verify(Check check) {
        Parcel data = Parcel.obtain(), reply = Parcel.obtain();
        Binding verified = null;
        boolean installed = false;
        try {
            if (check.cancelled.get()) return;
            data.writeInterfaceToken(PrivilegedProtocol.SERVICE);
            if (!check.service.transact(PrivilegedProtocol.INFO, data, reply, 0)) throw new SecurityException("Unsupported identity protocol");
            reply.readException();
            int version = reply.readInt(), uid = reply.readInt(), owner = reply.readInt();
            String packageName = reply.readString(), apk = reply.readString(), actualGeneration = reply.readString();
            String component = reply.readString(), authority = reply.readString();
            PrivilegedTarget target = PrivilegedTarget.resolve(context);
            if (version != PrivilegedProtocol.VERSION || uid != check.sourceUid
                    || owner != Process.myUid() || !context.getPackageName().equals(packageName)
                    || !context.getApplicationInfo().sourceDir.equals(apk)
                    || !check.generation.equals(actualGeneration) || !target.component.equals(component)
                    || !target.authority.equals(authority)) throw new SecurityException();
            if (check.cancelled.get()) return;
            check.stream = PrivilegedProtocol.open(check.service, check.lease, "shell,v2,raw:id -u");
            if (check.cancelled.get()) { check.stream.close(); return; }
            Map<String, Object> probe = AdbShell.execute(check.stream, check.cancelled);
            if (!Integer.valueOf(0).equals(probe.get("exitCode"))) throw new IOException("Identity probe unavailable");
            if (!Integer.toString(uid).equals(String.valueOf(probe.get("output")).trim())) throw new SecurityException("Identity mismatch");
            verified = new Binding(check.service, uid, check.generation);
            Binding candidate = verified;
            verified.death = () -> lost(candidate);
            check.service.linkToDeath(verified.death, 0);
            synchronized (this) {
                if (checking != check || check.cancelled.get() || !allowed || !generation.equals(check.generation)) return;
                if (!check.service.isBinderAlive()) throw new IOException("Service died during verification");
                binding = verified; pendingService = null; checking = null; starting = false; error = ""; errorCode = "";
                recoveryUntil = 0; retryAfter = 0; verificationFailures = 0;
                installed = true;
                notifyAll();
            }
            changed();
        } catch (ActivationFailure invalidTarget) {
            failed(check, invalidTarget.getMessage(), !"component_unavailable".equals(invalidTarget.code));
        } catch (SecurityException rejected) {
            failed(check, "ADB 服务身份或组件验证不一致，已拒绝授权。请检查安装包是否完整，再重新激活。", true);
        } catch (Exception | LinkageError failure) {
            failed(check, "暂未完成内置服务验证，正在等待恢复。", false);
        }
        finally {
            if (!installed && verified != null) unlink(verified);
            check.timeout.cancel(false);
            if (check.stream != null) try { check.stream.close(); } catch (IOException ignored) { }
            reply.recycle(); data.recycle();
        }
    }

    private synchronized void failed(Check check, String message, boolean fatal) {
        check.cancelled.set(true);
        if (check.stream != null) try { check.stream.close(); } catch (IOException ignored) { }
        if (checking != check) return;
        checking = null; error = message;
        errorCode = fatal ? "identity_rejected" : "verification_pending";
        if (fatal) {
            identityRejected = true; starting = false; recoveryUntil = 0;
            pendingService = null;
            PrivilegedProtocol.stop(check.service);
        } else {
            verificationFailures = Math.min(verificationFailures + 1, 3);
            retryAfter = SystemClock.elapsedRealtime() + (verificationFailures == 1 ? 1000
                    : verificationFailures == 2 ? 3000 : 30000);
            // Keep the candidate reference for immediate user STOP, even while no check is active.
        }
        notifyAll(); changed();
    }

    private synchronized void lost(Binding expected) {
        if (binding != expected) return;
        binding = null; starting = false;
        restartRequired = true; recoveryUntil = 0;
        errorCode = "service_stopped";
        error = "ADB 服务进程已停止，原因尚未确认。请开启无线调试后点击“激活 ADB”；配对仍有效时无需重新配对。";
        notifyAll(); changed();
    }

    private synchronized String begin(AtomicBoolean cancelled, java.util.function.Consumer<String> onStarting) throws IOException {
        if (cancelled.get() || Thread.currentThread().isInterrupted())
            throw new java.io.InterruptedIOException("Activation cancelled");
        // A delayed connect/discovery request must not replace a service that has just rebound.
        if (ready()) return null;
        disable();
        String next = UUID.randomUUID().toString();
        if (!records.edit().putBoolean("enabled", true).putString("generation", next).putInt("bootCount", bootCount).commit())
            throw new ActivationFailure("record_failed", "无法保存 ADB 激活授权，请检查应用存储后重试。");
        generation = next; allowed = true; starting = true; error = ""; errorCode = ""; handoffReceived = false;
        restartRequired = false; identityRejected = false; verificationFailures = 0; retryAfter = 0;
        onStarting.accept(next);
        if (cancelled.get() || Thread.currentThread().isInterrupted()) {
            disable();
            throw new java.io.InterruptedIOException("Activation cancelled");
        }
        changed();
        return next;
    }

    synchronized void block() {
        allowed = false; starting = false;
        recoveryUntil = 0; retryAfter = 0;
        Binding old = binding; binding = null;
        Check check = checking; checking = null;
        IBinder pending = pendingService; pendingService = null;
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
        if (pending != null && (check == null || !pending.equals(check.service))
                && (old == null || !pending.equals(old.service))) PrivilegedProtocol.stop(pending);
        notifyAll(); changed();
    }

    synchronized void disable() throws IOException {
        block();
        generation = "";
        if (!records.edit().putBoolean("enabled", false).remove("generation").remove("bootCount").commit()) {
            error = "停用记录保存失败，请保持应用打开并重试停用。";
            errorCode = "stop_record_failed";
            throw new ActivationFailure(errorCode, error);
        }
        error = ""; errorCode = "";
    }

    // Serialize cancellation against the verifier's ready commit. Once verified, the capability
    // belongs to the app, not to the PC request that happened to activate it. Explicit STOP uses disable().
    synchronized void cancelActivation(String activation, boolean persist) throws IOException {
        if (activation == null || !generation.equals(activation) || ready()) return;
        String detail = error.isEmpty() ? "激活尚未完成，请求已取消或超时。配对记录保留，可再次点击“激活 ADB”。" : error;
        String code = errorCode.isEmpty() ? "activation_cancelled" : errorCode;
        if ("verification_pending".equals(code)) {
            code = "activation_cancelled";
            detail = "权限验证尚未完成，本次激活已取消或超时。配对记录保留，请保持无线调试开启后重试激活。";
        }
        if (persist) disable(); else block();
        error = detail; errorCode = code;
        changed();
    }

    synchronized boolean activatedBy(String activation) {
        return activation != null && generation.equals(activation) && ready();
    }

    void activate(LocalAdbClient bootstrap, AtomicBoolean cancelled, long deadline,
                  java.util.function.Consumer<String> onStarting) throws Exception {
        PrivilegedTarget target;
        try { target = PrivilegedTarget.resolve(context); }
        catch (ActivationFailure invalidTarget) {
            synchronized (this) {
                if (!ready()) { error = invalidTarget.getMessage(); errorCode = invalidTarget.code; changed(); }
            }
            throw invalidTarget;
        }
        String activation;
        try { activation = begin(cancelled, onStarting); }
        catch (ActivationFailure recordFailure) {
            synchronized (this) {
                if (!ready()) { error = recordFailure.getMessage(); errorCode = recordFailure.code; changed(); }
            }
            throw recordFailure;
        }
        if (activation == null) return;
        try {
            String apk = context.getApplicationInfo().sourceDir;
            String command = "trap '' HUP; CLASSPATH=" + quote(apk)
                    + " /system/bin/app_process /system/bin --nice-name=openworld_privileged_" + Process.myUid()
                    + " " + quote(OpenWorldPrivilegedService.class.getName()) + " " + quote(context.getPackageName())
                    + " " + Process.myUid() + " " + quote(apk) + " " + quote(activation)
                    + " " + quote(target.component) + " " + quote(target.authority)
                    + " </dev/null >/dev/null 2>&1 &";
            Map<String, Object> result = bootstrap.runShell(command, cancelled);
            if (!Integer.valueOf(0).equals(result.get("exitCode")))
                throw new ActivationFailure("starter_failed", "无线调试已连接，但 ADB 服务启动命令未完成。请保持无线调试开启后重试激活。");
            synchronized (this) {
                while (!cancelled.get() && !Thread.currentThread().isInterrupted() && allowed
                        && generation.equals(activation) && !ready()) {
                    long remaining = deadline - SystemClock.elapsedRealtime();
                    if (remaining <= 0 || (!starting && !error.isEmpty())) break;
                    wait(Math.min(remaining, 100));
                }
                if (activatedBy(activation)) return;
                if (!error.isEmpty() && !"verification_pending".equals(errorCode))
                    throw new ActivationFailure(errorCode, error);
                if (cancelled.get() || Thread.currentThread().isInterrupted())
                    throw new ActivationFailure("activation_cancelled", "激活尚未完成，请求已取消或超时。请保持连接后重试激活。");
                errorCode = handoffReceived ? "verification_timeout" : "handoff_timeout";
                error = handoffReceived ? "ADB 服务已回传，但未在时限内通过权限验证，本次激活已结束。请保持无线调试开启后重试激活。"
                                : "无线调试已连接，但未收到内置 ADB 服务回传。服务可能未启动或回传被系统阻止；请检查安装包组件，并保持开发者选项和 USB 调试开启后重试。";
                throw new ActivationFailure(errorCode, error);
            }
        } catch (Exception | LinkageError failure) {
            synchronized (this) {
                if (activatedBy(activation)) return;
                if (generation.equals(activation) && error.isEmpty()) {
                    errorCode = failure instanceof ActivationFailure ? ((ActivationFailure) failure).code : "starter_interrupted";
                    error = failure instanceof ActivationFailure ? failure.getMessage()
                            : "ADB 服务启动未完成，启动连接已中断或不可用。配对记录保留，请保持无线调试开启后重试激活。";
                }
            }
            if (failure instanceof ActivationFailure) throw (ActivationFailure) failure;
            throw new ActivationFailure("starter_interrupted", "ADB 服务启动未完成，请保持无线调试开启后重试激活；配对记录保留。");
        } finally {
            cancelActivation(activation, true);
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
