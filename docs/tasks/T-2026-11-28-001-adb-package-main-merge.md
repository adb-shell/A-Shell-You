# T-2026-11-28-001 — ADB包名修复合入main并对齐test

## T0—T4：授权、基线与设计

- 2026-11-28 Asia/Tokyo；用户要求“合并到main，然后推送，提示‘修复ADB的包名问题’，完成后切回test等待工作”。本次明确授权正常merge/commit/push；沿用每次合并后本地/远端test同步最新main的长期要求。
- 当前implementing/T5。执行/Primary：Codex、openworld-master与openworld-release-engineer；Product/Release owner：用户；无新安全语义，沿用T001静态审查。按TASK_TEMPLATE建立，Risk low（已有源码合并），最高本轮验证V0；产品V2/V3结果不提升。
- 输入：main/e3fd784cdc8f761107bcb00094a23a9361f6f4e7，test/a736146958fa218c0d900a0a4ad57386b900e5b6；本地远端一致，两工作区clean、无活动任务，main/test独有0/2。
- 两提交为e1526fa源码修复及a736146纯文档回执，净31文件436增/62删。版本文件与CI无差异；产品包括真实ProviderInfo/authority和入口类、激活完成取消免误停、阶段错误与双端提示。
- Baseline：OW-BL-2026-11-21-956ecf4-embedded-adb-worktree / [Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)；ADR0030/D-038与ADB-P19—P22，既有外部资产保持。STATE/WORK-20261128-001-r1 / CE-20261128-T001-01。
- 使用现有main工作区 `C:/Code/A-Shell-You-main-adb-20261113` 合并，主工作区 `C:/Code/A-Shell-You` 保留test；不创建/删除worktree，不stash/reset，保留既有恢复点。
- 为保留用户指定说明，采用no-ff合并，提交标题精确为“修复ADB的包名问题”；暂存前核对非Markdown产品tree与test输入一致。随后test快进main、推送两支并核对SHA/0-0。
- 授权包含必要任务/回执文档及其stage/commit/push。禁止新产品修改、强推/history rewrite、版本变化、编译/监控、设备、签名/发布；e1526fa双编译不查询结果，继续unknown。
- 回滚：保留起点与全部历史，如需撤回另行正常revert，不自动reset/force-push。出现非预期用户变更或合并冲突时先保护现有工作。

## T5—T6：执行与验证

起点status/remote/祖先关系、净差异和版本/CI不变已核对。main执行 `git merge --no-ff --no-commit test` 无冲突，待核对并创建指定说明的提交；推送与最终对齐结果待回填。不以Git合并成功代替编译或设备验收。

## T7—T8：文档与交付

登记本任务、STATE/WORK/Event，完成后同步入口、16_EMBEDDED_ADB与全局任务记忆；Decision/ADR/Baseline/External Registry/Build/Debug/TEST_MATRIX reviewed-no-change。回执只改Markdown，两支最终保持同SHA与clean，主工作区停留test等待工作，不再次触发CI。
