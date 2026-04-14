package com.ruoyi.jst.common.service;

import com.ruoyi.jst.common.dto.BizNoRuleQueryReqDTO;
import com.ruoyi.jst.common.dto.BizNoRuleSaveReqDTO;
import com.ruoyi.jst.common.dto.BizNoRuleUpdateReqDTO;
import com.ruoyi.jst.common.vo.BizNoRuleResVO;

import java.util.List;

/**
 * 编号规则管理服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstBizNoRuleService {

    /**
     * 查询编号规则列表。
     *
     * @param query 查询条件
     * @return 规则列表
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:list
     */
    List<BizNoRuleResVO> selectBizNoRuleList(BizNoRuleQueryReqDTO query);

    /**
     * 查询编号规则详情。
     *
     * @param ruleId 规则ID
     * @return 规则详情
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:query
     */
    BizNoRuleResVO selectBizNoRuleById(Long ruleId);

    /**
     * 新增编号规则。
     *
     * @param req 新增请求
     * @return 新规则ID
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:add
     */
    Long addBizNoRule(BizNoRuleSaveReqDTO req);

    /**
     * 修改编号规则。
     *
     * @param req 修改请求
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:edit
     */
    void editBizNoRule(BizNoRuleUpdateReqDTO req);

    /**
     * 删除编号规则。
     *
     * @param ruleIds 规则ID数组
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:remove
     */
    void removeBizNoRule(Long[] ruleIds);
}
