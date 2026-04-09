package com.ruoyi.jst.points.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.points.dto.ExchangeApplyDTO;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序兑换订单 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/wx/mall/exchange")
public class WxMallExchangeController extends BaseController {

    @Autowired
    private MallExchangeService mallExchangeService;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @PostMapping("/apply")
    public AjaxResult apply(@Valid @RequestBody ExchangeApplyDTO req) {
        return AjaxResult.success(mallExchangeService.apply(req));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @PostMapping("/{exchangeId}/pay/mock-success")
    public AjaxResult mockPaySuccess(@PathVariable("exchangeId") Long exchangeId) {
        mallExchangeService.mockPaySuccess(exchangeId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @PostMapping("/{exchangeId}/cancel")
    public AjaxResult cancel(@PathVariable("exchangeId") Long exchangeId) {
        mallExchangeService.cancel(exchangeId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/my")
    public TableDataInfo my(@RequestParam(value = "status", required = false) String status) {
        startPage();
        return getDataTable(mallExchangeService.selectMyList(status));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/{exchangeId}")
    public AjaxResult detail(@PathVariable("exchangeId") Long exchangeId) {
        return AjaxResult.success(mallExchangeService.getMyDetail(exchangeId));
    }
}
