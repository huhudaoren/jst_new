package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstParticipantMapper;
import com.ruoyi.system.domain.JstParticipant;
import com.ruoyi.system.service.IJstParticipantService;

/**
 * 临时参赛档案-允许无正式账号存在Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstParticipantServiceImpl implements IJstParticipantService 
{
    @Autowired
    private JstParticipantMapper jstParticipantMapper;

    /**
     * 查询临时参赛档案-允许无正式账号存在
     * 
     * @param participantId 临时参赛档案-允许无正式账号存在主键
     * @return 临时参赛档案-允许无正式账号存在
     */
    @Override
    public JstParticipant selectJstParticipantByParticipantId(Long participantId)
    {
        return jstParticipantMapper.selectJstParticipantByParticipantId(participantId);
    }

    /**
     * 查询临时参赛档案-允许无正式账号存在列表
     * 
     * @param jstParticipant 临时参赛档案-允许无正式账号存在
     * @return 临时参赛档案-允许无正式账号存在
     */
    @Override
    public List<JstParticipant> selectJstParticipantList(JstParticipant jstParticipant)
    {
        return jstParticipantMapper.selectJstParticipantList(jstParticipant);
    }

    /**
     * 新增临时参赛档案-允许无正式账号存在
     * 
     * @param jstParticipant 临时参赛档案-允许无正式账号存在
     * @return 结果
     */
    @Override
    public int insertJstParticipant(JstParticipant jstParticipant)
    {
        jstParticipant.setCreateTime(DateUtils.getNowDate());
        return jstParticipantMapper.insertJstParticipant(jstParticipant);
    }

    /**
     * 修改临时参赛档案-允许无正式账号存在
     * 
     * @param jstParticipant 临时参赛档案-允许无正式账号存在
     * @return 结果
     */
    @Override
    public int updateJstParticipant(JstParticipant jstParticipant)
    {
        jstParticipant.setUpdateTime(DateUtils.getNowDate());
        return jstParticipantMapper.updateJstParticipant(jstParticipant);
    }

    /**
     * 批量删除临时参赛档案-允许无正式账号存在
     * 
     * @param participantIds 需要删除的临时参赛档案-允许无正式账号存在主键
     * @return 结果
     */
    @Override
    public int deleteJstParticipantByParticipantIds(Long[] participantIds)
    {
        return jstParticipantMapper.deleteJstParticipantByParticipantIds(participantIds);
    }

    /**
     * 删除临时参赛档案-允许无正式账号存在信息
     * 
     * @param participantId 临时参赛档案-允许无正式账号存在主键
     * @return 结果
     */
    @Override
    public int deleteJstParticipantByParticipantId(Long participantId)
    {
        return jstParticipantMapper.deleteJstParticipantByParticipantId(participantId);
    }
}
