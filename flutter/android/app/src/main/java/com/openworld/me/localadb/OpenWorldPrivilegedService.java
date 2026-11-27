package com.openworld.me.localadb;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.system.Os;
import android.system.OsConstants;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * App-private Shizuku-style shell server. Started explicitly through paired ADB, not an Android
 * Service and not a root exploit. Lifecycle/Binder delivery derives from Shizuku (see assets notice).
 * Each data stream is a descriptor; privileged operations never execute on the app's UID.
 */
public final class OpenWorldPrivilegedService extends Binder {
    private final int ownerUid;
    private final String packageName, apk, generation, component, authority;
    private final PrivilegedSystem system;
    private final Handler main;
    private final Set<Channel> channels = new HashSet<>();
    private final AtomicBoolean stopping = new AtomicBoolean();
    private final AtomicBoolean maintenancePending = new AtomicBoolean();
    private final ExecutorService maintenance = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "openworld-privileged-maintenance");
        thread.setDaemon(true);
        return thread;
    });
    private IBinder appLease;
    private IBinder.DeathRecipient appDeath;
    // Uniqueness guard only: no commands/data are accepted over this socket.
    private final LocalServerSocket singleton;

    public static void main(String[] args) {
        try {
            if (args.length != 6 || (Process.myUid() != 2000 && Process.myUid() != 0)) return;
            if (!args[3].matches("[a-f0-9-]{36}")) return;
            try { Os.setsid(); } catch (Exception ignored) { }
            Looper.prepareMainLooper();
            OpenWorldPrivilegedService service = new OpenWorldPrivilegedService(
                    args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], args[5]);
            service.start();
            Looper.loop();
        } catch (Throwable ignored) {
            // Startup failure is reported by the bounded app-side activation handshake, not logs
            // containing command lines or device/installation identifiers.
        }
        System.exit(0);
    }

    private OpenWorldPrivilegedService(String packageName, int ownerUid, String apk,
                                       String generation, String component, String authority) throws Exception {
        this.packageName = packageName; this.ownerUid = ownerUid;
        this.apk = apk; this.generation = generation;
        this.component = component; this.authority = authority;
        system = new PrivilegedSystem(packageName, ownerUid, apk, component, authority);
        main = new Handler(Looper.getMainLooper());
        singleton = acquireSingleton(ownerUid);
        attachInterface(null, PrivilegedProtocol.SERVICE);
    }

    private static LocalServerSocket acquireSingleton(int uid) throws Exception {
        // STOP is asynchronous. An old process may also need one 5s delivery cycle to learn
        // that its generation was revoked while the app was being recreated.
        long deadline = SystemClock.elapsedRealtime() + 6000;
        while (true) {
            try { return new LocalServerSocket("openworld.privileged." + uid); }
            catch (IOException busy) {
                if (SystemClock.elapsedRealtime() >= deadline) throw busy;
                Thread.sleep(100);
            }
        }
    }

    private void start() {
        // Provider/system Binder calls can stall while the app or system is being recreated.
        // Coalesce all triggers into one worker so the main looper can always process STOP.
        // Registration is best-effort and happens once. A stuck optional observer registration
        // must not block the mandatory periodic handoff/authorization worker.
        thread("openworld-privileged-observers", () -> system.observe(this::requestMaintenance));
        requestMaintenance();
        main.postDelayed(new Runnable() {
            @Override public void run() {
                if (stopping.get()) return;
                requestMaintenance();
                main.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private void requestMaintenance() {
        if (stopping.get() || !maintenancePending.compareAndSet(false, true)) return;
        try {
            maintenance.execute(() -> {
                try {
                    if (stopping.get()) return;
                    system.verifyInstallation();
                    IBinder lease = currentClient();
                    if (lease == null) deliver();
                    else {
                        try {
                            if (!PrivilegedProtocol.checkLease(lease, generation)) main.post(this::shutdown);
                        } catch (Exception | LinkageError unavailable) {
                            // Retrying handoff is safe; an absent reply alone is not revocation.
                            forgetClient();
                            deliver();
                        }
                    }
                } catch (PrivilegedSystem.InstallationChanged changed) {
                    main.post(this::shutdown);
                } catch (Exception | LinkageError unavailable) {
                    // A failed system query cannot prove uninstall/replacement. Refuse individual
                    // OPENs until identity can be checked, but preserve the existing shell process.
                } finally { maintenancePending.set(false); }
            });
        } catch (java.util.concurrent.RejectedExecutionException closed) {
            maintenancePending.set(false);
        }
    }

    private void deliver() {
        if (stopping.get() || !system.isRunning()) return;
        try {
            Bundle reply = system.send(this, generation);
            if (reply != null && !reply.getBoolean("accepted", false)) main.post(this::shutdown);
            else if (reply != null && reply.getBoolean("ready", false)) rememberClient(reply.getBinder("lease"));
        } catch (PrivilegedSystem.InstallationChanged changed) { main.post(this::shutdown); }
        catch (Exception | LinkageError ignored) { /* A transient app restart is not revocation. */ }
    }

    private synchronized IBinder currentClient() {
        return appLease != null && appLease.isBinderAlive() ? appLease : null;
    }

    private synchronized void rememberClient(IBinder lease) throws RemoteException {
        if (lease == null || stopping.get() || lease.equals(appLease)) return;
        forgetClient();
        IBinder.DeathRecipient death = () -> {
            synchronized (OpenWorldPrivilegedService.this) {
                if (!lease.equals(appLease)) return;
                appLease = null; appDeath = null;
            }
            requestMaintenance();
        };
        lease.linkToDeath(death, 0);
        appLease = lease; appDeath = death;
    }

    private synchronized void forgetClient() {
        if (appLease != null && appDeath != null) try { appLease.unlinkToDeath(appDeath, 0); }
        catch (RuntimeException ignored) { }
        appLease = null; appDeath = null;
    }

    private void enforceOwner() {
        if (Binder.getCallingUid() != ownerUid || stopping.get()) throw new SecurityException("Caller denied");
        try { system.verifyInstallation(); }
        catch (PrivilegedSystem.InstallationChanged changed) {
            main.post(this::shutdown);
            throw changed;
        } catch (Exception unavailable) { throw new IllegalStateException("Installation verification unavailable"); }
    }

    @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) return super.onTransact(code, data, reply, flags);
        data.enforceInterface(PrivilegedProtocol.SERVICE);
        // Revocation only reduces privileges. The owning full UID can always STOP even when
        // PackageManager is temporarily unavailable; execution still requires installation proof.
        if (code == PrivilegedProtocol.STOP) {
            if (Binder.getCallingUid() != ownerUid) throw new SecurityException("Caller denied");
            main.post(this::shutdown);
            return true;
        }
        enforceOwner();
        if (code == PrivilegedProtocol.INFO) {
            reply.writeNoException();
            reply.writeInt(PrivilegedProtocol.VERSION);
            reply.writeInt(Process.myUid());
            reply.writeInt(ownerUid);
            reply.writeString(packageName);
            reply.writeString(apk);
            reply.writeString(generation);
            reply.writeString(component);
            reply.writeString(authority);
            return true;
        }
        if (code != PrivilegedProtocol.OPEN) return super.onTransact(code, data, reply, flags);
        String destination = data.readString();
        IBinder lease = data.readStrongBinder();
        if (destination == null || destination.length() > 65535 || destination.indexOf('\0') >= 0
                || lease == null) throw new IllegalArgumentException("Invalid channel");
        Channel channel = null;
        try {
            synchronized (channels) {
                if (stopping.get() || channels.size() >= 24) throw new IOException("Channel limit");
                channel = new Channel(lease);
                channels.add(channel);
            }
            channel.open(destination);
            reply.writeNoException();
            reply.writeStrongBinder(channel);
            reply.writeInt(1);
            channel.peer.writeToParcel(reply, 0);
            channel.peer.close();
            channel.peer = null;
            channel.begin();
            return true;
        } catch (Exception error) {
            if (channel != null) channel.close();
            throw new IllegalStateException("Privileged stream unavailable");
        }
    }

    private void shutdown() {
        if (!stopping.compareAndSet(false, true)) return;
        maintenance.shutdownNow();
        forgetClient();
        Channel[] current;
        synchronized (channels) { current = channels.toArray(new Channel[0]); }
        for (Channel channel : current) channel.close();
        long deadline = SystemClock.elapsedRealtime() + 1000;
        main.post(new Runnable() {
            @Override public void run() {
                boolean empty;
                synchronized (channels) { empty = channels.isEmpty(); }
                if (!empty && SystemClock.elapsedRealtime() < deadline) {
                    main.postDelayed(this, 50);
                    return;
                }
                try { singleton.close(); } catch (IOException ignored) { }
                System.exit(0);
            }
        });
    }

    private final class Channel extends Binder implements IBinder.DeathRecipient {
        private final IBinder lease;
        private final AtomicBoolean closed = new AtomicBoolean();
        private final AtomicBoolean processStopping = new AtomicBoolean();
        private final java.util.concurrent.atomic.AtomicInteger workers = new java.util.concurrent.atomic.AtomicInteger();
        private ParcelFileDescriptor local, peer;
        private LocalSocket socket;
        private volatile java.lang.Process process;
        private Runnable action;
        private final Object outputLock = new Object();

        Channel(IBinder lease) { this.lease = lease; attachInterface(null, PrivilegedProtocol.CHANNEL); }

        void open(String destination) throws Exception {
            lease.linkToDeath(this, 0);
            if (destination.startsWith("localabstract:")
                    && destination.substring(14).matches("scrcpy_[0-9a-f]{8}")) {
                socket = new LocalSocket();
                socket.connect(new LocalSocketAddress(destination.substring(14), LocalSocketAddress.Namespace.ABSTRACT));
                local = ParcelFileDescriptor.dup(socket.getFileDescriptor());
                peer = ParcelFileDescriptor.dup(socket.getFileDescriptor());
                if (closed.get()) throw new IOException("Cancelled");
                return;
            }
            if (!(destination.startsWith("shell,v2,raw:") || "sync:".equals(destination)))
                throw new IOException("Unsupported stream");
            ParcelFileDescriptor[] pair = ParcelFileDescriptor.createSocketPair();
            local = pair[0]; peer = pair[1];
            if (closed.get()) throw new IOException("Cancelled");
            action = () -> {
                try {
                    InputStream input = new FileInputStream(local.getFileDescriptor());
                    OutputStream output = new FileOutputStream(local.getFileDescriptor());
                    if ("sync:".equals(destination)) sync(input, output);
                    else shell(destination.substring(13), input, output);
                } catch (Exception ignored) { }
                finally { close(); }
            };
        }

        void begin() {
            if (closed.get()) { release(); return; }
            if (action != null) launch("openworld-privileged-stream", action);
        }

        @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == INTERFACE_TRANSACTION) return super.onTransact(code, data, reply, flags);
            data.enforceInterface(PrivilegedProtocol.CHANNEL);
            if (Binder.getCallingUid() != ownerUid) throw new SecurityException("Caller denied");
            if (code == PrivilegedProtocol.CLOSE) { close(); return true; }
            return false;
        }

        @Override public void binderDied() { close(); }

        void close() {
            if (closed.compareAndSet(false, true)) {
                try { lease.unlinkToDeath(this, 0); } catch (RuntimeException ignored) { }
            }
            // Descriptors can be assigned concurrently by OPEN after a lease death. OPEN also
            // checks closed before returning; release() is deliberately repeatable.
            release();
            retire();
        }

        private void retire() {
            // A cancelled command with uncooperative descendants retains its slot. This bounds
            // server threads even if a ROM cannot promptly interrupt a process pipe.
            if (closed.get() && workers.get() == 0) synchronized (channels) { channels.remove(this); }
        }

        private void launch(String name, Runnable task) {
            workers.incrementAndGet();
            thread(name, () -> { try { task.run(); } finally { workers.decrementAndGet(); retire(); } });
        }

        private void release() {
            java.lang.Process value = process;
            if (value != null && processStopping.compareAndSet(false, true))
                launch("openworld-privileged-cleanup", () -> {
                    value.destroy();
                    try { value.getOutputStream().close(); } catch (IOException ignored) { }
                    try { value.getInputStream().close(); } catch (IOException ignored) { }
                    try { value.getErrorStream().close(); } catch (IOException ignored) { }
                });
            if (local != null) {
                try { Os.shutdown(local.getFileDescriptor(), OsConstants.SHUT_RDWR); } catch (Exception ignored) { }
                try { local.close(); } catch (IOException ignored) { }
            }
            if (peer != null) try { peer.close(); } catch (IOException ignored) { }
            if (socket != null) try { socket.close(); } catch (IOException ignored) { }
        }

        private void shell(String command, InputStream input, OutputStream output) throws Exception {
            if (command.isEmpty() || closed.get()) throw new IOException("Cancelled");
            java.lang.Process child = new ProcessBuilder("/system/bin/sh", "-c", command).start();
            process = child;
            if (closed.get()) { release(); return; }
            CountDownLatch drained = new CountDownLatch(2);
            copyOutput(child.getInputStream(), output, 1, drained);
            copyOutput(child.getErrorStream(), output, 2, drained);
            launch("openworld-privileged-stdin", () -> {
                try {
                    byte[] header = new byte[5], buffer = new byte[65536];
                    while (!closed.get()) {
                        fully(input, header, 5);
                        int length = size(header, 1);
                        int kind = header[0] & 255;
                        if (kind == 4 && length == 0) { child.getOutputStream().close(); continue; }
                        if (kind != 0 || length < 0 || length > buffer.length) throw new IOException("Invalid stdin");
                        fully(input, buffer, length);
                        child.getOutputStream().write(buffer, 0, length);
                        child.getOutputStream().flush();
                    }
                } catch (Exception error) { close(); }
            });
            int exitCode = child.waitFor();
            drained.await();
            if (!closed.get()) packet(output, 3, new byte[]{(byte) exitCode}, 1);
        }

        private void copyOutput(InputStream input, OutputStream output, int kind, CountDownLatch drained) {
            launch("openworld-privileged-output", () -> {
                try {
                    byte[] bytes = new byte[16384];
                    int count;
                    while (!closed.get() && (count = input.read(bytes)) != -1) packet(output, kind, bytes, count);
                } catch (Exception error) { close(); }
                finally { drained.countDown(); }
            });
        }

        private void packet(OutputStream output, int kind, byte[] bytes, int count) throws IOException {
            synchronized (outputLock) {
                output.write(new byte[]{(byte) kind, (byte) count, (byte) (count >> 8), (byte) (count >> 16), (byte) (count >> 24)});
                output.write(bytes, 0, count);
                output.flush();
            }
        }

        private void sync(InputStream input, OutputStream output) throws Exception {
            byte[] header = new byte[8];
            fully(input, header, 8);
            int length = size(header, 4);
            if (!tag(header, "SEND") || length < 1 || length > 256) throw new IOException("Invalid SEND");
            byte[] path = new byte[length]; fully(input, path, length);
            String requested = new String(path, StandardCharsets.US_ASCII);
            String target;
            if ("/data/local/tmp/openworld-scrcpy-5.0.1.jar,33152".equals(requested))
                target = "/data/local/tmp/openworld-scrcpy-5.0.1.jar";
            else if ("/data/local/tmp/openworld-scrcpy-touch-5.0.1.jar,33152".equals(requested))
                target = "/data/local/tmp/openworld-scrcpy-touch-5.0.1.jar";
            else
                throw new IOException("Invalid asset target");
            File temporary = new File(target + "." + UUID.randomUUID() + ".part");
            FileDescriptor fd = Os.open(temporary.getPath(), OsConstants.O_WRONLY | OsConstants.O_CREAT
                    | OsConstants.O_EXCL | OsConstants.O_NOFOLLOW, 0600);
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                int total = 0;
                try (FileOutputStream file = new FileOutputStream(fd)) {
                    byte[] bytes = new byte[65536];
                    while (!closed.get()) {
                        fully(input, header, 8);
                        if (tag(header, "DONE")) break;
                        length = size(header, 4);
                        if (!tag(header, "DATA") || length < 1 || length > bytes.length || total + length > 733930)
                            throw new IOException("Invalid asset data");
                        fully(input, bytes, length);
                        digest.update(bytes, 0, length); file.write(bytes, 0, length); total += length;
                    }
                    if (closed.get() || total != 733930) throw new IOException("Incomplete asset");
                    StringBuilder hash = new StringBuilder();
                    for (byte b : digest.digest()) hash.append(String.format(java.util.Locale.US, "%02x", b & 255));
                    if (!"764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536".contentEquals(hash))
                        throw new IOException("Asset checksum mismatch");
                    file.getFD().sync();
                }
                Os.rename(temporary.getPath(), target);
                output.write(new byte[]{'O','K','A','Y',0,0,0,0}); output.flush();
            } finally { if (temporary.exists()) temporary.delete(); }
        }
    }

    private static boolean tag(byte[] header, String text) {
        for (int i = 0; i < 4; i++) if (header[i] != text.charAt(i)) return false;
        return true;
    }

    private static int size(byte[] bytes, int offset) {
        return (bytes[offset] & 255) | ((bytes[offset + 1] & 255) << 8)
                | ((bytes[offset + 2] & 255) << 16) | ((bytes[offset + 3] & 255) << 24);
    }

    private static void fully(InputStream input, byte[] bytes, int length) throws IOException {
        int offset = 0;
        while (offset < length) {
            int count = input.read(bytes, offset, length - offset);
            if (count <= 0) throw new java.io.EOFException();
            offset += count;
        }
    }

    private static void thread(String name, Runnable action) {
        Thread thread = new Thread(action, name); thread.setDaemon(true); thread.start();
    }
}
