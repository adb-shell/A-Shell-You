# ADR-0021: Independent scrcpy video and ADB-first touch

- Status：accepted for implementation；Record Type：contemporaneous，2026-10-14。
- Owner：用户；实现：ashell-master及Android/Rust/Network/Flutter领域；D-026 / T-2026-10-14-001。
- Evidence：V0，未编译/真机验证；详细合同见[14_ADB_MIRROR](../AI_ENGINEERING/14_ADB_MIRROR.md)。

## Context

用户要求独立ADB投屏、多PC接收、任意PC全局关闭，并追加ADB触摸优先：有ADB时接管所有远控画面的触摸，无ADB才使用已有无障碍。先前不触碰原输入链的约束由本轮明确补充收窄为“只增加互斥分流与手势释放，不改变原采集/无障碍其他功能”。

## Decision

固定官方scrcpy-server 5.0.1；设备端分别维护video-only及control-only会话。原ADB身份共用，长连接与Shell队列隔离。字段41独立传输视频、状态及投屏窗口触摸，普通画面保留原输入协议，在Android已授权入口按真实Shell状态互斥分流。不开启ADB投屏也可使用ADB触摸。

多PC订阅同一视频源，4帧额度/PC及关键帧重入。最后订阅关闭释放视频，任意控制PC可全局关闭；触摸保持到ADB断开/撤销。手势采用单owner短租约，防止多PC互相抬指；无障碍Service/截图保持，切换仅结束旧手势。UI不代替服务端鉴权，view-only不能启动、停止或注入。

## Alternatives and Consequences

复用原VideoFrame/texture会混淆采集源和waiting；不采用。完整scrcpy桌面程序引入adb.exe/SDL及第二套窗口/网络；不采用。逐MOVE执行shell input开销大且连续手势差；采用scrcpy控制socket。独立PC解码后的JPEG本地桥增加CPU/画质损耗，作为可隔离的初始渲染实现，后续texture优化不可破坏原frame状态。

原采集无需新MediaProjection授权。厂商注入权限、两路编码器并发与慢链带宽共享仍需真机验证，不能用V0宣称可发布。素材许可/来源/hash见EXT-BIN-SCRCPY-001；不新增宿主codec二进制或工具链。

## Compatibility / Rollback / Verification

旧端忽略未知41，新端8秒无订阅回执显示不支持；旧40保持。server协议严格匹配5.0.1。没有存储迁移、版本或品牌变化；回滚事件内新增模块及接入点，保留既有ADB和原三条画面链。ADB-M01—M08 / AND / NET / RST / FLT用例需V2—V4证据；当前NOT_RUN。
