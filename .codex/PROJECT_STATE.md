# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261014-008`

Last Updated：2026-10-14（Asia/Tokyo；ADB样式/几何/触摸/节点穿透）

Observed At：2026-10-14（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

T004续接复核/C3：补齐LocalAdbTouch重复停止期限、旧writer隔离、尺寸读取超时；31路径均本任务。本地/远端main同为404b199，Build AShell clients active；用户授权提交推送main并dispatch一次both/aarch64，不监控。CE-20261014-T004-02 / WORK012；此为执行前快照，实际提交及接受结果见Git/交付。V0；未本地产品验证，下方“无本轮Git/CI”为此前时点。

当前T004：[ADB样式/画面/输入完善](../docs/tasks/T-2026-10-14-004-adb-ui-and-input.md)，main/404b199初始clean；本轮源码与记录未提交，D029/ADR0024/Baseline19/CE-20261014-T004-01。无本轮Git/CI/产品执行，仅V0；ADB-M17—M20待正式验证。前轮404b199已推送，dispatch204且未监控；下方T003/T002执行前段落均为历史记录。

T003续接C3：用户明确要求提交、推送并执行编排；范围为当前30路径，GitHub adb-shell/A-Shell-You/main，Build AShell clients both/aarch64一次、不监控。只读核对本地/远端main同为941b215、workflow active、diff检查通过。CE-20261014-T003-02 / WORK008；本条为执行前快照，提交SHA与dispatch回执见Git/最终交付；不宣称构建通过，下方“无授权/未提交”为此前时点。

当前T003：[布局/控件/独立黑屏遮罩](../docs/tasks/T-2026-10-14-003-adb-controls-and-overlay.md)，main/941b215初始clean，源码与相关记录未提交；D028/ADR0023/Baseline18/CE-20261014-T003-01。V0，未编译/真机；原Android采集/Accessibility/输入实现、品牌/依赖不变。无本轮Git/CI授权或动作。

前轮执行回执：941b215已提交推送，Build AShell clients both/aarch64获204，遵照用户要求未监控。以下T002执行前段落为历史快照，不代表当前HEAD，也不代表编译通过。

T002续接：用户明确授权提交/推送当前35路径到GitHub main，并触发一次Build AShell clients both/aarch64，不监控。本地/远端main均a0e46b1，V0 diff通过；CE-20261014-T002-02。此快照先于执行，实际提交SHA/dispatch回执见Git与最终交付，不能推断构建通过；下方未提交/无CI为执行前事实。

前轮交付回执：a0e46b1已提交推送；Build AShell clients both/aarch64获HTTP204且未监控。当前T002为其上的新修改，无新的Git/CI动作；不推断前轮或本轮编译通过。

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更：[T-2026-10-14-002](../docs/tasks/T-2026-10-14-002-adb-inline-and-screenshot.md)。main/a0e46b1初始clean；实现单源居中/双源左普通右ADB、Shell栏蓝/红按钮、独立ADB连续系统截图及多PC格式协商。PC仅适配视口和等待提示，原Android采集/无障碍/输入优先级保持。D-027 / ADR-0022 / Baseline17 / CE-20261014-T002-01；源码文档未提交，V0，构建/真机待验。

2026-10-12 T003 历史补充：当时按用户要求统一品牌字段/路径与两处公共服务匹配。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当时的检索结果不代表本次重新扫描全仓品牌。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-14-404b199-adb-layout-worktree` | Baseline19；旧基线冻结 |
| Branch | `main` | read-only Git observation |
| HEAD | `404b1991157308ebf700108dac3196f9d2889f91` | 本轮只读观察；前轮已提交推送 |
| Worktree | 初始clean；本轮ADB栏/压缩/触摸/节点穿透源码与记录未提交，无新外部资产 | T-2026-10-14-004 / CE-20261014-T004-01 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | 当前工作树未取得新包/正式构建/真机结果；本轮未触发或监控CI | ADB-M01—M12全部NOT_RUN；旧矩阵继续适用，不宣称构建成功 |
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
