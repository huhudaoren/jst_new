package com.ruoyi.jst.common.mapper;

import com.ruoyi.jst.common.domain.JstBizNoSeq;
import org.apache.ibatis.annotations.Param;

/**
 * 业务编号日序列 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface JstBizNoSeqMapper {

    /**
     * 查询指定规则在当天的序列快照。
     *
     * @param ruleCode 规则编码
     * @param dateKey 日期键
     * @return 序列快照
     */
    JstBizNoSeq selectByRuleCodeAndDateKey(@Param("ruleCode") String ruleCode,
                                           @Param("dateKey") String dateKey);

    /**
     * upsert 当天序列值。
     *
     * @param ruleCode 规则编码
     * @param dateKey 日期键
     * @param currentSeq 当前序号
     * @return 影响行数
     */
    int upsertCurrentSeq(@Param("ruleCode") String ruleCode,
                         @Param("dateKey") String dateKey,
                         @Param("currentSeq") Long currentSeq);
}
