package com.ruoyi.web.controller.jst;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.SysOperLog;
import com.ruoyi.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 销售敏感操作审计查询
 * <p>
 * 路径前缀：/admin/sales/audit
 * 角色：admin / jst_operator / jst_risk
 * <p>
 * 端点：
 * <ul>
 *   <li>GET /phone-views — 查看手机号完整号码日志（sys_oper_log.oper_url LIKE '%phone-full%'）</li>
 *   <li>GET /exports     — 查看导出操作日志（oper_url LIKE '%/export%'）</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/sales/audit")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator,jst_risk')")
public class AdminSalesAuditController extends BaseController {

    @Autowired
    private ISysOperLogService operLogService;

    /**
     * 销售查看完整手机号日志（分页）。
     * 过滤：oper_url 含 "phone-full"
     */
    @GetMapping("/phone-views")
    public TableDataInfo phoneViews() {
        startPage();
        SysOperLog query = new SysOperLog();
        query.setOperUrl("/sales/me/channels");
        // 利用 operUrl 字段在 service 层做 LIKE 过滤（若依 selectOperLogList 已支持 LIKE）
        // 实际 SQL：oper_url LIKE '%phone-full%' 通过参数传入
        query.setOperUrl("phone-full");
        return getDataTable(operLogService.selectOperLogList(query));
    }

    /**
     * 销售导出操作日志（分页）。
     * 过滤：oper_url 含 "/export"
     */
    @GetMapping("/exports")
    public TableDataInfo exports() {
        startPage();
        SysOperLog query = new SysOperLog();
        query.setOperUrl("/export");
        return getDataTable(operLogService.selectOperLogList(query));
    }
}
