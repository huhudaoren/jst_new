package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstContestSchedule;

import java.util.List;

/**
 * 赛事赛程阶段 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestScheduleService {

    JstContestSchedule selectByScheduleId(Long scheduleId);

    List<JstContestSchedule> selectByContestId(Long contestId);

    int insertJstContestSchedule(JstContestSchedule entity);

    int batchInsert(List<JstContestSchedule> list);

    int updateJstContestSchedule(JstContestSchedule entity);

    int deleteByScheduleId(Long scheduleId);

    int deleteByContestId(Long contestId);
}
