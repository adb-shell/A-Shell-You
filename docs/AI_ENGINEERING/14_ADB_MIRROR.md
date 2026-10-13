# AShell ADB 投屏与触摸优先级

2026-10-14，T-2026-10-14-002，main/a0e46b1上的未提交扩展；V0，**未编译、未真机验证**。见[本轮任务](../tasks/T-2026-10-14-002-adb-inline-and-screenshot.md)、[ADR-0022](../ADR/0022-inline-adb-screenshot.md)、[Baseline17](../BASELINE/17_ADB_INLINE_SCREENSHOT_BASELINE_2026-10-14.md)。前轮独立投屏/触摸由a0e46b1提交推送，Build AShell clients both/aarch64获HTTP204且未监控；接受请求不表示编译通过。本轮没有新的Git/CI动作。

## 使用与边界

两端更新后，先无线调试配对、连接并验证Shell。PC Shell栏保留返回/主页/多任务，提供“开投屏 / 关投屏”“开截图 / 关截图”，沿用无障碍栏的蓝色开启、红色关闭按钮样式。ADB画面已并入主界面：单路画面居中；普通画面与ADB都存在时，左普通、右ADB，中间竖分界线；关闭一侧后另一侧恢复全宽居中。原来的ADB浮动窗口外壳与放大按钮已移除，Shell栏仍可拖动、独立隐藏。

“开截图”使用ADB连续系统截图，替换当前ADB视频；“开投屏”可切回scrcpy视频。两模式共用右侧ADB画面位置，均可与普通共享/无视/穿透画面共同接收。黑边不接受触摸，画面支持点击、按住、拖动。任意有控制权限的普通PC可关闭设备当前ADB画面，其他PC同步关闭。单PC离开只取消自己的订阅，最后一个订阅离开释放采集资源；重连不会自动重新开启已停止的画面。

强制截图表示主动、持续通过ADB的系统`screencap`取图，不依赖无障碍权限、MediaProjection或视频编码器；**不能绕过Android的受保护内容/FLAG_SECURE限制**。最长边1280、JPEG质量82、最多2fps，更适合静态画面和视频编码器不可用时；不是高帧率视频模式。

**ADB已连接并验证Shell时，触摸优先使用ADB；否则回到已有无障碍触摸。** 不要求开启ADB视频：原屏幕共享与无障碍截图也使用同一Android输入选择器。接管时只结束无障碍的远程手势、清理延迟点击/滚轮队列，不关闭无障碍Service、权限或截图。关闭ADB投屏保留ADB触摸；断开/撤销ADB同时释放新模块的控制连接与手势。无障碍不存在且ADB不可用时，没有可用触摸路径。

完整键盘/文字输入并未迁移到ADB；Shell栏三键仍经字段40执行固定shell keyevent。本轮仅适配PC展示层的分栏视口和等待提示：原采集/VideoFrame解码/texture/首帧状态不变。ADB有真实帧后仅关闭当前会话waiting-for-image提示，保留配对等弹窗承载层，不用ADB帧伪造普通画面首帧。产品身份、声音、terminal和relay/加密策略保持。

## 链路与所有权

```mermaid
flowchart LR
  PC[PC Shell栏 / 主界面ADB画面] -->|Misc.remote_adb_video 41| HUB[Android Rust订阅/流控]
  HUB -->|独立JNI| MAN[LocalAdbManager]
  MAN --> VID[LocalAdbMirror / 私有ADB连接]
  MAN --> IN[LocalAdbTouch / 私有ADB连接]
  VID --> SV[scrcpy video-only server]
  VID --> CAP[LocalAdbScreenshot / screencap]
  IN --> SC[scrcpy control-only server]
  SV -->|H.264| VID
  CAP -->|JPEG| VID
  VID --> HUB
  HUB -->|每PC有界帧窗口| DEC[独立PC Decoder]
  DEC -->|本机最新JPEG槽| VIEW[Flutter RawImage]
  LEG[原MouseEvent / PointerDeviceEvent] --> ROUTE[Android输入分流]
  ROUTE -->|ADB可用| IN
  ROUTE -->|ADB不可用| ACC[原Accessibility输入]
```

- `LocalAdbManager`继续拥有原身份/配对/串行Shell队列。投屏和触摸各用单独`LocalAdbClient`、连接、线程，不占长期Shell队列。
- `LocalAdbMirror`启动一份video-only服务：H.264、max_size=1280、max_fps=20、2Mbps、目标关键帧间隔1秒；无audio/control/power_on。20秒启动/无帧超时仅停止自身。
- 截图模式复用其私有ADB连接，通过shell-v2执行固定`screencap -p`。单张PNG上限16MiB、stderr上限64KiB、显式退出码必须为0；先检查尺寸（每边<=8192、总像素<=32Mi）再降采样/缩放/编码，JPEG上限2MiB，Bitmap始终释放，无截图落盘。20秒无帧保护会取消私有连接；OOM结束本轮采集。切换模式先停止旧Run，待其finally清理后开启下一generation；显式停止会取消待切换请求。
- `LocalAdbTouch`在Shell验证后准备control-only服务：video=false、audio=false、clipboard_autosync=false、power_on=false。不需要MediaProjection。15秒启动、5秒写入超时；异常时显示控制通道错误，ADB仍可用期间不偷偷向无障碍重复注入。
- `RemoteDisplayLayout`保留普通画布左上原点，仅以可选`CanvasModel`桌面视口宽度控制缩放/居中；点击沿用既有坐标和`SCREEN_INFO.scale`。ADB区域使用本地坐标，由独立几何适配器换算到真实屏幕。旋转不匹配时释放当前手势。普通源按真实video/screenshot状态收起，未知/陈旧状态保留此前画面以兼容重连；两路组件保持挂载、各自接收。
- 触摸队列64项，合并相邻MOVE；溢出释放手势。每次拖动仅一个PC持有手势，10秒无事件自动抬起，其他PC不能松开该手势。退出/只读撤销释放对应owner。此为并发争用策略，并非跨PC同时多指注入。
- ADB区域的DOWN/MOVE/UP按序通过异步本机桥，避免桥接线程并行使按下/抬起乱序；切换peer/销毁后丢弃旧epoch输入。分栏只监听画面有无变化，不由每帧刷新整个主界面。

## 视频协议与背压

官方[5.0.1 Streamer](https://github.com/Genymobile/scrcpy/blob/v5.0.1/server/src/main/java/com/genymobile/scrcpy/device/Streamer.java)使用4字节codec-id、12字节session/frame元数据。最高位标识session，其后config/key位分别62/61；不能按旧scrcpy协议解析。Android有界读取，校验H264、尺寸与2MiB帧、64KiB配置；codec配置随关键帧发送。

`Misc.remote_adb_video=41`含token/action/sequence/generation/尺寸/keyframe/data/state/error/supported与ADB触摸优先级字段；旧40和VideoFrame编号不变。本轮追加format(15，h264/jpeg)和screenshot_supported(16)能力协商，旧空format按H.264兼容；动作增加screenshot，其他watch/unwatch/start/stop/ack/nack/touch/state/frame保持。start/screenshot/stop/touch必须为已认证普通控制会话，touch固定20字节有界整数。客户端不能提供server路径、启动参数或shell文本。

JPEG仅发送给声明支持的PC，旧PC得到更新提示，不送入旧H.264解码器；新PC连接旧Android仍可用H.264，截图按钮不可用。PC先验证JPEG头部尺寸与包尺寸一致（<=1280），再经既有本机JPEG槽给Flutter解码；不再做一遍H.264解码/转码。每张JPEG都是可独立恢复的关键帧，继续使用同一ACK/有界窗口。模式/generation变化清旧图与解码缓存，失败只停止ADB源。

最多32订阅者，每PC最多4帧在途，ACK按实际sequence归还额度。慢PC丢帧后等待关键帧，不堆积编码视频；其他PC独立继续。客户端解码队列8项，最新显示槽只有一帧；停止/generation变化清除旧图。旧页面owner不能取消新页面订阅。慢链路仍与原链共用网络带宽，队列有界不意味着物理带宽隔离。

PC使用现有`hwcodec` RAM解码器的独立实例。跨FRB本地桥暂用JPEG+base64，Flutter直接解码到`ui.Image`并释放上一帧，绕过普通ImageCache。50ms读取的是**本机帧/状态槽**，不恢复已移除的远程3秒status轮询。停止后空闲解码线程在250ms接收超时检查中释放decoder/RGB缓存，订阅保留以便再次开启。Android触摸优先级变化推送会推进Shell栏状态epoch，阻止之前在途的查询回执覆盖新状态。没有改FRB签名或手工修改生成文件。此适配有额外本机编码/解码CPU成本，后续可在独立接口内优化texture；目前不宣称达到scrcpy原桌面性能。

## 外部资产与构建

固定官方[scrcpy v5.0.1](https://github.com/Genymobile/scrcpy/releases/tag/v5.0.1)，733930字节，SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`，Apache-2.0。APK资产目录`flutter/android/app/src/main/assets/adb-mirror/`同时含LICENSE/NOTICE；文件哈希已与官方发布元数据比对，运行前再次校验。没有验证发布签名或重建上游二进制。

推送到两个模块专用固定临时路径，避免反复失败积累随机文件；scrcpy cleanup及shell EXIT trap清理成功启动的文件。中途取消上传可能留下固定临时文件，下次覆盖。正常视频/控制资源各自关闭，不使用killall、不关闭adbd、不关闭原屏幕共享。

Android沿用Java8/minSDK21，但此无线ADB功能仍需Android11+；不升级Gradle/NDK/Kotlin。Windows正式入口与CI已有`--hwcodec --flutter --vram`；现有image crate提供本地JPEG，无新增Cargo依赖。官方server单独打包，未恢复旧libadb.so或PC adb.exe。

## 待验证

TEST_MATRIX的ADB-M01—M12均NOT_RUN：原有ARM64/universal和Windows构建、多PC/背压、旋转/重连、触摸优先级与原链回归，加上分栏中心/点击坐标、只有ADB时菜单弹窗、跨PC切模式/停止、截图异常及新旧版本矩阵。ADB shell验证和控制socket建立不证明所有ROM允许InputManager注入；部分厂商额外调试授权及编码器并发限制需真机确认。不同PC可在下一关键帧/截图加入，不能承诺帧级严格同时显示。

《编译验证需求》：Android正式Linux构建机仓库根`./build.sh 1`；Windows正式环境`C:\Code\A-Shell-You`执行`build.cmd`。目标为新协议字段自动生成、Java JNI参数一致、Flutter分栏与JPEG接收可编译；随后Android11+与两PC执行上述矩阵。本轮未执行本地build/test/analyze/codegen、真机或CI，不能宣称编译/运行通过。
