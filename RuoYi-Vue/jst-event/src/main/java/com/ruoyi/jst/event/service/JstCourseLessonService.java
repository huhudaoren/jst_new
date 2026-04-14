package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstCourseLesson;

import java.util.List;

/**
 * 课程课时 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstCourseLessonService {

    JstCourseLesson selectByLessonId(Long lessonId);

    List<JstCourseLesson> selectByChapterId(Long chapterId);

    int insertJstCourseLesson(JstCourseLesson entity);

    int batchInsert(List<JstCourseLesson> list);

    int updateJstCourseLesson(JstCourseLesson entity);

    int deleteByLessonId(Long lessonId);

    int deleteByChapterId(Long chapterId);

    int deleteByCourseId(Long courseId);
}
