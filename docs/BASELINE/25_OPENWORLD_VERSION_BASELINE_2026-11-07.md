# OpenWorld 2.0.26版本工作区基线

- Baseline ID：OW-BL-2026-11-07-c27368c-version-worktree。
- Source：main/c27368cfb37f6e30217f06c8da7734065b0dccc5之上的本地版本修改，未提交/未编译；Task T-2026-11-07-003，Event CE-20261107-T003-01。
- 产品：开放世界 / OpenWorld / openworld / com.openworld.me；身份合同沿用Baseline24 / ADR-0027。
- 版本：主Rust包及openworld-portable-packer为2.0.26，Flutter为2.0.26+59；Android versionName由Flutter取2.0.26，versionCode仍59；Windows Runner与portable分别从Flutter/Cargo生成版本资源。
- 两份Cargo.lock仅本项目版本条目变化；Flutter构建号、第三方依赖/锁/checksum、工具链、工作流、签名和外部资产保持。
- Windows build.cmd默认VERSION及静态身份检查同步；不手工生成src/version.rs或平台生成配置。
- Evidence：V0静态核对；本版本正式构建/运行NOT_RUN。旧main/482eda5的编排不能证明本版本通过；按用户既有要求不查询旧run。
- Recovery：逐项反向审查本轮版本差异即可恢复5.2.1 / 5.2.1+59，无数据迁移或自动Git写入。

准确修改与验证见[版本任务](../tasks/T-2026-11-07-003-openworld-version.md)；旧Baseline保持冻结事实。
