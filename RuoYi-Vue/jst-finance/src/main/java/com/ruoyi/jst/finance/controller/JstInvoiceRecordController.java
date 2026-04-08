package com.ruoyi.jst.finance.controller;

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
import com.ruoyi.jst.finance.domain.JstInvoiceRecord;
import com.ruoyi.jst.finance.service.IJstInvoiceRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 发票记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/finance/jst_invoice_record")
public class JstInvoiceRecordController extends BaseController
{
    @Autowired
    private IJstInvoiceRecordService jstInvoiceRecordService;

    /**
     * 查询发票记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:invoice_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstInvoiceRecord jstInvoiceRecord)
    {
        startPage();
        List<JstInvoiceRecord> list = jstInvoiceRecordService.selectJstInvoiceRecordList(jstInvoiceRecord);
        return getDataTable(list);
    }

    /**
     * 导出发票记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:invoice_record:export')")
    @Log(title = "发票记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstInvoiceRecord jstInvoiceRecord)
    {
        List<JstInvoiceRecord> list = jstInvoiceRecordService.selectJstInvoiceRecordList(jstInvoiceRecord);
        ExcelUtil<JstInvoiceRecord> util = new ExcelUtil<JstInvoiceRecord>(JstInvoiceRecord.class);
        util.exportExcel(response, list, "发票记录数据");
    }

    /**
     * 获取发票记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:invoice_record:query')")
    @GetMapping(value = "/{invoiceId}")
    public AjaxResult getInfo(@PathVariable("invoiceId") Long invoiceId)
    {
        return success(jstInvoiceRecordService.selectJstInvoiceRecordByInvoiceId(invoiceId));
    }

    /**
     * 新增发票记录
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:invoice_record:add')")
    @Log(title = "发票记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstInvoiceRecord jstInvoiceRecord)
    {
        return toAjax(jstInvoiceRecordService.insertJstInvoiceRecord(jstInvoiceRecord));
    }

    /**
     * 修改发票记录
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:invoice_record:edit')")
    @Log(title = "发票记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstInvoiceRecord jstInvoiceRecord)
    {
        return toAjax(jstInvoiceRecordService.updateJstInvoiceRecord(jstInvoiceRecord));
    }

    /**
     * 删除发票记录
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:invoice_record:remove')")
    @Log(title = "发票记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{invoiceIds}")
    public AjaxResult remove(@PathVariable Long[] invoiceIds)
    {
        return toAjax(jstInvoiceRecordService.deleteJstInvoiceRecordByInvoiceIds(invoiceIds));
    }
}
