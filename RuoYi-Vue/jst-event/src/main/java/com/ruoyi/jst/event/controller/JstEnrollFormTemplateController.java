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
import com.ruoyi.jst.event.domain.JstEnrollFormTemplate;
import com.ruoyi.jst.event.service.IJstEnrollFormTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报名动态单模板（schema_json定义字段）Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/event/jst_enroll_form_template")
public class JstEnrollFormTemplateController extends BaseController
{
    @Autowired
    private IJstEnrollFormTemplateService jstEnrollFormTemplateService;

    /**
     * 查询报名动态单模板（schema_json定义字段）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_form_template:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        startPage();
        List<JstEnrollFormTemplate> list = jstEnrollFormTemplateService.selectJstEnrollFormTemplateList(jstEnrollFormTemplate);
        return getDataTable(list);
    }

    /**
     * 导出报名动态单模板（schema_json定义字段）列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_form_template:export')")
    @Log(title = "报名动态单模板（schema_json定义字段）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        List<JstEnrollFormTemplate> list = jstEnrollFormTemplateService.selectJstEnrollFormTemplateList(jstEnrollFormTemplate);
        ExcelUtil<JstEnrollFormTemplate> util = new ExcelUtil<JstEnrollFormTemplate>(JstEnrollFormTemplate.class);
        util.exportExcel(response, list, "报名动态单模板（schema_json定义字段）数据");
    }

    /**
     * 获取报名动态单模板（schema_json定义字段）详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_form_template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return success(jstEnrollFormTemplateService.selectJstEnrollFormTemplateByTemplateId(templateId));
    }

    /**
     * 新增报名动态单模板（schema_json定义字段）
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_form_template:add')")
    @Log(title = "报名动态单模板（schema_json定义字段）", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        return toAjax(jstEnrollFormTemplateService.insertJstEnrollFormTemplate(jstEnrollFormTemplate));
    }

    /**
     * 修改报名动态单模板（schema_json定义字段）
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_form_template:edit')")
    @Log(title = "报名动态单模板（schema_json定义字段）", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        return toAjax(jstEnrollFormTemplateService.updateJstEnrollFormTemplate(jstEnrollFormTemplate));
    }

    /**
     * 删除报名动态单模板（schema_json定义字段）
     */
    @PreAuthorize("@ss.hasPermi('jst:event:enroll_form_template:remove')")
    @Log(title = "报名动态单模板（schema_json定义字段）", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(jstEnrollFormTemplateService.deleteJstEnrollFormTemplateByTemplateIds(templateIds));
    }
}
