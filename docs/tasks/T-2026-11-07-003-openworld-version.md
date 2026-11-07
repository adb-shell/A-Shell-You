# T-2026-11-07-003：产品版本改为2.0.26

## T0–T4：授权、基线与方案

- Status：complete / T8（版本修改与Git交付）；2026-11-07（Asia/Tokyo）；Requester/Owner：当前用户；Primary：openworld-release-engineer。
- 用户明确要求“将5.2.1版本号更改为2.0.26”；只修改产品版本及必要校验/文档，Flutter构建号59保持。后续明确“提交并推送”，授权暂存、提交并推送当前main到origin/main；提交说明“更新版本号为2.0.26”。不触发或监控编译，不恢复先前已撤销的2026构建号方案。
- 起点：main/c27368cfb37f6e30217f06c8da7734065b0dccc5，工作区clean，无活动任务。此前main编排对应482eda5，与本轮版本无关。
- 来源Baseline24：OW-BL-2026-11-07-7361d39-identity-worktree；新快照Baseline25：OW-BL-2026-11-07-c27368c-version-worktree。
- State/Registry：STATE-20261107-003 / WORK-20261107-003；Event：CE-20261107-T003-01。沿用ADR-0027，N/A — no decision delta；无外部资产、依赖或工具链变化。
- 影响：Cargo主程序及portable包版本和锁文件本项目条目、Flutter pubspec、Windows build.cmd默认值、已有静态身份检查及当前版本文档。源码逻辑、协议、凭据和构建流程不变；历史Task/Baseline和第三方版本保留。
- 版本消费者：Rust build.rs调用hbb_common::gen_version生成版本；Android Gradle、Windows Runner及Apple plist取Flutter构建版本；build.py读取Cargo版本；portable资源取自身Cargo包版本。不手改生成文件。
- 回滚：如用户要求，逐项反向审查本轮版本差异，恢复5.2.1与5.2.1+59；无数据迁移，无自动reset或清理。

## T5–T6：修改与验证

- 将主Rust包、portable包和对应锁条目改为2.0.26，Flutter改为2.0.26+59，Windows默认VERSION改为2.0.26；同步现有身份检查版本预期。
- V0：`python scripts/check_product_identity.py`退出0，122项静态身份合同通过；当前HEAD已迁移，原135项中的13项迁移前基线检查不适用。独立TOML和逐文件对比确认7份非文档文件仅预期版本/校验差异，第三方锁条目/checksum、Flutter构建号59、产品逻辑与工作流不变，未暂存。
- ID-01本轮PASS；ID-02本次适用项PASS：17份Markdown的204个本地链接有效，`git diff --check`无错误。ID-03与WIN-08构建/产物运行NOT_RUN，本轮没有编译成功结论。

《编译验证需求》
- 命令：后续单独指令下执行Build-Clients-BT.yml / Both / 常规编译。
- 环境/目录：正式GitHub Windows/Ubuntu runner，准确版本提交检出的仓库根目录。
- 验证目标：Android versionName=2.0.26、versionCode=59；Windows/portable和运行时版本2.0.26，构建号59保持。本轮不执行。

## T7–T8：同步与交付

- 同步当前入口、总览/构建文档、版本基线及状态/任务记忆；历史交付回执保留原版本。
- ADR/Decision Log：N/A — no decision delta；External Asset Registry、安全/调试/路线图：N/A — 无资产、权限、流程或长期风险变化；TEST_MATRIX：沿用ID-01/02/03、WIN-08，在本任务记录版本专属证据，不改历史结果。
- 本地编辑阶段T8完成时HEAD为c27368c、index未变化，工作区为7份版本/校验文件及17份版本相关文档修改。用户随后授权提交推送，重新核对范围和diff后完成Git交付。编译、签名与发布均未执行。

### Git交付回执（CE-20261107-T003-02）

- 版本源码提交`df0e129c2431b39c38b0ac004d340737cd87bdce`，说明“更新版本号为2.0.26”；`git push origin main`退出0，`git ls-remote`确认origin/main与本地一致，提交后工作区clean。
- 后续只同步本回执和当前状态/入口文档并提交推送；版本、产品源码及工作流保持df0e129，不触发编译，不查询旧编排结果。
