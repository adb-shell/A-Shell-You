# OpenWorld 产品身份工作区基线

- Baseline ID：OW-BL-2026-11-07-7361d39-identity-worktree。
- Source：main/7361d39d34ad0bb147827e30fa61419a00b84038之上的未提交工作区，不是已编译产物。
- Task：T-2026-11-07-001；Event：CE-20261107-T001-01；Decision：D-034 / ADR-0027。
- Display/runtime：开放世界 / OpenWorld；crate/native/scheme/portable：openworld。
- Android/Apple/Linux ID：com.openworld.me；Android目录com/openworld/me；Flutter内部package为flutter_hbb。
- Versions：Rust5.2.1、Flutter5.2.1+59；依赖版本/checksum、pubspec.lock及SDK/NDK/Flutter/Rust选择不变。
- Native：libopenworld.so、openworld.dll、libopenworld.a、libopenworld.dylib；PC-Bulid/openworld.exe；本地Android openworld-aarch64-release.apk / openworld-universal-release.apk。
- CI：Build-Clients-BT.yml / Build OpenWorld Clients-BT；Both/Android/Windows、常规/原始编译、actions pins、jobs/权限/工具链保持；品牌缓存/临时签名展示/产物名同步。
- Compatibility：已部署ZEGO wire key、旧构建机变量和旧服务分类前缀受控保留；设备密钥/UUID/硬件指纹算法不变，见ADR-0027。
- Evidence：V0；正式构建/运行NOT_RUN。本机缺少Rust/Flutter/Java/CMake及正式C:/DevEnv、C:/DevTool；没有提交推送或云编排回执。
- Existing limits：Apple/Google注册和生产签名未核验；iOS exportOptions.plist有基线已存在的非法XML引号，不扩大为Apple发布修复。

详见[迁移记录](../AI_ENGINEERING/OPENWORLD_IDENTITY_MIGRATION_2026-11-07.md)。Baseline23及更早快照冻结保留。

交付补注：上述实施快照已作为源码ad88665e74ef48e251a1faefda6461e3c0e8112b提交推送test；main保持7361d39。Both/常规编译run42350649478/head ad88665已触发，用户随后要求不监控；最终编译/产物结果未查询，不能将此补注解释为V2/V3 PASS。
