package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.event.dto.PartnerDashboardQueryReqDTO;
import com.ruoyi.jst.event.mapper.PartnerDashboardMapperExt;
import com.ruoyi.jst.event.service.PartnerDashboardService;
import com.ruoyi.jst.event.vo.PartnerDashboardSummaryResVO;
import com.ruoyi.jst.event.vo.PartnerDashboardTodoItemResVO;
import com.ruoyi.jst.event.vo.PartnerDashboardTodoResVO;
import com.ruoyi.jst.event.vo.PartnerRecentNoticeResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PartnerDashboardServiceImpl implements PartnerDashboardService {

    private static final int NOTICE_SUMMARY_LENGTH = 60;

    @Autowired
    private PartnerDashboardMapperExt partnerDashboardMapperExt;

    @Override
    public PartnerDashboardSummaryResVO getSummary(PartnerDashboardQueryReqDTO query) {
        PartnerDashboardSummaryResVO summary = partnerDashboardMapperExt.selectSummary(query);
        if (summary == null) {
            summary = new PartnerDashboardSummaryResVO();
        }
        summary.setPendingEnrollCount(defaultCount(summary.getPendingEnrollCount()));
        summary.setMonthEnrollCount(defaultCount(summary.getMonthEnrollCount()));
        summary.setPendingScoreCount(defaultCount(summary.getPendingScoreCount()));
        summary.setPendingCertificateCount(defaultCount(summary.getPendingCertificateCount()));
        return summary;
    }

    @Override
    public PartnerDashboardTodoResVO getTodo(PartnerDashboardQueryReqDTO query) {
        PartnerDashboardTodoResVO todo = new PartnerDashboardTodoResVO();
        List<PartnerDashboardTodoItemResVO> pendingEnrollList = partnerDashboardMapperExt.selectPendingEnrollList(query);
        List<PartnerDashboardTodoItemResVO> pendingScoreList = partnerDashboardMapperExt.selectPendingScoreList(query);
        todo.setPendingEnrollList(pendingEnrollList == null ? new ArrayList<>() : pendingEnrollList);
        todo.setPendingScoreList(pendingScoreList == null ? new ArrayList<>() : pendingScoreList);
        return todo;
    }

    @Override
    public List<PartnerRecentNoticeResVO> getRecentNotices() {
        List<PartnerRecentNoticeResVO> notices = partnerDashboardMapperExt.selectRecentNoticeList(3);
        if (notices == null) {
            return new ArrayList<>();
        }
        notices.forEach(this::fillNoticeSummary);
        return notices;
    }

    private void fillNoticeSummary(PartnerRecentNoticeResVO notice) {
        String summary = notice.getSummary();
        if (StringUtils.isEmpty(summary)) {
            notice.setSummary("");
            return;
        }
        String plain = summary.replaceAll("<[^>]+>", "").replace("&nbsp;", " ").trim();
        if (plain.length() > NOTICE_SUMMARY_LENGTH) {
            plain = plain.substring(0, NOTICE_SUMMARY_LENGTH);
        }
        notice.setSummary(plain);
    }

    private Integer defaultCount(Integer value) {
        return value == null ? 0 : value;
    }
}
