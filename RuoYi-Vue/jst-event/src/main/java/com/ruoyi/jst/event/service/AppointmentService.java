package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.AppointmentApplyDTO;
import com.ruoyi.jst.event.vo.AppointmentApplyResVO;
import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;
import com.ruoyi.jst.event.vo.AppointmentRemainingVO;

import java.util.Date;
import java.util.List;

/**
 * 个人预约服务。
 */
public interface AppointmentService {

    AppointmentApplyResVO applyIndividual(Long userId, AppointmentApplyDTO req);

    String cancelIndividual(Long userId, Long appointmentId);

    AppointmentRemainingVO getRemaining(Long contestId, Date appointmentDate, String sessionCode);

    List<AppointmentListVO> selectMyList(Long userId);

    AppointmentDetailVO getMyDetail(Long userId, Long appointmentId);

    /**
     * 系统任务自动过期预约。
     *
     * @param appointmentId 预约ID
     * @return true=成功过期；false=状态已变化
     */
    boolean expireBySystem(Long appointmentId);
}
