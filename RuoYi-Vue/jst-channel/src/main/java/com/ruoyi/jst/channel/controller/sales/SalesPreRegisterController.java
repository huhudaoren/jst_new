package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.channel.dto.SalesPreRegisterCreateReqDTO;
import com.ruoyi.jst.channel.service.SalesPreRegisterService;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 销售本人预录入管理。
 * <p>
 * 类级权限继承自 BaseSalesController：jst_sales / jst_sales_manager / admin / jst_operator。
 * 方法级用具体 perms 收口。
 * <p>
 * 路径前缀 /sales/me/pre-register（触发 SalesScopeWebConfig 的导出水印拦截器
 * 和 RestrictedSalesActionInterceptor 限权拦截器，plan-02 Task 16 完善）。
 */
@RestController
@RequestMapping("/sales/me/pre-register")
public class SalesPreRegisterController extends BaseSalesController {

    @Autowired
    private SalesPreRegisterService preRegisterService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:list')")
    public TableDataInfo list() {
        Long salesId = SalesScopeUtils.currentSalesIdOrAllowAdminView();
        startPage();
        if (salesId == null) {
            return getDataTable(preRegisterService.listAllForAdmin());
        }
        return getDataTable(preRegisterService.listMine(salesId));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:add')")
    @Log(title = "新建预录入", businessType = BusinessType.INSERT)
    public AjaxResult create(@Valid @RequestBody SalesPreRegisterCreateReqDTO req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        return AjaxResult.success(preRegisterService.create(salesId, req.getPhone(), req.getTargetName()));
    }

    @PostMapping("/{preId}/renew")
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:renew')")
    @Log(title = "续期预录入", businessType = BusinessType.UPDATE)
    public AjaxResult renew(@PathVariable Long preId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        preRegisterService.renew(preId, salesId);
        return AjaxResult.success();
    }

    @DeleteMapping("/{preId}")
    @PreAuthorize("@ss.hasPermi('sales:me:prereg:remove')")
    @Log(title = "删除预录入", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long preId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        preRegisterService.remove(preId, salesId);
        return AjaxResult.success();
    }
}
