package com.ruoyi.jst.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.Date;
import java.util.List;

/**
 * 团队预约申请入参。
 */
public class TeamAppointmentApplyDTO {

    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    @NotBlank(message = "团队名称不能为空")
    private String teamName;

    @NotBlank(message = "场次不能为空")
    private String sessionCode;

    @NotNull(message = "预约日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date appointmentDate;

    @Valid
    @NotEmpty(message = "成员列表不能为空")
    private List<TeamMemberDTO> members;

    public Long getContestId() { return contestId; }
    public void setContestId(Long contestId) { this.contestId = contestId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public String getSessionCode() { return sessionCode; }
    public void setSessionCode(String sessionCode) { this.sessionCode = sessionCode; }
    public Date getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Date appointmentDate) { this.appointmentDate = appointmentDate; }
    public List<TeamMemberDTO> getMembers() { return members; }
    public void setMembers(List<TeamMemberDTO> members) { this.members = members; }

    public static class TeamMemberDTO {
        private Long userId;
        private Long participantId;

        @Valid
        private SubOrderInputDTO subOrderInput;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getParticipantId() { return participantId; }
        public void setParticipantId(Long participantId) { this.participantId = participantId; }
        public SubOrderInputDTO getSubOrderInput() { return subOrderInput; }
        public void setSubOrderInput(SubOrderInputDTO subOrderInput) { this.subOrderInput = subOrderInput; }
    }

    public static class SubOrderInputDTO {
        @Pattern(regexp = "wechat|bank_transfer|points", message = "支付方式仅支持 wechat/bank_transfer/points")
        private String payMethod;

        public String getPayMethod() { return payMethod; }
        public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    }
}
