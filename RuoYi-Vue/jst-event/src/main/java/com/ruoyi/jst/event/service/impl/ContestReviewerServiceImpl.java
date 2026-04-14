package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.domain.JstContestReviewer;
import com.ruoyi.jst.event.dto.ReviewerSaveReqDTO;
import com.ruoyi.jst.event.mapper.JstContestMapper;
import com.ruoyi.jst.event.mapper.JstContestReviewerMapper;
import com.ruoyi.jst.event.service.ContestReviewerService;
import com.ruoyi.jst.event.vo.ReviewerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 赛事评审老师服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ContestReviewerServiceImpl implements ContestReviewerService {

    @Autowired
    private JstContestReviewerMapper reviewerMapper;

    @Autowired
    private JstContestMapper jstContestMapper;

    /**
     * 获取赛事评审老师列表。
     *
     * @param contestId 赛事ID
     * @return 评审老师VO列表
     * @关联表 jst_contest_reviewer
     */
    @Override
    public List<ReviewerVO> listByContestId(Long contestId) {
        assertContestPartnerOwnership(contestId);
        List<JstContestReviewer> reviewers = reviewerMapper.selectByContestId(contestId);
        List<ReviewerVO> voList = new ArrayList<>();
        for (JstContestReviewer r : reviewers) {
            voList.add(toVO(r));
        }
        return voList;
    }

    /**
     * 配置赛事评审老师（先删后插，全量替换）。
     *
     * @param contestId 赛事ID
     * @param req       配置请求
     * @关联表 jst_contest_reviewer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveReviewers(Long contestId, ReviewerSaveReqDTO req) {
        // TX: 全量替换评审老师
        assertContestPartnerOwnership(contestId);
        String operator = currentOperatorName();
        reviewerMapper.deleteByContestId(contestId);
        List<JstContestReviewer> entities = new ArrayList<>();
        for (ReviewerSaveReqDTO.ReviewerItem item : req.getReviewers()) {
            JstContestReviewer entity = new JstContestReviewer();
            entity.setContestId(contestId);
            entity.setUserId(item.getUserId());
            entity.setReviewerName(item.getReviewerName());
            entity.setRole(StringUtils.isBlank(item.getRole()) ? "reviewer" : item.getRole());
            entity.setStatus(1);
            entity.setCreateBy(operator);
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            reviewerMapper.batchInsertIgnore(entities);
        }
    }

    /**
     * 查询用户作为评审老师负责的赛事ID列表。
     *
     * @param userId 用户ID
     * @return 赛事ID列表
     * @关联表 jst_contest_reviewer
     */
    @Override
    public List<Long> getReviewerContestIds(Long userId) {
        return reviewerMapper.selectContestIdsByUserId(userId);
    }

    /**
     * 校验赛事归属当前赛事方。
     */
    private void assertContestPartnerOwnership(Long contestId) {
        Long partnerId = JstLoginContext.currentPartnerId();
        if (partnerId == null) {
            return;
        }
        JstContest contest = jstContestMapper.selectJstContestByContestId(contestId);
        if (contest == null || !"0".equals(defaultDelFlag(contest.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        if (!Objects.equals(contest.getPartnerId(), partnerId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private ReviewerVO toVO(JstContestReviewer entity) {
        ReviewerVO vo = new ReviewerVO();
        vo.setId(entity.getId());
        vo.setContestId(entity.getContestId());
        vo.setUserId(entity.getUserId());
        vo.setReviewerName(entity.getReviewerName());
        vo.setRole(entity.getRole());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
    }
}
