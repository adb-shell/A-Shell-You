# T-2026-10-18-010：产品显示名统一为一个壳子

## 交付续接（2026-10-19 / r2）

- 用户明确授权“提交推送并编译”。核对test/4b729dd与远端test一致，工作区仅本任务33个文件；按授权提交推送test，再单次触发Build-Clients-New，Platform=Both、BuildMode=常规编译，沿用不监控要求。
- 本地不运行产品构建，不发布Release，不变更版本或签名配置；GitHub Actions使用现有测试签名和Actions产物。STATE/WORK-20261018-010-r2 / CE-20261019-T010-02。
- 准确提交、推送与dispatch接受回执见Git和本轮交付；触发成功不代表构建成功。以下保留2026-10-18实现阶段的授权与V0记录。

## T0—T4：授权、基线与设计

- Requester / Product Owner：用户；2026-10-18明确授权“能修改的进行修改……仅仅名称”。C0检查、C2显示名称及其必要兼容配置、C1最小事实同步；本轮没有Git写入、build/test/analyze/codegen、设备、签名、发布或生产操作授权。
- Source：test/4b729ddaba22df61a6103829676e1b07324b77e2。初始dirty仅Android `strings.xml` 的 `app_name` 已改为“一个壳子”，保留该既有修改。未切换分支、未覆盖其他工作。
- Principal / Owner：ashell-master；Flutter、Rust、Android显示资源、Release打包约束与Security身份边界由本会话复检，无代理。状态complete / T8（名称实现和V0），风险low，正式构建/设备验证仍需V2/V3。
- Baseline：AS-BL-2026-10-16-4d3c446-adb-render-ui；身份合同AS-BL-2026-10-12-a036f31-worktree、ADR0014；新增决定D-032。TEST_MATRIX：AND-08、WIN-08、FLT-05、RST-06。
- STATE/WORK-20261018-010；CE-20261018-T010-01。External Asset：N/A，无资产、依赖或工具链版本增量。
- 目标：Android应用/通知、Flutter界面/提示、窗口标题、平台产品描述显示“一个壳子”；AShell/ashell/me.ashell/flutter_hbb、协议、配置目录、程序/库文件名、服务内部标识、FFI/JNI符号保持。
- Impact Map：Rust与Flutter显示名入口→UI/翻译输出/Windows窗口；资源与manifest描述→系统显示；Windows/MSI显示名曾兼作快捷方式文件名，必须拆分兼容用途。作者、版权、厂商和2FA issuer不改；历史翻译键不改。
- 方案比较：不做全仓字符串替换；保留旧快捷方式文件名和清理目标，仅改产品展示。Windows新客户端查找旧标题作为兼容回退。自定义品牌继续返回原值。
- 停止/回滚条件：若名称参与未识别的持久化或协议标识，先保持原值；回滚本任务显示名与兼容补充，不修改已有用户数据。旧版本启动新版本窗口等跨版本行为仍需实际验证。

## T5—T8：实现与复检

- Flutter `appName` 与Rust `get_display_app_name`统一默认显示名；设置/关于/桌面标签使用显示入口。翻译在查表后替换产品名，查表键和插值合同不改。
- Android `app_name`保留此前修改；manifest仍引用原resource，launcher不变。iOS/macOS plist、Linux窗口与desktop/service、Flatpak产品名、Windows资源和Cargo产品描述同步；版本与依赖不改。build.cmd只改一行日志。
- Windows保留旧 `.lnk` 文件名及Publisher，服务/卸载条目的显示文字改名；服务键、程序路径、sc命令目标不改。窗口标题使用Unicode转义并兼容查找旧默认标题。
- MSI拆分ProductDisplay与ProductShortcut；快捷方式名称和清理目标保持，其他安装界面显示新名称。数据库及摘要代码页1252调整为936以容纳中文，Culture/en-us和ProductLanguage/1033保持，Product/UpgradeCode/安装目录/注册表标识生成规则保持。
- 保留：2FA issuer、作者/版权/CompanyName/Manufacturer/Vendor、翻译lookup keys、旧窗口兼容字符串及快捷方式名。不存在改包名、迁移数据、改变身份验证、屏幕采集、无障碍、ADB、账号/网络或编排逻辑的增量。
- V0完成：git diff检查；Cargo TOML/Apple plist/XML解析；MSI Python AST语法与默认/自定义名称分支核对；936文案编码；Windows快捷方式模板与HEAD逐行一致；包名、权限、2FA、翻译表、FFI导出和lockfiles与HEAD一致。
- 未运行本地产品构建/测试/分析/codegen或设备操作，未提交/推送/触发CI。不将V0等同于编译/运行通过。同步当前总览、项目记忆、Decision/Task/Event索引；ADR0014架构边界保持，不新增ADR或仓外资产。

## 编译验证需求（待授权）

- Android：正式Linux环境，在仓库根目录执行 `./build.sh 1`；检查应用列表、通知/权限界面、设置/关于、升级保留数据及原有连接/ADB链路。
- Windows：正式Windows构建环境，在仓库根目录执行 `build.cmd`；检查窗口/产品属性、重复启动/URI转发、旧版升级、服务、启动快捷方式及卸载。MSI另按现有正式打包流程验证中文显示和升级/清理；本轮未执行。
- iOS/macOS/Linux：对应正式平台构建并检查系统显示名称；当前只有配置静态解析证据。

## 编码依据

- [WiX代码页](https://docs.firegiant.com/wix/tools/codepage/)：安装数据库与摘要信息各有编码设置。
- [Microsoft代码页标识](https://learn.microsoft.com/en-us/windows/win32/intl/code-page-identifiers)：936为简体中文代码页。本任务不改变安装语言或业务逻辑。
