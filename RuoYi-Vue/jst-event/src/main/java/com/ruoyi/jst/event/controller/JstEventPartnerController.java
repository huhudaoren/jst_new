package com.ruoyi.jst.event.controller;

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
import com.ruoyi.jst.event.domain.JstEventPartner;
import com.ruoyi.jst.event.service.IJstEventPartnerService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 赛事方档案Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/event/jst_event_partner")
public class JstEventPartnerController extends BaseController
{
    @Autowired
    private IJstEventPartnerService jstEventPartnerService;

    /**
     * 查询赛事方档案列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:event_partner:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstEventPartner jstEventPartner)
    {
        startPage();
        List<JstEventPartner> list = jstEventPartnerService.selectJstEventPartnerList(jstEventPartner);
        return getDataTable(list);
    }

    /**
     * 导出赛事方档案列表
     */
    @PreAuthorize("@ss.hasPermi('jst:event:event_partner:export')")
    @Log(title = "赛事方档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstEventPartner jstEventPartner)
    {
        List<JstEventPartner> list = jstEventPartnerService.selectJstEventPartnerList(jstEventPartner);
        ExcelUtil<JstEventPartner> util = new ExcelUtil<JstEventPartner>(JstEventPartner.class);
        util.exportExcel(response, list, "赛事方档案数据");
    }

    /**
     * 获取赛事方档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:event:event_partner:query')")
    @GetMapping(value = "/{partnerId}")
    public AjaxResult getInfo(@PathVariable("partnerId") Long partnerId)
    {
        return success(jstEventPartnerService.selectJstEventPartnerByPartnerId(partnerId));
    }

    /**
     * 新增赛事方档案
     */
    @PreAuthorize("@ss.hasPermi('jst:event:event_partner:add')")
    @Log(title = "赛事方档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstEventPartner jstEventPartner)
    {
        return toAjax(jstEventPartnerService.insertJstEventPartner(jstEventPartner));
    }

    /**
     * 修改赛事方档案
     */
    @PreAuthorize("@ss.hasPermi('jst:event:event_partner:edit')")
    @Log(title = "赛事方档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstEventPartner jstEventPartner)
    {
        return toAjax(jstEventPartnerService.updateJstEventPartner(jstEventPartner));
    }

    /**
     * 删除赛事方档案
     */
    @PreAuthorize("@ss.hasPermi('jst:event:event_partner:remove')")
    @Log(title = "赛事方档案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{partnerIds}")
    public AjaxResult remove(@PathVariable Long[] partnerIds)
    {
        return toAjax(jstEventPartnerService.deleteJstEventPartnerByPartnerIds(partnerIds));
    }
}
