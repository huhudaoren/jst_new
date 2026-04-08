package com.ruoyi.jst.user.controller.wx;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.user.auth.WxAuthService;
import com.ruoyi.jst.user.auth.dto.BindPhoneReqDTO;
import com.ruoyi.jst.user.auth.dto.WxLoginReqDTO;
import com.ruoyi.jst.user.auth.vo.WxLoginResVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信小程序认证 Controller
 * <p>
 * 路径前缀:/jst/wx/auth
 * 关联文档:架构设计/27-用户端API契约.md §3.1
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/jst/wx/auth")
public class WxAuthController extends BaseController {

    @Autowired
    private WxAuthService wxAuthService;

    /**
     * 小程序登录(完全公开,无需 token)
     * Mock 模式 code 形如 MOCK_1001
     * @Anonymous 注解会被若依 PermitAllUrlProperties 自动加入白名单
     */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@Valid @RequestBody WxLoginReqDTO req) {
        WxLoginResVO vo = wxAuthService.login(req);
        return AjaxResult.success(vo);
    }

    /**
     * 绑定手机号(getPhoneNumber 授权后调)
     */
    @PostMapping("/bind-phone")
    public AjaxResult bindPhone(@Valid @RequestBody BindPhoneReqDTO req) {
        Long userId = SecurityUtils.getUserId();
        String mobile = wxAuthService.bindPhone(userId, req);
        return AjaxResult.success(mobile);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public AjaxResult logout() {
        // 若依 TokenService 自动通过 SecurityContext 清理,此处由前端清 storage 即可
        return AjaxResult.success();
    }
}
