package com.ruoyi.jst.event.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.event.service.WxCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mini-program certificate controller.
 */
@Validated
@RestController
@RequestMapping("/jst/wx/cert")
public class WxCertController extends BaseController {

    @Autowired
    private WxCertService wxCertService;

    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/my")
    public AjaxResult my() {
        return AjaxResult.success(wxCertService.selectMyCerts(SecurityUtils.getUserId()));
    }

    @PreAuthorize("@ss.hasRole('jst_student')")
    @GetMapping("/{certId}")
    public AjaxResult detail(@PathVariable("certId") Long certId) {
        return AjaxResult.success(wxCertService.selectCertDetail(SecurityUtils.getUserId(), certId));
    }
}
