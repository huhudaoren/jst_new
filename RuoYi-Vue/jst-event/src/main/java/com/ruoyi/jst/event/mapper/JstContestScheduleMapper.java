package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstContestSchedule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事赛程阶段 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestScheduleMapper {

    JstContestSchedule selectByScheduleId(@Param("scheduleId") Long scheduleId);

    List<JstContestSchedule> selectByContestId(@Param("contestId") Long contestId);

    int insertJstContestSchedule(@Param("entity") JstContestSchedule entity);

    int batchInsert(@Param("list") List<JstContestSchedule> list);

    int updateJstContestSchedule(@Param("entity") JstContestSchedule entity);

    int deleteByScheduleId(@Param("scheduleId") Long scheduleId);

    int deleteByContestId(@Param("contestId") Long contestId);
}
