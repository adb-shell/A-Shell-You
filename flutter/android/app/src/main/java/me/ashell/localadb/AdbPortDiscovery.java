package me.ashell.localadb;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/** Resolves only this Android device's TLS-connect advertisement, never another phone. */
final class AdbPortDiscovery {
    static int find(Context context, AtomicBoolean cancelled) throws Exception {
        NsdManager nsd = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        if (nsd == null) return 0;
        CountDownLatch found = new CountDownLatch(1);
        AtomicInteger port = new AtomicInteger();
        AtomicBoolean closed = new AtomicBoolean();
        // NSD accepts one resolver at a time on some Android releases. Preserve discoveries
        // arriving during a resolution, especially when another phone advertises first.
        class Resolver {
            final ArrayDeque<NsdServiceInfo> pending = new ArrayDeque<>();
            boolean resolving;
            synchronized void add(NsdServiceInfo service) {
                if (closed.get() || cancelled.get() || pending.size() >= 32) return;
                pending.add(service);
                next();
            }
            synchronized void next() {
                if (resolving || closed.get() || cancelled.get() || port.get() != 0 || pending.isEmpty()) return;
                resolving = true;
                try {
                    nsd.resolveService(pending.remove(), new NsdManager.ResolveListener() {
                        @Override public void onResolveFailed(NsdServiceInfo value, int code) { finished(); }
                        @Override public void onServiceResolved(NsdServiceInfo value) {
                            if (!closed.get() && !cancelled.get() && isLocal(value.getHost())
                                    && value.getPort() > 0 && value.getPort() <= 65535) {
                                port.compareAndSet(0, value.getPort());
                                found.countDown();
                            }
                            finished();
                        }
                    });
                } catch (RuntimeException ignored) { finished(); }
            }
            synchronized void finished() { resolving = false; next(); }
        }
        Resolver resolver = new Resolver();
        NsdManager.DiscoveryListener listener = new NsdManager.DiscoveryListener() {
            @Override public void onDiscoveryStarted(String type) { }
            @Override public void onDiscoveryStopped(String type) { }
            @Override public void onServiceLost(NsdServiceInfo service) { }
            @Override public void onStopDiscoveryFailed(String type, int code) { }
            @Override public void onStartDiscoveryFailed(String type, int code) { found.countDown(); }
            @Override public void onServiceFound(NsdServiceInfo service) {
                resolver.add(service);
            }
        };
        boolean started = false;
        try {
            nsd.discoverServices("_adb-tls-connect._tcp.", NsdManager.PROTOCOL_DNS_SD, listener);
            started = true;
            found.await(5, TimeUnit.SECONDS);
            return cancelled.get() ? 0 : port.get();
        } finally {
            closed.set(true);
            if (started) {
                try { nsd.stopServiceDiscovery(listener); } catch (RuntimeException ignored) { }
            }
        }
    }

    private static boolean isLocal(InetAddress address) {
        if (address == null) return false;
        if (address.isLoopbackAddress()) return true;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces != null && interfaces.hasMoreElements()) {
                Enumeration<InetAddress> addresses = interfaces.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) if (address.equals(addresses.nextElement())) return true;
            }
        } catch (Exception ignored) { }
        return false;
    }
}
