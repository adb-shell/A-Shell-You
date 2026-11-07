# T-2026-11-07-002：开放世界合并main与双端编排

## T0：任务、基线与授权

- Status：complete / T8（合并、推送与触发交付）；2026-11-07（Asia/Tokyo）；Owner：当前用户 / Codex；Primary：openworld-release-engineer，身份合同沿用openworld-master。
- 当前指令：版本号先不改，将当前test合并main并推送GitHub，提交说明“更新产品信息及指纹”，执行双端常规编译，不监控。
- 起点：test/d9f111944da644b427f7fbfc5970de6f14604ef4，main/7361d39d34ad0bb147827e30fa61419a00b84038；本地/远端一致，工作区clean，无未完成merge。
- 先前2.0.26/2026版本调整和未完成合并已按用户要求撤销，未提交、未推送、未触发编排；本任务不恢复该方案。
- Baseline24 / OW-BL-2026-11-07-7361d39-identity-worktree；产品身份源码ad88665，当前test文档回执d9f1119。沿用ADR-0027 / D-034，N/A — no decision delta，无新外部资产。
- State/Registry：STATE-20261107-002 / WORK-20261107-002；Events：CE-20261107-T002-01/02；矩阵ID-01/02，ID-03仅触发，ID-04运行NOT_RUN。
- 当前授权：main switch/merge/stage/commit/push origin/main及必要交付文档；一次Build-Clients-BT / Both / 常规编译，沿用已说明的临时测试签名/Actions产物。不改版本、不监控、不重复dispatch、不做tag/release/生产签名/凭据或设备操作。

## T1–T5：范围与合并

- 合入当前test完整产品身份修改，保持Rust/portable5.2.1、Flutter5.2.1+59；无业务、协议、依赖、工作流增量。
- main执行no-ff merge test，保留双方提交历史；15处冲突全部是文档，按已复核的test当前内容解决。补交付文档前，暂存合并树与test完全一致。
- 本任务只增加当前交付记录/记忆和入口指针；旧Task/Baseline保留历史。先前复查的可选插件/旧IDD兼容边界维持，不扩展为修复任务。
- 恢复点main7361d39、testd9f1119；如需撤回，由维护者单独审查合并revert，不自动reset/强推/删除用户数据。

## T6–T8：验证与回执

- V0：`python scripts/check_product_identity.py`退出0，135项静态身份合同通过；与test相比仅10份交付Markdown有差异，产品/工作流完全一致。137个本地文档链接有效，无未解决冲突，`git diff --check`与`git diff --cached --check`均退出0。版本5.2.1 / 5.2.1+59及已撤销版本任务/基线文件不存在均已核对。
- Git提交说明统一“更新产品信息及指纹”；test分支保持d9f1119，不改写历史。
- main编排只记录HTTP触发回执，不查询运行进度、日志或产物；没有编译/运行PASS结论。

### 精确交付回执

- 合并提交：`482eda5cd1404617512009cd032cc87e202c8d16`，说明“更新产品信息及指纹”；parents为main/7361d39与test/d9f1119。`git push origin main`退出0，`git ls-remote`确认origin/main指向482eda5、origin/test保持d9f1119；提交后工作区clean。
- 2026-11-07 17:38:48 +09:00，一次POST `Build-Clients-BT.yml/dispatches`获HTTP204，workflow394791509，ref=main，Platform=Both，BuildMode=常规编译。发起前再次核对远端main精确SHA为482eda5；未查询run/jobs/logs/artifacts，不记录run ID或构建结果。
- 后续仅同步本回执及当前入口/记忆，以同一提交说明补文档提交并推送main；产品、版本和工作流保持482eda5，不再次dispatch。T8关闭本次Git及触发交付，不表示编译或运行通过；新会话不能自动恢复监控。

《编译验证需求》
- 命令：Build-Clients-BT.yml workflow_dispatch，ref=main，Platform=Both，BuildMode=常规编译。
- 环境：现有GitHub Windows/Ubuntu runner及固定工具链，工作流临时测试签名/Actions产物。
- 执行目录：精确main合并提交检出后的仓库根目录。
- 目标：Windows x64与Android aarch64客户端构建；按用户要求不监控，不从dispatch接受推断成功。
