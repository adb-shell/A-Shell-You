# T-2026-10-18-007：ADB 功能合并主分支

## T0—T4：授权、基线与方案

- 用户要求将 test 合并到主分支、提交说明“ADB功能问题完善”、推送后构建一次；“mian”按仓库实际主分支 main 处理，延续不监控要求。Release owner；无委派或新增产品开发。
- 起点 test/82e0f7fb56eabc4998cfe92134f6c7a8519c122a、clean；本地 main/af1c72d，远端 main/130f748。fetch 后确认本地 main、远端 main 均为 test 祖先，test 与 origin/test 一致；无其他活动任务。
- Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0025/0026、D031 保持。授权 C3 fetch/switch/merge/add/commit/push 及单次工作流；无强推、版本/依赖/工作流修改、本地产品构建或设备操作。
- 方案：main 上以 --no-ff 合并 test，保留 test 七项提交与既有构建模式名称提交；只补最小交付记录，合并提交使用用户指定说明。推送 origin/main 后触发 Build-Clients-New.yml，ref=main、Platform=Both、BuildMode=常规编译。
- 影响：继承 test 已有 ADB 输入/剪贴板/媒体及常亮相关修复；本轮不新增业务代码、协议、JNI、权限或资产变更。备选 fast-forward 不会产生用户指定的合并说明，因此采用显式合并提交。回滚需另行授权反向合并，不改写历史。

## T5—T8：验证与交付边界

- 已无冲突合并；写入记录前暂存树与 test 完全一致，git diff --cached --check 通过。最终提交前再核对仅六份任务/记忆记录与 test 存在差异。
- V0 范围/祖先/合并树检查；RST-01 与 WIN-08、Android 构建由本次 CI 验证，结果不监控；此前 Windows 编排仅有接受回执，不能视为通过。设备功能仍待实测。
- 《编译验证需求》：GitHub Actions Build AShell Clients-New/main/Both/常规编译，单次运行；无需本地命令。准确 SHA、远端一致性与 dispatch 接受回执见 Git/会话交付。
- STATE/WORK-20261018-007 / CE-20261018-T007-01；Decision/ADR/领域合同/External Asset Registry：N/A — no decision or contract delta。同步本任务与五份最小记忆；记录写于提交前，不预写构建成功。
