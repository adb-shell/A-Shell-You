# AShell 模块设计 / Module Design

当前实现基线0dbe202：连接、2路Shell、媒体、遮罩和输入资源分别管理；instance/revision按真实状态变化推送。键盘与触摸共用control-only单writer并各自排队，显式粘贴与被动剪贴板同步区分paste标志。PC帧桥为独立二进制C ABI，不以base64装入状态JSON。 2026-10-19文档复核，仅V0，构建/ROM实测待证；[Baseline22](../BASELINE/22_RESTORED_SOURCE_BASELINE_2026-10-19.md)。详细合同：[本机ADB](12_LOCAL_ADB.md)、[远程ADB](13_REMOTE_ADB.md)、[媒体与输入](14_ADB_MIRROR.md)。


历史接管基线：2026-07-12，`77062b4`；当前实现见本页顶部

## 1. 启动与进程模型

`src/main.rs` 按 target/feature 分流；desktop 非 Flutter 走 `core_main()` + Sciter，Flutter 构建主要加载 `ashell` library。`src/core_main.rs` 继续处理 install、tray、server、CM、elevation、quick support 和 portable 参数。`flutter/lib/main.dart` 再按 window argument 启动 main/remote/file/terminal/port-forward/install/mobile。

设计含义：一次“启动问题”可能跨 Rust process args、native runner、Flutter engine 和 multi-window。不能只看 `main.dart`。

## 2. Connection 与 Service registry

`Server::new()` 注册 display/audio/clipboard/input 等服务；每个 `Connection` 负责：

1. stream handshake。
2. login request、password/hash、approve/2FA/trusted device。
3. permission 与 service subscription。
4. `Message`/`Misc` 分发。
5. cleanup、connection manager 与平台状态回收。

`src/server/connection.rs` 已接近 5k 行，是远控协议 God object。任何修改必须按消息类别确认 sender、receiver、权限、cleanup 和兼容版本。

## 3. Controller session

`LoginConfigHandler` 保存 ID、conn type、session options、password、codec、relay/direct 信息；`Client` 完成连接和 secure handshake；`client/io_loop.rs` 持有运行时状态机；`FlutterSession` 对 UI 暴露 event/texture。

当前 controller 强制 relay。这个 product decision 不等于 controlled endpoint 删除 direct code。

## 4. 视频模块

```text
display_service/video_service
→ scrap::Capturer
→ platform frame / Android FrameRaw
→ codec negotiation + QoS
→ VideoFrame
→ client decoder
→ Flutter texture/RGBA
```

能力：多 codec、hardware codec feature、display switch、quality/fps、ack、recording、camera、screenshot。

债务：capture/codec/renderer 状态分散；Android raw lifetime 需独立验证；`FrameRaw.force_next`、`VIDEO_RAW`、`PIXEL_SIZE*` 是脆弱全局不变量。

## 5. 输入模块

Controller 由 `input_model.dart`、`flutter_ffi.rs`、`client::send_mouse` 编码；endpoint 在 `Connection::on_message()` 分发到 `input_service`；desktop 通过 enigo/portable service，Android 通过 JNI → Accessibility。

AShell 自定义 Android 命令复用 mouse mask/url 通道，包括 blank、browser、analysis、back、share、touch-block、Dev selector。它们是协议命令，不是纯 UI 操作。

安全边界：UI 密码/按钮可见性不是协议授权；endpoint 必须独立检查 session permission。当前部分 Mouse/Touch/Key 和自定义 mask 未形成完整 server-side gate，列为 P0/P1。

## 6. Clipboard 与文件模块

- 文本/多格式 clipboard：`Clipboard`, `MultiClipboards`。
- Windows file clipboard：CLIPRDR + `libs/clipboard`。
- 文件传输：`FileAction`/`FileResponse` + `hbb_common::fs::TransferJob`。

已有 block/digest/zstd、cancel、conflict confirm、`.download` 完成切换和 mtime。受控端文件访问没有独立 session root sandbox，边界依赖远控认证与 file permission。

## 7. Terminal 与 port forward

- Terminal protocol：`OpenTerminal`, `TerminalData`, `ResizeTerminal`, `CloseTerminal`。
- Server：`src/server/terminal_service.rs`。
- Flutter：`terminal_model.dart`, `terminal_*` pages。
- 当前 service ID：`ts_<uuid>`。

`terminal.md` 中 `tmp_`/`persist_` 属历史设计，不能解释当前生命周期。Port forward 由 `src/port_forward.rs` 和 session interface 管理。

## 8. Android runtime 模块

- `MainService`：core/JNI、foreground keep-alive、normal capture、MethodChannel relay。
- `AccessibilityService`：input、overlay、screenshot/ignore、DevAutoSelectorController。
- Rust JNI：raw buffers、pixel gates、command dispatch。
- Flutter：permission UI、session waiting、status/reconnect。

这不是单 Kotlin 模块，而是三语言状态机。详见 `04_ANDROID_PIPELINE.md`。

## 9. Android local ADB — 独立新模块

当前LocalAdbPage→LocalAdbBridge→me.ashell/local_adb/v1→LocalAdbPlugin→LocalAdbManager。T004的PC专用协议/独立JNI也进入该manager；它拥有进程共享状态和有限串行worker，由LocalAdbClient执行本机配对/连接。AdbIdentity管身份，AdbLocalSocket管loopback transport，AdbShell管shell-v2。见 [12_LOCAL_ADB.md](12_LOCAL_ADB.md) / [13_REMOTE_ADB.md](13_REMOTE_ADB.md)。

T009 / ADR-0015旧runner/bridge/mDNS/Accessibility自动化及libadb.so保持退役。新NSD发现本机配对和连接两类端口，媒体另走字段41；remote terminal等原链保持。ADR-0017记录本机集成，ADR-0018扩展多PC请求和共享状态，ADR-0019/0020记录独立权限、发现和显式刷新边界。

aca7446追加PC `RemoteAdbSidebar`，三个固定导航命令仍走`RemoteAdbController.run('shell')`。`RemoteAdbSessionState`归RemotePage所有，不归自动隐藏的toolbar或短期dialog所有；保存会话可用性/可见性、epoch及Android状态快照，不存配对码、私钥或命令输出。不可用→可用自动显示，手动关闭在持续可用期间保持；无周期status。导航使用已确认状态，陈旧时才查询，不回退原Accessibility输入链。命令走LocalAdbCommands，与连接管理worker分离。具体生命周期与条件见13_REMOTE_ADB。

## 10. Windows privacy/virtual display

Privacy abstraction 支持 exclude/topmost、Magnifier、virtual display。当前 virtual display 选择 Amyuni，最多 monitor 数和 plug/unplug 通过 driver/IOCTL 管理。注入路径使用 helper process、remote memory/APC 和 low-level hooks。

它是高权限平台主模块，不是实验代码。任何失败都必须定义恢复显示、恢复输入、清 driver state 的回滚路径。

## 11. Product account/API 模块

两条登录路径：

- Flutter normal login/current user/expiry/UUID：`user_model.dart` 直接访问 `/api/*`。
- Rust OIDC：`hbbs_http/account.rs` device auth/query。

地址簿/设备组由 Flutter models 直接调用 API；sync/heartbeat 在 Rust endpoint；backend/database 均在仓外。

`src/common.rs::verify_login()` 的 unconditional `true` 不等于 endpoint password authentication 被删除，但说明 legacy/custom-client 产品准入不能作为安全边界。

### PC开发者免登录入口（2026-10-25 / T001）

`flutter/lib/desktop/pages/connection_page.dart` 的 Ctrl+Shift+H 仅在desktop main处理KeyDownEvent，先显示开发者密码框。`developer_login_bypass_model.dart::enableDeveloperLoginBypass(String password)` 精确校验UTF-8输入摘要，匹配 `_developerLoginPasswordHashes` 集合中任一摘要才设置内存开关；T001-r3保留原密码并新增第二个可接受密码。确认按钮、Enter/小键盘Enter共用提交逻辑，空值/错误输入保留弹窗并提示。

输入隐藏且禁用纠错/建议/个性化学习，不trim、不保存或发送。重复快捷键不叠窗；取消/Esc不启用；页面dispose只按自身tag关闭弹窗，关闭后丢弃输入引用。已解锁不再次弹窗，进程重启恢复默认false。主连接按钮和peer card继续消费原开关；不改工具栏另一个开发者选项、产品API或endpoint认证。V0，运行验收见TEST_MATRIX FLT-08与[任务](../tasks/T-2026-10-25-001-developer-login-password.md)。

## 12. ZEGO voice 模块

- Rust：创建 metadata、邀请、pending/active timestamp、accept/close/timeout。
- Flutter：engine lifecycle、room login、publish/play、first-audio-frame diagnostics。
- Android：incoming state → foreground/full-screen UI → 3 秒自动 accept → microphone permission → join。

旧 `audio_service` 不应被 ZEGO 重新启用。当前自动接听/无 reject UX、HTTP Token 与客户端 key 是安全/隐私风险，不是普通 UI 细节。

## 13. Config、crypto 与 local persistence

`hbb_common::config` 使用 confy/config files 保存 ID、options、password/trusted devices；`password_security` 以设备 UUID 派生 secretbox key。nonce 固定和 key 可预测性意味着它更接近本机混淆，不应作为抵御本地攻击者的强机密存储。

Flutter API token、cache 和 model state 经 native local options/JSON cache 保存；仓库没有统一 secrets abstraction。

## 14. Plugin、CLI 与 legacy UI

- Plugin：feature-gated，不能默认认定发布包启用。
- CLI：静态接口漂移，待 `cargo check --features cli`。
- Sciter：保留启动/兼容逻辑，但新产品 UI 主路径是 Flutter。
- Web bridge：多处 TODO；不是 desktop/mobile 能力的等价实现。

## 15. 模块维护准则

1. 先标明 active/compat/dormant/external。
2. 为每个状态定义 owner、创建、转移、cleanup。
3. 跨层命令必须画 sender→protocol→receiver→platform 全链。
4. UI 可见性永不替代 endpoint authorization。
5. raw pointer、JNI、WinAPI、driver、injection 需要单独 safety review。
6. 新增 API 必须记录 auth、transport、timeout、retry、idempotency、redaction。
7. 任何恢复路径不得借“重启服务”掩盖状态机错误。
