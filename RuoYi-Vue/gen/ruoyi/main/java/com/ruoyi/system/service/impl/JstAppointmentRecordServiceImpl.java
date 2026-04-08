package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstAppointmentRecordMapper;
import com.ruoyi.system.domain.JstAppointmentRecord;
import com.ruoyi.system.service.IJstAppointmentRecordService;

/**
 * 个人预约记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstAppointmentRecordServiceImpl implements IJstAppointmentRecordService 
{
    @Autowired
    private JstAppointmentRecordMapper jstAppointmentRecordMapper;

    /**
     * 查询个人预约记录
     * 
     * @param appointmentId 个人预约记录主键
     * @return 个人预约记录
     */
    @Override
    public JstAppointmentRecord selectJstAppointmentRecordByAppointmentId(Long appointmentId)
    {
        return jstAppointmentRecordMapper.selectJstAppointmentRecordByAppointmentId(appointmentId);
    }

    /**
     * 查询个人预约记录列表
     * 
     * @param jstAppointmentRecord 个人预约记录
     * @return 个人预约记录
     */
    @Override
    public List<JstAppointmentRecord> selectJstAppointmentRecordList(JstAppointmentRecord jstAppointmentRecord)
    {
        return jstAppointmentRecordMapper.selectJstAppointmentRecordList(jstAppointmentRecord);
    }

    /**
     * 新增个人预约记录
     * 
     * @param jstAppointmentRecord 个人预约记录
     * @return 结果
     */
    @Override
    public int insertJstAppointmentRecord(JstAppointmentRecord jstAppointmentRecord)
    {
        jstAppointmentRecord.setCreateTime(DateUtils.getNowDate());
        return jstAppointmentRecordMapper.insertJstAppointmentRecord(jstAppointmentRecord);
    }

    /**
     * 修改个人预约记录
     * 
     * @param jstAppointmentRecord 个人预约记录
     * @return 结果
     */
    @Override
    public int updateJstAppointmentRecord(JstAppointmentRecord jstAppointmentRecord)
    {
        jstAppointmentRecord.setUpdateTime(DateUtils.getNowDate());
        return jstAppointmentRecordMapper.updateJstAppointmentRecord(jstAppointmentRecord);
    }

    /**
     * 批量删除个人预约记录
     * 
     * @param appointmentIds 需要删除的个人预约记录主键
     * @return 结果
     */
    @Override
    public int deleteJstAppointmentRecordByAppointmentIds(Long[] appointmentIds)
    {
        return jstAppointmentRecordMapper.deleteJstAppointmentRecordByAppointmentIds(appointmentIds);
    }

    /**
     * 删除个人预约记录信息
     * 
     * @param appointmentId 个人预约记录主键
     * @return 结果
     */
    @Override
    public int deleteJstAppointmentRecordByAppointmentId(Long appointmentId)
    {
        return jstAppointmentRecordMapper.deleteJstAppointmentRecordByAppointmentId(appointmentId);
    }
}
