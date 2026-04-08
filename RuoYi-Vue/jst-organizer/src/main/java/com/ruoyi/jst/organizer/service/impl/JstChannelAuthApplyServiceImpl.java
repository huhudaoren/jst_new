package com.ruoyi.jst.organizer.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.organizer.mapper.JstChannelAuthApplyMapper;
import com.ruoyi.jst.organizer.domain.JstChannelAuthApply;
import com.ruoyi.jst.organizer.service.IJstChannelAuthApplyService;

/**
 * 渠道认证申请Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstChannelAuthApplyServiceImpl implements IJstChannelAuthApplyService 
{
    @Autowired
    private JstChannelAuthApplyMapper jstChannelAuthApplyMapper;

    /**
     * 查询渠道认证申请
     * 
     * @param applyId 渠道认证申请主键
     * @return 渠道认证申请
     */
    @Override
    public JstChannelAuthApply selectJstChannelAuthApplyByApplyId(Long applyId)
    {
        return jstChannelAuthApplyMapper.selectJstChannelAuthApplyByApplyId(applyId);
    }

    /**
     * 查询渠道认证申请列表
     * 
     * @param jstChannelAuthApply 渠道认证申请
     * @return 渠道认证申请
     */
    @Override
    public List<JstChannelAuthApply> selectJstChannelAuthApplyList(JstChannelAuthApply jstChannelAuthApply)
    {
        return jstChannelAuthApplyMapper.selectJstChannelAuthApplyList(jstChannelAuthApply);
    }

    /**
     * 新增渠道认证申请
     * 
     * @param jstChannelAuthApply 渠道认证申请
     * @return 结果
     */
    @Override
    public int insertJstChannelAuthApply(JstChannelAuthApply jstChannelAuthApply)
    {
        jstChannelAuthApply.setCreateTime(DateUtils.getNowDate());
        return jstChannelAuthApplyMapper.insertJstChannelAuthApply(jstChannelAuthApply);
    }

    /**
     * 修改渠道认证申请
     * 
     * @param jstChannelAuthApply 渠道认证申请
     * @return 结果
     */
    @Override
    public int updateJstChannelAuthApply(JstChannelAuthApply jstChannelAuthApply)
    {
        jstChannelAuthApply.setUpdateTime(DateUtils.getNowDate());
        return jstChannelAuthApplyMapper.updateJstChannelAuthApply(jstChannelAuthApply);
    }

    /**
     * 批量删除渠道认证申请
     * 
     * @param applyIds 需要删除的渠道认证申请主键
     * @return 结果
     */
    @Override
    public int deleteJstChannelAuthApplyByApplyIds(Long[] applyIds)
    {
        return jstChannelAuthApplyMapper.deleteJstChannelAuthApplyByApplyIds(applyIds);
    }

    /**
     * 删除渠道认证申请信息
     * 
     * @param applyId 渠道认证申请主键
     * @return 结果
     */
    @Override
    public int deleteJstChannelAuthApplyByApplyId(Long applyId)
    {
        return jstChannelAuthApplyMapper.deleteJstChannelAuthApplyByApplyId(applyId);
    }
}
