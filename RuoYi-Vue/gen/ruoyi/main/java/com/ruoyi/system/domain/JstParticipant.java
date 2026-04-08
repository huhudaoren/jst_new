package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 临时参赛档案-允许无正式账号存在对象 jst_participant
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstParticipant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 参赛人ID */
    private Long participantId;

    /** 关联正式账号ID，允许NULL（防孤儿）；认领后回填 */
    @Excel(name = "关联正式账号ID，允许NULL", readConverterExp = "防=孤儿")
    private Long userId;

    /** 档案类型：registered_participant正式 / temporary_participant临时 */
    @Excel(name = "档案类型：registered_participant正式 / temporary_participant临时")
    private String participantType;

    /** 参赛人姓名 */
    @Excel(name = "参赛人姓名")
    private String name;

    /** 性别：0未知 1男 2女 */
    @Excel(name = "性别：0未知 1男 2女")
    private Integer gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthday;

    /** 年龄（冗余便于检索） */
    @Excel(name = "年龄", readConverterExp = "冗=余便于检索")
    private Long age;

    /** 监护人姓名 */
    @Excel(name = "监护人姓名")
    private String guardianName;

    /** 监护人手机号（自动认领的关键匹配项） */
    @Excel(name = "监护人手机号", readConverterExp = "自=动认领的关键匹配项")
    private String guardianMobile;

    /** 学校 */
    @Excel(name = "学校")
    private String school;

    /** 所属机构 */
    @Excel(name = "所属机构")
    private String organization;

    /** 班级 */
    @Excel(name = "班级")
    private String className;

    /** 证件类型：id_card身份证/passport护照/... */
    @Excel(name = "证件类型：id_card身份证/passport护照/...")
    private String idCardType;

    /** 证件号（密文） */
    @Excel(name = "证件号", readConverterExp = "密=文")
    private String idCardNo;

    /** 创建该档案的渠道方ID，FK→jst_channel */
    @Excel(name = "创建该档案的渠道方ID，FK→jst_channel")
    private Long createdByChannelId;

    /** 认领状态：unclaimed未认领/auto_claimed自动认领/manual_claimed人工认领/pending_manual待人工 */
    @Excel(name = "认领状态：unclaimed未认领/auto_claimed自动认领/manual_claimed人工认领/pending_manual待人工")
    private String claimStatus;

    /** 已认领的正式用户ID（冗余便于检索） */
    @Excel(name = "已认领的正式用户ID", readConverterExp = "冗=余便于检索")
    private Long claimedUserId;

    /** 认领生效时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "认领生效时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date claimedTime;

    /** 可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方 */
    @Excel(name = "可见范围：channel_only仅创建渠道/platform平台/event_partner赛事方")
    private String visibleScope;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setParticipantId(Long participantId) 
    {
        this.participantId = participantId;
    }

    public Long getParticipantId() 
    {
        return participantId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setParticipantType(String participantType) 
    {
        this.participantType = participantType;
    }

    public String getParticipantType() 
    {
        return participantType;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
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

    public void setAge(Long age) 
    {
        this.age = age;
    }

    public Long getAge() 
    {
        return age;
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

    public void setSchool(String school) 
    {
        this.school = school;
    }

    public String getSchool() 
    {
        return school;
    }

    public void setOrganization(String organization) 
    {
        this.organization = organization;
    }

    public String getOrganization() 
    {
        return organization;
    }

    public void setClassName(String className) 
    {
        this.className = className;
    }

    public String getClassName() 
    {
        return className;
    }

    public void setIdCardType(String idCardType) 
    {
        this.idCardType = idCardType;
    }

    public String getIdCardType() 
    {
        return idCardType;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setCreatedByChannelId(Long createdByChannelId) 
    {
        this.createdByChannelId = createdByChannelId;
    }

    public Long getCreatedByChannelId() 
    {
        return createdByChannelId;
    }

    public void setClaimStatus(String claimStatus) 
    {
        this.claimStatus = claimStatus;
    }

    public String getClaimStatus() 
    {
        return claimStatus;
    }

    public void setClaimedUserId(Long claimedUserId) 
    {
        this.claimedUserId = claimedUserId;
    }

    public Long getClaimedUserId() 
    {
        return claimedUserId;
    }

    public void setClaimedTime(Date claimedTime) 
    {
        this.claimedTime = claimedTime;
    }

    public Date getClaimedTime() 
    {
        return claimedTime;
    }

    public void setVisibleScope(String visibleScope) 
    {
        this.visibleScope = visibleScope;
    }

    public String getVisibleScope() 
    {
        return visibleScope;
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
            .append("participantId", getParticipantId())
            .append("userId", getUserId())
            .append("participantType", getParticipantType())
            .append("name", getName())
            .append("gender", getGender())
            .append("birthday", getBirthday())
            .append("age", getAge())
            .append("guardianName", getGuardianName())
            .append("guardianMobile", getGuardianMobile())
            .append("school", getSchool())
            .append("organization", getOrganization())
            .append("className", getClassName())
            .append("idCardType", getIdCardType())
            .append("idCardNo", getIdCardNo())
            .append("createdByChannelId", getCreatedByChannelId())
            .append("claimStatus", getClaimStatus())
            .append("claimedUserId", getClaimedUserId())
            .append("claimedTime", getClaimedTime())
            .append("visibleScope", getVisibleScope())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
