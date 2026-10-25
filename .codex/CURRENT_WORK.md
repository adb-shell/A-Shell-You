# AShell Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261025-001`
Last Updated：2026-10-25
Active Task Count：0

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

none。T-2026-10-25-001源码/V0、test推送及单次编排触发完成；实际交互与编译结果仍待验证。历史授权不继承。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-10-25-001 / complete / T8（源码、V0与触发交付）。Ctrl+Shift+H先密码校验后进入进程内开发者免登录。test快进fd233fa后推送源码fe3c7d4；Build-Clients-BT Windows/常规编译HTTP204，run40200073517、head fe3c7d4，首次核对in_progress。后续仅补交付文档，无二次dispatch；main未推送，无本机产品或生产发布执行。

[详细任务](../docs/tasks/T-2026-10-25-001-developer-login-password.md) / STATE-20261025-001 / CE-20261025-T001-01/02。历史任务保留在TASK_HISTORY和CHANGELOG_AI；不得恢复已撤回的后续实现或继承历史授权。
