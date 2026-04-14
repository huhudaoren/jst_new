package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import com.ruoyi.jst.event.mapper.JstAppointmentSlotMapper;
import com.ruoyi.jst.event.service.JstAppointmentSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赛事预约时段 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstAppointmentSlotServiceImpl implements JstAppointmentSlotService {

    @Autowired
    private JstAppointmentSlotMapper mapper;

    @Override
    public JstAppointmentSlot selectBySlotId(Long slotId) {
        return mapper.selectBySlotId(slotId);
    }

    @Override
    public List<JstAppointmentSlot> selectByContestId(Long contestId) {
        return mapper.selectByContestId(contestId);
    }

    @Override
    public int insertJstAppointmentSlot(JstAppointmentSlot entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstAppointmentSlot(entity);
    }

    @Override
    public int batchInsert(List<JstAppointmentSlot> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstAppointmentSlot(JstAppointmentSlot entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstAppointmentSlot(entity);
    }

    @Override
    public int deleteBySlotId(Long slotId) {
        return mapper.deleteBySlotId(slotId);
    }

    @Override
    public int deleteByContestId(Long contestId) {
        return mapper.deleteByContestId(contestId);
    }
}
