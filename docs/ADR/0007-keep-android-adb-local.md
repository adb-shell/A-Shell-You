# ADR-0007: Keep Android ADB as a Local Subsystem

- Status：`superseded by ADR-0015`（2026-10-12 T009）；以下为历史决定
- Record Type：`backfill`
- Decision Date：2026-05；Recorded：2026-07-12
- Original Approver / Alternatives：not recorded
- Related Decision Log：D-007
- Implementation State：retired by T009；旧实现描述仅作历史，当前见 [ADR-0015](0015-retire-legacy-adb.md)

## Decision

Packaged `libadb.so` and the Android ADB/LADB page provide device-local wireless-debugging pair/connect/shell. They are not a PC remote ADB protocol and must stay separate from remote terminal/input until a new threat model and protocol are accepted.

## Consequences

ADB remains a high-privilege local capability with explicit device action、audit and revoke requirements. Local-only binary provenance is a release blocker.

## Compatibility / Verification / Reversal

Remote ADB needs a superseding/new ADR with endpoint authorization、allowlist、consent、audit and kill switch. Verify AND-06/08 across API levels and ABIs; external asset `EXT-BIN-ADB-001` remains blocking.

