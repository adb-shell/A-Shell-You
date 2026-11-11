# OpenWorld 网络与协议 / Network Protocol

2026-11-12 T002续接复查：ID获取仅用当前profile，移除文件名/旧后台地址和默认数组回退；显式server必须匹配配置。接收端RequestRelay校验本端relay，不接受跨配置目的地址；PC与对应Android需使用一致的relay地址形式。IPv6在线查询及WebSocket单次profile快照见[15_SERVER_PROFILES](15_SERVER_PROFILES.md)；V0，协议字段不变，兼容/运行待验证。

## 2026-11-12：PC服务器配置

ID/relay/公钥从集中配置取得；只在无活动会话时通过本机IPC切换并重新注册。维持strict relay-only；中继与返回值不同时用原`RequestRelay`协调双方，不仅替换PC socket。新增本机profile IPC，无protobuf修改；状态revision阻止旧服务器响应回显就绪。见[15_SERVER_PROFILES](15_SERVER_PROFILES.md)，V0，双端兼容/连接待联调。

## 2026-11-07 / T004：开发者画面优先

源码25bc2ce（test/51292c2内，现纳入main合并基线）新增`LoginRequest.developer_mode = 17`（bool）、`Features.developer_screen_priority = 3`（bool）、`Misc.screen_access = 42`（ScreenAccess：paused1/revision2/developer_mode3）。已有编号与类型不变。PC角色来自密码校验后的Rust进程标志；仅认证成功且有效登录的Android普通远控会话登记，先登记再订阅视频。其他会话类型及认证失败不参与。

Android存在至少一个开发者连接时，只给开发者分发普通VideoFrame、截图、ADB H.264/JPEG/layout。普通连接保留传输、心跳、状态和ZEGO信令；普通新建/重连也暂停。多开发者共享接收，最后开发者关闭/异常断链清理后恢复；退出清理幂等。屏幕暂停不重启服务或申请MediaProjection权限。

生产端与网络出队双重过滤，切换前排队帧按入队时刻废弃；暂停连接不参与视频ACK等待及QoS聚合，ADB清在途计数、恢复等待关键帧。控制入口拒绝普通连接的输入、截图、采集/隐私切换及ADB变更，仍处理状态/ACK/语音；无操作计时不会因策略暂停关闭连接。已经开始网络发送的包不能撤回，PC收到状态后遮住旧图并停止发布帧。

旧PC缺省普通，无法显示新暂停说明但仍受发送端过滤；旧Android忽略新字段且不具备策略，新PC明确提示更新。角色位是协作声明而非可信凭据，端点认证保持，修改版已授权客户端可伪造角色。详见[ADR-0028](../ADR/0028-developer-screen-priority.md)、[任务](../tasks/T-2026-11-07-004-developer-screen-priority.md)。V0，双端编译与运行未验。下方为历史沿革。

2026-11-07当前身份：开放世界 / OpenWorld / openworld / com.openworld.me。原功能合同保持，ADB及构建引用同步；实施起点为main/7361d39，已交付test；准确源码SHA、编译run与验收结果以本次迁移回执为准。见[本次迁移](OPENWORLD_IDENTITY_MIGRATION_2026-11-07.md)。下文旧日期记录为功能沿革，产品术语/路径按当前名称映射；冻结原文以对应Task/Baseline为准。

ADB当前合同：字段40请求/回执/instance与revision状态推送，apiVersion3；字段41独立媒体/触摸、mirrorVersion2模式化停止，H.264每PC最多24帧、独立JPEG/layout最多4帧，均限制8MiB在途。App-helper的LAYN/LAYP/LAYE节点帧及黑屏PNG属于本机私有流，不增加peer协议字段。原认证与传输策略保持，媒体发送同时受上方开发者策略约束。参数与`src/remote_adb_video.rs`、14_ADB_MIRROR对齐（2026-11-11 V0）。 当前详细合同：[本机ADB](12_LOCAL_ADB.md)、[远程ADB](13_REMOTE_ADB.md)、[媒体与触摸](14_ADB_MIRROR.md)。


接管基线：2026-07-12  
状态：`verified` + `verification-required`

2026-10-13 T004：追加 `Misc.remote_adb = 40`，JSON v1通过既有认证peer stream传输，不增加二次加密、不关闭原加密/relay-only。当前受控端校验authorized、普通会话及PC只读状态，ADB不依赖手机Accessibility键盘权限，独立JNI访问Android ADB manager；旧输入/terminal字段和处理保持。新旧端兼容、限额、owner取消和威胁模型见 [13_REMOTE_ADB.md](13_REMOTE_ADB.md)。仅V0，V2—V4待验证。

> 本文描述仓库内的端点协议实现。`hbbs`、`hbbr` 服务端源码不在本仓库，因此服务端部署、数据库和运营策略只能作为外部依赖记录，不能由本文推断。

2026-10-12 默认配置更新（T004 / V0）：`libs/hbb_common/src/config.rs` 的 `RENDEZVOUS_SERVERS`、`PUBLIC_RS_PUB_KEY` 已替换为用户指定值；`RENDEZVOUS_PORT / RELAY_PORT / WS_RENDEZVOUS_PORT / WS_RELAY_PORT` 为 `10056 / 10057 / 10058 / 10059`。`src/rendezvous_mediator.rs` 与 `flutter/lib/mobile/pages/server_page.dart` 的显式 relay 地址同步。`src/lan.rs` 的 `RENDEZVOUS_PORT + 3` 随之变为10059，其他端口派生表达式未改。未发现10055对应的旧端口字面量，不据此推定服务用途。

`RS_PUB_KEY` 编译环境覆盖、`Config::get_rendezvous_servers()` 的本地/构建配置优先级保持原样；本轮没有迁移已安装客户端设置，也没有验证服务端监听、Key匹配或连接行为。完整范围见 [T004](../tasks/T-2026-10-12-004-server-configuration.md)。

## 1. 网络角色与边界

OpenWorld 同一二进制同时包含两类角色：

- 控制端 client：发起远程桌面、文件、终端、端口转发、语音邀请等会话。
- 受控端 server：注册 ID、接受登录、采集屏幕、注入输入并提供能力服务。

外部基础设施包括：

- `hbbs`：ID 注册、rendezvous、地址协商。
- `hbbr`：relay 转发。
- 产品 HTTP API：账号、设备、地址簿、分组和同步。
- ZEGO Token service 与 ZEGO RTC：语音媒体链。

仓库只保存端点实现、协议定义和外部地址配置，不保存这些服务的完整实现。

## 2. 协议层次

```text
Flutter / legacy UI
  -> Rust session interface
  -> client.rs / server/connection.rs
  -> hbb_common protobuf messages
  -> encrypted peer stream or relay stream
  -> remote endpoint
```

主要锚点：

| 层 | 源码 |
|---|---|
| rendezvous 协议 | `libs/hbb_common/protos/rendezvous.proto` |
| 会话协议 | `libs/hbb_common/protos/message.proto` |
| 控制端连接 | `src/client.rs`, `src/client/io_loop.rs` |
| 受控端入口 | `src/server.rs`, `src/server/connection.rs` |
| rendezvous | `src/rendezvous_mediator.rs` |
| relay | `src/client.rs::{request_relay, create_relay}`, `src/rendezvous_mediator.rs` |
| socket/加密封装 | `libs/hbb_common/src/tcp.rs`, `libs/hbb_common/src/udp.rs` |
| UI bridge | `src/flutter_ffi.rs`, `src/ui_session_interface.rs` |

`message.proto::Message` 是会话消息总 union，包含登录、视频、音频、输入、剪贴板、文件、隧道、终端、权限、语音呼叫和自定义状态等类别。协议新增字段必须保持 protobuf 向后兼容：不得复用已发布 field number，不得只改单端。

## 3. Rendezvous、NAT 与 Relay

### 3.1 控制端当前策略

`src/client.rs::LoginConfigHandler::initialize(...)` 当前把 `force_relay` 固定为 true。其结果是 OpenWorld 控制端会话：

- 不启动 UDP punch、IPv6 punch 或直接连接候选。
- 显式 IP/domain:port 的直接连接入口被拒绝。
- Android reconnect 也通过 `sessionReconnect(..., forceRelay: true)` 保持 relay-only。

这是一条产品策略，不等于仓库已经删除所有 direct/NAT 代码。

### 3.2 受控端仍保留的能力

`src/rendezvous_mediator.rs` 仍会：

- 启动 rendezvous mediator 和 direct server。
- 维护 LAN/NAT 相关路径。
- 接收 `PunchHole`、`FetchLocalAddr` 等消息。
- 在 `handle_punch_hole(...)` 中根据 `ph.force_relay`、代理和 WebSocket 状态决定 relay。

因此准确结论是：

> 当前 OpenWorld 控制端强制 relay；受控端和兼容协议仍包含 direct/NAT 能力。

未来若要做到全局 relay-only，必须单独审计受控端、LAN discovery、direct server、NAT/STUN 探测和兼容客户端影响，不能只依赖控制端常量。

## 4. 会话建立与登录

典型链路：

```text
controller resolves peer ID through hbbs
  -> obtains relay/rendezvous instructions
  -> connects through hbbr
  -> secure_connection handshake
  -> LoginRequest
  -> controlled endpoint authentication/authorization
  -> PeerInfo + capability negotiation
  -> service messages
```

受控端认证仍由 `src/server/connection.rs` 执行，包含密码、点击确认、2FA、trusted devices 等分支。`src/common.rs::verify_login()` 当前近似直接返回 true，但它属于 legacy/custom UI gate，不能据此推断远程端点认证已关闭。

控制端 Android 自动重连的远端密码来源顺序是：当前进程会话缓存，其次构建内置的默认连接密码；禁止把本机 permanent password 当作远端密码。

## 5. 能力与权限消息

登录完成后，协议承载以下主要服务：

- 视频、光标、显示器和画质控制。
- 鼠标、触摸、键盘和自定义 Android command mask。
- 剪贴板与 file copy/paste。
- 文件传输与目录操作。
- Terminal、TCP tunnel、port forwarding。
- 聊天、消息框、截图、录屏、远程打印。
- privacy mode、block input、摄像头。
- ZEGO voice invitation state；媒体不走该 peer stream。

受控端必须在服务入口处执行权限检查，UI 是否显示按钮不能作为授权边界。已确认的审计缺口：Android Mouse/Touch/Key 和部分自定义 command 在授权连接后未与 desktop 分支一样统一检查 `peer_keyboard_enabled()`。该行为需要产品安全决策和正式环境验证。

## 6. 视频与输入在协议中的位置

### 视频

```text
platform capturer
  -> video_service encoder
  -> VideoFrame/codec messages
  -> relay stream
  -> controller decoder
  -> Flutter texture or RGBA
```

首帧等待属于控制端 UI 状态，不属于 rendezvous 在线状态。刷新请求只能唤醒已有正常视频链，不得隐式改变 Android frame source 或请求新的 MediaProjection token。

### 输入

```text
Flutter InputModel
  -> session_send_mouse/key
  -> MouseEvent/KeyEvent/TouchEvent
  -> server/connection.rs
  -> platform input service or Android JNI
```

OpenWorld 在 `MouseEvent.url` 上复用了若干 Android command 字符串。修改这些命令必须同时检查 Dart、Rust FFI、client、protobuf usage、server、JNI 与 Kotlin，且需要验证旧端点收到未知命令时的行为。

## 7. 文件、终端与隧道

- 文件传输：`src/client/io_loop.rs`、`src/server/connection.rs`、`libs/hbb_common/src/fs.rs` 与 Flutter `FileModel` 协作。
- Terminal：`src/server/terminal_service.rs` 创建 `ts_<uuid>` 临时会话；旧 `terminal.md` 的持久化描述不是当前实现。
- Tunnel/port forwarding：使用会话协议协商后转发字节流。
- Remote printer：Windows 专属能力，需显式 option 和平台依赖。

文件路径、符号链接、覆盖、权限和取消语义属于跨平台安全边界；未来改动必须包含恶意 peer 输入测试设计。

## 8. ZEGO 语音控制面

RustDesk peer protocol 只传：

- `VoiceCallRequest`
- `VoiceCallResponse`
- 取消、忙状态和邀请生命周期

Token 由外部 HTTP service 获取，音频经 ZEGO SDK 传输。原 RustDesk `audio_service` 不承载当前 ZEGO 媒体。控制通道授权与 ZEGO room/token 授权是两个独立边界，两者都必须成立。

当前明文 token transport、客户端内置 credential 和 Android 自动接受语义均已列入 `10_SECURITY_MODEL.md`，不能把“连接已授权”等价为“用户已同意打开麦克风”。

## 9. 加密与完整性风险

已确认需要专项审计的实现：

1. `src/client.rs::secure_connection(...)` 对缺失、无效或不匹配 signed key 的部分路径可能回退到非 secure 连接，呈 fail-open 风险。
2. `libs/hbb_common/src/tcp.rs` 的 secretbox 双向使用相同 key，方向计数器/nonce 初值相同，可能形成跨方向 nonce reuse。
3. 本地 password protection 使用可读取设备 UUID 派生材料，并在多项数据上使用固定 nonce，安全性更接近混淆而非强 at-rest protection。
4. 部分产品 API、sync 和 ZEGO token 地址使用明文 HTTP。

这些结论来自静态源码审计，尚未通过抓包、互操作或攻击复现。修复前必须设计协议版本、兼容窗口和降级拒绝策略。

## 10. 兼容性原则

协议改动必须满足：

- protobuf field number 只增不改，不复用。
- 新旧 controller/endpoint 至少定义一个明确兼容矩阵。
- 未知 option/command 必须安全忽略或明确拒绝。
- 鉴权和加密失败默认 fail-closed；若保留兼容降级，必须由显式配置开启并告警。
- relay-only 作为产品决策写入测试，不依赖 UI 参数。
- 平台权限在受控端强制，不能只在控制端隐藏入口。
- 日志不得记录密码、access token、ZEGO credential、完整剪贴板或文件内容。

## 11. 可观测性

最小诊断字段应包含：

- session correlation ID，不使用 credential 作关联键。
- controller/endpoint version 与 platform。
- rendezvous、relay、secure handshake、login、capability 各阶段结果。
- relay server 选择与重连次数；地址需脱敏。
- 视频首帧时间、frame source、codec、丢帧/解码错误。
- 权限拒绝的服务类型。

日志等级必须区分正常 relay-only、暂时断线、认证失败、协议不兼容和服务权限拒绝，避免把所有失败归类为“连接失败”。

## 12. 待正式验证

- OpenWorld controller 与当前生产 hbbs/hbbr 的完整 handshake/relay 兼容矩阵。
- 受控端是否仍可被非 OpenWorld 兼容客户端请求 direct/punch hole。
- signed key 异常时的真实 wire behavior。
- secretbox 双向 nonce 是否会在同一 key 下重复。
- Android input permission 关闭后的 server enforcement。
- 文件、terminal、tunnel 在恶意路径和中断条件下的边界。

所需命令、环境和验收目标统一记录在 `09_DEBUG_SYSTEM.md` 的《编译验证需求》中。
