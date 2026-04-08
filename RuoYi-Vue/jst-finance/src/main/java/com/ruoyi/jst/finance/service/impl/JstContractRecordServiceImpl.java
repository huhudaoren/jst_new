package com.ruoyi.jst.finance.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.finance.mapper.JstContractRecordMapper;
import com.ruoyi.jst.finance.domain.JstContractRecord;
import com.ruoyi.jst.finance.service.IJstContractRecordService;

/**
 * 合同记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstContractRecordServiceImpl implements IJstContractRecordService 
{
    @Autowired
    private JstContractRecordMapper jstContractRecordMapper;

    /**
     * 查询合同记录
     * 
     * @param contractId 合同记录主键
     * @return 合同记录
     */
    @Override
    public JstContractRecord selectJstContractRecordByContractId(Long contractId)
    {
        return jstContractRecordMapper.selectJstContractRecordByContractId(contractId);
    }

    /**
     * 查询合同记录列表
     * 
     * @param jstContractRecord 合同记录
     * @return 合同记录
     */
    @Override
    public List<JstContractRecord> selectJstContractRecordList(JstContractRecord jstContractRecord)
    {
        return jstContractRecordMapper.selectJstContractRecordList(jstContractRecord);
    }

    /**
     * 新增合同记录
     * 
     * @param jstContractRecord 合同记录
     * @return 结果
     */
    @Override
    public int insertJstContractRecord(JstContractRecord jstContractRecord)
    {
        jstContractRecord.setCreateTime(DateUtils.getNowDate());
        return jstContractRecordMapper.insertJstContractRecord(jstContractRecord);
    }

    /**
     * 修改合同记录
     * 
     * @param jstContractRecord 合同记录
     * @return 结果
     */
    @Override
    public int updateJstContractRecord(JstContractRecord jstContractRecord)
    {
        jstContractRecord.setUpdateTime(DateUtils.getNowDate());
        return jstContractRecordMapper.updateJstContractRecord(jstContractRecord);
    }

    /**
     * 批量删除合同记录
     * 
     * @param contractIds 需要删除的合同记录主键
     * @return 结果
     */
    @Override
    public int deleteJstContractRecordByContractIds(Long[] contractIds)
    {
        return jstContractRecordMapper.deleteJstContractRecordByContractIds(contractIds);
    }

    /**
     * 删除合同记录信息
     * 
     * @param contractId 合同记录主键
     * @return 结果
     */
    @Override
    public int deleteJstContractRecordByContractId(Long contractId)
    {
        return jstContractRecordMapper.deleteJstContractRecordByContractId(contractId);
    }
}
