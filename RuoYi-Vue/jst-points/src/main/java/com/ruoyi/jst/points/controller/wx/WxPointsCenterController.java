package com.ruoyi.jst.points.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.points.service.PointsCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序积分中心 Controller。
 */
@RestController
@RequestMapping("/jst/wx/points")
public class WxPointsCenterController extends BaseController {

    @Autowired
    private PointsCenterService pointsCenterService;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/center/summary")
    public AjaxResult summary() {
        return AjaxResult.success(pointsCenterService.getSummary(null));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/center/levels")
    public AjaxResult levels(@RequestParam(value = "role", required = false) String role) {
        return AjaxResult.success(pointsCenterService.getLevels(role));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/ledger")
    public TableDataInfo pointsLedger(@RequestParam(value = "changeType", required = false) String changeType) {
        startPage();
        return getDataTable(pointsCenterService.getPointsLedger(changeType));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/growth/ledger")
    public TableDataInfo growthLedger() {
        startPage();
        return getDataTable(pointsCenterService.getGrowthLedger());
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/tasks")
    public AjaxResult tasks() {
        return AjaxResult.success(pointsCenterService.getTasks());
    }
}
