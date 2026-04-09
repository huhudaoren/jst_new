package com.ruoyi.jst.marketing.mapper.lookup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Read-only user lookup mapper for jst-marketing.
 */
@Mapper
public interface MarketingUserLookupMapper {

    Map<String, Object> selectByUserId(@Param("userId") Long userId);

    List<Map<String, Object>> selectByUserIds(@Param("userIds") List<Long> userIds);
}
