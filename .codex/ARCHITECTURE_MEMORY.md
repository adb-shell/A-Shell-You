# AShell Architecture Memory

最后更新：2026-10-13（T004独立远程ADB；V0，编译/多PC运行待验证）
用途：跨会话架构速记；详细设计只在 `docs/AI_ENGINEERING/` 维护。

## 1. Runtime Topology

当前产品显示A Shell You、内部AShell/ashell、Android me.ashell；Flutter内部package保留flutter_hbb。身份变更详见 `docs/AI_ENGINEERING/ASHELL_IDENTITY_MIGRATION_2026-10-12.md`，V0 only。

```text
Flutter UI / legacy UI
  <-> Flutter Rust Bridge + C FFI
  <-> Rust controller/server core
  <-> protobuf peer protocol
  <-> hbbs/hbbr external infrastructure
  <-> remote AShell endpoint

Android Rust core
  <-> pkg2230 JNI
  <-> MainService / Accessibility / MediaProjection

Android LocalAdbPage（独立旁路）
  <-> me.ashell/local_adb/v1
  <-> LocalAdbPlugin / LocalAdbClient
  <-> loopback / 本机 adbd

Windows Rust core
  <-> capture/input
  <-> privacy helper/injection
  <-> Amyuni virtual display
```

完整图：`docs/AI_ENGINEERING/01_ARCHITECTURE.md`；本机ADB见12_LOCAL_ADB.md，T004远程扩展见13_REMOTE_ADB.md。Android共享manager被本机Plugin与专用Rust/JNI调用，不走产品采集JNI/输入/terminal，ADB投屏仍未实现。

## 2. Core Modules

| Domain | Primary anchors |
|---|---|
| startup | `src/main.rs`, `src/core_main.rs`, `flutter/lib/main.dart` |
| controller | `src/client.rs`, `src/client/io_loop.rs` |
| controlled endpoint | `src/server.rs`, `src/server/connection.rs` |
| protocol/config | `libs/hbb_common/` |
| capture/encode | `libs/scrap/`, `src/server/video_service.rs` |
| input | `libs/enigo/`, `src/server/input_service.rs` |
| file/terminal | `src/client/io_loop.rs`, `src/server/connection.rs`, `libs/hbb_common/src/fs.rs`, `src/server/terminal_service.rs` |
| Flutter session | `flutter/lib/models/model.dart`, `src/flutter_ffi.rs` |
| Android | `DFm8Y8iMScvB2YDw.kt`, `nZW99cdXQ0COhB2o.kt`, `pkg2230.rs` |
| Windows | `src/privacy_mode.rs`, `src/virtual_display_manager.rs` |
| HTTP/account | `src/hbbs_http/`, Flutter user/address/group models |

源码地图：`docs/AI_ENGINEERING/02_SOURCE_MAP.md`。模块职责：`docs/AI_ENGINEERING/03_MODULE_DESIGN.md`。

## 3. Cross-Layer Changes

Android custom command：

```text
overlay.dart
 -> input_model.dart
 -> flutter_ffi.rs
 -> ui_session_interface.rs
 -> client.rs
 -> message.proto usage
 -> server/connection.rs
 -> pkg2230.rs
 -> Kotlin service
```

FFI signature：

```text
src/flutter_ffi.rs
 -> FRB codegen
 -> bridge_generated*.rs
 -> flutter/lib/generated_bridge.dart
 -> all Dart callers
```

Brand/artifact/deep link：

```text
Cargo + config APP_NAME
 -> Rust helper/output names
 -> Flutter DynamicLibrary names
 -> CMake/Gradle/JNI System.loadLibrary
 -> Android manifest/applicationId/scheme
 -> packaging scripts
```

## 4. State Boundaries

Android:

```text
core service != screen share != frame source != controller first-frame waiting
```

Authentication:

```text
product account != hbbs registration != remote endpoint login != local legacy UI gate
```

Voice:

```text
peer invitation authorization != ZEGO token authorization != microphone user consent
```

Network:

```text
AShell controller relay-only != controlled endpoint has no direct/NAT compatibility
```

## 5. Detailed References

- Android：`docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md`
- Windows：`docs/AI_ENGINEERING/05_WINDOWS_PIPELINE.md`
- Network：`docs/AI_ENGINEERING/06_NETWORK_PROTOCOL.md`
- API：`docs/AI_ENGINEERING/07_API_SYSTEM.md`
- Build：`docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- Debug：`docs/AI_ENGINEERING/09_DEBUG_SYSTEM.md`
- Security：`docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- Roadmap：`docs/AI_ENGINEERING/11_ROADMAP.md`
- Architecture decisions：`docs/ADR/README.md`
- Source/version/dependency/toolchain baseline：`docs/BASELINE/BASELINE_INDEX.md`
- Task artifact：`TASK_TEMPLATE.md`
- Verification cases/evidence：`TEST_MATRIX.md`
