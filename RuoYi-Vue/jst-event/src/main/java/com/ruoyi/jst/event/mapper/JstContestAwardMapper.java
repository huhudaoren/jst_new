package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstContestAward;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 赛事奖项配置 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstContestAwardMapper {

    JstContestAward selectByAwardId(@Param("awardId") Long awardId);

    List<JstContestAward> selectByContestId(@Param("contestId") Long contestId);

    int insertJstContestAward(@Param("entity") JstContestAward entity);

    int batchInsert(@Param("list") List<JstContestAward> list);

    int updateJstContestAward(@Param("entity") JstContestAward entity);

    int deleteByAwardId(@Param("awardId") Long awardId);

    int deleteByContestId(@Param("contestId") Long contestId);
}
