# T-2026-11-21-005 — 内置ADB合并main并同步test

## T0—T4：恢复、范围与确认

- 用户当前明确要求“合并到mian，推送到github”；按仓库现有分支识别为`test → main`，不创建拼写错误的新分支。用户本会话此前明确要求每次合并后test保持最新main基线，纳入本次本地/远端对齐。
- 起点：test/e82f8dfdc0a743c51e15be49ccd89ae38fc3ad05，main/956ecf482ac173af710aa7b6ea0b9de2bc3b4d60，本地与远端一致；两个工作区clean，CURRENT_WORK无active任务，state-consistent。
- main工作区：`C:/Code/A-Shell-You-main-adb-20261113`；test工作区：`C:/Code/A-Shell-You`。使用现有worktree，不重置或删除资产；既有stash恢复点保持。
- 输入为febf7f0、e137d44、b12cbdb、e82f8df四提交，包含内置权限服务、保活恢复和双端文案及文档回执。main独有0/test独有4，净64文件2533增/316删；版本文件和CI工作流无差异。
- Baseline：`OW-BL-2026-11-21-956ecf4-embedded-adb-worktree`，见[Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)；本任务只提升既有提交到main，冻结记录不改。
- Skill：openworld-master，沿用openworld-release-engineer交付边界；ADR-0030/D-038，无新架构/依赖/外部资产决定。
- 方案：先登记任务，现有main工作区`merge --ff-only test`，核对完整tree与源test相同后推送main；回填回执文档，保持main/test最终本地和远端同SHA、ahead/behind 0/0。主工作区保持test供后续修改。
- 授权包含本批合并、stage/commit交付文档、推送及分支对齐。非目标：新产品改动、版本变化、重写历史、强推、删移、编译/监控、设备或生产发布。当前不监控要求继续有效，run44890553076最终结果保持unknown。
- 回滚：保留两个起点SHA与既有历史；如需回退，另按当时授权使用正常revert，不reset/force-push。

## T5—T6：执行与验证

- V0：起点/远端SHA与clean状态、0/4关系、完整差异和版本/CI不变已核对。现有main工作区`git merge --ff-only test`成功，main从956ecf4快进到e82f8df，无冲突、无额外merge commit；`git diff --exit-code main test`为0，完整tree一致。
- `git push origin main`成功；随后`git ls-remote`证实远端main/test均为`e82f8dfdc0a743c51e15be49ccd89ae38fc3ad05`，本地ahead/behind为0/0。test工作区始终保留test，无需切换到被另一worktree占用的main。
- 验证目标：快进无冲突，合并tree等于test输入；回执仅Markdown；最终main/test本地远端同SHA、0/0，两个工作区clean。
- 无本轮V1—V5。T001旧构建成功只覆盖febf7f0；b12cbdb构建结果未知，设备/OEM/蜂窝/重开App及安全负例仍NOT_RUN，合并不提升验证等级。

## T7—T8：文档与交付

complete/T8（合并推送范围），STATE/WORK-20261121-005-r2 / CE-20261121-T005-01/02。入口、内置ADB文档及Project State/Current Work/Memory/Task History/Changelog/Event已同步。Decision/ADR/Baseline/External Registry/Build/Debug/Test Matrix reviewed-no-change，无新增语义或验证结论；保留原冻结证据，未新建决策ID。

本回执作为纯Markdown提交补入两支，采用快进及同次推送，最终SHA以Git/远端核对为准；不改变产品源码或再次编排。最终核对包括完整tree一致、两支同SHA/0-0、两工作区clean，主工作区仍在test。Rust/portable2.0.26、Flutter2.0.26+59保持；本轮未触发或查询CI，b12cbdb构建结果继续unknown，不宣称运行验收通过。
