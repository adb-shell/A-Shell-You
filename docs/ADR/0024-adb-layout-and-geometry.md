# ADR-0024: Independent ADB layout capture and physical geometry

后继说明（2026-10-16）：[ADR-0026](0026-adb-app-side-rendering.md) 将下方“helper绘制JPEG”更新为“helper只读节点、App绘制JPEG”，其余隔离/几何原则保持。下方为原决定的历史实现说明。

- Status：accepted for implementation，2026-10-14；D-029，T-2026-10-14-004。V0，不代表构建或真机通过。
- Context：用户要求Shell栏严格参考无障碍栏、降低ADB画面负载、修复黑屏/间歇触摸，并新增节点线条/文字穿透。
- Decision：ADB画面三种互斥源h264/jpeg/layout，继续单设备拥有、多PC订阅及既有控制权限；layout由APK内独立shell UiAutomation helper绘制JPEG，不调用产品AccessibilityService。连接使用`FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES`，禁止默认connect抑制服务，禁止`uiautomator dump`落盘/反复注册。
- Geometry：采用普通采集的当前宽度整数除350规则，下限1，ADB仍保留1280边长上限；source_width/source_height单独传递实际显示几何，避免H.264对齐后尺寸承担显示比例/点击坐标。读取原QoS画质档位与FPS，不修改它；ADB保留独立有界码率与JPEG编码，不能把不同codec描述为字节/质量完全相同。
- Input：私有触摸连接有界清理、自动退避重连；PC合并相邻MOVE，保留DOWN/UP、限定队列及桥超时，递增本地sequence拒绝过期桥调用。原无障碍触摸优先级规则不变。仅自身黑色SurfaceControl标可信，不修改全局触摸遮挡保护。
- Protocol：字段41追加20(layout_supported)、21/22(source dimensions)，旧PC只收到可解码格式/更新提示；新PC对旧端source=0继续按编码尺寸点击。JPEG上限2MiB、编码边长1280、源边长16384；不新增脚本参数输入。
- Lifecycle：layout节点/图像仅内存、4096节点/64层/2fps上限，租约10秒及20秒无帧守卫，停止或最后PC离开释放私有进程连接。密码节点文字遮盖；没有节点的图形内容不能还原。不能与另一个系统UiAutomation实例同时占用；失败只反馈ADB模式错误。
- Alternatives：修改原AccessibilityService/SKL违反隔离要求；默认UiAutomation connect会抑制原服务；纯截图无法提供节点线条视图，因此均不采用。
- Rollback：恢复404b199的本任务差异即可回到两种ADB源；旧字段编号/品牌/依赖保持，不执行该Git动作。
- Verification：ADB-M17—M20待V2/V3/V4；Android11/12+/OEM、仅ADB/双权限、多PC、旋转/缩放、黑屏与原源回归。上游依据：[UiAutomation flags](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-12.0.0_r1/core/java/android/app/UiAutomation.java)、[SurfaceControl trusted overlay](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-12.0.0_r1/core/java/android/view/SurfaceControl.java)、[scrcpy control-only coordinates](https://github.com/Genymobile/scrcpy/blob/v5.0.1/server/src/main/java/com/genymobile/scrcpy/control/Controller.java)。仅参考接口、独立实现，没有新增外部binary。
