package com.ruoyi.jst.order.pay.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.order.vo.WechatPrepayVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * 微信支付 Mock 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
@ConditionalOnProperty(prefix = "jst.wxpay", name = "enabled", havingValue = "false", matchIfMissing = true)
public class MockWxPayServiceImpl implements WxPayService {

    @Override
    public WechatPrepayVO unifiedOrder(JstOrderMain order) {
        WechatPrepayVO vo = new WechatPrepayVO();
        vo.setPrepayId("MOCK_" + order.getOrderNo());
        vo.setNonceStr(UUID.randomUUID().toString().replace("-", ""));
        vo.setTimestamp(String.valueOf(System.currentTimeMillis() / 1000));
        vo.setPaySign("MOCK_SIGN_" + order.getOrderNo());
        vo.setMock(Boolean.TRUE);
        return vo;
    }

    @Override
    public NotifyPayload parseNotify(String body, Map<String, String> headers) {
        JSONObject jsonObject = JSON.parseObject(body);
        if (jsonObject == null || jsonObject.getString("outTradeNo") == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_WX_NOTIFY_INVALID.message(),
                    BizErrorCode.JST_ORDER_WX_NOTIFY_INVALID.code());
        }
        NotifyPayload payload = new NotifyPayload();
        payload.setOutTradeNo(jsonObject.getString("outTradeNo"));
        payload.setTransactionId(jsonObject.getString("transactionId"));
        return payload;
    }

    @Override
    public RefundResult refund(RefundRequest request) {
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setThirdPartyRefundNo("MOCK_RF_" + request.getRefundNo());
        return result;
    }

    @Override
    public RefundNotifyPayload parseRefundNotify(String body, Map<String, String> headers) {
        JSONObject jsonObject = JSON.parseObject(body);
        if (jsonObject == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_WX_REFUND_NOTIFY_INVALID.message(),
                    BizErrorCode.JST_ORDER_WX_REFUND_NOTIFY_INVALID.code());
        }
        String refundNo = jsonObject.getString("refundNo");
        if (refundNo == null) {
            refundNo = jsonObject.getString("outRefundNo");
        }
        if (refundNo == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_WX_REFUND_NOTIFY_INVALID.message(),
                    BizErrorCode.JST_ORDER_WX_REFUND_NOTIFY_INVALID.code());
        }
        RefundNotifyPayload payload = new RefundNotifyPayload();
        payload.setRefundNo(refundNo);
        payload.setThirdPartyRefundNo(jsonObject.getString("refundId"));
        return payload;
    }
}
