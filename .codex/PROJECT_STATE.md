# AShell Project State

Schema Version：`1.0`
State Revision：`STATE-20261017-001`
Last Updated / Observed At：2026-10-17（Asia/Tokyo）
Evidence Scope：`repository-observed / V0 / formal build and runtime pending`

最新局部观察：[T-2026-10-17-001](../docs/tasks/T-2026-10-17-001-build-mode-label.md)，起点main/130f748，初始clean。仅将新工作流的“完整编译”统一改名为“原始编译”，默认“常规编译”和运行逻辑保持；十处精确替换V0通过。用户授权本地提交，本次不push、不dispatch、不构建。当前Task/Event：T-2026-10-17-001 / CE-20261017-T001-01，WORK-20261017-001；提交结果以Git和会话交付为准。无Decision/ADR增量，产品与发布验证状态未提升。下方源码/基线表保留2026-10-16观察，未对期间其他提交作全仓复核。

当前[T001](../docs/tasks/T-2026-10-16-001-adb-docs-sync.md)同步截至4d3c446的ADB文档，初始工作区clean、本地/远端一致。节点/字体绘制已迁移到App，PC状态与提示已更新；两次回退后，当前没有无障碍权限控制或开发者选项菜单。本快照写于文档提交前，最终文档SHA、推送及dispatch接受回执见Git/会话交付。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | A Shell You / AShell；me.ashell；Flutter内部flutter_hbb | 当前manifest，产品身份未改 |
| Rust / Flutter version | 5.2.1 / 5.2.1+59 | manifests |
| Branch / implementation HEAD | main / 4d3c44640c4b001b9af2c3106e809564f8b615cf | 本任务文档提交前只读观察 |
| Baseline | AS-BL-2026-10-16-4d3c446-adb-render-ui | Baseline21；旧基线冻结 |
| Worktree | 初始clean；本轮仅ADB相关Markdown同步 | T001 / CE-20261016-T001-01 |
| ADB current | 既有身份/UID/命令/推送及独立触摸/媒体保持；Shell读节点、App绘图/预渲染黑屏PNG；PC分组状态、精简文字 | 12_LOCAL_ADB / 13_REMOTE_ADB / 14_ADB_MIRROR |
| Verification | V0源码、合同、引用与diff检查；无当前V2—V4通过结论 | TEST_MATRIX |
| Build evidence | 1c46e74修正既有编译错误；此前4d3c446 both/aarch64 dispatch204未监控，当前构建结果仍未知 | 08_BUILD_SYSTEM / T001 |
| Current actions | 用户授权文档commit/push后单次both/aarch64编排，不监控；未本地build/test/analyze/codegen或真机操作 | 当前用户指令 |
| Release readiness | BLOCKED，既有安全/资产/正式验证缺口保持 | 下表；文档推送不是发布 |

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

- Task：T-2026-10-16-001；活动状态以CURRENT_WORK为准。
- Event：CE-20261016-T001-01；STATE-20261016-001 / WORK-20261016-001。
- Decision：D-031 / ADR-0026（已有App绘图边界补录）；D-030及此前媒体/输入/遮罩隔离继续有效。
- Current contract：docs/AI_ENGINEERING/12_LOCAL_ADB.md、13_REMOTE_ADB.md、14_ADB_MIRROR.md。
- History：TASK_HISTORY、CHANGELOG_AI、CHANGE_EVENT_LOG、旧Task/ADR/Baseline；不把历史dispatch接受当构建成功。

## 5. Standing Authority State

本次明确授权文档更新、提交推送以及Build AShell clients单次dispatch（both/aarch64），不监控。没有产品代码、依赖/版本/签名、设备/生产或Release变更；本记录不授予下一任务权限，也不预写外部动作成功。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
