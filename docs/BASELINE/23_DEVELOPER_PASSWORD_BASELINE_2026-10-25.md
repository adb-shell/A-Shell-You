# Baseline23：PC开发者双密码与当前编排源码快照

- Baseline ID：`AS-BL-2026-10-25-48226fb-developer-password`。
- Source HEAD：`48226fbcdc08719b6e2a5c2e245dc9c10cc77ae9`（test，包含功能提交356f53b与交付文档）；观察日期2026-10-25（Asia/Tokyo），本地/远端test一致，初始clean。
- 合并前main：`fd233fa5178fd47dec5965b6c59d053d7e2db0fb`，为test祖先；四项差异提交均为开发者密码入口及工程记录。
- 本次 [T-2026-10-25-002](../tasks/T-2026-10-25-002-developer-password-main.md) 将既有test实现及同步文档合并main，不新增业务实现；准确main SHA与双端run见任务交付记录。
- 证据级别：V0源码/Git/workflow；test run40203581056在本任务起点仍in_progress，双密码交互与main双端结果未获PASS。Baseline01—22保持历史快照，不改写。

## PC开发者入口

`ConnectionPage`在desktop main收到Ctrl+Shift+H的KeyDownEvent时显示隐藏密码框；确认、Enter/小键盘Enter调用同一提交函数。`developer_login_bypass_model.dart::_developerLoginPasswordHashes`保存两个SHA-256摘要，UTF-8输入摘要匹配任一个才开启进程内免登录。大小写/标点/空格精确匹配；错误可继续输入，取消/Esc关闭后可重开；重复快捷键不叠窗，关闭丢弃输入，重启复位。

不改产品API/endpoint认证、Android/native、协议/生成桥或远控工具栏独立开发者密码。固定客户端摘要校验仅为UI入口，SEC-016保持开放。详细行为与维护见 [03_MODULE_DESIGN](../AI_ENGINEERING/03_MODULE_DESIGN.md)，验收见FLT-08。

## 身份、依赖与构建

Rust 5.2.1 / Flutter 5.2.1+59，AShell/ashell、me.ashell、flutter_hbb、libashell.so/ashell.dll保持。密码实现复用已声明crypto依赖，无锁文件、工具链、第三方资产或签名来源新增。

当前唯一CI文件 `.github/workflows/Build-Clients-BT.yml`；Platform=Both/Android/Windows，BuildMode=常规编译/原始编译。Android固定aarch64/arm64-v8a，Windows x64；GitHub-hosted Ubuntu22.04/Windows2022、临时测试签名、Actions产物。没有push/PR自动触发、仓库写入或GitHub Release；完整合同见 [Actions说明](../BUILD_CLIENTS_ACTIONS.md)。

当前源码在Baseline22之后已有配置/工作流等后继提交；本快照不把整个业务树描述为仍等于0dbe202。ADB具体合同继续由12/13/14领域文档与源码解释，T001/T002不修改它们的实现。
