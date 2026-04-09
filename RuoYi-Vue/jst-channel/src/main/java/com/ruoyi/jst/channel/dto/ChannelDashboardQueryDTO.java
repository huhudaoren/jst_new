package com.ruoyi.jst.channel.dto;

import jakarta.validation.constraints.Pattern;

import java.util.Date;

/**
 * Shared query object for channel dashboard wx endpoints.
 */
public class ChannelDashboardQueryDTO {

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "yearMonth must match yyyy-MM")
    private String yearMonth;

    @Pattern(regexp = "last7d|last30d|last90d", message = "period only supports last7d/last30d/last90d")
    private String period;

    private String keyword;

    @Pattern(regexp = "active|expired|replaced|manual_unbound", message = "bindStatus only supports active/expired/replaced/manual_unbound")
    private String bindStatus;

    @Pattern(regexp = "created|pending_pay|paid|reviewing|in_service|aftersale|completed|cancelled|closed",
            message = "status only supports created/pending_pay/paid/reviewing/in_service/aftersale/completed/cancelled/closed")
    private String status;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "startDate must match yyyy-MM-dd")
    private String startDate;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "endDate must match yyyy-MM-dd")
    private String endDate;

    private Integer pageNum;

    private Integer pageSize;

    private Long channelId;

    private Date startTime;

    private Date endTime;

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(String bindStatus) {
        this.bindStatus = bindStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
