package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstParticipantUserMapMapper;
import com.ruoyi.system.domain.JstParticipantUserMap;
import com.ruoyi.system.service.IJstParticipantUserMapService;

/**
 * 参赛档案-正式用户认领映射Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstParticipantUserMapServiceImpl implements IJstParticipantUserMapService 
{
    @Autowired
    private JstParticipantUserMapMapper jstParticipantUserMapMapper;

    /**
     * 查询参赛档案-正式用户认领映射
     * 
     * @param mapId 参赛档案-正式用户认领映射主键
     * @return 参赛档案-正式用户认领映射
     */
    @Override
    public JstParticipantUserMap selectJstParticipantUserMapByMapId(Long mapId)
    {
        return jstParticipantUserMapMapper.selectJstParticipantUserMapByMapId(mapId);
    }

    /**
     * 查询参赛档案-正式用户认领映射列表
     * 
     * @param jstParticipantUserMap 参赛档案-正式用户认领映射
     * @return 参赛档案-正式用户认领映射
     */
    @Override
    public List<JstParticipantUserMap> selectJstParticipantUserMapList(JstParticipantUserMap jstParticipantUserMap)
    {
        return jstParticipantUserMapMapper.selectJstParticipantUserMapList(jstParticipantUserMap);
    }

    /**
     * 新增参赛档案-正式用户认领映射
     * 
     * @param jstParticipantUserMap 参赛档案-正式用户认领映射
     * @return 结果
     */
    @Override
    public int insertJstParticipantUserMap(JstParticipantUserMap jstParticipantUserMap)
    {
        jstParticipantUserMap.setCreateTime(DateUtils.getNowDate());
        return jstParticipantUserMapMapper.insertJstParticipantUserMap(jstParticipantUserMap);
    }

    /**
     * 修改参赛档案-正式用户认领映射
     * 
     * @param jstParticipantUserMap 参赛档案-正式用户认领映射
     * @return 结果
     */
    @Override
    public int updateJstParticipantUserMap(JstParticipantUserMap jstParticipantUserMap)
    {
        jstParticipantUserMap.setUpdateTime(DateUtils.getNowDate());
        return jstParticipantUserMapMapper.updateJstParticipantUserMap(jstParticipantUserMap);
    }

    /**
     * 批量删除参赛档案-正式用户认领映射
     * 
     * @param mapIds 需要删除的参赛档案-正式用户认领映射主键
     * @return 结果
     */
    @Override
    public int deleteJstParticipantUserMapByMapIds(Long[] mapIds)
    {
        return jstParticipantUserMapMapper.deleteJstParticipantUserMapByMapIds(mapIds);
    }

    /**
     * 删除参赛档案-正式用户认领映射信息
     * 
     * @param mapId 参赛档案-正式用户认领映射主键
     * @return 结果
     */
    @Override
    public int deleteJstParticipantUserMapByMapId(Long mapId)
    {
        return jstParticipantUserMapMapper.deleteJstParticipantUserMapByMapId(mapId);
    }
}
