package com.ruoyi.jst.event.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.order.api.OrderCreationFacade;
import com.ruoyi.jst.order.dto.AppointmentOrderCreateReqDTO;
import com.ruoyi.jst.order.vo.AppointmentOrderCreateResVO;
import com.ruoyi.jst.event.domain.EventAppointmentRecord;
import com.ruoyi.jst.event.domain.EventAppointmentWriteoffItem;
import com.ruoyi.jst.event.domain.EventTeamAppointment;
import com.ruoyi.jst.event.domain.EventTeamAppointmentMember;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.dto.TeamAppointmentApplyDTO;
import com.ruoyi.jst.event.dto.TeamAppointmentQueryDTO;
import com.ruoyi.jst.event.enums.AppointmentStatus;
import com.ruoyi.jst.event.enums.TeamAppointmentStatus;
import com.ruoyi.jst.event.enums.WriteoffItemStatus;
import com.ruoyi.jst.event.mapper.AppointmentRecordMapperExt;
import com.ruoyi.jst.event.mapper.ContestMapperExt;
import com.ruoyi.jst.event.mapper.TeamAppointmentMapperExt;
import com.ruoyi.jst.event.mapper.WriteoffItemMapperExt;
import com.ruoyi.jst.event.service.TeamAppointmentService;
import com.ruoyi.jst.event.util.WriteoffQrCodeGenerator;
import com.ruoyi.jst.event.vo.TeamAppointmentApplyResVO;
import com.ruoyi.jst.event.vo.TeamAppointmentDetailVO;
import com.ruoyi.jst.event.vo.TeamAppointmentListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 团队预约服务实现。
 */
@Service
public class TeamAppointmentServiceImpl implements TeamAppointmentService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired private ContestMapperExt contestMapperExt;
    @Autowired private TeamAppointmentMapperExt teamAppointmentMapperExt;
    @Autowired private AppointmentRecordMapperExt appointmentRecordMapperExt;
    @Autowired private WriteoffItemMapperExt writeoffItemMapperExt;
    @Autowired private OrderCreationFacade orderCreationFacade;
    @Autowired private JstLockTemplate jstLockTemplate;
    @Autowired private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired private WriteoffQrCodeGenerator writeoffQrCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "TEAM_APPOINTMENT_APPLY", target = "#{req.contestId}", recordResult = true)
    public TeamAppointmentApplyResVO apply(Long channelId, Long operatorUserId, TeamAppointmentApplyDTO req) {
        if (channelId == null || operatorUserId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        if (req == null || req.getMembers() == null || req.getMembers().isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstContest contest = requireContest(req.getContestId());
        validateContestForAppointment(contest, req.getAppointmentDate());
        List<MemberSnapshot> members = resolveMembers(req.getMembers());
        List<WriteoffConfigItem> configItems = parseWriteoffConfig(contest.getWriteoffConfig());
        String operator = currentOperator();
        return jstLockTemplate.execute("lock:appointment:book:" + contest.getContestId() + ":" + req.getSessionCode(),
                3, 5, () -> doApply(contest, channelId, operatorUserId, operator, req, members, configItems));
    }

    @Override
    public List<TeamAppointmentListVO> selectChannelList(Long channelId, TeamAppointmentQueryDTO query) {
        if (channelId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return teamAppointmentMapperExt.selectChannelList(channelId, query);
    }

    @Override
    public TeamAppointmentDetailVO getChannelDetail(Long channelId, Long teamAppointmentId) {
        if (channelId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        TeamAppointmentDetailVO detail = teamAppointmentMapperExt.selectChannelDetail(channelId, teamAppointmentId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_NOT_FOUND.code());
        }
        detail.setMembers(teamAppointmentMapperExt.selectChannelMembers(teamAppointmentId));
        return detail;
    }

    private TeamAppointmentApplyResVO doApply(JstContest contest, Long channelId, Long operatorUserId, String operator,
                                              TeamAppointmentApplyDTO req, List<MemberSnapshot> members,
                                              List<WriteoffConfigItem> configItems) {
        if (contest.getAllowRepeatAppointment() == null || contest.getAllowRepeatAppointment() == 0) {
            for (MemberSnapshot member : members) {
                if (teamAppointmentMapperExt.countDuplicateAppointment(contest.getContestId(), member.participantId) > 0) {
                    throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_REPEAT_DENIED.message(),
                            BizErrorCode.JST_ORDER_APPOINTMENT_REPEAT_DENIED.code());
                }
            }
        }
        int bookedPersons = teamAppointmentMapperExt.countBookedPersons(contest.getContestId(),
                req.getAppointmentDate(), req.getSessionCode());
        int memberCount = members.size();
        if (contest.getAppointmentCapacity() != null && contest.getAppointmentCapacity() > 0
                && bookedPersons + memberCount > contest.getAppointmentCapacity()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_CAPACITY_FULL.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_CAPACITY_FULL.code());
        }

        Date now = DateUtils.getNowDate();
        EventTeamAppointment team = new EventTeamAppointment();
        team.setTeamNo(snowflakeIdWorker.nextBizNo("TA"));
        team.setContestId(contest.getContestId());
        team.setChannelId(channelId);
        team.setTeamName(req.getTeamName());
        team.setAppointmentDate(req.getAppointmentDate());
        team.setSessionCode(req.getSessionCode());
        team.setTotalPersons((long) memberCount);
        team.setMemberPersons((long) memberCount);
        team.setExtraPersons(0L);
        team.setExtraListJson(null);
        team.setWriteoffPersons(0L);
        team.setStatus(TeamAppointmentStatus.BOOKED.dbValue());
        team.setCreateBy(operator);
        team.setCreateTime(now);
        team.setUpdateBy(operator);
        team.setUpdateTime(now);
        team.setDelFlag("0");
        teamAppointmentMapperExt.insertTeamAppointment(team);

        List<Long> subOrderIds = new ArrayList<>();
        for (MemberSnapshot member : members) {
            AppointmentOrderCreateResVO orderRes = createSubOrder(contest, team, member, operatorUserId, operator);
            subOrderIds.add(orderRes.getOrderId());

            EventTeamAppointmentMember teamMember = new EventTeamAppointmentMember();
            teamMember.setTeamAppointmentId(team.getTeamAppointmentId());
            teamMember.setUserId(member.userId);
            teamMember.setParticipantId(member.participantId);
            teamMember.setMemberNo(snowflakeIdWorker.nextBizNo("TM"));
            teamMember.setNameSnapshot(member.memberName);
            teamMember.setMobileSnapshot(member.mobile);
            teamMember.setSubOrderId(orderRes.getOrderId());
            teamMember.setWriteoffStatus(WriteoffItemStatus.UNUSED.dbValue());
            teamMember.setCreateBy(operator);
            teamMember.setCreateTime(now);
            teamMember.setUpdateBy(operator);
            teamMember.setUpdateTime(now);
            teamMember.setDelFlag("0");
            teamAppointmentMapperExt.insertTeamMember(teamMember);

            EventAppointmentRecord record = new EventAppointmentRecord();
            record.setAppointmentNo(snowflakeIdWorker.nextBizNo("AP"));
            record.setContestId(contest.getContestId());
            record.setUserId(member.userId);
            record.setParticipantId(member.participantId);
            record.setChannelId(channelId);
            record.setAppointmentType("team");
            record.setTeamAppointmentId(team.getTeamAppointmentId());
            record.setAppointmentDate(req.getAppointmentDate());
            record.setSessionCode(req.getSessionCode());
            record.setOrderId(orderRes.getOrderId());
            record.setMainStatus(AppointmentStatus.BOOKED.dbValue());
            record.setQrCode(writeoffQrCodeGenerator.generateToken());
            record.setCreateBy(operator);
            record.setCreateTime(now);
            record.setUpdateBy(operator);
            record.setUpdateTime(now);
            record.setDelFlag("0");
            appointmentRecordMapperExt.insertAppointmentRecord(record);

            for (WriteoffConfigItem configItem : configItems) {
                EventAppointmentWriteoffItem item = new EventAppointmentWriteoffItem();
                item.setAppointmentId(record.getAppointmentId());
                item.setTeamAppointmentId(team.getTeamAppointmentId());
                item.setItemType(configItem.itemType);
                item.setItemName(configItem.itemName);
                item.setQrCode(writeoffQrCodeGenerator.generateToken());
                item.setStatus(WriteoffItemStatus.UNUSED.dbValue());
                item.setWriteoffQty(0L);
                item.setTotalQty(1L);
                item.setCreateBy(operator);
                item.setCreateTime(now);
                item.setUpdateBy(operator);
                item.setUpdateTime(now);
                item.setDelFlag("0");
                writeoffItemMapperExt.insertWriteoffItem(item);
            }
        }

        TeamAppointmentApplyResVO resVO = new TeamAppointmentApplyResVO();
        resVO.setTeamAppointmentId(team.getTeamAppointmentId());
        resVO.setTeamNo(team.getTeamNo());
        resVO.setMemberCount(memberCount);
        resVO.setSubOrderIds(subOrderIds);
        return resVO;
    }

    private AppointmentOrderCreateResVO createSubOrder(JstContest contest, EventTeamAppointment team,
                                                       MemberSnapshot member, Long operatorUserId, String operator) {
        AppointmentOrderCreateReqDTO orderReq = new AppointmentOrderCreateReqDTO();
        orderReq.setUserId(member.userId);
        orderReq.setParticipantId(member.participantId);
        orderReq.setChannelId(team.getChannelId());
        orderReq.setContestId(contest.getContestId());
        orderReq.setPartnerId(contest.getPartnerId());
        orderReq.setTeamAppointmentId(team.getTeamAppointmentId());
        orderReq.setRefId(member.participantId);
        orderReq.setListAmount(contest.getPrice() == null ? ZERO_AMOUNT : contest.getPrice());
        orderReq.setItemName(contest.getContestName() + "线下预约费");
        orderReq.setPayMethod(StringUtils.isBlank(member.payMethod) ? "wechat" : member.payMethod);
        orderReq.setPayInitiator("channel");
        orderReq.setPayInitiatorId(operatorUserId);
        orderReq.setAftersaleDeadline(calculateAftersaleDeadline(contest));
        orderReq.setOperator(operator);
        return orderCreationFacade.createAppointmentOrder(orderReq);
    }

    private Date calculateAftersaleDeadline(JstContest contest) {
        if (contest.getEventEndTime() != null && contest.getAftersaleDays() != null) {
            return DateUtils.addDays(contest.getEventEndTime(), contest.getAftersaleDays().intValue());
        }
        if (contest.getAftersaleDays() != null) {
            return DateUtils.addDays(DateUtils.getNowDate(), contest.getAftersaleDays().intValue());
        }
        return null;
    }

    private JstContest requireContest(Long contestId) {
        JstContest contest = contestMapperExt.selectById(contestId);
        if (contest == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        return contest;
    }

    private void validateContestForAppointment(JstContest contest, Date appointmentDate) {
        if (contest.getSupportAppointment() == null || contest.getSupportAppointment() != 1) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_NOT_SUPPORTED.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_NOT_SUPPORTED.code());
        }
        if (!"online".equals(contest.getAuditStatus())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.code());
        }
        if (appointmentDate == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (contest.getEventStartTime() != null && appointmentDate.before(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", contest.getEventStartTime())))) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (contest.getEventEndTime() != null && appointmentDate.after(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", contest.getEventEndTime())))) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private List<MemberSnapshot> resolveMembers(List<TeamAppointmentApplyDTO.TeamMemberDTO> memberDTOs) {
        List<MemberSnapshot> list = new ArrayList<>();
        for (TeamAppointmentApplyDTO.TeamMemberDTO memberDTO : memberDTOs) {
            if (memberDTO == null || (memberDTO.getParticipantId() == null && memberDTO.getUserId() == null)) {
                throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            Map<String, Object> participant = memberDTO.getParticipantId() != null
                    ? teamAppointmentMapperExt.selectParticipantById(memberDTO.getParticipantId())
                    : teamAppointmentMapperExt.selectParticipantByUserId(memberDTO.getUserId());
            if (participant == null || participant.isEmpty()) {
                throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_PARTICIPANT_NOT_FOUND.message(),
                        BizErrorCode.JST_ORDER_APPOINTMENT_PARTICIPANT_NOT_FOUND.code());
            }
            MemberSnapshot snapshot = new MemberSnapshot();
            snapshot.participantId = longValue(participant.get("participantId"));
            Long participantUserId = longNullable(participant.get("userId"));
            Long claimedUserId = longNullable(participant.get("claimedUserId"));
            snapshot.userId = memberDTO.getUserId() != null ? memberDTO.getUserId()
                    : (participantUserId != null ? participantUserId : claimedUserId);
            snapshot.memberName = stringValue(participant.get("name"));
            snapshot.mobile = stringValue(participant.get("guardianMobile"));
            snapshot.payMethod = memberDTO.getSubOrderInput() == null ? null : memberDTO.getSubOrderInput().getPayMethod();
            list.add(snapshot);
        }
        return list;
    }

    private List<WriteoffConfigItem> parseWriteoffConfig(String writeoffConfig) {
        if (StringUtils.isBlank(writeoffConfig)) {
            return Collections.singletonList(new WriteoffConfigItem("arrival", "到场核销"));
        }
        try {
            Object parsed = JSON.parse(writeoffConfig);
            List<WriteoffConfigItem> items = new ArrayList<>();
            if (parsed instanceof JSONArray array) {
                for (Object value : array) {
                    if (value instanceof JSONObject object) {
                        if (object.containsKey("enabled") && !object.getBooleanValue("enabled")) {
                            continue;
                        }
                        String itemType = object.getString("itemType");
                        if (StringUtils.isBlank(itemType)) {
                            itemType = object.getString("type");
                        }
                        if (StringUtils.isBlank(itemType)) {
                            continue;
                        }
                        items.add(new WriteoffConfigItem(itemType,
                                StringUtils.defaultIfBlank(object.getString("itemName"), defaultItemName(itemType))));
                    } else if (value instanceof String text && StringUtils.isNotBlank(text)) {
                        items.add(new WriteoffConfigItem(text, defaultItemName(text)));
                    }
                }
            }
            if (items.isEmpty()) {
                items.add(new WriteoffConfigItem("arrival", "到场核销"));
            }
            return items;
        } catch (Exception ex) {
            return Collections.singletonList(new WriteoffConfigItem("arrival", "到场核销"));
        }
    }

    private String defaultItemName(String itemType) {
        return switch (itemType) {
            case "arrival" -> "到场核销";
            case "gift" -> "礼品核销";
            case "ceremony" -> "仪式核销";
            default -> "自定义核销";
        };
    }

    private String currentOperator() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private Long longValue(Object value) {
        Long result = longNullable(value);
        return result == null ? 0L : result;
    }

    private Long longNullable(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? null : Long.valueOf(text);
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static class MemberSnapshot {
        private Long userId;
        private Long participantId;
        private String memberName;
        private String mobile;
        private String payMethod;
    }

    private static class WriteoffConfigItem {
        private final String itemType;
        private final String itemName;

        private WriteoffConfigItem(String itemType, String itemName) {
            this.itemType = itemType;
            this.itemName = itemName;
        }
    }
}
