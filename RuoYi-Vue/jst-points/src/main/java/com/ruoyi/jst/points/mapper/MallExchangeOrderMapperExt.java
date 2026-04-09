package com.ruoyi.jst.points.mapper;

import com.ruoyi.jst.points.dto.ExchangeQueryReqDTO;
import com.ruoyi.jst.points.vo.ExchangeDetailVO;
import com.ruoyi.jst.points.vo.ExchangeListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 商城兑换订单扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface MallExchangeOrderMapperExt {

    List<ExchangeListVO> selectAdminList(ExchangeQueryReqDTO query);

    List<ExchangeListVO> selectWxList(@Param("userId") Long userId, @Param("status") String status);

    ExchangeDetailVO selectDetail(@Param("exchangeId") Long exchangeId);

    int updateStatusByExpected(@Param("exchangeId") Long exchangeId,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("targetStatus") String targetStatus,
                               @Param("orderId") Long orderId,
                               @Param("updateBy") String updateBy,
                               @Param("updateTime") Date updateTime);

    int shipByExpected(@Param("exchangeId") Long exchangeId,
                       @Param("expectedStatus") String expectedStatus,
                       @Param("targetStatus") String targetStatus,
                       @Param("logisticsCompany") String logisticsCompany,
                       @Param("logisticsNo") String logisticsNo,
                       @Param("shipTime") Date shipTime,
                       @Param("updateBy") String updateBy,
                       @Param("updateTime") Date updateTime);

    int completeByExpected(@Param("exchangeId") Long exchangeId,
                           @Param("expectedStatus") String expectedStatus,
                           @Param("targetStatus") String targetStatus,
                           @Param("completeTime") Date completeTime,
                           @Param("updateBy") String updateBy,
                           @Param("updateTime") Date updateTime);
}
