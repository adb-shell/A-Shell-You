# T-2026-10-12-002：AShell 产品身份迁移

## Metadata / T0

- Status：completed — source changes and V0 only / T8；2026-10-12 Asia/Tokyo。
- Owner：当前用户；执行/跨域审查：Codex，ashell-master + Rust/Android/Flutter/Release/Security Skills。
- Source：test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76；initial clean。
- Baseline：CS-BL-2026-10-12-6fb0aa6 为源码观察参照；当前 HEAD 增加上轮记忆文档提交，源码身份未变。
- Authority：当前请求明确授权产品命名、包名、文件/目录及构建脚本修改；必要重命名属于本次明确范围。禁止执行构建、测试、analyzer、codegen、安装依赖、Git写入及发布/生产操作。
- State：STATE-20261012-002；Registry：WORK-20261012-003；Event：CE-20261012-T002-01。
- ADR：新增身份决定取代 ADR-0002；D-015。适用 TEST_MATRIX：AND-08、WIN-08、FLT-05、RST-01、RST-03、RST-06；本轮只做 V0，V1—V5 NOT_RUN。

## T1–T4：需求、影响与方案

| 领域 | 目标与约束 |
|---|---|
| 身份 | 内部 runtime AShell；小写 crate/artifact/scheme ashell；bundle/applicationId me.ashell |
| 显示 | PC 与 Android application A Shell You；Android launcher 一个壳子；身份与展示分开，避免空格进入 executable/scheme |
| ABI | Cargo库、DLL/SO、Dart/Kotlin/C++调用同步；pkg2230/ffi包本身不含旧品牌，保留JNI符号 |
| 构建 | 正式Android/Windows及保留平台引用；portable包标识字节长度、lock中workspace包条目、CI产物路径一起检查 |
| 协议/服务 | 本地状态事件/MethodChannel两端同步；protobuf field39保留编号；外部HTTP contract、endpoint和凭据不凭品牌推断新地址 |
| 历史/第三方 | 保留许可、上游依赖来源、旧基线/ADR/审查快照；当前文档与源码路径更新并登记例外 |

迁移含义：me.ashell为独立Android安装身份；AShell配置/服务目录与旧产品分离，不自动搬用户数据、不继承系统授权。恢复旧版本可使用原包和原数据，未执行任何卸载或设备操作。

Generated boundary：不改FRB类型/消息编解码结构；仅对已跟踪的C符号绑定执行同名迁移，与手写Rust导出保持一致。生成器仍为FRB1.80.1/cbindgen+ffigen，正式环境需重生成核对；本轮不执行codegen。protobuf只改源定义及调用者，由原构建链生成。

用户补充约束：Flutter 内部 package 必须保留 `flutter_hbb`；已撤回 `name: ashell` 和 `package:ashell/` 导入替换。这与 Android applicationId、原生库名及产品显示名独立，不以统一品牌为理由继续改动内部包名。

用户后续明确允许更改构建机旧变量和ZEGO字段：移除旧前缀回退，仅用ASHELL_*；JSON字段迁为ashellSessionId，客户端/内嵌服务DTO和示例/当前文档同步。本轮不更新构建机、不部署服务；正式启用须两端一起更新。记录于CE-20261012-T002-02。后续T003统一两处公共服务匹配文字及全部文档品牌措辞。

## T5–T8：实施与验证记录

实施范围、物理重命名、兼容保留项、V0证据和《编译验证需求》见 [迁移报告](../AI_ENGINEERING/ASHELL_IDENTITY_MIGRATION_2026-10-12.md)。新增 [观察基线](../BASELINE/06_ASHELL_IDENTITY_BASELINE_2026-10-12.md)；State STATE-20261012-003，最终Registry WORK-20261012-004，Event CE-20261012-T002-01。

验证：30项静态身份断言通过；包含9个XML/plist解析、Android组件和两种label、加载符号、portable marker、protobuf39类型/编号、Cargo.lock只改项目包名、FRB只改native符号、Flutter内部包名/import恢复、版本不变、diff whitespace。未执行编译、测试、analyzer、codegen、安装、产品或生产验证；不能保证产物可运行或发布。

T7同步：当前源码/构建引用、工程文档、ADR-0014/D-015、baseline、state/memory、event/history/changelog；历史报告/基线保留原事实。T8关闭仅表示用户授权的源码身份迁移和静态交付完成，外部资产及正式编译验证仍NOT_RUN。用户未来如要求回滚，应按a036f31逐项审阅反向patch，不覆盖后续工作。
