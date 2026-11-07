# OpenWorld Current Work Registry

Schema Version：`1.0`
Registry Revision：`WORK-20261107-001`
Last Updated：2026-11-07
Active Task Count：1

本文件只维护当前工作指针。已关闭任务保存在 TASK_HISTORY、CHANGELOG_AI 与各任务文件；历史授权不自动续用。

## 1. Active Tasks

T-2026-11-07-001 / active / T6：源码/文档及135项V0合同完成；用户已授权改用test，stage/commit/push origin/test后Both/常规编译并监控。已从main/7361d39保留工作区切到test/48226fb，产品树无基线差异，17份文档恢复当前快照；Git/CI最终回执待追加。Owner Codex；Events CE-20261107-T001-01/02；无main写入/部署/凭据轮换。见 [任务](../docs/tasks/T-2026-11-07-001-openworld-identity.md)。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

T-2026-10-25-002 / complete / T8（文档、Git与触发交付）。文档/Baseline23与BT入口同步，main以“开发者密码增加”合并提交42ba32c并推送，保留test48226fb全部历史；main Both/常规编译HTTP204/run40204268722/head42ba32c，首次核对Windows EXE/Android Native均in_progress。后续仅补同说明回执文档，无二次编排，无本机产品验证或生产发布。

[详细任务](../docs/tasks/T-2026-10-25-002-developer-password-main.md) / STATE-20261025-002 / CE-20261025-T002-01/02。历史任务保留在TASK_HISTORY和CHANGELOG_AI；不得恢复已撤回的后续实现或从其他会话/任务继承授权。
