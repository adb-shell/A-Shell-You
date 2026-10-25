# AShell Project State

Schema Version：`1.0`
State Revision：`STATE-20261025-001-r3`
Last Updated / Observed At：2026-10-25（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- 参考冻结基线：[Baseline22](../docs/BASELINE/22_RESTORED_SOURCE_BASELINE_2026-10-19.md)，`0dbe2027ceb3f1a72ae2700fe8e4942cfe67cec5`；实际源码已有后续提交，不能把旧快照当作当前HEAD。
- 本次r3续接起点：test / `4694e78739dc8493786d366eacf5a6e07d7163b8`，clean，远端test同SHA；main仍fd233fa。
- 前轮PC开发者密码入口源码fe3c7d4与交付记录4694e78已推送test；当前r3按同一会话追加请求支持两个密码，model与文档待提交，不改变main。
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
| PC开发者入口 | Ctrl+Shift+H先弹密码框；两个可接受密码任一精确匹配后确认/回车启用内存免登录，取消/错误不启用，重启复位；V0/FLT-08待运行 |
| 常亮与保活 | 默认连接服务运行期间静默常亮，无新增设置入口；ADB连接验证后尝试本应用电池优化豁免，不保证进程免杀 |

详见 [12本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)、[Android原链](../docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md)。

## 本轮工作与验证

- 当前任务：[T-2026-10-25-001 / r3](../docs/tasks/T-2026-10-25-001-developer-login-password.md)，一个model与领域/安全/测试矩阵/记忆同步；本次test提交推送和Windows编排待交付。
- Registry：WORK-20261025-001-r3；最新Event：CE-20261025-T001-03；Decision评估：N/A — no decision delta（D-033保留历史）。
- 授权：实现、提交推送test、单次Build AShell Clients-BT / Windows / 常规编译，临时测试签名及Actions产物；无main推送或生产发布。
- V0：源码调用/失败与取消/重入/销毁路径、diff检查；无本地编译/分析器/交互测试。FLT-08运行验收待证，dispatch接受不等于编译通过。
- 前轮编排回执：HTTP204，run40200073517 / head fe3c7d4，首次核对in_progress；该run不包含r3新增密码，不能用它证明本次实现。
- 当前workflow源码仅Build-Clients-BT.yml，旧构建文档入口为历史快照；不据此执行已删除工作流。SEC-016和既有正式验证/外部资产缺口保持。
