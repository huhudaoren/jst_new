package com.ruoyi.jst.risk.mapper;

import java.util.List;
import com.ruoyi.jst.risk.domain.JstRiskBlacklist;

/**
 * 风控黑白名单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstRiskBlacklistMapper 
{
    /**
     * 查询风控黑白名单
     * 
     * @param listId 风控黑白名单主键
     * @return 风控黑白名单
     */
    public JstRiskBlacklist selectJstRiskBlacklistByListId(Long listId);

    /**
     * 查询风控黑白名单列表
     * 
     * @param jstRiskBlacklist 风控黑白名单
     * @return 风控黑白名单集合
     */
    public List<JstRiskBlacklist> selectJstRiskBlacklistList(JstRiskBlacklist jstRiskBlacklist);

    /**
     * 新增风控黑白名单
     * 
     * @param jstRiskBlacklist 风控黑白名单
     * @return 结果
     */
    public int insertJstRiskBlacklist(JstRiskBlacklist jstRiskBlacklist);

    /**
     * 修改风控黑白名单
     * 
     * @param jstRiskBlacklist 风控黑白名单
     * @return 结果
     */
    public int updateJstRiskBlacklist(JstRiskBlacklist jstRiskBlacklist);

    /**
     * 删除风控黑白名单
     * 
     * @param listId 风控黑白名单主键
     * @return 结果
     */
    public int deleteJstRiskBlacklistByListId(Long listId);

    /**
     * 批量删除风控黑白名单
     * 
     * @param listIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstRiskBlacklistByListIds(Long[] listIds);
}
