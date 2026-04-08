package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstEnrollRecordMapper;
import com.ruoyi.system.domain.JstEnrollRecord;
import com.ruoyi.system.service.IJstEnrollRecordService;

/**
 * 报名记录（含动态单快照）Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstEnrollRecordServiceImpl implements IJstEnrollRecordService 
{
    @Autowired
    private JstEnrollRecordMapper jstEnrollRecordMapper;

    /**
     * 查询报名记录（含动态单快照）
     * 
     * @param enrollId 报名记录（含动态单快照）主键
     * @return 报名记录（含动态单快照）
     */
    @Override
    public JstEnrollRecord selectJstEnrollRecordByEnrollId(Long enrollId)
    {
        return jstEnrollRecordMapper.selectJstEnrollRecordByEnrollId(enrollId);
    }

    /**
     * 查询报名记录（含动态单快照）列表
     * 
     * @param jstEnrollRecord 报名记录（含动态单快照）
     * @return 报名记录（含动态单快照）
     */
    @Override
    public List<JstEnrollRecord> selectJstEnrollRecordList(JstEnrollRecord jstEnrollRecord)
    {
        return jstEnrollRecordMapper.selectJstEnrollRecordList(jstEnrollRecord);
    }

    /**
     * 新增报名记录（含动态单快照）
     * 
     * @param jstEnrollRecord 报名记录（含动态单快照）
     * @return 结果
     */
    @Override
    public int insertJstEnrollRecord(JstEnrollRecord jstEnrollRecord)
    {
        jstEnrollRecord.setCreateTime(DateUtils.getNowDate());
        return jstEnrollRecordMapper.insertJstEnrollRecord(jstEnrollRecord);
    }

    /**
     * 修改报名记录（含动态单快照）
     * 
     * @param jstEnrollRecord 报名记录（含动态单快照）
     * @return 结果
     */
    @Override
    public int updateJstEnrollRecord(JstEnrollRecord jstEnrollRecord)
    {
        jstEnrollRecord.setUpdateTime(DateUtils.getNowDate());
        return jstEnrollRecordMapper.updateJstEnrollRecord(jstEnrollRecord);
    }

    /**
     * 批量删除报名记录（含动态单快照）
     * 
     * @param enrollIds 需要删除的报名记录（含动态单快照）主键
     * @return 结果
     */
    @Override
    public int deleteJstEnrollRecordByEnrollIds(Long[] enrollIds)
    {
        return jstEnrollRecordMapper.deleteJstEnrollRecordByEnrollIds(enrollIds);
    }

    /**
     * 删除报名记录（含动态单快照）信息
     * 
     * @param enrollId 报名记录（含动态单快照）主键
     * @return 结果
     */
    @Override
    public int deleteJstEnrollRecordByEnrollId(Long enrollId)
    {
        return jstEnrollRecordMapper.deleteJstEnrollRecordByEnrollId(enrollId);
    }
}
