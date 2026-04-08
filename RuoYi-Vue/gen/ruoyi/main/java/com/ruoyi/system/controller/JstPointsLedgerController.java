package com.ruoyi.system.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.JstPointsLedger;
import com.ruoyi.system.service.IJstPointsLedgerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分流水Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_points_ledger")
public class JstPointsLedgerController extends BaseController
{
    @Autowired
    private IJstPointsLedgerService jstPointsLedgerService;

    /**
     * 查询积分流水列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_points_ledger:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstPointsLedger jstPointsLedger)
    {
        startPage();
        List<JstPointsLedger> list = jstPointsLedgerService.selectJstPointsLedgerList(jstPointsLedger);
        return getDataTable(list);
    }

    /**
     * 导出积分流水列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_points_ledger:export')")
    @Log(title = "积分流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstPointsLedger jstPointsLedger)
    {
        List<JstPointsLedger> list = jstPointsLedgerService.selectJstPointsLedgerList(jstPointsLedger);
        ExcelUtil<JstPointsLedger> util = new ExcelUtil<JstPointsLedger>(JstPointsLedger.class);
        util.exportExcel(response, list, "积分流水数据");
    }

    /**
     * 获取积分流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_points_ledger:query')")
    @GetMapping(value = "/{ledgerId}")
    public AjaxResult getInfo(@PathVariable("ledgerId") Long ledgerId)
    {
        return success(jstPointsLedgerService.selectJstPointsLedgerByLedgerId(ledgerId));
    }

    /**
     * 新增积分流水
     */
    @PreAuthorize("@ss.hasPermi('system:jst_points_ledger:add')")
    @Log(title = "积分流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstPointsLedger jstPointsLedger)
    {
        return toAjax(jstPointsLedgerService.insertJstPointsLedger(jstPointsLedger));
    }

    /**
     * 修改积分流水
     */
    @PreAuthorize("@ss.hasPermi('system:jst_points_ledger:edit')")
    @Log(title = "积分流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstPointsLedger jstPointsLedger)
    {
        return toAjax(jstPointsLedgerService.updateJstPointsLedger(jstPointsLedger));
    }

    /**
     * 删除积分流水
     */
    @PreAuthorize("@ss.hasPermi('system:jst_points_ledger:remove')")
    @Log(title = "积分流水", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ledgerIds}")
    public AjaxResult remove(@PathVariable Long[] ledgerIds)
    {
        return toAjax(jstPointsLedgerService.deleteJstPointsLedgerByLedgerIds(ledgerIds));
    }
}
