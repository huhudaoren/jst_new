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
 * 微信支付真实实现占位。
 *
 * @author jst
 * @since 1.0.0
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
}
