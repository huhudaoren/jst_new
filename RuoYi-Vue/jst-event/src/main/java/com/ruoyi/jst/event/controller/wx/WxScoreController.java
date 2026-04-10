package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.event.dto.WxScoreQueryDTO;
import com.ruoyi.jst.event.service.WxScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mini-program score controller.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/score")
public class WxScoreController extends BaseController {

    @Autowired
    private WxScoreService wxScoreService;

    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult my(WxScoreQueryDTO query) {
        return AjaxResult.success(wxScoreService.selectMyScores(SecurityUtils.getUserId(), query));
    }
}
