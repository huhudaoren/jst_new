package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesPreRegister;
import java.util.List;

public interface JstSalesPreRegisterMapper {
    JstSalesPreRegister selectJstSalesPreRegisterByPreId(Long preId);
    List<JstSalesPreRegister> selectJstSalesPreRegisterList(JstSalesPreRegister query);
    int insertJstSalesPreRegister(JstSalesPreRegister row);
    int updateJstSalesPreRegister(JstSalesPreRegister row);
    int deleteJstSalesPreRegisterByPreId(Long preId);
}
