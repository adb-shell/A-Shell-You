# OpenWorld 内置 ADB 权限服务 / Embedded Privileged Backend

2026-11-21 / [T-2026-11-21-001](../tasks/T-2026-11-21-001-embedded-adb.md) / [ADR-0030](../ADR/0030-app-private-privileged-service.md)。源码基点为与main对齐的test/956ecf4，版本2.0.26 / 2.0.26+59；本任务为未提交工作区实现，V0 静态审查，构建和真机 NOT_RUN。本文取代 12/13/14 中“日常操作持续连接无线 adbd、健康循环自动恢复无线连接”的旧合同；其他业务、权限和媒体协议沿用。

## 使用合同

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

- `PrivilegedBridge` 为 App 进程单例。`openworld_privileged_v1` 保存 enabled/generation；它只保存授权意图，不代表服务存活。配对身份及原 `local_adb_record_v1` 不迁移、不导出。
- 明确激活才创建新 generation、启动后台进程。后台脱离启动 Shell 会话、重定向标准输入输出；不保持 adbd 连接，不改变 USB/无线调试系统设置。
- Provider 仅接收 shell/root 调用的 attach；异步验证协议、UID、owner UID、包名、当前 APK 路径、generation 和真实 `id -u` 的退出码及输出。验证上限 8s；失败关闭候选服务。不能用持久化标记伪造 ready。
- 服务经 framework Binder 查询 IPackageManager/IActivityManager，按完整 owner UID 对应 Android user 查询；不调用 `ActivityThread.systemMain()` 或初始化 HWUI。SDK 30/31 的 ContentProvider 调用签名及 33 的 flags 宽度分别处理。
- UID observer 及每 5s 的服务侧检查只向已经运行、未 force-stop 的 App 重新递送 Binder；不自动启动停止的 App，不自动发现 adbd。App 重建后重新验证仍存活的服务。
- 单例 abstract socket `openworld.privileged.<uid>` 仅占名防重，无 accept/命令处理。旧实例退出等待上限 6s；它不是对外端口。安装身份变化或 App 拒绝 generation 时服务停止。

## 通道、并发和清理

`AdbChannel` 统一 bootstrap libadb stream 与运行时 FD。所有正常命令/投屏/截图/节点/黑屏/触摸/键盘/剪贴板/本应用无障碍/电池豁免不再传入无线端口。命令、媒体、输入、遮罩仍各自拥有 client/FD，取消一个请求不停止设备服务。

| 项目 | 约束 |
|---|---|
| 服务入口 | Binder 每次事务核对完整调用 UID 与当前安装身份；不接受客户端指定任意 app/user |
| 并发 | 服务最多 24 个通道；原命令队列 8 / 并发 2 / 20s，配对激活 35s、连接激活 20s 保持 |
| Shell | `/system/bin/sh -c`，stdout/stderr/真实退出码封装为 shell-v2；stdin 单包最多 65536 字节；命令输出原 64KiB 截断规则保持 |
| 媒体 | FD 传输数据，Binder 只传小型控制和描述符；scrcpy 只允许 `localabstract:scrcpy_[0-9a-f]{8}` |
| sync | 只允许原固定 scrcpy 5.0.1 视频和touch两个路径、长度733930及SHA256；随机临时文件 O_EXCL/O_NOFOLLOW、0600、fsync、rename；不是通用上传接口 |
| 取消 | epoch 拒绝迟到 OPEN；shutdown FD 唤醒阻塞 I/O，单通道 CLOSE；App 进程死亡经 lease Binder 清理它的通道 |
| 服务停止 | 立即拒绝新调用、关闭全部 FD、销毁拥有的子进程，最多 1s 清理后退出；不重放命令 |
| 异常后代 | 不合作的进程管道占用通道名额，不无限创建线程；取消不能撤销已经生效的系统修改或保证任意命令自行启动的后台后代全部退出 |

手机或任意有控制权 PC 的 `disconnect` 是设备级“停用”，保留配对；`revoke` 另清配对记录与身份。两者立即在内存中拒绝 Binder，随后串行持久化撤销，迟到启动不能恢复权限。普通 PC 断开、取消 owner、关闭 Shell 栏不调用服务 STOP；若关闭会话时其激活仍未完成，该次激活按 owner 取消。

## 状态与版本兼容

状态 JSON `apiVersion=5`，字段40/41、FRB、JNI签名及 `pair/pairConnect/connect/disconnect/revoke` 名称不变。`connected` 现在表示已验证且存活的 Binder 服务；`connectPort` 只表示最后激活端口。追加：

| 字段 | 含义 |
|---|---|
| activationSupported | 新执行后端能力标志；旧端缺失时 PC 保留旧连接文案 |
| executionBackend | embedded |
| activationState | inactive / starting / ready / activation_required |
| activationError | 验证、死亡或记录错误；不含配对码/命令/身份密钥 |
| serviceUid | ready 时实际 shell/root UID，否则 null |
| wirelessConnected | 激活临时 adbd 状态；不能作为业务可用判断 |
| recovering | false；不再后台恢复无线端口 |

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
- 目标：Release app_process/provider可加载；激活/关闭无线调试/蜂窝/重连/App重建/服务死亡/重启；多PC隔离与拒绝第三方UID；见 TEST_MATRIX 的 ADB-P01—P12，并回归 ADB-R/M/A、NET-04、DSP-09。

本轮未执行以上命令、产品测试、设备操作、依赖解析、CI触发或发布。回退前显式停用，再恢复旧APK和源码；配对密钥格式不变，但回退不会撤销用户已经执行的系统命令。
