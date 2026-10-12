# AShell Task History

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

最后更新：2026-10-12

> 本文件记录 AI 参与的工程任务、授权边界和验证结果。它不是产品 changelog，也不替代 Git history。

## T-2026-07-12-001：项目资产接管

- 状态：completed（repository-side asset takeover；external infrastructure 与正式构建仍待后续授权）。
- 请求者：项目 owner。
- 角色：AShell Principal Engineer。
- 授权范围：读取/分析源码和历史；创建文档、规则、memory、skills。
- 禁止范围：业务代码修改；删除；build/test；commit/push/merge/rebase；version bump；release/upload。
- 源码基线：`HEAD 77062b4`，分支 `main`。
- 初始工作树：clean。
- 执行内容：
  - 扫描 Rust workspace、Flutter、Android、Windows、network、HTTP/API。
  - 审计全部 39 个 tracked Markdown 与高信号源码注释。
  - 回溯本地 59 个 commits；确认导入前历史缺口。
  - 建立 `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md`—`11_ROADMAP.md`、审计/迁移/外部技能/接管报告。
  - 建立 `.codex` 长期 memory 和 `.agents/skills` 专属工程技能。
- 重要发现：
  - public repository/history credential exposure。
  - Android raw frame ownership、`static mut`、permission 和 ZEGO consent 风险。
  - transport/crypto/local storage 风险。
  - Windows privacy/injection/Amyuni 高权限面。
  - backend/hbbs/hbbr/DB 不在仓库。
  - ignored binary/driver assets 破坏 clean-clone reproducibility。
- 变更类型：仅文档、memory、skills 与入口规则；无业务源码修改。
- 验证：交付清单、Markdown code fence、活动源码路径、敏感字面值复制、Skill schema/metadata 与三类纸面 forward-test 全部通过；独立只读验收未发现剩余明确问题。未执行项目 build/test。
- 正式验证：见 `docs/AI_ENGINEERING/09_DEBUG_SYSTEM.md`《编译验证需求》。
- Git：未 stage、commit、push、merge 或 rebase。
- 删除/发布：无。

## T-2026-07-12-002：AI 工程体系强化

- 状态：completed（V0 repository-side governance strengthening；正式环境与外部资产仍待 owner 接管）。
- 请求者/批准者：项目 owner。
- 源码基线与 dirty state：`main` / `HEAD 77062b4`；开始时已有上一轮接管产生的文档、memory 和 Skill changes，均保留并在其上增量修改。
- 授权范围：创建入口、任务协议、开发流程、外部资产登记；完善 AI rules；审查并完善八个 AShell Skills；同步文档和长期记忆。
- 禁止/非目标：业务代码；build/test/analyze/codegen；Git 写入；删除/移动；版本、签名、打包、上传、部署、发布；credential 使用或验证。
- 方案与决定：建立入口 → 权限 → T0—T8 → domain truth/Skill → workflow → verification/memory 的固定链；外部资产使用统一 registry；见 `D-012`。
- 修改文件：根入口/流程/registry，新 Task Protocol 和强化报告，`.codex` rules/memory/logs，八个 Skills，以及 README/AGENTS/CLAUDE/工程索引中的入口指针。
- 安全/隐私/license：registry 不保存 secret、private endpoint 或 PII；记录 credential 类型与处置状态，不复述值；登记 external binary/driver、signing、AGPL/source-offer blockers。
- 验证：V0 静态检查；八个 Skill 通过官方 validator 规则和 metadata 检查；跨域 protobuf→Rust→Android→Flutter 前向案例正确触发 Master/Network/domain/Security/Release 路由及 C2/C3；入口/引用/资产分类复核。未执行项目 build/test。
- 《编译验证需求》：本轮没有业务或 build-system 行为变更，不申请编译；正式环境验证继续按 `09_DEBUG_SYSTEM.md` 管理。
- 回滚：仅通过用户批准的文档 patch 回退；不删除旧文档、不重写 Git history。
- Git/release actions：无。
- Related decisions/docs：`D-012`、`docs/AI_ENGINEERING/ASHELL_AI_ENGINEERING_STRENGTHENING_REPORT.md`。

## T-2026-07-12-003：AI 工程体系最终封版

- 状态：completed（repository-side governance seal；formal build/runtime/external infrastructure evidence remains open）。
- 请求者/批准者：项目 owner。
- 源码基线与 dirty state：`main` / `HEAD 77062b4`；开始时已有前两阶段的文档、memory、Skill changes，全部保留并增量扩展。
- 授权范围：创建 ADR、BASELINE、task template、test matrix；安全整合五项 Superpowers-style reasoning；更新入口、规则、memory 和报告。
- 禁止/非目标：业务代码；build/test/analyze/codegen；Git 写入；删除/移动；版本、签名、打包、上传、部署、发布；credential/production 操作；外部 Superpowers 安装。
- 方案与决定：见 D-013、ADR-0000 和 ADR-0013。
- 修改类型：仅 Markdown/YAML 文档、规则、memory 和 project Skill metadata；无业务源码。
- 主要交付：`docs/ADR/`、`docs/BASELINE/`、`TASK_TEMPLATE.md`、`TEST_MATRIX.md`、`SAFE_SUPERPOWERS_PROFILE.md`、`ashell-superpowers-safe`。
- 安全/隐私/license：Superpowers 只作官方只读参考，未安装或执行；adapter allowlist 仅 brainstorming/planning/debugging/verification/review，commit/push/release hard-denied；无 secret/PII 值进入新文档。
- 验证：V0 only；required files、ADR index/status、baseline anchors、Skill schema/metadata、Markdown fences/local links、sensitive patterns、delete/scope 和 diff whitespace checks。`ashell-superpowers-safe` 的 PC→Android Remote ADB 前向案例在同时收到实现、测试、commit、push、release 请求时仍保持 C0/T4，只输出 brainstorming/planning artifact，并在所有 mutation、project command、Git write、external/production 与 release 前停止。未执行项目 build/test。
- 正式验证：未来任务从 `TEST_MATRIX.md` 选 case 并按 `TASK_TEMPLATE.md` 输出《编译验证需求》；本次无业务/runtime 变化，不申请编译。
- 回滚：通过后续 patch/superseding ADR 调整；不删除 ADR、baseline 或历史记录。
- Git/release actions：无。
- Related decisions/docs：D-013、ADR-0000、ADR-0013、`docs/AI_ENGINEERING/ASHELL_AI_ENGINEERING_FINAL_SEAL_REPORT.md`。

## T-2026-07-12-004：AI 全局记忆增强

- 状态：completed（repository-owned session memory established；external/runtime/release blockers unchanged）。
- 请求者/批准者：项目 owner。
- 源码基线与 dirty state：`main` / `HEAD 77062b4` / `CS-BL-2026-07-12-77062b4`；开始时已有前三阶段未提交的文档、memory 和 Skill changes，全部保留。
- 授权范围：创建 `PROJECT_STATE`、`CURRENT_WORK`、`CHANGELOG_AI`、`CHANGE_EVENT_LOG`、`SESSION_START_PROTOCOL`；更新项目入口和 Task Protocol；同步必要的 Task Template、Decision/Task history。
- 禁止/非目标：业务代码；Git write；build/test/analyze/codegen；删除/移动；版本、签名、打包、上传、部署、发布；production/credential 操作。
- 方案与决定：五文件职责分离；Current Work 支持 0..N 并行 Task；全局记忆恢复与 T0—T8 任务激活分阶段；历史授权不继承；见 D-014。
- 修改文件：五个新 `.codex` memory 文件；`PROJECT_START_HERE.md`、`.codex/AI_RULES.md`、`AI_TASK_EXECUTION_PROTOCOL.md`、`TASK_TEMPLATE.md`、`DECISION_LOG.md`、`TASK_HISTORY.md`。
- 安全/隐私：状态与日志只保存 repository-relative path、Task/Event/ADR/Asset ID 和证据标签；不保存 secret、生产地址、设备标识、PII、绝对用户路径、prompt/transcript 或完整 diff。
- 验证：V0 required-file、8+8 order、protocol sync、Task/Event linkage、unique Event ID、Markdown fence/table/local-link、sensitive pattern、scope/delete 和 diff whitespace checks；独立无聊天上下文的新会话成功恢复 Baseline、State、Current Work、Decision、Skill、权限上限和下一停止门。首次演练发现并修复两个顺序歧义，回归结果 PASS。
- 《编译验证需求》：N/A；仅治理文档变更，无业务/runtime/build-system 行为变化，且本任务明确禁止编译。
- 回滚：通过后续获准的文档 patch / governance decision 修正；append-only Changelog/Event/Decision/Task history 不删除。
- Git/build/delete/version/release actions：none。
- Change Events：`CE-20260712-T004-01`—`CE-20260712-T004-06`。
- Related decisions/docs：D-014、ADR-0011、ADR-0012、ADR-0013、`.codex/SESSION_START_PROTOCOL.md`。

## T-2026-10-12-001：本轮接管记忆持久化

- 状态：completed / T8；工作区文件已保存，未进行远端同步。
- 请求者/批准者：当前用户；明确要求保存本轮记忆供工作区新对话使用。C1 仅覆盖记忆、报告及必要入口/状态/日志/基线文档。前序接管任务 C0 只读结果独立保留。
- T0 源码与现场：`test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890`，非 shallow、1条本地初始化提交；文档任务前 clean。新增观察基线 `CS-BL-2026-10-12-6fb0aa6`；不改旧 `CS-BL-2026-07-12-77062b4` 冻结内容。
- T1/T2 问题与证据：聊天结论不能替代可由新会话读取的项目文件；已有 D-014/session recovery 机制。把本轮源码/历史观察整理为短摘要和详细证据，明确来源、静态推断、未验证运行条件及覆盖缺口。
- T3/T4 范围：沿用既有恢复顺序，接入 AGENTS/Start Here，更新状态与当前资产观察。验收是文件存在、入口可追踪、引用有效、任务/事件/基线一致；不宣称已经在真实新聊天验证。
- T5 修改：13个 Markdown 文件，精确清单见 `CE-20261012-T001-01`；新增接管报告和观察基线，其余为入口、memory/state、资产登记与日志。无业务源码、manifest、lock、版本、生成文件或产品行为变化。
- 安全/隐私：只保存工程结论、源码相对路径和证据标签；不复制凭据值、生产地址、设备标识、PII、绝对用户路径或聊天原文。旧安全判断纠正为字面量事实与当前有效性未知，不试用凭据。
- T6 验证：V0 静态本地链接、源码基线、文档范围、入口顺序、Task/Event/State/Work 关联及 diff whitespace 检查；关闭后再次检查。V1—V5 NOT_RUN；未启动产品、设备或真实新聊天。
- 《编译验证需求》：N/A；本次仅保存文档，产品运行证据继续按报告与 TEST_MATRIX 保持待验证。
- T7 同步：PROJECT_MEMORY、PROJECT_STATE、CURRENT_WORK、CHANGELOG_AI、CHANGE_EVENT_LOG、TASK_HISTORY、工程概览、Baseline 索引及 External Registry。Decision Log/ADR 已评估 `N/A — no decision delta`，沿用 D-014；TEST_MATRIX 无本次产品测试结果可写入。
- T8 关闭：`STATE-20261012-002` / `WORK-20261012-002` / `CE-20261012-T001-01`；最后关闭当前活动行。后续任务重查 HEAD/status，不恢复旧授权。
- 回滚说明：如后续需要纠正，通过新的获准文档 patch 追加纠正记录；本次没有执行回退或删除历史。
- 未执行：业务修改、依赖安装、build/test/analyze/codegen、服务启动、Git stage/commit/push/fetch/pull/branch切换、部署/发布、云端上传、生产或凭据验证。
- 局限：文件只持久化在当前工作区，尚未进入 Git 提交或远端；新机器/新 worktree 必须包含这些文件。旧7月历史记录保留，不当作本轮实测结果。
- 交付：`docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md`、`docs/BASELINE/05_OBSERVED_BASELINE_2026-10-12.md`、`.codex/PROJECT_MEMORY.md` 及入口索引。

## T-2026-10-12-002：AShell产品身份迁移

- 状态：completed，source/V0交付；正式构建/运行验证NOT_RUN。
- 授权：当前用户明确要求全面替换AShell产品身份，包名me.ashell，内部ashell/AShell，后台/PC A Shell You、launcher一个壳子；禁止编译。中途用户明确要求保留Flutter内部包名flutter_hbb和其导入，已恢复。
- 初始现场：test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76，clean；该提交包含前轮记忆；本任务变更未提交。
- T0—T4：读取入口、规则、Task Protocol、基线/ADR及相关领域Skills；检查跨层身份/ABI/构建消费链。ADR-0014/D-015取代旧产品身份决定，技术内部命名与品牌分开。
- T5：源码/平台/构建配置和必要产品目录文件迁移；显示helper、Android launcher alias、native symbol和portable marker长度同步；对AShell兼容残留逐项登记。范围/路径见迁移报告与CE-20261012-T002-01。
- T6：30项静态名称、XML/JSON、路径、符号、lock/generated范围、版本和diff检查通过；不代表语义编译或运行成功。未执行编译/测试/analyzer/codegen/安装/服务/设备操作。
- T7/T8：同步当前工程文档、记忆、状态、D-015/ADR-0014、新观察基线、事件及历史；最终STATE-20261012-003 / WORK-20261012-004。旧历史/上游许可/外部契约保留，不伪造外部注册。
- 编译验证需求：迁移报告第5节；Android Linux正式入口和Windows new-build.cmd均未执行。jniLibs/ADB、Windows native、ZEGO lock和Apple/Google注册为限制。
- 回滚：未来获准后相对源HEAD逐项反向审阅patch；新包旧包数据独立，本轮没有设备数据迁移/删除、Git写入或发布。
- 交付：docs/AI_ENGINEERING/ASHELL_IDENTITY_MIGRATION_2026-10-12.md；docs/tasks/T-2026-10-12-002-ashell-identity.md；docs/BASELINE/06_ASHELL_IDENTITY_BASELINE_2026-10-12.md。

- 用户追加范围/CE-20261012-T002-02：移除旧前缀回退，只用ASHELL_*；ZEGO字段ashellSessionId两端同步。当时还有两处公共服务匹配文字保留，历史文档暂未统一；后续由T003/D-016明确授权完成。构建机signing.env和实际服务部署未执行，正式启用须同步更新。

## T-2026-10-12-003：全仓品牌文字与文档字段补齐

- 状态：completed / T8，V0 only；当前用户指出两处源码旧匹配并明确要求文档字段全部换名。
- 初始：test/a036f31，已有T002未提交变更；本轮仅两处Rust字符串与文档名称/字段/引用/记忆追加，不改Flutter内部包名。
- 实施：ashell.分类匹配；全工作区品牌词大小写统一；四份Markdown物理更名。历史文档名称规范化注记保留事实边界，真实源仓地址/旧原文从Git恢复，未修改Git对象。
- 验证：全文与文件路径检索排除.git零旧词命中；文档链接、源码谓词、Flutter包名/import和diff whitespace通过。没有编译/测试/analyzer/codegen/依赖安装/服务/设备/外部请求。
- 交付：迁移报告和docs/tasks/T-2026-10-12-003-brand-normalization.md；D-016、CE-20261012-T003-01、STATE-20261012-004 / WORK-20261012-006。
- 后续：正式API/heartbeat行为与完整构建仍NOT_RUN；当前任务权限交付关闭，不能从文档恢复执行授权。

### 后续任务规则

每个未来任务必须记录：

- request/owner/explicit authority。
- source baseline 与 initial dirty state。
- problem statement 和 affected domains。
- assumptions 与 out-of-scope。
- files changed，不覆盖用户已有改动。
- security/privacy/license impact。
- validation performed 与未执行项。
- build requirement/result。
- rollback。
- decision log references。
- Git/release 动作只有明确批准后记录。

## T-2026-10-12-004：服务器默认配置迁移

- 状态：completed / T8 / V0；请求者/批准者：当前用户明确提供替换值。
- 基线：test/f423335，initial clean且与origin/test一致；AS-BL-2026-10-12-a036f31-worktree经该提交固化，本轮追加配置delta。
- 范围：12份含旧值的源码/配置/脚本/文档同步；工程Network/API、外部资产登记与会话记忆更新。完整25文件清单见CE-20261012-T004-01。
- 安全/兼容：只替换指定默认公钥，不动密码或其他凭据；配置覆盖仍有效，旧客户端保存值及外部服务配套未迁移。未改变协议、账号认证、Flutter内部包或license。
- 验证：12份配置差异精确匹配预期字面替换；旧IP/端口/Key零残留（排除.git）、Key解码32字节、端口派生与diff检查；没有编译/运行成功结论。
- 《编译验证需求》：仅登记在任务文件，由正式构建环境后续明确授权执行；NET-01/02、API-04/05/08未运行。
- Git/release actions：本任务none；前序品牌提交和推送已完成，不继承授权。回滚只在未来明确授权后反向应用本任务patch。
- Decision/Event/State：D-017 / CE-20261012-T004-01 / STATE-20261012-005；详见docs/tasks/T-2026-10-12-004-server-configuration.md。

## T-2026-10-12-005：仅保留build.cmd并打包编译产物

- 状态：completed / T8 / V0；请求者/批准者：当前用户，明确授权旧入口删除、新入口迁名与优化。
- 基线：test/c0951b5，initial clean；新观察基线AS-BL-2026-10-12-c0951b5-pc-build-worktree。旧脚本实现从该HEAD可恢复。
- 修改：保留新环境编译配置，先清理受限Release输出后编译，直接压缩Release；移除额外驱动/DLL和根目录暂存装配，Brotli提前检查不自动安装。generator修复cwd/范围/失败传播；manifest和最终产物检查保留并加强。
- 安全/兼容：保留已编译Flutter/native依赖和产品身份；拒绝包外链接及清理路径中的reparse point；不因此保证依赖外部DLL/驱动的功能可用。不改变协议、运行代码、依赖版本或license。
- 验证：仅AST/PowerShell语法解析、CMD标签、打包输入、依赖边界和diff等V0；未执行脚本/单元测试/构建/实际打包。用户提供的旧日志不能作为T005构建通过证据。
- 实际Git/发布：none。实际删除/迁移：按用户请求替换旧build.cmd并移除new-build.cmd路径；未删除本机生成资产。
- 文档/记忆：工程构建/Windows/security/source map、当前入口/Skill、验证矩阵、外部资产、新baseline和恢复链同步；历史文档旧入口名称仍是历史证据。
- 编译验证需求：见任务文件；Windows正式环境build.cmd，验证清单/解压/启动/缺驱动与失败退出；全部NOT_RUN。
- Decision/Event：D-018 / CE-20261012-T005-01；任务docs/tasks/T-2026-10-12-005-pc-build-entry.md。

## T-2026-10-12-006：用户PNG统一产品图标

- 状态：completed / T8 / V0；批准者：当前用户，明确上传原图并要求保持设计、转换替换，禁止编译。
- 基线：test/c9b7cd135be838989953e7ab7037f8c6d8d6aa31，initial clean；引用AS-BL-2026-10-12-c0951b5-pc-build-worktree并记录T005已提交的drift。
- 修改：21图像（3个9尺寸ICO、3个源图/UI PNG、15个Android PNG）和loadIcon同源PNG回退、定向gitignore例外；9份工程文档/恢复记忆。精确清单在task；没有删除/移动。
- 原图：实际1254×1254 RGBA，转换为需要尺寸，保留原始设计、透明度及细节；原用户文件SHA不变。不执行重绘或自动描摹。
- 安全/兼容：没有新增网络、权限、FFI、安装器逻辑或依赖；继承既有Androidmonochrome复用前景，主题着色待设备确认。旧SVG保留但loadIcon不再使用；Flutter内部package不变。
- 验证：18PNG尺寸/alpha、3ICO全部27帧独立解码、Android内容范围、源图未变、引用/ignore、图片静态预览、diff和文件范围。System.Drawing的256多帧选择与独立帧解码差别已如实记录，不当作应用/系统显示测试。
- 正式验证：WIN-08、AND-08、FLT-02/07 NOT_RUN；《编译验证需求》登记于task，仅供未来明确授权的正式Windows/Linux环境。
- 记忆/Decision：更新恢复入口和状态；N/A — no decision delta，沿用ADR-0014；CE-20261012-T006-01 / STATE-20261012-007 / WORK-20261012-012。
- Git / build/test/analyze/project-codegen / dependency install / actual packaging / signing / version / external / release：none；未提交。
- 回滚：未来获准后按c9b7cd1逐文件反向恢复，本次不执行Git恢复/删除。交付docs/tasks/T-2026-10-12-006-product-icons.md。

## T-2026-10-12-007：图标覆盖复核与遗漏补齐

- 状态：completed / T8 / V0；批准者：当前用户，要求继续检查Windows/Android产品图标，发现遗漏继续生成替换，不编译。
- 基线：test/c9b7cd135be838989953e7ab7037f8c6d8d6aa31；T006的32文件已dirty。沿用AS-BL-2026-10-12-c0951b5-pc-build-worktree，不改写旧基线。
- 发现/修改：Android两处通知共用旧ic_stat_logo、默认悬浮旧vector，主题复用白卡片导致轮廓失真；Windows旧Sciter硬编码旧图、portable窗口无图标、卸载快捷方式强制系统图标。按同一原图补齐12图片、3 Rust、3 XML、1 ignore及9文档，共28文件；合并51文件未提交。
- 覆盖：主程序/服务/portable-service同EXE，托盘PNG/ICO、程序列表/文件关联、MSI AppIcon与快捷方式、旧UI及自解压窗口均追踪。Android Manifest应用/服务继承、桌面彩色/主题、前台通知/状态栏和默认悬浮图已核对。
- 安全/兼容：新增Win32 WM_SETICON仅针对本对象窗口，HICON由BasicApp持有，窗口先销毁。无依赖/权限/协议变化；系统RuntimeBroker和服务管理器通用图标不改，用户customSvg仍可覆盖。Mac/Linux/web专有资源保留。
- 验证：Pillow独立读取12PNG、16XML解析、alpha/单色/眼睛间隙和尺寸、include/资源/ignore引用、预览、T006图片与Flutter代码及原图hash不变、diff/范围/文档链接。正式WIN-08/AND-08/FLT-02/07/RST-01 NOT_RUN。
- 《编译验证需求》：已登记task，由未来明确授权的正式Windows/Linux环境执行；本地NWG源码/portable lock缺失，上游API检查不代替编译。实际Shell、第三方多窗口插件、OEM主题/图标缓存均待验证。
- Git/build/test/analyze/project-codegen/install/run/actual packaging/delete/move/version/sign/external/release：none。回滚仅未来获准后反向T007 patch，不覆盖T006。
- 文档/Decision：当前恢复链已同步；N/A — no decision delta，沿用ADR-0014；CE-20261012-T007-01 / STATE-20261012-008 / WORK-20261012-014。详见docs/tasks/T-2026-10-12-007-icon-coverage.md。

## T-2026-10-12-008：Android资源合并失败诊断与脚本修正

- 状态：completed — scoped source/V0，正式构建结果待验证；用户报告失败并确认原目录更新源码后增量构建。
- Baseline：main/fb5c393fe1e29a2cf805e75847820b315c468b3d，initial clean；此前图标commit/push/test→main已完成。本轮只改脚本与10文档，不继承Git/build/清理权限。
- 诊断：AGP7.3.1及merged-not-compiled-resources的AppCompat文件source-set异常与官方206674992同类问题相符，但缺少stacktrace/缓存和复现，暂为inferred；图片格式损坏无当前证据。
- 已确认：build.sh通过apk_in="$(...)"捕获完整构建标题/日志，随后require_file把多行文本当路径；命令替换内依赖set -e可能在失败后继续选择旧APK。两函数与调用者已显式分离日志/路径并传播错误码。
- 兼容/安全：无toolchain/Gradle/AGP/图标/权限/签名/依赖修改，无自动删除缓存。stderr保持日志可见，不暴露额外签名信息。回滚仅未来获准后反向本任务patch，保留图标提交。
- 验证：bash -n只解析、XML解析、静态失败流程/diff/范围/链接。未执行shell函数测试或构建，AND-08等正式证据NOT_RUN。
- 后续验证：构建机同步脚本，在flutter目录clean后回根运行build.sh 1或2；仍失败时提供首个Caused by及stacktrace。完整命令/范围见任务文件。
- 记忆：08_BUILD_SYSTEM、09_DEBUG_SYSTEM、恢复入口及ledger同步；N/A — no decision delta，沿用ADR-0014；CE-20261012-T008-01 / STATE-20261012-009 / WORK-20261012-016。
- Git/compile/test/analyze/codegen/clean/delete/move/install/sign/deploy/release：none。详见docs/tasks/T-2026-10-12-008-android-resource-build.md。

## 新任务模板

```text
## T-YYYY-MM-DD-NNN：标题
- 状态：planned/in-progress/blocked/completed
- 请求者/批准者：
- 源码基线与 dirty state：
- 授权范围：
- 禁止/非目标：
- 问题与证据：
- 方案与决定：
- 修改文件：
- 安全/隐私/license：
- 验证：
- 《编译验证需求》或正式结果：
- 回滚：
- Git/release actions：
- Related decisions/docs：
```

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
