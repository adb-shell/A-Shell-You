# AShell identity observed baseline

- Baseline ID：`AS-BL-2026-10-12-a036f31-worktree`。
- Source：`test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76`；身份迁移为该提交之上的未提交工作树，不是commit内容。
- Task/Events：`T-2026-10-12-002` / `CE-20261012-T002-01`、`CE-20261012-T002-02`；Decision：D-015 / ADR-0014。
- Scope：repository-observed / V0；formal build/runtime NOT_RUN。
- Product：A Shell You；runtime AShell；ORG me；Rust crate/library ashell；Flutter内部package保留flutter_hbb。
- Android：applicationId/namespace me.ashell；后台A Shell You；launcher一个壳子；scheme ashell；libashell.so。
- Windows：ashell.exe/ashell.dll；正式打包入口new-build.cmd；最终PC-Bulid/ashell.exe；用户界面/资源/服务显示名A Shell You。
- Versions：Rust5.2.1，Flutter5.2.1+59，不变；Cargo依赖版本/checksum不变；pubspec.lock不变。
- Generated：FRB已跟踪C声明只同步产品符号，未执行生成器；protobuf field39仅改源名称，保留类型/编号；后续正式生成/链接结果待验证。
- Compatibility：保留公共服务guard、上游依赖/驱动/许可与历史证据；按用户补充要求只用ASHELL_*构建变量，ZEGO字段ashellSessionId，不保留旧键兼容。构建机signing.env和实际服务未更新/部署；新Android包/配置身份不迁旧用户数据或系统权限。

后续T003补充：两处公共服务匹配按用户要求统一为ashell.；全部文档品牌术语/路径规范化，原始快照可从a036f31恢复。其余运行/验证限制不变；本节补充取代上行对旧匹配字符串的保留方案。
- Constraints：jniLibs/libadb资产缺口、ZEGO lock drift、Windows native依赖和Apple/Google注册未闭环；不能宣称完美编译、可运行或发布就绪。
- Details：[迁移报告](../AI_ENGINEERING/ASHELL_IDENTITY_MIGRATION_2026-10-12.md)。

旧 `CS-BL-2026-10-12-6fb0aa6` 和7月冻结基线保留。未来核对新HEAD与工作树；该基线不是长期权限凭证。
