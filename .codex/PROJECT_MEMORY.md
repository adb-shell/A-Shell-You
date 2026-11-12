# OpenWorld Project Memory

最新T003续检交付：源码test/0e5cc3d已推送；2026-11-12 14:50:21 UTC触发Both/常规编译，HTTP204，未查询运行进度/结果。此前未提交修复已纳入；后续回执仅文档，不再编排。24项静态通过，正式编译/真机待证。STATE/WORK-20261112-003-r7 / CE-20261112-T003-07。

最新T003本地续检：test/b25b9aa上未提交修复，确定未执行的权限命令还原原暂停记录；PC查询回执也更新菜单快照，共享监听不回写旧权限。24项静态与diff通过；原系统列表命令及采集/媒体/触摸/语音保持，编译/真机待证。STATE/WORK-20261112-003-r5 / CE-20261112-T003-05；本轮无提交推送或编排/监控。

2026-11-12 T003交付：源码test/115d0bb已推送；PC新增本应用无障碍开启/关闭/暂停恢复，动态自身组件、设备暂停记录、多PC真实状态，原采集/触摸/ZEGO源码保持。25项静态与13项离线Shell检查通过，真机待证；Both/常规编译于13:42:08 UTC获HTTP204，未查询新运行进度或结果。STATE/WORK-20261112-003-r3 / CE-20261112-T003-03；后续回执仅文档，不二次编排。

2026-11-12 / T001续修回执：源码c788e8d224433fc0e149d741d25ac174f7cb56df已推送origin/test，dispatch前远端一致。2026-11-11 16:33:34 UTC（2026-11-12 01:33:34 Asia/Tokyo）向Build-Clients-BT.yml（394791509）提交ref=test、Platform=Both、BuildMode=常规编译，HTTP204受理。按要求未查询新run/jobs/logs/artifacts，不监控；最终结果未知。 T8仅指定交付范围；13项离线检查通过，真实runner验证未取得。STATE、WORK-20261112-001-r6 / CE-20261112-T001-04—06；补回执不二次编排。

2026-11-12 T001续修：f4a71c6的Android主源DNS仍失败且_apt无法穿过RUNNER_TEMP父路径。现改/tmp独立目录、root 0755父目录与_apt:root 0700 partial，实际以_apt检查目录/源文件/解析器配置访问；主源→kernel.org→MIT顺序探测完整Jammy三套索引，APT官方签名验证成功才使用。每候选索引隔离，全部失败禁止安装；索引更新每次3分钟、安装10分钟、步骤总限24分钟。 13项离线回归与Bash语法通过，实际runner权限/DNS/签名验证待证；当前T7待test提交推送及Both/常规编译，不监控。STATE、WORK-20261112-001-r5 / CE-20261112-T001-04/05；[任务](../docs/tasks/T-2026-11-12-001-build-fixes.md)。

2026-11-12 / T-2026-11-12-001交付：修复源码f4a71c6e6d57ac41ca1ecf49aa50bb791ad8458b已提交推送origin/test，dispatch前远端SHA一致。2026-11-11 16:23:49 UTC（2026-11-12 01:23:49 Asia/Tokyo）向Build-Clients-BT.yml（394791509）发送ref=test、Platform=Both、BuildMode=常规编译，HTTP204受理。按用户要求未查询新run/jobs/logs/artifacts，未监控，最终构建结果未知。 T8仅指定交付范围；Bash语法/离线流程7/7通过，真实构建待证。STATE、WORK-20261112-001-r3 / CE-20261112-T001-01—03；补回执文档不二次触发。

2026-11-12 / T-2026-11-12-001：依据用户ZIP修复cd21705的Windows三处Rust编译错误；Android卡在APT镜像DNS解析，更新失败后旧索引继续安装，约28分钟后取消。Rust补宏导入与async move所有权；CI改独立官方HTTPS源/新索引，更新错误即退出，更新5分钟、安装10分钟、步骤17分钟上限。 Bash语法与7/7离线命令桩流程检查通过（成功、更新失败/超时、安装失败/超时、空包参数、缺少RUNNER_TEMP），未调用真实sudo/APT/网络。工作流仅替换依赖安装入口并加步骤时限，包清单、其他jobs、工具链、签名、权限保持；diff通过。YAML解析器与Rust/Flutter工具链缺失，采用精确工作流差异/缩进和Rust所有权复核，不能记作完整编译PASS。 当前active/T7，待提交test并触发双端常规编译，不监控；STATE、WORK-20261112-001-r2 / CE-20261112-T001-01/02。PC2现有四项地址更新一并保留，Android/PC1与共享Key保持。

最后更新：2026-11-12。用途：当前会话恢复入口，不替代源码。

## 0. 当前恢复基线

T-2026-11-11-002源码cd2170512613291d48709bc5ec9e393c8feb42e8已推送origin/test，后续仅补回执文档；输入Baseline26，test版本5.2.1/5.2.1+59保持。服务器地址唯一入口`libs/hbb_common/src/server_profiles.rs`：现有编辑已填齐三套四项地址，Android与PC1相同、PC2独立，空闲经后台确认后切换，独立账号/peer缓存。见[15_SERVER_PROFILES](../docs/AI_ENGINEERING/15_SERVER_PROFILES.md)。

STATE、WORK-20261111-002-r5 / CE-20261111-T002-01—05 / D-036 / ADR-0029；首轮V0 27/27、续接31/31及diff通过，六项相关单测/联调NOT_RUN，complete/T8仅本次交付范围。已推送test并触发双端常规编译，HTTP204受理；用户要求不监控，未查询run/jobs/logs/artifacts，构建结果未知。历史任务和编译回执只供追溯。

原PC双密码/开发者画面优先与Android采集/ADB/ZEGO合同保持，见ADR-0028；不把历史构建HTTP204当本轮证据。先核对HEAD/status与[PROJECT_STATE](PROJECT_STATE.md)、[CURRENT_WORK](CURRENT_WORK.md)。

当前ADB能力：

- 身份、配对、连接和Shell验证归Android进程；多PC共享真实状态，PC无三秒远程status刷新。
- 命令、媒体、黑屏、触摸各有独立资源；投屏/无视/穿透是同一ADB源的三种互斥模式，普通采集与接收另走原链。
- 视频由固定scrcpy 5.0.1 ScreenCapture进入独立GPU/MediaCodec；无视优先常驻UiAutomation截图，兼容raw/PNG；节点只由Shell读取，App绘图与文字，数字标红。
- 尺寸沿用width/350整数缩放，校验4096/4Mi像素；实时读取普通VideoQoS fps/ratio与码率公式，不写普通QoS。无视至少300ms、穿透至少80ms；参数对齐不等于实测帧率/带宽相同。
- PC独立解码与二进制本机帧桥；普通左/ADB右、单源居中；双栏回普通单栏只重挂本地Texture和校准视口，不重启远程采集。
- ADB可用时全局优先触摸；普通键盘、Ctrl+V、远程输入和单向文本剪贴板同步均已实现，原无障碍/模拟输入链保持。
- 黑屏为独立黑像素buffer层加App预绘白字PNG，排除自身采集；不能把静态代码当作所有ROM已验证。
- 默认core连接服务运行期间静默常亮，无新增主页/齿轮/设置入口。ADB连接验证后尝试本应用电池优化豁免；Shell按钮“点亮屏”仅发送唤醒键。
- T-2026-11-12-003新增PC本应用无障碍开启/关闭/暂停恢复菜单：Android动态识别自身组件，仅改自身系统列表条目，暂停记录归设备并同步多PC。无“进入开发者选项”PC菜单；Android本机原入口仍在。详见13_REMOTE_ADB，原采集/触摸/语音未改造。

完整合同：[本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)。本轮编译回执与真机证据分开，不继承历史成功结论。旧任务细节保留在TASK_HISTORY和docs/tasks，不再混写到当前恢复快照。

## 1. Entry and Source of Truth

所有 AI 先读：

1. `PROJECT_START_HERE.md`。
2. `.codex/AI_RULES.md`。
3. `docs/AI_ENGINEERING/AI_TASK_EXECUTION_PROTOCOL.md`。
4. 开发任务使用 `TASK_TEMPLATE.md`，并引用 `docs/BASELINE/BASELINE_INDEX.md`。
5. 长期决定查 `docs/ADR/README.md`；验证查 `TEST_MATRIX.md`。

工程事实层级：

1. 当前源码、manifest、build script 和 protocol definition。
2. `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md` 与对应领域文档。
3. `docs/ENGINEERING_*` 与 `docs/TASK_ENTRYPOINTS.md`，作为旧主套件和历史细节。
4. 本目录、`AGENTS.md`、`CLAUDE.md` 只作为规则、摘要和入口，不覆盖源码。

开发任务执行 `DEVELOPMENT_WORKFLOW.md`；仓外服务、database、driver 或 binary 查询 `EXTERNAL_ASSET_REGISTRY.md`。安全 Superpowers 只通过 `openworld-superpowers-safe` 的五项只读 allowlist 使用，外部包未安装。冲突时以源码为准；构建/运行行为只有正式环境验证后才能从 `verification-required` 提升为已验证。

## 2. Stable Identity

- 产品显示名：`开放世界`；内部runtime：`OpenWorld`；macOS ORG：`com.openworld`。
- Android application/后台/launcher及Windows快捷方式：`开放世界`；2FA issuer及产品作者/版权信息：`OpenWorld`。第三方署名和许可保持。
- 来源：RustDesk 深度二次开发；本地初始化与后续记忆提交，2026-04-13一次性导入来自历史源仓（证据见接管复核文档）；准确 upstream fork commit 未确认。
- Rust crate/library：`openworld`。
- Flutter package：`flutter_hbb`。用户明确要求保留此内部包名及 `package:flutter_hbb/` 导入，与产品品牌无绑定关系。
- Android applicationId：`com.openworld.me`。
- Android deep link scheme：`openworld`。
- 当前源码版本：Rust `5.2.1`，Flutter `5.2.1+59`；不得由 AI 自动修改。
- 根 license：AGPL-3.0；第三方 license/provenance 尚需完整审计。

## 3. Architecture Anchors

- Rust core：`src/`。
- shared config/protocol：`libs/hbb_common/`。
- capture：`libs/scrap/`。
- Flutter：`flutter/lib/`。
- Android runtime：`flutter/android/app/src/main/kotlin/com/openworld/me/`。
- Android active JNI：`libs/scrap/src/android/pkg2230.rs`；`ffi.rs` 是未导出的兼容层。
- Windows privacy/display：`src/privacy_mode.rs`、`src/privacy_mode/`、`src/virtual_display_manager.rs`。
- account/API/sync/download：`src/hbbs_http/` 与 Flutter models。
- external infrastructure：hbbs、hbbr、产品 API/DB、ZEGO RTC；ZEGO Token broker 仅有 deployment script 内嵌Go实现，缺独立受控工程与部署证据。

完整架构见 `docs/AI_ENGINEERING/01_ARCHITECTURE.md`。

## 4. Non-Negotiable Runtime Facts

- Android 必须分 core service、screen share、frame source、PC waiting 四层状态。
- 只有明确用户操作可以请求新的 MediaProjection permission。
- waiting/reconnect 只可刷新已有 normal video，不可自动切 ignore/screenshot。
- Android 真实 RGBA/Texture frame 必须清 PC waiting。
- controller 当前强制 relay；受控端仍保留 rendezvous/direct/NAT compatibility code。
- Windows 当前 virtual display implementation 是 Amyuni；RustDesk IDD 分支是 dormant。
- 旧Android本地ADB/LADB已退役；当前LocalAdbManager为独立实现，支持远程配对/Shell、媒体、触摸与文本输入。当前合同见12_LOCAL_ADB、13_REMOTE_ADB和14_ADB_MIRROR；不能恢复旧入口/自动化。
- ZEGO 媒体不走原 RustDesk audio service；peer protocol 只传 invitation/control state。
- `verify_login()` 的 legacy UI bypass 不等于产品 API、hbbs 或 endpoint authentication bypass。

## 5. Highest Risks

- 公开源仓内容有 credential 类型字面值；当前有效性和生产用途未测试，不把字面值存在等同已确认生产事件；值不得复制。
- 构建内置共享远控密码可由公开源码获知，permanent-password setter 不真正更新；需要每设备高熵凭据迁移。
- Android ImageReader DirectBuffer ownership 可能失效；JNI `static mut` 存在 data-race/UB 风险。
- Android input/custom command 在受控端的 permission enforcement 不完整。
- Android custom-scheme config import 可改写 rendezvous/API/trust key；update/plugin 缺完整 signature/hash/containment gate。
- ZEGO token transport/credential 和 Android auto-accept 存在隐私风险。
- peer secure handshake、secretbox nonce 和本地 password protection 需密码学专项审计。
- 产品 HTTP/sync 路径存在 plaintext/auth boundary 风险。
- Windows privacy/injection/virtual display 是高权限、崩溃恢复敏感区。
- 仍使用的 driver/helper assets 来源尚未闭环；旧 libadb.so 已排除，不再是当前构建前置。

风险详情见 `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`。

## 6. Mutation Rules

- 未经用户明确确认：不 commit、push、merge、rebase、stage、release、upload、version bump、删除源码或文档。
- 当前环境不是正式编译环境：不执行 Cargo/Flutter/Gradle/Android/Windows/Docker build/test。
- 需要验证时输出《编译验证需求》，列命令、环境、目录、目标，等待正式环境结果。
- 不修改无关代码，不覆盖用户工作树，不清理历史资产。
- 源码事实变化时同步更新 AI engineering docs、decision log 与 task history。
- 所有 secret、token、password、key、生产地址和 PII 必须脱敏。
- 所有开发任务记录 Baseline ID、相关 ADR 和 TEST_MATRIX case IDs。
- Superpowers adapter 仅 brainstorming/planning/debugging/verification/review；commit/push/release hard-denied。

完整规则见 `.codex/AI_RULES.md`。

## 7. Open Asset Gaps

- 2026-04-13 之前的 upstream/DaXianDesk history。
- hbbs/hbbr source、version、config 和部署拓扑。
- 产品 backend/OpenAPI/DB/schema/migration/backup。
- ZEGO broker 独立受控 project/commit、dependency lock、production provenance 和 credential ownership；当前 deployment script 只内嵌了部分 Go server source。
- 当前 Windows driver/helper 的 source revision、license 和 hashes；旧 ADB binary 历史记录保留，不是当前输入。
- 正式 Android/Windows build 与 regression evidence。

这些缺口未补齐前，不得宣称全系统接管或发布就绪。

完整状态、owner、所需材料和 release impact 统一维护在 `EXTERNAL_ASSET_REGISTRY.md`，本节只保留摘要。
