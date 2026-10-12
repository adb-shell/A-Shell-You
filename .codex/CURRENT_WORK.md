# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261013-002`

Last Updated：2026-10-13

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none；T-2026-10-13-001 已完成工作流配置、V0 核对和文档交付；尚无 runner 构建/Git/上传任务。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- Task ID：`T-2026-10-13-001`
- Result：独立手动 Android/Windows Actions；平台/ABI选择、临时测试签名、仅Artifacts；删除12个旧workflow与失效Dependabot；本轮配置/文档未提交。
- Closed At：2026-10-13（Asia/Tokyo）
- Final T-State：`T8 — complete`（配置交付/V0；runner V2待验证）
- State / Event：`STATE-20261013-001` / `CE-20261013-T001-01`
- Decision：D-020 / ADR-0016，保留ADR-0010 manual-only边界。
- Verification：V0 YAML/矩阵/Action SHA、11段Bash/1段Python AST/6段PowerShell、两份local shell语法、链接/权限/diff；产品源码/manifest/locks未改。
- Next Gate：Git写入/远端dispatch/构建须按新的明确请求开始T0；本轮未执行签名/打包/上传/发布。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md 中同 ID。
- Detailed record：docs/tasks/T-2026-10-13-001-client-actions.md。
