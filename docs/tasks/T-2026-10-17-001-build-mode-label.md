# T-2026-10-17-001：构建模式名称调整

按 `TASK_TEMPLATE.md` 的 T0—T8 记录；日期：2026-10-17（Asia/Tokyo）。

## Metadata / T0

- Status：complete / T8（实现与V0完成；Git结果见包含本记录的提交）。
- Requester / Product Owner：项目用户；Primary Skill / Domain Owner：ashell-release-engineer。
- Security Reviewer / Release Owner：本轮由当前会话执行静态范围审查；无发布动作。
- Source：main/130f748，初始 clean；PROJECT_STATE 仍为旧文档快照，本轮不回填其他提交的事实。
- Baseline reference：AS-BL-2026-10-16-4d3c446-adb-render-ui（历史工程基线）；本次实际工作流依据 130f748。
- Risk / Required verification：low / V0。
- Related ADR / D-ID / TEST_MATRIX cases / External assets：N/A；纯名称替换，不改变架构、工具链、依赖或产品行为，不新增运行用例或外部资产。
- State / Work / Event：STATE-20261017-001 / WORK-20261017-001 / CE-20261017-T001-01。

## T1—T2：需求、授权与影响

- 用户授权：将新工作流“完整编译”改为“原始编译”，然后提交；本次不执行。
- C0/C1/C2：读取、修改指定工作流及必要工程记录；C3：仅本次文件的 git add / commit。
- 未授权：push、workflow dispatch、本地/远端 build/test/analyze/codegen、安装、签名、删除、版本变更、发布、生产与凭据操作。
- 受影响：BuildMode 选项，以及 Android/Windows 的缓存和清理条件字符串；默认“常规编译”保持。
- Rust/Flutter/Android/Windows 产品代码、协议、API、数据、权限、签名资产：N/A，无改动。
- Asset：tracked workflow / repository-owned；外部资产无增量。
- 验收：旧标签在工作流中为零，新标签覆盖选项及全部九处判断；其余工作流文本不变。

## T3—T4：方案与确认

- 方案：对该工作流十处“完整编译”执行一致替换，避免只改下拉选项导致原始编译分支不再匹配。
- 备选：只改选项会使条件失配，拒绝；修改缓存策略超出请求，拒绝。
- 用户本轮明确请求即精确实现与提交授权，不另行索要确认。
- 兼容：新运行选“原始编译”；旧运行重跑仍使用旧提交，不修改历史。
- Rollback：经后续授权将十处标签统一恢复；无数据迁移、不可逆运行操作或发布。
- ADR / Decision：N/A — no decision delta，名称调整不改变长期设计。

## T5：变更记录

- `.github/workflows/Build-Clients-New.yml`：选项及 Android/Windows 判断同步改名；所有步骤、缓存策略和命令保持。
- `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md`、本任务与五份 `.codex` 记录：保存本次范围和验证事实。
- Generated / dependency / version / artifact：N/A，无改动；无既有 dirty 文件。

## T6：验证

- V0：PASS；新模式十处、旧模式零处；修改后的工作流与HEAD仅执行指定字符串替换的结果完全相同；diff空白检查通过。文件范围限于工作流及必要记录。
- V1—V5：NOT_RUN；用户明确要求本次不执行，不运行产品或工作流。
- 编译验证需求：N/A，本次仅名称替换，无构建执行请求。
- Security / privacy / license review：仅固定模式字符串变更，无新增输入执行、权限、凭据、下载、JNI 或许可变化。
- Build / release review：工具链、签名、产物、发布状态保持；不据此声明构建通过。

## T7：文档与记忆

- Updated：Build System、Task History、Project State、Current Work、AI Changelog、Change Event。
- N/A：ADR、Baseline 新增、Decision Log、External Registry、TEST_MATRIX、Security/Debug/Roadmap、入口和 Skills；无相应事实变化。

## T8：交付

- 结果：模式改名及静态验证完成，工程记录同步；无Decision/ADR/资产增量。
- Git：按本轮授权仅stage/commit本任务文件；最终SHA和工作区状态以Git及会话交付为准，不把自身提交SHA写入被提交文件。
- 本次不 push、不触发或监控 CI、不执行构建测试。
