package com.ruoyi.jst.common.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.domain.JstBizNoRule;
import com.ruoyi.jst.common.dto.BizNoRuleQueryReqDTO;
import com.ruoyi.jst.common.dto.BizNoRuleSaveReqDTO;
import com.ruoyi.jst.common.dto.BizNoRuleUpdateReqDTO;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.mapper.JstBizNoRuleMapper;
import com.ruoyi.jst.common.service.JstBizNoRuleService;
import com.ruoyi.jst.common.vo.BizNoRuleResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 编号规则管理服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class JstBizNoRuleServiceImpl implements JstBizNoRuleService {

    @Autowired
    private JstBizNoRuleMapper jstBizNoRuleMapper;

    /**
     * 查询编号规则列表。
     *
     * @param query 查询条件
     * @return 规则列表
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:list
     */
    @Override
    public List<BizNoRuleResVO> selectBizNoRuleList(BizNoRuleQueryReqDTO query) {
        List<JstBizNoRule> rows = jstBizNoRuleMapper.selectJstBizNoRuleList(query);
        List<BizNoRuleResVO> result = new ArrayList<>(rows == null ? 0 : rows.size());
        if (rows == null) {
            return result;
        }
        for (JstBizNoRule row : rows) {
            result.add(toResVO(row));
        }
        return result;
    }

    /**
     * 查询编号规则详情。
     *
     * @param ruleId 规则ID
     * @return 规则详情
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:query
     */
    @Override
    public BizNoRuleResVO selectBizNoRuleById(Long ruleId) {
        JstBizNoRule rule = requireRule(ruleId);
        return toResVO(rule);
    }

    /**
     * 新增编号规则。
     *
     * @param req 新增请求
     * @return 新规则ID
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:add
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "编号规则", action = "RULE_CREATE", target = "#{req.ruleCode}")
    public Long addBizNoRule(BizNoRuleSaveReqDTO req) {
        // TX: JstBizNoRuleServiceImpl.addBizNoRule
        assertUniqueRuleCode(req.getRuleCode(), null);
        JstBizNoRule rule = new JstBizNoRule();
        fillBaseFields(rule, req.getRuleCode(), req.getRuleName(), req.getPrefix(), req.getDateFormat(),
                req.getSeqLength(), req.getSeqStart(), req.getDescription(), req.getStatus());
        Date now = DateUtils.getNowDate();
        rule.setCreateBy(operatorName());
        rule.setCreateTime(now);
        rule.setUpdateBy(operatorName());
        rule.setUpdateTime(now);
        int inserted = jstBizNoRuleMapper.insertJstBizNoRule(rule);
        if (inserted <= 0 || rule.getRuleId() == null) {
            throw new ServiceException("新增编号规则失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return rule.getRuleId();
    }

    /**
     * 修改编号规则。
     *
     * @param req 修改请求
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:edit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "编号规则", action = "RULE_UPDATE", target = "#{req.ruleId}")
    public void editBizNoRule(BizNoRuleUpdateReqDTO req) {
        // TX: JstBizNoRuleServiceImpl.editBizNoRule
        JstBizNoRule current = requireRule(req.getRuleId());
        assertUniqueRuleCode(req.getRuleCode(), req.getRuleId());

        current.setRuleCode(req.getRuleCode());
        current.setRuleName(req.getRuleName());
        current.setPrefix(normalizePrefix(req.getPrefix()));
        current.setDateFormat(req.getDateFormat());
        current.setSeqLength(req.getSeqLength());
        current.setSeqStart(req.getSeqStart());
        current.setDescription(req.getDescription());
        current.setStatus(req.getStatus());
        current.setUpdateBy(operatorName());
        current.setUpdateTime(DateUtils.getNowDate());

        int updated = jstBizNoRuleMapper.updateJstBizNoRule(current);
        if (updated <= 0) {
            throw new ServiceException("修改编号规则失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    /**
     * 删除编号规则。
     *
     * @param ruleIds 规则ID数组
     * @关联表 jst_biz_no_rule
     * @关联权限 jst:admin:bizNoRule:remove
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "编号规则", action = "RULE_DELETE", target = "#{ruleIds}")
    public void removeBizNoRule(Long[] ruleIds) {
        // TX: JstBizNoRuleServiceImpl.removeBizNoRule
        if (ruleIds == null || ruleIds.length == 0) {
            throw new ServiceException("ruleIds 不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        int deleted = jstBizNoRuleMapper.deleteJstBizNoRuleByRuleIds(ruleIds);
        if (deleted <= 0) {
            throw new ServiceException("删除编号规则失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    private void assertUniqueRuleCode(String ruleCode, Long selfRuleId) {
        JstBizNoRule existing = jstBizNoRuleMapper.selectByRuleCode(ruleCode);
        if (existing == null) {
            return;
        }
        if (selfRuleId != null && selfRuleId.equals(existing.getRuleId())) {
            return;
        }
        throw new ServiceException("规则编码已存在: " + ruleCode, BizErrorCode.JST_COMMON_BIZ_NO_RULE_CODE_DUPLICATE.code());
    }

    private JstBizNoRule requireRule(Long ruleId) {
        JstBizNoRule rule = jstBizNoRuleMapper.selectJstBizNoRuleByRuleId(ruleId);
        if (rule == null) {
            throw new ServiceException("编号规则不存在: " + ruleId, BizErrorCode.JST_COMMON_BIZ_NO_RULE_NOT_FOUND.code());
        }
        return rule;
    }

    private BizNoRuleResVO toResVO(JstBizNoRule rule) {
        BizNoRuleResVO vo = new BizNoRuleResVO();
        vo.setRuleId(rule.getRuleId());
        vo.setRuleCode(rule.getRuleCode());
        vo.setRuleName(rule.getRuleName());
        vo.setPrefix(rule.getPrefix());
        vo.setDateFormat(rule.getDateFormat());
        vo.setSeqLength(rule.getSeqLength());
        vo.setSeqStart(rule.getSeqStart());
        vo.setDescription(rule.getDescription());
        vo.setStatus(rule.getStatus());
        vo.setCreateTime(rule.getCreateTime());
        vo.setUpdateTime(rule.getUpdateTime());
        return vo;
    }

    private void fillBaseFields(JstBizNoRule rule,
                                String ruleCode,
                                String ruleName,
                                String prefix,
                                String dateFormat,
                                Integer seqLength,
                                Long seqStart,
                                String description,
                                Integer status) {
        rule.setRuleCode(ruleCode);
        rule.setRuleName(ruleName);
        rule.setPrefix(normalizePrefix(prefix));
        rule.setDateFormat(dateFormat);
        rule.setSeqLength(seqLength);
        rule.setSeqStart(seqStart);
        rule.setDescription(description);
        rule.setStatus(status);
    }

    private String normalizePrefix(String prefix) {
        return StringUtils.isBlank(prefix) ? "" : prefix;
    }

    private String operatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }
}
