# T-2026-11-13-001：ADB无障碍快捷操作合并main

## 0. Task Metadata

| Field | Value |
|---|---|
| Status / T-State | active / T7，待Git交付与编排受理 |
| Requester | 项目维护者 |
| Primary Skill / Release Owner | openworld-release-engineer；同会话进行范围与安全复核 |
| Source | main/990a4aa48a77d63aa42dd12ac5d9f9e03b936e8f + test/d9d30eb9d6dc5b40dc6f831375fe3c83fe3b623e |
| Baseline | OW-BL-2026-11-12-unified-config-main / [Baseline28](../BASELINE/28_UNIFIED_CONFIG_MAIN_BASELINE_2026-11-12.md) |
| Created / Updated | 2026-11-13（Asia/Tokyo） |
| Initial Dirty State | 原test工作区server_profiles.rs有未提交编辑；另建main工作区保护，不暂存或改动该文件 |
| Risk / Evidence | medium；V0合并来源核对，V2/V3结果未知 |
| ADR / Decision | ADR-0018、ADR-0028、D-037；N/A — no decision delta |
| Tests | AMG-01；ADB-A01—A06真机NOT_RUN |
| External Assets | N/A — 沿用既有依赖与CI，无新增资产 |
| State / Work / Event | STATE/WORK-20261113-001-r1 / CE-20261113-T001-01 |

## T0—T1. Authority and Requirements

用户明确要求合并到主分支，提交说明“增加ADB无障碍开关快捷键”，随后执行编排；据此执行test→main合并、提交推送main及一次Build-Clients-BT.yml / ref=main / Platform=Both / BuildMode=常规编译，沿用不监控要求。本次不增加键盘按键映射，仅合入已实现的PC菜单快捷操作。

当前本地/远端test与main输入已核对；main保留2.0.26 / 2.0.26+59、产品身份及既有配置。原工作区配置编辑归用户，独立worktree完成合并，不stash/reset或覆盖。无main强推、test写入、版本变更、生产发布、设备操作或本地构建。

## T2—T4. Impact, Design and Confirmation

- Android：直接采用test已提交的LocalAdbAccessibility、LocalAdbCommands、LocalAdbManager。
- Flutter：直接采用test的remote_adb_controller/menu/session_state与remote_toolbar；动态当前应用目标、暂停恢复与状态修复合同保持。
- Rust/JNI/Network/API/Windows原生：无业务修改；MainService、无障碍生命周期、普通采集、ADB媒体触摸及ZEGO源码保持main。
- 文档：七份Markdown冲突保留双方历史，当前状态指向本任务；纳入13_REMOTE_ADB、D-037及T003任务，不提升历史测试结论。
- 构建：保留现有workflow和输入合同，权限contents:read，仅构建/产物，无release或仓库写入步骤。
- 安全：沿用身份/只读/开发者优先控制边界，仅当前App服务列表项；不触及其他应用配置或新凭据。

用户当前请求即本次Git与编排授权。来源固定后合并，冲突仅文档，不重复修改业务逻辑。验收为源码来源一致、主分支推送一致、一次编排受理；不等同运行通过。回滚需后续明确授权对合并提交revert，不能自动反向修改Android设置。

## T5—T6. Changes and Verification

合并输入固定如上，七份Markdown内容冲突已整合，业务代码自动合并。AMG-01通过：七个ADB源码路径与test输入Git内容哈希一致，其余912个非Markdown文件与main输入一致；173个相关本地文档链接有效，无冲突标记，工作区及暂存区diff检查通过。原test配置编辑字节哈希未变，不进入合并提交。

正式双端构建采用既有GitHub hosted环境；本地不执行Cargo/Flutter/Gradle/build/test/codegen。历史24项静态检查与13项离线Shell检查按原任务保留，不宣称重新运行或真机通过。

《编译验证需求》：workflow Build-Clients-BT.yml；ref=main；Platform=Both；BuildMode=常规编译。目标Android与Windows产物，触发后停止，不查询run/jobs/logs/artifacts。多PC连续权限开关、OEM和媒体/语音并存仍需设备验收。

## T7. Documentation and Memory

合入13_REMOTE_ADB、TEST_MATRIX、T003任务、D-037；更新PROJECT_STATE、CURRENT_WORK、CHANGELOG_AI、CHANGE_EVENT_LOG、TASK_HISTORY、PROJECT_MEMORY及本任务。新ADR、工具链/资产基线、External Registry、构建脚本N/A：无新增合同或资产。Baseline28保留冻结事实。

## T8. Handoff

待本次合并提交SHA、远端一致性及编排HTTP回执。完成后仅补文档回执并推送main，不重复编排；正式构建及设备结果保持未知。
