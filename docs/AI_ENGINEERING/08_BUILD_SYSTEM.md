# AShell 构建系统 / Build System

接管基线：2026-07-12  
状态：`verified` + `verification-required`

> 本轮只完成静态接管，没有运行任何 Rust、Flutter、Gradle、Android、Windows 或 Docker 构建/测试命令。下列命令是正式环境入口和待验证说明，不代表已通过。

版本、依赖、lock hashes 与正式 host contract 的当前冻结值见 `docs/BASELINE/`；正式验证 case 和 evidence schema 见根目录 `TEST_MATRIX.md`。本文解释构建链路，不替代 baseline snapshot。

## 1. 版本与工具链锚点

| 项目 | 当前源码值 |
|---|---|
| Rust package/crate | `ashell` |
| Rust version | `5.2.1` |
| Rust edition | `2021` |
| Rust MSRV | `1.75` |
| Flutter package | `flutter_hbb`（内部包名保留） |
| Flutter version | `5.2.1+59` |
| Android applicationId | `me.ashell` |
| Android compile/target/min SDK | `34 / 33 / 21` |
| Android native library | `libashell.so` |
| Windows native library | `ashell.dll` |

版本号是发布资产。本轮和后续 AI 工作都不得自动修改。

## 2. Cargo Workspace

根 `Cargo.toml` 同时是主 crate 与 workspace root。成员：

1. `libs/scrap`
2. `libs/hbb_common`
3. `libs/enigo`
4. `libs/clipboard`
5. `libs/virtual_display`
6. `libs/virtual_display/dylib`
7. `libs/portable`
8. `libs/remote_printer`

`workspace.exclude` 仍列出当前不存在的 `vdi/host` 与 `examples/custom_plugin`，属于构建元数据漂移。

主 feature 组合跨平台差异较大，`flutter` feature 引入 Flutter Rust Bridge；默认 feature 是 `use_dasp`。实际发布命令还叠加 `hwcodec`、`vram`、`portable` 等选项。不要仅用 `cargo build` 的默认结果替代产品构建验证。

## 3. 生成文件

以下文件需要区分“source of truth”和“generated artifact”：

| 产物 | 来源/生成器 |
|---|---|
| `src/bridge_generated.rs` | Flutter Rust Bridge codegen |
| `src/bridge_generated.io.rs` | Flutter Rust Bridge codegen |
| `flutter/lib/generated_bridge.dart` | Flutter Rust Bridge codegen |
| protobuf Rust output | `.proto` + build script/tooling |
| `src/version.rs` | 构建期生成/忽略，不是 tracked source |

FRB 文件带 1.80.1 生成标记，且 Git 历史显示 Rust FFI 后续仍有修改。任何 `src/flutter_ffi.rs` 签名变更都必须执行正式 codegen 并检查三方 diff；手工编辑 generated file 只能作为紧急诊断，不能作为最终方案。

## 4. Android 正式入口

推荐入口是 Linux 构建机仓库根目录的：

```bash
./build.sh 1
./build.sh 2
```

- mode 1：`aarch64` signed APK。
- mode 2：universal signed APK，包含 arm64-v8a、armeabi-v7a、x86_64。

脚本默认工具链根位于 `/opt/rustdesk-toolchain`，负责：

- Rust Android targets。
- Android SDK/NDK、vcpkg 与 native dependencies。
- 构建 `libashell.so`。
- 复制到 `flutter/android/app/src/main/jniLibs/<abi>/libashell.so`。
- Flutter/Gradle packaging、zipalign/signing 和产物命名。

签名环境是发布机机密，不得复制到仓库或诊断输出。

### Android打包错误传播（T008 / 2026-10-12）

`build.sh::flutter_build_split_abi_apk` 和 `flutter_build_universal_apk` 供调用者命令替换捕获，stdout仅返回APK路径；阶段标题、verbose与Flutter日志均输出stderr。Flutter构建失败显式保留退出码，两个模式调用者立即return，不再进入旧APK查找、签名和成品复制。此前全局set -e不能可靠保护命令替换内部，日志也曾污染apk_in；详见 [T008](../tasks/T-2026-10-12-008-android-resource-build.md)。

用户在原目录增量构建时遇到AGP7.3.1的mergeReleaseResources/source-sets异常，与官方206674992同类问题相符，根因仍需完整stacktrace和构建机验证。建议该机先对Flutter生成目录执行flutter clean再用修正后的build.sh重试；本轮未自动清理、未改AGP/Gradle/Kotlin版本、未构建。新图标配置保持不变，不能把脚本通道修正等同于资源合并错误已复现解决。

### 旧 ADB 打包依赖已退役（T009）

已删除旧 ADB 源码/混淆规则；build.sh 不再检查从未调用的 host adb，env.sh 也删除独立 adb 的 apt 安装/强制检查，保留标准 Android SDK platform-tools。app/build.gradle 通过 `jniLibs.excludes += ["**/libadb.so"]` 排除旧构建目录可能留下的二进制；其余库仍沿用 `useLegacyPackaging = true`。这是 [AGP 7.3 官方 excludes API](https://developer.android.com/reference/tools/gradle-api/7.3/com/android/build/api/dsl/JniLibsPackagingOptions) 支持的打包过滤。

当前检出没有 jniLibs、ADB-CODE 或 LADB 资产；不会下载或删除外部构建机资产。忽略规则和历史 provenance/许可记录保留。运行时主库/JNI、工具链版本、两种打包模式与 T008 错误传播保持；此修改只经 V0，须正式构建检查 APK 排除结果。

## 5. Windows 正式入口

推荐入口是 Windows 正式构建机仓库根目录：

```bat
build.cmd
```

该脚本按 `C:\DevEnv` + `C:\DevTool` 布局调用：

- Visual Studio Build Tools/MSVC。
- Rust 1.75 MSVC toolchain。
- Flutter 3.24.x 环境。
- LLVM/libclang。
- vcpkg dependencies。
- `build.py --portable --hwcodec --flutter --vram --skip-portable-pack`。
- portable self-extract packer。

最终自解压产物固定为 `PC-Bulid\ashell.exe`。这个目录名当前拼写就是 `PC-Bulid`，不要在未迁移所有脚本前擅自更正。

2026-10-12 T005：旧PC入口由原新环境脚本替换，仓库只保留 `build.cmd`。脚本先检查Python Brotli、工具链与manifest恢复状态；对固定的 `flutter/build/windows/x64/runner/Release` 路径和reparse point做检查后清除旧bundle，再编译。Rust/Flutter增量缓存保留，`CARGO_TARGET_DIR` 固定为仓库 `target`。

自解压直接消费本次Release目录，不再搬运到根目录暂存文件夹，也不复制 `WindowInjection.dll`、`usbmmidd_v2`、打印驱动/adapter或外部Driver.ps1。`dylib_virtual_display.dll` 是 `build.py` 从本仓源码生成并放入Release的DLL，仍随编译产物打包；Flutter插件/运行库和data资源同样保留。这不是全功能驱动安装包，依赖未附带组件的隐私模式、虚拟显示和打印功能仍需单独验证。

打包阶段不自动pip安装；Brotli需预装。Rust/Flutter正常构建仍可能解析/下载编译依赖，不承诺完全离线。`libs/portable/generate.py` 检查输入目录/启动文件及范围，拒绝指向bundle外的链接，压缩时不切换全局cwd；Cargo失败通过异常返回非零，CMD停止交付。脚本在正常和已处理失败路径恢复manifest，检测到旧备份则停止以保留恢复材料。最终只有成功且非空的packer产物才复制到输出目录。

验证范围：仅V0；用户提供的旧脚本日志只证明此前主编译成功，不能证明本次修改后的构建或自解压成功。任务及正式环境验证要求见 [T005](../tasks/T-2026-10-12-005-pc-build-entry.md)。

`PC-Build.md` 包含大量历史/环境搭建材料，其中仍有上游 RustDesk 名称和旧命令；当前入口以 `build.cmd` 源码为准。

## 6. 其他平台

通用历史入口包括：

```bash
python3 build.py --flutter --release
cd flutter && flutter pub get
cd flutter && flutter build apk --release
cargo build --release --features flutter
```

但 `build.py` 的 macOS/Linux 分支仍有 `librustdesk` 等上游命名残留，未在本轮验证。iOS、Linux、macOS、Web 的源码存在不等于 AShell 当前发布矩阵已经覆盖这些平台。

发布支持矩阵必须由产品 owner 明确：

- Tier 1：实际发布并有回归设备。
- Tier 2：可构建但不承诺生产支持。
- Source retained：仅保留上游代码。

## 7. CI/CD 现状

2026-10-13 T001 用唯一 `.github/workflows/build-clients.yml` 替换 12 个旧上游 workflow，并删除空 submodule 对应、目标仍为 master 的 Dependabot 编排。仅 `workflow_dispatch`，选择 android/windows/both；Android 选择 aarch64/universal，默认 both + aarch64。Android native matrix 与 Windows x64 独立运行，APK 等待所选 ABI 完整。

CI 在 GitHub-hosted Ubuntu 22.04 / Windows 2022 上独立配置工具链，不运行本地 build.sh/env.sh/build.cmd 或 Android helper shell scripts。Android 直接调用 vcpkg/cargo-ndk/Flutter；Windows 调用 build.py 与 portable generate.py，只打包本次 Release。全部直接 Action 固定 SHA、contents: read、checkout 不保存凭据，没有自动 Git 写入、Release/tag/store 或生产签名 secret。

Android Release APK 使用每次运行生成的临时测试 key，不能覆盖不同证书的安装。Windows 输出 unsigned EXE + ZIP；最终 artifacts 与 hash/provenance 保留 14 天，中间 JNI artifacts 1 天。tracked bridge 直接使用；不因删除 bridge workflow 而丢失 clean checkout 必需文件。旧 libadb.so 排除继续生效，8662f23 新增 local ADB 依赖保留。

为解除旧 workflow 文件读取，build.sh 的 FRB 版本保持自身默认，vcpkg 读取 manifest baseline；历史 flutter/build_fdroid.sh 保留自身冻结版本。二者未在本轮运行，现有 Linux/Windows 本地构建入口继续保留。

操作、完整版本表、签名/依赖限制见 [Actions 使用说明](../BUILD_CLIENTS_ACTIONS.md) 和 [CI baseline](../BASELINE/09_CLIENT_ACTIONS_BASELINE_2026-10-13.md)。只有 V0，GitHub runner V2 尚未执行；依然没有 push/PR 自动质量 gate、bridge drift 检测或正式发布证据。

## 8. 依赖与供应链

风险点：

- 多个 Cargo Git dependency 在 manifest 中未固定 `rev`，尽管 `Cargo.lock` 固定当前 snapshot。
- Flutter/Gradle/vcpkg/下载脚本形成多套 dependency resolver。
- ignored native binary 无可复现来源。
- Windows driver、DLL injection helper 和 virtual display driver 属于高权限第三方资产。
- 根 license 为 AGPL-3.0，商业分发还需要完整 third-party notices、source offer 与修改披露策略。

目标产物应包含 SBOM、dependency lock snapshot、binary hashes、toolchain versions、signing identity 和 source commit。

## 9. 构建变更规则

- 不在开发机“顺手升级”Rust、Flutter、NDK、Gradle、ZEGO 或 vcpkg baseline。
- toolchain 升级单独决策，先列兼容矩阵和 rollback。
- 更改 crate/package/SO/DLL/applicationId/deep link 必须全链搜索。
- 生成文件由指定 codegen 更新并与 source signature 一起审查。
- 签名 secret、keystore、token 和证书绝不进入仓库。
- 构建脚本不得静默下载未校验 executable。
- release artifact 必须能追溯到 commit、lockfile、toolchain 和 SBOM。
- AI 不得自动执行 build、sign、upload、release、version bump 或 Git 写操作。

## 10. 当前静态疑点

1. `src/cli.rs` 与当前 `LoginConfigHandler::initialize(...)` 参数/Interface 定义可能漂移；`cli` feature 需要正式编译验证。
2. `src/version.rs` 被旧结构文档当作 source，但实际为生成/ignored 文件。
3. `workspace.exclude` 引用不存在目录。
4. FRB generated files 与最近 FFI 修改可能不同步。
5. 非 Windows/Android build naming 尚有 RustDesk 残留。
6. 旧 libadb.so 已被排除（T009）；正式产物仍须检查排除生效及其他 ABI/JNI 库完整。
7. workflow 全手动，当前没有可证明的 required check。

## 11. 发布交付清单

正式发布至少需要：

- 明确 release request 和批准人。
- clean source baseline 与 reviewed diff。
- 正式环境完整构建和测试记录。
- 版本、渠道、平台和回滚策略。
- secret scan、dependency audit、SBOM、license review。
- artifact hash 与签名验证。
- Android 权限/targetSdk/store policy 复核。
- Windows driver/privacy/injection 行为复核。
- staging smoke test 和 upgrade/rollback test。
- release notes、known issues、owner/on-call。

本轮不具备也未执行上述发布动作。

## 产品图标资源（T006 / 2026-10-12）

- Windows主程序由 `flutter/windows/runner/Runner.rc` 读取 `resources/app_icon.ico`；自解压EXE由 `libs/portable/build.rs` 读取 `res/icon.ico`。两者与 `res/tray-icon.ico` 均已同步为同源9尺寸ICO（16/20/24/32/40/48/64/128/256）。
- `flutter/lib/common.dart::loadIcon` 优先读取 `flutter/assets/icon.png`（1024），失败读取独立同源 `icon-fallback.png`（256），再次失败保留布局空位；旧 `icon.svg` 保留但不再作为该入口回退。`src/tray.rs` 也优先读取已打包的Flutter主PNG，备用ICO保持同源。
- Android五密度 `ic_launcher.png` / `ic_launcher_round.png` 为48/72/96/144/192；`ic_launcher_foreground.png` 为108/162/216/324/432，按66/108比例居中保留完整原图。T006时anydpi-v26的monochrome复用前景，T007已改为独立机器人单色层（见下）；白色背景及Manifest的roundIcon指向ic_launcher保持不变。
- `res/icon.png` 同步为1024源图。现有构建脚本不会自动运行launcher icon生成器；两个新增Flutter PNG已加入根.gitignore的定向例外，pubspec沿用assets/声明。新增源图也会被其他使用共有Flutter角标的界面消费，不等于Apple/Linux/web专有图标迁移。
- 本次仅将用户提供的1254×1254 RGBA图做确定性尺寸/格式转换，未重新设计或修改原文件。21个图片资源及加载链的V0检查见 [T006](../tasks/T-2026-10-12-006-product-icons.md)，正式EXE/APK构建与显示均NOT_RUN。

### 图标覆盖补充（T007 / 2026-10-12）

- Android通知/前台服务的两处 `setSmallIcon(R.mipmap.ic_stat_logo)` 已替换五密度白色机器人alpha图（24/36/48/72/96）。两个adaptive XML的monochrome改用新 `ic_launcher_monochrome.png`（108/162/216/324/432），避免白底板变成唯一主题轮廓；彩色桌面图不变。
- 默认悬浮球 `drawable/floating_window.xml` 改为引用 `drawable-nodpi/ashell_floating_icon.png`（320），原Kotlin消费资源名保持不变。已有用户 `customSvg` 仍优先，未清理用户配置。六个新增Android PNG均有精确ignore例外。
- Windows旧Sciter `src/ui.rs::get_icon` 的Windows分支改为嵌入同源 `res/128x128.png`，覆盖其主窗/远程窗/CM/聊天窗消费链。portable `BasicApp.window_icon` 持有同一PNG生成的HICON，窗口builder绑定small，并通过WM_SETICON绑定big；不改变已有加载文字/动画。
- Windows EXE安装路径的卸载快捷方式 `IconLocation` 改为主程序exe索引0。服务及portable-service复用主EXE，程序列表/文件关联/普通快捷方式使用主EXE图标；MSI的AppIcon由preprocess从 `res/icon.ico` 复制。当前没有实际生成MSI资源副本或EXE。
- services.msc通用图标和OS派生RuntimeBroker辅助进程不属于项目品牌资源；未修改系统或第三方签名二进制。其他平台专用图标保留，通用 `res/128x128.png` 的既有bundle消费者也会看到新图。
- 详见 [T007覆盖表与28文件清单](../tasks/T-2026-10-12-007-icon-coverage.md)。12PNG/16XML及引用V0检查通过，T006图片hash不变；本地NWG源码/portable lock缺失，API仅对照官方上游源码。无编译、产品测试、运行、打包或Git写入，Windows实际Shell与Android设备显示待验证。

## 12. 2026-10-12 AShell身份迁移补充

当前命名和静态证据见 [迁移报告](ASHELL_IDENTITY_MIGRATION_2026-10-12.md)。Rust crate/native artifact迁为ashell，Flutter内部package保留flutter_hbb。Android与Windows正式脚本、portable marker长度、加载端和C符号已同步；未执行任何脚本/编译。FRB generated_bridge.dart仅做C符号的机械同步，与手写导出对应，未改API类型；正式环境仍需重生成核对，不能把V0检查当成codegen或构建通过。jniLibs缺失、ZEGO lock drift、外部签名/驱动及Apple/Google注册仍待验证。
