# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261013-005`

Last Updated：2026-10-13（Asia/Tokyo）

Observed At：2026-10-13（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T-2026-10-13-005 ADB bind导入修复](../docs/tasks/T-2026-10-13-005-adb-bind-import.md)。main/de8bc76已包含T004；用户Android CI日志显示ADB controller三处bind未定义，Flutter编译FAIL。当前工作树补显式platform_model导入，仅一行代码变更；静态核对完成，修复后未重编。

2026-10-12 T003 历史补充：当时按用户要求统一品牌字段/路径与两处公共服务匹配。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当时的检索结果不代表本次重新扫描全仓品牌。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-13-917e661-remote-adb-worktree` | `docs/BASELINE/12_REMOTE_ADB_BASELINE_2026-10-13.md`；旧基线冻结 |
| Branch | `main` | read-only Git observation |
| HEAD | `de8bc768f0a71cab3f60e93f321ae0f65d74c658` | T005只读观察；本轮无Git写入 |
| Worktree | 初始clean；当前仅ADB controller导入与相关记录修改，未提交 | T005 / CE-20261013-T005-01 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | 用户Android CI日志：修复前Flutter编译FAIL；修复后NOT_RUN，设备V3/V4仍待验 | T005 / ADB-R07；不推定其他job通过 |
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
- Latest completed AI task：`T-2026-10-13-005`（bind缺失导入已修复/V0；新SHA编译待验证）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261013-T005-01`。
- Latest decision：D-022 / ADR-0018；T005 reviewed-no-change，仅补Dart导入，无新架构决定。
- Architecture decisions：`docs/ADR/README.md`；ADR-0018扩展0017远程能力，ADR-0015旧退役/0014产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- T005当前故障反馈授权针对性代码修复和必要记录；不自动Git写入或重新dispatch。
- 本轮仅ADB controller一行导入及文档/记忆，未build/test/analyze/codegen、设备、Git、签名/发布。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
