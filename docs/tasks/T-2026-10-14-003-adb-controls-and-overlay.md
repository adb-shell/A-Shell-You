# T-2026-10-14-003 — ADB 控件恢复与独立黑色遮罩

- T0：main/941b215，初始 clean；Baseline17 `AS-BL-2026-10-14-a0e46b1-adb-inline-worktree` 的已提交后继。
- 请求/C2：状态悬浮移到主窗口右上；两幅图靠近中线；排查关闭按钮失效；Shell栏增加开/关黑屏。用户补充明确采用遮罩，不关闭物理显示。
- 边界：仅PC布局与独立ADB模块。原Android MediaProjection、无障碍、普通输入、品牌、依赖保持。无本轮Git/CI/build/test/analyze/codegen授权。
- T1—T4：Flutter/Android/Rust/Network跨层实施，Security/Release静态审查；使用对应AShell skills。独立SurfaceControl遮罩必须排除截图/镜像；不支持时失败退出，不用FLAG_SECURE或物理熄屏替代。
- T5：实现完成。事件 CE-20261014-T003-01，D028/ADR0023/Baseline18。
- 验证计划：V0链路/差异/生命周期检查。正式需Android/Windows构建、两PC并发开关、快速取消/重启、模式切换、遮罩下各采集源与旋转、断线清理。
- 文档/资产：实现后同步14_ADB_MIRROR与任务记忆；不新增外部二进制资产。

## 交付与证据

- PC状态浮层移到主界面右上；双源向中线靠近，1像素分隔，单源居中。普通画布/滚动条与ADB点击坐标同步。
- 源码发现私有libadb close无限join、stopping禁用关闭、native请求缺超时、旧帧额度不重置。补有界close/停止守卫/epoch隔离/可重试关闭/本机桥超时；拒绝请求回状态。故障未在真机复现，不声称已证明唯一原因。
- 遮罩：独立shell UID color layer，仅排除自身采集；Android11元数据/12+skip标记，缺接口失败退出。多PC状态推送，任一PC关闭，最后订阅退出或ADB断开清理；本机租约10秒无心跳退出。无物理熄屏，无原无障碍改动。
- 新字段17—19与主入口R8保留；未改生成桥/构建依赖/版本/身份。视频jar使用单份模块缓存，避免旧清理误删新实例；触摸实现未改。
- V0：源码调用链、协议字段/权限/大小、JNI签名/R8、生命周期/取消/旧Run、显示与点击偏移、原链差异及文档引用检查。ADB-M13—M16均NOT_RUN。
- 安全审查：新动作沿用认证普通会话+控制权限；请求不接受脚本、路径或图层参数。helper只运行已安装APK固定入口，以进程/ADB连接限定寿命。OEM隐藏API、各画面源实际排除效果和清理时序需要V3/V4；不绕过受保护内容。
- Release/External评估：工具链/依赖/官方scrcpy资产hash不变，自编helper随APK；正式编译/真机待验证。未Git写入/CI/产品执行。
- 《编译验证需求》：Android正式Linux环境仓库根执行`./build.sh 1`；Windows正式环境`C:\Code\A-Shell-You`执行`build.cmd`。核验Java/协议/R8/Dart，随后运行ADB-M13—M16及原链回归；本轮未执行。
- T6—T8：`git diff --check`通过；协议41共19字段、无编号重复；114个本地Markdown引用无缺失；原Android Kotlin/采集JNI/输入路由、依赖与构建脚本无差异。状态浮层仅显示，点击穿透到对应画面，避免右侧浮层误走左侧坐标。最终30个本任务路径，HEAD仍941b215；源码与记录交付，V0不等于功能验收。

## 同任务提交与编排授权

- 用户续接明确要求提交推送并执行编排。目标GitHub adb-shell/A-Shell-You/main；当前30路径均属本任务。只读核对本地/远端同为941b215，暂存区初始为空，diff检查通过，Build AShell clients active。
- 执行当前修改stage/commit/push后，单次dispatch `platform=both` / `android_arch=aarch64`，沿用不监控边界。无本地build/test/analyze/codegen，不发布Release/tag。
- CE-20261014-T003-02 / STATE006 / WORK008；D028/ADR0023/Baseline18保持。此为执行前记录，实际SHA/HTTP接受回执见Git与会话交付；接受编排不等于编译通过。
