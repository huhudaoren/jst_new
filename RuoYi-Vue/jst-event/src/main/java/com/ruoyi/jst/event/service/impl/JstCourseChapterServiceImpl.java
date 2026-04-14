package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstCourseChapter;
import com.ruoyi.jst.event.mapper.JstCourseChapterMapper;
import com.ruoyi.jst.event.service.JstCourseChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程章节 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstCourseChapterServiceImpl implements JstCourseChapterService {

    @Autowired
    private JstCourseChapterMapper mapper;

    @Override
    public JstCourseChapter selectByChapterId(Long chapterId) {
        return mapper.selectByChapterId(chapterId);
    }

    @Override
    public List<JstCourseChapter> selectByCourseId(Long courseId) {
        return mapper.selectByCourseId(courseId);
    }

    @Override
    public int insertJstCourseChapter(JstCourseChapter entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstCourseChapter(entity);
    }

    @Override
    public int batchInsert(List<JstCourseChapter> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstCourseChapter(JstCourseChapter entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstCourseChapter(entity);
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
