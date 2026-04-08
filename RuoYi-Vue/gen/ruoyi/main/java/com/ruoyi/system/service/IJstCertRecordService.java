package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstCertRecord;

/**
 * 证书记录Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstCertRecordService 
{
    /**
     * 查询证书记录
     * 
     * @param certId 证书记录主键
     * @return 证书记录
     */
    public JstCertRecord selectJstCertRecordByCertId(Long certId);

    /**
     * 查询证书记录列表
     * 
     * @param jstCertRecord 证书记录
     * @return 证书记录集合
     */
    public List<JstCertRecord> selectJstCertRecordList(JstCertRecord jstCertRecord);

    /**
     * 新增证书记录
     * 
     * @param jstCertRecord 证书记录
     * @return 结果
     */
    public int insertJstCertRecord(JstCertRecord jstCertRecord);

    /**
     * 修改证书记录
     * 
     * @param jstCertRecord 证书记录
     * @return 结果
     */
    public int updateJstCertRecord(JstCertRecord jstCertRecord);

    /**
     * 批量删除证书记录
     * 
     * @param certIds 需要删除的证书记录主键集合
     * @return 结果
     */
    public int deleteJstCertRecordByCertIds(Long[] certIds);

    /**
     * 删除证书记录信息
     * 
     * @param certId 证书记录主键
     * @return 结果
     */
    public int deleteJstCertRecordByCertId(Long certId);
}
