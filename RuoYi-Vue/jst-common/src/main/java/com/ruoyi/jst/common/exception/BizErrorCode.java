package com.ruoyi.jst.common.exception;

/**
 * 竞赛通业务错误码字典
 * <p>
 * 编码段约定（与 12-API边界与服务划分.md §4 异常码段一致）：
 * <ul>
 *   <li>1xxxx jst-user</li>
 *   <li>2xxxx jst-event</li>
 *   <li>3xxxx jst-order</li>
 *   <li>4xxxx jst-channel</li>
 *   <li>5xxxx jst-points</li>
 *   <li>6xxxx jst-organizer</li>
 *   <li>7xxxx jst-marketing</li>
 *   <li>8xxxx jst-message</li>
 *   <li>9xxxx jst-risk</li>
 *   <li>99xxx jst-finance</li>
 * </ul>
 * 使用示例：
 * <pre>
 *   throw new ServiceException("订单状态非法跃迁",
 *       BizErrorCode.JST_ORDER_ILLEGAL_TRANSIT.code());
 * </pre>
 *
 * @author jst
 * @since 1.0.0
 */
public enum BizErrorCode {

    // ========== jst-user (1xxxx) ==========
    JST_USER_NOT_FOUND(10001, "用户不存在"),
    JST_USER_DISABLED(10002, "账号已被禁用"),
    JST_USER_FROZEN(10003, "账号已被冻结"),
    JST_USER_BIND_CONFLICT(10011, "已存在有效的渠道绑定关系"),
    JST_USER_BIND_NOT_FOUND(10012, "绑定记录不存在"),
    JST_USER_ALREADY_CHANNEL(10013, "当前用户已是渠道方"),
    JST_USER_CLAIM_ILLEGAL_TRANSIT(10020, "档案认领状态非法跃迁"),
    JST_USER_CLAIM_ALREADY_CLAIMED(10021, "档案已被认领"),
    JST_USER_PARTICIPANT_NOT_FOUND(10030, "参赛档案不存在"),

    // ========== jst-event (2xxxx) ==========
    JST_EVENT_CONTEST_NOT_FOUND(20001, "赛事不存在"),
    JST_EVENT_CONTEST_NOT_ENROLLING(20002, "赛事不在报名期"),
    JST_EVENT_CONTEST_NOT_ONLINE(20003, "赛事未上线"),
    JST_EVENT_CONTEST_ILLEGAL_TRANSIT(20004, "赛事状态非法跃迁"),
    JST_EVENT_FORM_TEMPLATE_NOT_FOUND(20010, "动态表单模板不存在"),
    JST_EVENT_FORM_TEMPLATE_INVALID(20011, "动态表单模板无效"),
    JST_EVENT_FORM_TEMPLATE_ILLEGAL_TRANSIT(20012, "动态表单模板状态非法跃迁"),
    JST_EVENT_FORM_VALIDATION_FAIL(20013, "表单字段校验失败"),
    JST_EVENT_ENROLL_DUPLICATE(20020, "重复报名"),
    JST_EVENT_PARTNER_NOT_AUTHED(20030, "赛事方未通过审核"),
    JST_EVENT_COURSE_NOT_FOUND(20040, "课程不存在"),
    JST_EVENT_COURSE_NOT_ON(20041, "课程未上架或未审核通过"),

    JST_EVENT_ENROLL_NOT_FOUND(20021, "报名记录不存在"),
    JST_EVENT_ENROLL_ILLEGAL_TRANSIT(20022, "报名记录状态非法跃迁"),
    JST_EVENT_ENROLL_NOT_OWN(20023, "无权访问该报名记录"),
    JST_EVENT_ENROLL_PARTICIPANT_NOT_OWN(20024, "参赛人不属于当前用户"),
    JST_EVENT_ENROLL_DUPLICATE_PENDING(20025, "当前存在待审核或待补件的报名记录"),

    // ========== jst-order (3xxxx) ==========
    JST_ORDER_NOT_FOUND(30001, "订单不存在"),
    JST_ORDER_ILLEGAL_TRANSIT(30002, "订单状态非法跃迁"),
    JST_ORDER_AMOUNT_MISMATCH(30003, "订单金额校验失败"),
    JST_ORDER_ENROLL_NOT_FOUND(30004, "报名记录不存在"),
    JST_ORDER_ENROLL_NOT_APPROVED(30005, "报名记录未审核通过"),
    JST_ORDER_DUPLICATE_ORDER(30006, "当前报名已存在有效订单"),
    JST_ORDER_POINTS_NOT_SUPPORTED(30007, "当前赛事不支持积分抵扣"),
    JST_ORDER_PAY_METHOD_INVALID(30008, "当前支付方式不匹配订单金额"),
    JST_ORDER_PAY_FAIL(30010, "支付失败"),
    JST_ORDER_REFUND_DENIED(30020, "当前订单不允许退款"),
    JST_ORDER_REFUND_OVER_LIMIT(30021, "退款金额超出可退余额"),
    JST_ORDER_TEAM_APPT_FULL(30030, "团队预约人数已满"),
    JST_ORDER_TEAM_WRITEOFF_OVER(30031, "核销人数超过预约总数"),
    JST_ORDER_TEAM_ENDED(30032, "团队预约已结束，不可再核销"),
    JST_ORDER_WX_NOTIFY_INVALID(30040, "微信支付回调验签失败"),

    // ========== jst-channel (4xxxx) ==========
    JST_CHANNEL_NOT_AUTHED(40001, "渠道方未认证"),
    JST_CHANNEL_REBATE_NEGATIVE_INSUFFICIENT(40010, "负向台账抵扣不足"),
    JST_CHANNEL_SETTLE_ILLEGAL_TRANSIT(40020, "提现单状态非法跃迁"),

    // ========== jst-points (5xxxx) ==========
    JST_POINTS_INSUFFICIENT(50001, "积分余额不足"),
    JST_POINTS_CONCURRENT_CONFLICT(50002, "积分账户并发冲突，请重试"),
    JST_MALL_GOODS_OFFLINE(50010, "商品已下架"),
    JST_MALL_STOCK_INSUFFICIENT(50011, "商品库存不足"),

    // ========== jst-organizer (6xxxx) ==========
    JST_ORG_APPLY_NOT_FOUND(60001, "入驻申请不存在"),
    JST_ORG_APPLY_ILLEGAL_TRANSIT(60002, "入驻申请状态非法跃迁"),
    JST_ORG_APPLY_DUPLICATE(60003, "存在待处理或已通过的入驻申请"),
    JST_CHANNEL_AUTH_APPLY_NOT_FOUND(60010, "渠道认证申请不存在"),
    JST_CHANNEL_AUTH_APPLY_DUPLICATE(60011, "渠道认证申请重复提交"),
    JST_CHANNEL_AUTH_APPLY_ILLEGAL_TRANSIT(60012, "渠道认证申请状态非法跃迁"),

    // ========== jst-marketing (7xxxx) ==========
    JST_MARKETING_COUPON_INVALID(70001, "优惠券无效或已过期"),
    JST_MARKETING_COUPON_NOT_APPLICABLE(70002, "优惠券不适用于该订单"),
    JST_MARKETING_RIGHTS_NO_QUOTA(70010, "权益剩余额度不足"),

    // ========== jst-message (8xxxx) ==========
    JST_MSG_NOTICE_NOT_FOUND(80001, "公告不存在"),
    JST_MSG_TEMPLATE_NOT_FOUND(80002, "消息模板不存在"),
    JST_MSG_SEND_FAIL(80003, "消息发送失败"),

    // ========== jst-risk (9xxxx) ==========
    JST_RISK_BLACKLISTED(90001, "命中黑名单"),
    JST_RISK_RATE_LIMITED(90002, "操作过于频繁，已被风控限流"),

    // ========== jst-finance (99xxx) ==========
    JST_FINANCE_INVOICE_NOT_FOUND(99001, "发票不存在"),
    JST_FINANCE_PAY_RECORD_DUPLICATE(99002, "重复打款记录"),

    // ========== 通用 (20000+) ==========
    JST_COMMON_LOCK_TIMEOUT(99900, "操作过于频繁，请稍后再试"),
    JST_COMMON_PARAM_INVALID(99901, "参数校验失败"),
    JST_COMMON_AUTH_DENIED(99902, "无权访问该资源"),
    JST_COMMON_DATA_TAMPERED(99903, "数据已被篡改或并发修改");

    private final int code;
    private final String message;

    BizErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
