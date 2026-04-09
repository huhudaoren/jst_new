package com.ruoyi.jst.channel.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.jst.channel.dto.WithdrawAuditDTO;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.service.ChannelWithdrawAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin withdrawal audit endpoints.
 */
@Validated
@RestController
@RequestMapping("/jst/channel")
public class ChannelWithdrawAdminController extends BaseController {

    @Autowired
    private ChannelWithdrawAdminService channelWithdrawAdminService;

    @PreAuthorize("@ss.hasPermi('jst:channel:withdraw:list')")
    @GetMapping("/withdraw/list")
    public TableDataInfo list(@Valid WithdrawQueryReqDTO query) {
        startPage();
        return getDataTable(channelWithdrawAdminService.selectAdminList(query));
    }

    @PreAuthorize("@ss.hasPermi('jst:channel:withdraw:query')")
    @GetMapping("/withdraw/{settlementId}")
    public AjaxResult detail(@PathVariable("settlementId") Long settlementId) {
        return AjaxResult.success(channelWithdrawAdminService.getAdminDetail(settlementId));
    }

    @Log(title = "渠道提现审核", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:channel:withdraw:audit')")
    @PostMapping("/withdraw/{settlementId}/approve")
    public AjaxResult approve(@PathVariable("settlementId") Long settlementId,
                              @Valid @RequestBody WithdrawAuditDTO req) {
        channelWithdrawAdminService.approve(settlementId, req);
        return AjaxResult.success();
    }

    @Log(title = "渠道提现驳回", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:channel:withdraw:audit')")
    @PostMapping("/withdraw/{settlementId}/reject")
    public AjaxResult reject(@PathVariable("settlementId") Long settlementId,
                             @Valid @RequestBody WithdrawAuditDTO req) {
        channelWithdrawAdminService.reject(settlementId, req);
        return AjaxResult.success();
    }

    @Log(title = "渠道提现打款", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('jst:channel:withdraw:execute')")
    @PostMapping("/withdraw/{settlementId}/execute")
    public AjaxResult execute(@PathVariable("settlementId") Long settlementId) {
        channelWithdrawAdminService.execute(settlementId);
        return AjaxResult.success();
    }
}
