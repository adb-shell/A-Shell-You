# ADR-0020 — ADB Code Pairing and Explicit Refresh

- Date / Status：2026-10-13 / accepted for implementation；仅V0。
- Approver：当前用户明确要求移除二维码、取消PC三秒刷新、两端按模式显示输入框、突出PC已配对连接按钮；随后授权同步文档。
- Owner：Codex ashell-master；Flutter/Android/Network/Security/Release边界静态自审，无子agent。
- Related：D-024 / T-2026-10-13-007 / Baseline14。
- Partially supersedes：[ADR-0019](0019-adb-discovery-and-independent-control.md) 的QR/serviceName、PC周期刷新、长等待预算和模式展示。Android单一owner、独立权限、NSD、原链隔离与固定依赖继续有效。

## Context and decision

PC远程输入六位码即可让手机配对，用户不需要扫码；周期性状态刷新影响弹窗视觉，自动模式又显示不会消费的端口值，容易误解。

1. 移除二维码界面、生成逻辑、serviceName业务参数及专用等待。Android拒绝旧参数和其他未知业务参数，不静默降级执行。全局qr_flutter仍供其他页面使用。
2. PC只在开窗、手动刷新或本窗口操作回执时更新Android状态；不定时status，也不开窗预扫描。请求期间读取本地Rust回执表保持，以接收命令结果；Android本机前台三秒查询不变。
3. 两端默认自动模式仅显示配对码框，提交端口0，由手机在执行时发现端口；手动模式显示两端口框和扫描按钮，填写值优先、留空自动。切换解除焦点，输入控件有稳定key，手动值不在自动模式提交。
4. PC“我已配对”改主题色按钮；支持/权限/本窗口忙判断不变，无需配对码。仅样式变化，不能把灰色都解释为配对失败，也不能绕过服务端只读限制。
5. 状态查询错误不放弃在途命令；结果未知时尽力取消owner任务、不重放。任务尚未使用transport时的扫描失败/取消不误断已有ADB；已确认配对成功但后续连接失败须保留记录并说明。

Android普通watchdog20秒、pairConnect35秒，NSD6.5秒；PC status/cancel8秒、其他300秒，Rust等待表/回复路由310秒惰性清理。watchdog发起取消后仍需worker退出，不声称严格实时保证。

## Alternatives and consequences

- 保留周期刷新但隐藏进度条可更及时展示其他PC状态；用户选择移除周期刷新，因此明确告知需手动查询。
- 统一为可选端口可以取消模式按钮；用户选择保留两种模式并分别展示，未实施此替代方案。
- 仅隐藏QR UI会留下旧请求入口，故同时拒绝退役参数。带serviceName的旧PC需更新；普通pair/connect兼容。
- 状态是最近快照，多PC共享Android身份/连接不等于状态自动推送。任意shell的真实系统副作用不随取消或代码回滚撤销。

## Compatibility, evidence and rollback

Misc.remote_adb字段40、JSON v1、status.apiVersion=2、FRB签名、local_adb_record_v1和身份schema不变；无数据库/权限/依赖/工具链迁移。原Accessibility、视频、输入、terminal、ZEGO与产品身份不改。

feac956、efb2599已提交；本次文档同步开始时，PC controller/menu和Android local_adb_page三份Dart修改尚未提交。efb2599推送后的both/aarch64 CI仅有HTTP204 dispatch接受回执，未监控、不等于构建通过。当前用户追加授权提交全部待交付改动并再触发一次构建；实际结果由最终交付回执确认，不预写成功。

V0源码/差异/文档检查；正式ADB-R01—09、11—15及AND-09—13均待验证，旧QR正例R10为N/A。回退限本任务相关patch；不反向覆盖其他工作、不恢复旧ADB组件，不恢复已撤销身份或回滚shell副作用。详细当前合同见[13_REMOTE_ADB](../AI_ENGINEERING/13_REMOTE_ADB.md)。
