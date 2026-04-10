package com.ruoyi.jst.event.controller.partner;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.common.annotation.PartnerDataScope;
import com.ruoyi.jst.common.controller.BasePartnerController;
import com.ruoyi.jst.event.dto.PartnerDashboardQueryReqDTO;
import com.ruoyi.jst.event.service.PartnerDashboardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/jst/partner")
public class PartnerDashboardController extends BasePartnerController {

    @Autowired
    private PartnerDashboardService partnerDashboardService;

    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/dashboard/summary")
    public AjaxResult summary(@Valid PartnerDashboardQueryReqDTO query) {
        return AjaxResult.success(partnerDashboardService.getSummary(query));
    }

    @PreAuthorize("@ss.hasRole('jst_partner')")
    @PartnerDataScope(deptAlias = "c")
    @GetMapping("/dashboard/todo")
    public AjaxResult todo(@Valid PartnerDashboardQueryReqDTO query) {
        return AjaxResult.success(partnerDashboardService.getTodo(query));
    }

    @PreAuthorize("@ss.hasRole('jst_partner')")
    @GetMapping("/notice/recent")
    public AjaxResult recentNotice() {
        return AjaxResult.success(partnerDashboardService.getRecentNotices());
    }
}
