package com.ruoyi.jst.marketing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户权益核销记录出参（跨权益）。
 *
 * <p>解决前端聚合 N+1 问题，`/jst/wx/rights/writeoff-records` 端点返回。
 *
 * @author jst
 * @since 1.0.0
 */
public class RightsWriteoffRecordVO {

    /** 核销记录 ID */
    private Long recordId;
    /** 归属用户权益 ID (= user_rights_id) */
    private Long rightsId;
    /** 权益模板 ID */
    private Long rightsTemplateId;
    /** 权益名称 */
    private String rightsName;
    /** 权益类型（venue_reduce / course_free / support_priority / ...） */
    private String rightsType;
    /** 权益图标 emoji（前端展示用） */
    private String rightsIconEmoji;
    /** 核销单号 */
    private String writeoffNo;
    /** 核销金额/次数（use_amount） */
    private BigDecimal amount;
    /** 用户申请说明 */
    private String remark;
    /** 状态（pending / approved / rejected / rolled_back / used / expired） */
    private String status;
    /** 审核员 ID */
    private Long auditUserId;
    /** 审核备注（通过说明 / 驳回原因） */
    private String auditRemark;
    /** 核销/审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /** 申请时间 (= create_time) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public Long getRightsId() { return rightsId; }
    public void setRightsId(Long rightsId) { this.rightsId = rightsId; }
    public Long getRightsTemplateId() { return rightsTemplateId; }
    public void setRightsTemplateId(Long rightsTemplateId) { this.rightsTemplateId = rightsTemplateId; }
    public String getRightsName() { return rightsName; }
    public void setRightsName(String rightsName) { this.rightsName = rightsName; }
    public String getRightsType() { return rightsType; }
    public void setRightsType(String rightsType) { this.rightsType = rightsType; }
    public String getRightsIconEmoji() { return rightsIconEmoji; }
    public void setRightsIconEmoji(String rightsIconEmoji) { this.rightsIconEmoji = rightsIconEmoji; }
    public String getWriteoffNo() { return writeoffNo; }
    public void setWriteoffNo(String writeoffNo) { this.writeoffNo = writeoffNo; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getAuditUserId() { return auditUserId; }
    public void setAuditUserId(Long auditUserId) { this.auditUserId = auditUserId; }
    public String getAuditRemark() { return auditRemark; }
    public void setAuditRemark(String auditRemark) { this.auditRemark = auditRemark; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public Date getApplyTime() { return applyTime; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
}
