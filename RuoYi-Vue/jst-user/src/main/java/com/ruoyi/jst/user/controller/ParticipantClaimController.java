package com.ruoyi.jst.user.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.jst.user.dto.ClaimAdminReqDTO;
import com.ruoyi.jst.user.service.ParticipantClaimService;
import com.ruoyi.jst.user.vo.ParticipantClaimResVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时档案-认领 Controller
 * <p>
 * 路径前缀:/jst/user/participant/claim
 * 关联状态机:SM-14
 * 关联文档:架构设计/12-API边界与服务划分.md §3.5
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/jst/user/participant/claim")
public class ParticipantClaimController extends BaseController {

    @Autowired
    private ParticipantClaimService claimService;

    /**
     * 管理员手动认领
     * 权限:jst:user:participant:claim
     */
    @PreAuthorize("@ss.hasPermi('jst:user:participant:claim')")
    @PostMapping("/admin")
    public AjaxResult claimByAdmin(@Valid @RequestBody ClaimAdminReqDTO req) {
        ParticipantClaimResVO vo = claimService.claimByAdmin(
                req.getParticipantId(), req.getUserId(), req.getReason());
        return AjaxResult.success(vo);
    }

    /**
     * 撤销认领
     * 权限:jst:user:participant:claim
     */
    @PreAuthorize("@ss.hasPermi('jst:user:participant:claim')")
    @PostMapping("/revoke")
    public AjaxResult revokeClaim(@RequestParam Long participantId,
                                   @RequestParam String reason) {
        claimService.revokeClaim(participantId, reason);
        return AjaxResult.success();
    }
}
