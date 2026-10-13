# libadb accesses Conscrypt reflectively; SPAKE2 and Conscrypt contain JNI entrypoints.
-keep class org.conscrypt.** { *; }
-keep class io.github.muntashirakon.crypto.spake2.** { *; }
# Independent JNI bridge and the pinned libadb TLS identity-cache reset.
-keep class me.ashell.localadb.RemoteAdbNative { *; }
-keepclassmembers class me.ashell.localadb.LocalAdbManager {
    public void dispatchRemote(java.lang.String, java.lang.String, java.lang.String);
    public void disconnectRemote(java.lang.String);
}
-keep class io.github.muntashirakon.adb.SslUtils { *; }
