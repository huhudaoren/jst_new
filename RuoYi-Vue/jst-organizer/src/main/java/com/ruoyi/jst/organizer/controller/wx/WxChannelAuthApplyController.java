package com.ruoyi.jst.organizer.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyReqDTO;
import com.ruoyi.jst.organizer.service.ChannelAuthApplyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序-渠道认证申请 Controller
 * <p>
 * 路径前缀：/jst/wx/channel/auth/apply
 * 关联文档：架构设计/27-用户端API契约.md §3.10
 *
 * @author jst
 * @since 1.0.0
 */
@Validated
@RestController
@RequestMapping("/jst/wx/channel/auth/apply")
public class WxChannelAuthApplyController extends BaseController {

    @Autowired
    private ChannelAuthApplyService channelAuthApplyService;

    /**
     * 当前用户提交渠道认证申请
     *
     * @param req 申请入参
     * @return 申请单号与申请ID
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @PostMapping
    public AjaxResult submitApply(@Valid @RequestBody ChannelAuthApplyReqDTO req) {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelAuthApplyService.submitApply(userId, req));
    }

    /**
     * 查询当前用户申请历史
     *
     * @return 申请历史列表
     */
    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult myApplies() {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(channelAuthApplyService.listMyApplies(userId));
    }
}
