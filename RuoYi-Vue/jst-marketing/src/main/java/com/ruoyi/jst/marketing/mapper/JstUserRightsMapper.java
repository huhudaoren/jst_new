package com.ruoyi.jst.marketing.mapper;

import java.util.List;
import com.ruoyi.jst.marketing.domain.JstUserRights;

/**
 * 用户权益持有Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstUserRightsMapper 
{
    /**
     * 查询用户权益持有
     * 
     * @param userRightsId 用户权益持有主键
     * @return 用户权益持有
     */
    public JstUserRights selectJstUserRightsByUserRightsId(Long userRightsId);

    /**
     * 查询用户权益持有列表
     * 
     * @param jstUserRights 用户权益持有
     * @return 用户权益持有集合
     */
    public List<JstUserRights> selectJstUserRightsList(JstUserRights jstUserRights);

    /**
     * 新增用户权益持有
     * 
     * @param jstUserRights 用户权益持有
     * @return 结果
     */
    public int insertJstUserRights(JstUserRights jstUserRights);

    /**
     * 修改用户权益持有
     * 
     * @param jstUserRights 用户权益持有
     * @return 结果
     */
    public int updateJstUserRights(JstUserRights jstUserRights);

    /**
     * 删除用户权益持有
     * 
     * @param userRightsId 用户权益持有主键
     * @return 结果
     */
    public int deleteJstUserRightsByUserRightsId(Long userRightsId);

    /**
     * 批量删除用户权益持有
     * 
     * @param userRightsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstUserRightsByUserRightsIds(Long[] userRightsIds);
}
