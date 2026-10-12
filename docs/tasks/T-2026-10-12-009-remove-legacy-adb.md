# T-2026-10-12-009 — 移除旧本地 ADB 组件

## 0. Task Metadata

| Field | Value |
|---|---|
| Status / T-State | complete / T8（源码交付，运行验收 pending） |
| Requester | 当前用户 / Product Owner |
| Principal / Domain / Review | Codex / ashell-master；Flutter、Android；Security、Release 静态复核 |
| Created / Updated | 2026-10-12 |
| Source Branch / HEAD | main / 13f792d552db49fd1b702ff24cb2b3d0c5030c38 |
| Initial Dirty State | clean；前轮 T008 已包含在 13f792d，旧记忆的未提交状态过期 |
| Baseline ID | AS-BL-2026-10-12-13f792d-adb-retired-worktree；旧 PC 基线保持，source delta 对比 13f792d |
| Risk / Highest required verification | medium / V3；本轮仅允许 V0，V2/V3 未执行 |
| Related ADR / D-ID | ADR-0007 → ADR-0015 / D-019 |
| TEST_MATRIX | AND-01/02/04/05/06/07/08，FLT-06，WIN-02/08 |
| External assets | EXT-BIN-ADB-001、EXT-REF-ADB-002/003 退役 |
| State / Registry / Event | STATE-20261012-010 / WORK-20261012-017 → 018 / CE-20261012-T009-01 |

## T0. Authority and Baseline

- 当前明确请求：移除 App 主页右侧 ADB 页面、旧组件和 Windows/Android 残留，只剔除、不接入新组件、不影响其他功能。
- 授权：C0 只读；C1 同步当前事实/记忆；C2 修改相关代码/配置；C3 delete 仅限下列 5 个旧 ADB 源文件。当前请求已明确授权剔除，不重复索取删除批准。
- 禁止：build/test/analyze/codegen、安装依赖、设备操作、Git 写入/提交/推送、版本/签名/发布、服务访问、历史或用户数据删除。
- 没有并发活动任务；不恢复任何旧任务授权。无额外子代理。

## T1. Requirements

1. Android 主页保留原 ServerPage，移除右侧 AdbPage 和相关轮询/桥接。
2. 移除旧配对、mDNS、进程 shell、无线调试自动化和专用权限/混淆规则。
3. 避免旧构建机 jniLibs 中的 libadb.so 再打包；不改变其余 native library 打包行为。
4. Windows/远控终端/输入/采集/重连/ZEGO 等保持原实现。正式运行验证不得冒充完成。

## T2. Impact Map and Compatibility

| Domain | Evidence / Planned change |
|---|---|
| Flutter | home_page.dart → adb_page.dart → common.dart::AndroidAdbManager → consts.dart::AndroidChannel；删除此完整本地链 |
| Android | oFtTiPzsqzBHGigp.kt 的 11 个 ashell_adb_* 分支 → adb/ 四文件；nZW99cdXQ0COhB2o.kt 只移除旧无线调试自动化 |
| Windows / Rust | 未找到旧 ADB 运行模块；远程 terminal 和 DevAutoSelectorController 不是旧本地 ADB，保留；message.proto 仅修正陈旧注释，不改消息字段 |
| Build / permissions | build.sh 去掉未调用的 host adb 前置检查；env.sh 去掉独立 adb 的 apt 安装和强制检查；Gradle 排除 libadb.so，保留 useLegacyPackaging；移除旧 ADB ProGuard 规则、WRITE_SECURE_SETTINGS 和 CHANGE_WIFI_MULTICAST_STATE |
| State / data | 旧 SharedPreferences(ashell_adb)、配对私钥及设备系统设置不清理；本轮不执行已安装应用的数据迁移或撤权 |
| Assets | 5 个 tracked 源文件删除；当前 jniLibs、ADB-CODE、LADB 不存在；忽略规则保留，防止历史研究材料被误纳入；外部构建机未访问 |
| Documentation | 更新当前架构、权限、构建与 AI 入口；历史接管/集成文档保留且注明退役 |

## T3. Design and Rollback

- 删除功能链；不新增占位页、不接入新 ADB、不升级依赖、不改 JNI/网络协议。
- 仅移除无障碍服务中旧 ADB 自动化的独立状态、方法和事件钩子。已查其私有辅助方法无非 ADB 调用者；保留 SKL、无视、输入、黑屏、DevAutoSelectorController。
- 保留 ACCESS_WIFI_STATE、CHANGE_WIFI_STATE：MainService 的 Wi-Fi 保活仍使用 WifiLock；保留 WRITE_SETTINGS 等其他权限。
- ADR-0015 记录退役；ADR-0007 的现有实现声明被取代，新组件未来单独设计。
- 回滚：未来明确授权后从 13f792d 精确恢复本任务文件/删除段，不使用整仓 reset、不覆盖后续修改；无设备数据不可逆变更。
- 替代方案仅隐藏页签会留下执行/打包链，与用户完整剔除要求不符，未采用。

## T4. Implementation Confirmation

《操作确认》已由当前用户“移除…剔除旧的ADB组件和相关…确保只进行剔除”提供；仅限 T2 范围。非相关代码、构建执行和 Git 操作仍禁止。

## T5. Change Record

计划删除：

- flutter/lib/mobile/pages/adb_page.dart
- flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbManager.kt
- flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbRunner.kt
- flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbDnsDiscover.kt
- flutter/android/app/src/main/kotlin/me/ashell/adb/AShellAdbState.kt

其他实际文件清单在 CE-20261012-T009-01 和最终 diff；所有都是源文件/文档，无生成代码修改。

删除 4 个 Kotlin 文件后的空 adb/ 目录仍在本机。删除该空目录的命令被自动审批策略拦截（仅返回 blocked by policy），未绕过限制；只读复核目录内为 0 个文件，不参与构建/运行，也不会被 Git 跟踪。

全仓复查补充：env.sh 的两处独立 host adb 预装/强制检查已删除；保留 Android SDK 标准 platform-tools。res/msi/preprocess.py 的 adb-shell 是 GitHub 账号路径，不是组件调用/依赖，未改动。根 .gitignore 中 ADB-CODE/LADB 规则继续阻止历史研究资产误入版本库；不是产品残留。

## T6. Verification

- V0 PASS：5 个旧模块文件移除；运行树旧符号检索仅命中 Gradle libadb.so 排除项；Activity 仅减少 11 个 ashell_adb_* 分支，其他字符串分支集合不变。
- V0 PASS：7 个共享源码/Manifest/ProGuard 文件的 diff 全为删除；Accessibility 删除 962 行，其中 36 个自动化方法在剩余类中无悬空调用。保留 SKL/input/ignore/DevAutoSelectorController 逻辑，无新增行为。
- V0 PASS：XML 解析并按结构比对，仅移除两项权限；message.proto 去注释后与 HEAD 完全相同；Windows/Rust/JNI/MainService/DevAutoSelectorController/依赖锁均无 diff。
- V0 PASS：build.sh 通过 Git Bash --noprofile --norc -n，仅语法解析，未执行脚本；git diff --check 无异常。两种打包模式和 T008 失败传播未改变。
- V0：env.sh 同样仅做 Bash 语法解析；全仓非文档检索按含义复核，不删除 SDK 平台工具或仓库账号名。
- Local asset observation：ADB-CODE、LADB、jniLibs 当前不存在，未删除或接触外部资产。
- V1/V2/V3/V4/V5：NOT_RUN；遵守无构建/运行约束。

《编译验证需求》（仅记录，未执行）：

- Android 正式 Linux 构建机，在仓库根按既有模式运行 ./build.sh 1 和 ./build.sh 2；验证 APK 不含 libadb.so，所有 ABI 原生主库仍在，主页不可进入 ADB。
- Windows 正式构建机，在仓库根运行 build.cmd；验证启动和连接 Android 后画面、输入、文件/终端及 ZEGO 与原行为一致。
- 设备检查 AND-01/02/04/05/07/08；AND-06 改为退役检查：无 ADB 页签、配对/执行/自动启用无线调试入口。不得为验证重新授予旧 ADB 特权。

## Security / Privacy / Compliance Review

旧高权限执行路径移除，系统无线调试/历史配对数据不自动变更；不主张历史授权已经撤销。没有新增协议、网络端点、凭据或二进制。保留历史来源/许可记录，不能把缺失 libadb.so 变成已验证供应链。

## Build / Release Review

不再依赖旧 libadb.so；其排除规则依据 AGP 7.3 JniLibsPackagingOptions.excludes 官方 API。其余工具链、锁文件、native 提取方式、版本和 Windows 打包保持。G2/G3 未满足，没有正式产物。

## T7. Documentation Delta

已同步 docs/AI_ENGINEERING、ADR-0015/0007 索引、Baseline 新快照、Decision Log、Task History、External Asset Registry、Test Matrix、Project State、Current Work、ChangeLog/Event、入口和三个领域 Skills。历史记录保留；旧集成档案明确为 RETIRED。安全/构建/调试/路线文档已区分退役路径与剩余风险。

## T8. Handoff and Closure

- 源码剔除和 V0 交付完成；运行验收未完成，不宣称“已编译成功”或“所有功能无回归”。
- 16 个代码/配置文件变化（其中删除 5 个）；其他修改为事实同步文档，完整清单见 CE-20261012-T009-01。
- 总变更清单为 47 个文件：16 个代码/配置，31 个文档/Skills/记忆文件。空 adb/ 目录保留的工具限制见 T5。
- 未执行项目代码/构建/测试/analyze/codegen、Git 写入、依赖安装、设备/数据/缓存清理或外部服务操作。
- 新组件接入、正式构建与运行验证不在本轮执行范围；前述《编译验证需求》供正式环境后续使用。
- 最终源 HEAD 仍为 13f792d；本任务变更均未提交，CURRENT_WORK 无活动任务。
