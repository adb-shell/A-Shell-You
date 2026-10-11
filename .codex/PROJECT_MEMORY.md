# CloudSend Project Memory

最后更新：2026-10-12

用途：AI 会话的稳定入口，不替代源码和完整工程文档。

## 0. Latest Recovery Snapshot

最新复核：[2026-10-12 接管认知](../docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md)。Baseline：`CS-BL-2026-10-12-6fb0aa6`；本地 `test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890`，非 shallow，仅一次初始化提交。原审查工作树 clean；随后 `T-2026-10-12-001` 只保存记忆文档，未提交/推送。新对话必须重新核对 HEAD/status，不能假定工作树永远 clean。

- **来源**：本地 HEAD 的975个 tracked blob 路径/hash与历史源 `Super-Build/Cloudsend@cbce0ba36c5c` 一致。源仓60提交、最早2026-04-13无父导入；准确 RustDesk fork point 未知。RustDesk1.4.0对照有615同路径同blob文件，仅证明部分继承。
- **四个关键边界**：Android core/share/frame/waiting 分离；controller relay-only 不删除 endpoint direct/NAT兼容；产品账号不是 endpoint密码认证；ZEGO邀请/Token/录音同意分离，媒体走SDK。
- **容易遗漏的当前行为**：永久密码 getter固定常量、setter不更新；Android已认证输入分支缺desktop对应keyboard gate；terminal有进程内persistent但重连回放TODO；record_upload dormant；ADB为本地子系统。
- **风险解释**：JNI正常帧保存Image buffer裸地址后延迟读取；握手有non-secure fallback；双向nonce无方向标识；Android来电3秒自动接受；默认API/token HTTP与本地token保护有边界缺口。均为源码事实/静态风险，未证明已利用漏洞或凭据当前有效。
- **当前资产**：jniLibs目录不存在，不能沿用7月本机libadb.so存在记录；ZEGO声明未进入pubspec.lock；broker有脚本内嵌Go实现，但独立工程/实际部署未知。`.info`是Windows构建输入，不是已证明的运行时配置入口。
- **验证**：仅V0；未build/test/analyze/codegen、未运行服务/设备/生产验证，未审相邻API/WEB/SERVER仓。55处Rust测试标记不等于通过；Flutter cm_test是手动harness。
- **恢复方式**：入口→状态/活动任务→本摘要→相关报告章节/源码。保持中文解释与English anchor；后续用户请求决定权限，不从已关闭任务恢复授权。文件在同一检出中可读；新worktree/机器必须实际包含这些文件才能恢复。

## 1. Entry and Source of Truth

所有 AI 先读：

1. `PROJECT_START_HERE.md`。
2. `.codex/AI_RULES.md`。
3. `docs/AI_ENGINEERING/AI_TASK_EXECUTION_PROTOCOL.md`。
4. 开发任务使用 `TASK_TEMPLATE.md`，并引用 `docs/BASELINE/BASELINE_INDEX.md`。
5. 长期决定查 `docs/ADR/README.md`；验证查 `TEST_MATRIX.md`。

工程事实层级：

1. 当前源码、manifest、build script 和 protocol definition。
2. `docs/AI_ENGINEERING/00_PROJECT_OVERVIEW.md` 与对应领域文档。
3. `docs/ENGINEERING_*` 与 `docs/TASK_ENTRYPOINTS.md`，作为旧主套件和历史细节。
4. 本目录、`AGENTS.md`、`CLAUDE.md` 只作为规则、摘要和入口，不覆盖源码。

开发任务执行 `DEVELOPMENT_WORKFLOW.md`；仓外服务、database、driver 或 binary 查询 `EXTERNAL_ASSET_REGISTRY.md`。安全 Superpowers 只通过 `cloudsend-superpowers-safe` 的五项只读 allowlist 使用，外部包未安装。冲突时以源码为准；构建/运行行为只有正式环境验证后才能从 `verification-required` 提升为已验证。

## 2. Stable Identity

- 产品/runtime：`CloudSend`。
- Android 显示名：`云计划`。
- 来源：RustDesk 深度二次开发；当前本地只有2026-10-12初始化历史，2026-04-13一次性导入来自上节映射的历史源仓；导入前 upstream commit 未确认。
- Rust crate/library：`cloudsend`。
- Flutter package：`flutter_hbb`。
- Android applicationId：`com.cloudsend.app`。
- Android deep link scheme：`cloudsend`。
- 当前源码版本：Rust `5.2.1`，Flutter `5.2.1+59`；不得由 AI 自动修改。
- 根 license：AGPL-3.0；第三方 license/provenance 尚需完整审计。

## 3. Architecture Anchors

- Rust core：`src/`。
- shared config/protocol：`libs/hbb_common/`。
- capture：`libs/scrap/`。
- Flutter：`flutter/lib/`。
- Android runtime：`flutter/android/app/src/main/kotlin/com/cloudsend/app/`。
- Android active JNI：`libs/scrap/src/android/pkg2230.rs`；`ffi.rs` 是未导出的兼容层。
- Windows privacy/display：`src/privacy_mode.rs`、`src/privacy_mode/`、`src/virtual_display_manager.rs`。
- account/API/sync/download：`src/hbbs_http/` 与 Flutter models。
- external infrastructure：hbbs、hbbr、产品 API/DB、ZEGO RTC；ZEGO Token broker 仅有 deployment script 内嵌Go实现，缺独立受控工程与部署证据。

完整架构见 `docs/AI_ENGINEERING/01_ARCHITECTURE.md`。

## 4. Non-Negotiable Runtime Facts

- Android 必须分 core service、screen share、frame source、PC waiting 四层状态。
- 只有明确用户操作可以请求新的 MediaProjection permission。
- waiting/reconnect 只可刷新已有 normal video，不可自动切 ignore/screenshot。
- Android 真实 RGBA/Texture frame 必须清 PC waiting。
- controller 当前强制 relay；受控端仍保留 rendezvous/direct/NAT compatibility code。
- Windows 当前 virtual display implementation 是 Amyuni；RustDesk IDD 分支是 dormant。
- Android ADB 是设备本地 LADB 子系统，不是 PC remote ADB protocol。
- ZEGO 媒体不走原 RustDesk audio service；peer protocol 只传 invitation/control state。
- `verify_login()` 的 legacy UI bypass 不等于产品 API、hbbs 或 endpoint authentication bypass。

## 5. Highest Risks

- 公开源仓内容有 credential 类型字面值；当前有效性和生产用途未测试，不把字面值存在等同已确认生产事件；值不得复制。
- 构建内置共享远控密码可由公开源码获知，permanent-password setter 不真正更新；需要每设备高熵凭据迁移。
- Android ImageReader DirectBuffer ownership 可能失效；JNI `static mut` 存在 data-race/UB 风险。
- Android input/custom command 在受控端的 permission enforcement 不完整。
- Android custom-scheme config import 可改写 rendezvous/API/trust key；update/plugin 缺完整 signature/hash/containment gate。
- ZEGO token transport/credential 和 Android auto-accept 存在隐私风险。
- peer secure handshake、secretbox nonce 和本地 password protection 需密码学专项审计。
- 产品 HTTP/sync 路径存在 plaintext/auth boundary 风险。
- Windows privacy/injection/virtual display 是高权限、崩溃恢复敏感区。
- 当前缺失的 `libadb.so` 和未闭环的 driver/helper assets 限制 clean-clone reproducibility。

风险详情见 `docs/AI_ENGINEERING/10_SECURITY_MODEL.md`。

## 6. Mutation Rules

- 未经用户明确确认：不 commit、push、merge、rebase、stage、release、upload、version bump、删除源码或文档。
- 当前环境不是正式编译环境：不执行 Cargo/Flutter/Gradle/Android/Windows/Docker build/test。
- 需要验证时输出《编译验证需求》，列命令、环境、目录、目标，等待正式环境结果。
- 不修改无关代码，不覆盖用户工作树，不清理历史资产。
- 源码事实变化时同步更新 AI engineering docs、decision log 与 task history。
- 所有 secret、token、password、key、生产地址和 PII 必须脱敏。
- 所有开发任务记录 Baseline ID、相关 ADR 和 TEST_MATRIX case IDs。
- Superpowers adapter 仅 brainstorming/planning/debugging/verification/review；commit/push/release hard-denied。

完整规则见 `.codex/AI_RULES.md`。

## 7. Open Asset Gaps

- 2026-04-13 之前的 upstream/DaXianDesk history。
- hbbs/hbbr source、version、config 和部署拓扑。
- 产品 backend/OpenAPI/DB/schema/migration/backup。
- ZEGO broker 独立受控 project/commit、dependency lock、production provenance 和 credential ownership；当前 deployment script 只内嵌了部分 Go server source。
- ADB binary、Windows driver/helper 的 source revision、license 和 hashes。
- 正式 Android/Windows build 与 regression evidence。

这些缺口未补齐前，不得宣称全系统接管或发布就绪。

完整状态、owner、所需材料和 release impact 统一维护在 `EXTERNAL_ASSET_REGISTRY.md`，本节只保留摘要。
