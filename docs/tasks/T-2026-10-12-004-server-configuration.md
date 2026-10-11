# T-2026-10-12-004：服务器默认配置迁移

- Status：completed / T8 / V0；Owner：当前用户；执行：Codex / master + Network/API静态复核。
- Source：test/f423335e90885975c101484977abbe8c7524b42e；initial clean，与origin/test一致。既有品牌迁移已提交并推送，旧a036f31未提交快照不再代表当前HEAD。
- Baseline：AS-BL-2026-10-12-a036f31-worktree经f423335固化；本轮追加服务器配置变更，不改产品身份/版本。
- Authority：用户明确提供新IP、端口映射和新Key，授权替换仓内源码/脚本/文档；敏感具体值不在新增task/log/memory重复，仅保留于既有配置位置。
- Plan：替换匹配的旧IP和Key；七个端口按顺序映射到10083—10089；核对RENDEZVOUS_PORT派生、PUBLIC_RS_PUB_KEY/RS_PUB_KEY覆盖、relay显式值、API和ZEGO、.info与部署脚本默认值；不修改第三方/无关端口。
- Impact：默认服务位置和公钥信任锚变化；协议类型/字段不变。外部服务、已安装客户端保存的自定义配置、构建环境变量不由本轮文件修改自动迁移。
- Test Matrix：NET-01/NET-02、API-04/API-05/API-08；仅V0源码/文本复核，正式连接/握手/账号/语音验证NOT_RUN。
- Forbidden：构建/测试/analyzer/codegen、安装、实际密钥试用、线上请求、服务启动/部署、Git stage/commit/push。
- T6：12份含原值的文件逐一与HEAD按指定映射转换的预期文本比较一致；排除.git后旧IP、七端口字面量和Key零残留；新公钥可Base64解码为32字节。RENDEZVOUS/API/relay/WebSocket及现有派生关系已静态核对；Flutter内部flutter_hbb保留，diff检查通过。未发现映射到10085的原端口字面量，不推测用途。
- T7—T8：Network/API文档、外部资产、Decision/Task/Event和恢复入口同步；D-017 / CE-20261012-T004-01 / STATE-20261012-005 / WORK-20261012-008。未提交、推送或部署。
- 限制：`RS_PUB_KEY`、`RENDEZVOUS_SERVER`、`API_SERVER` 构建环境/CI秘密值、`Config::get_rendezvous_servers()` 的覆盖配置及已安装客户端保存值没有由本任务自动迁移。公钥格式通过不证明hbbs/hbbr私钥配套或服务可用。
- 回滚：未来明确授权后对本轮配置patch反向恢复，不覆盖其他工作。

## 编译验证需求（本轮不执行）

- Android正式Linux构建机、仓库根目录：按 `docs/AI_ENGINEERING/08_BUILD_SYSTEM.md` 的 `./build.sh 1` / `./build.sh 2` 入口验证相关ABI；Windows正式构建机、仓库根目录：`new-build.cmd`。先核对环境覆盖值和外部依赖，目标为新默认配置进入产物。
- 正式设备与配套服务：后续另行授权验证NET-01/NET-02和API-04/API-05/API-08，包括注册/中继/公钥握手、账号API和ZEGO Token。结果全部NOT_RUN，本任务不承诺编译或连接成功。
