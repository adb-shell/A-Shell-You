# 开放世界产品身份迁移

2026-11-07；T-2026-11-07-001 / D-034 / ADR-0027。起点main/7361d39、clean；参考f423335e“更新产品信息及指纹”。源码已按用户要求交付test/ad88665，Both/常规编译run42350649478已触发；随后按用户要求停止监控，最终结果未查询。见[任务回执](../tasks/T-2026-11-07-001-openworld-identity.md)。

## 当前名称与影响

| 范围 | 当前值与消费者 |
|---|---|
| 显示 | 开放世界；Rust/Flutter helper、Android labels、Windows资源/标题/快捷方式、Linux/Apple元数据 |
| runtime | OpenWorld；配置/日志/服务/IPC/portable目录；macOS ORG=com.openworld |
| native | openworld crate/library；libopenworld.so / openworld.dll / libopenworld.a / libopenworld.dylib；全部导出/加载端同步 |
| 包与scheme | com.openworld.me / openworld://；Gradle/三份manifest/Apple工程/Linux/Flatpak/Intent |
| Android | Java/Kotlin产品包迁至com/openworld/me；随机类名、pkg2230.ClsFx9V0S、ffi.FFI、hbb.KeyEventConverter保持 |
| ADB | com.openworld.me/local_adb/v1；Java_com_openworld_me_*、Shell类名/R8、配对alias、临时文件/线程、帧桥成对同步 |
| 协议 | openworld_status字段39 / OpenworldStatus；状态模型/事件/配置键同步；所有字段编号/类型保持 |
| 打包 | openworld portable marker及派生长度、openworld-portable-packer、PC-Bulid/openworld.exe、Android本地/CI产物、安装/服务/desktop/PAM |
| 云编译 | Build OpenWorld Clients-BT；原输入/jobs/actions pins/permissions/工具链/步骤保持，品牌路径/缓存/临时证书名同步 |
| 文档 | 当前入口/领域文档、openworld-*项目Skills、Task/ADR/Baseline及薄记忆同步 |

macOS launchd/卸载模板分别使用com.openworld.me和实际配置目录；更新挂载目录、app包名及内部可执行名跟随OpenWorld。模板中的上游占位文本在执行前由correct_app_name转换。

必要重命名包含Android两棵包目录、九个Skill目录、Flatpak两份文件、Linux desktop/service/PAM和悬浮球PNG。图像内容、scrcpy资产、依赖版本/checksum、产品版本、认证/权限/采集/重连/输入控制流保持。

## 有意保留的标识

1. src/client/helper.rs的ashellSessionId是外部已部署JSON合同；局部变量已改名，HTTP键保持以免强制服务发布。仓内broker同时接受新旧键，新键优先。
2. build.sh / env.sh的ASHELL_*仅作为现有构建机回退，OPENWORLD_*优先；凭据值和外部signing.env不改。
3. src/common.rs::is_public兼容旧域名前缀；新品牌同时支持，heartbeat复用分类；真实endpoint不改。
4. 冻结历史任务/基线/决定、历史报告链接、实际仓库URL及第三方aShellYou来源保留原名；Git历史和当前仓库目录不重写。
5. flutter_hbb、hbb_common、scrap、pkg2230/ffi、第三方RustDesk/驱动/SDK标识、许可及外部Apple/Google注册资料不属于产品名称替换。

本次不轮换签名/凭据，不重置设备指纹。新Android包独立安装，不继承旧包私有数据、权限或ADB配对；新Windows身份不自动搬迁旧数据。

## 验证边界

- scripts/check_product_identity.py检查产品清单、组件/JNI/R8/Shell/C ABI、protobuf、portable、CI、版本/依赖/安全输入及图片。数量以任务回执为准。
- 执行Python AST、TOML、XML/plist、Shell语法与差异检查；未执行产品进程。
- generated_bridge.dart只同步三个产品C导出名，不改FRB API/结构/序号；正式生成和平台链接待工具链验证。
- iOS exportOptions.plist的非法XML引号在原HEAD同样存在；生产profile/Google注册仍为外部未验证。

## 编译验证需求

| 环境 | 仓库根入口 | 验证目标 |
|---|---|---|
| 正式Linux Android | ./build.sh 1；./build.sh 2 | 包名/labels、libopenworld各ABI、R8/JNI、启动授权、ADB与原链 |
| 正式Windows | build.cmd | DLL导出加载、portable包头、快捷方式/服务/安装卸载、PC-Bulid/openworld.exe |
| GitHub Actions | Build-Clients-BT.yml / Both / 常规编译 | 包含本次改动的精确提交、双端构建、签名/产物/run证据 |

本机未发现产品工具链；使用用户明确授权的test云编排，run42350649478/head ad88665。V0不能证明编译或运行通过，编译PASS也不能代替设备验收。正式运行验证覆盖AND-08、ADB-R07/M01/M16/M20、WIN-08、FLT-05、RST-03/06及新旧peer组合。
