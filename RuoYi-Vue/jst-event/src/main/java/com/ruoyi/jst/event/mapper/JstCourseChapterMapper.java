package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstCourseChapter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程章节 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstCourseChapterMapper {

    JstCourseChapter selectByChapterId(@Param("chapterId") Long chapterId);

    List<JstCourseChapter> selectByCourseId(@Param("courseId") Long courseId);

    int insertJstCourseChapter(@Param("entity") JstCourseChapter entity);

    int batchInsert(@Param("list") List<JstCourseChapter> list);

    int updateJstCourseChapter(@Param("entity") JstCourseChapter entity);

    int deleteByChapterId(@Param("chapterId") Long chapterId);

    int deleteByCourseId(@Param("courseId") Long courseId);
}
