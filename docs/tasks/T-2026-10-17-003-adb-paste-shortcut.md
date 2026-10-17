# T-2026-10-17-003：ADB Ctrl+V 与画面参数对齐

## T0—T4：范围、证据与授权

- Status：complete / T8（实现与V0；Git/CI接受回执见最终交付）；2026-10-17；Product Owner：用户；Primary：ashell-master。Flutter、Android、Rust、Network、Security、Release 由当前会话审查，不委派。
- 起点：test/8ef006ed3887075655ec8bc89c2eb138f1e8d619，初始 clean，与 origin/test 一致。前轮 test dispatch 已接受，未查询构建结果。
- Baseline：AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0026 / D031。源码以当前 HEAD 为准。
- C0/C2：检查原剪贴板并接入直接 Ctrl+V；对齐 ADB 与普通画面的缩放/画质/帧率目标，保留独立链路。C1：必要任务与合同增量。C3：完成后提交推送 test，单次 test 编排；不监控。不执行本地产品 build/test/analyze/codegen 或真机操作，不改 main、身份、无障碍和普通采集。
- 证据：普通 Ctrl+V 经 native/Flutter 两种键盘来源汇入 Session.send_key_event；ADB paste 已有独立 scrcpy 控制连接但只接菜单。ADB 额外 960 上限、JPEG 码率等待与普通截图 300ms 采集不同；普通无障碍截图进入视频编码，不能宣称编码或性能完全相同。
- 验收：普通画面与 ADB 画面获得焦点后 Ctrl+V 可走 ADB Unicode 粘贴；无 ADB 保持旧路由；长按不重复、释放无残留；只读/禁用剪贴板/断线不越权；两种键盘来源、多窗口隔离；媒体缩放与原配置对齐、慢接收端不堆积截图。
- Impact：桌面 Session 输入单一分流，PC ADB 状态/粘贴消费者、ADB 视口焦点、Android ADB profile/截图调度、Android ADB media 分发。普通服务、无障碍、剪贴板实现、协议/生成桥、依赖/资产/版本保持。
- 风险 medium；V0 + V2/V3 待证；TEST_MATRIX 的 FLT/AND/ADB 媒体/触摸/多 PC/重连案例，增加直接粘贴与质量对照。无新增长期架构决策；回退仅本次 test 提交。

## T5—T8：实现、验证及交付

- 实现：Session.send_key_event新增仅桌面Flutter生效的ADB快捷键分流；识别legacy/Map/Translate，校验当前session、只读/剪贴板/能力，按键释放和长按不重复；Dart按请求显式读剪贴板并复用paste RPC，不依赖画面源。ADB画面鼠标区域补齐原生抓键和页面焦点；菜单保留编辑用途，复用文本校验。
- 生命周期：每会话至多4项；断线/换设备实例/关闭会话取消待处理请求。文本无shell插值、无日志/落盘。旧端/无ADB保持普通键盘路由。
- 画质复检：原H.264固定1Mbps上限会截断大画面/Best选择的计算码率，移除该额外上限；保留受限尺寸、quality ratio、200kbps下限及发送回执边界。Java fallback同步。此处按既有ADB公式兑现选中等级，不声称与VP8/JPEG等编码同码率同画质；高画质相应需要更高带宽。
- 媒体：删掉独立960 profile及全部调用；恢复原width/350 + 1280边界；JPEG按300ms及低QoS FPS调度、扣除采集耗时，删掉按JPEG长度使用视频码率预算造成的额外等待；JPEG/layout每PC在途4帧，H.264窗口24及8MiB保持。画质等级继续来自普通QoS，独立采集/编码不改为普通视频链。
- V0：按两种键盘入口、释放、菜单焦点、双画面、无共享ADB、会话/实例代次、权限与async释放逐段复检；构造器调用、旧media()/960/旧JPEG预算残留检索、diff空白/范围检查完成。普通服务/无障碍/原剪贴板/协议/生成文件/构建脚本未改。未运行本地build/test/analyze/codegen或设备验证。
- 《编译验证需求》：Build-Clients-New.yml / test / Platform=Both / BuildMode=常规编译；只 dispatch，不监控。真机需覆盖中文/换行/emoji、焦点、两种输入来源、ADB 无普通共享、多 PC、连续粘贴/释放/撤销、同屏画质/旋转/延迟。
- 性能结论以正式设备和相同网络实测为准；不以参数对齐代替流畅度证据。
- STATE-20261017-003 / WORK-20261017-003 / CE-20261017-T003-01；无新 ADR/Decision/资产。
- 最小合同13_REMOTE_ADB/14_ADB_MIRROR及工程记忆已同步；ADR/Baseline/Decision/External Registry/TEST_MATRIX不扩充，沿用原边界与本任务验收案例。最终Git/CI实际回执见交付，不以dispatch接受替代编译通过。
