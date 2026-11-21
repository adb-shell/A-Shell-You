# T-2026-11-21-002：内置 ADB 服务保活与重新绑定

## T0 / Metadata / Authority

- Status：complete / T8（本轮源码交付）；2026-11-21 Asia/Tokyo；Owner：Codex / openworld-master，Android实现、Flutter状态、Security静态自审；无独立代理。正式构建与设备待验。
- 用户明确要求继续完善保活，尽量在关闭无线调试、切网、App退出重开后免重新激活。C2覆盖本模块与状态展示、配套文档；本轮没有新增Git写入、编译/设备操作、发布或版本修改请求。
- 输入：clean test/e137d4413d04385bfe16c43a87897cf5e0be54c3；当前main已包含在test中，无分支对齐缺口。前轮源码febf7f0双端CI通过仅属于输入版本，不覆盖本轮更改。
- Session recovery：STATE/WORK-20261121-001-r5 / CE-T001-07，无活动冲突，state-consistent；依据TASK_TEMPLATE。引用冻结Baseline `OW-BL-2026-11-21-956ecf4-embedded-adb-worktree`（实现固化febf7f0、回执e137d44）、D-038/ADR0030、EXT-REF-SHIZUKU-001。
- 非目标：不获取root、不改系统调试/安全选项、不创建互相拉起的守护进程、不绕过force-stop、不开放第三方接口、不自动配对或通过无线端口激活。不承诺非root在所有ROM上只因重启失效。

## T1/T2 / Requirements and Impact

- 源码证实：Bridge任何验证超时/异常都会STOP服务且持久错误阻止再次绑定；Service每5s安装查询任意异常都会退出；Binder递送在主Looper同步执行，可能阻塞STOP；App重建初期直接显示activation_required；电池豁免首次失败后同一连接不再尝试。
- 目标：暂时的系统查询/验证故障不销毁仍活的服务；保留身份、调用UID、安装路径及generation校验。App重建优先恢复原Binder；真实重启/死亡/撤销明确区分；所有重试有并发/频率/时间边界，用户Shell命令不重放。
- 影响：Android PrivilegedBridge/Provider/Protocol/System/Service、LocalAdbManager/KeepAlive与Flutter本机/PC状态展示；字段40/41及Rust/JNI/原Kotlin采集/ZEGO不改。无新依赖或二进制。
- 安全：恢复不是授权；未验证不connected；临时系统错误拒绝本次特权调用但不杀daemon；确认安装变化/错身份/用户停用必须拒绝并停止；force-stop期间不拉起App。

## T3/T4 / Design and Confirmation

- 沿用ADR0030的单独shell进程与私有Binder/FD。增加恢复窗口、boot-count辅助识别、验证退避、App进程lease及双observer/周期递送回退；把系统查询与Provider递送移出daemon主Looper且限一个在途任务。
- 已有电池豁免限定本包，失败最多三次；不永久持有WakeLock、不改全局进程限制、不自动重放用户操作。
- 兼容：追加reconnecting状态/字段含义，旧控制端仍以connected作为实际可用门。旧持久化记录缺boot-count时先尝试绑定；显式激活写新记录，不迁移配对密钥。
- 用户当前明确修改请求作为C2实施确认，无需再确认相同范围。回滚：恢复本批源码，保留原配对；停止/撤销语义不改变。

## T5 / Change Record

实际修改7份Java与4份Dart的恢复语义，未改Rust/JNI/原Kotlin/Manifest/依赖/服务器配置/版本：

| 文件 | 变化 |
|---|---|
| PrivilegedBridge.java | 20s重绑定窗口、bootCount辅助识别、8s探测与1s/3s/30s退避；暂时失败不STOP，错身份明确拒绝；候选保留用于立即停用；取消队列不发新探测；begin原子复用及请求激活所有权 |
| PrivilegedProtocol.java / PrivilegedProvider.java | ready后才回传应用lease；shell/root限定的generation/ready检查；保留accepted字段和原service协议 |
| PrivilegedSystem.java | InstallationChanged与系统暂时异常分离；新增process observer，UID与5s周期回退保持；不拉起force-stop App |
| OpenWorldPrivilegedService.java | 一次性observer注册线程、单维护线程和事件合并；App lease死亡重新递送；明确撤销即退出；STOP不等待安装查询；暂时查询失败不退出，执行仍需安装验证 |
| LocalAdbManager.java | apiVersion6，恢复窗口先等原服务，发现/启动前再次复用；只有真正新建才拥有激活取消权 |
| LocalAdbKeepAlive.java | 同一可用周期最多3次本包电池豁免尝试，5s/30s退避、并发1 |
| local_adb_bridge/page.dart、remote_adb_controller/menu.dart | reconnecting展示/操作守卫，恢复期不重复配对激活，PC菜单仍可停用；T003另按用户要求精简文字与排版并移除手机停用按钮 |

并发协调：收到用户另一个聊天明确授权的T003分工通知后，四份Dart的文案/布局交给T003；本任务不覆盖其文字或布局，继续负责Java和T002记录。其变更单独登记，不将其工作计为本任务授权或验证。CE-20261121-T002-02。

## T6 / Verification

- 已执行V0：11份Java/Dart词法括号扫描无问题，diff whitespace检查通过；按源码复核启动/恢复/真实死亡/安装查询错误、超时/迟到验证、lease撤销、connect发现期间恢复及取消所有权。修正复核发现的lease存活不能替代授权、排队取消探测以及observer注册阻塞周期维护问题。
- 上述词法检查不是Java/Dart编译；并行T003文案与布局变更由其自行复核，不继承本任务的早先扫描结果。
- 最终7份Java词法复检、diff检查通过；新增文档链接检查22个有效（检查时点），Rust/JNI/原Kotlin/Manifest/版本与构建输入无差异。T003闭环确认保留reconnecting及操作条件，明确停用入口文档同步为PC菜单。
- 参考固定Shizuku revision的BinderSender/Starter，process observer不引入新依赖；BOOT_COUNT依据Android官方API24+文档，仅可读时作辅助判断。未增加OOM/cgroup改写或多进程互拉。
- 正式V1—V4均NOT_RUN，ADB-P13—P18已登记；关闭无线调试/蜂窝/重开App/系统回收/真实重启及安全负例必须在设备验收。旧run44883517881只覆盖febf7f0。

《编译验证需求》
- 命令/入口：推送本轮源码后，GitHub Actions `Build-Clients-BT.yml` / `ref=test` / `Platform=Both` / `BuildMode=常规编译`；或正式Linux根目录`./build.sh 1`、Windows根目录`build.cmd`，按相应环境授权执行。
- 环境：既有Actions hosted-runner工具链，或文档规定正式Linux/Windows构建机；Android11+真机及已认证PC远控，覆盖蜂窝和主要OEM。
- 执行目录：相应仓库根，构建源码SHA必须对应本批提交；当前未提交，不能用e137d44冒充新版本。
- 验证目标：Release/R8与双端恢复状态兼容，ADB-P01—P18及ADB-R/M/A、NET-04/DSP-09；重点验证同一daemon跨切网/App重建、暂时失败不误停、撤销不会恢复。

## T7/T8 / Handoff

已同步16_EMBEDDED_ADB、08/09/10、TEST_MATRIX、ADR0030/D-038实施补充、任务历史/Change Event/全局记忆与入口。STATE/WORK-20261121-002-r2，CE-T002-01/02；Current Work仅关闭T002并保留T003的独立关闭记录。N/A — no new decision：沿用D-038；Baseline29冻结、External Asset Registry reviewed-no-change，无新外部资产/依赖/版本。

改动保留test工作区，未提交、未推送、未触发新CI、未安装或操作设备。普通App无法自行恢复被系统彻底杀死的shell进程，不承诺所有ROM只在重启后失效；本轮减少可恢复故障造成的重复激活。
