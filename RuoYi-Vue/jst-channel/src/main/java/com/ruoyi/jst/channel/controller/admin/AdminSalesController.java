package com.ruoyi.jst.channel.controller.admin;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.channel.domain.JstSales;
import com.ruoyi.jst.channel.dto.SalesCreateOnestopReqDTO;
import com.ruoyi.jst.channel.dto.SalesCreateReqDTO;
import com.ruoyi.jst.channel.dto.SalesResignApplyReqDTO;
import com.ruoyi.jst.channel.dto.SalesSetManagerReqDTO;
import com.ruoyi.jst.channel.dto.SalesUpdateRateReqDTO;
import com.ruoyi.jst.channel.service.SalesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin 销售档案管理（admin / jst_operator 可访问）。
 * 路径前缀：/admin/sales
 * <p>
 * 端点：
 * <ul>
 *   <li>GET /admin/sales/list — 列表（分页）</li>
 *   <li>GET /admin/sales/{salesId} — 详情</li>
 *   <li>POST /admin/sales — 创建（关联 sys_user，自动生成 sales_no）</li>
 *   <li>PUT /admin/sales/{salesId}/rate — 改默认费率</li>
 *   <li>PUT /admin/sales/{salesId}/manager — 设置/取消主管</li>
 *   <li>POST /admin/sales/{salesId}/resign-apply — 申请离职（设 expectedResignDate）</li>
 *   <li>POST /admin/sales/{salesId}/resign-execute — 立即执行离职</li>
 *   <li>POST /admin/sales/{salesId}/transition-end — 终结过渡期</li>
 * </ul>
 */
@RestController
@RequestMapping("/admin/sales")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
public class AdminSalesController extends BaseController {

    @Autowired
    private SalesService salesService;

    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('jst:sales:list')")
    public TableDataInfo list(JstSales query) {
        startPage();
        return getDataTable(salesService.listForAdmin(query));
    }

    @GetMapping("/{salesId}")
    @PreAuthorize("@ss.hasPermi('jst:sales:list')")
    public AjaxResult detail(@PathVariable Long salesId) {
        return AjaxResult.success(salesService.getById(salesId));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('jst:sales:add')")
    @Log(title = "销售档案", businessType = BusinessType.INSERT)
    public AjaxResult create(@Valid @RequestBody SalesCreateReqDTO req) {
        JstSales row = new JstSales();
        row.setSysUserId(req.getSysUserId());
        row.setSalesName(req.getSalesName());
        row.setPhone(req.getPhone());
        row.setManagerId(req.getManagerId());
        row.setCommissionRateDefault(req.getCommissionRateDefault());
        row.setIsManager(Boolean.TRUE.equals(req.getAsManager()) ? 1 : 0);
        return AjaxResult.success(salesService.create(row));
    }

    /**
     * 一站式新建销售：同事务建 sys_user + sys_user_role + jst_sales。
     * <p>
     * 原 {@link #create} 端点仍保留（要求调用方已有 sys_user_id），本端点为前端主流入口。
     */
    @PostMapping("/onestop")
    @PreAuthorize("@ss.hasPermi('jst:sales:add')")
    @Log(title = "销售一站式新建", businessType = BusinessType.INSERT)
    public AjaxResult createOnestop(@Valid @RequestBody SalesCreateOnestopReqDTO req) {
        Long salesId = salesService.createOnestop(req);
        return AjaxResult.success(salesId);
    }

    @PutMapping("/{salesId}/rate")
    @PreAuthorize("@ss.hasPermi('jst:sales:edit:rate')")
    @Log(title = "销售费率", businessType = BusinessType.UPDATE)
    public AjaxResult updateRate(@PathVariable Long salesId, @Valid @RequestBody SalesUpdateRateReqDTO req) {
        salesService.updateDefaultRate(salesId, req.getRate());
        return AjaxResult.success();
    }

    @PutMapping("/{salesId}/manager")
    @PreAuthorize("@ss.hasPermi('jst:sales:edit:manager')")
    @Log(title = "销售主管", businessType = BusinessType.UPDATE)
    public AjaxResult updateManager(@PathVariable Long salesId, @RequestBody SalesSetManagerReqDTO req) {
        salesService.updateManager(salesId, req.getManagerId());
        return AjaxResult.success();
    }

    @PostMapping("/{salesId}/resign-apply")
    @PreAuthorize("@ss.hasPermi('jst:sales:resign-apply')")
    @Log(title = "申请离职", businessType = BusinessType.UPDATE)
    public AjaxResult applyResign(@PathVariable Long salesId, @Valid @RequestBody SalesResignApplyReqDTO req) {
        salesService.applyResign(salesId, req.getExpectedResignDate());
        return AjaxResult.success();
    }

    @PostMapping("/{salesId}/resign-execute")
    @PreAuthorize("@ss.hasPermi('jst:sales:resign-execute')")
    @Log(title = "立即执行离职", businessType = BusinessType.UPDATE)
    public AjaxResult executeResign(@PathVariable Long salesId) {
        salesService.executeResign(salesId);
        // Note: SalesResignationService (plan-02 Task 15) 会订阅离职事件做 binding 转移 + pre_register 失效 + sys_user 禁用
        return AjaxResult.success();
    }

    @PostMapping("/{salesId}/transition-end")
    @PreAuthorize("@ss.hasPermi('jst:sales:transition-end')")
    @Log(title = "终结过渡期", businessType = BusinessType.UPDATE)
    public AjaxResult endTransition(@PathVariable Long salesId) {
        salesService.endTransition(salesId);
        return AjaxResult.success();
    }
}
