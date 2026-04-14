package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstScoreItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事成绩项 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstScoreItemMapper {

    JstScoreItem selectByItemId(@Param("itemId") Long itemId);

    List<JstScoreItem> selectByContestId(@Param("contestId") Long contestId);

    int insertJstScoreItem(@Param("entity") JstScoreItem entity);

    int batchInsert(@Param("list") List<JstScoreItem> list);

    int updateJstScoreItem(@Param("entity") JstScoreItem entity);

    int deleteByItemId(@Param("itemId") Long itemId);

    int deleteByContestId(@Param("contestId") Long contestId);
}
