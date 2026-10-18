# T-2026-10-18-008：ADB 媒体、黑屏、命令与弹窗排查

## T0—T4：授权、基线、影响和方案

- 用户授权定位并修复 ADB 投屏/无视/穿透卡顿、黑屏兼容、命令 exit 255 与 PC 弹窗拖动卡顿；提交推送 test 后单次 Both/常规编译，不监控。原采集、无障碍、输入、产品身份保持。
- 起点 main/e3b4454 clean；fetch 后 test/82e0f7f 快进 main/e3b4454，工作区无其他修改。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0025/0026、D031 保持。无其他活动任务。
- Owner：ashell-master；Android/Flutter/Rust/Network/Security/Release 领域技能；无代理委派。C2 精确修复和 C3 test Git/单次 CI 已由当前请求授权；不本地产品构建、不操作手机、不强推、不改依赖版本。
- 已证实：截图每帧重启 screencap 并传回全尺寸 raw；穿透固定 500ms 而原穿透最小 80ms；ADB 使用普通媒体动态 QoS fps；拖动弹窗逐事件 setState 重建所有字段；libadb 大写入分片未逐包等待 OKAY；侧栏遗漏命令输出。255 是 shell 实际退出码，设备具体原因还需 stderr，不能虚构权限成功或重放命令。
- 方案：独立常驻截图通道在 shell 缩放压缩，保留 screencap 兼容路径；穿透复用事件更新的节点缓存并对齐刷新间隔；隔离 ADB 帧率与普通媒体动态降速；弹窗只更新位移；黑屏使用可排除采集的 buffer 图层，保留 App 预绘制文字；命令协议与错误如实处理。协议字段40/41、授权、共享 manager、多 PC 语义不变。
- 风险：隐藏系统 API、ROM 图层行为与真实性能需 Android 11—15 实机验证；不得承诺所有设备已通过。无新增外部二进制或依赖；AOSP 与固定 libadb 源码只读核对。回滚本任务提交即可。

## T5—T8：实施、验证与交付

- 实施与V0复检完成：常驻截图在Shell缩放压缩，单帧请求/租约/有界读取，异常时关闭并等待旧writer结束才回退；R8保留新增入口。穿透目标80ms且依赖系统事件失效缓存；视频独立30fps上限，截图300ms。保留width/350、1280边界及质量档位。PC弹窗仅位移更新，帧重绘隔离与取帧耗时扣除。黑屏使用一像素buffer缩放及独立白字层，创建和事务均设采集排除标记，旋转更新尺寸。
- 命令直接UTF-8 OPEN，避免子命令读取脚本stdin；scrcpy同步数据按4KiB等待ACK。侧栏保留有界实际输出，截图/scrcpy显示真实退出码与有界错误，连接失败区分握手/UID验证和通道异常。255本身无唯一根因；未取得失败设备输出，不能宣称所有255已解决。没有添加命令自动重放、权限伪造或系统全局设置。
- V0：源码调用/可见性、Java/Dart词法括号、R8入口、取消/回退资源与diff检查；本地产品build/test/analyze/codegen及设备操作均NOT_RUN。原MainService/Accessibility/原视频服务/输入链实现/协议/构建脚本无修改。状态推送、多PC语义保持。
- 《编译验证需求》：GitHub Actions Build AShell Clients-New，ref=test，Platform=Both，BuildMode=常规编译，单次触发且不监控。
- 真机：AND 原共享/无障碍回归、ADB-only/双画面、多 PC、连续切换、横竖屏、黑屏时投屏/无视真实画面、Unicode/长命令/失败255、弹窗拖动；所有未经执行项待证。
- STATE/WORK-20261018-008 / CE-20261018-T008-01；同步13_REMOTE_ADB/14_ADB_MIRROR的最小变更合同及五份记忆。Decision/ADR/External Asset Registry：N/A — no new architecture/dependency/binary。提交/推送及dispatch准确回执见交付；运行兼容和流畅度仍verification-required。

## 只读外部核对

- [libadb 3.1.1 AdbStream](https://github.com/MuntashirAkon/libadb-android/blob/3.1.1/libadb/src/main/java/io/github/muntashirakon/adb/AdbStream.java)：write内部拆包未逐包等待OKAY，read/close语义核对。
- [AOSP UiAutomation](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android14-release/core/java/android/app/UiAutomation.java)：无Accessibility模式及截图入口。
- [AOSP SurfaceControl](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android12-release/core/java/android/view/SurfaceControl.java)：SKIP_SCREENSHOT与SECURE语义不同；Android11元数据及Android15方法另行核对。厂商实现/真实屏幕效果未验证。
