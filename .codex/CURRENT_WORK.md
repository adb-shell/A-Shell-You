# AShell Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261025-001-r3`
Last Updated：2026-10-25
Active Task Count：1

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

T-2026-10-25-001 / active / T7（r3）：两个可接受密码的model实现与V0完成，10份Markdown/111个本地引用有效；test/4694e78起点。沿用当前会话已确认的test推送与Windows常规编排交付，准备提交本次11路径；不从其他会话或历史任务继承授权。CE-20261025-T001-03；[任务](../docs/tasks/T-2026-10-25-001-developer-login-password.md)。

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
