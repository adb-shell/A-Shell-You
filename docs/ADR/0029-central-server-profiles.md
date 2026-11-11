# ADR-0029：集中端点与PC独立服务器配置切换

- Status：accepted for implementation / V0；2026-11-12。
- Decision：D-036；Task：[T-2026-11-11-002](../tasks/T-2026-11-11-002-server-profiles.md)。
- 批准：用户明确要求切换test实施；续接确认Android和PC1保持现有地址、PC2留空自行填写。

2026-11-12提交前：现有工作区已更新Android/PC1地址并填齐PC2；保留既有编辑，公共ID Key不变。用户追加授权test提交推送与双端常规编译，不监控。上面的“留空”记录为初次实施输入。

## Context

ID/relay/API/语音Token地址原来分散于Rust、Flutter和构建输入。PC需要在两套独立服务间切换，各服务对应自己的Android，共用ID服务器公钥；Android不能受PC操作影响。

## Decision

使用`hbb_common::server_profiles`作为单一编译期配置源：Android一套、PC两套及公共Key；PC持久化编号和revision，不复制端点。仅空闲时通过本机后台确认后切换，真实注册状态保持独立。PC2空白时不可选。

独立服务不共用账号授权或同名peer的设备配置/密码；PC1保留旧数据路径，PC2使用独立命名空间，切换要求重新登录。旧异步响应按revision/generation过滤；GUI与后台保持同一选择。没有新增FRB签名、protobuf或JNI合同。

## Alternatives

- 继续在多个语言/构建文件复制常量：维护易遗漏，未选。
- 运行时外部JSON覆盖：与本次“单一源码文件、编译后使用”要求不符，未选。
- 仅Flutter修改三个option：不能覆盖原硬编码中继/Token，也无法处理后台服务和账户缓存，未选。
- 活动会话无缝迁移：独立ID/中继不共享会话状态，本次选择空闲切换。

## Consequences / Validation

地址改动需要重新编译；B需准备对应Android及兼容Token broker。公共ID Key不是broker鉴权凭据。旧后台不支持profile切换，须更新完整PC产物。HTTP旧部署按用户要求保持；无真实服务访问、凭据变更或发布。

源码与维护说明见[Server Profiles](../AI_ENGINEERING/15_SERVER_PROFILES.md)。SCP-01为V0，其余单测、双端构建和联调均待证；不声明运行验收通过。
