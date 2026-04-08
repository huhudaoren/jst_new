package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstContractRecord;

/**
 * 合同记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstContractRecordMapper 
{
    /**
     * 查询合同记录
     * 
     * @param contractId 合同记录主键
     * @return 合同记录
     */
    public JstContractRecord selectJstContractRecordByContractId(Long contractId);

    /**
     * 查询合同记录列表
     * 
     * @param jstContractRecord 合同记录
     * @return 合同记录集合
     */
    public List<JstContractRecord> selectJstContractRecordList(JstContractRecord jstContractRecord);

    /**
     * 新增合同记录
     * 
     * @param jstContractRecord 合同记录
     * @return 结果
     */
    public int insertJstContractRecord(JstContractRecord jstContractRecord);

    /**
     * 修改合同记录
     * 
     * @param jstContractRecord 合同记录
     * @return 结果
     */
    public int updateJstContractRecord(JstContractRecord jstContractRecord);

    /**
     * 删除合同记录
     * 
     * @param contractId 合同记录主键
     * @return 结果
     */
    public int deleteJstContractRecordByContractId(Long contractId);

    /**
     * 批量删除合同记录
     * 
     * @param contractIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstContractRecordByContractIds(Long[] contractIds);
}
