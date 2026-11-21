# Embedded privileged execution

OpenWorld implements a private shell process and Binder execution backend for its own Android UID.
It does not install or impersonate the Shizuku manager and does not authorize third-party apps.

The Binder handoff/version compatibility and UID-observer lifecycle in `PrivilegedSystem.java`
and `OpenWorldPrivilegedService.java` are adapted from RikkaApps/Shizuku:

- Source revision: `b844bc491f1790c72328e1a8e5b2349f8978f0ea`.
- https://github.com/RikkaApps/Shizuku/tree/b844bc491f1790c72328e1a8e5b2349f8978f0ea
- Upstream files: `server/src/main/java/rikka/shizuku/server/BinderSender.java`,
  `server/src/main/java/rikka/shizuku/server/ShizukuService.java`,
  `server/src/main/java/rikka/shizuku/server/api/IContentProviderUtils.java`,
  `manager/src/main/jni/starter.cpp` (process/session lifecycle reference).
- Copyright: Shizuku contributors. License: Apache License 2.0; see `LICENSE.Shizuku`.
- Modifications: app-private UID/installation/generation checks; reflective framework access;
  descriptor-based bounded shell-v2 and scrcpy streams; no general Binder forwarding, third-party
  authorization manager, force-stop recovery, root acquisition, public TCP listener, or upstream APK.
- Shizuku name/icon/application ID/manager permission names are not reused as product identity.

The shell process remains subject to Android/OEM lifecycle and SELinux restrictions. It is not a
permanent permission grant to the app UID. Android reboot or process termination requires activation.
