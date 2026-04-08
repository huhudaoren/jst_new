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
import com.ruoyi.system.domain.JstEventSettlement;
import com.ruoyi.system.service.IJstEventSettlementService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事方结算单Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_event_settlement")
public class JstEventSettlementController extends BaseController
{
    @Autowired
    private IJstEventSettlementService jstEventSettlementService;

    /**
     * 查询赛事方结算单列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_settlement:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstEventSettlement jstEventSettlement)
    {
        startPage();
        List<JstEventSettlement> list = jstEventSettlementService.selectJstEventSettlementList(jstEventSettlement);
        return getDataTable(list);
    }

    /**
     * 导出赛事方结算单列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_settlement:export')")
    @Log(title = "赛事方结算单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstEventSettlement jstEventSettlement)
    {
        List<JstEventSettlement> list = jstEventSettlementService.selectJstEventSettlementList(jstEventSettlement);
        ExcelUtil<JstEventSettlement> util = new ExcelUtil<JstEventSettlement>(JstEventSettlement.class);
        util.exportExcel(response, list, "赛事方结算单数据");
    }

    /**
     * 获取赛事方结算单详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_settlement:query')")
    @GetMapping(value = "/{eventSettlementId}")
    public AjaxResult getInfo(@PathVariable("eventSettlementId") Long eventSettlementId)
    {
        return success(jstEventSettlementService.selectJstEventSettlementByEventSettlementId(eventSettlementId));
    }

    /**
     * 新增赛事方结算单
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_settlement:add')")
    @Log(title = "赛事方结算单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstEventSettlement jstEventSettlement)
    {
        return toAjax(jstEventSettlementService.insertJstEventSettlement(jstEventSettlement));
    }

    /**
     * 修改赛事方结算单
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_settlement:edit')")
    @Log(title = "赛事方结算单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstEventSettlement jstEventSettlement)
    {
        return toAjax(jstEventSettlementService.updateJstEventSettlement(jstEventSettlement));
    }

    /**
     * 删除赛事方结算单
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_settlement:remove')")
    @Log(title = "赛事方结算单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{eventSettlementIds}")
    public AjaxResult remove(@PathVariable Long[] eventSettlementIds)
    {
        return toAjax(jstEventSettlementService.deleteJstEventSettlementByEventSettlementIds(eventSettlementIds));
    }
}
