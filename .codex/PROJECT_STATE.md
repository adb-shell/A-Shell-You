# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261112-003-r7`
Last Updated / Observed At：2026-11-12（Asia/Tokyo）
Evidence Scope：`repository-observed / static and offline shell checks; product/device NOT_RUN`

当前T003 / complete/T8续检交付：源码0e5cc3dfe567ffe0617849cea25c0e0eb5276797已推送test，触发前本地/远端一致。2026-11-12 14:50:21 UTC触发Both/常规编译，HTTP204；未查询运行进度/结果。回执文档续交不再编排。24项静态与diff通过，正式编译/设备待证。WORK-20261112-003-r7 / CE-20261112-T003-07；以下保留前阶段证据，不再代表当前未提交。

最新续接：T003 / complete/T8本轮本地复检，test/b25b9aa上有未提交修复。命令确定未执行时还原暂停记录；PC查询按revision/epoch合并菜单快照，共享监听不回写旧权限。24项静态与diff通过；只变五个ADB源码文件和相关记录，原Kotlin生命周期/采集/媒体/输入/语音及系统列表修改命令保持。无提交推送、无编排或进度查询；正式构建与设备NOT_RUN。WORK-20261112-003-r5 / CE-20261112-T003-04/05。下方为首次交付历史记录。

当前 T-2026-11-12-003 / complete/T8（本次交付）：test/a8abdd4输入，源码115d0bb3b26e441aa1d13e0f8bcbd79ef69acc42已推送test，实现ADB本应用无障碍开启/关闭/暂停恢复。动态自身组件、设备暂停记录与多PC状态，保留其他服务列表；原无障碍生命周期/采集/媒体/触摸/ZEGO源码不改。25项静态合同、13项离线Shell和diff通过，真机NOT_RUN。2026-11-12 13:42:08 UTC触发Both/常规编译，HTTP204；未查询运行进度或结果。回执仅文档续交、不再编排。WORK-20261112-003-r3 / CE-20261112-T003-01/02/03 / D-037。下方既有任务为历史记录，不继承构建结论。

最新续接：T-2026-11-12-001 / complete/T8（续修交付），test/bbebc74 clean。f4a71c6的Android依赖步骤仍DNS失败，并出现_apt索引权限警告；此前7项命令桩不代表Linux/网络通过。已修正/tmp沙箱路径与有界三镜像选择，Bash语法/13项离线回归通过，Linux权限与runner DNS仍待CI证据。已推送test/c788e8d并触发Both/常规编译，HTTP204；不监控，未查询新结果。后续仅补回执文档。WORK-20261112-001-r6 / CE-20261112-T001-04—06。

当前任务T-2026-11-12-001 / complete/T8（指定交付范围）：test/e0ea612 clean恢复；用户ZIP证实cd21705的Windows编译失败与Android依赖镜像DNS阻塞后取消。源码与CI修复已提交为f4a71c6e6d57ac41ca1ecf49aa50bb791ad8458b并推送test，Bash语法/离线流程7/7通过；保留PC2新地址。Both/常规编译已HTTP204受理，不监控，未查询新结果；后续只补回执文档。WORK-20261112-001-r3 / CE-20261112-T001-01—03；[详情](../docs/tasks/T-2026-11-12-001-build-fixes.md)。下方T002回执保留当时证据，不再代表最终结果未知。

## 当前源码基线

- 本轮从干净main/5d2d2e932cfb390a7b3d0278bea6a7e080746c95按用户要求切换已有test/51292c29a025ee3ad6a3915285b8f21e7e260ee8。本次实现已提交为cd2170512613291d48709bc5ec9e393c8feb42e8并推送origin/test；后续仅补回执文档。
- 输入基线[Baseline26](../docs/BASELINE/26_DEVELOPER_SCREEN_PRIORITY_BASELINE_2026-11-07.md)，test版本5.2.1/5.2.1+59保持；没有合入main的2.0.26版本更改。
- 最新授权已执行：test提交推送、Both/常规编译HTTP204，不监控且未查询结果。无main写入、版本变化或生产发布。

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

- [T-2026-11-11-002](../docs/tasks/T-2026-11-11-002-server-profiles.md)，complete/T8（仅本次交付范围）：集中配置与PC状态栏选择器已实现。提交前现有编辑已更新三套地址：Android与PC1相同，PC2独立且四项填齐；共享Key保持。
- 唯一维护文件`libs/hbb_common/src/server_profiles.rs`；独立账号/peer缓存、后台确认、真实注册状态、旧响应过滤与UI刷新。见[15_SERVER_PROFILES](../docs/AI_ENGINEERING/15_SERVER_PROFILES.md)。
- 首轮V0 27/27、续接31/31静态合同及diff通过；四项配置与两项WebSocket单测已维护未运行；本机无cargo/rustc/flutter/dart，双端构建仅有HTTP204受理，未查询结果；设备/语音/API联调NOT_RUN，不能声明所有功能验收通过。
- WORK-20261111-002-r5 / CE-20261111-T002-01—05 / D-036 / ADR-0029。生成桥接/协议/版本/依赖/构建脚本保持，无真实服务访问或凭据变更。
