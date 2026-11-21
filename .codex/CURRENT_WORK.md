# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261121-005-r2`
Last Updated：2026-11-21
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

none。T005已完成main快进合并推送及两支对齐；回执仅文档补交，主工作区保留test。未触发/监控CI，现有构建结果unknown。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

2026-11-21 / T-2026-11-21-005 / complete T8：main/956ecf4快进合入test/e82f8df并push成功，四个既有提交完整保留、无冲突；完整tree一致，远端main/test均e82f8df、本地0/0。入口/任务/全局记忆与内置ADB文档同步后关闭；本回执只补Markdown，两支保持相同SHA，主工作区保留test。版本不变，不新增构建/监控/设备或发布动作。STATE/WORK-T005-r2 / CE-01/02 / N/A — no decision delta。[任务](../docs/tasks/T-2026-11-21-005-embedded-adb-main-merge.md)。

2026-11-21 / T-2026-11-21-004 / complete T8（调整后的Git交付与触发范围）：T002保活与T003文案已一并提交推送test/b12cbdb，32文件757增/224删，远端SHA一致；18:38:40 Asia/Tokyo单次Both/常规编译HTTP204，run44890553076精确head_sha。用户随后要求不监控，已停止所有后续运行/日志/产物查询，不取消或重触发，最终结果unknown；最后18:40:22两平台准备环境中。任务与Build/Debug/Matrix/全局记忆同步后关闭，回执仅文档补交；main/版本保持，设备NOT_RUN。STATE/WORK-T004-r3 / CE-01—03 / N/A — no decision delta。[任务](../docs/tasks/T-2026-11-21-004-adb-resilience-build.md)。

2026-11-21 / T-2026-11-21-002 / complete T8（源码范围）：test/e137d44工作区完成暂时异常不误停、恢复窗口/退避/boot-count、授权lease与独立维护/双observer、激活复用取消竞态、有限电池豁免重试。7份Java与4份Dart恢复语义，11文件初次/7份Java最终词法检查、diff及文档链接V0通过；正式构建/真机NOT_RUN，旧CI不覆盖。本轮未提交推送或触发CI；T003独立文案/布局已保留，手机停用按钮由其按用户要求移除，PC停用可用。全部历史/状态同步后关闭T002，保留下方T003关闭记录。STATE/WORK-20261121-002-r2 / CE-T002-01/02 / D-038、ADR0030 / [任务](../docs/tasks/T-2026-11-21-002-embedded-adb-resilience.md)。

2026-11-21 / T-2026-11-21-003 / complete T8（源码/V0）：双端ADB文案精简和排版完成；按补充移除Android标题图标及停用按钮、刷新内嵌状态卡、圆角对齐主页13、删除双端Shell常驻限制说明。用户允许跨聊天协调，T002 Java及Dart恢复逻辑保留；PC仍可停用。静态差异/词法/回调守卫比较通过，未编译/真机/提交推送。任务、12/13领域文档、Task History/Changelog/State/Event已同步后关闭本任务；STATE/WORK-20261121-003-r2 / CE-01/02，N/A — no decision delta。[任务](../docs/tasks/T-2026-11-21-003-adb-ui-copy.md)。T002独立活动状态保持。

2026-11-21 / T-2026-11-21-001 / complete T8：源码febf7f0f20460a0bfffbc0ee0a2581828a30d7c0已推送test；Both/常规编译run44883517881精确匹配，于17:46:24 Asia/Tokyo全部SUCCESS，Android Native/APK和Windows EXE及既有包校验/上传均通过。产物链接、使用步骤、临时测试签名及设备待验边界已记录；STATE、TASK_HISTORY、CHANGELOG、Decision/ADR证据和Change Event同步后关闭本 registry。本批仅回执文档补交、不二次编译；main仍956ecf4，版本2.0.26保持。STATE/WORK-20261121-001-r5 / CE-05—07；设备/蜂窝/OEM/安全负例NOT_RUN。[任务](../docs/tasks/T-2026-11-21-001-embedded-adb.md)。下方为前阶段历史。

2026-11-21 / T-2026-11-21-001 / complete T8：test02删除，main已有合并推送；按用户后续要求test快进同步并推送956ecf4，远端main/test同SHA、0/0。保全并恢复29份代码/清单/许可，16份文档冲突已解决；当前test工作区内置ADB方案源码完成、未提交，版本沿用2.0.26。无线调试仅用于显式激活，常态Binder/FD执行，服务失效需重新激活；V0检查完成，正式V1—V4全部NOT_RUN。无新改造提交推送/构建/CI/发布。用户分支对齐流程已记录；stash 3bd0f1e保留恢复点。STATE/WORK-20261121-001-r2 / CE-20261121-T001-01—04 / D-038 / ADR-0030 / Baseline29。[任务](../docs/tasks/T-2026-11-21-001-embedded-adb.md)。下方保留此前关闭记录。

2026-11-15 / T-2026-11-15-002：main/d75a950合入test/609aec9，提交说明“增加无障碍”。按用户明确选择采用test完整服务器配置，Android/PC1当前相同、PC2独立；UDID显示移除来自test，既有ADB无障碍功能及其余917个非Markdown文件保持main，版本2.0.26 / 2.0.26+59。AMG-02来源与文档冲突检查通过；complete/T8仅本地合并提交交付，具体SHA以Git为准。本轮未推送、未编译或监控，test工作区与分支保持。 [任务](../docs/tasks/T-2026-11-15-002-accessibility-main-merge.md) / STATE、WORK-20261115-002-r2 / CE-20261115-T002-01/02；Baseline28，沿用既有ADR。

以下为双方历史指针：

2026-11-15 / T001交付回执：源码696a87bb294088a57f59d89fce3f3d266ca3dab5已提交并推送origin/test，提交说明“移除PC右下角UDID显示”，触发前本地/远端SHA一致且工作区clean。2026-11-15 09:27:19 UTC（18:27:19 Asia/Tokyo）向Build-Clients-BT.yml（394791509）发送ref=test、Platform=Both、BuildMode=常规编译，HTTP204受理。按用户要求不监控，未查询新run/jobs/logs/artifacts，最终编译结果未知。 complete/T8仅关闭本次提交推送与触发范围；STATE、WORK-20261115-001-r3 / CE-20261115-T001-03。补回执只改文档，不二次编排。

[任务](../docs/tasks/T-2026-11-15-001-hide-desktop-udid.md)。V0与交付完成不表示构建或设备验收通过。

以下为原编辑阶段与前次任务历史指针：

2026-11-15 / T-2026-11-15-001：在clean test/caf0702上移除PC右下角识别码/UUID显示与双击复制，唯一产品源码为connection_page.dart两块展示代码（53行）。状态/服务器切换、账号UUID、Rust桥接、Android与配置保持。V0精确差异及diff检查通过；未编译/运行、未提交推送，complete/T8仅源码编辑交付。 [任务](../docs/tasks/T-2026-11-15-001-hide-desktop-udid.md) / STATE、WORK-20261115-001-r1 / CE-20261115-T001-01；DECISION_LOG、ADR、Baseline和外部资产reviewed-no-change。

以下为前次任务历史指针：

T-2026-11-13-001 / complete / T8：合并提交`a9c7e1027849c296e2b8ed48b0cb5fae026ce0f9`已推送origin/main，两个parents为main/990a4aa和test/d9d30eb；提交说明“增加ADB无障碍开关快捷键”。触发前本地/远端SHA一致。2026-11-12 15:32:41 UTC（2026-11-13 00:32:41 Asia/Tokyo）向Build-Clients-BT.yml发送ref=main、Platform=Both、BuildMode=常规编译，HTTP204受理。未查询run/jobs/logs/artifacts，不监控，最终结果未知。 STATE/WORK-20261113-001-r2 / CE-20261113-T001-02。AMG-01通过：7个ADB源码与test一致，其余912个非Markdown文件与main一致，173个本地文档链接有效，无冲突标记，diff检查通过。原test未提交配置字节未变且未纳入提交；test分支保持d9d30eb。正式构建/多PC及OEM真机仍待证。

以下为合并前的历史交付记录：

T-2026-11-12-002 / complete / T8（合并交付）。合并提交`c30efd61449a0e02b5b0546a25ddf948c5ad5d4e`已推送origin/main，并核对远端SHA一致；两个parents分别为main/5d2d2e9、test/a8abdd4。test仍为a8abdd4，推送后工作区clean。提交说明“统一配置文件(大改造节点一)”。本次只补交付回执，不改变源码、不触发或监控编译。

T003 / complete / T8续检修复交付：源码0e5cc3d已推送test；2026-11-12 14:50:21 UTC触发Build-Clients-BT.yml / Both / 常规编译，HTTP204。未查询运行进度/结果；回执文档续交不二次构建。24项静态检查通过，正式编译/真机待证。STATE/WORK-20261112-003-r7 / CE-20261112-T003-07。

以下为前阶段历史指针：

T003 / complete / T8本轮本地复检：修正未执行命令遗留暂停记录、PC查询快照合并及监听回写。24项静态与diff通过；仅五个ADB源码文件及相关记录变化。改动保留test工作区，未提交推送、未触发或查询编排，正式设备与构建待证。[任务](../docs/tasks/T-2026-11-12-003-adb-accessibility-control.md) / STATE/WORK-20261112-003-r5 / CE-20261112-T003-04/05。

以下为首次交付回执：

[T-2026-11-12-003](../docs/tasks/T-2026-11-12-003-adb-accessibility-control.md) / complete / T8：源码115d0bb已推送test，Both/常规编译于2026-11-12 13:42:08 UTC获HTTP204。未查询运行进度/结果；后续只补回执文档、不二次触发。25项静态与13项离线Shell检查通过，正式构建/设备待证。STATE/WORK-20261112-003-r3 / CE-20261112-T003-03 / D-037。

以下为上一任务历史指针：

T-2026-11-12-001 / complete / T8续修交付：test/c788e8d已推送，APT改/tmp沙箱路径与三候选签名镜像，13项离线回归通过；产品源码/配置无增量。Both/常规编译HTTP204受理，不监控且未查询新运行，结果未知；后续只补回执文档。

[任务](../docs/tasks/T-2026-11-12-002-unified-config-main-merge.md) / Baseline28 / STATE、WORK-20261112-002-r2 / CE-20261112-T002-01/02。V0来源、链接、差异与历史保留检查通过；T8不代表编译/运行通过。旧任务保留在TASK_HISTORY与CHANGELOG_AI。
