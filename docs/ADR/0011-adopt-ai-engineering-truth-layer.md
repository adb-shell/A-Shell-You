# ADR-0011: Adopt the AI Engineering Truth Layer

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

- Status：`accepted`
- Record Type：`backfill`
- Decision Date / Recorded Date：2026-07-12
- Decision Owner：AShell project owner
- Related Decision Log：D-011
- Implementation State：implemented；Evidence：V0

## Decision

Use current source/manifests/protocol/build scripts as implementation truth；use `docs/AI_ENGINEERING/` as current repository-side architecture knowledge；retain old `ENGINEERING_*` and upstream documents as historical/no-regression evidence；keep `.codex` as thin memory/decision/task records.

## Consequences

New maintainers get a coherent truth hierarchy while historical material remains available. Maintaining current docs becomes part of authorized implementation completion.

## Reversal / Verification

Changing truth hierarchy requires a superseding ADR and migration map；old documents are not deleted automatically. V0 checks entry links、source anchors and contradiction labels.

