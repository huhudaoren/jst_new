package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstParticipantUserMap;

/**
 * 参赛档案-正式用户认领映射Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstParticipantUserMapService 
{
    /**
     * 查询参赛档案-正式用户认领映射
     * 
     * @param mapId 参赛档案-正式用户认领映射主键
     * @return 参赛档案-正式用户认领映射
     */
    public JstParticipantUserMap selectJstParticipantUserMapByMapId(Long mapId);

    /**
     * 查询参赛档案-正式用户认领映射列表
     * 
     * @param jstParticipantUserMap 参赛档案-正式用户认领映射
     * @return 参赛档案-正式用户认领映射集合
     */
    public List<JstParticipantUserMap> selectJstParticipantUserMapList(JstParticipantUserMap jstParticipantUserMap);

    /**
     * 新增参赛档案-正式用户认领映射
     * 
     * @param jstParticipantUserMap 参赛档案-正式用户认领映射
     * @return 结果
     */
    public int insertJstParticipantUserMap(JstParticipantUserMap jstParticipantUserMap);

    /**
     * 修改参赛档案-正式用户认领映射
     * 
     * @param jstParticipantUserMap 参赛档案-正式用户认领映射
     * @return 结果
     */
    public int updateJstParticipantUserMap(JstParticipantUserMap jstParticipantUserMap);

    /**
     * 批量删除参赛档案-正式用户认领映射
     * 
     * @param mapIds 需要删除的参赛档案-正式用户认领映射主键集合
     * @return 结果
     */
    public int deleteJstParticipantUserMapByMapIds(Long[] mapIds);

    /**
     * 删除参赛档案-正式用户认领映射信息
     * 
     * @param mapId 参赛档案-正式用户认领映射主键
     * @return 结果
     */
    public int deleteJstParticipantUserMapByMapId(Long mapId);
}
