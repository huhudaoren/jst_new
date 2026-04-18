package com.ruoyi.jst.finance.controller.wx;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.finance.dto.InvoiceTitleSaveReqDTO;
import com.ruoyi.jst.finance.service.InvoiceTitleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序-发票抬头管理 Controller。
 * <p>
 * 路径前缀：/jst/wx/user/invoice-title
 * 对应任务：BACKEND-UX-POLISH-TODO-ROUND-2.md · B3
 * <p>
 * 权限：任一已登录角色（jst_student / jst_channel / jst_partner）可管理自己的抬头，
 * 用户归属校验在 Service 层二次确认（防横向越权）。
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/user/invoice-title")
public class WxInvoiceTitleController extends BaseController {

    @Autowired
    private InvoiceTitleService invoiceTitleService;

    /**
     * 当前用户发票抬头列表（默认排前）。
     *
     * @return List&lt;InvoiceTitleVO&gt;
     * @关联表 jst_invoice_title
     * @关联权限 已登录
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")
    @GetMapping("/list")
    public AjaxResult list() {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(invoiceTitleService.listByUser(userId));
    }

    /**
     * 新增 / 更新 抬头（titleId 非空 = 更新）。
     *
     * @param req 抬头请求 DTO
     * @return AjaxResult { data: titleId }
     * @关联表 jst_invoice_title
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")
    @Log(title = "发票抬头保存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult save(@Valid @RequestBody InvoiceTitleSaveReqDTO req) {
        Long userId = SecurityUtils.getUserId();
        Long titleId = invoiceTitleService.save(userId, req);
        return AjaxResult.success(titleId);
    }

    /**
     * 软删抬头（归属校验）。
     *
     * @param id 抬头 ID
     * @return AjaxResult.success
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")
    @Log(title = "发票抬头删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable("id") Long id) {
        Long userId = SecurityUtils.getUserId();
        invoiceTitleService.softDelete(userId, id);
        return AjaxResult.success();
    }

    /**
     * 设为默认（事务内先清旧默认再设新）。
     *
     * @param id 抬头 ID
     * @return AjaxResult.success
     */
    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel') or @ss.hasRole('jst_partner')")
    @Log(title = "发票抬头设默认", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/default")
    public AjaxResult setDefault(@PathVariable("id") Long id) {
        Long userId = SecurityUtils.getUserId();
        invoiceTitleService.setDefault(userId, id);
        return AjaxResult.success();
    }
}
