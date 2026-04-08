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
import com.ruoyi.system.domain.JstRightsWriteoffRecord;
import com.ruoyi.system.service.IJstRightsWriteoffRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 权益核销记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_rights_writeoff_record")
public class JstRightsWriteoffRecordController extends BaseController
{
    @Autowired
    private IJstRightsWriteoffRecordService jstRightsWriteoffRecordService;

    /**
     * 查询权益核销记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_writeoff_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        startPage();
        List<JstRightsWriteoffRecord> list = jstRightsWriteoffRecordService.selectJstRightsWriteoffRecordList(jstRightsWriteoffRecord);
        return getDataTable(list);
    }

    /**
     * 导出权益核销记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_writeoff_record:export')")
    @Log(title = "权益核销记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        List<JstRightsWriteoffRecord> list = jstRightsWriteoffRecordService.selectJstRightsWriteoffRecordList(jstRightsWriteoffRecord);
        ExcelUtil<JstRightsWriteoffRecord> util = new ExcelUtil<JstRightsWriteoffRecord>(JstRightsWriteoffRecord.class);
        util.exportExcel(response, list, "权益核销记录数据");
    }

    /**
     * 获取权益核销记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_writeoff_record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(jstRightsWriteoffRecordService.selectJstRightsWriteoffRecordByRecordId(recordId));
    }

    /**
     * 新增权益核销记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_writeoff_record:add')")
    @Log(title = "权益核销记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        return toAjax(jstRightsWriteoffRecordService.insertJstRightsWriteoffRecord(jstRightsWriteoffRecord));
    }

    /**
     * 修改权益核销记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_writeoff_record:edit')")
    @Log(title = "权益核销记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        return toAjax(jstRightsWriteoffRecordService.updateJstRightsWriteoffRecord(jstRightsWriteoffRecord));
    }

    /**
     * 删除权益核销记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_writeoff_record:remove')")
    @Log(title = "权益核销记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(jstRightsWriteoffRecordService.deleteJstRightsWriteoffRecordByRecordIds(recordIds));
    }
}
