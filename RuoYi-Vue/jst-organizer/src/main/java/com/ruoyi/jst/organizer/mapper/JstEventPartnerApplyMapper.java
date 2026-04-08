package com.ruoyi.jst.organizer.mapper;

import java.util.List;
import com.ruoyi.jst.organizer.domain.JstEventPartnerApply;

/**
 * 赛事方入驻申请Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstEventPartnerApplyMapper 
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
     * 删除赛事方入驻申请
     * 
     * @param applyId 赛事方入驻申请主键
     * @return 结果
     */
    public int deleteJstEventPartnerApplyByApplyId(Long applyId);

    /**
     * 批量删除赛事方入驻申请
     * 
     * @param applyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstEventPartnerApplyByApplyIds(Long[] applyIds);
}
