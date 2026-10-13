# AShell Project Memory

最后更新：2026-10-13

用途：AI 会话的稳定入口，不替代源码和完整工程文档。

## 0. Latest Recovery Snapshot

最新：`T-2026-10-13-005`，[ADB bind导入修复](../docs/tasks/T-2026-10-13-005-adb-bind-import.md)。main/de8bc76已固化T004；用户Android CI报remote_adb_controller.dart三处bind未定义，compileFlutterBuildRelease失败。补直接import platform_model.dart show bind，源码仅一行；修复后未重编/提交/dispatch，需新SHA构建。STATE-20261013-005 / CE-20261013-T005-01；D-022/ADR-0018 reviewed-no-change。以下状态保留历史时点。

最新：`T-2026-10-13-004`，[PC远程ADB](../docs/tasks/T-2026-10-13-004-remote-adb.md)。main/917e661初始clean，独立PC菜单/Misc.remote_adb(40)/JNI与Android共享manager已在未提交工作树实现。各PC查询Android同一配对记录、连接和队列；回执只给origin；revoke清App身份，系统列表仍手动忘记。无ADB投屏，原无障碍/视频/输入处理保持。V0，正式构建/多PC/权限验收待验证；无Git/产品执行。D-022 / ADR-0018 / STATE-20261013-004 / CE-20261013-T004-01；以下全部“最新”仅指各段历史时点。

最新：`T-2026-10-13-003`，[本机 ADB 文档同步](../docs/tasks/T-2026-10-13-003-local-adb-docs.md)。观察 main/f84a78f，initial clean；8662f23 的独立模块保持，当前主页 ServerPage → LocalAdbPage，API 30+ 手动配对/连接/id验证/shell。无 PC ADB/ADB 投屏；旧 LADB 仍退役。当前事实见 [12_LOCAL_ADB.md](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)。本轮仅 Markdown/V0；V2/V3 结果未提供，未执行 Git 写入、构建、设备或发布。D-021 / ADR-0017 / STATE-20261013-003 / CE-20261013-T003-01；以下为各任务当时快照，不覆盖当前 HEAD。

最新：`T-2026-10-13-002`，见 [SDK 初始化修复](../docs/tasks/T-2026-10-13-002-android-sdk-setup.md)。main/c521601、initial clean；T001 已按同一会话“提交github”要求推送。用户报告 sdkmanager 缺失，native/APK 两 job 补固定 SDK setup / CLI 16.0；修复后 V0，运行待验证。STATE-20261013-002 / CE-20261013-T002-01，D-020 reviewed-no-change。本快照在修复提交前写入，后续提交/推送状态以 Git 回执为准；以下 T001 为当时历史快照。

当前最新：`T-2026-10-13-001`，见 [独立客户端 Actions](../docs/BUILD_CLIENTS_ACTIONS.md) 与 [任务](../docs/tasks/T-2026-10-13-001-client-actions.md)。main/8662f23、initial clean；HEAD 已含新的隔离 local ADB。唯一 build-clients.yml 手动选择 Android/Windows/both 与 aarch64/universal，默认 both+aarch64；Android 临时测试签名、Windows unsigned EXE/ZIP；仅 Actions artifacts 14天。旧12个workflow及失效Dependabot移除，本地脚本元数据解耦。只完成V0，未实际编译/Git/签名/上传；本轮修改未提交。D-020 / ADR-0016 / CE-20261013-T001-01 / STATE-20261013-001。

以下 T009 及更早段落是当时快照；“未接入新组件”不能覆盖8662f23当前源码。T009删除的旧libadb.so仍排除，新的local ADB不是该旧组件。

当前最新：`T-2026-10-12-009`，见 [旧 ADB 退役](../docs/tasks/T-2026-10-12-009-remove-legacy-adb.md)。main/13f792d，初始 clean；T008 已提交。删除主页右侧 ADB 页、Dart/native 通道、4 个 Kotlin 模块、无线调试自动化及两项专用权限，排除旧 libadb.so，移除 host adb 检查；新组件未接入。Windows/远控核心/JNI/MainService/DevAutoSelectorController 未改。V0 核对完成，未 build/test/analyze/codegen/操作设备/Git 写入；本轮修改未提交。D-019 / ADR-0015 / CE-20261012-T009-01 / STATE-20261012-010。

以下 T008/T007 为当时快照，不代表当前工作区状态。

当前最新：`T-2026-10-12-008`，见 [Android构建诊断](../docs/tasks/T-2026-10-12-008-android-resource-build.md)。main/fb5c393（修改图标），开始clean；T006/T007此前已按明确请求推送test并快进合并/推送main。用户原目录更新源码后增量编译遇到mergeReleaseResources/source-sets，疑似旧AGP增量问题，未取得完整stacktrace，不能确认根因。已静态修正build.sh两打包函数的stdout路径协议与错误码传播；不会在Flutter失败后继续选旧APK/签名。未改图标和工具链，未清缓存/编译/Git写入；源码修改未提交，只有bash -n及静态检查。CE-20261012-T008-01 / STATE-20261012-009；后续构建机clean/retry需要实际结果。

以下T007为图标修改完成、Git提交之前的历史快照，不代表当前分支或dirty状态。

最新任务：`T-2026-10-12-007`，见 [图标覆盖复核](../docs/tasks/T-2026-10-12-007-icon-coverage.md)。源test/c9b7cd135be838989953e7ab7037f8c6d8d6aa31，initial dirty为T006的32文件；新增补齐后合并51文件未提交。Android通知/主题使用同源机器人白色alpha，默认悬浮球保留完整原图；Windows旧Sciter改为读取同源128 PNG、自解压窗口持有small/big图标、卸载快捷方式引用主EXE。主EXE/服务/托盘/MSI消费链已静态核对；OS RuntimeBroker、系统服务管理器通用图标及用户customSvg不覆盖。12PNG/16XML、引用/ignore、源图及T006图片hash保护检查通过，只有V0。NWG API对照官方源码，本地解析依赖不可核对；实际Windows/Android显示未验证。事件CE-20261012-T007-01；STATE-20261012-008；无架构决定delta，禁止继承编译或Git权限。

T006历史：[产品图标记录](../docs/tasks/T-2026-10-12-006-product-icons.md)。从同一原PNG转换21个图片资源，统一Windows主程序/自解压EXE/托盘、Flutter角标及Android桌面；原图实际1254方形RGBA，源图/UI主图规范为1024。loadIcon使用同源PNG主图与备用，不再回退旧SVG。此轮图片和Flutter代码在T007保持hash不变；T006没有覆盖的通知/悬浮图及主题配置由T007补齐。两轮均未编译/测试/实际打包/安装/Git写入。

T005历史：统一PC入口为build.cmd，直接打包本次清理后编译的Release，不下载/装配外部DLL和驱动；Brotli需预装，共用generator传播Cargo错误并检查输入边界，保留manifest恢复与最终产物检查。当前由c9b7cd1固化；历史基线AS-BL-2026-10-12-c0951b5-pc-build-worktree保持冻结。修改后正式构建和未附带外部组件时的功能仍待验证。

产品 A Shell You，内部 AShell/ashell，Android me.ashell，后台 A Shell You、launcher 一个壳子。**Flutter内部package必须保留flutter_hbb及其导入。** 两处公共服务匹配使用ashell.；构建变量ASHELL_*，ZEGO字段ashellSessionId。基线 `AS-BL-2026-10-12-a036f31-worktree` 加T003增量已由f423335固化。历史文档品牌名称按用户要求规范化，原文字/源仓地址从Git a036f31恢复。详见 [迁移报告](../docs/AI_ENGINEERING/ASHELL_IDENTITY_MIGRATION_2026-10-12.md)。未编译/测试/codegen；实际构建机和服务未操作。下段接管记录保留历史日期与验证结论，不表示当前HEAD或运行验证。

### 迁移前接管证据（历史）

最新复核：[2026-10-12 接管认知](../docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md)。Baseline：`CS-BL-2026-10-12-6fb0aa6`；本地 `test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890`，非 shallow，仅一次初始化提交。原审查工作树 clean；随后 `T-2026-10-12-001` 只保存记忆文档，未提交/推送。新对话必须重新核对 HEAD/status，不能假定工作树永远 clean。

- **来源**：本地 HEAD 的975个 tracked blob 路径/hash与历史源 `历史源仓@cbce0ba36c5c` 一致。源仓60提交、最早2026-04-13无父导入；准确 RustDesk fork point 未知。RustDesk1.4.0对照有615同路径同blob文件，仅证明部分继承。
- **四个关键边界**：Android core/share/frame/waiting 分离；controller relay-only 不删除 endpoint direct/NAT兼容；产品账号不是 endpoint密码认证；ZEGO邀请/Token/录音同意分离，媒体走SDK。
- **容易遗漏的当前行为**：永久密码 getter固定常量、setter不更新；Android已认证输入分支缺desktop对应keyboard gate；terminal有进程内persistent但重连回放TODO；record_upload dormant；旧本地 ADB 子系统已在 T009 移除。
- **风险解释**：JNI正常帧保存Image buffer裸地址后延迟读取；握手有non-secure fallback；双向nonce无方向标识；Android来电3秒自动接受；默认API/token HTTP与本地token保护有边界缺口。均为源码事实/静态风险，未证明已利用漏洞或凭据当前有效。
- **当前资产**：jniLibs目录不存在，不能沿用7月本机libadb.so存在记录；ZEGO声明未进入pubspec.lock；broker有脚本内嵌Go实现，但独立工程/实际部署未知。`.info`是Windows构建输入，不是已证明的运行时配置入口。
- **验证**：仅V0；未build/test/analyze/codegen、未运行服务/设备/生产验证，未审相邻API/WEB/SERVER仓。55处Rust测试标记不等于通过；Flutter cm_test是手动harness。
- **恢复方式**：入口→状态/活动任务→本摘要→相关报告章节/源码。保持中文解释与English anchor；后续用户请求决定权限，不从已关闭任务恢复授权。文件在同一检出中可读；新worktree/机器必须实际包含这些文件才能恢复。

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

- 产品显示名：`A Shell You`；内部runtime：`AShell`；ORG：`me`。
- Android application/后台：`A Shell You`；launcher：`一个壳子`。
- 来源：RustDesk 深度二次开发；本地初始化与后续记忆提交，2026-04-13一次性导入来自上节映射的历史源仓；准确 upstream fork commit 未确认。
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
- 旧Android本地ADB/LADB已在T009移除；8662f23本机模块由T004扩展PC专用请求与Android共享状态，详见12_LOCAL_ADB.md/13_REMOTE_ADB.md。无ADB投屏，不能恢复旧入口/自动化。
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
