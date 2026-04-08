package com.ruoyi.jst.order.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.order.dto.CreateOrderReqDTO;
import com.ruoyi.jst.order.dto.OrderQueryReqDTO;
import com.ruoyi.jst.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序订单 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/order")
public class WxOrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/create")
    public AjaxResult create(@Valid @RequestBody CreateOrderReqDTO req) {
        return AjaxResult.success(orderService.createMixedPay(req));
    }

    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/{orderId}")
    public AjaxResult detail(@PathVariable("orderId") Long orderId) {
        return AjaxResult.success(orderService.getWxDetail(orderId));
    }

    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public TableDataInfo my(@Valid OrderQueryReqDTO query) {
        startPage();
        return getDataTable(orderService.selectMyList(query));
    }

    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/{orderId}/cancel")
    public AjaxResult cancel(@PathVariable("orderId") Long orderId) {
        orderService.cancel(orderId);
        return AjaxResult.success();
    }
}
