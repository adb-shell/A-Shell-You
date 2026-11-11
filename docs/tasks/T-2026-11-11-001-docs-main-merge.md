# T-2026-11-11-001：项目文档同步与main合并

## T0 / Authority and Baseline

- Status：handoff / T7，文档与合并树已就绪，Git交付待记录；Owner：Codex / openworld-master，Release自审；日期2026-11-11（Asia/Tokyo）。
- 用户明确要求更新项目文档，完成后合并并推送主分支。授权文档编辑、必要switch/merge/冲突解决/stage/commit/push origin/main；不包含编排、产品测试、安装、部署、版本升级或历史重写。
- 起点test/51292c29a025ee3ad6a3915285b8f21e7e260ee8 clean，main/9040c53d37e3542c7ab8948b64beec1f0df0cd93；两分支均已与远端只读核对一致。共同祖先d9f111944da644b427f7fbfc5970de6f14604ef4；无并发活动任务。
- 恢复状态STATE/WORK-20261107-004-r4可解释：test有25bc2ce开发者画面优先及51292c2回执，main有df0e129版本2.0.26及9040c53回执。
- Baseline：OW-BL-2026-11-07-d9f1119-screen-priority-worktree（26）与OW-BL-2026-11-07-c27368c-version-worktree（25）。相关ADR-0027/0028、D-034/035。

## T1—T4 / Requirements, Impact, Design and Approval

- 整理当前文档中的旧快照、分支、版本和开发者画面优先说明；历史Task/冻结Baseline保持追溯，不复述口令。
- 合并保留main既有Rust/portable 2.0.26与Flutter2.0.26+59；功能/协议保持test/25bc2ce的实现。无新增业务代码、生成桥或依赖修改。
- 准备no-ff合并树，在合并提交前完成文档更新和冲突审查。文档历史日志保留两分支任务条目，当前状态统一为合并后的main；不以整份覆盖方式丢弃main版本历史。
- 验收：main包含test历史与主分支版本提交，最终非文档文件分别等于上述功能/版本来源，文档链接/身份静态合同通过，push后远端HEAD一致且工作区clean。
- 原“不监控”要求继续有效：不查询旧Actions结果，也不触发新编译。历史HTTP204仅证明接受请求，不代表构建成功。
- 当前指令已覆盖C1文档与C3合并提交推送，无需重复确认；若发现无法由两侧事实消解的产品冲突则停止对应变更。

## T6 / Verification

- V0 PASS：`python scripts/check_product_identity.py`通过122项身份合同；`git diff HEAD --check`通过，无残留冲突标记。31份新增/变更Markdown中的351个本地链接均存在。
- 来源核对PASS：相对test的全部非文档差异仅7份版本相关文件，且这7份与main/9040c53完全一致；因此功能、协议、生成桥、依赖与工作流无合并外增量。Baseline25/26正文分别与main/test原文一致；日志保留双方T002/T003/T004及对应事件。
- 十处冲突全部为文档并已解决；无业务冲突。push后远端引用与工作区状态另记交付回执。
- V1—V4：本轮不执行；DSP-02—10和既有构建/真机证据边界保持，不继承旧成功结论。

## T7 / Documentation and Memory

- 同步入口、项目总览/架构/源码地图/模块/协议、构建与ADB说明、ADR/Baseline索引、TEST_MATRIX及工程记忆；新增`docs/DEVELOPER_MODE.md`说明重试、双PC场景、暂停/恢复、版本和密码摘要维护。
- 新增Baseline27：`OW-BL-2026-11-11-main-developer-priority-merge`，固定合并输入与来源规则，旧Baseline25/26不重写。
- Decision Log：reviewed-no-change，沿用D-035，无新架构决定；External Asset Registry：reviewed-no-change，无新外部资产。
- STATE/WORK-20261111-001；CE-20261111-T001-01用于合并准备和文档，后续Git回执单独追加。

## T8 / Delivery

- 待完成静态验证、合并提交和push origin/main后记录准确SHA；本任务不触发编排或发布。
- 回滚由维护者根据合并parents逐项审查后另行授权，不自动reset/强推，不删除用户数据。
