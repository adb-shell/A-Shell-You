# ADR-0017: Integrate an Isolated Phone-local ADB Client

- Status：accepted（用户明确授权自主选型、设计并实现本机 ADB；本记录补记该决定）
- Record Type：backfill
- Decision Date：2026-10-13（8662f23 实现提交日）；Recorded / Reviewed：2026-10-13
- Decision Owner / Approver：当前产品用户；原要求见 T003 的授权摘要，非未来动作授权
- Primary / Security / Release review：Codex 使用 ashell-master、Android/Flutter 领域与 Security/Release 静态约束核对；非独立人工审计
- Related Task / Decision：T-2026-10-13-003 / D-021
- Baseline / Cases / Assets：AS-BL-2026-10-13-f84a78f-local-adb；AND-06/08/09—13、FLT-04/05/06；EXT-LIB-ADB-004—007
- Supersession：补充 ADR-0015；只推进其“未来另接新组件”阶段，旧组件退役决定继续有效；不复活 ADR-0007 的旧实现
- Implementation：8662f23 已提交；f84a78f 复核模块未变
- Verification：源码/V0；V2/V3 结果未提供。下次审查在正式构建、真机反馈或远程化设计时进行

## Context and Drivers

用户要求在主页原右侧位置新增模块化 ADB，以 Android 无线调试获得 shell，为后续 ADB 投屏准备。原产品信息、无障碍、画面传输、远控链路必须保持；先实现代码，测试后再整理文档。旧 LADB runner、权限和 binary 已按 T009 删除，不应从旧记录直接恢复。

## Decision

使用 `libadb-android:3.1.1` 作为协议依赖，在 App 内新增 Java localadb 包与 Flutter 页面。三个小接入点分别负责页面挂载、插件注册和 Gradle 依赖；不导入整个上游 App，不拷贝旧 `libadb.so`。详细类/通道/依赖合同以 [12_LOCAL_ADB.md](../AI_ENGINEERING/12_LOCAL_ADB.md) 为准。

| 方案 | 取舍 | 本次处置 |
|---|---|---|
| 独立协议库 + 小型本机模块 | 可限定接入点和依赖图；仍需处理 TLS/身份/取消、JNI 及库兼容 | 已采用 |
| 恢复旧 LADB runner / binary / 自动化 | 会恢复 T009 退役链和来源依赖，不符合新组件隔离要求 | 未采用 |
| 导入 AShellYou 或 LADB 整个 App | 页面/生命周期/构建耦合扩大，超出只增加本机能力范围 | 未采用 |
| 同时增加 PC ADB 或 scrcpy | 需要新增授权、协议与画面/输入链路 | 留待独立设计，未实现 |

## Invariants and Consequences

- 仅 Android API 30+ 配对码流程，固定 loopback、本机用户主动 pair/connect/shell；无 mDNS、自动启用调试或后台重连。
- 原 MainService、Accessibility、MediaProjection、Rust/JNI、peer protocol、terminal、ZEGO 不承载本模块。connected 不映射产品共享/首帧状态。
- 配对成功与连接分离；连接经 `id` 验证。单次 shell-v2 提供真实 exit code；20 秒取消 watchdog、64 KiB 输出上限，不提供交互 PTY。
- 手动端口、ROM loopback/分屏限制、上游 EOF/key lifetime 兼容处理和新 JNI 依赖增加了维护面，必须正式验证。
- 新 RSA 身份以 AndroidKeyStore AES-GCM 包装存储，不迁移旧 key。配对码不保存；连接断开不等于撤销系统信任、清除私钥或终止后台子进程。
- 本地任意 shell 尚无 command allowlist/持久审计/逐条批准；不能把本模块直接当作已批准的远程命令服务。也没有新增对既有远控触控 UI 的隔离。

## Build, License and Verification

新增固定版本 libadb、SPAKE2、BC、Conscrypt，前两者关闭传递依赖；沿用产品 SDK/Gradle/AGP/Java、Rust 和 Flutter 生成链。Release keep 保护反射与 JNI；旧 libadb.so 排除继续生效。

libadb 采用上游 Apache-2.0 选项；SPAKE2 的 LGPL-3.0 和未审计状态单独登记；BC、Conscrypt 的 license/notice 见模块文档。尚未取得最终 AAR/JAR/SO hash、SBOM 和分发材料，不能声称完整可复现或发布就绪。资产 owner 为 OWNER-REQUIRED。

V0：当前源码与 8662f23 模块对比、调用合同/常量、文档引用和变更范围检查。V2：正式 Linux 两构建模式/各 ABI/Release shrink；V3：AND-09—13 与原 AND-01/02/04/05/07/08、FLT-04/05/06 回归。证据未回填时保持 NOT_RUN；只记录验证要求，不在文档任务执行。

## Migration, Rollback and Revoke

无协议字段或产品配置迁移，旧 prefs/系统授权不自动操作。回退应未来经授权审阅 8662f23 的 12 路径，去掉三个钩子与九个独立文件，保护后续 CI/产品改动；不能整仓 reset。运行时可断开 ADB，系统忘记配对才撤销该系统信任；本版没有独立重置 key UI。撤回代码不会回滚 shell 已造成的系统变化。

## Amendment History

- 2026-10-13：按用户当前文档同步请求补记；架构实现已经提交，本轮仅 Markdown/V0，无代码、构建、设备或 Git 写入。
