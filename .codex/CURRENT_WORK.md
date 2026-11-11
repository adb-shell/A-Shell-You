# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261111-002-r5`
Last Updated：2026-11-12
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

none；本次指定交付范围已关闭，正式运行验证仍待证。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-11-11-002 / complete / T8（源码、文档、test提交推送与双端编译触发交付）。源码cd21705已推送origin/test；Both/常规编译HTTP204。用户要求不监控，未查询运行/日志/产物，最终结果未知。三套现有地址已填齐，Android=PC1、PC2独立；共享Key保持。补回执文档不二次编排。

[详细任务](../docs/tasks/T-2026-11-11-002-server-profiles.md) / STATE-20261111-002-r5 / CE-20261111-T002-01—05 / D-036 / ADR-0029。T8不表示V1/V2/V3/V4通过；历史任务留在TASK_HISTORY和CHANGELOG_AI。
