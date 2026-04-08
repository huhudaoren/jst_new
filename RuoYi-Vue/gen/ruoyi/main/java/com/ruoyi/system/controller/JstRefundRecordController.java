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
import com.ruoyi.system.domain.JstRefundRecord;
import com.ruoyi.system.service.IJstRefundRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 退款/售后单Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_refund_record")
public class JstRefundRecordController extends BaseController
{
    @Autowired
    private IJstRefundRecordService jstRefundRecordService;

    /**
     * 查询退款/售后单列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_refund_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRefundRecord jstRefundRecord)
    {
        startPage();
        List<JstRefundRecord> list = jstRefundRecordService.selectJstRefundRecordList(jstRefundRecord);
        return getDataTable(list);
    }

    /**
     * 导出退款/售后单列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_refund_record:export')")
    @Log(title = "退款/售后单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRefundRecord jstRefundRecord)
    {
        List<JstRefundRecord> list = jstRefundRecordService.selectJstRefundRecordList(jstRefundRecord);
        ExcelUtil<JstRefundRecord> util = new ExcelUtil<JstRefundRecord>(JstRefundRecord.class);
        util.exportExcel(response, list, "退款/售后单数据");
    }

    /**
     * 获取退款/售后单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_refund_record:query')")
    @GetMapping(value = "/{refundId}")
    public AjaxResult getInfo(@PathVariable("refundId") Long refundId)
    {
        return success(jstRefundRecordService.selectJstRefundRecordByRefundId(refundId));
    }

    /**
     * 新增退款/售后单
     */
    @PreAuthorize("@ss.hasPermi('system:jst_refund_record:add')")
    @Log(title = "退款/售后单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRefundRecord jstRefundRecord)
    {
        return toAjax(jstRefundRecordService.insertJstRefundRecord(jstRefundRecord));
    }

    /**
     * 修改退款/售后单
     */
    @PreAuthorize("@ss.hasPermi('system:jst_refund_record:edit')")
    @Log(title = "退款/售后单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRefundRecord jstRefundRecord)
    {
        return toAjax(jstRefundRecordService.updateJstRefundRecord(jstRefundRecord));
    }

    /**
     * 删除退款/售后单
     */
    @PreAuthorize("@ss.hasPermi('system:jst_refund_record:remove')")
    @Log(title = "退款/售后单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{refundIds}")
    public AjaxResult remove(@PathVariable Long[] refundIds)
    {
        return toAjax(jstRefundRecordService.deleteJstRefundRecordByRefundIds(refundIds));
    }
}
