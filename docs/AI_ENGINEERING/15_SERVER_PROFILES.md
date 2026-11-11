# 集中服务器配置 / Server Profiles

2026-11-12，T-2026-11-11-002；输入test/51292c2，基线[Baseline26](../BASELINE/26_DEVELOPER_SCREEN_PRIORITY_BASELINE_2026-11-07.md)。提交前已有三套地址更新；V0静态检查不能证明编译或服务联调通过。

## 1. 日常维护

地址只在[`libs/hbb_common/src/server_profiles.rs`](../../libs/hbb_common/src/server_profiles.rs)修改，然后重新编译对应客户端。这是编译进程序的源码配置，不是安装后读取的外部文本文件。

| 配置 | 用途 | 当前内容 |
|---|---|---|
| `ANDROID` | Android固定配置；iOS沿用移动端分支 | 已填写；当前Android与PC1地址相同 |
| `PC_1` | PC首次启动默认配置 | 已填写；当前Android与PC1地址相同 |
| `PC_2` | PC可选配置2 | 现有工作区编辑已填齐四项，独立于PC1；空配置仍禁用 |
| `SERVER_PUBLIC_KEY` | 三套配置共用的ID服务器公钥 | 保留现有公钥；不是服务器私钥 |

每套包含四个字段：

| 字段 | 填写要求 |
|---|---|
| `id_server` | ID服务器host:port，必须显式写端口 |
| `relay_server` | 中继服务器host:port，必须显式写端口 |
| `api_server` | HTTP(S) API基础地址，通常不包含客户端自动拼接的`/api/...` |
| `voice_token_url` | HTTP(S)语音Token签发接口，含实际完整路径 |

维护PC2时保留`id = "pc_2"`。四项全部有效后才可选择；空值、缺端口、非法协议或URL内用户名/密码会拒绝。建议维护时优先HTTPS；已有HTTP部署未在本任务擅自迁移。示例地址仅可用`*.example.invalid`占位，不能将占位当真实服务上线。

`voice_token_url`是签发服务地址，不是固定通话Token。动态Token仍由现有接口获取。原ZEGO流程保持：PC发起通话时从当前配置的签发接口取得双方Token，Android作为被叫接收邀请内的callee Token，不新增第二次独立签发请求。Token broker的既有鉴权合同和ZEGO App配置保持；共用ID公钥不代表两套独立broker自动共用账户或鉴权配置，B部署需匹配现有客户端合同。

Android只读取`ANDROID`，PC切换不会修改或通知Android的配置。B是独立服务器，需连接注册在B的Android；要制作B的Android包，应修改`ANDROID`后重新编译。修改PC1/PC2不会改变Android选择逻辑。

## 2. PC操作与生命周期

1. 点击主窗口左下角“就绪/连接中”等状态文字，选择服务器；原设置页的ID/中继入口也打开同一选择器。
2. 远控、文件、终端、语音等会话须先结束；账号/设备同步或登录进行中时拒绝切换。
3. Flutter暂停新API请求和新连接，关闭空闲子窗口；Rust再次检查会话和OIDC状态，通过本机IPC请求后台服务切换。
4. 后台校验完整配置、预期revision和活动连接，先持久化选中编号，再发布配置并重启rendezvous注册。地址本身仍从源码读取。
5. 收到服务确认后刷新账号和设备界面；切换后需在所选服务器重新登录。注册状态从真实后台响应取得，点击切换不会直接显示就绪。
6. 重启保留上次选择；首次启动或未知编号默认PC1。填写错误/未配置不能切换，服务不可达显示失败；确认超时只查询结果，不重复提交切换。迟到的配置同步由状态轮询刷新UI。

两套独立服务之间不迁移正在运行的会话。已确认切换但B离线时保留B选择并显示真实未就绪，可再选择配置1；不静默回退A。

## 3. Source Map / 合同

| 入口 | 责任 |
|---|---|
| `server_profiles.rs` / `config.rs` | 配置、平台选择、地址校验、`server-profile`/revision持久化与数据路径 |
| `src/server_profile.rs` / `src/ipc.rs` | 空闲切换、服务确认、配置同步、注册状态revision |
| `src/ui_interface.rs` / `src/flutter_ffi.rs` | 复用现有option FRB接口；启动先同步服务选择；缓存写入完成后返回 |
| `src/common.rs` / `src/client.rs` / `rendezvous_mediator.rs` | API/公钥/ID/relay读取及注册重启 |
| `src/client/helper.rs` | 当前配置的语音Token URL，现有broker鉴权保持 |
| `src/hbbs_http/sync.rs` / `account.rs` | 旧配置异步HTTP/OIDC结果失效 |
| `server_profile_switch.dart` / `server_profile_state.dart` | 选择器、切换互斥、迟到确认恢复、界面缓存刷新 |
| `http_service.dart` / account/peer models | 请求revision检查、写入计数、设备/账号状态刷新 |

原`.info`端点赋值已移入唯一配置文件，保留文件供`build.cmd`兼容读取。旧本地端点设置、安装包文件名配置、导入/二维码/深链服务器配置、`RENDEZVOUS_SERVER`/`API_SERVER`/`RS_PUB_KEY`构建覆盖不再改变有效端点。其他普通设置仍使用原配置系统。

配置1沿用原`peers`、Ab/Group、收藏和最近ID数据；配置2使用`peers_pc_2`、Ab/Group后缀及本地option前缀。账号token、用户信息、邮箱、策略时间戳按配置隔离；切换时清理目标登录信息。窗口尺寸/语言等通用偏好保持共用。进程密码缓存使用profile+peer ID作键，防止两个服务器出现同一设备ID时误用密码。局域网发现本身仍是原LAN功能，不代表设备在当前ID服务器已注册。

维持strict relay-only和现有远端认证。若ID服务器返回的relay与当前配置不同，通过已有`RequestRelay`重新协调双方；不能只修改PC的一端socket。接收端校验请求的relay与自己的集中配置相符，拒绝跨配置地址；PC和对应Android应填写一致的relay地址形式。中继不再回退到远端下发或ID端口推导值。PC连接取ID地址及兼容IPC helper只读取当前配置；旧安装包文件名、后台地址字符串和默认服务器数组不再参与选择。显式`id@server`必须匹配当前配置，历史`@public`对应平台原默认配置，不能在PC2下绕回PC1。在线状态查询使用通用host/port解析，支持配置中的IPv6写法。WebSocket保持原协议端口/路径换算，但从一次profile快照取得ID/relay/API，测试采用独立输入、不写真实配置。没有改protobuf字段、JNI、生成桥接、采集/ADB/开发者画面优先策略、产品版本或依赖。原IPC OnlineStatus保留，新状态消息仅在后台声明profile能力后发送；旧后台不支持切换，应使用同版本后台和GUI。

## 4. 验证与交付限制

测试用例见[TEST_MATRIX](../../TEST_MATRIX.md)的SCP-01—09。初次V0检查覆盖当时的地址保留、集中消费者、PC2留空、平台分支、缓存命名空间、revision过滤、无生成/协议/版本变更和diff。四项配置Rust单测及两项WebSocket转换单测已维护，均未执行。2026-11-12续接复查31/31 V0合同与diff通过；新增检查覆盖所有ID入口、接收端relay、IPv6、桌面/移动HTTP和缓存调度边界。提交前保留现有三套地址编辑，另核对十二项地址语法、公共Key及运行源码唯一来源；初次“地址保留/PC2留空”不再描述当前值。当前本机未发现cargo/rustc/flutter/dart；用户已授权Actions双端常规编译且不监控，回执见任务，未进行设备连接、API登录、语音或生产服务验证。

《编译验证需求》（以下是正式环境交接命令，本任务未执行）

| 命令 | 环境要求 / 执行目录 | 验证目标 |
|---|---|---|
| `cargo test --manifest-path libs/hbb_common/Cargo.toml server_profiles::tests` | 配套Rust及原生依赖的正式环境；仓库根目录 | 四项配置选择/完整性/命名空间/URL单测 |
| `cargo test --manifest-path libs/hbb_common/Cargo.toml websocket::tests` | 同上；仓库根目录 | 两项WebSocket测试，覆盖14个原端口、自定义端口、IPv4/IPv6、HTTP(S)及透传情形 |
| `build.cmd` | `PC-Build.md`规定的Windows环境；仓库根目录 | Rust、Flutter、DLL与PC产物完整编译 |
| `./build.sh 1` | 正式Linux Android工具链；仓库根目录 | Android Rust库、Flutter/Kotlin及APK构建 |

联调需维护者先填写真实B配置，并准备分别注册在A/B的Android和相同ID的隔离测试设备。验证A→B→A、重启记忆、忙状态拒绝、服务断线/IPC超时、旧响应丢弃、账号/收藏/密码隔离、远控/文件/终端/ADB/双向语音及原开发者模式。不得以构建HTTP204或V0替代这些运行证据。

恢复方式：空闲时切回PC1；若更改地址有误，恢复此文件对应字段并重新编译。PC HTTP切换门和请求超时只作用桌面端，移动端HTTP和缓存异步写入调度沿用原流程。空闲子窗口未关闭成功时拒绝切换。原配置1设备数据保留；本任务无Git回滚、提交、推送、部署或凭据轮换。
