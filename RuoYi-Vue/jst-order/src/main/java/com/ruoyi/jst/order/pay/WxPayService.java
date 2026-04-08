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
}
