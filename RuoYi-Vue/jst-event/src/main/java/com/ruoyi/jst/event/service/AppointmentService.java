package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;

import java.util.List;

/**
 * 个人预约查询服务。
 */
public interface AppointmentService {

    List<AppointmentListVO> selectMyList(Long userId);

    AppointmentDetailVO getMyDetail(Long userId, Long appointmentId);
}
