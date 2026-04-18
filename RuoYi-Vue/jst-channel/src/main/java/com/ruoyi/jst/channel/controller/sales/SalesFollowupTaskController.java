package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.channel.service.SalesFollowupTaskService;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 销售跟进任务管理（销售本人视角）。
 * <p>
 * 路径前缀：/sales/me/tasks
 * <p>
 * 权限点：sales:me:tasks:{list,complete}
 */
@RestController
@RequestMapping("/sales/me/tasks")
public class SalesFollowupTaskController extends BaseSalesController {

    @Autowired
    private SalesFollowupTaskService taskService;

    /**
     * 我的任务列表。
     *
     * @param status 状态过滤（pending / in_progress / completed / overdue），可为空查全部
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sales:me:tasks:list')")
    public TableDataInfo list(@RequestParam(required = false) String status) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        return getDataTable(taskService.listMine(salesId, status));
    }

    /**
     * 完成任务。
     *
     * @param taskId         任务 ID
     * @param linkedRecordId 关联的跟进记录 ID（可为 null）
     */
    @PostMapping("/{taskId}/complete")
    @PreAuthorize("@ss.hasPermi('sales:me:tasks:complete')")
    @Log(title = "完成跟进任务", businessType = BusinessType.UPDATE)
    public AjaxResult complete(@PathVariable Long taskId,
                               @RequestParam(required = false) Long linkedRecordId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        taskService.complete(taskId, salesId, linkedRecordId);
        return AjaxResult.success();
    }
}
