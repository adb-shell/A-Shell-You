# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261014-002`

Last Updated：2026-10-14（独立ADB投屏与触摸优先级）

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none。T-2026-10-14-001代码复核完成，已获用户明确授权提交/推送及单次编排；本文为执行前记录，Git/dispatch实际回执见最终交付，不监控。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-14-001`
- Result：独立scrcpy投屏、多PC接收/全局停止、PC独立窗口与Shell栏两按钮；用户追加ADB优先触摸，无ADB才回到原无障碍。
- Closed At：2026-10-14（Asia/Tokyo）
- Final T-State：`T8 — source and documentation complete`（V0；正式V2/V3/V4待验证）
- State / Event：`STATE-20261014-002` / `CE-20261014-T001-02`（01实现事件保持）
- Decision：D-026 / ADR-0021 / Baseline16；旧记录保留历史。
- Verification：V0源码/协议/JNI/R8/生命周期、官方server hash、166个文档本地链接和diff空白通过；原采集与构建入口保持。无产品build/test/analyze/codegen或设备验证。
- Next Step：紧接执行用户已授权的stage/commit/push main与一次Build AShell clients both/aarch64，不监控；最终SHA/接受结果见交付回执。正式构建结果与ADB-M01—M08真机仍待用户验证。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md中同ID。
- Detailed record：docs/tasks/T-2026-10-14-001-adb-mirror.md；main/a1486ee、initial clean。
