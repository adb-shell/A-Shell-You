# T-2026-10-18-003：静默常亮与默认值

## T0—T4：范围、证据与授权

- Status：complete / T8（实现与 V0）；2026-10-18；按 TASK_TEMPLATE.md 记录。Primary：ashell-master；Flutter/Android 由当前会话处理，不委派；Security/Release 无新增信任/构建边界。
- Session recovery：test/9e28a9057c627da160a5063ffd49bf74e222a84a，初始 clean；T002 已完成，与 origin/test 一致，无并行任务。基线 AS-BL-2026-10-16-4d3c446-adb-render-ui，ADR0026 / D031；当前源码优先，旧任务记录保留历史。
- C0/C2：用户最初要求主页隐藏常亮选项、放入 App 设置，后续明确取消新增顶栏齿轮和设置入口，默认静默保持常亮。最新要求覆盖入口方案；允许删除本任务新增且未提交的 AppSettingsPage 及其路由。C1：必要 Android 合同、Task 与记忆。交付续接 C3：用户明确“推送test并编译”，授权提交推送 origin/test 后单次 Build-Clients-New / ref=test / Platform=Both / BuildMode=常规编译；沿用不监控要求，不执行本地产品编译或设备操作。
- 证据：ServerPage.EnhancementsSection 暴露三档；HomePage 的旧 SettingsPage 路由被注释，旧页初始化请求权限并修改其他选项；KeepScreenOnState 空值默认真实远程连接。
- 最终方案：主页移除常亮专用 UI/状态，无新增设置页、齿轮或弹窗。Dart 与 Kotlin 未配置/未知值统一 service-on，保留用户已保存的 never/during-controlled/service-on。旧 dormant 设置页仅对齐默认解析，不开放其路由；不强制写配置，不新增权限请求。
- 不变量：service-on 仍以 core isReady 判定；during-controlled 仍用真实授权连接；不修改原 MediaProjection、无障碍、ADB、电池豁免、传输、JNI/协议或权限申请。不改变用户已有选项，不加迁移标记/强制重写。
- Impact：Flutter ServerPage 与 dormant 默认解析；Android KeepScreenOnState 默认分支。HomePage 恢复本任务修改前源码；新增页面删除，无残留引用。无外部资产、依赖、生成桥、签名/版本变化。风险 low，后台常亮仍需既有悬浮权限；正式 V2/V3 待证。
- 不采用恢复完整旧设置页：会引入本任务外的权限/设置行为。回滚：反向恢复本次局部 UI/默认补丁，既有持久化键兼容，无数据迁移。Decision/ADR/Baseline/External Registry：N/A — no decision delta。

## T5—T8：修改与验证

- Change Event：CE-20261018-T003-01（原方案）、CE-20261018-T003-02（取消入口）、CE-20261018-T003-03（授权 Git/CI 交付）；STATE/WORK-20261018-003-r3。
- 最终实际变更：`flutter/lib/mobile/pages/server_page.dart` 移除常亮控件/缓存，保留悬浮权限刷新；`settings_page.dart` 与 `flutter/android/app/src/main/kotlin/me/ashell/KeepScreenOnState.kt` 默认分支对齐。相对 HEAD 仅三份源码文件，无原采集/无障碍/传输实现变化。r2 明确删除的唯一文件为本任务创建的 `flutter/lib/mobile/pages/app_settings_page.dart`，同批移除唯一 import/导航引用，未删除原项目文件。
- V0：源码复检默认分支与真实 core 状态，检查主页无常亮控件、无新增齿轮/AppSettingsPage 残留、HomePage 与 HEAD 一致、diff 范围/空白。无新增权限请求、timer 或 observer，不写其他设置。FLT-04/05/06、AND-01/08 对应运行用例未执行。
- 《编译验证需求》：正式 Linux Android 构建机、仓库根目录 `./build.sh 1`；检查 Dart/Kotlin 编译、无新增 UI 入口/提示、空配置默认、既有选项兼容、core 停启、前后台/悬浮权限。V1—V3 NOT_RUN，不以 V0 代替构建/真机通过。
- 最小 Android 合同及五份 `.codex` 任务记忆同步；Decision/ADR/Baseline/External Registry 无增量。交付前再次检查 test/9e28a90 与当前三份源码差异、工作流参数及白名单文件；用户授权提交推送并单次编排，准确 commit/push/dispatch 回执见会话交付，不把请求接受当构建通过。
