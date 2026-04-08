package com.ruoyi.jst.event.mapper;

import java.util.List;
import com.ruoyi.jst.event.domain.JstScoreRecord;

/**
 * 成绩记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstScoreRecordMapper 
{
    /**
     * 查询成绩记录
     * 
     * @param scoreId 成绩记录主键
     * @return 成绩记录
     */
    public JstScoreRecord selectJstScoreRecordByScoreId(Long scoreId);

    /**
     * 查询成绩记录列表
     * 
     * @param jstScoreRecord 成绩记录
     * @return 成绩记录集合
     */
    public List<JstScoreRecord> selectJstScoreRecordList(JstScoreRecord jstScoreRecord);

    /**
     * 新增成绩记录
     * 
     * @param jstScoreRecord 成绩记录
     * @return 结果
     */
    public int insertJstScoreRecord(JstScoreRecord jstScoreRecord);

    /**
     * 修改成绩记录
     * 
     * @param jstScoreRecord 成绩记录
     * @return 结果
     */
    public int updateJstScoreRecord(JstScoreRecord jstScoreRecord);

    /**
     * 删除成绩记录
     * 
     * @param scoreId 成绩记录主键
     * @return 结果
     */
    public int deleteJstScoreRecordByScoreId(Long scoreId);

    /**
     * 批量删除成绩记录
     * 
     * @param scoreIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstScoreRecordByScoreIds(Long[] scoreIds);
}
