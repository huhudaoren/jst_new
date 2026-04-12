# 任务报告 - MSG-TRIGGER 消息触发器联动

## Step 1 阅读完成
已按要求阅读并对齐以下文档：
1. `D:/coding/jst_v1/CLAUDE.md`
2. `D:/coding/jst_v1/架构设计/13-技术栈与依赖清单.md`
3. `D:/coding/jst_v1/架构设计/15-Redis-Key与锁规约.md`
4. `D:/coding/jst_v1/架构设计/16-安全与敏感字段.md`
5. `D:/coding/jst_v1/架构设计/25-从0到1开发流程.md`（§2 + §3）
6. `D:/coding/jst_v1/架构设计/30-接口测试指南.md`
7. `D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`
8. 任务相关 DDL：`02-jst-event.sql`、`03-jst-order.sql`、`04-jst-channel.sql`、`06-jst-organizer.sql`、`08-jst-message.sql`
9. 状态机：`D:/coding/jst_v1/架构设计/11-状态机定义.md`
10. API 契约：`12-API边界与服务划分.md`、`27-用户端API契约.md`

## A. 任务前自检（Step 2 答题）
1. 涉及表：  
   `jst_order_main`、`jst_payment_record`、`jst_refund_record`、`jst_channel_auth_apply`、`jst_channel`、`jst_enroll_record`、`jst_score_record`、`jst_rebate_settlement`、`jst_message`
2. 涉及状态机：  
   `SM-1`（订单主状态）、`SM-2`（退款单）、`SM-3`（渠道认证结果）、`SM-6`（报名审核）、`SM-9`（渠道提现）、`SM-19`（成绩发布）
3. 涉及权限标识：  
   `jst:order:refund:execute`、`jst:organizer:channelApply:approve`、`jst:organizer:channelApply:reject`、`jst:event:enrollRecord:audit`、`jst:channel:withdraw:execute`、`jst:event:score_record:edit`；  
   同时复用角色权限 `@ss.hasRole('jst_student')` / `@ss.hasRole('jst_channel')`（wx 端支付/消息接口）
4. 涉及锁名：  
   `lock:wxpay:notify:{outTradeNo}`、`lock:refund:execute:{refundId}`、`lock:org:channelApply:{applyId}`、`lock:enroll:audit:{enrollId}`、`lock:channel:withdraw:execute:{settlementId}`
5. 事务边界：  
   - `OrderServiceImpl.mockPaySuccess(...)` / `handleWxPayNotify(...)`（`@Transactional`）→ 支付成功后 `afterCommit` 发 `OrderPaidEvent`  
   - `RefundServiceImpl.execute(...)`（`@Transactional`）→ `afterCommit` 发 `RefundSuccessEvent`  
   - `ChannelAuthApplyServiceImpl.approve(...)` / `reject(...)`（`@Transactional`）→ `afterCommit` 发认证事件  
   - `EnrollRecordServiceImpl.audit(...)`（`@Transactional`）→ `afterCommit` 发 `EnrollAuditEvent`  
   - `ChannelWithdrawAdminServiceImpl.execute(...)`（`@Transactional`）→ `afterCommit` 发 `WithdrawPaidEvent`  
   - `JstScoreRecordServiceImpl.updateJstScoreRecord(...)`（无显式事务注解，存在事务时走 `afterCommit`，否则立即发布）
6. ResVO 字段（脱敏后）：  
   本卡未新增 ResVO，复用消息中心返回 `WxMessageVO`：  
   `messageId/type(msgType)/title/content/readStatus(readFlag)/bizType/bizId/linkUrl/linkText/createTime`（无敏感字段）
7. 复用样板：  
   `D:/coding/jst_v1/RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`  
   （并参考既有跨模块事件样板 `ChannelAuthApprovedEvent`）

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/OrderPaidEvent.java`
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/RefundSuccessEvent.java`
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/EnrollAuditEvent.java`
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/ScorePublishedEvent.java`
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/WithdrawPaidEvent.java`
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/ChannelAuthRejectedEvent.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/domain/JstMessage.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/mapper/JstMessageMapper.java`
- `RuoYi-Vue/jst-message/src/main/resources/mapper/message/JstMessageMapper.xml`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/support/MessagePublisher.java`
- `RuoYi-Vue/jst-message/src/main/java/com/ruoyi/jst/message/listener/BusinessMessageListener.java`

修改文件：
- `RuoYi-Vue/jst-common/src/main/java/com/ruoyi/jst/common/event/ChannelAuthApprovedEvent.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/impl/OrderServiceImpl.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/impl/RefundServiceImpl.java`
- `RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/ChannelAuthApplyServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/EnrollRecordServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/JstScoreRecordServiceImpl.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/lookup/ChannelLookupMapper.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/ChannelWithdrawAdminServiceImpl.java`
- `test/wx-tests.http`（追加 `MSG-1/2/3` 测试块）

实现说明（对照任务卡）：
- Part A：落地 `MessagePublisher`，接入 `SchemaInspector`，缺表/缺列降级 warn，不阻塞主流程。
- Part B~G：7 个业务触发点全部改为发布 Spring Event（`jst-order` 未直接依赖 `jst-message`）。
- Part H：补齐 5 个新事件 + 扩展 2 个渠道认证事件（共 7 类业务事件模型）。
- Part I：`BusinessMessageListener` 完成 7 类事件监听与消息模板写入。
- Part J：`test/wx-tests.http` 新增 MSG 场景断言。

## C. mvn compile 结果
执行命令：`mvn -f D:/coding/jst_v1/RuoYi-Vue/pom.xml compile -DskipTests`

结果：
- 18 模块全部 SUCCESS
- `[INFO] BUILD SUCCESS`
- `[INFO] Total time: 28.907 s`
- `Finished at: 2026-04-11T00:18:59+08:00`

## D. HTTP / 冒烟结果（真实执行）
本地执行脚本结果文件：  
`D:/coding/jst_v1/subagent/tasks/任务报告/msg-trigger-smoke2-results.json`

关键结果（HTTP 状态码 + body 业务码）：
1. `POST /jst/wx/auth/login`（MOCK_1001）→ HTTP 200，`code=200`
2. `POST /jst/wx/order/create`（enrollId=8901）→ HTTP 200，`code=200`，创建 `orderId=94433`
3. `POST /jst/wx/pay/mock-success?orderId=94433` → HTTP 200，`code=200`
4. `GET /jst/wx/message/my`（1001）→ HTTP 200，`code=200`，`data=[]`
5. `POST /jst/wx/auth/login`（MOCK_1002）→ HTTP 200，`code=200`
6. `POST /jst/wx/channel/auth/apply`（1002）→ HTTP 200，`code=60011`（重复提交）
7. `POST /login`（admin）→ HTTP 200，`code=200`
8. `POST /jst/wx/message/read-all`（1002）→ HTTP 200，`code=200`

事件触发与降级证据（日志）：
- `D:/coding/jst_v1/subagent/tasks/任务报告/msg-trigger-smoke2.log`  
  `00:34:05.813 ... WARN ... [MessagePublisher] skip send because jst_message table/columns missing`

结论：
- 订单支付触发链路已走通（可见订单从 `pending_pay -> paid`，随后进入 MessagePublisher）。
- 当前环境下消息未入库，原因是 `jst_message` 结构探测未通过，符合本卡“降级不阻塞主流程”要求。

## E. 遗留 TODO
- 当前测试库缺少/不匹配 `jst_message` 关键结构，导致 `MSG-1-3/MSG-2-3` 的“消息存在性断言”无法在本环境转绿。  
  需补齐 `jst_message` 表及关键列后重跑 `MSG-1/2/3`。
- `MOCK_1002` 当前为“渠道认证重复提交”(code=60011)，未拿到新的 `applyId`，因此本轮未执行新的 `approve` 触发。  
  需先 reset fixtures 或换未申请用户后重跑 `MSG-2`。

## F. 我做了任务卡之外的什么
- 为保证 `withdraw_paid` 事件能准确投递到用户，补充了 `ChannelLookupMapper.selectUserIdByChannelId(...)`（只读查询）。
- 为统一事件载荷结构，扩展了 `ChannelAuthApprovedEvent`（新增 `applyId/bizId/bizType/extraData`），保持原构造器兼容。

## G. 自检清单确认（16-安全文档 §8）
- [x] 未改前端、未改 WxMessageController（遵循任务卡限制）
- [x] `jst-order` 未直接依赖 `jst-message`，全部通过 Spring Event 解耦
- [x] 消息写入失败/缺表缺列不阻塞主事务（降级 warn）
- [x] 关键写路径保持 `@Transactional`，并在 `afterCommit` 发布事件
- [x] 复用既有锁规范，未新增未登记锁名
- [x] 未新增 `${}` 动态 SQL 拼接
- [x] `test/wx-tests.http` 已追加 MSG 测试块
- [x] `mvn compile -DskipTests` 已通过（18 模块）
