package com.ruoyi.jst.points.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.points.mapper.JstLevelConfigMapper;
import com.ruoyi.jst.points.domain.JstLevelConfig;
import com.ruoyi.jst.points.service.IJstLevelConfigService;

/**
 * 等级配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstLevelConfigServiceImpl implements IJstLevelConfigService 
{
    @Autowired
    private JstLevelConfigMapper jstLevelConfigMapper;

    /**
     * 查询等级配置
     * 
     * @param levelId 等级配置主键
     * @return 等级配置
     */
    @Override
    public JstLevelConfig selectJstLevelConfigByLevelId(Long levelId)
    {
        return jstLevelConfigMapper.selectJstLevelConfigByLevelId(levelId);
    }

    /**
     * 查询等级配置列表
     * 
     * @param jstLevelConfig 等级配置
     * @return 等级配置
     */
    @Override
    public List<JstLevelConfig> selectJstLevelConfigList(JstLevelConfig jstLevelConfig)
    {
        return jstLevelConfigMapper.selectJstLevelConfigList(jstLevelConfig);
    }

    /**
     * 新增等级配置
     * 
     * @param jstLevelConfig 等级配置
     * @return 结果
     */
    @Override
    public int insertJstLevelConfig(JstLevelConfig jstLevelConfig)
    {
        jstLevelConfig.setCreateTime(DateUtils.getNowDate());
        return jstLevelConfigMapper.insertJstLevelConfig(jstLevelConfig);
    }

    /**
     * 修改等级配置
     * 
     * @param jstLevelConfig 等级配置
     * @return 结果
     */
    @Override
    public int updateJstLevelConfig(JstLevelConfig jstLevelConfig)
    {
        jstLevelConfig.setUpdateTime(DateUtils.getNowDate());
        return jstLevelConfigMapper.updateJstLevelConfig(jstLevelConfig);
    }

    /**
     * 批量删除等级配置
     * 
     * @param levelIds 需要删除的等级配置主键
     * @return 结果
     */
    @Override
    public int deleteJstLevelConfigByLevelIds(Long[] levelIds)
    {
        return jstLevelConfigMapper.deleteJstLevelConfigByLevelIds(levelIds);
    }

    /**
     * 删除等级配置信息
     * 
     * @param levelId 等级配置主键
     * @return 结果
     */
    @Override
    public int deleteJstLevelConfigByLevelId(Long levelId)
    {
        return jstLevelConfigMapper.deleteJstLevelConfigByLevelId(levelId);
    }
}
