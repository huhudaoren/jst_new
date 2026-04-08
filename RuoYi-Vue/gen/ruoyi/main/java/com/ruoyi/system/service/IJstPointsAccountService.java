package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstPointsAccount;

/**
 * 积分账户Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstPointsAccountService 
{
    /**
     * 查询积分账户
     * 
     * @param accountId 积分账户主键
     * @return 积分账户
     */
    public JstPointsAccount selectJstPointsAccountByAccountId(Long accountId);

    /**
     * 查询积分账户列表
     * 
     * @param jstPointsAccount 积分账户
     * @return 积分账户集合
     */
    public List<JstPointsAccount> selectJstPointsAccountList(JstPointsAccount jstPointsAccount);

    /**
     * 新增积分账户
     * 
     * @param jstPointsAccount 积分账户
     * @return 结果
     */
    public int insertJstPointsAccount(JstPointsAccount jstPointsAccount);

    /**
     * 修改积分账户
     * 
     * @param jstPointsAccount 积分账户
     * @return 结果
     */
    public int updateJstPointsAccount(JstPointsAccount jstPointsAccount);

    /**
     * 批量删除积分账户
     * 
     * @param accountIds 需要删除的积分账户主键集合
     * @return 结果
     */
    public int deleteJstPointsAccountByAccountIds(Long[] accountIds);

    /**
     * 删除积分账户信息
     * 
     * @param accountId 积分账户主键
     * @return 结果
     */
    public int deleteJstPointsAccountByAccountId(Long accountId);
}
