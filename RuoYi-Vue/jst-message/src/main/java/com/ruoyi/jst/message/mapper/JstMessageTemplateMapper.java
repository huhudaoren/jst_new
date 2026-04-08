package com.ruoyi.jst.message.mapper;

import java.util.List;
import com.ruoyi.jst.message.domain.JstMessageTemplate;

/**
 * 消息模板Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstMessageTemplateMapper 
{
    /**
     * 查询消息模板
     * 
     * @param templateId 消息模板主键
     * @return 消息模板
     */
    public JstMessageTemplate selectJstMessageTemplateByTemplateId(Long templateId);

    /**
     * 查询消息模板列表
     * 
     * @param jstMessageTemplate 消息模板
     * @return 消息模板集合
     */
    public List<JstMessageTemplate> selectJstMessageTemplateList(JstMessageTemplate jstMessageTemplate);

    /**
     * 新增消息模板
     * 
     * @param jstMessageTemplate 消息模板
     * @return 结果
     */
    public int insertJstMessageTemplate(JstMessageTemplate jstMessageTemplate);

    /**
     * 修改消息模板
     * 
     * @param jstMessageTemplate 消息模板
     * @return 结果
     */
    public int updateJstMessageTemplate(JstMessageTemplate jstMessageTemplate);

    /**
     * 删除消息模板
     * 
     * @param templateId 消息模板主键
     * @return 结果
     */
    public int deleteJstMessageTemplateByTemplateId(Long templateId);

    /**
     * 批量删除消息模板
     * 
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstMessageTemplateByTemplateIds(Long[] templateIds);
}
