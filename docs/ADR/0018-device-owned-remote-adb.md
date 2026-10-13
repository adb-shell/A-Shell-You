# ADR-0018: Device-owned ADB with Remote Session Requests

T006更新：本文保留T004决策历史；keyboard gate、发现范围、UI和时限由 [ADR-0019](0019-adb-discovery-and-independent-control.md) 更新，当前合同见13_REMOTE_ADB。

- Status：accepted for implementation；V0 only，V2/V3/V4 待验证。
- Date / Approver：2026-10-13，当前用户明确要求 PC 顶栏配对/连接/命令/撤销/状态及多 PC 共用 Android 能力。
- Record Type：current decision；Task T-2026-10-13-004 / D-022。
- Owner / Review：Codex ashell-master，Android/Flutter/Network/Rust 实现约束与 Security/Release 静态自审；不是独立人工审计。
- Baseline：AS-BL-2026-10-13-917e661-remote-adb-worktree；Cases ADB-R01—07；Assets EXT-LIB-ADB-004—007 不增版本。
- Supersession：扩展 ADR-0017 的本机模块；其“无 PC ADB/无 mDNS/engine owns connection”仅适用于8662f23。ADR-0015 旧退役和无投屏边界保持。

## Context and Decision

PC 只是代替 Android 本机页面提交数据；私钥、配对记录、ADB transport 的 owner 都必须是 Android 进程。采用 `LocalAdbManager` singleton，同时供本机 MethodChannel 和独立 JNI 使用。PC 以专用 `Misc.remote_adb = 40` JSON v1 发送请求，复用现有认证/加密 peer stream，不新增加密层、不降低已有传输安全。

PC `session_get_common` 增加 key 而不改 FRB ABI。请求端轮询结果，Android 按请求 origin 回执，状态由各 PC 每3秒查询。操作有限串行，20秒 watchdog，64KiB输出；无命令自动重放。配对成功和 shell 可用分离，连接必须 `id` 验证。NSD 只发现本机连接端口，手工端口可回退。

| 方案 | 决定与原因 |
|---|---|
| 专用协议 + Android 单一 owner | 采用；多 PC 共用真实状态、原命令/画面链无需承担 ADB |
| 每 PC 独立 ADB key/隧道 | 不采用；不符合 Android 获取能力、各 PC 共享记录的要求 |
| 复用鼠标 mask/terminal | 不采用；权限/消息语义混杂，可能触碰原链 |
| 独立明文 TCP 服务或关闭 session 加密 | 不采用；无需额外监听端口，现有会话足够传送简单 JSON |
| 同时加入 scrcpy 或 ADB 投屏 | 非目标，另行设计和授权 |

## Authority and Threat Model

请求受 `self.authorized` + keyboard/control permission gate 保护，仅普通远控连接；不是依赖 UI 显隐。控制权限关闭及连接销毁取消该 owner 的未完成请求；空闲共享 ADB 不因 PC 离开而失效。回复与 owner/随机 token/Flutter session 关联，输出不广播。

用户要求通用远程 ADB shell，因此本次不加 shell command allowlist、逐条二次审批或持久审计；不把前期 roadmap 的候选约束伪写成已实现。任何获准远程控制的 PC 可以提交任意 shell，这是扩大后的能力边界；本模块不修复原会话认证/固定密码等既有问题。协议 method 有限白名单、大小/并发/时间有界，内容不落日志。完整威胁模型见 [13_REMOTE_ADB](../AI_ENGINEERING/13_REMOTE_ADB.md)。

撤销是全局 App 身份重置，影响所有 PC；系统配对列表旧条目仍须手动忘记。清理 libadb 3.1.1 的缓存 TLS identity 需要固定反射兼容点和 Release keep。撤销失败返回明确错误；不可宣称已撤销系统信任或清除了系统列表。

## Compatibility, Verification and Rollback

字段40追加，旧端忽略未知字段，新PC显示status超时；不复用旧编号，不改原命令枚举。无新依赖/Manifest/版本/工具链变更；normal hbb_common build生成protobuf，FRB无需变化。Android11以下模块拒绝操作。

V0已按源码核对；正式Android/Windows构建及多PC/旧新端/R8/JNI/取消/撤销重配验收待完成。静态自审不等于发布批准。回退须审阅本任务event路径的独立模块与最小钩子，恢复此前local plugin生命周期，保留其他CI和产品改动；旧身份格式兼容，新增记录可闲置。已执行的shell系统副作用与撤销的key无法靠代码回退恢复，需按设备真实状态重新配对。
