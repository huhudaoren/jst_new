package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstContestAward;
import com.ruoyi.jst.event.mapper.JstContestAwardMapper;
import com.ruoyi.jst.event.service.JstContestAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赛事奖项配置 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstContestAwardServiceImpl implements JstContestAwardService {

    @Autowired
    private JstContestAwardMapper mapper;

    @Override
    public JstContestAward selectByAwardId(Long awardId) {
        return mapper.selectByAwardId(awardId);
    }

    @Override
    public List<JstContestAward> selectByContestId(Long contestId) {
        return mapper.selectByContestId(contestId);
    }

    @Override
    public int insertJstContestAward(JstContestAward entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstContestAward(entity);
    }

    @Override
    public int batchInsert(List<JstContestAward> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstContestAward(JstContestAward entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstContestAward(entity);
    }

    @Override
    public int deleteByAwardId(Long awardId) {
        return mapper.deleteByAwardId(awardId);
    }

    @Override
    public int deleteByContestId(Long contestId) {
        return mapper.deleteByContestId(contestId);
    }
}
