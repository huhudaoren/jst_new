package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstStudentChannelBinding;

/**
 * 学生-渠道方绑定关系（同一user同时仅1条active）Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstStudentChannelBindingMapper 
{
    /**
     * 查询学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param bindingId 学生-渠道方绑定关系（同一user同时仅1条active）主键
     * @return 学生-渠道方绑定关系（同一user同时仅1条active）
     */
    public JstStudentChannelBinding selectJstStudentChannelBindingByBindingId(Long bindingId);

    /**
     * 查询学生-渠道方绑定关系（同一user同时仅1条active）列表
     * 
     * @param jstStudentChannelBinding 学生-渠道方绑定关系（同一user同时仅1条active）
     * @return 学生-渠道方绑定关系（同一user同时仅1条active）集合
     */
    public List<JstStudentChannelBinding> selectJstStudentChannelBindingList(JstStudentChannelBinding jstStudentChannelBinding);

    /**
     * 新增学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param jstStudentChannelBinding 学生-渠道方绑定关系（同一user同时仅1条active）
     * @return 结果
     */
    public int insertJstStudentChannelBinding(JstStudentChannelBinding jstStudentChannelBinding);

    /**
     * 修改学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param jstStudentChannelBinding 学生-渠道方绑定关系（同一user同时仅1条active）
     * @return 结果
     */
    public int updateJstStudentChannelBinding(JstStudentChannelBinding jstStudentChannelBinding);

    /**
     * 删除学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param bindingId 学生-渠道方绑定关系（同一user同时仅1条active）主键
     * @return 结果
     */
    public int deleteJstStudentChannelBindingByBindingId(Long bindingId);

    /**
     * 批量删除学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param bindingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstStudentChannelBindingByBindingIds(Long[] bindingIds);
}
