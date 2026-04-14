package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstAppointmentSlot;

import java.util.List;

/**
 * 赛事预约时段 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstAppointmentSlotService {

    JstAppointmentSlot selectBySlotId(Long slotId);

    List<JstAppointmentSlot> selectByContestId(Long contestId);

    int insertJstAppointmentSlot(JstAppointmentSlot entity);

    int batchInsert(List<JstAppointmentSlot> list);

    int updateJstAppointmentSlot(JstAppointmentSlot entity);

    int deleteBySlotId(Long slotId);

    int deleteByContestId(Long contestId);
}
