# ADR-0027：开放世界产品身份

- Status：accepted for implementation；2026-11-07；Owner：当前用户。
- Task：T-2026-11-07-001；Decision：D-034；Supersedes：ADR-0014 的当前产品命名，保留历史事实。
- Source：main/7361d39 之上的工作区；参考 f423335e 迁移，旧构建回执不作为本次证据。

## 决定

用户可见名称“开放世界”；runtime OpenWorld；crate、native library、可执行文件、scheme、portable marker 为 openworld；Android applicationId/namespace、Apple bundle、Linux application ID 为 com.openworld.me。Android包路径为 com/openworld/me；macOS配置ORG为com.openworld，get_full_name对当前产品返回完整bundle ID。

客户端、localadb、平台元数据、安装/服务、云编译和本地脚本同步。flutter_hbb、hbb_common/scrap、非品牌随机类名及pkg2230/ffi保持；现有图标不重设计，带品牌资源文件名和引用同步。

## 链路与兼容

- C/JNI导出与Dart/C++/Kotlin/Java消费者成对迁移。openworld_status保留string类型及字段39，字段40/41不变；Rust生成变体为OpenworldStatus。
- 对外ZEGO请求继续发送已部署wire key ashellSessionId，本地变量改名；仓内broker接受openworldSessionId和旧键，新键优先。endpoint、凭据、room/stream派生及鉴权不变。
- 构建使用OPENWORLD_*；旧ASHELL_*只用于既有signing.env/工具链路径回退，不更换密钥或证书；新配置写出新前缀。
- 公共服务分类兼容新旧前缀，heartbeat使用同一函数；不推导或更改真实服务地址。
- Windows快捷方式显示“开放世界”，创建/启动/卸载消费者一致；内部服务/配置身份OpenWorld；portable marker长度由新字符串计算。

## 数据、证据与恢复

新Android包为独立安装身份，不自动继承旧包私有数据、系统授权或ADB配对；不重置设备UUID/硬件指纹算法/密码/密钥。新平台配置/安装身份不冒充既有产品的升级迁移。

第三方依赖/驱动/许可、真实仓库URL、外部签名注册资料及冻结历史任务/基线保持原事实。恢复点为7361d39；回滚需按任务清单反向审查，保护后续变更，不自动删除安装数据。没有Git写入、部署或发布。

## 验证

scripts/check_product_identity.py执行V0合同核验。Android/Windows build、JNI/R8/FFI加载、安装及新旧peer真机组合仍需V2—V4。Apple/Google新bundle注册和签名profile是外部资产，不能由字符串替换证明有效。
