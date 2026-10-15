# T-2026-10-16-001 — 同步当前ADB文档并推送编排

## 0. Metadata / Task Brief

按根目录TASK_TEMPLATE.md记录；日期2026-10-16（Asia/Tokyo）。Requester/Product Owner为用户；Primary为Codex/ashell-master，领域采用Android、Flutter、Network知识边界，Security/Release进行静态边界核对。未启用并行agent。

| Field | Value |
|---|---|
| Status / T-State | 文档与V0完成；T8交付记录，Git推送及单次dispatch回执另见会话 |
| Source / Initial state | main/4d3c44640c4b001b9af2c3106e809564f8b615cf；本地/远端一致，clean |
| Baseline | AS-BL-2026-10-16-4d3c446-adb-render-ui / Baseline21；Baseline20保留历史 |
| Risk / Verification | 文档低风险；本轮V0，产品V2/V3/V4待正式结果 |
| Decision | D-031 / ADR-0026，补录已有绘图进程边界 |
| Matrix / Assets | ADB-M26—M30新增待验，既有M01—M25、R01—R22持续；EXT-LIB-ADB-004—007、EXT-BIN-SCRCPY-001不变 |
| Memory | STATE-20261016-001 / WORK-20261016-001 / CE-20261016-T001-01 |

## T0—T1. Authority and acceptance

用户明确：“同步并更新文档，完成后推送并执行编排。”本轮C1允许文档同步；C3允许暂存/提交/推送既有origin/main及Build AShell clients单次dispatch（platform=both、android_arch=aarch64）。延续用户不监控编排的要求。未授权新增业务功能、历史重写、依赖/版本/签名/发布/生产配置变更；不执行本地产品build/test/analyze/codegen或设备操作。

已恢复项目入口、规则、任务协议、领域技能及记忆；旧记忆停留7607349，与4d3c446存在已实现代码差异，按源码更新。验收是当前合同、历史回退、验证边界一致；不得恢复此前已回退的开发者菜单或无障碍权限控制。无初始用户dirty文件需隔离。

## T2—T3. Impact and design

| Domain | Source / impact | Evidence |
|---|---|---|
| Android | localadb下Capture/Nodes/Snapshot/Renderer及PrivacyHint/Overlay/LocalAdbPrivacy；说明App绘图、helper采集/图层职责 | 源码verified，设备效果verification-required |
| Flutter | overlay.dart、remote_adb_sidebar.dart、remote_page.dart；状态行/提示、真实phase显示 | 源码verified，运行UI待验 |
| Rust/JNI/Network | 字段40/41及原普通画面/输入链无本轮变化，私有节点帧不属于新增peer协议 | 差异verified |
| Security | 原认证/只读gate、密码节点遮盖、内存数据限额与不抑制原服务保持 | V0，不冒充动态安全证明 |
| API/Windows runtime | N/A，本轮没有实现变化；PC仅文档说明已有Dart展示 | diff verified |
| Release/assets | 工作流/工具链/固定libadb、scrcpy与hash保持；无字体文件或外部资产下载 | tracked/external登记保持 |
| Docs/memory | 更新12/13/14、领域入口、测试矩阵、资产登记、ADR/基线/任务和薄记忆 | 本轮唯一编辑范围 |

选择按当前源码重写变更段落、保留历史任务/基线；不把所有旧文档批量替换成“当前”，不将用户报告或dispatch回执写为测试通过。ADR0026补录进程职责变化；其他架构决定沿用。没有持久化数据/网络schema迁移。文档回退可通过独立revert恢复，若需执行仍按届时授权；本次无不可逆数据操作。

## T4—T5. Confirmation and change record

当前用户指令已给出文档、Git和编排授权，无需重复确认。实际修改均Markdown：

- 当前合同：12_LOCAL_ADB、13_REMOTE_ADB、14_ADB_MIRROR及相关领域入口，明确helper节点流、App排版、字体回退、遮罩layerStack与PC展示。
- 验证/资产：TEST_MATRIX新增M26—M30全部NOT_RUN；资产登记说明无新增字体/图片依赖。
- 记录：ADR0026/D031、Baseline21、本文及索引、入口和.codex记忆。
- 回退记录：393acaa为两次恢复点，当前4d3c446仅包含随后明确要求的两处文字移除；已回退的无障碍权限控制和开发者入口不是当前功能。

## T6. Verification record

V0核对当前源码、提交差异、符号/调用入口、原链范围和Markdown本地链接；提交前检查git diff空白与仅Markdown范围。未运行本地构建/测试/analyze/codegen或设备；不更改原业务源码、工作流或依赖。检查命令结果和本次commit/push/dispatch准确回执由会话交付提供，本文不预写外部动作成功。

本轮静态检查结果：31个Markdown文件、220个本地链接目标有效、无非Markdown差异，`git diff --check`通过。当前ADB模块内未检出已回退的developerOptions/accessibilityPause/accessibilityResume/accessibilityEnable/accessibilityDisable或相关helper符号。以上是文档/源码范围证据，不是构建或设备通过结果。

《编译验证需求》：本次授权在GitHub Actions执行build-clients.yml，ref=main、both/aarch64；用于Android/Windows正式构建。后续验收需安装对应产物，执行ADB-M26—M30及已有双PC/原链回归。Android11—17及OEM兼容性仍待逐项证据，不监控本次CI、不据204更新测试结果。

## T7—T8. Documentation and handoff

STATE-20261016-001、WORK-20261016-001、CE-20261016-T001-01与D031保持同一源码快照；全量调用细节集中在14_ADB_MIRROR，其余入口只做导航。旧Task/ADR/Baseline保留时点含义。

当前文档完成后提交推送并单次dispatch，不执行Release/tag、不监控编排。最终文档提交SHA、远端同步及接受回执见Git/本轮最终交付；运行兼容性、构建结果和真机性能仍待用户后续提供证据。任务记录不授权后续会话自动继续代码或发布工作。
