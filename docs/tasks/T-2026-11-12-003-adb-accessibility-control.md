# T-2026-11-12-003 — ADB 控制本应用无障碍

## 0. Task Metadata

- Status / T-State：verifying / T7；2026-11-12（Asia/Tokyo）。
- Requester：用户；Principal / Domain / Security / Release：Codex（openworld-master，Android/Flutter/Security/Release 技能约束审阅，不委派）。
- Source Branch / HEAD：test / a8abdd4c04951f930ea240c9951378ee3b8c165f；初始 clean，无用户修改。
- Baseline：OW-BL-2026-11-07-d9f1119-screen-priority-worktree（Baseline26，历史输入）；当前实现以 test/a8abdd4 源码为准。
- Risk：medium；最高验证 V4（正式设备联调待证）；ADR-0018、ADR-0028；既有 ADB-R21/R22、ADB-M21/M32、NET-04、DSP-09。
- External Asset：N/A，无新依赖；STATE / WORK-20261112-003-r2；CE-20261112-T003-01/02；D-037。

## T0. Authority and Baseline

用户明确批准 test 实现、复检、提交推送 test、双端编排且不监控。C0 读取、C1 相关工程记录、C2 独立 ADB 模块和菜单、C3 test Git 写及 Build-Clients-BT.yml / Both / 常规编译已授权。未授权 main 合并、版本更改、历史重写、产品发布、真实设备或生产配置操作；本机不执行产品构建。旧任务授权和构建结论不继承。发现并发修改或范围冲突停止覆盖。

## T1. Requirements

PC 菜单新增本应用无障碍开启、关闭、暂停/恢复。Android 从安装元数据识别自身包和服务；暂停记录归 Android，不归 PC，其他 PC/重连可恢复。只改本服务在系统启用列表中的条目，保留其他应用。关闭/暂停自然停止本无障碍服务的输入、截图、绘图和遮罩；不得主动停止普通共享、ADB 或 ZEGO。

验收：动态组件、暂停后恢复按钮、真实配置与绑定状态、多人同时操作串行且有界、只读/开发者优先 gate 不变、旧 Android endpoint 能力缺失提示、ADB 断线不自动恢复权限、不创建媒体/语音控制副作用。OEM settings 限制和服务绑定时间待真机确认；工作资料等共享父用户无障碍配置不得猜测写入。

## T2. Impact Map

| Domain | Entry / Files | Impact / Evidence |
|---|---|---|
| Flutter | remote_toolbar → remote_adb_menu/controller/session_state | 菜单直执行与设备状态；verified |
| Android | RemoteAdbNative → LocalAdbManager → 新 LocalAdbAccessibility → LocalAdbCommands | 独立命令 socket、系统配置、私有暂停记录；verified |
| Network/Rust | field40 JSON / remote_adb.rs / server/connection.rs | 沿用认证、只读、developer priority；不改协议/生成桥 |
| Windows/API/媒体/语音 | N/A | 不修改业务链 |
| Security | 自身组件限定、参数拒绝、其他服务保留 | 特权 sink 为已授权 ADB Shell |
| Build/docs | 现有双端编排、13_REMOTE_ADB/状态记录 | 无版本/工具链/依赖变化 |

资产均为 tracked active 源码和新模块；旧 ADB dormant、不恢复。新增 JSON 字段可选，旧 PC 忽略；新 PC 检查能力。暂停记录独立于配对记录，撤销 ADB 不恢复权限。

## T3. Design

系统 PackageManager + Context 包名解析本服务；PC 不传包名/组件/用户。每次写入按当时系统列表仅增删本组件，不回放列表快照。不写全局关闭、不 force-stop/pm disable、不调用原无障碍 UI 命令。并发权限操作不堆积；同一 Android 一次只执行一个，忙时返回明确状态。启用/恢复需实际绑定后才报可用，关闭/暂停需解绑后才报完成。

暂停仅记录原本启用的自身组件；本来关闭不制造恢复资格。持久化先于 Shell 修改，异常/断线保留可恢复证据；手动开启/关闭成功清除暂停记录。状态走已有 revision 推送，不加 PC 定时刷新。

替代方案：硬编码包名、覆盖全列表、force-stop 应用均拒绝。长期架构沿用 ADR-0018/0028，不新增协议或 ADR。回滚：后续经授权 revert 本任务；系统权限需明确开启/关闭恢复，不能随代码回滚自动改系统设置。

## T4. Implementation Confirmation

用户本轮“按这个方案在test分支实现…提交推送test并执行双端编排，不监控”即授权；无需再次确认。范围不含原投屏、远控、语音改造。

## T5. Change Record

实际变更：新增`localadb/LocalAdbAccessibility.java`；`LocalAdbManager`增加入口/状态及取消挂接；Flutter仅`remote_adb_menu.dart`、`remote_adb_controller.dart`、`remote_adb_session_state.dart`、`remote_toolbar.dart`增加直接菜单操作和状态绑定。原Kotlin无障碍生命周期、MainService、媒体/触摸、Rust协议/JNI及ZEGO文件未改。生成文件、依赖、版本、外部资产 N/A；用户原改动无。CE-20261112-T003-02。

## T6. Verification

- V0：25项静态合同/修改范围/词法平衡检查与`git diff --check`通过；复核原服务ctx、取消回调和Rust控制权限边界，不能替代编译。
- 离线命令桩：从实际Java命令表达式提取Shell，以Git Bash模拟settings运行13项通过：空/null启用、保留他项、仅删自己、重复/短名组件、已到目标状态、字面特殊字符、255拒绝、写前外部变化75。无Android设置/设备/网络操作；不是产品运行测试。
- V1/V2：本机无 Java/Dart/Flutter 编译器，不运行产品测试/构建；交由获准的双端 CI，触发后不监控。
- V3/V4：多 PC、OEM、改包名、权限启停、多媒体/语音并存、断线重连 NOT_RUN。
- V5：N/A，无生产操作。
- 《编译验证需求》：Build-Clients-BT.yml，ref=test，Platform=Both，BuildMode=常规编译；GitHub hosted Linux/Windows 正式环境；目标 Android Java/Kotlin/R8 与 PC Dart 编译，最终结果本轮不查询。

## Security / Build Review

沿用认证与允许控制 gate；包/组件来自 Android 自己而非网络参数；命令引用转义、仅特定 secure key 写入、不读取其他配置或凭据。不引入网络/加密/回放/原生 ABI 改动。无新第三方授权/SBOM、无生成输出。OEM 拒绝和外部设置并发需如实报告；运行通过待证。

## T7. Documentation Delta

已同步13_REMOTE_ADB、TEST_MATRIX（ADB-A01—A06）、PROJECT_MEMORY、PROJECT_STATE/CURRENT_WORK/CHANGELOG_AI/CHANGE_EVENT_LOG/DECISION_LOG/TASK_HISTORY。ADR/基线冻结文档/External Asset Registry/入口/技能 N/A：不改长期协议或外部资产。D-037记录本权限操作语义与边界。

## T8. Handoff and Closure

源码与文档复检完成；待test提交推送和一次双端dispatch。不监控编译；产品编译、OEM/多PC真机与原链联调NOT_RUN。不得将静态检查或dispatch等同运行/构建通过。待交付后记录准确SHA/HTTP回执，无main或生产操作。
