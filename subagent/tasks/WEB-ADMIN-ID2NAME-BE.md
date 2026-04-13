# WEB-ADMIN-ID2NAME-BE — 管理端列表 ID→名称 后端补齐

> 优先级：P1 | 预估：M | Agent：Backend Agent

---

## 一、背景

管理后台 15+ 个列表页面展示的是原始业务 ID（如 `userId: 1001`、`contestId: 94701`），用户无法识别。需要在后端 Mapper 查询中 LEFT JOIN 关联表，把 ID 翻译为名称返回给前端。

## 二、改动方式

**统一方式**：在现有 Mapper XML 的 `selectXxxList` 查询中追加 LEFT JOIN，在 resultMap 或 domain 类中追加 `xxxName` 字段（使用 `@Transient` 或直接加非持久化属性）。

**不改 Controller / Service 逻辑**，只改 Mapper XML + Domain 类。

## 三、需要补充的字段清单

### 3.1 高频翻译对（跨多个页面复用）

| 源字段 | JOIN 表 | 取的名称字段 | 新增属性名 |
|---|---|---|---|
| `user_id` / `owner_id`(owner_type=user) | `sys_user` | `nick_name` | `userName` |
| `contest_id` | `jst_contest` | `contest_name` | `contestName` |
| `channel_id` | `jst_channel` | `channel_name` | `channelName` |
| `order_id` | `jst_order_main` | `order_no` | `orderNo` |
| `participant_id` | `jst_participant` | `real_name` | `participantName` |
| `partner_id` | `jst_event_partner` | `partner_name` | `partnerName` |
| `goods_id` | `jst_mall_goods` | `goods_name` | `goodsName` |
| `coupon_template_id` | `jst_coupon_template` | `coupon_name` | `couponTemplateName` |

### 3.2 逐模块改动清单

**渠道模块 (jst-channel)**

| Mapper XML | 补充 JOIN | 新增字段 |
|---|---|---|
| `JstRebateLedgerMapper.xml` | +jst_channel(channelName) +jst_order_main(orderNo) | `channelName`, `orderNo` |
| `JstRebateRuleMapper.xml` | +jst_contest(contestName) +jst_channel(channelName) | `contestName`, `channelName` |
| `JstEventSettlementMapper.xml` | +jst_event_partner(partnerName) +jst_contest(contestName) | `partnerName`, `contestName` |
| `JstRebateSettlementMapper.xml` | +jst_channel(channelName) | `channelName` |

**订单模块 (jst-order)**

| Mapper XML | 补充 JOIN | 新增字段 |
|---|---|---|
| `JstAppointmentRecordMapper.xml` | +jst_order_main(orderNo) +sys_user(userName) +jst_contest(contestName) | `orderNo`, `userName`, `contestName` |
| `JstOrderItemMapper.xml` | +jst_order_main(orderNo) | `orderNo` |
| `JstTeamAppointmentMapper.xml` | +jst_contest(contestName) | `contestName` |

**积分模块 (jst-points)**

| Mapper XML | 补充 JOIN | 新增字段 |
|---|---|---|
| `JstPointsAccountMapper.xml` | +sys_user(userName) +jst_level_config(levelName) | `ownerName`, `levelName` |
| `JstPointsLedgerMapper.xml` | +sys_user(userName) | `ownerName` |
| `JstMallExchangeOrderMapper.xml` | +sys_user(userName) +jst_mall_goods(goodsName) | `userName`, `goodsName` |
| `JstMallGoodsMapper.xml` | 无需（已有 goodsName） | — |

**营销模块 (jst-marketing)**

| Mapper XML | 补充 JOIN | 新增字段 |
|---|---|---|
| `JstUserCouponMapper.xml` | +sys_user(userName) +jst_coupon_template(couponName) +jst_order_main(orderNo) | `userName`, `couponTemplateName`, `orderNo` |
| `JstUserRightsMapper.xml` | +sys_user(userName) | `userName` |

**用户模块 (jst-user)**

| Mapper XML | 补充 JOIN | 新增字段 |
|---|---|---|
| `JstChannelMapper.xml` | +sys_user(userName) | `userName` |
| `JstStudentChannelBindingMapper.xml` | +sys_user(userName as studentName) +jst_channel(channelName) | `studentName`, `channelName` |
| `JstParticipantMapper.xml` | +sys_user(userName as claimUserName) | `claimUserName` |

**赛事模块 (jst-event)**

| Mapper XML | 补充 JOIN | 新增字段 |
|---|---|---|
| `JstEnrollFormTemplateMapper.xml` | +jst_contest(contestName as ownerName) | `ownerName` |
| `JstCertTemplateMapper.xml` | +jst_contest(contestName as ownerName) | `ownerName` |

## 四、Domain 类修改

每个需要新增字段的 Domain 类，添加非持久化属性：

```java
/** 关联名称（非持久化，JOIN 查出） */
private String contestName;
private String channelName;
private String userName;
// ... 等
```

**不加 `@TableField(exist = false)` 等注解**（项目用 MyBatis 不是 MP），直接在 resultMap 中 `<result>` 映射即可。

## 五、SQL JOIN 模板

```xml
<!-- 示例：JstRebateLedgerMapper.xml -->
<sql id="selectJstRebateLedgerVo">
    select rl.*,
           c.channel_name as channelName,
           o.order_no as orderNo
    from jst_rebate_ledger rl
    left join jst_channel c on rl.channel_id = c.channel_id
    left join jst_order_main o on rl.order_id = o.order_id
</sql>
```

**注意**：
- 必须用 LEFT JOIN（关联表可能无数据）
- 必须加 `del_flag = '0'` 条件到 JOIN 子句（`ON ... AND c.del_flag = '0'`）
- 不改 WHERE 条件，不改排序，不改分页逻辑
- 只改 SELECT 和 FROM 部分

## 六、DoD

- [ ] 18 个 Mapper XML 追加 JOIN
- [ ] 对应 Domain 类追加名称属性
- [ ] 编译通过（`mvn compile -DskipTests`）
- [ ] 启动后端，抽查 3 个列表接口确认返回 xxxName 字段
- [ ] 报告交付（含改动文件清单）
