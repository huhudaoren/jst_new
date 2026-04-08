package com.ruoyi.jst.organizer.controller;

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
import com.ruoyi.jst.organizer.domain.JstChannelAuthApply;
import com.ruoyi.jst.organizer.service.IJstChannelAuthApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 渠道认证申请Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/jst/organizer/jst_channel_auth_apply")
public class JstChannelAuthApplyController extends BaseController
{
    @Autowired
    private IJstChannelAuthApplyService jstChannelAuthApplyService;

    /**
     * 查询渠道认证申请列表
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channel_auth_apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstChannelAuthApply jstChannelAuthApply)
    {
        startPage();
        List<JstChannelAuthApply> list = jstChannelAuthApplyService.selectJstChannelAuthApplyList(jstChannelAuthApply);
        return getDataTable(list);
    }

    /**
     * 导出渠道认证申请列表
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channel_auth_apply:export')")
    @Log(title = "渠道认证申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstChannelAuthApply jstChannelAuthApply)
    {
        List<JstChannelAuthApply> list = jstChannelAuthApplyService.selectJstChannelAuthApplyList(jstChannelAuthApply);
        ExcelUtil<JstChannelAuthApply> util = new ExcelUtil<JstChannelAuthApply>(JstChannelAuthApply.class);
        util.exportExcel(response, list, "渠道认证申请数据");
    }

    /**
     * 获取渠道认证申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channel_auth_apply:query')")
    @GetMapping(value = "/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId)
    {
        return success(jstChannelAuthApplyService.selectJstChannelAuthApplyByApplyId(applyId));
    }

    /**
     * 新增渠道认证申请
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channel_auth_apply:add')")
    @Log(title = "渠道认证申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstChannelAuthApply jstChannelAuthApply)
    {
        return toAjax(jstChannelAuthApplyService.insertJstChannelAuthApply(jstChannelAuthApply));
    }

    /**
     * 修改渠道认证申请
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channel_auth_apply:edit')")
    @Log(title = "渠道认证申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstChannelAuthApply jstChannelAuthApply)
    {
        return toAjax(jstChannelAuthApplyService.updateJstChannelAuthApply(jstChannelAuthApply));
    }

    /**
     * 删除渠道认证申请
     */
    @PreAuthorize("@ss.hasPermi('jst:organizer:channel_auth_apply:remove')")
    @Log(title = "渠道认证申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{applyIds}")
    public AjaxResult remove(@PathVariable Long[] applyIds)
    {
        return toAjax(jstChannelAuthApplyService.deleteJstChannelAuthApplyByApplyIds(applyIds));
    }
}
