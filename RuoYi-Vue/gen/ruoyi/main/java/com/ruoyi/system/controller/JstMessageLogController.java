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
import com.ruoyi.system.domain.JstMessageLog;
import com.ruoyi.system.service.IJstMessageLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 消息发送日志Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_message_log")
public class JstMessageLogController extends BaseController
{
    @Autowired
    private IJstMessageLogService jstMessageLogService;

    /**
     * 查询消息发送日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_log:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstMessageLog jstMessageLog)
    {
        startPage();
        List<JstMessageLog> list = jstMessageLogService.selectJstMessageLogList(jstMessageLog);
        return getDataTable(list);
    }

    /**
     * 导出消息发送日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_log:export')")
    @Log(title = "消息发送日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstMessageLog jstMessageLog)
    {
        List<JstMessageLog> list = jstMessageLogService.selectJstMessageLogList(jstMessageLog);
        ExcelUtil<JstMessageLog> util = new ExcelUtil<JstMessageLog>(JstMessageLog.class);
        util.exportExcel(response, list, "消息发送日志数据");
    }

    /**
     * 获取消息发送日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_log:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(jstMessageLogService.selectJstMessageLogByLogId(logId));
    }

    /**
     * 新增消息发送日志
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_log:add')")
    @Log(title = "消息发送日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstMessageLog jstMessageLog)
    {
        return toAjax(jstMessageLogService.insertJstMessageLog(jstMessageLog));
    }

    /**
     * 修改消息发送日志
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_log:edit')")
    @Log(title = "消息发送日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstMessageLog jstMessageLog)
    {
        return toAjax(jstMessageLogService.updateJstMessageLog(jstMessageLog));
    }

    /**
     * 删除消息发送日志
     */
    @PreAuthorize("@ss.hasPermi('system:jst_message_log:remove')")
    @Log(title = "消息发送日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(jstMessageLogService.deleteJstMessageLogByLogIds(logIds));
    }
}
