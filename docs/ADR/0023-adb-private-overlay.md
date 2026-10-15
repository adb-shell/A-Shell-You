# ADR-0023 — 独立 ADB 黑屏遮罩与采集隔离

后继说明（2026-10-16）：[ADR-0026](0026-adb-app-side-rendering.md) 补充已有白字层的App侧PNG预渲染及真实layerStack绑定；下方保留最初黑层方案时点，当前实现以14_ADB_MIRROR为准。

- 状态：accepted for implementation / V0；2026-10-14，T-2026-10-14-003，D-028。
- 请求：手机上层黑色遮罩，PC仍可看见底层真实画面；用户明确排除关闭物理屏幕的替代方案。
- 决定：新增APK内`AdbPrivacyOverlay.main`，经已验证的本机ADB以shell UID运行；只创建独立SurfaceControl黑色color layer。Android11使用平台`WINDOW_TYPE_DONT_SCREENSHOT`创建元数据；12+使用`setSkipScreenshot(true)`，先设排除属性再show/apply。不设FLAG_SECURE，不更改电源/亮度，不复用或修改无障碍黑屏。
- 所有权：`LocalAdbPrivacy`私有ADB连接/线程，设备单例；协议41追加privacy字段17—19与black_on/black_off。已认证普通控制PC可操作；所有订阅PC共享状态，任意控制PC可关闭。关投屏不关遮罩；最后订阅离开、ADB断开/撤销时关闭。
- 恢复：helper读取本机stdin租约，10秒无心跳自行退出，Binder死亡释放图层；不是PC周期查询。启动/退出有界；异常清理期间保持stopping，等待租约后再允许开启。独立JNI不进入原采集JNI；helper入口与native callback由R8保留。
- 替代：普通View遮罩可能被一起采集，FLAG_SECURE会遮黑采集内容，物理熄屏不符合用户补充；均不使用。平台接口缺失失败退出，不开启普通遮罩兜底。
- 风险：依赖Android隐藏API和OEM实现。源码符合AOSP契约，不证明每台设备的物理显示、截图与录屏结果；受保护内容仍不可绕过。需要Android11/12+/不同ROM、正常共享/无视/穿透/ADB视频/截图、多PC、横竖屏和断链实测。
- 来源：[Android11 SurfaceControl](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-11.0.0_r1/core/java/android/view/SurfaceControl.java)、[Android12 SurfaceControl](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-12.0.0_r1/core/java/android/view/SurfaceControl.java)、[Android11 SurfaceFlinger](https://android.googlesource.com/platform/frameworks/native/+/android-11.0.0_r1/services/surfaceflinger/SurfaceFlinger.cpp)。实现自行编写，无新第三方二进制。
- 验证：ADB-M13—M16 NOT_RUN；只完成V0。移除本模块入口/新增字段使用即可回退，原无障碍/采集实现不依赖本模块。
