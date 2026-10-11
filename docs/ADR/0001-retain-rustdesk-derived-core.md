# ADR-0001: Retain RustDesk-derived Core

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

- Status：`retrospective`
- Record Type：`backfill`
- Decision Date：2026-04-13 or earlier；Recorded：2026-07-12
- Original Decision Owner / Alternatives：not recorded
- Related Decision Log：D-001
- Implementation State：implemented
- Evidence / Verification：V0；exact upstream fork point unknown

## Context and Decision

Current source is a RustDesk-derived multi-platform remote-desktop core imported as a DaXianDesk snapshot. The repository proves the retained architecture but not the original approval, exact upstream commit or complete alternatives. Maintenance continues on this core rather than assuming a clean rewrite.

## Consequences

AShell inherits protocol/platform maturity plus legacy names、compatibility paths、large files、AGPL/third-party obligations and upstream-diff debt. This record remains retrospective until the owner explicitly re-approves it with lineage evidence.

## Compatibility / Verification / Reversal

Do not delete upstream compatibility blindly. A core rewrite or upstream rebase requires a new ADR, migration plan, protocol/platform matrix and rollback. Source lineage is tracked in `docs/BASELINE/01_UPSTREAM_BASELINE.md`.

