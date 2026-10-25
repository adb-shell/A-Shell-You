# T-2026-10-25-001：PC开发者免登录入口密码确认

## 0. Task Metadata / Session Recovery

- 模板：`TASK_TEMPLATE.md`；日期：2026-10-25（Asia/Tokyo）；Status：complete / T8（源码、推送与编排触发完成；运行验收未完成）；Risk：medium。
- Requester/Product Owner：当前用户；执行：Codex；协调 `ashell-master`，实现 `ashell-flutter-engineer`，Security/Release由同一执行者按对应Skill自审。
- 初始main / `fd233fa5178fd47dec5965b6c59d053d7e2db0fb`，clean，无并行活动任务。中断续接后再次确认clean，上轮patch未落盘。
- 参考基线：[AS-BL-2026-10-19-0dbe202-restored](../BASELINE/22_RESTORED_SOURCE_BASELINE_2026-10-19.md)，通过 [BASELINE_INDEX](../BASELINE/BASELINE_INDEX.md) 定位。实际HEAD已有后续源码/配置/工作流提交，记录state-drift，不声称业务树仍等于0dbe202。
- 恢复指针STATE/WORK-20261019-006、CE-20261019-T006-01；本任务STATE/WORK-20261025-001、CE-20261025-T001-01/02。历史授权继承none。
- ADR/Decision：N/A — no decision delta；D-033保留历史含义，不新增长期架构决定或改写冻结Baseline。
- TEST_MATRIX：FLT-01/02/04/06/07，新FLT-08；源码交付V0，正式验收V1/V2/V3。External Asset IDs：N/A，无新依赖、服务或二进制。

## T0 / T4. Authority and Confirmation

- C0/C2：用户明确要求PC Ctrl+Shift+H先弹出密码框，按指定口令确认/回车进入免登录模式；原请求为精确实现授权。C1文档/记忆同步按Task Protocol执行，不记录口令值。
- 续接C3：用户明确要求完成后推送test并执行编排；授权必要的分支同步、stage/commit/push及一次目标工作流dispatch。采用当前唯一Build-Clients-BT、Windows/常规编译，沿用临时测试签名与Actions产物；不发布Release、不推送main。
- 未授权：本机产品build/test/analyze/codegen、设备/生产操作、版本/依赖变更、删除/历史重写、生产签名/发布。后续若扩大范围、出现重叠修改或分支冲突则重新分析。
- 非目标：服务端授权、远控端认证、工具栏另一个开发者选项、Android、协议/FFI。既有未提交修改none，其他提交完整保留。

## T1. Requirements

当前 `ConnectionPage._handleDeveloperLoginBypassShortcut` 直接调用无参 `enableDeveloperLoginBypass`；主连接按钮和peer card共享该进程内存开关。

1. PC主窗口快捷键弹出隐藏输入、自动聚焦的密码框；长按/重复快捷键不堆叠。
2. 精确匹配用户指定口令（大小写/标点/空格，不trim）；确认、Enter和小键盘Enter共用校验；成功提示并关闭。
3. 空值/错误值不启用；取消/Esc/页面销毁不启用；关闭重开不保留输入/错误。
4. 原免登录连接行为和进程内有效期保持；已解锁不重复开启弹窗，重启默认false。
5. 输入不写日志/磁盘/网络；文档不记录口令。真实焦点/窗口/连接行为需正式运行验证。

## T2. Impact Map / Asset Classification

- Flutter active/tracked：main.dart → DesktopTabPage → DesktopHomePage → ConnectionPage → password dialog → developer_login_bypass_model；两份业务源码，peer_card仅核对消费者。
- State/lifecycle：ConnectionPage拥有弹窗状态/tag，TextField内部controller随组件销毁；局部输入关闭后清空引用；不新增外部controller/timer。
- Security：本地输入→摘要校验→内存免登录开关；仅UI gate，不替代API/endpoint授权，SEC-016不因本次改动关闭。
- Rust/FFI/Android/Windows native/Network/API：N/A，无协议、持久化、schema、native或generated变更。
- Build/Release：复用已有crypto依赖，不改锁文件/工具链；只执行现有Windows测试产物编排。
- Documentation：03_MODULE_DESIGN、10_SECURITY_MODEL、TEST_MATRIX、本任务与memory/history。证据为当前源码V0；运行行为verification-required。

## T3. Design / Compatibility / Rollback

- model改为带密码、返回bool的校验入口，只保存SHA-256摘要，按UTF-8精确匹配；摘要用于避免额外明文副本，不宣称抵御修改版客户端。
- 页面复用OverlayDialogManager/CustomAlertDialog/dialogButton，确认/回车同一提交函数，重复弹窗/提交保护，按tag关闭自己的弹窗。
- 错误不改变开关；取消/销毁不提交；异常只显示通用提示。无新数据存储或迁移。
- 备选：只在快捷键里校验会保留无条件model入口，弃用；远端校验涉及新服务，超范围。
- 顺序：model → page → V0 → 文档/记忆 → test提交推送 → dispatch。人工回退本任务差异即可恢复旧行为，不自动执行reset/强推。

## T5. Change Record

实际修改两份Dart与10份Markdown，均为本任务实现/文档/记忆；无generated产物、版本/依赖/资产调整。事件CE-20261025-T001-01。源码唯一启用调用携带密码，model成功分支才写true；peer card和连接消费者保持。无计划外源码变更。

## T6. Verification / 编译验证需求

- V0：PASS，人工核对唯一启用调用、成功/失败/取消路径、键盘/重入/销毁生命周期；`git diff --check`通过，10份Markdown/111个本地引用有效；仅两份Dart和任务同步文档。不把此记录等同实际交互PASS。
- V1：NOT_RUN，本机分析器/测试未授权。正式Flutter环境可在flutter/运行 `flutter analyze lib/models/developer_login_bypass_model.dart lib/desktop/pages/connection_page.dart`，需另行授权。
- V2：已触发GitHub Actions `Build-Clients-BT.yml`，test、Platform=Windows、BuildMode=常规编译；HTTP204，[run 40200073517](https://github.com/adb-shell/A-Shell-You/actions/runs/40200073517)，首次核对in_progress，尚无编译成功结论。
- V3：NOT_RUN；FLT-08覆盖正确/空/错误/空格/大小写/标点、按钮/Enter/小键盘Enter、取消/Esc、重复快捷键、窗口销毁、解锁连接与重启；FLT-02/04/06/07补多窗口/生命周期/连接/焦点回归。
- V4/V5：N/A，无服务端授权/生产行为调整。

## Security / Build / Release Review

失败不启用；固定客户端gate仍可由修改版绕过，不作为服务端授权。密码只短暂保留于弹窗内存，文档不保存值；无网络发送/日志。无download/plugin/JNI/driver/license/SBOM增量。CI只构建、临时测试签名和Actions产物，G3生产发布未授权；当前构建/设备验收未知。

## T7 / T8. Documentation / Handoff

已同步domain/security、TEST_MATRIX、TASK_HISTORY、CHANGE_EVENT_LOG、CHANGELOG_AI、PROJECT_STATE、PROJECT_MEMORY；Git/编排回执已取得，CURRENT_WORK最后关闭。
N/A：ADR、DECISION_LOG（no decision delta）、Baseline冻结快照、EXTERNAL_ASSET_REGISTRY、build/debug/roadmap和入口规则/Skills；未产生对应增量。

Git已执行：切换test、从7e45842快进至fd233fa，保留main五项后续提交；12路径提交为 `fe3c7d445a531a11ebbe5c30c9e3b047e2874ba3`（PC开发者模式增加密码验证），push成功且远端test同SHA，main仍fd233fa。工作区在该提交推送后clean。

编排：workflow 394791509 / Build AShell Clients-BT，2026-10-25 22:58:45（Asia/Tokyo）在test按Windows/常规编译启动；run 40200073517，源码SHA为fe3c7d4，首次核对in_progress。只触发一次，不把请求接受或运行中当作构建PASS。

CE-20261025-T001-02补记上述实际回执，仅修改本任务及六份.codex记录；后续文档提交不改变编排使用的产品源码/工作流树。无本机产品执行、删除、版本或生产发布。后续确认门为额外验证/修复或发布；FLT-08交互仍NOT_RUN，不继承历史任务授权。
