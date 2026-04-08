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
import com.ruoyi.system.domain.JstCourseLearnRecord;
import com.ruoyi.system.service.IJstCourseLearnRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程学习记录Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_course_learn_record")
public class JstCourseLearnRecordController extends BaseController
{
    @Autowired
    private IJstCourseLearnRecordService jstCourseLearnRecordService;

    /**
     * 查询课程学习记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_course_learn_record:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstCourseLearnRecord jstCourseLearnRecord)
    {
        startPage();
        List<JstCourseLearnRecord> list = jstCourseLearnRecordService.selectJstCourseLearnRecordList(jstCourseLearnRecord);
        return getDataTable(list);
    }

    /**
     * 导出课程学习记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_course_learn_record:export')")
    @Log(title = "课程学习记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstCourseLearnRecord jstCourseLearnRecord)
    {
        List<JstCourseLearnRecord> list = jstCourseLearnRecordService.selectJstCourseLearnRecordList(jstCourseLearnRecord);
        ExcelUtil<JstCourseLearnRecord> util = new ExcelUtil<JstCourseLearnRecord>(JstCourseLearnRecord.class);
        util.exportExcel(response, list, "课程学习记录数据");
    }

    /**
     * 获取课程学习记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_course_learn_record:query')")
    @GetMapping(value = "/{learnId}")
    public AjaxResult getInfo(@PathVariable("learnId") Long learnId)
    {
        return success(jstCourseLearnRecordService.selectJstCourseLearnRecordByLearnId(learnId));
    }

    /**
     * 新增课程学习记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_course_learn_record:add')")
    @Log(title = "课程学习记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstCourseLearnRecord jstCourseLearnRecord)
    {
        return toAjax(jstCourseLearnRecordService.insertJstCourseLearnRecord(jstCourseLearnRecord));
    }

    /**
     * 修改课程学习记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_course_learn_record:edit')")
    @Log(title = "课程学习记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstCourseLearnRecord jstCourseLearnRecord)
    {
        return toAjax(jstCourseLearnRecordService.updateJstCourseLearnRecord(jstCourseLearnRecord));
    }

    /**
     * 删除课程学习记录
     */
    @PreAuthorize("@ss.hasPermi('system:jst_course_learn_record:remove')")
    @Log(title = "课程学习记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{learnIds}")
    public AjaxResult remove(@PathVariable Long[] learnIds)
    {
        return toAjax(jstCourseLearnRecordService.deleteJstCourseLearnRecordByLearnIds(learnIds));
    }
}
