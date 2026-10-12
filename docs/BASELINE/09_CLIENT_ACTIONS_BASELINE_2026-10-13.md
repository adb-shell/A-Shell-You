# AShell Client Actions Baseline

- Baseline ID：`AS-BL-2026-10-13-8662f23-client-actions-worktree`。
- Source：main / 8662f2369a33f5286c20f9de4d640d2072175ca7；initial clean，以下工作流修改未提交。
- Task / Event / Decision：T-2026-10-13-001 / CE-20261013-T001-01 / D-020 / ADR-0016。
- Evidence：repository-observed / V0；GitHub runner V2、设备 V3 NOT_RUN。

| Item | Workflow configuration |
|---|---|
| Entry / triggers | .github/workflows/build-clients.yml / workflow_dispatch only |
| Inputs | platform both/android/windows；android_arch aarch64/universal |
| Hosts | ubuntu-22.04 / windows-2022；managed image/patch mutable |
| Rust / Flutter / bridge | 1.75.0 / 3.24.5 + existing dropdown patch / tracked FRB outputs |
| Android | cargo-ndk 3.1.2；NDK 27.2.12479018；native API 21；SDK/Build Tools 34；Temurin JDK 17 |
| Native features | ARM64/ARMv7 flutter,hwcodec；x86_64 flutter；Windows flutter,hwcodec,vram |
| Windows | MSVC x64；LLVM 15.0.6；Python 3.11.9；CMake 3.28.3；Brotli 1.1.0 |
| vcpkg | 6f29f12e82a8293156836ad81cc9bf5af41fe836，与 vcpkg.json baseline 核对；res/vcpkg overlays |
| Windows packaging | build.py + libs/portable/generate.py；current Release only；unsigned EXE/ZIP |
| Android signing | generated ephemeral test JKS；Release APK；not production / not stable between runs |
| Artifacts | final 14 days，native intermediates 1 day；hash、commit、toolchain、resolved lock evidence |
| Actions | 9 distinct direct actions pinned to full official commit SHA；exact pins live in workflow |

产品版本、native artifact 名称、runtime、源锁文件保持 HEAD；当前源包含 8662f23 新 local ADB，不再沿用旧状态摘要的“未接入新组件”。新 CI 不调用旧本地 Bash/CMD 入口。build.sh 从 manifest 获取 vcpkg baseline；历史 F-Droid 脚本冻结旧版本，避免引用已删除编排。

Root Cargo Android 构建 --locked；Windows build.py 沿用已有依赖解析机制。Flutter pubspec 的 ZEGO 声明/lock 存在旧漂移，需要 pub get 后的实际 lock evidence；本轮不静默更新锁文件。hosted image/工具链 patch、依赖下载和 native binary provenance 仍需 runner 结果，不宣称 bit-for-bit reproducibility 或发布就绪。旧 Baseline 01—08 保留历史冻结意义。
