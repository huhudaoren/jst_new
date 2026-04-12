package com.ruoyi.jst.event.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.event.ScorePublishedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.ruoyi.jst.event.mapper.JstScoreRecordMapper;
import com.ruoyi.jst.event.domain.JstScoreRecord;
import com.ruoyi.jst.event.service.IJstScoreRecordService;

/**
 * 成绩记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstScoreRecordServiceImpl implements IJstScoreRecordService 
{
    @Autowired
    private JstScoreRecordMapper jstScoreRecordMapper;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 查询成绩记录
     * 
     * @param scoreId 成绩记录主键
     * @return 成绩记录
     */
    @Override
    public JstScoreRecord selectJstScoreRecordByScoreId(Long scoreId)
    {
        return jstScoreRecordMapper.selectJstScoreRecordByScoreId(scoreId);
    }

    /**
     * 查询成绩记录列表
     * 
     * @param jstScoreRecord 成绩记录
     * @return 成绩记录
     */
    @Override
    public List<JstScoreRecord> selectJstScoreRecordList(JstScoreRecord jstScoreRecord)
    {
        return jstScoreRecordMapper.selectJstScoreRecordList(jstScoreRecord);
    }

    /**
     * 新增成绩记录
     * 
     * @param jstScoreRecord 成绩记录
     * @return 结果
     */
    @Override
    public int insertJstScoreRecord(JstScoreRecord jstScoreRecord)
    {
        jstScoreRecord.setCreateTime(DateUtils.getNowDate());
        return jstScoreRecordMapper.insertJstScoreRecord(jstScoreRecord);
    }

    /**
     * 修改成绩记录
     * 
     * @param jstScoreRecord 成绩记录
     * @return 结果
     */
    @Override
    public int updateJstScoreRecord(JstScoreRecord jstScoreRecord)
    {
        JstScoreRecord before = null;
        if (jstScoreRecord != null && jstScoreRecord.getScoreId() != null) {
            before = jstScoreRecordMapper.selectJstScoreRecordByScoreId(jstScoreRecord.getScoreId());
        }
        jstScoreRecord.setUpdateTime(DateUtils.getNowDate());
        int updated = jstScoreRecordMapper.updateJstScoreRecord(jstScoreRecord);
        if (updated > 0 && shouldPublishScoreEvent(before, jstScoreRecord)) {
            publishAfterCommit(buildScorePublishedEvent(before, jstScoreRecord));
        }
        return updated;
    }

    /**
     * 批量删除成绩记录
     * 
     * @param scoreIds 需要删除的成绩记录主键
     * @return 结果
     */
    @Override
    public int deleteJstScoreRecordByScoreIds(Long[] scoreIds)
    {
        return jstScoreRecordMapper.deleteJstScoreRecordByScoreIds(scoreIds);
    }

    /**
     * 删除成绩记录信息
     * 
     * @param scoreId 成绩记录主键
     * @return 结果
     */
    @Override
    public int deleteJstScoreRecordByScoreId(Long scoreId)
    {
        return jstScoreRecordMapper.deleteJstScoreRecordByScoreId(scoreId);
    }

    private boolean shouldPublishScoreEvent(JstScoreRecord before, JstScoreRecord request) {
        if (before == null || request == null || request.getScoreId() == null) {
            return false;
        }
        if (!Objects.equals("published", request.getPublishStatus())) {
            return false;
        }
        return !Objects.equals("published", before.getPublishStatus());
    }

    private ScorePublishedEvent buildScorePublishedEvent(JstScoreRecord before, JstScoreRecord request) {
        Long userId = request.getUserId() == null ? before.getUserId() : request.getUserId();
        Long scoreId = request.getScoreId() == null ? before.getScoreId() : request.getScoreId();
        Map<String, Object> extraData = new LinkedHashMap<>();
        if (request.getScoreValue() != null) {
            extraData.put("scoreValue", request.getScoreValue());
        } else if (before.getScoreValue() != null) {
            extraData.put("scoreValue", before.getScoreValue());
        }
        if (request.getAwardLevel() != null) {
            extraData.put("awardLevel", request.getAwardLevel());
        } else if (before.getAwardLevel() != null) {
            extraData.put("awardLevel", before.getAwardLevel());
        }
        return new ScorePublishedEvent(this, userId, scoreId, "score_published", extraData);
    }

    private void publishAfterCommit(Object event) {
        if (event == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishEvent(event);
                }
            });
            return;
        }
        eventPublisher.publishEvent(event);
    }
}
