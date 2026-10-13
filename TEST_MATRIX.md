# AShell Commercial Test Matrix

2026-10-13 ADB 增量：8662f23 / [Baseline 11](docs/BASELINE/11_LOCAL_ADB_BASELINE_2026-10-13.md) 已接入独立本机模块。AND-06 继续验证旧组件退役；新增 AND-09—13 验证新实现。T003 仅文档/V0，所有新增 V2/V3 用例 `NOT_RUN`，没有从文档同步请求推定编译或真机 PASS。

最后更新：2026-07-12  
Baseline：`CS-BL-2026-07-12-77062b4`  
当前执行状态：本次封版只完成 V0 文档/源码静态盘点；下列 V1—V5 cases 均未执行

> 本矩阵定义覆盖和证据，不授权 build、test、analyze、codegen、device、network、production 或 release。V1—V4 需要独立 C3 + G2；V5 另需 G3、staging/production 和 rollout 授权。

## 1. Result and Evidence Rules

| Result | 含义 |
|---|---|
| `PASS` | 在指定 Baseline、环境和步骤上满足 oracle，并有完整 evidence |
| `FAIL` | 结果不满足 oracle；不得降级验收 |
| `BLOCKED` | 缺环境、owner、asset、contract 或 authority |
| `NOT_RUN` | 尚未执行 |
| `N/A` | 确认不适用，并记录理由/批准者 |
| `WAIVED` | 风险被明确接受；不计 PASS |

每个 PASS 必须绑定：Task/ADR/Baseline ID、commit + dirty state、toolchain/service/device、steps/command + exit/result、expected/actual、sanitized evidence、executor、independent reviewer、evidence owner、日期和 retention location。

## 2. Verification Levels

| Level | Evidence | Authority |
|---|---|---|
| V0 | diff/path/reference/schema/docs/Skill/static secret pattern | C0/C1 task scope |
| V1 | format/lint/unit/static analyzer | explicit C3 + suitable toolchain |
| V2 | formal product build/package | G2 + explicit C3 |
| V3 | Android/Windows device/OS/runtime regression | G2 + explicit device C3 |
| V4 | isolated hbbs/hbbr/API/ZEGO/security integration | G2 + isolated environment authority |
| V5 | staging rollout、monitoring and rollback observation | G3 + production/release authority |

Static review, an empty test suite, a build without runtime cases or “no error observed” never upgrades evidence automatically.

## 3. Formal Environment IDs

| ID | Required Environment | Evidence Owner |
|---|---|---|
| `E-A` | Controlled Linux Android build host；Android 10/13/14/15；AOSP/Huawei/ColorOS；arm64-v8a/armeabi-v7a/x86_64；isolated test account/relay | Android QA owner |
| `E-W` | Formal Windows host；supported Win10/11；installed/portable；single/multi display；Amyuni present/absent；recoverable snapshot | Windows QA owner |
| `E-F` | Baseline-pinned Flutter/Dart；desktop + Android；multi-window capable | Flutter QA owner |
| `E-R` | Baseline-pinned Rust/MSVC/native dependencies；workspace features/platform targets | Rust QA owner |
| `E-N` | Isolated versioned hbbs/hbbr；old/new controller/endpoint；latency/loss injection；disposable credentials | Network/infra evidence owner |
| `E-P` | Mock/contract + isolated backend/DB/token broker；synthetic data；clock/TTL control；DB snapshot | Backend/data evidence owner |

## 4. Current Test Asset Baseline

- Rust source contains 55 `#[test]` / `#[tokio::test]` marker lines across 26 files；none were executed in this seal.
- `flutter/test/cm_test.dart` is a manual `flutter run` harness；`flutter_test` remains commented in `pubspec.yaml`, so it is not an automated suite.
- No tracked Kotlin `src/test` or `src/androidTest` files were found.
- No repository evidence proves a complete Windows、Network or API E2E harness or current formal results.

These are coverage facts, not pass results.

## 5. Android Suite

| ID | P | Target | Positive + Negative / Compatibility / Recovery | Pass Oracle | Level / Env | Owners |
|---|---|---|---|---|---|---|
| AND-01 | P0 | Core/share lifecycle + permission | explicit start/stop；hidden boot/reconnect/legacy paths；Android 14+ one-shot token；projection stop/service death | hidden paths never prompt；projection loss only loses share；core/JNI/relay remain；guarded recovery has no loop | V3 / E-A | Android / Android QA |
| AND-02 | P0 | First frame and frame source | normal/SKL/ignore/one-shot；RGBA + Texture；static Huawei screen；no frame/failure | real frame clears waiting；normal refresh never auto-fallback/rebind/prompt/forge state | V3 / E-A | Android+Flutter / Android QA |
| AND-03 | P1 | Display metrics | rotate、split、fold、narrow/small、density change；rapid changes | no divide-by-zero、token restart or rebind storm；orientation/topology recover | V3 / E-A | Android / Android QA |
| AND-04 | P0 | Reconnect | 10s/70s loss、latency、jitter、relay failure、process background | one 2.5s timer；60s behavior and last frame hold；strict relay；no timer pileup | V3+V4 / E-A+E-N | Android+Network / QA+infra |
| AND-05 | P0 | Input authorization | permission on/off；mouse/key/touch；command masks 37–44；modified controller | controlled endpoint rejects unauthorized actions；UI hiding is not oracle | V3+V4 / E-A+E-N | Android+Network+Security / QA |
| AND-06 | P0 | Legacy ADB retirement (T009) | fresh install/upgrade；home swipe/back；three ABI APK inventory | no old page/channel/automation/libadb.so；ServerPage and other native libraries remain | V2+V3 / E-A | Android+Security+Release / QA |
| AND-07 | P0 | ZEGO voice consent | incoming/accept/reject/cancel/background/busy/reconnect/mic denial | no auto mic；reject is distinct；busy state clears；token/room scoped | V3+V4 / E-A+E-P | Android+Flutter+API+Security / QA+backend |
| AND-08 | P0 | Packaging/permissions | clean approved native assets；ABI/JNI；manifest/boot/exported components；permission revoke | hashes/license/provenance match；all ABIs link；no hidden privilege grant | V2+V3 / E-A | Android+Release+Security / Release evidence owner |
| AND-09 | P0 | Local ADB pairing/connect | API 30+ 正确/错误/过期六位码与配对端口；改用连接端口；API < 30；撤销系统配对 | 只有配对成功后再连接并通过 id/exit=0 才 ready；失败不假报；旧系统提示不支持；无自动调试/重连 | V3 / E-A | Android+Security / QA |
| AND-10 | P0 | Shell-v2 result | id、Unicode stdout/stderr、非零退出码、空/NUL/超长输入；连续单次命令 | 输出/exitCode 真实；坏输入拒绝；无 PTY/会话持久化假象；原产品状态不受查询影响 | V3 / E-A | Android+Flutter / QA |
| AND-11 | P0 | Timeout/cancel/output bound | 错误端口、阻塞命令、重复点击、取消、20 秒 watchdog；输出超过 64 KiB | UI 不阻塞/不重复回调；取消后实际清理完成才可重试；超限 truncated、exitCode 可空；不自动重试有副作用命令 | V3 / E-A | Android+Flutter / QA |
| AND-12 | P0 | Identity/lifecycle | 同进程重复断开连接、进程重启、前后台/切页/engine 销毁；测试环境损坏身份/忘记配对 | 身份不静默替换；无配对码/命令内容日志；断开不伪称撤权；无 timer/worker 泄漏；恢复需明确操作 | V3 / E-A | Android+Security / QA |
| AND-13 | P0 | New ADB Release dependencies | 本地模式1/2或独立CI；目标三ABI；Release shrink；全新依赖解析 | 四项版本/来源可追溯；SPAKE2/Conscrypt JNI 可加载；旧libadb.so缺席；原libashell/ZEGO完整；无新增敏感权限 | V2+V3 / E-A | Android+Release+Security / Release evidence owner |

ADB 详细步骤、限制和取证项见 [本机 ADB](docs/AI_ENGINEERING/12_LOCAL_ADB.md)。至少覆盖 Android 10 的拒绝路径、Android 11/13/14/15 与目标 ROM；pair 和 connect 两种端口必须分开测。以无副作用查询验证原 AND-01/02/04/05/07/08、FLT-04/05/06；不要把用户主动修改系统的 shell 副作用当成实现自动改动。取消只是关闭传输，不以后台子进程一定被终止作为当前实现保证。

### PC 远程 ADB 增量（T004—T007 / 当前用例 NOT_RUN，旧QR用例N/A）

源码与步骤：[13_REMOTE_ADB.md](docs/AI_ENGINEERING/13_REMOTE_ADB.md)。本机AND-09—13继续验证；engine销毁仅取消本机owner，共享manager/worker归进程，不能以仍存在的共享worker判泄漏。以下V2/V3/V4须正式环境取证。

| ID | P | 场景 | Pass oracle | Level / Owner | Result |
|---|---|---|---|---|---|
| ADB-R01 | P0 | PC菜单打开先取状态；A正确/错误/过期码配对但不connect；B打开同一Android | 输入提交前状态确认；B看到Android记录，可我已配对直接连接，id成功才connected；顶栏自动隐藏不销毁弹窗请求 | V3 / Flutter+Android | NOT_RUN |
| ADB-R02 | P0 | A/B/C同时shell/手动刷新；队列达到8项；每端不同可辨识无副作用输出 | 有限串行、超额busy、不交叉回执；查询同一Android状态、无PC周期status；非零exit/Unicode/64KiB结果真实 | V3+V4 / Network+Android | NOT_RUN |
| ADB-R03 | P0 | 断线、弹窗关闭/ESC、PC切只读；排队/执行中取消；手机keyboard开关切换 | 仅owner未完成任务取消；空闲连接/取消扫描保留共享ADB；手机keyboard不影响独立ADB；不重放命令 | V3+V4 / Flutter+Network | NOT_RUN |
| ADB-R04 | P0 | 有排队请求时revoke；再刷新/进程重启/重新配对；模拟清理失败 | 旧任务取消、App身份/记录失效；R8后SSLContext不复用旧key；失败不虚报；系统列表手动忘记提示正确；原会话保留 | V3+V4 / Android+Security | NOT_RUN |
| ADB-R05 | P1 | NSD先收到其他设备广告；发现被限制、端口变化、多本机广告、服务丢失 | 仅选本机唯一端口，普通发现6.5秒；清旧值，可手工回退；paired不冒充有效shell | V3 / Android | NOT_RUN |
| ADB-R06 | P0 | 新旧PC/Android组合；非Android；未认证/关闭控制/文件/terminal/camera；坏JSON/超大/未知方法 | 旧端status超时清本地等待；非Androidunsupported；受控端拒绝越权，reply仅发起者；原字段行为不变 | V2+V4 / Network+Security | NOT_RUN |
| ADB-R07 | P0 | Android Release各目标ABI与PC build.cmd；JNI/protobuf；本机回归及原链并行查询 | 两端字段40可用，FRB未变；JNI/R8加载和撤销重配通过；AND-01/02/04/05/07/08、FLT-04/05/06保持 | V2+V3 / Release+domains | NOT_RUN |
| ADB-R08 | P0 | 手机本地配对连接；A/B两PC打开ADB弹窗或手动刷新；手机keyboard=N/无Accessibility | 本次查询返回手机记录/真实连接；不承诺自动推送；view-only能看状态但操作被server拒绝；原输入逻辑保持 | V3+V4 / Android+Network | NOT_RUN |
| ADB-R09 | P0 | 两端只填码、手填配对端口且连接留空；错码/关闭系统窗/配对后关闭调试 | 自动发现两类端口并pair-connect-id一步完成；部分成功保留paired/显示连接失败；无假connected | V3 / Android+Flutter | NOT_RUN |
| ADB-R10 | P1 | 历史PC二维码正例（T006） | efb2599已移除该功能，本用例退役；不以移除视为测试通过，拒绝旧参数转ADB-R14 | V3+V4 / Android+Security | N/A |
| ADB-R11 | P1 | PC各菜单、标题栏拖动/缩小窗口、toolbar自动隐藏、断线重连、旧Android | 图标样式一致，配对无shell页签；窗口可见、输入可填写、状态失败明确；旧API提示更新，不重放shell | V3 / Flutter | NOT_RUN |
| ADB-R12 | P1 | 两端默认自动模式；输入手动端口后切回自动再切回手动；扫描期间尝试切换 | 自动仅配对码框，无端口框/扫描按钮，提交端口0；手动显示全部并使用填写值、留空自动；焦点不串框、配对码不被端口覆盖，执行中不可切换 | V3 / Flutter+Android | NOT_RUN |
| ADB-R13 | P1 | PC开窗后闲置30秒、手动刷新、执行命令；其他PC改状态；本机前后台 | PC无周期status/进度条闪动；开窗/手动/操作回执更新，其他PC变化需再次查询；保留请求回执等待，本机前台3秒查询不变 | V3+V4 / Flutter+Network | NOT_RUN |
| ADB-R14 | P0 | 两端UI检查；旧PC发送serviceName（含null）、其他未知参数及正常六位码；本机MethodChannel同类请求 | 无二维码入口/生成/专用等待；退役或未知参数返回invalid、无配对副作用；正常手动/自动配对仍可用 | V3+V4 / Android+Security | NOT_RUN |
| ADB-R15 | P0 | 已配对可控会话/只读/状态失败/忙；有在途shell时手动状态查询失败；已有ADB下取消配对扫描；配对成功后连接失败 | PC连接按钮可操作时主题色、不依赖配对码；真实禁用条件保持；状态失败不丢命令结果，不重放；未使用transport的扫描取消保留连接，部分成功保留记录并说明 | V3+V4 / Flutter+Android+Network | NOT_RUN |

## 6. Windows Suite

| ID | P | Target | Positive + Negative / Compatibility / Recovery | Pass Oracle | Level / Env | Owners |
|---|---|---|---|---|---|---|
| WIN-01 | P1 | Capture/fallback | DXGI/GDI/magnifier；single/multi screen；display change/lock | correct first frame/display；fallback truthful；resource cleanup | V3 / E-W | Rust/Windows / Windows QA |
| WIN-02 | P0 | Portable/input/SAS/UAC | installed/portable；keyboard layouts；UAC/secure desktop；permission off | authorized remote input only；failure visible；service/helper cleanup | V3 / E-W | Rust/Windows+Security / QA |
| WIN-03 | P0 | Privacy modes | topmost/exclude/magnifier/virtual；entry/exit；unsupported OS/driver missing | protection success is real or fail-closed；desktop/input always recover | V3 / E-W | Windows+Security / QA |
| WIN-04 | P0 | Virtual display recovery | add/remove/multi；kill/crash/reboot；Windows 10/11/24H2；third-party VD | original topology restored；no fast-loop crash、orphan display or input loss | V3 / E-W | Windows+Release / QA |
| WIN-05 | P0 | Driver/native artifacts | clean install/update/uninstall/rollback；bad hash/signature/publisher/ABI | untrusted artifact hard-fails；rollback succeeds；reboot state known | V2+V3 / E-W | Release+Security / Release evidence owner |
| WIN-06 | P0 | Privacy hooks/injection | local input blocked/remote allowed；DLL missing/tampered；escape/recovery | no unsafe loader fallback；hook/window failure restores safe state | V3 / E-W | Windows+Security / QA |
| WIN-07 | P1 | Remote printer | install/update/uninstall；consent；default/selected printer；large/concurrent/invalid XPS | no partial state、OOM or handle leak；cancel/cleanup and OS compatibility proven | V3 / E-W | Windows+Security / QA |
| WIN-08 | P1 | Artifact smoke | installed/portable launch、upgrade、uninstall、repair、wrong architecture | name/version/hash/signature correct；clean removal and rollback | V2+V3 / E-W | Release / Release evidence owner |

## 7. Flutter Suite

| ID | P | Target | Positive + Negative / Compatibility / Recovery | Pass Oracle | Level / Env | Owners |
|---|---|---|---|---|---|---|
| FLT-01 | P1 | Analyzer/unit/widget baseline | analyzer + actual test discovery；failure/empty suite | configured tests run；zero discovered is not PASS | V1 / E-F | Flutter / Flutter QA |
| FLT-02 | P1 | Startup/multi-window/session | mobile/desktop/CM；open/reuse/hide/close；wrong/late window ID | events stay in correct engine/session；teardown complete | V1+V3 / E-F | Flutter / QA |
| FLT-03 | P0 | EventToUI/first frame | session ID；RGBA/Texture；duplicate/late/malformed event | correct model receives event；both frame types clear waiting | V1+V3 / E-F+E-A | Flutter+Rust / QA |
| FLT-04 | P0 | Lifecycle/reconnect | timers/subscriptions/controllers；logout/nav/background/close；race/late callback | no leak、double reconnect、post-dispose update or stale client | V1+V3 / E-F | Flutter / QA |
| FLT-05 | P0 | Bridge contract | FRB/handwritten FFI/MethodChannel；generated drift；bad ABI/error | signatures match all sides；generated diff reviewed；errors bounded | V1+V2 / E-F+E-R | Flutter+Rust+Android / QA |
| FLT-06 | P1 | Product flows | account/file/terminal/ZEGO success、deny、timeout、back navigation | state/error/consent remains domain-correct；no sensitive logging | V1+V3+V4 / E-F+E-A+E-P | Flutter+API+Security / QA |
| FLT-07 | P2 | UX/accessibility | narrow/wide、localization、keyboard/focus、window close/restore | no inaccessible controls、layout loss or stale dialog overlay | V1+V3 / E-F | Flutter / QA |

## 8. Rust Suite

| ID | P | Target | Positive + Negative / Compatibility / Recovery | Pass Oracle | Level / Env | Owners |
|---|---|---|---|---|---|---|
| RST-01 | P1 | Workspace/features/cfg | all workspace crates；flutter/cli/platform features；missing optional deps | intended matrix compiles；dormant feature failures reported, not hidden | V1+V2 / E-R | Rust+Release / Rust QA |
| RST-02 | P1 | Existing inline tests | all discovered test targets；zero/skipped/panic | 55 marker baseline reconciled with executed inventory；all required pass | V1 / E-R | Rust / QA |
| RST-03 | P0 | FFI/JNI/generated bridge | symbols、types、ownership、pkg2230/ffi drift、bad inputs | ABI matches consumers；foreign memory lifetime proven | V1+V2+V3 / E-R+E-A | Rust+Android+Flutter / QA |
| RST-04 | P0 | Unsafe/concurrency | raw frame、GlobalRef、static state、shutdown/replacement、panic/error | no race/UAF/alias violation；resources released；failure bounded | V1+V3 / E-R+E-A | Rust+Security / QA |
| RST-05 | P0 | File/clipboard/terminal/path | traversal/symlink/oversize/cancel/partial/concurrent/untrusted command | containment、permission、size and cleanup hold | V1+V4 / E-R+E-N | Rust+Network+Security / QA |
| RST-06 | P1 | Config/process/artifact identity | corrupt/old config；role args；SO/DLL names；migration rollback | correct defaults/migration；no role or artifact ambiguity | V1+V2 / E-R | Rust+Release / QA |
| RST-07 | P1 | Fuzz/property | protocol/path/parser corpus；malformed/large/random input | no panic/hang/OOM/escape；repro corpus retained sanitized | V4 / isolated | Rust+Security / Security evidence owner |

## 9. Network Suite

| ID | P | Target | Positive + Negative / Compatibility / Recovery | Pass Oracle | Level / Env | Owners |
|---|---|---|---|---|---|---|
| NET-01 | P0 | Controller strict relay | normal connect；explicit IP/domain；UDP/IPv6/direct candidates | controller uses relay and rejects direct entry/candidates | V4 / E-N | Network / infra evidence owner |
| NET-02 | P0 | Rendezvous/relay recovery | resolve/register/relay loss；latency/loss/60s+ disconnect | bounded retry、no resource leak、truthful state and recovery | V4 / E-N | Network / infra |
| NET-03 | P0 | Handshake/crypto | missing/invalid/mismatched key、tamper、downgrade、directional nonce | failures are fail-closed；keys/nonces direction-separated | V4 / E-N | Network+Security / Security evidence owner |
| NET-04 | P0 | Authentication/capability | password/click/2FA/trusted device；permission on/off；replay | endpoint enforces auth and each capability；revocation works | V4 / E-N | Network+Security / QA |
| NET-05 | P0 | Protocol compatibility | old/new controller/endpoint；unknown fields/commands；duplicate/out-of-order | published numbers unchanged；unknown behavior safe；matrix documented | V4 / E-N | Network+Rust / Network QA |
| NET-06 | P0 | Service transport | video/input/file/terminal/tunnel interruption、malicious path、oversize | permission/bounds/cleanup hold across reconnect/cancel | V4 / E-N | Network+Rust / QA |
| NET-07 | P1 | Voice control | invite/accept/reject/cancel/busy/reconnect/platform unknown | control state consistent；no stale busy or implicit media consent | V4 / E-N+E-P | Network+API+Flutter / QA |
| NET-08 | P1 | Endpoint direct compatibility boundary | compatible endpoint requests with/without force relay | retained endpoint behavior matches policy；not mislabeled global relay-only | V4 / E-N | Network / infra |

## 10. API and Data Suite

| ID | P | Target | Positive + Negative / Compatibility / Recovery | Pass Oracle | Level / Env | Owners |
|---|---|---|---|---|---|---|
| API-01 | P0 | Product login/token | login/logout、expiry、revoke、clock skew、bad token | server authorization and local cleanup; no token log | V4 / E-P | API+Security / backend evidence owner |
| API-02 | P1 | Identity separation | Flutter product account、Rust OIDC、endpoint auth cross-state | no domain token/state substitutes another | V1+V4 / E-F+E-P | API+Flutter+Network / QA |
| API-03 | P1 | AB/device/group contracts | CRUD、old/new schema、pagination、idempotency、conflict/concurrency | versioned contract and deterministic conflict behavior | V4 / E-P | API / backend QA |
| API-04 | P0 | Heartbeat/config/disconnect | TLS、server auth、replay/tamper/timeout/stale response | unauthenticated control rejected；safe retry/rollback | V4 / E-P | API+Security / backend QA |
| API-05 | P0 | HTTP policy | 2xx/4xx/5xx、timeout、malformed/oversize、concurrent same URL | unique request IDs、size/time bounds、redaction and correct errors | V1+V4 / E-P | API / QA |
| API-06 | P0 | Downloader | redirect/host、path/symlink、partial/cancel、hash/size | approved host/integrity/containment；partial cleanup | V4 / E-P | API+Security / QA |
| API-07 | P0 | Record upload dormant | all normal builds/configs；attempted enable path | remains unreachable/disabled until new ADR + consent/security | V0+V2 / E-R | API+Release+Security / QA |
| API-08 | P0 | ZEGO broker | user/peer/room scope、TTL、replay、rate limit、TLS、revoke | short scoped token；no client signing secret；fail-closed | V4 / E-P | API+Security / backend evidence owner |
| API-09 | P0 | External DB migration/recovery | expand/contract、tenant isolation、backup/restore、RPO/RTO | migration and rollback preserve isolation/data；owner evidence | V4 / E-P | Backend/Data / data evidence owner |

## 11. Cross-domain Release Journeys

| ID | P | Journey | Required Result |
|---|---|---|---|
| E2E-01 | P0 | Android relay → auth → share → first frame → input → loss/reconnect | AND/FLT/RST/NET cases remain consistent；no permission or fallback drift |
| E2E-02 | P0 | Windows capture → input → privacy → forced failure → topology restore | real desktop/input/topology fully restored |
| E2E-03 | P0 | Product account/device → remote connect → logout/revoke | identity domains stay isolated；revocation clears capability |
| E2E-04 | P0 | Permission on/off file/terminal/clipboard with interruption | endpoint denies unauthorized action and cleans partial state |
| E2E-05 | P0 | ZEGO invite → consent/reject → busy clear/reconnect | no auto accept/mic；token and state expire correctly |

Every release-affecting task selects all impacted domain cases and at least the matching E2E journey. Omission needs explicit rationale and owner approval.

## 12. Negative, Compatibility and Rollback Gate

- P0/P1 applicable cases must PASS before release；P2 non-PASS needs explicit risk acceptance.
- `BLOCKED`、`NOT_RUN` and `WAIVED` never count as PASS.
- Auth、permission、crypto、signature、hash、path containment and update failures must fail closed.
- Protocol/schema/config/driver changes require old/new compatibility and downgrade/rollback cases.
- State、driver、display、database or installer changes require recovery/rollback PASS, not only happy-path PASS.
- External backend/hbbs/hbbr/database claims cannot PASS from client-only repository evidence.

## 13. Evidence Package Template

```text
Task / ADR / Baseline ID:
Commit + dirty state:
Artifact/source hash:
Toolchain/service/device/OS/ROM/ABI:
TEST_MATRIX case ID:
Steps or command + exit/result:
Expected / actual:
Sanitized log/screenshot/dump/metrics:
Negative / compatibility / rollback result:
Executor / independent reviewer / evidence owner:
UTC + local timestamp:
Retention location:
```

Never include token、password、credential、production URL/IP、peer/device ID、contacts、file/clipboard/terminal content or PII.

## 14. Formal Verification Request

### Client Actions（T-2026-10-13-001）

| ID | Target / Action | Pass Oracle | Level / Environment | Status |
|---|---|---|---|---|
| CI-01 | dispatch android + aarch64 | 仅 ARM64 native + APK jobs；APK arm64-v8a 核心 SO 完整、apksigner 成功；Windows skipped | V2 / GitHub ubuntu-22.04 | NOT_RUN |
| CI-02 | dispatch android + universal | 三 native jobs 成功后生成三 ABI APK；无 libadb.so；新 local ADB 的依赖保留 | V2 / GitHub ubuntu-22.04 | NOT_RUN |
| CI-03 | dispatch windows | Android jobs skipped；EXE + 完整 ZIP、当前 Release 所有必需文件与 SHA256；无外部 driver 拼装 | V2 / GitHub windows-2022 | NOT_RUN |
| CI-04 | dispatch both / failure / rerun | 两平台独立成功上传；一边失败不取消另一边、不上传失败成品；同 run 未过期 native artifacts 可复用；无 Release/tag/Git 写入 | V2 / 两种 hosted runner | NOT_RUN |

本轮 V0 YAML/矩阵/语法/权限静态检查不升级上述状态。安装/启动与功能回归继续使用 AND-08、WIN-08、FLT-05、RST-03 等现有 cases。签名与下载说明见 [Actions 使用说明](docs/BUILD_CLIENTS_ACTIONS.md)。

T002 / 2026-10-13：用户反馈初版 Android NDK step 因 sdkmanager 缺失而失败（127）；未提供 run URL/SHA/ABI，不能映射为任一完整 case 的结果。两个 Android job 已补 SDK setup；上表 NOT_RUN 指修复后的运行。CI-01/02/04 额外检查 JDK → setup-android → sdkmanager 顺序、CLI 16.0、统一 SDK root 与 NDK 安装；通过 Run workflow 选择修复分支重新发起，保留新 run URL 和 SHA。

T005 / 2026-10-12 PC入口补充（全部NOT_RUN）：正式Windows环境从仓库根运行 `build.cmd`；确认仅一个入口、最终 `PC-Bulid/ashell.exe` 的解压清单与本次Release一致。缺外部DLL/驱动时仍能完成产物打包；缺Brotli在编译前失败；源目录越界链接被拒绝；portable Cargo失败时CMD非零、不交付旧packer；manifest成功/失败路径可恢复。WIN-03/05/06/07/08分别验证未附带组件时的功能边界、资产策略和启动行为；这些源码规则不等于测试通过。

When execution is needed but not authorized, output and stop:

```text
《编译验证需求》
- 命令：<exact approved command set>
- 环境要求：<E-A/E-W/E-F/E-R/E-N/E-P and prerequisites>
- 执行目录：<absolute/declared project directory>
- 验证目标：<case IDs and observable pass/fail oracle>
```
