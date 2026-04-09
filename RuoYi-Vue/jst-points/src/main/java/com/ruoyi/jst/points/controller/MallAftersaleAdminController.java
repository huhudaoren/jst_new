package com.ruoyi.jst.points.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.points.dto.AftersaleAuditDTO;
import com.ruoyi.jst.points.dto.AftersaleQueryReqDTO;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * 商城售后后台 Controller。
 */
@Validated
@RestController
@RequestMapping("/jst/points/mall/aftersale")
public class MallAftersaleAdminController extends BaseController {

    @Autowired
    private MallAftersaleService mallAftersaleService;

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:audit')")
    @GetMapping("/list")
    public TableDataInfo list(AftersaleQueryReqDTO query) {
        startPage();
        return getDataTable(mallAftersaleService.selectAdminList(query));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:audit')")
    @GetMapping("/{refundId}")
    public AjaxResult detail(@PathVariable("refundId") Long refundId) {
        return AjaxResult.success(mallAftersaleService.getAdminDetail(refundId));
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:audit')")
    @PostMapping("/{refundId}/approve")
    public AjaxResult approve(@PathVariable("refundId") Long refundId, @Valid @RequestBody AftersaleAuditDTO req) {
        mallAftersaleService.approve(refundId, req);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:points:mall:aftersale:audit')")
    @PostMapping("/{refundId}/reject")
    public AjaxResult reject(@PathVariable("refundId") Long refundId, @Valid @RequestBody AftersaleAuditDTO req) {
        mallAftersaleService.reject(refundId, req);
        return AjaxResult.success();
    }
}
