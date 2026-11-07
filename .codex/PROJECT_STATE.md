# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261107-004-r4`
Last Updated / Observed At：2026-11-07（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- 本次起点main/9040c53d37e3542c7ab8948b64beec1f0df0cd93 clean；按用户要求切换已有test/d9f111944da644b427f7fbfc5970de6f14604ef4 clean，源码已以“开发者模式画面优先”提交推送test/25bc2ce04484c6d40f8c06325008d78a347432f7；main无写入。
- 当前工作区快照：[Baseline26](../docs/BASELINE/26_DEVELOPER_SCREEN_PRIORITY_BASELINE_2026-11-07.md)，`OW-BL-2026-11-07-d9f1119-screen-priority-worktree`。test版本仍5.2.1/5.2.1+59，未合入main的2.0.26版本更改。
- 用户续接授权test提交推送和双端编译、不监控；Both/常规编译HTTP204接受，单次定位尚无run，不继续查询。无merge/部署/设备操作；回执文档另行提交，不二次编排。历史run不沿用。
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

- 当前任务：[T-2026-11-07-004](../docs/tasks/T-2026-11-07-004-developer-screen-priority.md)，T8源码/文档/Git/触发交付完成；用户要求不监控，构建最终结果未知。
- Registry：WORK-20261107-004-r4；Events：CE-20261107-T004-01—05；Decision：D-035 / ADR-0028。暂停操作保留语音、test提交推送、双端编排均已得到本轮明确授权。
- V0：122项身份合同、旧协议字段保持/新字段唯一、C ABI两侧匹配、密码摘要/generated/依赖边界及diff通过；无修改generated、版本、构建工作流或Kotlin/JNI原采集代码。
- 四项纯策略单测已加入未执行；本机无rustc/cargo/dart/flutter。DSP-03双端编排接受、最终结果未查；DSP-02及04—10单测/真机通话/兼容验收NOT_RUN，不能声称零编译错误或完美运行。
- 唯一workflow394791509 / Build-Clients-BT / Both / 常规编译于2026-11-07 09:26:23 UTC请求，HTTP204，source25bc2ce。不监控、不重试dispatch、不发布。角色声明不是可信凭证，SEC-016及既有外部资产风险保持。
