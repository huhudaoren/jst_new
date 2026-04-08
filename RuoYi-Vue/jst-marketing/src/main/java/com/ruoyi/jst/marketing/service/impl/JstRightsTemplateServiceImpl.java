package com.ruoyi.jst.marketing.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.marketing.mapper.JstRightsTemplateMapper;
import com.ruoyi.jst.marketing.domain.JstRightsTemplate;
import com.ruoyi.jst.marketing.service.IJstRightsTemplateService;

/**
 * 权益模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstRightsTemplateServiceImpl implements IJstRightsTemplateService 
{
    @Autowired
    private JstRightsTemplateMapper jstRightsTemplateMapper;

    /**
     * 查询权益模板
     * 
     * @param rightsTemplateId 权益模板主键
     * @return 权益模板
     */
    @Override
    public JstRightsTemplate selectJstRightsTemplateByRightsTemplateId(Long rightsTemplateId)
    {
        return jstRightsTemplateMapper.selectJstRightsTemplateByRightsTemplateId(rightsTemplateId);
    }

    /**
     * 查询权益模板列表
     * 
     * @param jstRightsTemplate 权益模板
     * @return 权益模板
     */
    @Override
    public List<JstRightsTemplate> selectJstRightsTemplateList(JstRightsTemplate jstRightsTemplate)
    {
        return jstRightsTemplateMapper.selectJstRightsTemplateList(jstRightsTemplate);
    }

    /**
     * 新增权益模板
     * 
     * @param jstRightsTemplate 权益模板
     * @return 结果
     */
    @Override
    public int insertJstRightsTemplate(JstRightsTemplate jstRightsTemplate)
    {
        jstRightsTemplate.setCreateTime(DateUtils.getNowDate());
        return jstRightsTemplateMapper.insertJstRightsTemplate(jstRightsTemplate);
    }

    /**
     * 修改权益模板
     * 
     * @param jstRightsTemplate 权益模板
     * @return 结果
     */
    @Override
    public int updateJstRightsTemplate(JstRightsTemplate jstRightsTemplate)
    {
        jstRightsTemplate.setUpdateTime(DateUtils.getNowDate());
        return jstRightsTemplateMapper.updateJstRightsTemplate(jstRightsTemplate);
    }

    /**
     * 批量删除权益模板
     * 
     * @param rightsTemplateIds 需要删除的权益模板主键
     * @return 结果
     */
    @Override
    public int deleteJstRightsTemplateByRightsTemplateIds(Long[] rightsTemplateIds)
    {
        return jstRightsTemplateMapper.deleteJstRightsTemplateByRightsTemplateIds(rightsTemplateIds);
    }

    /**
     * 删除权益模板信息
     * 
     * @param rightsTemplateId 权益模板主键
     * @return 结果
     */
    @Override
    public int deleteJstRightsTemplateByRightsTemplateId(Long rightsTemplateId)
    {
        return jstRightsTemplateMapper.deleteJstRightsTemplateByRightsTemplateId(rightsTemplateId);
    }
}
