# T-2026-10-25-002：开发者密码文档同步、合并main与双端编排

## 0. Task Metadata / Session Recovery

- 模板：`TASK_TEMPLATE.md`；2026-10-25（Asia/Tokyo）；Status：complete / T8（文档、main合并推送与双端触发完成，构建/运行验收仍待证）；Risk：medium（Git/CI交付）。
- Requester/Product Owner：当前用户；执行/协调：Codex / ashell-master；文档与CI：ashell-release-engineer；Flutter/Security按本会话已读Skill自审；生产Release owner：N/A，本任务不发布。
- 起点：test/48226fbcdc08719b6e2a5c2e245dc9c10cc77ae9，clean；main/fd233fa5178fd47dec5965b6c59d053d7e2db0fb；两者均与远端一致。main是test祖先，四项差异提交均为T001密码入口/双密码/交付记录。
- 恢复STATE/WORK-20261025-001-r3、CE-20261025-T001-04，无并发活动任务，state-consistent。新指针STATE/WORK-20261025-002，CE-20261025-T002-01。
- 参考Baseline22；新冻结源码快照 [Baseline23](../BASELINE/23_DEVELOPER_PASSWORD_BASELINE_2026-10-25.md) / AS-BL-2026-10-25-48226fb-developer-password，通过 [BASELINE_INDEX](../BASELINE/BASELINE_INDEX.md) 定位。
- ADR/Decision：N/A — no decision delta，保持现有本地UI gate边界；不另立架构决定。
- TEST_MATRIX：FLT-08及FLT-02/04/06/07运行验收仍NOT_RUN；双端构建V2按实际run记录。External Asset IDs：N/A — 无新资产/依赖或签名来源。

## T0 / T1 / T4. Authority and Requirements

- 当前用户明确授权：更新同步文档、合并test到main、提交推送，提交说明精确为“开发者密码增加”，再执行main双端编译。
- C1：更新当前工程/维护文档和薄记忆；C3：必要切分支、merge、stage/commit/push main与一次workflow_dispatch，Platform=Both、BuildMode=常规编译。
- 生产发布、生产签名/密钥、版本/依赖、删除/强推/历史重写、本机build/test/analyze、设备验证不在范围。
- 目标：main包含test的两个密码实现，文档反映当前行为与唯一BT入口，远端main与交付提交一致，main双端编排已接受且可定位run。
- 保留test现有历史与分支，不新增业务实现；若分支并发变化或merge冲突则停止写入重新调查，禁止覆盖用户修改。

## T2 / T3. Impact Map and Design

- active/tracked源码：`connection_page.dart`密码弹窗与`developer_login_bypass_model.dart`摘要集合；本轮只合并已有实现，不改变产品代码、协议、FFI、Android/native/API或持久化。
- 文档：更新入口/总览/架构/源码地图与构建文档，补密码维护步骤；新增Baseline23，保留冻结Baseline22；更新当前任务和六份.codex状态/历史。
- 方案：从干净main执行 `merge --no-ff --no-commit test`，文档同步加入同一merge commit，再以指定说明提交推送。这样保留四项test提交且合并提交可直接评审最终源码与文档。
- 不采用squash/强推；无schema/数据迁移。回滚由维护者按指定merge父提交制定人工revert，不在本次自动执行。
- CI沿用workflow394791509 / `.github/workflows/Build-Clients-BT.yml`，Both触发Android Native、Android APK与Windows EXE；Android固定aarch64/arm64-v8a，Windows x64。临时测试签名、Actions artifacts保持，不创建Release/tag或改工作流。
- 安全：密码明文不写入文档，摘要集合仅本地入口校验，不替代服务端/endpoint授权，SEC-016不关闭；无新credential访问范围。

## T5. Change Record

已完成入口/总览/架构/源码地图/模块维护/构建说明、Baseline23/index与任务薄记忆同步。main相对起点共20份Markdown（含test已有文档）及两份既有Dart；相对test的本轮变化全部为Markdown，产品/工作流树一致。新业务修改none，日志不保存口令或生产标识。

## T6. Verification / 编译验证需求

- V0：PASS，20份Markdown/222个本地引用有效，工作区和暂存差异检查通过；相对test无非Markdown差异，merge无冲突。已核对提交说明、双parent与远端main同SHA。
- V1/V3：NOT_RUN，FLT-08须两个独立未解锁进程验证两密码，再覆盖错误/取消/Enter/重复快捷键/关闭重开/重启；本机分析/运行需独立授权。
- V2：main的BT/Both/常规编译已触发，HTTP204，[run40204268722](https://github.com/adb-shell/A-Shell-You/actions/runs/40204268722)，head42ba32c。首次核对workflow、Windows EXE和Android Native均in_progress；Android APK依赖native完成。无构建PASS结论。
- 前轮test run40203581056/head356f53b在本次起点只读核对仍in_progress，无构建成功结论；不能替代main双端结果。
- V4/V5：N/A，无集成/生产操作。

## Build / Release / Compatibility Review

工作流仅workflow_dispatch、contents:read、checkout不保留凭据；Android与Windows使用临时测试签名，产物留在Actions。源码/版本/锁文件/生成桥和第三方资产不变。G3生产发布未授权；历史构建/设备缺口仍按TEST_MATRIX/EXTERNAL_ASSET_REGISTRY保留，不能因合并或dispatch关闭。

## T7 / T8. Documentation and Handoff

已同步domain/build、Baseline23与index、入口及六项薄记忆；Decision/ADR/External Registry与测试用例语义无增量，已评估并保留；TEST_MATRIX已有FLT-08覆盖两密码。

Git交付：main合并提交 `42ba32cc7fcb3cf3cb05959a139d181e870a5691`，提交说明精确为“开发者密码增加”；parents为fd233fa与48226fb，保留全部test历史。push main成功，远端main同SHA，test保持48226fb；该次推送后工作区clean。

编排：workflow394791509 / Build AShell Clients-BT，main / Both / 常规编译，HTTP204；run40204268722，head42ba32c，首次核对in_progress，Windows EXE/Android Native均在运行。只触发一次，未完成构建/设备验收，无本机产品执行、版本变更或生产发布。

CE-20261025-T002-02补齐上述回执，仅本任务与六份.codex记录；回执文档后继提交同样使用用户指定说明“开发者密码增加”，不改变编排的产品/工作流树，也不再次编排。Global memory全部完成同步评估，CURRENT_WORK最后关闭。后续额外运行验收、发布或回滚仍需对应范围授权。
