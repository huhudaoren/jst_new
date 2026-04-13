package com.ruoyi.jst.user.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 渠道方档案对象 jst_channel
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 渠道方ID */
    private Long channelId;

    /** 关联用户账号ID，FK→jst_user */
    @Excel(name = "关联用户账号ID，FK→jst_user")
    private Long userId;

    /** 渠道类型：teacher教师/organization机构/individual个人 */
    @Excel(name = "渠道类型：teacher教师/organization机构/individual个人")
    private String channelType;

    /** 渠道方名称 */
    @Excel(name = "渠道方名称")
    private String channelName;

    /** 联系手机 */
    @Excel(name = "联系手机")
    private String contactMobile;

    /** 证件号（密文） */
    @Excel(name = "证件号", readConverterExp = "密=文")
    private String idCardNo;

    /** 营业执照号（机构必填） */
    @Excel(name = "营业执照号", readConverterExp = "机=构必填")
    private String businessLicenseNo;

    /** 认证材料附件JSON：[{type,name,url}] */
    @Excel(name = "认证材料附件JSON：[{type,name,url}]")
    private String certMaterialsJson;

    /** 认证状态：pending待审/approved通过/rejected驳回/suspended暂停 */
    @Excel(name = "认证状态：pending待审/approved通过/rejected驳回/suspended暂停")
    private String authStatus;

    /** 认证通过时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "认证通过时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date authTime;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String authRemark;

    /** 当前等级ID，FK→jst_level_config */
    @Excel(name = "当前等级ID，FK→jst_level_config")
    private Long currentLevelId;

    /** 启停：0停用 1启用 */
    @Excel(name = "启停：0停用 1启用")
    private Integer status;

    /** 风控标记：0正常 1黑名单 2待复核 */
    @Excel(name = "风控标记：0正常 1黑名单 2待复核")
    private Integer riskFlag;

    /** 渠道标签（逗号分隔） */
    @Excel(name = "渠道标签", readConverterExp = "逗=号分隔")
    private String tags;

    /** 收款户名 */
    @Excel(name = "收款户名")
    private String bankAccountName;

    /** 收款账号（密文） */
    @Excel(name = "收款账号", readConverterExp = "密=文")
    private String bankAccountNo;

    /** 开户行 */
    @Excel(name = "开户行")
    private String bankName;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    /** 关联用户昵称（JOIN查出，非持久化） */
    private String userName;

    public void setChannelId(Long channelId) 
    {
        this.channelId = channelId;
    }

    public Long getChannelId() 
    {
        return channelId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setChannelType(String channelType) 
    {
        this.channelType = channelType;
    }

    public String getChannelType() 
    {
        return channelType;
    }

    public void setChannelName(String channelName) 
    {
        this.channelName = channelName;
    }

    public String getChannelName() 
    {
        return channelName;
    }

    public void setContactMobile(String contactMobile) 
    {
        this.contactMobile = contactMobile;
    }

    public String getContactMobile() 
    {
        return contactMobile;
    }

    public void setIdCardNo(String idCardNo) 
    {
        this.idCardNo = idCardNo;
    }

    public String getIdCardNo() 
    {
        return idCardNo;
    }

    public void setBusinessLicenseNo(String businessLicenseNo) 
    {
        this.businessLicenseNo = businessLicenseNo;
    }

    public String getBusinessLicenseNo() 
    {
        return businessLicenseNo;
    }

    public void setCertMaterialsJson(String certMaterialsJson) 
    {
        this.certMaterialsJson = certMaterialsJson;
    }

    public String getCertMaterialsJson() 
    {
        return certMaterialsJson;
    }

    public void setAuthStatus(String authStatus) 
    {
        this.authStatus = authStatus;
    }

    public String getAuthStatus() 
    {
        return authStatus;
    }

    public void setAuthTime(Date authTime) 
    {
        this.authTime = authTime;
    }

    public Date getAuthTime() 
    {
        return authTime;
    }

    public void setAuthRemark(String authRemark) 
    {
        this.authRemark = authRemark;
    }

    public String getAuthRemark() 
    {
        return authRemark;
    }

    public void setCurrentLevelId(Long currentLevelId) 
    {
        this.currentLevelId = currentLevelId;
    }

    public Long getCurrentLevelId() 
    {
        return currentLevelId;
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

    public void setTags(String tags) 
    {
        this.tags = tags;
    }

    public String getTags() 
    {
        return tags;
    }

    public void setBankAccountName(String bankAccountName) 
    {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountName() 
    {
        return bankAccountName;
    }

    public void setBankAccountNo(String bankAccountNo) 
    {
        this.bankAccountNo = bankAccountNo;
    }

    public String getBankAccountNo() 
    {
        return bankAccountNo;
    }

    public void setBankName(String bankName) 
    {
        this.bankName = bankName;
    }

    public String getBankName() 
    {
        return bankName;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("channelId", getChannelId())
            .append("userId", getUserId())
            .append("channelType", getChannelType())
            .append("channelName", getChannelName())
            .append("contactMobile", getContactMobile())
            .append("idCardNo", getIdCardNo())
            .append("businessLicenseNo", getBusinessLicenseNo())
            .append("certMaterialsJson", getCertMaterialsJson())
            .append("authStatus", getAuthStatus())
            .append("authTime", getAuthTime())
            .append("authRemark", getAuthRemark())
            .append("currentLevelId", getCurrentLevelId())
            .append("status", getStatus())
            .append("riskFlag", getRiskFlag())
            .append("tags", getTags())
            .append("bankAccountName", getBankAccountName())
            .append("bankAccountNo", getBankAccountNo())
            .append("bankName", getBankName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
