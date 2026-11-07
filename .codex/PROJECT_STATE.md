# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261107-002`
Last Updated / Observed At：2026-11-07（Asia/Tokyo）
Evidence Scope：`repository-observed / V0`

## 当前源码基线

- T002当前交付：按最新用户指令保持5.2.1 / 5.2.1+59，将test/d9f1119合并main/7361d39后推送，再触发Both/常规编译，不监控。原2.0.26/2026修改已撤销，不能恢复；合并、push和dispatch准确回执见[当前任务](../docs/tasks/T-2026-11-07-002-openworld-main.md)。下列T001记录为来源沿革。

- 本次起点 main / `7361d39d34ad0bb147827e30fa61419a00b84038`，初始clean；用户指定test交付后，身份迁移源码已提交推送`ad88665e74ef48e251a1faefda6461e3c0e8112b`，说明“更新开放世界产品信息及指纹”；main保持。
- 当前工作区快照：[Baseline24](../docs/BASELINE/24_OPENWORLD_IDENTITY_BASELINE_2026-11-07.md)，`OW-BL-2026-11-07-7361d39-identity-worktree`；实现与验证范围见[身份迁移](../docs/AI_ENGINEERING/OPENWORLD_IDENTITY_MIGRATION_2026-11-07.md)。Baseline23与历史Git/编排回执只作追溯。
- 用户续接确认改用test提交推送及Both/常规编译；起点test/48226fb与main产品树一致，17份文档恢复切换前快照。dispatch HTTP204，run42350649478/head ad88665；用户随后要求“不监控”，已停止本地轮询，未取消云编译；真机及生产未操作。
- 回退后撤回的实现、实验方案和诊断结论不属于当前实现。后续会话先核对 HEAD/status，再由代码重新建立证据；不能沿用历史聊天中的“已修改”判断。

## 当前能力摘要

| 领域 | 当前实现与限制 |
|---|---|
| 产品身份 | 显示“开放世界”；内部 OpenWorld/openworld、com.openworld.me；flutter_hbb保持；Rust 5.2.1 / Flutter 5.2.1+59 |
| Android 原链 | core连接服务、MediaProjection、无障碍输入/无视/穿透/黑屏保持各自原入口；服务运行不等于屏幕共享或首帧 |
| ADB身份 | Android进程持有配对身份；双NSD发现，pairConnect后以真实 id -u 验证；配对记录不代表Shell已可用 |
| ADB执行 | 2路独立命令、设备共享状态变化推送、有限意外断链恢复；PC无三秒远程status |
| ADB画面 | scrcpy ScreenCapture + 独立GPU/H.264；常驻截图JPEG及raw/PNG兼容回退；Shell取节点、App绘穿透 |
| ADB参数 | width/350整数缩放；4096边长/4Mi像素界限；只读普通VideoQoS，运行时更新；无视至少300ms，节点至少80ms |
| ADB输入 | Shell可用时全局优先触摸，独立control-only连接；普通键盘、远程输入、Ctrl+V及PC→Android纯文本剪贴板同步 |
| 黑屏 | 独立黑色buffer层及App预渲染白字PNG，排除自身采集；OEM表现待真机证据 |
| PC界面 | 普通左/ADB右、单源居中；Shell栏无悬浮提示；状态分组；无障碍栏无视频/特殊/防触状态文字 |
| PC开发者入口 | Ctrl+Shift+H先弹密码框；两个可接受密码任一精确匹配后确认/回车启用内存免登录，取消/错误不启用，重启复位；V0/FLT-08待运行 |
| 常亮与保活 | 默认连接服务运行期间静默常亮，无新增设置入口；ADB连接验证后尝试本应用电池优化豁免，不保证进程免杀 |

详见 [12本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)、[Android原链](../docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md)。

## 本轮工作与验证

- 当前Task T-2026-11-07-002 / STATE、WORK-20261107-002 / CE-20261107-T002-01/02；main no-ff merge test已准备，15处冲突仅文档且按test内容解决；补交付文档前合并树与test完全一致。当前授权main提交推送、双端常规编译只触发；版本、产品及工作流保持test，不监控，不代表正式验证通过。

以下为T001静态验证与旧test触发回执，不能替代本次main交付结果：

- 当前交付：[T-2026-11-07-001](../docs/tasks/T-2026-11-07-001-openworld-identity.md)，T8源码/文档/Git/触发交付完成；按用户要求不监控，正式编译与运行验收仍待证。
- Registry：WORK-20261107-001；Events：CE-20261107-T001-01/02/03；Decision：D-034 / ADR-0027。
- 当前交付授权覆盖产品身份与文档、test切换/stage/commit/push origin/test及Build-Clients-BT / Both / 常规编译，沿用工作流临时测试签名和Actions产物；用户最新要求停止监控，无main写入/部署/凭据轮换。
- V0：135项静态身份合同通过；四份Shell、工作流14段Bash/14段PowerShell语法、嵌入Python AST、TOML/XML/plist及diff检查。已有iOS exportOptions.plist非法引号单列，未混入本次修复。
- 变更范围：156个原路径文件修改、71个必要文件改名及5个新文件；旧路径均有新文件对应，58份Markdown/324个本地链接通过。
- 新包是独立安装身份，旧数据/系统权限/ADB配对不自动迁移；设备UUID/硬件指纹算法、凭据和版本保持。外部ZEGO请求键、签名环境变量和旧域名分类保留显式兼容。
- 唯一workflow为Build-Clients-BT.yml / Build OpenWorld Clients-BT / Both / 常规编译；run42350649478最后观察为Windows EXE与Android Native均在原生依赖构建，停止监控后不再查询，最终结果未知。ID-03未取得PASS，ID-04真机仍NOT_RUN。SEC-016与既有外部资产缺口保持。
