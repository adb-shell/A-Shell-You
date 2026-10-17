# T-2026-10-17-002：test 分支 ADB 远程输入与画面传输

## T0—T4：范围与授权

- Status：complete / T8（实现及V0；Git/CI接受回执见交付）；日期 2026-10-17；用户为 Product Owner。
- Primary：ashell-master；Android / Flutter / Rust / Network / Security / Release 由当前会话分别审查，不委派。
- 起点：main/af1c72d，初始 clean；本地领先 origin/main 一条既有 CI 文案提交，完整保留；已创建并切换 test。
- 基线：AS-BL-2026-10-16-4d3c446-adb-render-ui / ADR0026 / D031；实际代码以 af1c72d 为准。
- 授权：读取、修改独立 ADB 输入与画面模块、必要工程记录；创建 test、提交并推送 test；单次编排编译 test，不监控。无本地 build/test/analyze/codegen、设备操作、版本/签名/发布授权。
- 边界：普通共享、无障碍、普通输入与剪贴板同步、产品身份及协议字段不修改。
- 验收：PC 显式读取剪贴板并经 ADB 粘贴 Unicode 文本；独立输入不依赖投屏开启；优化 ADB 帧传递和截图开销，保持画面物理尺寸/坐标及多 PC 独立订阅。
- 证据：PC 解码后每帧 base64→JSON→base64 解码；截图每帧系统 PNG 编码后再解码/缩放/JPEG。当前截图间隔已扣除采集耗时，不重复修复。
- 方案：现有 scrcpy 5.0.1 控制通道增加有界粘贴及 ACK；PC 原生二进制帧交接；截图优先读取系统原始像素，兼容回退 PNG。保留普通录屏比例/画质，避免单纯加码率。
- 风险 medium；目标 V0 + CI 编译及 Android/Windows 真机验证，未执行部分不宣称通过。相关 TEST_MATRIX：ADB 本地/远端配对、触摸、媒体、多 PC、撤销/重连；新增 Unicode 粘贴、取消、原始截图格式/旋转检查。
- 不增加依赖或外部资产；参考 scrcpy v5.0.1 ControlMessageReader/Controller 与 AOSP Android 11/15 screencap 源码，独立实现适配。
- 回退：仅回退本次 test 提交；无生产状态或数据迁移；不得改写 main。

## T5—T8：实现与验证

- 输入：菜单独立输入弹窗；16 KiB UTF-8及JSON边界；Android能力协商、控制权限保持；4项队列、10秒ACK、按owner/request取消、断线失败且不重放。沿用触摸连接，与媒体/命令隔离。libadb 3.1.1大写入分片不等待READY，新粘贴固定<=4096字节逐片write/flush。
- 媒体：PC移除base64/JSON大帧；C ABI所有权成对释放，状态/帧代次核对。每PC在途/解码窗口24帧、发送侧8 MiB保留；8帧在30FPS时仅容纳约267ms回执往返，是本次修复的中继停顿来源之一。
- 分辨率：保留width/350及物理坐标，投屏/截图最长边960，穿透和黑屏绘图profile不改；截图原始RGBA/RGBX/RGB565格式/色域/行尺寸验证，24 MiB原始缓冲上限、运行结束回收，格式不兼容回退PNG。小帧目标上限12FPS，保留码率预算及扣除采集时间的调度。
- V0：源码逐段审查通过；12个源码文件范围检查、Rust/Dart ABI符号对应、旧base64通路清除、UTF-8/NUL检查、diff空白检查通过；修复复检发现的Java checked exception声明。未运行产品编译/测试/分析器/代码生成/真机。
- 《编译验证需求》：GitHub Actions `Build-Clients-New.yml`，ref=test，Platform=Both，BuildMode=常规编译；正式Ubuntu/Windows hosted runners按工作流编译。用户已授权单次dispatch，不监控。编译结果未知。
- 真机待验：中文/换行/emoji/空格/超长文本/拒绝粘贴输入框；无画面时输入；多PC取消/断线；投屏与普通画面并开/独立关闭；截图静态/滚动/旋转、PNG兼容回退、RGB565/P3、内存及中继不同RTT下帧率/延迟。尚无“与普通录屏一样流畅”的实测证据。
- 安全/ABI/发布审查：文本无shell插值、无日志/落盘；保留远端控制鉴权；buffer生命周期和owner/generation隔离；无依赖/资产/版本/签名/生成桥接变更。普通服务/Kotlin/采集JNI/无障碍/主输入及剪贴板代码零差异。
- 最小合同同步13_REMOTE_ADB/14_ADB_MIRROR及五份工程记录；Decision/ADR/Baseline/External Registry N/A — 延伸既有独立ADB边界，无新依赖或长期架构边界变化；旧基线不覆写。后续性能结论须基于正式实测。
- STATE-20261017-002 / WORK-20261017-002 / CE-20261017-T002-01。保留af1c72d既有提交；仅推送test，不推main。最终commit和dispatch接受回执见交付。
