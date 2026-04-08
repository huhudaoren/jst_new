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
import com.ruoyi.system.domain.JstCertTemplate;
import com.ruoyi.system.service.IJstCertTemplateService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 证书模板Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_cert_template")
public class JstCertTemplateController extends BaseController
{
    @Autowired
    private IJstCertTemplateService jstCertTemplateService;

    /**
     * 查询证书模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_cert_template:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstCertTemplate jstCertTemplate)
    {
        startPage();
        List<JstCertTemplate> list = jstCertTemplateService.selectJstCertTemplateList(jstCertTemplate);
        return getDataTable(list);
    }

    /**
     * 导出证书模板列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_cert_template:export')")
    @Log(title = "证书模板", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstCertTemplate jstCertTemplate)
    {
        List<JstCertTemplate> list = jstCertTemplateService.selectJstCertTemplateList(jstCertTemplate);
        ExcelUtil<JstCertTemplate> util = new ExcelUtil<JstCertTemplate>(JstCertTemplate.class);
        util.exportExcel(response, list, "证书模板数据");
    }

    /**
     * 获取证书模板详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_cert_template:query')")
    @GetMapping(value = "/{templateId}")
    public AjaxResult getInfo(@PathVariable("templateId") Long templateId)
    {
        return success(jstCertTemplateService.selectJstCertTemplateByTemplateId(templateId));
    }

    /**
     * 新增证书模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_cert_template:add')")
    @Log(title = "证书模板", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstCertTemplate jstCertTemplate)
    {
        return toAjax(jstCertTemplateService.insertJstCertTemplate(jstCertTemplate));
    }

    /**
     * 修改证书模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_cert_template:edit')")
    @Log(title = "证书模板", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstCertTemplate jstCertTemplate)
    {
        return toAjax(jstCertTemplateService.updateJstCertTemplate(jstCertTemplate));
    }

    /**
     * 删除证书模板
     */
    @PreAuthorize("@ss.hasPermi('system:jst_cert_template:remove')")
    @Log(title = "证书模板", businessType = BusinessType.DELETE)
	@DeleteMapping("/{templateIds}")
    public AjaxResult remove(@PathVariable Long[] templateIds)
    {
        return toAjax(jstCertTemplateService.deleteJstCertTemplateByTemplateIds(templateIds));
    }
}
