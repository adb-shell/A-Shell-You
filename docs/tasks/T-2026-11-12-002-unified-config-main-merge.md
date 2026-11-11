# T-2026-11-12-002：统一配置合并main

## 0. Task Metadata

| Field | Value |
|---|---|
| Task ID / Title | T-2026-11-12-002 / 统一配置合并main |
| Status / Current T-State | complete / T8（本次合并交付） |
| Requester / Product Owner | 用户 |
| Principal Engineer / Primary Skill | Codex / openworld-master |
| Domain Owners / Security Reviewer / Release Owner | Codex按master与release/security边界核对；无子代理 |
| Created / Updated | 2026-11-12，Asia/Tokyo |
| Source Branch / HEAD | main/5d2d2e932cfb390a7b3d0278bea6a7e080746c95 + test/a8abdd4c04951f930ea240c9951378ee3b8c165f |
| Baseline ID | 输入OW-BL-2026-11-11-main-developer-priority-merge；输出OW-BL-2026-11-12-unified-config-main |
| Initial Dirty State / Existing User Changes | test工作区clean；d3b3f54/a8abdd4已提交的用户配置完整保留 |
| Risk Level / Required Highest Verification | low（合并交付）；V0，不承担运行验收 |
| Related ADR / D-ID | ADR-0027/0028/0029，D-034/035/036 |
| Related TEST_MATRIX IDs | UCM-01；SCP、BCF、DSP历史结论保持 |
| Related External Asset IDs | N/A — 沿用既有hbbs/hbbr/API/ZEGO与CI记录，无新增部署 |
| Project State / Work Registry Revision | STATE-20261112-002-r2 / WORK-20261112-002-r2 |
| Change Event IDs | CE-20261112-T002-01/02 |

## T0. Authority and Baseline

用户原文：“合并并推送到主分支mian，提交说明‘统一配置文件(大改造节点一)’”。仓库实际主分支为main，远端无mian；已向用户说明按main执行。起点test与origin/test同为a8abdd4，main与origin/main同为5d2d2e9；工作区干净、无其他活动任务。

任务类型为Git合并交付。授权switch/merge、必要冲突解决和文档同步、stage/commit/push；不包含新业务开发、版本提升、编译/测试/codegen、监控、发布、生产访问或凭据操作。工作树出现无关用户修改或远端并发更新时先保护并核对，不强制推送。

### Confirmation Record

| Level / Action Class | Applicable / Authorized By | Exact Scope / Still Forbidden |
|---|---|---|
| C0 observe | 用户本轮请求 | 仓库、远端分支与V0静态核对 |
| C1 docs/rules/Skills | 必要合并同步 | 文档冲突、基线和任务记录；不修改权限规则 |
| C2 code/config/build script | 仅合并已有test内容 | 不新增产品逻辑或更改用户配置 |
| C3 Git write | 用户2026-11-12明确要求 | test→main正常合并、提交、推送；无force、reset或历史重写 |
| C3 build/test/analyze/codegen/device/integration | N/A — 本轮未要求 | 不执行 |
| C3 delete/move/history rewrite | N/A | 不执行 |
| C3 version/sign/package | N/A | 保留main版本，不执行 |
| C3 upload/deploy/release/store | N/A | Git push以外不执行 |
| C3 production/secret/credential | N/A | 不访问、不输出实际配置值 |

## T1. Requirements

将test的集中配置、PC切换、编译/CI修复和最新用户配置合入main，提交标题精确使用`统一配置文件(大改造节点一)`。保留main的2.0.26 / 2.0.26+59和双方历史；解决文档矛盾后正常推送origin/main。验收为远端main包含双方输入、标题准确、工作区clean、test不变。运行功能及历史构建结果不在本轮验收范围。

## T2. Impact Map

| Domain | Entry / Files | Impact / Evidence |
|---|---|---|
| Rust/Network/API | src、libs/hbb_common、server_profiles.rs | 全部采用test输入，静态来源核对；无新协议决定 |
| Flutter | flutter/lib的切换入口与状态/模型 | 保留test实现；Android仍读取固定配置 |
| Android/Windows | 原runtime、JNI、ADB、开发者画面优先 | 本轮不追加修改；源码无合并冲突 |
| Build/Release | manifest、lockfile、build.cmd、CI helper/workflow | main版本文件+test CI修复，实际编译未知 |
| Documentation/Memory | 15份冲突Markdown、基线/任务/维护入口 | 保留双方历史，当前状态改为main整合快照 |
| Security/Privacy/License | 生产配置、认证与外部资产 | 不输出地址/Key，不访问服务，无新依赖或许可证变化 |

Asset Classification：源码/文档为active/tracked；`.info`为tracked compatibility；generated文件无新增改写；产物为N/A；实际服务器及CI执行环境为external/unverified。协议、持久化、ABI、旧新端兼容合同沿用test实现；本次仅合并，不重新设计。

## T3. Design

使用`git merge --no-ff --no-commit test`保留两个父提交；代码自动合并，Markdown按实际源码和时间线解决冲突。main原版本文件保留，test非文档功能文件完整纳入。相较squash，正常merge可直接核对来源且保留历史，因此不采用squash。

沿用ADR-0027/0028/0029，无新长期架构决定。安全停止点为提交前；如需回滚，后续经用户授权创建合并revert，当前不执行回滚或历史重写。不改运行时fail-open/fail-closed策略，无新增不可逆操作或feature flag。

## T4. Implementation Confirmation

用户本轮明确授权，目标origin/main、说明`统一配置文件(大改造节点一)`。必要文档冲突处理属于合并范围，无额外确认门；范围变化或远端并发冲突才重新评估。

## T5. Change Record

- main/5d2d2e9合并test/a8abdd4；15份Markdown冲突，产品源码自动合并。
- 当前版本来自main；集中配置与编译/CI修复来自test；用户最新三套端点内容完整保留。
- 冲突文档包括全局状态/历史、项目入口、ADR索引、测试矩阵、工程概述/架构/源码图/构建文档和Actions说明；同步Baseline28与15_SERVER_PROFILES。
- 发现Android与PC1目前不同，更新当前维护表；历史相同/PC2留空的观察记录按原时间点保留。
- 依赖、生成物、产物、协议及版本无本轮新增变更；外部资产登记沿用test，不新增服务或声称部署成功。事件CE-20261112-T002-01。

## T6. Verification

| Level | Case / Environment | Executed / Result |
|---|---|---|
| V0 | UCM-01：来源、非文档文件一致性、主版本、配置完整性、冲突标记、链接与diff | PASS：41个非文档分支差异文件与预期来源逐一哈希一致；32份Markdown的375个本地链接有效；冲突标记为零，双方历史任务/事件引用保留，diff --check通过 |
| V1 | 单测/analyzer | NOT_RUN — 本轮仅合并交付 |
| V2 | Windows/Android正式构建 | NOT_RUN — 不触发构建 |
| V3 | 设备/系统runtime | NOT_RUN — 无本轮设备验证 |
| V4 | 服务联调/安全集成 | NOT_RUN — 不访问服务器 |
| V5 | 预发/生产观测 | NOT_RUN — 未授权且非本轮范围 |

最高要求V0；SCP、BCF、DSP历史验证不提升。c788e8d双端编排HTTP204不代表成功；离线CI检查13/13亦不代表实际runner DNS恢复。正式编译和运行需求沿用[Server Profiles](../AI_ENGINEERING/15_SERVER_PROFILES.md)与[构建修复任务](T-2026-11-12-001-build-fixes.md)，本轮不执行。

## Security / Privacy / Compliance Review

信任边界、认证/授权、加密/网络、下载/插件、JNI/并发、ADB/无障碍和驱动合同全部沿用输入源码；本次只做来源一致性核对，不声称完成新的安全审计。生产地址/Key未写入任务文档或工具输出；无秘密轮换、部署、许可证/SBOM或依赖变化。剩余风险为原功能和外部服务未取得完整运行验证。

## Build / Release Review

目标main源码交付，Windows与Android产物不在范围；manifest及lockfile保留main版本，CI代码采用test修复。无构建、签名、打包、安装、升级、上传产物、发布或监控动作；G2/G3不作通过结论，工具链/运行外部资产可用性未复验。

## T7. Documentation Delta

updated：工程00/01/02/08及15、ADR索引、Baseline28及索引、DECISION_LOG、TASK_HISTORY、TEST_MATRIX、PROJECT_STATE、CURRENT_WORK、PROJECT_MEMORY、CHANGELOG_AI、CHANGE_EVENT_LOG、AGENTS/PROJECT_START_HERE和Actions入口。双方历史保留。

EXTERNAL_ASSET_REGISTRY：沿用test已登记的CI镜像与目录事实，无额外部署；Security/Debug及其他工程文档采用test已有增量。规则/Skills/路线图：N/A，本轮不改变。

## T8. Handoff and Closure

- Outcome：合并提交`c30efd61449a0e02b5b0546a25ddf948c5ad5d4e`已推送origin/main，并核对远端SHA一致；两个parents分别为main/5d2d2e9、test/a8abdd4。test仍为a8abdd4，推送后工作区clean。提交说明“统一配置文件(大改造节点一)”。本次只补交付回执，不改变源码、不触发或监控编译。
- Git动作：switch main、merge --no-ff --no-commit test、解决文档冲突、add/commit/push；合并标题准确，远端main与本地一致，test不变。V0及Git交付验收通过。
- 本回执仅修改任务和6份全局记忆文件；补记后提交推送，不二次构建。最终本地/远端SHA与clean状态在完成回复前再次核对。
- 兼容性/回滚：保留双方历史；未运行任何回滚动作。
- 未执行：构建、单测、codegen、监控、设备/生产服务访问、删除、改版本、签名或发布。
- Definition of Done：已满足UCM-01、两输入父提交、origin/main同步、test不变及推送后clean；任务/全局状态回执已同步。T8仅源码合并交付，不表示全部运行验收通过。
