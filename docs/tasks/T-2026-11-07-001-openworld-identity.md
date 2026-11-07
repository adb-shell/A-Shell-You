# T-2026-11-07-001：开放世界产品身份迁移

## Metadata / T0

- Status：active / T6；T5实现与V0审查完成，用户已确认test提交推送和双端云编译，正式结果待证；2026-11-07（Asia/Tokyo）。
- 用户明确要求：显示名“开放世界”，AShell/ashell 产品身份迁为 OpenWorld/openworld，现有 me.ashell 迁为 com.openworld.me；包括后续 ADB、云编译编排及文档同步，仅修改产品信息。
- Source：main / 7361d39d34ad0bb147827e30fa61419a00b84038；初始工作区 clean；参考迁移 f423335e90885975c101484977abbe8c7524b42e。
- Baseline：Baseline23 / AS-BL-2026-10-25-48226fb-developer-password；本次追加 Baseline24。
- Primary：Codex / openworld-master（本次由ashell-master更名）；Rust、Flutter、Android、Network/API、Release/Security 合同由主执行者逐项核对。
- Authority：C2产品身份、必要路径及文档；用户对提交推送/双端编译确认单回复“切换到test分支，然后继续”，据此授权test切换、stage/commit/push origin/test、Both/常规编译及监控，沿用工作流临时测试签名/Actions产物；不含main写入、远端部署、凭据轮换或业务重构。
- Validation：用户要求确保编译和链路；本机尚未发现 Rust/Flutter/Java/CMake 正式工具链，先完成 V0，再记录正式构建缺口。不能把静态检查当作 V2/V3 通过。
- Registry：WORK-20261107-001；Event：CE-20261107-T001-01；Decision：D-034 / ADR-0027。

## T1–T4：范围、影响与方案

| 层 | 迁移内容及不变量 |
|---|---|
| 产品 | 开放世界 / OpenWorld / openworld / com.openworld.me；版本 5.2.1 / 5.2.1+59 不变 |
| Rust/Flutter | crate、库名、C ABI 导出与消费者、Dart 状态类型/事件统一；flutter_hbb、hbb_common、scrap 等内部/上游标识保留 |
| Android | Gradle/manifest/package 路径、ADB JNI/Shell/R8、资源名、Intent、MethodChannel 同步；非品牌随机类名和 pkg2230/ffi ABI 名保持 |
| Windows/Linux/Apple | runner、资源、安装/服务/portable、bundle/scheme 与加载路径同步；既有第三方驱动和依赖身份保持 |
| 协议/API | protobuf field39/40/41 编号、类型及编码不变；外部服务地址/凭据不变；已部署 ZEGO 请求字段保留兼容，仓内服务支持新旧键 |
| 构建 | 唯一 Build-Clients-BT 工作流及本地脚本同步新产品名、native artifact、缓存和测试签名展示；旧签名环境变量仅作为明确兼容入口 |
| 数据 | 新 Android package 是独立安装身份；不复制旧数据/系统授权/ADB 配对；不更改设备 UUID、硬件指纹算法、密钥或认证逻辑 |
| 文档 | 当前工程文档、入口、Skills 和记忆同步；冻结历史任务/基线/决策证据保留原名称，避免伪造历史产物 |

恢复点为本次修改前 HEAD；必要重命名只发生在当前仓库内，无 Git 索引/历史修改。若回滚，须按本任务变更清单逐项反向应用，保留之后的用户工作。

Generated：只同步身份相关的既有 C 符号绑定，不改 FRB API 类型/序号/消息结构；正式生成链与产品编译仍需对应工具链验证。新旧 peer 组合、JNI 加载、ADB 四条独立链、安装/启动/卸载为重点。

TEST_MATRIX：AND-08、AND-12/13、ADB-R07、ADB-M01/M16/M20、WIN-08、FLT-05、RST-01/03/06、NET/API 兼容与 E2E-03。

## T5–T8：实施与验证

源码、必要路径及当前文档同步完成，尚未提交。包括Android两棵包目录、九个Skill目录、Flatpak/Linux打包资源及PNG共71个文件路径重命名；PNG内容SHA-256相同。当前实现/兼容边界见[身份迁移](../AI_ENGINEERING/OPENWORLD_IDENTITY_MIGRATION_2026-11-07.md)，决定为[ADR-0027](../ADR/0027-openworld-product-identity.md)。

- V0 PASS：`python scripts/check_product_identity.py`，135项产品、JNI/R8/Shell/C ABI、protobuf、portable、macOS launchd/update及依赖/认证输入合同；桥接仅三个C符号改名，protobuf只改字段名/注释而不改编号/类型。
- V0 PASS：四份Shell `bash -n`；CI内14段Bash、14段PowerShell分别语法解析，嵌入Python AST、TOML、Android/XML和主Info.plist静态解析、`git diff --check`。工作流与原HEAD比较，除品牌映射外jobs/actions pins/permissions/工具链及参数结构一致。
- 范围复核：156个原路径文件修改、71个文件改名和5个新增文件；全部旧路径均有对应新文件，无未映射删除。58份变更Markdown的324个本地链接全部可解析。
- macOS模板统一真实bundle与配置目录；安装/更新脚本、挂载目录和app名同步。没有Apple正式编译或运行证据。
- 有意保留：已部署ZEGO的ashellSessionId请求键（仓内broker接受新旧键）；旧ASHELL_*构建机输入回退；旧域名分类；真实仓库/endpoint、第三方来源和冻结历史。不更改身份认证或设备UUID/硬件指纹算法。
- 基线既有缺口：`flutter/ios/exportOptions.plist`含非法XML引号，原HEAD同样存在；未夹带修复，Apple/Google注册及签名资料属于外部未验证。
- ID-03（V2构建）、ID-04（V3/V4运行）均NOT_RUN；本机缺正式Rust/Flutter/Java/CMake工具链。无Git索引/分支/提交/推送、workflow dispatch、签名上传或生产操作，旧run不作本轮证据。

后续授权与执行：用户指定使用现有test分支，已核对origin/main=7361d39、origin/test=48226fb；两分支产品树一致，差异仅17份文档。使用保留工作区的switch迁移到test，文档冲突按切换前快照恢复，保留main已有Baseline23及任务回执。Git自动保留的恢复stash未删除。提交/push和Build-Clients-BT / Both / 常规编译结果将在本节追加，不包含main合并、正式发布、凭据修改或设备操作。Event CE-20261107-T001-02。
