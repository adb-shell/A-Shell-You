# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261013-008`

Last Updated：2026-10-14（Asia/Tokyo；T008跨日文档交付）

Observed At：2026-10-13（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更：[T008文档同步](../docs/tasks/T-2026-10-13-008-adb-sidebar-docs-sync.md)。观察main/aca7446，初始clean；980f254已固化前轮UI/文档，aca7446增加独立Shell栏、顶栏开关及无障碍栏标题。状态按事件查询，可用时自动显示且普通回执保留手动关闭；无PC周期status、无投屏传输。当前仅Markdown/V0，八份Dart源码hash保护，无Git/构建/CI动作。下一阶段投屏待设计，当前SHA构建/真机证据未取得。

2026-10-12 T003 历史补充：当时按用户要求统一品牌字段/路径与两处公共服务匹配。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当时的检索结果不代表本次重新扫描全仓品牌。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-13-aca7446-adb-sidebar` | Baseline15；旧基线冻结 |
| Branch | `main` | read-only Git observation |
| HEAD | `aca74467a3f83392c6a440e6507a63508f9294f6` | T008只读观察；本地origin/main同SHA，未远端查询 |
| Worktree | 初始clean；本轮仅Markdown文档待提交，未改源码 | T008 / CE-20261013-T008-01；八份Dart hash保护 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | 未取得aca7446的新包/正式构建/真机结果；本轮未触发或监控CI | ADB-R01—09、11—19待验，旧R10为N/A；不宣称构建成功 |
| Release readiness | `BLOCKED` | security、external assets、V2—V4、signing/SBOM/rollback gaps |

## 2. Governance and Knowledge Readiness

| Area | State | Canonical Reference |
|---|---|---|
| Repository archaeology | 核心链V0已复核，逐文件覆盖/外部系统/运行验证未完成 | `TAKEOVER_REVIEW_2026-10-12.md` |
| AI task governance | T0—T8、C0—C3、ADR、Baseline、Task Template、Test Matrix active | `PROJECT_START_HERE.md` + task protocol |
| Global session memory | 本轮证据已保存并接入恢复入口；V0引用/范围检查完成，未实际启动新聊天验证 | `SESSION_START_PROTOCOL.md` + memory files |
| Rust/Flutter/Android/Windows source understanding | V0 documented | domain architecture documents |
| External infrastructure | incomplete / external | `EXTERNAL_ASSET_REGISTRY.md` |
| Formal build/device/integration validation | not executed | `TEST_MATRIX.md` |
| Commercial release gate | not satisfied | Security Model + External Registry |

## 3. Current Blocking Areas

| Category | Representative IDs / Gap | Current State | Owner / Next Gate |
|---|---|---|---|
| Credential/security evidence | `SEC-001` and related credential-type exposure | 字面量路径已确认；当前有效性/生产用途未测试，不能复制值 | project owner + Security / G0 |
| Transport/auth/privacy | `SEC-002`—`SEC-016` applicable items | source-reviewed；dynamic proof pending | domain + Security / G1—G2 |
| Backend and data | `EXT-SVC-001`—`003`, `EXT-DATA-001` | server/source/schema/owner missing | backend/infra owner |
| ZEGO service | `EXT-SVC-004`, `EXT-SVC-005` | partial/external；incident and lock drift | API/Security owner |
| Retired Android ADB assets | `EXT-BIN-ADB-001`；`EXT-REF-ADB-002/003` | RETIRED，不再是当前发布 blocker；历史来源记录保留 | Release 验证 APK 无 libadb.so |
| New local ADB dependencies | `EXT-LIB-ADB-004`—`007` | 固定版本/许可来源已记录，hash/NOTICE/SBOM/Release JNI/R8/ROM验证未闭环 | Android/Security/Release，OWNER-REQUIRED；AND-09—13 |
| Windows native assets | `EXT-WIN-001`—`009` applicable items | external/generated/unverified | Windows/Release/Security owner |
| Build/sign/release | `EXT-SIGN-*`, `EXT-BUILD-*`, `EXT-CI-001`, `EXT-REL-001` | formal environments and custody incomplete | Release owner / G2—G3 |
| Upstream/compliance | `EXT-HIST-001`, `EXT-COMP-001` | exact fork lineage、SBOM/NOTICE/source-offer incomplete | source/legal/release owner |

## 4. Current Memory Pointers

- Active task registry：`CURRENT_WORK.md`。
- Detailed task history：`TASK_HISTORY.md`。
- Latest documented changes：`T-2026-10-13-008`（Shell栏事实与下一阶段投屏待设计交接；仅文档/V0）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261013-T008-01`。
- Latest decision：D-025补录Shell栏UI；D-024 / ADR-0020无二维码/显式刷新继续有效，ADR-0019独立权限/NSD保持。
- Architecture decisions：`docs/ADR/README.md`；ADR-0018扩展0017远程能力，ADR-0015旧退役/0014产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- T008用户仅要求更新当前文档，并提出稍后集成ADB投屏方向；本轮不开始代码实现或执行Git/CI操作。
- 无本地build/test/analyze/codegen、设备、删除/版本/发布；先前任务的CI接受不是当前源码成功证据。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
