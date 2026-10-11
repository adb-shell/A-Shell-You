# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261012-014`

Last Updated：2026-10-12T07:20:08+09:00

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none；T007已完成资源补齐与V0静态核验，正式编译/设备显示未执行。新请求从T0重新核对。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-12-007`
- Result：补齐Android通知/主题/默认悬浮图、Windows旧UI/自解压窗口small+big/卸载快捷方式；本轮28文件，T006合并后51文件未提交。原图及T006产品文件保持；未编译/运行/实际打包。
- Closed At：2026-10-12T07:20:08+09:00
- Final T-State：`T8 — complete`
- State / Event：`STATE-20261012-008` / `CE-20261012-T007-01`
- Decision：N/A — no decision delta；D-018、D-017、D-015/D-016 / ADR-0014仍有效；治理沿用D-014。
- Verification：V0 12PNG/16XML、尺寸/alpha/单色眼睛间隙、源图及T006图片/Flutter代码SHA未变、资源/include/ignore/文档链接、预览及51文件范围检查；V1—V5 NOT_RUN。
- Next Gate：new user request must start at T0；historical authorization inherited `none`。
- Changelog pointer：`CHANGELOG_AI.md` 中 `T-2026-10-12-007`。
- Detailed record：`TASK_HISTORY.md` 中同 Task ID；修改清单见 `CHANGE_EVENT_LOG.md` 中同 Event。
