# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261013-016`

Last Updated：2026-10-14（T008跨日交付，ID沿用启动日期）

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

无活动任务。T008已同步main/aca7446的Shell栏、ADB合同、验证矩阵和恢复记忆；26份Markdown待提交，八份Dart源码hash保持。下一阶段ADB投屏传输待设计，尚未选型或实施；本轮没有Git写入、构建或CI操作。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-13-008`
- Result：当前Shell栏开关/三键/生命周期、多PC快照与原链边界同步；R16—19及投屏待设计交接齐备。
- Closed At：2026-10-14（Asia/Tokyo）
- Final T-State：`T8 — documentation complete`（V0；正式V2/V3/V4待验证）
- State / Event：`STATE-20261013-008` / `CE-20261013-T008-01`
- Decision：D-025 / ADR-0020 UI补录 / Baseline15；旧记录保留历史。
- Verification：26份Markdown、145个本地文件链接有效、diff空白通过、八份Dart SHA-256不变；无产品build/test/analyze/codegen或设备验证。
- Next Step：用户后续启动ADB投屏传输设计；核对新HEAD/status，再明确独立媒体通道和多PC所有权。当前无投屏实现/新依赖，未Git/CI操作。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md中同ID。
- Detailed record：docs/tasks/T-2026-10-13-008-adb-sidebar-docs-sync.md；main/aca7446、initial clean，文档待提交。
