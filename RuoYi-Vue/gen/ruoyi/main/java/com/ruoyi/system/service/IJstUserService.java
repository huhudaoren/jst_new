package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstUser;

/**
 * 用户主-学生/家长/渠道方业务账号Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstUserService 
{
    /**
     * 查询用户主-学生/家长/渠道方业务账号
     * 
     * @param userId 用户主-学生/家长/渠道方业务账号主键
     * @return 用户主-学生/家长/渠道方业务账号
     */
    public JstUser selectJstUserByUserId(Long userId);

    /**
     * 查询用户主-学生/家长/渠道方业务账号列表
     * 
     * @param jstUser 用户主-学生/家长/渠道方业务账号
     * @return 用户主-学生/家长/渠道方业务账号集合
     */
    public List<JstUser> selectJstUserList(JstUser jstUser);

    /**
     * 新增用户主-学生/家长/渠道方业务账号
     * 
     * @param jstUser 用户主-学生/家长/渠道方业务账号
     * @return 结果
     */
    public int insertJstUser(JstUser jstUser);

    /**
     * 修改用户主-学生/家长/渠道方业务账号
     * 
     * @param jstUser 用户主-学生/家长/渠道方业务账号
     * @return 结果
     */
    public int updateJstUser(JstUser jstUser);

    /**
     * 批量删除用户主-学生/家长/渠道方业务账号
     * 
     * @param userIds 需要删除的用户主-学生/家长/渠道方业务账号主键集合
     * @return 结果
     */
    public int deleteJstUserByUserIds(Long[] userIds);

    /**
     * 删除用户主-学生/家长/渠道方业务账号信息
     * 
     * @param userId 用户主-学生/家长/渠道方业务账号主键
     * @return 结果
     */
    public int deleteJstUserByUserId(Long userId);
}
