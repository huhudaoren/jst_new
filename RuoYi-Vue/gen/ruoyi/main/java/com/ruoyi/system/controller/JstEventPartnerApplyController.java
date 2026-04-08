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
import com.ruoyi.system.domain.JstEventPartnerApply;
import com.ruoyi.system.service.IJstEventPartnerApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事方入驻申请Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_event_partner_apply")
public class JstEventPartnerApplyController extends BaseController
{
    @Autowired
    private IJstEventPartnerApplyService jstEventPartnerApplyService;

    /**
     * 查询赛事方入驻申请列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_partner_apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstEventPartnerApply jstEventPartnerApply)
    {
        startPage();
        List<JstEventPartnerApply> list = jstEventPartnerApplyService.selectJstEventPartnerApplyList(jstEventPartnerApply);
        return getDataTable(list);
    }

    /**
     * 导出赛事方入驻申请列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_partner_apply:export')")
    @Log(title = "赛事方入驻申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstEventPartnerApply jstEventPartnerApply)
    {
        List<JstEventPartnerApply> list = jstEventPartnerApplyService.selectJstEventPartnerApplyList(jstEventPartnerApply);
        ExcelUtil<JstEventPartnerApply> util = new ExcelUtil<JstEventPartnerApply>(JstEventPartnerApply.class);
        util.exportExcel(response, list, "赛事方入驻申请数据");
    }

    /**
     * 获取赛事方入驻申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_partner_apply:query')")
    @GetMapping(value = "/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId)
    {
        return success(jstEventPartnerApplyService.selectJstEventPartnerApplyByApplyId(applyId));
    }

    /**
     * 新增赛事方入驻申请
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_partner_apply:add')")
    @Log(title = "赛事方入驻申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstEventPartnerApply jstEventPartnerApply)
    {
        return toAjax(jstEventPartnerApplyService.insertJstEventPartnerApply(jstEventPartnerApply));
    }

    /**
     * 修改赛事方入驻申请
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_partner_apply:edit')")
    @Log(title = "赛事方入驻申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstEventPartnerApply jstEventPartnerApply)
    {
        return toAjax(jstEventPartnerApplyService.updateJstEventPartnerApply(jstEventPartnerApply));
    }

    /**
     * 删除赛事方入驻申请
     */
    @PreAuthorize("@ss.hasPermi('system:jst_event_partner_apply:remove')")
    @Log(title = "赛事方入驻申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        return toAjax(jstEventPartnerApplyService.deleteJstEventPartnerApplyByApplyIds(applyIds));
    }
}
