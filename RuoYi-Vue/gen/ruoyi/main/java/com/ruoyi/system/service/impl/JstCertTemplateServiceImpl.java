package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.JstCertTemplateMapper;
import com.ruoyi.system.domain.JstCertTemplate;
import com.ruoyi.system.service.IJstCertTemplateService;

/**
 * 证书模板Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstCertTemplateServiceImpl implements IJstCertTemplateService 
{
    @Autowired
    private JstCertTemplateMapper jstCertTemplateMapper;

    /**
     * 查询证书模板
     * 
     * @param templateId 证书模板主键
     * @return 证书模板
     */
    @Override
    public JstCertTemplate selectJstCertTemplateByTemplateId(Long templateId)
    {
        return jstCertTemplateMapper.selectJstCertTemplateByTemplateId(templateId);
    }

    /**
     * 查询证书模板列表
     * 
     * @param jstCertTemplate 证书模板
     * @return 证书模板
     */
    @Override
    public List<JstCertTemplate> selectJstCertTemplateList(JstCertTemplate jstCertTemplate)
    {
        return jstCertTemplateMapper.selectJstCertTemplateList(jstCertTemplate);
    }

    /**
     * 新增证书模板
     * 
     * @param jstCertTemplate 证书模板
     * @return 结果
     */
    @Override
    public int insertJstCertTemplate(JstCertTemplate jstCertTemplate)
    {
        jstCertTemplate.setCreateTime(DateUtils.getNowDate());
        return jstCertTemplateMapper.insertJstCertTemplate(jstCertTemplate);
    }

    /**
     * 修改证书模板
     * 
     * @param jstCertTemplate 证书模板
     * @return 结果
     */
    @Override
    public int updateJstCertTemplate(JstCertTemplate jstCertTemplate)
    {
        jstCertTemplate.setUpdateTime(DateUtils.getNowDate());
        return jstCertTemplateMapper.updateJstCertTemplate(jstCertTemplate);
    }

    /**
     * 批量删除证书模板
     * 
     * @param templateIds 需要删除的证书模板主键
     * @return 结果
     */
    @Override
    public int deleteJstCertTemplateByTemplateIds(Long[] templateIds)
    {
        return jstCertTemplateMapper.deleteJstCertTemplateByTemplateIds(templateIds);
    }

    /**
     * 删除证书模板信息
     * 
     * @param templateId 证书模板主键
     * @return 结果
     */
    @Override
    public int deleteJstCertTemplateByTemplateId(Long templateId)
    {
        return jstCertTemplateMapper.deleteJstCertTemplateByTemplateId(templateId);
    }
}
