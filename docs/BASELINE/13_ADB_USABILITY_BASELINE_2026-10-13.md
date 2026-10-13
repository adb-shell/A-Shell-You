# AS-BL-2026-10-13-504adea-adb-usability

2026-10-13 / repository-observed / V0；source main/504adea0119d9ff96d33a16cd27e559d1492a723 + T006修改。初始clean，最终提交SHA由Git回执确认；旧Baseline12保持历史冻结。

- Product 5.2.1、Flutter5.2.1+59、me.ashell、ashell native identity不变。
- Rust1.75/Flutter3.24.5/JDK17/NDK27及现有build-clients.yml不变；用户授权both/aarch64单次dispatch，不监控。
- 无新依赖。libadb3.1.1/spake2-android2.2.1/BC1.81/Conscrypt2.5.3不变；二维码使用现有qr_flutter4.1.0。
- Misc.remote_adb字段40、JSON v1、FRB keys/signatures不变；status.apiVersion=2，新discover/pairConnect及connectPort/serviceName；AdbIdentity和local_adb_record_v1不迁移。
- ADB独立auth gate、双NSD、配对后自动连接、PC扫码、独立拖动窗口。ADR-0019 / D-023 / CE-20261013-T006-01。
- 研究固定来源：LADB60f48029cf9d8e0bc848ca41a7bd76694d4ab796、aShellYou0fa5da1846b9b589c3d7f4993fb2d65b35b462ac；独立实现发现逻辑，未整包复制/构建上游。
- V0检查不等于V2/V3；无本地SDK/toolchain，未运行本地build/test/analyze/codegen。GitHub只提交dispatch，不收集后续状态。Release provenance/hash/SBOM、ROM/NSD/QR和多PC验收仍待提供。
