# ADR-0022: Inline ADB display and independent screenshot source

- Status：accepted for implementation；Record Type：contemporaneous。
- Decision Date / Recorded Date / Last Reviewed：2026-10-14；Next Review：正式构建/双PC真机验收时。
- Decision Owner / Approver：项目owner，当前用户明确要求；Security / Release Reviewer：Codex按领域Skill静态自审，无独立运行验收。
- Scope：PC分栏、Shell栏按钮、独立ADB连续截图及格式协商。
- Task / Decision：T-2026-10-14-002 / D-027。
- Baseline / Tests / Assets：AS-BL-2026-10-14-a0e46b1-adb-inline-worktree；ADB-M01—M12；EXT-BIN-SCRCPY-001未改变。
- Supersedes：0021的PC浮动展示部分，其独立媒体/触摸与多PC规则继续有效；Superseded By：none。
- Implementation：uncommitted worktree；Evidence：V0，V2—V4 NOT_RUN。

## Context and decision

用户要求一个主界面接收普通与ADB画面，并增加不依赖无障碍的连续截图。选择`RemoteDisplayLayout`：单源居中，双源普通左/ADB右、中心1px分界；关闭一源恢复单源。普通画布仅增加可选桌面视口尺寸，左上原点不变，ADB区域单独显示和输入，不喂入原VideoFrame/texture/首帧状态。只关闭ADB首帧已替代的waiting提示，保留其他弹窗Overlay。

Android私有ADB连接执行固定`screencap -p`，用shell-v2二进制帧读取PNG，受限解码为最长边1280的JPEG，最多2fps，通过字段41现有订阅/ACK通道分发。视频与截图为同一ADB画面的互斥模式；切换先释放旧Run，任一控制PC停止对所有PC生效，最后订阅退出停止采集。

Shell栏保留三导航键和可拖动容器，增加两对开/关按钮，沿用无障碍栏颜色、圆角、字号。原Android采集/无障碍Service及触摸优先级不变。

## Alternatives and consequences

| 方案 | 收益 | 代价 / 选择 |
|---|---|---|
| 保留浮动窗口 | 主画布零适配 | 不符合用户要求，不采用 |
| ADB帧送入原视频链 | 复用单一画布 | 覆盖原图、耦合首帧/权限，不采用 |
| 分栏 + 独立截图源 | 两路接收，不依赖视频编码器截图 | 采用；增加有限视口适配、图像转换成本和共享带宽占用 |

连续系统截图是低帧率模式，有PNG采集/JPEG转换CPU与内存成本。无需无障碍不等于绕过Android保护层/FLAG_SECURE；不承诺ROM兼容或性能。

## Protocol, security and build

- RemoteAdbVideo追加format(15)、screenshot_supported(16)和screenshot动作；空format兼容旧H.264。新Android不给旧PC发JPEG，新PC连接旧Android禁用截图；原40/41编号与FRB签名不改，构建时按现有流程生成protobuf，无持久配置迁移。
- 已认证普通会话才能订阅；启动/截图/停止/触摸要求控制权限。命令固定，不接受peer命令/路径参数，截图不落盘、不写日志，身份仍由Android共享manager持有。
- PNG<=16MiB、stderr<=64KiB、每边<=8192且像素<=32Mi，先探测尺寸再降采样；JPEG<=2MiB且每边<=1280；每PC在途4帧、客户端队列8帧，20秒无帧停止私有源。
- Java8/minSDK、Gradle、Rust/image/hwcodec、Flutter、server资产/许可及CI入口不变，无新外部文件。截图不用scrcpy视频编码器；触摸沿用既有control-only服务。

## Verification and reversal

V0核对producer/consumer、JNI/R8签名、旧端格式、取消/切换所有权、分栏坐标、差异范围和文档链接；ADB-M09—M12覆盖单/双源、弹窗、旋转/缩放、双PC切模式、失败恢复/旧版本；V2—V4均待正式环境。

立即停止：任意控制PC关闭当前ADB模式，不影响普通采集或共享Shell。源码回退需后续Git授权，回退本任务扩展至a0e46b1；无不可逆数据迁移，回退后复核原视频/触摸矩阵。

## Approval and references

2026-10-14：用户明确要求双画面、Shell栏样式及强制连续截图；本轮仅实施该范围，无新的Git/CI授权。

[当前合同](../AI_ENGINEERING/14_ADB_MIRROR.md)、[任务](../tasks/T-2026-10-14-002-adb-inline-and-screenshot.md)。官方参考：[AOSP screencap](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/main/cmds/screencap/screencap.cpp)、[image 0.24.9 JPEG decoder](https://github.com/image-rs/image/blob/v0.24.9/src/codecs/jpeg/decoder.rs)。独立实现，未复制额外源码/二进制。
