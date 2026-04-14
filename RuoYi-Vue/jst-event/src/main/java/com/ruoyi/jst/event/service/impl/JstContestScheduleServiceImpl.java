package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstContestSchedule;
import com.ruoyi.jst.event.mapper.JstContestScheduleMapper;
import com.ruoyi.jst.event.service.JstContestScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赛事赛程阶段 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstContestScheduleServiceImpl implements JstContestScheduleService {

    @Autowired
    private JstContestScheduleMapper mapper;

    @Override
    public JstContestSchedule selectByScheduleId(Long scheduleId) {
        return mapper.selectByScheduleId(scheduleId);
    }

    @Override
    public List<JstContestSchedule> selectByContestId(Long contestId) {
        return mapper.selectByContestId(contestId);
    }

    @Override
    public int insertJstContestSchedule(JstContestSchedule entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstContestSchedule(entity);
    }

    @Override
    public int batchInsert(List<JstContestSchedule> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstContestSchedule(JstContestSchedule entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstContestSchedule(entity);
    }

    @Override
    public int deleteByScheduleId(Long scheduleId) {
        return mapper.deleteByScheduleId(scheduleId);
    }

    @Override
    public int deleteByContestId(Long contestId) {
        return mapper.deleteByContestId(contestId);
    }
}
