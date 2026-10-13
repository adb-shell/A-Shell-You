# T-2026-10-14-002 — 主界面双画面与 ADB 连续截图

2026-10-14续接授权：用户要求“推送并执行编排”，授权提交本任务全部代码/文档并推送GitHub main，触发一次Build AShell clients（both/aarch64），沿用不监控边界。复核本地/远端main均a0e46b1，35个待交付路径均为本任务，V0差异检查通过。本段为执行前记录，最终SHA与dispatch接受结果见Git/会话回执；不代表构建成功。CE-20261014-T002-02，以下“无Git/CI”是此前代码交付时点。

## T0—T4 / Authority, requirements and design

- Status：complete / T8（源码与文档交付，正式验收待验证）；2026-10-14；ashell-master，Flutter/Android/Rust/Network主责，Security/Release静态自审；无子agent。
- Source：main/a0e46b10c27c7e6a407c5a61d9e7012b785f99c6，initial clean。前轮已推送，Build AShell clients both/aarch64获204且按要求未监控；不推断编译通过。
- Baseline：父AS-BL-2026-10-14-a1486ee-adb-mirror-worktree（16），本任务快照AS-BL-2026-10-14-a0e46b1-adb-inline-worktree（17）；D-027 / ADR-0022。
- 用户明确要求：ADB画面并入主界面；单路居中，双路普通画面左/ADB右、中心竖分界；Shell栏按钮仿无障碍栏；增加独立ADB强制连续截图传输。
- C2授权布局/视口适配、独立ADB模式/协议/采集及事实文档；不改原Android采集/无障碍Service、产品身份/工具链、relay或触摸优先级。本次没有新的Git/编排/本地构建授权。
- 方案：原画布仅增加可选桌面视口尺寸，保持左上原点与输入坐标；真实原状态确认全关后收起旧画面，不用ADB状态伪造原首帧。ADB inline RawImage保留独立输入/解码。Android私有ADB连接用shell-v2逐次执行固定screencap -p，验证退出码、有界PNG读取/降采样/JPEG传输，默认最多2fps；与H264模式互斥，保留多PC/ACK/global-stop。
- 边界：强制指主动持续系统截图、不依赖MediaProjection/Accessibility或视频编码器，不绕过Android保护层/FLAG_SECURE；不承诺隐藏受保护画面可见。无新增二进制/依赖，旧PC能力协商隔离JPEG。

## T5—T8 / Delivery record

- 布局：RemoteDisplayLayout两路保留挂载，普通视口左上原点不变，仅变可用宽度；按真实video/screenshot与ADB帧状态决定分栏，单路居中。浮动ADB窗口的拖动/放大外壳移除，独立触摸/解码保留。
- 弹窗：ADB首帧只关闭本会话waiting提示；原Overlay保留，其无内容层在ADB有帧时可穿透，不影响配对/命令弹窗；不改原waitForFirstImage与定时器。不让每帧或ADB显隐重建整个RemotePage而触发原dismissAll。
- 采集：LocalAdbScreenshot固定shell-v2/screencap，检查退出、尺寸、字节上限，降采样/JPEG82/1280/最多2fps；取消、OOM、20秒超时和切换清理只作用于私有ADB源。原Shared Shell不承载长期采集。
- 协议：41不改号，新增15/16字段及screenshot动作；JNI末尾boolean两端一致，R8规则覆盖；旧PC不接JPEG，新PC对旧Android禁用截图。沿用token/generation/ACK/owner隔离及多PC停止。
- V0：源码接口/布局/坐标/生命周期与权限核对，RemoteAdbVideo共16字段无重复；diff空白检查通过；受保护的原Android采集/无障碍Service、JNI兼容模块、输入路由、构建/依赖路径与HEAD一致。文档本地链接检查通过。无产品build/test/analyze/codegen、真机或新Git/CI。
- 记录：14_ADB_MIRROR、ADR0022/D027、Baseline17、ADB-M09—M12、当前工程文档与memory同步；CE-20261014-T002-01，STATE003/WORK004。本轮代码/文档待提交，运行正确性和UI效果仍待正式验证。

《编译验证需求》：正式Linux仓库根执行`./build.sh 1`，正式Windows环境`C:\Code\A-Shell-You`执行`build.cmd`；验证新增protobuf生成/Java JNI参数/Flutter分栏和JPEG编译。Android11+与两PC执行ADB-M01—M12，重点单/双源坐标、只有ADB时弹窗、跨PC模式切换与关闭、原链回归。本轮未执行这些命令。
