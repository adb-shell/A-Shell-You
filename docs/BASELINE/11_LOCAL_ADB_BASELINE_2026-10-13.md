# AShell Local ADB Observed Baseline — 2026-10-13

- Baseline ID：`AS-BL-2026-10-13-f84a78f-local-adb`
- Source branch / HEAD：`main` / `f84a78f87d1009c24acfdff478cf95bb463547e5`
- Initial worktree：clean；本次仅后续 Markdown 工作树修改
- Implementation commit：`8662f2369a33f5286c20f9de4d640d2072175ca7`
- Scope：独立本机 ADB 及必要构建事实；不重新基线化后续 CI 的运行结果
- Evidence：repository-observed / V0；正式构建与设备结果 `VERIFICATION-REQUIRED`
- Task / Decision：T-2026-10-13-003 / D-021 / ADR-0017

## Source and Dependency Snapshot

ADB 实现提交新增九文件，原 Activity、主页、app Gradle 三文件增加七行。当前 localadb Java、Dart 与独立 Gradle/ProGuard 文件相对 8662f23 无差异。完整来源与合同见 [12_LOCAL_ADB.md](../AI_ENGINEERING/12_LOCAL_ADB.md)。

| 项目 | 固定值 | 证据 / 状态 |
|---|---|---|
| 产品 | Rust 5.2.1；Flutter 5.2.1+59；内部 flutter_hbb；me.ashell | manifests；本次不修改 |
| Android SDK | compile 34 / min 21 / target 33；ADB 功能 API >= 30 | app/build.gradle / LocalAdbPlugin |
| AGP / wrapper / Java | 7.3.1 / Gradle 7.6.4 / Java 8 | Android Gradle sources；SOURCE-PINNED |
| Kotlin | plugin 2.1.21；stdlib 严格 1.9.10 | 沿用既有声明，不声称版本已统一 |
| ADB library | com.github.MuntashirAkon:libadb-android:3.1.1 | JitPack；transitive=false；源码注释 SHA c849886ebc6d48e7b46d967e78a6bb65c90c3b74 |
| Pairing | com.github.MuntashirAkon.spake2-java:spake2-android:2.2.1 | JitPack；transitive=false |
| ASN.1 | org.bouncycastle:bcprov-jdk15to18:1.81 | Maven Central |
| TLS | org.conscrypt:conscrypt-android:2.5.3 | Maven Central |
| Shrinker | local-adb.pro 保留 org.conscrypt.** / io.github.muntashirakon.crypto.spake2.** | Release 配置来源 |
| Native packaging | 继续排除 **/libadb.so，保持 useLegacyPackaging | 新 AAR JNI 库不等于旧 runner；各 ABI 待产物核对 |
| 本地构建入口 | 仓库根 ./build.sh 1 / ./build.sh 2 | 未调用；不修改脚本 |
| 独立 CI | build-clients.yml，另见客户端 Actions 文档 | 当前 HEAD 含 SDK/MSYS2/host dependency 后续修复，不在本次重审 |

## Evidence Limits

本次只读取源码、Git 历史/差异与上游许可来源，不执行 Gradle 依赖解析、编译、测试、安装、设备或工作流。固定坐标和源码 SHA 不等于实际解析产物的 SHA-256；AAR/JAR/SO、JNI ABI、Release R8、签名和 ROM 结果均待正式收集。

EXT-LIB-ADB-004—007 是新资产；EXT-BIN-ADB-001/EXT-REF-ADB-002/003 继续 RETIRED。旧基线 01—10 均保留原日期与冻结含义，不以新观察覆盖历史。
