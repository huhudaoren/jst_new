package com.ruoyi.jst.event.service;

import java.util.List;
import com.ruoyi.jst.event.domain.JstContest;

/**
 * 赛事主Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstContestService 
{
    /**
     * 查询赛事主
     * 
     * @param contestId 赛事主主键
     * @return 赛事主
     */
    public JstContest selectJstContestByContestId(Long contestId);

    /**
     * 查询赛事主列表
     * 
     * @param jstContest 赛事主
     * @return 赛事主集合
     */
    public List<JstContest> selectJstContestList(JstContest jstContest);

    /**
     * 新增赛事主
     * 
     * @param jstContest 赛事主
     * @return 结果
     */
    public int insertJstContest(JstContest jstContest);

    /**
     * 修改赛事主
     * 
     * @param jstContest 赛事主
     * @return 结果
     */
    public int updateJstContest(JstContest jstContest);

    /**
     * 批量删除赛事主
     * 
     * @param contestIds 需要删除的赛事主主键集合
     * @return 结果
     */
    public int deleteJstContestByContestIds(Long[] contestIds);

    /**
     * 删除赛事主信息
     * 
     * @param contestId 赛事主主键
     * @return 结果
     */
    public int deleteJstContestByContestId(Long contestId);
}
