package com.ruoyi.jst.finance.vo;

import java.util.Date;

/**
 * 小程序端合同记录出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class ContractRecordVO {

    private Long contractId;
    private String contractNo;
    private String contractType;
    private String status;
    private String fileUrl;
    private Date effectiveTime;
    private Date signTime;
    private String remark;
    private Date createTime;

    /** 甲方标识（默认 jst） */
    private String partyA;
    /** 甲方名称快照 */
    private String partyAName;
    /** 乙方标识（partner:ID / channel:ID） */
    private String partyB;
    /** 乙方名称快照 */
    private String partyBName;
    /** 关联结算单 ID（同 refSettlementId，兼容 C 端字段名） */
    private Long settlementId;
    /** 关联结算单号（LEFT JOIN jst_rebate_settlement / jst_event_settlement） */
    private String settlementNo;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getPartyA() { return partyA; }
    public void setPartyA(String partyA) { this.partyA = partyA; }
    public String getPartyAName() { return partyAName; }
    public void setPartyAName(String partyAName) { this.partyAName = partyAName; }
    public String getPartyB() { return partyB; }
    public void setPartyB(String partyB) { this.partyB = partyB; }
    public String getPartyBName() { return partyBName; }
    public void setPartyBName(String partyBName) { this.partyBName = partyBName; }
    public Long getSettlementId() { return settlementId; }
    public void setSettlementId(Long settlementId) { this.settlementId = settlementId; }
    public String getSettlementNo() { return settlementNo; }
    public void setSettlementNo(String settlementNo) { this.settlementNo = settlementNo; }
}
