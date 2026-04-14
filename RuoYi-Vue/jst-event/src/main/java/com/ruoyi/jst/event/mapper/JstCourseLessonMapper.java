package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstCourseLesson;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程课时 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstCourseLessonMapper {

    JstCourseLesson selectByLessonId(@Param("lessonId") Long lessonId);

    List<JstCourseLesson> selectByChapterId(@Param("chapterId") Long chapterId);

    int insertJstCourseLesson(@Param("entity") JstCourseLesson entity);

    int batchInsert(@Param("list") List<JstCourseLesson> list);

    int updateJstCourseLesson(@Param("entity") JstCourseLesson entity);

    int deleteByLessonId(@Param("lessonId") Long lessonId);

    int deleteByChapterId(@Param("chapterId") Long chapterId);

    int deleteByCourseId(@Param("courseId") Long courseId);
}
