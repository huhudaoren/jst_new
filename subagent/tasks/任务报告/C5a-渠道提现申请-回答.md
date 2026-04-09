# 任务报告 - C5a 渠道提现申请

## A. Step 2 自检答题
1. 涉及表：
   - 写表：`jst_rebate_settlement`、`jst_rebate_ledger`
   - 读表：`jst_channel`、`jst_contest`
2. 涉及状态机：
   - `SM-8 rebate_ledger`：`withdrawable -> in_review`、`in_review -> withdrawable`
   - `SM-9 rebate_settlement`：`(new) -> pending`、`pending -> closed`
3. 涉及权限标识：
   - `jst:channel:withdraw:apply`
   - `jst:channel:withdraw:my`
   - 控制器权限表达式：
     - 申请/取消：`@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:apply')`
     - 汇总/列表/详情：`@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:my')`
4. 涉及锁名：
   - `lock:channel:withdraw:apply:{channelId}`，等待 3s，持有 10s
   - `lock:channel:withdraw:cancel:{settlementId}`，等待 3s，持有 5s
5. 事务边界：
   - [ChannelWithdrawServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelWithdrawServiceImpl.java) 的 `applyWithdraw`、`cancelWithdraw` 均已显式 `@Transactional(rollbackFor = Exception.class)`
6. 主要 DTO / VO：
   - DTO：`WithdrawApplyDTO`、`WithdrawQueryReqDTO`、`RebateLedgerQueryReqDTO`
   - VO：`RebateSummaryVO`、`RebateLedgerListVO`、`WithdrawApplyResVO`、`WithdrawListVO`、`WithdrawDetailVO`
7. 复用样板：
   - [RefundServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/impl/RefundServiceImpl.java) 的锁内校验与状态流转风格
   - [ParticipantClaimServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java) 的事务与错误码组织方式

## B. 交付物清单
新增文件：
- [RebateSettlementStatus.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/enums/RebateSettlementStatus.java)
- [WithdrawApplyDTO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/WithdrawApplyDTO.java)
- [WithdrawQueryReqDTO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/WithdrawQueryReqDTO.java)
- [RebateLedgerQueryReqDTO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/dto/RebateLedgerQueryReqDTO.java)
- [RebateSummaryVO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/RebateSummaryVO.java)
- [RebateLedgerListVO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/RebateLedgerListVO.java)
- [WithdrawApplyResVO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/WithdrawApplyResVO.java)
- [WithdrawListVO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/WithdrawListVO.java)
- [WithdrawDetailVO.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/vo/WithdrawDetailVO.java)
- [RebateLedgerMapperExt.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/RebateLedgerMapperExt.java)
- [RebateSettlementMapperExt.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/RebateSettlementMapperExt.java)
- [ContestLookupMapper.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/ContestLookupMapper.java)
- [ChannelLookupMapper.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/ChannelLookupMapper.java)
- [ChannelWithdrawService.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/ChannelWithdrawService.java)
- [ChannelWithdrawServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelWithdrawServiceImpl.java)
- [WxChannelWithdrawController.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/controller/wx/WxChannelWithdrawController.java)
- [RebateLedgerMapperExt.xml](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/RebateLedgerMapperExt.xml)
- [RebateSettlementMapperExt.xml](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/RebateSettlementMapperExt.xml)
- [ContestLookupMapper.xml](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/lookup/ContestLookupMapper.xml)

修改文件：
- [BizErrorCode.java](/D:/coding/jst_v1/RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/exception/BizErrorCode.java)
- [15-Redis-Key与锁规约.md](/D:/coding/jst_v1/架构设计/15-Redis-Key与锁规约.md)
- [99-test-fixtures.sql](/D:/coding/jst_v1/架构设计/ddl/99-test-fixtures.sql)
- [wx-tests.http](/D:/coding/jst_v1/test/wx-tests.http)

## C. 实现说明
已完成以下接口与链路：
- `GET /jst/wx/channel/rebate/summary`
- `GET /jst/wx/channel/rebate/ledger/list`
- `POST /jst/wx/channel/withdraw/apply`
- `POST /jst/wx/channel/withdraw/{settlementId}/cancel`
- `GET /jst/wx/channel/withdraw/list`
- `GET /jst/wx/channel/withdraw/{settlementId}`

本次实现重点：
- 通过当前登录 `userId` 反查 `channelId`，并校验渠道已认证且启用
- 申请提现时对所选 ledger 做 `FOR UPDATE` 锁定，并校验归属、状态、方向、金额一致性
- 创建提现单后批量推进 `withdrawable -> in_review`
- 取消 `pending` 提现单时回退 `in_review -> withdrawable`
- 汇总接口按 4 KPI 聚合：可提现、审核中、累计打款、退款回退
- 明细和详情接口补齐 `contestName`

## D. mvn compile 结果
执行目录：`D:\coding\jst_v1\RuoYi-Vue`

执行命令：`mvn compile -DskipTests`

结果：
- `[INFO] BUILD SUCCESS`
- `[INFO] Total time: 42.191 s`
- 全量 18 模块编译通过

## E. .http 测试块追加情况
按任务卡当前阶段要求，本轮仅追加测试块，未实际启动服务执行。

已追加到 [wx-tests.http](/D:/coding/jst_v1/test/wx-tests.http)：
- `C5a-W1` 渠道登录
- `C5a-W2` rebate summary 4KPI 断言
- `C5a-W3` ledger list 按 `withdrawable` 过滤
- `C5a-W4` happy path 创建提现单
- `C5a-W4.1` 我的提现单列表
- `C5a-W4.2` 提现单详情
- `C5a-W5` 金额不一致失败，断言 `40012`
- `C5a-W6` 混入其他渠道 ledger 失败，断言 `40011`
- `C5a-W7` 混入已 `in_review` ledger 失败，断言 `40011`
- `C5a-W8` 创建可取消提现单
- `C5a-W8.1` 取消 `pending` 提现单成功
- `C5a-W9` 取消已关闭提现单失败，断言 `40013`
- `C5a-W10` 学生登录
- `C5a-W10.1` 学生角色访问渠道接口应返回 403

## F. 风险 / TODO
1. 本轮未实际启动 `ruoyi-admin` 或执行 `.http`，接口运行态验收待阶段汇总时统一验证。
2. 任务卡契约中写的是 `payee_account_json`、`invoice_json`、`cancel_time`，但当前 `jst_rebate_settlement` 真实表结构没有这 3 个字段。实现中已兼容现有 schema：
   - 收款账户快照写入 `bank_account_snapshot`
   - 发票信息快照写入 `remark`
   - 取消时间不单列存储，沿用 `update_time`
3. 当前仅覆盖 C5a 的申请与取消链路，`reviewing/approved/rejected/paid` 仍留给 C5b 继续承接。

## G. 启动报错复盘与修复
本轮联调时出现启动失败：
- 报错位置：`ChannelWithdrawServiceImpl`
- 报错信息：`required a bean of type 'com.fasterxml.jackson.databind.ObjectMapper' that could not be found`

根因：
- 我在 [ChannelWithdrawServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelWithdrawServiceImpl.java) 里单独注入了 `ObjectMapper`
- 但当前项目其余 impl 的 JSON 处理统一使用 `fastjson2`，例如 [FormTemplateServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/FormTemplateServiceImpl.java)、[PartnerApplyServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/PartnerApplyServiceImpl.java)、[MockWxPayServiceImpl.java](/D:/coding/jst_v1/RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/pay/impl/MockWxPayServiceImpl.java)
- 因此这次新增实现偏离了项目现有约定，导致运行期按 Spring Bean 注入 `ObjectMapper` 时失败

修复方案：
- 移除 `ObjectMapper` 注入
- 统一改为 `fastjson2`：
  - `JSON.toJSONString(...)` 序列化收款账户 / 发票信息
  - `JSON.parseObject(..., WithdrawDetailVO.PayeeAccount.class)` 反序列化收款账户
  - `JSON.parseObject(..., Map.class)` 反序列化发票信息
- 修复后保留原有业务语义不变，只调整 JSON 实现方式以对齐项目基线
## H. 任务外补充
1. 额外新增了 [ChannelLookupMapper.java](/D:/coding/jst_v1/RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/ChannelLookupMapper.java)，用于在 `jst-channel` 模块内安全解析当前登录渠道，避免跨模块直接依赖别的 service。
2. `CHANNEL_WITHDRAW_LOCK_TIMEOUT` 通过捕获公共锁超时异常并映射到渠道提现专属错误码 `40014`，这样前端可以直接按任务卡约定处理。
3. 启动联调中暴露出的 `ObjectMapper` 注入问题已经当场修复，并回写到本报告，后续同类实现应优先对齐项目已有 `fastjson2` 用法。

## I. 自检清单
- [x] 锁文档已登记
- [x] 写接口已加 `@Transactional(rollbackFor = Exception.class)`
- [x] 控制器已按任务卡写明 `@PreAuthorize`
- [x] 申请提现做了渠道归属、状态、方向、金额一致性校验
- [x] 取消提现仅允许 `pending -> closed`
- [x] fixture 已补渠道账号、角色、ledger 聚合样例
- [x] `.http` 已覆盖 W1-W10 场景
- [x] `mvn compile -DskipTests` 全量 18 模块 SUCCESS
- [ ] `.http` 实际执行与数据库对账，按任务卡豁免留待阶段联调
