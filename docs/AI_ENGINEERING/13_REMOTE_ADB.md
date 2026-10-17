# AShell PC 远程 ADB

test T006（2026-10-18）：仅 ADB、无原键盘权限时复用 PC 原剪贴板监听，通过字段40 `clipboard(text)` 向 Android 同步纯文本；`clipboardSyncSupported=true` 与真实 connected 协商，尊重只读/剪贴板权限及原首次同步选项。scrcpy SET_CLIPBOARD(paste=false) 只更新手机剪贴板，Ctrl+V 仍为显式 paste=true；自动同步1在途+1最新待发，显式4项队列独立优先，断线/关闭/取消清理。单次UTF-8≤16KiB且JSON≤32KiB，无新增监听或网络轮询。已有无障碍权限时继续原同步/模拟输入链；原双向行为不改，新增通道仅PC→Android文本，不承诺各输入法必定显示建议。见[T006](../tasks/T-2026-10-18-006-adb-clipboard-sync.md)，V0，构建/真机待证。

test T005（2026-10-18）：普通键盘经原 KeyEvent 在 Android 已认证入口分流到独立 ADB 输入连接，保留原 modifiers，覆盖 Map/Legacy/Translate 的字母数字、符号和常用编辑键；无 ADB 仍走原无障碍。各 PC 修饰键独立，键盘队列128项，完整 down/up、不留设备按下；普通输入不写剪贴板，Ctrl+V 仍在 PC 前置截获并走原 Unicode paste/ACK。中文/表情等不在虚拟键盘映射范围的内容继续用 Ctrl+V/远程输入；不声称 UHID/游戏长按或任意输入法支持。无新 peer 字段；新两端配套验证。见[T005](../tasks/T-2026-10-18-005-adb-keyboard.md)，V0，构建/真机待证。

test T004（2026-10-18）：Shell 按钮文案由“点亮屏幕”改为“点亮屏”，命令保持。原本仅供 ADB 的本地 Ctrl+V 分流扩展无 ADB 情况：当前会话具有键盘/剪贴板权限且非只读时，PC 按需读取文本，经既有 `sessionInputString / KeyEvent.seq` 输入到无障碍；ADB 可用继续优先 ADB。新增 `accessibilityPasteRequests` 仅为 PC Rust→Dart 本地 poll 字段，不是 peer 协议。保留大小校验、最多4项、按键重复/释放去重、epoch/instance/权限复查；不增加剪贴板监听或网络轮询，不修改普通剪贴板同步。两端更新后验证，见[T004](../tasks/T-2026-10-18-004-accessibility-paste-cursor.md)；下方各增量按历史时点理解。

test 增量（2026-10-18，[T002](../tasks/T-2026-10-18-002-adb-keep-alive-and-wakeup.md)）：既有状态通道携带本应用电池优化豁免状态，多个 PC 共用 Android 的 PowerManager 回读结果，旧端忽略新增字段；不增加 PC 轮询。“刷新 ADB 状态”弹窗显示辅助保活状态/失败说明。Shell 栏新增“点亮屏幕”，经原鉴权 shell RPC 发送 `input keyevent 224`，不依赖投屏、无视、无障碍；仍受远控权限、ADB 连接与命令队列约束。仅唤醒，不自动解锁或关闭遮罩。V0，正式效果待证。

test 增量（2026-10-18，[T001](../tasks/T-2026-10-18-001-keep-screen-on-and-adb-input.md)）：已有“远程输入”弹窗自动聚焦，回车/数字键盘回车或“确定”发送，Shift+Enter 换行；输入法组合文字期间不拦截回车，按住回车不重复发送。发送期间只读，成功回执后清空并重新聚焦，失败保留文本。沿用原文本校验、paste RPC 与取消生命周期；普通/ADB 画面的 Ctrl+V 链路不改。V0，Windows 输入法与真机输入效果待验证。

test 分支增量（2026-10-17，[T002](../tasks/T-2026-10-17-002-adb-input-performance.md)、[T003](../tasks/T-2026-10-17-003-adb-paste-shortcut.md)）：远程画面获得焦点后，Ctrl+V 在 Android ADB 可用且支持文本输入时直接粘贴电脑文本；native/Flutter 键盘入口共用分流，按 session 定位，长按/释放不重复。ADB 视口补齐焦点；无 ADB 保留原快捷键路由，遵守只读/禁用剪贴板设置。菜单“远程输入”仍可编辑后发送。Android `apiVersion=4` / `textInputSupported`、字段40 `paste(text)`，UTF-8 最多16 KiB及JSON大小检查；文本不拼入shell、不记录，不增加后台剪贴板监听。沿用独立scrcpy输入连接、4项队列和10秒ACK，关闭/重连/撤销取消待发送操作且不重放。ACK仅表示消息已处理，不保证输入框允许粘贴。普通剪贴板实现、无障碍及采集保持；公共键盘入口仅新增ADB快捷键分流。仅V0，构建/设备验收待证；下方为main基线。

当前核对：2026-10-16，main/`4d3c446`；[T001](../tasks/T-2026-10-16-001-adb-docs-sync.md) / [Baseline21](../BASELINE/21_ADB_RENDER_UI_BASELINE_2026-10-16.md)。PC负责远程输入，身份、配对记录、Shell验证和执行均属于Android进程。当前支持配对/连接、命令、撤销、状态推送、Shell栏及[独立ADB画面/触摸](14_ADB_MIRROR.md)。仅V0，未宣称构建或多PC真机验收通过。

## 配对与连接

PC和Android都应更新安装包。普通远控顶栏的ADB图标与其他菜单样式一致，分别提供配对、命令、撤销、状态、断开弹窗；标题栏可拖动。配对窗口只处理配对和连接，不混入命令界面。

当前主线不包含曾实现后回退的“暂停/恢复无障碍”“授予/关闭无障碍”或“进入开发者选项”菜单。用户两次回退至 `393acaa` 后，`4d3c446` 仅移除PC视频/特殊状态文字与Shell栏悬浮提示；不得依据历史会话将这些已回退功能当作当前支持能力。

| 模式 | 输入与执行 |
|---|---|
| 只填配对码（默认） | 仅显示六位码，不显示两个端口或扫描按钮；保持手机系统配对码窗口打开，点击“配对并连接 ADB”，Android发现端口、配对、连接并验证Shell |
| 手动端口 | 显示配对端口、连接端口、六位码及“扫描并填充端口”；填写值直接使用，留空自动发现；配对端口来自配对码窗口，连接端口来自无线调试首页 |
| 我已配对，连接并验证 Shell | 无需输入配对码；保持无线调试开启，自动模式发现连接端口，手动模式使用填写值或留空发现；不使用配对端口 |

两端自动模式始终提交端口0，不消费保留的隐藏端口。切换模式解除焦点并维持输入控件身份。PC开窗查询状态，不预扫描端口。已配对连接按钮可操作时使用主题色，仍受支持状态、只读、当前窗口任务和连接操作状态约束。

二维码及serviceName已移除，旧端发送该参数返回invalid；系统不会通过mDNS广播六位码，模块不通过截图或无障碍提取它。发现/连接发生在Android本机，PC与手机无需处于同一局域网。

配对成功只记为paired。连接须通过TLS/ADB握手，再执行 `id -u`，退出码0且输出为2000或0才置connected；不主动获取root。连接失败区分“握手未完成”和“Shell通道或UID验证未完成”。已配对但连接失败保留记录，可点击“我已配对”或手填当前连接端口；App记录不等于系统配对数据库仍认可该身份。

自动连接在本次预算内最多尝试两次，首次/重试发现窗口为3.5秒/1.5秒，发现不到可尝试上次成功端口；多个本机候选要求人工选择。手动连接也最多两次，仍使用指定端口。仅重试连接和UID验证，不重放配对码或用户命令。固定libadb的OPEN竞态与Android编译修复见 [本机模块](12_LOCAL_ADB.md)。

## Shell栏

开关位于“移动端操作”左侧；`RemotePage`拥有会话级显示状态。确认ADB可用且非只读时自动出现，持续可用时保留用户手动关闭；断开或只读时隐藏，再次可用时自动显示。开关与拖动位置不在不同PC之间同步，设备功能状态由Android共享。

样式参照无障碍栏，包含三键、开/关投屏、开/关无视、开/关黑屏、开/关穿透、网址栏/搜索及收起按钮。无障碍侧只移除“开防触 / 关防触”两个按钮，原实现保留；ADB侧没有防触按钮。详见 [画面与侧栏合同](14_ADB_MIRROR.md)。

Shell栏无鼠标悬浮小字提示，图标语义名称仍保留；按钮回调、启用条件和样式不变。穿透进度只保留画面中心提示。主界面右上状态栏分隔展示普通状态与ADB投屏/无视/黑屏/穿透/命令状态，真实phase及未知状态含义见上述合同；没有新增定时刷新。

三键分别运行 `input keyevent 4/3/187`；网址经校验/引用后运行 `am start -a android.intent.action.VIEW -d …`。使用已确认状态，仅controller陈旧时先查询；不再每次点击都使共享状态失效。处理中的重复提交会被阻止，失败显示结果，不自动重放、不回退原无障碍回调。画面和黑屏动作使用独立协议与忙状态，Shell命令在执行并不等于ADB能力丢失。

## 状态同步与执行通道

- Android `LocalAdbManager`保存共享状态，`instance`区分进程实例，`revision`随真实快照变化递增。配对、连接、任务数等变化经独立 `RemoteAdbNative.state` 推送；新订阅收到最近快照，不传播配对码、命令或输出。
- 状态走字段40的 `kind=state`，与字段41的订阅token绑定；PC按peer、token、instance/revision接收。菜单controller与Shell栏共用 `RemoteAdbSessionState`，拒绝旧回执覆盖新快照。已有真实快照时，旧触摸优先级通知不再覆盖它。
- PC不恢复3秒远程status轮询；打开弹窗、手动刷新、会话就绪/恢复可控或必要的陈旧查询仍有效。Android本机页前台3秒查询保留；Android进程每秒检查一次共享连接并仅在快照变化时推送。PC的16/100ms媒体槽与200ms请求结果轮询都只访问本地Rust桥。
- 仅当前会话成功连接过且未显式断开/撤销时，意外断链允许最多3次后台连接恢复。恢复期间connected仍为false，需重新通过UID验证才可用；不自动配对、不重放命令、不重开画面/黑屏。App重启仅保留身份/记录，不自动恢复连接。
- 旧端无状态推送时仍依赖查询；`paired`、`connected`、媒体phase、`touchReady`互不代替，不把控制通道建立当作所有ROM注入成功的证明。

| 通道 | 并发与所有权 | 取消/断开 |
|---|---|---|
| 发现、配对、连接、断开、撤销 | manager单worker，最多1执行+7排队；状态查询不排队 | 改连接的任务可以影响设备共享能力；纯发现失败不关闭已有连接 |
| Shell命令 | `LocalAdbCommands`最多2并发，总计8项；每项自己的连接/线程/20秒超时 | 单命令取消/失败关闭自己的连接，不撤销共享Shell或其他采集/触摸 |
| 投屏/截图/穿透 | 单一设备模式，专用连接/Run，多PC订阅 | 模式化停止、旧Run隔离，见14_ADB_MIRROR |
| 黑屏 / 触摸 | 各自连接和生命周期 | 黑屏与画面分别控制；触摸不依赖普通共享是否开启 |

命令只回给发起PC，最多4096 Java UTF-16 units，无NUL；输出64KiB、真实退出码/截断状态，非交互且不保留跨命令工作目录。多个独立命令可并发，但任意用户命令对Android系统的副作用仍可能互相影响；资源通道隔离不限制命令语义。

关闭弹窗、断线或只读取消相应owner请求。apiVersion3使用requestId精确取消目标请求，旧端兼容owner取消；状态查询失败不会直接取消另一个进行中的命令。结果超时只能表示结果未知，不证明命令未执行，也不回滚后台子进程副作用。

断开保留身份；撤销是全设备App身份动作，会清记录并取消相关任务，影响所有PC。系统无线调试列表仍须手动忘记App条目。PC从不持有手机ADB私钥，不能单独撤销某台PC的“ADB配对”。

## 权限与传输

请求经现有已认证普通远控会话传输。排除file-transfer、terminal、camera，port-forward不走此分支。status允许读取，其他操作拒绝PC主动只读 `disable_keyboard`。手机Accessibility/keyboard开关不是ADB权限依据，原键盘处理不因此开放。

ADB的JSON不额外加密，沿用原peer stream认证/加密/relay策略，不新增公网明文端口或修改原传输设置。本机ADB连接限定loopback，TLS/SPAKE2由既有库完成。任意shell是已有普通认证且可控PC的权限范围，不新增命令白名单、逐条审批或持久审计；代码隔离不等于系统副作用隔离。

## 调用链与协议

`RemoteAdbController → session_get_common → src/remote_adb.rs → Misc.remote_adb(40) → authenticated server branch → RemoteAdbNative → LocalAdbManager`；手机 `LocalAdbPlugin` 同时访问该manager。媒体与触摸另经 `src/remote_adb_video.rs` / 字段41，原terminal和采集JNI不承担ADB Shell任务。

| owner / 入口 | 职责 |
|---|---|
| `desktop/pages/remote_page.dart` | 管理会话状态、媒体订阅与主界面布局，释放时解绑 |
| `desktop/adb/remote_adb_session_state.dart` | 共享真实snapshot、epoch、可用性和手动显示开关 |
| `remote_adb_controller.dart` / `remote_adb_menu.dart` | 弹窗及Shell请求、回执、状态合并、requestId取消 |
| `remote_adb_sidebar.dart` | 独立拖动位置、三键/URL、模式按钮、兼容旧端状态 |
| `remote_adb_mirror.dart` / `remote_adb_mirror_view.dart` | 订阅、画面与黑屏分别等待、几何/触摸、本机RGBA或JPEG显示 |
| `desktop/widgets/remote_toolbar.dart` | 同款ADB菜单与“移动端操作”左侧开关 |

上述Dart路径相对 `flutter/lib/`。JSON请求版本仍为1，状态 `apiVersion=3`、`mirrorVersion=2`；FRB签名和身份存储schema未变。

| method | 参数/语义 |
|---|---|
| status | 立即读取快照，不主动连接 |
| discover | 双NSD返回pairPort/connectPort/ambiguous，0表示未找到唯一值 |
| pair | port/code；兼容仅配对，port0自动发现 |
| pairConnect | port/code/connectPort；配对后连接验证，部分成功保留记录 |
| connect | port0/省略自动发现；已在相同端口连接时复用 |
| shell | command；独立命令通道，返回output/exitCode/truncated |
| disconnect / revoke | 全设备共享连接/身份操作 |
| cancel | apiVersion3可传requestId，仅针对该owner的请求；不传则取消owner全部未完成请求 |

业务参数只接受port/connectPort/code/command/requestId，未知参数含退役serviceName均拒绝。请求32KiB、响应512KiB；PC等待表64项、Android路由64项且每连接10项，310秒后惰性清理。PC status/cancel等待8秒，其余300秒；本机桥调用有5秒超时。manager默认20秒、pairConnect35秒，命令从开始执行计20秒；守卫是取消预算，不是已验证的硬实时完成保证。

## 发现、依赖与验收

`AdbPortDiscovery`独立实现双NSD（配对与连接服务），仅接受本机接口/loopback，单resolver、有界队列、服务丢失清结果与延迟重试；独立扫描最多6.5秒。不扫描其他设备，不修改系统无线调试设置。研究来源仍是已登记的LADB/aShellYou源码，未引入其整包运行时；来源、依赖及许可见 [12_LOCAL_ADB](12_LOCAL_ADB.md) / [外部资产](../../EXTERNAL_ASSET_REGISTRY.md)。

[TEST_MATRIX](../../TEST_MATRIX.md) ADB-R01—R22（R10退役）及ADB-M01—M30覆盖模式UI、两端口、真实UID、推送乱序、多PC独立命令、请求取消、重复开关、绘图和原链回归。当前均无新的V2/V3/V4通过结论；HTTP204只证明dispatch被接受。本次文档提交推送后按授权单次执行Build AShell clients（both/aarch64），不监控；实际回执见会话交付。
