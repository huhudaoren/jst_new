package com.ruoyi.jst.order.controller.wx;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.order.dto.BatchPayOrderReqDTO;
import com.ruoyi.jst.order.service.WxChannelOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序-渠道方代支付 Controller。
 * <p>
 * 路径前缀：/jst/wx/channel
 * 对应任务：BACKEND-UX-POLISH-TODO-ROUND-2.md · B1 批量支付
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/channel")
public class WxChannelOrderController extends BaseController {

    @Autowired
    private WxChannelOrderService wxChannelOrderService;

    /**
     * 渠道方批量支付当前 pending_pay 订单。
     *
     * @param req 订单 ID 列表（1~50 条）
     * @return 批量支付聚合响应（batchPayNo + totalAmount + items[]）
     * @关联表 jst_order_main
     * @关联状态机 SM-1（不发生跃迁，订单仍为 pending_pay，回调后由 /jst/wx/pay/notify 转 paid）
     * @关联权限 hasRole('jst_channel')
     */
    @PreAuthorize("@ss.hasRole('jst_channel')")
    @Log(title = "渠道批量支付", businessType = BusinessType.INSERT)
    @PostMapping("/orders/batch-pay")
    public AjaxResult batchPay(@Valid @RequestBody BatchPayOrderReqDTO req) {
        return AjaxResult.success(wxChannelOrderService.batchPay(req));
    }
}
