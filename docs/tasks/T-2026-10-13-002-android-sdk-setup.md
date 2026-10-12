# T-2026-10-13-002：Android Actions SDK 初始化修复

## T0—T4. Brief / Impact / Authority

- Template：`TASK_TEMPLATE.md`；日期：2026-10-13；owner：Codex / ashell-release-engineer，Security 由同一 Codex 复核。
- Source：main / c52160116e1f6f582aba64fb05465c972a5d0673；initial clean；恢复 STATE-20261013-001 / WORK-20261013-002，旧记忆中的“未提交”已由同一会话后续提交完成。
- Baseline：AS-BL-2026-10-13-8662f23-client-actions-worktree；新增 CLI 固定版本快照，不覆盖旧 baseline。
- 用户反馈 native job 在首条 sdkmanager 命令以 command not found / 127 失败。日志未提供 run URL、源 SHA 或 ABI，因此不推测完整运行记录或其他 job 结果。
- 验收：两个 Android job 在首次 sdkmanager 调用前显式准备 SDK command-line tools、PATH 和统一 SDK 根路径；原平台/ABI/测试签名/Actions artifact 行为保持。
- Root cause（source verified + user-reported log）：c521601 的 native 和 APK job 均直接调用 sdkmanager，却没有初始化 Android SDK；当前 runner PATH 没有该命令。失败在 NDK 下载前，与 NDK 编译兼容性无关。
- Scope：仅工作流 SDK 初始化、相关文档/记忆；无业务、JNI、协议、Windows job、本地构建脚本和锁文件修改。
- Authority：延续用户独立 Actions 实现及同一会话“提交github”要求，完成修复、V0、对应 commit/push 到既有 origin/main。未请求远端 dispatch、产品 build/test/codegen、生产签名或发布，本轮不执行这些动作。
- Design：两 job 的 JDK 17 setup 后使用固定 SHA 的 setup-android v3.2.2、固定 CLI 12266719 / 16.0，显式仅安装 platform-tools，避免 action 旧默认 tools 包；action 接受 licenses 并导出 PATH/ANDROID_HOME/ANDROID_SDK_ROOT。后续 sdkmanager 显式指定同一 sdk_root。
- Alternative：只拼接 runner 预装路径无法处理 CLI 未安装或目录变化，故不采用。
- Security：新增 action SHA 经上游 GitHub tag API 和源码核对；下载来自 action 指定的 Google Android repository；无新 secrets/写权限/产物范围。固定版本不等于完整供应链或 runtime 验证。
- Related：D-020 / ADR-0016（实现修复，无长期决策变化）；EXT-BUILD-004、EXT-CI-001；CI-01、CI-02、CI-04。Rollback：按 c521601 对应路径审阅逆向 patch；不整仓 reset。

## T5. Change Record

- 两个 Android job 新增 SDK setup；去掉重复 license shell 命令；打印 sdkmanager 路径/版本；NDK/SDK 安装固定 sdk_root。
- Android build-info 增加 command-line tools 版本，便于定位运行环境。

## T6. Verification

- V0 PASS：YAML、11 段 Bash 语法、1 段 inline Python AST；两个 job 的 JDK → SDK setup → sdkmanager 顺序、固定 CLI/Action SHA、sdk_root、无重复 license 命令、权限/输入/矩阵不变；Windows job 与 c521601 完全相同。仅使用会话既有临时 YAML 解析器，无项目依赖安装。
- 用户提供的是修复前 runner 初始化失败证据；修复后 runner 构建尚未执行，不能宣称编译成功。
- 《编译验证需求》：修复推送后，从 Actions → Build AShell clients → Run workflow 选择 main，先 android+aarch64，再 android+universal；环境为 workflow 的 Ubuntu 22.04，执行目录为 checkout 根目录。观察两个 job 中 sdkmanager 路径/16.0、NDK 安装、后续 native/APK 构建及 artifacts。不要 Re-run 旧失败记录来验证新源码，因为它仍使用原运行 SHA。

## T7—T8. Handoff

- Status：T8 — configuration/V0 complete；构建说明、debug、registry、matrix、baseline10 和全局记忆已同步，文档引用与 diff 纳入提交前核对。
- Git 交付：沿用同一会话要求提交并推送至 origin/main；本记录在提交前写入，最终 commit/push 成败以 Git 回执为准。没有执行 runner/product build、签名或发布。
- Event：CE-20261013-T002-01；Decision：reviewed-no-change；产品版本和原编译选项保持。
