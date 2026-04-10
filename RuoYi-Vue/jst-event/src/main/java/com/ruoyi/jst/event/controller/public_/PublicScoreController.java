package com.ruoyi.jst.event.controller.public_;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.RateLimiter;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.LimitType;
import com.ruoyi.jst.event.dto.PublicScoreQueryDTO;
import com.ruoyi.jst.event.service.WxScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public score query controller.
 */
@Anonymous
@Validated
@RestController
@RequestMapping("/jst/public/score")
public class PublicScoreController extends BaseController {

    @Autowired
    private WxScoreService wxScoreService;

    @Anonymous
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @GetMapping("/query")
    public AjaxResult query(PublicScoreQueryDTO query) {
        return AjaxResult.success(wxScoreService.queryPublicScores(query));
    }
}
