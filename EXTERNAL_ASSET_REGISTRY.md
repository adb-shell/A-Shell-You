# AShell External Asset Registry

2026-10-19 T006：按0dbe202同步文档，无新增、下载或替换外部资产。EXT-LIB-ADB-004—007及EXT-BIN-SCRCPY-001保持；视频helper复用固定scrcpy 5.0.1，常驻截图/节点/遮罩helper来自APK。字体使用App内公开SANS_SERIF与系统回退，无外部字体文件。新旧CI入口以[当前构建说明](docs/BUILD_CLIENTS_ACTIONS.md)为准；以下登记保留各时点证据，当前构建/设备结果仍待验证。

2026-10-14 T004：新增APK自编`AdbLayoutCapture`，使用设备UiAutomation/SurfaceControl隐藏接口；未下载/新增第三方库或binary，EXT-BIN-SCRCPY-001保持。平台来源、权限/生命周期/兼容边界见[ADR0024](docs/ADR/0024-adb-layout-and-geometry.md)，V0；OEM兼容和正式产物仍待验证。

2026-10-14 T002：主界面分栏/连续截图无新外部依赖或二进制；截图调用设备系统screencap并使用Android Bitmap及既有image crate，原EXT-BIN-SCRCPY-001未变。D-027 / Baseline17，V0，未构建。

2026-10-14 / EXT-BIN-SCRCPY-001：官方 Genymobile scrcpy-server v5.0.1（Apache-2.0），733930字节；SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`已与官方发布元数据比对。固定资产和LICENSE/NOTICE位于`flutter/android/app/src/main/assets/adb-mirror/`；仅在Android已授权ADB shell执行，宿主不执行。来源/链接见资产NOTICE与[投屏合同](docs/AI_ENGINEERING/14_ADB_MIRROR.md)。上游owner Genymobile；集成owner AShell维护者；签名/可复现重建/真机证据未取得，不据此宣称release-ready。T-2026-10-14-001 / D-026 / Baseline16。

2026-10-13 T008：main/aca7446的Shell栏复用既有ADB shell接口，仅增加Flutter侧入口/状态，无新增库、服务、binary或工具链。ADB远程投屏传输为下一阶段待设计方向，尚未选定或引入scrcpy/其他媒体组件；后续确定选型时再登记固定来源、许可、hash/NOTICE等，不将计划登记为已安装资产。详见[当前ADB](docs/AI_ENGINEERING/13_REMOTE_ADB.md)与[路线图](docs/AI_ENGINEERING/11_ROADMAP.md)。

2026-10-13 T007：ADB二维码消费已在efb2599移除，ADB不再导入qr_flutter；该库仍供其他产品页面使用，未移除全局依赖。双NSD沿用T006已登记的 [LADB](https://github.com/tytydraco/LADB/tree/60f48029cf9d8e0bc848ca41a7bd76694d4ab796) 与 [aShellYou](https://github.com/DP-Hridayan/aShellYou/tree/0fa5da1846b9b589c3d7f4993fb2d65b35b462ac) 研究来源；AOSP AdbQrCode仅历史研究，不是当前实现。EXT-LIB-ADB-004—007版本不变，原EXT-REF-ADB-002/003仍退役；本轮不新增/下载/变更依赖、binary或外部服务。见13_REMOTE_ADB与Baseline14；hash/NOTICE/SBOM和正式产物证据仍待补。

2026-10-13 T004：新增PC ADB沿用EXT-LIB-ADB-004—007固定依赖，不添加外部服务/库。libadb3.1.1 SslUtils缓存重置为版本绑定反射兼容点，Release keep/撤销重配待验证，见 [远程ADB](docs/AI_ENGINEERING/13_REMOTE_ADB.md)。T003记录的hash/ABI/NOTICE/SBOM缺口保持；旧EXT-BIN-ADB-001与EXT-REF-ADB-002/003仍RETIRED。

最后更新：2026-10-13（ADB条目与消费状态；其他资产保留原观察日期）

状态：repository-side inventory complete；owner/provenance onboarding incomplete  
范围：不在 Git tracked source 中，或不能由 clean clone 独立复现的 service、database、driver、binary、signing/build/release infrastructure 和历史 provenance

> 本登记表不保存 credential、token、password、private key、生产 IP/domain、连接串或 PII。发现这类值时只记录资产类型、所在路径和处置状态。

2026-10-12 存在性复核：当前检出 `jniLibs/` 不存在，ADB binary 标为 MISSING；历史hash保留，不代表当前文件。ZEGO broker为脚本内嵌实现的PARTIAL资产、SDK lock drift仍在。相邻API/WEB/SERVER仓和实际生产服务未检查，其他资产条目仍为带日期的历史材料。证据见 [接管认知](docs/AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md)。

## 1. 状态词汇

T004 / 2026-10-12：`EXT-SVC-001`—`004` 的仓内默认地址、相关端口与服务器公钥引用按用户输入迁移；`.info` 和Token部署脚本同步。`EXT-CI-001` 的服务器/公钥secret值、构建机环境、服务监听/公私钥配套及客户端已保存配置均未读取或修改；资产状态不提升为已部署或已验证。证据：`docs/tasks/T-2026-10-12-004-server-configuration.md`、Network/API工程文档。

| 状态 | 含义 |
|---|---|
| `EXTERNAL` | 服务/基础设施本来就在仓库外，需独立 owner 与 contract |
| `PARTIAL` | 仓库只有 client、contract、脚本或文档，没有完整资产 |
| `LOCAL-ONLY` | 当前机器存在，但被 ignore、未跟踪或无法由 clean clone 获取 |
| `GENERATED` | source 在仓库，binary/artifact 需正式构建生成 |
| `OS-DERIVED` | 运行时依赖目标操作系统提供或从 OS 文件派生，不是仓内可发布的独立 artifact |
| `DORMANT` | 代码或接口仍存在，但不是当前 active product path；启用前必须重新接管 |
| `MISSING` | 需要的 source、contract、binary 或运维材料未提供 |
| `UNVERIFIED` | 存在某些材料，但 version/hash/signature/license/owner 未核验 |
| `BLOCKING` | 缺口阻止正式验证、可复现构建、安全接受或发布 |
| `RETIRED` | 已退出现役依赖；历史 provenance/许可记录保留，不代表设备授权自动撤销 |

Owner 为空时统一写 `OWNER-REQUIRED`，不能由 AI 推断个人或组织。

2026-07-12 只读基线：Git tracked files 中按 `.dll/.exe/.sys/.cat/.so/.aar/.jks/.keystore/.p12/.zip` 扩展名盘点为 0。该结果只说明 binary 没有随当前 source snapshot 跟踪，不代表运行时不依赖它们，也不代表可安全重新下载。

## 2. 登记要求

每个外部资产最终应具备：

- 业务/技术 owner 与 on-call。
- source repository 或 vendor source，固定 commit/version。
- contract/schema/config（脱敏）。
- artifact SHA-256、signature/publisher 和获取方式。
- license、NOTICE 和分发限制。
- build/deploy/runbook、环境清单和 dependency。
- authentication/authorization、secret owner 和 rotation policy，但不记录 secret 值。
- monitoring、backup/restore、RPO/RTO、incident 和 rollback。
- 测试/生产边界与验证证据。

未满足这些字段时，不得描述为“已接管”“可复现”或“发布就绪”。

## 3. External Services and Data

| ID | 资产 | 当前存在性 | 仓内消费/证据锚点 | Owner | 主要缺口 | 影响 |
|---|---|---|---|---|---|---|
| `EXT-SVC-001` | Product account/device API backend | `PARTIAL`：只有 Dart/Rust clients | `flutter/lib/models/user_model.dart`, `ab_model.dart`, `group_model.dart`, `src/hbbs_http/` | `OWNER-REQUIRED` | server repo/version、OpenAPI、auth/ACL、deploy/topology、SLA/logs | backend behavior 与权限无法闭环；full-system verification `BLOCKING` |
| `EXT-SVC-002` | `hbbs` rendezvous server | `EXTERNAL/MISSING` | `src/rendezvous_mediator.rs`, `libs/hbb_common/protos/rendezvous.proto`, config clients | `OWNER-REQUIRED` | source/version、production config、topology、key management、logs/monitoring | ID registration/handshake/compatibility 验证 `BLOCKING` |
| `EXT-SVC-003` | `hbbr` relay server | `EXTERNAL/MISSING` | `src/client.rs::{request_relay, create_relay}`, rendezvous protocol | `OWNER-REQUIRED` | source/version、relay policy、capacity、TLS/key、logs/monitoring、DR | relay-only product path 的 integration evidence `BLOCKING` |
| `EXT-DATA-001` | Product database | `MISSING` | client models/contracts only；仓库无 schema/migration | `OWNER-REQUIRED` | engine/version、schema/migration、tenant ACL、backup/restore、RPO/RTO、retention/audit | account/device/data security 与恢复能力 `BLOCKING` |
| `EXT-SVC-004` | ZEGO token broker | `PARTIAL`：deployment script 内嵌 Go module/server source，但没有独立受控 project、`go.sum`、tests、CI/IaC 或生产部署证据 | `src/client/helper.rs`, `scripts/deploy_zego_token_service.sh`, ZEGO docs | `OWNER-REQUIRED` | 独立 source repo/commit、dependency lock、tests/SBOM、HTTPS/auth/rate limit、deploy/logs、credential ownership | 已发现 credential-type material 的暴露面；不得在本文复述其值。Incident response 与 token abuse control 为 P0 `BLOCKING` |
| `EXT-SVC-005` | ZEGO RTC account/service and SDK distribution | `EXTERNAL/UNVERIFIED`；dependency 被声明，但 tracked `pubspec.lock` 未找到对应 ZEGO entry | `flutter/lib/models/zego_voice_call_model.dart`, Flutter dependency config | `OWNER-REQUIRED` | account owner、exact SDK lock/artifact hash、native binary provenance/license、quota/SLA、data region/retention、incident logs | clean clone dependency drift、voice media/privacy 与 release validation `BLOCKING` |
| `EXT-SVC-006` | Firebase/Google client project | `PARTIAL/UNVERIFIED` | `flutter/ios/Runner/GoogleService-Info.plist` 等 client configuration | `OWNER-REQUIRED` | 是否仍 active、project owner、bundle/SHA restriction、API scope/quota、privacy purpose | 旧 upstream ownership 或滥用风险；采用前必须确认 |
| `EXT-OPS-001` | DNS、TLS certificates、load balancer、production endpoint routing | `EXTERNAL` | endpoint/config consumers；具体值不在本表 | `OWNER-REQUIRED` | inventory、certificate owner/expiry、environment split、change/rollback/runbook | endpoint migration 和 HTTPS availability `BLOCKING` |
| `EXT-OPS-002` | Monitoring、logging、alerting、incident/on-call | `MISSING` | repository 只有 local logs/diagnostic behavior | `OWNER-REQUIRED` | metrics/log pipeline、redaction、retention、alerts、SLO/SLA、incident process | 生产可观测性和安全响应 `BLOCKING` |

## 4. Android Native and Reference Assets

| ID | 资产 | 当前存在性 | 仓内消费/证据锚点 | Owner | 主要缺口 | 影响 |
|---|---|---|---|---|---|---|
| `EXT-BIN-ADB-001` | 旧 libadb.so（三 ABI） | RETIRED / T009；当前本地缺失 | 旧 Runner 已删除，Gradle 排除 | 历史 owner 待确认 | 历史 provenance/hash/license 记录保留，未验证来源 | 不再是当前发布 blocker；验证 APK 无旧库 |
| `EXT-REF-ADB-002` | ADB-CODE/ 历史研究材料 | RETIRED / T009；当前本地缺失 | 仅历史集成档案 | 历史 owner 待确认 | origin/revision/legal 记录未闭环 | 不再是当前构建依赖；不得重新纳入产品 |
| `EXT-REF-ADB-003` | LADB/ 历史参考源码 | RETIRED / T009；当前本地缺失 | 仅历史集成档案 | 历史 owner 待确认 | upstream/license 历史义务保留 | 不再是当前构建依赖；不能冒充新组件来源 |
| `EXT-LIB-ADB-004` | libadb-android 3.1.1 | EXTERNAL / SOURCE-PINNED | local-adb.gradle；LocalAdbClient / AdbShell | OWNER-REQUIRED | JitPack；源码注释 revision c849886ebc6d48e7b46d967e78a6bb65c90c3b74；Apache-2.0 选项；实际 AAR hash/NOTICE/解析结果待补 | 本机 ADB 协议依赖；Release 构建验证待补 |
| `EXT-LIB-ADB-005` | spake2-android 2.2.1 | EXTERNAL / SOURCE-PINNED | local-adb.gradle / local-adb.pro；libadb 配对 | OWNER-REQUIRED | JitPack；LGPL-3.0；上游 README 标未审计；artifact hash/JNI ABI/分发材料待补 | 配对与 native 依赖；安全/合规审查未闭环 |
| `EXT-LIB-ADB-006` | bcprov-jdk15to18 1.81 | EXTERNAL / SOURCE-PINNED | local-adb.gradle / AdbIdentity | OWNER-REQUIRED | Maven Central；MIT 风格许可；JAR hash/notice 待补 | ASN.1 证书构造；无全局 provider 注册 |
| `EXT-LIB-ADB-007` | conscrypt-android 2.5.3 | EXTERNAL / SOURCE-PINNED | local-adb.gradle / local-adb.pro；libadb TLS | OWNER-REQUIRED | Maven Central；Apache-2.0；AAR/SO hash、第三方 notices/ABI 待补 | TLS 与反射/JNI 依赖；Release R8/加载待验证 |

新资产的完整 Maven 坐标、上游许可链接、仓库过滤与构建要求集中在 [12_LOCAL_ADB.md](docs/AI_ENGINEERING/12_LOCAL_ADB.md) / [Baseline 11](docs/BASELINE/11_LOCAL_ADB_BASELINE_2026-10-13.md)。SOURCE-PINNED 仅指源码固定版本，不代表实际下载/验证完成。未把旧 libadb.so 的历史 hash 复用到任何新依赖。

历史记录：2026-07-12 当时本机存在三个 `libadb.so` 和一份 license 文件，均未 tracked；2026-10-12 当前检出并不存在该目录，未重新取得或验证这些文件。以下仅保留7月记录的 SHA-256：

| ABI | Observed SHA-256 |
|---|---|
| `arm64-v8a` | `47EA035FA5ED57F6149A2B025BBBD4B21584C355C05D0400416804715E4C12DE` |
| `armeabi-v7a` | `0AFEA102225CD4DDA85D2C01F36A56473724B741CC63B6386163EE286EFF268E` |
| `x86_64` | `62CC0F7707C83C98AA4DE699492C61091EDA9D68B3DB9EA2A185B179DA505BFA` |

这些历史 hash 不能证明当前文件存在，也不能证明 source、publisher、license、完整性或 release approval。

## 5. Windows Drivers, DLLs and Helpers

2026-10-12 T005：下列外部Windows组件仍是对应运行功能的依赖，但PC `build.cmd` 已停止将它们装配进自解压包，也不再把它们是否存在作为打包门。`EXT-WIN-001`—`005` 的来源/运行验证缺口不等于当前纯Release打包阻塞；完整功能发布仍需各自资产证据。`EXT-WIN-006` 是本仓编译产物，继续打包。其他历史workflow/installer未改。

| ID | 资产 | 当前存在性 | 仓内消费/证据锚点 | Owner | 主要缺口 | 影响 |
|---|---|---|---|---|---|---|
| `EXT-WIN-001` | Amyuni/`usbmmidd_v2` virtual-display package | `EXTERNAL/UNVERIFIED`；T005起PC自解压不附带 | `src/virtual_display_manager.rs` | `OWNER-REQUIRED` | vendor/upstream source/version、INF/CAT/SYS/installer hashes、publisher/signature、license、OS support | current active virtual display功能仍依赖；纯Release打包不阻塞 |
| `EXT-WIN-002` | `deviceinstaller64.exe` | `EXTERNAL`，随 `usbmmidd_v2` package 使用 | `src/virtual_display_manager.rs` | `OWNER-REQUIRED` | binary version/hash/signature/source/license | 高权限 driver install/uninstall `BLOCKING` |
| `EXT-WIN-003` | `WindowInjection.dll` / RustDeskTempTopMostWindow source | `EXTERNAL/UNVERIFIED`；当前 binary 缺失；T-2026-10-13-001 删除旧 helper workflow，新 CI 不装配该 DLL | `src/privacy_mode/win_topmost_window.rs`；旧 workflow 可从 8662f23 恢复 | `OWNER-REQUIRED` | approved source fork/commit、reproducible build、hash/Authenticode、license、ABI matrix | privacy injection 与本地高权限代码执行 `BLOCKING`；不是纯 Release 测试包输入 |
| `EXT-WIN-004` | Printer driver package | `EXTERNAL/UNVERIFIED` | `libs/remote_printer/`, `build.cmd` | `OWNER-REQUIRED` | driver source/version、INF/CAT/SYS hashes、signature/publisher、license、OS matrix | remote printer install/release `BLOCKING` |
| `EXT-WIN-005` | `printer_driver_adapter.dll` | `EXTERNAL/UNVERIFIED` | `src/server/printer_service.rs`, `build.cmd` | `OWNER-REQUIRED` | source/ABI/version/hash/signature/license | runtime DLL loading and printer service `BLOCKING` |
| `EXT-WIN-006` | `dylib_virtual_display.dll` | `GENERATED`：source tracked in `libs/virtual_display/`，artifact not tracked | Windows packaging and virtual display FFI | Release owner required | formal toolchain result、artifact hash、Authenticode、ABI test | source provenance较好；仍需正式 build/sign evidence |
| `EXT-WIN-007` | `RuntimeBroker_ashell.exe` helper | `OS-DERIVED/UNVERIFIED`：运行时复制 Windows `RuntimeBroker.exe` 后改名；仓内不存在独立 helper source/binary 是预期状态 | `src/privacy_mode/win_topmost_window.rs`, Windows runtime copy path | Windows/Security owner required | supported Windows build matrix、源文件 Microsoft signature 校验、复制/注入/清理 contract、EDR compatibility | 行为随 OS build 漂移；privacy helper 路径必须按目标 OS 验证 |
| `EXT-WIN-008` | Windows `XpsPrint.dll` OS prerequisite | `OS-DERIVED`：`PrintXPSRawData` implementation 已 tracked，仓外依赖仅为 Windows OS API/DLL | `src/platform/windows.cc`, `src/platform/windows.rs` | Windows owner required | supported OS/API matrix、resource/error contract | 不是缺失的 AShell function；仍需 Win10/11 print compatibility 验证 |
| `EXT-WIN-009` | Dormant RustDesk IDD driver package | `DORMANT/MISSING`：user-mode dylib source tracked，driver INF/SYS/CAT 不在仓库；current active backend 为 Amyuni | `libs/virtual_display/dylib/src/win10/`, `src/virtual_display_manager.rs` | Architecture/Release owner required | 保留/淘汰决定；若启用则需 source/version/hash/signature/license/rollback | 不得误标为 active 或混入当前 release；启用前为 `BLOCKING` |

## 6. Signing, Build and Release Infrastructure

| ID | 资产 | 当前存在性 | 仓内消费/证据锚点 | Owner | 主要缺口 | 影响 |
|---|---|---|---|---|---|---|
| `EXT-SIGN-001` | Android release keystore/signing environment | `EXTERNAL` | `build.sh`, Gradle signing configuration | Release/Security owner required | key owner/custody、alias policy、CI access、rotation、backup、certificate expiry | signed Android release `BLOCKING` |
| `EXT-SIGN-002` | Windows Authenticode identity、timestamp 与 remote signing service | `MISSING/EXTERNAL` | `res/job.py` 保留 remote signing client 线索；canonical Windows build 尚无完整签名 gate | Release/Security owner required | signing service repo/API、certificate chain、HSM/key custody、timestamp、ACL/audit、artifact hash binding、rotation/revoke | trustworthy Windows release `BLOCKING` |
| `EXT-BUILD-001` | Linux Android formal build host/toolchain | `EXTERNAL` | `build.sh`, `/opt/rustdesk-toolchain`-style layout | Release owner required | host image/version、SDK/NDK/JDK/Rust/Flutter/vcpkg locks、access、rebuild/runbook | Android V2 build evidence `BLOCKING` |
| `EXT-BUILD-002` | Windows formal build host and caches | `EXTERNAL` | `build.cmd`, `C:\DevEnv` / `C:\DevTool` layout | Release owner required | host image、VS/Rust/Flutter/LLVM/vcpkg locks、third-party cache provenance、access | Windows V2 build evidence `BLOCKING` |
| `EXT-BUILD-003` | Internal artifact registry / immutable binary store | `MISSING` | required by remaining driver/DLL and release provenance | Release/Security owner required | storage、ACL、retention、checksum/signature manifest、promotion policy | clean-clone reproducibility and supply chain `BLOCKING` |
| `EXT-BUILD-004` | GitHub-hosted Android/Windows CI environment | `CONFIGURED / FIX V0 ONLY`；初版 SDK 初始化失败由用户报告 | build-clients.yml；Ubuntu 22.04 / Windows 2022；T002 两 Android job 固定 setup-android v3.2.2 SHA / CLI 12266719（16.0），由 Google repository 获取 SDK tools | Repository/Release owner | 修复后 CI-01—04 run URL、实际 image/MSVC/JDK patch、resolved lock、artifact hashes、安装验证；新增 Action pin 见 baseline10 | 独立于 EXT-BUILD-001/002 本地构建机；修复后 runner V2 待验证 |
| `EXT-CI-001` | GitHub environments、runners、secrets、branch/release protections | `EXTERNAL/UNVERIFIED`；T-2026-10-13-001 已完成仓内 V0 整改 | `build-clients.yml` 唯一手动入口，固定 direct Action SHA、contents: read、无 commit/push/release/生产 secrets；hosted runner 独立配置 | Repository/Release owner required | runner 实际执行、required checks、environment protection、依赖 provenance/SBOM；尚无完整发布链 | 测试 artifacts only，14 天；runner V2 和 release system 仍未验证 |
| `EXT-REL-001` | Distribution channels and release storage | `EXTERNAL/UNVERIFIED` | 旧 GitHub/WinGet/F-Droid workflow 已移除；历史 packaging docs 保留；当前仅 Actions 测试 artifacts | Product/Release owner required | supported channels、accounts、publisher identity、approval、rollout/rollback、source offer | no authorized release path is currently documented |
| `EXT-COMP-001` | AGPL Corresponding Source / third-party notice delivery | `MISSING` | root AGPL license and binary distribution obligations | Legal/Product/Release owner required | upstream baseline、source archive/tag、offer mechanism、NOTICE/SBOM、legal review | commercial distribution compliance `BLOCKING` |

## 7. Upstream and Dependency Provenance

| ID | 资产 | 当前存在性 | 仓内证据 | Owner | 主要缺口 | 影响 |
|---|---|---|---|---|---|---|
| `EXT-HIST-001` | RustDesk/DaXianDesk upstream baseline and pre-2026 history | `MISSING` | root commit is a snapshot; no upstream tag/commit recorded | `OWNER-REQUIRED` | exact fork point、patch lineage、authors、license/CVE mapping | differential maintenance、CVE triage、attribution `BLOCKING` |
| `EXT-DEP-001` | Cargo Git repositories and revisions | `EXTERNAL/PARTIAL` | Cargo manifests + lockfile; many manifests do not pin `rev` | Domain/Release owner required | approved source list、manifest pin、license/security review、mirror policy | lock update and supply-chain drift risk |
| `EXT-DEP-002` | Flutter/Gradle/Maven/vcpkg/Python/tool downloads | `EXTERNAL/PARTIAL` | pub/Gradle/vcpkg/build scripts | Domain/Release owner required | complete lock/verification、hash/signature、mirror/cache policy、license/SBOM | build reproducibility and dependency takeover risk |

普通、已锁定且可由标准 package manager 复现的依赖继续由 manifests/lockfiles 管理；本表登记的是未固定、高权限、binary-only 或 release-critical 外部资产。

## 8. Release Blockers Summary

正式发布前至少关闭：

1. `EXT-SVC-004` credential/token-broker incident response。
2. T009 退役验证：确认 APK 不再打入旧 libadb.so；EXT-BIN-ADB-001 历史来源缺口不再阻断当前产品。
3. `EXT-WIN-001`—`005` driver/DLL/helper provenance 与 signature。
4. `EXT-SIGN-001`、`002` signing custody 和 verification。
5. `EXT-BUILD-001`—`003` formal build environment 与 immutable artifact store。
6. `EXT-CI-001` / `EXT-REL-001` hardened approval、channel 和 rollback。
7. Full-system validation 所需的 backend、hbbs/hbbr、database、ZEGO contracts 和 owners。
8. `EXT-HIST-001` / `EXT-COMP-001` upstream、SBOM、NOTICE 和 source-offer governance。

关闭状态必须有证据，不能只把 `OWNER-REQUIRED` 改成姓名或把本机 binary 复制进仓库。

## 9. Asset Intake Template

```text
Registry ID：
Asset name/class：
Purpose and consumers：
Owner / on-call：
Source repository / vendor：
Version / commit：
Artifact hash / signature / publisher：
License / redistribution：
Acquisition and reproducible build：
Environment / dependency：
Authentication and secret owner（no values）：
Monitoring / backup / rollback：
Test evidence：
Release impact：
Status and review date：
```

## 10. Maintenance Rules

- 新增仓外依赖、binary、driver、service 或 database 前先登记，再设计集成。
- version、source、hash、signature、owner、license 或 contract 改变时更新同一 ID，不复制新表。
- 不把 production value、credential 或 private URL 写入本文件。
- 不下载、安装、执行或替换登记资产，除非通过对应 `EXECUTE` / `DESTRUCTIVE` / `EXTERNAL` 确认门。
- 未取得资产本体时，只能验证仓内 contract 和调用边界。
- 每次 release readiness review 都复核所有 `BLOCKING` 条目和 review date。
