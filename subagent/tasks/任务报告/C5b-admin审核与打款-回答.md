# 任务报告 - C5b admin审核与打款

## A. 任务前自检
1. 涉及表：`jst_rebate_settlement`、`jst_rebate_ledger`、`jst_payment_pay_record`
2. 涉及状态机：`SM-9(jst_rebate_settlement.status)`、`SM-8(jst_rebate_ledger.status)`
3. 涉及权限标识：`jst:channel:withdraw:list`、`jst:channel:withdraw:query`、`jst:channel:withdraw:audit`、`jst:channel:withdraw:execute`
4. 涉及锁名：`lock:channel:withdraw:audit:{settlementId}`、`lock:channel:withdraw:execute:{settlementId}`、`lock:channel:withdraw:offset:{channelId}`
5. 事务边界：`ChannelWithdrawAdminServiceImpl.approve`、`reject`、`execute`
6. 出参 VO：`WithdrawAdminListVO`、`WithdrawAdminDetailVO`，详情里包含 `previewNegativeOffset`
7. 复用样板：`jst-order` 的退款审核/执行拆分模式，以及 C5a 的 `ChannelWithdrawServiceImpl`

## B. 交付物清单
新增文件：
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\dto\WithdrawAuditDTO.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\vo\WithdrawAdminListVO.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\vo\WithdrawAdminDetailVO.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\service\ChannelWithdrawAdminService.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\service\impl\ChannelWithdrawAdminServiceImpl.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\controller\ChannelWithdrawAdminController.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\payout\PayoutService.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\payout\impl\MockPayoutServiceImpl.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\payout\impl\RealPayoutServiceImpl.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\mapper\lookup\PaymentPayRecordLookupMapper.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\resources\mapper\channel\lookup\PaymentPayRecordLookupMapper.xml`

修改文件：
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\mapper\RebateSettlementMapperExt.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\java\com\ruoyi\jst\channel\mapper\RebateLedgerMapperExt.java`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\resources\mapper\channel\RebateSettlementMapperExt.xml`
- `D:\coding\jst_v1\RuoYi-Vue\jst-channel\src\main\resources\mapper\channel\RebateLedgerMapperExt.xml`
- `D:\coding\jst_v1\RuoYi-Vue\jst-common\src\main\java\com\ruoyi\jst\common\exception\BizErrorCode.java`
- `D:\coding\jst_v1\架构设计\15-Redis-Key与锁规约.md`
- `D:\coding\jst_v1\架构设计\ddl\99-test-fixtures.sql`
- `D:\coding\jst_v1\test\admin-tests.http`

## C. 结果说明
已实现：
1. admin 提现单分页列表与详情查询
2. `pending -> approved` 审核通过，且在审核阶段完成同渠道 `negative` 台账自动抵扣
3. `pending -> rejected` 驳回，并把正向 `in_review` 台账恢复为 `withdrawable`
4. `approved -> paid` 独立打款执行，落 `jst_payment_pay_record`
5. mock / real 两套打款实现占位，默认 `jst.payout.enabled=mock`
6. execute 幂等：已 `paid` 的 settlement 再次执行直接成功返回

关键实现约束：
1. 负向抵扣只在 approve 阶段发生，按 `create_time asc, ledger_id asc` 消耗
2. 被抵扣的负向台账只写入 `settlement_id`，状态保持 `negative`
3. `actual_pay_amount < 0` 时抛 `40015`
4. execute 只推进正向 `in_review -> paid` 台账

## D. 编译结果
通过：
- `mvn -pl jst-channel,jst-common -am compile -DskipTests`
- 结果：`BUILD SUCCESS`

未通过：
- `mvn compile -DskipTests`
- 失败模块：`jst-points`
- 失败原因：仓库现有 `MallExchangeServiceImpl` / `MallGoodsServiceImpl` 缺少大量方法与错误码，和本次 C5b 改动无直接关系；本次未扩展修复该模块

## E. .http 测试补充
已追加 `C5b-A1 ~ C5b-A13` 测试块到 `D:\coding\jst_v1\test\admin-tests.http`，覆盖：
1. admin 登录
2. 列表分页
3. 详情 `previewNegativeOffset`
4. 无负向抵扣 approve
5. 有负向抵扣 approve
6. overflow 失败 `40015`
7. reject happy path
8. reject approved 失败
9. execute happy path
10. execute 幂等
11. execute pending 失败
12. channel 角色访问 admin 接口被拒绝

说明：
- `.http` 块已补齐，但本地未逐条实际点击执行

## F. 风险与说明
1. 全仓编译未满足任务卡 DoD，不是 C5b 代码面失败，而是 `jst-points` 当前工作区本身存在未完成实现
2. `previewNegativeOffset` 详情预览按“当前未绑定 negative 台账”实时计算，适合 pending 场景；一旦 approve 后，前端应以 settlement 实际字段为准
3. `RealPayoutServiceImpl` 仍是占位抛错，没有接真实三方打款

## G. 自检清单
- [x] 后台接口已显式 `@PreAuthorize`
- [x] 写接口已加操作日志注解
- [x] 写接口已加 `@Transactional(rollbackFor = Exception.class)`
- [x] 审核与打款拆成独立接口
- [x] 负向抵扣锁、审核锁、执行锁已登记
- [x] finance 落表通过 lookup mapper
- [x] mapper XML 未使用 `${}`
- [x] fixture 已补齐 C5b 独立场景
- [x] 模块级编译已通过
- [ ] 全仓编译通过
