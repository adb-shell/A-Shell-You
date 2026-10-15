# ADR-0026: ADB节点读取与App绘图分离

- Date / Status：2026-10-16 / accepted implementation，retrospective backfill；D-031 / T-2026-10-16-001。
- Source：227bbe0、a2fc3c3、e5c80ad；当前4d3c446。本轮仅补录既有实现，无新增产品行为。
- 补充ADR-0023/0024/0025；旧节点helper绘制JPEG的实现描述由本文取代，原采集/输入隔离边界保持。

## Context

用户先后反馈节点不可用、穿透helper退出134、手机黑屏无反应。退出码134通常对应原生SIGABRT，单凭反馈或源码不足以确认某个ROM的具体崩溃根因。将字体、系统context与绘图放进shell app_process会引入额外运行环境依赖，既有实现已将这些工作移回正常App进程。

## Decision

1. `AdbLayoutCapture` 只建立UiAutomation、按请求读取节点和回传阶段/失败，不调用ActivityThread.systemMain、不初始化字体或编码JPEG。保留DONT_SUPPRESS及私有租约，不停止原AccessibilityService。
2. `AdbLayoutNodes` 汇集默认显示窗口和活动/焦点根，清理自身缓存、去重并限制节点/深度。`AdbLayoutSnapshot` 对节点数量、文本和序列化长度设限，密码遮盖；只通过私有ADB流传到App，不改变字段40/41。
3. `AdbLayoutRenderer` 在App用软件Bitmap/Canvas、StaticLayout排版，公开逻辑字体SANS_SERIF及Android回退；数字红色、其余白色，节点按可点击性绘框。采集前后及编码后核对真实几何；失败与有效空节点图分开报告。
4. `AdbPrivacyHint` 在App生成有界白字PNG，`LocalAdbPrivacy` 通过启动帧送给helper。`AdbPrivacyOverlay` 只显示像素，黑层绑定真实layerStack，白字子层沿用截图排除/可信遮罩设置；READY在事务应用后发送。

## Alternatives and consequences

继续让shell helper初始化系统context和字体无法消除进程环境依赖；绑定厂商字体文件不具备通用路径；打包新字体增加资产/许可维护，本轮均未采用。公开字体族依赖Android字体回退，仍需目标设备验证，不能承诺任意ROM不会失败。

节点穿透仅重绘系统实际提供的结构，不生成不存在的文字或受保护像素。私有节点格式和PNG只驻内存且有界，不引入磁盘dump。App承担排版/压缩成本，helper承担节点访问；节点目标约2fps，不代表达到实测性能。

## Verification, compatibility and rollback

当前V0源码/文档核对；ADB-M26—M28及原并行/关闭矩阵待正式构建和真机。Android11—17是验证目标，不能写成已全部适配验证通过。隐藏API、其他UiAutomation占用、目标App不提供节点、OEM截图排除行为均需区分。

两个私有helper随同APK更新，网络协议、配对身份、外部依赖和原无障碍服务均不变。文档回退不改变运行行为；如需实现回退必须另行明确范围，不能以恢复旧helper绘图来自动扩大本任务。

当前合同：[媒体与触摸](../AI_ENGINEERING/14_ADB_MIRROR.md)；基线：[Baseline21](../BASELINE/21_ADB_RENDER_UI_BASELINE_2026-10-16.md)。
