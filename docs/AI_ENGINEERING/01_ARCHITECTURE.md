# OpenWorld 完整架构 / Architecture

当前整合输入为main/5d2d2e9与test/a8abdd4，见[Baseline28](../BASELINE/28_UNIFIED_CONFIG_MAIN_BASELINE_2026-11-12.md)及[合并任务](../tasks/T-2026-11-12-002-unified-config-main-merge.md)。保留Rust/portable 2.0.26、Flutter 2.0.26+59与OpenWorld产品身份，纳入服务器配置、PC切换及编译/CI修复。本轮只合并推送；历史编排受理及离线检查不能证明合并后编译或运行通过。

## 2026-11-12：Server Profiles

`hbb_common::server_profiles`提供编译期Android/PC1/PC2端点及共用公钥；桌面经服务IPC持久化profile+revision后重启注册，按配置隔离账号/peer数据。Flutter复用option桥接并过滤旧异步结果；Android固定配置、不参与PC选择。完整当前合同见[15_SERVER_PROFILES](15_SERVER_PROFILES.md)，后续旧端点优先级描述由该合同取代。

2026-11-07 T004当前：在test/d9f1119工作区增加开发者画面优先。PC本地模式通过进程native标志进入LoginRequest；Android在原端点认证后统一裁定各普通远控会话的画面资格。暂停媒体分发而保留会话与ZEGO，最后开发者离开恢复。普通VideoFrame和独立ADB媒体共用策略，不合并两条采集链。角色声明不是抗伪造授权，原认证不变。见[ADR-0028](../ADR/0028-developer-screen-priority.md)、[协议](06_NETWORK_PROTOCOL.md)；V0，正式编译/运行待证。以下密码任务描述仅代表当时快照。

PC本地密码校验经手写native C ABI设置进程模式，供desktop各engine连接/重连写入LoginRequest。Android在原端点认证后统一裁定各普通远控会话的画面资格；有开发者时暂停普通画面与控制，保留会话和ZEGO，最后开发者离开恢复。普通VideoFrame和独立ADB媒体共用连接策略，两条采集链仍独立。角色声明不是抗伪造授权；详见[ADR-0028](../ADR/0028-developer-screen-priority.md)、[协议](06_NETWORK_PROTOCOL.md)。

ADB由Android持有身份和设备状态，命令/媒体/黑屏/输入资源独立；视频复用scrcpy ScreenCapture，经独立GPU/H.264编码，截图优先常驻UiAutomation，节点由Shell读取/App绘制。普通采集与接收独立，QoS参数只读共享。详细合同：[本机ADB](12_LOCAL_ADB.md)、[远程ADB](13_REMOTE_ADB.md)、[媒体与输入](14_ADB_MIRROR.md)。

历史接管基线：2026-07-12，`77062b4`；当前实现见本页顶部

## 1. 运行体与信任边界

```mermaid
flowchart LR
    PCUI["PC Flutter / Sciter UI"] --> PCBridge["Rust FFI + Client Session"]
    PCBridge --> HBBS["External hbbs / ID rendezvous"]
    PCBridge --> HBBR["External hbbr / relay"]
    HBBR --> Endpoint["Controlled Rust Connection"]
    HBBS --> Endpoint
    Endpoint --> Services["Video / Input / Clipboard / File / Terminal"]
    Services --> AndroidJNI["Android Rust JNI"]
    AndroidJNI --> AndroidKotlin["MainService + AccessibilityService"]
    PCUI --> ProductAPI["External Product API"]
    Endpoint --> ProductAPI
    PCBridge --> ZegoToken["External ZEGO Token service"]
    PCUI --> ZegoRTC["ZEGO SDK media"]
    AndroidKotlin --> ZegoRTC
    LocalAdbUI["Android LocalAdbPage"] --> LocalAdbPlugin["独立 localadb MethodChannel / Java"]
    LocalAdbPlugin --> LocalAdbd["本机 adbd / loopback"]
    RemoteAdbUI["PC RemoteAdbMenu / Shell栏"] --> AdbMessage["Misc.remote_adb / authenticated session"]
    AdbMessage --> AdbJNI["isolated RemoteAdbNative JNI"]
    AdbJNI --> LocalAdbPlugin
```

必须分开的信任域：

- 控制端（`controller`）：发起连接、解码画面、产生输入和文件操作。
- 受控端（`controlled endpoint`）：认证会话并执行高权限本地操作。
- rendezvous/relay：仓外基础设施；只在协议客户端中有定义。
- 产品 API：账号、地址簿、设备组、策略和 sync；后端不在仓库。
- ZEGO：Token service 与 ZEGO RTC 是两个外部域。
- Android OS：`MediaProjection`、Accessibility、overlay、前台服务分别由系统授权。
- Windows OS：driver、service、injection、privacy window 和 input hook 是本机高权限边界。

## 2. 分层架构

| 层 | 主要目录 | 职责 |
|---|---|---|
| Product UI | `flutter/lib/`, `src/ui/` | 桌面/移动/web UI、多窗口、遗留 Sciter |
| State/Bridge | `flutter/lib/models/`, `src/flutter*.rs`, `src/ui_*_interface.rs` | session/model、事件流、FFI、texture/RGBA |
| Controller | `src/client.rs`, `src/client/` | rendezvous、relay、登录、解码、文件/终端/输入发送 |
| Controlled endpoint | `src/server.rs`, `src/server/` | 认证、服务订阅、输入/文件/终端/视频执行 |
| Shared protocol | `libs/hbb_common/` | protobuf、config、socket/stream、crypto、fs |
| Capture/Input | `libs/scrap/`, `libs/enigo/`, `libs/clipboard/` | 采集、编码辅助、输入、剪贴板 |
| Native platform | `src/platform/`, `src/privacy_mode*`, Android Kotlin | OS service、driver、JNI、MediaProjection、Accessibility |
| Product integration | `src/hbbs_http/`, Flutter API models | OIDC、login、address book、group、sync、download |
| Packaging | `build*`, `env.sh`, `flutter/*`, `res/`, `.github/workflows/` | 生成、编译、签名、portable、平台发布 |

## 3. 控制面与数据面

### 3.1 控制面

- rendezvous registration、ID/PK、relay request：`rendezvous.proto`。
- session login、permission、display switch、privacy、terminal、ZEGO invitation：`message.proto`。
- Flutter/Android 状态同步：Rust event stream、MethodChannel、`openworld_status`。
- 产品策略：`hbbs_http::sync` heartbeat/config/disconnect。

### 3.2 数据面

- 视频：capture → codec → `VideoFrame` → relay stream → decoder → texture/RGBA。
- 音频：普通远控音频仍走 RustDesk `AudioFrame`；当前 1v1 语音走 ZEGO SDK。
- 输入：Flutter → Rust message → endpoint `input_service` → OS/Accessibility。
- 文件：`FileAction`/`FileResponse` + `TransferJob` block/digest/compression。
- 终端：`TerminalAction`/`TerminalResponse` + PTY service。
- Android raw frame：Kotlin direct buffer → JNI `FrameRaw` → Rust video service。

## 4. 会话架构

### 4.1 Controller

`LoginConfigHandler` 持有 peer、connection type、password/options、codec 和 relay 状态；`Client` 完成 rendezvous 与 stream 建立；`client/io_loop.rs` 是消息主循环；`FlutterSession` 将事件送到 Dart。

OpenWorld controller 固定 `force_relay = true`，拒绝显式 direct address，并跳过 direct candidate。这个决定只证明 OpenWorld controller 的默认路径；`rendezvous_mediator.rs::handle_punch_hole()` 在 controlled endpoint 仍可响应未设置 `force_relay` 的兼容请求。

### 4.2 Controlled endpoint

`RendezvousMediator::start_all()` 建立本机 server、注册 ID/PK、启动 sync，并保留 direct/LAN/NAT 继承逻辑。`Server` 注册服务；每个 `Connection` 完成登录、权限和消息分发。

远控会话认证与 `src/common.rs::verify_login()` 不同。后者当前直接返回 `true`，主要属于 legacy/custom-client UI 校验；受控端实际认证仍在 `src/server/connection.rs` 与 `hbb_common::password_security`。

## 5. UI 与状态管理

Flutter 使用混合状态方案：

- `provider` 承担主 session/model 注入。
- GetX `.obs`/`Rx*` 用于地址簿、设备组和局部响应式状态。
- 大量全局 singleton/registry 位于 `common.dart`、`model.dart`、`server_model.dart`。
- desktop 使用 `desktop_multi_window`，remote/file/terminal/port-forward 等窗口拥有独立 Flutter engine；plugin 注册必须在每个 runner/engine 完整执行。

此混合模型可运行，但状态所有权分散，容易产生 timer、stale client、dialog overlay 和跨 engine 生命周期问题。

## 6. Android 四层状态架构

Android 不能用一个“服务开关”描述：

1. core service/JNI：`_isReady`、`MAIN_SERVICE_CTX`。
2. screen share：`_isStart`、`mediaProjection`、`captureStarting`。
3. frame source：normal `ImageReader`、`SKL`、`shouldRun` ignore、one-shot。
4. PC display：`waitForFirstImage`、`waitForImageTimer`、last frame/reconnect。

core service 在线不等于投屏；projection 丢失不等于 relay 断开；只有真实 RGBA/texture frame 到 UI 才能清 waiting。详见 `04_ANDROID_PIPELINE.md`。

## 7. Windows 架构

- capture：DXGI 优先、GDI fallback，privacy 可切 Magnifier。
- input：`src/server/input_service.rs` → `libs/enigo/` / `SendInput`，portable service 可代理高权限操作。
- privacy：exclude/topmost window、Magnifier、virtual display 三类实现。
- virtual display：当前活跃选择为 Amyuni `usbmmidd_v2`，实际 platform addition 是 `amyuni_virtual_displays`；`openworld_virtual_displays` 只属于未选用 RustDesk IDD 分支。
- helper/injection：`RuntimeBroker_openworld.exe`、`WindowInjection.dll`、low-level hooks。

详见 `05_WINDOWS_PIPELINE.md`。

## 8. 仓外 API 架构

Flutter 普通产品登录和资源 API多为 Dart 直接 HTTP；Rust `account.rs` 主要提供 OIDC device auth；`sync.rs` 提供 endpoint heartbeat/strategy；`downloader.rs` 提供下载；`record_upload` 休眠。仓库没有后端数据库实现。

详见 `07_API_SYSTEM.md`。

## 9. 架构不变量

- 源码与运行时状态必须分层，不用 UI 文案代替真实状态。
- Android hidden recovery 不得弹 `MediaProjection` 授权。
- Android disconnect 不得自动停止 screen share。
- waiting 不得自动切 ignore/screenshot fallback。
- ZEGO 不得进入旧 `audio_service` 媒体链。
- T009移除旧ADB/LADB；本机LocalAdbPage与PC专用Misc.remote_adb共用LocalAdbManager，Shell栏三键仍经原ADB命令。2026-10-14 T001追加Misc.remote_adb_video(41)、LocalAdbMirror/LocalAdbTouch、独立PC解码与主界面分栏；普通画面输入在Android授权入口按ADB状态分流，无ADB时走原无障碍。原视频、截图、waiting、terminal保持。见[投屏与触摸架构](14_ADB_MIRROR.md)。
- controller relay-only 与 endpoint direct surface 必须明确区分。
- 任何 protobuf 改动必须同步 sender、receiver、FFI/UI 和兼容策略。
- Windows privacy/virtual-display 修改必须带 driver/OS/permission 恢复方案。
- secret 不得写入客户端、跟踪文档或脚本默认值。

## 10. 架构债务

- 根历史被压成单次导入，缺少可重放 upstream strategy。
- 大型 God files 与 global/static state 形成高耦合。
- Android raw buffer 和 `static mut` 横跨语言/线程安全边界。
- transport crypto、HTTP 与 fail-open 行为缺少正式 threat model。
- active/dormant/legacy feature 没有自动可达性检查。
- 构建和二进制资产依赖本机隐式状态。
- 手动-only Actions、少量测试和弱 commit 语义使回归证据不足。
