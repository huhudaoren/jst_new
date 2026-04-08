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
import com.ruoyi.system.domain.JstRiskRule;
import com.ruoyi.system.service.IJstRiskRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风控规则Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_risk_rule")
public class JstRiskRuleController extends BaseController
{
    @Autowired
    private IJstRiskRuleService jstRiskRuleService;

    /**
     * 查询风控规则列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRiskRule jstRiskRule)
    {
        startPage();
        List<JstRiskRule> list = jstRiskRuleService.selectJstRiskRuleList(jstRiskRule);
        return getDataTable(list);
    }

    /**
     * 导出风控规则列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_rule:export')")
    @Log(title = "风控规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRiskRule jstRiskRule)
    {
        List<JstRiskRule> list = jstRiskRuleService.selectJstRiskRuleList(jstRiskRule);
        ExcelUtil<JstRiskRule> util = new ExcelUtil<JstRiskRule>(JstRiskRule.class);
        util.exportExcel(response, list, "风控规则数据");
    }

    /**
     * 获取风控规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_rule:query')")
    @GetMapping(value = "/{riskRuleId}")
    public AjaxResult getInfo(@PathVariable("riskRuleId") Long riskRuleId)
    {
        return success(jstRiskRuleService.selectJstRiskRuleByRiskRuleId(riskRuleId));
    }

    /**
     * 新增风控规则
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_rule:add')")
    @Log(title = "风控规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRiskRule jstRiskRule)
    {
        return toAjax(jstRiskRuleService.insertJstRiskRule(jstRiskRule));
    }

    /**
     * 修改风控规则
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_rule:edit')")
    @Log(title = "风控规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRiskRule jstRiskRule)
    {
        return toAjax(jstRiskRuleService.updateJstRiskRule(jstRiskRule));
    }

    /**
     * 删除风控规则
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_rule:remove')")
    @Log(title = "风控规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{riskRuleIds}")
    public AjaxResult remove(@PathVariable Long[] riskRuleIds)
    {
        return toAjax(jstRiskRuleService.deleteJstRiskRuleByRiskRuleIds(riskRuleIds));
    }
}
