package com.ruoyi.jst.order.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.security.SecurityCheck;
import com.ruoyi.jst.order.domain.JstOrderItem;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.domain.JstPaymentRecord;
import com.ruoyi.jst.order.dto.CreateOrderReqDTO;
import com.ruoyi.jst.order.dto.OrderQueryReqDTO;
import com.ruoyi.jst.order.enums.OrderStatus;
import com.ruoyi.jst.order.enums.OrderRefundStatus;
import com.ruoyi.jst.order.enums.PayMethod;
import com.ruoyi.jst.order.mapper.JstOrderItemMapper;
import com.ruoyi.jst.order.mapper.JstOrderMainMapper;
import com.ruoyi.jst.order.mapper.JstPaymentRecordMapper;
import com.ruoyi.jst.order.mapper.OrderItemMapperExt;
import com.ruoyi.jst.order.mapper.OrderMainMapperExt;
import com.ruoyi.jst.order.mapper.PaymentRecordMapperExt;
import com.ruoyi.jst.order.mapper.lookup.BindingLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.CouponLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.EnrollLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.PointsLookupMapper;
import com.ruoyi.jst.order.mapper.lookup.RebateLedgerInsertMapper;
import com.ruoyi.jst.order.mapper.lookup.RebateRuleLookupMapper;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.order.service.AmountCalculator;
import com.ruoyi.jst.order.service.OrderService;
import com.ruoyi.jst.order.vo.CreateOrderResVO;
import com.ruoyi.jst.order.vo.OrderDetailVO;
import com.ruoyi.jst.order.vo.OrderListVO;
import com.ruoyi.jst.order.vo.WechatPrepayVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 订单领域服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired private JstOrderItemMapper jstOrderItemMapper;
    @Autowired private JstOrderMainMapper jstOrderMainMapper;
    @Autowired private JstPaymentRecordMapper jstPaymentRecordMapper;
    @Autowired private OrderItemMapperExt orderItemMapperExt;
    @Autowired private OrderMainMapperExt orderMainMapperExt;
    @Autowired private PaymentRecordMapperExt paymentRecordMapperExt;
    @Autowired private EnrollLookupMapper enrollLookupMapper;
    @Autowired private CouponLookupMapper couponLookupMapper;
    @Autowired private PointsLookupMapper pointsLookupMapper;
    @Autowired private RebateRuleLookupMapper rebateRuleLookupMapper;
    @Autowired private BindingLookupMapper bindingLookupMapper;
    @Autowired private RebateLedgerInsertMapper rebateLedgerInsertMapper;
    @Autowired private AmountCalculator amountCalculator;
    @Autowired private WxPayService wxPayService;
    @Autowired private JstLockTemplate jstLockTemplate;
    @Autowired private SnowflakeIdWorker snowflakeIdWorker;

    @Value("${jst.biz.points-cash-rate:100}")
    private Integer pointsCashRate;

    @Value("${jst.wxpay.enabled:false}")
    private boolean wxPayEnabled;

    /**
     * 创建混合支付订单。
     *
     * @param req 创建订单入参
     * @return 创建结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "订单", action = "CREATE_MIXED_PAY", target = "#{req.enrollId}", recordResult = true)
    public CreateOrderResVO createMixedPay(CreateOrderReqDTO req) {
        Long userId = currentUserId();
        // LOCK: lock:order:create:{userId}
        return jstLockTemplate.execute("lock:order:create:" + userId, 3, 10, () ->
                // LOCK: lock:enroll:create-order:{enrollId}
                jstLockTemplate.execute("lock:enroll:create-order:" + req.getEnrollId(), 3, 10,
                        () -> doCreateMixedPay(userId, req)));
    }

    /**
     * 模拟支付成功。
     *
     * @param orderId 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "订单", action = "MOCK_PAY_SUCCESS", target = "#{orderId}")
    public void mockPaySuccess(Long orderId) {
        if (wxPayEnabled) {
            throw new ServiceException("当前环境已启用真实微信支付，禁止使用 mock-success",
                    BizErrorCode.JST_ORDER_PAY_FAIL.code());
        }
        JstOrderMain order = requireOrder(orderId);
        SecurityCheck.assertSameUser(order.getUserId());
        processPaySuccessWithLock(order.getOrderNo(), "MOCK_TXN_" + order.getOrderNo());
    }

    /**
     * 处理微信支付回调。
     *
     * @param body 原始回调体
     * @param headers 请求头
     * @return 微信回调响应
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "订单", action = "WXPAY_NOTIFY", recordParams = false)
    public String handleWechatNotify(String body, Map<String, String> headers) {
        WxPayService.NotifyPayload payload = wxPayService.parseNotify(body, headers);
        processPaySuccessWithLock(payload.getOutTradeNo(), payload.getTransactionId());
        return "SUCCESS";
    }

    /**
     * 取消订单。
     *
     * @param orderId 订单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "订单", action = "CANCEL", target = "#{orderId}")
    public void cancel(Long orderId) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        // LOCK: lock:order:cancel:{orderId}
        jstLockTemplate.execute("lock:order:cancel:" + orderId, 3, 5, () -> {
            JstOrderMain order = requireOrder(orderId);
            SecurityCheck.assertSameUser(order.getUserId());
            OrderStatus current = OrderStatus.fromDb(order.getOrderStatus());
            current.assertCanTransitTo(OrderStatus.CANCELLED); // SM-1
            doCancel(order, current, operator, now);
        });
    }

    @Override
    public OrderDetailVO getWxDetail(Long orderId) {
        JstOrderMain order = requireOrder(orderId);
        SecurityCheck.assertSameUser(order.getUserId());
        return requireDetail(orderId);
    }

    @Override
    public List<OrderListVO> selectMyList(OrderQueryReqDTO query) {
        if (query == null) {
            query = new OrderQueryReqDTO();
        }
        query.setUserId(currentUserId());
        return orderMainMapperExt.selectWxList(query);
    }

    @Override
    public List<OrderListVO> selectAdminList(OrderQueryReqDTO query) {
        return orderMainMapperExt.selectAdminList(query == null ? new OrderQueryReqDTO() : query);
    }

    @Override
    public OrderDetailVO getAdminDetail(Long orderId) {
        return requireDetail(orderId);
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return userId;
    }

    private String operatorName() {
        Long userId = SecurityUtils.getUserId();
        return userId == null ? "system" : String.valueOf(userId);
    }

    private JstOrderMain requireOrder(Long orderId) {
        JstOrderMain order = jstOrderMainMapper.selectJstOrderMainByOrderId(orderId);
        if (order == null || !"0".equals(order.getDelFlag())) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }
        return order;
    }

    private OrderDetailVO requireDetail(Long orderId) {
        OrderDetailVO detail = orderMainMapperExt.selectDetail(orderId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }
        return detail;
    }

    private CreateOrderResVO doCreateMixedPay(Long userId, CreateOrderReqDTO req) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        Map<String, Object> enroll = requireEnroll(req.getEnrollId());
        SecurityCheck.assertSameUser(longValue(enroll.get("userId")));
        if (!Objects.equals(stringValue(enroll.get("auditStatus")), "approved")) {
            throw new ServiceException(BizErrorCode.JST_ORDER_ENROLL_NOT_APPROVED.message(),
                    BizErrorCode.JST_ORDER_ENROLL_NOT_APPROVED.code());
        }
        clearCancelledOrderBindingIfNeeded(enroll, operator, now);

        Long pointsToUse = safePoints(req.getPointsToUse());
        if (pointsToUse > 0 && integerValue(enroll.get("supportPointsDeduct")) != 1) {
            throw new ServiceException(BizErrorCode.JST_ORDER_POINTS_NOT_SUPPORTED.message(),
                    BizErrorCode.JST_ORDER_POINTS_NOT_SUPPORTED.code());
        }

        Map<String, Object> coupon = req.getCouponId() == null ? null : requireCoupon(req.getCouponId(), userId, now);
        PointsSnapshot pointsSnapshot = pointsToUse > 0 ? loadPointsSnapshot(userId) : null;
        if (pointsSnapshot != null && pointsSnapshot.userAvailablePoints < pointsToUse) {
            throw new ServiceException(BizErrorCode.JST_POINTS_INSUFFICIENT.message(),
                    BizErrorCode.JST_POINTS_INSUFFICIENT.code());
        }

        Long contestId = longValue(enroll.get("contestId"));
        Map<String, Object> binding = bindingLookupMapper.selectActiveBinding(userId);
        Long channelId = binding == null ? null : longNullable(binding.get("channelId"));
        Map<String, Object> rule = channelId == null ? null : rebateRuleLookupMapper.selectMatchedRule(contestId, channelId, now);
        AmountCalculator.AmountResult amountResult = amountCalculator.calculate(buildCalcInput(enroll, coupon, rule, pointsToUse));
        if (req.getCouponId() != null && amountResult.getCouponAmount().compareTo(ZERO_AMOUNT) <= 0) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_NOT_APPLICABLE.message(),
                    BizErrorCode.JST_MARKETING_COUPON_NOT_APPLICABLE.code());
        }
        validateRequestedPayMethod(req.getPayMethod(), amountResult.getNetPayAmount());

        JstOrderMain order = buildOrder(req, enroll, channelId, amountResult, now, operator);
        jstOrderMainMapper.insertJstOrderMain(order);
        JstOrderItem item = buildOrderItem(order, enroll, amountResult, now, operator);
        jstOrderItemMapper.insertJstOrderItem(item);

        if (req.getCouponId() != null && couponLookupMapper.lockCoupon(req.getCouponId(), order.getOrderId(), userId, operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (pointsToUse > 0) {
            freezePoints(pointsSnapshot, pointsToUse, order.getOrderId(), now, "订单待支付冻结积分");
        }
        if (enrollLookupMapper.bindOrder(req.getEnrollId(), order.getOrderId(), operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        if (channelId != null) {
            rebateLedgerInsertMapper.insertLedger(order.getOrderId(), item.getItemId(), channelId, contestId,
                    longNullable(rule == null ? null : rule.get("ruleId")), item.getListAmount(), item.getNetPayShare(),
                    item.getServiceFeeShare(), amountResult.getRebateBase(), item.getRebateShare(),
                    dateValue(enroll.get("eventEndTime")), operator, now, "C2 订单创建预写计提");
        }

        CreateOrderResVO resVO = buildCreateRes(order, amountResult);
        if ("zero_price".equals(order.getOrderType())) {
            completeZeroPriceOrder(order, now);
            return resVO;
        }

        WechatPrepayVO wechatPrepay = null;
        if ("wechat".equals(req.getPayMethod())) {
            wechatPrepay = wxPayService.unifiedOrder(order);
        }
        OrderStatus.CREATED.assertCanTransitTo(OrderStatus.PENDING_PAY); // SM-1
        if (orderMainMapperExt.updateStatusByExpected(order.getOrderId(), OrderStatus.CREATED.dbValue(),
                OrderStatus.PENDING_PAY.dbValue(), null, operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        resVO.setWechatPrepay(wechatPrepay);
        return resVO;
    }

    private void doCancel(JstOrderMain order, OrderStatus current, String operator, Date now) {
        if (orderMainMapperExt.updateStatusByExpected(order.getOrderId(), current.dbValue(),
                OrderStatus.CANCELLED.dbValue(), null, operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        if (order.getCouponId() != null && couponLookupMapper.unlockCoupon(order.getCouponId(), order.getOrderId(), operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (safePoints(order.getPointsUsed()) > 0) {
            PointsSnapshot snapshot = loadPointsSnapshot(order.getUserId());
            unfreezePoints(snapshot, safePoints(order.getPointsUsed()), order.getOrderId(), now, "订单取消解冻");
        }
        clearEnrollOrderLink(order.getOrderId(), operator, now);
    }

    private void processPaySuccessWithLock(String outTradeNo, String transactionId) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        if (StringUtils.isBlank(transactionId)) {
            transactionId = "WXPAY_" + outTradeNo;
        }
        // LOCK: lock:wxpay:notify:{outTradeNo}
        String finalTransactionId = transactionId;
        jstLockTemplate.execute("lock:wxpay:notify:" + outTradeNo, 5, 10, () -> {
            JstOrderMain order = orderMainMapperExt.selectByOrderNo(outTradeNo);
            if (order == null) {
                throw new ServiceException(BizErrorCode.JST_ORDER_WX_NOTIFY_INVALID.message(),
                        BizErrorCode.JST_ORDER_WX_NOTIFY_INVALID.code());
            }
            OrderStatus current = OrderStatus.fromDb(order.getOrderStatus());
            if (current == OrderStatus.PAID || current == OrderStatus.COMPLETED || current == OrderStatus.CANCELLED) {
                return null;
            }
            current.assertCanTransitTo(OrderStatus.PAID); // SM-1
            if (orderMainMapperExt.updateStatusByExpected(order.getOrderId(), current.dbValue(),
                    OrderStatus.PAID.dbValue(), now, operator, now) == 0) {
                throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            insertPaymentRecord(order, finalTransactionId, now, operator);
            finalizeBenefits(order, now);
            return null;
        });
    }

    private void completeZeroPriceOrder(JstOrderMain order, Date now) {
        String operator = operatorName();
        OrderStatus.CREATED.assertCanTransitTo(OrderStatus.COMPLETED); // SM-1 特例
        if (orderMainMapperExt.updateStatusByExpected(order.getOrderId(), OrderStatus.CREATED.dbValue(),
                OrderStatus.COMPLETED.dbValue(), now, operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        finalizeBenefits(order, now);
    }

    private void finalizeBenefits(JstOrderMain order, Date now) {
        String operator = operatorName();
        if (order.getCouponId() != null && couponLookupMapper.useCoupon(order.getCouponId(), order.getOrderId(), operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (safePoints(order.getPointsUsed()) > 0) {
            PointsSnapshot snapshot = loadPointsSnapshot(order.getUserId());
            spendPoints(snapshot, safePoints(order.getPointsUsed()), order.getOrderId(), now, "支付成功消耗积分");
        }
    }

    private void insertPaymentRecord(JstOrderMain order, String transactionId, Date now, String operator) {
        JstPaymentRecord existing = paymentRecordMapperExt.selectLatestByOrderId(order.getOrderId());
        if (existing != null && "success".equals(existing.getPayStatus())) {
            return;
        }
        JstPaymentRecord record = new JstPaymentRecord();
        record.setPaymentNo(snowflakeIdWorker.nextBizNo("PAY"));
        record.setOrderId(order.getOrderId());
        record.setPayMethod(order.getPayMethod());
        record.setCashAmount(safeAmount(order.getNetPayAmount()));
        record.setPointsAmount(safeAmount(order.getPointsDeductAmount()));
        record.setPointsUsed(safePoints(order.getPointsUsed()));
        record.setThirdPartyNo(transactionId);
        record.setPayStatus("success");
        record.setPayTime(now);
        record.setCreateBy(operator);
        record.setCreateTime(now);
        record.setUpdateBy(operator);
        record.setUpdateTime(now);
        record.setDelFlag("0");
        jstPaymentRecordMapper.insertJstPaymentRecord(record);
    }

    private Map<String, Object> requireEnroll(Long enrollId) {
        Map<String, Object> enroll = enrollLookupMapper.selectEnrollForOrder(enrollId);
        if (enroll == null || enroll.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_ENROLL_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_ENROLL_NOT_FOUND.code());
        }
        return enroll;
    }

    private Map<String, Object> requireCoupon(Long couponId, Long userId, Date now) {
        Map<String, Object> coupon = couponLookupMapper.selectUserCoupon(couponId);
        if (coupon == null || coupon.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (!Objects.equals(longValue(coupon.get("userId")), userId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        if (!Objects.equals(stringValue(coupon.get("status")), "unused") || integerValue(coupon.get("templateStatus")) != 1) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        Date validStart = dateValue(coupon.get("validStart"));
        Date validEnd = dateValue(coupon.get("validEnd"));
        if ((validStart != null && validStart.after(now)) || (validEnd != null && validEnd.before(now))) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_INVALID.message(),
                    BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        return coupon;
    }

    private void clearCancelledOrderBindingIfNeeded(Map<String, Object> enroll, String operator, Date now) {
        Long linkedOrderId = longNullable(enroll.get("orderId"));
        if (linkedOrderId == null) {
            return;
        }
        JstOrderMain linkedOrder = jstOrderMainMapper.selectJstOrderMainByOrderId(linkedOrderId);
        if (linkedOrder != null && OrderStatus.CANCELLED.dbValue().equals(linkedOrder.getOrderStatus())) {
            if (enrollLookupMapper.clearOrder(longValue(enroll.get("enrollId")), linkedOrderId, operator, now) == 0) {
                throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return;
        }
        throw new ServiceException(BizErrorCode.JST_ORDER_DUPLICATE_ORDER.message(),
                BizErrorCode.JST_ORDER_DUPLICATE_ORDER.code());
    }

    private void clearEnrollOrderLink(Long orderId, String operator, Date now) {
        List<JstOrderItem> items = orderItemMapperExt.selectByOrderId(orderId);
        if (items.isEmpty()) {
            return;
        }
        JstOrderItem firstItem = items.get(0);
        if (firstItem.getRefId() != null) {
            enrollLookupMapper.clearOrder(firstItem.getRefId(), orderId, operator, now);
        }
    }

    private PointsSnapshot loadPointsSnapshot(Long userId) {
        Map<String, Object> map = pointsLookupMapper.selectPointsSnapshot(userId);
        if (map == null || map.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        PointsSnapshot snapshot = PointsSnapshot.fromMap(map);
        if (snapshot.accountId == null) {
            pointsLookupMapper.insertPointsAccount(userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints,
                    operatorName(), DateUtils.getNowDate());
            snapshot = PointsSnapshot.fromMap(pointsLookupMapper.selectPointsSnapshot(userId));
        }
        return snapshot;
    }

    private void freezePoints(PointsSnapshot snapshot, Long points, Long orderId, Date now, String remark) {
        if (snapshot.accountId != null && pointsLookupMapper.freezePointsAccount(snapshot.accountId, snapshot.version, points,
                operatorName(), now) == 0) {
            throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                    BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
        }
        if (pointsLookupMapper.freezeUserPoints(snapshot.userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints, points,
                operatorName(), now) == 0) {
            throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                    BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
        }
        if (snapshot.accountId != null) {
            pointsLookupMapper.insertPointsLedger(snapshot.accountId, "student", snapshot.userId, "freeze", "enroll",
                    orderId, -points, snapshot.userAvailablePoints - points, snapshot.userId, operatorName(), now, remark);
        }
    }

    private void spendPoints(PointsSnapshot snapshot, Long points, Long orderId, Date now, String remark) {
        if (snapshot.accountId != null && pointsLookupMapper.spendPointsAccount(snapshot.accountId, snapshot.version, points,
                operatorName(), now) == 0) {
            throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                    BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
        }
        if (pointsLookupMapper.spendUserPoints(snapshot.userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints, points,
                operatorName(), now) == 0) {
            throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                    BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
        }
        if (snapshot.accountId != null) {
            pointsLookupMapper.insertPointsLedger(snapshot.accountId, "student", snapshot.userId, "spend", "enroll",
                    orderId, 0L, snapshot.userAvailablePoints, snapshot.userId, operatorName(), now, remark);
        }
    }

    private void unfreezePoints(PointsSnapshot snapshot, Long points, Long orderId, Date now, String remark) {
        if (snapshot.accountId != null && pointsLookupMapper.unfreezePointsAccount(snapshot.accountId, snapshot.version, points,
                operatorName(), now) == 0) {
            throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                    BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
        }
        if (pointsLookupMapper.unfreezeUserPoints(snapshot.userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints, points,
                operatorName(), now) == 0) {
            throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                    BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
        }
        if (snapshot.accountId != null) {
            pointsLookupMapper.insertPointsLedger(snapshot.accountId, "student", snapshot.userId, "unfreeze", "enroll",
                    orderId, points, snapshot.userAvailablePoints + points, snapshot.userId, operatorName(), now, remark);
        }
    }

    private AmountCalculator.CalcInput buildCalcInput(Map<String, Object> enroll, Map<String, Object> coupon,
                                                      Map<String, Object> rule, Long pointsToUse) {
        AmountCalculator.CalcInput input = new AmountCalculator.CalcInput();
        input.setListAmount(bigDecimalValue(enroll.get("contestPrice")));
        input.setContestId(longValue(enroll.get("contestId")));
        input.setPointsToUse(pointsToUse);
        input.setPointsCashRate(pointsCashRate);
        if (coupon != null) {
            input.setCouponType(stringValue(coupon.get("couponType")));
            input.setCouponFaceValue(bigDecimalValue(coupon.get("couponFaceValue")));
            input.setCouponDiscountRate(bigDecimalValue(coupon.get("couponDiscountRate")));
            input.setCouponThresholdAmount(bigDecimalValue(coupon.get("couponThresholdAmount")));
            input.setApplicableContestIds(stringValue(coupon.get("applicableContestIds")));
        }
        if (rule != null) {
            input.setServiceFeeMode(stringValue(rule.get("serviceFeeMode")));
            input.setServiceFeeValue(bigDecimalValue(rule.get("serviceFeeValue")));
            input.setRebateMode(stringValue(rule.get("rebateMode")));
            input.setRebateValue(bigDecimalValue(rule.get("rebateValue")));
        }
        return input;
    }

    private void validateRequestedPayMethod(String payMethod, BigDecimal netPayAmount) {
        PayMethod method = PayMethod.fromDb(payMethod);
        if (method == PayMethod.POINTS && netPayAmount.compareTo(ZERO_AMOUNT) > 0) {
            throw new ServiceException(BizErrorCode.JST_ORDER_PAY_METHOD_INVALID.message(),
                    BizErrorCode.JST_ORDER_PAY_METHOD_INVALID.code());
        }
    }

    private JstOrderMain buildOrder(CreateOrderReqDTO req, Map<String, Object> enroll, Long channelId,
                                    AmountCalculator.AmountResult amountResult, Date now, String operator) {
        JstOrderMain order = new JstOrderMain();
        order.setOrderNo(snowflakeIdWorker.nextBizNo("OD"));
        order.setOrderType(amountResult.getOrderType());
        order.setBusinessType("enroll");
        order.setUserId(currentUserId());
        order.setParticipantId(longValue(enroll.get("participantId")));
        order.setChannelId(channelId);
        order.setContestId(longValue(enroll.get("contestId")));
        order.setPartnerId(longNullable(enroll.get("partnerId")));
        order.setListAmount(amountResult.getListAmount());
        order.setCouponAmount(amountResult.getCouponAmount());
        order.setPointsDeductAmount(amountResult.getPointsDeductAmount());
        order.setPointsUsed(amountResult.getPointsUsed());
        order.setPlatformBearAmount(amountResult.getPlatformBearAmount());
        order.setNetPayAmount(amountResult.getNetPayAmount());
        order.setServiceFee(amountResult.getServiceFee());
        order.setPayMethod(resolveStoredPayMethod(req.getPayMethod(), amountResult.getPointsUsed(), amountResult.getNetPayAmount()));
        order.setPayInitiator("self");
        order.setPayInitiatorId(currentUserId());
        order.setOrderStatus(OrderStatus.CREATED.dbValue());
        order.setRefundStatus(OrderRefundStatus.NONE.dbValue());
        order.setAftersaleDeadline(calculateAftersaleDeadline(enroll));
        order.setCouponId(req.getCouponId());
        order.setAllowSelfRefund(1);
        order.setCreateBy(operator);
        order.setCreateTime(now);
        order.setUpdateBy(operator);
        order.setUpdateTime(now);
        order.setDelFlag("0");
        return order;
    }

    private JstOrderItem buildOrderItem(JstOrderMain order, Map<String, Object> enroll,
                                        AmountCalculator.AmountResult amountResult, Date now, String operator) {
        JstOrderItem item = new JstOrderItem();
        item.setOrderId(order.getOrderId());
        item.setSkuType("enroll");
        item.setRefId(longValue(enroll.get("enrollId")));
        item.setItemName(stringValue(enroll.get("contestName")) + "报名费");
        item.setQuantity(1L);
        item.setListAmount(amountResult.getListAmount());
        item.setCouponShare(amountResult.getCouponAmount());
        item.setPointsShare(amountResult.getPointsDeductAmount());
        item.setNetPayShare(amountResult.getNetPayAmount());
        item.setServiceFeeShare(amountResult.getServiceFee());
        item.setRebateShare(amountResult.getRebateAmount());
        item.setRefundAmount(ZERO_AMOUNT);
        item.setRefundPoints(0L);
        item.setCreateBy(operator);
        item.setCreateTime(now);
        item.setUpdateBy(operator);
        item.setUpdateTime(now);
        item.setDelFlag("0");
        return item;
    }

    private CreateOrderResVO buildCreateRes(JstOrderMain order, AmountCalculator.AmountResult amountResult) {
        CreateOrderResVO vo = new CreateOrderResVO();
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setListAmount(amountResult.getListAmount());
        vo.setCouponAmount(amountResult.getCouponAmount());
        vo.setPointsDeductAmount(amountResult.getPointsDeductAmount());
        vo.setPointsUsed(amountResult.getPointsUsed());
        vo.setNetPayAmount(amountResult.getNetPayAmount());
        vo.setOrderType(amountResult.getOrderType());
        return vo;
    }

    private Date calculateAftersaleDeadline(Map<String, Object> enroll) {
        Date eventEnd = dateValue(enroll.get("eventEndTime"));
        int aftersaleDays = integerValue(enroll.get("aftersaleDays")) > 0 ? integerValue(enroll.get("aftersaleDays")) : 7;
        return eventEnd == null ? DateUtils.addDays(DateUtils.getNowDate(), aftersaleDays) : DateUtils.addDays(eventEnd, aftersaleDays);
    }

    private String resolveStoredPayMethod(String requestedMethod, Long pointsUsed, BigDecimal netPayAmount) {
        if (safePoints(pointsUsed) > 0 && safeAmount(netPayAmount).compareTo(ZERO_AMOUNT) > 0) {
            return PayMethod.POINTS_CASH_MIX.dbValue();
        }
        return PayMethod.fromDb(requestedMethod).dbValue();
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

    private int integerValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? 0 : Integer.parseInt(text);
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

    private static class PointsSnapshot {
        private Long userId;
        private long userAvailablePoints;
        private long userFrozenPoints;
        private Long accountId;
        private Integer version;

        private static PointsSnapshot fromMap(Map<String, Object> map) {
            PointsSnapshot snapshot = new PointsSnapshot();
            snapshot.userId = map.get("userId") == null ? null : ((Number) map.get("userId")).longValue();
            snapshot.userAvailablePoints = map.get("userAvailablePoints") == null ? 0L : ((Number) map.get("userAvailablePoints")).longValue();
            snapshot.userFrozenPoints = map.get("userFrozenPoints") == null ? 0L : ((Number) map.get("userFrozenPoints")).longValue();
            snapshot.accountId = map.get("accountId") == null ? null : ((Number) map.get("accountId")).longValue();
            snapshot.version = map.get("version") == null ? 0 : ((Number) map.get("version")).intValue();
            return snapshot;
        }
    }
}
