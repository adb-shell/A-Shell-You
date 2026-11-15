# T-2026-11-15-001：移除PC右下角UDID显示

## 续接授权：test提交与双端编译

2026-11-15 T001续接：用户明确授权提交并推送本次UDID显示移除到test，然后触发PC与Android常规编译，不监控。起点test/caf0702，远端一致；工作区仅本任务1份Dart源码和9份配套文档。使用Build-Clients-BT.yml（394791509，active），ref=test、Platform=Both、BuildMode=常规编译；先推送核对SHA再单次dispatch，不查询新run/jobs/logs/artifacts。当前active/T7，Git与触发回执待完成。STATE、WORK-20261115-001-r2 / CE-20261115-T001-02。

C3范围：本任务stage/commit/push、既有CI双端常规编译（沿用临时测试签名和Actions产物）；无main写入、版本/生产凭据变化或发布。停止于HTTP受理回执，不监控或宣称编译成功。以下原编辑阶段记录保留其当时边界；本节是最新授权。

## 0. Task Metadata

| Field | Value |
|---|---|
| Task ID / Title | T-2026-11-15-001 / 移除PC右下角UDID显示 |
| Status / Current T-State | verifying / T7（提交与双端构建触发） |
| Requester / Product Owner | 用户 |
| Principal Engineer / Primary Skill | Codex / openworld-flutter-engineer |
| Domain Owners / Security Reviewer / Release Owner | Codex；仅Flutter展示层，无权限或发布动作 |
| Created / Updated | 2026-11-15，Asia/Tokyo |
| Source Branch / HEAD | test/caf0702aac2b3719619ebdf4337be82346d394df |
| Baseline ID | OW-BL-2026-11-07-d9f1119-screen-priority-worktree，Baseline26；实际输入以HEAD为准 |
| Initial Dirty State / Existing User Changes | clean；无活动任务、无未提交用户修改 |
| Risk Level / Required Highest Verification | low / V0 |
| Related ADR / D-ID | ADR-0028/0029与D-035/036保持；无新决策 |
| Related TEST_MATRIX IDs | FLT-07展示/布局、FLT-06账号合同 |
| Related External Asset IDs | N/A — 无外部资产变化或访问 |
| Project State / Registry Revision | STATE-20261115-001-r2 / WORK-20261115-001-r2 |
| Change Event IDs | CE-20261115-T001-01/02 |

## T0. Authority and Baseline

用户明确要求移除PC右下角UDID显示，授权对应UI源码与必要文档修改。当前test/caf0702、clean；相较旧状态test/0e5cc3d已有后续提交，属于可解释drift。当前test版本5.2.1/5.2.1+59及配置保持，不沿用main任务的分支或版本判断。

Confirmation Record：C0观察、C1配套文档、C2该展示块修改已由2026-11-15请求覆盖。C3 Git写入、build/test/analyze/codegen、文件删除/移动、版本/签名/发布、生产/凭据操作均N/A且不执行。非目标为移除内部设备标识、改变登录绑定或Android界面；发现无关用户修改时保护、不覆盖。

## T1. Requirements

PC主窗口右下角不再显示“识别码”标签、UUID值或双击复制入口，不残留加载圈/错误占位。左下角服务状态、服务器切换、启动服务及账号校验保持。源码与静态检查为本轮验收；真实窗口视觉待证。

## T2. Impact Map

main.dart → DesktopTabPage → DesktopHomePage.buildRightPane → ConnectionPage → OnlineStatusWidget。`_buildUserInfo`独占FutureBuilder、UUID读取与复制，右侧Expanded/Padding/Align只承载此组件。

bind.mainGetUuid → generated FRB → src/flutter_ffi.rs::main_get_uuid → get_uuid；user_model.dart另有三个账号调用，本次保留。仅active/tracked手写展示源码一处变化；Android、Rust、协议、存储、API、generated与外部服务不改。无新增状态、timer、订阅或dispose要求；其他资产分类N/A。

## T3. Design

移除私有展示方法及唯一调用它的右侧布局块，保留Row、底栏高度和其他children。相比仅隐藏Text，不残留UUID请求、占位或复制行为。flutter/services.dart仍用于KeyDownEvent/LogicalKeyboardKey，保留导入。

无新ADR/Decision、schema迁移、feature flag或不可逆操作；身份/认证、服务器切换、Android生命周期和开发者优先合同保持。如需回滚，可经后续授权恢复输入HEAD中的两段UI，不更改标识存储。

## T4. Implementation Confirmation

原请求已精确授权该显示移除，包含connection_page.dart及必要记录；无额外确认门，不继承历史提交推送或编译授权。

## T5. Change Record

唯一产品源码为flutter/lib/desktop/pages/connection_page.dart：删除_buildUserInfo和右侧Expanded，合计53行。移除文本、复制与展示专用FutureBuilder加载/错误分支；无新增元素、依赖、版本、桥接、协议、配置或生成产物变更。CE-20261115-T001-01。

## T6. Verification

V0 PASS：与HEAD比对，源码差异精确为两块移除；该页无_buildUserInfo/mainGetUuid/识别码/machineCode残留。showServerProfileSwitch、mainGetConnectStatus、启动服务与开发者快捷键保持；user_model.dart三个mainGetUuid调用、Rust桥接、Android server_page和server_profiles均无差异。git diff --check通过。

V1单测/analyzer、V2编译、V3窗口交互、V4联调、V5生产观测均NOT_RUN，本轮为UI编辑。未为可逆展示删除新增镜像实现的测试；FLT-07视觉结果待证，最高已达V0，不等于运行PASS。

### 编译验证需求（未执行）

- 命令：build.cmd。
- 环境：PC-Build.md规定的Windows构建机与既有DevEnv/DevTool工具链。
- 执行目录：仓库根目录。
- 目标：窄/宽PC主窗口底栏无识别码/UUID/复制区域，状态、服务器选择、启动服务和账号设备校验正常。

## Security / Privacy / Compliance Review

减少标识展示，不改变生成、存储、认证或上传语义，不输出实际设备标识。无新增权限、信任边界、网络、JNI/unsafe、ADB/驱动或许可证变化；相关项N/A — 仅删除展示块。

## Build / Release Review

目标为test工作区源码交付；无编译、签名、打包、安装、上传或发布，版本/依赖/产物名保持。实际窗口与服务未验证，G2/G3无通过结论，历史CI受理记录不作为本轮证据。

## T7. Documentation Delta

updated：03_MODULE_DESIGN、TEST_MATRIX、本任务、PROJECT_STATE/CURRENT_WORK/PROJECT_MEMORY、TASK_HISTORY/CHANGELOG_AI/CHANGE_EVENT_LOG。reviewed-no-change：DECISION_LOG与ADR（无决策增量）、Baseline26（冻结快照）、EXTERNAL_ASSET_REGISTRY（无资产变化）；其他领域/路线图/入口规则/Skills无必要增量。

## T8. Handoff and Closure

授权显示移除完成，V0通过；改动留在test工作区，HEAD不变。未执行Git写入、编译/测试、监控或外部访问。源码范围与文档/记忆同步，无用户改动被覆盖、无新增敏感值或产物；真实窗口视觉待证。T8仅关闭本次源码编辑交付。
