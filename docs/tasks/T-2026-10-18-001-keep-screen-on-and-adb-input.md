# T-2026-10-18-001：主页常亮设置与 ADB 远程输入

## T0—T4：范围、证据与授权

- Status：complete / T8（实现与 V0；Git/dispatch 回执见最终交付）；2026-10-18；Primary：ashell-master，Android/Flutter/Rust/Network/Security/Release 由当前会话审查，不委派。
- 起点：test/00193fac70a866723f854ff4d2c358f898345aa9，初始 clean，与 origin/test 一致。
- Baseline：AS-BL-2026-10-16-4d3c446-adb-render-ui，ADR0026 / D031；实现真相以当前 HEAD 为准。
- C0/C2：参照 RustDesk，在 App 主页开放常亮设置，以实际远程连接控制常亮；完善已有 ADB 远程输入弹窗的回车/确定操作，保留 Ctrl+V。C1：必要合同及任务记忆。C3：提交推送 test、单次 Build-Clients-New / test / Both / 常规编译，不监控。
- 证据：四处强制写入 service-on；悬浮窗缓存模式并以 MediaProjection.isStart 判断被控；主页未暴露选项。ADB paste 已有菜单/后端，缺回车发送及自动焦点。
- 上游参考：RustDesk master 的 [settings_page.dart](https://github.com/rustdesk/rustdesk/blob/master/flutter/lib/mobile/pages/settings_page.dart)、[server_model.dart](https://github.com/rustdesk/rustdesk/blob/master/flutter/lib/models/server_model.dart) 与 [FloatingWindowService.kt](https://github.com/rustdesk/rustdesk/blob/master/flutter/android/app/src/main/kotlin/com/carriez/flutter_hbb/FloatingWindowService.kt)（2026-10-18 阅读）；沿用既有三档和 keep-screen-on 配置，不照搬以屏幕共享替代实际连接的判断。
- Impact：主页/旧设置入口、ServerModel 常亮通知；Activity/悬浮窗共用本机策略，Rust CM 提供只读授权连接判定。ADB 只改弹窗，不改 paste RPC、Ctrl+V、输入传输或媒体。无依赖/协议/生成桥/身份变更。
- 生命周期：连接判定读取 CM 锁，不保存 JNI 引用；后台沿用悬浮窗原有 1 秒刷新；最后一个真实远控断开后释放常亮标记；不修改系统超时时间，不申请唤醒/解锁。输入框与 FocusNode 随弹窗释放。
- 风险 medium；验证 AND-01/04/05/08、FLT-04/05/06、ADB-R07 及本任务多 PC/ADB-only/权限/重启/输入法案例；V2/V3 待证。不执行本地产品 build/test/analyze/codegen 或真机操作。

## T5—T8：实现与验证

- 主页三档选项与说明完成，旧设置入口同步读真实值，删除四处强制赋值。前台 Activity/后台悬浮窗共用 KeepScreenOnState，JNI 读取授权 CM 远控连接；不以共享/ADB 状态冒充连接。多 PC 使用 any，断开最后一台释放标记，空模式默认连接期间；service-on 指 core isReady。
- UI 写入设置完成后调用 refresh_keep_screen_on；原连接接受流程保持，只在 CM 完成后追加常亮刷新，避免抢读旧授权值。同数量客户端状态变化、媒体状态通知、前台恢复也刷新；后台沿用既有悬浮窗 timer，无新轮询器。前台无悬浮权限也可常亮，后台需权限且明确提示。配置/连接读取均为本机，无新网络请求。
- ADB 已有 paste 菜单复用，自动焦点、回车/数字键盘回车发送、Shift+Enter 换行；组合输入交还输入法，KeyRepeat 不重复发；按钮为“确定”。busy 只读、成功清空/复焦、失败保留。FocusNode/Controller 随弹窗销毁；文本/权限校验与取消沿用既有 controller。
- V0：逐项检查配置读写顺序、CM 授权/断连、多 PC、core 与 projection 分离、前后台/权限、JNI 名称/ABI/R8/锁范围、键盘组合/焦点/异步销毁；diff 范围/空白与旧强制赋值残留检索。MainService/Accessibility/原采集 JNI/媒体/ADB paste 后端/Ctrl+V/生成桥/构建脚本未改。
- V1/V2/V3 NOT_RUN：未运行本地 build/test/analyze/codegen 或真机操作。V0 不证明编译、系统 ROM 常亮或 IME 行为通过。
- 《编译验证需求》：GitHub Build-Clients-New.yml，ref=test，Platform=Both，BuildMode=常规编译；使用工作流现有 Linux Android / Windows 工具链，仅 dispatch，不监控。验证 Release JNI 加载与 Android/Windows 编译；真机覆盖零连接/两 PC/只断一台/最后断线、ADB-only、共享开关、模式切换/重启/悬浮授权、中文候选词/多行/emoji/长按回车/断线失败/直接 Ctrl+V。
- 最小合同 04_ANDROID_PIPELINE/13_REMOTE_ADB 与工程记忆同步。STATE/WORK-20261018-001、CE-20261018-T001-01；Decision/ADR/Baseline/External Registry 无增量，未引入外部资产或依赖。提交推送 test 与单次 dispatch 的准确 SHA/接受回执见最终交付，不推定构建结果。
