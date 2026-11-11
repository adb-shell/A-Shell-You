# Baseline27：main版本与开发者画面优先整合

- Baseline ID：`OW-BL-2026-11-11-main-developer-priority-merge`
- 日期：2026-11-11（Asia/Tokyo）；Owner：Codex / openworld-master；Release自审。
- 状态：SOURCE-PINNED输入 / V0合并快照；不代表构建或运行通过。
- 任务：[T-2026-11-11-001](../tasks/T-2026-11-11-001-docs-main-merge.md)。准确合并SHA、push及静态验证结果由任务追加回执记录，本快照不预写尚未生成的commit。

## 输入与保留规则

| 来源 | 固定输入 | 合并保留 |
|---|---|---|
| main | `9040c53d37e3542c7ab8948b64beec1f0df0cd93` | 版本源码df0e129、Rust/portable 2.0.26、Flutter 2.0.26+59、默认构建版本及身份静态校验 |
| test | `51292c29a025ee3ad6a3915285b8f21e7e260ee8` | 功能源码25bc2ce、开发者画面优先协议与实现、历史编排受理回执 |
| 共同祖先 | `d9f111944da644b427f7fbfc5970de6f14604ef4` | 既有产品身份、ADB和原功能基础 |

合并使用no-ff保留双方历史。十处冲突均为文档；历史日志同时保留主分支版本任务与test功能任务，当前入口/状态改为整合语义。非文档文件以test为功能来源，七份版本文件以main为准：`Cargo.toml`、`Cargo.lock`、`flutter/pubspec.yaml`、`libs/portable/Cargo.toml`、`libs/portable/Cargo.lock`、`build.cmd`、`scripts/check_product_identity.py`。本轮无新的产品、协议、依赖、生成桥或工作流改动。

## 当前合同

- 产品“开放世界 / OpenWorld / openworld / com.openworld.me”，版本2.0.26 / 2.0.26+59，构建号59保持。
- PC双密码校验后启用进程模式，供新建/重连携带角色；Android只登记已认证普通远控会话。
- 开发者存在时，普通PC暂停普通/截图/ADB画面及输入/采集控制，保留连接和ZEGO；最后开发者离开恢复。
- 角色为协作声明，不是抗伪造凭据；沿用D-035 / [ADR-0028](../ADR/0028-developer-screen-priority.md)，没有新增架构决定或外部资产。

## 验证与历史边界

本轮V0核对来源一致性、版本、静态身份合同、Markdown链接和Git差异/远端引用；不执行编译、单测、codegen、设备或发布，也不监控历史编排。四项策略单测未跑，多PC语音与旧版本兼容仍待真机验收。

2026-11-07 test/25bc2ce的Both/常规编译仅记录HTTP204受理，最终结果未知；它不证明本次main整合版本已构建通过。[Baseline25](25_OPENWORLD_VERSION_BASELINE_2026-11-07.md)、[Baseline26](26_DEVELOPER_SCREEN_PRIORITY_BASELINE_2026-11-07.md)和旧任务正文冻结保留，不能把其中的旧分支、旧版本或未提交状态当作当前状态。
