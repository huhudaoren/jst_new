# 任务卡 FIX-LINK-BE - 后端接口补齐

## 阶段
阶段 E 收尾 / **jst-channel** + **jst-user**

## 背景
前端 api/*.js 中封装了 6 个后端不存在的接口，调用会 404。本卡补齐这些接口，使前端链路完全闭环。

## 交付物

### Part A — 渠道排行榜 2 接口
**文件**：`RuoYi-Vue/jst-channel/.../controller/wx/WxChannelDashboardController.java` + `ChannelSupplementService`

新增：
- `GET /jst/wx/channel/dashboard/top-contests?period=&limit=5`
  - 按渠道方绑定学生的报名数聚合，返回 Top N 赛事（赛事名 / 类别 / 报名人数）
  - 权限：`@ss.hasRole('jst_channel')`
  - SQL：`SELECT c.contest_name, c.category, COUNT(e.enroll_id) AS enroll_count FROM jst_enroll_record e JOIN jst_contest c ON ... WHERE e.channel_id = #{channelId} GROUP BY c.contest_id ORDER BY enroll_count DESC LIMIT #{limit}`

- `GET /jst/wx/channel/dashboard/top-students?limit=5`
  - 按绑定学生的报名数/支付金额排行
  - 返回 Top N 学生（姓名脱敏 / 报名数 / 支付金额）

### Part B — 渠道订单详情 1 接口
**文件**：`WxChannelDashboardController.java` + `ChannelSupplementService`

新增：
- `GET /jst/wx/channel/orders/{orderId}`
  - 查询单条订单详情（校验 `channel_id` 归属，防越权）
  - 返回 VO 含：基础信息 + 费用明细（priceOriginal / couponDiscount / pointsDiscount / userNetPay / platformFee / rebateBase / rebateRate / rebateAmount）+ 归属渠道信息 + 时间轴
  - 费用明细字段从 `jst_order_main` + `jst_order_item` + `jst_rebate_ledger` 关联查询

### Part C — 临时参赛档案管理 3 接口
**文件**：`RuoYi-Vue/jst-user/.../controller/wx/WxParticipantController.java`

新增：
- `GET /jst/wx/channel/participant/my?status=&page=&size=`
  - 查询当前渠道方创建的临时档案列表
  - 支持按状态筛选：`unclaimed` / `claimed` / `conflict`
  - 权限：`@ss.hasRole('jst_channel')`
  - 校验：`create_by = currentUserId`

- `PUT /jst/wx/channel/participant/{id}`
  - 更新临时档案（仅 `unclaimed` 状态允许修改）
  - 校验：`create_by = currentUserId` + `status = unclaimed`
  - 可修改字段：姓名 / 性别 / 出生日期 / 监护人姓名 / 监护人手机号 / 学校 / 证件信息

- `DELETE /jst/wx/channel/participant/{id}`
  - 软删除临时档案（仅 `unclaimed` 状态允许删除）
  - 校验同上
  - 走 `del_flag = '2'`

### Part D — 测试
**修改**：`test/wx-tests.http`

追加测试段：
- `FIX-BE-1` 渠道登录
- `FIX-BE-2` GET top-contests → 返回 Top 5
- `FIX-BE-3` GET top-students → 返回 Top 5
- `FIX-BE-4` GET orders/{orderId} → 返回费用明细
- `FIX-BE-5` GET channel/participant/my → 返回列表
- `FIX-BE-6` PUT channel/participant/{id} → 更新成功
- `FIX-BE-7` DELETE channel/participant/{id} → 软删除成功

## DoD
- [ ] 6 个新端点全部实现
- [ ] 所有端点有 @PreAuthorize 权限控制
- [ ] 所有端点有归属校验（channelId / createBy）
- [ ] mvn compile 18 模块 BUILD SUCCESS
- [ ] wx-tests.http 追加 7 段测试
- [ ] 任务报告 `FIX-LINK-BE-回答.md`
- [ ] commit: `fix(be): FIX-LINK-BE 后端接口补齐（排行榜/订单详情/临时档案管理）`

## 不许做
- ❌ 不许改前端
- ❌ 不许改已有接口签名
- ❌ 不许动 C1~C8 已完成的核心链路
- ❌ 费用明细 VO 字段如果 jst_order_main 某些列不存在，用 null 兜底不要报错

## 依赖：E0-1 ✅
## 优先级：P0（链路闭环阻塞）

---
派发时间：2026-04-10
