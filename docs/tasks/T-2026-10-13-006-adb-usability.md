# T-2026-10-13-006 — ADB discovery and usable remote pairing

按 TASK_TEMPLATE.md 记录；2026-10-13；Requester/Product Owner：当前用户；Principal/domain/security/release review：Codex，ashell-master 与对应 Android/Flutter/Network/Rust/Security/Release skills。Status source-complete / T8交付；风险 high；目标验证 V2/V3/V4，当前 V0。

## T0 / T4 Authority and baseline

- 当前用户明确授权实现、整理文档、提交和推送 GitHub，然后运行 Build AShell clients，不监控；C0/C1/C2 及 C3 stage/commit/push/CI dispatch 已满足，无需重复确认。
- Source main/504adea0119d9ff96d33a16cd27e559d1492a723；initial clean。T005 已提交，修正旧记忆中的 dirty/HEAD 漂移。
- 继承Baseline12，新增 AS-BL-2026-10-13-504adea-adb-usability；关联ADR-0017/0018/0019、D-023、ADB-R01—11；EXT-LIB-ADB-004—007。State006 / WORK012 / CE-20261013-T006-01。
- 不修改产品身份、Accessibility、capture、input、MediaProjection、ZEGO、原加密/relay 策略；无本地产品构建/依赖安装/设备测试、版本、发布或历史重写。Git 冲突/超出范围则停止对应动作。

## T1 Requirements / acceptance

PC toolbar 使用同款图标菜单；配对与 shell 等分窗；配对窗口可拖动。手机状态可被所有已认证普通会话读取；ADB 独立于无障碍键盘开关。自动发现本机配对/连接端口、手动覆盖、配对后自动连接并验证 shell；PC 支持二维码免手输码。Android 系统六位码不是 mDNS 数据，禁止假称可扫描读取。

## T2 / T3 Impact and design

- Flutter ADB module → existing common FFI keys → Misc.remote_adb field40 → authenticated server branch → isolated localadb JNI → process singleton。原 capture/input dispatcher 不改。
- verified：server_model 初始化 mobile enable-keyboard=N，但 remote_adb 用 peer_keyboard_enabled 拦截 status/mutations。ADB 状态只要求已认证普通会话；ADB mutation 另拒绝 PC view-only（disable_keyboard），不依赖 Android accessibility-backed keyboard。
- Android NSD 双服务发现，只接纳本机 interface/loopback；串行 resolve、有限等待、取消清理、不扫描第三方主机。参考 LADB/aShellYou upstream 公共实现思路，保留固定 libadb 3.1.1，无整包引入/新二进制。
- 新增 discover/pairConnect 与可选 connectPort/serviceName，保持旧 pair/connect 兼容；QR 使用系统 WIFI:T:ADB schema、临时随机口令/服务名，限定本机和指定服务，秘密不持久化/日志/状态广播。
- 状态由 Android 保存；PC 每 3 秒读取，结果只返回 origin；请求按 Android 队列串行，取消只影响 owner；共享 revoke 保持明确确认。
- 迁移不改 identity/record schema；回滚本任务明确文件可恢复旧行为；不恢复退休组件。新旧对端不支持新增方法应明确提示，禁止自动重放 shell。
- 生成桥/原 proto 字段不变；Android NSD 使用旧 API 兼容现有 compileSdk，避免上游新扩展 API 构建要求。

## T5 Changes

CE-20261013-T006-01：独立 ADB Java/Dart/Rust、toolbar 接入与配套文档；没有初始用户修改。实际清单以最终 diff 与 Change Event 为准。

## T6 Verification / security / build review

计划 V0：完整请求响应链、authorization、NSD local-only/取消、窗口生命周期、API/类型与依赖、diff whitespace。V1/V3/V4 NOT_RUN；V2 用户授权 GitHub both/aarch64 单次运行，不监控结果。CI 使用已配置 Flutter3.24.5/Rust1.75/NDK27/JDK17。

测试选择 ADB-R01—07，并补端口双发现/一键连接/QR/状态失效/只读会话负例。设备身份只在 Android，二维码秘密仅持有窗口/操作期；UI 不作为权限边界。失败后不伪造 paired/connected；配对成功但连接失败须保留配对记录与清晰错误。没有新增运行依赖或 upstream 源码复制，来源与许可证仅记录研究；发布 SBOM/signature/real-device 仍待正式证据。

## T7 / T8 Handoff

已同步domain docs、ADR-0019/D-023、Baseline13、TEST_MATRIX、external assets、State/Current Work/Changelog/Events/History；入口补最新指针。V0差异/请求响应/参数与超时/取消/NSD本机过滤/原链范围复核；本机没有Flutter/Dart/Java正式工具链，V1/V2/V3/V4未在本地执行。CI已授权both/aarch64；不把触发当构建通过，不监控运行。

本文件是提交前交付快照：实现与记录完成，stage/commit/push/dispatch将在随后按授权执行，实际SHA与dispatch回执由本会话最终答复给出；不可把计划写成已执行。回滚仅针对本事件路径，无identity迁移，无删除/版本/发布/设备操作。剩余正式验收由用户新包反馈补充。
