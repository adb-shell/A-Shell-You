# AShell 工程路线图 / Roadmap

接管基线：2026-07-12  
状态：`proposed`

> 本路线图是接管后的工程建议，不表示已获开发、发布、密钥轮换、历史改写或基础设施操作授权。所有业务逻辑修改和外部状态变更均需单独批准。

## 1. 排序原则

优先级按以下顺序确定：

1. 已暴露 credential、远程权限和内存安全。
2. 无法复现构建、无法回归和失去证据链。
3. 协议/状态机稳定性和生命周期债务。
4. 架构解耦和维护效率。
5. 新功能。

任何新功能都不能以扩大 P0/P1 安全风险或绕过正式验证为代价。

## 2. Phase 0：控制暴露面

目标：在不扩大故障面的前提下建立可信安全基线。

### P0-1 Credential incident response

- 确认 public repository 暴露范围和 credential 当前有效性。
- 由 owner 轮换/撤销 ZEGO、Token service、API 等相关 credential。
- 将文档/脚本默认值模板化并接入 secret store。
- 评估 Git history rewrite、clone/fork/cache 和部署端更新顺序。
- 添加受控 secret scan gate。

需要明确授权：secret rotation、远端配置、history rewrite、force-push 和部署均不由 AI 自动执行。

### P0-2 Transport and authentication

- 所有产品 API、sync、ZEGO token broker 迁移 HTTPS。
- `secure_connection` 失败改为 fail-closed，并设计兼容开关/版本窗口。
- 复核 secretbox key/nonce 方向隔离。
- 服务端强制 Android input/custom-command permission。
- 把 Dev selector UI password 明确限定为 UI gate，协议另设 authorization。

### P0-3 Android native memory

- JNI callback 内复制 ImageReader plane 到 Rust-owned buffer，或建立可证明的 ownership contract。
- 用 atomic/lock/state object 替代跨线程 `static mut PIXEL_SIZE*`。
- 修复 scale factor 0 与物理/逻辑宽度比较。
- 同步保护 screenshot/Accessibility shared buffer。
- 验证 MainService replacement 期间 GlobalRef 生命周期。

### P0-4 Voice privacy

- 取消 Android 自动接受、cancel=accept 和无拒绝入口。
- 麦克风启用必须有当前用户明确同意和可见状态。
- Token 绑定 authenticated user/peer/room/TTL。

## 3. Phase 1：建立可重复工程基线

### P1-1 Reproducible build

- 固定 Rust/Flutter/NDK/JDK/vcpkg/FRB 版本。
- 将仍使用的 Windows drivers/DLL helper 纳入有 hash、license、source revision 的 artifact registry。
- 建立 Android/Windows clean-clone build manifest。
- 生成 SBOM 与 third-party notices。

### P1-2 CI quality gates

- 恢复最小 PR checks：format、lint、unit、bridge drift、secret scan、dependency policy。
- Actions 固定 immutable revision、最小 token permission、隔离 untrusted PR。
- 大平台 build 可先 nightly/manual，但结果必须回写 commit status。
- 禁止未审查 workflow 执行签名或发布。

### P1-3 Test baseline

- Android 四层状态机与 MediaProjection regression tests。
- controller relay-only、auth、permission 和 reconnect protocol tests。
- Flutter FFI/event/timer lifecycle tests。
- file/terminal/path fuzz/property tests。
- Windows privacy/Amyuni recovery matrix。
- API contract/timeout/malformed/oversize tests。

### P1-4 Observability

- 全链 session correlation ID。
- structured event/checkpoint，统一 redaction。
- Android frame source 与首帧 metrics。
- relay、auth、permission、voice 分层 error taxonomy。
- 可控诊断包和 retention policy。

## 4. Phase 2：偿还结构性技术债

### P2-1 Flutter lifecycle and state

- 明确 global/session model ownership。
- 消除 `ServerModel` timer 泄漏和无效 controller dispose。
- 收敛 Provider/GetX/StatefulWidget 边界。
- 为 multi-window/session teardown 建立统一 lifecycle。

### P2-2 Android runtime state object

- 将 core/share/frame/waiting 的 contract 固化为事件和状态转换。
- 拆分超大 MainService/AccessibilityService 文件的职责。
- 将 `pkg2230` JNI 命名/ownership 封装成受测接口。
- 决定 `ffi.rs`/`ffi.kt` 兼容层的保留周期；未批准前不删除。

### P2-3 Network policy

- 明确“controller-only relay”还是“全产品 relay-only”。
- 如果全局 relay-only，逐项关闭 direct server、LAN、NAT/STUN 兼容面并做旧客户端策略。
- 协议权限集中到受控端 capability gate。
- version/capability negotiation 文档化。

### P2-4 API contracts

- 接管后台仓库、OpenAPI、DB schema、migration、backup/restore。
- 统一 Dart/Rust HTTP client policy。
- endpoint 配置集中化与环境分层。
- record upload 在完成 consent/security design 前保持 dormant。

### P2-5 Windows isolation

- 把 privacy/injection/driver 作为高权限子系统隔离。
- 清理 UB/unsafe initialization 和 process-local display count。
- 明确 Amyuni 与 dormant RustDesk IDD 路线，避免双实现配置混用。

## 5. Phase 3：治理与可维护性

- 补录上游 RustDesk/DaXianDesk 基线、license provenance 和本地修改边界。
- 所有重大决定写入 `.codex/DECISION_LOG.md`。
- 所有实施任务写入 `.codex/TASK_HISTORY.md`，包含验证和 rollback。
- 重写 AShell README、security contact、contribution ownership；旧上游文档保留历史标识。
- 建立 code owner/domain owner、review SLA 和 incident owner。
- 采用规范 commit/PR subject；保留可审计变更原因。

## 6. Phase 4：产品能力候选

只有 Phase 0/1 达到可接受基线后再评估：

- PC远程ADB及完整Shell栏的正式构建与多PC验收（源码4d3c446/V0，见13_REMOTE_ADB、14_ADB_MIRROR和最新ADB矩阵）。
- 更完整的设备管理/分组能力。
- terminal persistence。
- 多平台正式支持扩展。
- record upload。
- 新 capture/virtual-display backend。

旧本地LADB保持退役；8662f23本机模块已扩展专用PC ADB协议、Android共享manager与状态，aca7446的Shell栏通过独立ADB命令实现返回/主页/多任务。T-2026-10-14-001新增独立ADB投屏与ADB优先触摸，见 [14_ADB_MIRROR.md](14_ADB_MIRROR.md)，V2/V3/V4仍待证据。完整键盘/文字输入未迁移；不能把ADB shell可用等同于视频可用。

### ADB当前实现与后续候选（2026-10-16 T001）

源码4d3c446已集成scrcpy5.0.1独立投屏、系统截图无视、节点绘制穿透、白字黑屏、多PC共享与ADB优先触摸，配对/UID验证、独立命令通道、真实状态推送及模式化停止保持。节点与黑屏文字在App绘制，helper只采集节点或显示PNG；PC分组状态与提示精简已实现。详见[当前合同](14_ADB_MIRROR.md)、[Baseline21](../BASELINE/21_ADB_RENDER_UI_BASELINE_2026-10-16.md)。当前V0；不得将源码完成等同于可发布。无障碍权限控制和开发者入口已回退，不列为当前能力或默认待恢复任务。

后续先完成Android/Windows正式构建与目标ROM、多PC、原链并行验收，包括AdbStreamCompat/R8、黑屏图层排除采集、无普通共享时触摸、快速开关/取消/断链恢复。没有实测数据前不承诺“与普通投屏同样流畅”。

以下仅为候选，不是已实施或当前任务授权：

- 独立ADB键盘、文本/中文输入、剪贴板与ADB画面滚轮/多指能力；现有Shell命令不等于完整输入体验。
- 测量RGBA本机桥复制/base64开销，再判断是否需要专用texture；当前网络H.264与PC本机RGBA必须区分。
- 按实测优化截图/节点的耗时和带宽；系统不给出的节点/受保护像素不能通过状态或绘制伪造。
- 如有明确需求，再设计持续流式终端；当前单次命令仍是20秒/64KiB、非交互。

旧T008投屏交接输入由ADR0021—0026和对应任务记录承接，历史来源见[Baseline15](../BASELINE/15_ADB_SIDEBAR_BASELINE_2026-10-13.md)，不再作为当前未实现清单。本轮仅更新文档，按授权提交推送后单次both/aarch64编排，不监控；不新增上述候选功能。

## 7. 决策门

Gate 的当前规范定义、证据和授权关系以根目录 `DEVELOPMENT_WORKFLOW.md` 为准；本节只说明路线图阶段需要经过哪些 gate，不提供 AI 执行动作授权。

| Gate | 进入条件 | 批准者 |
|---|---|---|
| G0 安全处置 | credential owner、影响面、轮换/回滚方案 | 项目 owner + security/ops |
| G1 修复开发 | design、兼容矩阵、test plan | Principal Engineer + domain owner |
| G2 正式构建 | clean baseline、toolchain、secret policy | Release owner |
| G3 发布 | 全矩阵结果、SBOM、签名、rollback | 项目 owner + release/security |
| G4 删除/迁移 | 知识已迁、引用已替、恢复点明确 | 文档/代码 owner |

本轮只完成资产接管，未跨越任何 gate。

## 8. 90 天建议节奏

### 0—14 天

- 完成 credential incident decision。
- 补齐外部服务/后台/构建 artifact owner。
- 在正式环境执行接管验证矩阵。
- 对 Android frame ownership、input permission、ZEGO consent 做设计评审。

### 15—45 天

- 实施获批的 P0 修复。
- 建立 clean build manifest、artifact provenance、最小 CI。
- 增加协议/Android/Flutter 生命周期核心回归。

### 46—90 天

- 完成 TLS/credential/storage 迁移。
- 收敛 Android/Flutter 状态与 lifecycle。
- 接管 backend/API/DB 文档。
- 完成 Windows privacy/virtual display 专项安全验证。

时间只是建议；以风险关闭证据而非日历作为阶段完成标准。

## 9. 完成定义

路线图条目只有在以下条件全部满足时才可标为完成：

- 设计和 owner 已记录。
- 代码/配置 diff 已审查。
- 正式环境验证已回填。
- security/privacy/license 影响已评估。
- rollback 已演练或明确。
- 文档、memory、decision log、task history 已同步。
- 需要发布的变更已取得单独发布授权。

“代码已写”“本机能跑”或“没有收到报错”都不是完成定义。
