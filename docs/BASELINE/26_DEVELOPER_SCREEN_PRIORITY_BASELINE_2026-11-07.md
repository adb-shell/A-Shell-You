# Baseline26：开发者画面优先工作区

- ID：`OW-BL-2026-11-07-d9f1119-screen-priority-worktree`。
- Source：test/d9f111944da644b427f7fbfc5970de6f14604ef4之上的未提交改动；Task T-2026-11-07-004 / D-035 / ADR-0028。
- 切换前main/9040c53，test原版本5.2.1 / 5.2.1+59保持；未把main的2.0.26版本改动合入。Baseline25在main已有编号，本任务使用26，冻结旧基线不改。
- 产品身份开放世界 / OpenWorld / openworld / com.openworld.me；依赖、工具链、既有密码摘要及生成桥不变。
- 新实现：PC进程开发者标记、认证后Android连接策略、普通/截图/ADB媒体过滤、ACK/QoS隔离、暂停UI和真实帧恢复；普通会话与ZEGO保持。
- 协议增量：LoginRequest17、Features3、Misc42及ScreenAccess，既有wire字段保持。
- 核心入口：`src/developer_mode.rs`、`src/screen_policy.rs`、`src/server/connection.rs`、`src/client/io_loop.rs`、`src/remote_adb_video.rs`、`src/flutter.rs`、`flutter/lib/models/model.dart`。
- V0：122项身份合同、协议字段/ABI/摘要/生成文件边界及diff检查；四项策略单测存在但未执行。无当前V1/V2/V3/V4通过证据。
- Git：只按用户要求切换test，无stage/commit/push/merge；无编排、构建、部署或设备操作。
- 详细边界与正式验证需求：[任务](../tasks/T-2026-11-07-004-developer-screen-priority.md)、[ADR-0028](../ADR/0028-developer-screen-priority.md)。
