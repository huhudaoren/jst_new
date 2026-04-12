package com.ruoyi.jst.event.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.event.domain.EventAppointmentRecord;
import com.ruoyi.jst.event.domain.EventAppointmentWriteoffItem;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.dto.AppointmentApplyDTO;
import com.ruoyi.jst.event.enums.AppointmentStatus;
import com.ruoyi.jst.event.mapper.AppointmentRecordMapperExt;
import com.ruoyi.jst.event.mapper.ContestMapperExt;
import com.ruoyi.jst.event.mapper.TeamAppointmentMapperExt;
import com.ruoyi.jst.event.mapper.WriteoffItemMapperExt;
import com.ruoyi.jst.event.service.AppointmentService;
import com.ruoyi.jst.event.util.WriteoffQrCodeGenerator;
import com.ruoyi.jst.event.vo.AppointmentApplyResVO;
import com.ruoyi.jst.event.vo.AppointmentDetailVO;
import com.ruoyi.jst.event.vo.AppointmentListVO;
import com.ruoyi.jst.event.vo.AppointmentRemainingVO;
import com.ruoyi.jst.order.api.OrderCreationFacade;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.dto.AppointmentOrderCreateReqDTO;
import com.ruoyi.jst.order.enums.OrderStatus;
import com.ruoyi.jst.order.mapper.JstOrderMainMapper;
import com.ruoyi.jst.order.mapper.OrderMainMapperExt;
import com.ruoyi.jst.order.vo.AppointmentOrderCreateResVO;
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
import java.util.Objects;

/**
 * 个人预约服务实现。
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private AppointmentRecordMapperExt appointmentRecordMapperExt;

    @Autowired
    private WriteoffItemMapperExt writeoffItemMapperExt;

    @Autowired
    private ContestMapperExt contestMapperExt;

    @Autowired
    private TeamAppointmentMapperExt teamAppointmentMapperExt;

    @Autowired
    private OrderCreationFacade orderCreationFacade;

    @Autowired
    private JstOrderMainMapper jstOrderMainMapper;

    @Autowired
    private OrderMainMapperExt orderMainMapperExt;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private WriteoffQrCodeGenerator writeoffQrCodeGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentApplyResVO applyIndividual(Long userId, AppointmentApplyDTO req) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        if (req == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstContest contest = requireContest(req.getContestId());
        validateContestForAppointment(contest, req.getAppointmentDate());
        ParticipantSnapshot participant = resolveParticipant(userId, req.getParticipantId());
        List<WriteoffConfigItem> configItems = parseWriteoffConfig(contest.getWriteoffConfig());
        String operator = currentOperator(userId);
        String lockKey = "lock:appointment:book:" + contest.getContestId() + ":" + req.getSessionCode();
        return jstLockTemplate.execute(lockKey, 3, 5, () ->
                doApply(userId, contest, participant, req, configItems, operator));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cancelIndividual(Long userId, Long appointmentId) {
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return jstLockTemplate.execute("lock:appointment:cancel:" + appointmentId, 2, 5, () ->
                doCancel(userId, appointmentId, currentOperator(userId)));
    }

    @Override
    public AppointmentRemainingVO getRemaining(Long contestId, Date appointmentDate, String sessionCode) {
        JstContest contest = requireContest(contestId);
        validateContestForAppointment(contest, appointmentDate);
        if (StringUtils.isBlank(sessionCode)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        int individualBookedPersons = appointmentRecordMapperExt.countBookedIndividuals(contestId, appointmentDate, sessionCode);
        int teamBookedPersons = teamAppointmentMapperExt.countBookedPersons(contestId, appointmentDate, sessionCode);
        int bookedPersons = individualBookedPersons + teamBookedPersons;
        Integer capacity = contest.getAppointmentCapacity();

        AppointmentRemainingVO vo = new AppointmentRemainingVO();
        vo.setContestId(contestId);
        vo.setAppointmentDate(appointmentDate);
        vo.setSessionCode(sessionCode);
        vo.setAppointmentCapacity(capacity);
        vo.setIndividualBookedPersons(individualBookedPersons);
        vo.setTeamBookedPersons(teamBookedPersons);
        vo.setBookedPersons(bookedPersons);
        vo.setRemainingCapacity(capacity != null && capacity > 0 ? Math.max(capacity - bookedPersons, 0) : null);
        return vo;
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean expireBySystem(Long appointmentId) {
        EventAppointmentRecord record = appointmentRecordMapperExt.selectById(appointmentId);
        if (record == null || !"0".equals(defaultDelFlag(record.getDelFlag()))) {
            return false;
        }
        AppointmentStatus current = AppointmentStatus.fromDb(record.getMainStatus());
        if (current != AppointmentStatus.BOOKED) {
            return false;
        }
        // SM-11: booked -> expired
        current.assertCanTransitTo(AppointmentStatus.EXPIRED);
        Date now = DateUtils.getNowDate();
        String operator = "system";
        int updated = appointmentRecordMapperExt.updateMainStatus(appointmentId, AppointmentStatus.BOOKED.dbValue(),
                AppointmentStatus.EXPIRED.dbValue(), operator, now);
        if (updated == 0) {
            return false;
        }
        writeoffItemMapperExt.expireUnusedByAppointmentId(appointmentId, operator, now);
        return true;
    }

    private AppointmentApplyResVO doApply(Long userId, JstContest contest, ParticipantSnapshot participant,
                                          AppointmentApplyDTO req, List<WriteoffConfigItem> configItems,
                                          String operator) {
        if (contest.getAllowRepeatAppointment() == null || contest.getAllowRepeatAppointment() == 0) {
            if (teamAppointmentMapperExt.countDuplicateAppointment(contest.getContestId(), participant.participantId) > 0) {
                throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_REPEAT_NOT_ALLOWED.message(),
                        BizErrorCode.JST_ORDER_APPOINTMENT_REPEAT_NOT_ALLOWED.code());
            }
        }

        int individualBookedPersons = appointmentRecordMapperExt.countBookedIndividuals(contest.getContestId(),
                req.getAppointmentDate(), req.getSessionCode());
        int teamBookedPersons = teamAppointmentMapperExt.countBookedPersons(contest.getContestId(),
                req.getAppointmentDate(), req.getSessionCode());
        int bookedPersons = individualBookedPersons + teamBookedPersons;
        if (contest.getAppointmentCapacity() != null && contest.getAppointmentCapacity() > 0
                && bookedPersons + 1 > contest.getAppointmentCapacity()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_CAPACITY_FULL_V2.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_CAPACITY_FULL_V2.code());
        }

        Date now = DateUtils.getNowDate();
        AppointmentOrderCreateResVO orderRes = null;
        if (needCreateOrder(contest)) {
            AppointmentOrderCreateReqDTO orderReq = new AppointmentOrderCreateReqDTO();
            orderReq.setUserId(userId);
            orderReq.setParticipantId(participant.participantId);
            orderReq.setContestId(contest.getContestId());
            orderReq.setPartnerId(contest.getPartnerId());
            orderReq.setTeamAppointmentId(null);
            orderReq.setRefId(participant.participantId);
            orderReq.setListAmount(safeAmount(contest.getPrice()));
            orderReq.setItemName(contest.getContestName() + "线下预约费");
            orderReq.setPayInitiator("self");
            orderReq.setPayInitiatorId(userId);
            orderReq.setAftersaleDeadline(calculateAftersaleDeadline(contest));
            orderReq.setOperator(operator);
            orderRes = orderCreationFacade.createAppointmentOrder(orderReq);
        }

        EventAppointmentRecord record = new EventAppointmentRecord();
        record.setAppointmentNo(snowflakeIdWorker.nextBizNo("AP"));
        record.setContestId(contest.getContestId());
        record.setUserId(userId);
        record.setParticipantId(participant.participantId);
        record.setChannelId(null);
        record.setAppointmentType("individual");
        record.setTeamAppointmentId(null);
        record.setAppointmentDate(req.getAppointmentDate());
        record.setSessionCode(req.getSessionCode());
        record.setOrderId(orderRes == null ? null : orderRes.getOrderId());
        record.setMainStatus(AppointmentStatus.BOOKED.dbValue());
        record.setQrCode(writeoffQrCodeGenerator.generateToken());
        record.setCreateBy(operator);
        record.setCreateTime(now);
        record.setUpdateBy(operator);
        record.setUpdateTime(now);
        record.setRemark(buildAppointmentRemark(req));
        record.setDelFlag("0");
        appointmentRecordMapperExt.insertAppointmentRecord(record);

        List<String> qrCodes = new ArrayList<>();
        for (WriteoffConfigItem configItem : configItems) {
            EventAppointmentWriteoffItem item = new EventAppointmentWriteoffItem();
            item.setAppointmentId(record.getAppointmentId());
            item.setTeamAppointmentId(null);
            item.setItemType(configItem.itemType);
            item.setItemName(configItem.itemName);
            item.setQrCode(writeoffQrCodeGenerator.generateToken());
            item.setStatus("unused");
            item.setWriteoffQty(0L);
            item.setTotalQty(1L);
            item.setCreateBy(operator);
            item.setCreateTime(now);
            item.setUpdateBy(operator);
            item.setUpdateTime(now);
            item.setDelFlag("0");
            writeoffItemMapperExt.insertWriteoffItem(item);
            qrCodes.add(item.getQrCode());
        }

        AppointmentApplyResVO resVO = new AppointmentApplyResVO();
        resVO.setAppointmentId(record.getAppointmentId());
        resVO.setAppointmentNo(record.getAppointmentNo());
        resVO.setOrderId(orderRes == null ? null : orderRes.getOrderId());
        resVO.setOrderNo(orderRes == null ? null : orderRes.getOrderNo());
        resVO.setOrderStatus(orderRes == null ? null : orderRes.getOrderStatus());
        resVO.setAppointmentStatus(record.getMainStatus());
        resVO.setQrCodes(qrCodes);
        return resVO;
    }

    private String doCancel(Long userId, Long appointmentId, String operator) {
        EventAppointmentRecord record = appointmentRecordMapperExt.selectMyRecord(userId, appointmentId);
        if (record == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_NOT_FOUND.code());
        }

        if (appointmentRecordMapperExt.countUsedItems(appointmentId) > 0) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_CANCEL_HAS_USED_ITEM.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_CANCEL_HAS_USED_ITEM.code());
        }

        AppointmentStatus current = AppointmentStatus.fromDb(record.getMainStatus());
        current.assertCanTransitTo(AppointmentStatus.CANCELLED);
        Date now = DateUtils.getNowDate();
        if (appointmentRecordMapperExt.updateMainStatus(appointmentId, current.dbValue(),
                AppointmentStatus.CANCELLED.dbValue(), operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        writeoffItemMapperExt.voidUnusedByAppointmentId(appointmentId, operator, now);

        // Q-07: 检查赛事级 allow_appointment_refund 配置
        JstContest contest = contestMapperExt.selectById(record.getContestId());
        if (contest != null && contest.getAllowAppointmentRefund() != null
                && contest.getAllowAppointmentRefund() == 1) {
            // allow_appointment_refund=1: 正常走退款链路（复用 C4 范式）
            boolean needManualRefund = cancelLinkedOrderIfNeeded(record.getOrderId(), userId, operator, now);
            return needManualRefund ? "预约已取消，退款将在 1-3 个工作日内处理" : "预约已取消";
        } else {
            // allow_appointment_refund=0（默认）: 仅取消预约状态，不触发退款
            // 仍需取消未支付订单（created/pending_pay → cancelled）
            cancelUnpaidOrderOnly(record.getOrderId(), userId, operator, now);
            return "预约已取消，本活动不支持预约退款";
        }
    }

    /**
     * 仅取消未支付的关联订单（Q-07: 不支持退款时使用）
     */
    private void cancelUnpaidOrderOnly(Long orderId, Long userId, String operator, Date now) {
        if (orderId == null) {
            return;
        }
        JstOrderMain order = jstOrderMainMapper.selectJstOrderMainByOrderId(orderId);
        if (order == null || !"0".equals(defaultDelFlag(order.getDelFlag()))) {
            return;
        }
        if (!Objects.equals(userId, order.getUserId()) || !"appointment".equals(order.getBusinessType())) {
            return;
        }
        OrderStatus current = OrderStatus.fromDb(order.getOrderStatus());
        // 仅取消 created / pending_pay 状态订单，已付款的不动
        if (current == OrderStatus.CREATED || current == OrderStatus.PENDING_PAY) {
            current.assertCanTransitTo(OrderStatus.CANCELLED);
            orderMainMapperExt.updateStatusByExpected(orderId, current.dbValue(),
                    OrderStatus.CANCELLED.dbValue(), null, operator, now);
        }
        // 已支付的订单保持原状态，不触发退款
    }

    private boolean cancelLinkedOrderIfNeeded(Long orderId, Long userId, String operator, Date now) {
        if (orderId == null) {
            return false;
        }
        JstOrderMain order = jstOrderMainMapper.selectJstOrderMainByOrderId(orderId);
        if (order == null || !"0".equals(defaultDelFlag(order.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        if (!Objects.equals(userId, order.getUserId()) || !"appointment".equals(order.getBusinessType())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        OrderStatus current = OrderStatus.fromDb(order.getOrderStatus());
        if (current == OrderStatus.CREATED || current == OrderStatus.PENDING_PAY) {
            current.assertCanTransitTo(OrderStatus.CANCELLED);
            if (orderMainMapperExt.updateStatusByExpected(orderId, current.dbValue(),
                    OrderStatus.CANCELLED.dbValue(), null, operator, now) == 0) {
                throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return false;
        }
        return current == OrderStatus.PAID
                || current == OrderStatus.REVIEWING
                || current == OrderStatus.IN_SERVICE
                || current == OrderStatus.AFTERSALE
                || current == OrderStatus.COMPLETED;
    }

    private ParticipantSnapshot resolveParticipant(Long userId, Long participantId) {
        Map<String, Object> participant = participantId != null
                ? teamAppointmentMapperExt.selectParticipantById(participantId)
                : teamAppointmentMapperExt.selectParticipantByUserId(userId);
        if (participant == null || participant.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_APPOINTMENT_PARTICIPANT_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_APPOINTMENT_PARTICIPANT_NOT_FOUND.code());
        }
        Long ownerUserId = longNullable(participant.get("userId"));
        Long claimedUserId = longNullable(participant.get("claimedUserId"));
        if (!Objects.equals(ownerUserId, userId) && !Objects.equals(claimedUserId, userId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        ParticipantSnapshot snapshot = new ParticipantSnapshot();
        snapshot.participantId = longValue(participant.get("participantId"));
        snapshot.participantName = stringValue(participant.get("name"));
        return snapshot;
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
        if (contest.getEventStartTime() != null
                && appointmentDate.before(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", contest.getEventStartTime())))) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (contest.getEventEndTime() != null
                && appointmentDate.after(DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd", contest.getEventEndTime())))) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
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

    private String buildAppointmentRemark(AppointmentApplyDTO req) {
        if (req == null || (req.getCouponId() == null && safePoints(req.getPointsToUse()) <= 0)) {
            return null;
        }
        return "couponId=" + req.getCouponId() + ",pointsToUse=" + safePoints(req.getPointsToUse());
    }

    private boolean needCreateOrder(JstContest contest) {
        return safeAmount(contest.getPrice()).compareTo(ZERO_AMOUNT) > 0;
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

    private String currentOperator(Long userId) {
        return userId == null ? "system" : String.valueOf(userId);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private Long safePoints(Long points) {
        return points == null ? 0L : points;
    }

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
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

    private static class ParticipantSnapshot {
        private Long participantId;
        private String participantName;
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
