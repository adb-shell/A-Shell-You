# Baseline21 — ADB绘图与PC展示文档同步

- ID：`AS-BL-2026-10-16-4d3c446-adb-render-ui`。
- Source：main/`4d3c44640c4b001b9af2c3106e809564f8b615cf`，任务开始本地及origin/main一致、工作区clean；本文写于文档提交前。
- Previous：Baseline20源码7607349，文档由0ec8b5b提交固化；旧快照冻结。
- 实现沿革：227bbe0节点兼容与文字排版；a2fc3c3将节点图绘制移到App；e5c80ad将黑屏文字预渲染移到App、绑定layerStack、数字红色及移除底部重复进度；393acaa增加PC分组状态；4d3c446仅删除桌面视频/特殊状态显示及Shell栏悬浮提示。
- 回退边界：两轮尝试的无障碍暂停/恢复/授予/关闭、开发者选项入口均已撤回。当前4d3c446相对393acaa只有overlay.dart和remote_adb_sidebar.dart两份Dart差异，没有新增菜单或权限控制实现。
- 当前：helper只读节点、App绘图与字体回退；黑屏helper只显示有界PNG；PC状态来源/顺序见14_ADB_MIRROR，原字段数据及移动端状态保留。apiVersion3、mirrorVersion2、字段40/41、全局ADB优先触摸和独立命令/媒体资源保持。
- 保持：产品5.2.1、Flutter5.2.1+59、libadb3.1.1、scrcpy5.0.1及原hash/许可；无依赖、工具链、构建入口、身份、协议字段或外部资产更新。
- Evidence：V0源码/提交/diff/文档引用核对。此前4d3c446推送后both/aarch64 dispatch获204，未监控；不代表构建通过。当前版本构建、Android11—17/OEM与多PC真机证据待补。
- 本轮范围：仅Markdown同步，提交推送后单次Build AShell clients（both/aarch64），不监控、不运行本地产品验证。新文档SHA和本次dispatch接受结果以Git及会话交付为准，不预写成功。
- Links：[任务](../tasks/T-2026-10-16-001-adb-docs-sync.md)、[ADR0026](../ADR/0026-adb-app-side-rendering.md)、[当前合同](../AI_ENGINEERING/14_ADB_MIRROR.md)。
