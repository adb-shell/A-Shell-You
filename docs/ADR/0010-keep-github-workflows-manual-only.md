# ADR-0010: Keep GitHub Workflows Manual-Only

- Status：`accepted`
- Record Type：`backfill`
- Decision Date：2026-06-25；Recorded：2026-07-12
- Original Approver / Alternatives：not recorded
- Related Decision Log：D-010
- Implementation State：implemented governance debt；Evidence：V0

## Decision

Current workflows remain manually triggered while their RustDesk targets、mutable actions、permissions、secrets、auto commit/push and publish paths are not hardened. Manual trigger is not a trusted quality or release gate.

## Consequences

No automatic PR/push regression evidence exists；manual execution can still be unsafe. CI recovery is a planned governance change, not something AI enables automatically.

## Reversal / Verification

Restoring CI requires a superseding ADR or approved amendment covering immutable actions、least privilege、untrusted PR isolation、required checks and artifact provenance. Git writes and releases remain separately forbidden.

## Amendment — 2026-10-13

[ADR-0016](0016-independent-client-actions.md) replaces the inherited workflow implementation with independent manual Android/Windows builds and Actions-only artifacts. The current user authorized this scope; manual-only remains in force. Direct actions are SHA-pinned, checkout credentials are not persisted, token permissions are contents: read, and no production signing or publish jobs exist. Automatic PR/push required checks are still absent; runner build evidence remains pending.

