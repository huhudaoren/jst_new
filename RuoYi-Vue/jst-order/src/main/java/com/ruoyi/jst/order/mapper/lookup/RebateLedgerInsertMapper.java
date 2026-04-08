package com.ruoyi.jst.order.mapper.lookup;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 返点台账写入 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface RebateLedgerInsertMapper {

    int insertLedger(@Param("orderId") Long orderId,
                     @Param("itemId") Long itemId,
                     @Param("channelId") Long channelId,
                     @Param("contestId") Long contestId,
                     @Param("ruleId") Long ruleId,
                     @Param("listAmount") BigDecimal listAmount,
                     @Param("netPayAmount") BigDecimal netPayAmount,
                     @Param("serviceFee") BigDecimal serviceFee,
                     @Param("rebateBase") BigDecimal rebateBase,
                     @Param("rebateAmount") BigDecimal rebateAmount,
                     @Param("eventEndTime") Date eventEndTime,
                     @Param("createBy") String createBy,
                     @Param("createTime") Date createTime,
                     @Param("remark") String remark);
}
