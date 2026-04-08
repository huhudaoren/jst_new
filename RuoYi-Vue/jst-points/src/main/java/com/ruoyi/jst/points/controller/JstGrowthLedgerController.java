package com.ruoyi.jst.points.controller;

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
import com.ruoyi.jst.points.domain.JstGrowthLedger;
import com.ruoyi.jst.points.service.IJstGrowthLedgerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 成长值流水Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/points/jst_growth_ledger")
public class JstGrowthLedgerController extends BaseController
{
    @Autowired
    private IJstGrowthLedgerService jstGrowthLedgerService;

    /**
     * 查询成长值流水列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:growth_ledger:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstGrowthLedger jstGrowthLedger)
    {
        startPage();
        List<JstGrowthLedger> list = jstGrowthLedgerService.selectJstGrowthLedgerList(jstGrowthLedger);
        return getDataTable(list);
    }

    /**
     * 导出成长值流水列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:growth_ledger:export')")
    @Log(title = "成长值流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstGrowthLedger jstGrowthLedger)
    {
        List<JstGrowthLedger> list = jstGrowthLedgerService.selectJstGrowthLedgerList(jstGrowthLedger);
        ExcelUtil<JstGrowthLedger> util = new ExcelUtil<JstGrowthLedger>(JstGrowthLedger.class);
        util.exportExcel(response, list, "成长值流水数据");
    }

    /**
     * 获取成长值流水详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:points:growth_ledger:query')")
    @GetMapping(value = "/{ledgerId}")
    public AjaxResult getInfo(@PathVariable("ledgerId") Long ledgerId)
    {
        return success(jstGrowthLedgerService.selectJstGrowthLedgerByLedgerId(ledgerId));
    }

    /**
     * 新增成长值流水
     */
    @PreAuthorize("@ss.hasPermi('jst:points:growth_ledger:add')")
    @Log(title = "成长值流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstGrowthLedger jstGrowthLedger)
    {
        return toAjax(jstGrowthLedgerService.insertJstGrowthLedger(jstGrowthLedger));
    }

    /**
     * 修改成长值流水
     */
    @PreAuthorize("@ss.hasPermi('jst:points:growth_ledger:edit')")
    @Log(title = "成长值流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstGrowthLedger jstGrowthLedger)
    {
        return toAjax(jstGrowthLedgerService.updateJstGrowthLedger(jstGrowthLedger));
    }

    /**
     * 删除成长值流水
     */
    @PreAuthorize("@ss.hasPermi('jst:points:growth_ledger:remove')")
    @Log(title = "成长值流水", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ledgerIds}")
    public AjaxResult remove(@PathVariable Long[] ledgerIds)
    {
        return toAjax(jstGrowthLedgerService.deleteJstGrowthLedgerByLedgerIds(ledgerIds));
    }
}
