# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261012-018`

Last Updated：2026-10-12T20:52:15+09:00

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none；T009 已完成源码剔除、V0 核对和文档交付。正式构建/设备验证未执行；没有活动构建或 Git 任务。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-12-009`
- Result：旧 ADB 功能链/页面/模块/专用权限移除，排除旧 libadb.so，未接入新组件；源码和文档修改未提交。
- Closed At：2026-10-12T20:52:15+09:00
- Final T-State：`T8 — complete`（源码交付；运行验收待验证）
- State / Event：`STATE-20261012-010` / `CE-20261012-T009-01`
- Decision：D-019 / ADR-0015；既有其他边界保持。
- Verification：V0 删除边界、旧符号/通道扫描、Manifest XML、Bash 仅解析、协议无字段变化、保留源码/范围检查；V2/V3 NOT_RUN。
- Next Gate：新组件/编译/设备/Git 操作须按新的用户请求开始 T0；历史授权不继承。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md 中同 ID。
- Detailed record：docs/tasks/T-2026-10-12-009-remove-legacy-adb.md。
