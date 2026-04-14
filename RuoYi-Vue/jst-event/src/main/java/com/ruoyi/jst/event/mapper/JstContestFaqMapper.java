package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstContestFaq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事常见问题 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestFaqMapper {

    JstContestFaq selectByFaqId(@Param("faqId") Long faqId);

    List<JstContestFaq> selectByContestId(@Param("contestId") Long contestId);

    int insertJstContestFaq(@Param("entity") JstContestFaq entity);

    int batchInsert(@Param("list") List<JstContestFaq> list);

    int updateJstContestFaq(@Param("entity") JstContestFaq entity);

    int deleteByFaqId(@Param("faqId") Long faqId);

    int deleteByContestId(@Param("contestId") Long contestId);
}
