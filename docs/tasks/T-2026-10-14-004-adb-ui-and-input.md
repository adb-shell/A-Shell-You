# T-2026-10-14-004 — ADB 栏、画面与触摸完善

- T0：main/404b199，初始clean；Baseline18已提交后继。前轮push成功、Build AShell clients dispatch获204，未监控，不能推断构建成功。
- Task Brief/C2：本次八项请求授权Shell栏严格参考无障碍栏、ADB尺寸压缩/提示清理、普通画面ADB光标、黑屏与触摸恢复、独立ADB节点穿透。无本轮Git/CI/本地build/test/analyze/codegen动作。
- Impact Map：Flutter ADB模块与普通图像光标；Android localadb；专用RemoteAdbVideo协议/Rust JNI。原无障碍、MediaProjection、普通编码/输入路由、身份与依赖保持。由ashell-master协调Android/Flutter/Rust/Network，Security/Release静态审查。
- 方案：统一读取普通画面的整数缩放规则；原比例显示与坐标同源；触摸有界清理/自动恢复/有界队列；遮罩标可信避免Android12+遮挡拦截；独立shell UiAutomation绘制节点，连接必须使用DONT_SUPPRESS_ACCESSIBILITY_SERVICES，不能暂停原服务。穿透作为第三种ADB画面源，任意PC关闭，能力协商兼容旧端。
- 不变量：权限仍为已认证普通会话+控制授权；单设备一个ADB源、多PC共享；不改生成桥/版本/构建依赖；节点和图像仅内存、有大小/帧率/租约限制。
- T3/T4：用户当前精确实现授权；按TASK_TEMPLATE压缩记录。回滚以404b199为恢复点，未经授权不执行Git恢复。Baseline18；测试选择ADB-M01—M16并补新增案例。
- T5：实施中，CE-20261014-T004-01。正式Android/Windows构建及真机/多PC验证待授权环境；当前只做V0源码/协议/差异检查。
- T7/T8：实施后同步领域事实与薄记忆；无新增外部二进制。未知：OEM隐藏接口、UI节点可见性、设备旋转/遮罩下注入，需真实设备验证。

## 交付与静态证据

- T5完成：Shell栏采用原栏宽度/缩放/按钮内距/圆角/阴影/分隔线，短窗口滚动兜底；去模式水印、普通画面ADB光标响应；新增开/关穿透。
- Android独立AdbCaptureProfile对齐原宽度/350缩放；读取QoS档位/FPS，ADB另设码率上限，JPEG档位映射。源物理几何与编码尺寸拆分，PC显示与触摸用同一矩形；旋转/换模式释放旧手势。
- 触摸有界close/取消回收/自动重试、32项PC队列/相邻MOVE合并/超时UP/递增序号拒绝迟到native请求；旧Run隔离。SurfaceControl仅自身trusted，不改全局安全设置。
- 独立UiAutomation helper读取节点、绘制线条/文字，DONT_SUPPRESS保持原无障碍服务；节点/帧仅内存，有限大小/数量/深度/帧率/租约。三ADB源互斥，多PC共享/任一关闭；原采集继续独立。
- Security：固定动作/固定APK入口、已认证普通控制会话、字段20—22能力/几何边界；新旧端各自回退，密码节点遮盖。不声明还原未提供节点的游戏/视频等内容，也不绕过系统保护内容。
- Release：无依赖/版本/构建脚本/生成桥改动；R8新增入口和JNI保留。UiAutomation/OEM/黑屏注入仍需正式设备确认。
- V0执行：源码调用链、协议/JNI参数、R8入口、旧端格式/零几何、权限/队列/超时/旧Run审查；Java/Dart去注释字符串后的括号平衡；git diff --check。仅文本检查，不等于分析器/编译/运行通过。
- T7：14_ADB_MIRROR、安全模型、ADR0024/D029、Baseline19、测试矩阵、资产评估及STATE007/事件/任务/Changelog同步；外部资产未变，AGENTS/Skills/构建文档无必要变更。
- T8：源码与记录交付，未提交、未推送、未运行编排。ADB-M17—M20均NOT_RUN；原ADB-M01—M16与原链回归仍待正式验证。

《编译验证需求》

- Android：正式Linux构建环境，仓库根执行`./build.sh 1`；Windows：正式工具链环境，在`C:\Code\A-Shell-You`执行`build.cmd`。
- 目标：新增Java helper、JNI/R8与20—22协议生成、Dart可编译；随后两PC+Android11/12+/OEM验证ADB-M17—M20、黑屏/旋转/迟到UP/服务保活/原采集及无障碍不中断。
- 本轮没有执行上述命令或任何产品测试；V2—V4待正式结果。

- 收尾V0：14个Java/Dart/Rust源码文本括号无异常，专用协议22字段无重复，81个本地Markdown引用存在，git diff --check通过。31个本任务路径未提交，STATE007/WORK010；原Android Kotlin/采集JNI/普通输入路由/无障碍栏与构建脚本无差异。对屏幕尺寸异常补充错误返回，避免启动回调抛出异常或健康守卫被中止。

## 续接复核与 GitHub 编排授权

- 用户当前明确要求查漏补缺后推送GitHub并执行编排；C2补漏/C3提交推送和单次workflow dispatch。目标adb-shell/A-Shell-You/main；Build AShell clients，platform=both、android_arch=aarch64，沿用不监控要求。
- 补漏：LocalAdbTouch重复stop保持首次关闭期限；writer在wait返回后检查active/cancelled，防止旧Run取走新Run事件；尺寸读取纳入busySince超时守卫。其余原链保持。
- 复核：新Java入口/API与R8，JNI参数/协议20—22、三画面源切换/旧端协商、多PC共享状态、PC显示与触摸矩形、输入序号/取消/超时。V0；新增真机复核场景为密集stop、超时后立即重连并拖动、屏幕尺寸读取异常，归ADB-M18。
- 执行前只读证据：本地及远端main均404b1991157308ebf700108dac3196f9d2889f91；31个路径均本任务；Build AShell clients（382599743）active；受保护原链与构建脚本无差异。
- CE-20261014-T004-02 / STATE008 / WORK012；本记录先于commit/push/dispatch，最终SHA及接受回执以Git和会话交付为准。未运行本地产品build/test/analyze/codegen或设备测试；不得把dispatch接受称为构建通过。
