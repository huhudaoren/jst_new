package com.ruoyi.jst.organizer.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.organizer.mapper.JstEventPartnerApplyMapper;
import com.ruoyi.jst.organizer.domain.JstEventPartnerApply;
import com.ruoyi.jst.organizer.service.IJstEventPartnerApplyService;

/**
 * 赛事方入驻申请Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstEventPartnerApplyServiceImpl implements IJstEventPartnerApplyService 
{
    @Autowired
    private JstEventPartnerApplyMapper jstEventPartnerApplyMapper;

    /**
     * 查询赛事方入驻申请
     * 
     * @param applyId 赛事方入驻申请主键
     * @return 赛事方入驻申请
     */
    @Override
    public JstEventPartnerApply selectJstEventPartnerApplyByApplyId(Long applyId)
    {
        return jstEventPartnerApplyMapper.selectJstEventPartnerApplyByApplyId(applyId);
    }

    /**
     * 查询赛事方入驻申请列表
     * 
     * @param jstEventPartnerApply 赛事方入驻申请
     * @return 赛事方入驻申请
     */
    @Override
    public List<JstEventPartnerApply> selectJstEventPartnerApplyList(JstEventPartnerApply jstEventPartnerApply)
    {
        return jstEventPartnerApplyMapper.selectJstEventPartnerApplyList(jstEventPartnerApply);
    }

    /**
     * 新增赛事方入驻申请
     * 
     * @param jstEventPartnerApply 赛事方入驻申请
     * @return 结果
     */
    @Override
    public int insertJstEventPartnerApply(JstEventPartnerApply jstEventPartnerApply)
    {
        jstEventPartnerApply.setCreateTime(DateUtils.getNowDate());
        return jstEventPartnerApplyMapper.insertJstEventPartnerApply(jstEventPartnerApply);
    }

    /**
     * 修改赛事方入驻申请
     * 
     * @param jstEventPartnerApply 赛事方入驻申请
     * @return 结果
     */
    @Override
    public int updateJstEventPartnerApply(JstEventPartnerApply jstEventPartnerApply)
    {
        jstEventPartnerApply.setUpdateTime(DateUtils.getNowDate());
        return jstEventPartnerApplyMapper.updateJstEventPartnerApply(jstEventPartnerApply);
    }

    /**
     * 批量删除赛事方入驻申请
     * 
     * @param applyIds 需要删除的赛事方入驻申请主键
     * @return 结果
     */
    @Override
    public int deleteJstEventPartnerApplyByApplyIds(Long[] applyIds)
    {
        return jstEventPartnerApplyMapper.deleteJstEventPartnerApplyByApplyIds(applyIds);
    }

    /**
     * 删除赛事方入驻申请信息
     * 
     * @param applyId 赛事方入驻申请主键
     * @return 结果
     */
    @Override
    public int deleteJstEventPartnerApplyByApplyId(Long applyId)
    {
        return jstEventPartnerApplyMapper.deleteJstEventPartnerApplyByApplyId(applyId);
    }
}
