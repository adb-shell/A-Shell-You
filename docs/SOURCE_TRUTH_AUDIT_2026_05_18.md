# Source Truth Audit - 2026-05-18

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

This document records the latest full Markdown and source-code verification pass.

> [!IMPORTANT]
> This is a fixed-date audit from 2026-05-18. For current handoff, task entrypoints, Android runtime, ZEGO voice call, ADB/LADB, and documentation trust boundaries, prefer the 2026-06-03 engineering main set: `docs/ENGINEERING_INDEX.md`, `docs/ENGINEERING_BASELINE.md`, `docs/ENGINEERING_ANDROID_RUNTIME.md`, `docs/TASK_ENTRYPOINTS.md`, `docs/REPO_TRUE_STRUCTURE_MAP.md`, and `docs/DOCUMENT_AUDIT.md`.
> The `Trusted Guidance Set` below reflects the 2026-05-18 audit moment and is superseded by `docs/DOCUMENT_AUDIT.md`.

## Trusted Guidance Set

Use these documents as the current project knowledge base:

- `CLAUDE.md`
- `docs/ENGINEERING_INDEX.md`
- `docs/ENGINEERING_BASELINE.md`
- `docs/ENGINEERING_ANDROID_RUNTIME.md`
- `docs/TASK_ENTRYPOINTS.md`
- `docs/CHANGELOG.md`
- `terminal.md`

## Current Source Truth

- Runtime/product name: `AShell`.
- Android visible app name: `云计划`.
- Android package/applicationId: `me.ashell`.
- Rust crate/package version: `5.2.1`.
- Flutter package version: `5.2.1+59`.
- Rust library/crate output name: `ashell`.
- Android native library: `libashell.so`.
- Windows native library: `ashell.dll`.
- Portable packer package: `ashell-portable-packer` version `5.2.1`.
- Current Windows build entry: `new-build.cmd`.
- Current Windows portable output directory: `PC-Bulid`.
- Legacy Windows build entry: `build.cmd`, retained for old environment compatibility.

## Verified Source Anchors

- Android label source: `flutter/android/app/src/main/res/values/strings.xml`, key `app_name = 云计划`.
- Android manifest labels: `flutter/android/app/src/main/AndroidManifest.xml`, `android:label="@string/app_name"` for the app and accessibility service.
- Android foreground notification title: `flutter/android/app/src/main/kotlin/me/ashell/DFm8Y8iMScvB2YDw.kt`, `getString(R.string.app_name)`.
- Android Kotlin native load: `flutter/android/app/src/main/kotlin/ffi.kt` and `flutter/android/app/src/main/kotlin/pkg2230.kt`, `System.loadLibrary("ashell")`.
- Android Dart native load: `flutter/lib/models/native_model.dart`, `DynamicLibrary.open('libashell.so')`.
- Android build copy path: `build.sh`, `target/<triple>/release/libashell.so` to `flutter/android/app/src/main/jniLibs/<abi>/libashell.so`.
- Windows runner native load: `flutter/windows/runner/main.cpp`, `LoadLibraryA("ashell.dll")`.
- Windows CMake install path: `flutter/windows/CMakeLists.txt`, installs/renames `ashell.dll`.
- Windows Dart native load: `flutter/lib/models/native_model.dart`, `DynamicLibrary.open('ashell.dll')`.
- Rust package source: `Cargo.toml`, `name = "ashell"`, `version = "5.2.1"`, `[lib] name = "ashell"`.
- Portable package source: `libs/portable/Cargo.toml` and `libs/portable/Cargo.lock`, `ashell-portable-packer` `5.2.1`.
- Flutter package source: `flutter/pubspec.yaml`, `version: 5.2.1+59`.
- PC build script source: `new-build.cmd`, `VERSION=5.2.1`, `PC-Bulid`, `ashell-portable-packer.exe`.

## Android Status Monitor Source Truth

- Status query key: `DFm8Y8iMScvB2YDwGYN("ashell_status")`.
- Rust transport: `src/server/connection.rs`, `ashell_status_message() -> Option<Message>`.
- Invalid/JNI-failed status samples must be skipped, not converted to hardcoded false JSON.
- Flutter event: `update_ashell_status`.
- Flutter model/widget: `AShellStatusModel` / `AShellStatusMonitor`.
- UI waiting state: nullable status fields render as gray `--`; red is only for a real `false` value from Android.

Current payload semantics:

- `video = _isStart && mediaProjection != null`.
- `screenshot = shouldRun && nZW99cdXQ0COhB2o.isOpen`.
- `share = _isStart`.
- `ignore = shouldRun || nZW99cdXQ0COhB2o.isIgnorePending`.
- `blank = BIS`.
- `penetrate = SKL`.
- `touchblock = nZW99cdXQ0COhB2o.isTouchBlockOn`.
- `accessibility = nZW99cdXQ0COhB2o.isOpen`.

## Background Documents

These files are inherited upstream/vendor/background material and must not override current source anchors:

- `README.md`: contains a top current-source-truth overlay; upstream RustDesk README body is retained as background.
- `PC-Build.md`: contains a top current-source-truth overlay; old RustDesk / `rustdesk-1.4.6` environment examples are background.
- `docs/README-ZH.md`, `docs/SECURITY.md`, `docs/CONTRIBUTING.md`, `docs/CONTRIBUTING-ZH.md`, `docs/CODE_OF_CONDUCT.md`, `docs/CODE_OF_CONDUCT-ZH.md`, and `docs/DEVCONTAINER.md`: retained English/Chinese upstream/project-policy documents.
- READMEs under `libs/`, `flutter/`, `res/`, and `src/lang/`: vendor/upstream subsystem notes.

## Residual Rules

- Historical words such as `RustDesk`, `librustdesk.dll`, `libdaxian.so`, `com.daxian.dev`, or `rustdesk_core_main` may appear only in changelog, migration history, guardrails, or upstream/vendor background.
- Do not reintroduce those old names into active Android/PC build, runtime loading, status monitor, or side-button code.
- Do not rewrite third-party dependency versions in lockfiles just because they contain values such as `1.4.0` or `5.2.0`; only project package entries are synchronized.
