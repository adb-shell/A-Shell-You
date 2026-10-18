# AShell Project State

Schema Version：`1.0`
State Revision：`STATE-20261019-006`
Last Updated / Observed At：2026-10-19（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- 用户指定的实现基线：`0dbe2027ceb3f1a72ae2700fe8e4942cfe67cec5`（完整优化adb投屏）。
- 本次开始时：当前分支 test；本地 main/test、远端 main/test 均为上述提交；工作区 clean。
- 本次只更新 Markdown 文档与工程记忆。后续文档提交不改变上述业务源码基线。
- 当前基线：[Baseline22](../docs/BASELINE/22_RESTORED_SOURCE_BASELINE_2026-10-19.md)。历史基线、任务、编排授权只代表各自时点。
- 回退后撤回的实现、实验方案和诊断结论不属于当前实现。后续会话先核对 HEAD/status，再由代码重新建立证据；不能沿用历史聊天中的“已修改”判断。

## 当前能力摘要

| 领域 | 当前实现与限制 |
|---|---|
| 产品身份 | 显示“一个壳子”；内部 AShell/ashell、me.ashell、flutter_hbb 保持；Rust 5.2.1 / Flutter 5.2.1+59 |
| Android 原链 | core连接服务、MediaProjection、无障碍输入/无视/穿透/黑屏保持各自原入口；服务运行不等于屏幕共享或首帧 |
| ADB身份 | Android进程持有配对身份；双NSD发现，pairConnect后以真实 id -u 验证；配对记录不代表Shell已可用 |
| ADB执行 | 2路独立命令、设备共享状态变化推送、有限意外断链恢复；PC无三秒远程status |
| ADB画面 | scrcpy ScreenCapture + 独立GPU/H.264；常驻截图JPEG及raw/PNG兼容回退；Shell取节点、App绘穿透 |
| ADB参数 | width/350整数缩放；4096边长/4Mi像素界限；只读普通VideoQoS，运行时更新；无视至少300ms，节点至少80ms |
| ADB输入 | Shell可用时全局优先触摸，独立control-only连接；普通键盘、远程输入、Ctrl+V及PC→Android纯文本剪贴板同步 |
| 黑屏 | 独立黑色buffer层及App预渲染白字PNG，排除自身采集；OEM表现待真机证据 |
| PC界面 | 普通左/ADB右、单源居中；Shell栏无悬浮提示；状态分组；无障碍栏无视频/特殊/防触状态文字 |
| 常亮与保活 | 默认连接服务运行期间静默常亮，无新增设置入口；ADB连接验证后尝试本应用电池优化豁免，不保证进程免杀 |

详见 [12本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)、[Android原链](../docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md)。

## 本轮工作与验证

- 当前任务：[T-2026-10-19-006](../docs/tasks/T-2026-10-19-006-restored-docs-sync.md)，文档与V0同步完成；Git交付的准确SHA和分支一致性见实际引用及会话回执。
- Registry：WORK-20261019-006；Event：CE-20261019-T006-01；Decision：D-033。
- 授权：文档同步，以“文档更新”提交推送test，再合并推送main；本次不运行或触发构建。
- 验证：源码对照、24份Markdown/249个本地引用和git diff --check通过，非文档树与0dbe202一致；本轮无编译、分析器、真机或集成测试。历史dispatch接受不等于当前构建通过。
- 后续修复必须以此源码和新的复现证据为准。现有风险与验证缺口见 TEST_MATRIX、10_SECURITY_MODEL、EXTERNAL_ASSET_REGISTRY，不在本轮修改业务代码。
