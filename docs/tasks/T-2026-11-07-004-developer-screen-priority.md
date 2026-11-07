# T-2026-11-07-004：开发者连接画面优先

## T0 / Authority and Baseline

- Status：active / T7交付，源码及文档就绪，当前执行test提交推送与双端编排；用户要求不监控，正式结果待证。Owner：Codex / openworld-master，Network主导，Flutter/Rust/Security自审。
- 用户要求在test实现已分析方案：开发者连接Android时普通PC暂停画面，连接和ZEGO语音保留；新连接/重连同样适用；最后开发者离开恢复。
- 授权：切换现有test、业务源码及必要协议/文档修改；用户续接“提交test，并执行双端编译，不监控”，明确授权stage/commit/push origin/test及Build-Clients-BT / Both / 常规编译，沿用既有工作流测试签名与Actions产物；无main写入或生产发布。触发后仅记录接受回执，不监控run/jobs/logs。
- 起点main/9040c53 clean；切换test/d9f111944da644b427f7fbfc5970de6f14604ef4 clean。test产品身份OpenWorld、版本5.2.1；main另有2.0.26版本提交，本任务不合并或改版本。
- Baseline：OW-BL-2026-11-07-7361d39-identity-worktree（Baseline24），实际源码以上述test HEAD为准；无其他活动任务。

## T1 / Requirements

- A先连接并通话，B开发者加入：仅A画面暂停，不触发断连/挂断。
- B先连接，A新建或重连：A从授权起暂停画面，语音仍可用。
- 多开发者均可接收；全部退出/断链清理后恢复普通连接。
- 用户已通过异步问题明确选择“同时暂停操作，保留语音”：同时暂停普通连接输入、采集控制与ADB变更，与当前实现一致。该答复不涉及提交推送或编排授权。
- 开发者资格为现有客户端的进程内模式，经原端点认证后生效；不是可抵抗修改版客户端的授权凭证。现有口令不入协议/日志，不引入仓外签发服务。

## T2 / Impact

- Flutter本地模式→手写native桥→Rust进程状态→LoginRequest角色→Android按连接策略→普通VideoFrame/截图/ADB输出→PC暂停展示。
- 覆盖视频ACK/QoS、残留队列、解码回调、异常连接清理；保留MediaProjection生命周期和ZEGO状态机。
- 协议新增字段不复用；旧PC默认为普通，新PC遇到旧Android明确报告能力不支持。generated FRB不手改，protobuf由既有build.rs生成。
- 相关ADR：0021/0025/0027；新决定需记录画面优先与客户端角色信任边界。External Asset：无新增。

## T3 / Design

- Android唯一策略源维护授权普通会话及开发者集合，帧入队/出队双重过滤，保留状态/心跳/语音消息。
- 新连接在视频订阅前登记；关闭和Drop均幂等释放。恢复仅刷新现有正常视频编码与ADB关键帧，不申请权限或重启服务。
- PC将策略暂停与首帧等待/断连区分，保留语音入口，拒收旧画面，恢复等待新帧。

## T6 / Verification

- V0 PASS：`python scripts/check_product_identity.py`退出0，122项；`git diff --check`退出0。协议静态对照保留既有字段类型/编号，新17/3/42无冲突；手写C ABI两侧匹配；既有密码摘要、锁文件、generated、版本、工作流保持。
- 文档V0：23份变更Markdown、267个本地链接，缺失0；最终工作区43个变更路径，均为本任务增量，无删除。
- 已自审：注册先于视频订阅且只在授权普通会话；on_close/Drop幂等；Android/iOS/desktop feature边界；发送端/出队/解码发布过滤；Texture恢复通知、多窗缓存顺序、ACK/QoS、ADB关键帧与待剪贴板同步、无操作超时、ZEGO入口和生命周期分离。
- `src/screen_policy.rs`四项单测已加入，NOT_RUN。本机Get-Command未发现rustc/cargo/dart/flutter，正式V1/V2/V3/V4尚未执行；不能据静态检查承诺零编译错误或完美运行。
- 正式验证：FLT-08、AND-01/02/04、NET权限/兼容、E2E-05及新增双PC画面优先矩阵。
- 必测：两种连接顺序、多开发者、错误登录、普通重连、开发者崩溃、正常/ADB各模式、恢复关键帧、持续双向语音与新呼叫、旧端兼容。

## T7 / Documentation and Handoff

- 已同步架构/源码地图/模块/Android/协议/ADB/安全/调试合同、ADR-0028、Baseline26、TEST_MATRIX及工程记忆。D-035 / CE-20261107-T004-01/02 / STATE、WORK-20261107-004。
- 续接行为确认：CE-20261107-T004-03；STATE、WORK-20261107-004-r2。仅更新确认记录，源码与验证结果不变。
- External Asset Registry：reviewed-no-change；没有新增服务、凭据、依赖、工具链或外部资产。原SEC-016边界保持，不冒充可信角色认证。
- test工作区未提交；没有stage/commit/push/merge/编排/安装/部署；main无写入，版本及工作流无改动。回滚仅本任务增量，Git撤销另行授权。

## 《编译验证需求》

| 命令/动作 | 环境及执行目录 | 验证目标/停止边界 |
|---|---|---|
| `rustc --edition=2021 --test src/screen_policy.rs -o <临时目录>/openworld-screen-policy-tests`，再执行生成的测试程序 | Rust1.75正式构建机；仓库根目录；Windows输出使用.exe | 四项纯策略单测，不依赖产品native库；失败先修复，不计PASS |
| `build.cmd` | Windows正式构建机C:\DevEnv/C:\DevTool；仓库根目录 | PC源码、protobuf与DLL/Flutter链接；检查新C ABI加载；不发布 |
| `./build.sh 1` | Linux正式构建机、既有Android SDK/NDK/JDK/Flutter；仓库根目录 | Android native及APK编译/R8；不安装或改设备 |
| `Build-Clients-BT.yml`，ref=test，Platform=Both，BuildMode=常规编译 | 现有GitHub Actions；须先提交推送本任务增量 | 仅现有临时测试签名/Actions产物；核对源码SHA与Windows/Android结果；无生产发布 |

用户已明确授权本轮test提交推送及Both/常规编译，并要求不监控；CE-20261107-T004-04。仅核对dispatch接受及必要运行定位，最终构建结果不查询、不推定成功。编译结果由后续明确请求或正式回执验证，运行验收仍需两台PC与Android按DSP-04—10完成。
