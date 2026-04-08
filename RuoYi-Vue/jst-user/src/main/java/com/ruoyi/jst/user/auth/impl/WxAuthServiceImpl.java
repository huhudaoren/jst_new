package com.ruoyi.jst.user.auth.impl;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.user.auth.WxAuthService;
import com.ruoyi.jst.user.auth.dto.BindPhoneReqDTO;
import com.ruoyi.jst.user.auth.dto.WxLoginReqDTO;
import com.ruoyi.jst.user.auth.vo.WxLoginResVO;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.mapper.JstUserMapper;
import com.ruoyi.jst.user.mapper.RoleLookupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 微信小程序认证服务实现
 * <p>
 * 支持 mock / 微信真实环境两种登录模式，统一复用若依 token 体系。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class WxAuthServiceImpl implements WxAuthService {

    private static final Logger log = LoggerFactory.getLogger(WxAuthServiceImpl.class);

    private static final String WX_CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    private static final String ROLE_KEY_STUDENT = "jst_student";

    @Value("${jst.wxauth.mock:true}")
    private boolean mockMode;

    @Value("${jst.wxauth.appid:}")
    private String appid;

    @Value("${jst.wxauth.secret:}")
    private String secret;

    @Autowired
    private JstUserMapper jstUserMapper;

    @Autowired
    private RoleLookupMapper roleLookupMapper;

    @Autowired
    private TokenService tokenService;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 微信小程序登录
     *
     * @param req 登录请求
     * @return 登录结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxLoginResVO login(WxLoginReqDTO req) {
        String code = req.getCode();
        String openid;
        String unionid = null;

        if (mockMode) {
            if (code == null || !code.startsWith("MOCK_")) {
                throw new ServiceException("Mock 模式下 code 必须以 MOCK_ 开头",
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            String userIdStr = code.substring("MOCK_".length());
            openid = "openid_test_" + userIdStr;
            log.info("[WxAuth] Mock 登录 code={} -> openid={}", code, openid);
        } else {
            if (appid.isEmpty() || secret.isEmpty()) {
                throw new ServiceException("未配置微信 appid/secret",
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            String url = String.format(WX_CODE2SESSION_URL, appid, secret, code);
            try {
                String resp = restTemplate.getForObject(url, String.class);
                JSONObject json = JSONObject.parseObject(resp);
                if (json.containsKey("errcode") && json.getIntValue("errcode") != 0) {
                    log.warn("[WxAuth] 微信 code2session 失败 resp={}", resp);
                    throw new ServiceException("微信登录失败: " + json.getString("errmsg"));
                }
                openid = json.getString("openid");
                unionid = json.getString("unionid");
            } catch (ServiceException ex) {
                throw ex;
            } catch (Exception e) {
                log.error("[WxAuth] 调用微信接口异常", e);
                throw new ServiceException("微信登录服务暂不可用");
            }
        }

        JstUser query = new JstUser();
        query.setOpenid(openid);
        List<JstUser> existing = jstUserMapper.selectJstUserList(query);

        JstUser user;
        boolean isNewUser = false;
        Date now = new Date();
        if (existing == null || existing.isEmpty()) {
            user = new JstUser();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setUserType("student");
            user.setStatus(1);
            user.setRiskFlag(0);
            user.setRegisterTime(now);
            user.setLastLoginTime(now);
            user.setNickname("用户" + IdUtils.fastSimpleUUID().substring(0, 6));
            user.setCreateBy("wxauth");
            user.setCreateTime(now);
            jstUserMapper.insertJstUser(user);
            isNewUser = true;
            log.info("[WxAuth] 新用户注册 userId={} openid={}", user.getUserId(), openid);
        } else {
            user = existing.get(0);
            if (user.getStatus() != null && user.getStatus() == 0) {
                throw new ServiceException(BizErrorCode.JST_USER_DISABLED.message(),
                        BizErrorCode.JST_USER_DISABLED.code());
            }
            user.setLastLoginTime(now);
            user.setUpdateBy(String.valueOf(user.getUserId()));
            user.setUpdateTime(now);
            jstUserMapper.updateJstUser(user);
        }

        List<SysRole> roles = buildRoles(user);

        SysUser sysUser = new SysUser();
        sysUser.setUserId(user.getUserId());
        sysUser.setUserName("wx_" + user.getUserId());
        sysUser.setNickName(user.getNickname());
        sysUser.setRoles(roles);

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUser(sysUser);
        loginUser.setPermissions(new HashSet<>());

        String token = tokenService.createToken(loginUser);

        WxLoginResVO vo = new WxLoginResVO();
        vo.setToken(token);
        vo.setIsNewUser(isNewUser);
        vo.setRoles(buildRoleKeys(roles));

        WxLoginResVO.UserBriefVO brief = new WxLoginResVO.UserBriefVO();
        brief.setUserId(user.getUserId());
        brief.setNickname(user.getNickname());
        brief.setAvatar(user.getAvatar());
        brief.setMobileMasked(MaskUtil.mobile(user.getMobile()));
        brief.setUserType(user.getUserType());
        brief.setPhoneBound(user.getMobile() != null && !user.getMobile().isEmpty());
        vo.setUserInfo(brief);

        return vo;
    }

    /**
     * 绑定手机号
     *
     * @param userId 当前用户 ID
     * @param req    绑定请求
     * @return 手机号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String bindPhone(Long userId, BindPhoneReqDTO req) {
        if (userId == null) {
            throw new ServiceException("userId 不能为空");
        }

        String mobile;
        if (mockMode) {
            mobile = req.getEncryptedData();
            if (mobile == null || mobile.length() != 11) {
                throw new ServiceException("Mock 模式下 encryptedData 应为 11 位明文手机号");
            }
        } else {
            throw new ServiceException("真实微信解密尚未实现");
        }

        JstUser user = jstUserMapper.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }

        user.setMobile(mobile);
        user.setUpdateBy(String.valueOf(userId));
        user.setUpdateTime(new Date());
        jstUserMapper.updateJstUser(user);
        log.info("[WxAuth] 绑定手机 userId={} mobile={}", userId, MaskUtil.mobile(mobile));
        return mobile;
    }

    private List<SysRole> buildRoles(JstUser user) {
        List<SysRole> roles = new ArrayList<>();
        roles.add(buildRole(ROLE_KEY_STUDENT));
        List<String> dbRoleKeys = roleLookupMapper.selectRoleKeysByUserId(user.getUserId());
        if (dbRoleKeys == null) {
            return roles;
        }
        for (String roleKey : dbRoleKeys) {
            if (roleKey != null && !ROLE_KEY_STUDENT.equals(roleKey)) {
                roles.add(buildRole(roleKey));
            }
        }
        return roles;
    }

    private List<String> buildRoleKeys(List<SysRole> roles) {
        List<String> roleKeys = new ArrayList<>();
        for (SysRole role : roles) {
            if (role != null && role.getRoleKey() != null) {
                roleKeys.add(role.getRoleKey());
            }
        }
        return roleKeys;
    }

    private SysRole buildRole(String roleKey) {
        SysRole role = new SysRole();
        role.setRoleKey(roleKey);
        return role;
    }
}
