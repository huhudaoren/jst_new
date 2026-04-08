package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstAppointmentRecord;

/**
 * 个人预约记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstAppointmentRecordService 
{
    /**
     * 查询个人预约记录
     * 
     * @param appointmentId 个人预约记录主键
     * @return 个人预约记录
     */
    public JstAppointmentRecord selectJstAppointmentRecordByAppointmentId(Long appointmentId);

    /**
     * 查询个人预约记录列表
     * 
     * @param jstAppointmentRecord 个人预约记录
     * @return 个人预约记录集合
     */
    public List<JstAppointmentRecord> selectJstAppointmentRecordList(JstAppointmentRecord jstAppointmentRecord);

    /**
     * 新增个人预约记录
     * 
     * @param jstAppointmentRecord 个人预约记录
     * @return 结果
     */
    public int insertJstAppointmentRecord(JstAppointmentRecord jstAppointmentRecord);

    /**
     * 修改个人预约记录
     * 
     * @param jstAppointmentRecord 个人预约记录
     * @return 结果
     */
    public int updateJstAppointmentRecord(JstAppointmentRecord jstAppointmentRecord);

    /**
     * 批量删除个人预约记录
     * 
     * @param appointmentIds 需要删除的个人预约记录主键集合
     * @return 结果
     */
    public int deleteJstAppointmentRecordByAppointmentIds(Long[] appointmentIds);

    /**
     * 删除个人预约记录信息
     * 
     * @param appointmentId 个人预约记录主键
     * @return 结果
     */
    public int deleteJstAppointmentRecordByAppointmentId(Long appointmentId);
}
