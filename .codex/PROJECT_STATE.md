# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261013-001`

Last Updated：2026-10-13（Asia/Tokyo）

Observed At：2026-10-13（Asia/Tokyo）

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T-2026-10-13-001 客户端 Actions](../docs/tasks/T-2026-10-13-001-client-actions.md)。当前 main/8662f23，initial clean；T009 已由03aec41固化，c81d9f7更新配置，8662f23已新增隔离 local ADB，旧摘要“未接入新组件”已过期。本轮将 GitHub 编排替换为独立手动 Android/Windows 工作流，仅测试 artifacts；本轮修改未提交。只有 V0，runner build/安装 NOT_RUN；新 local ADB 运行语义未在本任务重新审计。

T003补充：用户明确要求两处公共服务匹配字符串也改为ashell.，全部文档字段/代码示例/路径/文件名统一品牌。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当前工作区排除.git后，旧品牌文本/路径大小写不敏感检索零命中。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-13-8662f23-client-actions-worktree` | `docs/BASELINE/09_CLIENT_ACTIONS_BASELINE_2026-10-13.md` |
| Branch | `main` | read-only Git observation |
| HEAD | `8662f2369a33f5286c20f9de4d640d2072175ca7` | 当前只读观察；本轮无 Git 写入 |
| Worktree | 本任务开始 clean；工作流/元数据兼容/文档记忆修改未提交 | 删除13份旧编排配置，新增独立客户端工作流；详见任务事件 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | 本任务 `NOT_RUN / VERIFICATION-REQUIRED` | CI-01—04 runner 首次构建/下载、签名/ABI、Windows运行待验证；历史设备风险不因CI源码整改关闭 |
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
| Retired Android ADB assets | `EXT-BIN-ADB-001`；`EXT-REF-ADB-002/003` | RETIRED，不再是当前发布 blocker；历史来源记录保留 | Release 验证 APK 无 libadb.so；新组件另行定义 |
| Windows native assets | `EXT-WIN-001`—`009` applicable items | external/generated/unverified | Windows/Release/Security owner |
| Build/sign/release | `EXT-SIGN-*`, `EXT-BUILD-*`, `EXT-CI-001`, `EXT-REL-001` | formal environments and custody incomplete | Release owner / G2—G3 |
| Upstream/compliance | `EXT-HIST-001`, `EXT-COMP-001` | exact fork lineage、SBOM/NOTICE/source-offer incomplete | source/legal/release owner |

## 4. Current Memory Pointers

- Active task registry：`CURRENT_WORK.md`。
- Detailed task history：`TASK_HISTORY.md`。
- Latest completed AI task：`T-2026-10-13-001`（独立客户端Actions源码/V0；V2未执行）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261013-T001-01`。
- Latest decision：`D-020` / ADR-0016 独立手动客户端Actions；既有产品/网络边界保持。
- Architecture decisions：`docs/ADR/README.md`；ADR-0015 取代 ADR-0007 的现役实现决定；ADR-0014 产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- 本轮客户端Actions编辑范围在交付后关闭；未来 Git/runner 执行请求从 T0 重新核对，不继承历史权限。
- 本轮删除12个旧workflow和1份失效Dependabot配置；没有业务/用户数据删除、Git写入、产品构建/测试/codegen、实际签名/打包、远端dispatch/上传/发布。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
