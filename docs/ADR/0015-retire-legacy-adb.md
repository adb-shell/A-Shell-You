# ADR-0015: Retire Legacy Local ADB

- Status：accepted
- Record Type：contemporaneous
- Decision Date：2026-10-12
- Approver：当前用户明确要求移除旧 ADB，后续另行对接新组件
- Related：D-019 / T-2026-10-12-009；supersedes ADR-0007 的现役实现决定
- Implementation State：source edits / V0；V2/V3 NOT_RUN

## Context and Decision

移除 Android 主页右侧 AdbPage、Dart wrapper/constants、11 个 native MethodChannel handler、四个 Kotlin ADB 模块以及 Accessibility 中的无线调试自动操作。删除 WRITE_SECURE_SETTINGS、CHANGE_WIFI_MULTICAST_STATE 和旧 ADB ProGuard keep；build.sh 不再要求从未调用的 host adb；Gradle 排除 **/libadb.so，防止旧构建目录残留进入 APK。

不添加新组件或占位功能。其余 native packaging 模式、Wi-Fi 保活、MediaProjection、SKL/ignore/input、DevAutoSelectorController、terminal、ZEGO 和 Windows 实现保持。Rust protocol 只修正陈旧注释，字段未改变。

## Alternatives and Consequences

仅隐藏页签仍会保留旧执行/权限/打包链，不符合完整剔除要求。当前不再提供原本的本地 ADB 配对、mDNS、shell 和自动无线调试操作。新 ADB 未来另行设计，不能直接恢复旧通道，也不能混入远程 terminal 或 side-button 权限模型。

旧 app prefs、私钥、配对/系统无线调试状态不自动清理；源码退役不等于设备撤权。历史来源与许可记录保留，EXT-BIN-ADB-001/EXT-REF-ADB-002/003 不再是当前构建必需资产。未检查外部构建机或设备。

## Subsequent Implementation Amendment — 2026-10-13

[ADR-0017](0017-isolated-local-adb.md) 补记 8662f23 的独立 localadb 新实现，推进本记录所说“未来另接新组件”的阶段。上文描述保留 T009 当时状态；旧 runner/bridge/权限/自动化/libadb.so 的退役决定不变，不恢复 ADR-0007 的旧实现。当前本机 ADB 能力见 [12_LOCAL_ADB.md](../AI_ENGINEERING/12_LOCAL_ADB.md)。

## Verification and Reversal

V0：旧符号/引用检索、删除边界与保留文件对比、Manifest 解析、Bash 仅语法解析、协议语义及范围检查。正式 APK 排除效果、升级、采集/输入/重连/ZEGO/Windows 交互为 NOT_RUN，见 TEST_MATRIX AND-01/02/04/05/06/07/08、FLT-06、WIN-02/08。

回滚须未来明确授权，从 13f792d552db49fd1b702ff24cb2b3d0c5030c38 按任务清单恢复本次文件/删除段，不覆盖其他改动或运行整仓 reset。旧协议安全边界不因退役或回滚而自动放宽。
