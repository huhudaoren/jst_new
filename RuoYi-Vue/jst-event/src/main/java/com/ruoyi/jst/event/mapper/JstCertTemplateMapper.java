package com.ruoyi.jst.event.mapper;

import java.util.List;
import com.ruoyi.jst.event.domain.JstCertTemplate;

/**
 * 证书模板Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstCertTemplateMapper 
{
    /**
     * 查询证书模板
     * 
     * @param templateId 证书模板主键
     * @return 证书模板
     */
    public JstCertTemplate selectJstCertTemplateByTemplateId(Long templateId);

    /**
     * 查询证书模板列表
     * 
     * @param jstCertTemplate 证书模板
     * @return 证书模板集合
     */
    public List<JstCertTemplate> selectJstCertTemplateList(JstCertTemplate jstCertTemplate);

    /**
     * 新增证书模板
     * 
     * @param jstCertTemplate 证书模板
     * @return 结果
     */
    public int insertJstCertTemplate(JstCertTemplate jstCertTemplate);

    /**
     * 修改证书模板
     * 
     * @param jstCertTemplate 证书模板
     * @return 结果
     */
    public int updateJstCertTemplate(JstCertTemplate jstCertTemplate);

    /**
     * 删除证书模板
     * 
     * @param templateId 证书模板主键
     * @return 结果
     */
    public int deleteJstCertTemplateByTemplateId(Long templateId);

    /**
     * 批量删除证书模板
     * 
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstCertTemplateByTemplateIds(Long[] templateIds);
}
