# T-2026-10-18-002：ADB 辅助保活与点亮屏幕

## T0—T4：范围、证据与授权

- Status：complete / T8（实现与 V0；Git/dispatch 回执见最终交付）；起点 test/0b5ad995e473b767fbdd9b07f1bbaa1f1b7ca511，初始 clean，本地与 origin/test 一致。
- 用户授权：新增 ADB 保活与 Shell 栏“点亮屏幕”，检查缺漏；提交推送 test 后单次 Build-Clients-New / Both / 常规编译，不监控。普通无障碍、媒体传输、共享、权限与输入链不改；不执行本地产品 build/test/analyze/codegen 或真机命令。
- Primary：ashell-master；Android/Flutter/Network/Security/Release 当前会话审查，不委派。Baseline：AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0025/0026、D031；现有 TEST_MATRIX 的 AND-01/04/05/08、ADB-R04/R06/R07、ADB-M21/M22 为回归边界。
- 设计：连接并验证 Shell 后，仅对 context.getPackageName() 设置 Doze 电池优化豁免，复用 LocalAdbCommands 的独立连接/有界队列/超时/取消；每次连接最多一次尝试，已有豁免跳过，失败不改变共享 Shell。状态用 PowerManager 回读，并经既有状态推送到多 PC；不关闭全局省电、不加守护循环/唤醒锁、不承诺进程免杀。
- 唤醒：Shell 栏复用既有 shell RPC 发送 input keyevent 224（KEYCODE_WAKEUP），不会将已亮屏设备切换成息屏；不自动解锁、切换画面源或移除黑屏遮罩。
- 官方依据：Android [KEYCODE_WAKEUP](https://developer.android.com/reference/android/view/KeyEvent#KEYCODE_WAKEUP)、[Doze 豁免说明](https://developer.android.com/training/monitoring-device-state/doze-standby)、[AOSP DeviceIdleController](https://github.com/aosp-mirror/platform_frameworks_base/blob/android-15.0.0_r1/apex/jobscheduler/service/java/com/android/server/DeviceIdleController.java)。仅查阅，无新增外部资产。
- 风险：OEM 可拒绝命令/豁免；无线调试或 App 已离线时无法下发唤醒；系统豁免是持久配置，ADB 断开不会撤销，可在系统电池设置中恢复。包名只从本机 Context 获取并作为单个 shell 参数转义；不接收远端目标包名。

## T5—T8

- 新增 LocalAdbKeepAlive；Manager 只追加初始化、连接协调、断开重配复位和 status 字段。复用有界 Commands，最多一个辅助保活任务；取消中的旧连接释放前不重复提交，失败不周期重试，连接阶段结束后仍可正常下发其他命令。既有豁免不写入，不清除任何包的白名单。
- 本机状态模型/ADB 页与 PC 状态弹窗新增真实豁免、执行中、失败/未知展示；旧客户端忽略附加字段，新 PC 对旧手机不显示该能力。PC 直接使用原状态推送和显式刷新，无新增轮询。包名动态获取且转义，结果以 PowerManager 查询为准，不把 exit=0 当作豁免成功。
- Shell 栏增加同款“点亮屏幕”按钮，keycode224，经既有可用性/只读检查、远端控制授权和独立 shell 队列；不修改原导航/输入/无障碍/媒体代码，失败沿用命令错误提示。
- V0：逐段复查连接完成前后/端口写入、重复连接、重配及断开取消、旧任务释放、有限队列、状态 JSON 附加字段与 null、多 PC revision/epoch、命令权限/转义与无画面唤醒；diff 范围/空白/引用检查。MainService、Accessibility、普通采集 JNI、所有 ADB 媒体/触摸实现、剪贴板/传输/协议/生成桥/Manifest/依赖和工作流无变更。
- V1/V2/V3 NOT_RUN；未执行本地产品 build/test/analyze/codegen、设备注入或系统白名单变更。V0 不表示 APK/PC 编译、厂商省电或唤醒实测已通过。
- 《编译验证需求》：GitHub Build-Clients-New.yml，ref=test，Platform=Both，BuildMode=常规编译；沿用现有 Linux Android 与 Windows 工具链，只 dispatch、不监控。设备需覆盖已/未豁免、OEM 拒绝/查询不可用、ADB 断开/重配/多 PC、Shell 堵塞/取消、息屏/已亮屏连续点击、锁屏/遮罩、无普通共享及双源画面持续传输。
- 12_LOCAL_ADB/13_REMOTE_ADB/14_ADB_MIRROR 及最小任务记忆同步。STATE/WORK-20261018-002、CE-20261018-T002-01；Decision/ADR/Baseline/External Registry 无增量。最终 Git SHA 与 CI 接受回执见交付，不推定构建结果。
