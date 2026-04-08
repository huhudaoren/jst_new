package com.ruoyi.jst.risk.mapper;

import java.util.List;
import com.ruoyi.jst.risk.domain.JstAuditLog;

/**
 * 操作审计日志Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstAuditLogMapper 
{
    /**
     * 查询操作审计日志
     * 
     * @param auditId 操作审计日志主键
     * @return 操作审计日志
     */
    public JstAuditLog selectJstAuditLogByAuditId(Long auditId);

    /**
     * 查询操作审计日志列表
     * 
     * @param jstAuditLog 操作审计日志
     * @return 操作审计日志集合
     */
    public List<JstAuditLog> selectJstAuditLogList(JstAuditLog jstAuditLog);

    /**
     * 新增操作审计日志
     * 
     * @param jstAuditLog 操作审计日志
     * @return 结果
     */
    public int insertJstAuditLog(JstAuditLog jstAuditLog);

    /**
     * 修改操作审计日志
     * 
     * @param jstAuditLog 操作审计日志
     * @return 结果
     */
    public int updateJstAuditLog(JstAuditLog jstAuditLog);

    /**
     * 删除操作审计日志
     * 
     * @param auditId 操作审计日志主键
     * @return 结果
     */
    public int deleteJstAuditLogByAuditId(Long auditId);

    /**
     * 批量删除操作审计日志
     * 
     * @param auditIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstAuditLogByAuditIds(Long[] auditIds);
}
