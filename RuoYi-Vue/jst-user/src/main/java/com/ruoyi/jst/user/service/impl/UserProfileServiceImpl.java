package com.ruoyi.jst.user.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.mapper.JstUserMapper;
import com.ruoyi.jst.user.mapper.UserProfileLookupMapper;
import com.ruoyi.jst.user.service.UserProfileService;
import com.ruoyi.jst.user.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private JstUserMapper jstUserMapper;

    @Autowired
    private UserProfileLookupMapper userProfileLookupMapper;

    @Override
    public UserProfileVO getProfile(Long userId) {
        JstUser user = jstUserMapper.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }

        UserProfileVO profile = new UserProfileVO();
        profile.setUserId(user.getUserId());
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        profile.setRealName(user.getRealName());
        profile.setGender(user.getGender());
        profile.setBirthday(user.getBirthday());
        profile.setMobileMasked(MaskUtil.mobile(user.getMobile()));
        profile.setGuardianName(user.getGuardianName());
        profile.setGuardianMobileMasked(MaskUtil.mobile(user.getGuardianMobile()));
        profile.setUserType(user.getUserType());
        profile.setCurrentLevelId(user.getCurrentLevelId());
        profile.setTotalPoints(user.getTotalPoints());
        profile.setAvailablePoints(user.getAvailablePoints());
        profile.setGrowthValue(user.getGrowthValue());
        profile.setRegisterTime(user.getRegisterTime());

        fillStats(profile, userProfileLookupMapper.selectProfileStats(userId));
        fillChannelBinding(profile, userProfileLookupMapper.selectChannelBinding(userId));
        return profile;
    }

    private void fillStats(UserProfileVO profile, Map<String, Object> stats) {
        if (stats == null || stats.isEmpty()) {
            profile.setEnrollCount(0);
            profile.setScoreCount(0);
            profile.setCertCount(0);
            profile.setCourseCount(0);
            profile.setFrozenPoints(0L);
            profile.setCouponCount(0);
            profile.setUnreadMsgCount(0);
            return;
        }
        profile.setEnrollCount(toInteger(stats.get("enrollCount")));
        profile.setScoreCount(toInteger(stats.get("scoreCount")));
        profile.setCertCount(toInteger(stats.get("certCount")));
        profile.setCourseCount(toInteger(stats.get("courseCount")));
        profile.setFrozenPoints(toLong(stats.get("frozenPoints")));
        profile.setLevelName(toString(stats.get("levelName")));
        profile.setCouponCount(toInteger(stats.get("couponCount")));
        profile.setUnreadMsgCount(toInteger(stats.get("unreadMsgCount")));
    }

    private void fillChannelBinding(UserProfileVO profile, Map<String, Object> binding) {
        if (binding == null || binding.isEmpty()) {
            return;
        }
        UserProfileVO.ChannelBindingVO channelBinding = new UserProfileVO.ChannelBindingVO();
        channelBinding.setName(toString(binding.get("name")));
        channelBinding.setSchool(toString(binding.get("school")));
        channelBinding.setRemark(toString(binding.get("remark")));
        if (StringUtils.isBlank(channelBinding.getName())
                && StringUtils.isBlank(channelBinding.getSchool())
                && StringUtils.isBlank(channelBinding.getRemark())) {
            return;
        }
        profile.setChannelBinding(channelBinding);
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private String toString(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}
