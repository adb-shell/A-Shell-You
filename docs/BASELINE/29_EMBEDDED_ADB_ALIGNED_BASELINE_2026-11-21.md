# Baseline29：对齐 main 后的内置 ADB 工作区

- Baseline ID：`OW-BL-2026-11-21-956ecf4-embedded-adb-worktree`
- Observed：2026-11-21 / T-2026-11-21-001 / CE-20261121-T001-04
- Base：test、main、origin/test、origin/main均为 `956ecf482ac173af710aa7b6ea0b9de2bc3b4d60`；远端核对一致，ahead/behind为0/0
- Working tree：内置ADB改造在test上恢复，未提交；单凭基点SHA不能复现改造，需本任务patch或未来固化提交
- Version：沿用main的Rust/portable 2.0.26、Flutter2.0.26+59；版本/lock/build默认版本/校验脚本与main逐文件相同
- Preservation：临时stash `3bd0f1e6f63a2d89c5f4b26aa7c8cf1da6f10aca` 保留恢复点；29份Java/Dart/Manifest/R8/许可文本的Git规范化内容与保存版本全部一致。16份文档顶部冲突已合并，保留主分支历史；索引无未合并项，新代码未暂存
- Previous snapshot：[同步前实现初稿](27_EMBEDDED_ADB_BASELINE_2026-11-21.md)，原609aec9工作区记录冻结保留；此前功能源码与main相同，不代表缺少14项功能
- Contract / dependency / protocol：沿用[16_EMBEDDED_ADB](../AI_ENGINEERING/16_EMBEDDED_ADB.md)、ADR-0030；字段40/41和JNI/FRB不变，无依赖或binary升级
- Verification：V0源内容保全、冲突标记、版本保持、Manifest、资产、调用点与diff；正式构建/设备/ABI/R8/OEM/蜂窝/双PC NOT_RUN
- Git workflow：用户要求今后每次合test到main后，回test开发前先对齐最新main，保全未提交工作；不能以reset/force-push丢弃分支独有代码
- Handoff：[任务及完整回执](../tasks/T-2026-11-21-001-embedded-adb.md)，新实现提交/推送、构建和发布未执行
