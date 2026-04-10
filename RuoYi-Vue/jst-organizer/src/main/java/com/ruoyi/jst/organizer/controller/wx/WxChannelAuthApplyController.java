package com.ruoyi.jst.organizer.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyReqDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthResubmitReqDTO;
import com.ruoyi.jst.organizer.service.ChannelAuthApplyService;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplyVO;
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
 * 小程序-渠道认证申请 Controller
 * <p>
 * 路径前缀：/jst/wx/channel/auth
 * 关联文档：架构设计/27-用户端API契约.md §3.10
 * 关联状态机：SM-3
 * 关联决策：Q-02 驳回 ≤3 次后锁定
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/channel/auth")
public class WxChannelAuthApplyController extends BaseController {

    @Autowired
    private ChannelAuthApplyService channelAuthApplyService;

    /**
     * 查询当前用户的认证申请记录（最新一条）
     * <p>
     * 返回含 rejectCount / lockedForManual / reject_reason
     *
     * @return 最新认证申请 VO
     * @关联表 jst_channel_auth_apply
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult myLatest() {
        Long userId = SecurityUtils.getUserId();
        ChannelAuthApplyVO latest = channelAuthApplyService.getMyLatest(userId);
        return AjaxResult.success(latest);
    }

    /**
     * 提交渠道认证申请
     * <p>
     * 含 channelType 差异校验：teacher 必填 teacher_cert_url；
     * organization 必填 business_license_no + business_license_url；
     * individual 必填 id_card_front_url（通过 materialsJson 内校验）
     *
     * @param req 申请入参
     * @return 申请单号与申请ID
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/apply")
    public AjaxResult submitApply(@Valid @RequestBody ChannelAuthApplyReqDTO req) {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelAuthApplyService.submitApply(userId, req));
    }

    /**
     * 查询当前用户申请历史
     *
     * @return 申请历史列表
     * @关联表 jst_channel_auth_apply
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/apply/my")
    public AjaxResult myApplies() {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelAuthApplyService.listMyApplies(userId));
    }

    /**
     * 驳回后重提申请
     * <p>
     * Q-02: 服务端检查 rejectCount < 3，否则返回 JST_CHANNEL_AUTH_LOCKED
     *
     * @param id  原被驳回的申请ID
     * @param req 重提入参
     * @return 新提交结果
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3 rejected → pending
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/resubmit/{id}")
    public AjaxResult resubmit(@PathVariable("id") Long id,
                               @Valid @RequestBody ChannelAuthResubmitReqDTO req) {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelAuthApplyService.resubmit(userId, id, req));
    }

    /**
     * 撤回 pending 状态的申请
     *
     * @param id 申请ID
     * @return 操作结果
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3
     * @关联权限 hasRole('jst_student')
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping("/cancel/{id}")
    public AjaxResult cancelApply(@PathVariable("id") Long id) {
        Long userId = SecurityUtils.getUserId();
        channelAuthApplyService.cancelApply(userId, id);
        return AjaxResult.success();
    }
}
