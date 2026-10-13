# AShell AI Changelog

Schema Version：`1.0`  
Coverage Start：2026-07-12  
Last Updated：2026-10-12T05:39:32+09:00

Mode：`append-only task-level index`

> 本文件记录可由 `.codex/TASK_HISTORY.md` 和交付物证明的 AI-assisted engineering task 结果。它不是产品 changelog、Git history、Decision Log 或逐文件修改日志。2026-07-12 之前的提交没有可靠 AI attribution，因此不补猜；精确修改事件在本日志建立前不可重建，统一标记为 retrospective/backfill。

## T-2026-07-12-001：项目资产接管

- Record Type：`retrospective/backfill`
- Status：`completed — repository-side only`
- Scope：全仓 archaeology、架构/文档审计、长期 memory 与八个领域 Skills。
- Outcome：建立 `docs/AI_ENGINEERING/`、初始 `.codex/` 和 `.agents/skills/`；识别外部资产、安全与可复现性缺口。
- Business Behavior Change：none。
- Highest Verification：V0；未执行项目 build/test。
- Decisions：D-001—D-011；ADR backfill 由后续任务完成。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-07-12-002：AI 工程体系强化

- Record Type：`retrospective/backfill`
- Status：`completed — repository-side governance`
- Scope：统一入口、AI rules、T0—T8、Development Workflow、External Asset Registry 与八个 Skills 审查。
- Outcome：形成固定任务/权限/外部资产链路；未扩大业务或外部权限。
- Business Behavior Change：none。
- Highest Verification：V0；未执行项目 build/test。
- Decisions：D-012 / ADR-0012。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-07-12-003：AI 工程体系最终封版

- Record Type：`retrospective/backfill`
- Status：`completed — repository-side governance seal`
- Scope：ADR、Baseline、Task Template、Test Matrix 与只读 Safe Superpowers adapter。
- Outcome：建立 14 项 ADR、工程 Baseline、52-case test matrix 和安全推理边界。
- Business Behavior Change：none。
- Highest Verification：V0；V1—V5 未执行。
- Decisions：D-013 / ADR-0000 / ADR-0013。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-07-12-004：AI 全局记忆增强

- Record Type：`contemporaneous`
- Status：`completed — repository-owned global session memory`
- Scope：Project State、multi-session Current Work、AI Changelog、Change Event ledger、Session Start Protocol，以及入口/任务协议同步。
- Outcome：新 Codex 会话可按项目文件恢复 Baseline、当前状态、并行任务、AI 历史、重大决定、相关 ADR/Skill 和下一确认门；旧会话权限明确不继承。
- Persistent Changes：五个新 `.codex` 文件；入口、AI Rules、Task Protocol、Task Template、Decision/Task history integration。
- Business Behavior Change：none。
- Change Event IDs：`CE-20260712-T004-01`—`CE-20260712-T004-06`。
- Highest Verification：V0；blind session recovery + ambiguity-fix regression PASS；未执行项目 build/test。
- Decision / ADR：D-014；ADR-0011、ADR-0012、ADR-0013 context。
- Residual Risk：file-based memory is as-of and cooperative, not a lock or production telemetry；每个新会话仍须重查 HEAD/dirty state 和当前授权。
- Next Gate：下一个用户任务从 Session Start Protocol + T0 开始；无 standing C2/C3。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-10-12-001：本轮接管记忆持久化

- Record Type：`contemporaneous`
- Status：`completed — workspace-local documentation`
- Authority：当前用户要求将本轮记忆保存供本工作区新对话恢复；C1 文档编辑。前序接管审查为 C0，期间保持只读。
- Source：`test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890`；初始工作树 clean。
- Outcome：保存带源码锚点的接管报告、短记忆和新观察基线；接入 AGENTS/Start Here/工程索引，纠正旧 HEAD、branch、本机 ADB 资产与凭据有效性表述。
- Baseline：`CS-BL-2026-10-12-6fb0aa6`；旧7月基线和历史记录保留。
- Business Behavior Change：none；仅13个 Markdown 文件新建/更新。
- Change Event：`CE-20261012-T001-01`。
- Highest Verification：V0 文件/引用/基线/变更范围/状态关联检查；无实际新聊天恢复测试，无项目 build/test/analyze/codegen。
- Decision / ADR：沿用 D-014；`N/A — no decision delta`；无新 ADR。
- Residual Risk：记忆是带日期的静态证据；同一检出可读，其他机器/worktree需实际包含这些文件；未提交、推送或上传云端。
- Next Gate：本任务关闭；新请求重查 HEAD/status 并从 T0 开始，不继承权限。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID；完整修改清单见同 Event。

## T-2026-10-12-002：AShell产品身份迁移

- Record Type：contemporaneous；Status：completed — source/V0 only。
- Authority：当前用户明确要求改产品身份/包名/文件/构建引用；后续明确保留flutter_hbb及其导入；禁止执行编译等命令。
- Source：test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76；initial clean；当前变更未提交。
- Outcome：AShell/ashell、me.ashell、后台/PC A Shell You、launcher一个壳子；跨层native加载和打包消费端、状态事件、当前文档/Skills同步。Flutter内部package不改。
- Decision/Baseline/Events：D-015 / ADR-0014；AS-BL-2026-10-12-a036f31-worktree；CE-20261012-T002-01、CE-20261012-T002-02。
- Verification：30项静态断言及引用/范围复核；V1—V5 NOT_RUN，无编译/运行成功结论。
- Residual：公共服务guard和来源记录保留；按追加授权只用ASHELL_*变量与ashellSessionId字段，实际构建机/服务待同步。新包不继承旧数据和权限；native资产、ZEGO lock、签名及Apple/Google注册待验证。
- Detail：TASK_HISTORY同ID；docs/tasks/T-2026-10-12-002-ashell-identity.md；迁移报告含编译验证需求。
- Next Gate：新用户任务从T0重新核对；没有standing build/Git/release权限。

## T-2026-10-12-003：统一全部品牌残留与文档字段

- Status：completed — source/V0；用户明确要求补齐截图两处匹配和所有文档字段/文件名。
- Source：test/a036f31；保留T002未提交变更。新增两处ashell.匹配；41份初始含旧词文档统一品牌，四份文档改名/引用同步；历史文本加规范化说明，原文/真实源仓地址留在Git。
- Boundary：Flutter内部flutter_hbb保留；不改上游RustDesk技术名，不编造新源仓URL，不操作远端/部署。
- Verification：排除.git后全文/路径零旧词命中，本地链接/两处谓词/Flutter包名和diff检查通过；未build/test/analyze/codegen。
- Decision/Event/State：D-016补充ADR-0014；CE-20261012-T003-01；STATE-20261012-004 / WORK-20261012-006。
- Detail：docs/tasks/T-2026-10-12-003-brand-normalization.md；迁移报告；TASK_HISTORY同ID。

## T-2026-10-12-004：更新服务器默认配置

- Status：completed — source/V0；用户明确提供新IP、七端口映射和服务器Key。
- Source：test/f423335，开始时clean且与origin/test一致；此前品牌变更已由用户提交并按请求推送。
- Outcome：12份原配置/源码/脚本/文档仅作指定值替换，旧值排除.git后零残留；同步工程文档与恢复入口。本轮未提交。
- Verification：逐文件对照HEAD的预期字面替换一致；端口派生、公钥格式和diff检查通过；运行/构建/握手/部署均NOT_RUN。自定义配置和构建环境仍可覆盖默认值。
- Decision/Event/State：D-017；CE-20261012-T004-01；STATE-20261012-005 / WORK-20261012-008。
- Detail：docs/tasks/T-2026-10-12-004-server-configuration.md；TASK_HISTORY同ID；无standing Git/build/external权限。

## T-2026-10-12-005：PC入口合并和编译产物打包

- Status：completed — source/V0；用户明确授权旧脚本删除与新入口迁名、移除外部驱动装配。
- Source：test/c0951b5，initial clean。build.cmd现为唯一PC入口；generator修正失败传播与输入边界；同步工程说明、入口、Skill和恢复记忆。
- Verification：Python AST与3段内嵌PowerShell仅解析，CMD label/调用关系与diff静态检查；不执行产品脚本、安装、构建、测试或打包。
- Limits：Python Brotli需预置；未附带组件的运行功能不保证可用；其他历史workflow未改，旧基线保留。
- Decision/Event/State：D-018 / CE-20261012-T005-01 / STATE-20261012-006 / WORK-20261012-010。
- Detail：docs/tasks/T-2026-10-12-005-pc-build-entry.md；新baseline07；本轮未提交/推送。

## T-2026-10-12-006：原图统一Windows与Android产品图标

- Status：completed / T8 / V0；用户明确授权保持原图设计做尺寸/格式转换及指定资源替换，禁止编译。
- Source：test/c9b7cd1，initial clean；T005已由该HEAD固化。本任务32文件未提交：21图片、2加载/ignore配置、9任务/工程/恢复文档。
- Outcome：3个9尺寸ICO，1024源图/UI主图、256备用PNG；Android五密度普通/备用圆形/自适应前景。loadIcon不再回退旧SVG，两个新增PNG可被Git发现。
- Verification：文件解码、PNG/ICO尺寸与alpha、内容留白、源图SHA未变、资源引用和ignore例外、静态预览及diff检查；无build/test/analyze/codegen/安装/实际打包/Git写入。
- Limits：正式Windows Shell/EXE与Android APK/主题/启动器显示未验证；通知、悬浮球、横向Logo、动画和其他平台专有资源不在范围。
- Decision：N/A — no decision delta，沿用ADR-0014；Event：CE-20261012-T006-01；STATE-20261012-007 / WORK-20261012-012。
- Detail：docs/tasks/T-2026-10-12-006-product-icons.md；后续构建/Git按新的明确请求执行。

## T-2026-10-12-007：Windows / Android 图标覆盖补齐

- Status：completed / T8 / V0；用户追加授权检查所有Windows/Android产品图标并继续生成替换，继续禁止编译。
- Source：test/c9b7cd1；initial dirty为T006的32文件，本轮28文件，与T006重叠9文件，合并51文件未提交。
- Outcome：Android五密度通知小图/独立monochrome、默认悬浮球；Windows旧Sciter同源图、自解压窗口small/big、卸载快捷方式品牌图标；精确ignore例外。主EXE/服务/托盘/MSI路径已核对。
- Verification：12PNG/16XML、尺寸/alpha/遮罩空洞、图片预览、include/资源引用、T006图片及Flutter加载代码hash保护、范围/diff/记忆链接。仅V0；无build/test/analyze/codegen/安装/运行/打包/Git写入。
- Limits：系统服务管理器与OS RuntimeBroker图标不改；用户自定义悬浮图可覆盖默认；其他平台专有资源不在范围。本地NWG依赖源码缺失，正式编译与Shell/设备显示仍待验证。
- Decision：N/A — no decision delta，沿用ADR-0014；Event CE-20261012-T007-01；STATE-20261012-008 / WORK-20261012-014。
- Detail：docs/tasks/T-2026-10-12-007-icon-coverage.md；本条补充T006历史范围，不改写当时结论。

## T-2026-10-12-008：Android资源错误诊断与脚本结果通道

- Status：completed — scoped source/V0；资源合并根因及构建机验证仍verification-required。
- Source：main/fb5c393（修改图标），initial clean；前轮test/main已同步，本轮11文件未提交。
- Evidence：用户确认旧目录增量构建；AGP7.3.1的source-sets异常与官方同类问题相符，未复现。脚本Missing file混入早先打包标题，则由stdout捕获路径可直接解释。
- Outcome：build.sh split/universal日志送stderr、stdout仅路径、构建失败和调用者保留非零退出，不再失败后选择旧APK。没有自动clean或版本升级，图标不改。
- Verification：bash -n、XML解析、静态输出/失败链、diff/范围与链接；build/test/运行/清缓存/Git全部none。构建机后续clean/retry步骤见task。
- Decision/Event：N/A — no decision delta；CE-20261012-T008-01；STATE-20261012-009 / WORK-20261012-016。
- Detail：docs/tasks/T-2026-10-12-008-android-resource-build.md；源码修正未宣称资源合并已恢复。

### 后续索引更新规则

- 每个 completed、blocked 或 cancelled AI task 追加一条；长任务仅在正式 handoff milestone 时追加。
- 只保存 scope、outcome、state delta、event IDs、verification、decision/ADR、residual risk 和 next gate 的摘要。
- 完整授权、设计、文件、验证与 rollback 仍以 `TASK_HISTORY.md` / Task artifact 为准。
- 历史纠正使用新的 `corrects/supersedes` 条目，不静默删除旧条目。
- 不保存 prompt/transcript、完整 diff、secret、生产地址、peer/device ID、PII 或绝对用户路径。

## T-2026-10-12-009：移除旧 ADB 页面与组件

- 日期：2026-10-12；状态：completed（源码交付/V0，运行验收 pending）。
- Source：main / 13f792d552db49fd1b702ff24cb2b3d0c5030c38；initial clean；T008 先前已提交。
- Authority：当前用户明确要求剔除，含 C2 相关代码/配置和 C3 删除 5 个旧组件源文件；C1 同步当前工程事实。未继承旧 Git/build 权限。
- Outcome：删除 AdbPage、Dart wrapper/constants、11 个 native handler、4 个 Kotlin 模块、36 个 Accessibility 自动化方法及其状态/事件钩子；移除两项专用权限/keep 规则，排除 libadb.so，移除 host adb 检查。未接入新组件。
- Preserve：Windows/Rust 核心/JNI/MainService/DevAutoSelectorController/依赖版本及协议字段不变；其余 native packaging 与 Wi-Fi 保活保留；用户 prefs/配对/系统设置未操作。
- Verification：V0 diff/删除边界、残留/调用扫描、Manifest XML、Bash 仅语法解析、协议字段及保留代码对比。V2/V3 NOT_RUN。
- Decision / Baseline / Event：D-019 / ADR-0015；AS-BL-2026-10-12-13f792d-adb-retired-worktree；CE-20261012-T009-01。
- Docs：当前领域/入口/Skills、Test Matrix、External Registry、ADR/Baseline、全局记忆已同步；旧集成记录标 HISTORICAL/RETIRED，未删除历史。
- Git / build / install / device / release：none；本轮修改未提交。正式验证需求和回滚见 [任务记录](../docs/tasks/T-2026-10-12-009-remove-legacy-adb.md)。

## T-2026-10-13-001：独立客户端 GitHub Actions

- Status：completed — workflow/configuration/V0；runner V2 NOT_RUN。
- Source：main/8662f23，initial clean；新 local ADB 已在 HEAD，本轮不改。
- Outcome：清理12个旧 workflow 与失效 submodule Dependabot；唯一 build-clients.yml 手动选择 Android/Windows/both、aarch64/universal，默认 both+aarch64，Android 临时测试签名，Windows unsigned EXE/ZIP；仅 Actions artifacts，14 天。
- Compatibility：不调用本地 Bash/CMD 入口；build.sh 和历史 F-Droid 解除对删除文件的版本读取；产品源码/manifest/locks/generated bridge 保持。
- Verification：V0 YAML、矩阵、固定SHA、shell/Python/PowerShell语法、引用和diff；没有编译、远端运行、签名、上传或Git写入。
- Decision/Event/State：D-020 / ADR-0016；CE-20261013-T001-01；STATE-20261013-001 / WORK-20261013-002。
- Details：[使用说明](../docs/BUILD_CLIENTS_ACTIONS.md)、[任务](../docs/tasks/T-2026-10-13-001-client-actions.md)、baseline09。临时签名不能跨次覆盖安装，Windows驱动能力/依赖解析/runner兼容仍需正式验证。

## T-2026-10-13-002：修复 Android SDK 初始化

- Source：main/c521601，initial clean；corrects T001 交付后的状态：用户随后明确要求“提交github”，T001 已提交并推送。
- Outcome：两个 Android job 显式 setup-android v3.2.2 / CLI 16.0，配置 PATH/SDK root/许可证；后续 sdkmanager 打印路径/版本并固定安装根目录。仅配置修复，Windows job 和原编译选项保持。
- Evidence：用户报告旧流程 command not found / 127；修复后 V0 YAML/11 Bash/1 Python、步骤顺序/固定 SHA/不变量与 diff/引用检查。runner 未重跑。
- Authority：延续同一会话工作流修复与 Git 提交要求；无远端 dispatch/产品编译/签名/发布。
- Decision/State/Event：D-020 reviewed-no-change；STATE-20261013-002 / WORK-20261013-004 / CE-20261013-T002-01。
- Details：[任务](../docs/tasks/T-2026-10-13-002-android-sdk-setup.md)、baseline10；需用新 SHA 发起 Run workflow。

## T-2026-10-13-003：同步独立本机 ADB 文档

- Source：main/f84a78f、initial clean；8662f23模块无后续差异。
- Outcome：12_LOCAL_ADB集中说明使用、源码/桥接、生命周期、身份、依赖、排查与验证；ADR-0017/D-021补记新模块；Baseline11/External Registry/AND-09—13/入口/Skills/记忆同步。历史LADB正文不重写。
- Authority / verification：当前文档同步请求C1；仅Markdown/V0，无产品代码、Git写入、构建测试或设备操作。V2/V3未提供结果，不标PASS。
- State / Event：STATE-20261013-003 / WORK-20261013-006 / CE-20261013-T003-01；文档交付complete，修改未提交。
- Detail：[当前ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[任务](../docs/tasks/T-2026-10-13-003-local-adb-docs.md)。PC ADB与ADB投屏未实现；新依赖hash/NOTICE/JNI与ROM验证待补。

## T-2026-10-13-004：PC 远程 ADB 与 Android 共享状态

- Record Type / status：current / completed repository implementation；运行待验证。
- Source：main/917e661初始clean，工作树未提交。
- Change：PC顶栏配对/连接/命令/撤销/刷新/断开；专用Misc.remote_adb和独立JNI；Android共享身份/状态、有限队列与owner取消。
- Scope boundary：不改原无障碍/画面/输入处理，不加入ADB投屏，不降低原会话加密；撤销只清App身份，系统列表需手工忘记。
- Evidence：V0静态；ADB-R01—07的V2/V3/V4待正式环境，无Git/构建/设备/发布。
- Decision / event / state：D-022 / ADR-0018；CE-20261013-T004-01；STATE-20261013-004 / WORK-20261013-008。
- Detailed record：docs/tasks/T-2026-10-13-004-remote-adb.md；docs/AI_ENGINEERING/13_REMOTE_ADB.md。

## T-2026-10-13-005：修复远程 ADB Dart 编译失败

- Source：main/de8bc76，initial clean；当前修复未提交。
- Evidence / fix：Android用户job日志三处bind未定义；controller补platform_model.dart显式导入，产品代码仅一行。
- Verification：V0，修复前CI FAIL、修复后NOT_RUN；无Git/build/test/device/dispatch/release。
- Decision / Event：D-022 reviewed-no-change；CE-20261013-T005-01 / STATE-20261013-005 / WORK-20261013-010。
- Detail：docs/tasks/T-2026-10-13-005-adb-bind-import.md。
