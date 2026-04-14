package com.ruoyi.jst.common.exception;

/**
 * Business error codes shared across JST modules.
 */
public enum BizErrorCode {

    // ========== jst-user (1xxxx) ==========
    JST_USER_NOT_FOUND(10001, "用户不存在"),
    JST_USER_DISABLED(10002, "账号已被禁用"),
    JST_USER_FROZEN(10003, "账号已被冻结"),
    JST_USER_BIND_CONFLICT(10011, "已存在有效的渠道绑定关系"),
    JST_USER_BIND_NOT_FOUND(10012, "绑定记录不存在"),
    JST_USER_ALREADY_CHANNEL(10013, "当前用户已是渠道方"),
    JST_USER_CLAIM_ILLEGAL_TRANSIT(10020, "档案认领状态非法流转"),
    JST_USER_CLAIM_ALREADY_CLAIMED(10021, "档案已被认领"),
    JST_USER_PARTICIPANT_NOT_FOUND(10030, "参赛档案不存在"),
    JST_USER_ADDRESS_NOT_FOUND(10031, "收货地址不存在"),

    // ========== jst-event (2xxxx) ==========
    JST_EVENT_CONTEST_NOT_FOUND(20001, "赛事不存在"),
    JST_EVENT_CONTEST_NOT_ENROLLING(20002, "赛事不在报名期"),
    JST_EVENT_CONTEST_NOT_ONLINE(20003, "赛事未上架"),
    JST_EVENT_CONTEST_ILLEGAL_TRANSIT(20004, "赛事状态非法流转"),
    JST_EVENT_FORM_TEMPLATE_NOT_FOUND(20010, "动态表单模板不存在"),
    JST_EVENT_FORM_TEMPLATE_INVALID(20011, "动态表单模板无效"),
    JST_EVENT_FORM_TEMPLATE_ILLEGAL_TRANSIT(20012, "动态表单模板状态非法流转"),
    JST_EVENT_FORM_VALIDATION_FAIL(20013, "表单字段校验失败"),
    JST_EVENT_ENROLL_DUPLICATE(20020, "重复报名"),
    JST_EVENT_ENROLL_NOT_FOUND(20021, "报名记录不存在"),
    JST_EVENT_ENROLL_ILLEGAL_TRANSIT(20022, "报名记录状态非法流转"),
    JST_EVENT_ENROLL_NOT_OWN(20023, "无权访问该报名记录"),
    JST_EVENT_ENROLL_PARTICIPANT_NOT_OWN(20024, "参赛人不属于当前用户"),
    JST_EVENT_ENROLL_DUPLICATE_PENDING(20025, "当前存在待审核或待补件的报名记录"),
    JST_EVENT_PARTNER_NOT_AUTHED(20030, "赛事方未通过审核"),
    JST_EVENT_COURSE_NOT_FOUND(20040, "课程不存在"),
    JST_EVENT_COURSE_NOT_ON(20041, "课程未上架或未审核通过"),
    JST_EVENT_SCORE_NOT_FOUND(20050, "成绩记录不存在"),
    JST_EVENT_SCORE_NOT_EDITABLE(20051, "当前成绩状态不允许编辑"),
    JST_EVENT_SCORE_ILLEGAL_TRANSIT(20052, "成绩状态非法流转"),
    JST_EVENT_SCORE_IMPORT_FAILED(20053, "成绩导入失败"),
    JST_EVENT_CERT_TEMPLATE_NOT_FOUND(20060, "证书模板不存在"),
    JST_EVENT_CERT_NOT_FOUND(20061, "证书记录不存在"),
    JST_EVENT_CERT_ILLEGAL_TRANSIT(20062, "证书状态非法流转"),
    JST_EVENT_CERT_BATCH_EMPTY(20063, "没有可生成证书的已发布成绩"),
    JST_EVENT_REVIEWER_NOT_FOUND(20070, "评审老师不存在"),
    JST_EVENT_REVIEWER_DUPLICATE(20071, "评审老师重复配置"),
    JST_EVENT_TEAM_SIZE_INVALID(20080, "团队人数不满足要求"),
    JST_EVENT_TEAM_PHONE_DUPLICATE(20081, "团队成员手机号重复"),
    JST_EVENT_TEAM_NOT_SUPPORTED(20082, "当前赛事不支持团队报名"),
    JST_EVENT_SLOT_CAPACITY_FULL(20083, "预约时段名额不足"),
    JST_EVENT_CERT_GENERATE_NO_ENROLL(20084, "报名记录不存在或未通过审核"),

    // ========== jst-order (3xxxx) ==========
    JST_ORDER_NOT_FOUND(30001, "订单不存在"),
    JST_ORDER_ILLEGAL_TRANSIT(30002, "订单状态非法流转"),
    JST_ORDER_AMOUNT_MISMATCH(30003, "订单金额校验失败"),
    JST_ORDER_ENROLL_NOT_FOUND(30004, "报名记录不存在"),
    JST_ORDER_ENROLL_NOT_APPROVED(30005, "报名记录未审核通过"),
    JST_ORDER_DUPLICATE_ORDER(30006, "当前报名已存在有效订单"),
    JST_ORDER_POINTS_NOT_SUPPORTED(30007, "当前赛事不支持积分抵扣"),
    JST_ORDER_PAY_METHOD_INVALID(30008, "当前支付方式与订单金额不匹配"),
    JST_ORDER_PAY_FAIL(30010, "支付失败"),
    JST_ORDER_REFUND_DENIED(30020, "当前订单不允许退款"),
    JST_ORDER_REFUND_OVER_LIMIT(30021, "退款金额超出可退余额"),
    JST_ORDER_TEAM_APPT_FULL(30030, "团队预约人数已满"),
    JST_ORDER_TEAM_WRITEOFF_OVER(30031, "核销人数超过预约总数"),
    JST_ORDER_TEAM_ENDED(30032, "团队预约已结束，不可再核销"),
    JST_ORDER_WX_NOTIFY_INVALID(30040, "微信支付回调验签失败"),
    JST_ORDER_REFUND_NOT_FOUND(30050, "退款单不存在"),
    JST_ORDER_REFUND_ILLEGAL_TRANSIT(30051, "退款状态非法流转"),
    JST_ORDER_REFUND_DUPLICATE(30052, "当前订单已存在有效退款单"),
    JST_ORDER_NO_REFUND_FOR_ZERO_PRICE(30053, "零元订单不支持退款"),
    JST_ORDER_WX_REFUND_NOTIFY_INVALID(30054, "微信退款回调验签失败"),
    JST_ORDER_REFUND_EXECUTE_FAIL(30055, "退款执行失败"),
    JST_ORDER_APPOINTMENT_NOT_SUPPORTED(30070, "当前赛事未开启线下预约"),
    JST_ORDER_APPOINTMENT_CAPACITY_FULL(30071, "当前场次预约名额不足"),
    JST_ORDER_APPOINTMENT_PARTICIPANT_NOT_FOUND(30072, "预约成员档案不存在"),
    JST_ORDER_APPOINTMENT_REPEAT_DENIED(30073, "当前赛事不允许重复预约"),
    JST_ORDER_APPOINTMENT_NOT_FOUND(30074, "预约记录不存在"),
    JST_ORDER_APPOINTMENT_ILLEGAL_TRANSIT(30075, "预约状态非法流转"),
    JST_ORDER_TEAM_APPOINTMENT_NOT_FOUND(30076, "团队预约不存在"),
    JST_ORDER_TEAM_APPOINTMENT_ILLEGAL_TRANSIT(30077, "团队预约状态非法流转"),
    JST_ORDER_TEAM_APPOINTMENT_ENDED(30078, "团队预约已结束，不可再核销"),
    JST_ORDER_WRITEOFF_QR_INVALID(30080, "核销二维码无效"),
    JST_ORDER_WRITEOFF_ITEM_STATUS_INVALID(30081, "核销子项状态非法"),
    JST_ORDER_WRITEOFF_RECORD_NOT_FOUND(30082, "核销记录不存在"),
    JST_ORDER_APPOINTMENT_REPEAT_NOT_ALLOWED(30091, "当前赛事不允许重复预约"),
    JST_ORDER_APPOINTMENT_CAPACITY_FULL_V2(30092, "当前场次预约名额不足"),
    JST_ORDER_APPOINTMENT_CANCEL_HAS_USED_ITEM(30093, "预约已存在已核销子项，不可取消"),

    // ========== jst-channel (4xxxx) ==========
    JST_CHANNEL_NOT_AUTHED(40001, "渠道方未认证"),
    JST_CHANNEL_REBATE_NEGATIVE_INSUFFICIENT(40010, "负向台账抵扣不足"),
    JST_CHANNEL_WITHDRAW_LEDGER_INVALID(40011, "所选返点台账状态非法或不属于当前渠道"),
    JST_CHANNEL_WITHDRAW_AMOUNT_MISMATCH(40012, "提现金额与后端重算不一致"),
    JST_CHANNEL_WITHDRAW_STATUS_INVALID(40013, "提现单状态非法流转"),
    JST_CHANNEL_WITHDRAW_LOCK_TIMEOUT(40014, "提现申请处理中，请稍后重试"),
    JST_CHANNEL_WITHDRAW_NEGATIVE_OVERFLOW(40015, "负向台账抵扣后金额小于0"),
    JST_CHANNEL_PAYOUT_FAILED(40016, "打款执行失败"),
    JST_CHANNEL_SETTLE_ILLEGAL_TRANSIT(40020, "提现单状态非法流转"),

    // ========== jst-points (5xxxx) ==========
    JST_POINTS_INSUFFICIENT(50001, "积分余额不足"),
    JST_POINTS_CONCURRENT_CONFLICT(50002, "积分账户并发冲突，请重试"),
    JST_MALL_GOODS_OFFLINE(50010, "商品已下架"),
    JST_MALL_STOCK_INSUFFICIENT(50011, "商品库存不足"),
    JST_MALL_GOODS_NOT_FOUND(50012, "商品不存在"),
    JST_MALL_EXCHANGE_STATUS_INVALID(50013, "兑换单状态非法流转"),
    JST_MALL_CANCEL_NOT_ALLOWED(50014, "当前兑换单不允许取消"),
    JST_MALL_EXCHANGE_NOT_FOUND(50015, "兑换单不存在"),
    JST_MALL_ROLE_NOT_ALLOWED(50016, "当前角色不允许执行此操作"),
    MALL_AFTERSALE_NOT_ALLOWED(60030, "当前兑换单不允许发起售后"),
    MALL_AFTERSALE_COUPON_USED(60031, "虚拟券已使用，不可退"),
    MALL_AFTERSALE_STATUS_INVALID(60032, "售后单状态非法流转"),

    // ========== jst-organizer (6xxxx) ==========
    JST_ORG_APPLY_NOT_FOUND(60001, "入驻申请不存在"),
    JST_ORG_APPLY_ILLEGAL_TRANSIT(60002, "入驻申请状态非法流转"),
    JST_ORG_APPLY_DUPLICATE(60003, "存在待处理或已通过的入驻申请"),
    JST_CHANNEL_AUTH_APPLY_NOT_FOUND(60010, "渠道认证申请不存在"),
    JST_CHANNEL_AUTH_APPLY_DUPLICATE(60011, "渠道认证申请重复提交"),
    JST_CHANNEL_AUTH_APPLY_ILLEGAL_TRANSIT(60012, "渠道认证申请状态非法流转"),
    JST_MARKETING_COUPON_TEMPLATE_NOT_FOUND(60050, "优惠券模板不存在"),
    JST_MARKETING_COUPON_CLAIM_LIMIT(60051, "当前优惠券已达到领取上限"),
    JST_MARKETING_COUPON_EXPIRED(60052, "当前优惠券已过期"),
    JST_MARKETING_RIGHTS_TEMPLATE_NOT_FOUND(60060, "权益模板不存在"),
    JST_MARKETING_RIGHTS_NOT_OWNED(60061, "当前权益不属于登录用户"),
    JST_MARKETING_RIGHTS_EXPIRED(60062, "当前权益已过期"),
    JST_MARKETING_RIGHTS_REQUIRE_SCAN(60063, "当前权益需从扫码入口核销"),
    JST_MARKETING_RIGHTS_STATUS_INVALID(60064, "当前权益状态不允许执行此操作"),

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
    JST_FINANCE_SETTLEMENT_NOT_FOUND(99003, "赛事结算单不存在"),
    JST_FINANCE_SETTLEMENT_STATUS_INVALID(99004, "赛事结算单状态非法流转"),

    // ========== E0 phase-e-prep ==========
    JST_CHANNEL_AUTH_LOCKED(10040, "认证申请已达最大驳回次数，请联系客服"),
    JST_APPOINTMENT_REFUND_NOT_ALLOWED(10041, "本活动不支持预约退款"),
    JST_CHANNEL_UNBIND_FAILED(10042, "解绑学生失败"),
    JST_PARTICIPANT_AUTO_CLAIM_CONFLICT(10043, "临时档案自动认领存在多个候选，需人工处理"),

    // ========== common ==========
    JST_COMMON_BIZ_NO_RULE_NOT_FOUND(99910, "编号规则不存在"),
    JST_COMMON_BIZ_NO_RULE_DISABLED(99911, "编号规则未启用"),
    JST_COMMON_BIZ_NO_RULE_CODE_DUPLICATE(99912, "编号规则编码重复"),
    JST_COMMON_BIZ_NO_GENERATE_FAILED(99913, "编号生成失败"),
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
