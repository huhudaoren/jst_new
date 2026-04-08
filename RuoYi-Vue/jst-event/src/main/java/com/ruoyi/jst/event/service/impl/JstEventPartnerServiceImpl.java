package com.ruoyi.jst.event.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.jst.event.mapper.JstEventPartnerMapper;
import com.ruoyi.jst.event.domain.JstEventPartner;
import com.ruoyi.jst.event.service.IJstEventPartnerService;

/**
 * 赛事方档案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
@Service
public class JstEventPartnerServiceImpl implements IJstEventPartnerService 
{
    @Autowired
    private JstEventPartnerMapper jstEventPartnerMapper;

    /**
     * 查询赛事方档案
     * 
     * @param partnerId 赛事方档案主键
     * @return 赛事方档案
     */
    @Override
    public JstEventPartner selectJstEventPartnerByPartnerId(Long partnerId)
    {
        return jstEventPartnerMapper.selectJstEventPartnerByPartnerId(partnerId);
    }

    /**
     * 查询赛事方档案列表
     * 
     * @param jstEventPartner 赛事方档案
     * @return 赛事方档案
     */
    @Override
    public List<JstEventPartner> selectJstEventPartnerList(JstEventPartner jstEventPartner)
    {
        return jstEventPartnerMapper.selectJstEventPartnerList(jstEventPartner);
    }

    /**
     * 新增赛事方档案
     * 
     * @param jstEventPartner 赛事方档案
     * @return 结果
     */
    @Override
    public int insertJstEventPartner(JstEventPartner jstEventPartner)
    {
        jstEventPartner.setCreateTime(DateUtils.getNowDate());
        return jstEventPartnerMapper.insertJstEventPartner(jstEventPartner);
    }

    /**
     * 修改赛事方档案
     * 
     * @param jstEventPartner 赛事方档案
     * @return 结果
     */
    @Override
    public int updateJstEventPartner(JstEventPartner jstEventPartner)
    {
        jstEventPartner.setUpdateTime(DateUtils.getNowDate());
        return jstEventPartnerMapper.updateJstEventPartner(jstEventPartner);
    }

    /**
     * 批量删除赛事方档案
     * 
     * @param partnerIds 需要删除的赛事方档案主键
     * @return 结果
     */
    @Override
    public int deleteJstEventPartnerByPartnerIds(Long[] partnerIds)
    {
        return jstEventPartnerMapper.deleteJstEventPartnerByPartnerIds(partnerIds);
    }

    /**
     * 删除赛事方档案信息
     * 
     * @param partnerId 赛事方档案主键
     * @return 结果
     */
    @Override
    public int deleteJstEventPartnerByPartnerId(Long partnerId)
    {
        return jstEventPartnerMapper.deleteJstEventPartnerByPartnerId(partnerId);
    }
}
