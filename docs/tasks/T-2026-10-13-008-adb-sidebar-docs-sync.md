# T-2026-10-13-008 — Shell栏与ADB后续交接文档同步

按TASK_TEMPLATE.md压缩记录T0—T8；2026-10-13启动、2026-10-14完成（Asia/Tokyo）。

## 0. Metadata

| 字段 | 值 |
|---|---|
| Task / State | T-2026-10-13-008；文档同步完成/T8，产品验证待完成 |
| Requester / Owner | 当前用户 / Codex ashell-master；Flutter/安全/发布事实静态自审，无子agent |
| Source | main/aca74467a3f83392c6a440e6507a63508f9294f6，初始clean |
| Baseline | AS-BL-2026-10-13-aca7446-adb-sidebar；Baseline15，Baseline14冻结 |
| Risk / Verification | 文档低风险；V0；产品V2/V3/V4待验证 |
| ADR / Decision | ADR-0018/0019/0020沿用，0020追加UI事实；D-025 |
| Matrix | ADB-R16—19新增；R01—09/11—15与AND-09—13保持；原链AND-01/02/04/05/07/08、FLT-04/05/06 |
| External | EXT-LIB-ADB-004—007保持；无新资产，投屏候选未选型 |
| State / Registry / Event | STATE-20261013-008 / WORK-20261013-016（交付） / CE-20261013-T008-01 |

## T0—T1. Authority and requirements

用户要求更新并同步当前文档，稍后开始ADB远程投屏传输。本轮C0只读与C1 Markdown更新；不开始投屏代码，不新增源码/依赖/协议、不执行Git写入、本地build/test/analyze/codegen、设备、CI触发/监控、删除、版本、签名、发布或生产动作。

恢复发现文档仍为efb2599 + 三份dirty的T007交付前快照，实际980f254和aca7446均已提交，当前clean：属于可解释drift而非冲突。先保护八份Dart SHA-256，再更新当前事实，旧任务/基线/事件保留历史。

验收：当前入口和domain准确描述Shell栏位置/条件/手动开关/三键、状态查询和多PC限制；无原输入链改动或投屏已实现的错误承诺；测试与交接记录齐备；源码hash不变。

## T2. Impact map

| 领域 | 证据与本轮动作 |
|---|---|
| Flutter | 只读核对RemotePage、toolbar、两侧栏、controller/menu/session state；更新模块所有权与UI合同 |
| Android/Rust/Network | 复用既有ADB请求、gate、共享manager和shell方法，aca7446不改这些源码；状态非实时push |
| Security | 三键是主动shell操作；UI状态非授权，不回退原输入或申请投屏；原只读gate保持 |
| Windows/Build | 八份Dart影响客户端编译面，但无工具链/版本/生成桥变化；成功构建结果未取得 |
| API/原产品链 | 无账户/HTTP、无障碍功能、视频、terminal、ZEGO改造 |
| 外部资产 | 无新增依赖/下载；后续投屏选型与来源许可等待下一任务 |
| Docs/Memory | 当前说明、四项测试、Baseline15、D025、任务/事件/恢复指针同步 |

当前侧栏是tracked active，早期任务/基线是historical，投屏是proposed，运行效果是verification-required。

## T3—T4. Design and confirmation

本次原请求已明确授权文档更新，无须再次确认。保留历史，仅更新当前canonical说明和入口快照；ADR0020追加UI实施证据，按ADR规则不为复用既有协议的普通UI入口另建ADR。投屏仅记录问题和隔离边界，不预选scrcpy、承载通道或发布范围。

回退限本轮Markdown差异；不恢复旧基线/QR，不覆盖他人修改，不改变任何ADB身份或系统状态。

## T5. Change record

- 13_REMOTE_ADB：已实现三键、自动显示/手动关闭、查询与可用性、会话owner、epoch/teardown、原链边界和投屏未实现。
- 00/01/02/03及12：当前功能、源码图和模块入口；修正过时“NSD仅连接端口”和“无ADB输入”表述。
- 08/10/外部登记：当前构建证据、安全边界、无新增投屏依赖。
- TEST_MATRIX：R13对齐事件查询，新增R16—19且均NOT_RUN。
- 11_ROADMAP：后续采集/传输/解码、所有权/多PC、隔离/兼容与验证待设计。
- Baseline15、ADR0020补录、入口及七份.codex记忆：来源与当前交接。精确范围见CE-20261013-T008-01。

## T6. Verification

V0完成：源码事实与文档对照、26份Markdown修改范围、145个本地文件链接均有效，git diff --check通过；八份Dart SHA-256与开始同步前完全一致。没有改动产品源码。V1/V2/V3/V4未运行，未取得当前SHA的构建/真机结果，不能将静态核对写成运行通过。

《编译验证需求》：Windows正式环境在仓库根执行`build.cmd`；Android正式Linux环境在仓库根执行`./build.sh 1`（既有aarch64入口），安装匹配客户端后验证R16—19与原链回归。正式环境要求沿用08_BUILD_SYSTEM与Baseline04；这里只记录后续验收，不在本次执行。

## T7—T8. Handoff

T008文档同步完成并关闭，26份Markdown待提交；本轮未提交/推送或触发构建。下一任务以新的HEAD/status恢复上下文，先设计ADB独立投屏传输，不把Shell可用当视频ready、不把单次命令JSON当持续媒体通道、不复用原无障碍/视频链的授权状态。当前无新投屏代码/资产，既有侧栏与ADB运行仍待正式结果。
