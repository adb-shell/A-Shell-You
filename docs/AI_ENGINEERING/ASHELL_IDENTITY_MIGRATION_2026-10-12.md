# A Shell You 产品身份迁移与静态核验

日期：2026-10-12；Task `T-2026-10-12-002`；Decision `D-015` / [ADR-0014](../ADR/0014-ashell-product-identity.md)。源状态为 `test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76`，开始时 clean。本报告描述该 HEAD 之上的未提交工作树，不是构建或发布报告。

## 1. 最终命名边界

| 用途 | 值 | 主要依据 |
|---|---|---|
| 用户可见产品名、PC标题 | A Shell You | `src/common.rs::get_display_app_name`、`flutter/lib/common.dart::appName`、Windows runner/resource |
| 内部 runtime / 配置身份 | AShell；ORG 为 me | `libs/hbb_common/src/config.rs::APP_NAME/ORG` |
| Rust crate/library、二进制前缀 | ashell | 根 `Cargo.toml`、`src/main.rs`、两份 Cargo.lock 的本项目条目 |
| Flutter 内部 Dart package | flutter_hbb，明确保留 | `flutter/pubspec.yaml`、`package:flutter_hbb/` imports；与 applicationId、native library、显示名独立 |
| Android applicationId / namespace | me.ashell | `flutter/android/app/build.gradle`、三个 manifest、Kotlin package/import |
| Android application / 通知 / 最近任务名称 | A Shell You | `strings.xml::app_name`、manifest application label、Activity TaskDescription |
| Android launcher 名称 | 一个壳子 | `strings.xml::launcher_name`、manifest `.Launcher` activity-alias |
| URI scheme | ashell | Android manifest、`src/common.rs::get_uri_prefix`、Apple/Linux关联 |
| 原生库 | libashell.so / ashell.dll / libashell.dylib / libashell.a | Cargo、Dart/Kotlin/C++加载器、CMake、Xcode和CI |
| Windows正式入口最终文件 | PC-Bulid/ashell.exe | `new-build.cmd::FINAL_EXE` |
| Android正式入口最终文件 | ashell-aarch64-release.apk / ashell-universal-release.apk | `build.sh::finalize_named_apk_to_flutter_dir` 调用；Flutter原始app-release.apk仍是中间输入 |

用户明确纠正：不要把 `flutter_hbb` 当成 AShell 品牌。已撤回该包名及86个Dart文件的关联导入替换；后续会话不得从“全面改品牌”重新推导出内部包名改名授权。`hbb_common`、`scrap`、`pkg2230`、`ffi` 等技术标识也未因品牌统一而改名。

## 2. 修改区域及跨层连接

1. Rust身份、配置、日志/UI文本、TOTP issuer及native导出同步。`src/flutter.rs` 的 `ashell_core_main` / `ashell_core_main_args` / `get_ashell_app_name` 与Dart生成声明、Windows动态符号及Linux/macOS调用匹配。显示名称通过helper分离，避免空格进入服务名、URI和文件路径。
2. Android源码从旧产品包目录移至 `flutter/android/app/src/main/kotlin/me/ashell/`，18个文件保留；四个ADB类/文件前缀改为AShellAdb。Gradle、manifest、proguard、JNI侧import同步。KeyboardKeyEventMapper仍保留既有 `package hbb`。JNI入口 `pkg2230` / `ffi` 未改，其原生库加载名改为ashell。
3. Android主Activity保留真实入口和deep link；新增launcher alias分开桌面名称，并设置TaskDescription维持最近任务名称。系统/OEM是否覆盖标签尚未设备验证。
4. 本地状态类型、MethodChannel、配置option及事件名同步AShell/ashell。`message.proto::ashell_status` 保留string类型和field number 39，Rust变体/调用和Flutter状态事件一起改名；未执行protobuf生成。
5. Windows runner、资源信息、安装/卸载快捷方式和服务显示名称、MSI预处理同步。内部服务/配置身份为AShell，展示为A Shell You。`RuntimeBroker_ashell.exe` 在privacy代码、portable packer和MSI处理端一致。
6. `libs/portable` 改为ashell-portable-packer；writer两端marker为ashell，reader长度由 `"ashell".len()` 推导，避免旧标识的9字节遗留长度读错；启动文件直接使用ashell.exe，移除同名/仅大小写的重命名步骤。
7. `build.sh`、`new-build.cmd`、旧 `build.cmd`、`build.py`、`flutter/build_fdroid.sh`、保留CI平台路径同步；Linux desktop/service/PAM、Flatpak和AppImage资源及消费路径同步。Apple bundle设置me.ashell，app目录AShell.app，显示A Shell You。没有执行这些脚本。
8. 当前工程文档、入口、memory和九个项目Skills的产品前缀已同步为ashell。后续T003按用户明确要求将旧ADR、基线、接管报告、日志的品牌文字/字段和路径也统一；四份文档文件名迁为ASHELL_/ashell并同步引用。历史提交、日期和验证边界不变，原文本和真实历史源仓地址从a036f31恢复，未虚构改名后的源仓链接。

## 3. 保留项与兼容边界

- 用户补充授权后，ZEGO token请求JSON键改为 `ashellSessionId`；`src/client/helper.rs`、`scripts/deploy_zego_token_service.sh`内嵌Go DTO/读取处/示例及三份ZEGO文档同步。没有保留旧键兼容；实际token服务部署未执行，正式使用必须与新客户端同步更新。
- T003按用户明确要求将 `src/common.rs::is_public` 和 `src/hbbs_http/sync.rs::heartbeat_url` 的匹配字符串统一为 `ashell.`。这是本地分类谓词调整，不新增请求目标，也不证明外部ashell域名已配置；未访问服务。
- 用户补充授权后，`build.sh` / `env.sh` 只读取/写出ASHELL_*，移除旧前缀变量回退。正式构建机的既有signing.env和自定义环境需同步变量名；五个签名项为ASHELL_ANDROID_SIGN_ENABLED、KEYSTORE_PATH、KEY_ALIAS、STORE_PASSWORD、KEY_PASSWORD（后四项同样含ASHELL_ANDROID_前缀）。证书/alias/密码值与外部工具链目录未更换，未操作构建机。
- 上游RustDesk/第三方仓库URL、协议/驱动标识、许可证、历史README、原有图标素材保留。未设计新logo，也未删除来源归属。源码级静态检索不能证明产物中不存在所有旧上游字符串或图形指纹。
- iOS `GoogleService-Info.plist` 保留原注册数据；Apple/Google新bundle注册、签名profile及关联配置必须由其owner提供，不能靠把字符串改成me.ashell伪造有效注册。
- Android新包与旧包并存，用户数据、Accessibility/MediaProjection权限与ADB配对状态不自动继承；AShell配置/服务位置与旧产品身份分开。本次没有迁移或删除设备数据。跨版本PC/Android、外部插件和旧deep link兼容性未验证。

## 4. 实际完成的静态核验（V0）

- 30项名称/引用断言通过：9个XML/plist解析、manifest组件文件存在、Gradle包名、两个JNI loader、Windows GetProcAddress与Rust导出对应、展示名、portable marker、protobuf字段编号/类型、Flatpak JSON及版本不变。
- 两份Cargo.lock对比HEAD只变化本项目包名；依赖版本和checksum未改。`flutter/pubspec.lock`未改，产品版本仍5.2.1 / 5.2.1+59。
- `flutter/lib/generated_bridge.dart` 对比HEAD只保留AShell原生C符号迁移，没有FRB结构/API类型变更；Flutter内部package和imports已恢复。正式环境仍需按生成链核对声明，不能把手工标识同步当作codegen完成。
- 当前修改/新增Markdown的显式本地链接已核对，未发现缺失目标；CI行内needs目标存在。修改的Rust/Dart/Java/Kotlin代码HTTP URL集合与源HEAD一致（核对过程不记录地址值）。
- T003最终补齐两处分类谓词及全部文档/文件名，工作区文本和文件路径的旧品牌大小写不敏感扫描为零命中（排除.git历史/对象）。`git diff --check`通过；Flutter内部package仍为flutter_hbb。Git历史和原始来源事实没有改写。
- 本轮没有执行Cargo/Flutter/Gradle/CMake/build.py/生成器、测试、analyzer、依赖安装、产品/服务启动、签名/打包、Git写操作、上传或部署。

## 5. 编译验证需求（NOT_RUN）

这里只列后续正式环境入口，不构成本轮执行授权。

| 环境 | 正式入口（仓库根目录） | 需要取得的证据 |
|---|---|---|
| 已配置Linux Android构建机 | `./build.sh 1`；`./build.sh 2` | Rust/JNI库与APK ABI一致，me.ashell manifest、两种label、实际安装/冷启动、权限和远控链 |
| PC-Build.md规定的Windows构建机 | `new-build.cmd` | ashell.dll导出/加载、runner/portable和资源匹配，安装/卸载/服务/快捷方式、最终PC-Bulid/ashell.exe |
| 保留Linux/Apple/CI目标 | 原对应workflow和平台配置，独立授权后核验 | bundle注册/签名、资源路径、非主目标的平台依赖和发布元数据 |

已知限制：当前 `jniLibs` 不存在，libadb.so及Windows native/driver资产尚未闭环；ZEGO依赖在pubspec.yaml声明但未进入pubspec.lock；保留CI的VERSION仍是历史1.4.0，未擅自升级；Apple签名/Google注册与新bundle未验证。构建脚本本身包含依赖安装、签名和打包动作，本轮均未调用。静态引用一致不能保证编译、链接或运行成功。

接管恢复使用 [PROJECT_MEMORY](../../.codex/PROJECT_MEMORY.md)、[观察基线](../BASELINE/06_ASHELL_IDENTITY_BASELINE_2026-10-12.md)；逐任务记录见 [Task](../tasks/T-2026-10-12-002-ashell-identity.md)。
