# AShell Task History

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

最后更新：2026-10-18

> 本文件记录 AI 参与的工程任务、授权边界和验证结果。它不是产品 changelog，也不替代 Git history。

## T-2026-10-18-008：ADB性能、黑屏、命令与弹窗

- 用户授权检查修复五项ADB问题后推送test并编排；main/e3b4454 clean，test快进基线。完成独立截图常驻压缩/回退、节点刷新缓存、独立视频帧率、黑屏图层、命令协议及诊断、弹窗位移局部更新。原共享/无障碍/输入实现保持。
- V0完成；Android11—15设备/流畅度/黑屏及255具体失败命令仍待验证，无本地构建或设备操作。最小合同/记忆同步，Decision/ADR/资产无新增。按授权test提交推送、单次Both/常规编译、不监控；准确回执见交付。STATE/WORK-20261018-008 / CE-20261018-T008-01，[任务](../docs/tasks/T-2026-10-18-008-adb-runtime-performance.md)。

## T-2026-10-18-007：ADB 功能合并主分支

- 用户授权 test 合并 main、提交说明“ADB功能问题完善”、推送后单次构建；沿用 Both/常规编译、不监控。fetch 后确认 main/origin/main 均是 test/82e0f7f 的祖先，显式合并无冲突。
- 本轮只合并既有变更与最小记录，源码树与 test 一致；V0 检查通过，构建/设备待证。准确 SHA/dispatch 见交付；STATE/WORK-20261018-007 / CE-20261018-T007-01，[任务](../docs/tasks/T-2026-10-18-007-merge-adb-to-main.md)。

## T-2026-10-18-006 r4：提交修复并编排

- 用户授权交付 r3 到 test，推送后单次 Build-Clients-New Windows/常规编译，不监控。源码与六份任务/记忆文件共七路径，执行前核对通过；Git/dispatch 回执见交付，不代表构建通过。STATE/WORK-20261018-006-r4 / CE-20261018-T006-04。

## T-2026-10-18-006 r3：Windows E0616

- Complete/T8（源码与 V0）：用户报告本次 Windows 构建失败，日志 checkout 对应 test/de12290；修复私有字段访问，沿用原剪贴板使用的公开会话信息。无原无障碍/媒体/输入链变更。
- 已检查访问级别、类型、cfg、锁生命周期与 diff；未重新构建/提交/推送/编排。STATE/WORK-20261018-006-r3 / CE-20261018-T006-03；[任务](../docs/tasks/T-2026-10-18-006-adb-clipboard-sync.md)。

## T-2026-10-18-006 r2：提交推送与编排

- 用户追加 C3 授权：交付既有 T004/T005/T006 到 test，单次 Build-Clients-New Both/常规编译，不监控。无本地产品构建/测试或真机操作。
- 执行前分支/remote/diff 检查通过；准确 SHA、远端一致性和 dispatch 回执见 Git/会话交付，不提升 V0 验证等级。STATE/WORK-20261018-006-r2 / CE-20261018-T006-02。

## T-2026-10-18-006：ADB 剪贴板同步

- Complete/T8（实现与V0）：新增仅ADB剪贴板文本分支，原监听/无障碍/模拟输入保留；只更新剪贴板、不自动输入，Ctrl+V独立优先，能力协商及权限/生命周期取消。
- V1—V3 NOT_RUN；T004/T005 dirty保留，无Git/CI操作，无Decision/ADR/资产增量。
- [任务](../docs/tasks/T-2026-10-18-006-adb-clipboard-sync.md)，STATE/WORK-20261018-006 / CE-20261018-T006-01。

## T-2026-10-18-005：ADB 普通键盘输入

- Complete/T8（实现与 V0）：保留 T004 dirty，新增独立 ADB 键盘模块、原 KeyEvent 前置分流、JNI/混淆签名与 Flutter gate；普通键不写剪贴板，Ctrl+V 保持，修饰键按 PC 隔离。
- V1—V3 NOT_RUN；未提交/推送/编排，无 Decision/ADR/资产增量。
- [任务](../docs/tasks/T-2026-10-18-005-adb-keyboard.md)，STATE/WORK-20261018-005 / CE-20261018-T005-01。

## T-2026-10-18-004：光标与无障碍粘贴

- Complete/T8（实现与 V0）：test/738a3fe clean，用户授权按钮文案、禁用光标与无障碍 Ctrl+V 修复。光标与触摸同源权限；文本经既有消息单次输入到当前焦点，ADB 优先与权限检查保持。
- V0，V2/V3 待证；当前改动未提交、推送或编排。最小合同及任务记忆同步，无 Decision/ADR/资产增量。
- [任务](../docs/tasks/T-2026-10-18-004-accessibility-paste-cursor.md)，STATE/WORK-20261018-004 / CE-20261018-T004-01。

## T-2026-10-18-003：静默常亮与默认值

- Complete/T8（r2 续接）：test/9e28a90 clean 起点；用户追加要求不显示齿轮/设置入口，已删除本任务新增页面与路由，主页常亮控件保持隐藏。未配置/未知值默认 service-on 静默生效，保留用户已保存选项。相对 HEAD 仅三份 Flutter/Kotlin 源码与最小合同/记忆更新，无原功能链修改。
- V0 完成，未运行本地产品 build/test/analyze/codegen/真机。r3 用户授权提交推送 test 并单次 Build-Clients-New Both/常规编译，不监控；准确 Git/dispatch 回执见交付，正式验证清单见[任务](../docs/tasks/T-2026-10-18-003-keep-screen-on-settings.md)。
- STATE/WORK-20261018-003-r3 / CE-20261018-T003-03；Decision/ADR/资产无增量。

## T-2026-10-18-002：ADB 辅助保活与点亮屏幕

- Complete/T8：test/0b5ad99 clean；ADB 连接后设置当前应用省电豁免，保留已有豁免，回读系统状态、不伪称免杀；独立有界命令通道与取消保持。Shell 栏新增唤醒键224，原无障碍与媒体传输不改。
- V0；V2/V3 未运行。授权提交推送 test 并单次 Both/常规编译，不监控；实际 Git/dispatch 回执见最终交付。
- [任务](../docs/tasks/T-2026-10-18-002-adb-keep-alive-and-wakeup.md)，STATE/WORK-20261018-002、CE-20261018-T002-01；Decision/ADR/资产无增量。

## T-2026-10-18-001：主页常亮与 ADB 输入弹窗

- Complete/T8：test/00193fa clean 起点；主页开放三档常亮，以真实授权远控连接支持多 PC/ADB-only，移除 service-on 强制覆盖。共用窗口策略保留系统息屏设置，后台需悬浮权限。
- ADB 已有远程输入复用 paste，自动焦点、回车/确定、Shift+Enter，普通/ADB 画面 Ctrl+V 保持。未改普通采集/无障碍/输入传输/生成文件/依赖。
- V0 检查，V2/V3 待证；用户授权提交推送 test 并单次 Both/常规编译，不监控；实际 Git/dispatch 接受回执见交付。
- [任务](../docs/tasks/T-2026-10-18-001-keep-screen-on-and-adb-input.md)，STATE/WORK-20261018-001、CE-20261018-T001-01；无 Decision/ADR/资产变化。

## T-2026-10-17-003：ADB Ctrl+V与画面参数对齐

- Complete/T8：test/8ef006e clean起点，实现直接快捷粘贴与ADB焦点；取消媒体960限制、JPEG最多2秒额外等待，目标采集300ms、截图在途4帧。保留普通链路及两端控制边界。
- V0复检；V2/V3仍待证。用户授权提交推送test及单次test Both/常规编译，禁止监控。实际commit/dispatch接受结果见交付。
- [任务](../docs/tasks/T-2026-10-17-003-adb-paste-shortcut.md)，STATE/WORK-20261017-003、CE-20261017-T003-01；无Decision/ADR/资产增量。

## T-2026-10-17-002：test ADB远程输入与媒体性能

- Complete/T8：实现/V0完成，起点main/af1c72d clean，创建并切换test。新增菜单粘贴、原生二进制帧交接、24帧回执窗口、960媒体最长边、原始截图与PNG回退；普通链路保持。
- 授权提交推送test、Build-Clients-New Both/常规编译单次dispatch，不监控；无本地产品编译/测试/设备操作。构建/流畅度尚待正式验证。
- [任务](../docs/tasks/T-2026-10-17-002-adb-input-performance.md)，STATE/WORK-20261017-002 / CE-20261017-T002-01；Decision/ADR/资产无增量，Git/dispatch结果见交付。

## T-2026-10-17-001：构建模式名称调整

- Result：实现/V0完成；将新工作流“完整编译”选项及九处判断改为“原始编译”，其他工作流文本不变。
- Authority：用户明确要求修改并提交，本次不执行；仅本地git add/commit，无push/dispatch/build/test。
- Source：main/130f748，初始clean；Skill：ashell-release-engineer。
- Verification：十处精确替换、模式匹配和diff静态检查通过；未执行产品或CI。
- Records：[任务](../docs/tasks/T-2026-10-17-001-build-mode-label.md)，CE-20261017-T001-01 / STATE-20261017-001 / WORK-20261017-001；N/A — no decision delta。
- Git结果以包含本记录的提交及会话交付为准；发布/构建通过状态不提升。

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

## T-2026-10-13-001：独立 Android/Windows Actions

- 日期：2026-10-13；状态：completed — configuration/V0，runner V2 未执行。
- Source：main/8662f2369a33f5286c20f9de4d640d2072175ca7；initial clean。T009 与当前 HEAD 有可解释漂移，新 local ADB 保留。
- Authority：当前用户明确授权工作流新增/旧编排删除、平台与 ABI 选择、默认测试签名和仅 Actions 下载；没有继承 Git/build/发布权限。
- Outcome：一个独立手动入口；native Android matrix + APK assembly；Windows build.py + portable generator；14 天成品下载/hash/provenance。删除 12 个旧 workflow 和失效 Dependabot，解除 build.sh/F-Droid 元数据依赖。
- Verification：V0 YAML/输入/矩阵/9个直接 Action SHA、11段 Bash/1段Python AST/6段PowerShell、两份本地 shell 仅语法、diff/引用/范围核对；未实际运行产品或 CI。
- Limits：临时 Android 测试证书跨运行不一致；Windows unsigned 且无外部 driver/helper；Pub lock/依赖可用性与 hosted runner 兼容需首次执行验证。
- Documentation：使用说明、build/debug/security/source map、ADR/baseline/registry/test matrix、入口/Release skill/全局记忆同步。
- Decision/Event：D-020 / ADR-0016 / CE-20261013-T001-01；STATE-20261013-001；关闭 WORK-20261013-002。
- Git/build/sign/package/upload/release：none；仅授权旧编排删除。恢复点 8662f23；[具体任务与编译验证需求](../docs/tasks/T-2026-10-13-001-client-actions.md)。

## T-2026-10-13-002：Android Actions SDK setup

- 日期：2026-10-13；source main/c521601，initial clean；owner Codex / Release，Security 约束复核。
- T001 后续用户明确“提交github”，已 commit/push c521601 到 origin/main；旧记录保留其提交前时点。
- 用户报告 sdkmanager command not found / 127，native/APK 源码均缺 SDK 初始化；补固定 setup-android SHA / CLI 16.0、显式 platform-tools、路径/版本诊断和 SDK root。
- V0：YAML、11 Bash、1 inline Python AST、两个 job 的初始化顺序、固定 action SHA、权限/选择/矩阵/Windows job 不变、diff/链接。修复后 V2 NOT_RUN。
- 同一会话既有 Git 请求覆盖本次修复提交/推送；本记录提交前写入，最终 Git 状态见回执。没有 dispatch、产品构建、依赖安装、生产签名/发布或文件删除。
- Decision/Event：D-020 / ADR-0016 reviewed-no-change；CE-20261013-T002-01；状态与记忆/构建说明/registry/matrix/baseline10 同步。
- [详细任务](../docs/tasks/T-2026-10-13-002-android-sdk-setup.md)：修复推送后新建 Run workflow 验证，旧 Re-run 保留旧 SHA。

## T-2026-10-13-003：独立本机 ADB 文档同步

- 日期：2026-10-13；状态：complete / T8 / V0（文档交付）；source main/f84a78f，initial clean。
- Authority：用户当前明确同步整理ADB文档；此前8662f23代码实现与Git交付已完成，本轮仅C1，不自动Git/构建/设备操作。
- Outcome：新增12_LOCAL_ADB当前说明、ADR-0017、Baseline11和任务；修正主页/新模块/旧组件退役的混淆，登记四项新依赖、AND-09—13，更新相关领域/入口/Skills/全局记忆。原ADB档案正文与冻结基线保留。
- Verification：V0源码合同/常量、8662f23模块对比、变更范围、diff/新增链接/新文件空白检查；上游许可来源只读核对。未收到APK/编译日志/真机结果，V2/V3仍NOT_RUN。
- Boundary：无PC ADB或ADB投屏；原产品代码、无障碍、画面/远控链不改。加密身份/配对/连接/取消等文档陈述不等于运行验收。
- Decision / State / Event：D-021 / ADR-0017；STATE-20261013-003 / WORK-20261013-006；CE-20261013-T003-01。
- Final：34个Markdown文件（4新增、30修改）留工作树；无stage/commit/push、build/test/analyze/codegen、依赖安装、设备、删除、签名或发布。
- Detail：[任务记录](../docs/tasks/T-2026-10-13-003-local-adb-docs.md)；后续按正式环境结果补证据，PC ADB/投屏另行设计。

## T-2026-10-13-004：PC 独立远程 ADB

- Date / state：2026-10-13 / T8 repository implementation complete；运行验收待验证。
- Authority：当前六项用户指令授权模块化PC ADB及必要协议/独立JNI/Android共享状态；C1事实同步；无本轮C3。
- Source：main/917e6612cdfd819b824626564478a3ff1d84eec4，initial clean；本轮修改未提交。
- Outcome：PC菜单/弹窗、专用Misc字段40、既有FFI ABI新key、独立JNI、Android进程manager/有限队列/记录/NSD/撤销；本机页面与所有PC共用身份和状态。
- Boundary：原产品信息/无障碍/输入/画面链处理保持，无ADB投屏；原会话认证/加密不改。ADB请求需控制权限，结果只给origin；App撤销不假报系统列表已清。
- Verification：V0接口/所有权/取消/字段/保护路径diff/链接和空白检查；V2—V4 NOT_RUN。静态修正顶栏自动隐藏导致controller销毁、NSD丢发现、取消清理竞态和等待表回收。
- Decision / Baseline / Event：D-022 / ADR-0018 / Baseline12 / CE-20261013-T004-01。
- Actions：无stage/commit/push、产品build/test/analyze/codegen、依赖更新、设备、版本/签名/发布或文件删除。
- Detail：[T004](../docs/tasks/T-2026-10-13-004-remote-adb.md)、[接口与验收](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)；下一步正式两端构建/多PC结果回填。

## T-2026-10-13-005：ADB controller 缺少 bind 导入

- Date / State：2026-10-13 / T8源码修复完成，正式重编待验证。
- Source / Authority：main/de8bc768f0a71cab3f60e93f321ae0f65d74c658、initial clean；用户反馈Android CI失败，C2针对性修复/C1记录，无本轮C3。
- Evidence：用户job日志三处getter bind未定义，compileFlutterBuildRelease失败；common.dart未export platform_model.dart的getter。
- Outcome：controller增加一行直接import；核对同类调用/SessionID/FRB合同，原链/CI/依赖无改动。
- Verification：V0检查完成；用户证据为修复前FAIL，修复后V1/V2/V3/V4 NOT_RUN。
- Records：CE-20261013-T005-01 / STATE-20261013-005 / WORK-20261013-010；Baseline12引用当前de8bc76固化状态，D-022/ADR-0018 reviewed-no-change。
- Actions：无Git写入、build/test/analyze/codegen、设备、dispatch/发布；临时签名日志URL未落仓。
- Detail：[T005](../docs/tasks/T-2026-10-13-005-adb-bind-import.md)，提交修复后需新SHA构建，重跑旧commit不带修复。

## T-2026-10-13-006 — ADB 可用性与智能配对

- 用户请求：PC统一ADB菜单图标、修复灰色控件和状态不同步、配对/命令等独立窗口且可拖动；手机自动发现配对/连接端口、只填码或手动覆盖、一键pair-connect；可研究LADB/aShellYou；实现后提交推送并单次运行Build AShell clients，不监控。
- Source：main/504adea initial clean；T005导入已提交；远端main只读核对同SHA。Master和各域静态自审，无子agent。
- 实现：ADB从mobile keyboard gate解耦，保留authorized普通会话及PC只读限制；双NSD/local-only/串行解析/有界取消；pairConnect和QR指定本机服务；共享状态与阶段；同款toolbar和独立拖动弹窗、手机UI。
- 验证：V0调用/权限/参数/owner/超时/NSD/窗口生命周期与git diff --check；新文档相对链接有效；原Kotlin/Accessibility/libs/scrap/protobuf字段/依赖/构建脚本未变。无本地Flutter/Dart/Java工具链，未运行本地build/test/analyze/codegen或设备测试。
- 文档：domain12/13、安全/构建、ADR0019/D023、Baseline13、TEST_MATRIX ADB-R08—11、外部来源与memory齐备。No delete/version/release/production；原capture/input链保持。
- 提交前T8记录：源码与记录完成；stage/commit/push及GitHub both/aarch64 dispatch已明确授权，将紧接执行；准确SHA与接受回执以本会话最终交付为准，不能把计划记成完成或构建PASS。按用户要求不监控。
- STATE-20261013-006 / WORK-20261013-012 / CE-20261013-T006-01；详见docs/tasks/T-2026-10-13-006-adb-usability.md。

## T-2026-10-13-007 — ADB 后续代码事实与文档同步

- 2026-10-13，观察main/efb2599，初始dirty为PC controller/menu及Android local_adb_page；均为本会话先前已获准实现，文档曾按用户要求延后。
- 历史交付：feac956固化T006；efb2599移除QR/未知参数入口、修正发现及取消/状态请求，已推送，both/aarch64 dispatch获HTTP204，按要求未监控。没有新包成功证据。
- 本轮仅Markdown：文档对齐无QR、PC显式刷新、两端模式显示、PC连接按钮主题色，以及当前请求/超时/取消合同；三份源码hash保护。ADR0020/D024/Baseline14、R12—15与记忆同步，R10退役N/A。
- 当前用户追加明确授权提交所有待交付代码/文档、推送GitHub main、单次Build AShell clients both/aarch64，不监控。本文为执行前记录，准确新SHA与接受回执由最终交付确认。
- V0源码对照/空白/本地链接/hash；无本地build/test/analyze/codegen、设备、删除/版本/发布/生产操作；原产品链不变。
- STATE-20261013-007 / WORK-20261013-014 / CE-20261013-T007-01；详见[任务记录](../docs/tasks/T-2026-10-13-007-adb-docs-sync.md)。

## T-2026-10-13-008 — Shell栏与投屏后续交接文档同步

- 2026-10-13（Asia/Tokyo），main/aca7446，初始clean；980f254已固化T007改动，aca7446提交八份Dart侧栏代码，旧任务快照保留。
- 用户授权C1同步当前文档；稍后集成ADB投屏传输。本次只改Markdown，不开始投屏或修改现有源码、不执行Git/构建/CI/设备操作。
- 文档覆盖Shell栏自动显示/手动关闭与左侧顶栏开关、三键ADB调用、session state/epoch/teardown、多PC快照/无周期查询，原无障碍栏仅标题与布局。修正当前入口的未提交及无ADB输入等旧说法。
- 新增Baseline15、D025、R16—19（NOT_RUN），ADR0020补充UI证据；路线图记录投屏采集/媒体传输/PC解码、所有权/多PC、隔离和兼容待设计，不预选组件。
- V0检查：源码对照、Markdown引用/空白、仅文档范围、八份Dart SHA-256保护；正式构建/真机结果未取得，不提升为V2/V3/V4通过。
- STATE008 / WORK016 / CE-20261013-T008-01；详见[任务记录](../docs/tasks/T-2026-10-13-008-adb-sidebar-docs-sync.md)。文档待提交，下一任务先重新核对HEAD/status。

## T-2026-10-14-001 — scrcpy 投屏与 ADB 优先触摸

- Source：main/a1486ee2ad2bca991b272a8c7780d2daf2f7c526，初始clean；T008文档已固化。用户当前授权代码集成和追加触摸优先级；无Git/构建/CI授权。
- 实现：Android独立video/control服务、固定官方server资产、独立JNI/字段41、按PC流控、独立PC解码/画面；Shell栏开/关投屏，多PC全局停止。原共享与截图也通过最小输入分流使用ADB，无ADB才回到Accessibility；关闭视频保留ADB触摸。
- 生命周期：generation/token隔离陈旧帧/页面，旋转重置解码，停止和断线清理；多PC手势互斥、只读/离开/超时释放，有界视频/输入队列。原MediaProjection、采集JNI、正常VideoFrame、构建入口/依赖版本未改。
- 文档：14_ADB_MIRROR、ADR0021/D026、Baseline16、外部资产及ADB-M01—M08矩阵；当前工程入口与记忆同步，旧记录保留历史。
- V0：源码/JNI/R8/协议producer-consumer和字段编号检查、官方asset hash/ZIP结构、diff范围/空白/文档本地链接。未运行build/test/analyze/codegen、设备、Git写入、CI或发布；正式构建与真机验证NOT_RUN。
- STATE-20261014-001 / WORK-20261014-001 / CE-20261014-T001-01；[详细记录](../docs/tasks/T-2026-10-14-001-adb-mirror.md)。代码/文档交付完成，未提交，不代表运行验收通过。

- 2026-10-14续接：用户明确要求复核后推送并运行编排、不监控。核对main及远端仍a1486ee，49个路径均本任务；修正状态epoch与停止解码资源释放，复核V0通过。准备执行stage/commit/push和一次Build AShell clients both/aarch64，实际回执见Git/最终交付；STATE/WORK-20261014-002 / CE-20261014-T001-02。

## T-2026-10-14-002 — 主界面分栏与ADB连续截图

- main/a0e46b10c27c7e6a407c5a61d9e7012b785f99c6，initial clean；前轮已推送、编排204接受且未监控，仍无编译成功证据。
- 用户C2授权PC单/双源居中分栏、Shell栏按钮样式与ADB强制连续截图；只在独立模块和必要PC视口/弹窗接入处修改，原Android采集/无障碍/输入链及工具链不变。
- Android私有连接固定screencap、受限PNG解码/JPEG传输；沿用41共享订阅/ACK，追加格式能力避免旧PC误解码，视频/截图互斥切换/全局停止。
- V0源码/协议/JNI/R8/坐标/生命周期/范围/文档链接核对通过；无build/test/analyze/codegen或设备验证。ADB-M01—M12待正式环境，不能把源代码交付说成运行验收通过。
- D027 / ADR0022 / Baseline17 / STATE003 / WORK004 / CE-20261014-T002-01；T8源码与文档完成、未提交，无本轮Git/CI。详见[任务](../docs/tasks/T-2026-10-14-002-adb-inline-and-screenshot.md)。

- 续接授权：用户要求推送并执行编排；核对本地与远端main均a0e46b1，35路径均本任务。提交/推送main后仅触发一次Build AShell clients both/aarch64，不监控；CE-20261014-T002-02 / STATE004 / WORK005。本文为执行前记录，Git/最终回执记录真实SHA和接受结果；无本地产品构建/设备验证。

## T-2026-10-14-003 — 布局、ADB关闭恢复、独立黑色遮罩

- 用户C2授权并补充遮罩要求；main/941b215初始clean。完成全主界面右上浮层/双源贴近中线、ADB按钮关闭重试/私有close守卫/本机请求超时、新采集generation流控清理。
- 新建APK内shell UID遮罩及独立管理器，排除其自身截图/镜像，设备单例多PC状态；最后订阅退出与ADB断开/异常清理。不熄屏、不改原Android无障碍/采集/输入实现。
- V0静态检查；未复现用户故障、未编译/真机验证；ADB-M13—M16 NOT_RUN。详细[任务](../docs/tasks/T-2026-10-14-003-adb-controls-and-overlay.md)含正式编译验证需求。
- T8源码与记录交付；D028/ADR0023/Baseline18/STATE005/WORK007/CE-20261014-T003-01。未提交/推送/CI或生产操作；External Registry reviewed-no-change。

- 同任务追加C3：用户要求提交推送并执行编排；当前30路径，main与远端均941b215。将stage/commit/push到main，触发一次Build AShell clients both/aarch64，不监控。CE-20261014-T003-02 / STATE006 / WORK008；实际SHA/接受结果见Git与最终回执，不表示构建/真机通过。

## T-2026-10-14-004 — ADB样式/压缩/触摸/穿透

- C2八项实现请求；main/404b199初始clean。前轮push成功/dispatch204，不监控；本轮无Git/CI/本地产品执行。
- 按无障碍栏参数重做Shell栏、去整栏缩放，追加节点穿透；普通图像ADB光标响应能力变化；去ADB画面常驻标签。
- 独立尺寸规则/物理几何/压缩档位、触摸恢复与本机序号、可信遮罩；UiAutomation DONT_SUPPRESS、4096节点/64层/2fps/10秒租约。原Android Kotlin/采集JNI/服务/输入路由/品牌依赖保持。
- V0源码/协议/JNI/R8/有界资源/文本括号与diff检查；正式构建/真机/多PC仍NOT_RUN。详情docs/tasks/T-2026-10-14-004-adb-ui-and-input.md；D029/ADR0024/Baseline19/STATE007/CE-20261014-T004-01。

- 当前用户追加C2补漏/C3提交推送编排：触摸重复stop保持首次期限、旧Run不能取走新队列事件、尺寸读取纳入守卫。31路径、本地/远端main404b199、Build AShell clients active。CE-20261014-T004-02 / STATE008 / WORK012；将commit/push后dispatch both/aarch64一次且不监控，实际回执见Git/交付。V0不升级为构建或真机通过。

## T-2026-10-14-005 — 同步当前ADB文档，提交推送但不编排

- 来源main/76073498e6b8a405f175c654f54275d143fdcc02，initial clean；现有“增加黑屏文字”代码已在7607349，本轮无业务代码差异。
- 用户明确C1文档+C3 commit/push到既有origin/main；明确不执行编排，未运行或监控CI，未执行本地产品build/test/analyze/codegen/设备/生产动作。
- 补录80887c1—7607349代码事实，更新12/13/14、架构/源码/Android/协议/构建调试/安全/路线入口、TEST_MATRIX、External Registry与会话记忆。新增ADR0025/D030/Baseline20；保留旧历史任务/基线。
- V0合同、来源、链接和diff核对；已有Java编译错误由1c46e74修正源码，当前仍无构建/真机通过证据。后续正式验收覆盖双PC独立命令、状态推送、UID验证、连续模式开关与原链隔离、白字遮罩/R8等。
- 当前文档交付记录写于提交前；实际commit/push SHA与clean结果由Git/会话交付提供。STATE009 / WORK014 / CE-20261014-T005-01；详情docs/tasks/T-2026-10-14-005-adb-docs-sync.md。

## T-2026-10-16-001 — ADB绘图与PC展示文档同步

- 用户明确文档同步后推送并执行编排；沿用不监控要求。初始main/4d3c44640c4b001b9af2c3106e809564f8b615cf、clean，本地/远端一致。
- 补齐0ec8b5b文档之后227bbe0/a2fc3c3/e5c80ad/393acaa/4d3c446代码事实；当前合同说明App绘图/字体、遮罩绑定、数字红色、去重提示、PC分组状态及悬浮文字精简。
- 历史回退：曾尝试无障碍权限管理及开发者入口，用户两次明确恢复393acaa并强推同步；最后4d3c446只修改overlay.dart和remote_adb_sidebar.dart两处文字显示，不包含被撤回的菜单/权限功能。已有4d3c446 both/aarch64 dispatch204回执，未监控、无构建结论。
- 本轮仅Markdown，ADR0026/D031/Baseline21、新M26—M30及记忆同步；未改原产品/无障碍/采集/输入/协议/依赖/构建实现。
- V0来源、diff、文档引用和范围复核；产品测试NOT_RUN。文档完成后commit/push到origin/main，单次Build AShell clients（both/aarch64），不监控；准确SHA及接受结果见Git/会话交付，不预写成功。
- STATE-20261016-001 / WORK-20261016-001 / CE-20261016-T001-01；[详细任务](../docs/tasks/T-2026-10-16-001-adb-docs-sync.md)。
