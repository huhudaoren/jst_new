package com.ruoyi.jst.user.service;

import com.ruoyi.jst.user.dto.ChannelParticipantUpdateReqDTO;
import com.ruoyi.jst.user.vo.ChannelParticipantResVO;

import java.util.List;

public interface ChannelParticipantService {

    List<ChannelParticipantResVO> selectMyParticipants(Long userId, String status);

    ChannelParticipantResVO updateParticipant(Long userId, Long participantId, ChannelParticipantUpdateReqDTO req);

    void deleteParticipant(Long userId, Long participantId);
}
