# T-2026-11-28-001 — ADB包名修复合入main并对齐test

## T0—T4：授权、基线与设计

- 2026-11-28 Asia/Tokyo；用户要求“合并到main，然后推送，提示‘修复ADB的包名问题’，完成后切回test等待工作”。本次明确授权正常merge/commit/push；沿用每次合并后本地/远端test同步最新main的长期要求。
- 当前complete/T8（合并推送与分支对齐）。执行/Primary：Codex、openworld-master与openworld-release-engineer；Product/Release owner：用户；无新安全语义，沿用T001静态审查。按TASK_TEMPLATE建立，Risk low（已有源码合并），最高本轮验证V0；产品V2/V3结果不提升。
- 输入：main/e3fd784cdc8f761107bcb00094a23a9361f6f4e7，test/a736146958fa218c0d900a0a4ad57386b900e5b6；本地远端一致，两工作区clean、无活动任务，main/test独有0/2。
- 两提交为e1526fa源码修复及a736146纯文档回执，净31文件436增/62删。版本文件与CI无差异；产品包括真实ProviderInfo/authority和入口类、激活完成取消免误停、阶段错误与双端提示。
- Baseline：OW-BL-2026-11-21-956ecf4-embedded-adb-worktree / [Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)；ADR0030/D-038与ADB-P19—P22，既有外部资产保持。STATE/WORK-20261128-001-r2 / CE-20261128-T001-01/02。
- 使用现有main工作区 `C:/Code/A-Shell-You-main-adb-20261113` 合并，主工作区 `C:/Code/A-Shell-You` 保留test；不创建/删除worktree，不stash/reset，保留既有恢复点。
- 为保留用户指定说明，采用no-ff合并，提交标题精确为“修复ADB的包名问题”；暂存前核对非Markdown产品tree与test输入一致。随后test快进main、推送两支并核对SHA/0-0。
- 授权包含必要任务/回执文档及其stage/commit/push。禁止新产品修改、强推/history rewrite、版本变化、编译/监控、设备、签名/发布；e1526fa双编译不查询结果，继续unknown。
- 回滚：保留起点与全部历史，如需撤回另行正常revert，不自动reset/force-push。出现非预期用户变更或合并冲突时先保护现有工作。

## T5—T6：执行与验证

起点status/remote/祖先关系、净差异和版本/CI不变已核对。main执行 `git merge --no-ff --no-commit test` 无冲突，暂存tree最初与test/a736146完全一致；补入4份任务登记文档后，非Markdown产品tree仍相同，diff检查通过。

合并提交 `3c520c71c484a5e4e7ebdfc8f85286b600ade8de`，标题精确为“修复ADB的包名问题”，两个parents为main/e3fd784、test/a736146。主工作区test通过 `git merge --ff-only main` 对齐；`git push --atomic origin main test`成功。随后ls-remote确认远端两支均3c520c7，本地ahead/behind 0/0、完整tree一致，两工作区clean，主工作区为test。

本轮未触发或查询CI，e1526fa双编译结果继续unknown，设备/改包/蜂窝/OEM验收仍NOT_RUN。版本Rust/portable2.0.26、Flutter2.0.26+59保持；Git合并不提升运行验证等级。

## T7—T8：文档与交付

本任务、STATE/WORK/Event、入口、16_EMBEDDED_ADB及全局任务记忆已同步，complete/T8；Decision/ADR/Baseline/External Registry/Build/Debug/TEST_MATRIX reviewed-no-change。本回执纯Markdown补交test，并将main快进到同一回执提交后原子推送两支；最终SHA以Git核对为准，不改变产品或再次编排。主工作区保留test等待工作，不能只切分支而保留旧基点。
