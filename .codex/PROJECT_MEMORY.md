# AShell Project Memory

最后更新：2026-10-25。用途：当前会话恢复入口，不替代源码。

## 0. 当前恢复基线

当前冻结源码快照为 [Baseline23](../docs/BASELINE/23_DEVELOPER_PASSWORD_BASELINE_2026-10-25.md)，test/48226fb。PC主窗口Ctrl+Shift+H先弹密码框，`_developerLoginPasswordHashes`保存两个摘要，任一精确匹配后确认/回车开启进程内免登录；错误可重试，取消/Esc关闭后可重开，重启复位。维护见03_MODULE_DESIGN / FLT-08，文档不保存口令明文。

当前T-2026-10-25-002 / STATE/WORK-20261025-002 / CE-20261025-T002-01：用户明确要求同步文档、以“开发者密码增加”合并提交推送main并执行Both/常规编译；当前准备no-ff merge，实际交付以任务回执为准。唯一工作流Build-Clients-BT，旧入口不再作为当前导航。本轮不新增产品实现、依赖或生产发布。

先读 [PROJECT_STATE](PROJECT_STATE.md) 和 [CURRENT_WORK](CURRENT_WORK.md)，核对HEAD/status。T001的源码356f53b、test交付48226fb与run40203581056只代表前轮证据；新main双端结果单独记录，V1/V3仍未验。旧基线/撤回实现不自动恢复，不把历史授权或dispatch当作当前构建PASS。

当前ADB能力：

- 身份、配对、连接和Shell验证归Android进程；多PC共享真实状态，PC无三秒远程status刷新。
- 命令、媒体、黑屏、触摸各有独立资源；投屏/无视/穿透是同一ADB源的三种互斥模式，普通采集与接收另走原链。
- 视频由固定scrcpy 5.0.1 ScreenCapture进入独立GPU/MediaCodec；无视优先常驻UiAutomation截图，兼容raw/PNG；节点只由Shell读取，App绘图与文字，数字标红。
- 尺寸沿用width/350整数缩放，校验4096/4Mi像素；实时读取普通VideoQoS fps/ratio与码率公式，不写普通QoS。无视至少300ms、穿透至少80ms；参数对齐不等于实测帧率/带宽相同。
- PC独立解码与二进制本机帧桥；普通左/ADB右、单源居中；双栏回普通单栏只重挂本地Texture和校准视口，不重启远程采集。
- ADB可用时全局优先触摸；普通键盘、Ctrl+V、远程输入和单向文本剪贴板同步均已实现，原无障碍/模拟输入链保持。
- 黑屏为独立黑像素buffer层加App预绘白字PNG，排除自身采集；不能把静态代码当作所有ROM已验证。
- 默认core连接服务运行期间静默常亮，无新增主页/齿轮/设置入口。ADB连接验证后尝试本应用电池优化豁免；Shell按钮“点亮屏”仅发送唤醒键。
- PC没有无障碍权限控制或“进入开发者选项”菜单；Android本机ADB页原“打开开发者选项”入口仍在。不根据历史要求擅自补回PC菜单。

完整合同：[本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)。本轮仅V0，不继承历史构建/真机成功结论。旧任务细节保留在TASK_HISTORY和docs/tasks，不再混写到当前恢复快照。

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

开发任务执行 `DEVELOPMENT_WORKFLOW.md`；仓外服务、database、driver 或 binary 查询 `EXTERNAL_ASSET_REGISTRY.md`。安全 Superpowers 只通过 `ashell-superpowers-safe` 的五项只读 allowlist 使用，外部包未安装。冲突时以源码为准；构建/运行行为只有正式环境验证后才能从 `verification-required` 提升为已验证。

## 2. Stable Identity

- 产品显示名：`一个壳子`；内部runtime：`AShell`；ORG：`me`。
- Android application/后台/launcher：`一个壳子`。Windows旧快捷方式文件名、2FA issuer及作者/版权保留 `A Shell You`，不作全局替换。
- 来源：RustDesk 深度二次开发；本地初始化与后续记忆提交，2026-04-13一次性导入来自历史源仓（证据见接管复核文档）；准确 upstream fork commit 未确认。
- Rust crate/library：`ashell`。
- Flutter package：`flutter_hbb`。用户明确要求保留此内部包名及 `package:flutter_hbb/` 导入，与产品品牌无绑定关系。
- Android applicationId：`me.ashell`。
- Android deep link scheme：`ashell`。
- 当前源码版本：Rust `5.2.1`，Flutter `5.2.1+59`；不得由 AI 自动修改。
- 根 license：AGPL-3.0；第三方 license/provenance 尚需完整审计。

## 3. Architecture Anchors

- Rust core：`src/`。
- shared config/protocol：`libs/hbb_common/`。
- capture：`libs/scrap/`。
- Flutter：`flutter/lib/`。
- Android runtime：`flutter/android/app/src/main/kotlin/me/ashell/`。
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
