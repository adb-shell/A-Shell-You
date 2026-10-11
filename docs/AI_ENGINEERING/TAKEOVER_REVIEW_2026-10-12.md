# A-Shell-You 项目接管认知 2026-10-12

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

本项目当前承载 AShell 远控产品，主链为 Flutter UI、Rust controller/endpoint 与 Android Kotlin/JNI；Windows 还有独立 capture/input/privacy/virtual-display 维护面。保留大量 RustDesk 实现，同时定制 Android 控制、relay 策略、账号准入、ADB 与 ZEGO。

Evidence date：2026-10-12（Asia/Tokyo）。Source：`test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890`。Baseline：`CS-BL-2026-10-12-6fb0aa6`。

本文保存本轮只读接管分析的工程结论，不保存聊天原文。`verified` 仅表示源码/版本记录直接支持；`inferred` 为静态推断；`external` 为本仓之外的依赖；运行结果均 `verification-required`。源码行号属于上述快照，后续代码变动时按函数名重新定位。

## 1. 项目概况与来源

- Rust crate/library `ashell`，版本 `5.2.1`，edition 2021，MSRV 1.75；Flutter package `flutter_hbb`，版本 `5.2.1+59`。证据：`Cargo.toml:1`、`flutter/pubspec.yaml:19`。
- Android applicationId `me.ashell`，可见名云计划；library `libashell.so`，Windows `ashell.dll`。证据：`flutter/android/app/build.gradle:69`、`flutter/lib/models/native_model.dart:129`。
- 本地 `main`、`test` 及对应 origin refs 同指上述 HEAD；无本地 tag，非 shallow，当前分支只有一次初始化提交。审查开始/结束均 clean；之后记忆文档落盘会产生未提交文档变更。
- 当前 origin 是 `adb-shell/A-Shell-You`；用户指定的历史源为 `历史源仓`，两者不要混称同一个 remote。
- 只读 GitHub API tree 与 `git ls-tree -r HEAD` 对比：本地 HEAD 和源仓 `cbce0ba36c5c` 的 975 个 blob 路径/内容哈希完全一致，无单边文件。范围是 Git tracked 内容，不包含 ignored/local artifacts，也不证明构建产物一致。
- 源仓可读历史 60 条；最早 `96a9c7cee2cd8e42dda4d8c868e4e609b15e40ae` 为 2026-04-13 无父提交整仓导入。准确 RustDesk fork 点、导入前历史和作者归属未知。
- 与 RustDesk 1.3.9/1.4.0 tree 分别有 581/615 个同路径同 blob 文件。数量不代表开发量或确切 fork 版本。`src/server/video_service.rs`、`src/server/service.rs`、`libs/enigo/src/win/win_impl.rs`、`src/hbbs_http/account.rs` 与 1.4.0 相同；`libs/hbb_common/src/fs.rs` 与 1.4.0 引用的 hbb_common commit 相同。

可追溯链接：源仓快照（提交 cbce0ba36c5c；原始链接见 a036f31 中本文件）、源仓初始导入（提交 96a9c7cee2cd8e42dda4d8c868e4e609b15e40ae；原始链接见 a036f31 中本文件）、[RustDesk 1.4.0](https://github.com/rustdesk/rustdesk/tree/1.4.0)、[hbb_common 对照](https://github.com/rustdesk/hbb_common/tree/6e556f7e1751a3a709cd5cca0df7268ba3cb1c48)。这些是当次观察，远端后续变化需重新读取；未 fetch/pull。

## 2. 架构与资源边界

| 层 | 职责与源码入口 |
|---|---|
| 启动/进程 | `src/main.rs` feature/target 分流；`src/core_main.rs:31` 处理 desktop server/service/tray/CM/install/elevation；Windows Flutter runner 先加载 Rust DLL |
| Flutter | `flutter/lib/main.dart:40` 分 mobile/desktop/multi-window；`models/model.dart:3295` 的 FFI 聚合 session models；Provider、GetX、StatefulWidget 混用 |
| Bridge | FRB：`src/flutter_ffi.rs` + generated Rust/Dart；另有手写 RGBA C FFI 和 Android `MethodChannel('mChannel')` |
| Controller | `src/client.rs` 的 Client/LoginConfigHandler；`client/io_loop.rs` 汇合 peer/UI/timer，发送输入、管理解码/文件/terminal/voice |
| Endpoint | `src/rendezvous_mediator.rs:59` 登记与接入；`src/server.rs:105` 服务注册；`server/connection.rs:400` 每连接认证、权限、分发、清理 |
| Shared/native | `libs/hbb_common/` 协议/config/crypto/fs；`libs/scrap/` capture/codec/JNI；`libs/enigo/` input；`libs/clipboard/` clipboard |
| Android | `oFtTiPzsqzBHGigp.kt` Activity；`XerQvgpGBzr8FDFr.kt` 权限 Activity；`DFm8Y8iMScvB2YDw.kt` MainService；`nZW99cdXQ0COhB2o.kt` Accessibility |
| 产品 API | Flutter user/ab/group models；Rust `src/hbbs_http/` OIDC/download/sync；record_upload dormant |
| Windows | `src/server/portable_service.rs` IPC/shared memory；`src/privacy_mode*`、`src/virtual_display_manager.rs`；driver/DLL 属独立资产 |

生命周期：`flutter.rs:1369 session_start_()` 首个 UI session 起线程；`ui_session_interface.rs:1862 io_loop()` 为 current-thread Tokio runtime，UI 经 unbounded Data channel，返回 StreamSink events。`Connection` 使用 tokio::select；`server/service.rs:294 GenericService::run()` 为订阅服务建线程，无订阅休眠、错误退避。每 display 解码线程、Flutter engine、Android HandlerThread/截图线程、Windows helper process 不共用一个生命周期。

资产分类：`libs/` 混合继承和定制代码，不能整体跳过；`src/ui/` 是 Sciter compatibility；plugin 要求 flutter+plugin_framework，默认不启用（`src/lib.rs:51`）；RustDesk IDD 未被当前 IDD_IMPL 选中。FRB generated 文件标记 1.80.1；protobuf 由 `libs/hbb_common/build.rs:1` 生成，`src/version.rs` 由 `build.rs:83` 生成。当前 `.gitmodules` 为空，hbb_common 是普通跟踪文件，不是本地 submodule。

## 3. 核心调用链

### 3.1 启动与状态

Windows：`runner/main.cpp:21 wWinMain → ashell.dll → flutter.rs:125 ashell_core_main_args → core_main → main.dart:40 → initEnv/native bridge/models`。Rust main 不等于 Flutter 产品唯一入口。

Android：`main.dart:178 runMobileApp → native_model.dart:294 同步配置目录 → server_model.dart:418 ensureCoreService → mChannel ensure_core_service → Activity/MainService → JNI context + Rust server → flutter_ffi.rs:1823 main_start_service → 清 stop-service + rendezvous restart`。

必须区分 core service/JNI、screen share、frame source、PC waiting。`server_model.dart:175` 的 status_num 是 rendezvous 注册状态；`_isReady` 可由无投屏的 core-only 分支置 true，不代表授权/首帧。当前 mobile 首页仅 Android 非 outgoing-only 增加 ServerPage/AdbPage，ConnectionPage 位于 if(false)（`mobile/pages/home_page.dart:58`）。保留 iOS/Linux/macOS/Web 文件不等于当前产品可用或正式支持。

### 3.2 ID、连接、认证、断开

1. `config.rs:979 Config::get_id()` 读取/生成/保存 ID；`rendezvous_mediator.rs:633/664` 在 key 未确认时 register_pk，否则 RegisterPeer；UUID mismatch 可换 ID 重登，循环退出 reset_online 后退避。
2. `client.rs:1790 LoginConfigHandler::initialize()` 固定 force_relay；`:228/284` 拒绝 direct IP/domain:port 并跳过 UDP/IPv6 candidate；`:573` 只走 relay，失败不回落 direct。
3. Endpoint 仍保留 punch/direct/NAT：`rendezvous_mediator.rs:543 handle_punch_hole`；`:716 direct_server` 受 OPTION_DIRECT_SERVER/stop-service 控制，并非无条件监听。`:694 get_relay_server` 有固定非空 endpoint 优先分支，具体地址不记录。
4. `client.rs:3274 handle_hash → LoginRequest`；`connection.rs:1908 validate_one_password/validate_password` 校验 password+salt+challenge；`:2057` 起按 File/Camera/Terminal/Tunnel 检查选项；`:1349 send_logon_response` 完成适用 2FA 后 authorized=true、PeerInfo 和订阅。
5. `connection.rs:2315` 常规消息 authorized 总门仍在。`common.rs:1788 verify_login()==true` 是另一 legacy/UI gate，不等于 endpoint 或 backend 鉴权关闭。
6. `client/io_loop.rs:247`、`connection.rs:914` 处理 EOF/错误/约30秒无数据；`connection.rs:974/3792` 清理 privacy、订阅、CM、clipboard、tunnel。`ui_session_interface.rs:1253 reconnect` 防重复 Connecting，关闭旧轮次后起新线程。
7. Android Flutter 重连：`model.dart:232` 单 timer、300ms 首试、2.5s 周期、60s 静默窗口；forceRelay=true。`:1193` waiting 只 normal refresh；`:3371` 密码优先 session/process peer cache、再 default-connect-password，不使用本机永久密码冒充远端密码。

### 3.3 采集、编码、传输、显示

`授权订阅 monitor → video_service::create_capturer/run → frame/YUV 或 texture → Encoder::encode_to_message → VideoFrame → subscribers/Connection.tx_video → stream → 每 display 解码线程 → Decoder → Flutter RGBA/texture → onEvent2UIRgba 清 waiting`。

锚点：`video_service.rs:195/466/1050`、`client/io_loop.rs:1332`、`client.rs:1527/2682`、`libs/scrap/src/common/codec.rs:624`、`flutter.rs:446/495`、`flutter/lib/models/model.dart:3634`。codec/硬件分支存在不证明当前构建可用；采集/编码错误可 fallback/SWITCH，解码队列溢出可 refresh，网络收包不等于 UI 显示。

Android normal：`start_screen_share → 权限 Activity → 真实授权结果 → MainService.startCapture → ImageReader RGBA → plane buffer → pkg2230 JNI → VIDEO_RAW.FrameRaw → scrap Android Capturer`。证据：MainService `:1240/1255/1281`，`pkg2230.rs:1784/224/235`，`libs/scrap/src/common/android.rs:37`。

- `restoreMediaProjection:1528` 默认不弹权限；legacy init_service/start_capture 不授权；Android 14+ 不复用 saved token；仅显式操作可允许新授权。
- projection callback/VirtualDisplay 创建失败进入 `handleProjectionStoppedKeepService:1429`；PC 断开、关闭共享不等于停止 core。非显式 onDestroy 保留 JNI context 并 guarded restart（MainService `:647`）。
- SKL 是 Accessibility node hierarchy 绘制；ignore 是 API30+ screenshot loop。证据：`nZW99cdXQ0COhB2o.kt:805/845/2449`、`EqljohYazB0qrhnj.kt:39/69`。辅助 buffer 经过 JNI direct-buffer 复制，不能误报为直接拿 heap buffer 地址。
- 侧按钮开/关共享有主动帧源切换；waiting/reconnect 不自动切 ignore。`forceVideoFrameRefresh:1216` 要求正常共享完整，不重绑 VirtualDisplay。

Windows：`libs/scrap/src/dxgi/mod.rs:74` DXGI 失败可 GDI fallback；`portable_service.rs:900` 在 helper 运行且主屏时用共享内存，否则普通 capturer；privacy 可改变采集器/显示拓扑。

### 3.4 输入与其他业务

输入：`overlay/InputModel → flutter_ffi.rs:1645 session_send_mouse → ui_session_interface.rs:1181 → client.rs:3001 → MouseEvent/KeyEvent/Pointer → connection.rs:2315 → desktop input_service / Android pkg2230 JNI → OS/Accessibility`。自定义 wheelblank/browser/analysis/back/start/stop/touchblock/devselector 复用 mouse mask/url；`message.proto:199` url 是 field5。Android gesture/text 路径位于 `nZW99cdXQ0COhB2o.kt:648/780/1310`。

| 功能 | 实现、结束/异常边界与证据 |
|---|---|
| 文件 | FileAction/FileResponse + TransferJob；分块/压缩/冲突/cancel；.download 后 rename/mtime，部分最终化错误被 .ok() 忽略。`connection.rs:2584`、`hbb_common/src/fs.rs:453/561/596` |
| 剪贴板 | OS callback→订阅消息，退出 unsubscribe；端点 clipboard/单向策略检查，CLIPRDR/Unix 文件剪贴板另有分支。`clipboard_service.rs:47`、`connection.rs:1798/2502` |
| Terminal | TerminalAction→TerminalServiceProxy→portable_pty→shell；registry 在内存，ID ts_<uuid>，支持 persistent 与已有 ID 重连；重连回放 TODO，队列满可丢输出；Close/Drop 清 shell/线程，移动端不支持。`terminal_service.rs:19/173/476/577/625/659/727`、`connection.rs:3076` |
| Port forward | 本地监听0.0.0.0端口，远端会话双向转发，EOF/错误结束；endpoint enable-tunnel 检查，目标 connect 位于后续密码校验前。`port_forward.rs:45/197`、`connection.rs:2095` |
| 原生声音 | 平台采样→Opus→AudioFrame→audio decoder；不是 ZEGO 双向媒体。`audio_service.rs:29/468`、`client.rs:2836` |
| ZEGO | Rust 请求 token/邀请/接受/关闭，Flutter SDK loginRoom/publish/play；失败/关闭 leave 清媒体/timer。`client/io_loop.rs:945/981`、`zego_voice_call_model.dart:265/297`、`model.dart:549` |
| Android ADB | AdbPage→mChannel→Manager/Runner/DNS→nativeLibraryDir/libadb.so process；pair/connect/serial/shell，含授予自身 WRITE_SECURE_SETTINGS 命令。`adb/AShellAdbRunner.kt:91/273/298/367`；不是 PC remote ADB 协议 |
| Windows privacy | connection-owned，exclude/Magnifier/virtual-display；IDD_IMPL=Amyuni，turn-on guard/turn-off 恢复 topology/registry。`privacy_mode.rs:53`、`virtual_display_manager.rs:8`、`win_virtual_display.rs:67/459/518` |
| Windows printing | adapter→PRN data→文件协议→controller Windows XPS；adapter 初始化失败不注册。`server.rs:137`、`printer_service.rs:147`、`platform/windows.cc:924` |

### 3.5 产品账号、API、配置

- `login.dart:520 → user_model.dart:347 login → /api/login → validateUser:297 → token/user_info → AB/group`。email 被用作到期时间/可选 UUID，admin 与空 email 有通过分支。它是客户端策略，不证明 backend ACL。
- `refreshCurrentUser:136` 请求 currentUser，401 重置账号/资源；`logOut:275` finally 清本地。OIDC 在 `hbbs_http/account.rs:145/166/207/309` 独立线程轮询，remember-me 保存同一 LocalConfig 键。
- `ab_model.dart:111` 新旧地址簿兼容，`group_model.dart:39` 拉资源，API 使用 Bearer/分页；服务器租户隔离/数据库 schema 未在主仓证明。
- `rendezvous_mediator.rs:66 → hbbs_http/sync.rs:38/86` 活跃 heartbeat：3s tick、空闲目标15s；上传 sysinfo/连接状态，响应可断连接或合并删除 options（`:236/261/313`）。该请求链传空 header，未发送产品 Bearer；后台其他鉴权未知。
- `record_upload.rs:19 ENABLE=false` 仅 getter，`video_service.rs:967` 有门控，当前 dormant。
- `.info` 由 `new-build.cmd:78/84` 及旧 build.cmd 读为构建环境；未找到运行时代码直接读文件。API source 优先级见 `common.rs:1046`：exe license→option→编译常量→rendezvous派生→默认。
- `config.rs:618/642` 文件名 APP_NAME+suffix，desktop ProjectDirs（含平台 patch），mobile APP_DIR。`native_model.dart:150/224/294` 传 documents 路径并同步 Android service；APP_HOME_DIR 是另一概念。
- LocalConfig 普通配置保存 token/user_info；AB/group 压缩+本地 symmetric_crypt 并按 token 绑定。`config.rs:1712/2113/2237`、`password_security.rs:183`。

## 4. 二次开发证据

| 已确认变更 | 源仓提交 | 耦合面 |
|---|---|---|
| Android 双通道、状态与无障碍检测 | `9eac836b86fd` | Kotlin services、Flutter overlay/models |
| Android 黑屏/点选与 JNI | `8bc280f34814` | MainService、Accessibility、Dev selector、active/compat JNI |
| strict relay、Android reconnect | `c6b409b5a843` | client/session、Flutter models、Android lifecycle、CM |
| ADB manager/runner/DNS/state 新增 | `747429dc71c3` | Android native process、MethodChannel、UI、packaging |
| ZEGO 媒体替换/邀请扩展 | `ec8d19ecb583`、`e769ea82bb5a` | Flutter SDK、proto、Rust/CM/session、Android voice |
| Windows 新环境入口 | `6ff35b3c54b3` | new-build.cmd 与 PC-Build.md |
| workflows 手动触发 | `56367a206cc7` | CI 验证/发布入口 |

当前 schema 还有 `Misc.ashell_status=39`、terminal messages/persistent option、ZEGO metadata（`message.proto:680/845/849/911/938/977`）。与参考标签不同不等于所有差异均由某位作者新增。品牌/SO/DLL/package 改名只计身份/打包变更；Terminal/privacy/port-forward 的完整原创归属仍不明。

## 5. 工程运行条件

- Android 正式入口 `build.sh 1/2`（Linux）；脚本/环境默认 Rust1.75、Flutter3.24.5、JDK17、NDK27.2.12479018；mode2 包含 arm64/armv7/x86_64。`env.sh:51`、`build.sh:111`。
- Gradle compile/target/min SDK 34/33/21；AGP7.3.1、Kotlin2.1.21、Gradle7.6.4。见 `flutter/android/app/build.gradle:54`、`settings.gradle:20`、wrapper properties。API21 声明不保证所有截图/ADB功能可用。
- Windows `new-build.cmd` 期望配置好的 DevEnv/DevTool，使用 Rust1.75 MSVC、Flutter、LLVM、vcpkg；调用 build.py portable/hwcodec/flutter/vram，输出 PC-Bulid。脚本会切工具链/解析依赖/打包，不是只读检查。
- Cargo/pub/Gradle/vcpkg/CocoaPods 多套依赖；FRB1.80.1、vcpkg baseline 位于 `vcpkg.json:86`。build.sh 包含安装/codegen/SDK patch/外仓 fetch-checkout/签名等副作用，未经单独授权不执行。
- 当前 jniLibs 目录不存在；pubspec 声明 ZEGO，但 lock 无其 entry；native ADB、WindowInjection/driver/printer adapter 来源、版本/hash/签名未闭环。
- `RuntimeBroker_ashell.exe` 由目标系统 RuntimeBroker 派生（`platform/windows.rs:1198`），不是缺少独立 helper 工程。XPS 调用由仓内 C++ 实现，依赖目标 Windows XpsPrint.dll。
- Android 权限相互独立：MediaProjection、Accessibility、RECORD_AUDIO、overlay、前台服务、wireless debugging；仓库声明不能代替真机批准状态。
- hbbs/hbbr、产品 API/DB 均 external；token broker 为 `scripts/deploy_zego_token_service.sh:193` 内嵌 Go 的 PARTIAL 资产，实际部署版本/治理未知。相邻 API/WEB/SERVER 工作区未纳入本审查。

## 6. 风险与认知缺口

以下记录源码事实和可能后果，不报告已利用漏洞或未执行的运行结果，不复制任何凭据值/生产地址/设备标识。

| 项目 | 源码事实 | 影响/验证边界 |
|---|---|---|
| 固定永久密码 | `config.rs:1060/1079` setter 不更新，getter 返回常量；`connection.rs:1933` 实际校验它；默认验证模式包含永久密码 | 并非无认证，但不是每设备可独立设置的永久秘密；未测试有效性 |
| 握手降级 | `client.rs:699/725` 缺失/无法验证 signed key 有继续或 non-secure fallback；`server.rs:171` 部分无效/空消息仍进 Connection | 存在未设置流加密仍继续路径，不等于所有会话明文；未抓包/攻击 |
| 双向 nonce | `hbb_common/src/tcp.rs:202/300` 共用 key、收发计数同初值、nonce 无方向标识 | 缺方向隔离；安全后果需专项验证 |
| Android input gate | `connection.rs:2321/2343/2386` 在 authorized 门内直接 JNI，无 desktop 对应 peer_keyboard_enabled | 已授权会话能力限制不一致，不是未认证任意控制 |
| 正常帧 ownership | MainService `:1255` Image.use 关闭后，`pkg2230.rs:224/247/1784` 仍以 pointer 延迟读/复制 | 锁不延长 Image buffer lifetime，ownership 缺口明确；UAF/崩溃未复现 |
| 自动接听 | `server_model.dart:796/884/900` 3s auto submit、cancel=submit；已有录音授权可直接 accept | 每次来电并非明确同意；后台采音/ROM行为未运行验证 |
| HTTP/control trust | 默认 API/token HTTP、token client 凭据型字面量；sync 请求无产品 Bearer，响应可配置/断连接 | 后台鉴权/TLS终结/凭据有效性未知；公开可见源码不能证明现网凭据仍有效 |
| 本地存储 | LocalConfig token；`password_security.rs:183` UUID 整形 key + zero nonce | 未见该链系统 secret vault，不能称强本地隔离 |
| 更新执行 | `downloader.rs:142/173` 下载链、`flutter_ffi.rs:2622` 更新执行未见强制 hash/signature/publisher gate | 取决于 URL/content 信任；未做替换攻击；plugin 另受 feature gate，不能算默认开放 |
| 产品 UI 准入 | `user_model.dart:370/395` 先更新后校验；`connection_page.dart:484` 不等异步刷新；`:391` 存开发旁路 | 可能残留/放行本地 UI 状态，不证明 endpoint/backend 绕过 |
| 生命周期 | `server_model.dart:212` 未持有 periodic timer；`model.dart:3344/3678` 创建/关闭未覆盖此 timer | 可能保留对象和轮询，未测量泄漏 |
| 平台可用性 | `home_page.dart:58/100` 在 iOS/outgoing-only 可为空列表却取元素 | 静态可达异常路径，未运行；不能仅凭平台目录宣称支持 |
| Windows 高权限 | DLL injection、display topology、driver/IPC/shared memory；存在恢复代码 | 外部 DLL 行为、UAC/多屏/崩溃恢复未验证 |

其他缺口：terminal 重连回放 TODO/输出丢弃；文件最终化部分错误忽略；HTTP bridge 以 URL 作结果槽，同 URL 并发错配为推断；ADB 进程 waitFor 再读 pipe 有背压疑点；JNI static mut 与多线程 buffer 状态需专项核查。以上均不触发本轮修复授权。

### 文档纠偏

1. 7月 Project State 的 main/77062b4/dirty 是历史快照；10月本地为 test/6fb0aa6，source tree 对应 cbce0ba。
2. 7月本机三 ABI libadb.so/hash 记录只能作 historical；10月当前 jniLibs 不存在。
3. `.info` 是 Windows build input；`_isReady` 不是 MediaProjection ready；FFI 内标 global 的模型仍需看实际 constructor。
4. `verify_login()==true` 不等于 endpoint auth 关闭；record_upload dormant；terminal 支持进程内 persistent。
5. 完整 ZEGO broker 缺失不等于没有任何后端源码；脚本内嵌实现存在。旧文档的“真实生产凭据事件”不能代替当前有效性证据。

## 7. 覆盖与验证边界

重点阅读 main/core_main/runner、Flutter main/native/model/server/input/user/ab/group/ZEGO、Android Activity/permission/MainService/Accessibility/helper/ADB、active/compat JNI、client/io_loop/rendezvous/server/connection/service/video/audio/clipboard/terminal/port_forward、proto/config/password/tcp/fs、HTTP/sync/token/download/update、Windows privacy/portable/virtual display、manifests/scripts/workflows。

未逐行覆盖所有 tracked 文件、所有 widget/Accessibility 自动化/CM IPC consumer、全部 native codecs/OS backends；未审计外部 SDK 内部、所有路径/symlink/ZIP 输入、完整供应链和所有密码学组合。相邻仓、实际用户配置/运行日志、设备/后台/数据库未访问。产品构建、测试、服务、依赖安装、凭据试用、部署及 Git 写入均未执行。

测试现状：Rust 55处 test/tokio::test 标记，不是55个通过测试；Flutter `test/cm_test.dart:19` 是手动 UI harness、pubspec flutter_test 被注释；未发现 Android src/test/androidTest；workflows 仅 workflow_dispatch。`TEST_MATRIX.md` 是要求，不是本轮运行记录。

日志/错误机制：`hbb_common/src/lib.rs:374` 有 debug/release logger、等级及轮转；401 reset、OIDC轮询、sync限速、download cancel、voice失败清理存在。部分 .ok()/allow_err! 弱化错误可见性；OIDC result/userInfo/完整URL/服务错误body进入日志的路径需注意（`account.rs:209`、`user_model.dart:221`、`helper.rs:112`、`downloader.rs:91/116`），本轮不采集日志。

《编译验证需求》仅记录缺口：匹配 Linux 环境、仓库根目录的 `./build.sh 1`/`./build.sh 2`；匹配 Windows 正式环境、仓库根目录的 `new-build.cmd`；目标是依赖/生成桥/产物一致性。上述脚本有安装、Git、签名等副作用，另行授权并确认精确阶段后才可执行。本轮没有 PASS 的产品运行证据。

## 8. 新会话恢复

从根 `AGENTS.md → PROJECT_START_HERE.md` 开始，按既有顺序读 `.codex/PROJECT_STATE.md`、`.codex/CURRENT_WORK.md`、历史和 `.codex/PROJECT_MEMORY.md`，再依任务进入本文和源码。先重查 branch/HEAD/status；若与本基线不同，本报告只是历史导航。不要恢复旧授权、自动修复风险或把文档落盘等同云端上传/Git提交。

关联：[当前状态](../../.codex/PROJECT_STATE.md)、[短记忆](../../.codex/PROJECT_MEMORY.md)、[基线](../BASELINE/05_OBSERVED_BASELINE_2026-10-12.md)、[外部资产](../../EXTERNAL_ASSET_REGISTRY.md)。
