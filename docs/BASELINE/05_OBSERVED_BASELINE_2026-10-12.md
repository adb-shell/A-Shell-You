# AShell 源码观察基线 2026-10-12

> 名称规范化说明（2026-10-12）：按用户明确要求，本页品牌术语、字段和路径已统一为当前名称；历史日期、提交标识及验证结论不变。这不是当年文本的逐字副本，原文和真实历史源仓地址请从 Git 提交 a036f31 的对应文件恢复。

Baseline ID：`CS-BL-2026-10-12-6fb0aa6`。类型：repository-observed / V0；不是 product build/release baseline。保留 `CS-BL-2026-07-12-77062b4` 历史快照，不修改其冻结值。

| 项目 | 观察结果 |
|---|---|
| Branch / HEAD | test / 6fb0aa6e147901243d4cb14a5dc68ae75a13b890 |
| 本地 refs | main/test/origin main/test 同 HEAD；无本地 tags |
| 历史 | 非 shallow；当前分支1条初始化提交 |
| 审查工作树 | 审查开始/结束 clean；后续记忆落盘文档未提交，不属于该 HEAD |
| 当前 origin | adb-shell/A-Shell-You |
| 历史源内容映射 | 历史源仓 cbce0ba36c5c；975个 tracked blob 路径/hash一致；不含 ignored assets |
| 历史源边界 | 可读60提交；96a9c7cee2cd 为2026-04-13无父导入，准确 RustDesk fork 点未知 |
| 上游参考 | RustDesk1.3.9/1.4.0同路径同blob数量581/615；非fork证明；1.4.0 hbb_common ref=6e556f7e1751a3a709cd5cca0df7268ba3cb1c48 |
| 产品 | ashell5.2.1 / flutter_hbb5.2.1+59 / me.ashell |
| 声明工具链 | Rust1.75/edition2021；环境脚本Flutter3.24.5/JDK17/NDK27.2.12479018；Gradle7.6.4/AGP7.3.1/Kotlin2.1.21 |
| Android SDK | compile34 / target33 / min21，不表示全部功能支持API21 |
| 生成边界 | FRB1.80.1标记；proto由hbb_common/build.rs生成；src/version.rs为build.rs生成 |
| 当前缺口 | jniLibs不存在；ZEGO声明与pubspec.lock不一致；外部driver/DLL/签名和部署证据未闭环 |
| 验证 | 仅源码、文件存在性、本地Git及公开source metadata只读比较；V1—V5 NOT_RUN |

证据和覆盖详见 [接管认知](../AI_ENGINEERING/TAKEOVER_REVIEW_2026-10-12.md)。远端比较结果来自同轮只读审查；未 fetch/pull，未下载运行资产，未验证生产服务。

此基线不包含已授权记忆文档的后续工作树内容，不改变源码、dependency lock、版本、签名或产品行为。未来新增源码/依赖/环境观察应追加新快照，不覆盖本文件中的历史结论。
