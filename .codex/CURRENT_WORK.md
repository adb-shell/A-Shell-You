# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261013-008`

Last Updated：2026-10-13

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

无活动任务。T004仓库实现已交付，正式两端编译/多PC验收待用户在正式环境提供结果；见Last Closed Task。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-13-004`
- Result：独立PC ADB菜单/专用协议/JNI与Android共享manager、有限队列、配对记录/连接/shell/撤销已实现；相关文档/记忆已同步。
- Closed At：2026-10-13（Asia/Tokyo）
- Final T-State：`T8 — complete repository implementation`（V0；V2/V3/V4待验证，非运行验收完成）
- State / Event：`STATE-20261013-004` / `CE-20261013-T004-01`
- Decision：D-022 / ADR-0018；Baseline12。旧退役、产品身份与CI边界保持。
- Verification：V0跨层合同/字段/JNI/权限/所有权与取消、保护路径diff、文档链接/空白；原无障碍/视频/输入源码未改，session仅新增ADB钩子。
- Next Gate：正式两端构建/Release JNI与多PC验收；ADB投屏仍未实现。本轮无Git写入/build/test/device/dispatch/sign/package/release。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md 中同 ID。
- Detailed record：docs/tasks/T-2026-10-13-004-remote-adb.md；观察main/917e661，initial clean，本任务代码/文档留工作树未提交。
