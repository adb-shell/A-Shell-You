# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261012-008`

Last Updated：2026-10-12T07:20:08+09:00

Observed At：2026-10-12T07:20:08+09:00

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T007图标覆盖复核](../docs/tasks/T-2026-10-12-007-icon-coverage.md)，继承T006原图资源。产品身份由f423335、服务器默认配置由c0951b5、PC入口和portable generator由c9b7cd1固化；两轮图标、加载引用、ignore和文档合并51文件未提交。Android通知/主题/悬浮图与Windows旧UI/自解压窗口/卸载快捷方式已补齐。仅保留build.cmd、只打包本次Release，不附加外部DLL/驱动。之前的 [接管认知](../docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md)及旧基线保留历史事实。Flutter内部package保留flutter_hbb；只有V0检查，未编译或运行验证。

T003补充：用户明确要求两处公共服务匹配字符串也改为ashell.，全部文档字段/代码示例/路径/文件名统一品牌。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当前工作区排除.git后，旧品牌文本/路径大小写不敏感检索零命中。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-12-c0951b5-pc-build-worktree` | `docs/BASELINE/07_PC_BUILD_BASELINE_2026-10-12.md` |
| Branch | `test` | read-only Git observation |
| HEAD | `c9b7cd135be838989953e7ab7037f8c6d8d6aa31` | read-only Git observation；已固化T005，旧baseline不改写 |
| Worktree | T007开始时为T006的32文件；T007涉及28文件，与T006重叠9文件，合并51文件未提交 | 精确列表见T006/T007；图片及Flutter既有delta获保护，未stage/commit/push |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | T005/T006/T007后 `NOT_RUN / VERIFICATION-REQUIRED` | 图像解码/alpha、XML与静态引用通过，不代表新EXE/APK生成或实际显示通过；系统/自定义图边界见T007 |
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
- Latest completed AI task：`T-2026-10-12-007`（补齐Windows/Android产品图标覆盖/V0；正式构建显示未验证）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261012-T007-01`。
- Latest product decision：`D-018`PC入口/打包范围；服务器仍沿用D-017，产品身份D-015/D-016 / ADR-0014；治理仍沿用D-014。
- Architecture decisions：`docs/ADR/README.md`；ADR-0014取代ADR-0002产品身份决定。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- 旧任务与本轮T-2026-10-12-007图标补齐授权在交付后关闭；后续请求按当前用户授权重新定界，不继承历史权限。
- 本轮仅图片派生、资源覆盖、必要Windows图标引用/句柄绑定、Android XML/ignore及文档调整；原用户文件未改。没有删除移动、Git写入、产品构建/测试/codegen、安装、实际打包或部署。此前Git动作不构成本轮或未来权限。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
