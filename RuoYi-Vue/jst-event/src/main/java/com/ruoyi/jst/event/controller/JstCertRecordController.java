package com.ruoyi.jst.event.controller;

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
import com.ruoyi.jst.event.domain.JstCertRecord;
import com.ruoyi.jst.event.service.IJstCertRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 证书记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/event/jst_cert_record")
public class JstCertRecordController extends BaseController
{
    @Autowired
    private IJstCertRecordService jstCertRecordService;

    /**
     * 查询证书记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstCertRecord jstCertRecord)
    {
        startPage();
        List<JstCertRecord> list = jstCertRecordService.selectJstCertRecordList(jstCertRecord);
        return getDataTable(list);
    }

    /**
     * 导出证书记录列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:export')")
    @Log(title = "证书记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstCertRecord jstCertRecord)
    {
        List<JstCertRecord> list = jstCertRecordService.selectJstCertRecordList(jstCertRecord);
        ExcelUtil<JstCertRecord> util = new ExcelUtil<JstCertRecord>(JstCertRecord.class);
        util.exportExcel(response, list, "证书记录数据");
    }

    /**
     * 获取证书记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:query')")
    @GetMapping(value = "/{certId}")
    public AjaxResult getInfo(@PathVariable("certId") Long certId)
    {
        return success(jstCertRecordService.selectJstCertRecordByCertId(certId));
    }

    /**
     * 新增证书记录
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:add')")
    @Log(title = "证书记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstCertRecord jstCertRecord)
    {
        return toAjax(jstCertRecordService.insertJstCertRecord(jstCertRecord));
    }

    /**
     * 修改证书记录
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:edit')")
    @Log(title = "证书记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstCertRecord jstCertRecord)
    {
        return toAjax(jstCertRecordService.updateJstCertRecord(jstCertRecord));
    }

    /**
     * 删除证书记录
     */
    @PreAuthorize("@ss.hasPermi('jst:event:cert_record:remove')")
    @Log(title = "证书记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{certIds}")
    public AjaxResult remove(@PathVariable Long[] certIds)
    {
        return toAjax(jstCertRecordService.deleteJstCertRecordByCertIds(certIds));
    }
}
