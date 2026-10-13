# AShell ADB mirror baseline

- Baseline ID：AS-BL-2026-10-14-a1486ee-adb-mirror-worktree。
- Source：main/a1486ee2ad2bca991b272a8c7780d2daf2f7c526 + T-2026-10-14-001未提交改动；初始clean。
- Parent：[Baseline15](15_ADB_SIDEBAR_BASELINE_2026-10-13.md)；D-026 / ADR-0021。
- AShell/Rust/Flutter/Android/工具链版本不变；不改Cargo.lock/Gradle依赖。
- 新资产：官方scrcpy-server v5.0.1，733930字节，SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`，Apache-2.0，已比对发布元数据，未验证签名/重建。
- 协议：新增Misc.remote_adb_video(41)，不变更40/原VideoFrame；源码由现有protobuf生成器在正式构建时生成，FRB接口签名不变。
- 行为：独立投屏/多PC有界分发、开关/独立窗口；control-only服务提供ADB优先触摸，无ADB回到原无障碍。普通共享/截图输入经Android分流，新增无障碍远程手势释放方法，其他Accessibility/采集保持。
- 构建：资产离线随APK；Windows现有hwcodec/image依赖复用，CI与正式入口不改。
- 证据：V0源码/边界/资产hash；V2—V4 NOT_RUN；未Git写入、build/test/analyze/codegen、CI或发布。
