# 任务报告 - F-MARKETING-RIGHTS-BE 优惠券与权益后端

## A. Step 1 阅读文档结论
1. 已阅读 `D:\coding\jst_v1\subagent\BACKEND_AGENT_PROMPT.md` 与任务卡 `D:\coding\jst_v1\subagent\tasks\F-MARKETING-RIGHTS-BE-优惠券与权益后端.md`。
2. 已对照 `架构设计/ddl/07-jst-marketing.sql`、`OrderServiceImpl.java`、`RefundServiceImpl.java`、状态机与 API 契约完成实现边界确认。
3. 关键 probe 结论：
   - 本地 schema **不存在** `jst_campaign` 表，本期按任务卡要求返回固定 mock 3 条活动。
   - 本地 schema **不存在** `jst_rights_writeoff_history`，真实存在的是 `jst_rights_writeoff_record`，本次详情历史与自助核销记录均落该表。
   - C2/C4 已有 `user_coupon` 锁定/核销/回退链路未改动，本次仅新增模板管理、领券中心、选券预计算、权益中心与活动专题接口。

## B. Step 2 自检答题
1. 涉及表：`jst_coupon_template`、`jst_user_coupon`、`jst_coupon_issue_batch`、`jst_rights_template`、`jst_user_rights`、`jst_rights_writeoff_record`、只读 lookup `jst_user` / `jst_contest`
2. 涉及状态机：SM-16 优惠券、SM-17 用户权益
3. 涉及权限标识：
   - admin：`jst:marketing:coupon:list/query/add/edit/remove/publish/offline/issue`
   - admin：`jst:marketing:rights:list/query/add/edit/remove/publish/offline/issue`
   - wx：`@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')`
   - wx：`@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:rights:my')`
4. 涉及锁名：`lock:coupon:claim:{userId}:{templateId}`、`lock:rights:writeoff:{userRightsId}`
5. 事务边界：
   - `CouponTemplateServiceImpl.create/update/delete/publish/offline/issue`
   - `CouponUserServiceImpl.claim`
   - `RightsTemplateServiceImpl.create/update/delete/publish/offline/issue`
   - `RightsUserServiceImpl.applyWriteoff`
6. VO/Res 交付：`CouponTemplateVO`、`ClaimableCouponVO`、`MyCouponVO`、`CouponSelectResVO`、`RightsTemplateVO`、`MyRightsVO`、`RightsDetailVO`、`CampaignVO`
7. 不许做约束核对：
   - [x] 未跨 import `jst-order` / `jst-user` entity
   - [x] 未触碰 C2/C4 既有 `user_coupon` lock/use/void 路径
   - [x] 未自建 `jst_campaign` 表，活动接口走 mock 3 条
   - [x] 未在权益 apply-writeoff 中实现真实扫码

## C. 交付物清单
### 新增
- DTO：
  - `RuoYi-Vue/jst-marketing/.../dto/CouponTemplateSaveDTO.java`
  - `RuoYi-Vue/jst-marketing/.../dto/CouponIssueDTO.java`
  - `RuoYi-Vue/jst-marketing/.../dto/CouponSelectDTO.java`
  - `RuoYi-Vue/jst-marketing/.../dto/RightsTemplateSaveDTO.java`
  - `RuoYi-Vue/jst-marketing/.../dto/RightsIssueDTO.java`
  - `RuoYi-Vue/jst-marketing/.../dto/RightsWriteoffApplyDTO.java`
- VO：
  - `RuoYi-Vue/jst-marketing/.../vo/CouponTemplateVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/ClaimableCouponVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/MyCouponVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/CouponSelectResVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/RightsTemplateVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/MyRightsVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/RightsDetailVO.java`
  - `RuoYi-Vue/jst-marketing/.../vo/CampaignVO.java`
- 枚举：
  - `CouponType.java` / `CouponStatus.java` / `RightsStatus.java` / `QuotaMode.java` / `WriteoffMode.java`
- Service：
  - `CouponTemplateService.java` / `CouponUserService.java` / `RightsTemplateService.java` / `RightsUserService.java` / `CampaignService.java`
  - 对应实现 `CouponTemplateServiceImpl.java` / `CouponUserServiceImpl.java` / `RightsTemplateServiceImpl.java` / `RightsUserServiceImpl.java` / `CampaignServiceImpl.java`
- Controller：
  - `controller/admin/CouponTemplateAdminController.java`
  - `controller/admin/RightsTemplateAdminController.java`
  - `controller/wx/WxCouponController.java`
  - `controller/wx/WxRightsController.java`
  - `controller/wx/WxCampaignController.java`
- Mapper：
  - `CouponTemplateMapperExt.java`
  - `UserCouponMapperExt.java`
  - `RightsTemplateMapperExt.java`
  - `UserRightsMapperExt.java`
  - `lookup/MarketingUserLookupMapper.java`
  - `lookup/ContestLookupMapper.java`
  - 对应 XML 6 个
- Helper：`MarketingSupport.java`

### 修改
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `架构设计/15-Redis-Key与锁规约.md`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/wx-tests.http`
- `test/admin-tests.http`

## D. 本次实现摘要
1. admin 优惠券模板：完成 REST 风格 CRUD、publish/offline、批量发券；批量发券写入 `jst_coupon_issue_batch`，并按 `stack_rule=once` 过滤重复发放。
2. wx 优惠券中心：完成 claimable 模板列表、主动领券、我的优惠券列表、选券预计算。选券只计算不锁定，仍由 C2 下单链路负责真正 lock/use。
3. admin 权益模板：完成 CRUD、publish/offline、批量发权益；发放时根据 `jst_user.user_type` 归一化 `owner_type=student/channel`。
4. wx 权益中心：完成我的权益列表、详情、申请核销；`self_apply` 直接写 `jst_rights_writeoff_record` 并扣减额度，`scan` 直接返回 `60063`。
5. wx 营销活动：因缺少 `jst_campaign`，返回固定 mock 3 条活动详情，并按关联模板动态组装可领券列表与倒计时。
6. 兼容处理：
   - `CouponType` 支持 `direct` -> `direct_reduce`
   - `QuotaMode` 兼容旧值 `times/period`
   - `WriteoffMode` 兼容旧值 `online_auto/offline_confirm`

## E. mvn compile 结果
执行目录：`D:\coding\jst_v1\RuoYi-Vue`
命令：`mvn compile -DskipTests`
结果：`[INFO] BUILD SUCCESS`

## F. .http 测试块追加
### wx-tests.http
- `MR-CP-W1 ~ MR-CP-W5`
- `MR-RT-W1 ~ MR-RT-W5`
- `MR-CM-W1 ~ MR-CM-W2`

### admin-tests.http
- `MR-CP-A1 ~ MR-CP-A2`
- `MR-RT-A1 ~ MR-RT-A2`

说明：按任务卡豁免，本轮仅追加测试块，**未实际启动服务逐条执行** `.http`。

## G. fixture 追加说明
1. 新增 3 个优惠券模板：
   - `9761` 满 50 减 10
   - `9762` 9 折券
   - `9763` 直减 20
2. 新增 2 个权益模板：
   - `9771` 自助核销课程权益
   - `9772` 扫码型线下活动权益
3. 为 `9001` 预置：
   - 2 张可用券：`9861` / `9862`
   - 2 条可用权益：`9871` / `9872`
4. 任务卡写的是“1 个可用权益”，本次额外补了第 2 条扫码型权益，目的是让 `MR-RT-W4 scan -> 60063` 可以独立落在 fixture 场景里，不依赖 admin 先发权益。

## H. 风险 / TODO
1. `jst_campaign` 缺表：本期已按任务卡走 mock 3 条；若后续真正建表，需要将 `CampaignServiceImpl` 替换为真实查询实现。
2. `jst_rights_writeoff_history` 与本地 schema 不一致：当前以真实表 `jst_rights_writeoff_record` 为准，报告已留痕。
3. `.http` 未实跑：接口运行态、权限菜单是否已在目标环境授权，仍需主流程阶段汇总时统一验证。

## I. 任务外处理
1. 重建并清理了 `BizErrorCode.java`，避免新增营销错误码时受原文件乱码影响导致编译失败。
2. 在兼容旧 DDL 注释值的前提下，为营销模块补了一层枚举归一化，减少 `times/period`、`online_auto/offline_confirm` 与本期任务卡值的冲突。
