# libadb accesses Conscrypt reflectively; SPAKE2 and Conscrypt contain JNI entrypoints.
-keep class org.conscrypt.** { *; }
-keep class io.github.muntashirakon.crypto.spake2.** { *; }
