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
import com.ruoyi.system.domain.JstAuditLog;
import com.ruoyi.system.service.IJstAuditLogService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 操作审计日志Controller
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@RestController
@RequestMapping("/system/jst_audit_log")
public class JstAuditLogController extends BaseController
{
    @Autowired
    private IJstAuditLogService jstAuditLogService;

    /**
     * 查询操作审计日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_audit_log:list')")
    @GetMapping("/list")
    public TableDataInfo list(JstAuditLog jstAuditLog)
    {
        startPage();
        List<JstAuditLog> list = jstAuditLogService.selectJstAuditLogList(jstAuditLog);
        return getDataTable(list);
    }

    /**
     * 导出操作审计日志列表
     */
    @PreAuthorize("@ss.hasPermi('system:jst_audit_log:export')")
    @Log(title = "操作审计日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JstAuditLog jstAuditLog)
    {
        List<JstAuditLog> list = jstAuditLogService.selectJstAuditLogList(jstAuditLog);
        ExcelUtil<JstAuditLog> util = new ExcelUtil<JstAuditLog>(JstAuditLog.class);
        util.exportExcel(response, list, "操作审计日志数据");
    }

    /**
     * 获取操作审计日志详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:jst_audit_log:query')")
    @GetMapping(value = "/{auditId}")
    public AjaxResult getInfo(@PathVariable("auditId") Long auditId)
    {
        return success(jstAuditLogService.selectJstAuditLogByAuditId(auditId));
    }

    /**
     * 新增操作审计日志
     */
    @PreAuthorize("@ss.hasPermi('system:jst_audit_log:add')")
    @Log(title = "操作审计日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody JstAuditLog jstAuditLog)
    {
        return toAjax(jstAuditLogService.insertJstAuditLog(jstAuditLog));
    }

    /**
     * 修改操作审计日志
     */
    @PreAuthorize("@ss.hasPermi('system:jst_audit_log:edit')")
    @Log(title = "操作审计日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody JstAuditLog jstAuditLog)
    {
        return toAjax(jstAuditLogService.updateJstAuditLog(jstAuditLog));
    }

    /**
     * 删除操作审计日志
     */
    @PreAuthorize("@ss.hasPermi('system:jst_audit_log:remove')")
    @Log(title = "操作审计日志", businessType = BusinessType.DELETE)
	@DeleteMapping("/{auditIds}")
    public AjaxResult remove(@PathVariable Long[] auditIds)
    {
        return toAjax(jstAuditLogService.deleteJstAuditLogByAuditIds(auditIds));
    }
}
