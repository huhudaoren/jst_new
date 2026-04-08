package com.ruoyi.jst.message.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.message.mapper.JstMessageLogMapper;
import com.ruoyi.jst.message.domain.JstMessageLog;
import com.ruoyi.jst.message.service.IJstMessageLogService;

/**
 * 消息发送日志Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstMessageLogServiceImpl implements IJstMessageLogService 
{
    @Autowired
    private JstMessageLogMapper jstMessageLogMapper;

    /**
     * 查询消息发送日志
     * 
     * @param logId 消息发送日志主键
     * @return 消息发送日志
     */
    @Override
    public JstMessageLog selectJstMessageLogByLogId(Long logId)
    {
        return jstMessageLogMapper.selectJstMessageLogByLogId(logId);
    }

    /**
     * 查询消息发送日志列表
     * 
     * @param jstMessageLog 消息发送日志
     * @return 消息发送日志
     */
    @Override
    public List<JstMessageLog> selectJstMessageLogList(JstMessageLog jstMessageLog)
    {
        return jstMessageLogMapper.selectJstMessageLogList(jstMessageLog);
    }

    /**
     * 新增消息发送日志
     * 
     * @param jstMessageLog 消息发送日志
     * @return 结果
     */
    @Override
    public int insertJstMessageLog(JstMessageLog jstMessageLog)
    {
        jstMessageLog.setCreateTime(DateUtils.getNowDate());
        return jstMessageLogMapper.insertJstMessageLog(jstMessageLog);
    }

    /**
     * 修改消息发送日志
     * 
     * @param jstMessageLog 消息发送日志
     * @return 结果
     */
    @Override
    public int updateJstMessageLog(JstMessageLog jstMessageLog)
    {
        jstMessageLog.setUpdateTime(DateUtils.getNowDate());
        return jstMessageLogMapper.updateJstMessageLog(jstMessageLog);
    }

    /**
     * 批量删除消息发送日志
     * 
     * @param logIds 需要删除的消息发送日志主键
     * @return 结果
     */
    @Override
    public int deleteJstMessageLogByLogIds(Long[] logIds)
    {
        return jstMessageLogMapper.deleteJstMessageLogByLogIds(logIds);
    }

    /**
     * 删除消息发送日志信息
     * 
     * @param logId 消息发送日志主键
     * @return 结果
     */
    @Override
    public int deleteJstMessageLogByLogId(Long logId)
    {
        return jstMessageLogMapper.deleteJstMessageLogByLogId(logId);
    }
}
