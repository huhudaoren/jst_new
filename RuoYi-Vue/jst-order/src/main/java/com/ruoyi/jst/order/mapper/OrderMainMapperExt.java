package com.ruoyi.jst.order.mapper;

import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.dto.OrderQueryReqDTO;
import com.ruoyi.jst.order.vo.OrderDetailVO;
import com.ruoyi.jst.order.vo.OrderListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 订单主扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface OrderMainMapperExt {

    JstOrderMain selectByOrderNo(@Param("orderNo") String orderNo);

    int updateStatusByExpected(@Param("orderId") Long orderId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("targetStatus") String targetStatus,
                               @Param("payTime") Date payTime,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    int updateRefundByExpected(@Param("orderId") Long orderId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("targetStatus") String targetStatus,
                               @Param("expectedRefundStatus") String expectedRefundStatus,
                               @Param("targetRefundStatus") String targetRefundStatus,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    List<OrderListVO> selectWxList(OrderQueryReqDTO query);

    List<OrderListVO> selectAdminList(OrderQueryReqDTO query);

    OrderDetailVO selectDetail(@Param("orderId") Long orderId);
}
