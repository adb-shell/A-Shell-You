# GitHub Actions 客户端编译

入口：[`Build AShell clients`](../.github/workflows/build-clients.yml)。仅手动编译，产物保存在 Actions，不创建 GitHub Release、tag 或商店发布。

## 使用

1. 工作流提交并推送到仓库默认分支后，打开 **Actions → Build AShell clients → Run workflow**。
2. 选择要编译的分支与参数：

| 参数 | 可选值 | 默认 |
|---|---|---|
| `platform` | `android`、`windows`、`both` | `both` |
| `android_arch` | `aarch64`、`universal`；仅编译 Windows 时忽略 | `aarch64` |

3. 完成后从运行页 **Artifacts** 或各 job 的下载链接取得产物。最终 artifacts 保留 14 天；内部 `android-native-*` 中间文件保留 1 天。仓库策略可限制保留期限。

| 选择 | 成品 |
|---|---|
| Android / aarch64 | `ashell-android-aarch64-test-signed.apk`，仅 arm64-v8a |
| Android / universal | `ashell-android-universal-test-signed.apk`，arm64-v8a + armeabi-v7a + x86_64 |
| Windows | `ashell-windows-x64.exe` 自解压程序、`ashell-windows-x64.zip` 完整 Release bundle |

两平台 job 独立运行；一边失败不取消另一边。Android native matrix 中所有所选 ABI 成功后才打包 APK。下载包附带 `SHA256SUMS.txt`、源 commit/工具链信息和实际解析的 lockfile；Android 另附签名证书信息与 APK 文件清单。只上传成功检查的成品目录，不上传签名密钥、工作区或日志全集。中间文件过期后重新运行 Android 全部 jobs。

## 签名与安装

- Android 是 **Release 编译 + 测试签名**，无需设置 GitHub Secrets。每次运行在 runner 临时目录生成测试 keystore，通过临时 `flutter/android/key.properties` 接入现有 Gradle 配置，并执行 `apksigner verify`。
- **每次运行的测试证书不同**，也不能覆盖生产证书签名的安装。需要卸载已有应用后再安装，卸载会清除应用数据；需要保留数据时应另行配置稳定的测试签名身份。本工作流不提供生产签名模式。
- Windows 成品没有 Authenticode 签名，系统可能显示未知发布者提示。ZIP 解压后从完整目录启动 `ashell.exe`，不能只取 ZIP 内的单个 EXE。

## 工具链和构建边界

全部 CI 配置位于 workflow：Ubuntu 22.04 / Windows 2022 hosted runner、Rust 1.75.0、Flutter 3.24.5、cargo-ndk 3.1.2、NDK 27.2.12479018（API 21）、JDK 17、Android SDK/Build Tools 34、Windows LLVM 15.0.6 / Python 3.11.9 / CMake 3.28.3 / Brotli 1.1.0、vcpkg `6f29f12e82a8293156836ad81cc9bf5af41fe836`。宿主 image、MSVC/JDK patch 和包源仍可能变动；完整 reproducibility 尚未验证。

- native Android 直接执行 vcpkg 和 cargo-ndk，ARM 两 ABI 使用 `flutter,hwcodec`，x86_64 使用 `flutter`；合并相应的 `libashell.so` 与 NDK `libc++_shared.so` 后执行 Flutter APK 构建。APK 检查全部预期 ABI 和四种核心库，并排除退役 `libadb.so`。HEAD 中新的 local ADB 模块和 Maven dependencies 保留。
- Windows 调用 `python build.py --portable --hwcodec --flutter --vram --skip-portable-pack`，再调用 `libs/portable/generate.py`。仅打包本次 Release，包括本仓编译的 `dylib_virtual_display.dll` 和 Flutter 插件；不附加外部驱动、`WindowInjection.dll` 或上游自定义 engine。相关功能仍需单独验证。
- 使用 tracked FRB bridge，不运行旧 bridge workflow 或自动重生成。两平台应用现有 Flutter dropdown patch，产品版本/包名/协议不改。
- 不调用本地 `build.sh`、`env.sh`、`build.cmd`、`flutter/ndk_*.sh` 或 `flutter/build_android_deps.sh`。本地构建机仍使用原入口；为删除旧工作流解除的元数据依赖不改变入口。
- 唯一 trigger 是 `workflow_dispatch`，`contents: read`，checkout 不保留凭据，直接 Action 引用固定 commit SHA；vcpkg cache 只含编译依赖，不含密钥。

## 验证状态

2026-10-13 仅完成 V0 YAML、Bash/Python/PowerShell 语法、矩阵/权限/路径和源依赖核对，**尚未在 GitHub runner 编译**。当前 `flutter/pubspec.yaml` 的 ZEGO 声明尚未进入 tracked lock，`flutter pub get` 会解析并把实际 lock 保存到 artifact；本轮不自动更新源 lock。CI 首次执行仍需确认 JitPack/ZEGO/vcpkg/Cargo 下载与工具链兼容。

《编译验证需求》：在 GitHub Actions 按 `android+aarch64`、`android+universal`、`windows`、`both` 四组运行，从 checkout 根目录执行工作流定义的步骤；确认 job 选择、APK 签名与 ABI、EXE/ZIP 完整性、失败不交付和下载。参考 [任务记录](tasks/T-2026-10-13-001-client-actions.md)、[CI 基线](BASELINE/09_CLIENT_ACTIONS_BASELINE_2026-10-13.md) 和 [TEST_MATRIX](../TEST_MATRIX.md)。

GitHub 行为参考：[手动运行工作流](https://docs.github.com/en/actions/how-tos/manage-workflow-runs/manually-run-a-workflow)、[下载 workflow artifacts](https://docs.github.com/en/actions/how-tos/manage-workflow-runs/download-workflow-artifacts)、[upload-artifact](https://github.com/actions/upload-artifact)。
