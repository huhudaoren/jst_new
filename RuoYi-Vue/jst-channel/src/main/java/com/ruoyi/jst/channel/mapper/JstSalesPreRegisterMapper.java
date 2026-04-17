package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesPreRegister;
import java.util.List;

public interface JstSalesPreRegisterMapper {
    JstSalesPreRegister selectJstSalesPreRegisterByPreId(Long preId);
    List<JstSalesPreRegister> selectJstSalesPreRegisterList(JstSalesPreRegister query);
    int insertJstSalesPreRegister(JstSalesPreRegister row);
    int updateJstSalesPreRegister(JstSalesPreRegister row);
    int deleteJstSalesPreRegisterByPreId(Long preId);

    JstSalesPreRegister selectByPhonePending(@org.apache.ibatis.annotations.Param("phone") String phone);
    java.util.List<Long> selectExpiredPending();
    int markExpiredBatch(@org.apache.ibatis.annotations.Param("ids") java.util.List<Long> ids);
    int countActiveBySales(@org.apache.ibatis.annotations.Param("salesId") Long salesId);
    java.util.List<JstSalesPreRegister> selectMineByStatus(@org.apache.ibatis.annotations.Param("salesId") Long salesId,
                                                           @org.apache.ibatis.annotations.Param("status") String status);
    int markMatched(@org.apache.ibatis.annotations.Param("preId") Long preId,
                    @org.apache.ibatis.annotations.Param("channelId") Long channelId);
    int expirePendingBySales(@org.apache.ibatis.annotations.Param("salesId") Long salesId);
}
