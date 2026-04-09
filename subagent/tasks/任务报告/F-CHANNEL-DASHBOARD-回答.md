# 任务报告 - F-CHANNEL-DASHBOARD 渠道工作台

## A. 任务前自检
1. 涉及表：`jst_student_channel_binding`、`jst_user`、`jst_order_main`、`jst_rebate_ledger`、`jst_channel`、`jst_contest`
2. 涉及权限标识：`jst:channel:dashboard:monthly`、`jst:channel:dashboard:students`、`jst:channel:dashboard:orders`、`jst:channel:dashboard:stats`
3. 涉及状态口径：
   `本渠道学生 = jst_student_channel_binding.channel_id = currentChannelId AND status='active'`
   `本渠道订单 = 绑定学生 user_id 的订单，不按 jst_order_main.channel_id 判断`
   `本渠道返点 = jst_rebate_ledger.channel_id = currentChannelId`
4. 事务边界：本任务均为读接口，无新增事务方法
5. 分页边界：`students/orders` 均遵守 “先 lookup 当前渠道，再 `PageUtils.startPage()`，最后执行目标分页 SQL”
6. 出参 VO：`DashboardMonthlyVO`、`DashboardStudentVO`、`DashboardOrderVO`、`DashboardStatsVO`
7. 复用样板：[ChannelWithdrawServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelWithdrawServiceImpl.java)

## B. 交付物清单
新增文件：
- [ChannelDashboardQueryDTO.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/ChannelDashboardQueryDTO.java)
- [DashboardMonthlyVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/DashboardMonthlyVO.java)
- [DashboardStudentVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/DashboardStudentVO.java)
- [DashboardOrderVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/DashboardOrderVO.java)
- [DashboardStatsVO.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/DashboardStatsVO.java)
- [UserChannelBindingLookupMapper.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/UserChannelBindingLookupMapper.java)
- [OrderMainLookupMapper.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/OrderMainLookupMapper.java)
- [ChannelDashboardMapperExt.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/ChannelDashboardMapperExt.java)
- [ChannelDashboardService.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/ChannelDashboardService.java)
- [ChannelDashboardServiceImpl.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelDashboardServiceImpl.java)
- [WxChannelDashboardController.java](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/wx/WxChannelDashboardController.java)
- [UserChannelBindingLookupMapper.xml](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/lookup/UserChannelBindingLookupMapper.xml)
- [OrderMainLookupMapper.xml](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/lookup/OrderMainLookupMapper.xml)
- [ChannelDashboardMapperExt.xml](D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/ChannelDashboardMapperExt.xml)

修改文件：
- [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql)
- [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http)

## C. 实现结果
已实现 4 个 wx 接口：
1. `GET /jst/wx/channel/dashboard/monthly`
2. `GET /jst/wx/channel/students`
3. `GET /jst/wx/channel/orders`
4. `GET /jst/wx/channel/dashboard/stats`

实现要点：
- 当前渠道解析沿用提现模块的 `requireCurrentChannelId()` 口径，要求渠道已审核通过且启用。
- `students/orders` 查询严格基于 `active binding`，没有偷用 `jst_order_main.channel_id`。
- 月度统计中：
  `newStudentCount` 按绑定生效时间 `bind_time` 统计；
  `orderCount` 按订单创建时间 `create_time` 统计；
  `orderPaidAmount` 按支付时间 `pay_time` 统计已支付订单金额；
  `rebateEstimatedAmount` 按 `jst_rebate_ledger.accrual_time` 统计正向 `withdrawable/in_review/paid`。
- 趋势图按天补零，保证 `last7d/30d/90d` 返回固定长度数组。

## D. mvn compile 结果
```text
[INFO] BUILD SUCCESS
[INFO] Total time: 25.041 s
```

说明：已执行全仓 [pom.xml](D:/coding/jst_v1/RuoYi-Vue/pom.xml) 的 `mvn compile`，18 个模块全部通过。

## E. .http 测试补充
已追加到 [wx-tests.http](D:/coding/jst_v1/test/wx-tests.http)：
- `F-CD-0` 登录渠道用户 9201
- `F-CD-1` 当月月度汇总
- `F-CD-2` 指定历史月份汇总
- `F-CD-3` 学生列表分页 + keyword 过滤
- `F-CD-4` 订单按状态过滤
- `F-CD-5` 订单按日期范围过滤
- `F-CD-6` `last7d` 趋势
- `F-CD-6.1` `last30d` 趋势
- `F-CD-7` 学生角色访问 403
- `F-CD-8` 空渠道返回空列表

说明：`.http` 测试块已补齐，但本轮未逐条点跑。

## F. 夹具补充
在 [99-test-fixtures.sql](D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql) 追加了：
- 空渠道账号 `9203`，用于 `F-CD-8`
- 绑定学生 `9211/9212`
- 绑定关系 `5211/5212`
- 订单 `OD_FCD_001 ~ OD_FCD_004`
- 返点台账 `94521 ~ 94523`

其中刻意放入了 `user 已绑定到 9201，但 order.channel_id 不是 9201` 的样例，用来卡死错误实现，确保接口真的按“绑定学生订单”口径查询。

## G. 遗留 TODO
- `monthly/stats` 的时间口径在任务卡里没有把 `orderCount` 与 `amount` 的时间轴写到列级别，本次按最贴近页面理解的方式实现：订单数走 `create_time`，支付金额走 `pay_time`，趋势金额跟随订单创建日聚合已支付金额；联调时若前端希望统一改成支付日口径，可只改 `OrderMainLookupMapper.xml`。
- 权限点 `jst:channel:dashboard:*` 是否已同步进本地权限数据，还需要联调时确认；当前 controller 已用 `jst_channel` 角色兜底。

## H. 我做了任务卡之外的什么
- 额外补了空渠道 `9203`，让 “无绑定学生也不报错” 能独立回归。
- 额外补了“绑定学生订单但 `order.channel_id` 不匹配”的夹具，帮助主 Agent 在联调时更早发现错误 SQL。

## I. 自检清单
- [x] 4 个接口都在 `wx` 门面下
- [x] 每个接口都有权限注解
- [x] ReqDTO 加了 JSR-303
- [x] 出参统一为 VO / `AjaxResult.success(...)` / `TableDataInfo`
- [x] 没有跨模块 import user/order entity
- [x] lookup mapper 未使用 `@Select ... LIMIT`
- [x] `startPage()` 放在 lookup 之后、分页 SQL 之前
- [x] XML 未使用 `${}`
- [x] 全仓 `mvn compile` 通过
