# T-2026-10-12-003：完成品牌文字与文档命名统一

- Status：complete / T8 — source/V0 only；Owner：当前用户；执行：Codex / ashell-master、API静态审查。
- Source：test / a036f31f80baeb6cd68bcf169f9e9fa8db39fe76；初始含T002迁移的未提交修改，保持已有内容，仅追加本轮明确改动。
- Baseline：AS-BL-2026-10-12-a036f31-worktree；ADR-0014 / D-015；本任务为用户进一步明确命名范围。
- Authority：当前用户明确要求各大小写旧产品文字全部更换，含两处截图所示匹配、文档字段、历史文档/文件名和引用；明确保留flutter_hbb的既有指示持续有效。
- Impact：src/common.rs::is_public、src/hbbs_http/sync.rs::heartbeat_url按新ashell.字符串识别；本地字符串变化不证明该域名存在或外部服务已更新。不新增网络地址或运行服务。
- Documents：旧报告、baseline/ADR/log的品牌术语和路径按当前命名展示；统一标注文本已规范化，原文和真实历史源仓地址保留在a036f31的Git记录，不能虚构新的上游仓地址。
- Renames：四份旧品牌命名Markdown文件更名为ASHELL_/ashell对应名，引用同步；只改当前文件系统，不写Git索引或历史。
- Verification：全文/文件名大小写不敏感扫描零命中（排除.git）、两处谓词为ashell.、Flutter包名/import保留、文档本地链接无缺失目标、diff whitespace通过。TEST_MATRIX参考API-04/API-05/RST-01/FLT-05；仅V0，不执行项目测试。
- Forbidden：编译/测试/analyzer/codegen、安装、Git写入、发布/部署、签名和真实服务访问。
- T6—T8：静态复核、迁移报告/ADR-0014/D-016、State/Memory、Task/Event/Changelog同步完成；最终STATE-20261012-004 / WORK-20261012-006 / CE-20261012-T003-01。没有提交或外部操作。
- 编译验证需求：沿用迁移报告正式环境入口，未执行；两处分类规则实际API/heartbeat影响需正式隔离环境验证。源码语法只改字符串，不以静态检查证明运行成功。
