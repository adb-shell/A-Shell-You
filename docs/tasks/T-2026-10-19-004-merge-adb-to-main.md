# T-2026-10-19-004：ADB优化合并main

## T0—T4：授权、基线与方案

- 用户要求test合并主分支、提交说明“完整优化adb投屏”、推送后执行主分支编排；mian/min按实际main处理，沿用不监控要求。Release owner，无代理或新增业务开发；本次请求授权fetch/switch/merge/add/commit/push及单次Actions。
- 起点test/f28055cdbabc4ad73e807b87ea932ce2de3d0533、clean；fetch确认本地/远端test一致，本地/远端main均e3b4454d6ebbf8d6769c383ed471a01da9e94106且为test祖先，无其他活动任务。
- Baseline AS-BL-2026-10-16-4d3c446-adb-render-ui；ADR0025/0026、D031及现有显示名称决定保持。影响为继承test的五项提交：ADB性能与媒体参数、产品显示名称、普通画面恢复、移除功能启动提示；本轮不新增协议/权限/采集/资产或编排配置变更。
- main上使用--no-ff显式合并，以保留原提交及用户指定的合并说明；fast-forward不会形成该合并提交。只补六份最小交付记录，推送origin/main后触发Build-Clients-New.yml，ref=main、Platform=Both、BuildMode=常规编译。
- 不强推、不改写历史、不改版本/签名/依赖、不本地编译或操作设备。回滚须另行授权revert合并提交，以第一父提交为主线；本轮无回滚动作。

## T5—T8：验证与交付边界

- 已无冲突合并；补记录前git diff --cached --exit-code test及git diff --cached --check均通过。提交前核对与test的差异仅本任务及五份记忆索引；源码与工作流保持test版本。
- V0：分支祖先关系、合并树一致性、范围及diff检查。正式Android/Windows构建交由本次Actions，未本地执行；设备覆盖AND-02、FLT-02/04、ADB-M07/M10、WIN-08仍待实测，dispatch接受不代表构建通过。
- 《编译验证需求》：GitHub Actions Build AShell Clients-New/main/Both/常规编译，单次触发，不监控；准确提交SHA、远端一致性及dispatch回执见Git与会话交付。
- STATE/WORK-20261019-004 / CE-20261019-T004-01；Decision/ADR/领域合同/External Asset Registry reviewed-no-change。本记录写于提交前，不预写推送/构建成功；后续交付以工具回执为准。
