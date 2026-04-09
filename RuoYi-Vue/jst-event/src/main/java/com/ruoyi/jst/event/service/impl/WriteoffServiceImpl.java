package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.event.domain.EventAppointmentRecord;
import com.ruoyi.jst.event.domain.EventAppointmentWriteoffItem;
import com.ruoyi.jst.event.domain.EventTeamAppointment;
import com.ruoyi.jst.event.dto.WriteoffRecordQueryDTO;
import com.ruoyi.jst.event.dto.WriteoffScanDTO;
import com.ruoyi.jst.event.enums.AppointmentStatus;
import com.ruoyi.jst.event.enums.TeamAppointmentStatus;
import com.ruoyi.jst.event.enums.WriteoffItemStatus;
import com.ruoyi.jst.event.mapper.AppointmentRecordMapperExt;
import com.ruoyi.jst.event.mapper.TeamAppointmentMapperExt;
import com.ruoyi.jst.event.mapper.WriteoffItemMapperExt;
import com.ruoyi.jst.event.service.WriteoffService;
import com.ruoyi.jst.event.util.WriteoffQrCodeGenerator;
import com.ruoyi.jst.event.vo.WriteoffRecordVO;
import com.ruoyi.jst.event.vo.WriteoffScanResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 核销服务实现。
 */
@Service
public class WriteoffServiceImpl implements WriteoffService {

    @Autowired private WriteoffItemMapperExt writeoffItemMapperExt;
    @Autowired private AppointmentRecordMapperExt appointmentRecordMapperExt;
    @Autowired private TeamAppointmentMapperExt teamAppointmentMapperExt;
    @Autowired private JstLockTemplate jstLockTemplate;
    @Autowired private WriteoffQrCodeGenerator writeoffQrCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "WRITEOFF_SCAN", target = "#{req.qrCode}", recordParams = false, recordResult = true)
    public WriteoffScanResVO scan(Long operatorUserId, WriteoffScanDTO req) {
        if (operatorUserId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        if (req == null || StringUtils.isBlank(req.getQrCode()) || !writeoffQrCodeGenerator.isValid(req.getQrCode())) {
            throw new ServiceException(BizErrorCode.JST_ORDER_WRITEOFF_QR_INVALID.message(),
                    BizErrorCode.JST_ORDER_WRITEOFF_QR_INVALID.code());
        }
        Map<String, Object> preview = requireScanContext(req.getQrCode());
        assertScannerScope(preview);
        Long itemId = longValue(preview.get("writeoffItemId"));
        return jstLockTemplate.execute("lock:writeoff:item:" + itemId, 2, 3, () -> doScan(operatorUserId, req));
    }

    @Override
    public List<WriteoffRecordVO> selectRecords(WriteoffRecordQueryDTO query) {
        Long partnerId = isPartnerUser() ? JstLoginContext.currentPartnerId() : null;
        return writeoffItemMapperExt.selectRecordList(partnerId, query);
    }

    private WriteoffScanResVO doScan(Long operatorUserId, WriteoffScanDTO req) {
        Map<String, Object> context = requireScanContext(req.getQrCode());
        assertScannerScope(context);
        Date now = DateUtils.getNowDate();
        String operator = currentOperator();
        Long itemId = longValue(context.get("writeoffItemId"));
        Long appointmentId = longValue(context.get("appointmentId"));
        Long teamAppointmentId = longNullable(context.get("teamAppointmentId"));
        String itemType = stringValue(context.get("itemType"));

        EventAppointmentWriteoffItem item = requireItem(itemId);
        WriteoffItemStatus.fromDb(item.getStatus()).assertCanTransitTo(WriteoffItemStatus.USED);
        long expectedQty = item.getWriteoffQty() == null ? 0L : item.getWriteoffQty();
        long targetQty = item.getTotalQty() == null || item.getTotalQty() <= 0 ? 1L : item.getTotalQty();
        if (writeoffItemMapperExt.updateUsed(itemId, item.getStatus(), expectedQty, targetQty, operatorUserId,
                resolveTerminal(req), now, operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }

        EventAppointmentRecord appointment = requireAppointment(appointmentId);
        String appointmentStatus = promoteAppointmentStatus(appointment, operator, now);

        String teamStatus = null;
        Long teamWriteoffPersons = null;
        Long teamTotalPersons = null;
        if (teamAppointmentId != null && "arrival".equals(itemType)) {
            TeamProgress progress = jstLockTemplate.execute("lock:team_appt:" + teamAppointmentId, 3, 5,
                    () -> promoteTeamArrival(teamAppointmentId, appointment.getParticipantId(), operator, now));
            teamStatus = progress.status;
            teamWriteoffPersons = progress.writeoffPersons;
            teamTotalPersons = progress.totalPersons;
        } else if (teamAppointmentId != null) {
            EventTeamAppointment team = requireTeam(teamAppointmentId);
            teamStatus = team.getStatus();
            teamWriteoffPersons = team.getWriteoffPersons();
            teamTotalPersons = team.getTotalPersons();
        }

        WriteoffScanResVO resVO = new WriteoffScanResVO();
        resVO.setItemId(itemId);
        resVO.setItemType(itemType);
        resVO.setItemName(stringValue(context.get("itemName")));
        resVO.setMemberName(stringValue(context.get("memberName")));
        resVO.setAppointmentStatus(appointmentStatus);
        resVO.setTeamStatus(teamStatus);
        resVO.setTeamWriteoffPersons(teamWriteoffPersons);
        resVO.setTeamTotalPersons(teamTotalPersons);
        resVO.setWriteoffTime(now);
        return resVO;
    }

    private String promoteAppointmentStatus(EventAppointmentRecord appointment, String operator, Date now) {
        int totalItems = appointmentRecordMapperExt.countAllItems(appointment.getAppointmentId());
        int usedItems = appointmentRecordMapperExt.countUsedItems(appointment.getAppointmentId());
        AppointmentStatus current = AppointmentStatus.fromDb(appointment.getMainStatus());
        AppointmentStatus target = null;
        if (usedItems >= totalItems && totalItems > 0 && current != AppointmentStatus.FULLY_WRITEOFF) {
            target = AppointmentStatus.FULLY_WRITEOFF;
        } else if (usedItems > 0 && current == AppointmentStatus.BOOKED) {
            target = AppointmentStatus.PARTIAL_WRITEOFF;
        }
        if (target == null || target == current) {
            return current.dbValue();
        }
        current.assertCanTransitTo(target);
        if (appointmentRecordMapperExt.updateMainStatus(appointment.getAppointmentId(), current.dbValue(),
                target.dbValue(), operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return target.dbValue();
    }

    private TeamProgress promoteTeamArrival(Long teamAppointmentId, Long participantId, String operator, Date now) {
        EventTeamAppointment team = requireTeam(teamAppointmentId);
        TeamAppointmentStatus current = TeamAppointmentStatus.fromDb(team.getStatus());
        if (current == TeamAppointmentStatus.CANCELLED || current == TeamAppointmentStatus.EXPIRED
                || current == TeamAppointmentStatus.NO_SHOW || current == TeamAppointmentStatus.PARTIAL_WRITEOFF_ENDED) {
            throw new ServiceException(BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_ENDED.message(),
                    BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_ENDED.code());
        }
        long currentPersons = team.getWriteoffPersons() == null ? 0L : team.getWriteoffPersons();
        long totalPersons = team.getTotalPersons() == null ? 0L : team.getTotalPersons();
        long targetPersons = currentPersons + 1;
        if (targetPersons > totalPersons) {
            throw new ServiceException(BizErrorCode.JST_ORDER_TEAM_WRITEOFF_OVER.message(),
                    BizErrorCode.JST_ORDER_TEAM_WRITEOFF_OVER.code());
        }
        TeamAppointmentStatus target = current;
        if (targetPersons >= totalPersons && current != TeamAppointmentStatus.FULLY_WRITEOFF) {
            target = TeamAppointmentStatus.FULLY_WRITEOFF;
        } else if (targetPersons > 0 && current == TeamAppointmentStatus.BOOKED) {
            target = TeamAppointmentStatus.PARTIAL_WRITEOFF;
        }
        if (target != current) {
            current.assertCanTransitTo(target);
        }
        if (teamAppointmentMapperExt.updateWriteoffProgress(teamAppointmentId, current.dbValue(), currentPersons,
                target.dbValue(), targetPersons, operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        teamAppointmentMapperExt.updateMemberWriteoffStatus(teamAppointmentId, participantId,
                WriteoffItemStatus.UNUSED.dbValue(), WriteoffItemStatus.USED.dbValue(), operator, now);
        TeamProgress progress = new TeamProgress();
        progress.status = target.dbValue();
        progress.writeoffPersons = targetPersons;
        progress.totalPersons = totalPersons;
        return progress;
    }

    private void assertScannerScope(Map<String, Object> context) {
        if (!isPartnerUser()) {
            return;
        }
        Long contestPartnerId = longNullable(context.get("partnerId"));
        if (!Objects.equals(contestPartnerId, JstLoginContext.currentPartnerId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private Map<String, Object> requireScanContext(String qrCode) {
        Map<String, Object> context = writeoffItemMapperExt.selectScanContextByQrCode(qrCode);
        if (context == null || context.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_WRITEOFF_QR_INVALID.message(),
                    BizErrorCode.JST_ORDER_WRITEOFF_QR_INVALID.code());
        }
        return context;
    }

    private EventAppointmentWriteoffItem requireItem(Long itemId) {
        EventAppointmentWriteoffItem item = writeoffItemMapperExt.selectById(itemId);
        if (item == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_WRITEOFF_RECORD_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_WRITEOFF_RECORD_NOT_FOUND.code());
        }
        return item;
    }

    private EventAppointmentRecord requireAppointment(Long appointmentId) {
        EventAppointmentRecord appointment = appointmentRecordMapperExt.selectById(appointmentId);
        if (appointment == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_NOT_FOUND.code());
        }
        return appointment;
    }

    private EventTeamAppointment requireTeam(Long teamAppointmentId) {
        EventTeamAppointment team = teamAppointmentMapperExt.selectTeamById(teamAppointmentId);
        if (team == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_TEAM_APPOINTMENT_NOT_FOUND.code());
        }
        return team;
    }

    private boolean isPartnerUser() {
        return JstLoginContext.currentPartnerId() != null
                && JstLoginContext.hasRole("jst_partner")
                && !JstLoginContext.hasRole("jst_platform_op");
    }

    private String resolveTerminal(WriteoffScanDTO req) {
        if (StringUtils.isNotBlank(req.getTerminal())) {
            return req.getTerminal();
        }
        if (StringUtils.isNotBlank(req.getScanType())) {
            return req.getScanType();
        }
        return "wx_scan";
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

    private static class TeamProgress {
        private String status;
        private Long writeoffPersons;
        private Long totalPersons;
    }
}
