# T-2026-10-18-009：ADB媒体参数对齐

## T0—T4：授权、基线与设计

- 用户要求尝试完全对齐普通视频的参数，修改后提交推送并编排。起点 test/66d0657，clean；沿用 origin/test、Build-Clients-New/Both/常规编译，单次触发、不监控。不本地产品构建、不操作设备。
- Owner：ashell-master；Android/Rust/Network，Security/Release约束复检，无代理。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0025/0026/D031隔离边界保持。TEST_MATRIX ADB-M01/02/04/07/20/21/23/24。
- 原普通视频按width/350整数缩放，使用VideoQoS实时fps/ratio与scrap硬件编码码率公式；ADB此前单独像素码率与固定30fps、额外1280缩放。scrcpy 5.0.1 SurfaceEncoder没有运行时更新参数接口，不能只重新读取JNI参数就宣称生效。
- 方案：只在ADB中复用scrcpy现有ScreenCapture，独立MediaCodec/GPU送帧实现运行时参数更新；读取普通QoS而不写回；共享原硬件码率计算并按ADB真实source scale校正，避免依赖普通共享是否启动。采集/解码/开关/字段41与原链隔离；无视300ms、穿透80ms保持真实采集节奏，JPEG与H.264不能宣称编码结果相同。
- 风险：GPU/编码器与ROM兼容需真机；双画面仍共用性能和带宽；设备编码对齐可能造成数像素差异。只复用仓内固定hash的scrcpy jar，不升级或新增依赖；失败如实报告。回滚本任务提交。

## T5—T8：实现、复检与交付

- 新增AdbVideoCapture/AdbVideoSurface：保留已校验的scrcpy ScreenCapture兼容层；硬件编码、GPU纹理传递、编码前实时送帧节奏，无CPU画面回读/视频转码。App每秒送SHPF参数并续租；helper更新码率而不反复重启，仅尺寸变化重建编码器。帧包保留scrcpy H.264格式，字段41/PC解码状态不变。
- AdbCaptureProfile/JNI对齐整数缩放、当前VideoQoS fps/ratio、base_bitrate与普通编码选择对应的硬件预算；按source scale替代普通SCREEN_SIZE的倍率，不改变SCREEN_SIZE。无视300ms/布局80ms下限加实时帧率约束，JPEG参数实时更新。取消额外1280缩小；Java/Rust/Dart统一4096边长、4Mi像素边界，编码器仍做必要尺寸对齐。
- 代码范围：localadb新增两类、AdbCaptureProfile/LocalAdbMirror/LocalAdbLayout、proguard入口；src/remote_adb_video.rs；独立ADB Dart画面与native buffer校验。没有修改原MainService、Accessibility、原视频服务/QoS、原输入、生成桥、protobuf、依赖、构建脚本或scrcpy二进制。active pkg2230/compat ffi检查：本次独立JNI入口无两者增量。
- V0：diff/源码资源生命周期与取消、JNI三参数和R8、Java/Dart词法括号检查；直接读取仓内scrcpy classes.dex核对反射类/方法描述符，hash仍764eb6f7…536。没有执行本地build/test/analyze/codegen或设备操作；硬件兼容/真实流畅度/两路同时打开带宽竞争仍待测。
- 验证需求：Build AShell Clients-New，ref=test，Platform=Both，BuildMode=常规编译；按授权单次触发不监控。真机检查Android11—15、仅ADB/双源、旋转、网速变差/恢复、质量档位、两个PC加入退出、连续开关、静止画面新PC首帧、黑屏排除及触摸。旧PC超过1280尺寸不支持，需两端更新。不能把JPEG与视频称为相同编码结果或承诺实际帧率完全一致。
- 最小14合同与五份记忆同步；Decision/ADR：仍是独立采集/编码/传输模块，现有隔离决定不变；外部资产版本与hash保持，仅使用其既有接口。准确提交/推送/dispatch回执见交付，不预写构建成功。

## 只读依据

- [scrcpy 5.0.1 ScreenCapture](https://github.com/Genymobile/scrcpy/blob/v5.0.1/server/src/main/java/com/genymobile/scrcpy/video/ScreenCapture.java)、SurfaceCapture/VideoConstraints/CaptureControl及Options，接口另与本地DEX核对。
- [Android MediaCodec](https://developer.android.com/reference/android/media/MediaCodec)：PARAMETER_KEY_VIDEO_BITRATE与动态参数；[SurfaceTexture](https://developer.android.com/reference/android/graphics/SurfaceTexture)：独立Handler回调和GL纹理更新。
