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

    /**
     * 提成成本趋势。
     *
     * @param bucket       聚合粒度：day / week / month
     * @param startDate    开始日期（yyyy-MM-dd，含）
     * @param endDate      结束日期（yyyy-MM-dd，含）
     * @param businessType 业务类型（可选）
     * @param region       地区关键字（可选）
     */
    @GetMapping("/commission-trend")
    public AjaxResult commissionTrend(@RequestParam(defaultValue = "day") String bucket,
                                      @RequestParam String startDate,
                                      @RequestParam String endDate,
                                      @RequestParam(required = false) String businessType,
                                      @RequestParam(required = false) String region) {
        return AjaxResult.success(dashboardService.getCommissionTrend(bucket, startDate, endDate, businessType, region));
    }

    /**
     * J 上限压缩统计。
     *
     * @param startDate    开始日期（yyyy-MM-dd，含）
     * @param endDate      结束日期（yyyy-MM-dd，含）
     * @param businessType 业务类型（可选）
     * @param region       地区关键字（可选）
     */
    @GetMapping("/compression-stats")
    public AjaxResult compressionStats(@RequestParam String startDate,
                                       @RequestParam String endDate,
                                       @RequestParam(required = false) String businessType,
                                       @RequestParam(required = false) String region) {
        return AjaxResult.success(dashboardService.getCompressionStats(startDate, endDate, businessType, region));
    }

    /**
     * 渠道业绩热力图。
     *
     * @param startDate    开始日期（yyyy-MM-dd，含）
     * @param endDate      结束日期（yyyy-MM-dd，含）
     * @param businessType 业务类型（可选）
     * @param region       地区关键字（可选）
     */
    @GetMapping("/channel-heatmap")
    public AjaxResult channelHeatmap(@RequestParam String startDate,
                                     @RequestParam String endDate,
                                     @RequestParam(required = false) String businessType,
                                     @RequestParam(required = false) String region) {
        return AjaxResult.success(dashboardService.getChannelHeatmap(startDate, endDate, businessType, region));
    }
}
