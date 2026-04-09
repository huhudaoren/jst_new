package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.WriteoffRecordQueryDTO;
import com.ruoyi.jst.event.dto.WriteoffScanDTO;
import com.ruoyi.jst.event.vo.WriteoffRecordVO;
import com.ruoyi.jst.event.vo.WriteoffScanResVO;

import java.util.List;

/**
 * 扫码核销服务。
 */
public interface WriteoffService {

    WriteoffScanResVO scan(Long operatorUserId, WriteoffScanDTO req);

    List<WriteoffRecordVO> selectRecords(WriteoffRecordQueryDTO query);
}
