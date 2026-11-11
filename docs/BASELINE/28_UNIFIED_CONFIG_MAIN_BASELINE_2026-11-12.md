# Baseline28：统一配置合并main

- Baseline ID：`OW-BL-2026-11-12-unified-config-main`
- 日期：2026-11-12；证据等级：repository-observed / V0，不代表构建或运行验收。
- 主分支输入：`5d2d2e932cfb390a7b3d0278bea6a7e080746c95`。
- 功能分支输入：`a8abdd4c04951f930ea240c9951378ee3b8c165f`。
- 共同祖先：`51292c29a025ee3ad6a3915285b8f21e7e260ee8`。
- [任务与Git回执](../tasks/T-2026-11-12-002-unified-config-main-merge.md)。提交说明：`统一配置文件(大改造节点一)`。

## 来源与保留边界

继承[Baseline27](27_MAIN_DEVELOPER_PRIORITY_BASELINE_2026-11-11.md)中的产品身份、开发者画面优先与主分支版本。Rust/portable保持2.0.26，Flutter保持2.0.26+59，构建号59；主分支manifest、lockfile、build.cmd和身份校验脚本保持原内容。

来自test的服务器配置、PC状态栏切换、缓存与账号隔离、Rust编译修复和Android依赖安装续修整体纳入。`libs/hbb_common/src/server_profiles.rs`完整采用a8abdd4，包含用户d3b3f54/a8abdd4的后续修改；三套四项均已填写，Android与PC1当前不同，共用ID公钥。文档不重复实际地址或公钥值。

源码无合并冲突；15份Markdown冲突按当前版本/实现与双方历史分别整合。来源文件保持原分支内容，本次不增加业务逻辑、协议字段、generated桥接、依赖或外部部署。

## 验证与限制

本轮仅执行合并来源、文件一致性、文档链接、差异和Git状态等V0核对，不执行编译、单测、codegen或监控。合并前c788e8d的Both/常规编译只记录HTTP204受理，最终结果未知；13项离线CI检查属于历史证据，不能证明实际runner或服务可用。SCP、DSP真机/联调项仍待证。

源配置和切换合同见[15_SERVER_PROFILES](../AI_ENGINEERING/15_SERVER_PROFILES.md)、[ADR-0029](../ADR/0029-central-server-profiles.md)。历史基线冻结保留；后续恢复以实际HEAD/status和远端分支为准。
