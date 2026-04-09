package com.ruoyi.jst.order.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.order.dto.RefundApplyDTO;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序退款 Controller。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx")
public class WxRefundController extends BaseController {

    @Autowired
    private RefundService refundService;

    /**
     * 学生发起退款申请。
     *
     * @param orderId 订单ID
     * @param req     申请入参
     * @return refundId / refundNo
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/order/{orderId}/refund")
    public AjaxResult apply(@PathVariable("orderId") Long orderId, @Valid @RequestBody RefundApplyDTO req) {
        return AjaxResult.success(refundService.apply(orderId, req));
    }

    /**
     * 查询我的退款详情。
     *
     * @param refundId 退款单ID
     * @return 退款详情
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/refund/{refundId}")
    public AjaxResult detail(@PathVariable("refundId") Long refundId) {
        return AjaxResult.success(refundService.getWxDetail(refundId));
    }

    /**
     * 查询我的退款列表。
     *
     * @param query 查询条件
     * @return 列表
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/refund/my")
    public TableDataInfo my(@Valid RefundQueryReqDTO query) {
        startPage();
        return getDataTable(refundService.selectMyList(query));
    }

    /**
     * 学生撤销退款申请。
     *
     * @param refundId 退款单ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/refund/{refundId}/cancel")
    public AjaxResult cancel(@PathVariable("refundId") Long refundId) {
        refundService.cancel(refundId);
        return AjaxResult.success();
    }
}
