package com.ruoyi.jst.channel.mapper.lookup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Read-only contest lookup mapper.
 */
@Mapper
public interface ContestLookupMapper {

    List<Map<String, Object>> selectContestNamesByIds(@Param("contestIds") List<Long> contestIds);
}
