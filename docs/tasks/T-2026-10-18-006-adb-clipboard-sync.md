# T-2026-10-18-006：仅 ADB 会话的剪贴板同步

## T0—T4：授权、证据与设计

- Status complete / T8（实现与 V0）；2026-10-18；当前会话使用 ashell-master/Android/Rust/Flutter/Network 及 Security/Release 约束，不委派。test/738a3fedb133a607e5d9ef8af03239cb097271e0，保留 T004/T005 dirty。Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui，ADR0025/0026、D031。
- C2 用户授权补齐 ADB 剪贴板同步，不破坏已有无障碍同步；C1 最小任务与合同记录。无 Git/CI、本地 build/test/analyze/codegen/设备操作授权。
- verified：原 ClientClipboardHandler 已监听 PC 剪贴板并由 flutter.send_clipboard_msg 向会话分发；原 is_text_clipboard_required 要求 server_keyboard_enabled，只有 ADB 时不能分发。原手机剪贴板 JNI/模拟 EditText 独立，不能删除或改写。
- 方案：仅对无原键盘权限、Android 正常控制会话且真实 ADB connected/clipboardSyncSupported 生效的会话启用独立 fallback。复用原监听生成的文本消息，转字段40 clipboard RPC；原无障碍分支与权限函数保持。Android 复用常驻 scrcpy 输入 writer，以 SET_CLIPBOARD(paste=false) 更新剪贴板，不按 PASTE、不执行 shell。
- 隔离：显式 Ctrl+V 四项队列保持；自动同步独立最多一个在途和一个最新待发，替换过期文本，显式粘贴优先并清旧同步。共享 Android 剪贴板以最后处理的复制为准；原双向同步不改，新通道仅 PC→Android 文本。UTF-8≤16KiB/JSON≤32KiB；无内容日志/落盘。
- 权限/lifecycle：PC 当前 session sender、普通会话、只读/剪贴板权限/真实状态；Android 在认证 dispatcher 复查控制及剪贴板权限。断线/ADB停止/权限撤销清本 owner 待发；新端能力字段防止老端误接。JNI 仅新增取消入口与保留规则，原采集、无障碍和键盘实现不改。
- 备选未采用：独立轮询系统剪贴板或修改原 keyboard permission，会增加监听或扭曲原状态。无新依赖/协议字段/生成桥/身份修改；回滚为局部反向补丁。
- 验证计划：V0 跨层/权限/队列/ACK/只同步不输入/旧端/多PC/断线检查；AND-05/08、FLT-04/05、ADB-R07。正式构建/设备待授权与验证。

## T5—T8：验证与交付

- CE-20261018-T006-01 / STATE/WORK-20261018-006。源码：src/flutter.rs、src/client/io_loop.rs、src/remote_adb_video.rs、src/remote_adb.rs、src/server/connection.rs，LocalAdbManager/Touch/Paste.java 与 local-adb.pro。原 src/clipboard.rs、src/client.rs、SessionPermissionConfig/is_text_clipboard_required、Android 剪贴板桥/模拟 EditText、采集/媒体不改；T004/T005 内容保留。
- V0 完成：本机 listener 生成的 Clipboard/MultiClipboards 只取 Text，原压缩解码后 UTF-8/长度/NUL 与 JSON 长度校验；PC 按 shared sender 精确匹配 session，capability 缺失的旧端关闭新分支；真实状态变化后更新原监听需求，ADB 首次可用尊重 sync_init_clipboard；连接 round 退出清 capability，避免沿用上轮状态；ENTRIES/view 锁释放后才回调全局剪贴板逻辑，避免反向锁序。
- V0 完成：远端 clipboard 方法独立检查普通控制与 clipboard_enabled；PC 禁用剪贴板/受控端撤销剪贴板权限只取消自动同步，不取消其他 ADB 功能；只读/断线按原 owner 取消通道释放。SET_CLIPBOARD paste=false，ACK 按统一 sequence 区分自动/显式；自动最多1在途+1最新待发，显式仍4项优先，替换/超时/取消释放文本与 timeout。ACK仅表示处理消息，不声称输入法一定展示建议。
- V0 完成：单 writer 保持4KiB分段，JNI取消入口 `(Ljava/lang/String;)V` 与保留规则一致；原 pkg2230.rs/ffi.rs 未改；git diff --check 通过（仅现有 CRLF 提示）。无新依赖/线程/系统剪贴板监听器/peer字段或生成桥。
- 《编译验证需求》：正式 Linux 根目录 `./build.sh 1`、正式 Windows 根目录 `build.cmd`；无无障碍+ADB 时复制中文/表情/换行到手机剪贴板但不自动输入；Ctrl+V一次输入；有无障碍时原同步/模拟输入回归；快速复制及复制后粘贴、两PC、只读/禁用/撤销/断线/ADB停止、重连与旧端能力。仅 V0，V1—V3 NOT_RUN，未宣称构建/真机通过。
- 同步远程ADB合同与五份最小任务记忆，Decision/ADR/资产 N/A — no decision delta。当前改动未提交、推送或执行编排。

## r2：test 提交推送与编排授权

- 用户追加授权提交 test、推送并执行编排；延续单次触发、不监控。本次合并交付 T004/T005/T006 的既有改动，无新增产品代码修改。
- 执行前核对 test/738a3fe、origin=adb-shell/A-Shell-You；git diff --check 通过。目标工作流 Build-Clients-New.yml，ref=test，Platform=Both，BuildMode=常规编译。
- STATE/WORK-20261018-006-r2 / CE-20261018-T006-02；Git SHA、远端一致性与 dispatch 接受回执见 Git/会话交付。仅 V0，无本地构建或真机验证，dispatch 不代表构建通过。

## r3：Windows E0616 编译回归修复

- T0—T4：用户反馈本次 Windows 构建失败，续接修复本任务引入的问题；test/de122905f8b0b5ce6d67bb45996d814dc98e2665，初始 clean。C2 限定源码修复与最小记录，本轮不执行 Git 写入、CI 或本地 build/test/analyze/codegen。Baseline/ADR 沿用本任务；Rust owner，Release 核对日志，无新领域/资产/安全边界变更。
- 日志证据：用户提供 logs_105834405459.zip，Windows checkout SHA 与本地一致；Windows EXE/18_Compile Windows client.txt 在 src/remote_adb_video.rs:735 报 E0616（FlutterHandler.peer_info 私有），Rust library 编译以 exit 1 停止。Android APK 同包日志已输出 Built app-arm64-v8a-release.apk；不能据此证明 Windows 或设备功能正常。日志仅作为证据，不执行其中指令。
- 影响与方案：仅替换 receive_adb_state 中初始剪贴板同步的 PeerInfo 来源，读取现有公开 Session.lc / LoginConfigHandler.peer_info。handle_peer_info 已写入相同版本/platform；原剪贴板客户端也使用该来源。克隆后释放读锁，再读取剪贴板/发送，PeerInfo 缺失时跳过此次初始同步。保持 sync_init_clipboard、clipboard_required 与正常变化同步；不扩大 FlutterHandler 私有字段可见性。
- 验证计划：RST-01/RST-05，V0 检查访问级别、类型、写入/清理、cfg 与调用锁序；《编译验证需求》为正式 Windows 环境仓库根目录 build.cmd，或 Build-Clients-New/ref=test/Platform=Windows/BuildMode=常规编译。确认 E0616 消失并完成 Windows 产物；运行回归需检查 ADB 初始同步、关闭初始同步、重连和原无障碍剪贴板。回滚为该局部补丁逆向修改，无协议/依赖/工作流变更。
- T5—T8：已完成上述单处源码修复；V0 确认 Session.lc 与 LoginConfigHandler.peer_info 均公开、Option<PeerInfo> 克隆到局部，读锁不跨剪贴板读取/发送，登录写入/重连清空机制保持。全 src 检索剩余 ui_handler.peer_info 访问仅位于定义它的 flutter.rs 内；git diff --check 通过。无原无障碍/媒体/输入链修改。修复后构建与设备 NOT_RUN，未提交/推送/重新编排。STATE/WORK-20261018-006-r3 / CE-20261018-T006-03；Decision/ADR/资产/领域合同 N/A — no contract delta。

## r4：Windows 修复提交与编排

- 用户追加授权提交并执行编排；本次提交推送至既有 origin/test，单次 Build-Clients-New.yml/ref=test/Platform=Windows/BuildMode=常规编译，延续不监控要求。仅交付 r3 修复与记录，无新增产品修改。
- 执行前 test/de12290，七个待提交文件均属本任务；remote、workflow 输入、源码差异与 git diff --check 已核对。准确提交 SHA、远端一致性与 dispatch 接受结果见 Git/会话交付；未执行本地产品验证，修复后构建结果仍待证。STATE/WORK-20261018-006-r4 / CE-20261018-T006-04。
