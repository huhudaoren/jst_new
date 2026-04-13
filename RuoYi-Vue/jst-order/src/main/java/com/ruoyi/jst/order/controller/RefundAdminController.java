package com.ruoyi.jst.order.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.common.scope.PartnerScope;
import com.ruoyi.jst.order.dto.RefundAuditDTO;
import com.ruoyi.jst.order.dto.RefundQueryReqDTO;
import com.ruoyi.jst.order.service.RefundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台退款 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
public class RefundAdminController extends BaseController {

    @Autowired
    private RefundService refundService;

    /**
     * 查询退款列表。
     *
     * @param query 查询条件
     * @return 分页列表
     */
    @PreAuthorize("@ss.hasRole('jst_partner') or @ss.hasPermi('jst:order:refund:list')")
    @PartnerScope(field = "partnerId")
    @GetMapping("/jst/order/refund/list")
    public TableDataInfo list(@Valid RefundQueryReqDTO query) {
        startPage();
        return getDataTable(refundService.selectAdminList(query));
    }

    /**
     * 审核通过退款申请。
     *
     * @param refundId 退款单ID
     * @param req      审核备注
     * @return 操作结果
     */
    @Log(title = "退款审核通过", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner') or @ss.hasPermi('jst:order:refund:approve')")
    @PostMapping("/jst/order/refund/{refundId}/approve")
    public AjaxResult approve(@PathVariable("refundId") Long refundId, @Valid @RequestBody RefundAuditDTO req) {
        refundService.approve(refundId, req);
        return AjaxResult.success();
    }

    /**
     * 驳回退款申请。
     *
     * @param refundId 退款单ID
     * @param req      审核备注
     * @return 操作结果
     */
    @Log(title = "退款审核驳回", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasRole('jst_partner') or @ss.hasPermi('jst:order:refund:approve')")
    @PostMapping("/jst/order/refund/{refundId}/reject")
    public AjaxResult reject(@PathVariable("refundId") Long refundId, @Valid @RequestBody RefundAuditDTO req) {
        refundService.reject(refundId, req);
        return AjaxResult.success();
    }

    /**
     * 执行退款。
     *
     * @param refundId 退款单ID
     * @return 操作结果
     */
    @Log(title = "退款执行", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:order:refund:execute')")
    @PostMapping("/jst/order/refund/{refundId}/execute")
    public AjaxResult execute(@PathVariable("refundId") Long refundId) {
        refundService.execute(refundId);
        return AjaxResult.success();
    }

    /**
     * 发起平台特批退款。
     *
     * @param orderId 订单ID
     * @param req     特批备注
     * @return refundId / refundNo
     */
    @Log(title = "平台特批退款", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('jst:order:refund:special')")
    @PostMapping("/jst/admin/order/{orderId}/special-refund")
    public AjaxResult specialRefund(@PathVariable("orderId") Long orderId, @Valid @RequestBody RefundAuditDTO req) {
        return AjaxResult.success(refundService.specialRefund(orderId, req));
    }
}
