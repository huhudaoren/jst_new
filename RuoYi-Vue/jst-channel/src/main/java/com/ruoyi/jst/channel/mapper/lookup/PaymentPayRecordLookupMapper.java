package com.ruoyi.jst.channel.mapper.lookup;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Lookup mapper for finance pay records.
 */
@Mapper
public interface PaymentPayRecordLookupMapper {

    int insertPayRecord(@Param("payNo") String payNo,
                        @Param("businessType") String businessType,
                        @Param("businessId") Long businessId,
                        @Param("targetType") String targetType,
                        @Param("targetId") Long targetId,
                        @Param("amount") BigDecimal amount,
                        @Param("payAccount") String payAccount,
                        @Param("payTime") Date payTime,
                        @Param("voucherUrl") String voucherUrl,
                        @Param("operatorId") Long operatorId,
                        @Param("createBy") String createBy,
                        @Param("createTime") Date createTime,
                        @Param("updateBy") String updateBy,
                        @Param("updateTime") Date updateTime,
                        @Param("remark") String remark);
}
