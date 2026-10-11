# T-2026-10-12-006：统一 Windows / Flutter / Android 产品图标

## 0. Task Metadata

- Status / T-State：complete / T8，资源与V0交付；Created / Updated：2026-10-12。
- Requester / Product Owner：当前用户；Principal / domain / Release reviewer：Codex，ashell-master、ashell-release-engineer、ashell-flutter-engineer；无子代理。
- Source：test / c9b7cd135be838989953e7ab7037f8c6d8d6aa31；initial worktree clean。
- Baseline：AS-BL-2026-10-12-c0951b5-pc-build-worktree，见 ../BASELINE/BASELINE_INDEX.md；该历史变更已在当前 c9b7cd1 固化，旧快照不改写。
- Recovery：STATE-20261012-006 / WORK-20261012-010；state drift 可解释为 T005 已提交；无 active task / conflict，旧任务授权不继承。
- Risk：low；required/achieved verification：V0；WIN-08、AND-08、FLT-02/07 仅登记相关正式验收，NOT_RUN。
- ADR / Decision：沿用 ADR-0014；N/A — no decision delta，不新建长期架构决定。
- External assets：用户本次提供的图像；转换后图片进入仓库，不增加运行时外部依赖。源图授权/版权来源由用户负责提供，不作额外许可认定。
- Event：CE-20261012-T006-01；目标关闭状态 STATE-20261012-007 / WORK-20261012-012。

## T0—T1. Authority and Requirements

- 当前明确授权：按上一轮方案，从用户原 PNG 生成并覆盖 Windows 主程序、自解压、托盘、Flutter 界面角标、Android 桌面图标及源图；保持原图设计。
- C0：读取、图像元数据及资源引用检查；C2：上述资源及必要加载回退/ignore配置调整；文档记忆按仓库协议同步。
- 图像尺寸/格式转换属于当前明确请求；不执行生成式重绘，不去除边缘/细节，不重新设计标志。
- 禁止：产品编译、build/test/analyze/codegen、依赖安装、实际打包/签名、设备/服务启动、Git写入/分支切换、删除移动、版本调整、上传发布。
- Non-goals：通知小图标、Android悬浮球、横向Logo、自解压动画，以及Apple/Linux/web专有资源。
- Acceptance：所有指定生效路径使用同源设计，尺寸与透明通道正确，ICO多分辨率可解码；Flutter加载失败不再回退旧SVG；资源不被ignore遗漏。

## T2. Impact Map and Evidence

| Domain | Entry / source | Change / classification |
|---|---|---|
| Windows | flutter/windows/runner/Runner.rc → resources/app_icon.ico | 主程序图标；tracked/generated image |
| Portable | libs/portable/build.rs → res/icon.ico | 自解压EXE图标；tracked/generated image |
| Tray | src/tray.rs::load_icon_from_asset / make_tray | 优先Flutter assets/icon.png，备用res/tray-icon.ico；不改Rust |
| Flutter | flutter/lib/common.dart::loadIcon，desktop tabbar/server page | 主PNG及独立同源备用PNG；旧icon.svg保留但不再由loadIcon引用 |
| Android | application/Launcher icon=@mipmap/ic_launcher；anydpi-v26 XML | 5密度普通/备用圆形/自适应前景共15PNG；保留白色背景和monochrome复用语义 |
| Source | res/icon.png；flutter/pubspec.yaml::flutter_icons | 同步1024源图；构建不会自动重新生成所有输出 |
| Git discovery | 根.gitignore的*png | 只为两个新Flutter资产增加例外；不执行git add |
| Runtime/security | FFI/JNI/网络/API/权限/持久化/凭据 | N/A，无相应变更 |

源文件实际为1254×1254 RGBA PNG，并非文字描述的1024×1024；SHA-256：88370d954e6a89be5e8373c7e02ebe322fb65f339a8d9416c22ab822ed9871d4。只保存来源摘要，不保存用户绝对路径。原文件不修改。

## T3—T4. Design and Confirmation

- 当前请求即精确范围实现确认，不重复索要批准。任何新增平台、重绘或正式构建另行定界。
- 确定性等比缩放；Windows三个ICO包含16/20/24/32/40/48/64/128/256；Flutter主图/源图1024、备用图256。
- Android普通与备用圆形沿用48/72/96/144/192尺寸；圆形备用保留原图白色圆角方形设计，不额外裁圆。前景画布108/162/216/324/432，原图等比放入中心66/99/132/198/264区域，保持透明留白，XML白色背景不变。
- 不能从位图无损恢复真实SVG路径；不自动描摹。loadIcon改用独立同源PNG回退，两图均失败时保留相同尺寸空位，不加载旧标志。旧SVG不删除。
- 不新增依赖；pubspec已有assets/目录声明。共有Flutter角标消费者都会使用新图，Apple/Linux专有图标未改。
- Rollback：如未来获准，相对c9b7cd1逐文件恢复本任务差异及移除新增资源；本次不执行Git回退/删除。

## T5. Change Record

使用本机现有 System.Drawing 做32位RGBA、高质量bicubic等比缩放；无自动描摹、生成式重绘、背景清理、裁切或依赖安装。ICO容器保存9个PNG压缩帧。全部目标路径限定在仓库内，未修改原始文件。

### 图片资源（21个）

| Path | Output |
|---|---|
| res/icon.png | 1024×1024 PNG，源图 |
| flutter/assets/icon.png | 1024×1024 PNG，新增，和res/icon.png字节一致 |
| flutter/assets/icon-fallback.png | 256×256 PNG，新增，独立同源回退 |
| res/icon.ico | 16/20/24/32/40/48/64/128/256，各32-bit RGBA |
| res/tray-icon.ico | 同上，字节一致 |
| flutter/windows/runner/resources/app_icon.ico | 同上，字节一致 |
| flutter/android/app/src/main/res/mipmap-mdpi/ic_launcher.png | 48×48 PNG |
| flutter/android/app/src/main/res/mipmap-mdpi/ic_launcher_round.png | 48×48 PNG |
| flutter/android/app/src/main/res/mipmap-mdpi/ic_launcher_foreground.png | 108×108 PNG，内容66×66 |
| flutter/android/app/src/main/res/mipmap-hdpi/ic_launcher.png | 72×72 PNG |
| flutter/android/app/src/main/res/mipmap-hdpi/ic_launcher_round.png | 72×72 PNG |
| flutter/android/app/src/main/res/mipmap-hdpi/ic_launcher_foreground.png | 162×162 PNG，内容99×99 |
| flutter/android/app/src/main/res/mipmap-xhdpi/ic_launcher.png | 96×96 PNG |
| flutter/android/app/src/main/res/mipmap-xhdpi/ic_launcher_round.png | 96×96 PNG |
| flutter/android/app/src/main/res/mipmap-xhdpi/ic_launcher_foreground.png | 216×216 PNG，内容132×132 |
| flutter/android/app/src/main/res/mipmap-xxhdpi/ic_launcher.png | 144×144 PNG |
| flutter/android/app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png | 144×144 PNG |
| flutter/android/app/src/main/res/mipmap-xxhdpi/ic_launcher_foreground.png | 324×324 PNG，内容198×198 |
| flutter/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png | 192×192 PNG |
| flutter/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png | 192×192 PNG |
| flutter/android/app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png | 432×432 PNG，内容264×264 |

### 引用与配置（2个）

- flutter/lib/common.dart：loadIcon先icon.png，再icon-fallback.png；两者失败时SizedBox保持布局。Flutter内部package及其导入不变。
- .gitignore：仅放行上述两个新增Flutter PNG；其他图片忽略策略不变。

### 文档与记忆（9个）

- docs/tasks/T-2026-10-12-006-product-icons.md（本文件）。
- docs/AI_ENGINEERING/08_BUILD_SYSTEM.md。
- PROJECT_START_HERE.md。
- .codex/PROJECT_MEMORY.md。
- .codex/PROJECT_STATE.md。
- .codex/CURRENT_WORK.md。
- .codex/CHANGELOG_AI.md。
- .codex/CHANGE_EVENT_LOG.md。
- .codex/TASK_HISTORY.md。

总计32个文件；其中两个Flutter PNG与本task为新增，其余为修改。未删除/移动文件。

## T6. Verification

- V0：18个PNG经独立Pillow读取尺寸、RGBA及alpha范围0—255全部通过；3个ICO的目录、边界和全部27个子帧独立解码通过。三ICO SHA-256一致：b836ec0b29047a49633d3cd1e77a0b25c1b641b210028918904c4c72420121b0。
- V0：5套Android前景的非透明像素限定在对应居中内容区；普通/备用圆形图同源同尺寸。源图与Flutter主图字节一致；原用户输入SHA-256未变。
- V0：实际查看了256界面备用图和432 Android前景图；原图圆角方形、黑色机器人、白色纹理及边缘细节保留，Android仅增加透明留白。
- 补充解码探针：System.Drawing的多帧Icon按16—128尺寸选择成功，请求256时该选择器返回128；将原ICO内256帧单独装入内存ICO后可解码为256。不能把此选择器当成Windows Shell显示验证，也未据此修改有效ICO尺寸。实际EXE资源及Shell缩放仍属NOT_RUN。
- V0：资源文件存在、pubspec assets/声明、Runner.rc/portable build.rs/tray路径、Android XML引用以及两个PNG的git ignore例外已核对；loadIcon没有旧SVG回退。diff whitespace与最终32文件范围检查作为交付关闭检查。
- V1—V5：NOT_RUN，用户禁止编译；未执行Flutter analyzer/widget tests、Cargo/Gradle、产品脚本或运行验证。
- 《编译验证需求》（仅登记，不执行）：正式Windows环境在仓库根运行build.cmd，核对主EXE/自解压/托盘/窗口角标；正式Linux Android环境在仓库根运行./build.sh 1及./build.sh 2，核对APK桌面图标、圆形mask、Android8+自适应及主题模式。WIN-08 / AND-08 / FLT-02/07均待正式授权和环境验证。

## T7—T8. Documentation and Handoff

- 已同步：08_BUILD_SYSTEM、PROJECT_START_HERE、PROJECT_MEMORY/STATE、CURRENT_WORK、CHANGELOG_AI、CHANGE_EVENT_LOG、TASK_HISTORY。
- ADR / Decision Log / toolchain baseline / External Asset Registry / TEST_MATRIX：评估无新增架构、工具链或外部运行资产；本任务记录图像来源与验收要求，无正式测试结果可登记，不改原历史快照。
- 不改变Git、构建流程、版本、签名、协议、包名、权限或用户文件；只有图像转换及对应源文件更新。
- 外观限制：同源位图缩小会有正常采样差异；Android主题图标仍按既有monochrome层复用前景，系统着色/圆形mask/启动器缓存待设备验证，未宣称主题模式保持原始黑白色。旧icon.svg文件保留，无loadIcon引用。
- Git/build/test/analyze/project-codegen/install/package/sign/upload/release：全部none。当前修改未提交；后续构建或Git写入均需要新的明确请求。
- Completion：21图片+2加载/ignore配置+9文档；原请求静态交付完成，不表示EXE/APK已经更新或编译通过。
