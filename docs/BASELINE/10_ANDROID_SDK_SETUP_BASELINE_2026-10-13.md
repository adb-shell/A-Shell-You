# Android SDK Setup Fix Baseline

- Baseline ID：`AS-BL-2026-10-13-c521601-android-sdk-setup-worktree`。
- Source：main / c52160116e1f6f582aba64fb05465c972a5d0673，initial clean；冻结本次修复工作树配置，原 CI 工具链继承 [baseline09](09_CLIENT_ACTIONS_BASELINE_2026-10-13.md)。
- Task / Event：T-2026-10-13-002 / CE-20261013-T002-01；D-020 / ADR-0016 reviewed-no-change。
- Delta：native 与 APK 两 job 在 JDK setup 后均初始化 SDK；10 种直接 Action，均固定完整 commit SHA。
- New pin：`android-actions/setup-android@9fc6c4e9069bf8d3d10b2204b1fb8f6ef7065407`，对应上游 v3.2.2；CLI 12266719 / 16.0，packages 显式 platform-tools，接受 licenses，由 action 导出 PATH、ANDROID_HOME 和 ANDROID_SDK_ROOT。
- Provenance：已只读核对上游 GitHub tag API、[action.yml](https://github.com/android-actions/setup-android/blob/9fc6c4e9069bf8d3d10b2204b1fb8f6ef7065407/action.yml) 与 [src/main.ts](https://github.com/android-actions/setup-android/blob/9fc6c4e9069bf8d3d10b2204b1fb8f6ef7065407/src/main.ts)。action 按固定 build number 从 Google Android repository 下载；本机未下载/安装 SDK，未补称实际工具归档的独立校验或完整供应链验证。
- Unchanged：Rust 1.75.0、Flutter 3.24.5、NDK 27.2.12479018、SDK/Build Tools 34、JDK 17、vcpkg baseline、产品版本/锁文件、本地脚本、Windows job、签名与 artifact 策略。
- Evidence：用户日志证明初版初始化失败；修复后 V0，不等于 runner V2 成功。正式验证见 [任务](../tasks/T-2026-10-13-002-android-sdk-setup.md)。旧 baseline 保留。
