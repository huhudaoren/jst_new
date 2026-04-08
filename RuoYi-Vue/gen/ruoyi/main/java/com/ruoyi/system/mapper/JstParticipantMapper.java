package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstParticipant;

/**
 * 临时参赛档案-允许无正式账号存在Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstParticipantMapper 
{
    /**
     * 查询临时参赛档案-允许无正式账号存在
     * 
     * @param participantId 临时参赛档案-允许无正式账号存在主键
     * @return 临时参赛档案-允许无正式账号存在
     */
    public JstParticipant selectJstParticipantByParticipantId(Long participantId);

    /**
     * 查询临时参赛档案-允许无正式账号存在列表
     * 
     * @param jstParticipant 临时参赛档案-允许无正式账号存在
     * @return 临时参赛档案-允许无正式账号存在集合
     */
    public List<JstParticipant> selectJstParticipantList(JstParticipant jstParticipant);

    /**
     * 新增临时参赛档案-允许无正式账号存在
     * 
     * @param jstParticipant 临时参赛档案-允许无正式账号存在
     * @return 结果
     */
    public int insertJstParticipant(JstParticipant jstParticipant);

    /**
     * 修改临时参赛档案-允许无正式账号存在
     * 
     * @param jstParticipant 临时参赛档案-允许无正式账号存在
     * @return 结果
     */
    public int updateJstParticipant(JstParticipant jstParticipant);

    /**
     * 删除临时参赛档案-允许无正式账号存在
     * 
     * @param participantId 临时参赛档案-允许无正式账号存在主键
     * @return 结果
     */
    public int deleteJstParticipantByParticipantId(Long participantId);

    /**
     * 批量删除临时参赛档案-允许无正式账号存在
     * 
     * @param participantIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstParticipantByParticipantIds(Long[] participantIds);
}
