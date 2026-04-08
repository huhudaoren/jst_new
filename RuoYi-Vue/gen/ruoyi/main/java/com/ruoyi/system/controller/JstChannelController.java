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
import com.ruoyi.system.domain.JstChannel;
import com.ruoyi.system.service.IJstChannelService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 渠道方档案Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_channel")
public class JstChannelController extends BaseController
{
    @Autowired
    private IJstChannelService jstChannelService;

    /**
     * 查询渠道方档案列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_channel:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstChannel jstChannel)
    {
        startPage();
        List<JstChannel> list = jstChannelService.selectJstChannelList(jstChannel);
        return getDataTable(list);
    }

    /**
     * 导出渠道方档案列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_channel:export')")
    @Log(title = "渠道方档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstChannel jstChannel)
    {
        List<JstChannel> list = jstChannelService.selectJstChannelList(jstChannel);
        ExcelUtil<JstChannel> util = new ExcelUtil<JstChannel>(JstChannel.class);
        util.exportExcel(response, list, "渠道方档案数据");
    }

    /**
     * 获取渠道方档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_channel:query')")
    @GetMapping(value = "/{channelId}")
    public AjaxResult getInfo(@PathVariable("channelId") Long channelId)
    {
        return success(jstChannelService.selectJstChannelByChannelId(channelId));
    }

    /**
     * 新增渠道方档案
     */
    @PreAuthorize("@ss.hasPermi('system:jst_channel:add')")
    @Log(title = "渠道方档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstChannel jstChannel)
    {
        return toAjax(jstChannelService.insertJstChannel(jstChannel));
    }

    /**
     * 修改渠道方档案
     */
    @PreAuthorize("@ss.hasPermi('system:jst_channel:edit')")
    @Log(title = "渠道方档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstChannel jstChannel)
    {
        return toAjax(jstChannelService.updateJstChannel(jstChannel));
    }

    /**
     * 删除渠道方档案
     */
    @PreAuthorize("@ss.hasPermi('system:jst_channel:remove')")
    @Log(title = "渠道方档案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{channelIds}")
    public AjaxResult remove(@PathVariable Long[] channelIds)
    {
        return toAjax(jstChannelService.deleteJstChannelByChannelIds(channelIds));
    }
}
