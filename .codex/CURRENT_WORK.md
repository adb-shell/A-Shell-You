# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261115-001-r3`
Last Updated：2026-11-15
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

none；T001已提交推送test并触发Both/常规编译，HTTP204，不监控；最终编译结果未知。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

2026-11-15 / T001交付回执：源码696a87bb294088a57f59d89fce3f3d266ca3dab5已提交并推送origin/test，提交说明“移除PC右下角UDID显示”，触发前本地/远端SHA一致且工作区clean。2026-11-15 09:27:19 UTC（18:27:19 Asia/Tokyo）向Build-Clients-BT.yml（394791509）发送ref=test、Platform=Both、BuildMode=常规编译，HTTP204受理。按用户要求不监控，未查询新run/jobs/logs/artifacts，最终编译结果未知。 complete/T8仅关闭本次提交推送与触发范围；STATE、WORK-20261115-001-r3 / CE-20261115-T001-03。补回执只改文档，不二次编排。

[任务](../docs/tasks/T-2026-11-15-001-hide-desktop-udid.md)。V0与交付完成不表示构建或设备验收通过。

以下为原编辑阶段与前次任务历史指针：

2026-11-15 / T-2026-11-15-001：在clean test/caf0702上移除PC右下角识别码/UUID显示与双击复制，唯一产品源码为connection_page.dart两块展示代码（53行）。状态/服务器切换、账号UUID、Rust桥接、Android与配置保持。V0精确差异及diff检查通过；未编译/运行、未提交推送，complete/T8仅源码编辑交付。 [任务](../docs/tasks/T-2026-11-15-001-hide-desktop-udid.md) / STATE、WORK-20261115-001-r1 / CE-20261115-T001-01；DECISION_LOG、ADR、Baseline和外部资产reviewed-no-change。

以下为前次任务历史指针：

T003 / complete / T8续检修复交付：源码0e5cc3d已推送test；2026-11-12 14:50:21 UTC触发Build-Clients-BT.yml / Both / 常规编译，HTTP204。未查询运行进度/结果；回执文档续交不二次构建。24项静态检查通过，正式编译/真机待证。STATE/WORK-20261112-003-r7 / CE-20261112-T003-07。

以下为前阶段历史指针：

T003 / complete / T8本轮本地复检：修正未执行命令遗留暂停记录、PC查询快照合并及监听回写。24项静态与diff通过；仅五个ADB源码文件及相关记录变化。改动保留test工作区，未提交推送、未触发或查询编排，正式设备与构建待证。[任务](../docs/tasks/T-2026-11-12-003-adb-accessibility-control.md) / STATE/WORK-20261112-003-r5 / CE-20261112-T003-04/05。

以下为首次交付回执：

[T-2026-11-12-003](../docs/tasks/T-2026-11-12-003-adb-accessibility-control.md) / complete / T8：源码115d0bb已推送test，Both/常规编译于2026-11-12 13:42:08 UTC获HTTP204。未查询运行进度/结果；后续只补回执文档、不二次触发。25项静态与13项离线Shell检查通过，正式构建/设备待证。STATE/WORK-20261112-003-r3 / CE-20261112-T003-03 / D-037。

以下为上一任务历史指针：

T-2026-11-12-001 / complete / T8续修交付：test/c788e8d已推送，APT改/tmp沙箱路径与三候选签名镜像，13项离线回归通过；产品源码/配置无增量。Both/常规编译HTTP204受理，不监控且未查询新运行，结果未知；后续只补回执文档。

[任务](../docs/tasks/T-2026-11-12-001-build-fixes.md) / STATE-20261112-001-r6 / CE-20261112-T001-01—06 / D-036 / ADR-0029。真实Linux权限、runner DNS和完整编译仍待证，不将离线检查提升为运行PASS。
