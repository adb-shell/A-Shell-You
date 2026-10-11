# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261012-006`

Last Updated：2026-10-12T05:39:32+09:00

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none。T003完成两处ashell.匹配及全部文档品牌命名统一；T002/T003变更仍未提交。后续任务重核HEAD/status和用户授权；没有构建/部署权限。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-12-003`
- Result：源码两处匹配、全部文档字段/路径/文件名统一；旧品牌全文/路径检索零命中（排除.git）。Flutter内部flutter_hbb保留；文档历史原文字在Git可恢复。未提交/构建/部署。
- Closed At：2026-10-12T05:39:32+09:00
- Final T-State：`T8 — complete`
- State / Event：`STATE-20261012-004` / `CE-20261012-T003-01`
- Decision：D-016补充D-015 / ADR-0014；治理沿用D-014。
- Verification：V0全文/路径检索、源码谓词、Flutter package/import、82份修改/新增Markdown链接及diff检查；V1—V5 NOT_RUN。
- Next Gate：new user request must start at T0；historical authorization inherited `none`。
- Changelog pointer：`CHANGELOG_AI.md` 中 `T-2026-10-12-003`。
- Detailed record：`TASK_HISTORY.md` 中同 Task ID；修改清单见 `CHANGE_EVENT_LOG.md` 中同 Event。
