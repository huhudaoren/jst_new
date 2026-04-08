package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstMessageLog;

/**
 * 消息发送日志Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstMessageLogService 
{
    /**
     * 查询消息发送日志
     * 
     * @param logId 消息发送日志主键
     * @return 消息发送日志
     */
    public JstMessageLog selectJstMessageLogByLogId(Long logId);

    /**
     * 查询消息发送日志列表
     * 
     * @param jstMessageLog 消息发送日志
     * @return 消息发送日志集合
     */
    public List<JstMessageLog> selectJstMessageLogList(JstMessageLog jstMessageLog);

    /**
     * 新增消息发送日志
     * 
     * @param jstMessageLog 消息发送日志
     * @return 结果
     */
    public int insertJstMessageLog(JstMessageLog jstMessageLog);

    /**
     * 修改消息发送日志
     * 
     * @param jstMessageLog 消息发送日志
     * @return 结果
     */
    public int updateJstMessageLog(JstMessageLog jstMessageLog);

    /**
     * 批量删除消息发送日志
     * 
     * @param logIds 需要删除的消息发送日志主键集合
     * @return 结果
     */
    public int deleteJstMessageLogByLogIds(Long[] logIds);

    /**
     * 删除消息发送日志信息
     * 
     * @param logId 消息发送日志主键
     * @return 结果
     */
    public int deleteJstMessageLogByLogId(Long logId);
}
