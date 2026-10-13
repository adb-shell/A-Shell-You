# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261013-007`

Last Updated：2026-10-13（Asia/Tokyo）

Observed At：2026-10-13（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更：[T007文档同步](../docs/tasks/T-2026-10-13-007-adb-docs-sync.md)。观察main/efb2599 + 三份先前已获准的未提交Dart修改：PC去除定时刷新、两端模式控件分离、PC连接按钮主题色；二维码/请求取消修复已由efb2599固化。本轮仅改文档并保护源码hash。V0；efb2599推送后的both/aarch64 dispatch有HTTP204接受回执但未监控，无构建/真机成功证据。用户现追加授权提交全部待交付改动、推送并单次触发同一编排，不监控。本文为交付前快照，实际SHA与外部动作以最终回执为准。

2026-10-12 T003 历史补充：当时按用户要求统一品牌字段/路径与两处公共服务匹配。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当时的检索结果不代表本次重新扫描全仓品牌。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-13-efb2599-adb-ui-worktree` | Baseline14；旧基线冻结 |
| Branch | `main` | read-only Git observation |
| HEAD | `efb2599d05ba5b3a08a9c63ee00bec20e2ce9c5b` | T007文档同步开始观察；交付SHA另见最终回执 |
| Worktree | 初始三份Dart dirty；本轮补ADB文档/测试矩阵/ADR/基线/恢复记忆，准备提交 | T007 / CE-20261013-T007-01；源码hash保护 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | efb2599 CI仅dispatch接受；当前UI尚无新包证据；本轮授权交付后再dispatch一次且不监控 | ADB-R01—09、11—15待验，旧R10为N/A；不宣称构建成功 |
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
- Latest documented changes：`T-2026-10-13-007`（补录二维码移除及UI简化，文档/V0；交付回执见最终答复）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261013-T007-01`。
- Latest decision：D-024 / ADR-0020；二维码移除、PC显式刷新、模式控件分离；ADR-0019独立权限/NSD仍有效。
- Architecture decisions：`docs/ADR/README.md`；ADR-0018扩展0017远程能力，ADR-0015旧退役/0014产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- T007当前用户授权同步文档，并追加提交待交付代码/文档、推送GitHub main、单次Build AShell clients both/aarch64，不监控；此记录不形成后续任务的持续授权。
- 无本地build/test/analyze/codegen、设备、删除/版本/发布；提交前记录不预报本轮Git/dispatch成功。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
