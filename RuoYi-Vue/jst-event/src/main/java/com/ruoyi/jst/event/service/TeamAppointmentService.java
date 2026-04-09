package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.TeamAppointmentApplyDTO;
import com.ruoyi.jst.event.dto.TeamAppointmentQueryDTO;
import com.ruoyi.jst.event.vo.TeamAppointmentApplyResVO;
import com.ruoyi.jst.event.vo.TeamAppointmentDetailVO;
import com.ruoyi.jst.event.vo.TeamAppointmentListVO;

import java.util.List;

/**
 * 团队预约服务。
 */
public interface TeamAppointmentService {

    TeamAppointmentApplyResVO apply(Long channelId, Long operatorUserId, TeamAppointmentApplyDTO req);

    List<TeamAppointmentListVO> selectChannelList(Long channelId, TeamAppointmentQueryDTO query);

    TeamAppointmentDetailVO getChannelDetail(Long channelId, Long teamAppointmentId);
}
