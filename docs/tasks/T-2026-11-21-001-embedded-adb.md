# T-2026-11-21-001：内置本应用专用 Shell 权限服务

## 2026-11-21 交付续接授权 / T7

用户本轮明确要求先提交推送、执行双编译，再说明改动和使用。当前test/956ecf4，工作区为上一阶段完整ADB改造及文档；远端test/main均956ecf4，diff检查通过，无他人新增工作。授权本任务stage/commit/push至test、既有Build-Clients-BT.yml / Platform=Both / BuildMode=常规编译及结果核对；沿用工作流临时测试签名和Actions测试产物，无main合并、生产签名、发布或额外改号。本节覆盖下方此前“未授权提交/构建”的历史边界。

先推送并核对精确源码SHA，再单次dispatch。真实设备ADB-P02—P12仍需安装后验收；CI成功也不能证明关闭无线调试/蜂窝/OEM存活。STATE/WORK-20261121-001-r3 / CE-20261121-T001-05。

## Metadata / T0

- Status：complete / T8（已授权源码与Git交付）；2026-11-21 Asia/Tokyo，正式构建/运行待证。
- Owner：Codex / openworld-master；Android 主责，Flutter/Network/Security/Release 静态自审；没有独立代理审查。
- 输入：test02/test `609aec91e3b56bc588d90592f36ffb82f4259386`，工作区 clean。引用 Baseline `AS-BL-2026-10-19-0dbe202-restored` 的 ADB 架构，并以当前源码为实现真相。
- State drift：初始 STATE/WORK 为 2026-11-15 的 test 快照；无活动任务。后按用户新要求对齐main，当前在test/956ecf4实施，继承主分支2.0.26版本及历史；当前基线为[Baseline29](../BASELINE/29_EMBEDDED_ADB_ALIGNED_BASELINE_2026-11-21.md)，同步前初稿基线冻结保留。
- C2：用户确认仅供开放世界、不依赖独立 Shizuku；随后明确要求“完成这个方案”。授权源码、依赖/清单/协议兼容和配套文档修改。
- C3：本轮明确授权删除test02、将test合main并推送、返回test；后明确要求先将main最新基线同步到test并继续开发，包含保存恢复当前改动及继承main的版本。仍未授权本次新ADB实现提交推送、构建/测试/codegen/设备操作/发布或额外改号。
- Git 回执：test02 无独有提交，远端不存在该分支；切到 test 后安全 `branch -d test02`。main 位于现有 clean worktree，`956ecf4` 已以 test/609aec9 为第二父提交；merge 返回 already up to date。origin/main 从 d75a950 快进到956ecf4，远端 SHA 已核实。当前 test/609aec9 clean 后开始本任务编辑。
- 分支追问时：`git rev-list --left-right --count test...main` 为`0 14`，test无main未包含的已提交代码。`git diff --quiet test main -- src libs/hbb_common/src libs/scrap/src flutter/lib flutter/android/app/src` exit0：本次改造前功能源码相同；差异为主分支版本、构建默认版本、校验脚本和文档，并不是少14项功能。
- 最终对齐：用户随后明确要求先同步再test开发；保存58份文件到stash `3bd0f1e6f63a2d89c5f4b26aa7c8cf1da6f10aca`，test快进并推送到`956ecf482ac173af710aa7b6ea0b9de2bc3b4d60`。远端main/test同SHA、ahead/behind0/0，当前test。恢复29份源码/清单/许可文本的Git内容完全一致，16份文档顶部冲突保留双方历史；无源码冲突、无未合并/暂存项，stash保留恢复点。
- 长期用户流程：以后每次test合main后、切回test开发前先对齐最新main并保护未完成改动；已写入AGENTS/DEVELOPMENT_WORKFLOW/PROJECT_MEMORY。本次对齐推送只移动基线，不包含新ADB改造。

## T1：需求与验收

无线调试用于用户显式激活独立 Shell 权限进程。服务存活时，PC 通过既有已认证 relay 会话调用，命令/投屏/截图/节点/黑屏/触摸/键盘/剪贴板/本应用权限操作均不得重新依赖 adbd 端口。PC 断开不撤销设备能力；App 重建可绑定仍存活服务；设备重启或系统杀掉服务必须如实显示需重新激活。

不承诺非 root 全 ROM 永不失效；不替用户关闭系统保护、不自动打开调试/屏幕共享、不对第三方 App 提供 Shizuku 授权管理。不宣称 App UID 自身变成 shell/root。

## T2：Impact Map

- Android：localadb 内新增 app_process/Binder 服务、身份核对、进程重绑定、每通道 FD 与取消；LocalAdbClient 分离 bootstrap 和执行，现有独立执行通道切换到 IPC。
- Flutter：本机/PC 配对、激活、停用、真实状态文案及兼容读取。
- Network：字段40/41、FRB/JNI ABI保持；状态 JSON 追加能力/生命周期字段，既有认证/只读/开发者优先不变。
- Security：远端会话→本应用→专用 Binder→shell。端点限制调用 UID/包/安装路径，禁止普通第三方应用访问；服务死亡/撤销失效关闭；不监听公网，不记录命令/配对码。
- Build：新增源码与 R8/Manifest 注册；第三方机制来源固定记录，不自动下载二进制或升级工具链。正式编译未授权。
- 原MediaProjection/无障碍运行时/ZEGO/账号/三套服务器配置不在业务修改范围；版本仅随用户要求继承main2.0.26，无额外改号。

## T3/T4：设计与确认

采用 Shizuku 的独立 app_process + Binder 传递机制，收紧为本应用专属执行后端，保留上游来源和许可；不集成第三方 App 管理 UI。配对库仅用于 bootstrap；执行接口适配现有 shell-v2、scrcpy socket 和媒体资源通道。Binder 传递描述符，视频数据不放入大 Binder Parcel。

显式激活建立新 generation；握手核实服务UID/协议/当前安装身份并执行真实 UID probe 后才ready。持久化配对与授权意图不等于服务存活。撤销/停用使 generation 失效，迟到启动不可恢复权限；owner取消仅关闭本次通道。服务死亡不自动配对/重放命令。

长期决定为[ADR-0030](../ADR/0030-app-private-privileged-service.md) / D-038；补充0018/0025，替代常态依赖TLS连接和有限adbd恢复部分。回退保留身份文件，撤回本任务新增源和调用改动；显式停用服务后安装旧版，不回滚用户已执行的系统命令。

## T5/T6：实施与验证

- 已实现6个Java组件、21个Java文件范围（含既有通道适配）、Manifest/R8及4份Dart状态交互。所有正常执行入口去除无线端口，原配对库只用于显式激活；apiVersion5追加真实激活状态，UI明确设备停用与普通会话断开差别。
- V0：审查身份/代次/取消/迟到OPEN、死亡与App重建、通道24上限、FD/子进程清理、两种scrcpy固定资产路径、协议帧与全部调用点；修正Shell参数局部变量重名、旧服务退出竞态、取消激活落盘竞态、Provider释放及death recipient清理。21份Java括号结构检查无异常，Manifest XML解析和provider属性核对完成；这不是Java/Dart编译或项目测试。
- scrcpy资产SHA256仍为`764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536`；Shizuku LICENSE规范LF内容SHA256为`cfc7749b96f63bd31c3c42b5c471bf756814053e847c10f3eb003417bc523d30`，Git恢复后的Windows CRLF文件SHA256为`3ddf9be5c28fe27dad143a5dc76eea25222ad1dd68934a047064e56ed2fa40c5`。许可文字完全一致，仅换行差异；无binary更新，来源见NOTICE/EXT-REF-SHIZUKU-001。
- Rust/protobuf/FRB/JNI/Kotlin原运行时及依赖无工作区改动，主分支版本文件/lock/build默认版本/校验脚本逐文件保持；字段40/41与server已认证/只读/developer-priority入口核对，JNI active/compatibility源码均未改变。25份Java/Dart括号结构、同步前36个新增文档本地链接及`git diff --check`通过；这些V0检查不等于编译。
- 需正式覆盖 ADB-P01—P12、ADB-R20—R25、ADB-M21—M32、ADB-A01—A06、NET-04/DSP-09；包括激活/关闭无线调试/蜂窝重连/App重建/服务死亡/重启/第三方UID拒绝/多PC撤销负例，全部NOT_RUN。没有独立安全代理或产物审查，不能称为release-ready。
- 《编译验证需求》：正式Linux仓库根 `./build.sh 1` / `./build.sh 2`，正式Windows根 `build.cmd`；目标为Release/R8/ABI、双端状态兼容及上述设备矩阵。NOT_RUN。

## T7/T8：同步与交付

- 已同步：00/01/02架构导航、12/13/14_ADB与新16_EMBEDDED_ADB、10_SECURITY_MODEL、08/09构建验证、11_ROADMAP、ADR0030/0018/0025/索引、Baseline初稿与29、External Registry、TEST_MATRIX、D-038、开发分支流程及全局记忆。STATE/WORK-20261121-001-r2。
- Change Event：CE-20261121-T001-01（Git操作回执与任务激活）、02（源码/来源与静态复核）、03（文档初稿）、04（对齐main/test、恢复与最终记录）。
- 本轮新源码保持test工作区；不自动提交、推送、编排或监控。
