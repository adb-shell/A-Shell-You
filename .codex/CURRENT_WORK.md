# AShell Current Work

Schema Version：`1.0`  
Registry Revision：`WORK-20261014-012`

Last Updated：2026-10-14（ADB样式/几何/触摸/节点穿透）

Active Task Count：0

> 本文件是支持 0..N 个并行 Codex 会话的协作 registry，不是锁、不是真相层，也不是授权凭证。任何新会话都必须重新执行 T0；历史 C1/C2/C3 不能继承。每个会话只更新自己的 Task ID 段，并在写入前重新读取 revision，防止覆盖其他会话。

## 1. Active Tasks

none。T-2026-10-14-004复核完成，触摸重复停止/旧Run隔离/尺寸读取超时补齐；STATE008 / CE-20261014-T004-02。用户已授权将当前31路径提交推送main，单次Build AShell clients both/aarch64、不监控；此为执行前关闭记录，实际SHA与接受回执见Git和最终交付。仅V0，未本地产品build/test/analyze/codegen或真机验证。

## 2. Concurrent Session Rules

- Different Task IDs may coexist only when file/module scope is separable and ownership is explicit.
- A second session seeing the same active Task ID may recover context, but must not resume T5 until the user confirms continuation and T0 is revalidated.
- If task scope overlaps another active row, stop at the concurrency gate and request coordination；do not overwrite or silently assume ownership.
- Missing event references、Baseline/HEAD drift、contradictory T-state or an uncertain owner changes status to `stale-review-required` or `conflict`；time alone does not prove stale.
- Closed rows are not deleted as an event；their detailed history moves to `CHANGELOG_AI.md` and `TASK_HISTORY.md`, while this file keeps only the latest closed pointer.

## 3. Allowed Status Values

`active / awaiting-confirmation / blocked / paused / handoff / stale-review-required / conflict / complete / cancelled / none`

## 4. Last Closed Task

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
