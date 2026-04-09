package com.ruoyi.jst.order.pay;

import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.vo.WechatPrepayVO;

import java.util.Map;

/**
 * 微信支付抽象。
 *
 * @author jst
 * @since 1.0.0
 */
public interface WxPayService {

    WechatPrepayVO unifiedOrder(JstOrderMain order);

    NotifyPayload parseNotify(String body, Map<String, String> headers);

    RefundResult refund(RefundRequest request);

    RefundNotifyPayload parseRefundNotify(String body, Map<String, String> headers);

    /**
     * 支付回调解析结果。
     */
    class NotifyPayload {
        private String outTradeNo;
        private String transactionId;

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public void setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }

    /**
     * 微信退款请求。
     */
    class RefundRequest {
        private String refundNo;
        private String orderNo;
        private String outTradeNo;
        private java.math.BigDecimal refundCash;
        private String reason;

        public String getRefundNo() {
            return refundNo;
        }

        public void setRefundNo(String refundNo) {
            this.refundNo = refundNo;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public String getOutTradeNo() {
            return outTradeNo;
        }

        public void setOutTradeNo(String outTradeNo) {
            this.outTradeNo = outTradeNo;
        }

        public java.math.BigDecimal getRefundCash() {
            return refundCash;
        }

        public void setRefundCash(java.math.BigDecimal refundCash) {
            this.refundCash = refundCash;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * 微信退款返回值。
     */
    class RefundResult {
        private boolean success;
        private String thirdPartyRefundNo;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getThirdPartyRefundNo() {
            return thirdPartyRefundNo;
        }

        public void setThirdPartyRefundNo(String thirdPartyRefundNo) {
            this.thirdPartyRefundNo = thirdPartyRefundNo;
        }
    }

    /**
     * 微信退款回调解析结果。
     */
    class RefundNotifyPayload {
        private String refundNo;
        private String thirdPartyRefundNo;

        public String getRefundNo() {
            return refundNo;
        }

        public void setRefundNo(String refundNo) {
            this.refundNo = refundNo;
        }

        public String getThirdPartyRefundNo() {
            return thirdPartyRefundNo;
        }

        public void setThirdPartyRefundNo(String thirdPartyRefundNo) {
            this.thirdPartyRefundNo = thirdPartyRefundNo;
        }
    }
}
