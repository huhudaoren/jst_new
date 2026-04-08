package com.ruoyi.jst.order.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * 返点规则轻量查询 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface RebateRuleLookupMapper {

    Map<String, Object> selectMatchedRule(@Param("contestId") Long contestId,
                                          @Param("channelId") Long channelId,
                                          @Param("now") Date now);
}
