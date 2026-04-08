package com.ruoyi.jst.order.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.order.dto.OrderQueryReqDTO;
import com.ruoyi.jst.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单后台 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/order/main")
public class OrderAdminController extends BaseController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("@ss.hasPermi('jst:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(@Valid OrderQueryReqDTO query) {
        startPage();
        return getDataTable(orderService.selectAdminList(query));
    }

    @PreAuthorize("@ss.hasPermi('jst:order:query')")
    @GetMapping("/{orderId}")
    public AjaxResult detail(@PathVariable("orderId") Long orderId) {
        return AjaxResult.success(orderService.getAdminDetail(orderId));
    }
}
