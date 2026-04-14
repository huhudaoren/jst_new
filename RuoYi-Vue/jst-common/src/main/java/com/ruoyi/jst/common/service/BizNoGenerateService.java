package com.ruoyi.jst.common.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.domain.JstBizNoRule;
import com.ruoyi.jst.common.domain.JstBizNoSeq;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.mapper.JstBizNoRuleMapper;
import com.ruoyi.jst.common.mapper.JstBizNoSeqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 统一业务编号生成服务。
 * <p>
 * 格式：prefix + dateKey + "-" + paddedSeq。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class BizNoGenerateService {

    private static final long BIZ_NO_KEY_TTL_HOURS = 48L;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private JstBizNoRuleMapper jstBizNoRuleMapper;

    @Autowired
    private JstBizNoSeqMapper jstBizNoSeqMapper;

    /**
     * 生成下一个业务编号。
     *
     * @param ruleCode 规则编码
     * @return 业务编号，例如 JST-CERT-20260413-0001
     * @关联表 jst_biz_no_rule / jst_biz_no_seq
     */
    @Transactional(rollbackFor = Exception.class)
    public String nextNo(String ruleCode) {
        // TX: BizNoGenerateService.nextNo
        if (StringUtils.isBlank(ruleCode)) {
            throw new ServiceException("ruleCode 不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        JstBizNoRule rule = jstBizNoRuleMapper.selectByRuleCode(ruleCode);
        if (rule == null) {
            throw new ServiceException("编号规则不存在: " + ruleCode, BizErrorCode.JST_COMMON_BIZ_NO_RULE_NOT_FOUND.code());
        }
        if (!Integer.valueOf(1).equals(rule.getStatus())) {
            throw new ServiceException("编号规则未启用: " + ruleCode, BizErrorCode.JST_COMMON_BIZ_NO_RULE_DISABLED.code());
        }

        String dateKey;
        try {
            dateKey = DateUtils.dateTimeNow(rule.getDateFormat());
        } catch (Exception e) {
            throw new ServiceException("编号规则日期格式不合法: " + ruleCode, BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        String redisKey = buildRedisKey(ruleCode, dateKey);
        long seq = nextSequence(rule, dateKey, redisKey);
        jstBizNoSeqMapper.upsertCurrentSeq(ruleCode, dateKey, seq);

        String seqStr = String.format("%0" + rule.getSeqLength() + "d", seq);
        return defaultPrefix(rule.getPrefix()) + dateKey + "-" + seqStr;
    }

    private long nextSequence(JstBizNoRule rule, String dateKey, String redisKey) {
        boolean exists = Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
        if (!exists) {
            long initBase = initialBaseValue(rule, dateKey);
            redisTemplate.opsForValue().setIfAbsent(redisKey, initBase, BIZ_NO_KEY_TTL_HOURS, TimeUnit.HOURS);
        }
        Long seq = redisTemplate.opsForValue().increment(redisKey);
        if (seq == null || seq <= 0) {
            throw new ServiceException("生成编号失败: Redis 递增异常", BizErrorCode.JST_COMMON_BIZ_NO_GENERATE_FAILED.code());
        }
        return seq;
    }

    private long initialBaseValue(JstBizNoRule rule, String dateKey) {
        JstBizNoSeq snapshot = jstBizNoSeqMapper.selectByRuleCodeAndDateKey(rule.getRuleCode(), dateKey);
        if (snapshot != null && snapshot.getCurrentSeq() != null) {
            return snapshot.getCurrentSeq();
        }
        long seqStart = rule.getSeqStart() == null ? 1L : rule.getSeqStart();
        return Math.max(seqStart - 1L, 0L);
    }

    private String buildRedisKey(String ruleCode, String dateKey) {
        return "jst:biz_no:" + ruleCode + ":" + dateKey;
    }

    private String defaultPrefix(String prefix) {
        return prefix == null ? "" : prefix;
    }
}
