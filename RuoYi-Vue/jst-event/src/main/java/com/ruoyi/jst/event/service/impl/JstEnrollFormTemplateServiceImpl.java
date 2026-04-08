package com.ruoyi.jst.event.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.event.mapper.JstEnrollFormTemplateMapper;
import com.ruoyi.jst.event.domain.JstEnrollFormTemplate;
import com.ruoyi.jst.event.service.IJstEnrollFormTemplateService;

/**
 * 报名动态单模板（schema_json定义字段）Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstEnrollFormTemplateServiceImpl implements IJstEnrollFormTemplateService 
{
    @Autowired
    private JstEnrollFormTemplateMapper jstEnrollFormTemplateMapper;

    /**
     * 查询报名动态单模板（schema_json定义字段）
     * 
     * @param templateId 报名动态单模板（schema_json定义字段）主键
     * @return 报名动态单模板（schema_json定义字段）
     */
    @Override
    public JstEnrollFormTemplate selectJstEnrollFormTemplateByTemplateId(Long templateId)
    {
        return jstEnrollFormTemplateMapper.selectJstEnrollFormTemplateByTemplateId(templateId);
    }

    /**
     * 查询报名动态单模板（schema_json定义字段）列表
     * 
     * @param jstEnrollFormTemplate 报名动态单模板（schema_json定义字段）
     * @return 报名动态单模板（schema_json定义字段）
     */
    @Override
    public List<JstEnrollFormTemplate> selectJstEnrollFormTemplateList(JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        return jstEnrollFormTemplateMapper.selectJstEnrollFormTemplateList(jstEnrollFormTemplate);
    }

    /**
     * 新增报名动态单模板（schema_json定义字段）
     * 
     * @param jstEnrollFormTemplate 报名动态单模板（schema_json定义字段）
     * @return 结果
     */
    @Override
    public int insertJstEnrollFormTemplate(JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        jstEnrollFormTemplate.setCreateTime(DateUtils.getNowDate());
        return jstEnrollFormTemplateMapper.insertJstEnrollFormTemplate(jstEnrollFormTemplate);
    }

    /**
     * 修改报名动态单模板（schema_json定义字段）
     * 
     * @param jstEnrollFormTemplate 报名动态单模板（schema_json定义字段）
     * @return 结果
     */
    @Override
    public int updateJstEnrollFormTemplate(JstEnrollFormTemplate jstEnrollFormTemplate)
    {
        jstEnrollFormTemplate.setUpdateTime(DateUtils.getNowDate());
        return jstEnrollFormTemplateMapper.updateJstEnrollFormTemplate(jstEnrollFormTemplate);
    }

    /**
     * 批量删除报名动态单模板（schema_json定义字段）
     * 
     * @param templateIds 需要删除的报名动态单模板（schema_json定义字段）主键
     * @return 结果
     */
    @Override
    public int deleteJstEnrollFormTemplateByTemplateIds(Long[] templateIds)
    {
        return jstEnrollFormTemplateMapper.deleteJstEnrollFormTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除报名动态单模板（schema_json定义字段）信息
     * 
     * @param templateId 报名动态单模板（schema_json定义字段）主键
     * @return 结果
     */
    @Override
    public int deleteJstEnrollFormTemplateByTemplateId(Long templateId)
    {
        return jstEnrollFormTemplateMapper.deleteJstEnrollFormTemplateByTemplateId(templateId);
    }
}
