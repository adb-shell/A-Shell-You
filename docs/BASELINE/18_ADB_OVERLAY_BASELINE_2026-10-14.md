# AS-BL-2026-10-14-941b215-adb-overlay-worktree

- 观察：main/941b2156531baacafd16e343452a9de8fc2b8bdb，初始clean；T-2026-10-14-003工作树未提交。
- 前轮941b215已推送；Build AShell clients both/aarch64获204、未监控；不能推出编译通过。
- Delta：主界面全宽状态悬浮、双图向中线靠拢；ADB控件/私有连接有界恢复；独立shell UID遮罩、协议41字段17—19、多PC全局开关。
- 原Android MainService/Accessibility/采集JNI/输入分流保持，PC仅展示坐标适配；字段40/品牌/版本/构建入口/依赖保持。
- helper由现有Java8/Android构建编入APK，有独立main与R8 keep；不额外下载编译server，不变scrcpy v5.0.1资产/hash。Android11+；OEM隐藏API实测待证。
- Evidence：V0；ADB-M01—M16正式构建/运行均未在本轮执行。无Git写入、CI、产品build/test/analyze/codegen。
