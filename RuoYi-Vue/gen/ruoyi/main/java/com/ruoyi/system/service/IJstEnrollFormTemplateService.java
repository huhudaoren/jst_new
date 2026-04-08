package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstEnrollFormTemplate;

/**
 * 报名动态单模板（schema_json定义字段）Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstEnrollFormTemplateService 
{
    /**
     * 查询报名动态单模板（schema_json定义字段）
     * 
     * @param templateId 报名动态单模板（schema_json定义字段）主键
     * @return 报名动态单模板（schema_json定义字段）
     */
    public JstEnrollFormTemplate selectJstEnrollFormTemplateByTemplateId(Long templateId);

    /**
     * 查询报名动态单模板（schema_json定义字段）列表
     * 
     * @param jstEnrollFormTemplate 报名动态单模板（schema_json定义字段）
     * @return 报名动态单模板（schema_json定义字段）集合
     */
    public List<JstEnrollFormTemplate> selectJstEnrollFormTemplateList(JstEnrollFormTemplate jstEnrollFormTemplate);

    /**
     * 新增报名动态单模板（schema_json定义字段）
     * 
     * @param jstEnrollFormTemplate 报名动态单模板（schema_json定义字段）
     * @return 结果
     */
    public int insertJstEnrollFormTemplate(JstEnrollFormTemplate jstEnrollFormTemplate);

    /**
     * 修改报名动态单模板（schema_json定义字段）
     * 
     * @param jstEnrollFormTemplate 报名动态单模板（schema_json定义字段）
     * @return 结果
     */
    public int updateJstEnrollFormTemplate(JstEnrollFormTemplate jstEnrollFormTemplate);

    /**
     * 批量删除报名动态单模板（schema_json定义字段）
     * 
     * @param templateIds 需要删除的报名动态单模板（schema_json定义字段）主键集合
     * @return 结果
     */
    public int deleteJstEnrollFormTemplateByTemplateIds(Long[] templateIds);

    /**
     * 删除报名动态单模板（schema_json定义字段）信息
     * 
     * @param templateId 报名动态单模板（schema_json定义字段）主键
     * @return 结果
     */
    public int deleteJstEnrollFormTemplateByTemplateId(Long templateId);
}
