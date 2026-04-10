package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.channel.mapper.lookup.ChannelSupplementLookupMapper;
import com.ruoyi.jst.channel.mapper.lookup.OrderMainLookupMapper;
import com.ruoyi.jst.channel.service.ChannelSupplementService;
import com.ruoyi.jst.channel.vo.ChannelOrderDetailResVO;
import com.ruoyi.jst.channel.vo.ChannelTopContestResVO;
import com.ruoyi.jst.channel.vo.ChannelTopStudentResVO;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.util.MaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 渠道方补充接口服务实现（E0-1 新增）
 * <p>
 * 关联表：jst_user_rights / jst_student_channel_binding / jst_enroll_record
 * 关联决策：Q-01 自助解绑
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ChannelSupplementServiceImpl implements ChannelSupplementService {

    private static final Logger log = LoggerFactory.getLogger(ChannelSupplementServiceImpl.class);
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final int DEFAULT_TOP_LIMIT = 5;

    @Autowired
    private ChannelSupplementLookupMapper supplementLookupMapper;

    @Autowired
    private OrderMainLookupMapper orderMainLookupMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    /**
     * 查询渠道方权益列表
     *
     * @param userId 当前渠道方用户ID
     * @return 权益列表（rightsName/remainQuota/expireTime）
     * @关联表 jst_user_rights / jst_rights_template
     */
    @Override
    public List<Map<String, Object>> selectMyRights(Long userId) {
        List<Map<String, Object>> rights = supplementLookupMapper.selectRightsByUserId(userId);
        return rights == null ? new ArrayList<>() : rights;
    }

    /**
     * 查指定绑定学生的成绩列表
     *
     * @param channelId 渠道方ID
     * @param studentId 学生用户ID
     * @return 成绩列表
     * @关联表 jst_student_channel_binding / jst_enroll_record / jst_contest
     */
    @Override
    public List<Map<String, Object>> selectStudentScores(Long channelId, Long studentId) {
        assertStudentBound(channelId, studentId);
        List<Map<String, Object>> scores = supplementLookupMapper.selectStudentScores(studentId);
        return scores == null ? new ArrayList<>() : scores;
    }

    /**
     * 查指定绑定学生的证书列表
     *
     * @param channelId 渠道方ID
     * @param studentId 学生用户ID
     * @return 证书列表
     * @关联表 jst_student_channel_binding / jst_enroll_record / jst_contest
     */
    @Override
    public List<Map<String, Object>> selectStudentCerts(Long channelId, Long studentId) {
        assertStudentBound(channelId, studentId);
        List<Map<String, Object>> certs = supplementLookupMapper.selectStudentCerts(studentId);
        return certs == null ? new ArrayList<>() : certs;
    }

    @Override
    public List<ChannelTopContestResVO> selectTopContests(Long channelId, String period, Integer limit) {
        Date endTime = DateUtils.getNowDate();
        Date startTime = resolveStartTime(period);
        Integer queryLimit = resolveLimit(limit);
        List<ChannelTopContestResVO> rows = orderMainLookupMapper.selectTopContestRanks(channelId, startTime, endTime, queryLimit);
        if (rows == null) {
            return new ArrayList<>();
        }
        for (ChannelTopContestResVO row : rows) {
            if (row.getEnrollCount() == null) {
                row.setEnrollCount(0);
            }
        }
        return rows;
    }

    @Override
    public List<ChannelTopStudentResVO> selectTopStudents(Long channelId, Integer limit) {
        List<ChannelTopStudentResVO> rows = orderMainLookupMapper.selectTopStudentRanks(channelId, resolveLimit(limit));
        if (rows == null) {
            return new ArrayList<>();
        }
        for (ChannelTopStudentResVO row : rows) {
            row.setStudentName(maskStudentName(row.getStudentName()));
            if (row.getEnrollCount() == null) {
                row.setEnrollCount(0);
            }
            if (row.getPayAmount() == null) {
                row.setPayAmount(BigDecimal.ZERO);
            }
        }
        return rows;
    }

    @Override
    public ChannelOrderDetailResVO selectOrderDetail(Long channelId, Long orderId) {
        Long ownerChannelId = orderMainLookupMapper.selectOrderChannelId(orderId);
        if (ownerChannelId == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }
        if (!Objects.equals(channelId, ownerChannelId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }

        Map<String, Object> detailMap = orderMainLookupMapper.selectChannelOrderDetail(orderId);
        if (detailMap == null || detailMap.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }

        ChannelOrderDetailResVO detail = new ChannelOrderDetailResVO();
        detail.setOrderId(toLong(detailMap.get("orderId")));
        detail.setOrderNo(toString(detailMap.get("orderNo")));
        detail.setContestId(toLong(detailMap.get("contestId")));
        detail.setContestName(toString(detailMap.get("contestName")));
        detail.setParticipantId(toLong(detailMap.get("participantId")));
        detail.setParticipantName(toString(detailMap.get("participantName")));
        detail.setStudentName(toString(detailMap.get("studentName")));
        detail.setMobileMasked(MaskUtil.mobile(toString(detailMap.get("studentMobile"))));
        detail.setOrderStatus(toString(detailMap.get("orderStatus")));
        detail.setRefundStatus(toString(detailMap.get("refundStatus")));
        detail.setPriceOriginal(toBigDecimal(detailMap.get("priceOriginal")));
        detail.setCouponDiscount(toBigDecimal(detailMap.get("couponDiscount")));
        detail.setPointsDiscount(toBigDecimal(detailMap.get("pointsDiscount")));
        detail.setPointsUsed(toLong(detailMap.get("pointsUsed")));
        detail.setUserNetPay(toBigDecimal(detailMap.get("userNetPay")));
        detail.setPayAmount(detail.getUserNetPay());
        detail.setPlatformFee(toBigDecimal(detailMap.get("platformFee")));
        detail.setRebateBase(toBigDecimal(detailMap.get("rebateBase")));
        detail.setRebateAmount(toBigDecimal(detailMap.get("rebateAmount")));
        detail.setRebateRate(calculateRate(detail.getRebateAmount(), detail.getRebateBase()));
        detail.setCreateTime(toDate(detailMap.get("createTime")));
        detail.setPayTime(toDate(detailMap.get("payTime")));
        detail.setChannelOwner(buildChannelOwner(detailMap));
        detail.setParticipantSnapshot(buildParticipantSnapshot(detailMap));
        detail.setTimeline(buildTimeline(detailMap));
        return detail;
    }

    /**
     * 渠道方自助解绑学生（Q-01）
     * <p>
     * 解绑后学生可被其他渠道方绑定，不回溯历史返点。
     *
     * @param channelId 渠道方ID
     * @param bindingId 绑定记录ID
     * @关联表 jst_student_channel_binding
     * @关联决策 Q-01
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: unbindStudent
    @OperateLog(module = "渠道", action = "CHANNEL_UNBIND", target = "#{bindingId}")
    public void unbindStudent(Long channelId, Long bindingId) {
        // LOCK: lock:channel:unbind:{bindingId}
        jstLockTemplate.execute("lock:channel:unbind:" + bindingId, 3, 5, () -> {
            Map<String, Object> binding = supplementLookupMapper.selectBindingById(bindingId);
            if (binding == null || binding.isEmpty()) {
                throw new ServiceException(BizErrorCode.JST_USER_BIND_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_BIND_NOT_FOUND.code());
            }

            // 归属校验：只能解绑自己渠道下的学生
            Long bindChannelId = toLong(binding.get("channelId"));
            if (!Objects.equals(channelId, bindChannelId)) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }

            String status = String.valueOf(binding.get("bindingStatus"));
            if (!"active".equals(status)) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_UNBIND_FAILED.message(),
                        BizErrorCode.JST_CHANNEL_UNBIND_FAILED.code());
            }

            Date now = DateUtils.getNowDate();
            int updated = supplementLookupMapper.unbindByBindingId(bindingId, "active",
                    String.valueOf(channelId), now);
            if (updated == 0) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_UNBIND_FAILED.message(),
                        BizErrorCode.JST_CHANNEL_UNBIND_FAILED.code());
            }

            log.info("[ChannelUnbind] 渠道方自助解绑 channelId={} bindingId={} studentUserId={}",
                    channelId, bindingId, binding.get("userId"));
            return null;
        });
    }

    /**
     * 校验学生是否绑定到指定渠道
     */
    private void assertStudentBound(Long channelId, Long studentUserId) {
        if (supplementLookupMapper.countBindingForChannel(channelId, studentUserId) <= 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private Date resolveStartTime(String period) {
        int days = switch (StringUtils.defaultString(period, "last30d")) {
            case "last7d" -> 7;
            case "last90d" -> 90;
            default -> 30;
        };
        return Date.from(LocalDate.now(ZONE_ID).minusDays(days - 1L).atStartOfDay(ZONE_ID).toInstant());
    }

    private Integer resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_TOP_LIMIT;
        }
        return Math.min(limit, 20);
    }

    private String maskStudentName(String studentName) {
        if (StringUtils.isBlank(studentName)) {
            return studentName;
        }
        if (studentName.startsWith("U") && studentName.length() > 1 && studentName.substring(1).chars().allMatch(Character::isDigit)) {
            return studentName;
        }
        return MaskUtil.realName(studentName);
    }

    private ChannelOrderDetailResVO.ChannelOwnerVO buildChannelOwner(Map<String, Object> detailMap) {
        Long ownerChannelId = toLong(detailMap.get("ownerChannelId"));
        if (ownerChannelId == null) {
            return null;
        }
        ChannelOrderDetailResVO.ChannelOwnerVO channelOwner = new ChannelOrderDetailResVO.ChannelOwnerVO();
        channelOwner.setChannelId(ownerChannelId);
        channelOwner.setName(toString(detailMap.get("ownerChannelName")));
        channelOwner.setChannelType(toString(detailMap.get("ownerChannelType")));
        return channelOwner;
    }

    private ChannelOrderDetailResVO.ParticipantSnapshotVO buildParticipantSnapshot(Map<String, Object> detailMap) {
        String originalParticipantName = toString(detailMap.get("originalParticipantName"));
        if (StringUtils.isBlank(originalParticipantName)) {
            return null;
        }
        ChannelOrderDetailResVO.ParticipantSnapshotVO snapshot = new ChannelOrderDetailResVO.ParticipantSnapshotVO();
        snapshot.setOriginalParticipantName(originalParticipantName);
        return snapshot;
    }

    private List<ChannelOrderDetailResVO.TimelineItemVO> buildTimeline(Map<String, Object> detailMap) {
        List<ChannelOrderDetailResVO.TimelineItemVO> timeline = new ArrayList<>();
        addTimelineItem(timeline, "返点入账", toDate(detailMap.get("rebateTime")));
        addTimelineItem(timeline, "支付完成", toDate(detailMap.get("payTime")));
        addTimelineItem(timeline, "订单创建", toDate(detailMap.get("createTime")));
        return timeline;
    }

    private void addTimelineItem(List<ChannelOrderDetailResVO.TimelineItemVO> timeline, String step, Date time) {
        if (time == null) {
            return;
        }
        ChannelOrderDetailResVO.TimelineItemVO item = new ChannelOrderDetailResVO.TimelineItemVO();
        item.setStep(step);
        item.setTime(time);
        timeline.add(item);
    }

    private BigDecimal calculateRate(BigDecimal amount, BigDecimal base) {
        if (amount == null || base == null || BigDecimal.ZERO.compareTo(base) == 0) {
            return null;
        }
        return amount.divide(base, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        if (val instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return new BigDecimal(String.valueOf(val));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Date toDate(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Date date) {
            return date;
        }
        return null;
    }

    private String toString(Object val) {
        return val == null ? null : String.valueOf(val);
    }

    private Long toLong(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Number) {
            return ((Number) val).longValue();
        }
        try {
            return Long.valueOf(String.valueOf(val));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
