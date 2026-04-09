package com.ruoyi.jst.order.pay.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.order.vo.WechatPrepayVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Real WeChat pay implementation placeholder.
 */
@Service
@ConditionalOnProperty(prefix = "jst.wxpay", name = "enabled", havingValue = "true")
public class RealWxPayServiceImpl implements WxPayService {

    @Override
    public WechatPrepayVO unifiedOrder(JstOrderMain order) {
        throw new ServiceException("真实微信支付暂未接入", BizErrorCode.JST_ORDER_PAY_FAIL.code());
    }

    @Override
    public NotifyPayload parseNotify(String body, Map<String, String> headers) {
        throw new ServiceException("真实微信支付暂未接入", BizErrorCode.JST_ORDER_PAY_FAIL.code());
    }

    @Override
    public RefundResult refund(RefundRequest request) {
        throw new ServiceException("真实微信退款暂未接入", BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.code());
    }

    @Override
    public RefundNotifyPayload parseRefundNotify(String body, Map<String, String> headers) {
        throw new ServiceException("真实微信退款回调暂未接入", BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.code());
    }
}
