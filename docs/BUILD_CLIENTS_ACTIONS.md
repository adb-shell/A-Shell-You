# GitHub Actions 客户端编译

2026-11-11 T001：主分支合并保留2.0.26 / 2.0.26+59并纳入开发者画面优先，见[文档与合并任务](tasks/T-2026-11-11-001-docs-main-merge.md)。本轮不触发或监控编译；以下历史回执都不能证明本次合并版本已编译通过。

2026-11-07 T004回执：开发者画面优先源码test/25bc2ce04484c6d40f8c06325008d78a347432f7已推送，Build-Clients-BT / Both / 常规编译POST HTTP204（09:26:23 UTC）。单次定位尚未列出run；用户要求不监控，未继续查询结果。仅补回执文档不再dispatch；具体边界见[任务](tasks/T-2026-11-07-004-developer-screen-priority.md)，不据HTTP204宣称编译成功。

T003版本源码df0e129已提交推送main，版本2.0.26 / 2.0.26+59，构建号59保持；未触发编译，见[版本任务](tasks/T-2026-11-07-003-openworld-version.md)。以下T002及T001是旧版本触发回执，不表示本版本已构建。

历史T002已将test/d9f1119合并main并推送482eda5，版本保持5.2.1 / 5.2.1+59；2026-11-07 17:38:48 +09:00一次Both/常规编译获HTTP204，按要求不监控，不查询run或结果。准确源码SHA与回执见[main交付任务](tasks/T-2026-11-07-002-openworld-main.md)。以下test运行属于迁移来源历史，不能替代本次main结果。

2026-11-07产品身份迁为开放世界/OpenWorld，唯一手动工作流Build OpenWorld Clients-BT。jobs/输入/工具链/actions pins/权限保持，native/缓存/签名展示/产物名同步。已按用户要求交付test并运行Both/常规编译；准确head/run与结果见本次迁移回执，旧run不能替代。见[身份迁移](AI_ENGINEERING/OPENWORLD_IDENTITY_MIGRATION_2026-11-07.md)。

## 入口与参数

| 入口 | 文件 | 参数 |
|---|---|---|
| Build OpenWorld Clients-BT | [Build-Clients-BT.yml](../.github/workflows/Build-Clients-BT.yml) | Platform：Both / Android / Windows，默认Both；BuildMode：常规编译 / 原始编译，默认常规编译 |

在Actions选择Build OpenWorld Clients-BT、Run workflow，再选择实际已推送的分支。双端编译选Platform=Both；仅PC选Windows；通常使用BuildMode=常规编译。参数区分大小写，旧Build-Clients/Build-Clients-New入口及platform/android_arch参数不适用。Android固定aarch64/arm64-v8a；需要universal时使用正式Linux本地入口build.sh 2，并另行授权/验证。

“常规编译”复用受key约束的依赖/native构建缓存；“原始编译”跳过构建产物缓存恢复并清理相应输出，仍可复用工具和源码下载缓存，不等于断网或绝对无缓存编译。两种模式以实际workflow条件为准，不改变产品功能开关。

## 产物

| 平台 | 文件 |
|---|---|
| Android | openworld-android-aarch64.apk（Release编译、临时测试签名） |
| Windows | openworld-windows-x64.exe自解压程序；openworld-windows-x64.zip完整Release bundle |

成品从Actions运行页Artifacts/下载链接获取，保留14天；Android内部native中间产物保留1天，过期后需重新执行完整相关job。另有build-info、SHA256SUMS、解析依赖/锁文件和签名证据，准确清单以产物为准。Android APK等待native完成；Windows独立job，分别上传ZIP与EXE。

Android每次运行生成临时测试keystore并执行apksigner verify，证书不同的包不能直接覆盖安装；卸载会清除应用数据，保留数据需另行设计稳定签名。当前Windows也使用临时自签名Authenticode测试证书，签名只在runner临时信任环境验证，不代表用户设备信任或生产发布签名。私钥不随artifact上传。ZIP应完整解压运行，不能只取其中主EXE。

## 工具链和边界

源码固定Rust 1.75.0、Flutter 3.24.5、cargo-ndk 3.1.2、NDK 27.2.12479018、Android API21/Build Tools34、JDK17、command-line tools12266719；vcpkg baseline为6f29f12e82a8293156836ad81cc9bf5af41fe836。Hosted runner为Ubuntu22.04/Windows2022，Windows LLVM15.0.6；宿主patch/下载源仍可能变化，不能由版本固定推定完全可复现。

- CI独立配置SDK/NDK/vcpkg并直接调用cargo-ndk、Flutter，Windows调用build.py和portable/generate.py，不运行本地build.sh/env.sh/build.cmd。
- Android检查目标ABI、核心native库与scrcpy资产；退役libadb.so继续排除，新LocalAdb及SPAKE2/Conscrypt依赖保留。
- Windows只打包本次Release及其插件/库；原有外部驱动功能的可用性仍需正式验收。
- 沿用tracked FRB桥，不以本任务自动生成或修改协议/桥文件。
- contents: read、checkout不保存凭据、直接Actions引用固定SHA；没有push/PR自动构建触发器，没有生产签名Secrets依赖。
- 本地构建机继续使用[build.sh](../build.sh) / [build.cmd](../build.cmd)，流程与CI相互独立。

## 验证记录

此页反映48226fb的BT workflow配置，V0。历史sdkmanager、Java或Windows失败记录留在各任务中；不以旧失败证明当前失败，也不以dispatch接受证明当前构建通过。正式结果必须记录源SHA、分支/参数、run/job、artifact/hash、签名和实际结论；T001的test Windows运行不替代T002的main双端运行，当前回执见各任务。
