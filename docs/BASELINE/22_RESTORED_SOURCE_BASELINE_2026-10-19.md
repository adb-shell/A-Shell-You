# Baseline22：回退后的源码与文档基线

- Baseline ID：`AS-BL-2026-10-19-0dbe202-restored`
- Source HEAD：`0dbe2027ceb3f1a72ae2700fe8e4942cfe67cec5`
- 观察时间：2026-10-19（Asia/Tokyo）；本地/远端test、main均在此提交，初始工作区clean。
- 本次任务：[T-2026-10-19-006](../tasks/T-2026-10-19-006-restored-docs-sync.md)。仅文档；后续“文档更新”提交保持该实现树。
- 证据：V0源码与Git观察；无本次构建/设备验收通过结论。冻结的历史Baseline01—21不重写。

## 身份和构建

产品显示“一个壳子”；runtime AShell、crate/library ashell、Flutter内部包flutter_hbb、Android me.ashell、scheme ashell不变。Rust 5.2.1 / Flutter 5.2.1+59；SO libashell.so、DLL ashell.dll。

本地入口build.sh/build.cmd；手动CI保留Build-Clients.yml与Build-Clients-New.yml，新工作流BuildMode默认“常规编译”，另有“原始编译”。准确平台/架构、工具链与签名见[构建说明](../BUILD_CLIENTS_ACTIONS.md)。本任务不执行编排，不把旧dispatch接受记录当作构建通过。

## 当前ADB实现

| 板块 | 源码锚点 / 约束 |
|---|---|
| 配对/连接/执行 | LocalAdbManager、AdbPortDiscovery、AdbShell、LocalAdbCommands；双NSD、UID验证、独立2路命令 |
| 视频 | AdbVideoCapture复用scrcpy 5.0.1 ScreenCapture；AdbVideoSurface独立GPU桥接到MediaCodec |
| 截图 | AdbScreenshotCapture常驻UiAutomation；LocalAdbScreenshot兼容raw/PNG；JPEG只驻内存 |
| 穿透 | AdbLayoutCapture/Nodes/Snapshot只取节点，AdbLayoutRenderer在App绘制；数字红色 |
| 遮罩 | LocalAdbPrivacy、AdbPrivacyOverlay；黑buffer层、App预绘白字PNG，排除自身采集需ROM实测 |
| 尺寸/画质 | AdbCaptureProfile width/350整数缩放；4096单边/4Mi像素；remote_adb_video读取实时VideoQoS，独立编码 |
| 输入/剪贴板 | LocalAdbTouch + LocalAdbKeyboard；全局ADB优先、Ctrl+V/远程输入、paste=false同步，原无障碍保留 |
| 同步/传输 | 字段40请求/状态、41独立媒体/触摸；每PC有界队列；二进制本机帧桥；不写原VideoFrame/VIDEO_RAW |
| PC UI | 双源普通左ADB右/单源居中；本地Texture恢复；状态分组；Shell栏无悬浮说明，按钮“点亮屏” |
| 常亮/保活 | KeepScreenOnState默认core service-on；无新增入口。LocalAdbKeepAlive只申请本应用电池优化豁免 |

官方scrcpy-server固定5.0.1、733930字节，SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`；资产不变。libadb-android 3.1.1及既有依赖保持，不引入PC adb.exe、旧libadb.so或额外字体。

## 认知恢复规则

当前合同为[12](../AI_ENGINEERING/12_LOCAL_ADB.md)、[13](../AI_ENGINEERING/13_REMOTE_ADB.md)、[14](../AI_ENGINEERING/14_ADB_MIRROR.md)，决策D-033。撤回的后续方案不作为当前能力或根因；任何后续修复先重新读实际代码和收集复现，不按旧聊天自动恢复改动。文档提交可改变HEAD，不改变本基线的实现含义。

验证重点：本地/远程配对后UID、双源开关独立性、普通单栏恢复、仅ADB输入、多PC争用/取消、黑屏排除、节点/字体、旋转、传输性能、Release/JNI反射。参数对齐不证明两种编码的画质、帧率、带宽相同；Android11—17是验收目标，非逐版本通过声明。
