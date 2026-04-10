package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.PartnerDashboardQueryReqDTO;
import com.ruoyi.jst.event.vo.PartnerDashboardSummaryResVO;
import com.ruoyi.jst.event.vo.PartnerDashboardTodoResVO;
import com.ruoyi.jst.event.vo.PartnerRecentNoticeResVO;

import java.util.List;

public interface PartnerDashboardService {

    PartnerDashboardSummaryResVO getSummary(PartnerDashboardQueryReqDTO query);

    PartnerDashboardTodoResVO getTodo(PartnerDashboardQueryReqDTO query);

    List<PartnerRecentNoticeResVO> getRecentNotices();
}
