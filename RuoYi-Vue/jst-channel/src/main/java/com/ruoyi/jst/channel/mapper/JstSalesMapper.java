package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSales;
import java.util.List;

public interface JstSalesMapper {
    JstSales selectJstSalesBySalesId(Long salesId);
    List<JstSales> selectJstSalesList(JstSales query);
    int insertJstSales(JstSales row);
    int updateJstSales(JstSales row);
    int deleteJstSalesBySalesId(Long salesId);
}
