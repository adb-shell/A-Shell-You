# T-2026-10-19-001：PC Android画面区域内等待提示

## T0—T4：授权、基线与设计

- 用户要求移除PC连接Android后的“已连接，等待画面”弹窗，在普通画面区域显示“已连接，等待画面传输”；开共享/无障碍无视/穿透在等待后续画面时显示小字，画面立即到达则立即结束，不刻意延长展示。用户补充：不得破坏画面传输或功能链路。
- Source：test/8e26f0b66e9cc0f1558371a555c26e9e90744f67，初始clean。C2仅上述PC等待显示及必要本地通知处理、C1最小记录；无本轮Git/CI/build/test/analyze/codegen/设备/发布授权。
- Owner：ashell-flutter-engineer；同会话检查Security权限边界和Rust现有通知桥语义，无代理。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui，ADR0022/0025/0026隔离规则保持；TEST_MATRIX AND-02/04/05、FLT-02/03/04/05/07、ADB-M07/M10。
- STATE/WORK-20261019-001 / CE-20261019-T001-01；风险low，当前complete/T8（实现与V0），正式构建及交互仍需V2/V3。External Asset：N/A，无依赖或资产增量；Decision/ADR：不新增架构决定，原core/share/frame/waiting边界保持。
- Impact Map：FfiModel会话内等待状态→RemoteDisplayLayout普通画面区域；InputModel原显式开关→仅记录等待意图→既有sessionSendMouse；RGBA/Texture→原onEvent2UIRgba→清理提示。ADB帧不清理普通首帧状态。
- 已核对：Texture事件只在渲染类型首次/变化时通知；现有sessionOnWaitingForImageDialogShow只重置本地notify_render_type，允许下一张真实Texture帧通知，不请求远程采集或重建纹理。
- 不采用固定展示时长、清空旧画面、重置waitForFirstImage、额外采集/刷新或新增协议。控制命令不等待UI通知桥，保持原发送顺序；通知失败不阻止命令。

## T5—T8：实现与验证

- 四份Dart源码：models/model.dart、models/input_model.dart、desktop/adb/remote_display_layout.dart、desktop/pages/remote_page.dart。Android native、Rust、网络/协议、生成桥、依赖和编排零修改。
- PC普通Android会话收到真实peer info/connected-wait事件后显示区域内提示；移动端、Web和摄像头等其他路径保留原行为。连接/密码/错误对话框保持；等待层不阻挡PC操作。
- 本PC显式开共享/无视/穿透时记录请求后的帧等待。已确认开启且已有普通画面则不强行展示；关同一模式取消，切换请求覆盖旧提示，异步失败只清理对应generation，重连/错误/关闭清理会话提示。
- 不把点击视为功能开启成功，不改AShellStatusData。提示表达“已请求、待普通链后续帧”，普通视频协议没有每帧来源标签，不声称收到的帧能证明切换模式成功；实际功能状态仍使用Android原状态包。
- 任意真实普通RGBA/Texture事件立即清理提示，无最短展示时长/新timer。保留旧帧；普通等待与ADB画面分栏，ADB单源启动/等待保持自身提示，不伪造普通首帧。
- V0通过：四份Dart源码及HEAD基线词法括号检查、diff检查；原控制命令编码区段与权限/ADB路由逐字一致；现有Future<void>桥签名和本地重置语义核对；RGBA/Texture同一清理入口、generation取消、已开启保护、无新增timer/首帧重置/采集调用。词法检查不等于Dart analyzer或编译通过。
- 静态生命周期复检覆盖：初次连接无帧、立即来帧、慢帧、重复点击已开启功能、快速开/关/切模式、旧失败回调、RGBA/Texture、断线/关闭、ADB-only/双源。实际网络/渲染交互未运行，不预写PASS。
- 当前Android合同与五份任务/状态索引同步；无Git/CI或产品执行，不继承T010的构建授权。回滚只撤本任务四份Dart显示增量，不迁移远端状态或用户数据。

## 编译验证需求（待授权）

- 命令：正式Flutter 3.24.5环境，在 `flutter/` 执行 `flutter analyze lib/models/model.dart lib/models/input_model.dart lib/desktop/adb/remote_display_layout.dart lib/desktop/pages/remote_page.dart`；正式Windows环境仓库根执行 `build.cmd`，或经另行授权触发现有Windows Actions。
- 验证：未开共享连接无弹窗且显示小字；快速首帧不强制停留；网络慢时开共享/无视/穿透提示直到真实帧；原按钮/输入/画面传输与切换行为保持；只读、重复开、快速开关、失败/关闭和重连无残留提示。
- 覆盖软件RGBA、PixelBuffer/GPU Texture、普通单源、仅ADB和双源，以及短断线60秒保留旧帧。两端无需协议或Android实现升级；本轮尚无编译/真机通过证据。
