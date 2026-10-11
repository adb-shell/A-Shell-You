# AShell Change Event Log

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

Schema Version：`1.0`  
Coverage Start：2026-07-12  
Last Updated：2026-10-12T05:39:32+09:00

Mode：`append-only logical persisted-change events`

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
