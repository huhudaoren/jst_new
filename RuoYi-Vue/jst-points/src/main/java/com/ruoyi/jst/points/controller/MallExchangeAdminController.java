package com.ruoyi.jst.points.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.points.dto.ExchangeQueryReqDTO;
import com.ruoyi.jst.points.dto.ExchangeShipDTO;
import com.ruoyi.jst.points.service.MallExchangeService;
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
 * 后台兑换订单 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/points/mall/exchange")
public class MallExchangeAdminController extends BaseController {

    @Autowired
    private MallExchangeService mallExchangeService;

    @PreAuthorize("@ss.hasPermi('jst:points:mall:exchange:list')")
    @GetMapping("/list")
    public TableDataInfo list(ExchangeQueryReqDTO query) {
        startPage();
        return getDataTable(mallExchangeService.selectAdminList(query));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:exchange:list')")
    @GetMapping("/{exchangeId}")
    public AjaxResult detail(@PathVariable("exchangeId") Long exchangeId) {
        return AjaxResult.success(mallExchangeService.getAdminDetail(exchangeId));
    }

    @Log(title = "商城兑换发货", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:points:mall:exchange:ship')")
    @PostMapping("/{exchangeId}/ship")
    public AjaxResult ship(@PathVariable("exchangeId") Long exchangeId, @Valid @RequestBody ExchangeShipDTO req) {
        mallExchangeService.ship(exchangeId, req);
        return AjaxResult.success();
    }

    @Log(title = "商城兑换完成", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:points:mall:exchange:ship')")
    @PostMapping("/{exchangeId}/complete")
    public AjaxResult complete(@PathVariable("exchangeId") Long exchangeId) {
        mallExchangeService.complete(exchangeId);
        return AjaxResult.success();
    }
}
