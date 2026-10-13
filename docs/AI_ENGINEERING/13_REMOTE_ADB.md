# AShell PC 远程 ADB

交付前核对：2026-10-13，main/efb2599 + 三份待提交 Dart 修改。feac956 固化双端口发现等能力，efb2599 移除二维码并修正请求/取消逻辑；当前工作树进一步移除 PC 定时刷新、简化两端配对模式并突出 PC 连接按钮。本次只同步文档，证据仍为 V0，交付SHA以Git最终回执为准。详见 [T007](../tasks/T-2026-10-13-007-adb-docs-sync.md)、[ADR-0020](../ADR/0020-adb-code-pairing-and-explicit-refresh.md)、[Baseline14](../BASELINE/14_ADB_CODE_PAIRING_BASELINE_2026-10-13.md)。

PC 通过已认证普通远控会话，把输入交给 Android App。ADB 私钥、配对记录、连接和操作队列均属于 Android 进程。没有加入 ADB 投屏、scrcpy 或输入注入；原 Accessibility、MediaProjection、normal/SKL/ignore、terminal、ZEGO、产品身份和传输策略保持。

## 使用

PC 和 Android 都需更新本次安装包。普通远控窗口顶栏使用与其他菜单相同的按钮样式和 ADB 图标，提供配对、命令、撤销、状态、断开五个独立弹窗；拖动标题栏可移动窗口，窗口位置限制在可见范围。配对弹窗只处理配对与连接。

| 配对方式 | 操作 |
|---|---|
| 只填配对码（默认） | 仅显示六位配对码输入框，不显示配对端口、连接端口或扫描按钮。保持手机系统配对码窗口开启，点击“配对并连接 ADB”；手机在执行时发现端口、配对、连接并验证 Shell。 |
| 手动端口 | 显示两个端口框、配对码框和“扫描并填充端口”。配对端口来自系统配对码窗口，连接端口来自无线调试首页；填写的端口直接使用，任一端口留空则自动发现。扫描未发现的旧值会清空，多个本机端口须手动核对。 |
| 已配对 | 保持无线调试开启，点击“我已配对，连接并验证 Shell”；无需配对码或打开系统配对码窗口。自动模式发现连接端口；手动模式使用所填连接端口，留空自动发现。连接时不使用配对端口。 |

两端均按模式实际创建/移除输入控件；切换时解除焦点并保持控件身份，手动端口值可保留用于切回，但自动模式始终提交端口 0，不消费隐藏值。PC 打开弹窗仅查询状态，不预扫描或填充隐藏端口。手动模式的扫描需点击按钮。

二维码配对的 UI、生成逻辑、serviceName 参数和专用等待分支已移除；旧端发送该参数会被拒绝，不静默转为普通配对。系统六位码不会经 mDNS 广播，模块不通过无障碍或截图提取它。PC 发起的发现和 ADB 操作都在 Android 本机执行，不要求 PC 与手机位于同一局域网。

PC 的“我已配对，连接并验证 Shell”使用主题色 ElevatedButton，可操作时突出显示。启用条件仍是已确认支持、controlAllowed 且本窗口无任务执行，不要求填写配对码；状态未确认、只读或执行中仍禁用。此样式调整不改变服务端授权，也不把 paired 等同于 connected。

配对后自动执行 connect，并用 id 的退出码及 uid=2000/0 验证；不会主动获取 root。配对成功但连接失败保留 paired 记录，明确提示重新连接或填写连接端口。paired 是 App 记录加身份文件存在性，并非查询系统配对数据库；系统手动忘记后必须重新验证。旧身份无记录时仍可直接连接，成功补记。

命令弹窗只执行 shell（不加 adb shell 前缀），显示真实退出码、输出和截断状态。断开保留身份；撤销弹窗要求点击明确确认按钮，清 App 共享身份/记录并取消队列，系统配对列表仍需手动忘记。

## 状态与多 PC

- PC 已取消每 3 秒自动刷新。打开弹窗查询一次；点击刷新按钮重新查询；本窗口配对/连接/命令等返回时，应用响应中的 Android 状态。手机或其他 PC 操作后的变化须手动刷新或重开弹窗，不能承诺 3 秒内推送同步。Android 本机页的前台 3 秒查询保持。
- 配对记录不保证 shell 可用，connected 来自 transport 和 id 验证。状态是最近一次查询/操作的快照；状态失效显示原因并暂时禁止提交。没有命令自动重放。
- 状态查询与命令结果独立：一次查询失败不会放弃已发送命令的结果；结果等待到期/失败时尽力请求 owner 取消，并说明结果尚未确认，不能把超时当作命令未执行。
- Android 一项执行、最多七项排队；各 PC 结果只回到 origin，不广播其他人的命令、输出或码。
- 关闭弹窗/断线取消该 owner 未完成请求，保留空闲共享连接；纯扫描，或配对/连接尚未接触 transport 时的扫描失败/取消，不关闭已有 ADB。取消已使用 transport 的任务可能关闭共享 ADB，其他 PC 下次查询才显示变化。
- 撤销是共享身份动作，影响所有 PC；不能单独“撤销某 PC 的 ADB 配对”，因为 PC 没有 ADB key。App 进程重启后身份与记录保留，连接和队列不保留。

## 权限修复与威胁边界

T004 使用 peer_keyboard_enabled 拦截包括 status 在内的所有 ADB 请求。server_model.dart 的 mobile 初始化将 enable-keyboard 设置为 N，该状态又与 Accessibility 键盘能力相连，造成普通已认证会话仍被拒绝，PC 控件全灰且看不到手机状态。

T006 把 ADB gate 与手机键盘能力分开：

1. 必须位于 server self.authorized 分支；只接受普通会话，排除 file-transfer、terminal、camera；port-forward 不进入此分支。
2. status 可由已认证普通会话读取，回复包含 controlAllowed。
3. 其余操作拒绝 disable_keyboard（PC 主动只读模式）；切只读与 Drop 取消该 owner 请求。手机 Accessibility/keyboard 开关不再作为 ADB 权限，不改原键盘处理。
4. 这是明确的独立 ADB 能力边界：拥有普通认证会话且未选择只读的 PC 可以使用本机配对后的任意 shell。不新增命令白名单、逐条审批或持久审计，也不修复原产品认证机制的既有问题。参见 ADR-0019。
5. 沿用原 peer stream 的认证/加密/relay 策略，JSON 不额外加密；不打开新明文监听服务。任意 shell 命令仍可能主动改变系统，代码隔离不等于限制命令副作用。

## 调用链与合同

PC showRemoteAdbDialog / RemoteAdbController → existing session_get_common keys → src/remote_adb.rs → Misc.remote_adb field40 → authenticated server branch → RemoteAdbNative 独立 JNI → LocalAdbManager → loopback adbd。LocalAdbPlugin 同时为 Android 本机页面服务。

请求保持 JSON v1：v、kind=request、id（PC Rust 生成）、method，及可选 port/code/connectPort/command。Android 拒绝未知业务参数，包括已退役 serviceName；本机 MethodChannel 同样保留并校验传入参数，不静默丢弃。状态 apiVersion=2；FRB 签名、protobuf 字段、identity/record schema 不变。新 PC 对旧 API 提示更新并禁用 discover/pairConnect，旧 pair/connect 仍兼容。

| 方法 | 参数与行为 |
|---|---|
| status | 不排队，返回真实共享状态和 apiVersion；不自动连接 |
| discover | 有界双服务扫描，返回 pairPort/connectPort/ambiguous；0 表示没找到唯一结果 |
| pair | 兼容旧端，仅配对；port=0 自动发现，code 六位数字 |
| pairConnect | port/code，可选 connectPort=0；成功配对后连接/id验证；后续连接失败或取消时保留已确认成功的配对记录并提示重新连接 |
| connect | port=0 或省略自动发现；无发现可回退最后成功端口；有多个本机端口则要求手动；成功返回验证结果 |
| shell | command 非空、无 NUL、最多4096 Java UTF-16 units；output/exitCode/truncated |
| disconnect / revoke | 共享操作，前者保留配对，后者清 App 身份及记录 |
| cancel | 只取消 origin owner 的未完成请求 |

响应 v/id/ok/state/result 或 error/message，加由 Rust gate 写入的 controlAllowed。请求32KiB，响应512KiB（容纳64KiB shell结果JSON转义）；PC等待表最多64项，Android回复路由最多64项/每连接10项，保留310秒后惰性清理。PC status/cancel 等待8秒，其余300秒（为8项、每项最多35秒的预算留余量），失败后的尽力取消另等最多8秒。Android默认watchdog20秒，pairConnect35秒；watchdog触发取消，worker退出后才能开始下一项，并非所有ROM已验证的硬实时期限。请求期间200ms轮询的是本地Rust回执表，不是恢复PC周期性远端status请求。

JNI 保持 Android 注册的 JVM + manager GlobalRef；native worker attach 线程，用 local frame 调用 Java main.post；manager 主线程管理队列，单 worker 独占连接，abort 关闭 socket 以取消阻塞。记录为 local_adb_record_v1；私钥仍 noBackupFilesDir/local-adb-identity.v1。撤销时的固定 libadb3.1.1 SslUtils.sslContext 清理及 keep 规则保持。

## 端口发现与来源

采用 aShellYou 的双 NSD 服务思路，参考 LADB 的发现流程，独立实现适配当前 Java/SDK 的 AdbPortDiscovery；未复制整包源码或引入新运行依赖。发现 _adb-tls-pairing._tcp. 与 _adb-tls-connect._tcp.，只接受本机网络接口/loopback。单 resolver、有界队列、服务丢失时清结果/待解析项/重试计数、ALREADY_ACTIVE 延迟重试；已知解析和待处理队列完成后再等待短收敛窗口，单次发现最多6.5秒。未解析出唯一端口时提示手动，不扫描其他设备端口、不修改系统无线调试设置。

沿用T006已登记的研究来源：[LADB DnsDiscover](https://github.com/tytydraco/LADB/blob/60f48029cf9d8e0bc848ca41a7bd76694d4ab796/app/src/main/java/com/draco/ladb/utils/DnsDiscover.kt)、[aShellYou AdbMdnsDiscovery](https://github.com/DP-Hridayan/aShellYou/blob/0fa5da1846b9b589c3d7f4993fb2d65b35b462ac/feature/shell/src/main/java/in/hridayan/ashell/shell/wifi_adb_shell/service/AdbMdnsDiscovery.kt)。现有 libadb 负责 TLS/SPAKE2，无需恢复 LADB adb binary/settings automation。此前 [AOSP AdbQrCode](https://github.com/aosp-mirror/platform_packages_apps_settings/blob/main/src/com/android/settings/wifi/dpp/AdbQrCode.java) 仅是已移除功能的历史研究来源；本次文档同步未重新抓取上游，也未新增依赖。

## 验证

仅 V0 源码与文档复核，未取得新包的正式构建或真机结果。efb2599 已推送 main，随后 Build AShell clients 的 both/aarch64 dispatch 获 HTTP204 接受；按当时要求未监控，不能记为 V2 PASS。本文为本轮交付前快照，三份 Dart UI 修改尚未提交，也不在该次 dispatch 的代码中。用户本轮追加授权提交全部待交付改动、推送并再触发单次编排；实际SHA与接受回执见最终交付，不监控。

正式验证见 [TEST_MATRIX](../../TEST_MATRIX.md) ADB-R01—09、11—15；旧QR正例ADB-R10已标N/A，新负例覆盖退役参数拒绝。两端新包、Android11+、至少两台PC、Release/R8/JNI、不同ROM的NSD、模式切换/手动刷新/错码/端口变化、权限负例、取消/超时/撤销重配均待验证。保留 AND-01/02/04/05/07/08、FLT-04/05/06 原链回归；新PC对不含字段40的旧端status超时，对非Android新端明确unsupported。
