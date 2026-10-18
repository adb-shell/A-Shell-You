# AShell 远程 ADB / Remote ADB

当前实现基线：0dbe202，2026-10-19源码复核，[Baseline22](../BASELINE/22_RESTORED_SOURCE_BASELINE_2026-10-19.md)。PC负责远程输入，身份、配对记录、Shell验证和执行归Android进程；支持多PC共享状态、独立命令、输入/剪贴板及[媒体与触摸](14_ADB_MIRROR.md)。本轮仅V0文档同步，不代表编译/设备通过。

## 配对与连接

PC和Android都应更新安装包。普通远控顶栏的ADB图标与其他菜单样式一致，分别提供配对、命令、撤销、状态、断开弹窗；标题栏可拖动。配对窗口只处理配对和连接，不混入命令界面。

PC菜单包含配对/连接、远程命令、远程输入、撤销、状态刷新和断开；没有“暂停/恢复无障碍”“授予/关闭无障碍”或“进入开发者选项”菜单。Android本机ADB页的设置入口仍在。不能依据历史聊天恢复当前源码不存在的菜单。弹窗位移由独立ValueNotifier/RepaintBoundary更新，拖动不重建整个表单。

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

样式参照无障碍栏，包含三键、开/关投屏、开/关无视、开/关黑屏、开/关穿透、网址栏/搜索、“点亮屏”及收起按钮。无障碍侧只移除“开防触 / 关防触”两个按钮，原实现保留；ADB侧没有防触按钮。详见 [画面与侧栏合同](14_ADB_MIRROR.md)。

Shell栏无鼠标悬浮小字提示，图标语义名称仍保留；按钮回调、启用条件和样式不变。穿透进度只保留画面中心提示。主界面右上状态栏分隔展示普通状态与ADB投屏/无视/黑屏/穿透/命令状态，真实phase及未知状态含义见上述合同；没有新增定时刷新。

三键分别运行 `input keyevent 4/3/187`，“点亮屏”运行 `input keyevent 224`，仅唤醒、不解锁或移除遮罩；网址经校验/引用后运行 `am start -a android.intent.action.VIEW -d …`。使用已确认状态，仅controller陈旧时先查询；不再每次点击都使共享状态失效。处理中的重复提交会被阻止，失败显示结果，不自动重放、不回退原无障碍回调。画面和黑屏动作使用独立协议与忙状态，Shell命令在执行并不等于ADB能力丢失。

## 键盘、远程输入与剪贴板

- 普通键盘经现有KeyEvent在Android已认证入口优先分流到ADB；LocalAdbKeyboard单writer覆盖字母数字、符号与常用编辑键，owner修饰键独立、队列128项、完整down/up。无ADB仍走原无障碍。普通键盘不写剪贴板；中文/表情等用Ctrl+V或远程输入，不承诺UHID、游戏长按或所有输入法映射。
- “远程输入”弹窗自动聚焦，回车/数字键盘回车或“确定”发送，Shift+Enter换行；输入法组合期间不拦截回车，发送中只读，成功清空、失败保留。
- 远程普通/ADB画面取得焦点后，Ctrl+V从当前PC读取文本，支持native/Flutter键盘入口、去重按键释放与重复；ADB可用优先paste(text)。无ADB而有原键盘/剪贴板权限时，经既有sessionInputString / KeyEvent.seq输入到无障碍。原模拟输入及剪贴板同步保留。
- 仅ADB会话也复用原PC剪贴板监听，通过clipboard(text)与scrcpy SET_CLIPBOARD(paste=false)同步纯文本，不自动粘贴到焦点框；显式Ctrl+V/远程输入使用paste=true。已有原键盘权限时继续原同步路径。新增ADB通道仅PC→Android，不承诺每个输入法都显示建议。
- 状态apiVersion=4、textInputSupported、clipboardSyncSupported用于协商；纯文本UTF-8≤16KiB、JSON≤32KiB，文本不拼入shell或落盘。显式输入队列4项，自动同步1在途+1最新待发、显式优先；ACK预算10秒。ACK表示服务处理消息，不保证目标输入框允许粘贴。
- 尊重只读、剪贴板开关和原首次同步选项；按session/instance/epoch复查，关闭/断线/撤销清队列且不重放。accessibilityPasteRequests仅为PC Rust→Dart本地结果字段，无新peer字段或PC网络轮询。

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

命令只回给发起PC，最多4096 Java UTF-16 units，无NUL；输出64KiB、真实退出码/截断状态，非交互且不保留跨命令工作目录。shell-v2直接发送UTF-8命令，逐包ACK并保留真实输出；exit255不能单凭码值判定为配对失败，须结合输出及握手/UID/通道异常信息。多个独立命令可并发，但任意用户命令对Android系统的副作用仍可能互相影响；资源通道隔离不限制命令语义。

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

上述Dart路径相对 `flutter/lib/`。JSON请求版本仍为1，状态 `apiVersion=4`、`mirrorVersion=2`；FRB签名和身份存储schema未变。

| method | 参数/语义 |
|---|---|
| status | 立即读取快照，不主动连接 |
| discover | 双NSD返回pairPort/connectPort/ambiguous，0表示未找到唯一值 |
| pair | port/code；兼容仅配对，port0自动发现 |
| pairConnect | port/code/connectPort；配对后连接验证，部分成功保留记录 |
| connect | port0/省略自动发现；已在相同端口连接时复用 |
| shell | command；独立命令通道，返回output/exitCode/truncated |
| paste / clipboard | text；显式粘贴 / 仅同步Android剪贴板 |
| disconnect / revoke | 全设备共享连接/身份操作 |
| cancel | apiVersion3可传requestId，仅针对该owner的请求；不传则取消owner全部未完成请求 |

业务参数只接受port/connectPort/code/command/text/requestId，未知参数含退役serviceName均拒绝。请求32KiB、响应512KiB；PC等待表64项、Android路由64项且每连接10项，310秒后惰性清理。PC status/cancel等待8秒，其余300秒；本机桥调用有5秒超时。manager默认20秒、pairConnect35秒，命令从开始执行计20秒；守卫是取消预算，不是已验证的硬实时完成保证。

## 发现、依赖与验收

`AdbPortDiscovery`独立实现双NSD（配对与连接服务），仅接受本机接口/loopback，单resolver、有界队列、服务丢失清结果与延迟重试；独立扫描最多6.5秒。不扫描其他设备，不修改系统无线调试设置。研究来源仍是已登记的LADB/aShellYou源码，未引入其整包运行时；来源、依赖及许可见 [12_LOCAL_ADB](12_LOCAL_ADB.md) / [外部资产](../../EXTERNAL_ASSET_REGISTRY.md)。

[TEST_MATRIX](../../TEST_MATRIX.md) ADB-R01—R25（R10退役）及ADB-M01—M32覆盖模式UI、两端口、真实UID、推送乱序、多PC独立命令、请求取消、重复开关、绘图和原链回归。当前均无新的V2/V3/V4通过结论；HTTP204只证明dispatch被接受。本次仅同步文档、提交推送test并合并推送main，不执行编排。
