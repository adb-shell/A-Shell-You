# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261111-001`
Last Updated：2026-11-11
Active Task Count：1

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

T-2026-11-11-001 / handoff / T7：文档与合并树已就绪，准备提交推送main。Owner：Codex；十处文档冲突已解，V0身份122项、31份Markdown/351链接、来源/差异检查通过。输入main/9040c53、test/51292c2；保留2.0.26 / 2.0.26+59，无额外产品改动或编排。STATE-20261111-001 / CE-20261111-T001-01；[任务](../docs/tasks/T-2026-11-11-001-docs-main-merge.md)。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-11-07-004 / complete / T8（源码、文档、test提交推送及编排触发交付）。源码25bc2ce已推送origin/test，说明“开发者模式画面优先”；当时main保持9040c53；后续合并见当前任务。Both/常规编译HTTP204，单次定位尚无run；用户要求不监控，最终编译结果未知。仅补回执文档不二次编排，真机与生产未操作。

[详细任务](../docs/tasks/T-2026-11-07-004-developer-screen-priority.md) / STATE-20261107-004-r4 / CE-20261107-T004-01—05 / D-035。T8仅关闭本次指定交付范围，不表示V1/V2/V3/V4通过；历史任务保留在TASK_HISTORY和CHANGELOG_AI，不继承旧授权或构建结论。
