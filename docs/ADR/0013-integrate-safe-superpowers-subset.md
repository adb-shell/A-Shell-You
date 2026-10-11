# ADR-0013: Integrate a Safe Superpowers Subset

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

- Status：`accepted`
- Record Type：`contemporaneous`
- Decision Date / Recorded Date：2026-07-12
- Decision Owner：AShell project owner
- Security / Release Review：required constraints incorporated
- Related Decision Log：D-013
- Implementation State：implemented as local policy adapter；Evidence：V0

## Context

Upstream Superpowers includes useful brainstorming、planning、systematic debugging、verification and review methods, but its broader workflow also contains worktree、plan execution、TDD/commit and branch-finishing behaviors. Direct installation or blanket enablement would conflict with AShell authority rules.

## Decision

Do not install or execute external Superpowers. Create the project-owned `ashell-superpowers-safe` Skill as a semantic adapter with an exact allowlist:

1. brainstorming
2. planning
3. debugging
4. verification
5. review

All other capabilities are denied. The adapter is read-only by default and never authorizes file edits、build/test、Git writes、commit、push、release、external/production access or credential use.

## Alternatives

- Install the full upstream package：rejected because it exposes out-of-scope execution/Git/release workflows and update/telemetry supply-chain surface.
- Reimplement five unrestricted execution skills：rejected because “verification/debugging” could be misread as command authority.
- Use a narrow local adapter：accepted；AShell rules remain authoritative.

## Consequences

The project gains structured reasoning without importing external code or expanding authority. Future upstream improvements are not automatic；the adapter must be reviewed deliberately.

## Verification / Reversal

V0 verifies the allowlist、forbidden actions、Skill schema/metadata and read-only forward scenarios. Remove or broaden the adapter only through a superseding ADR；commit/push/release remain hard-denied within this profile.

## Reference

- Official upstream reference reviewed 2026-07-12: `https://github.com/obra/superpowers`（reference only, not installed）.

