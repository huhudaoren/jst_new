package com.ruoyi.jst.risk.controller;

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
import com.ruoyi.jst.risk.domain.JstRiskCase;
import com.ruoyi.jst.risk.service.IJstRiskCaseService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风险工单Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/risk/jst_risk_case")
public class JstRiskCaseController extends BaseController
{
    @Autowired
    private IJstRiskCaseService jstRiskCaseService;

    /**
     * 查询风险工单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_case:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRiskCase jstRiskCase)
    {
        startPage();
        List<JstRiskCase> list = jstRiskCaseService.selectJstRiskCaseList(jstRiskCase);
        return getDataTable(list);
    }

    /**
     * 导出风险工单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_case:export')")
    @Log(title = "风险工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRiskCase jstRiskCase)
    {
        List<JstRiskCase> list = jstRiskCaseService.selectJstRiskCaseList(jstRiskCase);
        ExcelUtil<JstRiskCase> util = new ExcelUtil<JstRiskCase>(JstRiskCase.class);
        util.exportExcel(response, list, "风险工单数据");
    }

    /**
     * 获取风险工单详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_case:query')")
    @GetMapping(value = "/{caseId}")
    public AjaxResult getInfo(@PathVariable("caseId") Long caseId)
    {
        return success(jstRiskCaseService.selectJstRiskCaseByCaseId(caseId));
    }

    /**
     * 新增风险工单
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_case:add')")
    @Log(title = "风险工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRiskCase jstRiskCase)
    {
        return toAjax(jstRiskCaseService.insertJstRiskCase(jstRiskCase));
    }

    /**
     * 修改风险工单
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_case:edit')")
    @Log(title = "风险工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRiskCase jstRiskCase)
    {
        return toAjax(jstRiskCaseService.updateJstRiskCase(jstRiskCase));
    }

    /**
     * 删除风险工单
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_case:remove')")
    @Log(title = "风险工单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{caseIds}")
    public AjaxResult remove(@PathVariable Long[] caseIds)
    {
        return toAjax(jstRiskCaseService.deleteJstRiskCaseByCaseIds(caseIds));
    }
}
