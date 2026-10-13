# AS-BL-2026-10-13-efb2599-adb-ui-worktree

2026-10-13 / repository-observed / V0；source main/efb2599d05ba5b3a08a9c63ee00bec20e2ce9c5b + 下列三份未提交Dart修改。此快照在本轮提交前冻结，不冒充commit内容；实际交付SHA以后续Git回执为准，旧Baseline13不改写。

| 层 | 冻结事实 |
|---|---|
| 已提交 | feac956双NSD/独立ADB gate/一键连接；efb2599移除QR、校验未知参数、改请求和取消处理 |
| `flutter/lib/desktop/adb/remote_adb_controller.dart` | 移除周期status timer，保留初始/手动/回执更新及命令回执等待 |
| `flutter/lib/desktop/adb/remote_adb_menu.dart` | 去除开窗预扫描，模式控件分离、焦点/key与说明更新，已配对连接按钮主题色 |
| `flutter/lib/mobile/adb/local_adb_page.dart` | 相同模式展示/焦点/key和使用说明，本机前台3秒查询保持 |
| 产品/工具链 | 5.2.1 / Flutter5.2.1+59 / me.ashell / ashell；Rust1.75、Flutter3.24.5、JDK17、NDK27、工作流不变 |
| 依赖 | libadb3.1.1、spake2-android2.2.1、BC1.81、Conscrypt2.5.3不变；ADB不再消费qr_flutter，其他页面依赖保留 |
| 合同 | Misc.remote_adb40 / JSON v1 / apiVersion2 / FRB不变；业务参数port/connectPort/code/command，未知含serviceName拒绝；身份与record schema无迁移 |
| 预算 | Android20秒/pairConnect35秒/NSD6.5秒；PC status/cancel8秒、其他300秒；路由310秒惰性过期，非硬实时证明 |
| 隔离 | 无Accessibility/视频/输入/terminal/ZEGO/品牌改动，无ADB投屏 |

文档同步前的源码SHA-256（用于确认本轮仅改文档，不代表构建产物）：

| 文件简称 | SHA-256 |
|---|---|
| remote_adb_controller.dart | `87ADEE8E8776342E78642365FA421EEA8A07FB9FF75D24375C5669BF0237987E` |
| remote_adb_menu.dart | `E5B23AC809E3952CC7EE68D13968A968771C3A6A70D825C0F0EBAA6E80592F4B` |
| local_adb_page.dart | `D4915EB6A2D4765B168B6CFE2C0FEDEC9A53EC179139BE1BBFF64D6D2FD01F38` |

efb2599已推送，之后both/aarch64 dispatch获HTTP204接受，未监控；不包含本表未提交UI修改，无当前V2/V3/V4成功证据。本轮交付另获提交/推送/单次dispatch授权，结果以最终回执为准。关联[ADR-0020](../ADR/0020-adb-code-pairing-and-explicit-refresh.md)、[T007](../tasks/T-2026-10-13-007-adb-docs-sync.md)、D-024 / CE-20261013-T007-01。
