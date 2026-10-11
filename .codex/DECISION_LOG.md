# AShell Decision Log

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

最后更新：2026-10-12

> 这里记录可由源码、Git 历史或用户明确指令证明的重大决定。历史条目属于 retrospective record，不表示原提交时存在正式 ADR。未来决定必须记录备选项、风险、批准人和验证结果。

## 状态定义

- `accepted`：当前源码/用户指令确认。
- `retrospective`：从历史和源码回溯确认，原始理由可能不完整。
- `proposed`：尚未批准，不可据此实施。
- `superseded`：已被后续决定替代，但保留历史。

## D-001：RustDesk 深度二开架构继续保留

- 日期：2026-04-13 或更早；2026-07-12 回溯记录。
- 状态：`retrospective`。
- 决定：以 RustDesk 多平台 remote desktop core 为基础继续二开，而非重写协议/采集/输入栈。
- 证据：仓库结构、协议和根提交 `DaXianDesk`。
- 限制：导入前 upstream commit/完整历史未知。
- 影响：保留大量 RustDesk 命名、兼容路径、平台代码和 AGPL/第三方义务。
- Related ADR：ADR-0001（`retrospective`，不自动提升为 accepted）。

## D-002：产品品牌与 Android 身份迁移为 AShell

- 日期：2026-05-05—2026-05-13；2026-07-12 回溯记录。
- 状态：`accepted`。
- 决定：runtime/crate 使用 `AShell`/`ashell`，Android 使用 `云计划` 与 `me.ashell`。
- 影响：SO/DLL/deep link/package/build artifact 必须全链一致；上游注释和依赖名称不做盲目替换。
- Related ADR：ADR-0002。

## D-003：AShell 控制端会话强制 Relay

- 日期：2026-06；2026-07-12 回溯记录。
- 状态：`accepted`。
- 决定：`LoginConfigHandler` 固定 force relay，Android reconnect 明确 `forceRelay:true`，直接 IP/domain 入口拒绝。
- 影响：控制端不尝试 direct/UDP/IPv6 candidate。
- 边界：受控端 rendezvous/direct/NAT compatibility code 仍存在；不是全局移除 direct。
- Related ADR：ADR-0003。

## D-004：Android Core 与 Screen Share 分离

- 日期：2026-06；2026-07-12 回溯记录。
- 状态：`accepted`。
- 决定：core service/JNI/relay 生命周期不依赖 MediaProjection；projection loss 只丢 screen share。
- 不变量：hidden reconnect/boot/legacy path 不得弹授权；只有明确用户操作请求新 token。
- Related ADR：ADR-0004。

## D-005：Android active JNI 使用 `pkg2230`

- 日期：当前源码；2026-07-12 记录。
- 状态：`accepted`。
- 决定：`libs/scrap/src/android/mod.rs` 只导出 `pkg2230`。
- 影响：修改以 `pkg2230.rs` 为主，同时人工检查 `ffi.rs` compatibility drift；不得假设二者同步。
- Related ADR：ADR-0005。

## D-006：Android First-Frame 不自动切换回退源

- 日期：2026-06；2026-07-12 回溯记录。
- 状态：`accepted`。
- 决定：PC waiting/reconnect 可 normal video refresh，但不得自动启用 ignore/screenshot 或重绑 projection。
- 影响：任何真实 RGBA/Texture frame 到达都清 waiting。
- Related ADR：ADR-0006。

## D-007：Android ADB 是本地独立子系统

- 日期：2026-05；2026-07-12 回溯记录。
- 状态：`accepted`。
- 决定：packaged `libadb.so` 运行于 Android 本机，提供 wireless debugging pair/connect/local shell。
- 边界：PC remote ADB protocol 尚不存在；未来远程化必须新做 auth/audit/allowlist。
- Related ADR：ADR-0007。

## D-008：语音媒体迁移到 ZEGO

- 日期：2026-05-31—2026-06；2026-07-12 回溯记录。
- 状态：`accepted`。
- 决定：RustDesk peer channel 只处理 invitation state，音频媒体走 Flutter ZEGO SDK。
- 风险：token transport、client credential、auto-accept/consent 需优先整改。
- Related ADR：ADR-0008。

## D-009：Windows 当前 Virtual Display 采用 Amyuni

- 日期：当前源码；2026-07-12 记录。
- 状态：`accepted`。
- 决定：`src/virtual_display_manager.rs::IDD_IMPL` 选择 Amyuni。
- 边界：RustDesk IDD 分支和 `ashell_virtual_displays` key 仍保留但不是 active implementation。
- Related ADR：ADR-0009。

## D-010：GitHub Workflows 仅手动触发

- 日期：2026-06-25；2026-07-12 回溯记录。
- 状态：`accepted`，但属于治理债。
- 决定：现有 workflows 只保留 `workflow_dispatch`。
- 影响：无自动 PR/push quality gate；恢复 CI 必须先 harden permissions/actions/secrets。
- Related ADR：ADR-0010。

## D-011：建立 AI Engineering 新真相层

- 日期：2026-07-12。
- 状态：`accepted`，批准者：项目用户/owner。
- 决定：创建 `docs/AI_ENGINEERING/`、`.codex/` 和 `.agents/skills/`。
- 层级：源码第一；AI engineering docs 为当前架构真相；旧 `ENGINEERING_*` 保留历史；`.codex` 只做摘要/决定/任务。
- 限制：本轮不改业务代码、不删除旧文档、不编译、不提交、不发布。
- Related ADR：ADR-0011。

## D-012：建立 AI 任务状态机、权限矩阵与外部资产登记

- 日期：2026-07-12。
- 状态：`accepted`，批准者：项目用户/owner。
- Context：接管文档已建立，但长期维护仍需要唯一入口、逐任务确认门、语义化 Skill 路由和仓外资产责任边界。
- Decision：以 `PROJECT_START_HERE.md` 为唯一第一入口；以 `.codex/AI_RULES.md` 管行为权限；以 `AI_TASK_EXECUTION_PROTOCOL.md` 管 T0—T8；以 `DEVELOPMENT_WORKFLOW.md` 管团队 SDLC；以 `EXTERNAL_ASSET_REGISTRY.md` 管仓外服务、数据、driver、binary、build/sign/release 资产。
- Consequences/Risks：未来任务必须先分析和确认；Security/Release 在普通开发中为约束 reviewer；外部资产未闭环前不能宣称可复现或发布就绪。
- Verification：八个 Skill schema/metadata 静态验证；入口、规则和 registry 交叉引用检查。正式 build/runtime evidence 未执行。
- Rollback：文档可按用户批准恢复旧版，但不得通过删除历史文档完成；权限红线不能由下游 Skill 放宽。
- Approved by：项目用户/owner。
- Related task/docs：`T-2026-07-12-002`、`docs/AI_ENGINEERING/ASHELL_AI_ENGINEERING_STRENGTHENING_REPORT.md`。
- Related ADR：ADR-0012。

## D-013：AI 工程体系最终封版与安全 Superpowers 子集

- 日期：2026-07-12。
- 状态：`accepted`，批准者：项目用户/owner。
- Context：长期商业维护需要永久 ADR、可引用 baseline、统一 task/test evidence，以及不扩大权限的结构化推理能力。
- Decision：建立 `docs/ADR/`、`docs/BASELINE/`、`TASK_TEMPLATE.md` 和 `TEST_MATRIX.md`；采用本地 `ashell-superpowers-safe` adapter，只允许 brainstorming、planning、debugging、verification、review。
- Security boundary：不安装/执行外部 Superpowers；adapter 始终只读，不允许文件编辑、build/test、Git write、external/production access；commit、push、release hard-denied。
- Consequences：未来开发必须记录 Baseline ID、相关 ADR 与 TEST_MATRIX case IDs；accepted ADR 不授权 C2/C3；未执行验证保持 NOT_RUN/verification-required。
- Verification：V0 schema、metadata、link、fence、scope、sensitive-value 和 worktree checks；未执行项目 build/test。
- Rollback：通过 superseding ADR 调整治理；现有 ADR/baseline/history 不删除。扩大能力前需 owner + security/release review。
- Approved by：项目用户/owner。
- Related ADR：ADR-0000、ADR-0013。
- Related task/docs：`T-2026-07-12-003`、`docs/AI_ENGINEERING/ASHELL_AI_ENGINEERING_FINAL_SEAL_REPORT.md`。

## D-014：建立 Repository-Owned Global Session Memory

- 日期：2026-07-12。
- 状态：`accepted`，批准者：项目用户/owner。
- Context：任意数量的新 Codex 会话需要只依赖项目文件恢复当前状态、活动任务、AI 历史、修改事件和重大决定，同时不能依赖聊天记录或继承旧会话权限。
- Decision：建立 `.codex/PROJECT_STATE.md`、`CURRENT_WORK.md`、`CHANGELOG_AI.md`、`CHANGE_EVENT_LOG.md` 和 `SESSION_START_PROTOCOL.md`；在 `PROJECT_START_HERE.md` 固化八项全局记忆恢复顺序，在 Task Protocol T0/T5/T7/T8 固化恢复、事件和关闭同步。
- Responsibility Split：Project State 是可失效快照；Current Work 是 0..N 多会话 registry；AI Changelog 是 task-level index；Change Event Log 是 logical persisted-change ledger；Task History 继续保存详细授权/验证；Decision Log 只保存真实决定。
- Authority Boundary：memory 可恢复事实和历史，不能恢复或继承 C1/C2/C3。C0 只读任务不得为满足同步 checklist 自动写文件。
- Alternatives：依赖聊天/外部 memory 被拒绝，因为新会话不可保证取得；单一巨型 memory 文件被拒绝，因为状态、历史、决定和事件会相互覆盖并快速漂移。
- Consequences/Risks：增加受控双写和并发冲突风险；通过 Task ID、State/Registry Revision、append-only log、stale/conflict gate、写前重读和 source precedence 缓解。
- Verification：V0 structure/link/reference/sensitive/scope review and read-only session-recovery simulation；未执行项目 build/test。
- Rollback：通过后续获准 patch 或 superseding governance decision 调整；append-only history/event 不删除，旧授权仍不继承。
- Related ADR：ADR-0011、ADR-0012、ADR-0013；本次扩展既有 AI truth/task governance，不新增产品架构 ADR。
- Related Task：`T-2026-07-12-004`。

## D-015：迁移AShell产品身份并保留Flutter内部包名

- 日期：2026-10-12；状态：accepted；Approved by：当前用户明确请求及后续选项回复。
- Context：原产品身份需迁为A Shell You，仓库目录名不是运行产品身份。
- Decision：内部AShell/ashell；Android me.ashell，后台A Shell You、launcher一个壳子；PC A Shell You；跨层加载/打包/状态事件同步。Flutter内部package明确保留flutter_hbb，撤回其改名及关联导入。
- Compatibility：公共服务分类机制及上游/历史证据保留；用户追加授权移除旧前缀回退并改ZEGO字段ashellSessionId，客户端/内嵌服务源码同步，构建机和实际服务未操作。Android包与本地配置身份独立，不迁旧数据/权限；后续匹配名和文档统一见D-016。
- Verification：V0静态名称/路径/锁文件/生成声明核对；V1—V5 NOT_RUN，无编译成功保证。
- Consequences：正式环境须验证新包/库/快捷方式/服务与外部资产；Apple/Google注册及签名不能由字符串替换代办。
- Related：ADR-0014 supersedes ADR-0002；T-2026-10-12-002；CE-20261012-T002-01；迁移报告和观察基线。
- Rollback：未来明确授权后按源HEAD审阅恢复；本轮未回退Git、未操作已安装产品。

## D-016：完成所有文档与分类匹配的品牌名称统一

- 日期：2026-10-12；状态：accepted；批准者：当前用户明确指出剩余两处源码并要求文档字段全部换名。
- Decision：src/common.rs::is_public与src/hbbs_http/sync.rs::heartbeat_url使用ashell.匹配；全部文档中的品牌术语/代码示例/字段/路径统一，四份旧品牌文档更名，引用同步。Flutter内部package保留flutter_hbb。
- History：用户本轮明确授权覆盖旧文档品牌措辞；历史文本加入规范化说明，原文/真实源仓地址保留在Git a036f31；不伪造ashell源仓链接，不改提交hash、日期、验证结论或Git对象。
- Verification：V0全工作区文本/文件路径大小写不敏感扫描，排除.git；本地文档链接、两处谓词、Flutter包名/import及diff检查；未build/test/analyze/codegen/部署。
- Related：ADR-0014补充；T-2026-10-12-003 / CE-20261012-T003-01。该决定不授权外部域名配置或服务操作。

## D-017：更新仓内服务器默认配置

- 日期：2026-10-12；状态：accepted；批准者：当前用户明确提供替换值。
- Decision：现有IP/公钥引用按用户输入替换，相关服务端口顺序迁移至10083—10089；源码、.info、Token脚本与原有部署说明同步。保留配置覆盖优先级、协议和认证逻辑。
- Scope：本仓默认值；不替换相邻仓、构建机/CI秘密值、线上配置或用户持久化设置。新旧公钥配套与服务监听尚未验证。
- Verification：V0精确字面值差异、旧值残留及端口派生复核；没有构建/连接/部署验证。无新架构ADR，沿用Network/API边界。
- Related：T-2026-10-12-004 / CE-20261012-T004-01；Network/API工程文档。
- Rollback：未来明确授权后只反向恢复本任务patch；不自动重置仓库或服务。

### 下一决定模板

```text
## D-NNN：标题
- 日期：
- 状态：proposed/accepted/superseded
- Context：
- Decision：
- Alternatives：
- Consequences/Risks：
- Compatibility/Migration：
- Verification：
- Rollback：
- Approved by：
- Related task/commit/docs：
```
