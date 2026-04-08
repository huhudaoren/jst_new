package com.ruoyi.jst.event.mapper;

import java.util.List;
import com.ruoyi.jst.event.domain.JstCourseLearnRecord;

/**
 * 课程学习记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstCourseLearnRecordMapper 
{
    /**
     * 查询课程学习记录
     * 
     * @param learnId 课程学习记录主键
     * @return 课程学习记录
     */
    public JstCourseLearnRecord selectJstCourseLearnRecordByLearnId(Long learnId);

    /**
     * 查询课程学习记录列表
     * 
     * @param jstCourseLearnRecord 课程学习记录
     * @return 课程学习记录集合
     */
    public List<JstCourseLearnRecord> selectJstCourseLearnRecordList(JstCourseLearnRecord jstCourseLearnRecord);

    /**
     * 新增课程学习记录
     * 
     * @param jstCourseLearnRecord 课程学习记录
     * @return 结果
     */
    public int insertJstCourseLearnRecord(JstCourseLearnRecord jstCourseLearnRecord);

    /**
     * 修改课程学习记录
     * 
     * @param jstCourseLearnRecord 课程学习记录
     * @return 结果
     */
    public int updateJstCourseLearnRecord(JstCourseLearnRecord jstCourseLearnRecord);

    /**
     * 删除课程学习记录
     * 
     * @param learnId 课程学习记录主键
     * @return 结果
     */
    public int deleteJstCourseLearnRecordByLearnId(Long learnId);

    /**
     * 批量删除课程学习记录
     * 
     * @param learnIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstCourseLearnRecordByLearnIds(Long[] learnIds);
}
