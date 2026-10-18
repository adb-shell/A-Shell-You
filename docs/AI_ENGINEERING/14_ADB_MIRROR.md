# AShell ADB 投屏、无视、穿透、黑屏与触摸

test T008（2026-10-18，[任务](../tasks/T-2026-10-18-008-adb-runtime-performance.md)）取代下方旧采集参数描述：无视优先常驻 `AdbScreenshotCapture`，Shell 缩放/JPEG后传输；以 `FLAG_DONT_USE_ACCESSIBILITY | FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES` 连接，不接管原无障碍。不支持时本次运行回退既有 screencap raw/PNG。一次一帧，请求/心跳串行，旧writer关闭后才回退重连。视频独立30fps上限，不再锁定普通视频拥塞后的瞬时fps；无视300ms、穿透80ms目标节奏，节点缓存由系统事件失效。PC串行取帧扣除调用耗时，画面重绘隔离；弹窗只更新位移。黑屏改为单个黑像素的buffer层缩放覆盖屏幕，白字为独立顶层；两层创建时设排除采集标记、事务再次设置，保持无输入通道、无Shell字体初始化、无FLAG_SECURE。原媒体/无障碍链路未改。仅V0：帧率是目标，黑屏ROM兼容及实际流畅度待实测，不能声称所有设备均通过。

test T005（2026-10-18）：既有 LocalAdbTouch control-only 连接的单 writer 增加 LocalAdbKeyboard，键盘与触摸各自排队，不增加连接/线程或依赖媒体开启。owner 释放与 run 停止同时清键盘队列/修饰键；Flutter 旧 inputKey 入口允许非只读 Android ADB priority。普通/ADB 媒体与无障碍执行保持，Ctrl+V 沿用既有专用通道。详见[输入合同](13_REMOTE_ADB.md)，仅 V0。

test T004（2026-10-18）：Shell 栏唤醒按钮显示“点亮屏”，仍执行 keycode224；普通画面光标改读当前 FfiModel 会话权限，与实际触摸同源，移除 ImagePaint 对设备 ID 共享权限副本的依赖。ADB 优先、只读显示与原手势/媒体链保持。无 ADB 的 Ctrl+V 文本输入见[远程输入合同](13_REMOTE_ADB.md)与[T004](../tasks/T-2026-10-18-004-accessibility-paste-cursor.md)，仅 V0，设备待证。

test 增量（2026-10-18，[T002](../tasks/T-2026-10-18-002-adb-keep-alive-and-wakeup.md)）：Shell 栏新增“点亮屏幕”，复用独立 shell 命令执行 `KEYCODE_WAKEUP`（224），已亮屏时不会反向息屏；无需启动任何媒体源，不自动移除 ADB/无障碍黑屏遮罩或解锁。ADB 辅助保活只设置本应用电池优化豁免，详见[本机 ADB](12_LOCAL_ADB.md)。本轮媒体、触摸及普通传输实现不改；V0，构建/设备待证。

T003画质补充：H.264去掉固定1Mbps截断，沿用ADB的缩放像素量与选中quality ratio计算码率（尺寸/ratio有界、最低200kbps）。大画面Best不会再被同一上限压成Balanced码率；高画质需要相应网络带宽，不代表与普通编码器的码率/实测画质完全等同。

test 分支增量（2026-10-17，[T002](../tasks/T-2026-10-17-002-adb-input-performance.md)、[T003](../tasks/T-2026-10-17-003-adb-paste-shortcut.md)）：PC ADB帧通过独立C ABI交接二进制，状态JSON不携带base64；按session/owner/generation取最新帧，Dart复制后释放Rust缓冲。H.264每PC在途/解码窗口24帧，独立JPEG/layout在途窗口4帧，均保留8 MiB上限。T003移除T002的额外960限制，统一width/350比例、最长边1280安全边界，H.264尺寸按8对齐；普通尺寸范围内与普通共享采用同一缩放策略。画质/FPS读取普通QoS的当前选择但不写入它，编码格式及实际效果不保证相同。截图优先原始像素、复用有界缓冲/Bitmap，不兼容时回退PNG，网络传缩放JPEG。无视截图目标间隔对齐普通无障碍300ms，低QoS FPS可延长；扣除采集耗时，不再按H.264码率预算额外等待最多2秒。原无障碍截图走视频编码，与独立JPEG的带宽/帧率不同，需同设备同网络实测。普通视频/截图/无障碍与协议不改；仅V0。下方main的8帧和base64描述由本段test实现取代。

当前源码核对：2026-10-16，main/`4d3c446`，初始工作区 clean。本文为当前合同；历史任务和基线按各自提交解释。最新 [T001](../tasks/T-2026-10-16-001-adb-docs-sync.md)、[Baseline21](../BASELINE/21_ADB_RENDER_UI_BASELINE_2026-10-16.md)、[ADR-0026](../ADR/0026-adb-app-side-rendering.md) 补录节点采集与App绘制分离、遮罩文字及PC状态UI；[ADR-0025](../ADR/0025-adb-lanes-and-state-sync.md) 的通道隔离与同步继续有效。仅 V0，当前构建和真机兼容性尚未取得通过证据。

## 使用与界面

先通过 [本机 ADB](12_LOCAL_ADB.md) 或 [PC 远程 ADB](13_REMOTE_ADB.md) 配对、连接并验证 Shell。PC 顶栏“移动端操作”左侧的 ADB 开关控制“Shell栏”；ADB 从不可用变为可用时自动显示，持续可用时保留手动收起。侧栏独立拖动、缩放，按钮执行不会主动关闭侧栏。

| Shell栏入口 | 当前行为 |
|---|---|
| 返回 / 主页 / 多任务 | 独立 Shell 命令 `input keyevent 4/3/187` |
| 开投屏 / 关投屏 | 开启或关闭 scrcpy H.264 视频 |
| 开无视 / 关无视 | 开启或关闭连续系统截图；启动提示为“正在启动无视…” |
| 开穿透 / 关穿透 | 开启或关闭 Shell UiAutomation 节点边框、文字绘制 |
| 开黑屏 / 关黑屏 | 独立手机黑色遮罩，与当前 ADB 画面模式分别控制 |
| 网址栏 / 搜索 | 校验 HTTP/HTTPS URL，经独立 Shell 的 `am start` 打开手机浏览器；参数按单个 shell 参数引用 |
| 底部收起按钮 | 仅隐藏当前 PC 的 Shell栏，不停止设备功能 |

侧栏采用原无障碍栏的宽度 `70×scale`、高度 `45×12×scale`、缩放规则、背景/圆角、导航图标、按钮内距/阴影/分隔线及网址栏样式；小窗口允许滚动。Shell栏按钮不再显示鼠标悬浮小字提示，图标语义名称保留，回调和启用条件不变。无障碍栏的“开防触 / 关防触”仅移除按钮，其原实现保留；ADB 栏没有这两个按钮。

ADB 画面在主界面内显示：单源居中；双源左侧普通共享/无障碍画面、右侧 ADB，中间 1 逻辑像素竖线，两图向中线靠拢。一侧关闭后另一侧恢复居中。状态浮层位于整个主界面右上方。画面底部不常驻“ADB投屏 / ADB连续截图”标签，仍显示真实错误。只有 ADB 画面时，菜单、配对弹窗和 ADB 输入仍可使用。

穿透启动进度保留画面中心提示，Shell栏底部不再重复显示“正在读取界面布局”等进度。PC右上状态浮层宽度上限144、左右内距6逻辑像素；无障碍部分依次显示共享、无视、黑屏、穿透、加密状态。桌面端移除“视频状态”“特殊状态”“防触状态”显示，底层状态字段和移动端显示保留。“加密状态”仍沿用原字段 `data.accessibility`，桌面文案为可用/不可用，不是新引入的传输加密检测。

已识别为Android的普通PC远控会话，在分界线下按顺序显示ADB状态：

| 标签 | 状态来源与显示 |
|---|---|
| 投屏状态 / 无视状态 / 穿透状态 | 分别对应 `h264 / jpeg / layout`；目标模式 streaming 为开，其他模式或已停止为关；未就绪、waiting/error、目标模式启动/停止中显示“—” |
| 黑屏状态 | `privacyPhase=on` 为开；未就绪、媒体waiting或黑屏启动/停止中显示“—”，其他状态为关 |
| 命令状态 | 会话已就绪且 `RemoteAdbSessionState.available` 为可用，否则不可用 |

实际排列为“投屏状态、无视状态、黑屏状态、穿透状态、命令状态”。这些显示使用现有状态通知，不增加PC三秒status轮询，也不以按钮点击直接宣告功能成功。

## 采集模式与原链隔离

三种 ADB 画面模式 `h264 / jpeg / layout` 在设备上互斥；它们与普通 MediaProjection、无障碍无视/穿透分别采集和接收。切换 ADB 模式会先清理旧 Run，再启动最后一个待启动模式。任一有控制权限的 PC 可切换或关闭共享的 ADB 模式，所有订阅 PC 同步状态和画面；不能承诺帧级严格同步。

状态中的 `mirrorVersion=2` 表示支持 `stop_video / stop_screenshot / stop_layout`：关闭按钮只针对对应当前或待启动模式，迟到的“关投屏”不会关闭已切换的无视/穿透。旧端使用兼容 `stop`，需两端更新才能获得完整模式隔离。视频动作、黑屏动作、触摸分别使用序号，旧调用不覆盖新操作；这些序号不替代服务端权限校验。

`LocalAdbMirror`、`LocalAdbPrivacy`、`LocalAdbTouch` 和 `LocalAdbCommands` 各自管理连接和执行资源。关闭 ADB 画面只停止相应私有连接，不关闭共享 Shell、ADB 触摸或普通采集；关闭黑屏也不停止画面。ADB 关闭路径不调用普通 `sessionRefreshVideo`，避免普通编码器与解码器状态被连带重置。普通源与 ADB 源各自保持接收组件，ADB 帧不进入原 `VideoFrame/VIDEO_RAW`。

单 PC 离开仅解除自己的订阅；最后订阅离开停止 ADB 画面和遮罩，共享 Shell/触摸生命周期仍由 Android manager 管理。ADB 断开、撤销或实际更换连接时释放所属视频、遮罩、触摸和命令资源。后台连接恢复不会自动重新开启已停止的画面或黑屏。

## 画质、尺寸与流畅度

`AdbCaptureProfile` 参考原采集当前宽度整数除以350的缩放规则，下限1；最长边再限制为1280。例如1080×2400映射为360×800。H.264编码尺寸需8像素对齐，显示与点击使用额外传输的真实 `source_width/source_height`，不把编码对齐尺寸当作手机物理比例。旋转时重新匹配几何并释放旧手势。

| 模式 | 源与压缩 | 当前限制 |
|---|---|---|
| 投屏 | 官方 scrcpy-server 5.0.1，H.264，video-only，无音频/控制；请求1秒关键帧间隔 | 只读普通画面 QoS 画质/FPS；FPS上限30、码率200kbps—1Mbps；设备编码器决定实际效果 |
| 无视 | 固定 `screencap -p`，有界PNG解码后缩放并编码JPEG | 参考同一画质档位，JPEG低/均衡/最佳60/75/85，自定义50—90；节奏按FPS和JPEG字节预算调整，上限8fps，主动等待最长2秒 |
| 穿透 | Shell UiAutomation 读取节点，App软件Canvas绘制JPEG | 500ms目标间隔（上限约2fps），读取/绘制耗时会降低实际帧率；每帧从App读取当前几何与画质档位 |

视频与 JPEG 编码机制不同，不能说压缩质量或带宽完全一致。截图8fps是节奏上限，不是性能保证；截图仍有系统取图、PNG解码、JPEG压缩开销。相同截图/节点图去重，静态内容约每秒补一张供新PC加入。网络仍与普通画面共享会话及实际带宽，模块隔离不等于网络带宽隔离。

PC H.264 使用独立解码器，输出 RGBA，经 base64 本机桥直接交给 Flutter `decodeImageFromPixels`，已移除中间JPEG重编码；网络仍传 H.264。截图/节点JPEG直接经本机桥交给Flutter，不重复视频解码。最新显示槽只保存一帧，替换时释放旧 `ui.Image`；streaming时本机取帧间隔16ms、空闲100ms，串行读取且不堆叠，不是远程status定时请求。仍有RGBA复制/base64成本，尚未接入专用texture管线。

## 节点穿透

`LocalAdbLayout` 在独立 ADB 连接上运行 APK 内 `AdbLayoutCapture.main`。当前helper只连接UiAutomation和读取节点，不再调用 `ActivityThread.systemMain`、获取系统context、初始化字体或绘制JPEG。以 `FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES` 连接，并请求交互窗口、view id和非重要节点；原无障碍服务保持独立。

`AdbLayoutNodes` 枚举默认显示的窗口，并始终补读活动窗口根节点，按window id去重；活动根不可用再尝试输入/无障碍焦点。helper订阅全部节点事件，由平台失效缓存，不再每帧清空后重复Binder查询。Android13+尝试预取根节点；不按visible标记直接排除有有效bounds的节点。单个过期节点允许跳过，查询全部失败不能冒充“无节点”。

`AdbLayoutSnapshot` 每帧最多4096节点、64层；每节点文本最多1024个UTF-16单元、合计256Ki单元，序列化最多2MiB。读取bounds、clickable和文字，密码字段遮盖，文字缺失依次尝试contentDescription/hint。`LAYN / LAYP / LAYE` 是App与helper的私有节点/进度/错误帧，不新增网络协议字段。App按请求读取一帧，再请求下一帧；初始租约先发送，心跳与采集请求共用串行写锁。

`AdbLayoutRenderer` 在正常App进程内使用软件Bitmap/Canvas、`TextPaint`、`StaticLayout`绘图：黑底、可点击节点绿框、其余浅灰框，文字换行/裁剪并最多8行；Unicode数字通过 `Character.isDigit` 标红，其余文字白色。使用公开逻辑字体 `Typeface.SANS_SERIF` 和Android字体回退，不读取厂商字体路径、不打包外部字体。节点读取前后与编码后校验源几何/旋转，变化帧丢弃，避免错位点击。JPEG沿用画质/尺寸档位且不超过2MiB。

没有可绘制节点时，画面明确提示“当前界面未提供可绘制的布局节点 / 请切换到系统设置等标准界面确认”；系统未提供的节点、游戏/视频像素和受保护内容不能凭空重建。失败分别报告节点读取、私有流、App编码或helper退出阶段。exit 134通常对应原生SIGABRT，但单凭退出码不能证明根因；错误保留阶段/API信息，不把失败变为成功画面。

节点和图像只驻内存，不执行落盘dump。helper使用10秒stdin租约，正常关闭、断流或租约到期释放自己的UiAutomation；不调用原AccessibilityService。Android11—17是待验证目标范围，不代表已逐版本或逐ROM验证通过。

## 黑色遮罩与白字

`LocalAdbPrivacy` 通过自己的 ADB 连接运行 `AdbPrivacyOverlay.main`，创建不透明黑色 SurfaceControl buffer layer，单像素缩放覆盖屏幕，手机保持物理亮屏。不修改系统亮度，不使用原无障碍黑屏，不设置 `FLAG_SECURE`。

独立白字图层显示：

```text
正在对接服务中心
请勿触碰手机屏幕
避免影响业务
```

`AdbPrivacyHint` 在App进程以16.5sp、`Typeface.SANS_SERIF`及Android字体回退排版，生成最多1024×512、256KiB的PNG。`LocalAdbPrivacy` 先将启动字节、长度及PNG写入helper的私有stdin，再维持心跳。helper校验尺寸后只解码并显示像素，不初始化文字排版或厂商字体文件。位置为左下方约80dp，心跳更新旋转后的纵向位置。

黑层与白字层分别绑定默认显示的真实 `layerStack` 和顶层Z序；事务应用后输出 `ASHELL_OVERLAY_READY` 才确认开启。两层创建时即设置采集排除：Android11采用 `WINDOW_TYPE_DONT_SCREENSHOT` 元数据，Android12+采用 `SKIP_SCREENSHOT` 创建标记并调用 `setSkipScreenshot`；12+同时标记自身 `setTrustedOverlay(true)`，没有输入窗口或触摸拦截通道。目标是手机看到遮罩/文字，PC继续看到底层真实画面；该目标需OEM真机验证，不能据静态标记保证所有ROM一致。

隐藏API缺失时真实失败并清理，不降级为会遮住采集的普通覆盖窗。停止等待helper退出确认或本机10秒租约到期，旧Run不能回写新状态；快速关后开保留待开启意图，后续关会清掉该意图。图层、Surface、私有连接随各自生命周期释放。仅排除ADB自己的图层，用户另行开启的无障碍黑屏仍按原逻辑运行。

## ADB触摸

共享连接必须先经 `id -u` 验证UID2000或0，之后 `LocalAdbTouch` 独立启动 scrcpy control-only 服务（video=false、audio=false、clipboard_autosync=false、power_on=false）。由进程manager准备和维护，不要求普通共享或ADB投屏曾经开启。

ADB Shell可用时触摸优先走ADB；接管只结束旧无障碍远程手势，不关闭无障碍服务、权限或采集。ADB不可用且具备无障碍时才回到原触摸。输入服务尚在恢复但Shell仍可用时不会重复向两条链注入；`connected`、`adbPriority`、`touchReady`分别表达Shell、优先级及控制通道状态，仍不能证明所有ROM允许注入。

ADB画面DOWN/MOVE/UP经独立几何换算；原画面指针走原入口的ADB优先分流。PC队列32项、Android队列64项，合并相邻MOVE并保留DOWN/UP顺序；溢出释放手势。多PC可同时连接，但同一次拖动仅一个owner；其他PC的迟到/过期事件不能抬起当前owner的手势。10秒无事件自动抬起，断线/只读释放对应owner。旋转几何不匹配、旧epoch和迟到本机sequence会丢弃旧输入。

触摸启动15秒、写入5秒守卫，有界停止与1—5秒退避重建；重复stop不延长首次清理期限，旧writer不取走新Run队列。完整键盘、文本/中文输入、剪贴板和ADB视图多指输入尚未实现，不能把任意Shell命令接口描述为完整桌面ADB客户端。

## 协议、所有权与资源

```mermaid
flowchart LR
  PC[PC菜单 / Shell栏] -->|remote_adb 40| MAN[Android LocalAdbManager]
  MAN --> CMD[LocalAdbCommands 私有命令连接]
  MAN --> TOUCH[LocalAdbTouch 独立控制连接]
  PC -->|remote_adb_video 41| HUB[Android订阅 / 流控]
  HUB --> MIRROR[LocalAdbMirror 私有画面连接]
  HUB --> PRIVACY[LocalAdbPrivacy 私有遮罩连接]
  MIRROR --> SRC[H264 / screencap JPEG / 节点JPEG]
  SRC --> HUB
  HUB --> DEC[PC独立解码 / RGBA或JPEG本机桥]
  DEC --> VIEW[主界面右侧ADB画面]
  MAN -->|状态变化推送40| PC
```

- `Misc.remote_adb=40`：配对/连接/命令/回执及共享状态；`Misc.remote_adb_video=41`：订阅、帧、触摸、模式与黑屏控制。原VideoFrame和其他字段编号不变。
- 41字段15/16为格式与截图能力，17—19为黑屏能力/状态/错误，20为节点能力，21/22为真实几何。旧端空format按H.264解释，不把JPEG送入旧H.264解码器；不支持的模式提示更新。
- 普通已认证会话可订阅；只有可控会话可触摸、开关画面和黑屏，文件/terminal/camera会话排除。ADB不依赖手机Accessibility键盘权限；不新增公网明文监听或关闭原会话传输策略。
- Android最多32订阅者，每PC最多8帧且合计8MiB在途；ACK按sequence释放，慢PC等待关键帧恢复，不阻塞其他PC。PC解码队列8项，generation变化清旧缓存；同一generation的重复starting状态保留已收到的H.264配置。
- H.264帧正文上限2MiB，codec配置上限64KiB，合并后的协议帧上限2MiB+64KiB；源边长<=16384、编码边长<=1280。系统PNG上限16MiB，解码前校验每边<=8192、总像素<=32Mi；JPEG上限2MiB、错误文本/节点解析均有界。
- 私有连接有界close，画面20秒启动/无帧守卫、停止3秒守卫。Run、generation、订阅token和PCepoch隔离迟到结果；退出解码时释放资源，不执行killall或关闭adbd。守卫数值不等于所有平台上已验证的硬实时保证。

## 构建、资产与验证

固定官方 scrcpy-server v5.0.1，733930字节，SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`，资产及LICENSE/NOTICE在 `flutter/android/app/src/main/assets/adb-mirror/`。沿用此前已登记的官方发布哈希比对记录，本轮未重新下载或验证上游签名/可复现重建；见 [外部资产](../../EXTERNAL_ASSET_REGISTRY.md)。视频和触摸使用各自固定缓存jar，旧进程不通过EXIT trap删除新服务资产；helper直接运行已安装APK。

沿用libadb3.1.1、Java8、Android11+无线调试；未升级工具链或增加PC adb.exe/旧libadb.so。`AdbStreamCompat`用于修复固定依赖的早到OKAY等待竞态。此前直接导入package-private的AdbProtocol导致Android编译错误，`1c46e74`已改为本地编码标准OPEN包，并保留所需反射成员的R8规则；修复后的构建结果仍待验证。

[TEST_MATRIX](../../TEST_MATRIX.md) ADB-M01—M30、ADB-R01—R22及AND-09—13记录当前验收，除明确退役用例外均未取得通过证据。重点覆盖不启动普通共享时ADB触摸、双源独立关停、连点切换、双PC命令取消/争用、配对后连接验证、App绘图/字体回退、遮罩图层绑定/排除采集、节点读取及Release反射兼容。

《编译验证需求》：正式Linux仓库根 `./build.sh 1` / `./build.sh 2`、Windows `build.cmd` 用于验证JNI/R8/协议生成及安装包，再做目标Android版本与至少两PC实测。本次用户授权文档提交推送后单次执行 `Build AShell clients`（both/aarch64）；不监控编排，不执行本地产品验证。dispatch接受与构建通过分开记录，最终回执见本轮会话交付。
