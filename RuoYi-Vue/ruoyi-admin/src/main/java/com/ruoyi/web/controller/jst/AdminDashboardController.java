package com.ruoyi.web.controller.jst;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.jst.dto.AdminDashboardTopReqDTO;
import com.ruoyi.web.controller.jst.dto.AdminDashboardTrendReqDTO;
import com.ruoyi.web.service.jst.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台运营数据聚合 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/admin/dashboard")
public class AdminDashboardController extends BaseController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    /**
     * 查询平台运营总览。
     *
     * @return 总览指标
     * @关联表 jst_order_main / jst_refund_record / jst_user / jst_contest / jst_enroll_record
     * @关联权限 jst:admin:dashboard
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")
    @GetMapping("/overview")
    public AjaxResult overview() {
        return AjaxResult.success(adminDashboardService.getOverview());
    }

    /**
     * 查询平台运营趋势。
     *
     * @param query 查询参数
     * @return 趋势列表
     * @关联表 jst_order_main / jst_enroll_record
     * @关联权限 jst:admin:dashboard
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")
    @GetMapping("/trend")
    public AjaxResult trend(AdminDashboardTrendReqDTO query) {
        Integer days = resolveDays(query);
        if (days == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "days 仅支持 7 或 30");
        }
        return AjaxResult.success(adminDashboardService.getTrend(days));
    }

    /**
     * 查询赛事报名排行。
     *
     * @param query 查询参数
     * @return 排行列表
     * @关联表 jst_contest / jst_enroll_record / jst_order_main
     * @关联权限 jst:admin:dashboard
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")
    @GetMapping("/top-contests")
    public AjaxResult topContests(AdminDashboardTopReqDTO query) {
        Integer limit = resolveLimit(query);
        if (limit == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "limit 最小为 1，最大为 10");
        }
        return AjaxResult.success(adminDashboardService.getTopContests(limit));
    }

    /**
     * 查询渠道返点排行。
     *
     * @param query 查询参数
     * @return 排行列表
     * @关联表 jst_channel / jst_rebate_ledger / jst_student_channel_binding
     * @关联权限 jst:admin:dashboard
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")
    @GetMapping("/top-channels")
    public AjaxResult topChannels(AdminDashboardTopReqDTO query) {
        Integer limit = resolveLimit(query);
        if (limit == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "limit 最小为 1，最大为 10");
        }
        return AjaxResult.success(adminDashboardService.getTopChannels(limit));
    }

    /**
     * 查询平台待办计数。
     *
     * @return 待办计数
     * @关联表 jst_contest / jst_enroll_record / jst_refund_record / jst_rebate_settlement / jst_event_partner_apply / jst_channel_auth_apply
     * @关联权限 jst:admin:dashboard
     */
    @PreAuthorize("@ss.hasPermi('jst:admin:dashboard')")
    @GetMapping("/todo")
    public AjaxResult todo() {
        return AjaxResult.success(adminDashboardService.getTodo());
    }

    private Integer resolveDays(AdminDashboardTrendReqDTO query) {
        if (query == null || query.getDays() == null || query.getDays().isEmpty()) {
            return 7;
        }
        if ("7".equals(query.getDays()) || "30".equals(query.getDays())) {
            return Integer.parseInt(query.getDays());
        }
        return null;
    }

    private Integer resolveLimit(AdminDashboardTopReqDTO query) {
        if (query == null || query.getLimit() == null) {
            return 5;
        }
        if (query.getLimit() < 1 || query.getLimit() > 10) {
            return null;
        }
        return query.getLimit();
    }
}
