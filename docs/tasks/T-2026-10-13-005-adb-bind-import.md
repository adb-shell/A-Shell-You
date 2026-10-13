# T-2026-10-13-005 — 修复远程 ADB 缺少 bind 导入

## Metadata / T0

- Date / Requester：2026-10-13，当前用户反馈GitHub Android编译失败并提供job日志。
- Owner：Codex，ashell-release-engineer定位构建阶段、ashell-flutter-engineer核对Dart符号；同一agent静态自审，无独立运行验收。
- Source：main/de8bc768f0a71cab3f60e93f321ae0f65d74c658，initial clean；T004代码与文档已固化，旧记忆“未提交”仅是当时状态。
- Baseline：[AS-BL-2026-10-13-917e661-remote-adb-worktree](../BASELINE/12_REMOTE_ADB_BASELINE_2026-10-13.md)，T004实现如今由de8bc76提交；本任务仅增加一个Dart import，不改该冻结基线。
- Authority：当前故障反馈授权针对性的C2修复、C1证据同步；无本轮Git/build/test/analyze/codegen/device/dispatch/版本/签名/发布授权。
- Related：ADR-0018 / D-022 reviewed-no-change；ADB-R07；EXT-LIB-ADB-004—007 reviewed-no-change。
- State / Event：STATE-20261013-005 / WORK-20261013-010 / CE-20261013-T005-01。
- Status：T8，源码修复完成，正式重编待验证。修复风险low，当前达到V0；编译问题验收需V2，ADB功能仍需V3/V4。

## T1—T2. Evidence and Impact

只读获取用户提供的992行job日志，未保存签名URL/查询token或完整日志。2026-10-13 09:48:20 UTC，Flutter报告remote_adb_controller.dart原44/57/64行 `The getter 'bind' isn't defined`；09:48:26 UTC，`:app:compileFlutterBuildRelease`退出1，Gradle最终报assembleRelease失败。日志给出Flutter stable 3.24.5。ZEGO deprecated/unchecked只是相邻警告，不是此失败原因。

`flutter/lib/models/platform_model.dart:8`定义 `RustdeskImpl get bind => platformFFI.ffiBind;`。controller只import common.dart；common.dart虽然import platform_model.dart，却没有export它，Dart导入不传递。common.dart直接定义的SessionID正常可见。Android编译也会沿导入图解析desktop Dart文件，运行时不展示按钮不能消除静态解析错误。

影响仅controller的编译符号可见性；protocol、Rust/JNI、Android Java、权限、生命周期、视频/输入、身份、依赖/锁文件和CI配置均不变。不采用改Gradle/清缓存/抑制错误绕过。验收标准：同一Android构建不再出现这三处getter错误，且compileFlutterBuildRelease/后续打包真实成功；不得仅据首个错误消失宣称整体通过。

## T3—T5. Design and Change

在controller直接增加 `import '../../models/platform_model.dart' show bind;`，只引入所需公开getter，沿用项目已有FFI实例。无需修改生成bridge/导出接口、依赖或编译脚本。该getter的sessionGetCommon仍为现有Future<String?>合同。

当前故障修复请求是本次C2确认，未扩展其他操作。源码只有一行导入变更，相关文档/记忆保存失败证据与待重编状态。无新增长期决策/外部资产/工具链基线；回退仅审阅此行和本事件文档，去掉导入会恢复原编译错误，无数据迁移或运行副作用。

## T6. Verification / Security / Release

- V0：日志定位、声明/直接import/export可见性、ADB目录同类bind引用、原SessionID来源/FRB签名、diff空白、范围/文档链接检查；完成。
- V1：未运行analyzer/test，项目规则要求执行另行授权；此单行导入不增加复述实现的单元测试。
- V2：用户日志证明修复前Android Flutter编译FAIL；修复后NOT_RUN。未将日志中未出现的Java/R8/其他job宣称通过。
- V3/V4：ADB-R01—07的多PC/权限/设备矩阵仍待验证，当前修复不改变功能合同。
- Security/Privacy：仅公开符号导入，无新权限/日志/数据留存；临时日志签名不落仓。External Registry/ADR/SBOM/license reviewed-no-change。
- Git / build / delete / version / sign / deploy：none；工作树未提交。

《编译验证需求》
- 命令/入口：把修复提交到目标分支后，在 `build-clients.yml` 新建一次相同参数的Android构建；重跑旧commit的job不会包含修复。正式本地Linux可用仓库根 `./build.sh 1`（另需授权）。
- 环境：既有Actions Ubuntu/Flutter3.24.5/Android工具链，不需改版本或清缓存。
- 执行目录：Actions检出仓库，Flutter APK步骤在flutter/。
- 目标：ADB controller三处bind解析成功，compileFlutterBuildRelease和最终APK打包通过，记录新source SHA和日志；后续设备结果单独登记。

## T7—T8. Delivery

已同步13_REMOTE_ADB和全局任务/事件/状态记忆，保留历史快照。源码一行导入修复完成；尚无修复后构建结果，不承诺其他后续编译阶段已经通过。后续由用户提交修复并启动新SHA构建，本轮不自动Git写入或dispatch。
