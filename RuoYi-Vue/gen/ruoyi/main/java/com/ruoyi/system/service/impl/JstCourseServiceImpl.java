package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstCourseMapper;
import com.ruoyi.system.domain.JstCourse;
import com.ruoyi.system.service.IJstCourseService;

/**
 * 课程Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstCourseServiceImpl implements IJstCourseService 
{
    @Autowired
    private JstCourseMapper jstCourseMapper;

    /**
     * 查询课程
     * 
     * @param courseId 课程主键
     * @return 课程
     */
    @Override
    public JstCourse selectJstCourseByCourseId(Long courseId)
    {
        return jstCourseMapper.selectJstCourseByCourseId(courseId);
    }

    /**
     * 查询课程列表
     * 
     * @param jstCourse 课程
     * @return 课程
     */
    @Override
    public List<JstCourse> selectJstCourseList(JstCourse jstCourse)
    {
        return jstCourseMapper.selectJstCourseList(jstCourse);
    }

    /**
     * 新增课程
     * 
     * @param jstCourse 课程
     * @return 结果
     */
    @Override
    public int insertJstCourse(JstCourse jstCourse)
    {
        jstCourse.setCreateTime(DateUtils.getNowDate());
        return jstCourseMapper.insertJstCourse(jstCourse);
    }

    /**
     * 修改课程
     * 
     * @param jstCourse 课程
     * @return 结果
     */
    @Override
    public int updateJstCourse(JstCourse jstCourse)
    {
        jstCourse.setUpdateTime(DateUtils.getNowDate());
        return jstCourseMapper.updateJstCourse(jstCourse);
    }

    /**
     * 批量删除课程
     * 
     * @param courseIds 需要删除的课程主键
     * @return 结果
     */
    @Override
    public int deleteJstCourseByCourseIds(Long[] courseIds)
    {
        return jstCourseMapper.deleteJstCourseByCourseIds(courseIds);
    }

    /**
     * 删除课程信息
     * 
     * @param courseId 课程主键
     * @return 结果
     */
    @Override
    public int deleteJstCourseByCourseId(Long courseId)
    {
        return jstCourseMapper.deleteJstCourseByCourseId(courseId);
    }
}
