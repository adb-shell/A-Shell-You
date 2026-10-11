# T-2026-10-12-005：统一PC构建入口与产物打包

- Status：completed / T8 / V0；Owner：当前用户；执行：Codex / ashell-release-engineer，ashell-security-engineer静态复核。
- Baseline：test/c0951b5，initial clean；AS-BL-2026-10-12-a036f31-worktree经f423335固化，c0951b5包含T004配置；本轮追加构建策略变化。
- Authority：用户明确要求只保留一个PC脚本，删除旧build.cmd、将new-build.cmd迁为build.cmd，并停止下载/装配额外驱动。原请求即编辑和这两个文件删除/迁移的授权；不执行这些脚本、不编译/测试/analyze/codegen/安装/实际打包，不Git写入或发布。
- Problem：用户日志证明旧new-build主编译成功，装配WindowInjection.dll时停止，尚未到自解压；共用generate.py忽略Cargo失败，旧产物可能被误用。
- Design：保留新入口的C:\DevEnv/C:\DevTool环境与主编译参数；清理受限的Release输出目录后编译，直接压缩该目录。移除外部DLL/驱动、旧暂存目录和未被portable/build.rs使用的Runner.res装配；Brotli只检查、不自动pip安装。共用generator验证输入范围、传播Cargo失败并恢复明确的工作目录语义。
- Compatibility：产品版本/身份、Flutter内部包名、协议和运行逻辑不变。包仍包含Flutter/native编译依赖输出；不承诺完全离线构建。没有附加WindowInjection/Amyuni/打印驱动，不表示需要这些文件的功能仍完整。
- Files：build.cmd、new-build.cmd、libs/portable/generate.py、当前构建入口/工程文档/Skill/外部资产/验证及记忆；历史task和旧baseline保留，追加新指针。
- Recovery：旧脚本均可从c0951b5恢复；未来明确授权后反向恢复本轮patch。实际构建资产当前不删除；脚本中未来运行的清理仅限仓库内固定Release目录，并检查路径和reparse point。
- Verification：V0 Python AST语法与subprocess check=True结构检查、3段内嵌PowerShell只解析不执行、CMD标签目标/打包输入/额外资产调用检索及diff检查。未导入/运行项目脚本；WIN-03/05/06/07/08与正式构建/自解压/启动均NOT_RUN。
- Decision/State：D-018；CE-20261012-T005-01；STATE-20261012-006 / WORK-20261012-010。新基线AS-BL-2026-10-12-c0951b5-pc-build-worktree；当前文档与恢复入口同步，旧baseline/task保留。

## 编译验证需求（本轮不执行）

- 命令：build.cmd。
- 环境：正式Windows x64构建机，C:\DevEnv/C:\DevTool或脚本支持的环境路径；预先安装Python Brotli，具备Rust/Flutter/vcpkg及正常编译依赖。
- 目录：仓库根目录。
- 目标：PC-Bulid/ashell.exe来自本次成功的portable编译；解压内容与本次Release文件一致、无额外驱动；缺外部DLL不中断打包；Cargo失败时CMD非零且不发布旧产物；res/manifest.xml正常与失败路径均恢复；启动与缺驱动功能行为单独核验。
