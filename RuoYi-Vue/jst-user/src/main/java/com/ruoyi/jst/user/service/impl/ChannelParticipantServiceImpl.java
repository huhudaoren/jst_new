package com.ruoyi.jst.user.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.domain.Participant;
import com.ruoyi.jst.user.dto.ChannelParticipantUpdateReqDTO;
import com.ruoyi.jst.user.mapper.JstUserMapper;
import com.ruoyi.jst.user.mapper.ParticipantMapper;
import com.ruoyi.jst.user.service.ChannelParticipantService;
import com.ruoyi.jst.user.vo.ChannelParticipantResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChannelParticipantServiceImpl implements ChannelParticipantService {

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private JstUserMapper jstUserMapper;

    @Override
    public List<ChannelParticipantResVO> selectMyParticipants(Long userId, String status) {
        assertUserExists(userId);
        List<ChannelParticipantResVO> rows = participantMapper.selectChannelParticipantList(operatorName(userId), status);
        if (rows == null) {
            return new ArrayList<>();
        }
        rows.forEach(this::maskParticipant);
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "渠道临时档案", action = "CHANNEL_PARTICIPANT_UPDATE", target = "#{participantId}", recordResult = true)
    public ChannelParticipantResVO updateParticipant(Long userId, Long participantId, ChannelParticipantUpdateReqDTO req) {
        assertUserExists(userId);
        Participant participant = requireOwnedParticipant(userId, participantId);
        assertUnclaimed(participant);
        participant.setName(StringUtils.trim(req.getName()));
        participant.setGuardianMobile(resolveGuardianMobile(participant.getGuardianMobile(), req.getGuardianMobile()));
        participant.setSchool(StringUtils.trimToNull(req.getSchool()));
        participant.setClassName(StringUtils.trimToNull(req.getClassName()));
        participant.setUpdateBy(operatorName(userId));
        participant.setUpdateTime(DateUtils.getNowDate());
        if (participantMapper.updateChannelParticipant(participant) == 0) {
            throwDataTampered();
        }
        ChannelParticipantResVO detail = participantMapper.selectChannelParticipantDetail(participantId, operatorName(userId));
        if (detail == null) {
            throwDataTampered();
        }
        maskParticipant(detail);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "渠道临时档案", action = "CHANNEL_PARTICIPANT_DELETE", target = "#{participantId}")
    public void deleteParticipant(Long userId, Long participantId) {
        assertUserExists(userId);
        Participant participant = requireOwnedParticipant(userId, participantId);
        assertUnclaimed(participant);
        if (participantMapper.softDeleteChannelParticipant(participantId, operatorName(userId), operatorName(userId)) == 0) {
            throwDataTampered();
        }
    }

    private void maskParticipant(ChannelParticipantResVO vo) {
        String maskedMobile = MaskUtil.mobile(vo.getGuardianMobile());
        vo.setGuardianMobileMasked(maskedMobile);
        vo.setGuardianMobile(maskedMobile);
        if (StringUtils.isNotBlank(vo.getClaimedUserName())) {
            vo.setClaimedUserName(MaskUtil.realName(vo.getClaimedUserName()));
        }
    }

    private Participant requireOwnedParticipant(Long userId, Long participantId) {
        Participant participant = participantMapper.selectChannelOwnedTemporary(participantId, operatorName(userId));
        if (participant == null) {
            throw new ServiceException(BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.code());
        }
        return participant;
    }

    private void assertUnclaimed(Participant participant) {
        if (!"unclaimed".equals(participant.getClaimStatus())) {
            throw new ServiceException(BizErrorCode.JST_USER_CLAIM_ILLEGAL_TRANSIT.message(),
                    BizErrorCode.JST_USER_CLAIM_ILLEGAL_TRANSIT.code());
        }
    }

    private String resolveGuardianMobile(String currentMobile, String requestMobile) {
        String trimmed = StringUtils.trimToNull(requestMobile);
        if (trimmed == null || trimmed.contains("*")) {
            return currentMobile;
        }
        return trimmed;
    }

    private JstUser assertUserExists(Long userId) {
        JstUser user = jstUserMapper.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        return user;
    }

    private String operatorName(Long userId) {
        return String.valueOf(userId);
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }
}
