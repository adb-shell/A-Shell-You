# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261107-004-r2`
Last Updated / Observed At：2026-11-07（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- 本次起点main/9040c53d37e3542c7ab8948b64beec1f0df0cd93 clean；按用户要求切换已有test/d9f111944da644b427f7fbfc5970de6f14604ef4 clean，在test保留本任务未提交增量；main无写入。
- 当前工作区快照：[Baseline26](../docs/BASELINE/26_DEVELOPER_SCREEN_PRIORITY_BASELINE_2026-11-07.md)，`OW-BL-2026-11-07-d9f1119-screen-priority-worktree`。test版本仍5.2.1/5.2.1+59，未合入main的2.0.26版本更改。
- 本次仅test切换、源码及必要文档修改；无stage/commit/push/merge/编排/部署/设备操作。身份迁移与历史run结果见历史任务，不自动恢复监控或沿用验证结论。
- 回退后撤回的实现、实验方案和诊断结论不属于当前实现。后续会话先核对 HEAD/status，再由代码重新建立证据；不能沿用历史聊天中的“已修改”判断。

## 当前能力摘要

| 领域 | 当前实现与限制 |
|---|---|
| 产品身份 | 显示“开放世界”；内部 OpenWorld/openworld、com.openworld.me；flutter_hbb保持；Rust 5.2.1 / Flutter 5.2.1+59 |
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

- 当前任务：[T-2026-11-07-004](../docs/tasks/T-2026-11-07-004-developer-screen-priority.md)，T6 handoff；源码和T7文档就绪，T8未关闭。
- Registry：WORK-20261107-004-r2；Events：CE-20261107-T004-01/02/03；Decision：D-035 / ADR-0028。用户已明确确认同时暂停普通操作、保留语音，源码与验证状态不变；本次答复不涉及提交推送/编排授权。
- V0：122项身份合同、旧协议字段保持/新字段唯一、C ABI两侧匹配、密码摘要/generated/依赖边界及diff通过；无修改generated、版本、构建工作流或Kotlin/JNI原采集代码。
- 四项纯策略单测已加入未执行；本机无rustc/cargo/dart/flutter。DSP-02—10正式单测/双端编译/真机通话/新旧端矩阵NOT_RUN，不能声称零编译错误或完美运行。
- 待用户确认本轮提交推送test及Both/常规编译后取得真实CI证据；保留现有工作流，不增加发布动作。角色声明不是可信凭证，SEC-016及既有外部资产风险不在本任务关闭。
