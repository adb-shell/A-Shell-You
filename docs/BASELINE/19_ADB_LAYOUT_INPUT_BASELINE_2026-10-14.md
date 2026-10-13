# Baseline19 — ADB layout, geometry and input

- ID：`AS-BL-2026-10-14-404b199-adb-layout-worktree`。
- Source：main/404b1991157308ebf700108dac3196f9d2889f91，初始clean；T-2026-10-14-004源码工作树未提交。
- 增量：Shell栏原样式参数/穿透按钮；AdbCaptureProfile、AdbLayoutCapture、LocalAdbLayout；三种ADB源、真实屏幕几何/统一缩放、普通画面ADB光标、可信遮罩、触摸有界队列和恢复。
- Wire：专用RemoteAdbVideo字段20—22；本地JNI的state使用format字符串，frame附source dimensions；FRB签名/生成桥不变。R8保留新helper入口及quality JNI。
- 保持：scrcpy5.0.1/hash、libadb3.1.1、Android SDK/NDK/Gradle、版本身份、原MediaProjection/无障碍/采集JNI/普通协议。无新仓外资产。
- Evidence：V0源码/引用/协议/JNI/差异/文本括号检查；ADB-M17—M20 NOT_RUN，无本轮build/test/analyze/codegen/Git/CI。前轮404b199已推送，dispatch204且未监控，不能推断编译结果。
- Links：[task](../tasks/T-2026-10-14-004-adb-ui-and-input.md)、[ADR0024](../ADR/0024-adb-layout-and-geometry.md)、[current contract](../AI_ENGINEERING/14_ADB_MIRROR.md)。
