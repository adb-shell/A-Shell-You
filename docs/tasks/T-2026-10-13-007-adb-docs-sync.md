# T-2026-10-13-007 — ADB 界面与合同文档同步

按 TASK_TEMPLATE.md 的T0—T8压缩记录；2026-10-13（Asia/Tokyo）。本文为交付前快照，Git/CI精确回执以最终答复为准，不预报构建成功。

## 0. Metadata

| 字段 | 值 |
|---|---|
| Status / T-State | 文档同步完成，T8交付；正式构建/运行待验 |
| Requester / Owner | 当前用户 / Codex ashell-master；Flutter/Android/Network/Security/Release静态自审，无子agent |
| Source | main/efb2599d05ba5b3a08a9c63ee00bec20e2ce9c5b |
| Initial dirty | PC remote_adb_controller.dart、remote_adb_menu.dart、Android local_adb_page.dart；均来自本会话先前明确实现请求 |
| Baseline | AS-BL-2026-10-13-efb2599-adb-ui-worktree，Baseline14；旧Baseline13冻结 |
| Risk / Verification | 文档低风险，远程ADB仍有高权限边界；文档V0，产品V2/V3/V4待验 |
| ADR / Decision | ADR-0020部分取代0019；D-024 |
| Matrix | ADB-R01—09、11—15，AND-09—13；旧R10=N/A；原链AND-01/02/04/05/07/08、FLT-04/05/06 |
| External assets | EXT-LIB-ADB-004—007保持；旧EXT-BIN-ADB-001、EXT-REF-ADB-002/003不恢复 |
| State / Registry / Event | STATE-20261013-007 / WORK-20261013-014 / CE-20261013-T007-01 |

## T0—T1. Authority and requirements

用户先前要求代码优先、暂缓文档；本轮明确“现在可以同步修改文档了”，随后追加“修并同步后，提交到github并执行编排构建，不监测构建状态”。允许C0只读、C1文档与记忆、C3提交既有代码/本轮文档并推送main、单次Build AShell clients both/aarch64 dispatch。不新增产品逻辑，不运行本地build/test/analyze/codegen，不操作设备、删除、版本、签名、发布、生产或secret，不查询CI运行状态。

验收：当前说明不再承诺QR或PC三秒同步；模式/端口/按钮条件与请求取消/时限准确；已提交与工作树分开；保留历史；三份源码hash在文档同步期间不变。既有dirty不得恢复或覆盖。

## T2. Impact and evidence

| 范围 | 当前事实 / 文档影响 |
|---|---|
| Android Java + Rust | efb2599移除QR参数/等待、拒绝未知参数，NSD丢失清理、按transport使用情况取消、部分成功提示；PC等待300秒/路由310秒 |
| Flutter PC | dirty去周期status/开窗预扫描；模式控件分开；连接按钮ElevatedButton，真实启用条件保持 |
| Flutter Android | dirty按模式显示输入、焦点/key和说明；前台三秒查询保持 |
| Network/security | Android拥有身份/连接，多PC显式查询同一状态；原授权/只读gate、回执origin、加密/relay保持 |
| Windows/build | 无native Windows、版本、依赖、生成桥或工作流变更 |
| API/其他链 | 无HTTP/account、Accessibility、视频/输入/terminal/ZEGO改动 |
| 文档/资产 | domain12/13、安全/构建、入口、测试、ADR/基线/恢复记忆；外部资产仅消费状态，无下载或新增依赖 |

源码/当前文档为tracked active；旧ADR、Baseline与任务为historical；ROM/产物为verification-required。带serviceName的旧客户端收到invalid；普通pair/connect、字段40/JSON v1/apiVersion2/FRB和身份record schema保持。

## T3—T4. Design and confirmation

采用当前说明更新、旧历史保留、新ADR/基线/事件补录；不把旧任务整体重写为新功能，不删除其他页面使用的qr_flutter。ADR0020补录用户明确取舍，未新增产品决定。Baseline14记录三个SHA-256用于保护源码；无数据迁移。回退仅相关patch，不覆盖其他任务；不能恢复已撤销身份或撤销shell副作用。用户已追加提交/推送/单次CI授权，不重复确认。

## T5. Change record

本轮仅Markdown；精确路径见CE-20261013-T007-01。交付一并包含先前三份Dart改动，不能描述为本轮追加源码修改。

- 12/13：模式、端口来源/留空、直接连接、无QR、刷新差异、参数/取消/预算/兼容。
- 08/10/外部登记：构建证据边界、权限保持、二维码依赖消费退役。
- TEST_MATRIX：修正R02/R08，R10=N/A，新增R12—15。
- ADR0020/D024/Baseline14：已发生的决定与源码hash；0019只补后续关系，Baseline13冻结。
- 入口与七份.codex记忆：当前状态、证据与交付权限。

## T6. Verification

| 等级 | 执行与结果边界 |
|---|---|
| V0 | 源码/文档对照、残留当前QR/PC三秒承诺检查完成；diff空白通过，21份文档121个本地链接无断链，三份源码SHA-256与同步前相同 |
| V1 | NOT_RUN：无项目analyzer/unit/codegen |
| V2 | 无当前源码成功结果；历史efb2599仅HTTP204 dispatch接受；本轮交付后按授权再触发一次，不监控 |
| V3/V4 | NOT_RUN：无新包/设备操作，模式、多PC、Release/JNI与原链回归待验 |
| V5 | N/A：无生产/发布任务 |

《编译验证需求》：提交推送后执行GitHub build-clients.yml workflow_dispatch，ref=main、platform=both、android_arch=aarch64；沿用Rust1.75/Flutter3.24.5/JDK17/NDK27，只发起、不读取状态。未来需对应结果证明Windows/Android构建、模式与手动刷新、无QR及原链回归；不构成本地build授权。

## Security / release

主题色不等于授权；状态失败标未确认、只读仍拒绝。无码/命令/凭证落文档，无重放；超时不能证明未执行。取消只影响owner未完成任务，但关闭其使用的共享transport可能影响其他PC。状态快照非实时存活证明。固定依赖hash/NOTICE/SBOM与ROM验证待补，无发布准备结论升级。

## T7—T8. Handoff

domain、ADR、基线、测试、外部登记和恢复记忆已同步；旧历史保留、源码hash保护。最终工作树为三份既有Dart改动加本轮文档，准备按用户要求提交/推送并单次dispatch。

已发生事实：efb2599已推送、上次HTTP204接受已确认；本轮新提交SHA、push和dispatch是否接受，只在工具成功后于最终答复确认。构建不监控、不预写PASS；无本地项目执行、设备、删除、版本、发布或生产动作。
