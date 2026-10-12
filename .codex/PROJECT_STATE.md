# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261013-003`

Last Updated：2026-10-13（Asia/Tokyo）

Observed At：2026-10-13（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T-2026-10-13-003 本机 ADB 文档同步](../docs/tasks/T-2026-10-13-003-local-adb-docs.md)。观察 main/f84a78f，initial clean；8662f23 的独立 ADB 模块未变。当前文档已同步手动配对/连接/id验证/shell、生命周期、依赖与验证边界；仅 Markdown/V0，未取得本模块 V2/V3 结果。HEAD 已包含 c521601 / 128cff7 / f84a78f 后续 CI 配置和修复，不在本次重新审计 runner 成功情况；旧任务记录保留原时点。

2026-10-12 T003 历史补充：当时按用户要求统一品牌字段/路径与两处公共服务匹配。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当时的检索结果不代表本次重新扫描全仓品牌。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-13-f84a78f-local-adb` | `docs/BASELINE/11_LOCAL_ADB_BASELINE_2026-10-13.md`；旧基线冻结 |
| Branch | `main` | read-only Git observation |
| HEAD | `f84a78f87d1009c24acfdff478cf95bb463547e5` | T003 本轮只读观察；无 Git 写入 |
| Worktree | 本任务开始 clean；结束仅 ADB 相关 Markdown/记忆修改，未提交 | T003 / CE-20261013-T003-01 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | ADB V2/V3 `NOT_RUN / VERIFICATION-REQUIRED`；CI 成功证据未在本任务取得 | AND-09—13 与原链回归待正式结果；不能从同步文档请求推定 PASS |
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
- Latest completed AI task：`T-2026-10-13-003`（ADB 文档/V0；V2/V3 未取得结果）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261013-T003-01`。
- Latest decision：`D-021` / ADR-0017 补记独立本机 ADB；D-020 / ADR-0016 CI 边界保持。
- Architecture decisions：`docs/ADR/README.md`；ADR-0017 补充新模块，ADR-0015 对旧组件退役及 ADR-0014 产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- T003 当前请求只同步整理 ADB 文档；此前实现代码的 commit/push 已交付，不自动再次提交本轮文档。
- 本轮仅 Markdown，不修改产品代码/配置，不执行构建/测试/codegen、设备、删除、版本、签名/打包、远端 dispatch 或发布。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
