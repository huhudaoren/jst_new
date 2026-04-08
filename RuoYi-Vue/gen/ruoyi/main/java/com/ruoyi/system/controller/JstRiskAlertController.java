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
import com.ruoyi.system.domain.JstRiskAlert;
import com.ruoyi.system.service.IJstRiskAlertService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风险预警Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_risk_alert")
public class JstRiskAlertController extends BaseController
{
    @Autowired
    private IJstRiskAlertService jstRiskAlertService;

    /**
     * 查询风险预警列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_alert:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRiskAlert jstRiskAlert)
    {
        startPage();
        List<JstRiskAlert> list = jstRiskAlertService.selectJstRiskAlertList(jstRiskAlert);
        return getDataTable(list);
    }

    /**
     * 导出风险预警列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_alert:export')")
    @Log(title = "风险预警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRiskAlert jstRiskAlert)
    {
        List<JstRiskAlert> list = jstRiskAlertService.selectJstRiskAlertList(jstRiskAlert);
        ExcelUtil<JstRiskAlert> util = new ExcelUtil<JstRiskAlert>(JstRiskAlert.class);
        util.exportExcel(response, list, "风险预警数据");
    }

    /**
     * 获取风险预警详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_alert:query')")
    @GetMapping(value = "/{alertId}")
    public AjaxResult getInfo(@PathVariable("alertId") Long alertId)
    {
        return success(jstRiskAlertService.selectJstRiskAlertByAlertId(alertId));
    }

    /**
     * 新增风险预警
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_alert:add')")
    @Log(title = "风险预警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRiskAlert jstRiskAlert)
    {
        return toAjax(jstRiskAlertService.insertJstRiskAlert(jstRiskAlert));
    }

    /**
     * 修改风险预警
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_alert:edit')")
    @Log(title = "风险预警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRiskAlert jstRiskAlert)
    {
        return toAjax(jstRiskAlertService.updateJstRiskAlert(jstRiskAlert));
    }

    /**
     * 删除风险预警
     */
    @PreAuthorize("@ss.hasPermi('system:jst_risk_alert:remove')")
    @Log(title = "风险预警", businessType = BusinessType.DELETE)
	@DeleteMapping("/{alertIds}")
    public AjaxResult remove(@PathVariable Long[] alertIds)
    {
        return toAjax(jstRiskAlertService.deleteJstRiskAlertByAlertIds(alertIds));
    }
}
