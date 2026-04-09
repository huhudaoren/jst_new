package com.ruoyi.jst.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 核销记录查询入参。
 */
public class WriteoffRecordQueryDTO {

    private Long contestId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public Date getBeginDate() { return beginDate; }
    public void setBeginDate(Date beginDate) { this.beginDate = beginDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
}
