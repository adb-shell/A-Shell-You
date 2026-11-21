# ADR-0030: App-private Privileged Service After Wireless Activation

- Status：accepted for implementation
- Record Type：contemporaneous
- Decision Date / Recorded Date / Last Reviewed：2026-11-21
- Next Review：首次设备激活、切网存活与安全负例验证
- Decision Owner / Approvers：用户（本任务明确批准“只服务开放世界”及“完成这个方案”）
- Security Reviewer / Release Reviewer：Codex 依据对应领域 Skill 静态自审；独立审查 unknown — release owner required
- Scope：Android localadb、Flutter本机/PC状态及激活交互
- Related Task：[T-2026-11-21-001](../tasks/T-2026-11-21-001-embedded-adb.md)
- Related Decision Log：D-038
- Baseline / Test / Asset：OW-BL-2026-11-21-956ecf4-embedded-adb-worktree（同步前初稿609aec9另存） / ADB-P01—P12 / EXT-REF-SHIZUKU-001
- Supersedes：0018/0025 的常态 adbd 连接与自动无线恢复部分；其他授权和独立通道决策保持
- Superseded By：none
- Implementation State：test/febf7f0已推送，双端常规编译run44883517881全部SUCCESS
- Evidence / Verification：V0与Android arm64/Windows x64 V2构建通过；设备/安全验收及独立产物审查NOT_RUN，不是发布批准

## Context / Drivers

原每条执行通道重连无线 adbd，Wi-Fi或无线调试关闭使已配对身份无法继续执行。用户需要激活一次后通过既有远控链在不同网络上使用本应用能力，明确不需要第三方App授权。

## Decision / Invariants

采用 Shizuku 风格独立 app_process shell 服务，使用本应用专属 Binder 验证和 FD 数据通道；无线配对/连接仅作为显式 bootstrap。服务存活即可复用；不承诺非root全ROM永不退出。App UID不提权，无新公网监听、root获取、通用第三方Binder代理或独立Shizuku APK依赖。

保留现有remote relay鉴权/只读/developer-priority、协议字段40/41、方法名与媒体helper行为。应用恢复只绑定存活服务，服务死亡必须如实失效，不自动激活或重放Shell。

## Alternatives

| Option | Benefits | Costs / Decision |
|---|---|---|
| 持续无线adbd | 无独立服务 | 关闭无线/Wi-Fi后不能执行，无法满足需求 |
| 另装Shizuku | 成熟通用授权管理 | 用户明确排除额外App依赖 |
| 全量上游manager和第三方授权 | 可支持其他App | 超出已确认范围，扩大权限面 |
| 专用shell服务 | 保留现有功能，脱离无线端口 | 需要生命周期/隐藏接口/FD安全审查；采用 |

## Consequences / Security / License

设备所有有权限PC共享一次激活；单请求取消仅关闭自身FD。明确停用使全部能力停止，配对记录保留。Binder逐次核对完整UID和安装路径；Provider只收shell/root且验证generation/身份/真实UID。24通道上限、保留命令预算、撤销拒绝迟到回调。generation不是对抗已有shell/root攻击者的秘密凭证。现有远控端点鉴权强度及用户任意Shell副作用仍是既有风险。

独立进程仍受系统/OEM/SELinux影响，更新/重装/重启可能需激活；不存在“永久授予App shell权限”。Shizuku固定来源Apache-2.0和NOTICE随包交付，无新增二进制依赖。完整技术合同见[16_EMBEDDED_ADB](../AI_ENGINEERING/16_EMBEDDED_ADB.md)。

## Compatibility / Migration / Operations

apiVersion5追加状态，旧connected映射为服务ready；旧PC方法继续有效，新PC缺能力标志回退旧语义。原配对密钥格式不变；新增本机enabled/generation记录仅授权意图。首次升级必须显式激活，不能沿用旧连接假定服务已存在。旧PC断开会停用服务，需在混合版本验收中告知。没有服务器或版本调整、没有发布/部署；构建owner为OpenWorld维护者，正式结果未知。

## Verification

V0：调用点/差异、Manifest、固定资产、来源与权限链审查；V1—V4：ADB-P01—P12，关联ADB-R/M/A、NET-04/DSP-09，全部NOT_RUN。正式Linux/Windows命令与停止边界见领域文档。独立安全审查及Release产物证明仍待维护者提供。

## Rollback / Kill Switch

手机或有控制权PC明确停用，立刻拒绝新Binder并清理通道；配对身份保留。恢复本任务改动或安装旧APK前先停用。删除配对另用revoke；已执行系统命令/电池豁免/无障碍配置不自动回滚。验证回退到旧无线连接合同及无孤留媒体/遮罩；不得用全局kill其他Shell任务回滚。

## Approval / Amendment

2026-11-21 用户确认本应用独立能力并授权实现；授权整理test02/main/test分支，后续明确要求同步main最新基线到test并继续开发，已快进推送到956ecf4。主分支版本2.0.26一并继承，不含新改造提交或构建。Codex记录与实施；此批准不代表V2/V3或发布通过。
