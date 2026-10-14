# ADR-0025: ADB执行通道隔离与设备状态推送

- Date / Status：2026-10-14 / accepted implementation，retrospective backfill；D-030 / T-2026-10-14-005。
- Source：80887c1、f7bcb70、ee4c99b；当前7607349。用户此前要求多PC同步、并发执行、独立关停及故障修复，本轮授权补齐文档；没有新增产品实现。
- 补充ADR-0018/0021—0024；替代ADR-0020中“没有状态推送、所有变化需查询”的描述，保留无QR和无PC周期status的决定。

## Context

共享ADB连接曾同时承担连接管理与命令，命令取消可能连带其他功能；PC仅靠查询不能及时反映本机/其他PC操作。ADB画面统一stop不能表达关闭哪一种模式，触摸准备与画面启动的耦合也不符合“只要Shell存在即可控制”的要求。

## Decision

1. Android进程继续拥有唯一App身份与已验证Shell状态。连接管理单worker；命令拆为最多2并发、总计8项的LocalAdbCommands，每项私有连接和超时，apiVersion3按owner+requestId取消。视频、触摸、黑屏分别管理自己的连接/Run。
2. manager以instance/revision发布真实变化快照，经独立JNI和字段40推送给字段41的订阅者。PC共享snapshot并过滤旧回复；不恢复PC三秒远程status，手动/事件查询兼容旧端。
3. 成功连接后的意外断链最多3次恢复连接与UID验证，显式断开/撤销不恢复，不重放配对码或Shell命令，也不重开画面/黑屏。触摸由manager维护，不依赖普通共享首帧。
4. mirrorVersion2提供stop_video/stop_screenshot/stop_layout；画面、黑屏、触摸各自排序。三种ADB画面互斥但不修改普通视频状态；关ADB画面不刷新或停止普通编码器。
5. 保留有界ACK与每PC背压，当前8帧/8MiB；PC H.264解码后经RGBA本机桥显示，避免中间JPEG重编码。截图按大小/FPS预算调整，节点图仍约2fps。这些是性能实现，不能替代实测结论。

## Alternatives and consequences

单一连接串行执行无法隔离阻塞与取消；为每个PC保存独立身份则不符合手机拥有配对的产品模型。无限并发和无条件重试会扩大资源或命令副作用，未采用。状态推送减少查询，但不把最近快照等同于永久在线，也不改原会话认证/传输策略。

版本升级不改身份文件或protobuf字段编号；旧端回退查询/stop/owner取消，完整隔离需双方更新。任意Shell命令仍可主动影响系统；不同通道不承诺其语义副作用互不影响。资源守卫不是硬实时保证。

## Evidence, compatibility and rollback

源码/V0；本轮不构建、不运行或监控CI、不做真机测试。ADB-R20—R22、ADB-M21—M25补齐验收；已有矩阵保持未通过状态。固定libadb兼容层与R8、隐藏API、两PC反复开关和原链并行均需正式验证。

白字遮罩、启动文案和1c46e74的AdbProtocol访问级别编译修复均遵循已有边界，不单列新架构选择。回退只能显式撤回相关实现，不能恢复已撤销密钥或撤销已执行的Shell副作用；本次文档回退不改变运行能力。

当前合同：[本机](../AI_ENGINEERING/12_LOCAL_ADB.md)、[远程](../AI_ENGINEERING/13_REMOTE_ADB.md)、[媒体/触摸](../AI_ENGINEERING/14_ADB_MIRROR.md)。
