# AShell Engineering Baseline Index

最新 CI 工作树快照：[AS-BL-2026-10-13-c521601-android-sdk-setup-worktree](10_ANDROID_SDK_SETUP_BASELINE_2026-10-13.md)。T002 修复初版 sdkmanager 缺失；固定两 Android job 的 SDK setup 和 CLI 版本；修复后 V0，runner 构建待验证。原 [独立客户端 Actions 快照](09_CLIENT_ACTIONS_BASELINE_2026-10-13.md) 与以下旧快照保留冻结含义，不代表当前 HEAD。

最新 repository-observed 快照：[AS-BL-2026-10-12-13f792d-adb-retired-worktree](08_ADB_RETIREMENT_BASELINE_2026-10-12.md)。T009 删除旧 ADB 功能链/权限并排除旧 native binary，V0；构建和运行未验证。下文所称最新 PC 构建快照为此前历史状态，旧基线不重写。

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

最新 repository-observed 快照：[`AS-BL-2026-10-12-c0951b5-pc-build-worktree`](07_PC_BUILD_BASELINE_2026-10-12.md)，T005统一PC入口与纯Release打包，V0，修改后build/runtime未验证。之前的 [`AS-BL-2026-10-12-a036f31-worktree`](06_ASHELL_IDENTITY_BASELINE_2026-10-12.md) 已经f423335固化；[`CS-BL-2026-10-12-6fb0aa6`](05_OBSERVED_BASELINE_2026-10-12.md) 与下列 `CS-BL-2026-07-12-77062b4` / 01—04文件保留历史冻结含义。

基线日期：2026-07-12  
Baseline ID：`CS-BL-2026-07-12-77062b4`  
Source HEAD：`77062b4d8b63eae9a31afe288e3ac00a4f89e009`  
状态：repository-side baseline accepted；formal build/runtime baseline `verification-required`

> 本目录冻结可追溯的来源、产品版本、关键依赖和正式构建环境。它不保存 credential、生产地址或本机私有配置，也不证明未执行的 build/test 已通过。

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

