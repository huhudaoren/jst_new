package com.ruoyi.jst.order.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 小程序支付 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/jst/wx/pay")
public class WxPayController {

    @Autowired
    private OrderService orderService;

    @Value("${jst.wxpay.enabled:false}")
    private boolean wxPayEnabled;

    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/mock-success")
    public AjaxResult mockSuccess(@RequestParam("orderId") Long orderId) {
        if (wxPayEnabled) {
            throw new ServiceException("当前环境已启用真实微信支付，禁止使用 mock-success",
                    BizErrorCode.JST_ORDER_PAY_FAIL.code());
        }
        orderService.mockPaySuccess(orderId);
        return AjaxResult.success();
    }

    @Anonymous
    @PostMapping("/notify/wechat")
    public String notifyWechat(@RequestBody(required = false) String body,
                               @RequestHeader Map<String, String> headers) {
        return orderService.handleWechatNotify(body == null ? "{}" : body, headers);
    }
}
