package com.ruoyi.jst.event.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 团队报名提交请求 DTO。
 *
 * @author jst
 * @since 1.0.0
 */
public class TeamEnrollReqDTO {

    /** 赛事ID。 */
    @NotNull(message = "赛事ID不能为空")
    private Long contestId;

    /** 队长信息。 */
    @NotNull(message = "队长信息不能为空")
    @Valid
    private TeamMemberInfo leader;

    /** 队员信息列表。 */
    @NotNull(message = "队员列表不能为空")
    @Size(min = 1, message = "至少需要一名队员")
    @Valid
    private List<TeamMemberInfo> members;

    /** 预约时间段ID（可选）。 */
    private Long slotId;

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public TeamMemberInfo getLeader() {
        return leader;
    }

    public void setLeader(TeamMemberInfo leader) {
        this.leader = leader;
    }

    public List<TeamMemberInfo> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMemberInfo> members) {
        this.members = members;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    /**
     * 团队成员信息。
     */
    public static class TeamMemberInfo {

        @NotBlank(message = "姓名不能为空")
        @Size(max = 64, message = "姓名长度不能超过64")
        private String name;

        @NotBlank(message = "手机号不能为空")
        @Size(max = 20, message = "手机号长度不能超过20")
        private String phone;

        @Size(max = 30, message = "身份证号长度不能超过30")
        private String idCard;

        @Size(max = 128, message = "学校长度不能超过128")
        private String school;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getSchool() {
            return school;
        }

        public void setSchool(String school) {
            this.school = school;
        }
    }
}
