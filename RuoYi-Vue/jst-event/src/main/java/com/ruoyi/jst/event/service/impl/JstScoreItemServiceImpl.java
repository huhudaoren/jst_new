package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.event.domain.JstScoreItem;
import com.ruoyi.jst.event.mapper.JstScoreItemMapper;
import com.ruoyi.jst.event.service.JstScoreItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 赛事成绩项 Service 实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstScoreItemServiceImpl implements JstScoreItemService {

    @Autowired
    private JstScoreItemMapper mapper;

    @Override
    public JstScoreItem selectByItemId(Long itemId) {
        return mapper.selectByItemId(itemId);
    }

    @Override
    public List<JstScoreItem> selectByContestId(Long contestId) {
        return mapper.selectByContestId(contestId);
    }

    @Override
    public int insertJstScoreItem(JstScoreItem entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        return mapper.insertJstScoreItem(entity);
    }

    @Override
    public int batchInsert(List<JstScoreItem> list) {
        return mapper.batchInsert(list);
    }

    @Override
    public int updateJstScoreItem(JstScoreItem entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        return mapper.updateJstScoreItem(entity);
    }

    @Override
    public int deleteByItemId(Long itemId) {
        return mapper.deleteByItemId(itemId);
    }

    @Override
    public int deleteByContestId(Long contestId) {
        return mapper.deleteByContestId(contestId);
    }
}
