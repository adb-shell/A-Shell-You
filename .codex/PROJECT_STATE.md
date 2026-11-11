# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261111-001`
Last Updated / Observed At：2026-11-11（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- 当前任务：[T-2026-11-11-001](../docs/tasks/T-2026-11-11-001-docs-main-merge.md)，按用户要求同步文档后合并推送main；输入main/9040c53d37e3542c7ab8948b64beec1f0df0cd93、test/51292c29a025ee3ad6a3915285b8f21e7e260ee8，初始工作区干净且远端一致。
- 当前合并基线：[Baseline27](../docs/BASELINE/27_MAIN_DEVELOPER_PRIORITY_BASELINE_2026-11-11.md)。产品源码沿用test/25bc2ce，版本及校验文件保留main的2.0.26 / 2.0.26+59；不引入新的业务逻辑改动。
- 本轮只同步文档和Git交付，不触发或监控编译。此前test/25bc2ce的Both/常规编译HTTP204仅表示受理；最终结果未知，不能替代合并后版本验证。
- 回退后撤回的实现、实验方案和诊断结论不属于当前实现。后续会话先核对 HEAD/status，再由代码重新建立证据；不能沿用历史聊天中的“已修改”判断。

## 当前能力摘要

| 领域 | 当前实现与限制 |
|---|---|
| 产品身份 | 显示“开放世界”；内部 OpenWorld/openworld、com.openworld.me；flutter_hbb保持；Rust 2.0.26 / Flutter 2.0.26+59 |
| Android 原链 | core连接服务、MediaProjection、无障碍输入/无视/穿透/黑屏保持各自原入口；服务运行不等于屏幕共享或首帧 |
| ADB身份 | Android进程持有配对身份；双NSD发现，pairConnect后以真实 id -u 验证；配对记录不代表Shell已可用 |
| ADB执行 | 2路独立命令、设备共享状态变化推送、有限意外断链恢复；PC无三秒远程status |
| ADB画面 | scrcpy ScreenCapture + 独立GPU/H.264；常驻截图JPEG及raw/PNG兼容回退；Shell取节点、App绘穿透 |
| ADB参数 | width/350整数缩放；4096边长/4Mi像素界限；只读普通VideoQoS，运行时更新；无视至少300ms，节点至少80ms |
| ADB输入 | Shell可用时全局优先触摸，独立control-only连接；普通键盘、远程输入、Ctrl+V及PC→Android纯文本剪贴板同步 |
| 黑屏 | 独立黑色buffer层及App预渲染白字PNG，排除自身采集；OEM表现待真机证据 |
| PC界面 | 普通左/ADB右、单源居中；Shell栏无悬浮提示；状态分组；无障碍栏无视频/特殊/防触状态文字 |
| PC开发者入口 | Ctrl+Shift+H先弹密码框；两个可接受密码任一精确匹配后确认/回车启用内存免登录，取消/错误不启用，重启复位；V0/FLT-08待运行 |
| 开发者画面优先 | 进程模式供新建/重连使用；Android认证后暂停普通画面/盲输入/采集控制，保留会话与ZEGO；多开发者可接收，最后退出恢复；协作角色可由修改版伪造，正式验证待证 |
| 常亮与保活 | 默认连接服务运行期间静默常亮，无新增设置入口；ADB连接验证后尝试本应用电池优化豁免，不保证进程免杀 |

详见 [12本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)、[Android原链](../docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md)。

## 本轮工作与验证

- Registry：WORK-20261111-001；Events：CE-20261111-T001-01；沿用D-034/035、ADR-0027/0028，本轮无新决策或外部资产变化。
- V0核对合并来源、版本边界、文档链接、身份合同和差异；结果与Git回执记录在当前任务。主分支版本历史、test功能历史及冻结Baseline25/26均保留。
- 四项纯策略单测未执行；DSP-02及04—10单测/真机通话/兼容验收仍NOT_RUN，DSP-03仅有历史受理回执。本轮无编译、codegen、设备或生产操作。
- 开发者角色是协作声明，不是可信凭证；SEC-016及既有外部资产缺口保持。旧任务详情见TASK_HISTORY，不继承其授权或构建结论。
