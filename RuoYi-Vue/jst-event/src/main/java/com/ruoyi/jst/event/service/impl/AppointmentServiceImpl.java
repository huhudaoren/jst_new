package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.event.mapper.AppointmentRecordMapperExt;
import com.ruoyi.jst.event.mapper.WriteoffItemMapperExt;
import com.ruoyi.jst.event.service.AppointmentService;
import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 个人预约查询服务实现。
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired private AppointmentRecordMapperExt appointmentRecordMapperExt;
    @Autowired private WriteoffItemMapperExt writeoffItemMapperExt;

    @Override
    public List<AppointmentListVO> selectMyList(Long userId) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return appointmentRecordMapperExt.selectMyList(userId);
    }

    @Override
    public AppointmentDetailVO getMyDetail(Long userId, Long appointmentId) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        AppointmentDetailVO detail = appointmentRecordMapperExt.selectMyDetail(userId, appointmentId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_NOT_FOUND.code());
        }
        detail.setWriteoffItems(writeoffItemMapperExt.selectByAppointmentId(appointmentId));
        return detail;
    }
}
