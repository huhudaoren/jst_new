package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstScoreRecordMapper;
import com.ruoyi.system.domain.JstScoreRecord;
import com.ruoyi.system.service.IJstScoreRecordService;

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
        jstScoreRecord.setUpdateTime(DateUtils.getNowDate());
        return jstScoreRecordMapper.updateJstScoreRecord(jstScoreRecord);
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
}
