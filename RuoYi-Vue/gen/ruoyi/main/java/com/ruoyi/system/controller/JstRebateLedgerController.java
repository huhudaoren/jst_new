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
import com.ruoyi.system.domain.JstRebateLedger;
import com.ruoyi.system.service.IJstRebateLedgerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 返点计提台账Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_rebate_ledger")
public class JstRebateLedgerController extends BaseController
{
    @Autowired
    private IJstRebateLedgerService jstRebateLedgerService;

    /**
     * 查询返点计提台账列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rebate_ledger:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRebateLedger jstRebateLedger)
    {
        startPage();
        List<JstRebateLedger> list = jstRebateLedgerService.selectJstRebateLedgerList(jstRebateLedger);
        return getDataTable(list);
    }

    /**
     * 导出返点计提台账列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rebate_ledger:export')")
    @Log(title = "返点计提台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRebateLedger jstRebateLedger)
    {
        List<JstRebateLedger> list = jstRebateLedgerService.selectJstRebateLedgerList(jstRebateLedger);
        ExcelUtil<JstRebateLedger> util = new ExcelUtil<JstRebateLedger>(JstRebateLedger.class);
        util.exportExcel(response, list, "返点计提台账数据");
    }

    /**
     * 获取返点计提台账详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rebate_ledger:query')")
    @GetMapping(value = "/{ledgerId}")
    public AjaxResult getInfo(@PathVariable("ledgerId") Long ledgerId)
    {
        return success(jstRebateLedgerService.selectJstRebateLedgerByLedgerId(ledgerId));
    }

    /**
     * 新增返点计提台账
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rebate_ledger:add')")
    @Log(title = "返点计提台账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRebateLedger jstRebateLedger)
    {
        return toAjax(jstRebateLedgerService.insertJstRebateLedger(jstRebateLedger));
    }

    /**
     * 修改返点计提台账
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rebate_ledger:edit')")
    @Log(title = "返点计提台账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRebateLedger jstRebateLedger)
    {
        return toAjax(jstRebateLedgerService.updateJstRebateLedger(jstRebateLedger));
    }

    /**
     * 删除返点计提台账
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rebate_ledger:remove')")
    @Log(title = "返点计提台账", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ledgerIds}")
    public AjaxResult remove(@PathVariable Long[] ledgerIds)
    {
        return toAjax(jstRebateLedgerService.deleteJstRebateLedgerByLedgerIds(ledgerIds));
    }
}
