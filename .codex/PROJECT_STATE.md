# AShell Project State

Schema Version：`1.0`
State Revision：`STATE-20261025-002`
Last Updated / Observed At：2026-10-25（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- 当前冻结源码快照：[Baseline23](../docs/BASELINE/23_DEVELOPER_PASSWORD_BASELINE_2026-10-25.md)，`48226fbcdc08719b6e2a5c2e245dc9c10cc77ae9`；含T001双密码实现与交付记录，Baseline22保持历史含义。
- T002起点：test/48226fb，main/fd233fa，工作区clean，两分支与远端一致；main为test祖先，差异四项提交均属密码功能/记录。
- 已在main准备no-ff merge test，无冲突；同步文档加入同一提交，待以“开发者密码增加”提交推送和Both/常规编译。产品/工作流实现保持test原样。
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

- 当前任务：[T-2026-10-25-002](../docs/tasks/T-2026-10-25-002-developer-password-main.md)，文档同步、test合并main与双端编排；无新业务代码修改。
- Registry：WORK-20261025-002；Event：CE-20261025-T002-01；Decision评估：N/A — no decision delta。
- 当前用户授权：以“开发者密码增加”提交推送main，Build AShell Clients-BT / Both / 常规编译，沿用临时测试签名/Actions产物；无生产发布或本机产品验证。
- V0：文档/基线/入口与当前源码核对，产品/工作流树与test一致；20份Markdown/222个本地引用、diff检查通过。FLT-08运行仍未验，dispatch接受不等于编译通过。
- 前轮test run40203581056/head356f53b在T002起点仍in_progress；main双端run待触发，不能用前轮结果代替。
- 当前唯一workflow为Build-Clients-BT.yml，当前构建文档已纠正旧入口。SEC-016与既有正式验证/外部资产缺口保持。
