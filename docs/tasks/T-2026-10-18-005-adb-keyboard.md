# T-2026-10-18-005：ADB 普通键盘输入

## T0—T4：授权、证据与设计

- Status：complete / T8（实现与 V0）；2026-10-18；ashell-master 协调 Android/Rust/Flutter/Network，当前会话审查 Security/Release，不委派。
- Recovery：test/738a3fedb133a607e5d9ef8af03239cb097271e0，保留同会话 T004 未提交修改。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui，ADR0025/0026、D031；无依赖、外部资产或协议字段增量。
- C2：补齐 ADB 字母、数字、符号及常用编辑键，保持 Ctrl+V；C1 最小合同与任务记忆。本轮无 Git/CI、本地 build/test/analyze/codegen 或设备操作授权。
- verified：PC 两类键盘均汇合 send_key_event；Ctrl+V 在这里优先消费。Android 普通 KeyEvent 目前只进入无障碍，且原分支重写 modifiers。ADB 已有独立常驻 scrcpy control-only socket，单 writer 同时承载触摸和显式粘贴。
- 方案：在已认证 Android KeyEvent 入口、原 modifiers 处理之前分流到独立 ADB manager；无 ADB 时原无障碍继续。复用现有 protobuf、JNI manager 与 scrcpy 5.0.1 keycode 协议；普通编辑按键按每次 PC key-down 发送完整 down/up，保留 PC 重复事件，避免残留按下。修饰键只按 PC owner 记录到 metaState，不全局按住；不引入 shell-per-key、线程/连接或采集依赖。文本按虚拟键盘字符映射；Ctrl+V 保持原有带 ACK 的 Unicode 粘贴。
- 安全/生命周期：普通远控会话、非 view-only；ADB 以真实 shell 状态决定独占，不回落重复注入；有界独立键盘队列、owner 断开/只读释放和 run 停止清理。JNI byte[] 调用内复制、保留混淆入口。原采集、手势、认证、权限状态不改。
- 参考：scrcpy v5.0.1 app/src/control_msg.c 和 server/control/Controller.java 官方源码；键码包为 type0 + action + keycode + repeat + metaState。仅协议参考，未引入资产。
- 验证计划：V0 检查 Map/Translate/Legacy、大小写/数字/标点、Enter/Backspace/Delete/方向键/Tab、Ctrl+A/Shift 选区、Ctrl+V 去重、无障碍 fallback、只读/断线/多PC修饰键隔离；V2/V3 正式构建与设备待验证。回滚为局部反向补丁，无存储迁移。

## T5—T8：验证与交付

- CE-20261018-T005-01；STATE/WORK-20261018-005。新增 LocalAdbKeyboard，通过 LocalAdbManager.routeAdbKey(int,byte[],boolean) 接入 LocalAdbTouch 的单 writer；配套 local-adb.pro 保留签名。src/server/connection.rs 在原 Android KeyEvent 无障碍转换前独立分流；src/remote_adb_video.rs 提供 JNI 调用；InputModel.inputKey 对真实 ADB priority 放行，同时保留只读检查。
- V0 完成：认证入口/普通会话/只读限制；native 与 Flutter 会合路径；JNI `(I[BZ)Z` 与 Java/混淆一致，byte[] 生命周期；每包14字节、一次完整按键28字节、文本UTF-8按300字节边界拆包；Legacy 字符映射跳过 Shift 前导事件，Translate 控制字符转换，Map 直接使用 Android keycode；修饰键按 owner 隔离，断开/只读/stop 清理；单 writer 不并发写控制流，128项独立队列不改触摸队列；无源画面也可用。
- Ctrl+V 仍由 PC send_key_event 前置 hook 消费，未修改既有 LocalAdbPaste/clipboard RPC/ACK/大小限制；普通打字不写剪贴板。节点/无障碍键处理、普通画面采集与传输均保持。检查 pkg2230.rs 与 ffi.rs 原键盘 JNI 入口，本轮未改。
- 兼容边界：针对字母、数字、标点及编辑快捷键；不是 UHID 键盘或游戏持续按键。PC 重复 key-down 对应连续完整按键。中文/表情等虚拟键盘不能映射的文本继续用既有 Ctrl+V/远程输入，不宣称任意输入法逐键支持。新 PC+新 Android 为完整验证组合，无 peer schema/生成桥增量。
- 《编译验证需求》：正式 Linux 根目录 `./build.sh 1`、正式 Windows 根目录 `build.cmd`；按 TEST_MATRIX AND-05/08、FLT-04/05、ADB-R07，实测无障碍关闭但 ADB 可用、普通/ADB画面、Map/Translate/Legacy、大小写/Shift/符号/小键盘/Ctrl+A/Enter/删除/选区/Ctrl+V、重复/断线/两台PC/只读及无ADB回落。仅 V0；V1—V3 NOT_RUN，无构建或真机通过声明。
- 最小远程ADB/媒体合同与五份记忆同步；Decision/ADR/资产 N/A — no decision delta。保留 T004 未提交成果；本轮未提交、推送、执行编排。
