# 任务卡 FIX-2 - 后端接口补齐

## 阶段
阶段 E 收尾 / **jst-channel + jst-user**

## 背景
Test Agent 发现 5 处 API 404（前端封装了但后端不存在）+ 1 处渠道订单详情缺端点。

## 交付物

### Part A — 渠道排行榜 2 接口
**文件**：`WxChannelDashboardController.java` + `ChannelSupplementService`

- `GET /jst/wx/channel/dashboard/top-contests?period=&limit=5`
  - 按渠道方绑定学生的报名数聚合 Top N 赛事
  - 返回：`[{ contestId, contestName, category, enrollCount }]`
  - 权限：`@ss.hasRole('jst_channel')`

- `GET /jst/wx/channel/dashboard/top-students?limit=5`
  - 按绑定学生的报名数/支付金额排行
  - 返回：`[{ studentName(脱敏), enrollCount, payAmount }]`

### Part B — 渠道订单详情 1 接口
**文件**：`WxChannelDashboardController.java`

- `GET /jst/wx/channel/orders/{orderId}`
  - 查询单条订单详情（校验 channel_id 归属）
  - 返回费用明细：priceOriginal / couponDiscount / pointsDiscount / userNetPay / platformFee / rebateBase / rebateRate / rebateAmount
  - 归属渠道信息 + 时间轴
  - 字段从 jst_order_main + jst_order_item + jst_rebate_ledger 关联查询
  - 不存在的字段用 null 兜底

### Part C — 临时参赛档案管理 3 接口
**文件**：`WxParticipantController.java`

- `GET /jst/wx/channel/participant/my?status=&pageNum=&pageSize=`
  - 查询当前渠道方创建的临时档案列表
  - 按 status 筛选：unclaimed / claimed / conflict
  - 校验 create_by = currentUserId

- `PUT /jst/wx/channel/participant/{id}`
  - 更新临时档案（仅 unclaimed 允许修改）
  - 校验 create_by + status

- `DELETE /jst/wx/channel/participant/{id}`
  - 软删除（del_flag='2'，仅 unclaimed 允许）

### Part D — 测试
`test/wx-tests.http` 追加 FIX-2-* 段：
- FIX-2-1 GET top-contests
- FIX-2-2 GET top-students
- FIX-2-3 GET orders/{orderId}
- FIX-2-4 GET channel/participant/my
- FIX-2-5 PUT channel/participant/{id}
- FIX-2-6 DELETE channel/participant/{id}

## DoD
- [ ] 6 个新端点全部实现
- [ ] @PreAuthorize + 归属校验
- [ ] mvn compile 18 模块 SUCCESS
- [ ] 测试段追加
- [ ] 任务报告 `FIX-2-回答.md`
- [ ] commit: `fix(be): FIX-2 后端接口补齐（排行榜/订单详情/临时档案管理）`

## 不许做
- ❌ 不许改前端
- ❌ 不许改已有接口

## 依赖：无（与 FIX-1 并行）
## 优先级：P2

---
派发时间：2026-04-10
