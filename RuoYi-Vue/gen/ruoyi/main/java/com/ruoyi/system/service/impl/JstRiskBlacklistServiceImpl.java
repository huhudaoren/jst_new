package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstRiskBlacklistMapper;
import com.ruoyi.system.domain.JstRiskBlacklist;
import com.ruoyi.system.service.IJstRiskBlacklistService;

/**
 * 风控黑白名单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRiskBlacklistServiceImpl implements IJstRiskBlacklistService 
{
    @Autowired
    private JstRiskBlacklistMapper jstRiskBlacklistMapper;

    /**
     * 查询风控黑白名单
     * 
     * @param listId 风控黑白名单主键
     * @return 风控黑白名单
     */
    @Override
    public JstRiskBlacklist selectJstRiskBlacklistByListId(Long listId)
    {
        return jstRiskBlacklistMapper.selectJstRiskBlacklistByListId(listId);
    }

    /**
     * 查询风控黑白名单列表
     * 
     * @param jstRiskBlacklist 风控黑白名单
     * @return 风控黑白名单
     */
    @Override
    public List<JstRiskBlacklist> selectJstRiskBlacklistList(JstRiskBlacklist jstRiskBlacklist)
    {
        return jstRiskBlacklistMapper.selectJstRiskBlacklistList(jstRiskBlacklist);
    }

    /**
     * 新增风控黑白名单
     * 
     * @param jstRiskBlacklist 风控黑白名单
     * @return 结果
     */
    @Override
    public int insertJstRiskBlacklist(JstRiskBlacklist jstRiskBlacklist)
    {
        jstRiskBlacklist.setCreateTime(DateUtils.getNowDate());
        return jstRiskBlacklistMapper.insertJstRiskBlacklist(jstRiskBlacklist);
    }

    /**
     * 修改风控黑白名单
     * 
     * @param jstRiskBlacklist 风控黑白名单
     * @return 结果
     */
    @Override
    public int updateJstRiskBlacklist(JstRiskBlacklist jstRiskBlacklist)
    {
        jstRiskBlacklist.setUpdateTime(DateUtils.getNowDate());
        return jstRiskBlacklistMapper.updateJstRiskBlacklist(jstRiskBlacklist);
    }

    /**
     * 批量删除风控黑白名单
     * 
     * @param listIds 需要删除的风控黑白名单主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskBlacklistByListIds(Long[] listIds)
    {
        return jstRiskBlacklistMapper.deleteJstRiskBlacklistByListIds(listIds);
    }

    /**
     * 删除风控黑白名单信息
     * 
     * @param listId 风控黑白名单主键
     * @return 结果
     */
    @Override
    public int deleteJstRiskBlacklistByListId(Long listId)
    {
        return jstRiskBlacklistMapper.deleteJstRiskBlacklistByListId(listId);
    }
}
