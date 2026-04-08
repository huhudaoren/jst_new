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
import com.ruoyi.jst.risk.domain.JstRiskBlacklist;
import com.ruoyi.jst.risk.service.IJstRiskBlacklistService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 风控黑白名单Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/risk/jst_risk_blacklist")
public class JstRiskBlacklistController extends BaseController
{
    @Autowired
    private IJstRiskBlacklistService jstRiskBlacklistService;

    /**
     * 查询风控黑白名单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_blacklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRiskBlacklist jstRiskBlacklist)
    {
        startPage();
        List<JstRiskBlacklist> list = jstRiskBlacklistService.selectJstRiskBlacklistList(jstRiskBlacklist);
        return getDataTable(list);
    }

    /**
     * 导出风控黑白名单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_blacklist:export')")
    @Log(title = "风控黑白名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRiskBlacklist jstRiskBlacklist)
    {
        List<JstRiskBlacklist> list = jstRiskBlacklistService.selectJstRiskBlacklistList(jstRiskBlacklist);
        ExcelUtil<JstRiskBlacklist> util = new ExcelUtil<JstRiskBlacklist>(JstRiskBlacklist.class);
        util.exportExcel(response, list, "风控黑白名单数据");
    }

    /**
     * 获取风控黑白名单详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_blacklist:query')")
    @GetMapping(value = "/{listId}")
    public AjaxResult getInfo(@PathVariable("listId") Long listId)
    {
        return success(jstRiskBlacklistService.selectJstRiskBlacklistByListId(listId));
    }

    /**
     * 新增风控黑白名单
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_blacklist:add')")
    @Log(title = "风控黑白名单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRiskBlacklist jstRiskBlacklist)
    {
        return toAjax(jstRiskBlacklistService.insertJstRiskBlacklist(jstRiskBlacklist));
    }

    /**
     * 修改风控黑白名单
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_blacklist:edit')")
    @Log(title = "风控黑白名单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRiskBlacklist jstRiskBlacklist)
    {
        return toAjax(jstRiskBlacklistService.updateJstRiskBlacklist(jstRiskBlacklist));
    }

    /**
     * 删除风控黑白名单
     */
    @PreAuthorize("@ss.hasPermi('jst:risk:risk_blacklist:remove')")
    @Log(title = "风控黑白名单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{listIds}")
    public AjaxResult remove(@PathVariable Long[] listIds)
    {
        return toAjax(jstRiskBlacklistService.deleteJstRiskBlacklistByListIds(listIds));
    }
}
