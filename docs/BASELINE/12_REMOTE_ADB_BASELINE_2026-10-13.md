# Remote ADB Worktree Baseline — 2026-10-13

- Baseline ID：`AS-BL-2026-10-13-917e661-remote-adb-worktree`
- Observed branch / HEAD：`main` / `917e6612cdfd819b824626564478a3ff1d84eec4`，初始 clean。
- 实现范围：T-2026-10-13-004 未提交工作树；不能把新增功能归入917e661提交。
- Decision / Event：D-022 / ADR-0018 / CE-20261013-T004-01。
- Evidence：source/V0；V1—V4 NOT_RUN，不是正式产物基线。

| 项目 | 快照 |
|---|---|
| 产品身份/版本 | me.ashell、libashell/ashell.dll、5.2.1、5.2.1+59 不变 |
| 协议 | Misc.remote_adb字段40，JSON v1；既有会话传输策略不变 |
| Flutter ABI | session_get_common签名不变，request/poll/forget新key；无FRB生成文件变更 |
| Android | 进程级LocalAdbManager；独立RemoteAdbNative JNI；API30+；既有Java8/SDK/Gradle不变 |
| 依赖 | libadb3.1.1 / spake2 2.2.1 / BC1.81 / Conscrypt2.5.3，与Baseline11相同 |
| R8 | local-adb.pro新增RemoteAdbNative、manager两个回调、SslUtils反射保留 |
| 身份/记录 | 原noBackupFilesDir加密身份格式不变；local_adb_record_v1新增配对/连接端口记录 |
| 限额 | 8操作、20秒取消触发、64KiB输出、32KiB请求JSON/512KiB回复JSON |
| 构建 | 不改脚本/CI；沿用HEAD已有ABI配置，未重审runner成功状态 |

基线11及更早快照保留。两端必须编入此次Rust/protobuf，Android同时编入Java/R8；只更新PC界面不能激活新协议。实际产物hash、ABI/JNI/Release验证及外部依赖证据仍待正式环境提供。
