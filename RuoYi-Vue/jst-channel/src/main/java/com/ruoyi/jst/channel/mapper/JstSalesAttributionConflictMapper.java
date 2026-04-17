package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesAttributionConflict;
import java.util.List;

public interface JstSalesAttributionConflictMapper {
    JstSalesAttributionConflict selectJstSalesAttributionConflictByConflictId(Long conflictId);
    List<JstSalesAttributionConflict> selectJstSalesAttributionConflictList(JstSalesAttributionConflict query);
    int insertJstSalesAttributionConflict(JstSalesAttributionConflict row);
    int updateJstSalesAttributionConflict(JstSalesAttributionConflict row);
    int deleteJstSalesAttributionConflictByConflictId(Long conflictId);
}
