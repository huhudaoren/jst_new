# 任务报告 - F-ANALYSIS-BE 平台运营数据聚合接口

## A. 任务前自检
1. 涉及表：`jst_order_main`、`jst_refund_record`、`jst_user`、`jst_contest`、`jst_enroll_record`、`jst_rebate_ledger`、`jst_channel`、`jst_student_channel_binding`、`jst_rebate_settlement`、`jst_event_partner_apply`、`jst_channel_auth_apply`
2. 涉及状态机：`SM-1`、`SM-2`、`SM-4`、`SM-5`、`SM-6`、`SM-8`、`SM-9`
   说明：`jst_channel_auth_apply.apply_status` 仅用于待办计数，状态机附录未单列编号。
3. 涉及权限标识：`jst:admin:dashboard`
4. 涉及锁名：无
5. 事务边界：本卡全部为只读聚合查询，无新增 `@Transactional`
6. 输出对象字段：
   - `OverviewVO`：`todayOrderCount`、`todayRevenue`、`monthRevenue`、`totalUserCount`、`totalContestCount`、`totalEnrollCount`
   - `TrendPointVO`：`date`、`orderCount`、`revenue`、`enrollCount`
   - `ContestRankVO`：`contestId`、`contestName`、`enrollCount`、`totalRevenue`
   - `ChannelRankVO`：`channelId`、`channelName`、`studentCount`、`totalRebate`
   - `TodoVO`：`pendingContestAudit`、`pendingEnrollAudit`、`pendingRefund`、`pendingWithdraw`、`pendingPartnerApply`、`pendingChannelAuth`
7. 参考样板：
   - 聚合与补零参考 [ChannelDashboardServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelDashboardServiceImpl.java)
   - 注释与结构风格参考 [ParticipantClaimServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java)

## B. 交付物清单
新增文件：
- [AdminDashboardController.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/AdminDashboardController.java)
- [AdminDashboardTrendReqDTO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/dto/AdminDashboardTrendReqDTO.java)
- [AdminDashboardTopReqDTO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/dto/AdminDashboardTopReqDTO.java)
- [OverviewVO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/vo/OverviewVO.java)
- [TrendPointVO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/vo/TrendPointVO.java)
- [ContestRankVO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/vo/ContestRankVO.java)
- [ChannelRankVO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/vo/ChannelRankVO.java)
- [TodoVO.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/jst/vo/TodoVO.java)
- [AdminDashboardMapper.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/mapper/jst/AdminDashboardMapper.java)
- [AdminDashboardService.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/service/jst/AdminDashboardService.java)
- [AdminDashboardServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/service/jst/impl/AdminDashboardServiceImpl.java)
- [AdminDashboardMapper.xml](D:/coding/jst_v1/RuoYi-Vue/ruoyi-admin/src/main/resources/mapper/web/AdminDashboardMapper.xml)

修改文件：
- [admin-tests.http](D:/coding/jst_v1/test/admin-tests.http)

## C. 实现结果
已在 `ruoyi-admin` 增加 5 个平台运营聚合接口，统一挂载到 `/jst/admin/dashboard`：
1. `GET /jst/admin/dashboard/overview`
2. `GET /jst/admin/dashboard/trend`
3. `GET /jst/admin/dashboard/top-contests`
4. `GET /jst/admin/dashboard/top-channels`
5. `GET /jst/admin/dashboard/todo`

实现要点：
- Controller 统一使用 `@PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")`
- 返回统一使用 `AjaxResult.success(...)`
- 聚合 SQL 全部收敛到 `ruoyi-admin` 本地 `Mapper/XML`，未跨模块直接调用其他业务模块 Mapper
- `trend` 使用 Service 层补零，确保返回固定长度的连续 `7/30` 天序列
- 实现时已按当前仓库真实表结构修正历史命名：
  - 使用 `order_status`，不使用过期的 `status` 口径
  - 使用 `apply_status`
  - 使用 `jst_rebate_settlement`，不使用过期的 `jst_withdraw_record`
  - 使用 `net_pay_amount`，不使用过期的 `actual_amount`
- `monthRevenue` 采用“当月支付净额 - 当月完成退款现金额”的净收入口径
- `todayRevenue` 保持“今日已支付净额”口径，不额外抵扣今日退款

## D. SQL 口径对齐
本次实现采用以下固定口径：
- `todayOrderCount`：当天创建且 `order_status <> 'cancelled'`
- `todayRevenue`：当天支付成功订单 `net_pay_amount` 合计，状态口径为 `paid/reviewing/in_service/aftersale/completed/closed`
- `monthRevenue`：当月支付成功订单 `net_pay_amount` 合计减去当月 `jst_refund_record.status='completed'` 的 `actual_cash`
- 总用户数 / 总赛事数 / 总报名数：各自主表 `del_flag='0'` 总数
- `trend`：分别按订单创建、订单支付、报名创建聚合到天，在 Service 层补齐空日期
- `top-contests`：按报名数排序，次排序为总收入，再次排序为 `contestId`
- `top-channels`：按净返点排序，次排序为 `channelId`
- `todo`：读取 6 个 pending 计数，不引入任何状态流转、副作用或事务

## E. 运行中修正
在真实联调阶段发现 2 个需要收口的问题，已一并修复：
1. `top-channels` 初版 SQL 使用 `HAVING coalesce(b.student_count, 0)`，在当前 MySQL 口径下报 `Unknown column 'b.student_count' in 'having clause'`
   处理：改为外层 `WHERE` 条件过滤，复验通过。
2. `trend?days=15` 初版通过异常链返回 `code=500`
   处理：将 `days/limit` 校验前置到 Controller，非法参数现在返回 `AjaxResult.error(400, ...)`

## F. 编译与验收结果
### 1. mvn compile
已执行：
```text
mvn -f D:\coding\jst_v1\RuoYi-Vue\pom.xml compile -DskipTests
```
结果：
```text
[INFO] BUILD SUCCESS
[INFO] Total time: 23.396 s
```

### 2. 运行态接口验收
已实际启动 `ruoyi-admin` 并完成接口请求验证，结果如下：
- `overview`：成功，返回 `todayOrderCount/todayRevenue/monthRevenue/totalUserCount/totalContestCount/totalEnrollCount`
- `trend?days=7`：成功，返回 7 条连续序列
- `trend?days=30`：成功，返回 30 条连续序列
- `top-contests?limit=5`：成功，返回 Top 赛事列表
- `top-channels?limit=5`：成功，返回 Top 渠道列表
- `todo`：成功，返回 6 个待办计数字段
- `trend?days=15`：按预期失败，返回 `code=400`，`msg=days 仅支持 7 或 30`

本轮联调时的实际样例值：
- `overview.monthRevenue = 6691.90`
- `overview.totalContestCount = 24`
- `overview.totalEnrollCount = 18`
- `todo.pendingContestAudit = 4`
- `todo.pendingWithdraw = 7`

## G. HTTP 测试补充
已追加到 [admin-tests.http](D:/coding/jst_v1/test/admin-tests.http)：
- `FA-BE-1` `overview`
- `FA-BE-2` `trend?days=7`
- `FA-BE-3` `trend?days=30`
- `FA-BE-4` `top-contests?limit=5`
- `FA-BE-5` `top-channels?limit=5`
- `FA-BE-6` `todo`
- `FA-BE-7` 非法参数 `trend?days=15`

说明：本卡按要求补的是管理端测试文件 `test/admin-tests.http`，未改过期的 `test/api-tests.http`。

## H. 本卡未做事项
以下内容按任务卡要求未纳入本次实现：
- 未改 DDL
- 未改菜单 SQL
- 未改 fixture
- 未新增锁
- 未新增事务
- 未新增审计日志
- 未实现前端页面接入

## I. 前端后续提醒
本次后端已交付，但前端若要完全消费新能力，还需要后续接入：
1. [dashboard.js](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/api/admin/dashboard.js) 需要改为调用这 5 个新接口
2. [index.vue](D:/coding/jst_v1/RuoYi-Vue/ruoyi-ui/src/views/jst/dashboard/index.vue) 当前只消费 4 个 KPI / 3 个待办，若要吃满本次后端能力，还需继续接入 `trend` 和新增字段

## J. 我做了任务卡之外的什么
严格说，本次只做了很小的“联调收口型修正”，没有扩卡：
- 将 `top-channels` 的 SQL 过滤方式从 `HAVING` 调整为兼容当前库的写法
- 将非法参数返回码从通用异常 `500` 收敛为更明确的 `400`

这两项都属于保证本卡接口可用性和可验收性的必要修正。

## K. 自检清单
- [x] 5 个接口都在 `ruoyi-admin`
- [x] 所有接口都加了 `jst:admin:dashboard` 权限校验
- [x] 聚合 SQL 全在本模块本地 Mapper/XML
- [x] `trend` 已补零，返回连续 7/30 天序列
- [x] 空表/空命中时返回 `0` 或空数组
- [x] 未引入事务、锁、审计日志
- [x] 未改 DDL / 菜单 SQL / fixture
- [x] `test/admin-tests.http` 已补用例
- [x] 全仓 `mvn compile -DskipTests` 通过
- [x] 已完成运行态接口验收