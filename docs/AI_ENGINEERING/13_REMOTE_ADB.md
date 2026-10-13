# AShell PC 远程 ADB

最后核对：2026-10-13，T006 基于 main/504adea 实现。T005 的 bind 导入修复已在该 HEAD；T006 验证等级 V0，用户授权提交、推送并触发 Build AShell clients（both/aarch64），不监控构建。任务见 [T006](../tasks/T-2026-10-13-006-adb-usability.md)，决定见 [ADR-0019](../ADR/0019-adb-discovery-and-independent-control.md)。

PC 通过已认证普通远控会话，把输入交给 Android App。ADB 私钥、配对记录、连接和操作队列均属于 Android 进程。没有加入 ADB 投屏、scrcpy 或输入注入；原 Accessibility、MediaProjection、normal/SKL/ignore、terminal、ZEGO、产品身份和传输策略保持。

## 使用

PC 和 Android 都需更新本次安装包。普通远控窗口顶栏使用与其他菜单相同的按钮样式和 ADB 图标，提供配对、命令、撤销、状态、断开五个独立弹窗；拖动标题栏可移动窗口，窗口位置限制在可见范围。配对弹窗只处理配对与连接。

| 配对方式 | 操作 |
|---|---|
| 只填配对码（默认） | 手机打开无线调试的配对码窗口并保持开启；PC 输入六位码，点击“配对并连接 ADB”。手机实时发现配对端口，成功后发现连接端口并验证 Shell。 |
| 手动端口 | 输入配对端口和六位码；连接端口留空仍自动发现。配对端口也可留空。扫描按钮会填入本机当前发现的端口，未发现的旧值清空。 |
| 扫码免输码 | PC 点击生成二维码，手机用系统无线调试的“使用二维码配对设备”扫描；自动发现指定服务、配对、连接。不是用普通相机扫描，也不读取系统六位码。 |
| 已配对 | 点击“我已配对，连接并验证 Shell”，无需再次输入码；自动发现失败时切手动模式填写无线调试首页当前连接端口。 |

二维码采用 Android 系统的 WIFI:T:ADB schema。随机服务名与一次性六位口令只在当前窗口/请求中使用，不落盘、不加入状态广播；操作完成、取消或窗口关闭后移除二维码。排队期间等待 Android 执行；真正开始扫描后最多等待扫码 60 秒，整项 watchdog 90 秒。Android 系统六位码不会经 mDNS 广播，模块不通过无障碍或截图提取它。

配对后自动执行 connect，并用 id 的退出码及 uid=2000/0 验证；不会主动获取 root。配对成功但连接失败保留 paired 记录，明确提示重新连接或填写连接端口。paired 是 App 记录加身份文件存在性，并非查询系统配对数据库；系统手动忘记后必须重新验证。旧身份无记录时仍可直接连接，成功补记。

命令弹窗只执行 shell（不加 adb shell 前缀），显示真实退出码、输出和截断状态。断开保留身份；撤销弹窗要求点击明确确认按钮，清 App 共享身份/记录并取消队列，系统配对列表仍需手动忘记。

## 状态与多 PC

- 打开弹窗先读取 Android；状态每 3 秒刷新，手机本地和任意 PC 的操作共享相同 paired/connected/queue/phase。状态失效显示原因，输入框仍可填写；只有得到支持及当前会话权限后才提交操作。
- 状态和结果独立：配对记录不保证 shell 可用，connected 只来自实际 transport 和 id 验证。没有命令自动重放。
- Android 一项执行、最多七项排队；各 PC 结果只回到 origin，不广播其他人的命令、输出或码。
- 关闭弹窗/断线取消该 owner 未完成请求，保留空闲共享连接；取消端口扫描不关闭 ADB。取消正在使用 transport 的配对/连接/shell 可能关闭共享 ADB，其他 PC 状态随之更新。
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

请求保持 JSON v1：v、kind=request、id（PC Rust 生成）、method，及可选 port/code/connectPort/serviceName/command。状态增加 apiVersion=2；FRB 函数签名、protobuf 字段、identity/record schema 均不变。新 PC 遇旧 Android 显示更新提示，不调用新增方法；旧 pair/connect 仍兼容。

| 方法 | 参数与行为 |
|---|---|
| status | 不排队，返回真实共享状态和 apiVersion；不自动连接 |
| discover | 有界双服务扫描，返回 pairPort/connectPort/ambiguous；0 表示没找到唯一结果 |
| pair | 兼容旧端，仅配对；port=0 自动发现，code 六位数字 |
| pairConnect | port/code，可选 connectPort=0；成功配对后连接/id验证；可选 serviceName 仅允许 ashell- 加32位hex且port=0，用于扫码 |
| connect | port=0 或省略自动发现；无发现可回退最后成功端口；有多个本机端口则要求手动；成功返回验证结果 |
| shell | command 非空、无 NUL、最多4096 Java UTF-16 units；output/exitCode/truncated |
| disconnect / revoke | 共享操作，前者保留配对，后者清 App 身份及记录 |
| cancel | 只取消 origin owner 的未完成请求 |

响应 v/id/ok/state/result 或 error/message，加由 Rust gate 写入的 controlAllowed。请求32KiB，响应512KiB（容纳64KiB shell结果JSON转义）；PC等待表最多64项，Android回复路由最多64项/每连接10项，750秒惰性清理。PC状态8秒超时，其余740秒（覆盖8项最长90秒队列），不重放。Android默认watchdog20秒，pairConnect35秒，QR90秒；watchdog触发取消，worker退出后才能开始下一项，并非所有ROM已验证的硬实时期限。

JNI 保持 Android 注册的 JVM + manager GlobalRef；native worker attach 线程，用 local frame 调用 Java main.post；manager 主线程管理队列，单 worker 独占连接，abort 关闭 socket 以取消阻塞。记录为 local_adb_record_v1；私钥仍 noBackupFilesDir/local-adb-identity.v1。撤销时的固定 libadb3.1.1 SslUtils.sslContext 清理及 keep 规则保持。

## 端口发现与来源

采用 aShellYou 的双 NSD 服务思路，参考 LADB 的发现流程，独立实现适配当前 Java/SDK 的 AdbPortDiscovery；未复制整包源码或引入新运行依赖。发现 _adb-tls-pairing._tcp. 与 _adb-tls-connect._tcp.，只接受本机网络接口/loopback；二维码进一步匹配临时 serviceName。单 resolver、有界队列、已丢失服务清理、ALREADY_ACTIVE 延迟重试、短收敛窗口；普通发现最多6.5秒。未解析出唯一端口时提示手动，不扫描其他设备端口、不修改系统无线调试设置。

研究来源（2026-10-13）：[LADB DnsDiscover](https://github.com/tytydraco/LADB/blob/60f48029cf9d8e0bc848ca41a7bd76694d4ab796/app/src/main/java/com/draco/ladb/utils/DnsDiscover.kt)、[aShellYou AdbMdnsDiscovery](https://github.com/DP-Hridayan/aShellYou/blob/0fa5da1846b9b589c3d7f4993fb2d65b35b462ac/feature/shell/src/main/java/in/hridayan/ashell/shell/wifi_adb_shell/service/AdbMdnsDiscovery.kt)、[AOSP AdbQrCode](https://github.com/aosp-mirror/platform_packages_apps_settings/blob/main/src/com/android/settings/wifi/dpp/AdbQrCode.java)。选型原因：现有 libadb 已负责 TLS/SPAKE2，双服务发现足够满足自动端口，无需恢复 LADB 内置 adb binary 或其 settings grant/cycling。

## 验证

本地无项目工具链，仅 V0 请求响应、NSD与取消、Dart/API/生命周期、权限与范围静态复核。T005 的旧SHA构建错误已修复，但不能据此声称 T006 构建通过。按本轮授权提交后运行 Build AShell clients，platform=both、android_arch=aarch64；不监控，不记为 V2 PASS。

正式验证见 [TEST_MATRIX](../../TEST_MATRIX.md) ADB-R01—11：两端新包、Android11+、至少两台PC、Release/R8/JNI、不同ROM的NSD/QR、关闭系统窗口/错码/端口变化、权限负例、取消/超时/撤销重配；保留 AND-01/02/04/05/07/08、FLT-04/05/06 原链回归。新PC对不含字段40的旧端状态会超时，新PC对非Android新端明确unsupported。
