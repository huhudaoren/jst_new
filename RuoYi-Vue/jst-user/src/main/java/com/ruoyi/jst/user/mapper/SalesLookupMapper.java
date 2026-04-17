package com.ruoyi.jst.user.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 跨域查询：根据 sys_user_id 反查 sales_id（避免 jst-user 直接依赖 jst-channel 模块）。
 * <p>
 * 由 JstLoginEnricherImpl 在每个请求 preHandle 阶段调用，性能：走 uk_sys_user 索引，&lt; 1ms。
 *
 * @author jst
 * @since 1.0.0
 */
public interface SalesLookupMapper {
    Long selectSalesIdByUserId(@Param("userId") Long userId);
}
