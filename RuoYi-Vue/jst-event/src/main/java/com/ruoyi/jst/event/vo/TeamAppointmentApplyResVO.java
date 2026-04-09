package com.ruoyi.jst.event.vo;

import java.util.List;

/**
 * 团队预约申请结果。
 */
public class TeamAppointmentApplyResVO {

    private Long teamAppointmentId;
    private String teamNo;
    private Integer memberCount;
    private List<Long> subOrderIds;

    public Long getTeamAppointmentId() { return teamAppointmentId; }
    public void setTeamAppointmentId(Long teamAppointmentId) { this.teamAppointmentId = teamAppointmentId; }
    public String getTeamNo() { return teamNo; }
    public void setTeamNo(String teamNo) { this.teamNo = teamNo; }
    public Integer getMemberCount() { return memberCount; }
    public void setMemberCount(Integer memberCount) { this.memberCount = memberCount; }
    public List<Long> getSubOrderIds() { return subOrderIds; }
    public void setSubOrderIds(List<Long> subOrderIds) { this.subOrderIds = subOrderIds; }
}
