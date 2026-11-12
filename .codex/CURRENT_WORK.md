# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261112-003-r3`
Last Updated：2026-11-12
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

none；T003本次源码与编排触发交付已完成，正式构建和设备结果待证。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

[T-2026-11-12-003](../docs/tasks/T-2026-11-12-003-adb-accessibility-control.md) / complete / T8：源码115d0bb已推送test，Both/常规编译于2026-11-12 13:42:08 UTC获HTTP204。未查询运行进度/结果；后续只补回执文档、不二次触发。25项静态与13项离线Shell检查通过，正式构建/设备待证。STATE/WORK-20261112-003-r3 / CE-20261112-T003-03 / D-037。

以下为上一任务历史指针：

T-2026-11-12-001 / complete / T8续修交付：test/c788e8d已推送，APT改/tmp沙箱路径与三候选签名镜像，13项离线回归通过；产品源码/配置无增量。Both/常规编译HTTP204受理，不监控且未查询新运行，结果未知；后续只补回执文档。

[任务](../docs/tasks/T-2026-11-12-001-build-fixes.md) / STATE-20261112-001-r6 / CE-20261112-T001-01—06 / D-036 / ADR-0029。真实Linux权限、runner DNS和完整编译仍待证，不将离线检查提升为运行PASS。
