# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261012-008`

Last Updated：2026-10-12T06:16:00+09:00

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none；本轮服务器默认配置任务已完成V0交付，变更未提交。新会话仍须核对HEAD/status及当前请求。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-12-004`
- Result：12份原配置/源码/脚本/文档仅作指定服务器值替换；旧值零残留（排除.git），记忆同步。Flutter内部flutter_hbb保留。本轮未提交/构建/部署。
- Closed At：2026-10-12T06:16:00+09:00
- Final T-State：`T8 — complete`
- State / Event：`STATE-20261012-005` / `CE-20261012-T004-01`
- Decision：D-017；产品身份沿用D-015/D-016 / ADR-0014；治理沿用D-014。
- Verification：V0字面值差异、旧值扫描、端口派生、公钥格式、31处文档链接及diff检查；V1—V5 NOT_RUN。
- Next Gate：new user request must start at T0；historical authorization inherited `none`。
- Changelog pointer：`CHANGELOG_AI.md` 中 `T-2026-10-12-004`。
- Detailed record：`TASK_HISTORY.md` 中同 Task ID；修改清单见 `CHANGE_EVENT_LOG.md` 中同 Event。
