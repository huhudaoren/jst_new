package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstContestFaq;

import java.util.List;

/**
 * 赛事常见问题 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestFaqService {

    JstContestFaq selectByFaqId(Long faqId);

    List<JstContestFaq> selectByContestId(Long contestId);

    int insertJstContestFaq(JstContestFaq entity);

    int batchInsert(List<JstContestFaq> list);

    int updateJstContestFaq(JstContestFaq entity);

    int deleteByFaqId(Long faqId);

    int deleteByContestId(Long contestId);
}
