package com.ruoyi.jst.event.vo;

import java.util.List;

/**
 * 团队报名提交结果 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class TeamEnrollResVO {

    /** 队长的报名ID。 */
    private Long leaderEnrollId;

    /** 队长的报名编号。 */
    private String leaderEnrollNo;

    /** 全部成员的报名ID（含队长）。 */
    private List<Long> enrollIds;

    /** 团队总人数。 */
    private int teamSize;

    public Long getLeaderEnrollId() {
        return leaderEnrollId;
    }

    public void setLeaderEnrollId(Long leaderEnrollId) {
        this.leaderEnrollId = leaderEnrollId;
    }

    public String getLeaderEnrollNo() {
        return leaderEnrollNo;
    }

    public void setLeaderEnrollNo(String leaderEnrollNo) {
        this.leaderEnrollNo = leaderEnrollNo;
    }

    public List<Long> getEnrollIds() {
        return enrollIds;
    }

    public void setEnrollIds(List<Long> enrollIds) {
        this.enrollIds = enrollIds;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }
}
