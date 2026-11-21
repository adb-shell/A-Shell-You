# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261121-001-r5`
Last Updated / Observed At：2026-11-21（Asia/Tokyo）
Evidence Scope：`V0 + test source pushed + exact-head dual CI SUCCESS; device unverified`

最新交付：T001 complete/T8（源码、推送及双端编译范围），源码febf7f0f20460a0bfffbc0ee0a2581828a30d7c0已推送test，main仍956ecf4；这是对齐后新增开发提交，未合main。Build-Clients-BT.yml / Both / 常规编译run44883517881精确head_sha匹配，于2026-11-21 17:46:24 Asia/Tokyo以SUCCESS结束，Android Native/APK与Windows EXE全部通过。正式设备/蜂窝/OEM/安全负例仍NOT_RUN。本批仅回执与使用说明文档补交，不二次编译，最终Git状态以本批提交为准。STATE/WORK-20261121-001-r5 / CE-20261121-T001-07；以下为前阶段时点。

当前快照：T-2026-11-21-001 / complete T8（源码/Git范围）；当前test/956ecf482ac173af710aa7b6ea0b9de2bc3b4d60，内置ADB权限服务及配套文档未提交。无线调试仅显式配对/激活，正常命令与媒体用Binder/FD，服务存活时可跨网络继续调用；死亡/重启需重新激活。实际Release/OEM/蜂窝/双PC验证NOT_RUN。沿用main的Rust/portable2.0.26、Flutter2.0.26+59，原Rust/JNI/FRB/Kotlin运行时与服务器配置无本任务业务增量。

Git回执：test02本地删除（无独有提交、远端无此分支），main既有合并956ecf4已推送。用户随后明确授权先对齐再test开发；保存改造后将test快进到最新main并推送，远端main/test均956ecf4，ahead/behind为0/0。恢复29份代码/清单/许可文件内容全部一致，16份文档顶部冲突已保留双方历史并解决；索引无冲突/暂存。stash 3bd0f1e保留恢复点，新ADB代码未提交推送。用户长期要求合test到main后、回test作业前先对齐最新main，已写入DEVELOPMENT_WORKFLOW/AGENTS。

Latest：D-038 / ADR-0030 / OW-BL-2026-11-21-956ecf4-embedded-adb-worktree / CE-20261121-T001-01—04 / WORK-20261121-001-r2；[任务](../docs/tasks/T-2026-11-21-001-embedded-adb.md)、[执行合同](../docs/AI_ENGINEERING/16_EMBEDDED_ADB.md)。后续正式构建、设备矩阵及新源码提交需另按当前授权推进；无本轮CI触发/监控。下方均为历史时点，不代表当前分支或验证结论。

2026-11-15 / T-2026-11-15-002：main/d75a950合入test/609aec9，提交说明“增加无障碍”。按用户明确选择采用test完整服务器配置，Android/PC1当前相同、PC2独立；UDID显示移除来自test，既有ADB无障碍功能及其余917个非Markdown文件保持main，版本2.0.26 / 2.0.26+59。AMG-02来源与文档冲突检查通过；complete/T8仅本地合并提交交付，具体SHA以Git为准。本轮未推送、未编译或监控，test工作区与分支保持。 [任务](../docs/tasks/T-2026-11-15-002-accessibility-main-merge.md) / STATE、WORK-20261115-002-r2 / CE-20261115-T002-01/02；Baseline28，沿用既有ADR。

以下为两个分支的历史快照，不代表本轮授权或验证结果：

2026-11-15 / T001交付回执：源码696a87bb294088a57f59d89fce3f3d266ca3dab5已提交并推送origin/test，提交说明“移除PC右下角UDID显示”，触发前本地/远端SHA一致且工作区clean。2026-11-15 09:27:19 UTC（18:27:19 Asia/Tokyo）向Build-Clients-BT.yml（394791509）发送ref=test、Platform=Both、BuildMode=常规编译，HTTP204受理。按用户要求不监控，未查询新run/jobs/logs/artifacts，最终编译结果未知。 complete/T8仅关闭本次提交推送与触发范围；STATE、WORK-20261115-001-r3 / CE-20261115-T001-03。补回执只改文档，不二次编排。

以下续接与原编辑阶段为历史快照：

2026-11-15 T001续接：用户明确授权提交并推送本次UDID显示移除到test，然后触发PC与Android常规编译，不监控。起点test/caf0702，远端一致；工作区仅本任务1份Dart源码和9份配套文档。使用Build-Clients-BT.yml（394791509，active），ref=test、Platform=Both、BuildMode=常规编译；先推送核对SHA再单次dispatch，不查询新run/jobs/logs/artifacts。当前active/T7，Git与触发回执待完成。STATE、WORK-20261115-001-r2 / CE-20261115-T001-02。

以下为原编辑阶段快照：

2026-11-15 / T-2026-11-15-001：在clean test/caf0702上移除PC右下角识别码/UUID显示与双击复制，唯一产品源码为connection_page.dart两块展示代码（53行）。状态/服务器切换、账号UUID、Rust桥接、Android与配置保持。V0精确差异及diff检查通过；未编译/运行、未提交推送，complete/T8仅源码编辑交付。 [任务](../docs/tasks/T-2026-11-15-001-hide-desktop-udid.md) / STATE、WORK-20261115-001-r1 / CE-20261115-T001-01；DECISION_LOG、ADR、Baseline和外部资产reviewed-no-change。

当前源码观察为test/caf0702，版本5.2.1 / 5.2.1+59；下方为历史任务快照，不代表本轮分支、未提交状态或验证结果。

当前T-2026-11-13-001 / complete/T8：合并提交`a9c7e1027849c296e2b8ed48b0cb5fae026ce0f9`已推送origin/main，两个parents为main/990a4aa和test/d9d30eb；提交说明“增加ADB无障碍开关快捷键”。触发前本地/远端SHA一致。2026-11-12 15:32:41 UTC（2026-11-13 00:32:41 Asia/Tokyo）向Build-Clients-BT.yml发送ref=main、Platform=Both、BuildMode=常规编译，HTTP204受理。未查询run/jobs/logs/artifacts，不监控，最终结果未知。 AMG-01通过：7个ADB源码与test一致，其余912个非Markdown文件与main一致，173个本地文档链接有效，无冲突标记，diff检查通过。原test未提交配置字节未变且未纳入提交；test分支保持d9d30eb。正式构建/多PC及OEM真机仍待证。 STATE/WORK-20261113-001-r2 / CE-20261113-T001-02；仅补回执文档，不再次编排。见[合并任务](../docs/tasks/T-2026-11-13-001-adb-accessibility-main-merge.md)。

以下为合并前的历史交付快照：

当前T003 / complete/T8续检交付：源码0e5cc3dfe567ffe0617849cea25c0e0eb5276797已推送test，触发前本地/远端一致。2026-11-12 14:50:21 UTC触发Both/常规编译，HTTP204；未查询运行进度/结果。回执文档续交不再编排。24项静态与diff通过，正式编译/设备待证。WORK-20261112-003-r7 / CE-20261112-T003-07；以下保留前阶段证据，不再代表当前未提交。

最新续接：T003 / complete/T8本轮本地复检，test/b25b9aa上有未提交修复。命令确定未执行时还原暂停记录；PC查询按revision/epoch合并菜单快照，共享监听不回写旧权限。24项静态与diff通过；只变五个ADB源码文件和相关记录，原Kotlin生命周期/采集/媒体/输入/语音及系统列表修改命令保持。无提交推送、无编排或进度查询；正式构建与设备NOT_RUN。WORK-20261112-003-r5 / CE-20261112-T003-04/05。下方为首次交付历史记录。

当前 T-2026-11-12-003 / complete/T8（本次交付）：test/a8abdd4输入，源码115d0bb3b26e441aa1d13e0f8bcbd79ef69acc42已推送test，实现ADB本应用无障碍开启/关闭/暂停恢复。动态自身组件、设备暂停记录与多PC状态，保留其他服务列表；原无障碍生命周期/采集/媒体/触摸/ZEGO源码不改。25项静态合同、13项离线Shell和diff通过，真机NOT_RUN。2026-11-12 13:42:08 UTC触发Both/常规编译，HTTP204；未查询运行进度或结果。回执仅文档续交、不再编排。WORK-20261112-003-r3 / CE-20261112-T003-01/02/03 / D-037。下方既有任务为历史记录，不继承构建结论。

最新续接：T-2026-11-12-001 / complete/T8（续修交付），test/bbebc74 clean。f4a71c6的Android依赖步骤仍DNS失败，并出现_apt索引权限警告；此前7项命令桩不代表Linux/网络通过。已修正/tmp沙箱路径与有界三镜像选择，Bash语法/13项离线回归通过，Linux权限与runner DNS仍待CI证据。已推送test/c788e8d并触发Both/常规编译，HTTP204；不监控，未查询新结果。后续仅补回执文档。WORK-20261112-001-r6 / CE-20261112-T001-04—06。

当前任务T-2026-11-12-001 / complete/T8（指定交付范围）：test/e0ea612 clean恢复；用户ZIP证实cd21705的Windows编译失败与Android依赖镜像DNS阻塞后取消。源码与CI修复已提交为f4a71c6e6d57ac41ca1ecf49aa50bb791ad8458b并推送test，Bash语法/离线流程7/7通过；保留PC2新地址。Both/常规编译已HTTP204受理，不监控，未查询新结果；后续只补回执文档。WORK-20261112-001-r3 / CE-20261112-T001-01—03；[详情](../docs/tasks/T-2026-11-12-001-build-fixes.md)。下方T002回执保留当时证据，不再代表最终结果未知。

## 当前源码基线

- 合并提交`c30efd61449a0e02b5b0546a25ddf948c5ad5d4e`已推送origin/main，并核对远端SHA一致；两个parents分别为main/5d2d2e9、test/a8abdd4。test仍为a8abdd4，推送后工作区clean。提交说明“统一配置文件(大改造节点一)”。本次只补交付回执，不改变源码、不触发或监控编译。

- 本轮任务：[T-2026-11-12-002](../docs/tasks/T-2026-11-12-002-unified-config-main-merge.md)，complete/T8（合并交付）；用户授权合并推送main，提交说明“统一配置文件(大改造节点一)”。远端无mian，按实际main执行。
- 输入main/5d2d2e932cfb390a7b3d0278bea6a7e080746c95、test/a8abdd4c04951f930ea240c9951378ee3b8c165f；起始test工作区clean，两个远端分支与本地一致。
- [Baseline28](../docs/BASELINE/28_UNIFIED_CONFIG_MAIN_BASELINE_2026-11-12.md)继承Baseline27的2.0.26 / 2.0.26+59和开发者画面优先，合入test服务器配置、Windows编译及Android CI修复；三套四项地址完整保留a8abdd4，Android与PC1当前不同。
- 本轮不触发或监控构建。c788e8d的Both/常规编译仅有HTTP204回执，最终结果未知；此前失败和13项离线检查见[T001](../docs/tasks/T-2026-11-12-001-build-fixes.md)。
- 历史任务/版本/编译回执只供追溯；后续会话先核对HEAD/status，不继承旧授权或运行结论。

## 当前能力摘要

| 领域 | 当前实现与限制 |
|---|---|
| 产品身份 | 显示“开放世界”；内部 OpenWorld/openworld、com.openworld.me；flutter_hbb保持；Rust 2.0.26 / Flutter 2.0.26+59 |
| Android 原链 | core连接服务、MediaProjection、无障碍输入/无视/穿透/黑屏保持各自原入口；服务运行不等于屏幕共享或首帧 |
| ADB身份 | Android进程持有配对身份；无线NSD/连接仅供显式激活，以Binder身份及真实 id -u 验证ready；配对记录不代表权限服务存活 |
| ADB执行 | 内置独立Shell服务通过Binder/FD支持2路独立命令及媒体/输入，设备共享状态推送；服务死亡如实要求再激活，不自动恢复无线端口；PC无三秒远程status |
| ADB画面 | scrcpy ScreenCapture + 独立GPU/H.264；常驻截图JPEG及raw/PNG兼容回退；Shell取节点、App绘穿透 |
| ADB参数 | width/350整数缩放；4096边长/4Mi像素界限；只读普通VideoQoS，运行时更新；无视至少300ms，节点至少80ms |
| ADB输入 | Shell可用时全局优先触摸，独立control-only连接；普通键盘、远程输入、Ctrl+V及PC→Android纯文本剪贴板同步 |
| 黑屏 | 独立黑色buffer层及App预渲染白字PNG，排除自身采集；OEM表现待真机证据 |
| PC界面 | 主窗口右下角不显示识别码/UUID，内部账号设备标识保持；普通左/ADB右、单源居中；Shell栏无悬浮提示；状态分组；无障碍栏无视频/特殊/防触状态文字 |
| PC开发者入口 | Ctrl+Shift+H先弹密码框；两个可接受密码任一精确匹配后确认/回车启用内存免登录，取消/错误不启用，重启复位；V0/FLT-08待运行 |
| 开发者画面优先 | 进程模式供新建/重连使用；Android认证后暂停普通画面/盲输入/采集控制，保留会话与ZEGO；多开发者可接收，最后退出恢复；协作角色可由修改版伪造，正式验证待证 |
| 常亮与保活 | 默认连接服务运行期间静默常亮，无新增设置入口；ADB连接验证后尝试本应用电池优化豁免，不保证进程免杀 |

详见 [12本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)、[Android原链](../docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md)。

## 本轮工作与验证

- Registry：WORK-20261112-002-r2；Events：CE-20261112-T002-01/02；沿用D-034/035/036、ADR-0027/0028/0029，无新增架构决定或外部部署。
- 源码自动合并、15份Markdown冲突已解决；41个非文档差异文件与预期来源哈希一致，32份Markdown的375个本地链接有效，冲突标记为零、历史引用保留、diff检查通过。版本来自main、产品改动与用户地址来自test；Git回执见当前任务。
- 配置唯一维护处为`libs/hbb_common/src/server_profiles.rs`；Android固定、PC空闲切换、账号/peer隔离、后台确认和旧响应过滤保持。见[15_SERVER_PROFILES](../docs/AI_ENGINEERING/15_SERVER_PROFILES.md)。
- SCP-02/04—09、DSP-02/04—10等单测/真机/服务联调仍待证；本轮不执行编译、codegen、设备或生产操作。开发者角色仍为协作声明，不是可信凭证；SEC-016等外部缺口保持。
