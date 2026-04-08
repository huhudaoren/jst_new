package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.JstChannelAuthApply;

/**
 * 渠道认证申请Mapper接口
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public interface JstChannelAuthApplyMapper 
{
    /**
     * 查询渠道认证申请
     * 
     * @param applyId 渠道认证申请主键
     * @return 渠道认证申请
     */
    public JstChannelAuthApply selectJstChannelAuthApplyByApplyId(Long applyId);

    /**
     * 查询渠道认证申请列表
     * 
     * @param jstChannelAuthApply 渠道认证申请
     * @return 渠道认证申请集合
     */
    public List<JstChannelAuthApply> selectJstChannelAuthApplyList(JstChannelAuthApply jstChannelAuthApply);

    /**
     * 新增渠道认证申请
     * 
     * @param jstChannelAuthApply 渠道认证申请
     * @return 结果
     */
    public int insertJstChannelAuthApply(JstChannelAuthApply jstChannelAuthApply);

    /**
     * 修改渠道认证申请
     * 
     * @param jstChannelAuthApply 渠道认证申请
     * @return 结果
     */
    public int updateJstChannelAuthApply(JstChannelAuthApply jstChannelAuthApply);

    /**
     * 删除渠道认证申请
     * 
     * @param applyId 渠道认证申请主键
     * @return 结果
     */
    public int deleteJstChannelAuthApplyByApplyId(Long applyId);

    /**
     * 批量删除渠道认证申请
     * 
     * @param applyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteJstChannelAuthApplyByApplyIds(Long[] applyIds);
}
