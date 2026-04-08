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
import com.ruoyi.jst.event.domain.JstEnrollRecord;
import com.ruoyi.jst.event.service.IJstEnrollRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报名记录（含动态单快照）Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/event/jst_enroll_record")
public class JstEnrollRecordController extends BaseController
{
    @Autowired
    private IJstEnrollRecordService jstEnrollRecordService;

    /**
     * 查询报名记录（含动态单快照）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstEnrollRecord jstEnrollRecord)
    {
        startPage();
        List<JstEnrollRecord> list = jstEnrollRecordService.selectJstEnrollRecordList(jstEnrollRecord);
        return getDataTable(list);
    }

    /**
     * 导出报名记录（含动态单快照）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_record:export')")
    @Log(title = "报名记录（含动态单快照）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstEnrollRecord jstEnrollRecord)
    {
        List<JstEnrollRecord> list = jstEnrollRecordService.selectJstEnrollRecordList(jstEnrollRecord);
        ExcelUtil<JstEnrollRecord> util = new ExcelUtil<JstEnrollRecord>(JstEnrollRecord.class);
        util.exportExcel(response, list, "报名记录（含动态单快照）数据");
    }

    /**
     * 获取报名记录（含动态单快照）详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_record:query')")
    @GetMapping(value = "/{enrollId}")
    public AjaxResult getInfo(@PathVariable("enrollId") Long enrollId)
    {
        return success(jstEnrollRecordService.selectJstEnrollRecordByEnrollId(enrollId));
    }

    /**
     * 新增报名记录（含动态单快照）
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_record:add')")
    @Log(title = "报名记录（含动态单快照）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstEnrollRecord jstEnrollRecord)
    {
        return toAjax(jstEnrollRecordService.insertJstEnrollRecord(jstEnrollRecord));
    }

    /**
     * 修改报名记录（含动态单快照）
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_record:edit')")
    @Log(title = "报名记录（含动态单快照）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstEnrollRecord jstEnrollRecord)
    {
        return toAjax(jstEnrollRecordService.updateJstEnrollRecord(jstEnrollRecord));
    }

    /**
     * 删除报名记录（含动态单快照）
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_record:remove')")
    @Log(title = "报名记录（含动态单快照）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{enrollIds}")
    public AjaxResult remove(@PathVariable Long[] enrollIds)
    {
        return toAjax(jstEnrollRecordService.deleteJstEnrollRecordByEnrollIds(enrollIds));
    }
}
