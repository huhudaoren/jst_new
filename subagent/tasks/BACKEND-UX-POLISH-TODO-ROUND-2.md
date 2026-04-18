# 后端补齐 TODO Round 2 — UX 精品化第二波遗留

> **来源**：2026-04-18 前端扫描 `jst-uniapp` 全量 `TODO(backend)` 注释 (24 条)
> **前置**：Round 1 (`BACKEND-UX-POLISH-TODO.md`) 已 merge 进 main
> **分批**：Round 2A (字段扩展，高价值小代价) + Round 2B (新表新端点，后续批)
> **预估**：Round 2A ~1.5 人日 / Round 2B ~2.5 人日

## 一、Round 2A · 字段扩展 + 小端点（本次做）

> **已完成**：A1–A7 全部 ✅（2026-04-19, commit `ecf3c0c`，分支 `feat/backend-todo-round-2a`）。
> 报告：`subagent/tasks/任务报告/BACKEND-UX-POLISH-TODO-ROUND-2A-报告.md`。
> DDL：`架构设计/ddl/99-migration-backend-ux-polish-round2.sql`。

### ✅ A1. `DashboardMonthlyVO.totalStudentCount` 累计绑定学生数

**文件**：`RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/DashboardMonthlyVO.java`（或 dto 目录）
**新增字段**：`totalStudentCount: Integer` — 截至当前该渠道累计绑定学生数
**数据源 SQL**：
```sql
SELECT COUNT(*) FROM jst_user_channel_binding
WHERE channel_id = #{channelId}
  AND is_active = 1
  AND del_flag = '0'
```
**前端位置**：`jst-uniapp/pages/my/index.vue:363` — Summary 4 格"绑定学生"
**前端当前降级**：使用 `newStudentCount` (本月新增) 代替

---

### ✅ A2. `JstChannelAuthApply.rejectReason` 独立字段

**DDL**：
```sql
ALTER TABLE jst_channel_auth_apply
  ADD COLUMN reject_reason VARCHAR(500) DEFAULT NULL
  COMMENT '驳回原因 (业务语义, 区别于通用 audit_remark)';
```
**文件修改清单**：
- `JstChannelAuthApply.java` 加 `rejectReason` 属性 + getter/setter
- `ChannelAuthApplyVO.java` 同步
- `JstChannelAuthApplyMapper.xml` resultMap 加映射 + INSERT/UPDATE 子句包含
- `ChannelAuthApplyServiceImpl.auditReject()` 签入 reject_reason 字段

**前端位置**：`apply-status.vue:96`, `apply-form.vue:234`
**前端当前降级**：复用 `auditRemark`

---

### ✅ A3. `GET /jst/wx/channel/auth/{id}` 按 applyId 直查

**Controller**：`WxChannelAuthController` 增加
```java
@GetMapping("/auth/{id}")
public AjaxResult getById(@PathVariable Long id) {
    ChannelAuthApplyVO vo = service.getById(id, SecurityUtils.getUserId());
    return AjaxResult.success(vo);
}
```
**Service 约束**：
- 校验该 applyId 属于当前 userId
- 不属于 → 抛 `ServiceException("无权查看该认证申请")` (403)

**前端位置**：`apply-form.vue:213` — edit 模式按 rejectedId 查历史
**前端当前降级**：复用 `getMyChannelApply` 取最新记录 + applyId 一致性校验

---

### ✅ A4. `JstContest.offlineReserveRemaining` / `offlineReserveDeadline` 字段

**DDL**：
```sql
ALTER TABLE jst_contest
  ADD COLUMN offline_reserve_remaining INT DEFAULT NULL
    COMMENT '线下预约剩余名额 (null=不限量)',
  ADD COLUMN offline_reserve_deadline DATETIME DEFAULT NULL
    COMMENT '线下预约截止时间';
```
**文件修改**：
- `JstContest.java` + VO 加字段
- `JstContestMapper.xml` resultMap + insert/update
- 管理端 `views/jst/contest/index.vue` 和 `edit.vue` 加字段（可选）

**前端位置**：`contest/detail.vue:123, 494, 501`
**前端当前降级**：静态兜底文案"名额充足"

---

### ✅ A5. `/jst/wx/mall/list` 支持 `goodsType=virtual` 过滤

**文件**：`WxMallController` + `JstMallGoodsMapper.xml`
**Mapper 逻辑**：
```xml
<if test="goodsType != null and goodsType != ''">
  <choose>
    <when test="goodsType == 'virtual'">
      AND goods_type IN ('coupon', 'rights')
    </when>
    <otherwise>
      AND goods_type = #{goodsType}
    </otherwise>
  </choose>
</if>
```
**前端位置**：`pages-sub/mall/list.vue:73`
**前端当前降级**：不传 type 拉全部后前端过滤

---

### ✅ A6. `OrderVO.pointsUsed` + `refundEnabled`

**文件**：`OrderListVO.java` + `OrderDetailVO.java`
**新增字段**：
- `pointsUsed: Integer` — 订单实际使用积分数（从 `points_amount × 100` 计算，100 积分 = 1 元）
- `refundEnabled: Boolean` — 是否允许退款（规则：`status=paid` AND 在售后期内 AND 非零元订单）

**Service 计算**：
```java
vo.setPointsUsed(order.getPointsAmount() == null ? 0 :
    order.getPointsAmount().multiply(BigDecimal.valueOf(100)).intValue());
vo.setRefundEnabled(
    "paid".equals(order.getStatus())
    && isInAfterSalesPeriod(order)
    && order.getPayAmount().compareTo(BigDecimal.ZERO) > 0
);
```

**前端位置**：`my/order-detail.vue:313, 320`

---

### ✅ A7. `RefundVO.pointsRefund` + `couponRefund` + `expectedArrivalTime`

**文件**：`RefundListVO.java` + `RefundDetailVO.java`
**新增字段**：
- `pointsRefund: Integer` — 回退积分数
- `couponRefund: CouponRefundInfo` 对象 `{ couponName, faceAmount, couponStatus }`
- `expectedArrivalTime: LocalDateTime` — 预计到账（已通过状态时有值，其他为 null）

**Mapper LEFT JOIN**：
```sql
SELECT r.*,
       c.coupon_name       AS coupon_name,
       c.face_amount       AS coupon_face_amount,
       cu.status           AS coupon_status
FROM jst_refund r
LEFT JOIN jst_user_coupon cu ON cu.coupon_id = r.coupon_id
LEFT JOIN jst_coupon_template c ON c.id = cu.template_id
```

**expectedArrivalTime 计算**：`approvedTime + 7 天`（微信原路退回通常 1-7 天）

**前端位置**：`my/refund-list.vue:53,59,276,294,302`, `my/refund-detail.vue:71,79,205,217,254`

---

## 二、Round 2B · 新表新端点（下批做，单独 PR）

> **B1 + B3 已完成**：2026-04-19，分支 `feat/backend-round-2b-b1-b3`，commit `837f4b5`。
> B2（通知偏好）/ B4（账号注销）仍待做。
> 报告：`subagent/tasks/任务报告/BACKEND-UX-POLISH-ROUND-2B-B1-B3-报告.md`。
> DDL：`架构设计/ddl/99-migration-invoice-title.sql`（本地 DESC 已验证）。

### ✅ B1. POST `/jst/wx/channel/orders/batch-pay` 批量支付

**已采用**：简化聚合方案（每单独立 prepay，不建 `jst_batch_pay_order` 表）。真合单支付作为 TODO 留给后续优化。

**实现路径**：
- Controller：`jst-order/controller/wx/WxChannelOrderController.java`（路径 `/jst/wx/channel/orders/batch-pay`）
- Service：`jst-order/service/WxChannelOrderService.java` + `WxChannelOrderServiceImpl.java`
- DTO：`jst-order/dto/BatchPayOrderReqDTO.java`（orderIds @NotEmpty @Size max=50）
- VO：`jst-order/vo/BatchPayResVO.java` + `BatchPayItemVO.java`

**校验链**：订单存在 → 属于当前 channelId → status=pending_pay → netPayAmount>0；任一失败整批拒绝。

**返回**：`{ batchPayNo, totalAmount, count, items: [{ orderId, orderNo, payAmount, merchantPayParams }] }`

**前端位置**：`channel/orders.vue:246`

---

### B2. 通知偏好 `jst_user_notify_prefs`

**DDL**：
```sql
CREATE TABLE jst_user_notify_prefs (
  user_id       BIGINT     PRIMARY KEY,
  system_push   TINYINT(1) DEFAULT 1 COMMENT '系统推送',
  marketing_push TINYINT(1) DEFAULT 1 COMMENT '营销推送',
  email_notify  TINYINT(1) DEFAULT 0,
  sms_notify    TINYINT(1) DEFAULT 0,
  update_time   DATETIME
);
```
**端点**：
- `GET /jst/wx/user/notify-prefs` → 当前用户偏好
- `POST /jst/wx/user/notify-prefs` → 更新

**前端位置**：`my/settings.vue:199`
**前端当前降级**：本地 localStorage

---

### ✅ B3. 发票抬头 `jst_invoice_title`

**DDL**：`架构设计/ddl/99-migration-invoice-title.sql`
```sql
CREATE TABLE IF NOT EXISTS jst_invoice_title (
  title_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      BIGINT       NOT NULL,
  title_type   VARCHAR(16)  NOT NULL DEFAULT 'personal' COMMENT 'personal/company',
  title_name   VARCHAR(128) NOT NULL,
  tax_no       VARCHAR(64)  NULL,
  is_default   TINYINT(1)   NOT NULL DEFAULT 0,
  create_by    VARCHAR(64)  DEFAULT '',
  create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
  update_by    VARCHAR(64)  DEFAULT '',
  update_time  DATETIME     ON UPDATE CURRENT_TIMESTAMP,
  del_flag     CHAR(1)      NOT NULL DEFAULT '0',
  KEY idx_user (user_id, del_flag)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**实现路径（jst-finance 模块）**：
- Domain：`domain/JstInvoiceTitle.java`
- VO：`vo/InvoiceTitleVO.java`
- DTO：`dto/InvoiceTitleSaveReqDTO.java`（company + taxNo 非空校验）
- Mapper：`mapper/JstInvoiceTitleMapper.java` + `mapper/finance/JstInvoiceTitleMapper.xml`
- Service：`service/InvoiceTitleService.java` + `service/impl/InvoiceTitleServiceImpl.java`
- Controller：`controller/wx/WxInvoiceTitleController.java`

**端点**（路径前缀 `/jst/wx/user/invoice-title`）：
- `GET /list` → 当前用户全部抬头
- `POST /` → 新增/更新（titleId 非空=更新）
- `DELETE /{id}` → 软删
- `POST /{id}/default` → 切换默认（事务内清旧 + 标新）

**归属强校验**：所有写操作通过 `SecurityUtils.getUserId()` 取 userId，Service 层 `requireOwned(userId,titleId)` 越权抛 99902。

**前端位置**：`my/settings.vue:248`

---

### B4. 账号注销 `jst_deactivate_request`

**DDL**：
```sql
CREATE TABLE jst_deactivate_request (
  request_id  BIGINT   PRIMARY KEY AUTO_INCREMENT,
  user_id     BIGINT   NOT NULL,
  reason      VARCHAR(500),
  status      VARCHAR(16) DEFAULT 'pending' COMMENT 'pending/approved/rejected',
  apply_time  DATETIME,
  audit_time  DATETIME,
  audit_by    BIGINT,
  audit_remark VARCHAR(500)
);
```
**端点**：
- `POST /jst/wx/user/deactivate` → 创建注销申请
- admin 端：`/system/user-deactivate/list` + `/audit/{id}`

**前端位置**：`my/settings.vue:255`

---

## 三、执行建议

### Round 2A 顺序（建议单 PR）

1. **批 1**：A1 (Monthly VO) + A5 (mall virtual) — 最轻量，1 小时
2. **批 2**：A2 (DDL + reject_reason 字段) + A3 (auth/{id} 端点) — 关联，2 小时
3. **批 3**：A4 (Contest DDL + 字段) — 1 小时
4. **批 4**：A6 (Order VO 字段) + A7 (Refund VO + couponRefund) — 最大块，4 小时

### Round 2B（单独 PR，本次不做）

B1-B4 每个都要建新表 + 多端点 + admin 管理页，建议每项独立 PR。

### 前端配合（独立 PR，同时可做）

前端清理 `TODO(backend)` 注释 + 替换字段别名（新字段兜底保留）不依赖后端合并，可并行。

---

## 四、验收清单

Round 2A 完成后：
- [ ] 24 条 `TODO(backend)` 注释中 Round 2A 相关 **至少 18 条** 可被前端删除或改为正常使用
- [ ] `test/wx-tests.http` 新增至少 5 个 curl 测试用例
- [ ] `mvn -pl jst-channel,jst-user,jst-order,jst-event,jst-marketing compile` 全绿
- [ ] DDL 迁移 SQL 合入 `架构设计/ddl/99-migration-backend-ux-polish-round2.sql`
- [ ] 本文档每项加 ✅ + commit hash

---

**文档版本**：V1.0 · 2026-04-18 · 胡字蒙 + Claude Opus 4.7
