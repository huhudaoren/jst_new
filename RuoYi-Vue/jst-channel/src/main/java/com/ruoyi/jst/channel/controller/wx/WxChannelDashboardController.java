package com.ruoyi.jst.channel.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.channel.dto.ChannelDashboardQueryDTO;
import com.ruoyi.jst.channel.service.ChannelDashboardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Wx endpoints for channel dashboard pages.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/channel")
public class WxChannelDashboardController extends BaseController {

    @Autowired
    private ChannelDashboardService channelDashboardService;

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:monthly')")
    @GetMapping("/dashboard/monthly")
    public AjaxResult monthly(@Valid ChannelDashboardQueryDTO query) {
        return AjaxResult.success(channelDashboardService.getMonthly(query));
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:students')")
    @GetMapping("/students")
    public TableDataInfo students(@Valid ChannelDashboardQueryDTO query) {
        return getDataTable(channelDashboardService.selectStudentList(query));
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:orders')")
    @GetMapping("/orders")
    public TableDataInfo orders(@Valid ChannelDashboardQueryDTO query) {
        return getDataTable(channelDashboardService.selectOrderList(query));
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:dashboard:stats')")
    @GetMapping("/dashboard/stats")
    public AjaxResult stats(@Valid ChannelDashboardQueryDTO query) {
        return AjaxResult.success(channelDashboardService.getStats(query));
    }
}
