package com.ruoyi.jst.order.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 学生绑定关系轻量查询 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface BindingLookupMapper {

    Map<String, Object> selectActiveBinding(@Param("userId") Long userId);
}
