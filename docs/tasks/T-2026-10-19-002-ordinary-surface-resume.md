# T-2026-10-19-002：关闭ADB画面后的普通画面恢复

## r2：Git与构建交付

- 2026-10-19用户明确要求“推送并构建”：将T001等待提示和T002显示恢复合并提交推送到当前test，单次触发Build-Clients-New / Both / 常规编译，沿用不监控要求。无新业务或工作流修改。
- 执行前本地/远端test同为8e26f0b，15个预期路径，diff检查通过；沿用现有测试签名和Actions下载，不发布。准确提交SHA及dispatch回执见Git/会话交付，触发不等于构建或设备验证通过。
- STATE/WORK-20261019-002-r2 / CE-20261019-T002-02。下方无Git/CI记录属于r1实现阶段；冻结根因和实际效果仍需真机复测。

## T0—T4：授权、基线与诊断

- 用户要求解决双画面时关闭ADB投屏/无视/穿透后普通画面冻结、共享仍开启的问题，保护原传输和功能链。
- 起点 test/8e26f0b66e9cc0f1558371a555c26e9e90744f67；保留T001四份Dart及七份记录的未提交修改。仅本任务源码修复与必要记录授权，无Git/CI/build/test/analyze/codegen/设备操作。
- Owner：ashell-flutter-engineer；同会话按Android、Rust和Security领域核对停止路径与原链边界，无代理。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui，ADR0022/0025/0026保持；覆盖FLT-02/03/04/05/07、AND-02/04、ADB-M07/M10。
- verified：ADB scoped stop只关闭LocalAdbMirror对应私有Run；PC普通TextureModel由RemotePage持有，双/单画面布局不销毁其native纹理。普通Texture事件仅在首次/渲染类型改变时通知Dart；同一texture ID不会触发GetX更新。布局切换只改视口/对齐，缺少普通纹理层的显式恢复。
- inferred：关闭ADB后残留的普通Texture合成层/待消费帧可能无法靠上述一次性通知恢复。没有本轮运行日志或真机复现，不能将此认定为用户设备唯一根因。用户回复尚未尝试重新打开ADB观察普通画面是否恢复。
- 方案：仅双源退回仍有普通帧的单源时，在布局结束后重新挂载普通Flutter Texture层，保持native texture ID/指针/解码器；复用本地通知桥让下一张真实Texture帧再更新该层。合并重复布局事件，校验会话/peer/布局代次，快速重开或关闭页面取消旧回调。无远程刷新、重连、采集重启、权限或模式变更。
- STATE/WORK-20261019-002 / CE-20261019-T002-01；当前T8，显示恢复修复与V0完成，原报告的设备冻结仍待复测。无协议/桥签名/依赖/外部资产变化；沿用原隔离决定，无新ADR。

## T5—T8：实现与验证

- `RemoteDisplayLayout`：双栏退回普通单栏后，post-frame校准视口，检查mounted/布局代次/FFI/PeerInfo/普通帧和ADB状态，再请求本地纹理重绘。快速重开、页面关闭、peer替换使旧回调失效；没有新增timer/listener或远程操作。
- `TextureModel`：会话内repaintRevision只更新Flutter层；同一native texture ID也可生成新Texture render object。收到下一张真实Texture事件后最多再刷新一次；正常后续帧不递增revision，不增加逐帧Dart重建。
- `RemotePage`：Texture的key消费上述revision；不更换FFI、普通ImagePaint、输入组件或native纹理。软件RGBA只保留原视口/帧更新，不走新Texture恢复。
- V0通过：五份Dart（含T001）词法括号检查、diff检查；native纹理创建/释放类及updateCurrentDisplay逐字与HEAD一致；Android/Rust/协议/生成桥/依赖/工作流零差异；T001提示状态和不等待通知桥的命令顺序仍保留。
- 静态复核：覆盖本PC/另一PC停止、三种ADB格式共用hasFrame、快速关开/换源、普通源已关、仅ADB、重连/关窗口和下一帧状态清理。未执行Flutter analyzer、编译、测试或设备操作，以上不是运行PASS。
- 依赖源码只读核对：[固定版本PixelBuffer实现](https://raw.githubusercontent.com/rustdesk-org/flutter_texture_rgba_renderer/42797e0f03141dc2b585f76c64a13974508058b4/windows/texture_rgba.cpp)合并未消费的帧通知；[Flutter 3.24.5 TextureBox](https://raw.githubusercontent.com/flutter/flutter/3.24.5/packages/flutter/lib/src/rendering/texture.dart)通过合成层绘制。它们支持检查本地纹理恢复路径，但不能替代设备日志证明冻结原因；未更新依赖。
- 回滚只撤本任务布局恢复、Texture revision/key增量，保留T001等待提示。当前合同、原隔离决定补注与状态/任务索引同步；未提交、推送或编排。

## 编译验证需求（未执行）

- 命令/环境：正式Flutter 3.24.5环境，在`flutter/`运行 `flutter analyze lib/desktop/adb/remote_display_layout.dart lib/desktop/pages/remote_page.dart lib/models/desktop_render_texture.dart`；正式Windows构建机仓库根运行 `build.cmd`，或另行授权现有Windows Actions。
- 目标：普通共享持续显示动态内容，依次开/关ADB h264、jpeg、layout，各重复多次；普通画面恢复居中并继续更新。覆盖PixelBuffer/GPU和软件RGBA、快速关开、模式切换、另一PC关闭、普通源已关/仅ADB、窄窗/原始缩放/滚动、重连/关窗口；不得出现新的共享授权、无视自动开启或普通解码重启。
- 未获得复现日志或实测前，不声明冻结根因已完全确认或设备回归通过。
