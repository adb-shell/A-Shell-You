# T-2026-11-21-004 — ADB保活与界面文案提交、双端编译

## T0—T4：任务与确认

- 最新范围调整：2026-11-21 18:40—18:41 Asia/Tokyo用户补充“不进行监控”，立即停止后续run/jobs/logs/artifacts查询；不取消已经启动的GitHub构建。最终交付范围调整为提交推送、触发及回执，编译结果未知。
- 当前请求：用户明确要求“把这次ADB保活和界面文案的修改一起提交推送到test，然后执行Android和PC双编译，完成后告诉我结果”。本请求授权本批stage/commit/push、现有Both常规CI及等待结果和交付记录；不依赖历史授权。
- 起点：2026-11-21 18:37 Asia/Tokyo，test/e137d4413d04385bfe16c43a87897cf5e0be54c3；本地/远端test一致，远端main为956ecf482ac173af710aa7b6ea0b9de2bc3b4d60。工作区31个路径均属于已关闭T002/T003：7份Java、4份Dart及相关文档；无暂存、无活动并行任务。恢复状态consistent。
- 输入：[T002保活](T-2026-11-21-002-embedded-adb-resilience.md)、[T003文案](T-2026-11-21-003-adb-ui-copy.md)；两批修改完整保留。本任务只负责交付与构建证据，不新增业务改动。
- Baseline：`OW-BL-2026-11-21-956ecf4-embedded-adb-worktree`，见[Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)，冻结记录不覆写。
- Skill：openworld-release-engineer；沿用D-038/ADR-0030，N/A — no decision delta。
- 影响：Android Java恢复/保活与双端Dart界面；Rust/JNI/Kotlin/Manifest/协议/工具链/服务器配置及版本2.0.26、2.0.26+59保持。
- 方案：核对差异→提交推送test→核对远端完整SHA→单次dispatch→跟踪准确head_sha的jobs/logs/artifacts→记录结果并补交文档。禁止强推、main合并、版本变化、生产签名/发布、设备操作。
- CI：`.github/workflows/Build-Clients-BT.yml`，`ref=test`、`Platform=Both`、`BuildMode=常规编译`。现有Ubuntu22.04/Windows2022 hosted runners；Android arm64-v8a与Windows x64，既有临时测试签名/打包/Actions产物上传属于该测试构建流程。GitHub插件无dispatch能力，触发使用既有Git凭据的GitHub REST API，凭据仅内存使用、不输出或落盘；结果优先GitHub插件读取。
- 停止边界：报告真实成功/失败及其证据；CI受理不等于成功。失败先定位，不扩大为版本/依赖/生产改动。回滚需独立授权revert本批提交；当前保留原HEAD及既有stash恢复点。

## T5—T6：提交与验证

- V0：`git diff --check`通过，当前远端test与本地HEAD一致；输入两任务已有静态检查记录，未冒充本轮运行验证。
- 源码提交：`b12cbdb8f216cc27491cae478d9accfb1898ab24`，说明“完善内置ADB保活并精简双端界面文案”，32文件757增/224删；提交和push均exit0，远端test完整SHA核对一致，触发前工作区clean。
- 2026-11-21 09:38:40 UTC / 18:38:40 Asia/Tokyo单次dispatch，HTTP204；[run44890553076](https://github.com/adb-shell/A-Shell-You/actions/runs/44890553076)，attempt1，workflow394791509，参数Both/常规编译，API的head_sha精确匹配b12cbdb。
- 停止监控前最后观察为09:40:22 UTC / 18:40:22 Asia/Tokyo：Android Native（job136805214513）与Windows EXE（job136805214331）均in_progress、正在准备依赖/工具链；未取得APK阶段及最终结果。用户补充后不再查询运行/日志/产物，不取消、不重触发。未下载任何本次产物，V2结论unknown，不宣称PASS。
- 原V2验收目标供后续人工检查：Android Native/APK和Windows EXE全部完成，编译/既有包内容与测试签名验证成功，产物对应本次完整source SHA。
- V3/V4：ADB-P02—P18、FLT-07视觉交互、Wi-Fi/蜂窝/OEM/重开App、多PC及安全负例NOT_RUN；编译成功也不能证明只在重启后失效。

## T7—T8：文档与交付

complete/T8仅关闭用户调整后的Git交付与触发范围；CE-20261121-T004-01—03，STATE/WORK-20261121-004-r3。Build/Debug、TEST_MATRIX、两输入任务交付链接、入口及全局记忆已同步；Decision/ADR/External Registry reviewed-no-change，无新决策/资产，Baseline29冻结保留。

本批回执文档随原交付授权补交test，不改产品源码、不再次编译。main与版本保持，既有stash恢复点保留；不含生产发布或设备操作。既有CI产物使用临时测试签名，Android不保证覆盖旧安装，卸载会清除数据。编译最终结果、成品链接/哈希和运行验收均未取得，用户可自行打开本次run查看。
