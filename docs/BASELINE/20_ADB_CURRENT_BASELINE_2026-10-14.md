# Baseline20 — 当前ADB模块文档同步

- ID：`AS-BL-2026-10-14-7607349-adb-current`。
- Source：main/76073498e6b8a405f175c654f54275d143fdcc02，文档任务开始时clean；本轮只编辑Markdown，不修改产品源码。
- 历史提交：282b748固化Baseline19；80887c1侧栏/独立触摸/显示修复；f7bcb70执行通道、状态推送和连接恢复；ee4c99b触摸owner/启动配置/侧栏状态竞态；1c46e74修复package-private AdbProtocol编译访问；7607349增加黑屏白字和“正在启动无视…”文案。
- Current：apiVersion3、mirrorVersion2；字段40请求/回执/推送，字段41独立媒体/触摸；命令2并发/总计8项，模式化停止、独立黑屏/触摸。每PC媒体在途8帧/8MiB；H.264本机RGBA显示、截图最多8fps自适应、节点约2fps。
- 保持：scrcpy5.0.1及原hash/许可、libadb3.1.1及其他固定依赖、产品5.2.1/Flutter5.2.1+59、身份、原工具链和构建入口。没有新外部资产、权限或端口。
- Evidence：V0源码/提交/文档核对。此前1c46e74推送后Android/aarch64 dispatch仅记录HTTP204且未监控，不能认定构建通过；7607349对应构建/真机结果未知。本次明确不执行编排或本地产品验证。
- Links：[T005](../tasks/T-2026-10-14-005-adb-docs-sync.md)、[ADR0025](../ADR/0025-adb-lanes-and-state-sync.md)、[当前合同](../AI_ENGINEERING/14_ADB_MIRROR.md)。Baseline19及更早快照冻结保留。
