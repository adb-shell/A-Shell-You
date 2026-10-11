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
