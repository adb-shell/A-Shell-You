# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261013-006`

Last Updated：2026-10-13

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none；T-2026-10-13-003 文档/V0 交付完成，修改未提交；构建和设备验证待正式证据，不属于活动文档任务。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-13-003`
- Result：8662f23 独立本机 ADB 的使用/架构/依赖/验证、ADR/Baseline、入口/Skills 与记忆已同步；旧档案保持历史。
- Closed At：2026-10-13（Asia/Tokyo）
- Final T-State：`T8 — complete`（文档/V0；ADB V2/V3结果未提供）
- State / Event：`STATE-20261013-003` / `CE-20261013-T003-01`
- Decision：D-021 / ADR-0017 补记新模块；ADR-0015 旧退役与 D-020 / ADR-0016 CI 边界保持。
- Verification：V0 源码/8662f23差异、34文件仅Markdown、39个新增相对链接、diff/新文件空白与事件路径清单检查通过；原产品代码无diff。
- Next Gate：待正式构建/设备结果补证据；新 PC ADB/投屏需独立设计。本轮无Git写入/build/test/device/dispatch/sign/package/release。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md 中同 ID。
- Detailed record：docs/tasks/T-2026-10-13-003-local-adb-docs.md；观察main/f84a78f，initial clean，34份Markdown留工作树未提交。
