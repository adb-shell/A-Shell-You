# AShell AI Changelog

Schema Version：`1.0`  
Coverage Start：2026-07-12  
Last Updated：2026-10-18（静默常亮，无新增入口）

Mode：`append-only task-level index`

## T-2026-10-18-003：静默常亮与默认值

- test/9e28a90 clean 起点；主页移除常亮控件/缓存。r2 按用户补充删除本任务新增 AppSettingsPage 和齿轮路由，HomePage 恢复 HEAD；Kotlin/Dart 默认 service-on 静默生效，保存的有效选项不覆盖，不开放旧设置页。
- V0 完成，V2/V3 待证；原无障碍/采集/传输/ADB 保持。r3 用户授权提交推送 test 后单次 Build-Clients-New Both/常规编译，不监控；准确回执见交付。Decision/ADR/资产：N/A — no decision delta。
- [任务](../docs/tasks/T-2026-10-18-003-keep-screen-on-settings.md)，STATE/WORK-20261018-003-r3 / CE-20261018-T003-03（事件01/02保留历史）。

## T-2026-10-18-002：ADB 辅助保活与点亮屏幕

- test/0b5ad99 clean；LocalAdbKeepAlive 通过独立命令设置本应用 Doze 豁免，动态包名、单次尝试/取消、已有豁免保留；PowerManager 回读并在手机/PC 状态中展示。Shell 栏复用鉴权 shell 下发唤醒键224，已亮屏不切换成息屏。
- V0 完成，原无障碍/采集/传输及媒体链保持，无新依赖/权限/协议；V2/V3 待证。用户授权 test 提交推送及单次 Both/常规编译、不监控。
- [任务](../docs/tasks/T-2026-10-18-002-adb-keep-alive-and-wakeup.md)，STATE/WORK-20261018-002、CE-20261018-T002-01；Decision/ADR/资产 N/A，无增量。

## T-2026-10-18-001：主页常亮与 ADB 输入弹窗

- test/00193fa clean；主页三档 keep-screen-on，移除四处覆盖，前后台共用只读 CM 授权连接状态（排除文件等非远控），core-service 档独立于共享。设置持久化、后台权限说明、JNI/R8 保留。
- ADB 原 paste 弹窗增加自动焦点、回车/确定、Shift+Enter，保留输入法与 Ctrl+V 原链；无媒体/无障碍/普通采集改动。
- V0 完成，V2/V3 待证；授权提交推送 test、单次 Both/常规编译不监控。见[任务](../docs/tasks/T-2026-10-18-001-keep-screen-on-and-adb-input.md)，STATE/WORK-20261018-001、CE-20261018-T001-01。Decision/ADR/资产 N/A，无增量。

## T-2026-10-17-003：ADB Ctrl+V与画面参数对齐

- test/8ef006e clean起点。原键盘共用入口仅增加ADB Ctrl+V分流；ADB视口焦点、两种输入来源、按键释放、会话取消及共享文本校验。普通剪贴板/无障碍/采集未改。
- 移除960限制，恢复统一profile；JPEG对齐普通300ms截图节奏，取消复杂画面的额外码率等待，独立截图在途4帧。性能须真机测量，不能等同编码效果。
- V0源码及diff复检；授权test提交推送与单次test编排、不监控。见[T003](../docs/tasks/T-2026-10-17-003-adb-paste-shortcut.md)，STATE/WORK-20261017-003、CE-20261017-T003-01。无Decision/ADR/资产变化。

## T-2026-10-17-002：test ADB远程输入与媒体性能

- 从main/af1c72d创建test，保留既有CI提交；新增显式Unicode粘贴、二进制帧交接、24帧窗口、960媒体上限及原始截图/PNG回退。普通采集/无障碍/剪贴板链保持。
- V0范围、ABI、编码和diff检查通过；未运行本地产品验证，设备流畅度待证。按当前授权提交推送test并单次Build-Clients-New Both/常规编译，不监控；最终结果见交付。
- [任务](../docs/tasks/T-2026-10-17-002-adb-input-performance.md) / STATE-20261017-002 / CE-20261017-T002-01。Decision/ADR/资产：N/A — no decision delta，延伸既有ADB边界。

## T-2026-10-17-001：构建模式“原始编译”

- Source：main/130f748，初始clean；仅新工作流标签及必要记录。
- Delta：BuildMode选项及Android/Windows九处条件统一由“完整编译”改为“原始编译”；默认“常规编译”和构建/缓存命令保持。
- Verification：V0十处精确替换与diff检查通过；无产品测试或构建。
- Authority：用户授权修改并本地提交；不push、不执行或监控CI。
- Records：CE-20261017-T001-01 / STATE-20261017-001 / WORK-20261017-001；N/A — no decision delta。
- Task：[记录](../docs/tasks/T-2026-10-17-001-build-mode-label.md)。Git提交结果以Git和会话交付为准。

## T-2026-10-16-001：ADB绘图、状态展示与回退边界文档同步

- Source：main/4d3c446，初始clean、本地/远端一致；本轮仅Markdown。
- Delta：节点helper与App绘图分离、App预渲染遮罩PNG/字体回退、layerStack、红色数字与单处启动提示；PC状态分组、移除视频/特殊文字和Shell悬浮小字。已回退无障碍权限控制/开发者菜单明确不属于当前实现。
- Records：ADR0026 / D031 / Baseline21 / STATE-20261016-001 / WORK-20261016-001 / CE-20261016-T001-01；新增M26—M30待验项。
- Authority：文档提交推送后单次Build AShell clients both/aarch64，不监控；无业务代码、本地产品验证、版本/发布/生产变化。V0检查与Git/dispatch准确结果见任务/会话交付；不把204写成构建通过。
- Task：[T001](../docs/tasks/T-2026-10-16-001-adb-docs-sync.md)。下方记录保留原时点含义。

> 本文件记录可由 `.codex/TASK_HISTORY.md` 和交付物证明的 AI-assisted engineering task 结果。它不是产品 changelog、Git history、Decision Log 或逐文件修改日志。2026-07-12 之前的提交没有可靠 AI attribution，因此不补猜；精确修改事件在本日志建立前不可重建，统一标记为 retrospective/backfill。

## T-2026-07-12-001：项目资产接管

- Record Type：`retrospective/backfill`
- Status：`completed — repository-side only`
- Scope：全仓 archaeology、架构/文档审计、长期 memory 与八个领域 Skills。
- Outcome：建立 `docs/AI_ENGINEERING/`、初始 `.codex/` 和 `.agents/skills/`；识别外部资产、安全与可复现性缺口。
- Business Behavior Change：none。
- Highest Verification：V0；未执行项目 build/test。
- Decisions：D-001—D-011；ADR backfill 由后续任务完成。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-07-12-002：AI 工程体系强化

- Record Type：`retrospective/backfill`
- Status：`completed — repository-side governance`
- Scope：统一入口、AI rules、T0—T8、Development Workflow、External Asset Registry 与八个 Skills 审查。
- Outcome：形成固定任务/权限/外部资产链路；未扩大业务或外部权限。
- Business Behavior Change：none。
- Highest Verification：V0；未执行项目 build/test。
- Decisions：D-012 / ADR-0012。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-07-12-003：AI 工程体系最终封版

- Record Type：`retrospective/backfill`
- Status：`completed — repository-side governance seal`
- Scope：ADR、Baseline、Task Template、Test Matrix 与只读 Safe Superpowers adapter。
- Outcome：建立 14 项 ADR、工程 Baseline、52-case test matrix 和安全推理边界。
- Business Behavior Change：none。
- Highest Verification：V0；V1—V5 未执行。
- Decisions：D-013 / ADR-0000 / ADR-0013。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-07-12-004：AI 全局记忆增强

- Record Type：`contemporaneous`
- Status：`completed — repository-owned global session memory`
- Scope：Project State、multi-session Current Work、AI Changelog、Change Event ledger、Session Start Protocol，以及入口/任务协议同步。
- Outcome：新 Codex 会话可按项目文件恢复 Baseline、当前状态、并行任务、AI 历史、重大决定、相关 ADR/Skill 和下一确认门；旧会话权限明确不继承。
- Persistent Changes：五个新 `.codex` 文件；入口、AI Rules、Task Protocol、Task Template、Decision/Task history integration。
- Business Behavior Change：none。
- Change Event IDs：`CE-20260712-T004-01`—`CE-20260712-T004-06`。
- Highest Verification：V0；blind session recovery + ambiguity-fix regression PASS；未执行项目 build/test。
- Decision / ADR：D-014；ADR-0011、ADR-0012、ADR-0013 context。
- Residual Risk：file-based memory is as-of and cooperative, not a lock or production telemetry；每个新会话仍须重查 HEAD/dirty state 和当前授权。
- Next Gate：下一个用户任务从 Session Start Protocol + T0 开始；无 standing C2/C3。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID。

## T-2026-10-12-001：本轮接管记忆持久化

- Record Type：`contemporaneous`
- Status：`completed — workspace-local documentation`
- Authority：当前用户要求将本轮记忆保存供本工作区新对话恢复；C1 文档编辑。前序接管审查为 C0，期间保持只读。
- Source：`test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890`；初始工作树 clean。
- Outcome：保存带源码锚点的接管报告、短记忆和新观察基线；接入 AGENTS/Start Here/工程索引，纠正旧 HEAD、branch、本机 ADB 资产与凭据有效性表述。
- Baseline：`CS-BL-2026-10-12-6fb0aa6`；旧7月基线和历史记录保留。
- Business Behavior Change：none；仅13个 Markdown 文件新建/更新。
- Change Event：`CE-20261012-T001-01`。
- Highest Verification：V0 文件/引用/基线/变更范围/状态关联检查；无实际新聊天恢复测试，无项目 build/test/analyze/codegen。
- Decision / ADR：沿用 D-014；`N/A — no decision delta`；无新 ADR。
- Residual Risk：记忆是带日期的静态证据；同一检出可读，其他机器/worktree需实际包含这些文件；未提交、推送或上传云端。
- Next Gate：本任务关闭；新请求重查 HEAD/status 并从 T0 开始，不继承权限。
- Detailed Record：`TASK_HISTORY.md` 的同 Task ID；完整修改清单见同 Event。

## T-2026-10-12-002：AShell产品身份迁移

- Record Type：contemporaneous；Status：completed — source/V0 only。
- Authority：当前用户明确要求改产品身份/包名/文件/构建引用；后续明确保留flutter_hbb及其导入；禁止执行编译等命令。
- Source：test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76；initial clean；当前变更未提交。
- Outcome：AShell/ashell、me.ashell、后台/PC A Shell You、launcher一个壳子；跨层native加载和打包消费端、状态事件、当前文档/Skills同步。Flutter内部package不改。
- Decision/Baseline/Events：D-015 / ADR-0014；AS-BL-2026-10-12-a036f31-worktree；CE-20261012-T002-01、CE-20261012-T002-02。
- Verification：30项静态断言及引用/范围复核；V1—V5 NOT_RUN，无编译/运行成功结论。
- Residual：公共服务guard和来源记录保留；按追加授权只用ASHELL_*变量与ashellSessionId字段，实际构建机/服务待同步。新包不继承旧数据和权限；native资产、ZEGO lock、签名及Apple/Google注册待验证。
- Detail：TASK_HISTORY同ID；docs/tasks/T-2026-10-12-002-ashell-identity.md；迁移报告含编译验证需求。
- Next Gate：新用户任务从T0重新核对；没有standing build/Git/release权限。

## T-2026-10-12-003：统一全部品牌残留与文档字段

- Status：completed — source/V0；用户明确要求补齐截图两处匹配和所有文档字段/文件名。
- Source：test/a036f31；保留T002未提交变更。新增两处ashell.匹配；41份初始含旧词文档统一品牌，四份文档改名/引用同步；历史文本加规范化说明，原文/真实源仓地址留在Git。
- Boundary：Flutter内部flutter_hbb保留；不改上游RustDesk技术名，不编造新源仓URL，不操作远端/部署。
- Verification：排除.git后全文/路径零旧词命中，本地链接/两处谓词/Flutter包名和diff检查通过；未build/test/analyze/codegen。
- Decision/Event/State：D-016补充ADR-0014；CE-20261012-T003-01；STATE-20261012-004 / WORK-20261012-006。
- Detail：docs/tasks/T-2026-10-12-003-brand-normalization.md；迁移报告；TASK_HISTORY同ID。

## T-2026-10-12-004：更新服务器默认配置

- Status：completed — source/V0；用户明确提供新IP、七端口映射和服务器Key。
- Source：test/f423335，开始时clean且与origin/test一致；此前品牌变更已由用户提交并按请求推送。
- Outcome：12份原配置/源码/脚本/文档仅作指定值替换，旧值排除.git后零残留；同步工程文档与恢复入口。本轮未提交。
- Verification：逐文件对照HEAD的预期字面替换一致；端口派生、公钥格式和diff检查通过；运行/构建/握手/部署均NOT_RUN。自定义配置和构建环境仍可覆盖默认值。
- Decision/Event/State：D-017；CE-20261012-T004-01；STATE-20261012-005 / WORK-20261012-008。
- Detail：docs/tasks/T-2026-10-12-004-server-configuration.md；TASK_HISTORY同ID；无standing Git/build/external权限。

## T-2026-10-12-005：PC入口合并和编译产物打包

- Status：completed — source/V0；用户明确授权旧脚本删除与新入口迁名、移除外部驱动装配。
- Source：test/c0951b5，initial clean。build.cmd现为唯一PC入口；generator修正失败传播与输入边界；同步工程说明、入口、Skill和恢复记忆。
- Verification：Python AST与3段内嵌PowerShell仅解析，CMD label/调用关系与diff静态检查；不执行产品脚本、安装、构建、测试或打包。
- Limits：Python Brotli需预置；未附带组件的运行功能不保证可用；其他历史workflow未改，旧基线保留。
- Decision/Event/State：D-018 / CE-20261012-T005-01 / STATE-20261012-006 / WORK-20261012-010。
- Detail：docs/tasks/T-2026-10-12-005-pc-build-entry.md；新baseline07；本轮未提交/推送。

## T-2026-10-12-006：原图统一Windows与Android产品图标

- Status：completed / T8 / V0；用户明确授权保持原图设计做尺寸/格式转换及指定资源替换，禁止编译。
- Source：test/c9b7cd1，initial clean；T005已由该HEAD固化。本任务32文件未提交：21图片、2加载/ignore配置、9任务/工程/恢复文档。
- Outcome：3个9尺寸ICO，1024源图/UI主图、256备用PNG；Android五密度普通/备用圆形/自适应前景。loadIcon不再回退旧SVG，两个新增PNG可被Git发现。
- Verification：文件解码、PNG/ICO尺寸与alpha、内容留白、源图SHA未变、资源引用和ignore例外、静态预览及diff检查；无build/test/analyze/codegen/安装/实际打包/Git写入。
- Limits：正式Windows Shell/EXE与Android APK/主题/启动器显示未验证；通知、悬浮球、横向Logo、动画和其他平台专有资源不在范围。
- Decision：N/A — no decision delta，沿用ADR-0014；Event：CE-20261012-T006-01；STATE-20261012-007 / WORK-20261012-012。
- Detail：docs/tasks/T-2026-10-12-006-product-icons.md；后续构建/Git按新的明确请求执行。

## T-2026-10-12-007：Windows / Android 图标覆盖补齐

- Status：completed / T8 / V0；用户追加授权检查所有Windows/Android产品图标并继续生成替换，继续禁止编译。
- Source：test/c9b7cd1；initial dirty为T006的32文件，本轮28文件，与T006重叠9文件，合并51文件未提交。
- Outcome：Android五密度通知小图/独立monochrome、默认悬浮球；Windows旧Sciter同源图、自解压窗口small/big、卸载快捷方式品牌图标；精确ignore例外。主EXE/服务/托盘/MSI路径已核对。
- Verification：12PNG/16XML、尺寸/alpha/遮罩空洞、图片预览、include/资源引用、T006图片及Flutter加载代码hash保护、范围/diff/记忆链接。仅V0；无build/test/analyze/codegen/安装/运行/打包/Git写入。
- Limits：系统服务管理器与OS RuntimeBroker图标不改；用户自定义悬浮图可覆盖默认；其他平台专有资源不在范围。本地NWG依赖源码缺失，正式编译与Shell/设备显示仍待验证。
- Decision：N/A — no decision delta，沿用ADR-0014；Event CE-20261012-T007-01；STATE-20261012-008 / WORK-20261012-014。
- Detail：docs/tasks/T-2026-10-12-007-icon-coverage.md；本条补充T006历史范围，不改写当时结论。

## T-2026-10-12-008：Android资源错误诊断与脚本结果通道

- Status：completed — scoped source/V0；资源合并根因及构建机验证仍verification-required。
- Source：main/fb5c393（修改图标），initial clean；前轮test/main已同步，本轮11文件未提交。
- Evidence：用户确认旧目录增量构建；AGP7.3.1的source-sets异常与官方同类问题相符，未复现。脚本Missing file混入早先打包标题，则由stdout捕获路径可直接解释。
- Outcome：build.sh split/universal日志送stderr、stdout仅路径、构建失败和调用者保留非零退出，不再失败后选择旧APK。没有自动clean或版本升级，图标不改。
- Verification：bash -n、XML解析、静态输出/失败链、diff/范围与链接；build/test/运行/清缓存/Git全部none。构建机后续clean/retry步骤见task。
- Decision/Event：N/A — no decision delta；CE-20261012-T008-01；STATE-20261012-009 / WORK-20261012-016。
- Detail：docs/tasks/T-2026-10-12-008-android-resource-build.md；源码修正未宣称资源合并已恢复。

### 后续索引更新规则

- 每个 completed、blocked 或 cancelled AI task 追加一条；长任务仅在正式 handoff milestone 时追加。
- 只保存 scope、outcome、state delta、event IDs、verification、decision/ADR、residual risk 和 next gate 的摘要。
- 完整授权、设计、文件、验证与 rollback 仍以 `TASK_HISTORY.md` / Task artifact 为准。
- 历史纠正使用新的 `corrects/supersedes` 条目，不静默删除旧条目。
- 不保存 prompt/transcript、完整 diff、secret、生产地址、peer/device ID、PII 或绝对用户路径。

## T-2026-10-12-009：移除旧 ADB 页面与组件

- 日期：2026-10-12；状态：completed（源码交付/V0，运行验收 pending）。
- Source：main / 13f792d552db49fd1b702ff24cb2b3d0c5030c38；initial clean；T008 先前已提交。
- Authority：当前用户明确要求剔除，含 C2 相关代码/配置和 C3 删除 5 个旧组件源文件；C1 同步当前工程事实。未继承旧 Git/build 权限。
- Outcome：删除 AdbPage、Dart wrapper/constants、11 个 native handler、4 个 Kotlin 模块、36 个 Accessibility 自动化方法及其状态/事件钩子；移除两项专用权限/keep 规则，排除 libadb.so，移除 host adb 检查。未接入新组件。
- Preserve：Windows/Rust 核心/JNI/MainService/DevAutoSelectorController/依赖版本及协议字段不变；其余 native packaging 与 Wi-Fi 保活保留；用户 prefs/配对/系统设置未操作。
- Verification：V0 diff/删除边界、残留/调用扫描、Manifest XML、Bash 仅语法解析、协议字段及保留代码对比。V2/V3 NOT_RUN。
- Decision / Baseline / Event：D-019 / ADR-0015；AS-BL-2026-10-12-13f792d-adb-retired-worktree；CE-20261012-T009-01。
- Docs：当前领域/入口/Skills、Test Matrix、External Registry、ADR/Baseline、全局记忆已同步；旧集成记录标 HISTORICAL/RETIRED，未删除历史。
- Git / build / install / device / release：none；本轮修改未提交。正式验证需求和回滚见 [任务记录](../docs/tasks/T-2026-10-12-009-remove-legacy-adb.md)。

## T-2026-10-13-001：独立客户端 GitHub Actions

- Status：completed — workflow/configuration/V0；runner V2 NOT_RUN。
- Source：main/8662f23，initial clean；新 local ADB 已在 HEAD，本轮不改。
- Outcome：清理12个旧 workflow 与失效 submodule Dependabot；唯一 build-clients.yml 手动选择 Android/Windows/both、aarch64/universal，默认 both+aarch64，Android 临时测试签名，Windows unsigned EXE/ZIP；仅 Actions artifacts，14 天。
- Compatibility：不调用本地 Bash/CMD 入口；build.sh 和历史 F-Droid 解除对删除文件的版本读取；产品源码/manifest/locks/generated bridge 保持。
- Verification：V0 YAML、矩阵、固定SHA、shell/Python/PowerShell语法、引用和diff；没有编译、远端运行、签名、上传或Git写入。
- Decision/Event/State：D-020 / ADR-0016；CE-20261013-T001-01；STATE-20261013-001 / WORK-20261013-002。
- Details：[使用说明](../docs/BUILD_CLIENTS_ACTIONS.md)、[任务](../docs/tasks/T-2026-10-13-001-client-actions.md)、baseline09。临时签名不能跨次覆盖安装，Windows驱动能力/依赖解析/runner兼容仍需正式验证。

## T-2026-10-13-002：修复 Android SDK 初始化

- Source：main/c521601，initial clean；corrects T001 交付后的状态：用户随后明确要求“提交github”，T001 已提交并推送。
- Outcome：两个 Android job 显式 setup-android v3.2.2 / CLI 16.0，配置 PATH/SDK root/许可证；后续 sdkmanager 打印路径/版本并固定安装根目录。仅配置修复，Windows job 和原编译选项保持。
- Evidence：用户报告旧流程 command not found / 127；修复后 V0 YAML/11 Bash/1 Python、步骤顺序/固定 SHA/不变量与 diff/引用检查。runner 未重跑。
- Authority：延续同一会话工作流修复与 Git 提交要求；无远端 dispatch/产品编译/签名/发布。
- Decision/State/Event：D-020 reviewed-no-change；STATE-20261013-002 / WORK-20261013-004 / CE-20261013-T002-01。
- Details：[任务](../docs/tasks/T-2026-10-13-002-android-sdk-setup.md)、baseline10；需用新 SHA 发起 Run workflow。

## T-2026-10-13-003：同步独立本机 ADB 文档

- Source：main/f84a78f、initial clean；8662f23模块无后续差异。
- Outcome：12_LOCAL_ADB集中说明使用、源码/桥接、生命周期、身份、依赖、排查与验证；ADR-0017/D-021补记新模块；Baseline11/External Registry/AND-09—13/入口/Skills/记忆同步。历史LADB正文不重写。
- Authority / verification：当前文档同步请求C1；仅Markdown/V0，无产品代码、Git写入、构建测试或设备操作。V2/V3未提供结果，不标PASS。
- State / Event：STATE-20261013-003 / WORK-20261013-006 / CE-20261013-T003-01；文档交付complete，修改未提交。
- Detail：[当前ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[任务](../docs/tasks/T-2026-10-13-003-local-adb-docs.md)。PC ADB与ADB投屏未实现；新依赖hash/NOTICE/JNI与ROM验证待补。

## T-2026-10-13-004：PC 远程 ADB 与 Android 共享状态

- Record Type / status：current / completed repository implementation；运行待验证。
- Source：main/917e661初始clean，工作树未提交。
- Change：PC顶栏配对/连接/命令/撤销/刷新/断开；专用Misc.remote_adb和独立JNI；Android共享身份/状态、有限队列与owner取消。
- Scope boundary：不改原无障碍/画面/输入处理，不加入ADB投屏，不降低原会话加密；撤销只清App身份，系统列表需手工忘记。
- Evidence：V0静态；ADB-R01—07的V2/V3/V4待正式环境，无Git/构建/设备/发布。
- Decision / event / state：D-022 / ADR-0018；CE-20261013-T004-01；STATE-20261013-004 / WORK-20261013-008。
- Detailed record：docs/tasks/T-2026-10-13-004-remote-adb.md；docs/AI_ENGINEERING/13_REMOTE_ADB.md。

## T-2026-10-13-005：修复远程 ADB Dart 编译失败

- Source：main/de8bc76，initial clean；当前修复未提交。
- Evidence / fix：Android用户job日志三处bind未定义；controller补platform_model.dart显式导入，产品代码仅一行。
- Verification：V0，修复前CI FAIL、修复后NOT_RUN；无Git/build/test/device/dispatch/release。
- Decision / Event：D-022 reviewed-no-change；CE-20261013-T005-01 / STATE-20261013-005 / WORK-20261013-010。
- Detail：docs/tasks/T-2026-10-13-005-adb-bind-import.md。

## T-2026-10-13-006 — ADB pairing and remote usability

- 2026-10-13 / source-complete / V0。main/504adea initial clean；mobile keyboard=N导致ADB被拒已通过独立gate修正；双NSD/一键连接/PC系统QR/独立拖动弹窗/Android UI。
- Source与文档完成；C3提交推送及both/aarch64 CI dispatch已授权，实际回执见本会话最终交付；不监控，不声称V2/V3通过。
- STATE006 / WORK012 / CE-20261013-T006-01 / D-023 / ADR-0019 / Baseline13；Task History与docs/tasks/T-2026-10-13-006-adb-usability.md。无原无障碍/画面/输入/身份/版本/依赖或构建脚本变更。

## T-2026-10-13-007 — 同步 ADB 配对与刷新交互文档

- Source：main/efb2599 + 三份Dart dirty；二维码已在该提交移除，后续UI待交付。本轮不追加源码变更，保护hash。
- Docs：PC无三秒刷新/无预扫描、两端模式控件分离、连接按钮主题色；六位码/手动端口步骤、参数拒绝、取消和时限与多PC快照语义同步。
- Evidence：V0；历史efb2599 push/HTTP204 dispatch已确认，未监控，无新包成功结果。用户本轮追加提交推送/单次both/aarch64构建授权，实际回执见最终答复。
- Records：ADR0020/D024/Baseline14、STATE007/WORK014/CE-20261013-T007-01；R10=N/A，R12—15 NOT_RUN。原链/工具链/依赖保持。
- Detail：docs/tasks/T-2026-10-13-007-adb-docs-sync.md。

## T-2026-10-13-008 — 同步Shell栏与ADB投屏后续交接

- Source：main/aca7446，初始clean；仅Markdown，侧栏八份Dart hash保护。
- Docs：当前Shell栏开关/三键/可用性/生命周期与多PC查询限制；原链保持；投屏为下一阶段待设计，未实施。
- Records：D025 / ADR0020补录 / Baseline15 / STATE008 / WORK016 / CE-20261013-T008-01；R16—19全部NOT_RUN。
- Evidence：V0文档与源码对照/链接/差异/hash，未运行产品build/test/analyze/codegen或设备，不Git/CI操作。
- Detail：[T008](../docs/tasks/T-2026-10-13-008-adb-sidebar-docs-sync.md)。

## T-2026-10-14-001 — 独立 ADB 投屏与触摸优先级

- Source：main/a1486ee，初始clean；当前源码/官方asset/文档未提交。
- Delta：固定scrcpy-server v5.0.1，Android独立投屏与触摸连接，Misc41多PC流控，PC Shell栏开/关和独立画面。ADB触摸最高优先级，作用于原共享/截图；无ADB才用原Accessibility，原采集保持。
- Records：D026 / ADR0021 / Baseline16 / STATE-20261014-001 / WORK-20261014-001 / CE-20261014-T001-01。
- Evidence：V0调用/协议/生命周期/权限与资产hash、范围/文档检查；ADB-M01—M08均NOT_RUN。未编译、真机、Git写入或CI，不宣称投屏性能/ROM兼容已通过。
- Detail：[T001](../docs/tasks/T-2026-10-14-001-adb-mirror.md)、[使用与链路](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)。

- 同任务后续复核：修正旧status回执覆盖推送的epoch时序和停止视频后的decoder/RGB缓存释放。用户新增明确stage/commit/push main、单次both/aarch64 dispatch授权，不监控；CE-20261014-T001-02，STATE/WORK-20261014-002。此记录先于执行，实际Git/dispatch回执见最终交付；未本地构建或真机验证。

## T-2026-10-14-002 — 主界面ADB与连续截图

- Source：main/a0e46b1，初始clean；前轮已推送/CI204且未监控。本轮未提交、无新Git/CI。
- Delta：单源居中/双源分栏，移除ADB浮动外壳；Shell栏同款蓝/红按钮；独立有界系统截图、JPEG格式协商、视频/截图切换和多PC共享停止。原Android采集/无障碍/输入优先级保持，PC视口和弹窗最小适配。
- Records：D027 / ADR0022 / Baseline17 / STATE003 / WORK004 / CE-20261014-T002-01。
- Evidence：V0接口/生命周期/权限/坐标/范围/本地链接检查，正式构建/双PC真机未验证；ADB-M01—M12 NOT_RUN。
- Detail：[T002](../docs/tasks/T-2026-10-14-002-adb-inline-and-screenshot.md)、[当前合同](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)。

- T002续接：用户授权提交/推送当前main并单次触发Build AShell clients both/aarch64，不监控。本地/远端a0e46b1、35路径均本任务，diff检查通过；CE-20261014-T002-02 / STATE004 / WORK005。实际SHA/接受回执见Git/交付，不将dispatch当成构建成功。

## T-2026-10-14-003 — ADB控件与独立黑色遮罩

- main/941b215，初始clean；全窗口状态浮层/双图窄间隔、ADB停止与本机桥超时恢复、独立排除采集的黑色遮罩。原Android采集/无障碍/输入实现保持。
- D028/ADR0023/Baseline18/STATE005/WORK007/CE-20261014-T003-01；V0，源码与记录未提交，构建/真机ADB-M13—M16 NOT_RUN，无Git/CI。
- 详情：[T003](../docs/tasks/T-2026-10-14-003-adb-controls-and-overlay.md)。外部资产/依赖无变化；前轮941b215推送与dispatch204仅为历史执行回执。

- T003续接：用户授权提交/推送当前30路径并执行一次Build AShell clients both/aarch64，不监控。核对本地/远端main均941b215、workflow active；CE-20261014-T003-02 / STATE006 / WORK008。准确提交与接受结果以Git/交付回执为准，无本地产品验证。

## 2026-10-14 / T-2026-10-14-004

- ADB栏严格采用原栏样式参数，普通缩放规则/画质档位同步ADB，物理几何独立传递，去底部标签；普通ADB光标、黑屏触摸、间歇控制恢复；新增独立节点穿透与多PC开关。
- 新helper/R8/JNI/协议20—22，原采集/无障碍/输入路由未修改。V0，未构建/真机/CI/提交；新测试ADB-M17—M20 NOT_RUN。
- D029/ADR0024/Baseline19/STATE007/CE-20261014-T004-01；领域文档/安全/资产评估/任务薄记忆同步。当前源码为404b199上的未提交变更。

- T004续接：复核并修正触摸关闭期限、旧writer隔离、尺寸读取超时；用户授权提交推送当前31路径到main，并执行一次Build AShell clients both/aarch64、不监控。CE-20261014-T004-02 / STATE008 / WORK012；本地/远端404b199、workflow active，V0，最终SHA与dispatch接受回执见Git/交付。

## T-2026-10-14-005：当前ADB合同与修复文档同步

- Record Type：current docs sync + retrospective code-change backfill；source main/7607349，初始clean。
- Outcome：重整12/13/14，纠正旧串行/无push/固定2fps/JPEG本机视频桥/统一stop；补齐独立命令、UID/OPEN修复、状态推送、触摸/模式隔离、节点限制、黑屏白字和无视文案。同步领域导航、验证矩阵、ADR0025/D030/Baseline20及记忆。
- Business Behavior Change：none；仅Markdown，未改产品代码。无新依赖/资产，原有源码保持。
- Verification：V0源码、提交、文档引用/差异范围；产品构建/真机结果仍待证，历史dispatch204不等于通过。
- Authority：用户明确要求文档更新后提交推送；本次不执行编排或监控。最终Git结果见实际提交/会话回执，文档记录不预写成功。
- Detailed record：docs/tasks/T-2026-10-14-005-adb-docs-sync.md；STATE009 / WORK014 / CE-20261014-T005-01。
