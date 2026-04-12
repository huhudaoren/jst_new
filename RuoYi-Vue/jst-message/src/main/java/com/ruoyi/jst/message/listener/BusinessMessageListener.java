package com.ruoyi.jst.message.listener;

import com.ruoyi.jst.common.event.ChannelAuthApprovedEvent;
import com.ruoyi.jst.common.event.ChannelAuthRejectedEvent;
import com.ruoyi.jst.common.event.EnrollAuditEvent;
import com.ruoyi.jst.common.event.OrderPaidEvent;
import com.ruoyi.jst.common.event.RefundSuccessEvent;
import com.ruoyi.jst.common.event.ScorePublishedEvent;
import com.ruoyi.jst.common.event.WithdrawPaidEvent;
import com.ruoyi.jst.message.support.MessagePublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 业务事件消息监听器。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class BusinessMessageListener {

    private final MessagePublisher messagePublisher;

    public BusinessMessageListener(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    /**
     * 支付成功消息。
     *
     * @param event 支付事件
     */
    @EventListener
    public void onOrderPaid(OrderPaidEvent event) {
        String orderNo = stringValue(event.getExtraData(), "orderNo", String.valueOf(event.getBizId()));
        String payAmount = stringValue(event.getExtraData(), "payAmount", "--");
        messagePublisher.send(event.getUserId(), "business", "order_paid", event.getBizId(),
                "支付成功", "您的订单 " + orderNo + " 已支付成功，报名费 ¥" + payAmount);
    }

    /**
     * 退款成功消息。
     *
     * @param event 退款事件
     */
    @EventListener
    public void onRefundSuccess(RefundSuccessEvent event) {
        String refundNo = stringValue(event.getExtraData(), "refundNo", String.valueOf(event.getBizId()));
        String refundAmount = stringValue(event.getExtraData(), "refundAmount", "--");
        messagePublisher.send(event.getUserId(), "business", "refund_success", event.getBizId(),
                "退款到账", "您的退款 " + refundNo + " 已到账，金额 ¥" + refundAmount);
    }

    /**
     * 渠道认证通过消息。
     *
     * @param event 认证通过事件
     */
    @EventListener
    public void onChannelAuthApproved(ChannelAuthApprovedEvent event) {
        messagePublisher.send(event.getUserId(), "system", "auth_approved", event.getBizId(),
                "渠道认证通过", "恭喜！您的渠道认证已通过审核，可以开始使用渠道工作台。");
    }

    /**
     * 渠道认证驳回消息。
     *
     * @param event 认证驳回事件
     */
    @EventListener
    public void onChannelAuthRejected(ChannelAuthRejectedEvent event) {
        String rejectReason = stringValue(event.getExtraData(), "rejectReason", "请查看审核意见");
        messagePublisher.send(event.getUserId(), "system", "auth_rejected", event.getBizId(),
                "渠道认证驳回", "您的渠道认证申请被驳回，原因：" + rejectReason + "。请修改后重新提交。");
    }

    /**
     * 报名审核结果消息。
     *
     * @param event 报名审核事件
     */
    @EventListener
    public void onEnrollAudit(EnrollAuditEvent event) {
        String contestName = stringValue(event.getExtraData(), "contestName", "赛事");
        String rejectReason = stringValue(event.getExtraData(), "rejectReason", "请查看审核意见");
        if (Objects.equals("enroll_approved", event.getBizType())) {
            messagePublisher.send(event.getUserId(), "business", "enroll_approved", event.getBizId(),
                    "报名审核通过", "您报名的「" + contestName + "」已审核通过，请及时支付。");
            return;
        }
        messagePublisher.send(event.getUserId(), "business", "enroll_rejected", event.getBizId(),
                "报名审核未通过", "您报名的「" + contestName + "」审核未通过，原因：" + rejectReason);
    }

    /**
     * 成绩发布消息。
     *
     * @param event 成绩发布事件
     */
    @EventListener
    public void onScorePublished(ScorePublishedEvent event) {
        String contestName = stringValue(event.getExtraData(), "contestName", "赛事");
        String scoreValue = stringValue(event.getExtraData(), "scoreValue", "--");
        messagePublisher.send(event.getUserId(), "business", "score_published", event.getBizId(),
                "成绩发布", "「" + contestName + "」成绩已发布，您的成绩为：" + scoreValue);
    }

    /**
     * 提现打款消息。
     *
     * @param event 提现打款事件
     */
    @EventListener
    public void onWithdrawPaid(WithdrawPaidEvent event) {
        String applyAmount = stringValue(event.getExtraData(), "applyAmount", "--");
        messagePublisher.send(event.getUserId(), "business", "withdraw_paid", event.getBizId(),
                "提现到账", "您的提现申请 ¥" + applyAmount + " 已打款成功。");
    }

    private String stringValue(Map<String, Object> data, String key, String defaultValue) {
        if (data == null) {
            return defaultValue;
        }
        Object value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? defaultValue : text;
    }
}

