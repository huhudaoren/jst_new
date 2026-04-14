package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事预约时段 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstAppointmentSlotMapper {

    JstAppointmentSlot selectBySlotId(@Param("slotId") Long slotId);

    List<JstAppointmentSlot> selectByContestId(@Param("contestId") Long contestId);

    int insertJstAppointmentSlot(@Param("entity") JstAppointmentSlot entity);

    int batchInsert(@Param("list") List<JstAppointmentSlot> list);

    int updateJstAppointmentSlot(@Param("entity") JstAppointmentSlot entity);

    int deleteBySlotId(@Param("slotId") Long slotId);

    int deleteByContestId(@Param("contestId") Long contestId);
}
