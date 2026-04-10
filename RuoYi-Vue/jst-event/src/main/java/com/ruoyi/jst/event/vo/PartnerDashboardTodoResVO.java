package com.ruoyi.jst.event.vo;

import java.util.List;

public class PartnerDashboardTodoResVO {

    private List<PartnerDashboardTodoItemResVO> pendingEnrollList;
    private List<PartnerDashboardTodoItemResVO> pendingScoreList;

    public List<PartnerDashboardTodoItemResVO> getPendingEnrollList() {
        return pendingEnrollList;
    }

    public void setPendingEnrollList(List<PartnerDashboardTodoItemResVO> pendingEnrollList) {
        this.pendingEnrollList = pendingEnrollList;
    }

    public List<PartnerDashboardTodoItemResVO> getPendingScoreList() {
        return pendingScoreList;
    }

    public void setPendingScoreList(List<PartnerDashboardTodoItemResVO> pendingScoreList) {
        this.pendingScoreList = pendingScoreList;
    }
}
