package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户主-学生/家长/渠道方业务账号对象 jst_user
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 用户ID（业务正式账号主键） */
    private Long userId;

    /** 微信小程序 openid */
    @Excel(name = "微信小程序 openid")
    private String openid;

    /** 微信开放平台 unionid */
    @Excel(name = "微信开放平台 unionid")
    private String unionid;

    /** 手机号（学生本人或主账号） */
    @Excel(name = "手机号", readConverterExp = "学=生本人或主账号")
    private String mobile;

    /** 昵称 */
    @Excel(name = "昵称")
    private String nickname;

    /** 头像URL */
    @Excel(name = "头像URL")
    private String avatar;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 性别：0未知 1男 2女 */
    @Excel(name = "性别：0未知 1男 2女")
    private Integer gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthday;

    /** 证件号（密文存储，AES） */
    @Excel(name = "证件号", readConverterExp = "密=文存储，AES")
    private String idCardNo;

    /** 监护人姓名 */
    @Excel(name = "监护人姓名")
    private String guardianName;

    /** 监护人手机号 */
    @Excel(name = "监护人手机号")
    private String guardianMobile;

    /** 用户类型：student学生 / parent家长 / channel渠道方 */
    @Excel(name = "用户类型：student学生 / parent家长 / channel渠道方")
    private String userType;

    /** 当前等级ID，FK→jst_level_config.level_id */
    @Excel(name = "当前等级ID，FK→jst_level_config.level_id")
    private Long currentLevelId;

    /** 累计获得积分（只增不减，用于成长统计） */
    @Excel(name = "累计获得积分", readConverterExp = "只=增不减，用于成长统计")
    private Long totalPoints;

    /** 当前可用积分余额（不含冻结） */
    @Excel(name = "当前可用积分余额", readConverterExp = "不=含冻结")
    private Long availablePoints;

    /** 冻结积分（兑换中/退款中） */
    @Excel(name = "冻结积分", readConverterExp = "兑=换中/退款中")
    private Long frozenPoints;

    /** 成长值（升级用，默认只增不减） */
    @Excel(name = "成长值", readConverterExp = "升=级用，默认只增不减")
    private Long growthValue;

    /** 当前有效绑定渠道方ID，FK→jst_channel.channel_id */
    @Excel(name = "当前有效绑定渠道方ID，FK→jst_channel.channel_id")
    private Long boundChannelId;

    /** 账号状态：0禁用 1正常 2封禁 */
    @Excel(name = "账号状态：0禁用 1正常 2封禁")
    private Integer status;

    /** 风控标记：0正常 1黑名单 2待复核 */
    @Excel(name = "风控标记：0正常 1黑名单 2待复核")
    private Integer riskFlag;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "注册时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registerTime;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后登录时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastLoginTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setOpenid(String openid) 
    {
        this.openid = openid;
    }

    public String getOpenid() 
    {
        return openid;
    }

    public void setUnionid(String unionid) 
    {
        this.unionid = unionid;
    }

    public String getUnionid() 
    {
        return unionid;
    }

    public void setMobile(String mobile) 
    {
        this.mobile = mobile;
    }

    public String getMobile() 
    {
        return mobile;
    }

    public void setNickname(String nickname) 
    {
        this.nickname = nickname;
    }

    public String getNickname() 
    {
        return nickname;
    }

    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }

    public void setRealName(String realName) 
    {
        this.realName = realName;
    }

    public String getRealName() 
    {
        return realName;
    }

    public void setGender(Integer gender) 
    {
        this.gender = gender;
    }

    public Integer getGender() 
    {
        return gender;
    }

    public void setBirthday(Date birthday) 
    {
        this.birthday = birthday;
    }

    public Date getBirthday() 
    {
        return birthday;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setGuardianName(String guardianName) 
    {
        this.guardianName = guardianName;
    }

    public String getGuardianName() 
    {
        return guardianName;
    }

    public void setGuardianMobile(String guardianMobile) 
    {
        this.guardianMobile = guardianMobile;
    }

    public String getGuardianMobile() 
    {
        return guardianMobile;
    }

    public void setUserType(String userType) 
    {
        this.userType = userType;
    }

    public String getUserType() 
    {
        return userType;
    }

    public void setCurrentLevelId(Long currentLevelId) 
    {
        this.currentLevelId = currentLevelId;
    }

    public Long getCurrentLevelId() 
    {
        return currentLevelId;
    }

    public void setTotalPoints(Long totalPoints) 
    {
        this.totalPoints = totalPoints;
    }

    public Long getTotalPoints() 
    {
        return totalPoints;
    }

    public void setAvailablePoints(Long availablePoints) 
    {
        this.availablePoints = availablePoints;
    }

    public Long getAvailablePoints() 
    {
        return availablePoints;
    }

    public void setFrozenPoints(Long frozenPoints) 
    {
        this.frozenPoints = frozenPoints;
    }

    public Long getFrozenPoints() 
    {
        return frozenPoints;
    }

    public void setGrowthValue(Long growthValue) 
    {
        this.growthValue = growthValue;
    }

    public Long getGrowthValue() 
    {
        return growthValue;
    }

    public void setBoundChannelId(Long boundChannelId) 
    {
        this.boundChannelId = boundChannelId;
    }

    public Long getBoundChannelId() 
    {
        return boundChannelId;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setRiskFlag(Integer riskFlag) 
    {
        this.riskFlag = riskFlag;
    }

    public Integer getRiskFlag() 
    {
        return riskFlag;
    }

    public void setRegisterTime(Date registerTime) 
    {
        this.registerTime = registerTime;
    }

    public Date getRegisterTime() 
    {
        return registerTime;
    }

    public void setLastLoginTime(Date lastLoginTime) 
    {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getLastLoginTime() 
    {
        return lastLoginTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("openid", getOpenid())
            .append("unionid", getUnionid())
            .append("mobile", getMobile())
            .append("nickname", getNickname())
            .append("avatar", getAvatar())
            .append("realName", getRealName())
            .append("gender", getGender())
            .append("birthday", getBirthday())
            .append("idCardNo", getIdCardNo())
            .append("guardianName", getGuardianName())
            .append("guardianMobile", getGuardianMobile())
            .append("userType", getUserType())
            .append("currentLevelId", getCurrentLevelId())
            .append("totalPoints", getTotalPoints())
            .append("availablePoints", getAvailablePoints())
            .append("frozenPoints", getFrozenPoints())
            .append("growthValue", getGrowthValue())
            .append("boundChannelId", getBoundChannelId())
            .append("status", getStatus())
            .append("riskFlag", getRiskFlag())
            .append("registerTime", getRegisterTime())
            .append("lastLoginTime", getLastLoginTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
