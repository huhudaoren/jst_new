# 任务报告 - QUARTZ-TASKS 定时任务落地

## A0. Step 1 阅读确认
已按要求阅读并对齐以下文档：
1. `D:/coding/jst_v1/CLAUDE.md`
2. `架构设计/13-技术栈与依赖清单.md`
3. `架构设计/15-Redis-Key与锁规约.md`
4. `架构设计/16-安全与敏感字段.md`
5. `架构设计/25-从0到1开发流程.md`（§2 + §3）
6. `架构设计/30-接口测试指南.md`
7. `RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`
8. `架构设计/14-定时任务清单(Quartz).md`
9. `架构设计/11-状态机定义.md`
10. `架构设计/12-后端API契约总表.md`、`架构设计/27-小程序与支付API补充.md`
11. `subagent/tasks/QUARTZ-TASKS-定时任务落地.md`

## A. 任务前自检（Step 2 答题）
1. 涉及表：`jst_order_main`、`jst_refund_record`、`jst_rebate_ledger`、`jst_appointment_record`、`jst_appointment_writeoff_item`、`sys_job`
2. 涉及状态机：`SM-1`（订单）、`SM-2`（退款）、`SM-8`（返点台账）、`SM-11`（预约）
3. 涉及权限标识：本任务新增内容均为系统定时任务入口（`sys_job -> invokeTarget` 反射调用），未新增业务接口权限点
4. 涉及锁名：
   - `lock:job:orderTimeoutCancelTask`
   - `lock:job:refundTimeoutCloseTask`
   - `lock:job:rebateAutoSettleTask`
   - `lock:job:appointmentExpireTask`
   - `lock:order:cancel:{orderId}`
   - `lock:refund:approve:{refundId}`
5. 事务边界：
   - `OrderServiceImpl.cancelTimeoutOrder`（`@Transactional(rollbackFor = Exception.class)`）
   - `RefundServiceImpl.closeTimeoutPending`（`@Transactional(rollbackFor = Exception.class)`）
   - `JstRebateLedgerServiceImpl.settlePendingAccrual`（`@Transactional(rollbackFor = Exception.class)`）
   - `AppointmentServiceImpl.expireBySystem`（`@Transactional(rollbackFor = Exception.class)`）
6. ResVO 字段：本任务为定时任务与内部服务改造，未新增 Controller 出参 `ResVO`
7. 复用样板：`RuoYi-Vue/jst-user/src/main/java/com/ruoyi/jst/user/service/impl/ParticipantClaimServiceImpl.java`

## B. 交付物清单
新增文件：
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/task/OrderTimeoutCancelTask.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/task/RefundTimeoutCloseTask.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/enums/RebateLedgerStatus.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/task/RebateAutoSettleTask.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/task/AppointmentExpireTask.java`
- `架构设计/ddl/99-migration-quartz-jobs.sql`

修改文件：
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/OrderService.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/impl/OrderServiceImpl.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/mapper/OrderMainMapperExt.java`
- `RuoYi-Vue/jst-order/src/main/resources/mapper/order/OrderMainMapperExt.xml`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/RefundService.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/service/impl/RefundServiceImpl.java`
- `RuoYi-Vue/jst-order/src/main/java/com/ruoyi/jst/order/mapper/RefundRecordMapperExt.java`
- `RuoYi-Vue/jst-order/src/main/resources/mapper/order/RefundRecordMapperExt.xml`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/IJstRebateLedgerService.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/service/impl/JstRebateLedgerServiceImpl.java`
- `RuoYi-Vue/jst-channel/src/main/java/com/ruoyi/jst/channel/mapper/RebateLedgerMapperExt.java`
- `RuoYi-Vue/jst-channel/src/main/resources/mapper/channel/RebateLedgerMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/AppointmentService.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/service/impl/AppointmentServiceImpl.java`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/AppointmentRecordMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/AppointmentRecordMapperExt.xml`
- `RuoYi-Vue/jst-event/src/main/java/com/ruoyi/jst/event/mapper/WriteoffItemMapperExt.java`
- `RuoYi-Vue/jst-event/src/main/resources/mapper/event/WriteoffItemMapperExt.xml`
- `test/admin-tests.http`（追加 QT-0 ~ QT-5）

实现说明（按任务卡 1~5）：
1. 订单超时取消：新增 `orderTimeoutCancelTask.execute()`，分页扫描 `pending_pay` 且超时订单，逐条调用 `OrderService.cancelTimeoutOrder`。
2. 退款超时关闭：新增 `refundTimeoutCloseTask.execute()`，分页扫描 `pending` 且超时退款，逐条调用 `RefundService.closeTimeoutPending`。
3. 返点自动计提：新增 `rebateAutoSettleTask.execute()`，扫描 `pending_accrual + completed + aftersale_deadline<now`，逐条 `pending_accrual -> withdrawable`。
4. 预约过期：新增 `appointmentExpireTask.execute()`，扫描 `booked + appointment_date<today`，逐条过期并同步过期未核销子项。
5. sys_job 注册：新增 `99-migration-quartz-jobs.sql`，幂等注册 4 条 `JST_TASK` 任务。

## C. 编译结果
1. 全量编译（`RuoYi-Vue`）  
命令：`mvn compile -DskipTests`  
结果：`BUILD FAILURE`（阻塞于非本任务文件）
```text
[ERROR] /D:/coding/jst_v1/RuoYi-Vue/jst-organizer/src/main/java/com/ruoyi/jst/organizer/service/impl/ChannelAuthApplyServiceImpl.java:[254,24]
已在方法 approve(java.lang.Long,com.ruoyi.jst.organizer.dto.ApproveChannelReqDTO)中定义了变量 eventUserId
```

2. 任务相关模块编译  
命令：`mvn -pl jst-order,jst-channel,jst-event -am compile -DskipTests`  
结果：`BUILD SUCCESS`（本任务涉及模块均通过）

## D. .http 测试结果
已在 `test/admin-tests.http` 追加 `QT-0 ~ QT-5` 触发用例（`/monitor/job/list` + `/monitor/job/run`）。

本轮未能给出“实际运行返回体”结果，原因：
1. 按系统提示词要求，`.http` 前置应先完成全量可编译并启动 `ruoyi-admin`；
2. 当前被 `jst-organizer` 既有编译错误阻塞，导致无法进入稳定启动与执行 `.http` 的阶段。

结论：`D` 段按“阻塞”上报，待主 Agent 先解决全量编译阻塞后可立即复测 QT-0~QT-5。

## E. 遗留 TODO / 阻塞
1. 阻塞项：`jst-organizer` 模块 `ChannelAuthApplyServiceImpl` 局部变量重复定义，导致全量 `mvn compile -DskipTests` 失败。
2. 受阻塞影响：尚未完成 `ruoyi-admin` 启动后真实 `/monitor/job/run` 回放与响应体留档。
3. 解阻后建议顺序：
   - 修复 `jst-organizer` 编译错误
   - 重新执行全量编译
   - 启动 `ruoyi-admin`（8080）
   - 按 QT-0~QT-5 实测并回填响应体

## F. 我做了任务卡之外的什么
1. 新增 `RebateLedgerStatus` 枚举（原因：当前代码库缺少可复用的 SM-8 状态断言载体，任务内需要显式 `assertCanTransitTo`）。
2. 对任务卡中的旧状态描述做了“按 V4 基线落地”纠偏：
   - 返点状态：按现网模型使用 `pending_accrual -> withdrawable`，未采用卡面 `pending -> usable`
   - 预约状态：按现网模型使用 `booked -> expired`，未采用卡面 `confirmed -> expired`

## G. 自检清单确认（16-安全文档 §8）
- [x] 任务新增代码未引入新依赖
- [x] 写操作均放在 Service 层并加 `@Transactional(rollbackFor = Exception.class)`（单条事务）
- [x] 批处理入口未包大事务；单条失败不影响整批
- [x] 并发点使用 `jstLockTemplate.execute(...)`
- [x] 状态流转使用状态枚举断言 + 期望状态更新
- [x] Mapper.xml 未使用 `${}` 拼接 SQL
- [x] 定时任务日志覆盖“开始/结束/计数/异常”
- [x] sys_job 注册 SQL 幂等（`INSERT IGNORE`）
- [x] `.http` 测试块已追加
- [ ] QT-0~QT-5 实际绿灯结果（受全量编译阻塞，待解阻后补齐）
