# T-2026-10-12-008：Android资源合并错误与构建脚本结果通道修正

## T0—T1. Scope / Authority / Baseline

- Request：用户报告mergeReleaseResources失败，并确认是在原目录更新源码后继续编译；当前任务排查并修正可由源码确认的关联脚本错误。
- Source：main/fb5c393fe1e29a2cf805e75847820b315c468b3d，开始时clean；此前图标提交已推送test并快进合并/推送main。STATE-20261012-008是提交前快照，当前Git事实优先。
- Baseline：AS-BL-2026-10-12-c0951b5-pc-build-worktree + 后续fb5c393；旧baseline不改写。ADR沿用0014；N/A — no decision delta。
- Primary：ashell-release-engineer；Android资源静态检查。无子代理。T8 / completed — scoped source/V0；资源合并与正式构建结果verification-required，AND-08 NOT_RUN。
- Scope：build.sh两条Flutter打包函数及其调用者的stdout/失败传播；同步工程说明与记忆。原图及Android图标、Gradle/AGP/Flutter/Kotlin版本和签名配置不改。
- 禁止：执行构建/测试/项目分析/codegen、清理文件/缓存、安装/设备/服务操作、Git写入/推送、部署/签名/发布。前轮Git授权已完成，不扩展到本轮。

## T2. Evidence / Diagnosis

1. 用户日志：app:mergeReleaseResources对merged-not-compiled-resources/release/drawable/abc_btn_colored_material.xml抛出Unable to locate resourceFile in source-sets；缺少完整stacktrace和构建机缓存内容。
2. 本仓flutter/android/settings.gradle固定AGP7.3.1，wrapper为Gradle7.6.4；build.gradle把输出放到flutter/build。app sourceSets只扩展Kotlin/proto，没有自定义res路径。
3. abc_btn_colored_material.xml是AndroidX AppCompat资源（[上游文件](https://raw.githubusercontent.com/androidx/androidx/androidx-main/appcompat/appcompat/src/main/res/drawable/abc_btn_colored_material.xml)），不在本轮新增图标清单。最近图标提交未改Gradle配置或build.sh。
4. Google官方[问题记录240455044](https://issuetracker.google.com/issues/240455044?pli=1)描述相同merged-not-compiled-resources/source-sets错误；[官方修复列表](https://developer.android.com/studio/releases/fixed-bugs/studio/2022.1.1?hl=en)在AGP7.4.0-beta01记录206674992（Flaky build failure in MergeResources task）。结合旧目录增量构建，高度怀疑AGP旧版增量资源状态问题；尚未复现，不能认定已确认根因或保证clean解决。
5. 已确认脚本错误：flutter_build_split_abi_apk / flutter_build_universal_apk把section与flutter build日志写stdout，调用者通过apk_in="$(...)"捕获；sign_apk_manual随后把整段文本当路径检查。用户Missing file后出现更早的打包标题与此一致。
6. 两打包函数依赖全局set -e，但Bash普通命令替换中不能据此保证构建失败立即终止；原流程存在继续pick_existing_apk取到历史APK并掩盖失败的路径。

## T3—T5. Minimal Change / Rollback

- 两函数标题和完整构建输出送stderr；成功stdout仅返回一个APK路径，保留verbose日志可见性。
- Flutter build失败显式保留退出码并return，禁止失败后继续选择旧APK；target映射与两个调用者显式传播失败，签名/成品复制不可越过失败边界。
- 不自动删除缓存、不关闭shrink、不升级工具链、不把依赖资源复制到src/main/res。增量错误先由构建机清理生成目录验证。
- Rollback：未来获准后只反向本任务build.sh与文档patch，不回退图标提交；当前不执行任何恢复/删除/Git动作。

## T6. Verification / 编译验证需求

- 已完成V0：Git Bash --noprofile --norc -n build.sh仅语法解析通过，未执行脚本；两模式stdout/失败路径人工检查、三份图标XML解析、diff/资源/11文件范围及本地文档链接核对。图片/Gradle配置相对HEAD无diff。
- 构建机执行建议（本机不执行）：在确认的实际仓库flutter目录运行flutter clean，再回仓库根运行修正后的./build.sh 1（通用包用2）。clean只作为构建机生成资产清理步骤；保留源码jniLibs、签名文件与仓外依赖，不清全局Gradle缓存。
- 验证目标：mergeReleaseResources重新生成后通过；日志实时显示；Gradle失败保持非零且停止签名，不能出现把日志当APK路径的Missing file。失败需保存第一处完整Caused by与stacktrace，不能只看末尾Missing file。
- 无构建机访问，flutter/build在本地不存在；无法读取该机merger/source-set map或复现本次错误。正式编译/设备结果NOT_RUN。

## T7—T8. Handoff

- 已同步：08_BUILD_SYSTEM、09_DEBUG_SYSTEM、PROJECT_START_HERE、PROJECT_MEMORY/STATE、CURRENT_WORK、CHANGELOG_AI、CHANGE_EVENT_LOG、TASK_HISTORY。
- Event：CE-20261012-T008-01；目标STATE-20261012-009 / WORK-20261012-016。Decision/ADR/外部资产/基线无delta。
- 修改11文件：build.sh、本任务文件、08_BUILD_SYSTEM、09_DEBUG_SYSTEM、PROJECT_START_HERE及六份.codex恢复/事件文件。只声明脚本静态修正完成，AGP异常需要构建机实际结果；没有提交/推送。
