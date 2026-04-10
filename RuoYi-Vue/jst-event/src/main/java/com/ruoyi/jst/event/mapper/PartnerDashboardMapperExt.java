package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.dto.PartnerDashboardQueryReqDTO;
import com.ruoyi.jst.event.vo.PartnerDashboardSummaryResVO;
import com.ruoyi.jst.event.vo.PartnerDashboardTodoItemResVO;
import com.ruoyi.jst.event.vo.PartnerRecentNoticeResVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PartnerDashboardMapperExt {

    PartnerDashboardSummaryResVO selectSummary(PartnerDashboardQueryReqDTO query);

    List<PartnerDashboardTodoItemResVO> selectPendingEnrollList(PartnerDashboardQueryReqDTO query);

    List<PartnerDashboardTodoItemResVO> selectPendingScoreList(PartnerDashboardQueryReqDTO query);

    List<PartnerRecentNoticeResVO> selectRecentNoticeList(@Param("limit") Integer limit);
}
