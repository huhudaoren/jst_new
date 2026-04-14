package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstContestFaq;
import com.ruoyi.jst.event.mapper.JstContestFaqMapper;
import com.ruoyi.jst.event.service.JstContestFaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赛事常见问题 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstContestFaqServiceImpl implements JstContestFaqService {

    @Autowired
    private JstContestFaqMapper mapper;

    @Override
    public JstContestFaq selectByFaqId(Long faqId) {
        return mapper.selectByFaqId(faqId);
    }

    @Override
    public List<JstContestFaq> selectByContestId(Long contestId) {
        return mapper.selectByContestId(contestId);
    }

    @Override
    public int insertJstContestFaq(JstContestFaq entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstContestFaq(entity);
    }

    @Override
    public int batchInsert(List<JstContestFaq> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstContestFaq(JstContestFaq entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstContestFaq(entity);
    }

    @Override
    public int deleteByFaqId(Long faqId) {
        return mapper.deleteByFaqId(faqId);
    }

    @Override
    public int deleteByContestId(Long contestId) {
        return mapper.deleteByContestId(contestId);
    }
}
