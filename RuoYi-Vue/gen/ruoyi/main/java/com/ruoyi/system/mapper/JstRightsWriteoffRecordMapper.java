package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstRightsWriteoffRecord;

/**
 * 权益核销记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstRightsWriteoffRecordMapper 
{
    /**
     * 查询权益核销记录
     * 
     * @param recordId 权益核销记录主键
     * @return 权益核销记录
     */
    public JstRightsWriteoffRecord selectJstRightsWriteoffRecordByRecordId(Long recordId);

    /**
     * 查询权益核销记录列表
     * 
     * @param jstRightsWriteoffRecord 权益核销记录
     * @return 权益核销记录集合
     */
    public List<JstRightsWriteoffRecord> selectJstRightsWriteoffRecordList(JstRightsWriteoffRecord jstRightsWriteoffRecord);

    /**
     * 新增权益核销记录
     * 
     * @param jstRightsWriteoffRecord 权益核销记录
     * @return 结果
     */
    public int insertJstRightsWriteoffRecord(JstRightsWriteoffRecord jstRightsWriteoffRecord);

    /**
     * 修改权益核销记录
     * 
     * @param jstRightsWriteoffRecord 权益核销记录
     * @return 结果
     */
    public int updateJstRightsWriteoffRecord(JstRightsWriteoffRecord jstRightsWriteoffRecord);

    /**
     * 删除权益核销记录
     * 
     * @param recordId 权益核销记录主键
     * @return 结果
     */
    public int deleteJstRightsWriteoffRecordByRecordId(Long recordId);

    /**
     * 批量删除权益核销记录
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstRightsWriteoffRecordByRecordIds(Long[] recordIds);
}
