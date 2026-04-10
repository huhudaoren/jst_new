package com.ruoyi.jst.user.controller.wx;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.service.IJstUserService;
import com.ruoyi.jst.user.service.UserProfileService;
import com.ruoyi.jst.user.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序用户资料 Controller
 * <p>
 * 路径前缀:/jst/wx/user
 * 说明：所有方法强制 user_id = 当前登录用户 (SecurityCheck.assertSameUser),
 * 不接受前端传入的 userId 参数。
 *
 * @author jst
 * @since 1.0.0
 */
@RestController
@RequestMapping("/jst/wx/user")
public class WxUserController extends BaseController {

    @Autowired
    private IJstUserService jstUserService;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * 当前用户资料 (含脱敏字段)
     */
    @GetMapping("/profile")
    public AjaxResult getProfile() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message());
        }
        JstUser user = jstUserService.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        // 输出 Map(避免直返 Entity,符合架构规范)
        UserProfileVO profile = userProfileService.getProfile(userId);
        Map<String, Object> vo = new HashMap<>();
        vo.put("userId", user.getUserId());
        vo.put("nickname", user.getNickname());
        vo.put("avatar", user.getAvatar());
        vo.put("realName", user.getRealName());
        vo.put("gender", user.getGender());
        vo.put("birthday", user.getBirthday());
        vo.put("mobileMasked", MaskUtil.mobile(user.getMobile()));
        vo.put("guardianName", user.getGuardianName());
        vo.put("guardianMobileMasked", MaskUtil.mobile(user.getGuardianMobile()));
        vo.put("userType", user.getUserType());
        vo.put("currentLevelId", user.getCurrentLevelId());
        vo.put("totalPoints", user.getTotalPoints());
        vo.put("availablePoints", user.getAvailablePoints());
        vo.put("growthValue", user.getGrowthValue());
        vo.put("registerTime", user.getRegisterTime());
        vo.put("enrollCount", profile.getEnrollCount());
        vo.put("scoreCount", profile.getScoreCount());
        vo.put("certCount", profile.getCertCount());
        vo.put("courseCount", profile.getCourseCount());
        vo.put("frozenPoints", profile.getFrozenPoints());
        vo.put("levelName", profile.getLevelName());
        vo.put("couponCount", profile.getCouponCount());
        vo.put("unreadMsgCount", profile.getUnreadMsgCount());
        vo.put("channelBinding", profile.getChannelBinding());
        return AjaxResult.success(vo);
    }

    /**
     * 更新当前用户资料(仅允许更新非敏感字段)
     */
    @PutMapping("/profile")
    public AjaxResult updateProfile(@RequestBody Map<String, Object> req) {
        Long userId = SecurityUtils.getUserId();
        JstUser user = jstUserService.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message());
        }
        // 白名单更新:仅允许 nickname/avatar/realName/gender/birthday/guardianName/guardianMobile
        if (req.containsKey("nickname"))       user.setNickname((String) req.get("nickname"));
        if (req.containsKey("avatar"))         user.setAvatar((String) req.get("avatar"));
        if (req.containsKey("realName"))       user.setRealName((String) req.get("realName"));
        if (req.containsKey("gender"))         user.setGender((Integer) req.get("gender"));
        if (req.containsKey("birthday")) {
            Object raw = req.get("birthday");
            if (raw == null || (raw instanceof String && ((String) raw).isEmpty())) {
                user.setBirthday(null);
            } else if (raw instanceof Date) {
                user.setBirthday((Date) raw);
            } else if (raw instanceof String) {
                try {
                    user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse((String) raw));
                } catch (Exception e) {
                    throw new ServiceException("birthday 格式非法，应为 yyyy-MM-dd");
                }
            }
        }
        if (req.containsKey("guardianName"))   user.setGuardianName((String) req.get("guardianName"));
        if (req.containsKey("guardianMobile")) {
            Object gm = req.get("guardianMobile");
            String val = gm == null ? null : String.valueOf(gm).trim();
            if (val != null && !val.isEmpty() && !val.matches("^1[3-9]\\d{9}$")) {
                throw new ServiceException("guardianMobile 格式非法");
            }
            user.setGuardianMobile(val == null || val.isEmpty() ? null : val);
        }
        // 禁止从前端更新:status/risk_flag/user_type/current_level_id/points/openid/mobile
        jstUserService.updateJstUser(user);
        return AjaxResult.success();
    }
}
