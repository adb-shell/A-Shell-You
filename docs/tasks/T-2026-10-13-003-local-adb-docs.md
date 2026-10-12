# T-2026-10-13-003 — 同步独立本机 ADB 文档

## 0. Task Metadata

| Field | Value |
|---|---|
| Status / T-state | complete / T8；文档交付 V0，运行验收待证据 |
| Requester / Product Owner | 当前用户 |
| Principal / Skill | Codex / ashell-master |
| Domain / Security / Release | Android、Flutter、Security、Release skills 静态约束复核；无独立人工审计 |
| Created / Updated | 2026-10-13 |
| Branch / Source HEAD | main / f84a78f87d1009c24acfdff478cf95bb463547e5 |
| Implementation commit | 8662f2369a33f5286c20f9de4d640d2072175ca7；代码已在此前授权下提交推送 |
| Baseline | AS-BL-2026-10-13-f84a78f-local-adb；见 Baseline 11 |
| Initial dirty / concurrency | clean；CURRENT_WORK active=0；无文件冲突 |
| Risk / highest verification | 文档 low；底层 ADB 高权限，构建/运行仍需 V2/V3。本任务要求/执行 V0 |
| ADR / D-ID | ADR-0017 / D-021 补记已授权的实现决定；ADR-0015 旧组件退役保持 |
| Cases | AND-06/08/09—13，原链回归 AND-01/02/04/05/07、FLT-04/05/06 |
| Assets | EXT-LIB-ADB-004—007；旧 EXT-BIN-ADB-001 / EXT-REF-ADB-002/003 保持 RETIRED |
| State / Work / Event | STATE-20261013-003 / WORK-20261013-006；CE-20261013-T003-01 |

## T0. Authority and Recovery

当前用户明确要求同步、整理并更新 ADB 文档，解除此前“先代码、后文档”的延期要求。C0 读取与 C1 ADB 相关文档/记忆/入口同步获准；本轮不改产品代码、依赖、构建配置、原远控/采集/无障碍链。

本轮无 build/test/analyze/codegen/依赖解析、设备、删除/迁移、版本、签名、发布、生产或凭据动作。此前“提交github”已完成代码提交交付，本次文档修改未自动 stage/commit/push。原实现范围授权用于补记历史，不转成未来新功能授权。

恢复 PROJECT_START_HERE、规则、状态/工作/历史/Decision、ADR、Skills、Task Protocol/Template、基线与对应领域文档。STATE-20261013-002 观察 c521601 与当前 f84a78f 为可解释 drift：后续 SDK、MSYS2/host 依赖 CI 修复已在 Git；未覆写这些历史记录或声称其 runner 成功。

## T1. Requirements and Acceptance

现状：8662f23 的九个新模块文件与三个接入钩子已提交，文档仍多处写“没有新 ADB”“主页仅 ServerPage”。期望：一处完整当前说明，入口、领域文档、验证/资产/决定/记忆相互一致，旧文档明确历史适用范围。

验收：源码行为可追溯；使用步骤区分两个端口；说明配对/连接/退出码/取消/身份/构建依赖；PC ADB 与 ADB 投屏保持未实现；只改 Markdown。未知：没有具体编译日志、APK、设备型号/Android版本与测试步骤结果，不能推定 V2/V3 通过。

## T2. Impact Map

| Domain | Source anchors / facts | 本轮变更 |
|---|---|---|
| Flutter | home_page / local_adb_page / bridge；非 outgoing-only Android 入口 | 更新文档，不改 UI |
| Android | localadb 五 Java 类、Activity 注册；本机 loopback、独立身份 | 文档化真实合同与生命周期 |
| Build / dependencies | local-adb.gradle / local-adb.pro；四项固定版本与仓库过滤 | 新资产/基线登记，不运行解析 |
| Security / privacy | 系统配对、任意本地 shell、加密身份、断开不撤权 | 控制与缺口同时记录，无新增安全功能 |
| Rust / Windows / Network / API | 无新增 ADB peer/FFI/JNI/PC 视频接口 | N/A — 实现未接入且文档任务不修改 |
| Docs / memory | 当前领域、ADR、基线、TEST_MATRIX、入口/Skills | 修正陈旧当前断言，历史正文不重写 |

源码为 active/tracked；旧 runner/reference 为 retired/historical；AAR/JAR/SO 为 external、实际产物未验证。无数据/protocol/schema 迁移，无旧 key 导入。旧系统 API <30 拒绝 ADB 功能，不改变 App minSdk。

## T3. Design and Rollback

推荐并采用：新增 `docs/AI_ENGINEERING/12_LOCAL_ADB.md` 作为完整事实与使用入口，领域文档只保留必要摘要并链接；历史 `ADB_LADB_INTEGRATION_MEMORY.md` 追加当前指引、不改写旧实现正文。ADR-0017 补记选型、边界、备选与后果；Baseline 11 固定观察，不覆盖旧基线。

不采用把旧 LADB 文档整篇改成新实现（会混淆历史）、把所有历史报告全量重写（无必要）、继续只写“新组件已接入”但无使用/维护合同（不可交接）。回退文档按本事件路径和本轮 diff 审阅恢复；不删除旧资料、整仓 reset 或回滚已提交产品代码。

## T4. Confirmation

C1 由本次用户文档同步请求提供，范围未扩大；没有需等待的实现决策。C2/C3 本轮不执行。ADR 补记的是用户已授权自主设计并修改的已提交模块，未将计划中的 PC ADB/scrcpy 标为 accepted implementation。

## T5. Change Record

新增当前说明、ADR-0017、Baseline 11 与本任务。更新当前架构/模块/Android/build/debug/security/roadmap、ADR/Baseline 索引、TEST_MATRIX、External Registry、AGENTS/CLAUDE/PROJECT_START_HERE/README、Android/Flutter/Release skills 和 `.codex` 记忆。逐路径清单以 CE-20261013-T003-01 为准。

原实现补记：8662f23 新增九文件、原三文件增加七行；本轮无源码 diff、依赖版本变更或 generated output。既有 CI、产品信息和代码均保持。

## T6. Verification

| Level | 目标 / 实际 | Result |
|---|---|---|
| V0 | 源码合同/常量与三个钩子核对；8662f23 模块对比；上游许可来源；新增 Markdown 链接、新文件空白、旧断言、diff 与范围 | PASS — 仅静态，不证明构建/运行 |
| V1 | formatter/lint/unit/analyzer | NOT_RUN — 非文档任务所需，不调用 |
| V2 | Linux 两模式或独立 Android CI，Release/ABI/JNI/依赖 hash | NOT_RUN — 无本模块正式构建结果 |
| V3 | AND-09—13 / 原链回归 / ROM矩阵 | NOT_RUN — 无本模块真机结果 |
| V4/V5 | 隔离集成/生产 rollout | N/A — 本次未设计远程 ADB 或发布 |

《编译验证需求》：正式 Linux、仓库根，`./build.sh 1` / `./build.sh 2`；独立 CI 另按 BUILD_CLIENTS_ACTIONS.md。收集 source SHA、toolchain、依赖/ABI清单、APK hash、Release R8/JNI 结果；设备配对/id/错误端口/取消/退出码/Unicode/上限/重启/撤权与原链回归见 AND-09—13。只列需求，本次不执行。

## Security / Build Review

系统配对是 ADB 授权，UI 不等于远程权限门。本模块无专用 PC 协议、命令白名单或持久审计；加密保存身份不等于 RSA 私钥不可导出，断开不等于撤权或撤销命令副作用。SPAKE2 上游未审计；新依赖 artifact hash、第三方 NOTICE、SBOM/分发材料、ABI/R8 和签名尚待正式环境。

没有新增 credential/生产地址或读取真实设备身份。第三方公开源码/license 只读核对，无依赖安装。G2/G3 尚未满足，本次文档完成不构成 release-ready。

## T7. Documentation Delta

领域文档、ADR、Baseline、Decision、Task History、External Registry、Test Matrix、Project State/Memory、Current Work、AI Changelog、Change Event、入口/相关 Skills 均同步。AI_RULES/Task Protocol/Development Workflow 无权限规则变化，reviewed-no-change；历史冻结基线和旧报告正文保留。

## T8. Handoff

文档按源码补齐，34个 Markdown 文件（4新增、30修改），V0通过后关闭。原产品代码未改；文档修改留在工作树，本轮无 Git 写入、编译测试、设备操作、文件删除、签名/发布。后续可根据正式构建/设备结果补 evidence；PC ADB/投屏须另行需求设计。
