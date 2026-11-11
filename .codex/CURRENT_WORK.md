# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261111-001-r2`
Last Updated：2026-11-11
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

无活动任务。文档同步与test→main合并推送已完成；仅补交付回执，不触发或监控编译。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-11-11-001 / complete / T8（文档与main合并推送）。合并`d58196214f5006da0e26cdce050f5229063f5194`已推送origin/main并核对远端一致；test保持51292c2。版本2.0.26 / 2.0.26+59保持，纳入源码25bc2ce的开发者画面优先。V0身份122项、31份Markdown/351个本地链接及来源/差异检查通过；后续仅补回执文档。

[详细任务](../docs/tasks/T-2026-11-11-001-docs-main-merge.md) / STATE-20261111-001-r2 / CE-20261111-T001-01/02 / Baseline27。未触发或监控构建，旧test编排最终结果未知；T8不表示编译或真机验收通过。历史任务保留在TASK_HISTORY与CHANGELOG_AI，不继承旧授权或构建结论。
