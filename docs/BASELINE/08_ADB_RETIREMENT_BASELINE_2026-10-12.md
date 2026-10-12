# AShell Legacy ADB Retirement Baseline

- Baseline ID：AS-BL-2026-10-12-13f792d-adb-retired-worktree
- Source Branch / HEAD：main / 13f792d552db49fd1b702ff24cb2b3d0c5030c38
- Initial worktree：clean；本任务修改尚未提交
- Task / Event / Decision：T-2026-10-12-009 / CE-20261012-T009-01 / D-019 / ADR-0015
- Evidence：repository-observed / V0；formal build and runtime VERIFICATION-REQUIRED

产品身份、版本、工具链、依赖锁、JNI 和协议字段继承源 HEAD。此前 PC 入口/Release-only 策略已由 c9b7cd1 固化，图标由 fb5c393 固化，T008 Android 脚本修正已包含在本次 HEAD；不重写这些历史基线。

本快照移除旧 Android ADB 页面/桥接/执行/自动化模块与两项专用权限，排除旧 libadb.so，移除未使用的 host adb 检查。Windows 没有旧 ADB 运行实现命中，其代码/构建入口保持。旧 ADB 的外部资产标为 retired，不再要求补齐为当前构建输入；原来源/许可记录仍保存。

执行过 V0 源码差异/残留检查、Manifest XML 解析和 build.sh 仅语法解析。没有 build/test/analyze/codegen、安装、清构建缓存、Git 写入、设备或发布操作。该记录不是通过构建的产物基线，APK 中库排除及其他功能回归仍待正式环境验证。
