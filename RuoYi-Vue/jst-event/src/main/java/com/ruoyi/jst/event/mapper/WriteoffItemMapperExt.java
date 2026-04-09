package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.EventAppointmentWriteoffItem;
import com.ruoyi.jst.event.dto.WriteoffRecordQueryDTO;
import com.ruoyi.jst.event.vo.AppointmentWriteoffItemVO;
import com.ruoyi.jst.event.vo.WriteoffRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 核销子项扩展 Mapper。
 */
public interface WriteoffItemMapperExt {

    int insertWriteoffItem(@Param("item") EventAppointmentWriteoffItem item);

    EventAppointmentWriteoffItem selectById(@Param("writeoffItemId") Long writeoffItemId);

    Map<String, Object> selectScanContextByQrCode(@Param("qrCode") String qrCode);

    int updateUsed(@Param("writeoffItemId") Long writeoffItemId,
                   @Param("expectedStatus") String expectedStatus,
                   @Param("expectedWriteoffQty") Long expectedWriteoffQty,
                   @Param("targetWriteoffQty") Long targetWriteoffQty,
                   @Param("writeoffUserId") Long writeoffUserId,
                   @Param("writeoffTerminal") String writeoffTerminal,
                   @Param("writeoffTime") Date writeoffTime,
                   @Param("updateBy") String updateBy,
                   @Param("updateTime") Date updateTime);

    List<AppointmentWriteoffItemVO> selectByAppointmentId(@Param("appointmentId") Long appointmentId);

    List<WriteoffRecordVO> selectRecordList(@Param("partnerId") Long partnerId,
                                            @Param("query") WriteoffRecordQueryDTO query);
}
