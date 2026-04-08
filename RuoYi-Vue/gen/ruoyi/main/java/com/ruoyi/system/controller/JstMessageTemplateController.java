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
import com.ruoyi.system.domain.JstMessageTemplate;
import com.ruoyi.system.service.IJstMessageTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 消息模板Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_message_template")
public class JstMessageTemplateController extends BaseController
{
    @Autowired
    private IJstMessageTemplateService jstMessageTemplateService;

    /**
     * 查询消息模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_template:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstMessageTemplate jstMessageTemplate)
    {
        startPage();
        List<JstMessageTemplate> list = jstMessageTemplateService.selectJstMessageTemplateList(jstMessageTemplate);
        return getDataTable(list);
    }

    /**
     * 导出消息模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_template:export')")
    @Log(title = "消息模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstMessageTemplate jstMessageTemplate)
    {
        List<JstMessageTemplate> list = jstMessageTemplateService.selectJstMessageTemplateList(jstMessageTemplate);
        ExcelUtil<JstMessageTemplate> util = new ExcelUtil<JstMessageTemplate>(JstMessageTemplate.class);
        util.exportExcel(response, list, "消息模板数据");
    }

    /**
     * 获取消息模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return success(jstMessageTemplateService.selectJstMessageTemplateByTemplateId(templateId));
    }

    /**
     * 新增消息模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_template:add')")
    @Log(title = "消息模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstMessageTemplate jstMessageTemplate)
    {
        return toAjax(jstMessageTemplateService.insertJstMessageTemplate(jstMessageTemplate));
    }

    /**
     * 修改消息模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_template:edit')")
    @Log(title = "消息模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstMessageTemplate jstMessageTemplate)
    {
        return toAjax(jstMessageTemplateService.updateJstMessageTemplate(jstMessageTemplate));
    }

    /**
     * 删除消息模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_template:remove')")
    @Log(title = "消息模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(jstMessageTemplateService.deleteJstMessageTemplateByTemplateIds(templateIds));
    }
}
