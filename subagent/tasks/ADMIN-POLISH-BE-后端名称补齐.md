# ADMIN-POLISH-BE — 后端 Mapper 关联名称补齐

> 优先级：P1 | 预估：S | Agent：Backend Agent
> 配合 ADMIN-POLISH-BATCH1~3 前端改造

---

## 一、背景

代码生成的 Mapper 只查本表字段，前端精品化需要显示关联表的名称而非 ID。需要在 MapperExt.xml 中追加 LEFT JOIN 补齐名称字段。

## 二、测试库

```
url: jdbc:mysql://59.110.53.165:3306/jst_new
username: jst_new
password: J8zZpAa4zG8y6a7e
```

## 三、改动清单

对以下 Mapper 的 selectList 查询追加 LEFT JOIN 和名称字段：

| # | Mapper XML | 补充字段 | JOIN 表 |
|---|---|---|---|
| 1 | JstOrderItemMapper(Ext).xml | contestName, participantName | jst_contest, jst_participant |
| 2 | JstPaymentRecordMapper(Ext).xml | orderNo | jst_order_main |
| 3 | JstAppointmentRecordMapper(Ext).xml | contestName, userName | jst_contest, jst_user |
| 4 | JstTeamAppointmentMapper(Ext).xml | contestName, channelName | jst_contest, jst_channel |
| 5 | JstPointsLedgerMapper(Ext).xml | ownerName | jst_user / jst_channel (根据 owner_type) |
| 6 | JstScoreRecordMapper(Ext).xml | contestName, participantName, enrollNo | jst_contest, jst_participant, jst_enroll_record |
| 7 | JstCertRecordMapper(Ext).xml | contestName, participantName, templateName | jst_contest, jst_participant, jst_cert_template |
| 8 | JstBindingMapper(Ext).xml | userName, channelName | jst_user, jst_channel |
| 9 | JstGrowthLedgerMapper(Ext).xml | ownerName | 同 PointsLedger |
| 10 | JstMallExchangeOrderMapper(Ext).xml | goodsName, userName | jst_mall_goods, jst_user |

### Domain 新增 transient 字段

每个涉及的 Domain 类新增名称属性（非持久化，不影响现有 INSERT/UPDATE）：

```java
/** 赛事名称（JOIN 查出，非持久化） */
private String contestName;
// getter + setter
```

## 四、DoD

- [ ] 10 个 MapperExt.xml LEFT JOIN 补齐
- [ ] 10 个 Domain 新增 transient 名称字段
- [ ] 现有接口不受影响（新字段为追加，非修改）
- [ ] `mvn compile -DskipTests` 通过
- [ ] 报告交付

## 五、注意

- 使用 LEFT JOIN（不是 INNER JOIN），避免关联表无数据时主记录丢失
- ownerName 需要根据 owner_type 条件 JOIN 不同表（CASE WHEN 或 COALESCE）
- 只改 selectList 查询，不改 insert/update/delete
