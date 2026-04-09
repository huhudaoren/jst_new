package com.ruoyi.jst.channel.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.jst.channel.dto.RebateLedgerQueryReqDTO;
import com.ruoyi.jst.channel.dto.WithdrawApplyDTO;
import com.ruoyi.jst.channel.dto.WithdrawQueryReqDTO;
import com.ruoyi.jst.channel.service.ChannelWithdrawService;
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
 * Channel withdrawal wx endpoints.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/channel")
public class WxChannelWithdrawController extends BaseController {

    @Autowired
    private ChannelWithdrawService channelWithdrawService;

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:my')")
    @GetMapping("/rebate/summary")
    public AjaxResult summary() {
        return AjaxResult.success(channelWithdrawService.getSummary());
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:my')")
    @GetMapping("/rebate/ledger/list")
    public TableDataInfo ledgerList(@Valid RebateLedgerQueryReqDTO query) {
        // startPage 由 service 在 lookup 完成后调用，避免 PageHelper threadlocal 吃掉 lookup 查询
        return getDataTable(channelWithdrawService.selectLedgerList(query));
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:apply')")
    @PostMapping("/withdraw/apply")
    public AjaxResult apply(@Valid @RequestBody WithdrawApplyDTO req) {
        return AjaxResult.success(channelWithdrawService.apply(req));
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:apply')")
    @PostMapping("/withdraw/{settlementId}/cancel")
    public AjaxResult cancel(@PathVariable("settlementId") Long settlementId) {
        channelWithdrawService.cancel(settlementId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:my')")
    @GetMapping("/withdraw/list")
    public TableDataInfo myWithdrawList(@Valid WithdrawQueryReqDTO query) {
        return getDataTable(channelWithdrawService.selectMyWithdrawList(query));
    }

    @PreAuthorize("@ss.hasRole('jst_channel') or @ss.hasPermi('jst:channel:withdraw:my')")
    @GetMapping("/withdraw/{settlementId}")
    public AjaxResult detail(@PathVariable("settlementId") Long settlementId) {
        return AjaxResult.success(channelWithdrawService.getWithdrawDetail(settlementId));
    }
}
