package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstMessageTemplateMapper;
import com.ruoyi.system.domain.JstMessageTemplate;
import com.ruoyi.system.service.IJstMessageTemplateService;

/**
 * 消息模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstMessageTemplateServiceImpl implements IJstMessageTemplateService 
{
    @Autowired
    private JstMessageTemplateMapper jstMessageTemplateMapper;

    /**
     * 查询消息模板
     * 
     * @param templateId 消息模板主键
     * @return 消息模板
     */
    @Override
    public JstMessageTemplate selectJstMessageTemplateByTemplateId(Long templateId)
    {
        return jstMessageTemplateMapper.selectJstMessageTemplateByTemplateId(templateId);
    }

    /**
     * 查询消息模板列表
     * 
     * @param jstMessageTemplate 消息模板
     * @return 消息模板
     */
    @Override
    public List<JstMessageTemplate> selectJstMessageTemplateList(JstMessageTemplate jstMessageTemplate)
    {
        return jstMessageTemplateMapper.selectJstMessageTemplateList(jstMessageTemplate);
    }

    /**
     * 新增消息模板
     * 
     * @param jstMessageTemplate 消息模板
     * @return 结果
     */
    @Override
    public int insertJstMessageTemplate(JstMessageTemplate jstMessageTemplate)
    {
        jstMessageTemplate.setCreateTime(DateUtils.getNowDate());
        return jstMessageTemplateMapper.insertJstMessageTemplate(jstMessageTemplate);
    }

    /**
     * 修改消息模板
     * 
     * @param jstMessageTemplate 消息模板
     * @return 结果
     */
    @Override
    public int updateJstMessageTemplate(JstMessageTemplate jstMessageTemplate)
    {
        jstMessageTemplate.setUpdateTime(DateUtils.getNowDate());
        return jstMessageTemplateMapper.updateJstMessageTemplate(jstMessageTemplate);
    }

    /**
     * 批量删除消息模板
     * 
     * @param templateIds 需要删除的消息模板主键
     * @return 结果
     */
    @Override
    public int deleteJstMessageTemplateByTemplateIds(Long[] templateIds)
    {
        return jstMessageTemplateMapper.deleteJstMessageTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除消息模板信息
     * 
     * @param templateId 消息模板主键
     * @return 结果
     */
    @Override
    public int deleteJstMessageTemplateByTemplateId(Long templateId)
    {
        return jstMessageTemplateMapper.deleteJstMessageTemplateByTemplateId(templateId);
    }
}
