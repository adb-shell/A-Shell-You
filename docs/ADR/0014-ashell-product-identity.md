# ADR-0014: AShell Product Identity

- Status：accepted；Record Type：contemporaneous。
- Decision Date：2026-10-12；Owner：当前用户明确提出的产品迁移要求。
- Related Decision Log：D-015；Task：T-2026-10-12-002。
- Supersedes：ADR-0002；Implementation：source edits；Verification：V0 only，V1—V5 NOT_RUN。

## Decision

产品显示名为 A Shell You；内部 runtime AShell、小写标识 ashell。Rust crate/library 为 ashell；Flutter 的内部 package 保留 flutter_hbb，用户明确要求撤回其改名及关联导入变更。Android applicationId/namespace、Apple bundle为me.ashell。Android application/后台使用A Shell You，launcher alias显示一个壳子。URI scheme为ashell；DLL/SO/EXE对应ashell.dll/libashell.so/ashell.exe。

采用显示名与系统标识分离，避免空格进入scheme、Cargo crate及可执行文件名。同步源码、跨层ABI/本地事件、构建脚本、workspace lock条目和当前文档。Android源码迁至me/ashell，ADB类前缀AShell；项目Skill目录迁至ashell-*。

## Compatibility and alternatives

不采用只改UI的方案，因为旧包名/原生库/打包路径仍会暴露旧身份并可能断链。也不对上游依赖、驱动、外部服务契约、历史证据作无差别替换。

新Android包与旧包独立，不自动迁移用户配置、Accessibility/MediaProjection授权或ADB配对状态。AShell本地配置/服务目录独立，旧产品配置保持原处。protobuf状态字段保留39及原类型，只迁移源名称；旧PC与新Android跨版本行为待验证。用户补充明确允许移除旧前缀环境回退、将ZEGO字段改为ashellSessionId：本地构建脚本与客户端/内嵌token服务源码已同步；正式构建机signing.env和实际服务须同步更新，本轮未操作。T003进一步按用户明确要求把两处公共服务匹配统一为ashell.，并规范全部文档品牌术语/文件名；Flutter内部包名保留。

## Security, release and rollback

不改变权限、密码、凭据、签名、版本或第三方下载来源；旧许可和历史记录保留。签名资产、新bundle的Apple/Google注册、native ADB和Windows驱动需要正式环境owner验证。静态名称一致不能证明编译或运行成功。

回滚是恢复迁移前源码/产物并继续使用旧包及其数据，必须由后续用户授权执行；本轮没有Git、卸载、安装或数据搬迁。正式验证见身份迁移报告及AND-08/WIN-08/FLT-05/RST-01/RST-03/RST-06。
