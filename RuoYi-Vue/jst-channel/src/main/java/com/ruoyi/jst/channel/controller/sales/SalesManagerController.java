package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.service.SalesService;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售主管工作台 — 团队管理。
 * <p>
 * 路径前缀：/sales/manager/team
 * 角色：jst_sales_manager / admin / jst_operator
 * <p>
 * 端点：
 * <ul>
 *   <li>GET / — 返下属销售列表 + 每人当月简要业绩</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/sales/manager/team")
@PreAuthorize("@ss.hasAnyRoles('jst_sales_manager,admin,jst_operator')")
public class SalesManagerController extends BaseController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private JstSalesCommissionLedgerMapper ledgerMapper;

    /**
     * 团队成员列表（含当月订单数/GMV/提成简要聚合）。
     * admin / jst_operator 返所有销售；sales_manager 仅返下属。
     *
     * @param month 格式 YYYY-MM（缺省=当月）
     */
    @GetMapping
    public AjaxResult listTeam(@RequestParam(required = false) String month) {
        YearMonth ym = (month == null || month.isBlank())
                ? YearMonth.now()
                : YearMonth.parse(month.trim());
        Date start = Date.from(ym.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(ym.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // 确定要展示的销售列表
        List<JstSales> salesList = resolveSalesScope();

        List<Map<String, Object>> result = new ArrayList<>();
        for (JstSales s : salesList) {
            Map<String, Object> row = new HashMap<>();
            row.put("salesId", s.getSalesId());
            row.put("salesNo", s.getSalesNo());
            row.put("salesName", s.getSalesName());
            row.put("phone", s.getPhone());
            row.put("status", s.getStatus());
            row.put("isManager", s.getIsManager());

            // 当月简要业绩聚合
            Map<String, Object> perf = ledgerMapper.aggregatePerformance(s.getSalesId(), start, end);
            if (perf == null) perf = new HashMap<>();
            row.put("monthOrderCount", perf.getOrDefault("orderCount", 0));
            row.put("monthGmv", perf.getOrDefault("totalGmv", 0));
            row.put("monthCommission", perf.getOrDefault("totalCommission", 0));
            result.add(row);
        }
        return AjaxResult.success(result);
    }

    // ===== private helpers =====

    private List<JstSales> resolveSalesScope() {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        if (salesId == null) {
            // admin / jst_operator：返全部销售
            return salesService.listForAdmin(new JstSales());
        }
        // 销售主管：返自己的下属
        return salesService.listSubordinates(salesId);
    }
}
