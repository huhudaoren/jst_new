package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstAuditLogMapper;
import com.ruoyi.system.domain.JstAuditLog;
import com.ruoyi.system.service.IJstAuditLogService;

/**
 * 操作审计日志Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstAuditLogServiceImpl implements IJstAuditLogService 
{
    @Autowired
    private JstAuditLogMapper jstAuditLogMapper;

    /**
     * 查询操作审计日志
     * 
     * @param auditId 操作审计日志主键
     * @return 操作审计日志
     */
    @Override
    public JstAuditLog selectJstAuditLogByAuditId(Long auditId)
    {
        return jstAuditLogMapper.selectJstAuditLogByAuditId(auditId);
    }

    /**
     * 查询操作审计日志列表
     * 
     * @param jstAuditLog 操作审计日志
     * @return 操作审计日志
     */
    @Override
    public List<JstAuditLog> selectJstAuditLogList(JstAuditLog jstAuditLog)
    {
        return jstAuditLogMapper.selectJstAuditLogList(jstAuditLog);
    }

    /**
     * 新增操作审计日志
     * 
     * @param jstAuditLog 操作审计日志
     * @return 结果
     */
    @Override
    public int insertJstAuditLog(JstAuditLog jstAuditLog)
    {
        jstAuditLog.setCreateTime(DateUtils.getNowDate());
        return jstAuditLogMapper.insertJstAuditLog(jstAuditLog);
    }

    /**
     * 修改操作审计日志
     * 
     * @param jstAuditLog 操作审计日志
     * @return 结果
     */
    @Override
    public int updateJstAuditLog(JstAuditLog jstAuditLog)
    {
        jstAuditLog.setUpdateTime(DateUtils.getNowDate());
        return jstAuditLogMapper.updateJstAuditLog(jstAuditLog);
    }

    /**
     * 批量删除操作审计日志
     * 
     * @param auditIds 需要删除的操作审计日志主键
     * @return 结果
     */
    @Override
    public int deleteJstAuditLogByAuditIds(Long[] auditIds)
    {
        return jstAuditLogMapper.deleteJstAuditLogByAuditIds(auditIds);
    }

    /**
     * 删除操作审计日志信息
     * 
     * @param auditId 操作审计日志主键
     * @return 结果
     */
    @Override
    public int deleteJstAuditLogByAuditId(Long auditId)
    {
        return jstAuditLogMapper.deleteJstAuditLogByAuditId(auditId);
    }
}
