package com.ruoyi.jst.user.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 临时参赛档案 Entity
 * <p>
 * 对应表 jst_participant
 * <p>
 * 关键字段：
 * <ul>
 *   <li>userId 允许 NULL：防孤儿数据规则。临时档案在被认领前不持有正式 userId</li>
 *   <li>claimStatus 状态机参见 SM-14 / {@link com.ruoyi.jst.user.enums.ClaimStatus}</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
public class Participant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 参赛人ID */
    private Long participantId;

    /** 关联正式账号ID（允许 NULL） */
    private Long userId;

    /** 档案类型: registered_participant / temporary_participant */
    private String participantType;

    /** 姓名 */
    private String name;

    /** 性别: 0未知 1男 2女 */
    private Integer gender;

    /** 出生日期 */
    private Date birthday;

    /** 年龄(冗余) */
    private Integer age;

    /** 监护人姓名 */
    private String guardianName;

    /** 监护人手机号(自动认领关键匹配项) */
    private String guardianMobile;

    /** 学校 */
    private String school;

    /** 所属机构 */
    private String organization;

    /** 班级 */
    private String className;

    /** 证件类型 */
    private String idCardType;

    /** 证件号(密文,通过 AesTypeHandler 自动加解密) */
    private String idCardNo;

    /** 创建该档案的渠道方ID */
    private Long createdByChannelId;

    /** 认领状态: unclaimed/auto_claimed/manual_claimed/pending_manual */
    private String claimStatus;

    /** 已认领的正式用户ID(冗余) */
    private Long claimedUserId;

    /** 认领生效时间 */
    private Date claimedTime;

    /** 可见范围: channel_only/platform/event_partner */
    private String visibleScope;

    // ===== getter / setter =====
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getParticipantType() { return participantType; }
    public void setParticipantType(String participantType) { this.participantType = participantType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    public String getGuardianMobile() { return guardianMobile; }
    public void setGuardianMobile(String guardianMobile) { this.guardianMobile = guardianMobile; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getIdCardType() { return idCardType; }
    public void setIdCardType(String idCardType) { this.idCardType = idCardType; }
    public String getIdCardNo() { return idCardNo; }
    public void setIdCardNo(String idCardNo) { this.idCardNo = idCardNo; }
    public Long getCreatedByChannelId() { return createdByChannelId; }
    public void setCreatedByChannelId(Long createdByChannelId) { this.createdByChannelId = createdByChannelId; }
    public String getClaimStatus() { return claimStatus; }
    public void setClaimStatus(String claimStatus) { this.claimStatus = claimStatus; }
    public Long getClaimedUserId() { return claimedUserId; }
    public void setClaimedUserId(Long claimedUserId) { this.claimedUserId = claimedUserId; }
    public Date getClaimedTime() { return claimedTime; }
    public void setClaimedTime(Date claimedTime) { this.claimedTime = claimedTime; }
    public String getVisibleScope() { return visibleScope; }
    public void setVisibleScope(String visibleScope) { this.visibleScope = visibleScope; }
}
