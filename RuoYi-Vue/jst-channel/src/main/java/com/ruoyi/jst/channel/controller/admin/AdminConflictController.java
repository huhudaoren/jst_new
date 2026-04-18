package com.ruoyi.jst.channel.controller.admin;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.domain.JstSalesAttributionConflict;
import com.ruoyi.jst.channel.mapper.JstSalesAttributionConflictMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * Admin 销售归属冲突管理
 * <p>
 * 路径前缀：/admin/sales/conflict
 * 角色：admin / jst_operator
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/admin/sales/conflict")
@PreAuthorize("@ss.hasAnyRoles('admin,jst_operator')")
public class AdminConflictController extends BaseController {

    @Autowired
    private JstSalesAttributionConflictMapper conflictMapper;

    /**
     * 冲突列表（分页，可按 status 过滤）
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String status) {
        startPage();
        return getDataTable(conflictMapper.selectByStatus(status));
    }

    /**
     * 解决冲突
     */
    @PostMapping("/{conflictId}/resolve")
    @Log(title = "销售归属冲突", businessType = BusinessType.UPDATE)
    public AjaxResult resolve(@PathVariable Long conflictId,
                              @RequestBody Map<String, String> body) {
        JstSalesAttributionConflict conflict =
                conflictMapper.selectJstSalesAttributionConflictByConflictId(conflictId);
        if (conflict == null) {
            throw new ServiceException("冲突记录不存在: " + conflictId);
        }
        conflict.setStatus("resolved");
        conflict.setResolvedBy(SecurityUtils.getUserId());
        conflict.setResolvedAt(new Date());
        conflict.setResolutionRemark(body.getOrDefault("resolutionRemark", ""));
        conflictMapper.updateJstSalesAttributionConflict(conflict);
        return AjaxResult.success("已处理");
    }
}
