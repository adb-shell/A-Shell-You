# ADR-0016: Independent Manual Client Actions

- Status：accepted；Record Type：contemporaneous；Decision / Recorded / Reviewed：2026-10-13；Next Review：首次 runner 构建后。
- Decision Owner / Approver：当前项目用户；Security / Release reviewer：Codex 静态复核，正式发布 reviewer 未指定。
- Scope：独立 Android/Windows CI；Task：T-2026-10-13-001；Decision：D-020。
- Baseline：AS-BL-2026-10-13-8662f23-client-actions-worktree；Cases：CI-01—04；Assets：EXT-CI-001 / EXT-BUILD-004。
- Supersedes：none；补充 ADR-0010，继续 manual-only。Implementation：workflow source implemented；Evidence：V0，V2 NOT_RUN。

## Context and Decision

用户明确要求清理无用 GitHub 编排，CI 环境配置独立于其他 Linux/Windows 本地构建机，选择 Android/Windows/同时构建，Android aarch64/universal，默认测试签名且产物只供 Actions 下载。

采用唯一 `workflow_dispatch` 的 `build-clients.yml`，CI 直接配置安装工具链并调用 vcpkg/cargo-ndk/Flutter、Windows build.py/portable generate.py。删除旧多平台、发布、bridge、子模块更新与缓存清理 workflows；解除本地脚本对已删 workflow 的版本读取。保留 ADR-0010 的手动触发边界和本地构建入口。

## Alternatives and Invariants

| Option | Benefit | Cost / Decision |
|---|---|---|
| 调用 build.sh/build.cmd | 少量编排 | 隐式依赖本地路径/交互环境，用户明确排除 |
| 修补旧多平台 publish 链 | 沿用上游结构 | 包含多余平台/旧身份/发布与 secret 依赖，拒绝 |
| 独立手动客户端工作流 | 输入和产物清晰，runner 环境可审查 | 需维护与本地脚本相同的底层编译约束，采用 |

产品版本、applicationId、bridge、protocol、runtime 行为不改。当前 HEAD 新 local ADB 不属于已退役旧 libadb.so，继续保留。Windows 只打包编译 bundle，不重新引入外部 driver/helper。

## Security, Assets and Consequences

contents: read、direct Actions immutable SHA、checkout 不保留凭据、不自动对 PR/push 执行、不使用 production signing secrets、不创建 Release/tag。Android 临时测试 key 每次变化，跨签名安装不能原地覆盖；Windows unsigned。成品定向上传 Actions，保留 14 天，中间 JNI 1 天；不收集 key.properties/keystore 或日志全集。

工具链沿用现有主要版本；hosted image、外部依赖解析和实际 SDK/plugin 行为未实测，不能宣称完全可复现。完整签名/SBOM/许可、设备回归及发布仍是独立门；Actions 下载不是产品发布。

## Verification and Reversal

V0：YAML/矩阵、脚本语法、输入/权限/产物/元数据依赖审查。V2：CI-01—04 首次运行回填 commit、run URL、工具链和 artifact hashes。V3：沿用 Android/Windows 安装/运行矩阵。

回滚以 8662f23 的对应 workflow/脚本路径及本任务 patch 为单位；不整仓 reset、不改历史、不删除业务或用户数据。尚未执行远端 dispatch，因此没有已发布 artifact 或 rollout 状态需要撤回。

## Approval

2026-10-13 当前用户直接授权上述 CI 路线、旧编排清理、默认测试签名和 Actions-only 产物；该决定不授予本地执行、Git 写入或正式发布权限。
