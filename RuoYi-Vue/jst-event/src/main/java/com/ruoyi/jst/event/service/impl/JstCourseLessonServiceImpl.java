package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstCourseLesson;
import com.ruoyi.jst.event.mapper.JstCourseLessonMapper;
import com.ruoyi.jst.event.service.JstCourseLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程课时 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstCourseLessonServiceImpl implements JstCourseLessonService {

    @Autowired
    private JstCourseLessonMapper mapper;

    @Override
    public JstCourseLesson selectByLessonId(Long lessonId) {
        return mapper.selectByLessonId(lessonId);
    }

    @Override
    public List<JstCourseLesson> selectByChapterId(Long chapterId) {
        return mapper.selectByChapterId(chapterId);
    }

    @Override
    public int insertJstCourseLesson(JstCourseLesson entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstCourseLesson(entity);
    }

    @Override
    public int batchInsert(List<JstCourseLesson> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstCourseLesson(JstCourseLesson entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstCourseLesson(entity);
    }

    @Override
    public int deleteByLessonId(Long lessonId) {
        return mapper.deleteByLessonId(lessonId);
    }

    @Override
    public int deleteByChapterId(Long chapterId) {
        return mapper.deleteByChapterId(chapterId);
    }

    @Override
    public int deleteByCourseId(Long courseId) {
        return mapper.deleteByCourseId(courseId);
    }
}
