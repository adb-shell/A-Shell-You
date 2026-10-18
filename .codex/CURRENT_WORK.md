# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261018-009`

Last Updated：2026-10-18（ADB 剪贴板同步）

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

T009 / complete / T8（实现与V0）：ADB参数对齐、独立GPU编码和运行时码率/帧率更新；普通源不改。用户授权test提交推送、单次Both/常规编译、不监控。GPU/ROM、双源与多PC实际性能待证，准确Git/CI回执见交付。STATE/WORK-20261018-009 / CE-20261018-T009-01；docs/tasks/T-2026-10-18-009-adb-media-alignment.md。

T008 / complete / T8（源码与V0）：独立ADB媒体/黑屏/命令诊断/弹窗修复，原链路保持；255设备根因、黑屏及流畅度实测待证。用户授权推送test并单次Both/常规编译、不监控；准确Git/dispatch回执见交付，不预写构建通过。STATE/WORK-20261018-008 / CE-20261018-T008-01；见 docs/tasks/T-2026-10-18-008-adb-runtime-performance.md。

T007 合并交付：用户授权将 test/82e0f7f 合并并推送 main，说明“ADB功能问题完善”，单次 Both/常规编译、不监控。祖先/无冲突/合并树 V0 已核对，仅新增最小记录；准确 Git/dispatch 回执见交付，构建/设备待证。CE-20261018-T007-01；见 docs/tasks/T-2026-10-18-007-merge-adb-to-main.md。

T006 r4 交付续接：用户授权提交推送 r3 修复到 test，单次 Build-Clients-New Windows/常规编译，不监控。准确 SHA 与 dispatch 回执见交付；修复后构建/设备待证。CE-20261018-T006-04。

T006 r3 / complete / T8（源码修复与 V0）：按用户 Windows 失败日志修复 ADB 初始剪贴板同步的 E0616，使用现有公开会话信息；单处源码修改，原链路保持。test/de12290 + 本轮 dirty，修复后未构建/提交/推送/编排。CE-20261018-T006-03；见 docs/tasks/T-2026-10-18-006-adb-clipboard-sync.md。

T006 r2 交付续接：用户授权合并提交 T004/T005/T006 并推送 test，单次 Build-Clients-New Both/常规编译、不监控；准确 Git/dispatch 回执见会话交付，V0 与构建/真机待证边界保持。CE-20261018-T006-02。下列为实现阶段记录。

T-2026-10-18-006 / complete / T8（实现与 V0）：补齐仅 ADB 会话复制文本同步，原监听复用，paste=false，自动最新待发/显式优先与权限取消；原无障碍/模拟输入保留，T004/T005 dirty 保留。无 Git/CI/产品验证，构建/真机待证。见 docs/tasks/T-2026-10-18-006-adb-clipboard-sync.md。

T-2026-10-18-005 / complete / T8（实现与 V0）：ADB 普通键盘通过原 KeyEvent 接入独立单 writer，修饰键按 owner 隔离，保留 Ctrl+V；保留 T004 dirty；无 Git/CI/产品验证，构建与设备待证。见 docs/tasks/T-2026-10-18-005-adb-keyboard.md。

T-2026-10-18-004 / complete / T8（实现与 V0）：test/738a3fe clean 起点；Shell 文案“点亮屏”，普通画面光标与触摸同源会话权限，无 ADB 的 Ctrl+V 经现有 Seq 单次提交焦点文本；ADB 优先/权限/媒体/手势保持。最小合同及任务记忆已同步，编译/真机待证，未提交/推送/编排；CE-20261018-T004-01。见 docs/tasks/T-2026-10-18-004-accessibility-paste-cursor.md。

T-2026-10-18-003 / complete / T8（实现与 V0）：取消新增齿轮/设置入口，默认静默常亮；HomePage 恢复 HEAD，原链路不改。r3 用户授权提交推送 test 后单次 Build-Clients-New Both/常规编译，不监控；Git/dispatch 回执见会话交付，构建/真机待证。CE-20261018-T003-03；见 docs/tasks/T-2026-10-18-003-keep-screen-on-settings.md。

T-2026-10-18-002 / complete：test/0b5ad99 clean 起点；仅 ADB 模块添加本应用电池优化豁免、真实状态与 Shell 栏唤醒键，实现与 V0 完成；用户授权提交推送 test、单次 Both/常规编译、不监控。构建/真机待证，准确 Git/dispatch 回执见交付。见 docs/tasks/T-2026-10-18-002-adb-keep-alive-and-wakeup.md。

T-2026-10-18-001 / complete：test/00193fa clean 起点；主页常亮、真实连接判定和 ADB 弹窗回车/确定实现与 V0 完成，保留 Ctrl+V。用户授权提交推送 test 后单次 Both/常规编译，不监控；无本地产品验证，V2/V3 待证，准确 Git/dispatch 回执见交付。见 docs/tasks/T-2026-10-18-001-keep-screen-on-and-adb-input.md。

T-2026-10-17-003 / complete：test/8ef006e clean起点，直接Ctrl+V及ADB媒体参数对齐实现/V0完成；用户授权test提交推送、单次test Both/常规编译不监控。构建/真机待证，Git/dispatch回执见交付。见docs/tasks/T-2026-10-17-003-adb-paste-shortcut.md。

T-2026-10-17-002 / complete：test/af1c72d起点，独立ADB粘贴及媒体性能实现/V0完成；授权提交推送test、单次Build-Clients-New Both/常规编译，不监控；Git/dispatch回执见最终交付。未运行本地产品验证，普通链路保持。STATE/WORK-20261017-002，见docs/tasks/T-2026-10-17-002-adb-input-performance.md。

none。T-2026-10-17-001已完成名称替换及V0检查，按用户授权仅本地提交；不push、不dispatch、不构建。STATE-20261017-001 / CE-20261017-T001-01；[任务](../docs/tasks/T-2026-10-17-001-build-mode-label.md)。

以下为上一任务关闭记录：

none。T-2026-10-16-001文档同步/V0完成；31个Markdown文件、220个本地链接有效、diff检查通过，无业务代码/配置差异。实际commit/push及单次Build AShell clients（both/aarch64）接受结果见Git和会话交付。不监控、不运行本地产品验证。STATE-20261016-001 / CE-20261016-T001-01。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

- T-2026-10-17-001：新工作流选项及九处判断统一改为“原始编译”，默认“常规编译”保持；起点main/130f748、初始clean，V0精确替换通过；仅本地提交，Git结果见交付。
- 当前T-2026-10-16-001：同步4d3c446的App绘图/黑屏字体/PC状态/提示及两次回退边界；ADR0026/D031/Baseline21，初始clean，仅文档。任务：docs/tasks/T-2026-10-16-001-adb-docs-sync.md；产品验证NOT_RUN，Git/dispatch准确回执见交付。
- 历史T005：同步7607349的ADB当前合同/修复记录、ADR0025/D030/Baseline20、验证矩阵及记忆。源码工作区初始clean；仅文档。详细任务：docs/tasks/T-2026-10-14-005-adb-docs-sync.md。
- 下方为此前关闭快照，不代表本次Git/CI权限或当前HEAD。

- T004续接：本地/远端main均404b199，workflow active；V0检查后补齐触摸回收边界并执行上述Git/CI授权。ADB-M17—M20仍待正式结果，dispatch不等于通过。

- 当前：T-2026-10-14-004；Shell栏/尺寸画质/几何/ADB光标/黑屏与触摸恢复/独立节点穿透。V0，14个源码文本结构、协议22字段、81个文档引用与diff检查无异常；并非编译或真机通过。
- 主任务记录：docs/tasks/T-2026-10-14-004-adb-ui-and-input.md；ADB-M17—M20 NOT_RUN；原Android服务/Kotlin/采集JNI/输入路由/无障碍栏/构建脚本无差异。下方T003等为历史关闭快照。

- 当前：T-2026-10-14-003 / T8，布局、ADB控件恢复、独立黑色遮罩；STATE005/WORK007/CE-20261014-T003-01/D028/ADR0023/Baseline18。
- main/941b215初始clean，工作树本任务改动未提交；无本轮Git/CI/产品执行。见docs/tasks/T-2026-10-14-003-adb-controls-and-overlay.md，V0；ADB-M13—M16待正式环境。
- 以下T002为此前关闭记录。

- Task ID：`T-2026-10-14-002`
- Result：主界面单/双源居中、Shell栏同款按钮、独立ADB连续截图和格式协商；原Android采集/无障碍/触摸优先级保持，PC最小视口/弹窗适配。
- Closed At：2026-10-14（Asia/Tokyo）
- Final T-State：`T8 — source and documentation complete`（V0；正式V2/V3/V4待验证）
- State / Event：`STATE-20261014-004` / `CE-20261014-T002-02`（01实现事件保留）
- Decision：D-027 / ADR-0022 / Baseline17；旧记录保留历史。
- Verification：V0源码/协议/JNI/R8/生命周期/坐标与差异范围、本地文档链接和空白；原采集、输入路由及构建依赖保持。无产品build/test/analyze/codegen或设备验证。
- Next Step：用户已明确授权提交/推送本任务main、单次Build AShell clients both/aarch64且不监控；紧接执行，最终SHA/接受结果见Git与会话交付。ADB-M01—M12正式构建/真机结果仍待验证。
- Changelog / Task / Event：CHANGELOG_AI.md、TASK_HISTORY.md、CHANGE_EVENT_LOG.md中同ID。
- Detailed record：docs/tasks/T-2026-10-14-002-adb-inline-and-screenshot.md；main/a0e46b1、initial clean。
