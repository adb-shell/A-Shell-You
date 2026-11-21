# OpenWorld 内置 ADB 权限服务 / Embedded Privileged Backend

2026-11-21 T002保活与T003文案已合为test/b12cbdb并推送：新增App重建恢复窗口、验证退避、应用进程lease/授权检查、双observer及独立维护线程，修正暂时异常误停；界面精简且恢复语义保持。Both/常规编译run44890553076已启动，用户随后要求不监控，最终结果未知；[续修任务](../tasks/T-2026-11-21-002-embedded-adb-resilience.md)/[交付回执](../tasks/T-2026-11-21-004-adb-resilience-build.md)。下方T001成功编译仅覆盖输入版本，设备验收仍待证。

2026-11-21交付：源码test/febf7f0f20460a0bfffbc0ee0a2581828a30d7c0已推送；[双端常规编译44883517881](https://github.com/adb-shell/A-Shell-You/actions/runs/44883517881)于17:46:24 Asia/Tokyo以SUCCESS结束，Android Native/APK和Windows EXE全部通过。产物链接与来源见任务回执；构建不替代真机验收。

2026-11-21 / [T-2026-11-21-001](../tasks/T-2026-11-21-001-embedded-adb.md) / [ADR-0030](../ADR/0030-app-private-privileged-service.md)。源码在与main对齐的test/956ecf4上完成，提交为febf7f0，版本2.0.26 / 2.0.26+59。本文取代 12/13/14 中“日常操作持续连接无线 adbd、健康循环自动恢复无线连接”的旧合同；其他业务、权限和媒体协议沿用。构建证据见任务回执，真机验收仍NOT_RUN。

## 使用合同

### 安装后的操作顺序

1. 安装本次Android APK和PC版本。现有CI每次使用临时测试签名，Android可能无法覆盖旧包；保留数据需同一稳定签名，卸载会清除应用数据及配对身份。不要把本次测试包当作可无损覆盖的正式升级包。
2. Android 11+手机连接Wi-Fi，开启开发者选项、USB调试、无线调试。PC通过开放世界正常连接手机；PC不必与手机处于同一Wi-Fi，仍通过现有已认证relay会话操作。
3. 首次激活：手机打开“无线调试 → 使用配对码配对设备”，保持窗口打开；PC打开“配对与激活”，输入6位码并点“配对并激活”。也可使用手机ADB页同名按钮。自动发现失败时按系统页面填写配对端口和连接端口，二者不能混用。
4. App重开后若显示正在恢复已有ADB服务，先等待恢复，不必开启无线调试或重复配对。恢复失败且确需重新激活时，若配对记录仍在，选择已有配对的激活入口即可，无需再输入配对码。等状态明确显示已激活后再操作；不能把已配对或正在恢复当成可用。
5. 服务存活期间可关闭无线调试、切换蜂窝或Wi-Fi，PC重新连接后直接使用原ADB Shell、投屏/截图/节点、黑屏、触摸/键盘/文本输入和本应用权限功能。Shell输入如`id -u`即可，不加`adb shell`前缀。
6. 平时退出远控窗口即可；PC ADB菜单的“停用 / 断开 ADB”和“撤销配对”分别停止共享服务、清除本应用身份，不用于普通断开。T003按用户要求移除了手机页面的停用按钮；明确停用请用PC菜单。
7. 重启或服务被系统终止后，重新连接Wi-Fi并开启无线调试，点“激活 ADB”；只有配对记录被系统忘记、数据清除或身份重建后才需要重新配对。保留开发者选项/USB调试开启。

首次设备验收建议：ready后执行`id -u`，再关闭无线调试、切蜂窝、重连PC，重复命令并分别验证媒体/输入。预期shell UID为2000（既有root adbd为0）；若状态变为需激活，应记录机型/Android版本及操作顺序，不能把配对成功视为存活测试通过。

首次在 Android 11+ 开启无线调试，配对并激活。已有配对身份时，只需显式激活，无须重新输入配对码。激活通过真实 Shell 身份验证后，正常功能通过本机 Binder/文件描述符执行，关闭无线调试或 Wi-Fi 不会让应用主动断开权限服务。PC 仍须通过已认证的 OpenWorld relay 会话调用；蜂窝数据必须能连通该会话。

服务存活是前提，不是永久授权。设备重启、系统/OEM 杀进程、安装身份变化、显式停用都会使能力失效。开发者选项和 USB 调试应保留开启；具体 ROM 是否因关闭无线调试杀掉 Shell 进程必须真机验证。App UID 始终是普通应用 UID，实际特权操作在独立 shell UID 2000（或既有 root adbd 的 UID 0）进程执行。本实现不获取 root。

只服务开放世界，不依赖另装 Shizuku，不提供其他应用授权管理、通用 Binder 转发、Shizuku SDK 兼容 API 或标准公网 adb TCP 入口。PC“调用 ADB”指现有产品 Shell/输入/媒体功能，不是允许外部 adb.exe 直接连接手机端口。

## 调用与启动

```text
显式 pairConnect / connect
  → LocalAdbManager → LocalAdbClient.connectAdb → 配对身份 / TLS / id -u
  → PrivilegedBridge.activate → 后台 app_process OpenWorldPrivilegedService
  → PrivilegedProvider → INFO + id -u 验证 → ready → 关闭 bootstrap ADB

已认证 PC 字段40/41 → RemoteAdbNative / LocalAdbManager
  → 独立 LocalAdbClient.connect(cancelled) → 已验证 Binder
  → 每次 OPEN 返回 Channel Binder + ParcelFileDescriptor
  → shell-v2 / scrcpy 本机 socket / 固定资产 sync → 原有结果与媒体链
```

- `PrivilegedBridge` 为 App 进程单例。`openworld_privileged_v1` 保存 enabled/generation/bootCount；只保存授权意图，不代表服务存活。配对身份及原 `local_adb_record_v1` 不迁移、不导出。bootCount读取[Settings.Global.BOOT_COUNT](https://developer.android.com/reference/android/provider/Settings.Global#BOOT_COUNT)，仅在新旧计数均可读且不等时判定重启；缺失/不可读时尝试恢复，不能猜测已重启。
- 明确激活才创建新 generation、启动后台进程。后台脱离启动 Shell 会话、重定向标准输入输出；不保持 adbd 连接，不改变 USB/无线调试系统设置。
- Provider仅接收shell/root的attach；异步验证协议、UID、owner UID、包名、当前APK路径、generation和真实`id -u`。每次上限8s，错身份/协议/UID拒绝并停止候选；暂时的系统或探测失败取消本次探测，保留候选供再绑定。失败退避1s、3s、随后至少30s，且只在服务主动递送时触发；验证线程1个、队列1项，已取消的排队任务不再发起Binder调用。未验证不能ready。
- 服务经 framework Binder 查询 IPackageManager/IActivityManager，按完整 owner UID 对应 Android user 查询；不调用 `ActivityThread.systemMain()` 或初始化 HWUI。SDK 30/31 的 ContentProvider 调用签名及 33 的 flags 宽度分别处理。
- UID/process observer与每5s维护回退只向已经运行、未force-stop的App递送Binder；不启动停止的App、不自动发现adbd。App重建先进入20s恢复窗口，窗口内收到的8s验证可延长到该次探测结束；超时显示暂不可用，晚到合法服务仍可验证恢复。恢复期间暂不允许重复配对/激活，PC菜单的停用仍可执行。
- 验证成功才返回App进程lease。daemon保存death recipient；App死亡后重新递送，正常存活时每5s用仅shell/root可调用的lease核对generation及实际ready，避免反复获取Provider。lease存活不替代授权：明确拒绝即停止，暂时Binder错误则重试递送；直接STOP失败后也可通过lease/Provider拒绝结束服务。
- 系统查询、Provider和lease调用在单独维护线程，观察事件合并、最多一个维护任务在途，不占用daemon主Looper。observer仅在一次性注册线程中安装，注册阻塞不影响周期维护。PackageManager暂时异常保留daemon，但INFO/OPEN仍拒绝未经确认的安装身份；确认卸载/UID或APK变化才退出。STOP只要求原完整owner UID，在安装查询不可用时也能停用；它不执行特权命令。
- 延迟connect在恢复窗口等待原服务，并在发现/启动阶段再次检查ready。最终begin在同一锁内决定复用或新建；只有真正新激活才标记请求拥有该生命周期，取消复用请求不能停掉恢复好的服务。
- 本应用电池豁免仍是原包名限定的幂等命令，失败最多三次、间隔5s/30s；不重放用户Shell、不永久持有WakeLock、不调整全局进程限制。独立shell进程被强制终止后，普通App不能自行重建权限。
- 单例 abstract socket `openworld.privileged.<uid>` 仅占名防重，无 accept/命令处理。旧实例退出等待上限 6s；它不是对外端口。安装身份变化或 App 拒绝 generation 时服务停止。

## 通道、并发和清理

`AdbChannel` 统一 bootstrap libadb stream 与运行时 FD。所有正常命令/投屏/截图/节点/黑屏/触摸/键盘/剪贴板/本应用无障碍/电池豁免不再传入无线端口。命令、媒体、输入、遮罩仍各自拥有 client/FD，取消一个请求不停止设备服务。

| 项目 | 约束 |
|---|---|
| 服务入口 | INFO/OPEN核对完整调用UID与当前安装身份；STOP仅允许原完整owner UID减少权限；不接受任意app/user |
| 并发 | 服务最多 24 个通道；原命令队列 8 / 并发 2 / 20s，配对激活 35s、连接激活 20s 保持 |
| Shell | `/system/bin/sh -c`，stdout/stderr/真实退出码封装为 shell-v2；stdin 单包最多 65536 字节；命令输出原 64KiB 截断规则保持 |
| 媒体 | FD 传输数据，Binder 只传小型控制和描述符；scrcpy 只允许 `localabstract:scrcpy_[0-9a-f]{8}` |
| sync | 只允许原固定 scrcpy 5.0.1 视频和touch两个路径、长度733930及SHA256；随机临时文件 O_EXCL/O_NOFOLLOW、0600、fsync、rename；不是通用上传接口 |
| 取消 | epoch 拒绝迟到 OPEN；shutdown FD 唤醒阻塞 I/O，单通道 CLOSE；App 进程死亡经 lease Binder 清理它的通道 |
| 服务停止 | 立即拒绝新调用、关闭全部 FD、销毁拥有的子进程，最多 1s 清理后退出；不重放命令 |
| 异常后代 | 不合作的进程管道占用通道名额，不无限创建线程；取消不能撤销已经生效的系统修改或保证任意命令自行启动的后台后代全部退出 |

任意有控制权PC菜单的`disconnect`是设备级“停用”，保留配对；`revoke`另清配对记录与身份。两者立即在内存中拒绝Binder，随后串行持久化撤销，迟到启动不能恢复权限。手机MethodChannel保留既有方法兼容，但页面不再展示停用按钮。普通PC断开、取消owner、关闭Shell栏不调用服务STOP；若关闭会话时其激活仍未完成，该次激活按owner取消。

## 状态与版本兼容

状态JSON `apiVersion=6`，字段40/41、FRB、JNI签名及`pair/pairConnect/connect/disconnect/revoke`名称不变。`connected`表示已验证且存活的Binder；`connectPort`只表示最后激活端口。字段：

| 字段 | 含义 |
|---|---|
| activationSupported | 新执行后端能力标志；旧端缺失时 PC 保留旧连接文案 |
| executionBackend | embedded |
| activationState | inactive / starting / reconnecting / ready / activation_required；后者表示未恢复或已知失效，不笼统断言进程已停止 |
| activationError | 验证、死亡或记录错误；不含配对码/命令/身份密钥 |
| serviceUid | ready 时实际 shell/root UID，否则 null |
| wirelessConnected | 激活临时 adbd 状态；不能作为业务可用判断 |
| recovering | 仅已有权限服务的重新绑定窗口为true，不是无线端口恢复 |

原 revision/instance 推送、owner 请求ID、过期响应过滤、多PC共享状态保持。旧PC可照常调用新Android的方法；旧PC显示“断开ADB”实际会停用服务，新PC明确解释。新PC连接旧Android按旧能力标志运行。仅更新PC无法获得手机端新能力。新旧双方和旧APK回退均需验证，不能把字段兼容视为运行验收。

Rust 端已认证、键盘/只读许可和 developer-priority 继续控制请求与媒体；既有 `pkg2230.rs` / `ffi.rs` 和原采集 JNI 未改。普通 MediaProjection、无障碍服务生命周期、ZEGO、服务器配置和版本未改变。

## 来源、构建与验收

借鉴并改写 [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku/tree/b844bc491f1790c72328e1a8e5b2349f8978f0ea) 的独立进程、Binder递送和 UID observer 机制；固定 revision，Apache-2.0。资产 `adb-privileged/NOTICE.md`、`LICENSE.Shizuku` 随 APK 打包；登记 `EXT-REF-SHIZUKU-001`。没有新增 Maven、native starter、上游 APK 或外部服务；不是完整上游通用授权管理器。

平台参考：[AOSP IPackageManager Android13](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-13.0.0_r1/core/java/android/content/pm/IPackageManager.aidl)确认flags为long；[AOSP Shell manifest Android14](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-14.0.0_r1/packages/Shell/AndroidManifest.xml)列出进程查询/跨用户权限；[AOSP app SELinux](https://android.googlesource.com/platform/system/sepolicy/+/refs/heads/main/private/app.te)允许appdomain间传递已连接socket的读写/shutdown。这些是接口/策略参考，不是本应用或各OEM设备已通过验证的证明。

Manifest 注册专用 provider；`local-adb.pro` 保留 app_process 入口和 provider。普通 Java 引用参与原构建，无新 JNI/FRB/protobuf 生成。Release/R8、隐藏接口、SELinux 和 Android 11—16/主要OEM兼容属于正式验收重点。

《编译验证需求》
- 命令：正式 Linux 根目录 `./build.sh 1`、`./build.sh 2`；正式 Windows 根目录 `build.cmd`。
- 环境：仓库规定工具链与签名/SDK；Android 11+真实设备及双PC已认证relay环境，至少一台可切换蜂窝数据。
- 执行目录：对应正式构建机仓库根。
- 目标：Release app_process/provider可加载；激活/关闭无线调试/蜂窝/重连/App重建/服务死亡/重启；多PC隔离与拒绝第三方UID；见 TEST_MATRIX 的 ADB-P01—P18，并回归 ADB-R/M/A、NET-04、DSP-09。

T001按当时授权执行GitHub Actions双端常规编译；T002保活续修尚未构建或安装，不能沿用T001通过结论。CI是独立hosted-runner环境，其结果不能代替真机验收。回退前显式停用，再恢复旧APK和源码；配对密钥格式不变，但回退不会撤销用户已经执行的系统命令。
