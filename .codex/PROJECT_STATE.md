# OpenWorld Project State

Schema Version：`1.0`
State Revision：`STATE-20261112-002-r2`
Last Updated / Observed At：2026-11-12（Asia/Tokyo）
Evidence Scope：`repository-observed / merge V0；历史CI结果单独记录`

## 当前源码基线

- 合并提交`c30efd61449a0e02b5b0546a25ddf948c5ad5d4e`已推送origin/main，并核对远端SHA一致；两个parents分别为main/5d2d2e9、test/a8abdd4。test仍为a8abdd4，推送后工作区clean。提交说明“统一配置文件(大改造节点一)”。本次只补交付回执，不改变源码、不触发或监控编译。

- 本轮任务：[T-2026-11-12-002](../docs/tasks/T-2026-11-12-002-unified-config-main-merge.md)，complete/T8（合并交付）；用户授权合并推送main，提交说明“统一配置文件(大改造节点一)”。远端无mian，按实际main执行。
- 输入main/5d2d2e932cfb390a7b3d0278bea6a7e080746c95、test/a8abdd4c04951f930ea240c9951378ee3b8c165f；起始test工作区clean，两个远端分支与本地一致。
- [Baseline28](../docs/BASELINE/28_UNIFIED_CONFIG_MAIN_BASELINE_2026-11-12.md)继承Baseline27的2.0.26 / 2.0.26+59和开发者画面优先，合入test服务器配置、Windows编译及Android CI修复；三套四项地址完整保留a8abdd4，Android与PC1当前不同。
- 本轮不触发或监控构建。c788e8d的Both/常规编译仅有HTTP204回执，最终结果未知；此前失败和13项离线检查见[T001](../docs/tasks/T-2026-11-12-001-build-fixes.md)。
- 历史任务/版本/编译回执只供追溯；后续会话先核对HEAD/status，不继承旧授权或运行结论。

## 当前能力摘要

| 领域 | 当前实现与限制 |
|---|---|
| 产品身份 | 显示“开放世界”；内部 OpenWorld/openworld、com.openworld.me；flutter_hbb保持；Rust 2.0.26 / Flutter 2.0.26+59 |
| Android 原链 | core连接服务、MediaProjection、无障碍输入/无视/穿透/黑屏保持各自原入口；服务运行不等于屏幕共享或首帧 |
| ADB身份 | Android进程持有配对身份；双NSD发现，pairConnect后以真实 id -u 验证；配对记录不代表Shell已可用 |
| ADB执行 | 2路独立命令、设备共享状态变化推送、有限意外断链恢复；PC无三秒远程status |
| ADB画面 | scrcpy ScreenCapture + 独立GPU/H.264；常驻截图JPEG及raw/PNG兼容回退；Shell取节点、App绘穿透 |
| ADB参数 | width/350整数缩放；4096边长/4Mi像素界限；只读普通VideoQoS，运行时更新；无视至少300ms，节点至少80ms |
| ADB输入 | Shell可用时全局优先触摸，独立control-only连接；普通键盘、远程输入、Ctrl+V及PC→Android纯文本剪贴板同步 |
| 黑屏 | 独立黑色buffer层及App预渲染白字PNG，排除自身采集；OEM表现待真机证据 |
| PC界面 | 普通左/ADB右、单源居中；Shell栏无悬浮提示；状态分组；无障碍栏无视频/特殊/防触状态文字 |
| PC开发者入口 | Ctrl+Shift+H先弹密码框；两个可接受密码任一精确匹配后确认/回车启用内存免登录，取消/错误不启用，重启复位；V0/FLT-08待运行 |
| 开发者画面优先 | 进程模式供新建/重连使用；Android认证后暂停普通画面/盲输入/采集控制，保留会话与ZEGO；多开发者可接收，最后退出恢复；协作角色可由修改版伪造，正式验证待证 |
| 常亮与保活 | 默认连接服务运行期间静默常亮，无新增设置入口；ADB连接验证后尝试本应用电池优化豁免，不保证进程免杀 |

详见 [12本机ADB](../docs/AI_ENGINEERING/12_LOCAL_ADB.md)、[13远程ADB](../docs/AI_ENGINEERING/13_REMOTE_ADB.md)、[14媒体与输入](../docs/AI_ENGINEERING/14_ADB_MIRROR.md)、[Android原链](../docs/AI_ENGINEERING/04_ANDROID_PIPELINE.md)。

## 本轮工作与验证

- Registry：WORK-20261112-002-r2；Events：CE-20261112-T002-01/02；沿用D-034/035/036、ADR-0027/0028/0029，无新增架构决定或外部部署。
- 源码自动合并、15份Markdown冲突已解决；41个非文档差异文件与预期来源哈希一致，32份Markdown的375个本地链接有效，冲突标记为零、历史引用保留、diff检查通过。版本来自main、产品改动与用户地址来自test；Git回执见当前任务。
- 配置唯一维护处为`libs/hbb_common/src/server_profiles.rs`；Android固定、PC空闲切换、账号/peer隔离、后台确认和旧响应过滤保持。见[15_SERVER_PROFILES](../docs/AI_ENGINEERING/15_SERVER_PROFILES.md)。
- SCP-02/04—09、DSP-02/04—10等单测/真机/服务联调仍待证；本轮不执行编译、codegen、设备或生产操作。开发者角色仍为协作声明，不是可信凭证；SEC-016等外部缺口保持。
