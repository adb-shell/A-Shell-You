# AShell inline ADB and screenshot baseline

- Baseline ID：AS-BL-2026-10-14-a0e46b1-adb-inline-worktree。
- Source：main/a0e46b10c27c7e6a407c5a61d9e7012b785f99c6 + T-2026-10-14-002工作树；初始clean。
- Parent：[Baseline16](16_ADB_MIRROR_BASELINE_2026-10-14.md)，前轮由a0e46b1提交推送；历史Build AShell clients both/aarch64获204，未监控，构建结果unknown。
- D-027 / ADR-0022：单源居中、双源左右分栏、Shell栏同款按钮、ADB连续系统截图，视频/截图互斥，多PC共享及全局停止。
- 源码：remote_display_layout.dart、PC ADB视图/状态/侧栏、RemotePage/CanvasModel视口；Android LocalAdbScreenshot/LocalAdbMirror/manager；Rust remote_adb_video。
- 协议：Misc字段41不改编号，内部追加format(15)、screenshot_supported(16)、screenshot动作；JPEG不接入原VideoFrame，FRB签名不改。
- 原Android采集/无障碍Service/输入路由、身份、版本、依赖、工具链、构建脚本/编排保持；无新资产，scrcpy 5.0.1原SHA/许可继续适用。
- 资源：固定screencap命令、PNG<=16MiB、有界尺寸/像素、JPEG<=2MiB/1280、最多2fps、20秒无帧中止；不能绕过受保护内容限制。
- Evidence：V0源码/接口/范围/文档核对；ADB-M01—M12 / V2—V4 NOT_RUN。本轮无Git写入、本地build/test/analyze/codegen、CI或真机操作。
