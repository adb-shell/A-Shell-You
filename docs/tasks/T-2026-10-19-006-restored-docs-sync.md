# T-2026-10-19-006：回退基线后的文档与记忆同步

## 0. Task Metadata

| Field | Value |
|---|---|
| Task ID / Title | T-2026-10-19-006 / 回退基线后的文档与记忆同步 |
| Status / T-State | complete / T8（文档与V0；Git回执见交付） |
| Requester / Product Owner | 当前用户 |
| Principal Engineer / Primary Skill | Codex / ashell-master |
| Domain Owners | 主会话核对Android、Flutter、Rust、协议和发布文档；无子代理 |
| Security Reviewer / Release Owner | 主会话按security/release领域约束做文档范围复核 |
| Created / Updated | 2026-10-19（Asia/Tokyo） |
| Source Branch / HEAD | test / 0dbe2027ceb3f1a72ae2700fe8e4942cfe67cec5 |
| Baseline ID | AS-BL-2026-10-19-0dbe202-restored |
| Initial Dirty State | clean；本地/远端test、main四个引用一致 |
| Risk / Required Highest Verification | low（仅文档） / V0 |
| Related ADR / D-ID | ADR0017/0018/0021—0026；D-033 |
| Related TEST_MATRIX IDs | ADB-R01—R25（R10退役）、ADB-M01—M32、AND-09—13；仅更新验收说明 |
| Related External Assets | EXT-LIB-ADB-004—007、EXT-BIN-SCRCPY-001；版本和文件不变 |
| State / Work / Event | STATE-20261019-006 / WORK-20261019-006 / CE-20261019-T006-01 |

## T0. Authority and Baseline

用户明确指定恢复到0dbe202的工程认知，按实际代码更新文档，以“文档更新”提交推送test，再合并推送主分支。仓库实际主分支为main；“mian”按main处理。本次开始时回退已完成，不再改写历史或重复reset。

| Action | 授权与边界 |
|---|---|
| C0 | 读取当前源码、Git引用及文档 |
| C1 | 更新当前合同、入口、状态、记忆、任务/基线/验证记录 |
| C2 | 不适用；不改业务代码、配置、协议、依赖、构建脚本 |
| C3 Git | 用户本次明确授权提交“文档更新”、push test、merge/push main；计划fast-forward，不强推 |
| 其他C3 | 本次无build/test/analyze/codegen、CI dispatch、设备执行、版本/签名/发布、删除或生产访问 |

工作树无既有用户修改。若远端或工作树出现并发变化，先重核范围，不能覆盖。旧任务授权不延续。

## T1. Task Brief

将重复且互相覆盖的“当前”快照整理为一个由源码支撑的状态。修正旧文档的1280尺寸上限、base64帧桥、未实现键盘/剪贴板、旧截图节奏和旧构建入口说明。历史任务与冻结基线保留各自含义；撤回的后续实现不进入当前合同。

验收：业务树与0dbe202完全一致；当前入口指向同一基线；文档无已知过时参数；本地链接有效；test/main本地与远端同步至文档提交。不能宣称编译或设备通过。

## T2. Impact Map

| Domain | Source evidence | 本轮影响 |
|---|---|---|
| Android | LocalAdbManager、AdbCaptureProfile、AdbVideoCapture/Surface、Screenshot/Layout/Privacy helpers、KeepScreenOnState | 只同步能力、参数、生命周期及兼容限制说明 |
| Rust / Network | remote_adb.rs、remote_adb_video.rs、client/server路由、message.proto | 只记录字段40/41、QoS读取、二进制帧桥、流控与权限 |
| Flutter / Windows | desktop/adb、remote_page、remote_display_layout、overlay、input_model | 只同步PC视图、键盘/粘贴、状态和菜单边界 |
| API / Data | 未修改 | N/A，不涉及外部API或数据库 |
| Security / Privacy | ADB身份、现有已认证会话、原无障碍隔离 | 不改变授权或隐藏真实错误；不复制敏感值 |
| Build / Assets | 两个手动workflow、本地build入口、scrcpy固定资产 | 仅纠正文档；不下载/生成/替换资产 |
| Documentation / Memory | 当前合同、入口、.codex、任务/基线/矩阵 | 当前快照重整；历史记录保留 |

资产分类：本轮修改均为tracked Markdown，新任务/基线为文档；源文件、生成桥、二进制与外部服务不变。协议、存储schema、OS/ABI、旧端兼容行为无变化。

## T3. Design / Rollback

以Git对象0dbe202为实现真相，当前文档集中陈述最终行为，增量历史留在任务文件。D-033记录恢复规则，无新运行时架构ADR；既有ADR按历史范围解释，冲突以当前源码/合同为准。拒绝仅追加一段新说明而保留正文过时参数的做法。

文档可按本次提交回退，不影响运行时代码或数据；无feature flag、数据迁移或不可逆产品操作。合并采用fast-forward，不引入另一套源码。

## T4. Confirmation

用户本次原文已授权“根据实际代码…更新文档和同步，完成后推送到test后再合并到mian，提交信息为‘文档更新’”。本任务即落实该范围，无需重复确认。构建与额外修复不在本轮范围。

## T5. Change Record

入口/记忆去除堆叠的当前快照；12/13/14及相关架构、Android、源码索引、构建说明与实际代码对齐；更新外部资产说明、验证矩阵、Baseline22、D-033及事件/任务索引。无依赖、版本、生成文件或业务行为变更。实际文件清单以Git diff为准。

## T6. Verification

| Level / Case | 状态 | 判据 |
|---|---|---|
| V0源码对照 | PASS | 当前参数/调用链与0dbe202源文件一致 |
| V0文档差异与链接 | PASS | git diff --check；24份Markdown、249个本地引用有效 |
| V0非文档树 | PASS | 交付前0dbe202到工作树无非Markdown差异；提交后再核对 |
| V0 Git交付 | 交付时查询 | 目标为本地/远端test/main SHA相同且worktree clean；实际结果见会话最终回执 |
| V1—V5 | NOT_RUN | 文档任务不运行分析器、编译、设备、集成或生产验证 |

正式产品验收仍见TEST_MATRIX。本轮无需编译验证需求，不触发CI；已有源码的设备兼容与性能证据缺口不通过文档同步变为PASS。

## Security / Build Review

不修改信任边界、远控鉴权、剪贴板权限、配对身份、JNI所有权、无线调试或无障碍权限。命令真实退出码、黑屏/布局的ROM限制、参数与实际性能的区别写入文档。资产版本/许可不变；未加入字体或二进制，未访问外部生产服务。版本、签名、安装升级、工具链及G2/G3发布验收均无变更/无新增通过证据。

## T7. Documentation Delta

- 更新当前工程合同、入口、基线索引/新基线、验证矩阵、外部资产当前说明。
- 更新PROJECT_STATE、CURRENT_WORK、PROJECT_MEMORY、CHANGELOG_AI、CHANGE_EVENT_LOG、TASK_HISTORY、DECISION_LOG。
- ADR仅评估，不新建；规则与Skills不改。安全旧风险不因本轮文档整理关闭。

## T8. Handoff

文档同步与V0核对已完成。本任务通过提交“文档更新”交付到test，再fast-forward合并推送main。准确SHA和远端一致性以交付Git查询记录为准。本次不执行编排。
