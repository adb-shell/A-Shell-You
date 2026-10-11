# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261012-005`

Last Updated：2026-10-12T06:16:00+09:00

Observed At：2026-10-12T06:16:00+09:00

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T004服务器配置](../docs/tasks/T-2026-10-12-004-server-configuration.md)。[AShell身份迁移](../docs/AI_ENGINEERING/ASHELL_IDENTITY_MIGRATION_2026-10-12.md)已由f423335固化并推送；当前服务器默认值更新尚未提交。之前的 [接管认知](../docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md)及旧基线保留迁移前事实。Flutter内部package保留flutter_hbb；只完成静态检查，未执行编译或运行验证。

T003补充：用户明确要求两处公共服务匹配字符串也改为ashell.，全部文档字段/代码示例/路径/文件名统一品牌。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当前工作区排除.git后，旧品牌文本/路径大小写不敏感检索零命中。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-12-a036f31-worktree` | `docs/BASELINE/06_ASHELL_IDENTITY_BASELINE_2026-10-12.md` |
| Branch | `test` | read-only Git observation |
| HEAD | `f423335e90885975c101484977abbe8c7524b42e` | read-only Git observation |
| Worktree | T004开始时clean且与origin/test一致；当前服务器默认配置及文档更新未提交 | 精确列表每次会话重查；本任务未stage/commit/push |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | `NOT_RUN / VERIFICATION-REQUIRED` | Android/Windows/Network/API formal environments absent |
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
- Latest completed AI task：`T-2026-10-12-004`（服务器默认配置/V0；正式连接和构建未验证）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261012-T004-01`。
- Latest product decision：`D-017`服务器默认配置；产品身份仍沿用D-015/D-016 / ADR-0014；治理仍沿用D-014。
- Architecture decisions：`docs/ADR/README.md`；ADR-0014取代ADR-0002产品身份决定。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- 旧任务与本轮T-2026-10-12-004服务器配置变更授权在交付后关闭；后续请求按当前用户授权重新定界，不继承历史权限。
- 本轮仅执行明确授权范围内的源码/配置/文档编辑；没有Git写入、构建/测试/codegen、安装、服务访问或部署。此前品牌提交已按单独请求推送；该动作不构成本轮或未来Git写入权限。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
