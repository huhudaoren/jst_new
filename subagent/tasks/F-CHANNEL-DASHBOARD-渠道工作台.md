# 任务卡 F-CHANNEL-DASHBOARD - 渠道方工作台数据接口

## 阶段 / 模块
阶段 C 后端补齐 / **jst-channel**（读 jst-user / jst-order / jst-event 走 lookup）

## 业务背景

WX-C2 已交付渠道小程序，但 home/orders/students/data 4 页因后端接口缺失只落了骨架。本任务补齐这 4 个读接口，让渠道方能看到自己名下的学生、订单、月度 KPI 和数据图表。

## ⚠️ 口径定义（主 Agent 裁决）

**"本渠道学生"** = `jst_user_channel_binding` 表中 `channel_id = currentChannelId AND status='active'` 的 `user_id`。
**"本渠道订单"** = `jst_order_main` 中 `user_id` 属于上述集合的订单（不是 jst_order_main.channel_id，因为 channel 绑定在 user 维度）。
**"本渠道返点"** = `jst_rebate_ledger.channel_id = currentChannelId`。

接口拆成 **4 个独立 RESTful**，各自分页/筛选独立，DTO 可共享 base。

## 必读

1. C5a/C5b 任务卡（当前渠道身份解析方式 `requireCurrentChannelId()`）
2. `jst-channel/.../service/impl/ChannelWithdrawServiceImpl.java`（照搬 current channel 读取 + PageUtils.startPage 模式）
3. `架构设计/ddl/01-jst-user.sql` 的 `jst_user_channel_binding` 表
4. `架构设计/ddl/03-jst-order.sql` 的 `jst_order_main`
5. `jst-uniapp/pages-sub/channel/home.vue` / `orders.vue` / `students.vue` / `data.vue`（前端字段期望）
6. 原型 PNG：`小程序原型图/channel-home.png` / `channel-orders.png` / `channel-students.png` / `channel-data.png`

## 接口清单（全部 wx 侧，需登录 + `jst_channel` 角色）

### 1. GET /jst/wx/channel/dashboard/monthly
- 入参：无（默认当月；可选 `yearMonth=2026-04`）
- 返回：
  ```json
  {
    "yearMonth": "2026-04",
    "newStudentCount": 12,          // 本月新绑定学生数
    "orderCount": 38,                // 本月订单数
    "orderPaidAmount": "4580.00",    // 本月已支付订单金额
    "rebateEstimatedAmount": "458.00" // 本月预计返点（ledger 正向 withdrawable+in_review+paid）
  }
  ```
- 实现：全部按 `currentChannelId` 过滤，SQL 聚合。

### 2. GET /jst/wx/channel/students?keyword=&bindStatus=&pageNum=&pageSize=
- 我名下的学生列表分页
- 返回字段：`userId / nickname / avatar / bindTime / bindStatus / totalOrderCount / totalPaidAmount`
- join `jst_user + jst_user_channel_binding + 聚合 jst_order_main`
- 权限：`jst:channel:dashboard:students`

### 3. GET /jst/wx/channel/orders?status=&startDate=&endDate=&pageNum=&pageSize=
- 我名下学生的订单列表
- 返回：`orderId / orderNo / userId / userName / orderType / payAmount / status / createTime`
- 权限：`jst:channel:dashboard:orders`

### 4. GET /jst/wx/channel/dashboard/stats?period=last7d|last30d|last90d
- 图表数据（当期 vs 上一期）
- 返回：
  ```json
  {
    "period": "last30d",
    "orderTrend": [{"date":"2026-03-10","count":3,"amount":"380.00"}, ...],
    "rebateTrend": [{"date":"2026-03-10","amount":"38.00"}, ...],
    "topContests": [{"contestId":1,"contestName":"...","orderCount":5}]
  }
  ```
- 权限：`jst:channel:dashboard:stats`

## 交付物

新增：
- `jst-channel/.../dto/ChannelDashboardQueryDTO.java`（共享 base：yearMonth / period / keyword / pageNum / pageSize）
- `jst-channel/.../vo/DashboardMonthlyVO.java`
- `jst-channel/.../vo/DashboardStudentVO.java`
- `jst-channel/.../vo/DashboardOrderVO.java`
- `jst-channel/.../vo/DashboardStatsVO.java`（包含 `DashboardTrendPointVO` 内部类）
- `jst-channel/.../service/ChannelDashboardService.java` + Impl
- `jst-channel/.../controller/wx/WxChannelDashboardController.java`
- `jst-channel/.../mapper/lookup/UserChannelBindingLookupMapper.java` + xml
- `jst-channel/.../mapper/lookup/OrderMainLookupMapper.java`（若已存在扩展）+ xml
- `jst-channel/.../mapper/ChannelDashboardMapperExt.java` + xml（聚合查询集中放这里，复杂 SQL）

修改：
- `架构设计/ddl/99-test-fixtures.sql` 追加渠道 9201 的学生/订单聚合场景
- `test/wx-tests.http` 追加 F-CHANNEL-DASHBOARD 测试段

## PageHelper 注意

**必须遵守 C5a 的 lesson**：`startPage()` 必须放在 service 里「所有 lookup 查询之后、目标分页 SQL 之前」。lookup mapper 的 @Select 严禁出现 `LIMIT`。

## 测试

- F-CD-1 monthly 默认当月
- F-CD-2 monthly 指定历史月份
- F-CD-3 students 列表分页 + keyword 过滤
- F-CD-4 orders 按 status 过滤
- F-CD-5 orders 按日期范围过滤
- F-CD-6 stats last7d / last30d
- F-CD-7 学生角色调这些接口应 403
- F-CD-8 空渠道（无绑定学生）返回空列表不报错

## DoD

- [ ] mvn compile SUCCESS
- [ ] .http 全绿
- [ ] 前端 `home/orders/students/data.vue` 骨架可切换成真实数据（本任务只要后端 ready，前端对接由 WX-C3b 另起或 polish 合并）
- [ ] 任务报告 `F-CHANNEL-DASHBOARD-回答.md`
- [ ] commit: `feat(jst-channel): F-CHANNEL-DASHBOARD 渠道工作台 4 接口`

## 不许做

- ❌ 不许跨模块 import user/order entity，必须走 lookup mapper
- ❌ 不许在 @Select lookup 里写 LIMIT
- ❌ 不许漏 `requireCurrentChannelId()` 过滤，任何接口不加过滤等于越权看全平台
- ❌ 不许 mock 返回假数据

## 依赖：C5a ✅ / C5b ✅
## 优先级：中

---
派发时间：2026-04-09
