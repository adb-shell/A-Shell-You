# T-2026-10-19-003：移除无障碍栏功能启动提示

## r2：提交与编排授权

- 用户追加要求“提交并构建编排”；提交推送test后单次触发Build-Clients-New.yml，Platform=Both、BuildMode=常规编译，沿用不监控要求。
- 执行前本地及远端test均2bbdf50，10路径均为本任务三份Dart与必要记录，diff检查通过；没有额外源码修改或本地产品验证。
- STATE/WORK-20261019-003-r2 / CE-20261019-T003-02；准确提交SHA与dispatch回执见会话交付，不将dispatch接受视为构建通过。下方保留原实现阶段授权及验证范围。

## T0—T4

- 用户要求移除无障碍功能启动时的小字，保护功能和传输链。test/2bbdf50f5deb118b253771e8e9d78566d370f6b6，初始clean；本轮仅编辑及V0，无Git/CI/build/test/analyze/codegen/设备授权。
- Owner：ashell-flutter-engineer，同会话复核权限/桥边界，无代理。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui、ADR0022/0025/0026；FLT-02/04/07、AND-02/04、ADB-M07/M10。
- 移除T001添加的开共享/无视/穿透提示文案、专用展示状态和InputModel提示钩子；恢复原sessionSendMouse直接发送。连接后等待首帧的小字、ADB自身提示、T002普通纹理恢复保持。
- STATE/WORK-20261019-003 / CE-20261019-T003-01；无架构/协议/身份/依赖/外部资产变化，Decision/ADR reviewed-no-change。

## T5—T8

- 三份Dart移除提示文案、枚举/展示状态、开关钩子及其本地Texture通知调用；原连接等待状态和真实帧清理保留。InputModel恢复直接发送，功能权限/命令编码/错误传播保持。
- V0通过：InputModel与8e26f0b添加提示前实现忽略空白后完全一致；全Dart无残留专用状态/启动文案；T002布局恢复方法逐字保持，native纹理/Android/Rust/协议/ADB实现无差异；词法括号和diff检查通过。
- 完成T8（源码及V0），非编译/真机通过；未提交、推送、编排或执行产品验证。回滚可从起点提交恢复这三份Dart增量；当前Android合同和任务索引同步，无新Decision/ADR/外部资产。

## 编译验证需求（未执行）

- 正式Flutter 3.24.5环境，在flutter目录执行 `flutter analyze lib/models/input_model.dart lib/models/model.dart lib/desktop/adb/remote_display_layout.dart`；正式Windows环境仓库根执行 `build.cmd`，或另行授权Windows Actions。
- 验证：开共享/无视/穿透不再显示功能启动小字，命令/权限/画面和触摸保持；首次连接仍显示“已连接，等待画面传输”并由真实帧清理；ADB自身提示及双源关闭后的普通画面恢复保持。
