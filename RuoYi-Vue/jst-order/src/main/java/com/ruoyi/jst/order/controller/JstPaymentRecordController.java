package com.ruoyi.jst.order.controller;

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
import com.ruoyi.jst.order.domain.JstPaymentRecord;
import com.ruoyi.jst.order.service.IJstPaymentRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 支付记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/order/jst_payment_record")
public class JstPaymentRecordController extends BaseController
{
    @Autowired
    private IJstPaymentRecordService jstPaymentRecordService;

    /**
     * 查询支付记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:order:payment_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstPaymentRecord jstPaymentRecord)
    {
        startPage();
        List<JstPaymentRecord> list = jstPaymentRecordService.selectJstPaymentRecordList(jstPaymentRecord);
        return getDataTable(list);
    }

    /**
     * 导出支付记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:order:payment_record:export')")
    @Log(title = "支付记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstPaymentRecord jstPaymentRecord)
    {
        List<JstPaymentRecord> list = jstPaymentRecordService.selectJstPaymentRecordList(jstPaymentRecord);
        ExcelUtil<JstPaymentRecord> util = new ExcelUtil<JstPaymentRecord>(JstPaymentRecord.class);
        util.exportExcel(response, list, "支付记录数据");
    }

    /**
     * 获取支付记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:order:payment_record:query')")
    @GetMapping(value = "/{paymentId}")
    public AjaxResult getInfo(@PathVariable("paymentId") Long paymentId)
    {
        return success(jstPaymentRecordService.selectJstPaymentRecordByPaymentId(paymentId));
    }

    /**
     * 新增支付记录
     */
    @PreAuthorize("@ss.hasPermi('jst:order:payment_record:add')")
    @Log(title = "支付记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstPaymentRecord jstPaymentRecord)
    {
        return toAjax(jstPaymentRecordService.insertJstPaymentRecord(jstPaymentRecord));
    }

    /**
     * 修改支付记录
     */
    @PreAuthorize("@ss.hasPermi('jst:order:payment_record:edit')")
    @Log(title = "支付记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstPaymentRecord jstPaymentRecord)
    {
        return toAjax(jstPaymentRecordService.updateJstPaymentRecord(jstPaymentRecord));
    }

    /**
     * 删除支付记录
     */
    @PreAuthorize("@ss.hasPermi('jst:order:payment_record:remove')")
    @Log(title = "支付记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{paymentIds}")
    public AjaxResult remove(@PathVariable Long[] paymentIds)
    {
        return toAjax(jstPaymentRecordService.deleteJstPaymentRecordByPaymentIds(paymentIds));
    }
}
