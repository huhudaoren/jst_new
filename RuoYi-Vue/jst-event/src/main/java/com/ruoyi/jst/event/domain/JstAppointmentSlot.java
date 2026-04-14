package com.ruoyi.jst.event.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 赛事预约时段实体。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstAppointmentSlot extends BaseEntity {

    private Long slotId;
    private Long contestId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date slotDate;
    private String startTime;
    private String endTime;
    private String venue;
    private Integer capacity;
    private Integer bookedCount;
    private Integer status;
    private String delFlag;

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Date getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(Date slotDate) {
        this.slotDate = slotDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(Integer bookedCount) {
        this.bookedCount = bookedCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
