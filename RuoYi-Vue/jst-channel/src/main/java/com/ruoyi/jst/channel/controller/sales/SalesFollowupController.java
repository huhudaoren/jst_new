package com.ruoyi.jst.channel.controller.sales;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.channel.dto.FollowupCreateReqDTO;
import com.ruoyi.jst.channel.dto.FollowupQueryReqDTO;
import com.ruoyi.jst.channel.dto.FollowupUpdateReqDTO;
import com.ruoyi.jst.channel.service.SalesFollowupService;
import com.ruoyi.jst.common.controller.BaseSalesController;
import com.ruoyi.jst.common.util.SalesScopeUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 销售跟进记录管理（销售本人视角）。
 * <p>
 * 路径前缀：/sales/me/followup
 * <p>
 * 权限点：sales:me:followup:{list,add,edit,remove}
 */
@RestController
@RequestMapping("/sales/me/followup")
public class SalesFollowupController extends BaseSalesController {

    @Autowired
    private SalesFollowupService followupService;

    /**
     * 销售本人跟进记录列表（支持过滤：channelId / followupType / dateFrom / dateTo）。
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:list')")
    public TableDataInfo list(FollowupQueryReqDTO query) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        startPage();
        return getDataTable(followupService.listMine(salesId, query));
    }

    /**
     * 按渠道查跟进记录（admin / 主管 / 销售本人均可调）。
     */
    @GetMapping("/by-channel/{channelId}")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:list')")
    public AjaxResult listByChannel(@PathVariable Long channelId) {
        return AjaxResult.success(followupService.listByChannel(channelId));
    }

    /**
     * 新建跟进记录。
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('sales:me:followup:add')")
    @Log(title = "新建跟进记录", businessType = BusinessType.INSERT)
    public AjaxResult create(@Valid @RequestBody FollowupCreateReqDTO req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        return AjaxResult.success(followupService.create(salesId, req));
    }

    /**
     * 更新跟进记录（24h 内可改）。
     */
    @PutMapping("/{recordId}")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:edit')")
    @Log(title = "更新跟进记录", businessType = BusinessType.UPDATE)
    public AjaxResult update(@PathVariable Long recordId, @RequestBody FollowupUpdateReqDTO req) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        followupService.update(recordId, salesId, req);
        return AjaxResult.success();
    }

    /**
     * 删除跟进记录（24h 内可删）。
     */
    @DeleteMapping("/{recordId}")
    @PreAuthorize("@ss.hasPermi('sales:me:followup:remove')")
    @Log(title = "删除跟进记录", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long recordId) {
        Long salesId = SalesScopeUtils.currentSalesIdRequired();
        followupService.remove(recordId, salesId);
        return AjaxResult.success();
    }
}
