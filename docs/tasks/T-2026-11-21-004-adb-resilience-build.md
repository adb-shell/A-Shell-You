# T-2026-11-21-004 — ADB保活与界面文案提交、双端编译

## T0—T4：任务与确认

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
- Git提交、push、CI run/jobs/artifact：待执行后填入。
- V2目标：Android Native/APK和Windows EXE全部完成，编译/既有包内容与测试签名验证成功，产物对应本次完整source SHA。
- V3/V4：ADB-P02—P18、FLT-07视觉交互、Wi-Fi/蜂窝/OEM/重开App、多PC及安全负例NOT_RUN；编译成功也不能证明只在重启后失效。

## T7—T8：文档与交付

当前active/T5；CE-20261121-T004-01，STATE/WORK-20261121-004-r1。完成后同步Build/Debug、TEST_MATRIX、两输入任务交付链接、入口及全局记忆；Decision/ADR/External Registry评估无新决策/资产。产物使用既有临时测试签名，Android不保证覆盖旧安装，卸载会清除数据。最终回执与未验证项待实际结果。
