# AShell Project Start Here

当前实现基线：`0dbe2027ceb3f1a72ae2700fe8e4942cfe67cec5`。2026-10-19核对时本地/远端test、main一致、工作区clean。本次只同步文档和工程记忆，以“文档更新”提交推送test，再合并推送main，不执行构建。见[Baseline22](docs/BASELINE/22_RESTORED_SOURCE_BASELINE_2026-10-19.md)、[当前任务](docs/tasks/T-2026-10-19-006-restored-docs-sync.md)、[当前状态](.codex/PROJECT_STATE.md)。

撤回的后续实现和诊断不属于当前事实，不得从旧聊天自动恢复。后续文档提交可以更新HEAD，业务树仍以该基线为准；新会话先核对HEAD/status。历史任务/基线仅保留追溯含义，不继承旧授权或构建结论。

当前ADB包含远程配对/独立Shell、投屏/无视/节点穿透、独立白字黑屏、全局优先触摸、键盘/远程输入/剪贴板同步和本应用电池优化豁免。Android默认连接服务运行期间静默常亮。详细实现、参数和待验边界见[12本机ADB](docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](docs/AI_ENGINEERING/14_ADB_MIRROR.md)。

## 1. 五分钟启动顺序

所有新 Codex 会话第一份打开本文件。读到本节后，立即打开 [`.codex/SESSION_START_PROTOCOL.md`](.codex/SESSION_START_PROTOCOL.md) 作为执行细则，再从下列第 2 项继续；该协议不是额外的工程事实层，因此不改变用户规定的 1—8 编号。强制全局记忆读取顺序是：

1. 本文件 `PROJECT_START_HERE.md`。
2. [`.codex/AI_RULES.md`](.codex/AI_RULES.md)：不可绕过的权限和安全规则。
3. [`.codex/PROJECT_STATE.md`](.codex/PROJECT_STATE.md)：截至标记时间的 repository-observed 状态快照。
4. [`.codex/CURRENT_WORK.md`](.codex/CURRENT_WORK.md)：0..N 个活动任务、并发范围和 handoff。
5. [`.codex/CHANGELOG_AI.md`](.codex/CHANGELOG_AI.md)：AI task-level 历史索引。
6. [`.codex/DECISION_LOG.md`](.codex/DECISION_LOG.md)：重大决定流水。
7. [`docs/ADR/README.md`](docs/ADR/README.md) 与当前任务相关 ADR。
8. 当前任务相关的 [`.agents/skills/`](.agents/skills/) AShell Skill。

完成记忆恢复后、开始任何任务动作前，继续按任务激活顺序读取：

9. [`AI_TASK_EXECUTION_PROTOCOL.md`](docs/AI_ENGINEERING/AI_TASK_EXECUTION_PROTOCOL.md)：每个任务必须经过的 T0—T8 状态机。
10. [`TASK_TEMPLATE.md`](TASK_TEMPLATE.md)，并引用 [`BASELINE_INDEX.md`](docs/BASELINE/BASELINE_INDEX.md) 的具体 Baseline ID。
11. [`.codex/PROJECT_MEMORY.md`](.codex/PROJECT_MEMORY.md)、[`00_PROJECT_OVERVIEW.md`](docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md)、[`01_ARCHITECTURE.md`](docs/AI_ENGINEERING/01_ARCHITECTURE.md)、[`02_SOURCE_MAP.md`](docs/AI_ENGINEERING/02_SOURCE_MAP.md) 和相关领域文档。
12. 修改任务补读 [`DEVELOPMENT_WORKFLOW.md`](DEVELOPMENT_WORKFLOW.md)；验证读取 [`TEST_MATRIX.md`](TEST_MATRIX.md)；仓外资产读取 [`EXTERNAL_ASSET_REGISTRY.md`](EXTERNAL_ASSET_REGISTRY.md)。

第 1—8 项恢复上下文，第 9—12 项激活当前任务；两组均不得跳过。旧 `docs/ENGINEERING_*` 只作为历史细节和 no-regression 证据。项目文件可以恢复事实和历史，但绝不能恢复或继承旧会话授权。

### 文档职责不得混用

| 文档 | 唯一职责 |
|---|---|
| `PROJECT_START_HERE.md` | 启动、分流、权限提醒 |
| `.codex/AI_RULES.md` | AI 行为权限与不可越过的红线 |
| `.codex/SESSION_START_PROTOCOL.md` | 新会话恢复、并发、stale/conflict 和停止门算法 |
| `.codex/PROJECT_STATE.md` | 带时间戳、可失效的当前项目状态快照 |
| `.codex/CURRENT_WORK.md` | 多会话活动任务与 handoff registry，不保存可继承授权 |
| `.codex/CHANGELOG_AI.md` | AI task-level 结果时间线和索引 |
| `.codex/CHANGE_EVENT_LOG.md` | logical persisted-change event ledger |
| `AI_TASK_EXECUTION_PROTOCOL.md` | 单个任务 T0—T8 进入/退出条件 |
| `docs/AI_ENGINEERING/00`—`10` | 当前 repository-side 架构、链路、风险和验证事实 |
| `docs/ADR/` | 长期架构决定、备选、后果和替代关系 |
| `docs/BASELINE/` | upstream、版本、依赖和正式构建环境快照 |
| `TASK_TEMPLATE.md` | 每个开发任务的授权、设计、验证和交付记录 |
| `TEST_MATRIX.md` | Android/Windows/Flutter/Rust/Network/API 验证覆盖和证据门 |
| `DEVELOPMENT_WORKFLOW.md` | 人工与 AI 的长期开发/评审/交付流程 |
| `EXTERNAL_ASSET_REGISTRY.md` | 仓外资产、owner、provenance 与 release blockers |
| `11_ROADMAP.md` | proposed 路线，不是当前行为或授权 |
| takeover/strengthening report | 固定日期交付快照，不自动覆盖后续源码 |

## 2. 项目身份速记

- 产品显示名：`一个壳子`；内部runtime：`AShell`。
- Android 显示名：`一个壳子`。
- 来源：RustDesk 深度二次开发；当前 Git 不能证明 2026-04-13 之前的完整 fork history。
- Rust crate/library：`ashell`。
- Flutter package：`flutter_hbb`（内部包名保留，不影响产品身份）。
- Android applicationId：`me.ashell`。
- Android active JNI：`libs/scrap/src/android/pkg2230.rs`。
- Windows active virtual display：Amyuni；RustDesk IDD 分支是 dormant。
- AShell controller 强制 relay；controlled endpoint 仍保留 direct/NAT compatibility code。
- 产品 backend、业务 database、hbbs/hbbr server 和完整 ZEGO token broker 不在本仓库。

完整事实只从当前源码和 `docs/AI_ENGINEERING/` 获取。

## 3. 进入仓库后立即做什么

在任何修改前：

1. 完成全局记忆恢复，核对 `PROJECT_STATE`、`CURRENT_WORK` 和当前 Task/Event 指针。
2. 运行只读 `git status --short --branch`，记录 branch、HEAD 和已有 dirty state，并判断 state 是 consistent、drift 还是 conflict。
3. 识别请求类型：回答、审查、诊断、文档、代码、构建、发布、生产或安全事件。
4. 写明授权范围、禁止动作、假设和非目标；历史会话授权一律记为不继承。
5. 选择最窄的领域 Skill；跨两个以上领域时使用 `ashell-master` 协调。
6. 先完成需求与影响分析，再决定是否存在修改权限。
7. 发现并行任务或用户已有修改时保留并绕开；不得 reset、checkout、clean 或覆盖。

建议的任务开场记录：

```text
任务类型：
授权范围：
明确禁止：
影响领域：
使用 Skill：
当前证据级别：
需要的确认门：
```

## 4. 默认禁止事项

未经当前任务的明确、逐项授权，禁止：

- 自动 `git add`、stage、branch、tag、commit、push、merge、rebase、PR。
- 自动执行 Cargo、Flutter、Gradle、Android、Windows、Docker build/test/analyze/codegen。
- 删除、移动或覆盖源码、文档、历史资产、artifact。
- 修改版本号、签名、打包、上传、部署、发布、商店操作。
- 访问或改变 production endpoint、database、server、cloud、CI secret 或 signing asset。
- 测试、轮换、撤销、复述或传播 credential。
- 把 sub-agent、Skill、脚本或外部工具当作权限绕过方式。

只读搜索、源码阅读、文档检查和明确请求的文档编辑，不自动扩大为业务代码或外部状态变更。

## 5. 确认门

| 级别 | 典型工作 | 确认规则 |
|---|---|---|
| C0 | 回答、只读分析、审查、诊断 | 可在请求范围内继续，不改变项目状态 |
| C1 | 用户明确指定的文档、规则、Skill 创建或编辑 | 原请求可作为授权；新增范围必须重新确认 |
| C2 | 业务代码、配置、协议、依赖、build script 修改 | 先给需求、影响和方案；取得实现确认，或确认原请求已明确授权该精确范围 |
| C3 | Git 写入、编译测试、删除移动、版本、签名、发布、上传、部署、production/secret 操作 | 每类动作都要临近执行时单独明确确认；其他确认不可继承 |

历史会话、其他任务或上一个阶段的授权不会自动延续。成功完成 C2 也不授权 C3。

## 6. 所有任务的固定流程

```text
需求分析
  -> 影响分析
  -> 方案设计
  -> 确认门
  -> 修改
  -> 验证
  -> 文档/记忆同步
  -> 交付与未验证项
```

详细进入/退出条件见 [`AI_TASK_EXECUTION_PROTOCOL.md`](docs/AI_ENGINEERING/AI_TASK_EXECUTION_PROTOCOL.md)。任何阶段发现范围扩大、外部资产缺失、安全风险或证据不足，都回到分析/确认阶段。

## 7. Skill 路由

| 任务 | 首选 Skill |
|---|---|
| 跨域、架构、接管、迁移、复杂事故 | `ashell-master` |
| Rust、unsafe、FFI、JNI、Windows runtime | `ashell-rust-engineer` |
| Android service、MediaProjection、Accessibility | `ashell-android-engineer` |
| Flutter UI、state、multi-window、bridge | `ashell-flutter-engineer` |
| rendezvous、relay、protocol、auth、transport | `ashell-network-engineer` |
| account、HTTP、sync、backend contract、database boundary | `ashell-api-engineer` |
| threat、credential、permission、supply chain | `ashell-security-engineer` |
| build/release planning、artifact、signing、rollback | `ashell-release-engineer` |
| 只读 brainstorming、planning、debugging、verification、review | `ashell-superpowers-safe` |

领域 Skill 不增加权限。Security 和 release guardrail 高于实现便利。

`ashell-superpowers-safe` 不是外部 Superpowers 安装证明；它只允许五项只读能力，并硬性禁止 commit、push、release。完整边界见 [`SAFE_SUPERPOWERS_PROFILE.md`](docs/AI_ENGINEERING/SAFE_SUPERPOWERS_PROFILE.md)。

## 8. 高价值不变量

- Android：`core service != screen share != frame source != PC waiting`。
- 只有明确用户操作可请求新的 `MediaProjection` permission。
- waiting/reconnect 只可 normal refresh，不能自动切 ignore/screenshot。
- JNI 改动以 `pkg2230.rs` 为 active source，并检查 `ffi.rs` compatibility drift。
- Controller relay-only 不等于 endpoint direct/NAT code 已删除。
- UI visibility、developer password、client validation 不是 server authorization。
- T009已移除旧本地ADB/LADB；8662f23新localadb由T004扩展为Android进程共享，PC走专用ADB协议；remote terminal/side-button保持独立。见ADR-0015/0017/0018。
- ZEGO invitation、token authorization 和 microphone consent 是三个边界。
- Windows privacy/injection/driver 变更必须有失败恢复和安全审查。
- External asset 未登记 owner、version、hash、license 和获取方式时，不得宣称可复现或可发布。

## 9. 验证规则

- 静态源码/文档检查只能记为 static verification。
- 未在正式环境运行的 build/test/device/server 行为保持 `verification-required`。
- 当前环境禁止项目编译时，输出《编译验证需求》：命令、环境要求、执行目录、验证目标。
- 没有测试、没有错误输出或“看起来正确”都不是运行验证。
- 最终答复必须列出做了什么、没做什么、风险和后续确认门。

## 10. 当前接管状态

- Repository-side architecture、文档和 Skills 已完成第一轮接管。
- ADR、Baseline、Task Template、Test Matrix 和 safe Superpowers profile 已完成最终治理封版。
- Global memory、multi-session current-work registry、AI changelog、change-event ledger 和 session recovery protocol 已建立。
- External backend、hbbs/hbbr、database、token service、driver/binary provenance 尚未闭环。
- 正式 Android/Windows build/device evidence 尚未回填。
- 公开 credential、transport、update/plugin 和高权限平台风险仍是 release blockers。

本轮体系状态见 [`ASHELL_AI_ENGINEERING_STRENGTHENING_REPORT.md`](docs/AI_ENGINEERING/ASHELL_AI_ENGINEERING_STRENGTHENING_REPORT.md)。

最终封版状态见 [`ASHELL_AI_ENGINEERING_FINAL_SEAL_REPORT.md`](docs/AI_ENGINEERING/ASHELL_AI_ENGINEERING_FINAL_SEAL_REPORT.md)。

如对权限、状态层或真相来源有疑问：停在修改前，回到 `.codex/AI_RULES.md` 和任务协议。
