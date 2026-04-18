package com.ruoyi.jst.channel.controller.admin;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.channel.service.AdminSalesDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 销售 Dashboard
 * <p>
 * 路径前缀：/admin/sales/dashboard
 * 角色：admin / jst_operator / jst_finance / jst_analyst
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/sales/dashboard")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_finance,jst_analyst')")
public class AdminSalesDashboardController extends BaseController {

    @Autowired
    private AdminSalesDashboardService dashboardService;

    /**
     * 平台月度概览
     *
     * @param month 格式 YYYY-MM（缺省=当月）
     */
    @GetMapping("/overview")
    public AjaxResult overview(@RequestParam(required = false) String month) {
        return AjaxResult.success(dashboardService.getOverview(month));
    }

    /**
     * 单销售月度详情
     *
     * @param salesId 销售 ID
     * @param month   格式 YYYY-MM（缺省=当月）
     */
    @GetMapping("/sales/{salesId}")
    public AjaxResult salesDetail(@PathVariable Long salesId,
                                  @RequestParam(required = false) String month) {
        return AjaxResult.success(dashboardService.getSalesDetail(salesId, month));
    }

    /**
     * 各销售跟进活动汇总
     *
     * @param month 格式 YYYY-MM（缺省=当月）
     */
    @GetMapping("/followup-activity")
    public AjaxResult followupActivity(@RequestParam(required = false) String month) {
        return AjaxResult.success(dashboardService.getFollowupActivity(month));
    }
}
