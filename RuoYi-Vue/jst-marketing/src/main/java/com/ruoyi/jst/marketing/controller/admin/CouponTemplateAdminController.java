package com.ruoyi.jst.marketing.controller.admin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.marketing.dto.CouponIssueDTO;
import com.ruoyi.jst.marketing.dto.CouponTemplateSaveDTO;
import com.ruoyi.jst.marketing.service.CouponTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Coupon template admin controller.
 */
@Validated
@RestController
@RequestMapping("/jst/marketing/coupon/template")
public class CouponTemplateAdminController extends BaseController {

    @Autowired
    private CouponTemplateService couponTemplateService;

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:list')")
    @GetMapping
    public TableDataInfo list(@RequestParam(value = "couponName", required = false) String couponName,
                              @RequestParam(value = "couponType", required = false) String couponType,
                              @RequestParam(value = "status", required = false) Integer status) {
        startPage();
        return getDataTable(couponTemplateService.selectAdminList(couponName, couponType, status));
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:query')")
    @GetMapping("/{couponTemplateId}")
    public AjaxResult detail(@PathVariable("couponTemplateId") Long couponTemplateId) {
        return AjaxResult.success(couponTemplateService.getAdminDetail(couponTemplateId));
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:add')")
    @PostMapping
    public AjaxResult create(@Valid @RequestBody CouponTemplateSaveDTO req) {
        return AjaxResult.success(couponTemplateService.create(req));
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:edit')")
    @PutMapping
    public AjaxResult update(@Valid @RequestBody CouponTemplateSaveDTO req) {
        couponTemplateService.update(req);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:remove')")
    @DeleteMapping("/{couponTemplateId}")
    public AjaxResult remove(@PathVariable("couponTemplateId") Long couponTemplateId) {
        couponTemplateService.delete(couponTemplateId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:publish')")
    @PostMapping("/{couponTemplateId}/publish")
    public AjaxResult publish(@PathVariable("couponTemplateId") Long couponTemplateId) {
        couponTemplateService.publish(couponTemplateId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:offline')")
    @PostMapping("/{couponTemplateId}/offline")
    public AjaxResult offline(@PathVariable("couponTemplateId") Long couponTemplateId) {
        couponTemplateService.offline(couponTemplateId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('jst:marketing:coupon:issue')")
    @PostMapping("/{couponTemplateId}/issue")
    public AjaxResult issue(@PathVariable("couponTemplateId") Long couponTemplateId,
                            @Valid @RequestBody CouponIssueDTO req) {
        return AjaxResult.success(couponTemplateService.issue(couponTemplateId, req));
    }
}
