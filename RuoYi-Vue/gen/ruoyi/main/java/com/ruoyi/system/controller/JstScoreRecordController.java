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
import com.ruoyi.system.domain.JstScoreRecord;
import com.ruoyi.system.service.IJstScoreRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 成绩记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_score_record")
public class JstScoreRecordController extends BaseController
{
    @Autowired
    private IJstScoreRecordService jstScoreRecordService;

    /**
     * 查询成绩记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_score_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstScoreRecord jstScoreRecord)
    {
        startPage();
        List<JstScoreRecord> list = jstScoreRecordService.selectJstScoreRecordList(jstScoreRecord);
        return getDataTable(list);
    }

    /**
     * 导出成绩记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_score_record:export')")
    @Log(title = "成绩记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstScoreRecord jstScoreRecord)
    {
        List<JstScoreRecord> list = jstScoreRecordService.selectJstScoreRecordList(jstScoreRecord);
        ExcelUtil<JstScoreRecord> util = new ExcelUtil<JstScoreRecord>(JstScoreRecord.class);
        util.exportExcel(response, list, "成绩记录数据");
    }

    /**
     * 获取成绩记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_score_record:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        return success(jstScoreRecordService.selectJstScoreRecordByScoreId(scoreId));
    }

    /**
     * 新增成绩记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_score_record:add')")
    @Log(title = "成绩记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstScoreRecord jstScoreRecord)
    {
        return toAjax(jstScoreRecordService.insertJstScoreRecord(jstScoreRecord));
    }

    /**
     * 修改成绩记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_score_record:edit')")
    @Log(title = "成绩记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstScoreRecord jstScoreRecord)
    {
        return toAjax(jstScoreRecordService.updateJstScoreRecord(jstScoreRecord));
    }

    /**
     * 删除成绩记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_score_record:remove')")
    @Log(title = "成绩记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        return toAjax(jstScoreRecordService.deleteJstScoreRecordByScoreIds(scoreIds));
    }
}
