# T-2026-10-18-004：普通画面光标与无障碍粘贴

## T0—T4：授权、证据与设计

- Status：complete / T8（实现与 V0）；2026-10-18；Primary ashell-master，Flutter/Android/Rust/Network 与 Security/Release 约束由当前会话审查，不委派。按 TASK_TEMPLATE.md 记录。
- Recovery：test/738a3fedb133a607e5d9ef8af03239cb097271e0，与 origin/test 一致，初始 clean，无并行任务。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0025/0026、D031，无 Decision/ADR/依赖/外部资产增量。
- 用户 C2 授权三项：点亮屏幕改为点亮屏；修复无障碍可触摸时禁用光标；使无障碍 Ctrl+V 可粘贴。C1 最小合同/任务记忆；无本轮 Git/CI/本地 build/test/analyze/codegen/设备操作授权。不修改普通采集/无障碍手势/远端认证或 MediaProjection。
- 证据：ImagePaint 读取按设备 ID 共享的 KeyboardEnabledState，实际 InputModel 读取会话 FfiModel.keyboard；clearPermissions 仅清 map，共享副本可漂移。原 Ctrl+V 仅在 ADB 就绪时主动读取 PC 剪贴板；非 ADB 原键路径依赖手机剪贴板。Android <33 Seq 进入模拟 EditText，选区用 insert 而非 replace、press 的补发 keyup 可再次提交；33+ 无 IME connection 时没有文本 fallback。
- 方案：光标改用当前会话同源权限并订阅变更；既有 PC 本地快捷键桥新增无障碍文本请求，按键重复/释放继续去重，保留只读/剪贴板/键盘权限。PC 按需读当前剪贴板，经现有 sessionInputString/KeyEvent.seq 发送，无新 peer 字段；ADB 可用继续优先其独立输入。Android Seq 独立主线程提交：33+ 有 IME connection 则 commitText，否则只对当前输入焦点的可编辑节点替换选区并设置光标；不改其他键/手势逻辑。
- 生命周期/安全：当前 session sender 身份校验，最多4项待处理，epoch/销毁/权限复查，重连不重放；文本沿用16KiB/NUL验证且不写日志/文件。普通剪贴板同步保持。JNI/生成桥未改，无新增权限/依赖。风险 medium；运行兼容待 V2/V3。
- 参考：[Android AccessibilityInputConnection.commitText](https://developer.android.com/reference/android/accessibilityservice/InputMethod.AccessibilityInputConnection#commitText(java.lang.CharSequence,int,android.view.inputmethod.TextAttribute))，官方方法返回 void，提交后不重复发送节点文本。
- 回滚：反向本次局部补丁；无持久化/协议迁移。仅保留原 Ctrl+V 路由未采用，因为不能解决手机后台剪贴板及节点插入问题。

## T5—T8：验证与交付

- CE-20261018-T004-01；STATE/WORK-20261018-004。五份源码：`flutter/lib/desktop/adb/remote_adb_sidebar.dart`、`flutter/lib/desktop/pages/remote_page.dart`、`src/remote_adb_video.rs`、`flutter/lib/desktop/adb/remote_adb_mirror.dart`、`flutter/android/app/src/main/kotlin/me/ashell/nZW99cdXQ0COhB2o.kt`。
- V0 完成：光标 Provider 会话权限订阅与旧参数无残留；本地 poll 新字段的生产/消费/清零与 ADB 切换、输入权限/只读/剪贴板检查；异步剪贴板读取后的 epoch/instance/销毁复核；Seq 经既有 FRB/JNI 到无障碍主线程，只提交一次；焦点/选区索引/UTF-16/节点释放逐项检查；diff 空白/文件边界检查。普通按键、手势、采集、剪贴板同步、服务启停、协议/生成桥/依赖无修改。
- 验证：按 TEST_MATRIX FLT-04/05/06、AND-05/08、ADB-R07；覆盖无 ADB+无障碍、ADB优先、只读/关闭剪贴板/撤销输入权限、重复按键、重连/关闭窗口、空文本/中文/表情/换行、空字段/中间插入/选区替换、Android11/12与13+无IME回退。不支持编辑动作的应用仍需设备验证，不伪称粘贴 ACK。
- 《编译验证需求》：正式 Linux Android 根目录 `./build.sh 1`，正式 Windows 根目录 `build.cmd`；验证 Kotlin/Dart/Rust 构建及上述真机案例。本轮仅 V0，V1—V3 NOT_RUN。
- 同步 Android/远程ADB/媒体三个小节及五份任务记忆；Decision/ADR/资产无增量。当前 test 工作区未提交，未推送或执行编排。静态结论不等于真机效果通过。
