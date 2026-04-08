package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstEnrollRecord;

/**
 * 报名记录（含动态单快照）Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstEnrollRecordService 
{
    /**
     * 查询报名记录（含动态单快照）
     * 
     * @param enrollId 报名记录（含动态单快照）主键
     * @return 报名记录（含动态单快照）
     */
    public JstEnrollRecord selectJstEnrollRecordByEnrollId(Long enrollId);

    /**
     * 查询报名记录（含动态单快照）列表
     * 
     * @param jstEnrollRecord 报名记录（含动态单快照）
     * @return 报名记录（含动态单快照）集合
     */
    public List<JstEnrollRecord> selectJstEnrollRecordList(JstEnrollRecord jstEnrollRecord);

    /**
     * 新增报名记录（含动态单快照）
     * 
     * @param jstEnrollRecord 报名记录（含动态单快照）
     * @return 结果
     */
    public int insertJstEnrollRecord(JstEnrollRecord jstEnrollRecord);

    /**
     * 修改报名记录（含动态单快照）
     * 
     * @param jstEnrollRecord 报名记录（含动态单快照）
     * @return 结果
     */
    public int updateJstEnrollRecord(JstEnrollRecord jstEnrollRecord);

    /**
     * 批量删除报名记录（含动态单快照）
     * 
     * @param enrollIds 需要删除的报名记录（含动态单快照）主键集合
     * @return 结果
     */
    public int deleteJstEnrollRecordByEnrollIds(Long[] enrollIds);

    /**
     * 删除报名记录（含动态单快照）信息
     * 
     * @param enrollId 报名记录（含动态单快照）主键
     * @return 结果
     */
    public int deleteJstEnrollRecordByEnrollId(Long enrollId);
}
