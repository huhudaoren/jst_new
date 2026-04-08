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
import com.ruoyi.system.domain.JstRightsTemplate;
import com.ruoyi.system.service.IJstRightsTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 权益模板Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_rights_template")
public class JstRightsTemplateController extends BaseController
{
    @Autowired
    private IJstRightsTemplateService jstRightsTemplateService;

    /**
     * 查询权益模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_template:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRightsTemplate jstRightsTemplate)
    {
        startPage();
        List<JstRightsTemplate> list = jstRightsTemplateService.selectJstRightsTemplateList(jstRightsTemplate);
        return getDataTable(list);
    }

    /**
     * 导出权益模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_template:export')")
    @Log(title = "权益模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRightsTemplate jstRightsTemplate)
    {
        List<JstRightsTemplate> list = jstRightsTemplateService.selectJstRightsTemplateList(jstRightsTemplate);
        ExcelUtil<JstRightsTemplate> util = new ExcelUtil<JstRightsTemplate>(JstRightsTemplate.class);
        util.exportExcel(response, list, "权益模板数据");
    }

    /**
     * 获取权益模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_template:query')")
    @GetMapping(value = "/{rightsTemplateId}")
    public AjaxResult getInfo(@PathVariable("rightsTemplateId") Long rightsTemplateId)
    {
        return success(jstRightsTemplateService.selectJstRightsTemplateByRightsTemplateId(rightsTemplateId));
    }

    /**
     * 新增权益模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_template:add')")
    @Log(title = "权益模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRightsTemplate jstRightsTemplate)
    {
        return toAjax(jstRightsTemplateService.insertJstRightsTemplate(jstRightsTemplate));
    }

    /**
     * 修改权益模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_template:edit')")
    @Log(title = "权益模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRightsTemplate jstRightsTemplate)
    {
        return toAjax(jstRightsTemplateService.updateJstRightsTemplate(jstRightsTemplate));
    }

    /**
     * 删除权益模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_rights_template:remove')")
    @Log(title = "权益模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{rightsTemplateIds}")
    public AjaxResult remove(@PathVariable Long[] rightsTemplateIds)
    {
        return toAjax(jstRightsTemplateService.deleteJstRightsTemplateByRightsTemplateIds(rightsTemplateIds));
    }
}
