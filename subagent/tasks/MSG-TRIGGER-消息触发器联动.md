# 任务卡 MSG-TRIGGER - 消息触发器联动

## 阶段
阶段 F / **jst-order + jst-event + jst-organizer + jst-channel + jst-message**

## 背景
FIX-5 已建好消息中心 3 个接口（my/read/read-all），`jst_message` 表可用。但当前没有任何业务事件会写入消息——用户"我的消息"页永远空白。本卡在核心业务事件发生时自动写入 `jst_message`，让消息系统真正运转。

## PRD 依据
- §7.9 "系统消息：支付成功/退款通知/认证结果/成绩发布等"
- §8.9 "消息触发器与模板"

## 交付物

### Part A — 消息写入工具类
**新建**：`jst-message/.../support/MessagePublisher.java`

```java
@Component
public class MessagePublisher {
    @Autowired private JstMessageMapper messageMapper; // 若依生成的 mapper

    /**
     * 发送业务消息
     * @param userId   接收用户 ID
     * @param type     消息类型：system / business
     * @param bizType  业务类型：order_paid / refund_success / auth_approved / score_published 等
     * @param bizId    业务 ID（订单号/退款号/认证申请 ID 等）
     * @param title    消息标题
     * @param content  消息内容
     */
    public void send(Long userId, String type, String bizType, Long bizId, String title, String content) {
        JstMessage msg = new JstMessage();
        msg.setUserId(userId);
        msg.setMsgType(type);
        msg.setBizType(bizType);
        msg.setBizId(bizId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setReadStatus(0);
        msg.setCreateBy(String.valueOf(userId));
        msg.setCreateTime(new Date());
        msg.setDelFlag("0");
        messageMapper.insertJstMessage(msg);
    }
}
```

如 `jst_message` 表不存在或缺列，用 `SchemaInspector` 检测，缺失时 `log.warn` 不抛异常（降级不阻塞主流程）。

### Part B — 订单支付成功触发
**修改**：`jst-order/.../service/impl/OrderServiceImpl.java`（或 `WxPayServiceImpl`）

在支付成功回调处追加：
```java
messagePublisher.send(order.getUserId(), "business", "order_paid", order.getOrderId(),
    "支付成功", "您的订单 " + order.getOrderNo() + " 已支付成功，报名费 ¥" + order.getPayAmount());
```

**注意**：jst-order 不能直接依赖 jst-message。两种方案：
- **方案 A（推荐）**：用 Spring Event。`OrderServiceImpl` 发 `OrderPaidEvent`，`jst-message` 里写 `@EventListener` 监听
- **方案 B**：`MessagePublisher` 放 jst-common（跨模块共享），各模块直接注入

**选方案 A**：Event 类放 `jst-common/.../event/`（E0-1 已有先例 `ChannelAuthApprovedEvent`）

### Part C — 退款成功触发
**修改**：`jst-order/.../service/impl/RefundServiceImpl.java`

退款执行成功后：
```java
// 发给学生
messagePublisher.send(refund.getUserId(), "business", "refund_success", refund.getRefundId(),
    "退款到账", "您的退款 " + refund.getRefundNo() + " 已到账，金额 ¥" + refund.getRefundAmount());
```

### Part D — 渠道认证通过/驳回触发
**修改**：`jst-organizer/.../service/impl/ChannelAuthApplyServiceImpl.java`

审核通过时（`approve` 方法）：
```java
messagePublisher.send(apply.getUserId(), "system", "auth_approved", apply.getApplyId(),
    "渠道认证通过", "恭喜！您的渠道认证已通过审核，可以开始使用渠道工作台。");
```

审核驳回时（`reject` 方法）：
```java
messagePublisher.send(apply.getUserId(), "system", "auth_rejected", apply.getApplyId(),
    "渠道认证驳回", "您的渠道认证申请被驳回，原因：" + rejectReason + "。请修改后重新提交。");
```

### Part E — 报名审核结果触发
**修改**：`jst-event/.../service/impl/EnrollRecordServiceImpl.java`（或审核方法所在类）

审核通过：
```java
messagePublisher.send(enroll.getUserId(), "business", "enroll_approved", enroll.getEnrollId(),
    "报名审核通过", "您报名的「" + contestName + "」已审核通过，请及时支付。");
```

审核驳回：
```java
messagePublisher.send(enroll.getUserId(), "business", "enroll_rejected", enroll.getEnrollId(),
    "报名审核未通过", "您报名的「" + contestName + "」审核未通过，原因：" + rejectReason);
```

### Part F — 成绩发布触发
**修改**：`jst-event/.../service/impl/PartnerScoreServiceImpl.java`（或平台发布成绩的 service）

成绩发布（`publish_status` 变为 `published`）时，批量发消息给相关学生：
```java
for (ScoreRecord score : publishedScores) {
    messagePublisher.send(score.getUserId(), "business", "score_published", score.getScoreId(),
        "成绩发布", "「" + contestName + "」成绩已发布，您的成绩为：" + score.getScoreValue());
}
```

### Part G — 提现打款触发
**修改**：`jst-channel/.../service/impl/ChannelWithdrawAdminServiceImpl.java`

打款成功（`execute` 方法）：
```java
messagePublisher.send(settlement.getUserId(), "business", "withdraw_paid", settlement.getSettlementId(),
    "提现到账", "您的提现申请 ¥" + settlement.getApplyAmount() + " 已打款成功。");
```

### Part H — Event 定义（jst-common）
**新建**：
- `jst-common/.../event/OrderPaidEvent.java`
- `jst-common/.../event/RefundSuccessEvent.java`
- `jst-common/.../event/EnrollAuditEvent.java`
- `jst-common/.../event/ScorePublishedEvent.java`
- `jst-common/.../event/WithdrawPaidEvent.java`

每个 Event 包含：`userId / bizId / bizType / extraData(Map)`

### Part I — 消息监听器（jst-message）
**新建**：`jst-message/.../listener/BusinessMessageListener.java`

```java
@Component
public class BusinessMessageListener {
    @Autowired private MessagePublisher publisher;

    @EventListener
    public void onOrderPaid(OrderPaidEvent e) {
        publisher.send(e.getUserId(), "business", "order_paid", e.getOrderId(), ...);
    }

    @EventListener
    public void onRefundSuccess(RefundSuccessEvent e) { ... }

    // 其他 5 个事件同理
}
```

### Part J — 测试
`test/wx-tests.http` 追加：
- `MSG-1` 创建订单 + 支付 → 查消息列表应有 1 条 order_paid
- `MSG-2` 渠道认证审核通过 → 查消息应有 auth_approved
- `MSG-3` 全部已读 → readStatus 全变 1

## DoD
- [ ] MessagePublisher 工具类
- [ ] 7 个业务触发点全部接入
- [ ] 7 个 Event 类 + 1 个 Listener
- [ ] 降级：jst_message 缺表/缺列时不阻塞主流程
- [ ] mvn compile 18 模块 SUCCESS
- [ ] 测试段追加
- [ ] 任务报告 `MSG-TRIGGER-回答.md`
- [ ] commit: `feat(be): MSG-TRIGGER 消息触发器联动（7 业务事件→自动写消息）`

## 不许做
- ❌ 不许改前端
- ❌ 不许让消息写入失败阻塞主事务（try-catch 降级）
- ❌ 不许改 FIX-5 已完成的 WxMessageController
- ❌ 不许在 jst-order 直接 import jst-message（走 Spring Event）

## 依赖：FIX-5 ✅
## 优先级：P1

---
派发时间：2026-04-10
