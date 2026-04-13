# 任务报告 - WEB-ADMIN-ID2NAME-BE 管理端列表 ID→名称 后端补齐

## A. 任务前自检（Step 2 答题）

1. **涉及表**：16 个主表 + 8 个 JOIN 关联表（sys_user, jst_contest, jst_channel, jst_order_main, jst_event_partner, jst_mall_goods, jst_coupon_template, jst_level_config）
2. **涉及状态机**：无（纯读操作）
3. **涉及权限标识**：无变更（不改 Controller）
4. **涉及锁名**：无
5. **事务边界**：无（只改 SELECT 查询）
6. **ResVO 字段**：不适用（直接在 Domain 追加非持久化属性）
7. **复用样板**：无需

---

## B. 交付物清单

### Mapper XML 修改（16 个）

| 模块 | 文件 | 新增 JOIN | 新增字段 |
|---|---|---|---|
| jst-channel | JstRebateLedgerMapper.xml | +jst_channel +jst_order_main | channelName, orderNo |
| jst-channel | JstRebateRuleMapper.xml | +jst_contest +jst_channel | contestName, channelName |
| jst-channel | JstEventSettlementMapper.xml | +jst_event_partner +jst_contest | partnerName, contestName |
| jst-channel | JstRebateSettlementMapper.xml | +jst_channel | channelName |
| jst-order | JstAppointmentRecordMapper.xml | +jst_order_main +sys_user +jst_contest | orderNo, userName, contestName |
| jst-order | JstOrderItemMapper.xml | +jst_order_main | orderNo |
| jst-order | JstTeamAppointmentMapper.xml | +jst_contest | contestName |
| jst-points | JstPointsAccountMapper.xml | +sys_user +jst_level_config | ownerName, levelName |
| jst-points | JstPointsLedgerMapper.xml | +sys_user | ownerName |
| jst-points | JstMallExchangeOrderMapper.xml | +sys_user +jst_mall_goods | userName, goodsName |
| jst-marketing | JstUserCouponMapper.xml | +sys_user +jst_coupon_template +jst_order_main | userName, couponTemplateName, orderNo |
| jst-marketing | JstUserRightsMapper.xml | +sys_user | userName |
| jst-user | JstChannelMapper.xml | +sys_user | userName |
| jst-user | JstStudentChannelBindingMapper.xml | +sys_user +jst_channel | studentName, channelName |
| jst-event | JstEnrollFormTemplateMapper.xml | +jst_contest | ownerName |
| jst-event | JstCertTemplateMapper.xml | +jst_contest | ownerName |

### Domain 类修改（15 个，追加非持久化属性 + getter/setter）

| Domain | 新增属性 |
|---|---|
| JstRebateLedger | channelName, orderNo |
| JstRebateRule | contestName, channelName |
| JstEventSettlement | partnerName, contestName |
| JstRebateSettlement | channelName |
| JstAppointmentRecord | orderNo, userName, contestName |
| JstOrderItem | orderNo |
| JstTeamAppointment | contestName |
| JstPointsAccount | ownerName, levelName |
| JstPointsLedger | ownerName |
| JstMallExchangeOrder | userName, goodsName |
| JstUserCoupon | userName, couponTemplateName, orderNo |
| JstUserRights | userName |
| JstChannel | userName |
| JstStudentChannelBinding | studentName, channelName |
| JstEnrollFormTemplate | ownerName |
| JstCertTemplate | ownerName |

### 跳过的项

- **JstParticipantMapper.xml**：src 中不存在此文件（仅在 gen/ 目录）。实际使用的 `ParticipantMapper.xml` 已在 `selectChannelParticipantList` 中 JOIN 了 sys_user，无需额外处理。
- **JstMallGoodsMapper.xml**：已有 goodsName 字段，任务卡标注无需修改。

---

## C. mvn compile 结果

```
[INFO] BUILD SUCCESS
[INFO] Reactor Summary:
[INFO] ruoyi .............................................. SUCCESS
[INFO] ruoyi-common ....................................... SUCCESS
[INFO] ruoyi-system ....................................... SUCCESS
[INFO] ruoyi-framework .................................... SUCCESS
[INFO] ruoyi-quartz ....................................... SUCCESS
[INFO] ruoyi-generator .................................... SUCCESS
[INFO] jst-common ......................................... SUCCESS
[INFO] jst-user ........................................... SUCCESS
[INFO] jst-order .......................................... SUCCESS
[INFO] jst-event .......................................... SUCCESS
[INFO] jst-channel ........................................ SUCCESS
[INFO] jst-points ......................................... SUCCESS
[INFO] jst-organizer ...................................... SUCCESS
[INFO] jst-marketing ...................................... SUCCESS
[INFO] jst-message ........................................ SUCCESS
[INFO] jst-risk ........................................... SUCCESS
[INFO] jst-finance ........................................ SUCCESS
[INFO] ruoyi-admin ........................................ SUCCESS
[INFO] Total time: 50.819 s
```

18 模块全部 SUCCESS。

---

## D. 接口测试结果

启动后端（`mvn spring-boot:run`），使用 admin 账号登录获取 Token，抽查以下接口：

| 接口 | HTTP | 结果 | 说明 |
|---|---|---|---|
| `/jst/user/binding/list` | 200 | Total=11, channelName="测试_艺术工作室" | channelName 正确 JOIN 返回 |
| `/jst/user/binding/list` | 200 | studentName=null | sys_user.nick_name 为空，LEFT JOIN 正确返回 null |
| `/jst/channel/rebateLedger/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/channel/rebateRule/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/order/orderItem/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/order/appointmentRecord/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/points/pointsAccount/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/marketing/userCoupon/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/user/channel/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |
| `/jst/event/enrollFormTemplate/list` | 200 | Total=0 | 无测试数据，SQL 语法正确 |

所有接口均正常返回（无 SQL 语法错误、无 500 错误）。有数据的 `binding/list` 接口确认 `channelName` 字段正确通过 LEFT JOIN 返回。

---

## E. 遗留 TODO

- **JstParticipantMapper.xml** 不存在于 src，管理端参赛档案列表使用 `ParticipantUserMapMapper` + 自定义查询，已自带 JOIN，无需额外处理。如需在管理端新增独立的参赛档案 CRUD 列表，需先将 gen/ 代码迁移至 jst-user 模块。

---

## F. 任务卡之外的额外改动

- **WHERE 子句加了 `t.` 表别名前缀**：因为 LEFT JOIN 后存在同名列（如 `status`、`channel_id`、`order_id`、`user_id`、`contest_id` 等），MySQL 会报 "Column 'xxx' in where clause is ambiguous" 错误。添加 `t.` 前缀是必要的消歧义措施，过滤逻辑完全未变。

---

## G. 自检清单确认

- [x] 所有 LEFT JOIN 加了 `del_flag = '0'` 条件
- [x] 不改 Controller / Service
- [x] resultMap 追加了 `<result>` 映射
- [x] Domain 追加了非持久化属性 + getter/setter（含 javadoc 注释）
- [x] 18 模块编译通过
- [x] 启动后端抽查接口确认返回 xxxName 字段
- [x] 未引入新依赖
- [x] 未创建新表 / 改 DDL
- [x] 未修改若依原生模块
- [x] 未使用 Lombok / MyBatis-Plus
