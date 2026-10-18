# AShell Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261019-006`
Last Updated：2026-10-19
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

none。T-2026-10-19-006文档与V0完成；提交“文档更新”承载本轮记录，test→main Git交付结果以实际引用及会话最终回执为准。未执行编排。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-10-19-006 / complete / T8（文档与V0）。实现基线0dbe202，当前合同/入口/记忆、Baseline22/D-033及事件索引同步；24份Markdown、249个本地引用已核对，非文档树与基线一致。Git范围为提交推送test、fast-forward main并推送；实际提交及远端一致性以Git交付记录为准，本次无CI/产品验证。

[详细任务](../docs/tasks/T-2026-10-19-006-restored-docs-sync.md) / STATE-20261019-006 / CE-20261019-T006-01。历史任务保留在TASK_HISTORY和CHANGELOG_AI；不得恢复已撤回的后续实现或继承历史授权。
