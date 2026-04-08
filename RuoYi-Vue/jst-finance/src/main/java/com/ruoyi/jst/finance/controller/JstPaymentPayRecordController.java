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
import com.ruoyi.jst.finance.domain.JstPaymentPayRecord;
import com.ruoyi.jst.finance.service.IJstPaymentPayRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 统一打款记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/finance/jst_payment_pay_record")
public class JstPaymentPayRecordController extends BaseController
{
    @Autowired
    private IJstPaymentPayRecordService jstPaymentPayRecordService;

    /**
     * 查询统一打款记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:payment_pay_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstPaymentPayRecord jstPaymentPayRecord)
    {
        startPage();
        List<JstPaymentPayRecord> list = jstPaymentPayRecordService.selectJstPaymentPayRecordList(jstPaymentPayRecord);
        return getDataTable(list);
    }

    /**
     * 导出统一打款记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:payment_pay_record:export')")
    @Log(title = "统一打款记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstPaymentPayRecord jstPaymentPayRecord)
    {
        List<JstPaymentPayRecord> list = jstPaymentPayRecordService.selectJstPaymentPayRecordList(jstPaymentPayRecord);
        ExcelUtil<JstPaymentPayRecord> util = new ExcelUtil<JstPaymentPayRecord>(JstPaymentPayRecord.class);
        util.exportExcel(response, list, "统一打款记录数据");
    }

    /**
     * 获取统一打款记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:payment_pay_record:query')")
    @GetMapping(value = "/{payRecordId}")
    public AjaxResult getInfo(@PathVariable("payRecordId") Long payRecordId)
    {
        return success(jstPaymentPayRecordService.selectJstPaymentPayRecordByPayRecordId(payRecordId));
    }

    /**
     * 新增统一打款记录
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:payment_pay_record:add')")
    @Log(title = "统一打款记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstPaymentPayRecord jstPaymentPayRecord)
    {
        return toAjax(jstPaymentPayRecordService.insertJstPaymentPayRecord(jstPaymentPayRecord));
    }

    /**
     * 修改统一打款记录
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:payment_pay_record:edit')")
    @Log(title = "统一打款记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstPaymentPayRecord jstPaymentPayRecord)
    {
        return toAjax(jstPaymentPayRecordService.updateJstPaymentPayRecord(jstPaymentPayRecord));
    }

    /**
     * 删除统一打款记录
     */
    @PreAuthorize("@ss.hasPermi('jst:finance:payment_pay_record:remove')")
    @Log(title = "统一打款记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{payRecordIds}")
    public AjaxResult remove(@PathVariable Long[] payRecordIds)
    {
        return toAjax(jstPaymentPayRecordService.deleteJstPaymentPayRecordByPayRecordIds(payRecordIds));
    }
}
