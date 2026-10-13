# ADR-0019 — ADB discovery and independent control

- Status：accepted for implementation，V0；T-2026-10-13-006 / D-023，2026-10-13。
- 后续关系：以下保留T006决定原貌；二维码/serviceName、PC三秒轮询和相关时限已由[ADR-0020](0020-adb-code-pairing-and-explicit-refresh.md)取代。独立权限、Android owner、NSD与原链隔离仍有效。
- Approver：当前用户要求可用的PC远程ADB、自动配对连接、独立于原链；明确授权提交/推送/单次CI dispatch不监控。
- Owner：Codex ashell-master；Android/Flutter/Network/Rust/Security/Release静态自审。
- Supersedes：ADR-0018中keyboard gate、单连接端口发现及统一20秒等待部分；Android单一owner、原字段40和通道隔离不变。ADR-0015退役/0017固定依赖仍成立。

## Decision

手机mobile初始化enable-keyboard=N；用该开关拦ADB使状态与功能不可达。ADB gate改为已认证普通会话，status允许读取，操作另要求PC未选择只读disable_keyboard；不以Android无障碍键盘能力决定ADB。切只读/断线取消owner任务，手机keyboard变化不再取消ADB。原输入/权限处理不改。这是明确授予普通认证会话的独立shell能力，不是把UI当授权。

AdbPortDiscovery用本机NSD双服务、有限串行解析和取消清理。采用aShellYou双服务思路独立适配Java，不恢复LADB binary/settings自动化、不升级依赖。新增discover/pairConnect、apiVersion=2及可选connectPort/serviceName；旧pair/connect和JSON v1/FRB ABI保留。

普通模式输入六位码即可，配对后自动连接并id验证，失败保留真实配对记录。PC二维码模式遵循AOSP schema，用临时随机口令和服务名，只匹配本机指定服务。系统六位码不能通过mDNS获得，不加入无障碍/截图读取。手工端口为ROM发现失败的回退。

PC菜单复用原toolbar组件；独立可拖动对话框持有controller，不随toolbar自动隐藏销毁。配对窗口无命令/撤销页签。手机与所有PC以3秒轮询读取同一状态，输出只回origin。

## Alternatives / risks

- 保留keyboard gate会继续把ADB绑定无障碍状态；直接改原keyboard状态会触碰原链，因此均不采用。
- 不采用跨设备端口扫描或输入自动化；不采用新TCP明文服务或关闭既有peer加密。
- 既有认证缺陷不由本任务修复；普通认证会话可提交任意shell，没有新增逐条审批或命令白名单。
- QR等待60秒/操作90秒，一键配对35秒，其他20秒；8项队列配套740秒PC等待、750秒有界路由保留。取消扫描不会断共享ADB；取消操作无法回滚shell副作用。

## Evidence / rollback

Baseline AS-BL-2026-10-13-504adea-adb-usability；TEST_MATRIX ADB-R01—11，构建/ROM/多PC/旧新端/负例待正式证明。正常CI重编两端Rust，不改generated bridge。记录/identity schema无迁移，按CE-20261013-T006-01路径审阅回退；不能恢复已撤销key或撤销已经执行的shell命令。详细合同、来源与验证见 [13_REMOTE_ADB](../AI_ENGINEERING/13_REMOTE_ADB.md)。
