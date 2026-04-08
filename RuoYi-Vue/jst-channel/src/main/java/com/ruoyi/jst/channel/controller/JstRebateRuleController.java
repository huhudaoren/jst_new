package com.ruoyi.jst.channel.controller;

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
import com.ruoyi.jst.channel.domain.JstRebateRule;
import com.ruoyi.jst.channel.service.IJstRebateRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 渠道返点规则Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/channel/jst_rebate_rule")
public class JstRebateRuleController extends BaseController
{
    @Autowired
    private IJstRebateRuleService jstRebateRuleService;

    /**
     * 查询渠道返点规则列表
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRebateRule jstRebateRule)
    {
        startPage();
        List<JstRebateRule> list = jstRebateRuleService.selectJstRebateRuleList(jstRebateRule);
        return getDataTable(list);
    }

    /**
     * 导出渠道返点规则列表
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_rule:export')")
    @Log(title = "渠道返点规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRebateRule jstRebateRule)
    {
        List<JstRebateRule> list = jstRebateRuleService.selectJstRebateRuleList(jstRebateRule);
        ExcelUtil<JstRebateRule> util = new ExcelUtil<JstRebateRule>(JstRebateRule.class);
        util.exportExcel(response, list, "渠道返点规则数据");
    }

    /**
     * 获取渠道返点规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_rule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId)
    {
        return success(jstRebateRuleService.selectJstRebateRuleByRuleId(ruleId));
    }

    /**
     * 新增渠道返点规则
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_rule:add')")
    @Log(title = "渠道返点规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRebateRule jstRebateRule)
    {
        return toAjax(jstRebateRuleService.insertJstRebateRule(jstRebateRule));
    }

    /**
     * 修改渠道返点规则
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_rule:edit')")
    @Log(title = "渠道返点规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRebateRule jstRebateRule)
    {
        return toAjax(jstRebateRuleService.updateJstRebateRule(jstRebateRule));
    }

    /**
     * 删除渠道返点规则
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_rule:remove')")
    @Log(title = "渠道返点规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(jstRebateRuleService.deleteJstRebateRuleByRuleIds(ruleIds));
    }
}
