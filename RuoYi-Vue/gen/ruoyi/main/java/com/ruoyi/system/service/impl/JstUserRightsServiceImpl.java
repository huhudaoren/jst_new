package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstUserRightsMapper;
import com.ruoyi.system.domain.JstUserRights;
import com.ruoyi.system.service.IJstUserRightsService;

/**
 * 用户权益持有Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstUserRightsServiceImpl implements IJstUserRightsService 
{
    @Autowired
    private JstUserRightsMapper jstUserRightsMapper;

    /**
     * 查询用户权益持有
     * 
     * @param userRightsId 用户权益持有主键
     * @return 用户权益持有
     */
    @Override
    public JstUserRights selectJstUserRightsByUserRightsId(Long userRightsId)
    {
        return jstUserRightsMapper.selectJstUserRightsByUserRightsId(userRightsId);
    }

    /**
     * 查询用户权益持有列表
     * 
     * @param jstUserRights 用户权益持有
     * @return 用户权益持有
     */
    @Override
    public List<JstUserRights> selectJstUserRightsList(JstUserRights jstUserRights)
    {
        return jstUserRightsMapper.selectJstUserRightsList(jstUserRights);
    }

    /**
     * 新增用户权益持有
     * 
     * @param jstUserRights 用户权益持有
     * @return 结果
     */
    @Override
    public int insertJstUserRights(JstUserRights jstUserRights)
    {
        jstUserRights.setCreateTime(DateUtils.getNowDate());
        return jstUserRightsMapper.insertJstUserRights(jstUserRights);
    }

    /**
     * 修改用户权益持有
     * 
     * @param jstUserRights 用户权益持有
     * @return 结果
     */
    @Override
    public int updateJstUserRights(JstUserRights jstUserRights)
    {
        jstUserRights.setUpdateTime(DateUtils.getNowDate());
        return jstUserRightsMapper.updateJstUserRights(jstUserRights);
    }

    /**
     * 批量删除用户权益持有
     * 
     * @param userRightsIds 需要删除的用户权益持有主键
     * @return 结果
     */
    @Override
    public int deleteJstUserRightsByUserRightsIds(Long[] userRightsIds)
    {
        return jstUserRightsMapper.deleteJstUserRightsByUserRightsIds(userRightsIds);
    }

    /**
     * 删除用户权益持有信息
     * 
     * @param userRightsId 用户权益持有主键
     * @return 结果
     */
    @Override
    public int deleteJstUserRightsByUserRightsId(Long userRightsId)
    {
        return jstUserRightsMapper.deleteJstUserRightsByUserRightsId(userRightsId);
    }
}
