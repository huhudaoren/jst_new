package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesCommissionRate;
import java.util.List;

public interface JstSalesCommissionRateMapper {
    JstSalesCommissionRate selectJstSalesCommissionRateById(Long id);
    List<JstSalesCommissionRate> selectJstSalesCommissionRateList(JstSalesCommissionRate query);
    int insertJstSalesCommissionRate(JstSalesCommissionRate row);
    int updateJstSalesCommissionRate(JstSalesCommissionRate row);
    int deleteJstSalesCommissionRateById(Long id);

    java.math.BigDecimal selectLatestRate(@org.apache.ibatis.annotations.Param("salesId") Long salesId,
                                          @org.apache.ibatis.annotations.Param("businessType") String businessType,
                                          @org.apache.ibatis.annotations.Param("atTime") java.util.Date atTime);
}
