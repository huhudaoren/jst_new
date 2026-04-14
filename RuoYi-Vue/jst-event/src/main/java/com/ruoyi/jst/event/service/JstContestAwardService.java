package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstContestAward;

import java.util.List;

/**
 * 赛事奖项配置 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestAwardService {

    JstContestAward selectByAwardId(Long awardId);

    List<JstContestAward> selectByContestId(Long contestId);

    int insertJstContestAward(JstContestAward entity);

    int batchInsert(List<JstContestAward> list);

    int updateJstContestAward(JstContestAward entity);

    int deleteByAwardId(Long awardId);

    int deleteByContestId(Long contestId);
}
