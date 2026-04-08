package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.JstEventPartnerApply;

/**
 * 赛事方入驻申请Service接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface IJstEventPartnerApplyService 
{
    /**
     * 查询赛事方入驻申请
     * 
     * @param applyId 赛事方入驻申请主键
     * @return 赛事方入驻申请
     */
    public JstEventPartnerApply selectJstEventPartnerApplyByApplyId(Long applyId);

    /**
     * 查询赛事方入驻申请列表
     * 
     * @param jstEventPartnerApply 赛事方入驻申请
     * @return 赛事方入驻申请集合
     */
    public List<JstEventPartnerApply> selectJstEventPartnerApplyList(JstEventPartnerApply jstEventPartnerApply);

    /**
     * 新增赛事方入驻申请
     * 
     * @param jstEventPartnerApply 赛事方入驻申请
     * @return 结果
     */
    public int insertJstEventPartnerApply(JstEventPartnerApply jstEventPartnerApply);

    /**
     * 修改赛事方入驻申请
     * 
     * @param jstEventPartnerApply 赛事方入驻申请
     * @return 结果
     */
    public int updateJstEventPartnerApply(JstEventPartnerApply jstEventPartnerApply);

    /**
     * 批量删除赛事方入驻申请
     * 
     * @param applyIds 需要删除的赛事方入驻申请主键集合
     * @return 结果
     */
    public int deleteJstEventPartnerApplyByApplyIds(Long[] applyIds);

    /**
     * 删除赛事方入驻申请信息
     * 
     * @param applyId 赛事方入驻申请主键
     * @return 结果
     */
    public int deleteJstEventPartnerApplyByApplyId(Long applyId);
}
