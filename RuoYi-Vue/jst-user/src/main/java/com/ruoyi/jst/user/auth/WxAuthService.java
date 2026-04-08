package com.ruoyi.jst.user.auth;

import com.ruoyi.jst.user.auth.dto.BindPhoneReqDTO;
import com.ruoyi.jst.user.auth.dto.WxLoginReqDTO;
import com.ruoyi.jst.user.auth.vo.WxLoginResVO;

/**
 * 微信小程序认证服务
 * <p>
 * 关联表：jst_user
 * 流程：wx.login code → 调微信 code2Session → openid → 查/创 jst_user → 颁发若依 JWT
 * <p>
 * Mock 模式 (jst.wxauth.mock=true):
 *   入参 code 形如 MOCK_1001 → 直接以 userId=1001 颁发 token,跳过微信调用
 *   用于本地开发联调 99-test-fixtures.sql 中的测试用户
 *
 * @author jst
 * @since 1.0.0
 */
public interface WxAuthService {

    /**
     * 小程序登录
     */
    WxLoginResVO login(WxLoginReqDTO req);

    /**
     * 绑定手机号(getPhoneNumber 后授权)
     * @return 绑定后的手机号
     */
    String bindPhone(Long userId, BindPhoneReqDTO req);
}
