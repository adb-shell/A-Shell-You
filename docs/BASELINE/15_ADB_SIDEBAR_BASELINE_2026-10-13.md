# AShell ADB Shell栏 Baseline — 2026-10-13

- Baseline ID：`AS-BL-2026-10-13-aca7446-adb-sidebar`。
- Source：`main/aca74467a3f83392c6a440e6507a63508f9294f6`；文档同步前工作区clean，本地origin/main同SHA；未fetch或查询远端/CI。
- 前序：980f254固化配对UI与T007文档；aca7446提交八份Dart侧栏代码。Baseline14保留历史冻结含义，不重写。
- Record：T-2026-10-13-008 / D-025 / CE-20261013-T008-01；ADR-0018/0019/0020继续约束原能力。
- Evidence：V0 repository-observed；无当前SHA正式构建/真机结果，文档同步不提升验证等级。

## 当前范围

PC增加RemotePage拥有的RemoteAdbSessionState、独立可拖动RemoteAdbSidebar及位于“移动端操作”左侧的开关。ADB确认可用时自动展示Shell栏，持续可用时保留手动关闭；仅通过既有shell方法执行返回/主页/多任务。原无障碍栏只增加标题与布局空间，原功能回调保持。

无PC周期status；会话就绪/恢复可控、打开弹窗/手动刷新、导航点击和操作回执更新快照。Android身份/连接/队列、多PC共享及只读gate保持，无新的native/协议/FRB/依赖。ADB投屏传输未实现，下一阶段仅记录待设计事项。

产品版本保持Rust5.2.1 / Flutter5.2.1+59；字段40、JSON v1、status.apiVersion=2、local_adb_record_v1、固定libadb依赖与工具链无变更。此基线不是artifact manifest或release证明。

## 文档同步前源码SHA-256

用于证明本轮文档编辑不改动八份侧栏相关Dart文件，非APK/EXE哈希。

| 路径 | SHA-256 |
|---|---|
| `flutter/lib/common.dart` | `2418a11a97cae80e332b01a7d67571b65a5eaab77621ad7513122921cac8efba` |
| `flutter/lib/common/widgets/overlay.dart` | `89bee34bf492964da77acdf244738ff115cdd73bc924647f45819eb8009f92cd` |
| `flutter/lib/desktop/adb/remote_adb_controller.dart` | `3c9db8c77ecc4ced20db2e4e596699cf526782d97ab0e0448bebb66be4e116c0` |
| `flutter/lib/desktop/adb/remote_adb_menu.dart` | `44ba36d0f8561470528edce23751bae396247f99bc14a41ecd03404a825c12c1` |
| `flutter/lib/desktop/adb/remote_adb_session_state.dart` | `dc6e2a7158335b90f7cb089b71360e3e47f967c5ea88b6924b1f35eac0d3e69e` |
| `flutter/lib/desktop/adb/remote_adb_sidebar.dart` | `605b09a95d0e214be64ee3ed2716fd0dad8e13b99b0730bbb8628b2f8f368047` |
| `flutter/lib/desktop/pages/remote_page.dart` | `6cf4707e1b00589b518bd41e80bfe52f7cbb40b2cddf86b12a5265a5de3d60fa` |
| `flutter/lib/desktop/widgets/remote_toolbar.dart` | `149f18864b9fac77c908bb26b76b7042568eaaec6f03c4b78cde655d08c0676b` |

## 验证与后续

- 本轮范围：仅Markdown、差异/引用/源码hash静态核对；不执行Git写入、产品build/test/analyze/codegen、设备或CI操作。
- 正式验收：ADB-R01—09、11—19；原链AND-01/02/04/05/07/08、FLT-04/05/06。V2/V3/V4未验证，旧QR R10为N/A。
- 待正式环境按现有入口构建Windows与Android、验证三键/开关/多PC/原链；当前没有获准运行新构建或监控的动作。
- 事实说明：[13_REMOTE_ADB](../AI_ENGINEERING/13_REMOTE_ADB.md)；下一步：[11_ROADMAP](../AI_ENGINEERING/11_ROADMAP.md)；任务：[T008](../tasks/T-2026-10-13-008-adb-sidebar-docs-sync.md)。
