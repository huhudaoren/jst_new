package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstCourseLearnRecordMapper;
import com.ruoyi.system.domain.JstCourseLearnRecord;
import com.ruoyi.system.service.IJstCourseLearnRecordService;

/**
 * 课程学习记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstCourseLearnRecordServiceImpl implements IJstCourseLearnRecordService 
{
    @Autowired
    private JstCourseLearnRecordMapper jstCourseLearnRecordMapper;

    /**
     * 查询课程学习记录
     * 
     * @param learnId 课程学习记录主键
     * @return 课程学习记录
     */
    @Override
    public JstCourseLearnRecord selectJstCourseLearnRecordByLearnId(Long learnId)
    {
        return jstCourseLearnRecordMapper.selectJstCourseLearnRecordByLearnId(learnId);
    }

    /**
     * 查询课程学习记录列表
     * 
     * @param jstCourseLearnRecord 课程学习记录
     * @return 课程学习记录
     */
    @Override
    public List<JstCourseLearnRecord> selectJstCourseLearnRecordList(JstCourseLearnRecord jstCourseLearnRecord)
    {
        return jstCourseLearnRecordMapper.selectJstCourseLearnRecordList(jstCourseLearnRecord);
    }

    /**
     * 新增课程学习记录
     * 
     * @param jstCourseLearnRecord 课程学习记录
     * @return 结果
     */
    @Override
    public int insertJstCourseLearnRecord(JstCourseLearnRecord jstCourseLearnRecord)
    {
        jstCourseLearnRecord.setCreateTime(DateUtils.getNowDate());
        return jstCourseLearnRecordMapper.insertJstCourseLearnRecord(jstCourseLearnRecord);
    }

    /**
     * 修改课程学习记录
     * 
     * @param jstCourseLearnRecord 课程学习记录
     * @return 结果
     */
    @Override
    public int updateJstCourseLearnRecord(JstCourseLearnRecord jstCourseLearnRecord)
    {
        jstCourseLearnRecord.setUpdateTime(DateUtils.getNowDate());
        return jstCourseLearnRecordMapper.updateJstCourseLearnRecord(jstCourseLearnRecord);
    }

    /**
     * 批量删除课程学习记录
     * 
     * @param learnIds 需要删除的课程学习记录主键
     * @return 结果
     */
    @Override
    public int deleteJstCourseLearnRecordByLearnIds(Long[] learnIds)
    {
        return jstCourseLearnRecordMapper.deleteJstCourseLearnRecordByLearnIds(learnIds);
    }

    /**
     * 删除课程学习记录信息
     * 
     * @param learnId 课程学习记录主键
     * @return 结果
     */
    @Override
    public int deleteJstCourseLearnRecordByLearnId(Long learnId)
    {
        return jstCourseLearnRecordMapper.deleteJstCourseLearnRecordByLearnId(learnId);
    }
}
