package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.domain.JstScoreItem;

import java.util.List;

/**
 * 赛事成绩项 Service。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstScoreItemService {

    JstScoreItem selectByItemId(Long itemId);

    List<JstScoreItem> selectByContestId(Long contestId);

    int insertJstScoreItem(JstScoreItem entity);

    int batchInsert(List<JstScoreItem> list);

    int updateJstScoreItem(JstScoreItem entity);

    int deleteByItemId(Long itemId);

    int deleteByContestId(Long contestId);
}
