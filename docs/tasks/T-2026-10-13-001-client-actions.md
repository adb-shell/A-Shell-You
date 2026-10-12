# T-2026-10-13-001：独立 Android / Windows GitHub Actions

## 0. Task Metadata

- Status / T-State：complete — configuration/V0 / T8；Created / Updated：2026-10-13。
- Requester / Product Owner：当前项目用户；Principal / Primary Skill：Codex / ashell-release-engineer；Security reviewer：同一 Codex 按 ashell-security-engineer 约束复核；Release owner：项目用户。
- Source：main / 8662f2369a33f5286c20f9de4d640d2072175ca7；initial clean，无并行活动任务。
- Recovered State / Work：STATE-20261012-010 / WORK-20261012-018；当前登记 WORK-20261013-001。
- Baseline：AS-BL-2026-10-12-13f792d-adb-retired-worktree；本次追加独立 CI baseline，不覆盖旧快照。
- Risk：medium；本轮 V0，真实构建需 V2；cases：AND-06/08、WIN-05/08、FLT-05、RST-01/03/06、新增 CI-01—04。
- Related：ADR-0010/0014/0015、D-018；EXT-CI-001、EXT-BUILD-*、EXT-SIGN-*、EXT-WIN-*。
- Event：CE-20261013-T001-01。

## T0. Authority and Baseline

- Recovery：state-drift 可解释；T009 已进入 03aec41，此后 c81d9f7 更新配置，8662f23 新增隔离本地 ADB；不沿用旧文档“尚无新组件”的描述，不修改新组件。
- 当前用户明确授权新增独立 CI、移除无用 GitHub 编排、平台/ABI 选择、测试签名和仅 Actions artifact 的设计。原请求作为 C2 与指定旧编排删除授权，无需重复请求。
- C0/C1：源码/官方 Actions 文档只读核对及必要文档同步；C2：工作流和删除后元数据依赖兼容；C3 delete：下面列出的旧编排。历史权限不继承。
- 未授权且不执行：Git 写入、依赖安装、项目 build/test/analyze/codegen、实际签名/打包、远端 dispatch/上传/发布、production/secret 操作、版本修改。
- Stop：意外用户修改或必需外部私有资产；不擅自升级产品依赖以隐藏失败。

## T1. Requirements

1. 一个 workflow_dispatch 入口可选 android/windows/both；默认 both。
2. Android 可选 aarch64/universal；默认 aarch64；universal 为 arm64-v8a、armeabi-v7a、x86_64。
3. Actions 自行安装配置工具链、编译 native/Flutter 和打包；不调用现有 build.sh/env.sh/build.cmd/ndk_*.sh/build_android_deps.sh。
4. Android Release APK 默认临时测试签名，不使用生产 secrets；Windows x64 编译产物 ZIP 与自解压 EXE。
5. 仅 Actions artifacts，失败不上传伪成品；无 Release/tag/store/自动 commit/push。

## T2. Impact Map

| Domain | Scope / Evidence |
|---|---|
| Build/Release | .github/workflows；vcpkg manifest、native ABI、build.py、portable generate.py；source verified |
| Rust/Flutter/Android/Windows | 保持 runtime/manifest/产品版本/生成 bridge；编排使用 tracked bridge，native flags 遵循当前脚本 |
| Network/API/Data | N/A，无协议/endpoint/持久化变化 |
| Security | contents: read、固定 action SHA、checkout 不保留凭据、无 production secrets、artifact 定向上传 |
| Compatibility | build.sh 和历史 F-Droid 脚本读取旧 workflow 元数据，须解除依赖；本地构建入口不用于 CI |
| Assets | SDK/NDK/JDK/Flutter/Rust/vcpkg external；SO/DLL/EXE/APK generated；外部 driver/helper 不装配 |
| Docs/Memory | build/debug/security、ADR/baseline、external registry、task/event/state |

## T3. Design and Rollback

- Android native matrix 按 ABI 独立构建，再由 Android packaging job 合并本轮 SO；Windows job 独立运行，两平台互不阻塞。
- 保持 Rust 1.75.0、Flutter 3.24.5、NDK 27.2.12479018、vcpkg 6f29f12e82a8293156836ad81cc9bf5af41fe836；JDK 17、Windows LLVM 15.0.6。actions 使用不可变 commit。
- 测试 keystore 在 runner 临时目录生成，通过未跟踪 key.properties 接入已有 Gradle release signing。每次运行签名不同，跨次安装需卸载或另行设计稳定测试身份。
- Windows 调用 build.py 和 portable generate.py，只打包本轮 Release；不下载上游 engine 或外部 driver/helper。
- 备选：直接调用本地入口被用户排除；保留旧多平台/发布链不符合范围；生产签名不在本次需求。
- 删除清单（恢复点 HEAD 8662f23）：bridge.yml、ci.yml、clear-cache.yml、fdroid.yml、flutter-build.yml、flutter-ci.yml、flutter-nightly.yml、flutter-tag.yml、playground.yml、third-party-RustDeskTempTopMostWindow.yml、update_submodules.yml、winget.yml，均位于 .github/workflows；另移除空 submodule 配置对应的 .github/dependabot.yml（目标仍为 master）。ISSUE_TEMPLATE/FUNDING/Flutter patch 保留。
- 元数据兼容：本地脚本保留原版本值，vcpkg 从 manifest 读取；历史 F-Droid 使用自身冻结值。旧内容可由 Git 对上述明确路径恢复；不重写历史、不整仓 reset。
- ADR：新增独立手动客户端构建决定，保留 ADR-0010 的 manual-only 边界；无发布路线开启。

## T4. Confirmation

- 当前请求已明确覆盖实现/旧编排删除/测试签名设计/Actions 下载。工作流内容中的构建与 artifact 上传只在用户后续手动运行时发生，本轮不触发。
- 授权停止于可审查的本地修改与 V0。

## T5. Change Record

- 新增 build-clients.yml、使用说明、ADR-0016、baseline09；删除 T3 清单中的13份旧编排配置；build.sh/F-Droid脚本只解除旧版本读取依赖；其余为工程说明、Release skill与记忆同步。
- 不手工修改 generated bridge，不新增业务功能，不改 product/toolchain manifest。

## T6. Verification

- V0 PASS：YAML 解析、输入/3-job DAG/1与3 ABI矩阵、9种direct Action完整SHA、11段Bash、1段Python AST、6段PowerShell AST、两份本地shell仅语法、路径/依赖/权限/产物范围、文档链接与diff检查。临时纯Python YAML解析器从PyPI 6.0.2源码包校验发布SHA256后读取，未安装到项目或更新依赖；未执行workflow中的命令。
- V1—V5：NOT_RUN；本轮无构建执行授权，不用静态检查冒充 runner 成功。

《编译验证需求》：在 GitHub Actions 手动运行 build-clients.yml，分别选择 android+aarch64、android+universal、windows、both。环境为工作流配置的 GitHub-hosted Ubuntu/Windows runner；执行目录为 checkout 根目录。验证对应 job 选择、APK ABI/签名、Windows bundle/EXE、下载和 SHA256，不产生 Release/tag。

## Security / Build Review

- 无不可信 PR 自动执行、无 signing secret、无发布 token；临时测试 key 不缓存/上传。依赖下载来自原 manifest/固定工具链/action revision。
- product manifest 已存在的 lock drift、JitPack/ZEGO/native dependency 网络可用性需 runner 证明；不宣称完全可复现。Windows 未签名，缺外部组件的能力需设备验证。
- 只收集指定成品、hash、工具链/源 commit/实际 lockfile 作为 evidence；不上传工作区、key.properties、keystore、环境变量全集或原始日志。
- Install/upgrade/uninstall、G2 runtime、G3 release：NOT_RUN / 不授权。

## T7. Documentation Delta

- 已同步 build/debug/security、source map、ADR/Decision、CI baseline、External Registry、Test Matrix、Task History、Project State/Memory、Current Work、AI Changelog、Change Event；入口/Release Skill加入新CI路径。
- Roadmap：保持自动 quality gate/正式发布规划，无运行风险关闭。

## T8. Handoff

- 配置目标完成：独立手动CI、平台/ABI选择、测试签名、Actions-only成品；只达到V0，不宣称首次runner成功。
- Worktree：仅本任务配置/文档变更；Git/build/test/analyze/codegen/实际sign/package/upload/release均未执行。
- Deletion：13份明确范围旧编排配置；产品版本/业务源码/锁文件/本地build.cmd不改。
- Global memory：STATE-20261013-001 / WORK-20261013-002 / CE-20261013-T001-01 / D-020已同步。
- 下一执行门：按新的明确请求提交/推送或手动运行CI；四组编译验证需求见T6，安装与完整产品发布另行验收。
