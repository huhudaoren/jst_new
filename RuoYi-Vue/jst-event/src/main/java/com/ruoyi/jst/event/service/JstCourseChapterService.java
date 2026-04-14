package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstCourseChapter;

import java.util.List;

/**
 * 课程章节 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstCourseChapterService {

    JstCourseChapter selectByChapterId(Long chapterId);

    List<JstCourseChapter> selectByCourseId(Long courseId);

    int insertJstCourseChapter(JstCourseChapter entity);

    int batchInsert(List<JstCourseChapter> list);

    int updateJstCourseChapter(JstCourseChapter entity);

    int deleteByChapterId(Long chapterId);

    int deleteByCourseId(Long courseId);
}
