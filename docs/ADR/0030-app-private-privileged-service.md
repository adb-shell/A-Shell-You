# ADR-0030: App-private Privileged Service After Wireless Activation

2026-11-27 实施补充：test/e3fd784 工作区按实际安装 ProviderInfo/类引用解析组件、private Binder v2双向身份校验；取消仅影响同generation未ready的激活，ready提交是请求所有权转交给App的边界。分阶段错误与两端停止/重启显示已补齐。沿用既有决定与授权边界；源码/V0，不代表设备或发布验收，见[T001](../tasks/T-2026-11-27-001-adb-package-reconnect.md)。下方实施状态为2026-11-21历史记录。

- Status：accepted for implementation
- Record Type：contemporaneous
- Decision Date / Recorded Date / Last Reviewed：2026-11-21
- Next Review：首次设备激活、切网存活与安全负例验证
- Decision Owner / Approvers：用户（本任务明确批准“只服务开放世界”及“完成这个方案”）
- Security Reviewer / Release Reviewer：Codex 依据对应领域 Skill 静态自审；独立审查 unknown — release owner required
- Scope：Android localadb、Flutter本机/PC状态及激活交互
- Related Task：[T001](../tasks/T-2026-11-21-001-embedded-adb.md)、[T002保活续修](../tasks/T-2026-11-21-002-embedded-adb-resilience.md)
- Related Decision Log：D-038
- Baseline / Test / Asset：OW-BL-2026-11-21-956ecf4-embedded-adb-worktree（同步前初稿609aec9另存） / ADB-P01—P18 / EXT-REF-SHIZUKU-001
- Supersedes：0018/0025 的常态 adbd 连接与自动无线恢复部分；其他授权和独立通道决策保持
- Superseded By：none
- Implementation State：T001源码febf7f0已推送且双编译成功；T002基于test/e137d44的保活续修未提交、未构建
- Evidence / Verification：T002仅V0，旧run44883517881不覆盖新修改；设备/安全验收及独立产物审查NOT_RUN，不是发布批准

## Context / Drivers

原每条执行通道重连无线 adbd，Wi-Fi或无线调试关闭使已配对身份无法继续执行。用户需要激活一次后通过既有远控链在不同网络上使用本应用能力，明确不需要第三方App授权。

## Decision / Invariants

采用 Shizuku 风格独立 app_process shell 服务，使用本应用专属 Binder 验证和 FD 数据通道；无线配对/连接仅作为显式 bootstrap。服务存活即可复用；不承诺非root全ROM永不退出。App UID不提权，无新公网监听、root获取、通用第三方Binder代理或独立Shizuku APK依赖。

保留现有remote relay鉴权/只读/developer-priority、协议字段40/41、方法名与媒体helper行为。应用恢复只绑定存活服务，服务死亡必须如实失效，不自动激活或重放Shell。

T002实施补充：暂时系统查询/验证故障拒绝未验证执行但不销毁daemon；确认身份变化或明确撤销才停止。App重建先恢复已有服务，有限探测/退避；进程lease须同时验证generation及真实ready，不能以进程存活代替授权。STOP只允许原完整owner UID且不依赖安装查询；只有INFO/OPEN需要安装证明。单独维护线程与一次性observer注册线程避免阻塞停止路径；不引入进程互拉或绕过系统强停。

## Alternatives

| Option | Benefits | Costs / Decision |
|---|---|---|
| 持续无线adbd | 无独立服务 | 关闭无线/Wi-Fi后不能执行，无法满足需求 |
| 另装Shizuku | 成熟通用授权管理 | 用户明确排除额外App依赖 |
| 全量上游manager和第三方授权 | 可支持其他App | 超出已确认范围，扩大权限面 |
| 专用shell服务 | 保留现有功能，脱离无线端口 | 需要生命周期/隐藏接口/FD安全审查；采用 |

## Consequences / Security / License

设备所有有权限PC共享一次激活；单请求取消仅关闭自身FD。明确停用使全部能力停止，配对记录保留。INFO/OPEN逐次核对完整UID和安装路径；STOP仅允许原完整UID以减少权限。Provider只收shell/root且验证generation/身份/真实UID。24通道上限、保留命令预算、撤销拒绝迟到回调。generation不是对抗已有shell/root攻击者的秘密凭证。现有远控端点鉴权强度及用户任意Shell副作用仍是既有风险。

独立进程仍受系统/OEM/SELinux影响，更新/重装/重启可能需激活；不存在“永久授予App shell权限”。Shizuku固定来源Apache-2.0和NOTICE随包交付，无新增二进制依赖。完整技术合同见[16_EMBEDDED_ADB](../AI_ENGINEERING/16_EMBEDDED_ADB.md)。

## Compatibility / Migration / Operations

apiVersion6增加reconnecting语义，connected仍映射为服务ready；旧PC方法继续有效，新PC缺能力标志回退旧语义。原配对密钥格式不变；本机enabled/generation/bootCount仅为授权意图和重启辅助信息。首次升级必须显式激活，不能沿用旧连接假定服务已存在。旧PC断开会停用服务，需在混合版本验收中告知。没有服务器或版本调整、没有发布/部署；T002构建结果未知。

## Verification

V0：调用点/差异、来源与权限链、恢复/取消/撤销审查；T002的V1—V4：ADB-P01—P18，关联ADB-R/M/A、NET-04/DSP-09，全部NOT_RUN。正式命令与停止边界见领域文档和T002任务。独立安全审查及Release产物证明仍待维护者提供。

## Rollback / Kill Switch

有控制权PC通过ADB菜单明确停用，立刻拒绝新Binder并清理通道；配对身份保留。手机页面停用按钮由用户另行授权的T003移除，既有MethodChannel方法仍兼容。恢复本任务改动或安装旧APK前先停用。删除配对另用revoke；已执行系统命令/电池豁免/无障碍配置不自动回滚。验证回退到旧无线连接合同及无孤留媒体/遮罩；不得用全局kill其他Shell任务回滚。

## Approval / Amendment

2026-11-21 用户确认本应用独立能力并授权实现；授权整理test02/main/test分支，后续明确要求同步main最新基线到test并继续开发，已快进推送到956ecf4。主分支版本2.0.26一并继承，不含新改造提交或构建。Codex记录与实施；此批准不代表V2/V3或发布通过。
