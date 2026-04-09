package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.EventAppointmentRecord;
import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 预约记录扩展 Mapper。
 */
public interface AppointmentRecordMapperExt {

    int insertAppointmentRecord(@Param("record") EventAppointmentRecord record);

    EventAppointmentRecord selectById(@Param("appointmentId") Long appointmentId);

    EventAppointmentRecord selectMyRecord(@Param("userId") Long userId,
                                          @Param("appointmentId") Long appointmentId);

    List<AppointmentListVO> selectMyList(@Param("userId") Long userId);

    AppointmentDetailVO selectMyDetail(@Param("userId") Long userId,
                                       @Param("appointmentId") Long appointmentId);

    int updateMainStatus(@Param("appointmentId") Long appointmentId,
                         @Param("expectedStatus") String expectedStatus,
                         @Param("targetStatus") String targetStatus,
                         @Param("updateBy") String updateBy,
                         @Param("updateTime") Date updateTime);

    int countAllItems(@Param("appointmentId") Long appointmentId);

    int countUsedItems(@Param("appointmentId") Long appointmentId);

    int countBookedIndividuals(@Param("contestId") Long contestId,
                               @Param("appointmentDate") Date appointmentDate,
                               @Param("sessionCode") String sessionCode);
}
