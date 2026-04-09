package com.ruoyi.jst.marketing.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.marketing.dto.CouponSelectDTO;
import com.ruoyi.jst.marketing.service.CouponTemplateService;
import com.ruoyi.jst.marketing.service.CouponUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wx coupon controller.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/coupon")
public class WxCouponController extends BaseController {

    @Autowired
    private CouponTemplateService couponTemplateService;

    @Autowired
    private CouponUserService couponUserService;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')")
    @GetMapping("/template/claimable")
    public AjaxResult claimable() {
        return AjaxResult.success(couponTemplateService.selectClaimableTemplates(currentUserId()));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')")
    @PostMapping("/claim")
    public AjaxResult claim(@RequestParam("templateId") Long templateId) {
        return AjaxResult.success(couponUserService.claim(currentUserId(), templateId));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')")
    @GetMapping("/my")
    public TableDataInfo my(@RequestParam(value = "status", required = false) String status) {
        startPage();
        return getDataTable(couponUserService.selectMyCoupons(currentUserId(), status));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')")
    @PostMapping("/select")
    public AjaxResult select(@Valid @RequestBody CouponSelectDTO req) {
        return AjaxResult.success(couponUserService.selectBestCoupon(currentUserId(), req));
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return userId;
    }
}
