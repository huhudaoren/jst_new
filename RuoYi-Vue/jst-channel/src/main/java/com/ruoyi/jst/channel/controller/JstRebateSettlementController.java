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
import com.ruoyi.jst.channel.domain.JstRebateSettlement;
import com.ruoyi.jst.channel.service.IJstRebateSettlementService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 渠道提现/结算单Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/channel/jst_rebate_settlement")
public class JstRebateSettlementController extends BaseController
{
    @Autowired
    private IJstRebateSettlementService jstRebateSettlementService;

    /**
     * 查询渠道提现/结算单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_settlement:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstRebateSettlement jstRebateSettlement)
    {
        startPage();
        List<JstRebateSettlement> list = jstRebateSettlementService.selectJstRebateSettlementList(jstRebateSettlement);
        return getDataTable(list);
    }

    /**
     * 导出渠道提现/结算单列表
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_settlement:export')")
    @Log(title = "渠道提现/结算单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstRebateSettlement jstRebateSettlement)
    {
        List<JstRebateSettlement> list = jstRebateSettlementService.selectJstRebateSettlementList(jstRebateSettlement);
        ExcelUtil<JstRebateSettlement> util = new ExcelUtil<JstRebateSettlement>(JstRebateSettlement.class);
        util.exportExcel(response, list, "渠道提现/结算单数据");
    }

    /**
     * 获取渠道提现/结算单详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_settlement:query')")
    @GetMapping(value = "/{settlementId}")
    public AjaxResult getInfo(@PathVariable("settlementId") Long settlementId)
    {
        return success(jstRebateSettlementService.selectJstRebateSettlementBySettlementId(settlementId));
    }

    /**
     * 新增渠道提现/结算单
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_settlement:add')")
    @Log(title = "渠道提现/结算单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstRebateSettlement jstRebateSettlement)
    {
        return toAjax(jstRebateSettlementService.insertJstRebateSettlement(jstRebateSettlement));
    }

    /**
     * 修改渠道提现/结算单
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_settlement:edit')")
    @Log(title = "渠道提现/结算单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstRebateSettlement jstRebateSettlement)
    {
        return toAjax(jstRebateSettlementService.updateJstRebateSettlement(jstRebateSettlement));
    }

    /**
     * 删除渠道提现/结算单
     */
    @PreAuthorize("@ss.hasPermi('jst:channel:rebate_settlement:remove')")
    @Log(title = "渠道提现/结算单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{settlementIds}")
    public AjaxResult remove(@PathVariable Long[] settlementIds)
    {
        return toAjax(jstRebateSettlementService.deleteJstRebateSettlementBySettlementIds(settlementIds));
    }
}
