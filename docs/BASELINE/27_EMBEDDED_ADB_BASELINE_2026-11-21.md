# Baseline27：内置 ADB 权限服务工作区

- Baseline ID：`OW-BL-2026-11-21-609aec9-embedded-adb-worktree`
- Observed：2026-11-21 / T-2026-11-21-001；V0源码范围，无构建产物
- Base：test / `609aec91e3b56bc588d90592f36ffb82f4259386`；变更未提交，不能用此SHA单独复现新实现，须保留任务patch/待后续提交固化
- Git receipt：test02本地删除（无独有提交、远端无此分支）；已有main merge `956ecf482ac173af710aa7b6ea0b9de2bc3b4d60` 已推送；当前test，main独有改动不反合入
- Version：Rust5.2.1、Flutter5.2.1+59，未修改
- Source scope：localadb新增6个Java执行/协议/Provider组件，既有独立通道统一AdbChannel；Manifest/R8、4份Dart状态交互；未改Rust/protobuf/FRB/Kotlin原运行时/服务器配置
- Protocol：字段40/41及JNI/FRB不变；status apiVersion5，connected=已验证Binder存活；activation字段追加
- Dependencies：既有libadb等版本不变；Shizuku固定revision `b844bc491f1790c72328e1a8e5b2349f8978f0ea` 机制改写/Apache-2.0；无新Maven/native binary
- Asset：原scrcpy v5.0.1 / 733930 bytes / SHA256 `764eb6f79811d5211fe9df341120882ba9994c7a61b897d7bf3fb662e53bc536` 保持；新增Shizuku LICENSE/NOTICE文本
- Build environment：本轮未使用正式构建机、不解析依赖、不运行产品测试或设备命令
- Verification：调用/权限/状态/FD清理、Manifest与diff静态核对；V1—V4、Release/R8、ABI、Android11—16/OEM/蜂窝/双PC全部NOT_RUN
- Contracts：[16_EMBEDDED_ADB](../AI_ENGINEERING/16_EMBEDDED_ADB.md)、[ADR-0030](../ADR/0030-app-private-privileged-service.md)、[任务](../tasks/T-2026-11-21-001-embedded-adb.md)
- Rollback：先显式停用服务，再恢复旧源码/APK；身份密钥格式不变，已执行系统修改不自动还原
