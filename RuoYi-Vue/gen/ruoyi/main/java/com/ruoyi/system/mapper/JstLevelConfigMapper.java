package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstLevelConfig;

/**
 * 等级配置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstLevelConfigMapper 
{
    /**
     * 查询等级配置
     * 
     * @param levelId 等级配置主键
     * @return 等级配置
     */
    public JstLevelConfig selectJstLevelConfigByLevelId(Long levelId);

    /**
     * 查询等级配置列表
     * 
     * @param jstLevelConfig 等级配置
     * @return 等级配置集合
     */
    public List<JstLevelConfig> selectJstLevelConfigList(JstLevelConfig jstLevelConfig);

    /**
     * 新增等级配置
     * 
     * @param jstLevelConfig 等级配置
     * @return 结果
     */
    public int insertJstLevelConfig(JstLevelConfig jstLevelConfig);

    /**
     * 修改等级配置
     * 
     * @param jstLevelConfig 等级配置
     * @return 结果
     */
    public int updateJstLevelConfig(JstLevelConfig jstLevelConfig);

    /**
     * 删除等级配置
     * 
     * @param levelId 等级配置主键
     * @return 结果
     */
    public int deleteJstLevelConfigByLevelId(Long levelId);

    /**
     * 批量删除等级配置
     * 
     * @param levelIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstLevelConfigByLevelIds(Long[] levelIds);
}
