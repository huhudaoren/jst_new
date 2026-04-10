package com.ruoyi.jst.message.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.message.dto.WxMessageQueryDTO;
import com.ruoyi.jst.message.service.WxMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mini-program message center controller.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/message")
public class WxMessageController extends BaseController {

    @Autowired
    private WxMessageService wxMessageService;

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @GetMapping("/my")
    public AjaxResult my(WxMessageQueryDTO query) {
        return AjaxResult.success(wxMessageService.selectMyMessages(SecurityUtils.getUserId(), query));
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @PostMapping("/{id}/read")
    public AjaxResult read(@PathVariable("id") Long messageId) {
        wxMessageService.markMessageRead(SecurityUtils.getUserId(), messageId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasRole('jst_student') or @ss.hasRole('jst_channel')")
    @PostMapping("/read-all")
    public AjaxResult readAll() {
        wxMessageService.markAllRead(SecurityUtils.getUserId());
        return AjaxResult.success();
    }
}
