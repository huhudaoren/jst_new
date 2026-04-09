package com.ruoyi.jst.marketing.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.marketing.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wx campaign controller.
 */
@RestController
@RequestMapping("/jst/wx/campaign")
public class WxCampaignController extends BaseController {

    @Autowired
    private CampaignService campaignService;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')")
    @GetMapping("/list")
    public AjaxResult list() {
        return AjaxResult.success(campaignService.selectList(currentUserId()));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasPermi('jst:marketing:coupon:my')")
    @GetMapping("/{campaignId}")
    public AjaxResult detail(@PathVariable("campaignId") Long campaignId) {
        return AjaxResult.success(campaignService.getDetail(currentUserId(), campaignId));
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
