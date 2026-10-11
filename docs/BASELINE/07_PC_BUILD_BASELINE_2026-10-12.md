# PC Build Baseline — 2026-10-12

- ID：AS-BL-2026-10-12-c0951b5-pc-build-worktree。
- Source：test/c0951b5加T005未提交工作区；此前产品身份和服务器配置已固化在Git。旧01—06基线保持历史含义。
- Entry：唯一根目录PC入口build.cmd，来自原新环境脚本；旧build.cmd被替换、new-build.cmd不再存在。编译参数与C:\DevEnv/C:\DevTool布局保留。
- Payload：清理固定Release目录后编译，直接压缩本次Release；native Flutter依赖和本仓生成的dylib_virtual_display.dll保留。无附加WindowInjection、Amyuni、打印驱动或外部Driver.ps1。
- Output：PC-Bulid/ashell.exe；workspace Cargo输出固定target；Brotli预装，脚本不自动pip安装。
- Failures：路径范围/存在性检查，Cargo错误传给CMD，检查packer存在与非空，manifest备份恢复。旧输出可能保留但失败时不会报告新交付成功。
- Evidence：V0，正式构建/自解压/启动NOT_RUN。旧用户日志仅证明变更前主编译成功、缺DLL时停止；不是本基线构建证明。
- References：T-2026-10-12-005 / D-018 / CE-20261012-T005-01；08_BUILD_SYSTEM、TEST_MATRIX、EXTERNAL_ASSET_REGISTRY。
