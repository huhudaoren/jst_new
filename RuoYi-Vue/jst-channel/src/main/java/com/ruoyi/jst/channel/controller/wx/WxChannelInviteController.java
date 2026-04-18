package com.ruoyi.jst.channel.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.channel.mapper.lookup.ChannelDetailLookupMapper;
import com.ruoyi.jst.channel.mapper.lookup.ChannelLookupMapper;
import com.ruoyi.jst.channel.service.ChannelDistributionService;
import com.ruoyi.jst.channel.service.ChannelInviteService;
import com.ruoyi.jst.channel.service.InviteCodeService;
import com.ruoyi.jst.common.exception.BizErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序-渠道方邀请 & 分销 Controller
 * <p>
 * 路径前缀：/jst/wx/channel
 * 角色：jst_channel
 * <p>
 * 端点：
 * <ul>
 *   <li>GET /invite/code             — 我的邀请码 + qrcodeUrl</li>
 *   <li>GET /invite/list             — 我邀请的渠道列表</li>
 *   <li>GET /distribution/ledger     — 分销台账列表（分页，可按 status 过滤）</li>
 *   <li>GET /distribution/summary    — 分销收益汇总</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/jst/wx/channel")
@PreAuthorize("@ss.hasRole('jst_channel')")
public class WxChannelInviteController extends BaseController {

    @Autowired
    private ChannelLookupMapper channelLookupMapper;

    @Autowired
    private ChannelDetailLookupMapper channelDetailLookupMapper;

    @Autowired
    private InviteCodeService inviteCodeService;

    @Autowired
    private ChannelInviteService channelInviteService;

    @Autowired
    private ChannelDistributionService channelDistributionService;

    /**
     * 我的邀请码（若尚未生成则自动生成）
     */
    @GetMapping("/invite/code")
    public AjaxResult myInviteCode() {
        Long channelId = requireChannelId();
        // 先尝试获取已有邀请码
        var channelBasic = channelDetailLookupMapper.selectBasicByChannelId(channelId);
        String code;
        if (channelBasic != null && channelBasic.getInviteCode() != null
                && !channelBasic.getInviteCode().isBlank()) {
            code = channelBasic.getInviteCode();
        } else {
            // 自动生成
            code = inviteCodeService.generateForChannel(channelId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("inviteCode", code);
        // qrcodeUrl 留 null，后期接 OSS 小程序码
        result.put("qrcodeUrl", null);
        return AjaxResult.success(result);
    }

    /**
     * 我邀请的渠道列表（全量，不分页）
     */
    @GetMapping("/invite/list")
    public AjaxResult inviteList() {
        Long channelId = requireChannelId();
        return AjaxResult.success(channelInviteService.listByInviter(channelId));
    }

    /**
     * 我的分销台账列表（分页，可按 status 过滤）
     */
    @GetMapping("/distribution/ledger")
    public TableDataInfo distributionLedger(@RequestParam(required = false) String status) {
        Long channelId = requireChannelId();
        startPage();
        return getDataTable(channelDistributionService.listByInviter(channelId, status));
    }

    /**
     * 我的分销收益汇总
     *
     * @return { totalAmount, pendingAmount, accruedAmount }
     */
    @GetMapping("/distribution/summary")
    public AjaxResult distributionSummary() {
        Long channelId = requireChannelId();

        BigDecimal totalAmount = nullToZero(
                channelDistributionService.sumAmountByInviter(channelId, null));
        BigDecimal pendingAmount = nullToZero(
                channelDistributionService.sumAmountByInviter(channelId, "pending"));
        BigDecimal accruedAmount = nullToZero(
                channelDistributionService.sumAmountByInviter(channelId, "accrued"));

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalAmount", totalAmount);
        summary.put("pendingAmount", pendingAmount);
        summary.put("accruedAmount", accruedAmount);
        return AjaxResult.success(summary);
    }

    // ===== private helpers =====

    private Long requireChannelId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        Map<String, Object> channel = channelLookupMapper.selectCurrentByUserId(userId);
        if (channel == null || channel.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        Object id = channel.get("channelId");
        if (id == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_NOT_AUTHED.message(),
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        return id instanceof Number ? ((Number) id).longValue() : Long.parseLong(id.toString());
    }

    private BigDecimal nullToZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
