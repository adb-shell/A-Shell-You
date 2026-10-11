# T-2026-10-12-007：Windows / Android 图标覆盖复核与补齐

## 0. Task Metadata

- Status / T-State：completed / T8 / V0；Created：2026-10-12。
- Owner：当前用户；Principal：Codex / ashell-master；Release/Rust/Android/Security按对应Skill静态复核，无子代理。
- Source：test/c9b7cd135be838989953e7ab7037f8c6d8d6aa31；initial dirty为T006的32文件，记录hash后保护。
- Baseline：AS-BL-2026-10-12-c0951b5-pc-build-worktree（docs/BASELINE/BASELINE_INDEX.md），已由c9b7cd1固化；当前资源delta见T006，不改旧baseline。
- Recovery：STATE-20261012-007 / WORK-20261012-012；无其他活动任务，历史授权不继承。
- Risk：low；最高执行验证V0；WIN-08、AND-08、FLT-02/07、RST-01正式执行NOT_RUN。
- ADR / Decision：ADR-0014沿用；N/A — no decision delta。
- Event：CE-20261012-T007-01；关闭目标STATE-20261012-008 / WORK-20261012-014。

## T0—T1. Authority and Acceptance

- 当前用户明确要求检查并补齐Windows进程/任务栏/状态栏/服务等，以及Android图标遗漏；可以继续从同一原PNG生成并替换。
- C0读取、V0图像/资源检查；C2指定范围资源与必要图标引用调整、工程记忆同步。允许确定性尺寸、格式及系统单色遮罩转换；不生成式重绘。
- 禁止：编译、产品build/test/analyze/codegen、依赖安装、启动程序/服务/设备、实际打包/签名/上传、Git写入、删除移动、版本与权限变化。
- Acceptance：能从源码列出各产品图标消费链；可证实的旧标志或默认图标遗漏补齐；Android通知/主题轮廓能辨认机器人；T006图片保持不变。
- Non-goals：替换OS/设备类型/功能按钮图标、第三方签名可执行文件、用户持久化自定义图片，及Apple/Linux/web专有品牌资源。

## T2. Coverage Map / Findings

| Surface | Evidence | Finding / handling |
|---|---|---|
| Windows主EXE/进程/窗口/任务栏 | Runner.rc IDI_APP_ICON；win32_window.cpp::GetWindowClass | 消费T006 app_icon.ico，无第二旧图资源；实际Shell/多窗口显示待运行验证 |
| Windows托盘 | src/tray.rs::make_tray / load_icon_from_asset | Flutter主PNG优先、tray-icon.ico备用已同源 |
| Windows服务/portable-service进程 | src/platform/windows.rs::get_create_service；src/server/portable_service.rs | 服务/辅助命令复用主EXE，不存在独立产品服务图标文件 |
| Windows程序列表/文件关联/普通快捷方式 | windows.rs DisplayIcon、DefaultIcon、get_tray_shortcut；MSI wxs/preprocess.py | 引用主EXE或res/icon.ico；MSI Resources副本由preprocess生成，本地不存在，不伪造生成结果 |
| Windows卸载快捷方式 | windows.rs::install_me | EXE安装器强制msiexec.exe图标，补为应用exe的索引0，与MSI品牌路径一致 |
| Windows旧Sciter主窗/远程窗/CM/聊天窗 | src/ui.rs::get_icon → common.tis/header.tis；ui/cm.rs、ui/remote.rs | Windows原来命中硬编码旧PNG；添加windows分支读取同源res/128x128.png，其他OS分支不改 |
| 自解压EXE与进度窗口 | libs/portable/build.rs、src/ui.rs::build_ui | EXE已更新；NWG窗口未指定图标，补持有图标资源并绑定small/big |
| Android桌面/应用/服务继承图标 | main Manifest；debug/profile manifest无图标override | ic_launcher普通/自适应已更换；roundIcon也指向ic_launcher，备用圆形已同步 |
| Android通知/前台服务 | DFm8Y8iMScvB2YDw.kt两处setSmallIcon(R.mipmap.ic_stat_logo) | 五密度ic_stat_logo仍是旧标志，替换为原机器人白色alpha轮廓 |
| Android主题桌面 | anydpi-v26两个adaptive XML | monochrome复用带白底前景时仅剩底板形状，新增五密度机器人单色alpha层 |
| Android默认悬浮球 | drawable/floating_window.xml → DFrLMwitwQbfu7AC.createView | 旧矢量标志，改为同源320 PNG的bitmap资源；保持R.drawable标识及Kotlin调用不变 |
| Android自定义悬浮图 | DFrLMwitwQbfu7AC.onFirstCreate/createView | 持久化customSvg可覆盖默认图；保留用户配置，不自动抹除 |
| 启动画面/加载图/功能图 | styles、launch_background；portable label.png/spin.gif；scam.png、操作/OS SVG | 启动无独立旧品牌图；Loading文字、转圈、警告、系统/功能图不是旧产品标志，保留 |
| Windows OS-derived helper | privacy_mode/win_topmost_window.rs + windows.rs::check_update_broker_process | RuntimeBroker_ashell.exe复制自OS RuntimeBroker.exe，未修改系统/签名二进制；不能承诺所有OS显示均是产品图标 |

## T3—T4. Design / Confirmation / Rollback

- 当前请求直接授权上述遗漏补齐；不重复请求确认。
- 保留T006普通/自适应彩色图。单色层仅从原图黑色机器人提取alpha，保持眼睛/头身间隙及比例，通知版居中20dp/24dp，主题版居中66dp/108dp。白底板不作为系统单色轮廓。
- 默认悬浮球使用drawable-nodpi/ashell_floating_icon.png，原XML转为bitmap引用；无Kotlin、JNI、权限/生命周期变更。
- Windows旧UI从128 PNG生成data URI，使用已有sodiumoxide base64接口；不改依赖或非Windows旧分支。
- NWG以现有image-decoder从同源PNG加载图标，资源由BasicApp字段持有；window先销毁，Icon随后Drop。small由builder绑定，big用本机新建窗口的WM_SETICON绑定，不读写其他进程。
- Security：图片来自同一用户输入；所有输出限制仓内。新增Win32调用只传本对象有效HWND/HICON，生命周期明确；不触及服务执行路径、注入或权限。
- Rollback：未来获准后仅反向T007 patch与新增资产，不回退/覆盖T006；当前不执行Git恢复或删除。

## T5. Changes

本轮28文件：12图片、3 Rust、3 XML、1 ignore配置、9工程/记忆文档。与T006重叠9文件；合并工作树51文件未提交。未新增依赖或改动Kotlin/权限/协议。

| 路径（相对仓库根目录） | 修改与尺寸 |
|---|---|
| `res/128x128.png` | 替换为原图128×128；Windows legacy与portable窗口共同读取；也是既有Cargo.bundle通用资源 |
| `src/ui.rs` | `get_icon`新增Windows分支，include_bytes上述PNG后使用已有base64接口；Mac/Linux分支保留 |
| `libs/portable/src/ui.rs` | `WINDOW_ICON_DATA`、`BasicApp.window_icon`、`build_ui`绑定small/big窗口图标，资源持有至窗口销毁 |
| `src/platform/windows.rs` | `install_me`生成的卸载快捷方式IconLocation由msiexec.exe改为应用exe索引0 |
| `flutter/android/app/src/main/res/mipmap-mdpi/ic_stat_logo.png` | 替换；24×24，内容14×20 |
| `flutter/android/app/src/main/res/mipmap-hdpi/ic_stat_logo.png` | 替换；36×36，内容21×30 |
| `flutter/android/app/src/main/res/mipmap-xhdpi/ic_stat_logo.png` | 替换；48×48，内容28×40 |
| `flutter/android/app/src/main/res/mipmap-xxhdpi/ic_stat_logo.png` | 替换；72×72，内容42×60 |
| `flutter/android/app/src/main/res/mipmap-xxxhdpi/ic_stat_logo.png` | 替换；96×96，内容56×80 |
| `flutter/android/app/src/main/res/mipmap-mdpi/ic_launcher_monochrome.png` | 新增；108×108，内容46×66 |
| `flutter/android/app/src/main/res/mipmap-hdpi/ic_launcher_monochrome.png` | 新增；162×162，内容70×99 |
| `flutter/android/app/src/main/res/mipmap-xhdpi/ic_launcher_monochrome.png` | 新增；216×216，内容93×132 |
| `flutter/android/app/src/main/res/mipmap-xxhdpi/ic_launcher_monochrome.png` | 新增；324×324，内容139×198 |
| `flutter/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher_monochrome.png` | 新增；432×432，内容186×264 |
| `flutter/android/app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | monochrome引用独立同源遮罩，彩色层不变 |
| `flutter/android/app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` | 同上 |
| `flutter/android/app/src/main/res/drawable-nodpi/ashell_floating_icon.png` | 新增；原图320×320 RGBA，保留完整设计 |
| `flutter/android/app/src/main/res/drawable/floating_window.xml` | 旧vector改为引用新PNG的bitmap，资源名不变 |
| `.gitignore` | 放行上述六个新增Android PNG的精确路径，保留T006两条例外 |

工程/记忆文档9份：本文件、`docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`、`PROJECT_START_HERE.md`、`.codex/PROJECT_MEMORY.md`、`.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、`.codex/CHANGELOG_AI.md`、`.codex/CHANGE_EVENT_LOG.md`、`.codex/TASK_HISTORY.md`。

素材与生成：沿用用户原图SHA-256 `88370d954e6a89be5e8373c7e02ebe322fb65f339a8d9416c22ab822ed9871d4`，原文件未变。System.Drawing双三次缩放生成普通PNG。系统单色层从原图黑色机器人提取alpha：中央ROI只隔离白底杂边，实际glyph边界为x342/y241/w567/h805，未裁去机器人；按亮度和原alpha提取后等比居中。最终10个mask的RGB统一255、保留缩放alpha，眼睛/头身间隙为透明区域；低密度间隙因亚像素抗锯齿保留部分alpha。无生成式重绘、依赖安装或产品codegen。

## T6. Verification

- V0通过：独立Pillow只读解码12PNG，RGBA/尺寸/alpha范围0—255；10mask RGB全255，内容边界符合上表，眼睛/间隙采样透明或抗锯齿部分透明；16份Android资源/Manifest XML解析通过，新的monochrome/floating引用存在且无同名冲突。静态图片预览保留原机器人设计。
- V0通过：两个include_bytes相对路径解析到同一PNG；Windows主程序/服务/托盘/MSI/快捷方式及legacy链人工静态复核；新PNG全部被Git识别。T006全部21图片及Flutter加载代码SHA未变，原图未变，通用Loading文字/转圈与HEAD一致。HEAD/分支/暂存区未变；diff空白/文件范围与文档链接检查通过。
- 检查过程：首次RGB断言发现System.Drawing预乘alpha转换的边缘白色有舍入误差，已只规范化RGB为255，逐像素保留alpha；小尺寸透明间隙校验按真实抗锯齿覆盖率检查，未用硬阈值修改图形。
- 外部API依据：[NWG Icon](https://raw.githubusercontent.com/gabdube/native-windows-gui/master/native-windows-gui/src/resources/icon.rs)、[Window](https://raw.githubusercontent.com/gabdube/native-windows-gui/master/native-windows-gui/src/controls/window.rs)、[resources_helper](https://raw.githubusercontent.com/gabdube/native-windows-gui/master/native-windows-gui/src/win32/resources_helper.rs)官方源码确认image-decoder及small图标行为；本地crate源码/portable lock不可用，上游master不等于正式解析版本，依赖解析和编译未执行。
- 《编译验证需求》（仅登记）：Windows正式环境仓库根build.cmd，检查主进程、服务进程、任务栏、托盘、卸载快捷方式、自解压窗口small/big；需要旧UI渠道时另行批准对应inline构建。Linux正式Android环境./build.sh 1及./build.sh 2，检查桌面/通知/主题/悬浮图及自定义override。全部NOT_RUN。
- 限制：没有生成新EXE/APK，没有实际观察Shell、任务管理器、外部多窗口插件、OEM Launcher或图标缓存。services.msc自带服务类型图标和OS RuntimeBroker不是产品资源。旧用户customSvg配置仍可覆盖默认悬浮球。通用功能图和Mac/Linux/web专有图不属本轮替换范围；`res/32x32.png`、`res/128x128@2x.png`用于其他平台bundle/Linux包，未修改，不能据此声称全仓图片已全部迁移。

## T7—T8. Documentation / Handoff

- 已同步：08_BUILD_SYSTEM、任务记录、PROJECT_START_HERE、PROJECT_MEMORY/STATE、CURRENT_WORK、CHANGELOG_AI、CHANGE_EVENT_LOG、TASK_HISTORY。T006历史范围和当时结果保留，由本任务补充纠正当前monochrome/notification/floating覆盖。
- ADR/Decision/依赖基线/External Registry/TEST_MATRIX：无架构、依赖或外部运行资产delta；仅本任务登记图标证据和待验证项。
- 原图与用户配置不改；原始素材来源、hash与设计保留要求沿用T006。
- Git写入、build/test/analyze、产品codegen、实际打包、安装/运行、删除/移动、版本/签名/发布：none。仅静态资源与引用核验，不作为编译或运行成功结论。
