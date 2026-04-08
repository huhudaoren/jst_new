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
import com.ruoyi.system.domain.JstNotice;
import com.ruoyi.system.service.IJstNoticeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 公告Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_notice")
public class JstNoticeController extends BaseController
{
    @Autowired
    private IJstNoticeService jstNoticeService;

    /**
     * 查询公告列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_notice:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstNotice jstNotice)
    {
        startPage();
        List<JstNotice> list = jstNoticeService.selectJstNoticeList(jstNotice);
        return getDataTable(list);
    }

    /**
     * 导出公告列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_notice:export')")
    @Log(title = "公告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstNotice jstNotice)
    {
        List<JstNotice> list = jstNoticeService.selectJstNoticeList(jstNotice);
        ExcelUtil<JstNotice> util = new ExcelUtil<JstNotice>(JstNotice.class);
        util.exportExcel(response, list, "公告数据");
    }

    /**
     * 获取公告详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_notice:query')")
    @GetMapping(value = "/{noticeId}")
    public AjaxResult getInfo(@PathVariable("noticeId") Long noticeId)
    {
        return success(jstNoticeService.selectJstNoticeByNoticeId(noticeId));
    }

    /**
     * 新增公告
     */
    @PreAuthorize("@ss.hasPermi('system:jst_notice:add')")
    @Log(title = "公告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstNotice jstNotice)
    {
        return toAjax(jstNoticeService.insertJstNotice(jstNotice));
    }

    /**
     * 修改公告
     */
    @PreAuthorize("@ss.hasPermi('system:jst_notice:edit')")
    @Log(title = "公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstNotice jstNotice)
    {
        return toAjax(jstNoticeService.updateJstNotice(jstNotice));
    }

    /**
     * 删除公告
     */
    @PreAuthorize("@ss.hasPermi('system:jst_notice:remove')")
    @Log(title = "公告", businessType = BusinessType.DELETE)
	@DeleteMapping("/{noticeIds}")
    public AjaxResult remove(@PathVariable Long[] noticeIds)
    {
        return toAjax(jstNoticeService.deleteJstNoticeByNoticeIds(noticeIds));
    }
}
