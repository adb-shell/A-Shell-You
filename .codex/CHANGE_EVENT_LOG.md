# AShell Change Event Log

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

Schema Version：`1.0`  
Coverage Start：2026-07-12  
Last Updated：2026-10-25（PC开发者密码入口）

Mode：`append-only logical persisted-change events`

## CE-20261025-T001-03：同一功能追加第二个可接受密码

- 当前用户明确要求新增第二个密码并保留原密码；test/4694e78初始clean、远端一致。作为同一会话功能续接，交付继续使用已确认的test推送与Windows常规编排；不继承其他会话/任务授权。
- `flutter/lib/models/developer_login_bypass_model.dart` 从单摘要比较改为两个摘要const集合包含检查，原摘要原样保留；其余失败/成功状态、页面/连接/工具栏密码逻辑不变，不新增明文口令副本。
- 同步03_MODULE_DESIGN、10_SECURITY_MODEL、TEST_MATRIX、原任务r3节及PROJECT_STATE/PROJECT_MEMORY/CURRENT_WORK/CHANGELOG_AI/TASK_HISTORY/本日志；无新ADR/Decision/依赖/资产。
- V0 PASS：两个摘要/原摘要保留、唯一启用调用、diff和10份Markdown/111本地引用；Git/编排回执待回填。无本机产品build/test/analyze/设备/生产操作。STATE/WORK-20261025-001-r3。

## CE-20261025-T001-02：test推送与Windows编排回执

- 用户续接授权已执行：test快进fd233fa后提交fe3c7d445a531a11ebbe5c30c9e3b047e2874ba3，push/远端核对成功；main保持fd233fa，无main推送。
- Build AShell Clients-BT / workflow394791509，test / Windows / 常规编译，HTTP204；run40200073517、head fe3c7d4，首次核对in_progress。一次dispatch，未声称编译/交互PASS。
- 此记录只同步 `docs/tasks/T-2026-10-25-001-developer-login-password.md`、`.codex/PROJECT_STATE.md`、`PROJECT_MEMORY.md`、`CHANGELOG_AI.md`、`TASK_HISTORY.md`、`CURRENT_WORK.md`及本日志；无产品/工作流增量，不再次编排。
- STATE/WORK-20261025-001，T8关闭；本次无新Decision/ADR/外部资产，运行验收仍待证。

## CE-20261025-T001-01：PC开发者免登录入口密码确认

- 当前用户授权两份Dart实现与文档同步，续接授权提交推送test和一次Windows常规编排；main/fd233fa初始clean，test已从7e45842快进到当前main，原提交完整保留。
- `flutter/lib/desktop/pages/connection_page.dart`新增单实例隐藏密码框、统一确认/回车/取消和销毁清理；`flutter/lib/models/developer_login_bypass_model.dart`改为精确摘要校验后启用内存开关。
- 同步 `docs/AI_ENGINEERING/03_MODULE_DESIGN.md`、`10_SECURITY_MODEL.md`、`TEST_MATRIX.md`、本任务和PROJECT_STATE/PROJECT_MEMORY/CURRENT_WORK/TASK_HISTORY/CHANGELOG_AI及本日志；无新ADR/Decision/依赖/资产/版本/生成桥。
- V0调用与diff核对；本机build/test/analyze、设备/生产执行none；Git提交推送和dispatch结果待回填。固定本地校验不关闭SEC-016。
- STATE/WORK-20261025-001；[任务](../docs/tasks/T-2026-10-25-001-developer-login-password.md)。

## CE-20261019-T006-01：以0dbe202重整当前文档与记忆

- 用户明确授权文档同步及test→main交付，“文档更新”；源码回退已在本任务前完成。本次不再reset或强推，不执行CI。
- 当前恢复快照只指向0dbe202及其文档后继；工程事实由源码核对，历史聊天中的已撤回内容不进入当前合同。旧任务/基线保留历史含义。
- 仅Markdown，源/协议/构建/资产树保持；V0核对，未编译、真机、集成或生产执行。构建/性能/ROM结论仍待正式证据。
- STATE/WORK-20261019-006、D-033、Baseline22；[详细任务](../docs/tasks/T-2026-10-19-006-restored-docs-sync.md)。Git最终SHA/一致性以实际交付查询为准。

## CE-20261019-T004-01：ADB优化合并main

- 用户明确授权合并、提交、推送main并执行编排；本地/远端test=f28055c、main=e3b4454，初始clean。采用--no-ff合并保留五项提交，说明“完整优化adb投屏”；补记录前暂存树与test一致，diff检查通过。
- 本轮仅新增docs/tasks/T-2026-10-19-004-merge-adb-to-main.md并同步五份任务/记忆索引；业务及工作流无额外修改。推送后单次Build-Clients-New/main/Both/常规编译，不监控；准确Git/dispatch见交付，未本地产品验证，不预写构建成功。STATE/WORK-20261019-004。

## CE-20261019-T003-02：移除功能启动提示交付

- 用户授权提交并构建编排；执行前本地/远端test均2bbdf50，10路径包含三份Dart与必要任务记录。提交推送后单次Build-Clients-New/Both/常规编译，不监控；沿用测试签名及Actions产物下载。
- 无新增业务/配置或本地产品验证；准确Git/dispatch见会话交付，不预写构建/真机通过。STATE/WORK-20261019-003-r2；docs/tasks/T-2026-10-19-003-remove-feature-start-hints.md。

## CE-20261019-T003-01：移除功能启动提示

- 用户仅授权移除无障碍功能启动小字并保护原链；test/2bbdf50 clean，三份Dart删除专用提示，连接等待/ADB/T002恢复保持。V0原命令一致性、残留/词法/diff检查通过；无Git/CI/产品验证。STATE/WORK-20261019-003；docs/tasks/T-2026-10-19-003-remove-feature-start-hints.md。

## CE-20261019-T002-02：等待提示与显示恢复交付

- 用户授权推送并构建；本地/远端test均8e26f0b，15路径包含T001/T002源码与记录。提交推送后单次Build-Clients-New/Both/常规编译，不监控；测试签名/Actions下载沿用原编排。
- 无新增业务/配置或本地产品验证；准确Git/dispatch见会话交付，构建/真机不预写PASS。STATE/WORK-20261019-002-r2；docs/tasks/T-2026-10-19-002-ordinary-surface-resume.md。

## CE-20261019-T002-01：关闭ADB后的普通纹理恢复

- test/8e26f0b + T001 dirty，用户授权修复关闭ADB后三种模式共用的普通画面恢复；三份Dart增加本地Texture层重绘和下一帧通知，保留native生命周期、协议及原采集/输入。T001修改完整保留。
- V0完成；原报告未本机复现，用户也尚未试过重开ADB，冻结根因/效果待正式验证。无Git/CI/构建/设备操作。STATE/WORK-20261019-002；docs/tasks/T-2026-10-19-002-ordinary-surface-resume.md。

## CE-20261019-T001-01：PC画面区域等待提示

- test/8e26f0b初始clean，用户授权两项等待显示并强调不得破坏传输/功能；仅四份Dart展示与本机通知处理，原命令和native/protocol不改。无最短停留、清旧画面或新增采集操作。
- V0完成；Android合同、任务和五份索引同步。无Git/CI/构建/真机，无Decision/ADR/外部资产增量。STATE/WORK-20261019-001；docs/tasks/T-2026-10-19-001-inline-frame-wait.md。

## CE-20261019-T010-02：名称变更交付续接

- 用户授权提交推送并编译；test/4b729dd与远端一致，33个本任务文件。提交推送test后单次Build-Clients-New/Both/常规编译，不监控；无额外业务或构建配置修改。
- V0 diff与范围复检；准确Git/dispatch回执见交付，不预写构建通过。STATE/WORK-20261018-010-r2；docs/tasks/T-2026-10-18-010-display-name.md。

## CE-20261018-T010-01：产品显示名

- test/4b729dd + 既有Android app_name dirty；用户授权安全范围的显示名修改。产品显示“一个壳子”，默认品牌映射/平台元数据同步，Windows快捷方式和旧标题兼容；业务/权限/协议/身份保持。
- V0静态检查；未Git/CI/产品执行。当前总览、项目记忆、任务/决定/事件索引同步；无外部资产或依赖增量。STATE/WORK-20261018-010 / D-032；docs/tasks/T-2026-10-18-010-display-name.md。

## CE-20261018-T009-01：ADB媒体参数对齐

- 用户授权试验对齐后test提交推送、单次编排；test/66d0657初始clean。新增AdbVideoCapture/AdbVideoSurface、独立JNI参数/尺寸、ADB视图尺寸；原采集/无障碍/输入/协议/构建/资产版本不改。
- 普通QoS fps/ratio与码率公式、整数缩放；GPU编码前限帧，MediaCodec实时码率，私有socket参数/续租，逐帧截图节奏。最小14合同、任务与五份记忆同步。
- V0源代码/DEX接口/hash/取消回收/diff；本地产品验证NOT_RUN。参数对齐不代表实际图像/码率/帧率相同，GPU/设备/多PC仍待证。准确Git/dispatch回执见交付。

## CE-20261018-T008-01：独立ADB性能与兼容修复

- 当前用户C2/C3授权；test快进main/e3b4454，初始clean。修改localadb媒体/截图/遮罩/命令类、proguard-rules、desktop/adb弹窗/媒体/侧栏及src/remote_adb_video.rs，新增AdbScreenshotCapture/LocalAdbScreenshotSession。字段/依赖/原链路/构建脚本不改。
- 常驻截图、80ms节点与事件缓存、独立30fps、buffer遮罩及创建时排除、UTF-8直接命令/逐包ACK/真实错误、位移局部更新；最小13/14合同、本任务和五份记忆同事件。无外部资产或长期决定增量。
- V0引用、括号、资源/取消、diff检查；未本地产品验证。255与黑屏设备原因未获复现，实测待证。test推送及单次Both/常规编译获准，不监控；Git/dispatch回执见交付。

## CE-20261018-T007-01：test 合并 main 与编排授权

- 用户授权合并、提交“ADB功能问题完善”、推送 origin/main 及构建一次；单次 Build-Clients-New/main/Both/常规编译，不监控。
- 起点 test/82e0f7f clean；fetch 后 main/af1c72d、origin/main/130f748 均为 test 祖先，无冲突合并，写记录前暂存树与 test 相同。仅补任务与五份最小记忆，无额外业务/依赖/工作流/协议/资产改变。
- STATE/WORK-20261018-007；V0 祖先/树/范围/diff 检查，准确提交/推送/dispatch 回执见 Git/交付，不将触发视为构建或设备验证通过。

## CE-20261018-T006-04：Windows 修复提交推送及编排授权

- 用户追加提交/编排授权，目标 adb-shell/A-Shell-You/test；单次 Build-Clients-New.yml、Windows/常规编译，不监控。执行前 test/de12290，七路径均为 r3 及本次记录，diff 检查通过。
- 无新增产品代码、工作流或依赖修改；STATE/WORK-20261018-006-r4。准确 SHA/远端一致性/dispatch 接受结果见 Git 与交付；构建和设备验证待证。

## CE-20261018-T006-03：修复 Windows 私有字段访问编译错误

- 用户提供 logs_105834405459.zip：de12290 Windows Rust E0616，src/remote_adb_video.rs:735；日志作为诊断数据处理。初始 test/de12290 clean，本次局部修复该任务回归。
- 源码仅初始剪贴板同步读取 PeerInfo 的来源从 FlutterHandler 私有字段改为公开 Session.lc.peer_info，缺失时跳过初始同步，原权限/偏好和正常同步保持。
- V0 访问级别/写入清理/类型/锁序/cfg/差异检查通过；修复后构建/真机待证。仅最小任务与五份记忆同步，STATE/WORK-20261018-006-r3；无 Git/CI、本地产品执行、依赖/协议/工作流/ADR/资产变化。

## CE-20261018-T006-02：test 提交推送及编排授权

- 用户授权提交推送 test 并执行编排；目标 origin=adb-shell/A-Shell-You，执行前 test/738a3fe。既有 T004/T005/T006 改动统一交付，本轮只补最小交付记录，无新增产品代码修改。
- git diff --check 通过；Build-Clients-New.yml 已核对，单次 ref=test、Platform=Both、BuildMode=常规编译，不监控。准确 Git/dispatch 回执见交付，构建与设备结果仍未知。
- STATE/WORK-20261018-006-r2；无 Decision/ADR/资产/依赖/版本增量，无本地产品 build/test/analyze/codegen。

## CE-20261018-T006-01：ADB 剪贴板同步

- Task T-2026-10-18-006，STATE/WORK-20261018-006；test/738a3fe + T004/T005 dirty，实现与V0 complete。
- PC原监听的会话分发增加ADB fallback，字段40 clipboard RPC/能力标识，Android paste=false与独立最新自动队列，显式paste优先；取消JNI及keep规则匹配。原无障碍/普通剪贴板/媒体保持。
- V0：调用链/权限/锁序/owner/sequence/分段/清理与diff检查；构建/真机待证。未提交/推送/编排，无ADR/Decision/外部资产增量。

## CE-20261018-T005-01：ADB 普通键盘输入

- Task T-2026-10-18-005；STATE/WORK-20261018-005，test/738a3fe + T004 dirty；实现与 V0 complete。
- LocalAdbKeyboard + LocalAdbTouch/Manager/keep rule，Rust 原 Android KeyEvent/JNI，Flutter inputKey gate；原 paste hook/ACK、媒体与无障碍实现不改。
- 证据：类型/调用链/权限/owner释放/单writer/控制协议及 git diff 空白检查；构建/真机待证。无 Git/CI，无 ADR/Decision/外部资产增量；既有 T004 修改保留。

## CE-20261018-T004-01：光标与无障碍 Ctrl+V

- T004，用户 C2 三项修复授权，C1 必要合同/记忆；test/738a3fe clean，未执行 C3。
- 源码：`flutter/lib/desktop/adb/{remote_adb_sidebar,remote_adb_mirror}.dart`、`flutter/lib/desktop/pages/remote_page.dart`、`src/remote_adb_video.rs`、`flutter/android/app/src/main/kotlin/me/ashell/nZW99cdXQ0COhB2o.kt`。按钮缩字、光标使用会话权限、Ctrl+V 本地请求分流及焦点文本单次提交；无 peer/JNI/生成桥变化。
- 同步 `docs/AI_ENGINEERING/{04_ANDROID_PIPELINE,13_REMOTE_ADB,14_ADB_MIRROR}.md`、[任务](../docs/tasks/T-2026-10-18-004-accessibility-paste-cursor.md)、五份 `.codex` 任务记忆。STATE/WORK-20261018-004；Decision/ADR/资产无增量。
- V0 数据流/权限/生命周期/选区与 diff 范围检查；V1—V3 NOT_RUN，无 Git/CI，待正式双端构建与手机测试。

## CE-20261018-T003-03：test 提交推送及编排授权

- 用户明确“推送test并编译”；对当前 T003 三份源码及七份合同/任务记忆执行提交推送 origin/test，随后单次 Build-Clients-New.yml / ref=test / Platform=Both / BuildMode=常规编译，不监控。
- 交付前复查分支、diff、无入口残留及工作流参数；此批仅同步任务与五份 `.codex` 的授权/交付边界，不追加源码或构建配置修改。STATE/WORK-20261018-003-r3；无 Decision/ADR/资产增量。
- 准确 SHA、push/dispatch 接受回执见最终会话交付；不宣称 CI 通过，不执行本地编译或真机操作。

## CE-20261018-T003-02：取消新增常亮设置入口

- T003/r2；用户明确要求取消顶栏齿轮和设置入口，默认静默常亮。在上一轮自身未提交改动上移除 `flutter/lib/mobile/pages/home_page.dart` 的新增 import/路由，删除本任务新增 `flutter/lib/mobile/pages/app_settings_page.dart`；不删除原文件，无其他并行改动。
- 默认 service-on 与主页常亮控件移除保留；已有有效配置兼容，无强制写配置/新权限请求。静态复检无入口/引用残留、HomePage 与 HEAD 一致、diff 范围/空白；无构建/真机/Git/CI。
- 同步 Android 合同、[任务](../docs/tasks/T-2026-10-18-003-keep-screen-on-settings.md)及五份记忆；STATE/WORK-20261018-003-r2。无 Decision/ADR/资产增量；事件01为已被 r2 更新的历史方案。

## CE-20261018-T003-01：常亮设置迁移与 service-on 默认

- T-2026-10-18-003，test/9e28a90 clean；用户 C2 授权 UI 迁移及默认值调整，必要 C1 合同/记忆；无 C3 操作。
- 源码路径：`flutter/lib/mobile/pages/{app_settings_page,home_page,server_page,settings_page}.dart`、`flutter/android/app/src/main/kotlin/me/ashell/KeepScreenOnState.kt`；主页移除专用控件与状态，独立设置入口，仅默认分支调整，旧有效配置保留。
- 同步 `docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md`、`docs/tasks/T-2026-10-18-003-keep-screen-on-settings.md` 与 `.codex/{PROJECT_STATE,CURRENT_WORK,CHANGELOG_AI,TASK_HISTORY,CHANGE_EVENT_LOG}.md`。STATE/WORK-20261018-003；Decision/ADR/资产无增量。
- V0 差异/引用/入口与配置链检查；无编译/真机/Git/CI，当前改动未提交。见[任务](../docs/tasks/T-2026-10-18-003-keep-screen-on-settings.md)。

## CE-20261018-T002-01：ADB 豁免省电与唤醒键

- T-2026-10-18-002，test/0b5ad99 clean；新增包级电池优化豁免任务及真实状态，Shell 栏唤醒键复用独立命令链；普通链路、协议、构建无改动。
- V0，V2/V3 待证；用户授权 test 提交推送及单次 Both/常规编译，不监控。STATE/WORK-20261018-002；无 Decision/ADR/资产增量；Git/dispatch 接受回执见交付。
- [任务与正式验证清单](../docs/tasks/T-2026-10-18-002-adb-keep-alive-and-wakeup.md)。

## CE-20261018-T001-01：真实连接常亮与输入弹窗

- T-2026-10-18-001，test/00193fa clean；主页保持常亮设置、CM 只读 JNI 与共用前后台策略，已有 ADB 弹窗回车/确定发送，Ctrl+V 保持。
- 用户授权代码/必要记忆、test 提交推送、单次 Both/常规编译、不监控；V0，不执行本地产品验证。STATE/WORK-20261018-001；Decision/ADR/资产无增量，准确 Git/dispatch 回执见交付。
- [任务与验证边界](../docs/tasks/T-2026-10-18-001-keep-screen-on-and-adb-input.md)。

## CE-20261017-T003-01：ADB快捷粘贴及截图节奏

- T-2026-10-17-003，test/8ef006e clean，用户授权Ctrl+V、画面参数对齐、test提交推送、单次test编排不监控。
- 共用键盘入口2行ADB分流，独立模块处理快捷键/剪贴板/焦点；统一文本校验。移除960 profile、JPEG额外码率等待，300ms目标间隔与4帧回执窗口。
- V0静态审查，无产品编译/设备测试；原采集/无障碍/剪贴板实现与协议/生成代码保持。最小合同/任务/记忆同步，STATE/WORK-20261017-003。实际Git和dispatch接受回执见交付，不作为构建通过证据。

## CE-20261017-T002-01：test ADB输入与媒体优化

- Task T-2026-10-17-002；起点af1c72d clean，用户授权test分支代码/提交推送/单次CI，不监控。
- 12份独立ADB源码：UTF-8粘贴及ACK/取消；二进制帧所有权；回执窗口/媒体上限/原始截图。最小合同及任务/记忆同步；普通链路零差异。
- V0通过，产品编译/真机NOT_RUN；Git及dispatch接受证据见最终交付，不声称CI通过。STATE/WORK-20261017-002；无外部资产或Decision增量。

## CE-20261017-T001-01：构建模式标签同步替换

- Task：T-2026-10-17-001；source main/130f748，初始clean。
- Scope：`.github/workflows/Build-Clients-New.yml` 十处模式字符串及本任务、Build System和五份工程记忆。
- Result：选项和所有判断统一为“原始编译”；其他工作流文本与HEAD一致，旧工作流保持。
- Evidence：V0精确替换等价性、旧标签零匹配、新标签十次、diff检查；无构建或设备证据。
- Authority：用户当前明确授权修改和本地提交；不push、不dispatch、不执行build/test/analyze/codegen。
- Decision / external assets：N/A — no decision delta / no asset delta。

> 一个 Change Event 是一次逻辑完整的代码/文档持久化修改批次，不是每次按键、每个 patch hunk、只读调查或对话消息。本文件建立前的精确 edit-event 历史无法重建，只能按已知 Task 做 aggregate backfill。日志自身的创建/追加属于对应事件，不递归生成第二条事件。

## CE-20260712-T001-BACKFILL：项目资产接管变更集

- Record Type：`retrospective/aggregate`；精确事件未知。
- Task ID：`T-2026-07-12-001`
- Event Type：documentation / memory / Skill creation and update。
- Files：`docs/AI_ENGINEERING/`、初始 `.codex/`、`.agents/skills/` 与入口/审计文档；精确 per-patch 清单见当期交付和 worktree。
- State Delta：repository-side knowledge takeover established；no business behavior change。
- Verification：V0 only。
- Git / Build / Delete / External / Release：none。

## CE-20260712-T002-BACKFILL：AI 工程体系强化变更集

- Record Type：`retrospective/aggregate`；精确事件未知。
- Task ID：`T-2026-07-12-002`
- Event Type：governance documentation and Skill update。
- Files：entry、AI rules、Task Protocol、Development Workflow、External Registry、memory/logs、eight Skills and navigation pointers。
- State Delta：T0—T8、C0—C3 and external-asset governance established；no business behavior change。
- Verification：V0 only。
- Git / Build / Delete / External / Release：none。

## CE-20260712-T003-BACKFILL：AI 工程体系最终封版变更集

- Record Type：`retrospective/aggregate`；精确事件未知。
- Task ID：`T-2026-07-12-003`
- Event Type：ADR/Baseline/task/test/Safe-Superpowers governance creation。
- Files：`docs/ADR/`、`docs/BASELINE/`、`TASK_TEMPLATE.md`、`TEST_MATRIX.md`、Safe profile/Skill and related indexes。
- State Delta：repository-side AI governance sealed；no business behavior change。
- Verification：V0 only。
- Git / Build / Delete / External / Release：none。

## CE-20260712-T004-01：创建全局记忆核心文件

- Timestamp：2026-07-12T06:31:52+08:00
- Task ID：`T-2026-07-12-004`
- Actor / Primary Skill：AShell Principal Engineer / `ashell-master`
- Permission：`C1`，来自项目 owner 当前明确请求。
- Event Type：create。
- Files：`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/CHANGELOG_AI.md`、`.codex/CHANGE_EVENT_LOG.md`、`.codex/SESSION_START_PROTOCOL.md`。
- Change Summary：建立 current snapshot、multi-session work registry、task-level AI history、logical modification ledger and session recovery algorithm。
- Behavior / State Delta：AI governance only；no product/runtime behavior change。
- Related Decision / ADR：D-014 pending synchronization；ADR-0011、ADR-0012、ADR-0013 context。
- Verification：V0 pending final task audit。
- Git / Build / Delete / Move / Version / External / Release：none。

## CE-20260712-T004-02：接入入口、任务协议与治理索引

- Timestamp：2026-07-12T06:31:52+08:00
- Task ID：`T-2026-07-12-004`
- Actor / Primary Skill：AShell Principal Engineer / `ashell-master`
- Permission：`C1`，来自项目 owner 当前明确请求。
- Event Type：update。
- Files：`PROJECT_START_HERE.md`、`docs/AI_ENGINEERING/AI_TASK_EXECUTION_PROTOCOL.md`、`TASK_TEMPLATE.md`、`.codex/DECISION_LOG.md`、`.codex/CHANGE_EVENT_LOG.md`。
- Change Summary：固化八项 session recovery、T0/T5/T7/T8 memory transaction、Task artifact 字段和 D-014 governance decision。
- Behavior / State Delta：新会话与 AI task governance behavior changed；product/runtime behavior unchanged。
- Related Decision / ADR：D-014 / ADR-0011、ADR-0012、ADR-0013。
- Verification：V0 pending final task audit。
- Git / Build / Delete / Move / Version / External / Release：none。

## CE-20260712-T004-03：消除新会话恢复顺序歧义

- Timestamp：2026-07-12T06:40:28+08:00
- Task ID：`T-2026-07-12-004`
- Actor / Primary Skill：AShell Principal Engineer / `ashell-master`
- Permission：`C1`，来自项目 owner 当前明确请求。
- Event Type：update。
- Files：`PROJECT_START_HERE.md`、`.codex/AI_RULES.md`、`.codex/SESSION_START_PROTOCOL.md`、`docs/AI_ENGINEERING/AI_TASK_EXECUTION_PROTOCOL.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/CHANGE_EVENT_LOG.md`。
- Change Summary：明确 Session Start Protocol 位于第 1、2 项之间作为执行细则；统一 AI Rules 的全局恢复顺序；明确 active task 与 Task History closing record 的时序。
- Behavior / State Delta：session recovery ambiguity removed；product/runtime behavior unchanged。
- Related Decision / ADR：D-014 / ADR-0011、ADR-0012、ADR-0013。
- Verification：first blind recovery identified the ambiguity；regression pending。
- Git / Build / Delete / Move / Version / External / Release：none。

## CE-20260712-T004-04：完成全局记忆同步与任务关闭

- Timestamp：2026-07-12T06:42:13+08:00
- Task ID：`T-2026-07-12-004`
- Actor / Primary Skill：AShell Principal Engineer / `ashell-master`
- Permission：`C1`，来自项目 owner 当前明确请求。
- Event Type：update / task closure。
- Files：`.codex/SESSION_START_PROTOCOL.md`、`.codex/TASK_HISTORY.md`、`.codex/CHANGELOG_AI.md`、`.codex/CHANGE_EVENT_LOG.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`。
- Change Summary：修正 protocol metadata；写入 canonical task/changelog/event record；更新 final State pointers；最后关闭 Current Work active row。
- Behavior / State Delta：global memory task completed；active task count returns to zero；product/runtime behavior unchanged。
- Related Decision / ADR：D-014 / ADR-0011、ADR-0012、ADR-0013。
- Verification：V0 structural/sensitive/scope checks and blind recovery regression PASS；final closure recheck required immediately after this event。
- Git / Build / Delete / Move / Version / External / Release：none。

## CE-20260712-T004-05：修正最终恢复索引

- Timestamp：2026-07-12T06:43:50+08:00
- Task ID：`T-2026-07-12-004`
- Actor / Primary Skill：AShell Principal Engineer / `ashell-master`
- Permission：`C1`，来自项目 owner 当前明确请求。
- Event Type：corrective update。
- Files：`.codex/AI_RULES.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/CHANGELOG_AI.md`、`.codex/TASK_HISTORY.md`、`.codex/CHANGE_EVENT_LOG.md`。
- Change Summary：将 AI Rules 第 8 项固定为完整 Skill 路径；把 Last Closed Task 的 Changelog 指针改为 T-004；统一最终 State/Work/Event references。
- Behavior / State Delta：recovery index consistency corrected；product/runtime behavior unchanged。
- Related Decision / ADR：D-014 / ADR-0011、ADR-0012、ADR-0013。
- Verification：final V0 relation/order audit found both mismatches；full recheck follows this event。
- Corrects / Supersedes：corrects final pointer metadata produced around `CE-20260712-T004-04`；does not change its task-closure meaning。
- Git / Build / Delete / Move / Version / External / Release：none。

## CE-20260712-T004-06：规范机器可验证的恢复标识

- Timestamp：2026-07-12T06:45:42+08:00
- Task ID：`T-2026-07-12-004`
- Actor / Primary Skill：AShell Principal Engineer / `ashell-master`
- Permission：`C1`，来自项目 owner 当前明确请求。
- Event Type：corrective metadata update。
- Files：`.codex/AI_RULES.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/CHANGELOG_AI.md`、`.codex/TASK_HISTORY.md`、`.codex/CHANGE_EVENT_LOG.md`。
- Change Summary：把 AI Rules 第 2 项从代词改为完整路径；把 task/changelog event range 的两端都改为完整 Event ID；同步最终 State/Work pointer。
- Behavior / State Delta：machine-verifiable recovery references normalized；product/runtime behavior unchanged。
- Related Decision / ADR：D-014 / ADR-0011、ADR-0012、ADR-0013。
- Verification：addresses the only remaining order/relation failures from the final V0 audit；full recheck follows this event。
- Corrects / Supersedes：metadata references only；previous event meanings remain unchanged。
- Git / Build / Delete / Move / Version / External / Release：none。

## CE-20261012-T001-01：保存接管证据并接入新会话恢复

- Timestamp：2026-10-12T04:53:24+09:00
- Task ID：`T-2026-10-12-001`
- Actor / Primary Skill：Codex / `ashell-master`
- Permission：`C1`；当前用户明确要求保存本轮记忆供工作区新对话恢复；不扩大为业务修改或远端上传。
- Event Type：create / update / task closure；一个逻辑文档事务，包含其状态与日志同步。
- Files（13）：`AGENTS.md`、`PROJECT_START_HERE.md`、`EXTERNAL_ASSET_REGISTRY.md`、`.codex/PROJECT_MEMORY.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/CHANGELOG_AI.md`、`.codex/CHANGE_EVENT_LOG.md`、`.codex/TASK_HISTORY.md`、`docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md`、`docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md`、`docs/BASELINE/BASELINE_INDEX.md`、`docs/BASELINE/05_OBSERVED_BASELINE_2026-10-12.md`。
- Change Summary：保存本轮源码/历史审查摘要和证据报告，追加新观察基线；接入恢复入口、更新当前状态和历史指针，最后关闭活动任务。
- Behavior / State Delta：工作区跨会话导航更新；产品/runtime行为无变化。历史7月记录保留；当前 branch/HEAD、jniLibs存在性、broker源码范围及凭据有效性表述以10月观察纠正。
- Source of Truth / Generated Status：非生成 Markdown；源代码与本轮只读Git/API观察为证据，文档不覆盖源码或证明运行结果。
- Related Decision / ADR：沿用 D-014；`N/A — no decision delta`；无新 ADR。
- Verification：V0 引用、入口、基线、范围、状态关联与 diff whitespace 检查；关闭后复核。V1—V5 NOT_RUN，未做真实新聊天恢复测试。
- Git Write / Build / Delete / Move / Version / External Write / Release：none。
- Corrects / Supersedes：只纠正旧快照被误用为当前事实的风险，不重写历史基线、旧事件或原聊天结论；没有保存敏感值。

## CE-20261012-T002-01：AShell身份迁移及用户要求的Flutter包名保留

- Timestamp：2026-10-12T05:24:38+09:00；Task：T-2026-10-12-002。
- Actor/Skills：Codex；master、Rust、Flutter、Android、Release、Security领域规则（开始时ashell-*，现迁ashell-*）；未委派子代理。
- Authority：当前用户明确授权产品信息、包名、源码/目录/文件/构建脚本迁移；补充要求恢复flutter_hbb和关联导入；不执行构建。
- Type：一个逻辑身份迁移事务，含更新、新增、物理重命名、生成声明标识同步和记忆关闭。
- Files/areas：Cargo.toml及两份Cargo.lock；src与libs/hbb_common身份/状态引用；libs/portable；flutter平台配置/加载器/UI；build.sh/env.sh/build.py/build.cmd/new-build.cmd、CI/FDroid、res/Linux/MSI、flatpak/appimage；docs/AI_ENGINEERING、ADR-0014、baseline06、task、根入口、.codex和Skills。详细路径/行为见迁移报告；逐文件diff可相对a036f31只读查看。
- Moves：Android me/ashell目录18文件→me/ashell（四个ADB类文件更名）；九个Skills ashell-*→ashell-*；res/rustdesk.service、两份desktop、pam.d两份profile→ashell对应名；flatpak/rustdesk.json→ashell.json、com.rustdesk.RustDesk.metainfo.xml→me.ashell.metainfo.xml。只移项目文件，旧空父目录按检查移除，没有删除用户数据。
- Generated：flutter/lib/generated_bridge.dart只同步C产品符号，类型/codec不变；未运行生成器。protobuf源field39改名，编号/类型不变；lock只改workspace包名。
- Delta：产品/安装/配置/URI/原生库身份变化；显示与内部身份分离。外部JSON契约/公共服务guard/构建机环境回退保留。Flutter package及导入撤回无关改名。
- Verification：V0静态核验；9个XML/plist解析等30项断言通过，diff whitespace通过。正式build/test/analyze/codegen及运行验证均NOT_RUN。
- Decision/State：D-015 / ADR-0014；STATE-20261012-003 / WORK-20261012-004；旧基线/事件保持历史含义。
- Git write / Build / Dependency install / Version bump / Sign / External write / Release：none。

## CE-20261012-T002-02：按用户追加授权去除旧环境及ZEGO字段兼容名

- Timestamp：2026-10-12T05:29:57+09:00；Task：T-2026-10-12-002。
- Actor：Codex；Permission：当前用户明确表示ASHELL_*回退和ashellSessionId字段可以更改。
- Files：build.sh、env.sh、src/client/helper.rs、scripts/deploy_zego_token_service.sh；三份docs/ZEGO_*当前文档；迁移报告、ADR-0014、baseline06、task、PROJECT_MEMORY/STATE、Decision/Changelog/Task/Event和Current Work关闭指针。
- Change：构建脚本只使用ASHELL_*，移除旧前缀回退；客户端JSON、内嵌Go DTO/读取与示例改为ashellSessionId；现有证书/密码/工具链路径不变，未操作构建机或实际服务。
- Corrects/Supersedes：取代CE-20261012-T002-01中暂保留这两个兼容项的方案；Flutter内部flutter_hbb保留决定不变；公共服务guard和历史来源保留。
- Verification：静态全文检索无旧环境变量/JSON键；新JSON键两端与三份文档一致；diff whitespace通过。真实API联调、build/test/analyze/codegen均NOT_RUN。
- State：STATE-20261012-003 / WORK-20261012-004；最终交付关闭当前任务，后续构建机变量和服务部署须在正式环境另行安排。
- Git write / Build / Dependency install / Version bump / Sign / External write / Release：none。

## CE-20261012-T003-01：源码两处匹配与全部文档名称规范化

- Timestamp：2026-10-12T05:37:54+09:00；Task：T-2026-10-12-003；Actor：Codex / ashell-master、API静态审查。
- Authority：当前用户明确要求截图中两处及文档字段全部换名；包括既有历史文档措辞规范化和相应文件改名；保留flutter_hbb。
- Files/Delta：src/common.rs::is_public、src/hbbs_http/sync.rs::heartbeat_url的分类字符串改为ashell.；41份初始含旧词Markdown及其当前引用/记忆同步。原始文本在a036f31，提交hash/日期/验证结论不变，不将名称规范化冒充历史源码变化。
- Rename destinations：docs/ADR/0002-ashell-product-identity.md；docs/AI_ENGINEERING/ASHELL_AI_PRINCIPAL_ENGINEER_HANDOVER_REPORT.md、ASHELL_AI_ENGINEERING_STRENGTHENING_REPORT.md、ASHELL_AI_ENGINEERING_FINAL_SEAL_REPORT.md。原文件只做移动更名，不删除正文；旧名称按用户要求不在当前日志重复，源HEAD可追溯。
- Source references：真实历史源仓地址从a036f31接管报告恢复；移除含旧名的直接链接展示，没有替换成不存在的新仓链接。
- Verification：文本/路径大小写不敏感扫描零命中（排除.git），Markdown本地链接无缺失，Flutter包名/import保留，diff whitespace通过；无编译或运行证据。
- Decision/State：D-016 / ADR-0014补充；STATE-20261012-004 / WORK-20261012-006；取代T002对两处旧匹配及历史品牌措辞的保留方案。
- Git write / build/test/analyze/codegen / dependency install / credentials / external / release：none。

## CE-20261012-T004-01：替换服务器默认配置并同步恢复记录

- Timestamp：2026-10-12T06:16:00+09:00；Task ID：T-2026-10-12-004。
- Actor / Skill：Codex / ashell-master、ashell-network-engineer、ashell-api-engineer；没有子代理委派。
- Permission：当前用户明确提供目标值并要求替换本仓服务器信息；Event Type：update/create/task closure。
- Files（25）：`.info`、`libs/hbb_common/src/config.rs`、`src/common.rs`、`src/client/helper.rs`、`src/rendezvous_mediator.rs`、`flutter/lib/mobile/pages/server_page.dart`、`scripts/deploy_zego_token_service.sh`、`docs/DOCUMENT_AUDIT.md`、`docs/ENGINEERING_BASELINE.md`、`docs/ZEGO_VOICE_CALL_ARCHITECTURE.md`、`docs/ZEGO_VOICE_CALL_INTEGRATION.md`、`docs/ZEGO_TOKEN_SERVICE_DEPLOYMENT.md`、`docs/AI_ENGINEERING/06_NETWORK_PROTOCOL.md`、`docs/AI_ENGINEERING/07_API_SYSTEM.md`、`docs/tasks/T-2026-10-12-004-server-configuration.md`、`EXTERNAL_ASSET_REGISTRY.md`、`AGENTS.md`、`PROJECT_START_HERE.md`、`.codex/PROJECT_MEMORY.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/DECISION_LOG.md`、`.codex/CHANGELOG_AI.md`、`.codex/TASK_HISTORY.md`、`.codex/CHANGE_EVENT_LOG.md`。
- Delta：服务器默认IP、相关端口、公钥及脚本/文档引用替换；协议与配置覆盖语义不变。新task文件为新增，其余update；无generated变更。
- Corrects：恢复入口中a036f31未提交状态已过时，更新为本轮开始前已观察到的f423335和此前推送完成；保留旧任务历史。
- Verification：12文件对照HEAD仅指定字面值变更；旧值零残留、公钥Base64为32字节、端口派生和diff检查通过，仅V0。实际服务/构建NOT_RUN。
- Related：D-017；T004任务与Network/API工程文档；STATE-20261012-005 / WORK-20261012-008。
- Git / Build / Delete / Move / Version / External / Release：本任务全部none；未读取/修改外部秘密值、未试用Key。

## CE-20261012-T005-01：统一PC入口和自解压范围

- Timestamp：2026-10-12T06:37:59+09:00；Task ID：T-2026-10-12-005。
- Actor / Skill：Codex / ashell-release-engineer，ashell-security-engineer静态复核；无子代理。
- Authority：用户明确要求删除旧入口、迁名原新入口并只打包编译产物；Edit + scoped delete/move，不包含实际构建/打包或Git。
- Files（26）：`.agents/skills/ashell-release-engineer/SKILL.md`、`.codex/AI_RULES.md`、`.codex/CHANGE_EVENT_LOG.md`、`.codex/CHANGELOG_AI.md`、`.codex/CURRENT_WORK.md`、`.codex/DECISION_LOG.md`、`.codex/PROJECT_MEMORY.md`、`.codex/PROJECT_STATE.md`、`.codex/TASK_HISTORY.md`、`AGENTS.md`、`build.cmd`、`CLAUDE.md`、`docs/AI_ENGINEERING/02_SOURCE_MAP.md`、`docs/AI_ENGINEERING/05_WINDOWS_PIPELINE.md`、`docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`、`docs/AI_ENGINEERING/09_DEBUG_SYSTEM.md`、`docs/AI_ENGINEERING/10_SECURITY_MODEL.md`、`docs/BASELINE/07_PC_BUILD_BASELINE_2026-10-12.md`、`docs/BASELINE/BASELINE_INDEX.md`、`docs/tasks/T-2026-10-12-005-pc-build-entry.md`、`EXTERNAL_ASSET_REGISTRY.md`、`libs/portable/generate.py`、`new-build.cmd`、`PC-Build.md`、`PROJECT_START_HERE.md`、`TEST_MATRIX.md`。
- Delta：移除外部DLL/驱动装配与暂存搬运；清理固定Release后主编译并直接压缩，Brotli提前检查不自动安装；Cargo失败传递、输入路径边界、manifest恢复、临时输出后成功替换最终EXE。
- Delete/move：旧build.cmd内容被替换；new-build.cmd迁至build.cmd后原路径删除。只有这两个入口参与迁移，未清理实际生成资产；旧实现可从c0951b5恢复。新建task/baseline，其他文档update；无generated文件编辑。
- Verification：仅V0 Python AST、内嵌PowerShell解析、CMD标签/打包调用关系、路径/额外资产检索、文档引用及diff检查。修改后正式build/runtime NOT_RUN；旧用户日志不可冒充本轮成功证据。
- Compatibility：运行/协议/版本不变，compiled native DLL保留；外部组件功能完整性不保证。历史baseline/task不改写。
- Related：D-018；AS-BL-2026-10-12-c0951b5-pc-build-worktree；STATE-20261012-006 / WORK-20261012-010。
- Git / actual build/test/package / install / external / version/sign/release：全部none。

## CE-20261012-T006-01：用户原图统一产品图标

- Timestamp：2026-10-12T07:07:58+09:00；Task：T-2026-10-12-006；Actor：Codex；ashell-master / ashell-release-engineer / ashell-flutter-engineer；无子代理。
- Authority：当前用户明确提供PNG并要求按既定方案转换、替换指定平台图标，保持原设计，禁止编译。图像转换和必要加载/ignore引用调整获得本轮授权，不继承旧任务Git或构建权限。
- Source：test/c9b7cd135be838989953e7ab7037f8c6d8d6aa31，initial clean；纠正当前恢复入口中T005尚未提交的过时快照，历史记录不改写。
- Files（32）：res/icon.png、res/icon.ico、res/tray-icon.ico；flutter/windows/runner/resources/app_icon.ico；flutter/assets/icon.png与icon-fallback.png（新增）；flutter/android/app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/{ic_launcher.png,ic_launcher_round.png,ic_launcher_foreground.png}（15个）；flutter/lib/common.dart、.gitignore；docs/tasks/T-2026-10-12-006-product-icons.md（新增）、docs/AI_ENGINEERING/08_BUILD_SYSTEM.md、PROJECT_START_HERE.md；.codex/PROJECT_MEMORY.md、PROJECT_STATE.md、CURRENT_WORK.md、CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md。精确展开清单见task。
- Delta：21个图像通过本机System.Drawing确定性转换；ICO9尺寸，PNG18个；loadIcon新增同源PNG回退并移除旧SVG引用，不删除SVG；精确ignore例外避免新资源遗漏。Android自适应前景保留66/108内容比例，白色背景和monochrome引用不变。
- Provenance：用户源图实际1254×1254 RGBA；source SHA-256与ICO SHA-256记录在task，不保存用户绝对路径。无重绘、清边、裁切或依赖安装。
- Verification：V0独立Pillow解码18个PNG/27个ICO子帧、尺寸/透明度/留白、源图未变、资源路径/ignore规则、静态图片预览及diff检查。System.Drawing多帧256选择返回128但独立256 ICO帧可正常解码，不能冒充Shell运行验证。
- Related：ADR-0014沿用；N/A — no decision delta；STATE-20261012-007 / WORK-20261012-012。
- Git / project build/test/analyze/codegen / actual package / install / delete/move / version/sign / external write / release：none。修改尚未提交；正式EXE/APK显示NOT_RUN。

## CE-20261012-T007-01：补齐产品图标消费链

- Timestamp：2026-10-12T07:20:08+09:00；Task：T-2026-10-12-007；Actor：Codex / ashell-master，Release/Rust/Android/Security静态复核；无子代理。
- Authority：当前用户追加要求检查Windows进程/任务栏/状态栏/服务及Android图标遗漏，继续生成并替换；保持原图、禁止编译。必要资源引用和文档修改在范围内。
- Source：test/c9b7cd135be838989953e7ab7037f8c6d8d6aa31，initial dirty为T006的32文件；保留其21图像及Flutter加载代码hash。
- Files（28）：res/128x128.png；src/ui.rs、src/platform/windows.rs、libs/portable/src/ui.rs；flutter/android/app/src/main/res/mipmap-{mdpi,hdpi,xhdpi,xxhdpi,xxxhdpi}/ic_stat_logo.png；同五目录新ic_launcher_monochrome.png；mipmap-anydpi-v26/ic_launcher.xml与ic_launcher_round.xml；drawable/floating_window.xml；新增drawable-nodpi/ashell_floating_icon.png；.gitignore；新增docs/tasks/T-2026-10-12-007-icon-coverage.md；docs/AI_ENGINEERING/08_BUILD_SYSTEM.md、PROJECT_START_HERE.md；.codex/PROJECT_MEMORY.md、PROJECT_STATE.md、CURRENT_WORK.md、CHANGELOG_AI.md、CHANGE_EVENT_LOG.md、TASK_HISTORY.md。展开清单见task。
- Delta：12图片（6新增/6替换）；通知与主题使用原机器人单色alpha而非白底卡片，悬浮球保留完整原图；legacy Windows get_icon读取新PNG；portable由BasicApp持有HICON并绑定small/big；卸载快捷方式指向主EXE图标。无依赖/权限/版本/协议变更。
- Verification：12PNG/16XML静态检查、源图与既有资源hash、图片预览、消费路径/ignore/include、diff与文档链接；无正式编译运行证据。本地NWG源码/lock不可用，API对照官方上游master并记录限制。
- Related：N/A — no decision delta / ADR-0014；STATE-20261012-008 / WORK-20261012-014；合并T006后51文件未提交。
- Corrects / Supersedes：补齐T006未覆盖的notification/floating及legacy/portable窗口；T006时monochrome复用前景已由本轮独立mask取代。旧任务历史不改写。
- Git / build/test/analyze/project-codegen / install/run / actual package / delete/move / version/sign / external write / release：none。图片转换是获准资源生成，不是产品构建或代码生成。

## CE-20261012-T008-01：Android构建日志与返回路径隔离

- Timestamp：2026-10-12T07:41:06+09:00；Task：T-2026-10-12-008；Actor：Codex / ashell-release-engineer；Android静态复核，无子代理。
- Scope：用户报告Android构建失败并确认原目录增量编译；完成诊断并修正直接相关的脚本错误。前轮提交/推送/合并请求已完成，本轮不继承Git或构建权限。
- Source：main/fb5c393fe1e29a2cf805e75847820b315c468b3d，initial clean。纠正提交前STATE-008为当前状态的误读；T006/T007历史不改写。
- Files（11）：build.sh；新增docs/tasks/T-2026-10-12-008-android-resource-build.md；docs/AI_ENGINEERING/08_BUILD_SYSTEM.md、09_DEBUG_SYSTEM.md；PROJECT_START_HERE.md；.codex/PROJECT_MEMORY.md、PROJECT_STATE.md、CURRENT_WORK.md、CHANGELOG_AI.md、CHANGE_EVENT_LOG.md、TASK_HISTORY.md。
- Delta：split/universal标题与完整构建日志输出stderr，stdout仅APK路径；失败显式return原退出码；模式调用者显式传播失败，Flutter失败不再继续选历史APK、签名或复制。图标/依赖/版本/签名配置不改。
- Diagnosis：AGP7.3.1+旧目录增量MergeResources/source-sets问题与官方206674992相符，仍为inferred。已确认脚本stdout污染和失败传播缺口独立于该根因；未宣称clean或代码修正已解决AGP异常。
- Verification：bash --noprofile --norc -n仅语法解析、三份图标XML解析、stdout/失败路径人工静态检查、diff/范围/链接。无项目代码执行、无构建/测试结果。
- Related：N/A — no decision delta / ADR-0014；STATE-20261012-009 / WORK-20261012-016。
- Git/build/test/analyze/project-codegen/clean/delete/move/install/sign/deploy/release：none；未提交。正式构建机验证需求记录在task。

### 后续事件模板

```text
## CE-YYYYMMDD-TNNN-NN：标题
- Timestamp：
- Task ID：
- Actor / Primary Skill：
- Permission / Confirmation Reference：
- Event Type：create / update / delete / move / generated
- Files：repository-relative paths only
- Change Summary：
- Behavior / State Delta：
- Source of Truth / Generated Status：
- Related Decision / ADR：
- Verification：
- Git / Build / Delete / External / Release：
- Corrects / Supersedes：
```

## Logging Rules

- 每次有授权的 logical persisted-change batch 追加一个唯一 Event ID；每个 Event 必须关联 Task ID。
- 多会话使用 `CE-YYYYMMDD-TNNN-NN` 的 task-local sequence，写前重读，禁止复用 ID。
- 只读调查、计划、工具输出和未落盘建议不写 event。
- 不复制完整 diff、源码正文、prompt/transcript、secret/token/password/key、生产地址、IP、peer/device/UUID、PII 或绝对用户路径。
- 删除、移动、generated、Git、build、external、release 若实际发生必须逐项记录；未发生明确写 `none`。
- 错误记录通过新 Event `corrects/supersedes`，旧事件保留。

## CE-20261012-T009-01：旧 ADB 功能链退役

- Task：T-2026-10-12-009；日期：2026-10-12；Actor：Codex / ashell-master。
- Source：main / 13f792d552db49fd1b702ff24cb2b3d0c5030c38；开始 clean，本轮 Git 写入 none。
- Authority：当前用户明确要求剔除旧 ADB；C2 相关代码/配置，C3 删除 5 个旧组件源文件；C1 当前事实与记忆同步。
- Change：删除 AdbPage、AndroidAdbManager、11 个 MethodChannel handler、四个 Kotlin 模块及 36 个 Accessibility 自动化方法；移除两项专用权限/keep 规则、build.sh 的 host adb 检查和 env.sh 的独立 adb 预装/检查；排除旧 libadb.so。SDK platform-tools 保留；协议只改陈旧注释，字段不变。
- Preserve：Windows/Rust core/JNI/MainService/DevAutoSelectorController/依赖锁无修改；其余 native packaging、Wi-Fi 保活、采集/输入/重连/terminal/ZEGO 原实现保持。
- Decision / Baseline / State：D-019 / ADR-0015；AS-BL-2026-10-12-13f792d-adb-retired-worktree；STATE-20261012-010 / WORK-20261012-018。
- Verification：V0 残留/引用、删除边界、XML 结构、Bash 仅语法解析、diff/链接检查；V2/V3 NOT_RUN。
- Limit：删除空 adb/ 目录的命令被自动审批策略拦截，仅返回 blocked by policy；未绕过，只读复核目录内 0 个文件。历史设备 prefs/配对/系统设置未清理；外部构建机资产未访问。
- Build / test / analyze / codegen / install / device / sign / release：none。
- Files（含本事件日志）：

- `.agents/skills/ashell-android-engineer/SKILL.md`
- `.agents/skills/ashell-flutter-engineer/SKILL.md`
- `.agents/skills/ashell-release-engineer/SKILL.md`
- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/PROJECT_STATE.md`
- `.codex/TASK_HISTORY.md`
- `AGENTS.md`
- `CLAUDE.md`
- `EXTERNAL_ASSET_REGISTRY.md`
- `PROJECT_START_HERE.md`
- `TEST_MATRIX.md`
- `build.sh`
- `docs/ADB_LADB_INTEGRATION_MEMORY.md`
- `docs/ADR/0007-keep-android-adb-local.md`
- `docs/ADR/0015-retire-legacy-adb.md`
- `docs/ADR/README.md`
- `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md`
- `docs/AI_ENGINEERING/01_ARCHITECTURE.md`
- `docs/AI_ENGINEERING/02_SOURCE_MAP.md`
- `docs/AI_ENGINEERING/03_MODULE_DESIGN.md`
- `docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md`
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- `docs/AI_ENGINEERING/09_DEBUG_SYSTEM.md`
- `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- `docs/AI_ENGINEERING/11_ROADMAP.md`
- `docs/BASELINE/08_ADB_RETIREMENT_BASELINE_2026-10-12.md`
- `docs/BASELINE/BASELINE_INDEX.md`
- `docs/tasks/T-2026-10-12-009-remove-legacy-adb.md`
- `env.sh`
- `flutter/android/app/build.gradle`
- `flutter/android/app/proguard-rules`
- `flutter/android/app/src/main/AndroidManifest.xml`
- `flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbDnsDiscover.kt`
- `flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbManager.kt`
- `flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbRunner.kt`
- `flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbState.kt`
- `flutter/android/app/src/main/kotlin/me/ashell/nZW99cdXQ0COhB2o.kt`
- `flutter/android/app/src/main/kotlin/me/ashell/oFtTiPzsqzBHGigp.kt`
- `flutter/lib/common.dart`
- `flutter/lib/consts.dart`
- `flutter/lib/mobile/pages/adb_page.dart`
- `flutter/lib/mobile/pages/home_page.dart`
- `libs/hbb_common/protos/message.proto`

## CE-20261013-T001-01：独立客户端 Actions 与旧编排清理

- Date：2026-10-13（Asia/Tokyo）；Task：T-2026-10-13-001；Actor：Codex / ashell-release-engineer。
- Authority：当前用户明确要求新增独立Android/Windows编排、删除无用GitHub编排、平台/ABI选择、默认测试签名与Actions-only下载；不继承历史Git/build权限。
- Source：main/8662f2369a33f5286c20f9de4d640d2072175ca7；initial clean；当前HEAD新的local ADB保持。
- Added：.github/workflows/build-clients.yml、docs/BUILD_CLIENTS_ACTIONS.md、docs/ADR/0016-independent-client-actions.md、docs/BASELINE/09_CLIENT_ACTIONS_BASELINE_2026-10-13.md、docs/tasks/T-2026-10-13-001-client-actions.md。
- Deleted：.github/workflows下bridge.yml、ci.yml、clear-cache.yml、fdroid.yml、flutter-build.yml、flutter-ci.yml、flutter-nightly.yml、flutter-tag.yml、playground.yml、third-party-RustDeskTempTopMostWindow.yml、update_submodules.yml、winget.yml；.github/dependabot.yml。
- Compatibility edits：build.sh、flutter/build_fdroid.sh，仅解除读取已删workflow元数据，版本沿用；build.cmd和产品/生成源码/manifest/锁文件保持。
- Documentation：AGENTS.md、PROJECT_START_HERE.md、README.md、TEST_MATRIX.md、EXTERNAL_ASSET_REGISTRY.md、docs/AI_ENGINEERING/02_SOURCE_MAP.md、08_BUILD_SYSTEM.md、09_DEBUG_SYSTEM.md、10_SECURITY_MODEL.md、docs/ADR/README.md与0010 amendment、docs/BASELINE/BASELINE_INDEX.md、.agents/skills/ashell-release-engineer/SKILL.md。
- Memory：.codex/PROJECT_STATE.md、PROJECT_MEMORY.md、CURRENT_WORK.md、TASK_HISTORY.md、CHANGELOG_AI.md、DECISION_LOG.md及本事件。
- Delta：唯一手动工作流；platform both/android/windows，Android aarch64/universal；独立native matrix/Windows job；临时测试APK与unsigned Windows EXE/ZIP仅上传Actions，最终14天，中间1天；无自动Git/Release/production secrets。
- Verification：V0 YAML/矩阵/9种Action SHA、11段Bash/1段Python AST/6段PowerShell与两份本地shell语法、引用/路径/权限/diff；runner V2 NOT_RUN。
- Decision/Baseline/State：D-020 / ADR-0016；AS-BL-2026-10-13-8662f23-client-actions-worktree；STATE-20261013-001 / WORK-20261013-002。
- Actual actions：删除13份旧编排；无Git写入、产品build/test/analyze/codegen、实际签名/打包、远端dispatch/上传/发布；全部变更未提交。
- Recovery：按8662f23对应路径和本任务patch审阅恢复，不整仓reset或重写历史。运行签名/依赖/安装限制见任务与使用说明。

## CE-20261013-T002-01：Android SDK 初始化修复

- Date / Actor：2026-10-13 / Codex / Release；Task T-2026-10-13-002；source main/c521601，initial clean。
- Authority：原客户端 Actions 实现、当前失败反馈及同一会话“提交github”要求；T001 后续已提交推送 c521601，补充旧事件的提交前状态。
- Workflow：新增 ANDROID_CMDLINE_TOOLS_VERSION=12266719；两 Android job 使用 setup-android v3.2.2 固定 SHA，显式 platform-tools；去除重复 licenses 命令，打印路径/版本，安装固定 SDK root；Android build-info 记录 CLI。
- Files：.github/workflows/build-clients.yml；docs/BUILD_CLIENTS_ACTIONS.md；docs/AI_ENGINEERING/08_BUILD_SYSTEM.md、09_DEBUG_SYSTEM.md；EXTERNAL_ASSET_REGISTRY.md；TEST_MATRIX.md；docs/BASELINE/BASELINE_INDEX.md、10_ANDROID_SDK_SETUP_BASELINE_2026-10-13.md；docs/tasks/T-2026-10-13-002-android-sdk-setup.md；.codex/PROJECT_STATE.md、PROJECT_MEMORY.md、CURRENT_WORK.md、CHANGELOG_AI.md、TASK_HISTORY.md 及本 ledger。
- Review：用户日志证明初版初始化失败；修复后 V0 YAML/Bash/Python、初始化顺序/权限/Action SHA/Windows不变/diff/引用。无产品构建/远端 dispatch；仅按既有请求交付 commit/push，结果以 Git 回执为准。
- Decision：D-020 / ADR-0016 reviewed-no-change；baseline AS-BL-2026-10-13-c521601-android-sdk-setup-worktree；STATE-20261013-002 / WORK-20261013-004。
- Recovery / Next：按 c521601 对本次 patch 逐文件恢复；新运行选择修复 SHA，等待实际 SDK/NDK 安装、编译与 artifacts 证据。

## CE-20261013-T003-01：独立本机 ADB 文档回填与整理

- Date / Actor：2026-10-13 / Codex / ashell-master；Task T-2026-10-13-003。
- Authority：当前用户明确同步ADB文档（C1）；原实现8662f23已提交。本轮无C2产品改动、Git写入、编译/设备/发布动作。
- Source：main/f84a78f87d1009c24acfdff478cf95bb463547e5，initial clean；后续CI修复保留，localadb模块相对8662f23未变。
- Delta：当前说明取代入口中“新组件未接入”的陈旧断言，新增ADR-0017/Baseline11/四项依赖登记/AND-09—13，旧集成档案仅加跳转。正文保持当前、历史、未实现和未验证四者分明。
- Verification：V0源码/接口/常量/钩子对照、实现commit范围、上游license只读来源、diff/新增相对链接/新增文档空白/全Markdown范围；V2/V3 NOT_RUN。
- Decision / State / Registry：D-021 / ADR-0017；STATE-20261013-003 / WORK-20261013-006；AS-BL-2026-10-13-f84a78f-local-adb。
- Recovery：仅按本事件路径和文档diff审阅恢复；不回滚8662f23产品代码或后续CI提交，不删除历史资产。
- Final actions：34个Markdown文件（4新增、30修改），无文件删除；本轮未stage/commit/push、build/test/analyze/codegen、依赖安装、设备、版本、签名、上传或发布。

新增路径：

- `docs/AI_ENGINEERING/12_LOCAL_ADB.md`
- `docs/ADR/0017-isolated-local-adb.md`
- `docs/BASELINE/11_LOCAL_ADB_BASELINE_2026-10-13.md`
- `docs/tasks/T-2026-10-13-003-local-adb-docs.md`

修改路径：

- `AGENTS.md`
- `CLAUDE.md`
- `PROJECT_START_HERE.md`
- `README.md`
- `EXTERNAL_ASSET_REGISTRY.md`
- `TEST_MATRIX.md`
- `docs/ADB_LADB_INTEGRATION_MEMORY.md`
- `docs/ADR/README.md`
- `docs/ADR/0015-retire-legacy-adb.md`
- `docs/BASELINE/BASELINE_INDEX.md`
- `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md`
- `docs/AI_ENGINEERING/01_ARCHITECTURE.md`
- `docs/AI_ENGINEERING/02_SOURCE_MAP.md`
- `docs/AI_ENGINEERING/03_MODULE_DESIGN.md`
- `docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md`
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- `docs/AI_ENGINEERING/09_DEBUG_SYSTEM.md`
- `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- `docs/AI_ENGINEERING/11_ROADMAP.md`
- `.agents/skills/ashell-android-engineer/SKILL.md`
- `.agents/skills/ashell-flutter-engineer/SKILL.md`
- `.agents/skills/ashell-release-engineer/SKILL.md`
- `.codex/ARCHITECTURE_MEMORY.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/PROJECT_STATE.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/TASK_HISTORY.md`
- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`

## CE-20261013-T004-01 — PC 独立远程 ADB

- Date / Owner：2026-10-13 / Codex ashell-master，领域约束与Security/Release静态自审。
- Task / Authority：T-2026-10-13-004；当前用户六项请求授权模块化PC ADB与多PC共享Android状态，C2实现/C1文档；无本轮C3。
- Source：main/917e6612cdfd819b824626564478a3ff1d84eec4，initial clean；下列新增功能在未提交工作树，不属于917e661提交。
- Delta：PC菜单与dialog-owned controller、Misc.remote_adb字段40、原FFI ABI新key、专用JNI、Android singleton/有限队列/配对记录/NSD/撤销。原session dispatcher只增加ADB分支与owner取消钩子。
- Boundary：原产品身份、无障碍/采集JNI/视频/输入/terminal实现、构建脚本/CI/锁文件无改动；无ADB投屏；原会话加密/认证策略保持。
- Verification：V0跨层接口/字段/JNI/权限/有界所有权/取消时序审查，保护路径diff、新增链接/空白和event清单核对；ADB-R01—07的V2/V3/V4 NOT_RUN。
- Decision / State / Registry：D-022 / ADR-0018；STATE-20261013-004 / WORK-20261013-008；AS-BL-2026-10-13-917e661-remote-adb-worktree。
- Recovery：未来经授权按本事件路径审阅回退独立模块/钩子并恢复旧local plugin生命周期，保留其他CI/产品改动；shell系统副作用或已撤销身份不能靠代码回退恢复。
- Final actions：51个路径，零文件删除；未stage/commit/push、build/test/analyze/codegen、依赖更新、设备、版本/签名、上传或发布。
- Detailed record：docs/tasks/T-2026-10-13-004-remote-adb.md。

修改/新增路径（包含本事件文件）：

- `.agents/skills/ashell-android-engineer/SKILL.md`
- `.agents/skills/ashell-flutter-engineer/SKILL.md`
- `.codex/ARCHITECTURE_MEMORY.md`
- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/PROJECT_STATE.md`
- `.codex/TASK_HISTORY.md`
- `AGENTS.md`
- `CLAUDE.md`
- `EXTERNAL_ASSET_REGISTRY.md`
- `PROJECT_START_HERE.md`
- `README.md`
- `TEST_MATRIX.md`
- `docs/ADR/0017-isolated-local-adb.md`
- `docs/ADR/0018-device-owned-remote-adb.md`
- `docs/ADR/README.md`
- `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md`
- `docs/AI_ENGINEERING/01_ARCHITECTURE.md`
- `docs/AI_ENGINEERING/02_SOURCE_MAP.md`
- `docs/AI_ENGINEERING/03_MODULE_DESIGN.md`
- `docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md`
- `docs/AI_ENGINEERING/06_NETWORK_PROTOCOL.md`
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- `docs/AI_ENGINEERING/09_DEBUG_SYSTEM.md`
- `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- `docs/AI_ENGINEERING/11_ROADMAP.md`
- `docs/AI_ENGINEERING/12_LOCAL_ADB.md`
- `docs/AI_ENGINEERING/13_REMOTE_ADB.md`
- `docs/BASELINE/12_REMOTE_ADB_BASELINE_2026-10-13.md`
- `docs/BASELINE/BASELINE_INDEX.md`
- `docs/tasks/T-2026-10-13-004-remote-adb.md`
- `flutter/android/app/local-adb.pro`
- `flutter/android/app/src/main/java/me/ashell/localadb/AdbIdentity.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/AdbPortDiscovery.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbManager.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbPlugin.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/RemoteAdbNative.java`
- `flutter/lib/desktop/adb/remote_adb_controller.dart`
- `flutter/lib/desktop/adb/remote_adb_menu.dart`
- `flutter/lib/desktop/widgets/remote_toolbar.dart`
- `flutter/lib/mobile/adb/local_adb_bridge.dart`
- `flutter/lib/mobile/adb/local_adb_page.dart`
- `libs/hbb_common/protos/message.proto`
- `src/client/io_loop.rs`
- `src/flutter_ffi.rs`
- `src/lib.rs`
- `src/remote_adb.rs`
- `src/server/connection.rs`

## CE-20261013-T005-01 — 修复ADB controller缺少bind导入

- Date / Owner：2026-10-13 / Codex；Release定位日志，Flutter符号可见性修复。
- Task / Authority：T-2026-10-13-005，当前用户反馈GitHub Android失败；C2修复/C1记录，无C3。
- Source：main/de8bc768f0a71cab3f60e93f321ae0f65d74c658，initial clean；前序T004已提交，本轮未提交。
- Evidence：用户job日志显示remote_adb_controller.dart原44/57/64行getter bind未定义，compileFlutterBuildRelease失败；不保存临时签名URL或完整日志。
- Delta：一行import platform_model.dart show bind；不改原功能、协议、JNI/Java、依赖/CI或生成文件。
- Verification：V0声明/import/export、ADB调用、SessionID/FRB合同、diff空白/范围/链接；修复前CI FAIL，修复后V2 NOT_RUN，功能V3/V4待验。
- Decision / State / Registry：D-022 / ADR-0018 reviewed-no-change；STATE-20261013-005 / WORK-20261013-010；Baseline12引用与当前de8bc76关系见任务。
- Recovery：审阅本事件一行导入与文档diff回退，不整仓reset；去掉导入会恢复已知编译错误，无数据迁移。
- Actions：10路径（一个源码，其余记录），零删除；未stage/commit/push/build/test/analyze/codegen/device/dispatch/sign/release。

修改/新增路径：

- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/PROJECT_STATE.md`
- `.codex/TASK_HISTORY.md`
- `docs/AI_ENGINEERING/13_REMOTE_ADB.md`
- `docs/tasks/T-2026-10-13-005-adb-bind-import.md`
- `flutter/lib/desktop/adb/remote_adb_controller.dart`

## CE-20261013-T006-01 — ADB discovery and remote usability

- Date/Owner：2026-10-13 / Codex，T-2026-10-13-006；用户C1/C2与C3 stage/commit/push/Build AShell clients单次dispatch明确授权，不监控。
- Source：main/504adea0119d9ff96d33a16cd27e559d1492a723 initial clean；旧T005已提交，本事件为当前任务。
- Delta：独立ADB gate、NSD双端口、pairConnect/QR、分窗拖动与手机UI；原peer/FRB/identity合同兼容扩展，state apiVersion=2。文档同步ADR0019/D023/Baseline13/TEST_MATRIX/外部研究来源与memory。
- V0：静态caller/type/API/权限/NSD取消/窗口生命周期、diff whitespace、新文档相对链接、保护路径范围；本机无正式工具链。V1/V2/V3/V4未在本地执行；用户只授权CI触发，不监控。
- Actions：无删除、版本、依赖/工具链/工作流修改、原Accessibility/capture/input处理修改。此记录为提交前快照；随后Git/dispatch精确回执以Git与最终交付为准。
- Recovery：按下面明确路径审阅回退，无record/identity schema迁移；不能回滚已执行shell系统副作用。STATE006 / WORK012。

修改/新增路径：

- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/PROJECT_STATE.md`
- `.codex/TASK_HISTORY.md`
- `AGENTS.md`
- `EXTERNAL_ASSET_REGISTRY.md`
- `PROJECT_START_HERE.md`
- `TEST_MATRIX.md`
- `docs/ADR/0018-device-owned-remote-adb.md`
- `docs/ADR/0019-adb-discovery-and-independent-control.md`
- `docs/ADR/README.md`
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- `docs/AI_ENGINEERING/12_LOCAL_ADB.md`
- `docs/AI_ENGINEERING/13_REMOTE_ADB.md`
- `docs/BASELINE/13_ADB_USABILITY_BASELINE_2026-10-13.md`
- `docs/BASELINE/BASELINE_INDEX.md`
- `docs/tasks/T-2026-10-13-006-adb-usability.md`
- `flutter/android/app/src/main/java/me/ashell/localadb/AdbPortDiscovery.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbManager.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbPlugin.java`
- `flutter/lib/desktop/adb/remote_adb_controller.dart`
- `flutter/lib/desktop/adb/remote_adb_menu.dart`
- `flutter/lib/desktop/widgets/remote_toolbar.dart`
- `flutter/lib/mobile/adb/local_adb_bridge.dart`
- `flutter/lib/mobile/adb/local_adb_page.dart`
- `src/remote_adb.rs`
- `src/server/connection.rs`

## CE-20261013-T007-01 — ADB code-pairing and UI documentation sync

- Date/Owner：2026-10-13 / Codex，T-2026-10-13-007；用户先允许同步文档，再明确追加提交代码/文档、推送与单次Build AShell clients both/aarch64，不监控。
- Source：main/efb2599d05ba5b3a08a9c63ee00bec20e2ce9c5b，initial三份Dart dirty；源码SHA-256见Baseline14，本轮仅Markdown同步、不追加源码修改。
- Delta：当前文档去除QR正向能力/PC三秒同步承诺；说明两端模式显示、PC按钮主题色、刷新/参数拒绝/请求取消/时限。新ADR0020/D024/Baseline14/测试R12—15与恢复记忆；旧QR R10=N/A，旧Baseline13保持。
- Evidence：V0源码与文档对照、diff空白/本地链接/hash保护。efb2599历史push及HTTP204 dispatch已确认，未监控、无V2/V3/V4 PASS；本轮交付回执待执行后由最终答复确认。
- Boundaries：无本地build/test/analyze/codegen、设备、原链/工具链/依赖修改、删除/版本/发布/生产动作。文档回退只限下列路径，不能抹去后续或其他任务记录。
- Related：STATE007 / WORK014 / D024 / ADR0020 / T007；准确交付SHA以Git回执为准，不冒充提交后的实时状态。

本轮文档路径：

- `AGENTS.md`
- `PROJECT_START_HERE.md`
- `EXTERNAL_ASSET_REGISTRY.md`
- `TEST_MATRIX.md`
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- `docs/AI_ENGINEERING/12_LOCAL_ADB.md`
- `docs/AI_ENGINEERING/13_REMOTE_ADB.md`
- `docs/ADR/0019-adb-discovery-and-independent-control.md`
- `docs/ADR/0020-adb-code-pairing-and-explicit-refresh.md`
- `docs/ADR/README.md`
- `docs/BASELINE/14_ADB_CODE_PAIRING_BASELINE_2026-10-13.md`
- `docs/BASELINE/BASELINE_INDEX.md`
- `docs/tasks/T-2026-10-13-007-adb-docs-sync.md`
- `.codex/PROJECT_STATE.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/TASK_HISTORY.md`
- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`

交付一并包含先前用户授权的三份源码改动（本轮未重写）：

- `flutter/lib/desktop/adb/remote_adb_controller.dart`
- `flutter/lib/desktop/adb/remote_adb_menu.dart`
- `flutter/lib/mobile/adb/local_adb_page.dart`

## CE-20261013-T008-01 — Shell栏与ADB后续交接文档同步

- Task/Owner：T-2026-10-13-008 / Codex；2026-10-13启动、2026-10-14完成（Asia/Tokyo）。用户本轮授权同步文档，下一阶段投屏待设计。
- Source：main/aca74467a3f83392c6a440e6507a63508f9294f6，initial clean；980f254固化前轮记录，aca7446提交八份Dart侧栏代码。当前只改Markdown，源码hash见Baseline15。
- Delta：Shell栏位置/开关/三键、会话状态/查询/生命周期、原链边界、正式验收R16—19；修正当前未提交/无ADB输入旧表述，历史基线与任务不重写。
- Records：D025 / ADR0020 UI补录 / Baseline15 / STATE008 / WORK016；后续投屏采集/传输/解码与多PC所有权仅记待设计，无新选型或资产。
- Verification：V0源码对照、Markdown链接/差异、范围及八份Dart SHA-256；正式V2/V3/V4 NOT_RUN。无Git写入、CI触发/监控、产品执行、设备、删除/版本/发布或生产操作。
- Rollback：只限本批Markdown差异；不覆盖其他任务，不改变运行中的ADB身份/状态，不把文档同步当投屏实现。

本轮文档路径（26份）：

- `.codex/CHANGELOG_AI.md`
- `.codex/CHANGE_EVENT_LOG.md`
- `.codex/CURRENT_WORK.md`
- `.codex/DECISION_LOG.md`
- `.codex/PROJECT_MEMORY.md`
- `.codex/PROJECT_STATE.md`
- `.codex/TASK_HISTORY.md`
- `AGENTS.md`
- `CLAUDE.md`
- `EXTERNAL_ASSET_REGISTRY.md`
- `PROJECT_START_HERE.md`
- `TEST_MATRIX.md`
- `docs/ADR/0020-adb-code-pairing-and-explicit-refresh.md`
- `docs/ADR/README.md`
- `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md`
- `docs/AI_ENGINEERING/01_ARCHITECTURE.md`
- `docs/AI_ENGINEERING/02_SOURCE_MAP.md`
- `docs/AI_ENGINEERING/03_MODULE_DESIGN.md`
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`
- `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`
- `docs/AI_ENGINEERING/11_ROADMAP.md`
- `docs/AI_ENGINEERING/12_LOCAL_ADB.md`
- `docs/AI_ENGINEERING/13_REMOTE_ADB.md`
- `docs/BASELINE/15_ADB_SIDEBAR_BASELINE_2026-10-13.md`
- `docs/BASELINE/BASELINE_INDEX.md`
- `docs/tasks/T-2026-10-13-008-adb-sidebar-docs-sync.md`

## CE-20261014-T001-01 — 独立 scrcpy 视频和 ADB 优先触摸

- Task/Owner：T-2026-10-14-001 / Codex，2026-10-14，ashell-master跨域静态自审；无子agent。
- Authority：用户原请求scrcpy集成、多PC投屏/全局关闭，追加ADB触摸优先于Accessibility。C2代码/协议/资产/文档；未授权Git写入或产品构建/测试/CI。
- Source：main/a1486ee2ad2bca991b272a8c7780d2daf2f7c526，initial clean；未提交工作树。
- Delta：Android LocalAdbMirror/LocalAdbTouch及几何适配、独立JNI、Misc41、Rust广播/背压/独立PC解码、Flutter独立画面与Shell栏两按钮。最小原输入分流/能力gate、旧Accessibility手势释放；原视频和截图采集、身份/版本/工具链保持。
- Asset：EXT-BIN-SCRCPY-001，官方v5.0.1，733930字节，SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`；官方元数据比对和ZIP结构检查，未签名验证/重建。
- Records：D026 / ADR0021 / Baseline16 / STATE-20261014-001 / WORK-20261014-001；ADB-M01—M08全部NOT_RUN。
- Verification：V0源码/JNI/R8/上下游合同、字段40保持/41无重复、空白/范围/本地文档链接；正式编译、设备和双PC均未执行。
- Rollback：仅撤回下列本事件增量/新增文件，保留历史基线和原ADB身份；不停止原共享，不运行killall或关闭adbd。运行时私有连接停止只释放自身资源；中断上传可残留两个固定临时文件，下次覆盖。

代码与资产路径：

- `flutter/android/app/local-adb.pro`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbClient.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbManager.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbMirror.java`
- `flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbTouch.java`
- `flutter/android/app/src/main/kotlin/me/ashell/localadb/AdbTouchCoordinates.kt`
- `flutter/android/app/src/main/kotlin/me/ashell/nZW99cdXQ0COhB2o.kt`
- `flutter/android/app/src/main/assets/adb-mirror/`（server / LICENSE.scrcpy / NOTICE.md）
- `libs/hbb_common/protos/message.proto`
- `src/remote_adb_video.rs`
- `src/lib.rs`
- `src/client/io_loop.rs`
- `src/flutter_ffi.rs`
- `src/server/connection.rs`
- `flutter/lib/models/input_model.dart`
- `flutter/lib/desktop/pages/remote_page.dart`
- `flutter/lib/desktop/adb/remote_adb_sidebar.dart`
- `flutter/lib/desktop/adb/remote_adb_mirror.dart`
- `flutter/lib/desktop/adb/remote_adb_mirror_view.dart`

文档与记忆路径：`.codex/{CURRENT_WORK,PROJECT_STATE,PROJECT_MEMORY,CHANGELOG_AI,CHANGE_EVENT_LOG,DECISION_LOG,TASK_HISTORY}.md`；`AGENTS.md`、`PROJECT_START_HERE.md`、`EXTERNAL_ASSET_REGISTRY.md`、`TEST_MATRIX.md`；`docs/AI_ENGINEERING/{00_PROJECT_OVERVIEW,01_ARCHITECTURE,02_SOURCE_MAP,03_MODULE_DESIGN,04_ANDROID_PIPELINE,06_NETWORK_PROTOCOL,08_BUILD_SYSTEM,10_SECURITY_MODEL,11_ROADMAP,12_LOCAL_ADB,13_REMOTE_ADB,14_ADB_MIRROR}.md`；`docs/ADR/{README,0021-independent-scrcpy-mirror}.md`；`docs/BASELINE/{BASELINE_INDEX,16_ADB_MIRROR_BASELINE_2026-10-14}.md`；`docs/tasks/T-2026-10-14-001-adb-mirror.md`。

## CE-20261014-T001-02 — 复核补漏与 Git/CI 授权交付

- 用户同会话追加明确授权：查漏补缺后提交/推送GitHub main并运行编排，不监控。目标adb-shell/A-Shell-You；Build AShell clients / platform=both / android_arch=aarch64。
- 只读复核：本地main/远端main均a1486ee；工作树49个路径均为01事件，无其他任务改动；workflow存在且active。无本地产品build/test/analyze/codegen。
- 修补：`flutter/lib/desktop/adb/remote_adb_sidebar.dart`推送变更推进epoch，避免旧查询回执覆盖；`src/remote_adb_video.rs`空闲250ms检查停止状态并释放decoder/RGB，不移除订阅。原采集/编译依赖/协议字段不变。
- 记录同步：14_ADB_MIRROR、任务与.codex状态/记忆/日志；STATE/WORK-20261014-002。V0通过，正式V2—V4未取得结果。
- 本事件为stage/commit/push及单次dispatch执行前记录；实际SHA/HTTP接受回执见Git与会话交付。遵守不监控，不读取构建进度；不发布release、不修改生产系统。
- 最终补充：`flutter/lib/desktop/adb/remote_adb_mirror.dart`触摸经FIFO桥接并复验owner/epoch，防止异步native调用乱序；域文档和任务记录同步。

## CE-20261014-T002-01 — 主界面分栏、Shell按钮与ADB连续截图

- Task：T-2026-10-14-002；main/a0e46b1初始clean；用户明确要求本事件代码功能，未新授权Git/CI，未执行产品构建/设备操作。
- 新增源码：`flutter/lib/desktop/adb/remote_display_layout.dart`、`flutter/android/app/src/main/java/me/ashell/localadb/LocalAdbScreenshot.java`。
- 修改源码：`flutter/lib/desktop/adb/{remote_adb_mirror,remote_adb_mirror_view,remote_adb_sidebar}.dart`、`flutter/lib/desktop/pages/remote_page.dart`、`flutter/lib/models/model.dart`（CanvasModel可选视口）、`flutter/android/app/src/main/java/me/ashell/localadb/{LocalAdbMirror,LocalAdbManager}.java`、`src/remote_adb_video.rs`、`libs/hbb_common/protos/message.proto`。
- Delta：单源/双源居中显示、去ADB浮动外壳、同款蓝/红按钮；视频/系统截图互斥、JPEG能力协商、多PC共享及全局停止；只关闭已被ADB画面替代的等待提示，保留原首帧状态/弹窗层。
- Evidence：V0源码接口/权限/资源/坐标/字段/文档链接/空白；原Android采集/无障碍/输入路由、构建入口/依赖与HEAD一致；正式V2—V4 NOT_RUN。
- Docs：14_ADB_MIRROR、ADR0022/D027、Baseline17、TEST_MATRIX、任务、相关域摘要与入口/memory；STATE-20261014-003 / WORK-20261014-004。无新binary或外部依赖，EXT-BIN-SCRCPY-001保持。
- 前轮回执补录：a0e46b1已提交推送、Build AShell clients both/aarch64获HTTP204且未监控。本事件无Git写入/编排，不推断历史CI成功。

## CE-20261014-T002-02 — 用户授权推送并触发客户端编排

- 当前用户明确要求推送并执行编排；范围为本任务35个代码/文档路径，目标adb-shell/A-Shell-You的main，Build AShell clients / platform=both / android_arch=aarch64，单次dispatch、不监控。
- 核对本地HEAD和远端main均a0e46b1，暂存区为空，V0差异空白检查通过，无额外业务代码修改或本地构建/设备操作。
- 同步任务与状态/记忆/日志执行边界，STATE004 / WORK005；D027/ADR0022/Baseline17不变。此事件为执行前记录，准确提交SHA与接受结果见Git/最终回执；不宣称构建成功，不创建Release/tag。

## CE-20261014-T003-01 — 布局/ADB控件恢复与独立黑屏遮罩

- Task T-2026-10-14-003；main/941b215初始clean；用户授权本轮四项代码修改，追加明确遮罩而非熄屏。无本轮Git/CI授权或执行。
- 新增Java AdbPrivacyOverlay/LocalAdbPrivacy；修改独立LocalAdbClient/Mirror/Manager、R8、协议41、Rust独立媒体模块；PC mirror/controller/sidebar/layout/view与remote_page/CanvasModel最小显示适配。
- 恢复：有界私有close、停止守卫/旧Run隔离、PC桥接及操作超时、关闭可重试、旧帧额度清理；视频固定jar缓存避免旧trap误删新运行文件。
- 新黑层排除采集、Android11/12+分支、独立租约、设备单例与多PC状态；原Android采集/Accessibility/输入实现无差异，未改工具链/依赖/品牌。
- Docs：14_ADB_MIRROR、任务、ADR0023/D028、Baseline18、TEST_MATRIX与入口/记忆；STATE005/WORK007。External Registry reviewed-no-change，无新外部资产。
- Verification：V0源码/边界/接口/权限/生命周期/坐标/文档链接/diff；ADB-M13—M16 NOT_RUN。未build/test/analyze/codegen/设备/生产操作。

## CE-20261014-T003-02 — 用户授权提交推送与单次编排

- 本轮用户明确要求提交推送并执行编排；当前30路径均T003，目标adb-shell/A-Shell-You/main。核对本地/远端941b215、暂存区为空、V0 diff通过，Build AShell clients active。
- 同步本任务/状态/记忆/日志执行边界，STATE006/WORK008；D028/ADR0023/Baseline18 reviewed-no-change。将commit/push后dispatch一次both/aarch64、不监控；本条为执行前记录，最终SHA/接受回执见Git与交付。
- 无额外业务源码修改、本地产品构建/设备操作、Release/tag或生产变更；不将dispatch接受作为构建通过。

## CE-20261014-T004-01 — ADB栏、几何、输入与节点穿透

- Task：T-2026-10-14-004；权限C2用户当前八项请求；main/404b199初始clean；单次逻辑实施事件。
- 源码：flutter/lib/desktop/adb/{remote_adb_sidebar,remote_adb_mirror,remote_adb_mirror_view}.dart；flutter/lib/desktop/pages/remote_page.dart；flutter/lib/models/input_model.dart；flutter/android/app/src/main/java/me/ashell/localadb/{AdbCaptureProfile,AdbLayoutCapture,LocalAdbLayout,AdbPrivacyOverlay,LocalAdbManager,LocalAdbMirror,LocalAdbScreenshot,LocalAdbTouch}.java；flutter/android/app/proguard-rules；src/remote_adb_video.rs；libs/hbb_common/protos/message.proto。
- 记录：docs/tasks/T-2026-10-14-004-adb-ui-and-input.md；docs/ADR/0024-adb-layout-and-geometry.md与README；docs/BASELINE/19_ADB_LAYOUT_INPUT_BASELINE_2026-10-14.md与INDEX；docs/AI_ENGINEERING/{14_ADB_MIRROR,10_SECURITY_MODEL}.md；TEST_MATRIX.md；EXTERNAL_ASSET_REGISTRY.md；.codex/{PROJECT_STATE,CURRENT_WORK,DECISION_LOG,TASK_HISTORY,CHANGELOG_AI,CHANGE_EVENT_LOG}.md。
- Delta：去整栏FittedBox、沿用原控件样式；三种ADB源/源几何/统一尺寸档位/限额；去常驻标签、ADB光标；有界触摸恢复/本机递增序号；自身可信遮罩；不抑制原服务的节点helper。
- V0：跨层权限/协议字段/JNI/R8/旧端/资源生命周期/原链范围/差异与文本结构检查。未执行本地build/test/analyze/codegen、真机、多PC或CI；ADB-M17—M20 NOT_RUN。
- 无Git写入/依赖变更/新外部binary/删除文件/版本/发布/生产操作。D029/ADR0024/Baseline19/STATE007；详细证据及正式验证命令见任务文件。

## CE-20261014-T004-02 — 触摸回收复核与授权推送编排

- 用户当前授权查漏补缺、提交推送GitHub并执行编排；C2/C3同任务续接。31路径均T004，目标adb-shell/A-Shell-You/main；执行前本地/远端均404b199，workflow382599743 active。
- LocalAdbTouch补首次stop期限保持、wait后旧Run检查、metrics纳入超时；同步任务及薄记忆。D029/ADR0024/Baseline19 reviewed-no-change，STATE008/WORK012。
- V0复核调用链/JNI/R8/协议/资源回收/几何与原链差异；不运行本地产品构建/测试/分析/codegen。真机场景补ADB-M18，正式验证仍NOT_RUN。
- 紧接stage/commit/push当前31路径，单次Build AShell clients both/aarch64、不监控。此为执行前记录，最终SHA与接受结果见Git/交付，不宣称构建通过；无Release/tag/依赖/生产变更。

## CE-20261014-T005-01 — 同步7607349的ADB当前文档

- Task：T-2026-10-14-005；用户明确文档更新后提交推送，明确不执行编排。初始main/7607349、clean；没有新增代码变更。
- 当前合同：docs/AI_ENGINEERING/12_LOCAL_ADB.md、13_REMOTE_ADB.md、14_ADB_MIRROR.md。
- 关联导航：AGENTS.md、PROJECT_START_HERE.md、docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md、01_ARCHITECTURE.md、02_SOURCE_MAP.md、03_MODULE_DESIGN.md、04_ANDROID_PIPELINE.md、06_NETWORK_PROTOCOL.md、08_BUILD_SYSTEM.md、09_DEBUG_SYSTEM.md、10_SECURITY_MODEL.md、11_ROADMAP.md。
- 验证/资产：TEST_MATRIX.md、EXTERNAL_ASSET_REGISTRY.md；没有新增或替换外部资产。
- 决定/基线/任务：docs/ADR/0025-adb-lanes-and-state-sync.md、0020-adb-code-pairing-and-explicit-refresh.md后继说明、README.md；docs/BASELINE/20_ADB_CURRENT_BASELINE_2026-10-14.md、BASELINE_INDEX.md；docs/tasks/T-2026-10-14-005-adb-docs-sync.md。
- 记忆：.codex/PROJECT_STATE.md、PROJECT_MEMORY.md、CURRENT_WORK.md、CHANGELOG_AI.md、DECISION_LOG.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md；STATE009 / WORK014 / D030。
- Delta：真实推送/独立命令/UID及OPEN修复、模式化停止/全局触摸、RGBA和动态截图节奏、节点边界、白字遮罩/无视启动文案；将旧当前描述修正为源码事实，冻结历史记录不冒充现状。
- Verification：V0源码/提交/本地链接/范围/diff；不把日志修复或历史dispatch204当当前构建通过。文档commit/push按当前授权执行，最终SHA与结果见Git/交付。
- 未执行：业务代码/依赖/版本/工作流修改、构建编排及监控、本地产品build/test/analyze/codegen、设备/生产/Release/tag/删除操作。

## CE-20261016-T001-01 — 同步4d3c446的ADB绘图与PC展示合同

- Task：T-2026-10-16-001；用户授权文档更新/提交推送/单次编排，延续不监控要求。初始main/4d3c446、clean，本地与远端一致。
- 当前合同12/13/14及相关架构/模块/源码/Android/协议/构建/调试/安全/路线入口更新；TEST_MATRIX新增M26—M30全部NOT_RUN；External Registry无新资产。
- ADR0026/D031补录helper只采集节点、App绘图及黑屏PNG职责；Baseline21冻结当前源码与回退边界，旧Task/Baseline正文保留历史。
- PC当前状态顺序、未知值、桌面两行删除及Shell栏无悬浮说明写实；无被回退的无障碍权限控制或开发者菜单。AGENTS/PROJECT_START及.codex薄记忆同步STATE-20261016-001 / WORK-20261016-001。
- 本轮仅Markdown；V0源码/差异/本地链接核对。无本地产品build/test/analyze/codegen、设备、依赖/版本/生产/Release/tag或删除操作。
- 按授权提交推送后单次Build AShell clients both/aarch64，不监控；实际提交SHA和dispatch接受结果见Git/会话交付，本条不预写成功、不证明构建通过。
