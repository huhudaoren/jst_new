package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstUserMapper;
import com.ruoyi.system.domain.JstUser;
import com.ruoyi.system.service.IJstUserService;

/**
 * 用户主-学生/家长/渠道方业务账号Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstUserServiceImpl implements IJstUserService 
{
    @Autowired
    private JstUserMapper jstUserMapper;

    /**
     * 查询用户主-学生/家长/渠道方业务账号
     * 
     * @param userId 用户主-学生/家长/渠道方业务账号主键
     * @return 用户主-学生/家长/渠道方业务账号
     */
    @Override
    public JstUser selectJstUserByUserId(Long userId)
    {
        return jstUserMapper.selectJstUserByUserId(userId);
    }

    /**
     * 查询用户主-学生/家长/渠道方业务账号列表
     * 
     * @param jstUser 用户主-学生/家长/渠道方业务账号
     * @return 用户主-学生/家长/渠道方业务账号
     */
    @Override
    public List<JstUser> selectJstUserList(JstUser jstUser)
    {
        return jstUserMapper.selectJstUserList(jstUser);
    }

    /**
     * 新增用户主-学生/家长/渠道方业务账号
     * 
     * @param jstUser 用户主-学生/家长/渠道方业务账号
     * @return 结果
     */
    @Override
    public int insertJstUser(JstUser jstUser)
    {
        jstUser.setCreateTime(DateUtils.getNowDate());
        return jstUserMapper.insertJstUser(jstUser);
    }

    /**
     * 修改用户主-学生/家长/渠道方业务账号
     * 
     * @param jstUser 用户主-学生/家长/渠道方业务账号
     * @return 结果
     */
    @Override
    public int updateJstUser(JstUser jstUser)
    {
        jstUser.setUpdateTime(DateUtils.getNowDate());
        return jstUserMapper.updateJstUser(jstUser);
    }

    /**
     * 批量删除用户主-学生/家长/渠道方业务账号
     * 
     * @param userIds 需要删除的用户主-学生/家长/渠道方业务账号主键
     * @return 结果
     */
    @Override
    public int deleteJstUserByUserIds(Long[] userIds)
    {
        return jstUserMapper.deleteJstUserByUserIds(userIds);
    }

    /**
     * 删除用户主-学生/家长/渠道方业务账号信息
     * 
     * @param userId 用户主-学生/家长/渠道方业务账号主键
     * @return 结果
     */
    @Override
    public int deleteJstUserByUserId(Long userId)
    {
        return jstUserMapper.deleteJstUserByUserId(userId);
    }
}
