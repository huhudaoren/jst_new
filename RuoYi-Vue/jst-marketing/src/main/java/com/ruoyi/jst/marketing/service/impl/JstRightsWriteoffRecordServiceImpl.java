package com.ruoyi.jst.marketing.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.marketing.mapper.JstRightsWriteoffRecordMapper;
import com.ruoyi.jst.marketing.domain.JstRightsWriteoffRecord;
import com.ruoyi.jst.marketing.service.IJstRightsWriteoffRecordService;

/**
 * 权益核销记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRightsWriteoffRecordServiceImpl implements IJstRightsWriteoffRecordService 
{
    @Autowired
    private JstRightsWriteoffRecordMapper jstRightsWriteoffRecordMapper;

    /**
     * 查询权益核销记录
     * 
     * @param recordId 权益核销记录主键
     * @return 权益核销记录
     */
    @Override
    public JstRightsWriteoffRecord selectJstRightsWriteoffRecordByRecordId(Long recordId)
    {
        return jstRightsWriteoffRecordMapper.selectJstRightsWriteoffRecordByRecordId(recordId);
    }

    /**
     * 查询权益核销记录列表
     * 
     * @param jstRightsWriteoffRecord 权益核销记录
     * @return 权益核销记录
     */
    @Override
    public List<JstRightsWriteoffRecord> selectJstRightsWriteoffRecordList(JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        return jstRightsWriteoffRecordMapper.selectJstRightsWriteoffRecordList(jstRightsWriteoffRecord);
    }

    /**
     * 新增权益核销记录
     * 
     * @param jstRightsWriteoffRecord 权益核销记录
     * @return 结果
     */
    @Override
    public int insertJstRightsWriteoffRecord(JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        jstRightsWriteoffRecord.setCreateTime(DateUtils.getNowDate());
        return jstRightsWriteoffRecordMapper.insertJstRightsWriteoffRecord(jstRightsWriteoffRecord);
    }

    /**
     * 修改权益核销记录
     * 
     * @param jstRightsWriteoffRecord 权益核销记录
     * @return 结果
     */
    @Override
    public int updateJstRightsWriteoffRecord(JstRightsWriteoffRecord jstRightsWriteoffRecord)
    {
        jstRightsWriteoffRecord.setUpdateTime(DateUtils.getNowDate());
        return jstRightsWriteoffRecordMapper.updateJstRightsWriteoffRecord(jstRightsWriteoffRecord);
    }

    /**
     * 批量删除权益核销记录
     * 
     * @param recordIds 需要删除的权益核销记录主键
     * @return 结果
     */
    @Override
    public int deleteJstRightsWriteoffRecordByRecordIds(Long[] recordIds)
    {
        return jstRightsWriteoffRecordMapper.deleteJstRightsWriteoffRecordByRecordIds(recordIds);
    }

    /**
     * 删除权益核销记录信息
     * 
     * @param recordId 权益核销记录主键
     * @return 结果
     */
    @Override
    public int deleteJstRightsWriteoffRecordByRecordId(Long recordId)
    {
        return jstRightsWriteoffRecordMapper.deleteJstRightsWriteoffRecordByRecordId(recordId);
    }
}
