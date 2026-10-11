# ADR-0002: AShell Product Identity

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

- Status：`superseded` by [ADR-0014](0014-ashell-product-identity.md) on 2026-10-12；below is the preserved historical decision.
- Record Type：`backfill`
- Decision Date：2026-05；Recorded：2026-07-12
- Original Approver / Alternatives：not recorded
- Related Decision Log：D-002
- Implementation State：implemented；Evidence：V0

## Decision

Use `AShell`/`ashell` for runtime、crate/library and native artifacts；Android uses `云计划` and `me.ashell`；deep link is `ashell`. Preserve inherited RustDesk names where they are compatibility or third-party anchors.

## Consequences

Identity changes must be full-chain across Cargo、Flutter、Android、SO/DLL/EXE、deep link、installer、update and backend contracts. Bulk brand replacement is prohibited.

## Compatibility / Rollback / Verification

Any future rename requires a superseding ADR, migration window, old-client compatibility and artifact rollback. Validate with `02_VERSION_MATRIX.md` and affected `TEST_MATRIX.md` cases; current evidence is V0 only.

