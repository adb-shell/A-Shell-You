# AShell Engineering Baseline Index

当前：[AS-BL-2026-10-25-48226fb-developer-password](23_DEVELOPER_PASSWORD_BASELINE_2026-10-25.md)。观察源码test/48226fb，包含PC开发者双密码入口；T-2026-10-25-002同步文档并合并推送main，执行Both/常规编译。静态证据V0，准确main SHA和run见任务，不把dispatch当作构建PASS。

## 快照索引

以下01—22均为冻结的历史快照，不代表当前HEAD或可继承授权。当前源码与对应领域合同优先。

- [Baseline23：PC开发者双密码与当前编排源码快照](23_DEVELOPER_PASSWORD_BASELINE_2026-10-25.md)
- [Baseline22：回退后的源码与文档基线](22_RESTORED_SOURCE_BASELINE_2026-10-19.md)
- [Baseline21 — ADB绘图与PC展示文档同步](21_ADB_RENDER_UI_BASELINE_2026-10-16.md)
- [Baseline20 — 当前ADB模块文档同步](20_ADB_CURRENT_BASELINE_2026-10-14.md)
- [Baseline19 — ADB layout, geometry and input](19_ADB_LAYOUT_INPUT_BASELINE_2026-10-14.md)
- [AS-BL-2026-10-14-941b215-adb-overlay-worktree](18_ADB_OVERLAY_BASELINE_2026-10-14.md)
- [AShell inline ADB and screenshot baseline](17_ADB_INLINE_SCREENSHOT_BASELINE_2026-10-14.md)
- [AShell ADB mirror baseline](16_ADB_MIRROR_BASELINE_2026-10-14.md)
- [AShell ADB Shell栏 Baseline — 2026-10-13](15_ADB_SIDEBAR_BASELINE_2026-10-13.md)
- [AS-BL-2026-10-13-efb2599-adb-ui-worktree](14_ADB_CODE_PAIRING_BASELINE_2026-10-13.md)
- [AS-BL-2026-10-13-504adea-adb-usability](13_ADB_USABILITY_BASELINE_2026-10-13.md)
- [Remote ADB Worktree Baseline — 2026-10-13](12_REMOTE_ADB_BASELINE_2026-10-13.md)
- [AShell Local ADB Observed Baseline — 2026-10-13](11_LOCAL_ADB_BASELINE_2026-10-13.md)
- [Android SDK Setup Fix Baseline](10_ANDROID_SDK_SETUP_BASELINE_2026-10-13.md)
- [AShell Client Actions Baseline](09_CLIENT_ACTIONS_BASELINE_2026-10-13.md)
- [AShell Legacy ADB Retirement Baseline](08_ADB_RETIREMENT_BASELINE_2026-10-12.md)
- [PC Build Baseline — 2026-10-12](07_PC_BUILD_BASELINE_2026-10-12.md)
- [AShell identity observed baseline](06_ASHELL_IDENTITY_BASELINE_2026-10-12.md)
- [AShell 源码观察基线 2026-10-12](05_OBSERVED_BASELINE_2026-10-12.md)
- [AShell Formal Build Environment Baseline](04_BUILD_ENVIRONMENT.md)
- [AShell Dependency Baseline](03_DEPENDENCY_BASELINE.md)
- [AShell Version Matrix](02_VERSION_MATRIX.md)
- [RustDesk / DaXianDesk Upstream Baseline](01_UPSTREAM_BASELINE.md)

## 1. 文件职责

| 文件 | 记录内容 |
|---|---|
| `01_UPSTREAM_BASELINE.md` | RustDesk/DaXianDesk 来源证据、历史缺口和接管要求 |
| `02_VERSION_MATRIX.md` | AShell 产品、平台、协议/bridge 与 artifact 版本矩阵 |
| `03_DEPENDENCY_BASELINE.md` | Cargo、Flutter、Gradle、protobuf、vcpkg 等锁定状态 |
| `04_BUILD_ENVIRONMENT.md` | Android/Windows 正式构建环境、入口、外部资产和验证门 |

完整架构仍以当前源码和 `docs/AI_ENGINEERING/` 为准；外部资产状态仍以 `EXTERNAL_ASSET_REGISTRY.md` 为准。

## 2. 证据标签

| 标签 | 含义 |
|---|---|
| `SOURCE-PINNED` | 当前 tracked manifest/script/lockfile 直接固定 |
| `LOCK-PINNED` | lockfile 固定，但 manifest 可能允许更宽范围 |
| `SCRIPT-PINNED` | canonical script 固定；正式机器尚未验证 |
| `HISTORICAL` | 旧文档/workflow 的参考值，不自动成为 canonical |
| `EXTERNAL` | 值或资产由仓库外 owner 管理 |
| `DRIFT` | 两个来源不一致或声明与 lock 不一致 |
| `UNKNOWN` | 当前仓库无法证明 |
| `VERIFICATION-REQUIRED` | 需要正式环境证据 |

## 3. 基线变更控制

- 每个开发任务在 `TASK_TEMPLATE.md` 中引用 Baseline ID；不得只写“latest”。
- 产品版本、协议、身份、MSRV、Flutter/NDK/Gradle/vcpkg、driver/binary 或正式环境变化必须先做影响分析。
- 长期架构或工具链路线变化创建/更新 `docs/ADR/`；普通 lock refresh 仍需 task、review、SBOM/license 和 rollback 记录。
- manifest、lockfile、script 与正式环境证据不一致时标 `DRIFT`，不得选择一个方便的值冒充已统一。
- 基线更新只能追加新 Baseline ID；旧 baseline 不删除、不静默重写。
- accepted baseline 不授权 build、Git、version、sign、package、upload 或 release。

## 4. 当前封版边界

- 当前 source HEAD 固定，但工作树包含未提交的 AI 工程文档/Skill 资产；本目录没有把它们冒充 commit 内容。
- 本次只做 V0 静态核验，没有执行项目编译、测试、codegen、设备、服务或发布验证。
- `CS-BL-2026-07-12-77062b4` 可用于源码/文档任务；正式 release baseline 必须补齐 G2 证据、artifact hashes、signatures、SBOM 和 external asset owners。
