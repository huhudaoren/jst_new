package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.dto.TaskCreateReqDTO;
import com.ruoyi.jst.channel.service.SalesFollowupTaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 销售主管任务派发管理。
 * <p>
 * 路径前缀：/sales/manager/tasks
 * <p>
 * 仅销售主管（jst_sales_manager）和 admin 可访问。
 * 权限点：sales:manager:tasks:{list,assign}
 */
@RestController
@RequestMapping("/sales/manager/tasks")
@PreAuthorize("@ss.hasAnyRoles('jst_sales_manager,admin')")
public class SalesManagerTaskController extends BaseController {

    @Autowired
    private SalesFollowupTaskService taskService;

    /**
     * 派发任务给指定销售。
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:manager:tasks:assign')")
    @Log(title = "派发跟进任务", businessType = BusinessType.INSERT)
    public AjaxResult create(@Valid @RequestBody TaskCreateReqDTO req) {
        Long assignerUserId = SecurityUtils.getUserId();
        return AjaxResult.success(taskService.create(req, assignerUserId));
    }

    /**
     * 查我派出的任务。
     *
     * @param status 状态过滤，可为空查全部
     */
    @GetMapping("/assigned-by-me")
    @PreAuthorize("@ss.hasPermi('sales:manager:tasks:list')")
    public TableDataInfo listAssignedByMe(@RequestParam(required = false) String status) {
        Long userId = SecurityUtils.getUserId();
        startPage();
        return getDataTable(taskService.listAssignedByMe(userId, status));
    }
}
