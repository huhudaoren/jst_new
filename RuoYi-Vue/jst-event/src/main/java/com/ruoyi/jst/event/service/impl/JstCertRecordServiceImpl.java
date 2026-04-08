package com.ruoyi.jst.event.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.event.mapper.JstCertRecordMapper;
import com.ruoyi.jst.event.domain.JstCertRecord;
import com.ruoyi.jst.event.service.IJstCertRecordService;

/**
 * 证书记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstCertRecordServiceImpl implements IJstCertRecordService 
{
    @Autowired
    private JstCertRecordMapper jstCertRecordMapper;

    /**
     * 查询证书记录
     * 
     * @param certId 证书记录主键
     * @return 证书记录
     */
    @Override
    public JstCertRecord selectJstCertRecordByCertId(Long certId)
    {
        return jstCertRecordMapper.selectJstCertRecordByCertId(certId);
    }

    /**
     * 查询证书记录列表
     * 
     * @param jstCertRecord 证书记录
     * @return 证书记录
     */
    @Override
    public List<JstCertRecord> selectJstCertRecordList(JstCertRecord jstCertRecord)
    {
        return jstCertRecordMapper.selectJstCertRecordList(jstCertRecord);
    }

    /**
     * 新增证书记录
     * 
     * @param jstCertRecord 证书记录
     * @return 结果
     */
    @Override
    public int insertJstCertRecord(JstCertRecord jstCertRecord)
    {
        jstCertRecord.setCreateTime(DateUtils.getNowDate());
        return jstCertRecordMapper.insertJstCertRecord(jstCertRecord);
    }

    /**
     * 修改证书记录
     * 
     * @param jstCertRecord 证书记录
     * @return 结果
     */
    @Override
    public int updateJstCertRecord(JstCertRecord jstCertRecord)
    {
        jstCertRecord.setUpdateTime(DateUtils.getNowDate());
        return jstCertRecordMapper.updateJstCertRecord(jstCertRecord);
    }

    /**
     * 批量删除证书记录
     * 
     * @param certIds 需要删除的证书记录主键
     * @return 结果
     */
    @Override
    public int deleteJstCertRecordByCertIds(Long[] certIds)
    {
        return jstCertRecordMapper.deleteJstCertRecordByCertIds(certIds);
    }

    /**
     * 删除证书记录信息
     * 
     * @param certId 证书记录主键
     * @return 结果
     */
    @Override
    public int deleteJstCertRecordByCertId(Long certId)
    {
        return jstCertRecordMapper.deleteJstCertRecordByCertId(certId);
    }
}
