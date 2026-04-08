package com.ruoyi.jst.points.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.points.mapper.JstPointsAccountMapper;
import com.ruoyi.jst.points.domain.JstPointsAccount;
import com.ruoyi.jst.points.service.IJstPointsAccountService;

/**
 * 积分账户Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstPointsAccountServiceImpl implements IJstPointsAccountService 
{
    @Autowired
    private JstPointsAccountMapper jstPointsAccountMapper;

    /**
     * 查询积分账户
     * 
     * @param accountId 积分账户主键
     * @return 积分账户
     */
    @Override
    public JstPointsAccount selectJstPointsAccountByAccountId(Long accountId)
    {
        return jstPointsAccountMapper.selectJstPointsAccountByAccountId(accountId);
    }

    /**
     * 查询积分账户列表
     * 
     * @param jstPointsAccount 积分账户
     * @return 积分账户
     */
    @Override
    public List<JstPointsAccount> selectJstPointsAccountList(JstPointsAccount jstPointsAccount)
    {
        return jstPointsAccountMapper.selectJstPointsAccountList(jstPointsAccount);
    }

    /**
     * 新增积分账户
     * 
     * @param jstPointsAccount 积分账户
     * @return 结果
     */
    @Override
    public int insertJstPointsAccount(JstPointsAccount jstPointsAccount)
    {
        jstPointsAccount.setCreateTime(DateUtils.getNowDate());
        return jstPointsAccountMapper.insertJstPointsAccount(jstPointsAccount);
    }

    /**
     * 修改积分账户
     * 
     * @param jstPointsAccount 积分账户
     * @return 结果
     */
    @Override
    public int updateJstPointsAccount(JstPointsAccount jstPointsAccount)
    {
        jstPointsAccount.setUpdateTime(DateUtils.getNowDate());
        return jstPointsAccountMapper.updateJstPointsAccount(jstPointsAccount);
    }

    /**
     * 批量删除积分账户
     * 
     * @param accountIds 需要删除的积分账户主键
     * @return 结果
     */
    @Override
    public int deleteJstPointsAccountByAccountIds(Long[] accountIds)
    {
        return jstPointsAccountMapper.deleteJstPointsAccountByAccountIds(accountIds);
    }

    /**
     * 删除积分账户信息
     * 
     * @param accountId 积分账户主键
     * @return 结果
     */
    @Override
    public int deleteJstPointsAccountByAccountId(Long accountId)
    {
        return jstPointsAccountMapper.deleteJstPointsAccountByAccountId(accountId);
    }
}
