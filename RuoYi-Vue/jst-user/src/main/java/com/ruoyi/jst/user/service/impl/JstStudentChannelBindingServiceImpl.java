package com.ruoyi.jst.user.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.user.mapper.JstStudentChannelBindingMapper;
import com.ruoyi.jst.user.domain.JstStudentChannelBinding;
import com.ruoyi.jst.user.service.IJstStudentChannelBindingService;

/**
 * 学生-渠道方绑定关系（同一user同时仅1条active）Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstStudentChannelBindingServiceImpl implements IJstStudentChannelBindingService 
{
    @Autowired
    private JstStudentChannelBindingMapper jstStudentChannelBindingMapper;

    /**
     * 查询学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param bindingId 学生-渠道方绑定关系（同一user同时仅1条active）主键
     * @return 学生-渠道方绑定关系（同一user同时仅1条active）
     */
    @Override
    public JstStudentChannelBinding selectJstStudentChannelBindingByBindingId(Long bindingId)
    {
        return jstStudentChannelBindingMapper.selectJstStudentChannelBindingByBindingId(bindingId);
    }

    /**
     * 查询学生-渠道方绑定关系（同一user同时仅1条active）列表
     * 
     * @param jstStudentChannelBinding 学生-渠道方绑定关系（同一user同时仅1条active）
     * @return 学生-渠道方绑定关系（同一user同时仅1条active）
     */
    @Override
    public List<JstStudentChannelBinding> selectJstStudentChannelBindingList(JstStudentChannelBinding jstStudentChannelBinding)
    {
        return jstStudentChannelBindingMapper.selectJstStudentChannelBindingList(jstStudentChannelBinding);
    }

    /**
     * 新增学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param jstStudentChannelBinding 学生-渠道方绑定关系（同一user同时仅1条active）
     * @return 结果
     */
    @Override
    public int insertJstStudentChannelBinding(JstStudentChannelBinding jstStudentChannelBinding)
    {
        jstStudentChannelBinding.setCreateTime(DateUtils.getNowDate());
        return jstStudentChannelBindingMapper.insertJstStudentChannelBinding(jstStudentChannelBinding);
    }

    /**
     * 修改学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param jstStudentChannelBinding 学生-渠道方绑定关系（同一user同时仅1条active）
     * @return 结果
     */
    @Override
    public int updateJstStudentChannelBinding(JstStudentChannelBinding jstStudentChannelBinding)
    {
        jstStudentChannelBinding.setUpdateTime(DateUtils.getNowDate());
        return jstStudentChannelBindingMapper.updateJstStudentChannelBinding(jstStudentChannelBinding);
    }

    /**
     * 批量删除学生-渠道方绑定关系（同一user同时仅1条active）
     * 
     * @param bindingIds 需要删除的学生-渠道方绑定关系（同一user同时仅1条active）主键
     * @return 结果
     */
    @Override
    public int deleteJstStudentChannelBindingByBindingIds(Long[] bindingIds)
    {
        return jstStudentChannelBindingMapper.deleteJstStudentChannelBindingByBindingIds(bindingIds);
    }

    /**
     * 删除学生-渠道方绑定关系（同一user同时仅1条active）信息
     * 
     * @param bindingId 学生-渠道方绑定关系（同一user同时仅1条active）主键
     * @return 结果
     */
    @Override
    public int deleteJstStudentChannelBindingByBindingId(Long bindingId)
    {
        return jstStudentChannelBindingMapper.deleteJstStudentChannelBindingByBindingId(bindingId);
    }
}
