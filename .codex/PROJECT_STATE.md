# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261012-009`

Last Updated：2026-10-12T07:42:41+09:00

Observed At：2026-10-12T07:42:41+09:00

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T008 Android构建诊断](../docs/tasks/T-2026-10-12-008-android-resource-build.md)。T006/T007图标已由fb5c393（修改图标）提交/推送test并快进合并/推送main；当前main。本轮build.sh日志/返回路径隔离和失败传播修正及文档未提交。用户报告旧目录增量构建mergeReleaseResources异常，疑似AGP增量状态问题，待该机clean/retry和完整stacktrace确认；脚本二次Missing file路径污染已由源码确认并修改。产品身份f423335、服务器配置c0951b5、PC入口c9b7cd1及历史基线保持；Flutter内部flutter_hbb不变。只有V0静态检查，无本轮编译/运行成功证据。

T003补充：用户明确要求两处公共服务匹配字符串也改为ashell.，全部文档字段/代码示例/路径/文件名统一品牌。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当前工作区排除.git后，旧品牌文本/路径大小写不敏感检索零命中。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-12-c0951b5-pc-build-worktree` | `docs/BASELINE/07_PC_BUILD_BASELINE_2026-10-12.md` |
| Branch | `main` | read-only Git observation |
| HEAD | `fb5c393fe1e29a2cf805e75847820b315c468b3d` | 图标提交已按前轮明确请求同步test/main；本轮不继承Git权限 |
| Worktree | T008开始clean；build.sh与10份任务/工程/恢复文档，共11文件未提交 | 只有脚本结果通道/失败传播及文档，无图标/依赖delta |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | 用户报告Android增量mergeReleaseResources失败；T008修正后 `NOT_RUN / VERIFICATION-REQUIRED` | 原始片段不足以确认AGP根因；bash语法/静态检查不代表APK构建通过 |
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
| Android native assets | `EXT-BIN-ADB-001`；`EXT-REF-ADB-002` / `003` | jniLibs当前缺失；reference来源仍未闭环 | Android/Release owner |
| Windows native assets | `EXT-WIN-001`—`009` applicable items | external/generated/unverified | Windows/Release/Security owner |
| Build/sign/release | `EXT-SIGN-*`, `EXT-BUILD-*`, `EXT-CI-001`, `EXT-REL-001` | formal environments and custody incomplete | Release owner / G2—G3 |
| Upstream/compliance | `EXT-HIST-001`, `EXT-COMP-001` | exact fork lineage、SBOM/NOTICE/source-offer incomplete | source/legal/release owner |

## 4. Current Memory Pointers

- Active task registry：`CURRENT_WORK.md`。
- Detailed task history：`TASK_HISTORY.md`。
- Latest completed AI task：`T-2026-10-12-008`（Android错误诊断与脚本静态修正/V0；资源合并及正式编译结果待验证）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261012-T008-01`。
- Latest product decision：`D-018`PC入口/打包范围；服务器仍沿用D-017，产品身份D-015/D-016 / ADR-0014；治理仍沿用D-014。
- Architecture decisions：`docs/ADR/README.md`；ADR-0014取代ADR-0002产品身份决定。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- 旧任务与本轮T-2026-10-12-008脚本修正范围在交付后关闭；后续请求按当前用户授权重新定界，不继承历史权限。
- 本轮仅build.sh静态修改与文档；没有清缓存/删除移动、Git写入、产品构建/测试/codegen、安装、实际打包或部署。此前Git动作不构成本轮或未来权限。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
