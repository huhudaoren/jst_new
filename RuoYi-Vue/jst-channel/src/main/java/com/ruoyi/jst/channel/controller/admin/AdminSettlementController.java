package com.ruoyi.jst.channel.controller.admin;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.domain.JstSalesCommissionLedger;
import com.ruoyi.jst.channel.domain.JstSalesCommissionSettlement;
import com.ruoyi.jst.channel.mapper.JstSalesCommissionLedgerMapper;
import com.ruoyi.jst.channel.service.SalesCommissionSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin 销售提成月结管理
 * <p>
 * 路径前缀：/admin/sales/settlement
 * 角色：admin / jst_operator / jst_finance
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/sales/settlement")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_finance')")
public class AdminSettlementController extends BaseController {

    @Autowired
    private SalesCommissionSettlementService settlementService;

    @Autowired
    private JstSalesCommissionLedgerMapper ledgerMapper;

    @Autowired
    private com.ruoyi.jst.channel.service.SalesService salesService;

    /**
     * 月结单列表（分页）。
     * <p>
     * Plan-06 Task 21: 销售主管登录时自动按下属 salesIds 过滤（只能看自己团队的月结单）；
     * admin / jst_operator 不过滤看全量。
     */
    @GetMapping("/list")
    @Log(title = "月结单", businessType = BusinessType.OTHER)
    public TableDataInfo list(JstSalesCommissionSettlement query) {
        // 销售主管自动按下属团队过滤
        com.ruoyi.common.core.domain.model.LoginUser u =
            com.ruoyi.jst.common.util.SalesScopeUtils.getLoginUserQuietly();
        if (com.ruoyi.jst.common.util.SalesScopeUtils.isManagerRole(u)
                && !(u.getUser() != null && u.getUser().isAdmin())) {
            Long managerSalesId = com.ruoyi.jst.common.util.SalesScopeUtils.currentSalesId();
            if (managerSalesId != null) {
                java.util.List<Long> teamIds = salesService.resolveScopeSalesIds(managerSalesId);
                if (teamIds == null || teamIds.isEmpty()) teamIds = java.util.Collections.singletonList(-1L);
                query.getParams().put("scopeSalesIds", teamIds);
            }
        }
        startPage();
        return getDataTable(settlementService.listForAdmin(query));
    }

    /**
     * 月结单详情（含关联 ledger 行）
     */
    @GetMapping("/{settlementId}")
    public AjaxResult detail(@PathVariable Long settlementId) {
        JstSalesCommissionSettlement settlement =
                settlementService.getBySettlementId(settlementId);
        if (settlement == null) {
            return AjaxResult.error("月结单不存在: " + settlementId);
        }
        // 查关联 ledger
        List<JstSalesCommissionLedger> ledgers = ledgerMapper.selectBySettlementId(settlementId);
        Map<String, Object> data = new HashMap<>();
        data.put("settlement", settlement);
        data.put("ledgers", ledgers);
        return AjaxResult.success(data);
    }

    /**
     * 审核通过
     */
    @PostMapping("/{settlementId}/approve")
    @Log(title = "月结单审核", businessType = BusinessType.UPDATE)
    public AjaxResult approve(@PathVariable Long settlementId) {
        settlementService.approve(settlementId, SecurityUtils.getUserId());
        return AjaxResult.success("审核通过");
    }

    /**
     * 驳回
     */
    @PostMapping("/{settlementId}/reject")
    @Log(title = "月结单驳回", businessType = BusinessType.UPDATE)
    public AjaxResult reject(@PathVariable Long settlementId,
                             @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "");
        settlementService.reject(settlementId, reason);
        return AjaxResult.success("已驳回");
    }

    /**
     * 录入打款凭证
     */
    @PostMapping("/{settlementId}/pay-record")
    @Log(title = "月结单打款", businessType = BusinessType.UPDATE)
    public AjaxResult payRecord(@PathVariable Long settlementId,
                                @RequestBody Map<String, String> body) {
        String voucher = body.getOrDefault("voucher", "");
        settlementService.recordPayment(settlementId, voucher);
        return AjaxResult.success("打款录入成功");
    }
}
