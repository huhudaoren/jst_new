# 竞赛通 V4.1 — Maven 模块拆分 + 核心 API 契约

> 基于 RuoYi-Vue 4.x 多模块架构
> 所有 Controller 继承 `BaseController`，返回 `AjaxResult`，分页用 `startPage()` + `getDataTable()`
> 入参一律 `XxxReqDTO` / `XxxForm`（带 JSR-303），出参一律 `XxxResVO`，**禁止直返 Entity**

---

## 1. Maven 模块拆分

```
ruoyi-jst (parent)
├── ruoyi-framework         (若依原生)
├── ruoyi-system            (若依原生)
├── ruoyi-common            (若依原生)
├── jst-common              通用：异常码/枚举/工具/Redisson 配置/雪花ID
│
├── jst-user                用户/参赛档案/认领/渠道/绑定          (5表)
├── jst-event               赛事方/赛事/动态表单/报名/成绩/证书/课程 (9表)
├── jst-order               订单/支付/退款/团队预约/核销           (8表)
├── jst-channel             渠道返点规则/计提/结算                 (4表)
├── jst-points              积分账户/流水/等级/商城                (7表)
├── jst-organizer           赛事方/渠道方 入驻申请                 (2表)
├── jst-marketing           优惠券/权益                            (6表)
├── jst-message             公告/消息模板/发送                     (3表)
├── jst-risk                审计/风控                              (5表)
├── jst-finance             合同/发票/统一打款                     (3表)
│
└── ruoyi-admin             启动模块（聚合所有 jst-*）
```

**依赖原则**
- `jst-common` 被所有业务模块依赖
- 跨模块调用 → 通过 `XxxApi` 接口（service 层），禁止跨模块直接 import Mapper
- 高频依赖关系：`jst-order → jst-user / jst-event / jst-channel / jst-points / jst-marketing`

**包路径约定**
```
com.ruoyi.jst.{module}
  ├── controller
  ├── service / service.impl
  ├── mapper
  ├── domain          (Entity，仅 Mapper 内部使用)
  ├── dto             (ReqDTO/Form)
  ├── vo              (ResVO)
  ├── enums
  └── api             (跨模块对外接口)
```

---

## 2. 权限标识规约

格式：`jst:{module}:{resource}:{action}`，与 `sys_menu.perms` 一一对应。

| 模块 | 权限标识示例 |
|---|---|
| 用户 | `jst:user:list`, `jst:user:edit`, `jst:participant:claim` |
| 赛事 | `jst:contest:add`, `jst:contest:audit`, `jst:contest:publish` |
| 订单 | `jst:order:list`, `jst:order:refund`, `jst:order:specialRefund` |
| 返点 | `jst:rebate:rule:edit`, `jst:rebate:settlement:approve` |
| 积分 | `jst:points:adjust`, `jst:mall:goods:edit` |

---

## 3. 核心 API 契约

下文只列**最复杂、最易翻车**的接口；其余 CRUD 走若依生成器即可。

### 3.1 jst-order 混合支付下单 ⭐

```
POST /jst/order/createMixedPay
权限：登录用户即可（通过 token 获取 user_id）
事务：@Transactional(rollbackFor=Exception.class)
锁  ：lock:order:create:{userId} (可选，防重复提交)
```

**ReqDTO**
```java
public class CreateMixedPayOrderReqDTO {
    @NotNull private Long contestId;
    @NotNull private Long participantId;       // 含临时档案
    @NotNull @Size(min=1) private List<EnrollFormItemDTO> formItems; // 动态表单提交值
    private Long couponId;                     // 可选，使用的用户券
    private Long pointsToUse;                  // 可选，使用积分数（>0 触发混合支付）
    private String payMethod;                  // wechat/bank_transfer
    private Long channelIdHint;                // 可选，渠道代付时由渠道传入
}
```

**ResVO**
```java
public class CreateMixedPayOrderResVO {
    private Long orderId;
    private String orderNo;
    private BigDecimal listAmount;
    private BigDecimal couponAmount;
    private BigDecimal pointsDeductAmount;
    private Long pointsUsed;
    private BigDecimal netPayAmount;     // 用户实付
    private String payMethod;
    private WechatPrepayVO wechatPrepay; // 微信预下单参数（payMethod=wechat 时）
}
```

**关键流程**（必须在同一事务）
1. 校验 contest 状态 + form_template 校验 + 写 enroll_record（含 form_snapshot_json）
2. 校验 user_coupon.status='unused' 且适用 → 置 `locked`
3. 校验 points_account.available_points ≥ pointsToUse → 冻结
4. 计算分摊（按 item），写 order_main + order_item
5. 锁定 channel_id（按 student_channel_binding active 记录）
6. 调起微信预下单 / 生成对公转账待审
7. 写 jst_audit_log

### 3.2 jst-order 退款（含售后） ⭐

```
POST /jst/order/refund/apply           用户/客服发起
POST /jst/order/refund/approve         审核通过
POST /jst/order/refund/reject          驳回
权限：jst:order:refund / jst:order:specialRefund (特批超过售后期)
事务：必须
```

**核心逻辑（approve）**
1. 状态机 SM-2 跃迁校验
2. 资金回退顺序：现金 → 积分 → 优惠券（仅整单全退且原券有效期内）
3. 联动 `order_item.refund_amount/refund_points` 累加
4. 联动 `order_main.refund_status` 重算
5. 联动 `rebate_ledger`：未打款置 `rolled_back`；已打款新增 `negative` 记录
6. 触发证书作废 / 成绩相关回退（特批退款）
7. 写 jst_audit_log + 触发消息模板 `aftersale`

### 3.3 jst-order 团队扫码核销 ⭐⭐ 高并发

```
POST /jst/appointment/team/writeoff
权限：jst:appointment:writeoff（赛事方/渠道方/平台核销员）
锁  ：Redisson lock:team_appt:{teamAppointmentId}
事务：必须
```

**ReqDTO**
```java
public class TeamWriteoffReqDTO {
    @NotNull private Long teamAppointmentId;
    @NotNull private Long writeoffItemId;     // 哪个核销子项（到场/礼品/...）
    @NotNull @Min(1) private Integer qty;     // 本次核销人数
    @NotBlank private String terminal;        // 核销终端
}
```

**流程（锁内）**
1. 读 team_appointment FOR UPDATE（双保险）
2. 校验状态 ∈ {booked, partial_writeoff} 且非 ended/expired
3. 校验 writeoff_item.status != voided/expired
4. `writeoff_persons += qty`，且 `writeoff_persons ≤ total_persons`
5. 状态推进：
   - 0 → ≥1：booked → partial_writeoff
   - ≥total → fully_writeoff
6. 子项 writeoff_qty 累加
7. 写 audit_log

**特别说明**：场次结束扫描（定时任务）独立处理 `partial_writeoff_ended / no_show`

### 3.4 jst-channel 渠道提现 ⭐

```
POST /jst/channel/settlement/apply       渠道发起
POST /jst/channel/settlement/approve     平台审批 → 触发负向抵扣
POST /jst/channel/settlement/pay         打款回写
权限：jst:rebate:settlement:approve
锁  ：lock:rebate:settle:{channelId}
```

**approve 逻辑（核心）**
1. 状态机 SM-9 跃迁
2. 锁定该 channel 所有 `rebate_ledger.status='negative'` 记录
3. 按 ledger_id 倒序，逐条抵扣 `apply_amount`，每条变更 `status='paid'`
4. `negative_offset_amount = sum(已抵扣)`
5. `actual_pay_amount = apply_amount - negative_offset_amount`
6. 若 actual_pay_amount ≤ 0：直接置 `paid`，不打款，结余结转
7. 写 audit_log

### 3.5 jst-user 临时档案合并认领 ⭐

```
POST /jst/participant/claim/auto           系统自动（注册触发）
POST /jst/participant/claim/manual         用户手动绑定
POST /jst/participant/claim/admin          管理员手动合并
POST /jst/participant/claim/revoke         撤销认领
权限：jst:participant:claim
事务：必须
```

**自动认领规则**
- 触发时机：用户注册 / 完善资料后
- 匹配：`guardian_mobile + name` 唯一命中 1 条 unclaimed 档案
- 多候选 → `pending_manual`（不自动合并）
- 命中后写 `participant_user_map` + 更新 `participant.claim_status='auto_claimed'` + `claimed_user_id`
- **不**回写 enroll_record / order 等流水的主键，仅通过 map 关联

### 3.6 jst-points 积分账户调整 ⭐

```
POST /jst/points/adjust
权限：jst:points:adjust
事务：必须
锁  ：基于 points_account.version 乐观锁
```

**ReqDTO**
```java
public class PointsAdjustReqDTO {
    @NotNull private String ownerType;   // student/channel
    @NotNull private Long ownerId;
    @NotNull private Long pointsChange;  // 可正可负
    @NotNull private String sourceType;  // manual/award/...
    @NotBlank private String reason;
}
```

**流程**
1. SELECT account WHERE id=? AND version=?
2. 校验 available_points + pointsChange ≥ 0
3. UPDATE account SET available_points=?, version=version+1 WHERE id=? AND version=?
4. INSERT points_ledger
5. 失败重试 ≤3 次，否则抛 `ServiceException("积分调整并发冲突")`

### 3.7 jst-event 动态表单模板审核 + 报名快照

```
GET  /jst/enroll/form/template/{id}/render   获取生效模板渲染
POST /jst/enroll/form/template/save          保存草稿(版本+1)
POST /jst/enroll/form/template/submit        提交审核
POST /jst/enroll/form/template/audit         平台审核
权限：jst:contest:formTemplate:edit/audit
```

**关键约束**
- 模板每次保存 `template_version += 1`
- 报名时 `enroll_record` 必须存 `form_snapshot_json` + `template_version`，**不可后期回写**
- 模板审核 reject 不影响已通过版本

---

## 4. 通用响应规范

### 成功
```java
return AjaxResult.success(resVO);
return AjaxResult.success("操作成功", resVO);
```

### 失败
```java
throw new ServiceException("业务原因", BizErrorCode.ORDER_REFUND_DENIED.getCode());
```

### 分页
```java
@GetMapping("/list")
@PreAuthorize("@ss.hasPermi('jst:order:list')")
public TableDataInfo list(OrderQueryReqDTO query) {
    startPage();
    List<OrderListResVO> list = orderService.selectList(query);
    return getDataTable(list);
}
```

### 异常码段
| 段 | 模块 | 范围 |
|---|---|---|
| 1xxxx | jst-user | 10000-10999 |
| 2xxxx | jst-event | 20000-20999 |
| 3xxxx | jst-order | 30000-30999 |
| 4xxxx | jst-channel | 40000-40999 |
| 5xxxx | jst-points | 50000-50999 |
| 6xxxx | jst-organizer | 60000-60999 |
| 7xxxx | jst-marketing | 70000-70999 |
| 8xxxx | jst-message | 80000-80999 |
| 9xxxx | jst-risk | 90000-90999 |
| 99xxx | jst-finance | 99000-99999 |

---

## 5. 第三方集成边界

| 集成 | 模块 | 落点 |
|---|---|---|
| 微信小程序登录 | jst-user | `WxAuthService.code2Session` |
| 微信支付 / 退款 | jst-order | `WxPayService` 抽象 + 回调 `/jst/pay/notify/wechat` |
| 短信 | jst-message | `SmsChannelService` 接口（阿里云/腾讯云双实现） |
| OpenMAIC AI 课程 | jst-event | `MaicCourseService`，按 maic_source_id 拉取 |
| OSS（图片/凭证/合同） | jst-common | RuoYi 自带 + S3 兼容封装 |

---

## 6. 防雷点速查

| 风险 | 落点 | 防御 |
|---|---|---|
| 团队核销超卖 | SM-13 | Redisson 锁 + 状态机校验 |
| 商城兑换库存 | mall_goods.stock | Redisson 锁 + 乐观锁 |
| 积分并发扣减 | points_account | version 乐观锁 + 重试 |
| 重复绑定渠道 | binding | Redisson 锁 lock:bind:{userId} |
| 退款资金顺序 | 现→分→券 | 单一事务 + 状态机 |
| 返点负向抵扣 | rebate_settle.approve | 锁 + 倒序扫描 negative |
| 表单快照漂移 | enroll_record | form_snapshot_json 不可改 |
| 临时档案孤儿 | participant.user_id NULL | 流水存 participant_id 而非 user_id |

---

## 阶段 1-3 全部交付物

| 文件 | 内容 |
|---|---|
| `00-数据模型提取报告.md` | 51 张表的字段提取报告 |
| `ddl/01~10-jst-*.sql` | 10 个 SQL 文件，51 张业务表 |
| `11-状态机定义.md` | 25 个状态机 + Service 实现规范 |
| `12-API边界与服务划分.md` | Maven 模块 + 7 个核心 API 契约 + 防雷点 |

下一步可进入：
- 编写各模块 Maven `pom.xml` 与目录骨架
- 编写枚举类（enums 包，覆盖全部 25 个状态机）
- 选择某个核心域开始 Service/Controller 实现（建议从 `jst-user` 注册+认领 开始）
