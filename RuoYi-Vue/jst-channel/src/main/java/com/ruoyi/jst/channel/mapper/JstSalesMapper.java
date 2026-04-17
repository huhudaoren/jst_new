package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSales;
import java.util.List;

public interface JstSalesMapper {
    JstSales selectJstSalesBySalesId(Long salesId);
    List<JstSales> selectJstSalesList(JstSales query);
    int insertJstSales(JstSales row);
    int updateJstSales(JstSales row);
    int deleteJstSalesBySalesId(Long salesId);

    JstSales selectBySysUserId(@org.apache.ibatis.annotations.Param("sysUserId") Long sysUserId);
    JstSales selectBySalesNo(@org.apache.ibatis.annotations.Param("salesNo") String salesNo);
    java.util.List<JstSales> selectByManagerId(@org.apache.ibatis.annotations.Param("managerId") Long managerId);
    java.util.List<Long> selectActiveTeamIds(@org.apache.ibatis.annotations.Param("managerId") Long managerId);
}
