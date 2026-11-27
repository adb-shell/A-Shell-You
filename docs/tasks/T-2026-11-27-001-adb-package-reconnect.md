# T-2026-11-27-001 — 改包兼容与内置 ADB 切网恢复

2026-11-27交付回执：源码已作为test/e1526fa2a6fa2b688d965bb56ba6b422f5ac9a07提交推送；23:21:49 Asia/Tokyo单次Both/常规编译HTTP204受理，按要求不监控，最终结果unknown。详见[T002交付任务](T-2026-11-27-002-adb-package-build.md)。以下未提交/未触发描述为原源码阶段；设备验收仍NOT_RUN。

## 0. Task Metadata

按 `TASK_TEMPLATE.md` 建立；2026-11-27 Asia/Tokyo。Requester / Product Owner：用户；Principal：Codex / openworld-master；Android、Flutter、Network 与 Security 按对应 skill 自审，无委派。Release Owner：用户（本任务无发布）。Status：complete / T8（仅源码与V0交付）。

- Source：test/e3fd784cdc8f761107bcb00094a23a9361f6f4e7，初始 clean；main 与 test 已对齐，无并行活动任务。
- Baseline：OW-BL-2026-11-21-956ecf4-embedded-adb-worktree；[Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)，历史快照不改写。
- Risk：high（本机 shell capability）；ADR-0030 / D-038；TEST_MATRIX：ADB-P01—P18 与本批新增改包/取消竞态用例。
- Highest verification required：V3 真机；当前仅授权 V0。External Assets：现有 Shizuku 参考与正式构建环境，无新增依赖。
- Memory：STATE / WORK-20261127-001-r2；CE-20261127-T001-01/02。

## T0. Authority and Baseline

恢复入口、规则、全局状态、领域文档与 skill；Git/status 验证初始干净，保留 test。用户明确要求“在test上修复…以当前App实际包名和组件为准，兼容成品APK改包名…完善配对、激活和服务停止的错误提示…修复切换移动数据后ADB无法使用”。本轮 C0 observe、C1 docs、C2 source 已授权；C3 Git 写入、构建/测试/analyze/codegen、设备/CI、版本/签名/发布/生产、删除迁移均未授权。遇已有变更冲突或需扩大权限再停，不为当前编辑重复索要确认。

## T1. Requirements

- 成品 APK 改包后，Provider authority 与安装包名可能不同；现状按 package + suffix 猜测回传地址，启动入口是固定类名字符串。
- 用户只有他人“Wi-Fi 配对后切蜂窝失效”的反馈，不确定切网前 Shell 是否实际可用；无 APK/设备/错误现场。不能将源码缺陷等同于已复现现场根因。
- 验收：按安装 manifest 的实际组件/authority 启动和验证，保持完整 UID/APK/generation 校验；已验证的服务不因请求取消或 PC 断线误停；区分配对、引导连接、Binder 激活、恢复与服务停止错误。
- 支持边界：安装 package/组件/authority 改写；任意 DEX/JNI 符号重定位、删组件/破坏 ABI 不承诺自动修复。系统杀 shell、重启仍可能需重新激活。

## T2. Impact Map / Compatibility

- Android：PrivilegedBridge → installed Provider → PrivilegedSystem / OpenWorldPrivilegedService；LocalAdbManager cancellation ownership；LocalAdbClient 仍只在显式激活连接 adbd。
- Flutter：手机/PC ADB 状态与错误展示，检查会话重连与状态重放；不新增窗口或常驻自动激活。
- Rust / Network：检查 fields 40/41、disconnectRemote、watch cached-state replay；不改 protobuf/JNI/FRB ABI、relay-only 和远端权限。
- Security：Provider 限 shell/root，owner 为当前 full UID，校验实际安装、组件与 APK；不输出 generation、命令或敏感日志；不放宽第三方授权。
- Windows / API：N/A — 无平台或 HTTP 合同变更。Build/version：保持 2.0.26 / 2.0.26+59，不改依赖/生成物/CI。
- Assets：上述源码 active/tracked；正式设备/APK external/missing；历史 Baseline frozen。

## T3. Design / Rollback

读取 PackageManager 中 PrivilegedProvider.class 对应的真实 ProviderInfo，并核对 authority 的实际解析归属；将组件和 authority 传入 shell 进程并在两端复核。入口用实际 class reference，不混淆 Java namespace 与 applicationId。取消按本次 activation generation 且在 ready 之前撤销，显式停用/撤销继续无条件生效。状态不得伪造 ready；无自动重放命令、配对码或自动无线调试激活。

不采用批量替换包名、不将命令开放到 TCP；沿用 ADR-0030 / D-038，仅补实现约束。Rollback：未提交工作区精确逆向本任务补丁；不运行 destructive reset。private Binder 若扩展会同步双方，旧服务在安装替换时需重新激活；peer fields 保持兼容。无不可逆动作。

## T4. Implementation Confirmation

用户当前明确修复指令满足 C2；2026-11-27，工作区 test，无重确认门。本任务交付源码/V0，不以历史构建证明新代码。

## T5. Change Record

- 7份Java：新增PrivilegedTarget；PrivilegedSystem按实际authority解析归属；OpenWorldPrivilegedService接收并回传组件身份；PrivilegedProtocol private SERVICE/INFO v2；PrivilegedBridge实际类入口、错误码及generation/ready原子取消；LocalAdbManager取消和真实结果处理；LocalAdbClient区分引导握手/UID验证错误。
- local-adb.pro：保留入口/Provider及两处运行时类查询方法，避免R8将类引用折叠为固定字符串；产物需V2复核。
- 4份Dart：local_adb_bridge/page、remote_adb_controller/menu消费可选原因码并提示等待应用内已激活；手机停用按钮仍不恢复。状态apiVersion7，其余远端协议/ABI不变。
- 根因证据：authority拼接与固定入口、ready后请求取消仍全局block/disable均为verified源码缺陷；是否解释他人蜂窝失效仍inferred/verification-required，不能将“配对成功”当作已完成激活。

## T6. Verification Record

已执行V0：7份Java词法/括号检查通过，git diff --check通过；逐处复核新helper、双方INFO字段顺序、六参数启动、Android30/31 Provider调用与33 flags宽度，4份Dart差异人工检查。最终记忆回填后18处新/改本地Markdown链接检查通过；共27文件（12产品、15文档），HEAD仍e3fd784，main/test已提交历史0/0，改动均保留test工作区。未运行产品测试、analyze、codegen或编译。

安全/生命周期静态推演：

| 场景 | 当前源码结果 | 证据等级 |
|---|---|---|
| 安装package改变而authority保留，或一起改authority | 从真实ProviderInfo取值并解析回同package/full UID/APK/component | verified/source |
| 组件缺失/禁用/归属或进程不符 | 明确错误，拒绝发起新服务，不猜测其他组件 | verified/source |
| 系统查询抛暂时异常 | 既有daemon保留，INFO/OPEN拒绝；App验证重试，不当作授权 | verified/source |
| ready提交前取消 | 同一锁下block未完成代际，迟到验证无效 | verified/source |
| ready提交后取消/超时/owner断开 | 保留已验证服务，结果含真实状态；不调用全局disable | verified/source |
| 旧请求清理与新generation/显式停用 | 代际不符清理无效；显式disconnect/revoke仍立即block并持久化 | verified/source |
| PC切网重连 | Rust watch重发缓存状态，PC 15s重订阅/epoch隔离，正常执行Binder/FD；无新增自动激活 | verified/source |

正式ADB-P01—P22、设备切网/改包/退出恢复均NOT_RUN；V0不是执行成功证明。未查询旧CI，b12cbdb最终结果仍unknown。

核对平台资料：[AOSP Android13 IPackageManager](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/android13-release/core/java/android/content/pm/IPackageManager.aidl)的resolveContentProvider flags为long；旧API保持int。复核既有固定[Shizuku starter](https://github.com/RikkaApps/Shizuku/blob/b844bc491f1790c72328e1a8e5b2349f8978f0ea/manager/src/main/jni/starter.cpp)：上游cgroup切换仅root路径，不能据此将本次shell切网问题归咎缺少cgroup调整；未新增此类修改。

## T7. Documentation / Memory

同步12/13/16领域文档、TEST_MATRIX新增ADB-P19—P22、ADR0030/D-038实现约束及Task History、AI Changelog、State、Work、Change Event和入口摘要。Baseline冻结，External Registry/Build/Debug reviewed-no-change，无新增依赖、外部资产或构建事实。

## T8. Handoff

交付范围是test工作区源码修复与V0。未提交/推送，未改main/版本，未编译/设备/CI监控；现场反馈的最终根因及OEM存活仍待正式验证。不承诺任意APK编辑器损坏DEX/JNI后仍可运行。

《编译验证需求》
- 命令：正式Linux构建机根目录 `./build.sh 1`、`./build.sh 2`；正式Windows构建机根目录 `build.cmd`。
- 环境：沿用08_BUILD_SYSTEM正式Android/Flutter/Rust及Windows工具链、既有测试签名流程；本机不自动执行。
- 执行目录：两台正式构建机对应仓库根目录；源码必须包含本任务补丁并记录准确SHA/dirty state。
- 目标：双端编译、Release/R8保留运行时类查询，原包与仅改package/authority成品APK实际激活；ADB-P19—P22及旧矩阵回归。PC收到ready后执行id -u，再关闭无线调试/Wi-Fi、切蜂窝重连并重开App验证Shell/媒体/输入。服务死亡需记录阶段错误与脱敏系统证据，不能伪造已验证结论。
