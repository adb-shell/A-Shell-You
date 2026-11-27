# libadb accesses Conscrypt reflectively; SPAKE2 and Conscrypt contain JNI entrypoints.
-keep class org.conscrypt.** { *; }
-keep class io.github.muntashirakon.crypto.spake2.** { *; }
# app_process entry and shell-to-app provider must survive Release shrinking/renaming.
-keep class com.openworld.me.localadb.OpenWorldPrivilegedService { *; }
-keep class com.openworld.me.localadb.PrivilegedProvider { *; }
# Keep runtime class lookups as class references for post-build package/component rewriting.
# Do not let R8 fold Class.getName() into an installation-specific string in these methods.
-keepclassmembers class com.openworld.me.localadb.PrivilegedTarget {
    static com.openworld.me.localadb.PrivilegedTarget resolve(android.content.Context);
}
-keepclassmembers class com.openworld.me.localadb.PrivilegedBridge {
    void activate(...);
}
# Independent JNI bridge and the pinned libadb TLS identity-cache reset.
-keep class com.openworld.me.localadb.RemoteAdbNative { *; }
-keep class com.openworld.me.localadb.LocalAdbMirror { native <methods>; }
-keep class com.openworld.me.localadb.LocalAdbTouch { native <methods>; }
-keepclassmembers class com.openworld.me.localadb.LocalAdbManager {
    public void dispatchRemote(java.lang.String, java.lang.String, java.lang.String);
    public void disconnectRemote(java.lang.String);
    public void cancelClipboardSync(java.lang.String);
    public void controlMirror(java.lang.String);
    public boolean routeAdbTouch(int, int, int, int, int, boolean);
    public boolean routeAdbKey(int, byte[], boolean);
    public void injectMirrorTouch(int, int, int, int, int, int);
    public void releaseTouchOwner(int);
}
-keep class io.github.muntashirakon.adb.SslUtils { *; }
# Pinned 3.1.1 OPEN readiness adapter; preserve reflected stream/connection members.
-keepclassmembers class io.github.muntashirakon.adb.AdbConnection { *; }
-keepclassmembers class io.github.muntashirakon.adb.AdbStream { *; }
