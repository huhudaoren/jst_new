# 任务报告 - C9 商城售后 + F-POINTS-CENTER-BE 积分中心

## A. 任务前自检
1. 涉及表：`jst_mall_exchange_order`、`jst_refund_record`、`jst_points_account`、`jst_points_ledger`、`jst_growth_ledger`、`jst_level_config`、`jst_user_coupon`、`jst_user_rights`、`jst_order_main`、`jst_payment_record`
2. 涉及锁名：`lock:mall:aftersale:{exchangeId}`、`lock:points:freeze:{userId}`
3. 涉及权限：`jst:points:mall:aftersale:apply`、`jst:points:mall:aftersale:my`、`jst:points:mall:aftersale:audit`、小程序角色 `jst_student/jst_channel`
4. 事务边界：`MallAftersaleService.apply/approve/reject`
5. 复用样板：
   - `jst-order/.../RefundServiceImpl.java` 的退款拆分模式
   - `jst-points/.../MallExchangeServiceImpl.java` 的兑换单/积分锁处理
6. 返回对象：
   - 售后：`AftersaleApplyResVO`、`AftersaleListVO`、`AftersaleDetailVO`
   - 积分中心：`PointsCenterSummaryVO`、`LevelVO`、`PointsLedgerVO`、`GrowthLedgerVO`、`PointsTaskVO`

## B. 交付物清单
### 新增文件
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/dto/AftersaleApplyDTO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/dto/AftersaleAuditDTO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/dto/AftersaleQueryReqDTO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/enums/AftersaleStatus.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/AftersaleApplyResVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/AftersaleListVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/AftersaleDetailVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/PointsCenterSummaryVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/LevelVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/PointsTaskVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/PointsLedgerVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/vo/GrowthLedgerVO.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/RefundRecordLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/RefundRecordLookupMapper.xml`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/LevelConfigLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/LevelConfigLookupMapper.xml`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/GrowthLedgerLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/GrowthLedgerLookupMapper.xml`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/service/MallAftersaleService.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/service/PointsCenterService.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/service/impl/MallAftersaleServiceImpl.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/service/impl/PointsCenterServiceImpl.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/controller/wx/WxMallAftersaleController.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/controller/MallAftersaleAdminController.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/controller/wx/WxPointsCenterController.java`

### 修改文件
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/PointsAccountLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/PointsAccountLookupMapper.xml`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/MallOrderLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/MallOrderLookupMapper.xml`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/UserCouponLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/UserCouponLookupMapper.xml`
- `RuoYi-Vue/jst-points/src/main/java/com/ruoyi/jst/points/mapper/lookup/UserRightsLookupMapper.java`
- `RuoYi-Vue/jst-points/src/main/resources/mapper/points/lookup/UserRightsLookupMapper.xml`
- `架构设计/15-Redis-Key与锁规约.md`
- `架构设计/ddl/99-test-fixtures.sql`
- `test/wx-tests.http`
- `test/admin-tests.http`

## C. 实现说明
### Part A: 商城售后
1. 新增小程序售后申请、我的售后列表、售后详情接口。
2. 新增后台售后列表、详情、通过、驳回接口。
3. 售后链路统一走 `RefundRecordLookupMapper` 访问 `jst_refund_record` / `jst_order_main` / `jst_payment_record`，避免在新代码里直接跨模块 import `jst-order` 实体。
4. 申请时校验：
   - 虚拟商品只允许 `completed + refund_only`
   - 实物 `pending_ship` 只允许 `refund_only`
   - 实物 `shipped/completed` 只允许 `return_refund`
   - 已使用用户券直接报 `60031`
   - 已消耗权益报 `60030`
   - 存在阻塞售后单时报 `60032`
5. 审核通过时执行：
   - 现金退款走 `WxPayService.refund(...)`，本地 `jst.wxpay.enabled=false` 时按 mock 路径放行
   - 积分退款回充 `jst_user` + `jst_points_account`，并写 `change_type='aftersale_refund'`
   - 虚拟券改为 `refunded`
   - 虚拟权益改为 `revoked`
   - 待发货实物回补库存
6. 审核驳回时恢复兑换单原状态，并把售后状态记为 `rejected`。

### Part B: 积分中心
1. 新增 `summary` / `levels` / `points ledger` / `growth ledger` / `tasks` 五个接口。
2. `summary` 从 `jst_points_account + jst_user` 读取积分、成长值、累计获取/消耗，并按 `jst_level_config` 推导当前等级、下一级和差值。
3. `levels` 按 `applicable_role` 过滤，并补 `unlocked` 字段。
4. `points ledger` 与 `growth ledger` 分开分页查询。
5. `tasks` 返回 5 条固定 mock 数据，对齐原型和接口说明。

## D. 关键约束与偏差说明
1. 任务卡接口描述里写了 `pending_audit`，但实际库表与 C4 模板都使用 `pending/approved/rejected/refunding/completed`，本次实现沿用真实表结构状态值。
2. 任务卡 CCB 表里写了“任务占位返回空列表”，但接口章节、原型和验收项 `PC-W5` 都要求 5 条 mock；本次按后者实现。
3. `jst_mall_exchange_order.status` 仍沿用现有状态机，没有新增 `refunded`，售后完成后落到 `closed + aftersale_status=completed`。

## E. mvn compile 结果
命令：`mvn -pl jst-points -am compile`

结果：
- `[INFO] BUILD SUCCESS`
- `Total time: 11.200 s`

## F. .http 覆盖情况
### wx-tests.http
- 追加 `C9-W0 ~ C9-W7`
- 追加 `F-PC-W0 ~ F-PC-W5`

### admin-tests.http
- 追加 `C9-A0 ~ C9-A5`

说明：
- 用例块已补齐，但这一轮没有逐条点跑。

## G. Fixtures 补充
1. 新增积分中心用户 `9001`，积分/成长值口径固定为：
   - `availablePoints=1250`
   - `frozenPoints=100`
   - `totalEarn=3500`
   - `totalSpend=2150`
   - `growthValue=850`
   - `currentLevel=L2`
2. 新增等级配置 `L1~L4`。
3. 新增售后兑换夹具：
   - `99211` 虚拟券未使用，可申请售后
   - `99214` 虚拟券已使用，不可申请售后
   - `99212` 实物待发货，可仅退款
   - `99213` 实物已发货，可退货退款

## H. 任务卡之外我额外做的事
1. 为积分中心额外补了 `PointsLedgerVO`、`GrowthLedgerVO`，让流水接口出参更清晰，不直接暴露表实体。
2. 在 `MallOrderLookupMapper` 里补了 map 形态的订单/支付查询和退款状态更新，避免新售后服务继续依赖跨模块实体。

## I. 自检清单
- [x] 写接口有权限注解
- [x] 写接口带 `@Transactional(rollbackFor = Exception.class)`
- [x] 写接口带 `@OperateLog`
- [x] ReqDTO 带 JSR-303
- [x] 售后链路使用 `lock:mall:aftersale:{exchangeId}`
- [x] 积分退款复用 `lock:points:freeze:{userId}`
- [x] 新增跨模块访问全部走 lookup mapper
- [x] `mvn compile` 已通过
- [x] 交付报告已写入任务报告目录
