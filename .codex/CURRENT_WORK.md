# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261013-012`

Last Updated：2026-10-13

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

无活动源码任务。T006实现和记录完成；本文件为提交前快照，随后按用户授权stage/commit/push并单次dispatch，实际回执见会话最终交付，不监控构建。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-13-006`
- Result：独立ADB权限修复、双NSD、一键配对连接、PC系统QR与分窗拖动、Android UI；原链/CI/依赖不变。
- Closed At：2026-10-13（Asia/Tokyo）
- Final T-State：`T8 — source complete / delivery`（V0；正式V2/V3/V4待验证）
- State / Event：`STATE-20261013-006` / `CE-20261013-T006-01`
- Decision：D-023 / ADR-0019 / Baseline13；旧决定按历史保存。
- Verification：V0合同/参数/权限/NSD与取消/窗口/差异空白/保护路径/新文档链接；无本地工具链，未执行本地项目命令。
- Next Gate：本次已授权提交推送与both/aarch64 CI dispatch，准确结果见最终回执，不监控；后续用户提供新包验证结果。无删除/版本/发布/生产操作。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md 中同 ID。
- Detailed record：docs/tasks/T-2026-10-13-006-adb-usability.md；观察main/504adea，initial clean；提交前快照。
