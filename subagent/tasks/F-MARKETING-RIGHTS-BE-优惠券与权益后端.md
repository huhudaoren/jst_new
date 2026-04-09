# 任务卡 F-MARKETING-RIGHTS-BE - 优惠券 + 权益后端全套

## 阶段 / 模块
阶段 C 补齐 / **jst-marketing**（单模块打包）

## 业务背景

PRD §13 优惠券与营销 + §14 权益核销。C2/C4 已经在"使用券"和"券回退"路径上打通了锁定/核销/回退语义，但：
- **缺券模板管理 admin 接口**（手动运营券）
- **缺 wx 端领券中心 + 我的券列表 + 下单选券预计算**
- **缺营销活动专题页后端**（简单：按时间窗查 campaign + 关联券模板）
- **缺权益模板 admin 接口**
- **缺权益中心**（我的权益列表 / 详情 / 申请核销）

C6/C7 的核销引擎只处理预约类核销子项，**权益申请核销是另一条路径**（对应 writeoff-apply.html），本任务新建。

## ⚠️ 实测 Schema（主 Agent 已 probe）

### jst_coupon_template
- `coupon_template_id / coupon_name / coupon_type(full_reduce/discount/direct) / face_value / discount_rate / threshold_amount / applicable_role / applicable_contest_ids(JSON) / valid_days / valid_start / valid_end / stack_rule / status(draft/on/off)`

### jst_user_coupon
- `user_coupon_id / coupon_template_id / user_id / issue_batch_no / issue_source / status(new/locked/used/expired/voided) / valid_start / valid_end / order_id / use_time`

### jst_rights_template
- `rights_template_id / rights_name / rights_type(course/offline_activity/customer_service/etc) / quota_mode(unlimited/count/amount) / quota_value / valid_days / writeoff_mode(self_apply/scan) / applicable_role / status`

### jst_user_rights
- `user_rights_id / rights_template_id / owner_type / owner_id / source_type / source_ref_id / remain_quota / valid_start / valid_end / status(available/applying/approved/used/expired/voided/rejected)`

## ⚠️ Campaign 表检查
主 Agent 未 probe 到 `jst_campaign` 表，子 Agent 先 `SHOW TABLES LIKE 'jst_%campaign%'` 确认：
- 若存在：按真实列实现 campaign list/detail 接口
- 若不存在：本期 campaign 接口**返回固定 mock 3 条**（结合 coupon_template 组装），任务卡 §TODO 留痕

## CCB 决策

| # | 决策 | 选择 |
|---|---|---|
| 1 | 券领取方式 | 手动领券中心（主动 claim）+ 订单流程自动发券（C2 已做，不重写） |
| 2 | 用户可重复领同一券？ | 按 `coupon_template.stack_rule` 控制：`once` 限 1 次 / `unlimited` 不限 |
| 3 | 下单选券 | 前端传 orderAmount → 后端计算最优券 + 实时校验门槛 |
| 4 | 权益申请核销 | 学生发 apply → 自动通过（本期无人工审核）或按 writeoff_mode 分叉：`self_apply` 直接 used，`scan` 交给 C6 writeoff 走扫码 |
| 5 | 权益余量 | `remain_quota` 扣减；扣减到 0 自动置 used |
| 6 | 营销活动 | 若无 campaign 表本期返 mock；前端不阻塞 |
| 7 | 权限 | admin：`jst:marketing:coupon:*` / `jst:marketing:rights:*`；wx：`jst:marketing:coupon:my` / `rights:my` |

## 必读

1. PRD §13 / §14
2. `RuoYi-Vue/jst-order/.../service/impl/OrderServiceImpl.java` C2 的券锁定 & 核销代码（避免语义冲突）
3. `RuoYi-Vue/jst-order/.../service/impl/RefundServiceImpl.java` C4 的券回退代码
4. `架构设计/ddl/07-jst-marketing.sql`
5. 原型：`coupon-center.html` / `coupon-select.html` / `rights-center.html` / `rights-detail.html` / `writeoff-apply.html` / `campaign-topic.html`

## 接口

### admin 优惠券
- GET/POST/PUT/DELETE `/jst/marketing/coupon/template` CRUD + publish/offline
- POST `/jst/marketing/coupon/template/{id}/issue` — 批量发券（入参 userIds 数组）
  - 权限：`jst:marketing:coupon:issue`
  - 写 jst_user_coupon 批量
  - 校验 stack_rule

### wx 优惠券
- GET `/jst/wx/coupon/template/claimable` — 可领券列表（status=on + valid）
- POST `/jst/wx/coupon/claim?templateId=` — 学生主动领券
- GET `/jst/wx/coupon/my?status=&pageNum=` — 我的券（tab：未使用/已使用/已过期）
- POST `/jst/wx/coupon/select` — 下单选券预计算
  - 入参：`{orderAmount, contestId?, goodsId?, candidateCouponIds?}`
  - 返回：`{bestCouponId, bestDiscount, netPayAmount, alternatives:[...]}`
  - **仅计算不锁定**，真正锁定由 C2 下单接口做

### admin 权益
- GET/POST/PUT/DELETE `/jst/marketing/rights/template` CRUD + publish/offline
- POST `/jst/marketing/rights/template/{id}/issue` — 批量发权益
  - 入参 userIds 数组 + optional remainQuota override
  - 写 jst_user_rights

### wx 权益中心
- GET `/jst/wx/rights/my?status=&pageNum=` — 我的权益列表（tab：可用/申请中/已使用/已过期）
- GET `/jst/wx/rights/{userRightsId}` — 详情（含 template 基本信息 + 核销历史）
- POST `/jst/wx/rights/{userRightsId}/apply-writeoff`
  - 入参：`{writeoffAmount?, remark?}`
  - 逻辑：
    1. 校验 owner + status='available' + valid
    2. 锁 `lock:rights:writeoff:{userRightsId}`
    3. `writeoff_mode='self_apply'`：直接 approve → `remain_quota -=`，若 quota_mode=unlimited 或 remain_quota>0 保持 available；否则置 used
    4. `writeoff_mode='scan'`：本期拒绝（让用户走 C6 扫码入口）→ 40033 WRITEOFF_REQUIRE_SCAN
    5. 写入一条 `jst_rights_writeoff_history`（**先 probe 此表是否存在，不存在则只在 jst_user_rights.remark 记录 + 返回成功**）

### wx 营销活动
- GET `/jst/wx/campaign/list?pageNum=` — 进行中的活动
- GET `/jst/wx/campaign/{id}` — 活动详情（含可领券列表 + 倒计时）
- 若 `jst_campaign` 表不存在，返回 mock 3 条：包含 `campaignId / title / bannerUrl / startTime / endTime / linkedCouponTemplateIds / description`

## 交付物

新增：
- `jst-marketing/.../dto/` — CouponTemplateSaveDTO / CouponIssueDTO / CouponSelectDTO / RightsTemplateSaveDTO / RightsIssueDTO / RightsWriteoffApplyDTO
- `jst-marketing/.../vo/` — CouponTemplateVO / ClaimableCouponVO / MyCouponVO / CouponSelectResVO / RightsTemplateVO / MyRightsVO / RightsDetailVO / CampaignVO
- `jst-marketing/.../enums/` — CouponType / CouponStatus / RightsStatus / QuotaMode / WriteoffMode
- `jst-marketing/.../service/` — CouponTemplateService / CouponUserService / RightsTemplateService / RightsUserService / CampaignService + 各 Impl
- `jst-marketing/.../controller/admin/` — CouponTemplateAdminController / RightsTemplateAdminController
- `jst-marketing/.../controller/wx/` — WxCouponController / WxRightsController / WxCampaignController
- `jst-marketing/.../mapper/` CouponTemplateMapperExt / UserCouponMapperExt / RightsTemplateMapperExt / UserRightsMapperExt + xml

修改：
- `jst-common/.../BizErrorCode.java` 追加：
  - `COUPON_TEMPLATE_NOT_FOUND(60050)` / `COUPON_CLAIM_LIMIT(60051)` / `COUPON_EXPIRED(60052)`
  - `RIGHTS_TEMPLATE_NOT_FOUND(60060)` / `RIGHTS_NOT_OWNED(60061)` / `RIGHTS_EXPIRED(60062)` / `RIGHTS_REQUIRE_SCAN(60063)` / `RIGHTS_STATUS_INVALID(60064)`
- `架构设计/15-Redis-Key与锁规约.md` 登记 `lock:rights:writeoff:{userRightsId}` / `lock:coupon:claim:{userId}:{templateId}`
- `架构设计/ddl/99-test-fixtures.sql` 追加：
  - 3 个券模板（满减 50-10 / 折扣 9折 / 直减 20）
  - 2 个权益模板（课程学习 / 线下活动）
  - 用户 9001 预置 2 个可用券 + 1 个可用权益
- `test/wx-tests.http` + `test/admin-tests.http` 追加 F-MR 段

## 测试

**优惠券**
- MR-CP-W1 claimable 列表
- MR-CP-W2 学生领券
- MR-CP-W3 once 规则重复领应 60051
- MR-CP-W4 my coupon 列表
- MR-CP-W5 下单选券（满 50 减 10 订单 80 → best discount 10）
- MR-CP-A1 admin 券模板 CRUD
- MR-CP-A2 admin 批量发券

**权益**
- MR-RT-W1 my rights 列表
- MR-RT-W2 权益详情
- MR-RT-W3 self_apply 申请核销成功
- MR-RT-W4 scan 类型应 60063
- MR-RT-W5 已 used 再申请应 60064
- MR-RT-A1 admin 权益模板 CRUD
- MR-RT-A2 admin 批量发权益

**营销活动**
- MR-CM-W1 活动列表
- MR-CM-W2 活动详情 + 领券

## DoD
- [ ] mvn compile SUCCESS
- [ ] .http 全绿（.http 段子 Agent 补齐但不要求逐条点跑）
- [ ] 任务报告 `F-MARKETING-RIGHTS-BE-回答.md`
- [ ] commit: `feat(jst-marketing): F-MARKETING-RIGHTS-BE 券 + 权益 + 营销 全套后端`

## 不许做
- ❌ 不许触碰 C2 / C4 已有的 user_coupon lock/use/void 路径（会冲突）
- ❌ 不许在权益 apply-writeoff 里实现真实扫码（那是 C6 的事）
- ❌ 不许自建 campaign 表（先 probe 决定）
- ❌ 不许跨 import jst-order / jst-user entity

## 依赖：C2 ✅ / C4 ✅
## 优先级：高

---
派发时间：2026-04-09
