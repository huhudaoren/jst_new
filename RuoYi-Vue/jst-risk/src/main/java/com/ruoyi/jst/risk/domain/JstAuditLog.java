package com.ruoyi.jst.risk.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 操作审计日志对象 jst_audit_log
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstAuditLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long auditId;

    /** 操作人ID */
    @Excel(name = "操作人ID")
    private Long operatorId;

    /** 操作人姓名 */
    @Excel(name = "操作人姓名")
    private String operatorName;

    /** 操作人类型：admin/partner/channel/system */
    @Excel(name = "操作人类型：admin/partner/channel/system")
    private String operatorType;

    /** 模块名 */
    @Excel(name = "模块名")
    private String module;

    /** 动作：review/pay/adjust/refund/grant/revoke 等 */
    @Excel(name = "动作：review/pay/adjust/refund/grant/revoke 等")
    private String action;

    /** 目标对象类型 */
    @Excel(name = "目标对象类型")
    private String targetType;

    /** 目标对象ID */
    @Excel(name = "目标对象ID")
    private String targetId;

    /** 变更前快照 */
    @Excel(name = "变更前快照")
    private String beforeJson;

    /** 变更后快照 */
    @Excel(name = "变更后快照")
    private String afterJson;

    /** 操作IP */
    @Excel(name = "操作IP")
    private String ip;

    /** 终端：web/h5/api */
    @Excel(name = "终端：web/h5/api")
    private String terminal;

    /** 结果：success/fail */
    @Excel(name = "结果：success/fail")
    private String result;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date opTime;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setAuditId(Long auditId) 
    {
        this.auditId = auditId;
    }

    public Long getAuditId() 
    {
        return auditId;
    }

    public void setOperatorId(Long operatorId) 
    {
        this.operatorId = operatorId;
    }

    public Long getOperatorId() 
    {
        return operatorId;
    }

    public void setOperatorName(String operatorName) 
    {
        this.operatorName = operatorName;
    }

    public String getOperatorName() 
    {
        return operatorName;
    }

    public void setOperatorType(String operatorType) 
    {
        this.operatorType = operatorType;
    }

    public String getOperatorType() 
    {
        return operatorType;
    }

    public void setModule(String module) 
    {
        this.module = module;
    }

    public String getModule() 
    {
        return module;
    }

    public void setAction(String action) 
    {
        this.action = action;
    }

    public String getAction() 
    {
        return action;
    }

    public void setTargetType(String targetType) 
    {
        this.targetType = targetType;
    }

    public String getTargetType() 
    {
        return targetType;
    }

    public void setTargetId(String targetId) 
    {
        this.targetId = targetId;
    }

    public String getTargetId() 
    {
        return targetId;
    }

    public void setBeforeJson(String beforeJson) 
    {
        this.beforeJson = beforeJson;
    }

    public String getBeforeJson() 
    {
        return beforeJson;
    }

    public void setAfterJson(String afterJson) 
    {
        this.afterJson = afterJson;
    }

    public String getAfterJson() 
    {
        return afterJson;
    }

    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }

    public void setTerminal(String terminal) 
    {
        this.terminal = terminal;
    }

    public String getTerminal() 
    {
        return terminal;
    }

    public void setResult(String result) 
    {
        this.result = result;
    }

    public String getResult() 
    {
        return result;
    }

    public void setOpTime(Date opTime) 
    {
        this.opTime = opTime;
    }

    public Date getOpTime() 
    {
        return opTime;
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
            .append("auditId", getAuditId())
            .append("operatorId", getOperatorId())
            .append("operatorName", getOperatorName())
            .append("operatorType", getOperatorType())
            .append("module", getModule())
            .append("action", getAction())
            .append("targetType", getTargetType())
            .append("targetId", getTargetId())
            .append("beforeJson", getBeforeJson())
            .append("afterJson", getAfterJson())
            .append("ip", getIp())
            .append("terminal", getTerminal())
            .append("result", getResult())
            .append("opTime", getOpTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
