package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesAttributionConflict;
import java.util.List;

public interface JstSalesAttributionConflictMapper {
    JstSalesAttributionConflict selectJstSalesAttributionConflictByConflictId(Long conflictId);
    List<JstSalesAttributionConflict> selectJstSalesAttributionConflictList(JstSalesAttributionConflict query);
    int insertJstSalesAttributionConflict(JstSalesAttributionConflict row);
    int updateJstSalesAttributionConflict(JstSalesAttributionConflict row);
    int deleteJstSalesAttributionConflictByConflictId(Long conflictId);

    /** 按状态查冲突列表（admin 用） */
    List<JstSalesAttributionConflict> selectByStatus(@org.apache.ibatis.annotations.Param("status") String status);
}
