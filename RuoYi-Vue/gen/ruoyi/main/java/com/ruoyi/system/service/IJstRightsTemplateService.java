package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstRightsTemplate;

/**
 * 权益模板Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstRightsTemplateService 
{
    /**
     * 查询权益模板
     * 
     * @param rightsTemplateId 权益模板主键
     * @return 权益模板
     */
    public JstRightsTemplate selectJstRightsTemplateByRightsTemplateId(Long rightsTemplateId);

    /**
     * 查询权益模板列表
     * 
     * @param jstRightsTemplate 权益模板
     * @return 权益模板集合
     */
    public List<JstRightsTemplate> selectJstRightsTemplateList(JstRightsTemplate jstRightsTemplate);

    /**
     * 新增权益模板
     * 
     * @param jstRightsTemplate 权益模板
     * @return 结果
     */
    public int insertJstRightsTemplate(JstRightsTemplate jstRightsTemplate);

    /**
     * 修改权益模板
     * 
     * @param jstRightsTemplate 权益模板
     * @return 结果
     */
    public int updateJstRightsTemplate(JstRightsTemplate jstRightsTemplate);

    /**
     * 批量删除权益模板
     * 
     * @param rightsTemplateIds 需要删除的权益模板主键集合
     * @return 结果
     */
    public int deleteJstRightsTemplateByRightsTemplateIds(Long[] rightsTemplateIds);

    /**
     * 删除权益模板信息
     * 
     * @param rightsTemplateId 权益模板主键
     * @return 结果
     */
    public int deleteJstRightsTemplateByRightsTemplateId(Long rightsTemplateId);
}
