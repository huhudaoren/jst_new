package com.ruoyi.jst.event.service;

import java.util.List;
import com.ruoyi.jst.event.domain.JstCourse;

/**
 * 课程Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstCourseService 
{
    /**
     * 查询课程
     * 
     * @param courseId 课程主键
     * @return 课程
     */
    public JstCourse selectJstCourseByCourseId(Long courseId);

    /**
     * 查询课程列表
     * 
     * @param jstCourse 课程
     * @return 课程集合
     */
    public List<JstCourse> selectJstCourseList(JstCourse jstCourse);

    /**
     * 新增课程
     * 
     * @param jstCourse 课程
     * @return 结果
     */
    public int insertJstCourse(JstCourse jstCourse);

    /**
     * 修改课程
     * 
     * @param jstCourse 课程
     * @return 结果
     */
    public int updateJstCourse(JstCourse jstCourse);

    /**
     * 批量删除课程
     * 
     * @param courseIds 需要删除的课程主键集合
     * @return 结果
     */
    public int deleteJstCourseByCourseIds(Long[] courseIds);

    /**
     * 删除课程信息
     * 
     * @param courseId 课程主键
     * @return 结果
     */
    public int deleteJstCourseByCourseId(Long courseId);
}
