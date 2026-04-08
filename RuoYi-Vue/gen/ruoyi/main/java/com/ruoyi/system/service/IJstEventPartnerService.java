package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstEventPartner;

/**
 * 赛事方档案Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstEventPartnerService 
{
    /**
     * 查询赛事方档案
     * 
     * @param partnerId 赛事方档案主键
     * @return 赛事方档案
     */
    public JstEventPartner selectJstEventPartnerByPartnerId(Long partnerId);

    /**
     * 查询赛事方档案列表
     * 
     * @param jstEventPartner 赛事方档案
     * @return 赛事方档案集合
     */
    public List<JstEventPartner> selectJstEventPartnerList(JstEventPartner jstEventPartner);

    /**
     * 新增赛事方档案
     * 
     * @param jstEventPartner 赛事方档案
     * @return 结果
     */
    public int insertJstEventPartner(JstEventPartner jstEventPartner);

    /**
     * 修改赛事方档案
     * 
     * @param jstEventPartner 赛事方档案
     * @return 结果
     */
    public int updateJstEventPartner(JstEventPartner jstEventPartner);

    /**
     * 批量删除赛事方档案
     * 
     * @param partnerIds 需要删除的赛事方档案主键集合
     * @return 结果
     */
    public int deleteJstEventPartnerByPartnerIds(Long[] partnerIds);

    /**
     * 删除赛事方档案信息
     * 
     * @param partnerId 赛事方档案主键
     * @return 结果
     */
    public int deleteJstEventPartnerByPartnerId(Long partnerId);
}
