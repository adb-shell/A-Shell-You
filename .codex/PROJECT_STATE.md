# AShell Project State

Schema Version：`1.0`  
State Revision：`STATE-20261012-010`

Last Updated：2026-10-12T20:52:15+09:00

Observed At：2026-10-12T20:52:15+09:00

Evidence Scope：`repository-observed / V0 / external and runtime verification pending`

> 本文件是截至 `Observed At` 的当前状态快照，不是生产实时监控，也不替代源码、`docs/AI_ENGINEERING/`、ADR 或 Baseline。新会话必须用只读检查确认其新鲜度；发现漂移时记录 conflict/stale，不得用本文件覆盖源码事实或改写旧 Baseline。

最新变更证据：[T009 旧 ADB 退役](../docs/tasks/T-2026-10-12-009-remove-legacy-adb.md)。当前 main/13f792d，开始 clean；T008 已提交进入该 HEAD，旧记忆的 fb5c393/未提交状态已过期。本轮删除旧 ADB 页面/通道/模块/自动化/专用权限，排除 libadb.so，更新当前工程文档；所有本轮修改未提交。未接入新组件。只有 V0 静态证据，无正式构建或运行结论；T008 的资源合并错误是否在构建机恢复仍待验证。

T003补充：用户明确要求两处公共服务匹配字符串也改为ashell.，全部文档字段/代码示例/路径/文件名统一品牌。历史文档已标注名称规范化，原文字与真实源仓地址从a036f31恢复；当前工作区排除.git后，旧品牌文本/路径大小写不敏感检索零命中。

## 1. Source and Product Snapshot

| Field | Last Observed State | Evidence |
|---|---|---|
| Product/runtime | `A Shell You` / `AShell`；crate ashell；Android me.ashell；Flutter内部flutter_hbb | source + ADR-0014 |
| Rust / Flutter version | `5.2.1` / `5.2.1+59` | manifests + Baseline |
| Baseline ID | `AS-BL-2026-10-12-13f792d-adb-retired-worktree` | `docs/BASELINE/08_ADB_RETIREMENT_BASELINE_2026-10-12.md` |
| Branch | `main` | read-only Git observation |
| HEAD | `13f792d552db49fd1b702ff24cb2b3d0c5030c38` | 当前只读观察；本轮无 Git 写入 |
| Worktree | T009 开始 clean；旧 ADB 剔除及当前文档/记忆修改未提交 | 16 个代码/配置文件（含 5 个删除）；其余为文档，详见任务事件 |
| History mapping | a036f31提交前轮记忆；6fb0aa6初始化的975 blobs与历史源匹配是迁移前证据，准确RustDesk fork未知 | 历史接管报告；不能用该一致性描述当前dirty tree |
| Highest completed verification | `V0` repository/document/schema checks | `TEST_MATRIX.md` + task records |
| Formal build/runtime evidence | T009 之后 `NOT_RUN / VERIFICATION-REQUIRED` | APK 排除效果、升级、采集/输入/重连/语音与 Windows 交互待正式环境验证；T008 AGP 根因仍未确认 |
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
- Latest completed AI task：`T-2026-10-12-009`（旧 ADB 源码/配置剔除及静态核对；V2/V3 未执行）。
- Current active task：`none`；以 CURRENT_WORK 实时行和新会话只读复核为准。
- Latest recorded modification event：`CE-20261012-T009-01`。
- Latest product decision：`D-019` / ADR-0015 旧 ADB 退役；D-018/017/015/016 与既有产品/网络边界保持。
- Architecture decisions：`docs/ADR/README.md`；ADR-0015 取代 ADR-0007 的现役实现决定；ADR-0014 产品身份保持。

## 5. Standing Authority State

- There is no standing C2 or C3 authorization.
- Historical approvals in memory、Task History、Decision Log or ADR never transfer to a new session.
- 本轮 T009 源码剔除范围在交付后关闭；未来请求从 T0 重新核对，不继承历史权限。
- 本轮删除 5 个明确授权的旧 ADB 源文件并修改相关代码/配置/文档；没有设备数据/缓存删除、Git 写入、产品构建/测试/codegen、安装、打包或部署。
- 工具限制：删除空 adb/ 目录被自动审批策略拦截（blocked by policy）；本机保留该空目录，已核对 0 个文件，组件内容全部删除。未绕过限制。

## 6. Freshness and Update Contract

At every new session:

1. Compare branch、HEAD、dirty state and Baseline against a new read-only observation.
2. Read `CURRENT_WORK.md` and detect concurrent、stale or conflicting tasks.
3. If source or canonical engineering documents disagree with this file, mark this state stale and return to T0/T2.
4. Baseline drift creates a new Baseline ID through the approved process；never rewrite the old snapshot.
5. Do not store secret values、production addresses、peer/device identifiers、PII、absolute user paths or full command output here.

For an authorized persisted-change task, update this file at T7/T8 after canonical sources and event records are synchronized. A C0 read-only task reports `reviewed-no-change` or `not-authorized` instead of editing this file.
