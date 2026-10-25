# AShell Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261025-002`
Last Updated：2026-10-25
Active Task Count：1

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

T-2026-10-25-002 / active / T7：文档同步与V0完成，20份Markdown/222本地引用有效，产品/工作流树保持test48226fb；main已准备无冲突no-ff merge。用户明确授权以“开发者密码增加”提交推送main及Both/常规编译，当前待Git/CI回执。Owner：Codex/master，Release与Security自审；CE-20261025-T002-01，[任务](../docs/tasks/T-2026-10-25-002-developer-password-main.md)。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-10-25-001 / r3 / complete / T8（源码、V0与触发交付）。保留原密码并新增第二个可接受密码，model摘要集合任一精确匹配即可。源码356f53b已推送test；Build-Clients-BT Windows/常规编译HTTP204，run40203581056、head356f53b，首次核对in_progress。后续仅补交付文档，无r3二次dispatch；main未推送，无本机产品或生产发布执行。

[详细任务](../docs/tasks/T-2026-10-25-001-developer-login-password.md) / STATE-20261025-001-r3 / CE-20261025-T001-03/04。历史任务保留在TASK_HISTORY和CHANGELOG_AI；不得恢复已撤回的后续实现或从其他会话/任务继承授权。
