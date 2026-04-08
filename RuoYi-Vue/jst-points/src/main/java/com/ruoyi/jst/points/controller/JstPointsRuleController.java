package com.ruoyi.jst.points.controller;

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
import com.ruoyi.jst.points.domain.JstPointsRule;
import com.ruoyi.jst.points.service.IJstPointsRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 积分/成长值规则Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/points/jst_points_rule")
public class JstPointsRuleController extends BaseController
{
    @Autowired
    private IJstPointsRuleService jstPointsRuleService;

    /**
     * 查询积分/成长值规则列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstPointsRule jstPointsRule)
    {
        startPage();
        List<JstPointsRule> list = jstPointsRuleService.selectJstPointsRuleList(jstPointsRule);
        return getDataTable(list);
    }

    /**
     * 导出积分/成长值规则列表
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_rule:export')")
    @Log(title = "积分/成长值规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstPointsRule jstPointsRule)
    {
        List<JstPointsRule> list = jstPointsRuleService.selectJstPointsRuleList(jstPointsRule);
        ExcelUtil<JstPointsRule> util = new ExcelUtil<JstPointsRule>(JstPointsRule.class);
        util.exportExcel(response, list, "积分/成长值规则数据");
    }

    /**
     * 获取积分/成长值规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_rule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId)
    {
        return success(jstPointsRuleService.selectJstPointsRuleByRuleId(ruleId));
    }

    /**
     * 新增积分/成长值规则
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_rule:add')")
    @Log(title = "积分/成长值规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstPointsRule jstPointsRule)
    {
        return toAjax(jstPointsRuleService.insertJstPointsRule(jstPointsRule));
    }

    /**
     * 修改积分/成长值规则
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_rule:edit')")
    @Log(title = "积分/成长值规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstPointsRule jstPointsRule)
    {
        return toAjax(jstPointsRuleService.updateJstPointsRule(jstPointsRule));
    }

    /**
     * 删除积分/成长值规则
     */
    @PreAuthorize("@ss.hasPermi('jst:points:points_rule:remove')")
    @Log(title = "积分/成长值规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(jstPointsRuleService.deleteJstPointsRuleByRuleIds(ruleIds));
    }
}
