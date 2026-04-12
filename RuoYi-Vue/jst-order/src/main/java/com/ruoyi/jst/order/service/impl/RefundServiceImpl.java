package com.ruoyi.jst.order.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.event.RefundSuccessEvent;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.security.SecurityCheck;
import com.ruoyi.jst.order.domain.JstOrderItem;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.domain.JstPaymentRecord;
import com.ruoyi.jst.order.domain.JstRefundRecord;
import com.ruoyi.jst.order.dto.RefundApplyDTO;
import com.ruoyi.jst.order.dto.RefundAuditDTO;
import com.ruoyi.jst.order.dto.RefundQueryReqDTO;
import com.ruoyi.jst.order.enums.OrderRefundStatus;
import com.ruoyi.jst.order.enums.OrderStatus;
import com.ruoyi.jst.order.enums.PayMethod;
import com.ruoyi.jst.order.enums.RefundStatus;
import com.ruoyi.jst.order.mapper.JstOrderMainMapper;
import com.ruoyi.jst.order.mapper.JstRefundRecordMapper;
import com.ruoyi.jst.order.mapper.OrderItemMapperExt;
import com.ruoyi.jst.order.mapper.OrderMainMapperExt;
import com.ruoyi.jst.order.mapper.PaymentRecordMapperExt;
import com.ruoyi.jst.order.mapper.RefundRecordMapperExt;
import com.ruoyi.jst.order.mapper.lookup.CouponLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.EnrollLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.PointsLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.RebateLedgerLookupMapper;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.order.service.RefundService;
import com.ruoyi.jst.order.vo.RefundDetailVO;
import com.ruoyi.jst.order.vo.RefundListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RefundServiceImpl implements RefundService {

    private static final String ROLE_PARTNER = "jst_partner";
    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final EnumSet<OrderStatus> APPLY_ALLOWED_STATUSES =
            EnumSet.of(OrderStatus.PAID, OrderStatus.IN_SERVICE, OrderStatus.AFTERSALE);

    @Autowired private JstRefundRecordMapper jstRefundRecordMapper;
    @Autowired private JstOrderMainMapper jstOrderMainMapper;
    @Autowired private RefundRecordMapperExt refundRecordMapperExt;
    @Autowired private OrderMainMapperExt orderMainMapperExt;
    @Autowired private OrderItemMapperExt orderItemMapperExt;
    @Autowired private PaymentRecordMapperExt paymentRecordMapperExt;
    @Autowired private PointsLookupMapper pointsLookupMapper;
    @Autowired private CouponLookupMapper couponLookupMapper;
    @Autowired private EnrollLookupMapper enrollLookupMapper;
    @Autowired private RebateLedgerLookupMapper rebateLedgerLookupMapper;
    @Autowired private WxPayService wxPayService;
    @Autowired private JstLockTemplate jstLockTemplate;
    @Autowired private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> apply(Long orderId, RefundApplyDTO req) {
        Long userId = currentUserId();
        return jstLockTemplate.execute("lock:refund:apply:" + orderId, 3, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            JstOrderMain order = requireOrder(orderId);
            SecurityCheck.assertSameUser(order.getUserId());
            validateCreateRefund(order, false, now);
            JstOrderItem item = requireRefundItem(orderId, null);
            moveOrderToAftersaleIfNeeded(order, operator, now);
            JstRefundRecord refund = buildRefundRecord(order, item, req.getRefundType(), "user",
                    req.getReason(), RefundStatus.PENDING.dbValue(), null, null, now, operator);
            jstRefundRecordMapper.insertJstRefundRecord(refund);
            return buildResult(refund);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> specialRefund(Long orderId, RefundAuditDTO req) {
        Long operatorId = currentUserId();
        return jstLockTemplate.execute("lock:refund:apply:" + orderId, 3, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            JstOrderMain order = requireOrder(orderId);
            validateCreateRefund(order, true, now);
            JstOrderItem item = requireRefundItem(orderId, null);
            moveOrderToAftersaleIfNeeded(order, operator, now);
            JstRefundRecord refund = buildRefundRecord(order, item, "special_refund", "admin",
                    req.getAuditRemark(), RefundStatus.APPROVED.dbValue(), req.getAuditRemark(),
                    operatorId, now, operator);
            jstRefundRecordMapper.insertJstRefundRecord(refund);
            return buildResult(refund);
        });
    }

    @Override
    public RefundDetailVO getWxDetail(Long refundId) {
        JstRefundRecord refund = requireRefund(refundId);
        JstOrderMain order = requireOrder(refund.getOrderId());
        SecurityCheck.assertSameUser(order.getUserId());
        return requireDetail(refundId);
    }

    @Override
    public List<RefundListVO> selectMyList(RefundQueryReqDTO query) {
        RefundQueryReqDTO finalQuery = query == null ? new RefundQueryReqDTO() : query;
        finalQuery.setUserId(currentUserId());
        return refundRecordMapperExt.selectMyList(finalQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long refundId) {
        JstRefundRecord preview = requireRefund(refundId);
        JstOrderMain previewOrder = requireOrder(preview.getOrderId());
        SecurityCheck.assertSameUser(previewOrder.getUserId());
        jstLockTemplate.execute("lock:refund:apply:" + previewOrder.getOrderId(), 3, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            Long operatorId = currentOperatorId();
            JstRefundRecord refund = requireRefund(refundId);
            JstOrderMain order = requireOrder(refund.getOrderId());
            SecurityCheck.assertSameUser(order.getUserId());
            RefundStatus.fromDb(refund.getStatus()).assertCanTransitTo(RefundStatus.CLOSED);
            if (refundRecordMapperExt.updateStatusByExpected(refundId, RefundStatus.PENDING.dbValue(),
                    RefundStatus.CLOSED.dbValue(), "用户撤销退款申请", null, null, null,
                    operatorId, null, operator, now) == 0) {
                throwDataTampered();
            }
            restoreOrderStatus(refund, order, operator, now);
        });
    }

    @Override
    public List<RefundListVO> selectAdminList(RefundQueryReqDTO query) {
        RefundQueryReqDTO finalQuery = query == null ? new RefundQueryReqDTO() : query;
        if (isPartnerUser() && finalQuery.getPartnerId() == null) {
            finalQuery.setPartnerId(JstLoginContext.currentPartnerId());
        }
        return refundRecordMapperExt.selectAdminList(finalQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long refundId, RefundAuditDTO req) {
        jstLockTemplate.execute("lock:refund:approve:" + refundId, 3, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            Long operatorId = currentOperatorId();
            JstRefundRecord refund = requireRefund(refundId);
            JstOrderMain order = requireOrder(refund.getOrderId());
            assertPartnerScopeIfNeeded(order);
            RefundStatus.fromDb(refund.getStatus()).assertCanTransitTo(RefundStatus.APPROVED);
            if (refundRecordMapperExt.updateStatusByExpected(refundId, RefundStatus.PENDING.dbValue(),
                    RefundStatus.APPROVED.dbValue(), req.getAuditRemark(), null, null, null,
                    operatorId, null, operator, now) == 0) {
                throwDataTampered();
            }
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long refundId, RefundAuditDTO req) {
        jstLockTemplate.execute("lock:refund:approve:" + refundId, 3, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            Long operatorId = currentOperatorId();
            JstRefundRecord refund = requireRefund(refundId);
            JstOrderMain order = requireOrder(refund.getOrderId());
            assertPartnerScopeIfNeeded(order);
            RefundStatus.fromDb(refund.getStatus()).assertCanTransitTo(RefundStatus.REJECTED);
            if (refundRecordMapperExt.updateStatusByExpected(refundId, RefundStatus.PENDING.dbValue(),
                    RefundStatus.REJECTED.dbValue(), req.getAuditRemark(), null, null, null,
                    operatorId, null, operator, now) == 0) {
                throwDataTampered();
            }
            restoreOrderStatus(refund, order, operator, now);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeTimeoutPending(Long refundId, String remark) {
        return jstLockTemplate.execute("lock:refund:approve:" + refundId, 3, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = "system";
            JstRefundRecord refund = requireRefund(refundId);
            JstOrderMain order = requireOrder(refund.getOrderId());
            RefundStatus current = RefundStatus.fromDb(refund.getStatus());
            if (current != RefundStatus.PENDING) {
                return false;
            }
            // SM-2: pending -> closed
            current.assertCanTransitTo(RefundStatus.CLOSED);
            int updated = refundRecordMapperExt.updateStatusByExpected(refundId, RefundStatus.PENDING.dbValue(),
                    RefundStatus.CLOSED.dbValue(), remark, null, null, null,
                    null, null, operator, now);
            if (updated == 0) {
                return false;
            }
            restoreOrderStatus(refund, order, operator, now);
            return true;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long refundId) {
        jstLockTemplate.execute("lock:refund:execute:" + refundId, 5, 30, () -> {
            Date now = DateUtils.getNowDate();
            String operator = currentOperatorName();
            Long operatorId = currentOperatorId();
            JstRefundRecord refund = requireRefund(refundId);
            RefundStatus currentStatus = RefundStatus.fromDb(refund.getStatus());
            if (currentStatus == RefundStatus.COMPLETED) {
                return;
            }
            currentStatus.assertCanTransitTo(RefundStatus.REFUNDING);
            JstOrderMain order = requireOrder(refund.getOrderId());
            JstOrderItem item = requireRefundItem(order.getOrderId(), refund.getItemId());
            if (refundRecordMapperExt.updateStatusByExpected(refundId, RefundStatus.APPROVED.dbValue(),
                    RefundStatus.REFUNDING.dbValue(), null, null, null, null,
                    operatorId, null, operator, now) == 0) {
                throwDataTampered();
            }

            BigDecimal actualCash = safeAmount(order.getNetPayAmount());
            Long actualPoints = safePoints(order.getPointsUsed());

            processCashRefund(refund, order, actualCash);
            refundPoints(order, refundId, actualPoints, operatorId, operator, now);
            Integer couponReturned = returnCouponIfEligible(order, refund, operator, now) ? 1 : 0;
            rollbackRebate(order, operator, now);
            completeOrderRefund(order, operator, now);
            accumulateRefundItem(item, actualCash, actualPoints, operator, now);
            cancelEnroll(order, operator, now);

            if (refundRecordMapperExt.updateStatusByExpected(refundId, RefundStatus.REFUNDING.dbValue(),
                    RefundStatus.COMPLETED.dbValue(), null, actualCash, actualPoints, couponReturned,
                    operatorId, now, operator, now) == 0) {
                throwDataTampered();
            }
            publishAfterCommit(buildRefundSuccessEvent(refund, order, actualCash));
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleWechatRefundNotify(String body, Map<String, String> headers) {
        WxPayService.RefundNotifyPayload payload = wxPayService.parseRefundNotify(body, headers);
        return jstLockTemplate.execute("lock:wxpay:refund:notify:" + payload.getRefundNo(), 5, 10, () -> {
            JstRefundRecord refund = requireRefundByNo(payload.getRefundNo());
            RefundStatus status = RefundStatus.fromDb(refund.getStatus());
            if (status == RefundStatus.COMPLETED || status == RefundStatus.REFUNDING) {
                return "SUCCESS";
            }
            throw new ServiceException(BizErrorCode.JST_ORDER_WX_REFUND_NOTIFY_INVALID.message(),
                    BizErrorCode.JST_ORDER_WX_REFUND_NOTIFY_INVALID.code());
        });
    }

    private void validateCreateRefund(JstOrderMain order, boolean adminBypass, Date now) {
        if (order == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }
        if ("zero_price".equals(order.getOrderType()) || safeAmount(order.getListAmount()).compareTo(ZERO_AMOUNT) <= 0) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NO_REFUND_FOR_ZERO_PRICE.message(),
                    BizErrorCode.JST_ORDER_NO_REFUND_FOR_ZERO_PRICE.code());
        }
        OrderStatus current = OrderStatus.fromDb(order.getOrderStatus());
        if (!APPLY_ALLOWED_STATUSES.contains(current)) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_DENIED.message(),
                    BizErrorCode.JST_ORDER_REFUND_DENIED.code());
        }
        if (!adminBypass) {
            if (!Objects.equals(order.getAllowSelfRefund(), 1)) {
                throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_DENIED.message(),
                        BizErrorCode.JST_ORDER_REFUND_DENIED.code());
            }
            if (order.getAftersaleDeadline() == null || !order.getAftersaleDeadline().after(now)) {
                throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_DENIED.message(),
                        BizErrorCode.JST_ORDER_REFUND_DENIED.code());
            }
        }
        if (refundRecordMapperExt.countBlockingByOrderId(order.getOrderId()) > 0) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_DUPLICATE.message(),
                    BizErrorCode.JST_ORDER_REFUND_DUPLICATE.code());
        }
    }

    private void moveOrderToAftersaleIfNeeded(JstOrderMain order, String operator, Date now) {
        OrderStatus current = OrderStatus.fromDb(order.getOrderStatus());
        if (current == OrderStatus.AFTERSALE) {
            return;
        }
        current.assertCanTransitTo(OrderStatus.AFTERSALE);
        if (orderMainMapperExt.updateStatusByExpected(order.getOrderId(), current.dbValue(),
                OrderStatus.AFTERSALE.dbValue(), null, operator, now) == 0) {
            throwDataTampered();
        }
        order.setOrderStatus(OrderStatus.AFTERSALE.dbValue());
    }

    private void restoreOrderStatus(JstRefundRecord refund, JstOrderMain order, String operator, Date now) {
        String restoreStatus = resolveRestoreStatus(refund);
        if (Objects.equals(order.getOrderStatus(), restoreStatus)) {
            return;
        }
        if (orderMainMapperExt.updateStatusByExpected(order.getOrderId(), OrderStatus.AFTERSALE.dbValue(),
                restoreStatus, null, operator, now) == 0) {
            throwDataTampered();
        }
    }

    private void processCashRefund(JstRefundRecord refund, JstOrderMain order, BigDecimal actualCash) {
        if (actualCash.compareTo(ZERO_AMOUNT) <= 0) {
            return;
        }
        PayMethod payMethod = PayMethod.fromDb(order.getPayMethod());
        if (payMethod == PayMethod.BANK_TRANSFER || payMethod == PayMethod.POINTS) {
            return;
        }
        JstPaymentRecord paymentRecord = paymentRecordMapperExt.selectLatestByOrderId(order.getOrderId());
        if (paymentRecord == null || !"success".equals(paymentRecord.getPayStatus())) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.message(),
                    BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.code());
        }
        WxPayService.RefundResult result = wxPayService.refund(buildRefundRequest(refund, order));
        if (result == null || !result.isSuccess()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.message(),
                    BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.code());
        }
    }

    private WxPayService.RefundRequest buildRefundRequest(JstRefundRecord refund, JstOrderMain order) {
        WxPayService.RefundRequest request = new WxPayService.RefundRequest();
        request.setRefundNo(refund.getRefundNo());
        request.setOrderNo(order.getOrderNo());
        request.setOutTradeNo(order.getOrderNo());
        request.setRefundCash(safeAmount(order.getNetPayAmount()));
        request.setReason(refund.getReason());
        return request;
    }

    private void refundPoints(JstOrderMain order, Long refundId, Long points, Long operatorId, String operator, Date now) {
        if (points == null || points <= 0) {
            return;
        }
        PointsSnapshot snapshot = loadPointsSnapshot(order.getUserId());
        if (snapshot.accountId != null && pointsLookupMapper.refundPoints(snapshot.accountId, snapshot.version, points,
                operator, now) == 0) {
            throwPointsConflict();
        }
        if (pointsLookupMapper.refundUserPoints(snapshot.userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (snapshot.accountId != null) {
            pointsLookupMapper.insertRefundLedger(snapshot.accountId, snapshot.userId, refundId, points,
                    snapshot.userAvailablePoints + points, operatorId, operator, now, "退款退回积分");
        }
    }

    private boolean returnCouponIfEligible(JstOrderMain order, JstRefundRecord refund, String operator, Date now) {
        if (order.getCouponId() == null || safeAmount(order.getCouponAmount()).compareTo(ZERO_AMOUNT) <= 0) {
            return false;
        }
        if (!isFullRefund(order, refund)) {
            return false;
        }
        Map<String, Object> coupon = couponLookupMapper.selectUserCoupon(order.getCouponId());
        if (coupon == null || coupon.isEmpty()) {
            return false;
        }
        if (!"used".equals(stringValue(coupon.get("status")))) {
            return false;
        }
        Date validEnd = dateValue(coupon.get("validEnd"));
        if (validEnd == null || !validEnd.after(now)) {
            return false;
        }
        return couponLookupMapper.returnCouponToUnused(order.getCouponId(), order.getOrderId(), validEnd, operator, now) > 0;
    }

    private boolean isFullRefund(JstOrderMain order, JstRefundRecord refund) {
        return safeAmount(refund.getApplyCash()).compareTo(safeAmount(order.getNetPayAmount())) == 0
                && Objects.equals(safePoints(refund.getApplyPoints()), safePoints(order.getPointsUsed()));
    }

    private void rollbackRebate(JstOrderMain order, String operator, Date now) {
        List<Map<String, Object>> ledgers = rebateLedgerLookupMapper.selectByOrderId(order.getOrderId());
        if (ledgers == null || ledgers.isEmpty()) {
            return;
        }
        rebateLedgerLookupMapper.rollbackByOrderId(order.getOrderId(), operator, now);
        for (Map<String, Object> ledger : ledgers) {
            if (!"positive".equals(stringValue(ledger.get("direction")))) {
                continue;
            }
            if (!"paid".equals(stringValue(ledger.get("status")))) {
                continue;
            }
            rebateLedgerLookupMapper.insertNegativeLedger(order.getOrderId(),
                    longNullable(ledger.get("itemId")),
                    longNullable(ledger.get("channelId")),
                    longNullable(ledger.get("contestId")),
                    longNullable(ledger.get("ruleId")),
                    bigDecimalValue(ledger.get("listAmount")),
                    bigDecimalValue(ledger.get("netPayAmount")),
                    bigDecimalValue(ledger.get("serviceFee")),
                    bigDecimalValue(ledger.get("rebateBase")),
                    bigDecimalValue(ledger.get("rebateAmount")).negate(),
                    dateValue(ledger.get("eventEndTime")),
                    operator,
                    now,
                    "退款回滚已打款返点");
        }
    }

    private void completeOrderRefund(JstOrderMain order, String operator, Date now) {
        if (!Objects.equals(order.getOrderStatus(), OrderStatus.AFTERSALE.dbValue())) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_DENIED.message(),
                    BizErrorCode.JST_ORDER_REFUND_DENIED.code());
        }
        if (orderMainMapperExt.updateRefundByExpected(order.getOrderId(), OrderStatus.AFTERSALE.dbValue(),
                OrderStatus.CANCELLED.dbValue(), OrderRefundStatus.NONE.dbValue(),
                OrderRefundStatus.FULL.dbValue(), operator, now) == 0) {
            throwDataTampered();
        }
    }

    private void accumulateRefundItem(JstOrderItem item, BigDecimal actualCash, Long actualPoints, String operator, Date now) {
        if (orderItemMapperExt.accumulateRefundByExpected(item.getItemId(),
                safeAmount(item.getRefundAmount()), safePoints(item.getRefundPoints()), actualCash, actualPoints,
                operator, now) == 0) {
            throwDataTampered();
        }
    }

    private void cancelEnroll(JstOrderMain order, String operator, Date now) {
        enrollLookupMapper.cancelEnrollByOrderId(order.getOrderId(), "approved", "cancelled", operator, now);
    }

    private void assertPartnerScopeIfNeeded(JstOrderMain order) {
        if (!isPartnerUser()) {
            return;
        }
        if (!Objects.equals(order.getPartnerId(), JstLoginContext.currentPartnerId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private boolean isPartnerUser() {
        return JstLoginContext.currentPartnerId() != null
                && JstLoginContext.hasRole(ROLE_PARTNER)
                && !JstLoginContext.hasRole("jst_platform_op");
    }

    private JstRefundRecord buildRefundRecord(JstOrderMain order, JstOrderItem item, String refundType,
                                              String applySource, String reason, String status,
                                              String auditRemark, Long operatorId, Date now, String operator) {
        JstRefundRecord refund = new JstRefundRecord();
        refund.setRefundNo(snowflakeIdWorker.nextBizNo("RF"));
        refund.setOrderId(order.getOrderId());
        refund.setItemId(item == null ? null : item.getItemId());
        refund.setRefundType(refundType);
        refund.setApplySource(applySource);
        refund.setReason(reason);
        refund.setApplyCash(safeAmount(order.getNetPayAmount()));
        refund.setApplyPoints(safePoints(order.getPointsUsed()));
        refund.setCouponReturned(0);
        refund.setStatus(status);
        refund.setAuditRemark(auditRemark);
        refund.setOperatorId(operatorId);
        refund.setRemark(order.getOrderStatus());
        refund.setCreateBy(operator);
        refund.setCreateTime(now);
        refund.setUpdateBy(operator);
        refund.setUpdateTime(now);
        refund.setDelFlag("0");
        return refund;
    }

    private JstRefundRecord requireRefund(Long refundId) {
        JstRefundRecord refund = jstRefundRecordMapper.selectJstRefundRecordByRefundId(refundId);
        if (refund == null || !"0".equals(defaultDelFlag(refund.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.code());
        }
        return refund;
    }

    private JstRefundRecord requireRefundByNo(String refundNo) {
        JstRefundRecord refund = refundRecordMapperExt.selectByRefundNo(refundNo);
        if (refund == null || !"0".equals(defaultDelFlag(refund.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.code());
        }
        return refund;
    }

    private JstOrderMain requireOrder(Long orderId) {
        JstOrderMain order = jstOrderMainMapper.selectJstOrderMainByOrderId(orderId);
        if (order == null || !"0".equals(defaultDelFlag(order.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }
        return order;
    }

    private JstOrderItem requireRefundItem(Long orderId, Long itemId) {
        List<JstOrderItem> items = orderItemMapperExt.selectByOrderId(orderId);
        if (items == null || items.isEmpty()) {
            throwDataTampered();
        }
        if (itemId == null) {
            return items.get(0);
        }
        for (JstOrderItem item : items) {
            if (Objects.equals(item.getItemId(), itemId)) {
                return item;
            }
        }
        throwDataTampered();
        return null;
    }

    private RefundDetailVO requireDetail(Long refundId) {
        RefundDetailVO detail = refundRecordMapperExt.selectDetail(refundId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.code());
        }
        return detail;
    }

    private PointsSnapshot loadPointsSnapshot(Long userId) {
        Map<String, Object> map = pointsLookupMapper.selectPointsSnapshot(userId);
        if (map == null || map.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        PointsSnapshot snapshot = PointsSnapshot.fromMap(map);
        if (snapshot.accountId == null) {
            throwDataTampered();
        }
        return snapshot;
    }

    private Map<String, Object> buildResult(JstRefundRecord refund) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("refundId", refund.getRefundId());
        result.put("refundNo", refund.getRefundNo());
        return result;
    }

    private String resolveRestoreStatus(JstRefundRecord refund) {
        String restoreStatus = StringUtils.isBlank(refund.getRemark())
                ? OrderStatus.AFTERSALE.dbValue()
                : refund.getRemark();
        OrderStatus.fromDb(restoreStatus);
        return restoreStatus;
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return userId;
    }

    private Long currentOperatorId() {
        return SecurityUtils.getUserId();
    }

    private String currentOperatorName() {
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }

    private void throwPointsConflict() {
        throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
    }

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
    }

    private Long safePoints(Long points) {
        return points == null ? 0L : points;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
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

    private BigDecimal bigDecimalValue(Object value) {
        if (value == null) {
            return ZERO_AMOUNT;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof Number number) {
            return new BigDecimal(number.toString()).setScale(2, RoundingMode.HALF_UP);
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? ZERO_AMOUNT : new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
    }

    private Date dateValue(Object value) {
        return value instanceof Date ? (Date) value : null;
    }

    private RefundSuccessEvent buildRefundSuccessEvent(JstRefundRecord refund, JstOrderMain order, BigDecimal actualCash) {
        LinkedHashMap<String, Object> extraData = new LinkedHashMap<>();
        extraData.put("refundNo", refund.getRefundNo());
        extraData.put("refundAmount", safeAmount(actualCash));
        return new RefundSuccessEvent(this, order.getUserId(), refund.getRefundId(), "refund_success", extraData);
    }

    private void publishAfterCommit(Object event) {
        if (event == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishEvent(event);
                }
            });
            return;
        }
        eventPublisher.publishEvent(event);
    }

    private static class PointsSnapshot {
        private Long userId;
        private long userAvailablePoints;
        private long userFrozenPoints;
        private Long accountId;
        private Integer version;

        private static PointsSnapshot fromMap(Map<String, Object> map) {
            PointsSnapshot snapshot = new PointsSnapshot();
            snapshot.userId = map.get("userId") == null ? null : ((Number) map.get("userId")).longValue();
            snapshot.userAvailablePoints = map.get("userAvailablePoints") == null ? 0L
                    : ((Number) map.get("userAvailablePoints")).longValue();
            snapshot.userFrozenPoints = map.get("userFrozenPoints") == null ? 0L
                    : ((Number) map.get("userFrozenPoints")).longValue();
            snapshot.accountId = map.get("accountId") == null ? null : ((Number) map.get("accountId")).longValue();
            snapshot.version = map.get("version") == null ? 0 : ((Number) map.get("version")).intValue();
            return snapshot;
        }
    }
}
