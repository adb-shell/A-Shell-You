# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261113-001-r1`
Last Updated：2026-11-13
Active Task Count：1

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

T-2026-11-13-001 / active / T7：main合并ADB无障碍快捷操作；独立工作区，待提交推送和一次Both/常规编译。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

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
