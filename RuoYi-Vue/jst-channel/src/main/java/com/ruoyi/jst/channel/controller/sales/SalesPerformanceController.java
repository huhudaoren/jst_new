package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.channel.dto.SalesPerformanceVO;
import com.ruoyi.jst.channel.service.SalesPerformanceService;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 销售本人业绩聚合视图。
 * <p>
 * 返回的 VO 含 @Sensitive(money=true) 字段：销售本人调用时由 MaskSalaryAspect 自动置 null；
 * 主管 / admin 调用时返完整金额。
 */
@RestController
@RequestMapping("/sales/me/performance")
public class SalesPerformanceController extends BaseSalesController {

    @Autowired
    private SalesPerformanceService performanceService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('sales:me:performance:view')")
    public AjaxResult getMine(@RequestParam(required = false) String month) {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        SalesPerformanceVO vo = performanceService.aggregate(salesId, month);
        return AjaxResult.success(vo);
    }
}
