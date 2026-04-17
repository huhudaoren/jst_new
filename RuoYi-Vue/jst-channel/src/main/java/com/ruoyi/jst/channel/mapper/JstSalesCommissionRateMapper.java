package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesCommissionRate;
import java.util.List;

public interface JstSalesCommissionRateMapper {
    JstSalesCommissionRate selectJstSalesCommissionRateById(Long id);
    List<JstSalesCommissionRate> selectJstSalesCommissionRateList(JstSalesCommissionRate query);
    int insertJstSalesCommissionRate(JstSalesCommissionRate row);
    int updateJstSalesCommissionRate(JstSalesCommissionRate row);
    int deleteJstSalesCommissionRateById(Long id);
}
