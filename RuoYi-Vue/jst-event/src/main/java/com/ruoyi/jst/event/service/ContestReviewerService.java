package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.ReviewerSaveReqDTO;
import com.ruoyi.jst.event.vo.ReviewerVO;

import java.util.List;

/**
 * 赛事评审老师服务接口。
 *
 * @author jst
 * @since 1.0.0
 */
public interface ContestReviewerService {

    /**
     * 获取赛事评审老师列表。
     *
     * @param contestId 赛事ID
     * @return 评审老师列表
     * @关联表 jst_contest_reviewer
     */
    List<ReviewerVO> listByContestId(Long contestId);

    /**
     * 配置赛事评审老师（全量替换）。
     *
     * @param contestId 赛事ID
     * @param req       配置请求
     * @关联表 jst_contest_reviewer
     */
    void saveReviewers(Long contestId, ReviewerSaveReqDTO req);

    /**
     * 查询当前用户作为评审老师负责的赛事ID列表。
     *
     * @param userId 用户ID
     * @return 赛事ID列表
     * @关联表 jst_contest_reviewer
     */
    List<Long> getReviewerContestIds(Long userId);
}
