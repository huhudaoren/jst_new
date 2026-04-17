package com.ruoyi.jst.channel.mapper;

import com.ruoyi.jst.channel.domain.JstSalesFollowupRecord;
import java.util.List;

public interface JstSalesFollowupRecordMapper {
    JstSalesFollowupRecord selectJstSalesFollowupRecordByRecordId(Long recordId);
    List<JstSalesFollowupRecord> selectJstSalesFollowupRecordList(JstSalesFollowupRecord query);
    int insertJstSalesFollowupRecord(JstSalesFollowupRecord row);
    int updateJstSalesFollowupRecord(JstSalesFollowupRecord row);
    int deleteJstSalesFollowupRecordByRecordId(Long recordId);
}
