# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261014-002`

Last Updated：2026-10-14（Asia/Tokyo；独立ADB投屏与触摸优先级）

Observed At：2026-10-14（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

续接复核：用户已明确授权查漏补缺、stage/commit/push GitHub main及一次Build AShell clients both/aarch64，不监控。修补旧status覆盖推送及停投屏decoder残留；CE-20261014-T001-02。当前文档为提交前状态，实际交付SHA和dispatch接受结果以Git/最终回执为准；不宣称构建成功。

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更：[T-2026-10-14-001](../docs/tasks/T-2026-10-14-001-adb-mirror.md)。main/a1486ee初始clean；已实现独立scrcpy投屏、多PC共享/全局停止、PC Shell栏两按钮和独立窗口，以及用户追加的ADB优先触摸。原画面也按ADB/无障碍能力选触摸路径；仅增加输入分流和旧远程手势释放，原采集不变。状态推送和本机取帧不恢复远程周期status查询。源码与文档未提交；V0，编译/真机待验证，无Git/构建/CI动作。

2026-10-12 T003 历史补充：当时按用户要求统一品牌字段/路径与两处公共服务匹配。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当时的检索结果不代表本次重新扫描全仓品牌。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-14-a1486ee-adb-mirror-worktree` | Baseline16；旧基线冻结 |
| Branch | `main` | read-only Git observation |
| HEAD | `a1486ee2ad2bca991b272a8c7780d2daf2f7c526` | 本轮只读观察；历史文档提交已固化 |
| Worktree | 初始clean；本轮独立ADB媒体/触摸源码、资产和文档未提交 | T-2026-10-14-001 / CE-20261014-T001-01 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | 当前工作树未取得新包/正式构建/真机结果；本轮未触发或监控CI | ADB-M01—M08全部NOT_RUN；旧矩阵继续适用，不宣称构建成功 |
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
| New scrcpy server asset | `EXT-BIN-SCRCPY-001` | v5.0.1固定来源/大小/hash/Apache-2.0已记录并离线打包；未验证签名或重建上游产物 | Android/Release；ADB-M01—M08 |
| Windows native assets | `EXT-WIN-001`—`009` applicable items | external/generated/unverified | Windows/Release/Security owner |
| Build/sign/release | `EXT-SIGN-*`, `EXT-BUILD-*`, `EXT-CI-001`, `EXT-REL-001` | formal environments and custody incomplete | Release owner / G2—G3 |
| Upstream/compliance | `EXT-HIST-001`, `EXT-COMP-001` | exact fork lineage、SBOM/NOTICE/source-offer incomplete | source/legal/release owner |

## 4. Current Memory Pointers

- Active task registry：`CURRENT_WORK.md`。
- Detailed task history：`TASK_HISTORY.md`。
- Latest documented changes：`T-2026-10-14-001`（独立ADB投屏与触摸优先级；源码/资产/文档，V0）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261014-T001-02`（对01的复核与授权交付补录）。
- Latest decision：D-026 / ADR-0021独立scrcpy投屏与ADB优先触摸；D-024 / ADR-0020无二维码/显式刷新继续有效，ADR-0019独立权限/NSD保持。
- Architecture decisions：`docs/ADR/README.md`；ADR-0018扩展0017远程能力，ADR-0015旧退役/0014产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- 本轮用户授权scrcpy投屏及追加ADB触摸优先级；随后明确追加查漏补缺、提交/推送main和一次both/aarch64编排，不监控。该授权仅限本任务交付。
- 无本地build/test/analyze/codegen、设备、删除/版本/发布；先前任务的CI接受不是当前源码成功证据。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
