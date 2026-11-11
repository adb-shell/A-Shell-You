# T-2026-11-11-002：集中服务器配置与 PC 双配置切换

2026-11-12后续证据：用户提供的ZIP确认cd21705的Windows三处client.rs编译失败；Android在系统依赖下载时发生镜像DNS失败后取消。早期dispatch仅受理，不能表示通过。用户已授权修复并重新提交推送/编译且不监控，详见[T-2026-11-12-001](T-2026-11-12-001-build-fixes.md)。

## 2026-11-12提交推送与双端编译回执 / CE-20261111-T002-05

- 源码cd2170512613291d48709bc5ec9e393c8feb42e8已提交并推送origin/test，提交说明“集中服务器配置并支持PC双服务器切换”；dispatch前远端SHA与本地一致。2026-11-11 15:47:44 UTC（2026-11-12 00:47:44 Asia/Tokyo）向Build-Clients-BT.yml（394791509）发送ref=test、Platform=Both、BuildMode=常规编译，HTTP204受理。遵从用户要求，未查询run/jobs/logs/artifacts，未启动监控；构建最终结果未知。
- 提交前已保留三套配置和四份历史ZEGO文档的现有地址编辑；十二项地址本地语法、共享Key与运行源码唯一来源核对通过，暂存差异格式检查通过。原31/31检查是更新地址前的静态快照；实际服务与真机仍未验证。
- T8/complete仅关闭本次源码、文档、test提交推送与构建触发交付；六项单测未执行，构建/native加载和A/B/Android/语音联调待证，不表示所有功能已验收。
- STATE、WORK-20261111-002-r5。本次只补回执文档并提交推送，不再次dispatch；无main/版本/工作流/生产改动。

## 2026-11-12提交与双端编译授权 / CE-20261111-T002-04

- 2026-11-12提交前续接：用户明确授权提交并推送test，再触发PC和Android常规编译，要求不监控。现有工作区三套四项地址均已填写；ANDROID与PC_1相同，PC_2四项均不同，且Android/PC1已不同于初次迁移值。本轮保留这些现有编辑；共享ID公钥保持，地址只做本地语法与集中来源核对，未连接实际服务。历史“PC2留空/原地址保持”是先前检查时的快照。
- 起点本地test与origin/test均51292c29a025ee3ad6a3915285b8f21e7e260ee8；保留四份历史ZEGO文档已有地址更新。
- 使用Build-Clients-BT.yml（workflow 394791509，active），ref=test、Platform=Both、BuildMode=常规编译；沿用Actions临时测试签名/产物。先提交推送并核对远端SHA，再单次dispatch；不查询run/jobs/logs/artifacts，不启动监控。
- 当前active/T7，STATE、WORK-20261111-002-r4；执行结果另记。构建受理不等于成功，不扩展为main/版本/生产发布。

## 2026-11-12续接复查 / CE-20261111-T002-03

- 用户要求再次检查四类地址来源及Android/原功能隔离；原实施授权下修正遗漏，仍无Git写入、构建/测试/codegen、生产访问授权。
- 修正ID获取中遗留的安装包文件名/后台地址入口，PC1数组回退和显式`id@server`绕行；当前网络连接只用本机选中profile的数据。接收端校验RequestRelay目的地址，并移除远端/派生relay回退。在线查询修复IPv6 host/port拆分。
- WebSocket从单次配置快照读取，旧依赖可写endpoint的测试改为两个纯转换测试（14个情形，NOT_RUN）。PC HTTP切换/超时与原移动端流程分开，移动缓存写入调度恢复原方式；子窗口关闭失败不得继续切换。
- 全仓原端点字符串扫描：运行源码只命中`server_profiles.rs`，其余四份文件均为历史文档。API登录/同步/审计/插件及Token请求已追踪；ZEGO保留PC签发双方Token、Android接收callee邀请的原语义。
- V0：31/31复查合同、diff通过；新旧四项地址及共享ID Key/既有broker鉴权保持。协议/generated/JNI/Kotlin/ADB/ZEGO媒体/开发者画面策略/版本/依赖/构建脚本未变。无工具链，六项相关单测、完整编译/真机/服务验证NOT_RUN，不能扩大为运行PASS。
- STATE、WORK-20261111-002-r3；继续handoff/T7。test/51292c2工作区未提交；维护与验证命令见[15_SERVER_PROFILES](../AI_ENGINEERING/15_SERVER_PROFILES.md)。

## 初次实施记录 / Task Metadata / T0—T4

- Owner：Codex / openworld-master；领域：Rust、Flutter、Android、Network、API；Security/Release为约束复核。
- 当前授权：用户要求切换现有test并实施前述方案。已从干净main/5d2d2e9切至干净test/51292c2；只授权此次switch、代码与配套文档。无commit/push/merge、版本、编译/测试/codegen、设备/生产访问或发布授权。
- Baseline：OW-BL-2026-11-07-d9f1119-screen-priority-worktree（Baseline26），交付输入test/51292c2；test版本5.2.1保持，不合入main版本。
- 任务状态：handoff / T7，源码与文档交接完成，正式验证待证。Events：CE-20261111-T002-01/02；D-036 / ADR-0029。
- 当前机器未发现Rust/Flutter/Dart工具链；只能提供V0证据，不将“确保功能正常”表述为未经执行的运行验证。

## Requirements / Acceptance

1. 单一Rust源码文件维护Android一套、PC两套ID/relay/API/ZEGO Token服务地址及共用服务器公钥。
2. Android固定使用Android配置；PC默认配置1，在主窗口左下角切换。两套PC环境独立，各连接对应Android。
3. 旧持久化地址、构建环境和文件名覆盖不得覆盖集中配置；其他产品设置保持。
4. 空闲切换、后台服务确认、真实注册状态；账号、最近设备、保存密码与缓存按配置隔离。
5. 保持relay-only、认证、开发者画面优先、原共享/ADB/语音生命周期与协议字段兼容。
6. 用户已确认Android和PC1沿用现有地址，配置2四项留空由用户后续填写；空配置禁用，不是实施阻塞。

## Impact Map / Design

- hbb_common：配置数据、解析入口、配置编号持久化、环境隔离。
- Rust：ID/API/中继/Token消费者、桌面IPC切换及忙状态拒绝；复用FRB接口，不手改generated。
- Flutter：状态栏选项、切换互斥、停止旧环境后台请求、账号/设备界面刷新。
- Android：移除Flutter启动按钮独立relay字面值，读取统一Android配置；不改变权限/采集/JNI ABI。
- Network：沿用RequestRelay协调双方中继；不增加protobuf字段。
- 构建：保持脚本/工具链/版本；明确既有环境地址不再覆盖集中配置。
- External：EXT-SVC-001—004实际部署、配置2及Token鉴权匹配仍需维护者提供证据。

## Migration / Rollback

- 保留原文件与历史数据；配置1兼容原设备数据，配置2使用独立命名空间。
- 只保存选中编号与revision；集中源码改地址后需重新编译。
- 切换失败不得虚报成功；回到配置1可恢复默认环境。代码回退由用户另行决定，未执行Git回滚。

## Verification Plan / T6

- V0：配置消费者与覆盖路径、IPC/FRB合同、平台分支、缓存隔离、diff/敏感值检查。
- 待正式环境：FLT-02/04/05/06、RST-06、NET-01/02/04/05/07、API-01/02/03/04/08、AND-01/04/07及新增配置切换用例。
- Windows：仓库根目录build.cmd；Android：正式Linux环境仓库根目录./build.sh 1。需单独授权、对应工具链及隔离服务/真机；不得把静态结果记作PASS。

## T7/T8

2026-11-12交接：

- 唯一配置源`libs/hbb_common/src/server_profiles.rs`；Android固定自己的配置，PC选择器/后台IPC/ID/relay/API/语音Token消费接入。PC2四项按用户要求留空，未配置不能选。
- 空闲切换、后台持久化确认、revision与generation过滤、缓存/保存密码隔离、切换重新登录；服务不可达或旧后台拒绝切换，不伪造就绪。FRB缓存保存等待写入结束，迟到确认由状态轮询刷新模型。
- V0：27/27静态合同通过；原四项地址与共享公钥按HEAD逐项比对保留；源文件扫描未见原端点独立副本；`git -c core.safecrlf=false diff --check`通过。生成文件/FRB签名/protobuf/版本/依赖/构建脚本未改。
- V1：`server_profiles::tests`四项单测已添加但NOT_RUN；V2/V3/V4全部NOT_RUN。本机无Rust/Flutter/Dart工具链，未执行项目build/test/analyze/codegen，未调用生产服务或使用真实凭据。
- 文档同步：领域真相层、ADR-0029/D-036、TEST_MATRIX、外部资产与`.codex`当前状态/事件/历史；[维护与正式验证命令](../AI_ENGINEERING/15_SERVER_PROFILES.md)。
- test HEAD仍51292c29a025ee3ad6a3915285b8f21e7e260ee8，工作区修改未提交；无commit/push/merge或版本变更。原开发者画面优先、ADB、采集和认证路径未扩展。
- 剩余：正式环境双端编译、真实B配置及A/B联调；不得声称“所有功能已运行通过”。
