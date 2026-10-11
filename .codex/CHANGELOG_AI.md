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

### 后续索引更新规则

- 每个 completed、blocked 或 cancelled AI task 追加一条；长任务仅在正式 handoff milestone 时追加。
- 只保存 scope、outcome、state delta、event IDs、verification、decision/ADR、residual risk 和 next gate 的摘要。
- 完整授权、设计、文件、验证与 rollback 仍以 `TASK_HISTORY.md` / Task artifact 为准。
- 历史纠正使用新的 `corrects/supersedes` 条目，不静默删除旧条目。
- 不保存 prompt/transcript、完整 diff、secret、生产地址、peer/device ID、PII 或绝对用户路径。
