package com.ruoyi.jst.points.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.points.dto.AftersaleApplyDTO;
import com.ruoyi.jst.points.service.MallAftersaleService;
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
 * 小程序商城售后 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/wx/mall/aftersale")
public class WxMallAftersaleController extends BaseController {

    @Autowired
    private MallAftersaleService mallAftersaleService;

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:apply') or @ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @PostMapping("/apply")
    public AjaxResult apply(@Valid @RequestBody AftersaleApplyDTO req) {
        return AjaxResult.success(mallAftersaleService.apply(req));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:my') or @ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/my")
    public TableDataInfo my(@RequestParam(value = "status", required = false) String status) {
        startPage();
        return getDataTable(mallAftersaleService.selectMyList(status));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:my') or @ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/{refundId}")
    public AjaxResult detail(@PathVariable("refundId") Long refundId) {
        return AjaxResult.success(mallAftersaleService.getMyDetail(refundId));
    }
}
