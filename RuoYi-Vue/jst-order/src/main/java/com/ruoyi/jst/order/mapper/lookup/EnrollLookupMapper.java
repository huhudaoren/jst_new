package com.ruoyi.jst.order.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.Map;

/**
 * 报名订单关联轻量查询 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface EnrollLookupMapper {

    Map<String, Object> selectEnrollForOrder(@Param("enrollId") Long enrollId);

    int bindOrder(@Param("enrollId") Long enrollId,
                  @Param("orderId") Long orderId,
                  @Param("updateBy") String updateBy,
                  @Param("updateTime") Date updateTime);

    int clearOrder(@Param("enrollId") Long enrollId,
                   @Param("orderId") Long orderId,
                   @Param("updateBy") String updateBy,
                   @Param("updateTime") Date updateTime);

    int cancelEnrollByOrderId(@Param("orderId") Long orderId,
                              @Param("expectedAuditStatus") String expectedAuditStatus,
                              @Param("targetAuditStatus") String targetAuditStatus,
                              @Param("updateBy") String updateBy,
                              @Param("updateTime") Date updateTime);
}
