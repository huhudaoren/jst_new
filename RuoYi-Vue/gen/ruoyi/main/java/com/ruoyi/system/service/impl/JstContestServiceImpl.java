package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstContestMapper;
import com.ruoyi.system.domain.JstContest;
import com.ruoyi.system.service.IJstContestService;

/**
 * 赛事主Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstContestServiceImpl implements IJstContestService 
{
    @Autowired
    private JstContestMapper jstContestMapper;

    /**
     * 查询赛事主
     * 
     * @param contestId 赛事主主键
     * @return 赛事主
     */
    @Override
    public JstContest selectJstContestByContestId(Long contestId)
    {
        return jstContestMapper.selectJstContestByContestId(contestId);
    }

    /**
     * 查询赛事主列表
     * 
     * @param jstContest 赛事主
     * @return 赛事主
     */
    @Override
    public List<JstContest> selectJstContestList(JstContest jstContest)
    {
        return jstContestMapper.selectJstContestList(jstContest);
    }

    /**
     * 新增赛事主
     * 
     * @param jstContest 赛事主
     * @return 结果
     */
    @Override
    public int insertJstContest(JstContest jstContest)
    {
        jstContest.setCreateTime(DateUtils.getNowDate());
        return jstContestMapper.insertJstContest(jstContest);
    }

    /**
     * 修改赛事主
     * 
     * @param jstContest 赛事主
     * @return 结果
     */
    @Override
    public int updateJstContest(JstContest jstContest)
    {
        jstContest.setUpdateTime(DateUtils.getNowDate());
        return jstContestMapper.updateJstContest(jstContest);
    }

    /**
     * 批量删除赛事主
     * 
     * @param contestIds 需要删除的赛事主主键
     * @return 结果
     */
    @Override
    public int deleteJstContestByContestIds(Long[] contestIds)
    {
        return jstContestMapper.deleteJstContestByContestIds(contestIds);
    }

    /**
     * 删除赛事主信息
     * 
     * @param contestId 赛事主主键
     * @return 结果
     */
    @Override
    public int deleteJstContestByContestId(Long contestId)
    {
        return jstContestMapper.deleteJstContestByContestId(contestId);
    }
}
