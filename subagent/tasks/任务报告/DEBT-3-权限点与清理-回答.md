# 任务报告 - DEBT-3 权限点与清理

## A. Step 2 自检答题
1. 涉及表：
   - `sys_menu`
   - `sys_role`
   - `sys_role_menu`
   - `jst_points_account`
2. 涉及状态机：
   - 无。本卡只做权限数据补齐、orphan 清理和历史枚举清洗脚本交付，不改业务状态流转。
3. 涉及权限标识：
   - `jst:channel:withdraw:*`
   - `jst:channel:dashboard:*`
   - `jst:event:appointment:*`
   - `jst:event:writeoff:*`
   - `jst:points:mall:goods:*`
   - `jst:points:mall:exchange:*`
   - `jst:points:mall:browse`
   - `jst:points:mall:aftersale:*`
   - `jst:points:center:*`
   - `jst:marketing:coupon:*`
   - `jst:marketing:rights:*`
   - `jst:marketing:campaign:*`
4. 涉及锁名：
   - 无。本卡不涉及 Java 写链路改造。
5. 事务边界：
   - 无。本卡仅交付 SQL 迁移脚本，不新增 Service 事务方法。
6. ResVO 字段：
   - 无。本卡不改任何接口出参。
7. 复用样板：
   - [96-grant-jst-partner-permissions.sql](/D:/coding/jst_v1/架构设计/ddl/96-grant-jst-partner-permissions.sql)
   - [jst_channelMenu.sql](/D:/coding/jst_v1/RuoYi-Vue/gen/ruoyi/jst_channelMenu.sql)
   - [jst_appointment_recordMenu.sql](/D:/coding/jst_v1/RuoYi-Vue/gen/ruoyi/jst_appointment_recordMenu.sql)
   - [jst_mall_goodsMenu.sql](/D:/coding/jst_v1/RuoYi-Vue/gen/ruoyi/jst_mall_goodsMenu.sql)

## B. 交付物清单
新增文件：
- [95-migration-phase-c-permissions.sql](/D:/coding/jst_v1/架构设计/ddl/95-migration-phase-c-permissions.sql)
- [95-migration-unify-owner-type.sql](/D:/coding/jst_v1/架构设计/ddl/95-migration-unify-owner-type.sql)

未修改文件：
- [23-基础数据初始化.sql](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql)

清理结果：
- `D:\coding\jst_v1\test\temp_test_apply_patch.txt`：当前工作区不存在，核查后无需删除
- 仓库 `temp_* / test_*.tmp / *.orig / *.bak` 搜索结果：未发现其他匹配 orphan 文件

## C. 测试库验证结果
执行环境：
- 数据库：`jst_new`
- 验证方式：JDK 17 + 本地 Maven 仓库 `mysql-connector-j` 直连测试库

角色现状核查：
- `admin`：存在，`role_id=1`
- `jst_student`：存在，`role_id=100`
- `jst_partner`：存在，`role_id=101`
- `jst_channel`：存在，`role_id=102`
- `jst_platform_op`：测试库不存在

因此本次迁移脚本已做兼容：
- 平台角色绑定优先使用 `jst_platform_op`
- 若不存在则自动回落 `admin`

权限迁移幂等验证：
- 第 1 次执行：`totalUpdated=131`
  - `sys_menu` 新增 `60` 条
  - `sys_role_menu` 新增 `71` 条
- 第 2 次执行：`totalUpdated=0`
- 迁移后 `menu_id 9500~9611` 实际存在 `60` 条，和脚本内权限点数量一致

迁移后角色绑定统计：
- `admin`：新增绑定 `34` 条
- `jst_channel`：新增绑定 `16` 条
- `jst_student`：新增绑定 `21` 条

Part C 影响评估：
- 当前测试库 `jst_points_account.owner_type='student'` 行数：`1`
- [95-migration-unify-owner-type.sql](/D:/coding/jst_v1/架构设计/ddl/95-migration-unify-owner-type.sql) 已写好但**本卡未执行**
- 该脚本必须在**后端停服维护窗口**手动执行，避免和旧兼容写入并发冲突

## D. 任务卡差异与兼容说明
本次在脚本里额外兼容了 2 个源码已使用、但任务卡原文未列出的权限点：
- `jst:channel:withdraw:my`
  - 原因：[WxChannelWithdrawController.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/wx/WxChannelWithdrawController.java) 的汇总/列表/详情接口使用的是 `withdraw:my`
- `jst:event:writeoff:list`
  - 原因：[WxWriteoffController.java](/D:/coding/jst_v1/RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/controller/wx/WxWriteoffController.java) 的 `/records` 当前权限表达式仍然使用 `writeoff:list`

说明：
- 任务卡写的是 `writeoff:records`，脚本同时补了 `records` 和 `list`
- 这样既满足任务卡，也不破坏当前源码运行态

## E. Part D 基线脚本审计结论
结论：
- 未再发现新的 `menu_check_strict` 这类字段拼写错误
- 但发现 3 条需要记录的完整性问题，本卡按要求只列不修

问题 1：
- [23-基础数据初始化.sql:177](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql:177) 到 [23-基础数据初始化.sql:178](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql:178) 把 `role_id=100` 固定写成 `jst_platform_op`
- 但当前测试库实际 `role_id=100` 是 `jst_student`，且不存在 `jst_platform_op`
- 这说明 23 基线里的固定角色 ID 策略在非全新库上存在语义冲突风险

问题 2：
- [23-基础数据初始化.sql:177](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql:177) 到 [23-基础数据初始化.sql:180](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql:180) 只初始化了 `jst_platform_op / jst_partner / jst_channel`
- 缺少当前项目已广泛使用的 `jst_student`
- 这会导致依赖 `@ss.hasRole('jst_student')` 的小程序接口，在纯基线库上需要额外补角色数据

问题 3：
- [23-基础数据初始化.sql:173](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql:173) 的注释仍假设“下级菜单由代码生成器自动写入 sys_menu”
- 但阶段 C 大量接口是手写 Controller，并不会自动生成权限菜单
- 本卡新增的权限缺口，本质上就是这条假设失效后的完整性债务

## F. 我做了任务卡之外的什么
1. 额外补了 `withdraw:my` 与 `writeoff:list` 两个兼容权限点，因为当前源码真实依赖它们。
2. 额外把平台角色绑定做成了 `jst_platform_op -> admin` 的回落逻辑，因为测试库不存在 `jst_platform_op`。
3. 额外做了测试库双跑验证，并把实际新增条数写入报告，确保不是“只写脚本未验证”。

## G. 自检确认
- [x] 未修改 [23-基础数据初始化.sql](/D:/coding/jst_v1/架构设计/23-基础数据初始化.sql)
- [x] 已新增 [95-migration-phase-c-permissions.sql](/D:/coding/jst_v1/架构设计/ddl/95-migration-phase-c-permissions.sql)
- [x] 已新增 [95-migration-unify-owner-type.sql](/D:/coding/jst_v1/架构设计/ddl/95-migration-unify-owner-type.sql)
- [x] 权限迁移已在测试库执行两次，验证幂等通过
- [x] `owner_type` 清洗脚本已交付，但未在本卡执行
- [x] 已核查 `owner_type='student'` 真实影响行数
- [x] 已核查 orphan 文件，目标文件不存在，未发现其他匹配项
- [x] 已把 23 基线的完整性问题列入报告

## H. 遗留 TODO
1. 如果后续要让“全新库”直接具备当前小程序权限模型，建议后续单独补一张 role migration，把 `jst_student / jst_platform_op` 的基线角色模型统一掉。
2. [WxCampaignController.java](/D:/coding/jst_v1/RuoYi-Vue/jst-marketing/src/main/java/com/ruoyi/jst/marketing/controller/wx/WxCampaignController.java) 当前仍用 `jst:marketing:coupon:my` 作为权限兜底，而不是 `jst:marketing:campaign:list/query`；本卡只补数据库权限，不改 Java。
