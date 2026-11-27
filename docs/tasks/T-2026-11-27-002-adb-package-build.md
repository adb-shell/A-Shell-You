# T-2026-11-27-002 — ADB 改包与切网修复提交推送、双编译

## T0 / Metadata / Authority

2026-11-27 Asia/Tokyo；Requester/Product/Release owner：用户；执行：Codex / openworld-release-engineer，安全与领域静态核对沿用T001。按TASK_TEMPLATE建立，当前 complete/T8（提交推送与单次编排请求）。输入test/e3fd784cdc8f761107bcb00094a23a9361f6f4e7，27个待交付文件全部为上一轮T001改动（12产品、15文档），初始index空；本地与远端main/test均同SHA，无并行活动任务、无Git hooks。

- Baseline：OW-BL-2026-11-21-956ecf4-embedded-adb-worktree / [Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)，历史冻结；ADR0030 / D-038，无新决定。
- 用户当前明确授权“提交并推送，然后执行双编译”：stage/commit/push origin/test、单次Build-Clients-BT workflow_dispatch。沿用同会话“不进行监控”，触发后不查询新run/jobs/logs/artifacts，不自动重试dispatch。
- C0核查与C1回执文档、上述C3 Git/测试构建已授权。既有workflow临时测试签名、打包与Actions artifacts属于本次既有流程；不涉及生产发布、稳定签名、版本/依赖变化、main合并、强推、删除、设备操作。
- STATE/WORK-20261127-002-r2 / CE-20261127-T002-01/02。Risk：medium（交付已有源码，测试CI）；所需构建V2，结果unknown，设备V3/V4仍NOT_RUN。External：既有GitHub Actions及测试工具链，无新增资产。

## T1 / T2 — Scope and Impact

交付[T001](T-2026-11-27-001-adb-package-reconnect.md)的实际ProviderInfo/authority与入口类适配、ready后取消免误停、阶段错误及双端提示。Android/Flutter源码不再扩大；Rust/JNI/peer字段40/41、Windows产品机制、API、版本保持。确认全量改动属于本任务，不覆盖用户其他工作。

验收：提交推送准确源码→核实origin/test完整SHA→触发Both/常规编译一次并记录HTTP受理。未监控时编译成功/失败保持unknown，不能以HTTP204代替编译通过。

## T3 / T4 — Plan and Confirmation

- 核对diff、远端、当前CI→提交并push test→只读核实远端SHA→单次POST workflow_dispatch→补交纯文档回执，不再次触发CI。
- Workflow：`.github/workflows/Build-Clients-BT.yml`，ref=test、Platform=Both、BuildMode=常规编译；Android arm64-v8a / Ubuntu22.04，Windows x64 / Windows2022，使用既有固定工具链和临时测试签名，非本机build.sh/build.cmd。
- GitHub插件无dispatch能力；使用现有Git凭据内存调用GitHub REST，仅输出HTTP状态/时间，不输出或落盘凭据。无重复授权问题，用户已点名动作；网络失败不盲目重复触发。
- 回滚：无强推/history rewrite，必要时另行授权revert；本批纯文档回执不改变构建产品输入。版本2.0.26 / 2.0.26+59。

## T5 / T6 — Execution and Verification

提交前git diff --check及22处新/改本地Markdown链接核对通过，精确stage范围28文件（T001的27文件加本任务），无额外改动。提交 `e1526fa2a6fa2b688d965bb56ba6b422f5ac9a07`，说明“修复内置ADB改包兼容和激活后断线误停”，28文件403增/63删；push origin test成功，ls-remote及API均核实远端SHA一致，触发前工作区clean。

2026-11-27 **14:21:49 UTC / 23:21:49 Asia/Tokyo** 单次POST workflow394791509 / Build-Clients-BT，ref=test、Platform=Both、BuildMode=常规编译，14:21:50 UTC返回HTTP204。触发前核实workflow路径/state=active及test准确SHA；GitHub插件不支持该workflow元数据URL，改用现有Git凭据内存REST，未输出或落盘凭据。

按既有不监控要求，未查询新run/jobs/logs/artifacts，未取消或重复触发；**最终编译/产物结果unknown**，没有可核实run ID，不将HTTP204称为构建成功。[工作流入口](https://github.com/adb-shell/A-Shell-You/actions/workflows/Build-Clients-BT.yml)。T001 V0保持，本次未运行本机产品测试/设备，不新增源码修改。

## T7 — Documentation / Memory

新增本任务，更新STATE/WORK与Change Event；交付后同步T001指针、Build/Debug/Actions、内置ADB合同、TEST_MATRIX及任务全局摘要。ADR/Decision/Baseline/External Registry reviewed-no-change，无新决策或资产，不提升既有设备结论。

## T8 — Handoff

Git推送与单次编排请求已完成，本轮关闭；回执只改Markdown并补交test，不再次dispatch，构建产品输入仍为e1526fa。main、2.0.26 / 2.0.26+59保持。正式改包/蜂窝/重开App测试仍按ADB-P19—P22及T001需求执行，本轮不监控、不宣称构建成功。
