package com.ruoyi.jst.event.controller.public_;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.jst.event.dto.PublicCertVerifyQueryDTO;
import com.ruoyi.jst.event.service.WxCertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public certificate verify controller.
 */
@Anonymous
@Validated
@RestController
@RequestMapping("/jst/public/cert")
public class PublicCertController extends BaseController {

    @Autowired
    private WxCertService wxCertService;

    @Anonymous
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @GetMapping("/verify")
    public AjaxResult verify(PublicCertVerifyQueryDTO query) {
        return AjaxResult.success(wxCertService.verifyPublicCert(query));
    }
}
