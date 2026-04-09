package com.ruoyi.jst.points.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.points.domain.JstMallExchangeOrder;
import com.ruoyi.jst.points.domain.JstMallGoods;
import com.ruoyi.jst.points.dto.AftersaleApplyDTO;
import com.ruoyi.jst.points.dto.AftersaleAuditDTO;
import com.ruoyi.jst.points.dto.AftersaleQueryReqDTO;
import com.ruoyi.jst.points.enums.AftersaleStatus;
import com.ruoyi.jst.points.enums.MallExchangeStatus;
import com.ruoyi.jst.points.enums.MallGoodsType;
import com.ruoyi.jst.points.mapper.JstMallExchangeOrderMapper;
import com.ruoyi.jst.points.mapper.JstMallGoodsMapper;
import com.ruoyi.jst.points.mapper.lookup.MallOrderLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.PointsAccountLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.RefundRecordLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.UserCouponLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.UserRightsLookupMapper;
import com.ruoyi.jst.points.service.MallAftersaleService;
import com.ruoyi.jst.points.vo.AftersaleApplyResVO;
import com.ruoyi.jst.points.vo.AftersaleDetailVO;
import com.ruoyi.jst.points.vo.AftersaleListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 商城售后服务实现。
 */
@Service
public class MallAftersaleServiceImpl implements MallAftersaleService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private JstMallExchangeOrderMapper jstMallExchangeOrderMapper;
    @Autowired
    private JstMallGoodsMapper jstMallGoodsMapper;
    @Autowired
    private RefundRecordLookupMapper refundRecordLookupMapper;
    @Autowired
    private PointsAccountLookupMapper pointsAccountLookupMapper;
    @Autowired
    private UserCouponLookupMapper userCouponLookupMapper;
    @Autowired
    private UserRightsLookupMapper userRightsLookupMapper;
    @Autowired
    private MallOrderLookupMapper mallOrderLookupMapper;
    @Autowired
    private JstLockTemplate jstLockTemplate;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired
    private WxPayService wxPayService;

    @Value("${jst.wxpay.enabled:false}")
    private boolean wxPayEnabled;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "AFTERSALE_APPLY", target = "#{req.exchangeId}", recordResult = true)
    public AftersaleApplyResVO apply(AftersaleApplyDTO req) {
        Long userId = currentUserId();
        String lockKey = "lock:mall:aftersale:" + req.getExchangeId();
        return jstLockTemplate.execute(lockKey, 3, 10, () -> doApply(userId, req));
    }

    @Override
    public List<AftersaleListVO> selectMyList(String status) {
        return refundRecordLookupMapper.selectMyList(currentUserId(), resolveRefundStatuses(status));
    }

    @Override
    public AftersaleDetailVO getMyDetail(Long refundId) {
        AftersaleDetailVO detail = requireDetail(refundId);
        assertOwner(detail.getUserId());
        return detail;
    }

    @Override
    public List<AftersaleListVO> selectAdminList(AftersaleQueryReqDTO query) {
        AftersaleQueryReqDTO finalQuery = query == null ? new AftersaleQueryReqDTO() : query;
        return refundRecordLookupMapper.selectAdminList(finalQuery, resolveRefundStatuses(finalQuery.getStatus()));
    }

    @Override
    public AftersaleDetailVO getAdminDetail(Long refundId) {
        return requireDetail(refundId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "AFTERSALE_APPROVE", target = "#{refundId}")
    public void approve(Long refundId, AftersaleAuditDTO req) {
        Map<String, Object> preview = requireBizDetail(refundId);
        Long exchangeId = longValue(preview.get("exchangeId"));
        jstLockTemplate.execute("lock:mall:aftersale:" + exchangeId, 3, 10, () -> {
            doApprove(refundId, req);
            return null;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "AFTERSALE_REJECT", target = "#{refundId}")
    public void reject(Long refundId, AftersaleAuditDTO req) {
        Map<String, Object> preview = requireBizDetail(refundId);
        Long exchangeId = longValue(preview.get("exchangeId"));
        jstLockTemplate.execute("lock:mall:aftersale:" + exchangeId, 3, 10, () -> {
            doReject(refundId, req);
            return null;
        });
    }

    private AftersaleApplyResVO doApply(Long userId, AftersaleApplyDTO req) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        JstMallExchangeOrder exchange = requireExchange(req.getExchangeId());
        assertOwner(exchange.getUserId());
        if (refundRecordLookupMapper.countBlockingByExchangeId(exchange.getExchangeId()) > 0) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_STATUS_INVALID.message(),
                    BizErrorCode.MALL_AFTERSALE_STATUS_INVALID.code());
        }
        JstMallGoods goods = requireGoods(exchange.getGoodsId());
        MallGoodsType goodsType = MallGoodsType.fromDb(goods.getGoodsType());
        validateApply(exchange, goodsType, req.getRefundType());
        if (goodsType == MallGoodsType.VIRTUAL) {
            validateVirtualRefundable(exchange.getExchangeId());
        }

        Long orderId = exchange.getOrderId() == null ? exchange.getExchangeId() : exchange.getOrderId();
        String refundNo = "RFM" + snowflakeIdWorker.nextId();
        refundRecordLookupMapper.insertRefundRecord(refundNo, orderId, exchange.getExchangeId(), req.getRefundType(),
                "user", req.getReason(), safeAmount(exchange.getCashAmount()), safePoints(exchange.getPointsUsed()),
                "pending", exchange.getStatus(), operator, now);

        exchange.setStatus(MallExchangeStatus.AFTERSALE.dbValue());
        exchange.setAftersaleStatus("applying");
        exchange.setUpdateBy(operator);
        exchange.setUpdateTime(now);
        jstMallExchangeOrderMapper.updateJstMallExchangeOrder(exchange);

        if (exchange.getOrderId() != null) {
            Map<String, Object> order = mallOrderLookupMapper.selectOrderSnapshot(exchange.getOrderId());
            if (order != null && !"aftersale".equals(stringValue(order.get("orderStatus")))) {
                mallOrderLookupMapper.updateOrderStatusByExpected(exchange.getOrderId(),
                        stringValue(order.get("orderStatus")), "aftersale", null, operator, now);
            }
        }

        Map<String, Object> inserted = refundRecordLookupMapper.selectByRefundNo(refundNo);
        AftersaleApplyResVO vo = new AftersaleApplyResVO();
        vo.setRefundId(longValue(inserted.get("refundId")));
        vo.setRefundNo(refundNo);
        return vo;
    }

    private void doApprove(Long refundId, AftersaleAuditDTO req) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        Long operatorId = currentUserId();
        Map<String, Object> detail = requireBizDetail(refundId);
        if (!Objects.equals("pending", stringValue(detail.get("status")))) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_STATUS_INVALID.message(),
                    BizErrorCode.MALL_AFTERSALE_STATUS_INVALID.code());
        }
        Long exchangeId = longValue(detail.get("exchangeId"));
        JstMallExchangeOrder exchange = requireExchange(exchangeId);
        JstMallGoods goods = requireGoods(exchange.getGoodsId());

        if (refundRecordLookupMapper.updateStatusByExpected(refundId, "pending", "approved",
                req.getAuditRemark(), null, null, null, operatorId, null, operator, now, null) == 0) {
            throwDataTampered();
        }
        exchange.setAftersaleStatus("refunding");
        exchange.setUpdateBy(operator);
        exchange.setUpdateTime(now);
        jstMallExchangeOrderMapper.updateJstMallExchangeOrder(exchange);

        processCashRefund(detail);
        refundPoints(exchange.getUserId(), refundId, safePoints(exchange.getPointsUsed()), operatorId, operator, now);
        handleVirtualRollback(goods, exchangeId, operator, now);
        if (MallGoodsType.fromDb(goods.getGoodsType()) == MallGoodsType.PHYSICAL
                && MallExchangeStatus.fromDb(exchange.getStatus()) == MallExchangeStatus.AFTERSALE
                && "pending_ship".equals(stringValue(detail.get("refundRemark")))) {
            goods.setStock(goods.getStock() + exchange.getQuantity().intValue());
            goods.setUpdateBy(operator);
            goods.setUpdateTime(now);
            jstMallGoodsMapper.updateJstMallGoods(goods);
        }

        exchange.setStatus(MallExchangeStatus.CLOSED.dbValue());
        exchange.setAftersaleStatus("completed");
        exchange.setUpdateBy(operator);
        exchange.setUpdateTime(now);
        jstMallExchangeOrderMapper.updateJstMallExchangeOrder(exchange);

        if (exchange.getOrderId() != null) {
            String orderStatus = StringUtils.defaultIfBlank(stringValue(detail.get("orderStatus")), "aftersale");
            if (mallOrderLookupMapper.updateOrderRefundInfo(exchange.getOrderId(), orderStatus, "closed",
                    "full", operator, now) == 0) {
                mallOrderLookupMapper.updateOrderRefundInfo(exchange.getOrderId(), "aftersale", "closed",
                        "full", operator, now);
            }
        }

        if (refundRecordLookupMapper.updateStatusByExpected(refundId, "approved", "completed",
                req.getAuditRemark(), safeAmount(exchange.getCashAmount()), safePoints(exchange.getPointsUsed()), 0,
                operatorId, now, operator, now, null) == 0) {
            throwDataTampered();
        }
    }

    private void doReject(Long refundId, AftersaleAuditDTO req) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        Long operatorId = currentUserId();
        Map<String, Object> detail = requireBizDetail(refundId);
        if (!Objects.equals("pending", stringValue(detail.get("status")))) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_STATUS_INVALID.message(),
                    BizErrorCode.MALL_AFTERSALE_STATUS_INVALID.code());
        }
        Long exchangeId = longValue(detail.get("exchangeId"));
        JstMallExchangeOrder exchange = requireExchange(exchangeId);
        String originalExchangeStatus = StringUtils.defaultIfBlank(stringValue(detail.get("refundRemark")),
                MallExchangeStatus.COMPLETED.dbValue());

        if (refundRecordLookupMapper.updateStatusByExpected(refundId, "pending", "rejected",
                req.getAuditRemark(), null, null, null, operatorId, null, operator, now, null) == 0) {
            throwDataTampered();
        }

        exchange.setStatus(originalExchangeStatus);
        exchange.setAftersaleStatus("rejected");
        exchange.setUpdateBy(operator);
        exchange.setUpdateTime(now);
        jstMallExchangeOrderMapper.updateJstMallExchangeOrder(exchange);

        if (exchange.getOrderId() != null) {
            String restoreStatus = mapOrderStatusByExchangeStatus(originalExchangeStatus);
            mallOrderLookupMapper.updateOrderStatusByExpected(exchange.getOrderId(),
                    StringUtils.defaultIfBlank(stringValue(detail.get("orderStatus")), "aftersale"),
                    restoreStatus, null, operator, now);
        }
    }

    private void validateApply(JstMallExchangeOrder exchange, MallGoodsType goodsType, String refundType) {
        MallExchangeStatus exchangeStatus = MallExchangeStatus.fromDb(exchange.getStatus());
        if (goodsType == MallGoodsType.VIRTUAL) {
            if (exchangeStatus != MallExchangeStatus.COMPLETED || !"refund_only".equals(refundType)) {
                throw new ServiceException(BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.message(),
                        BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.code());
            }
            return;
        }
        if (exchangeStatus == MallExchangeStatus.PENDING_PAY || exchangeStatus == MallExchangeStatus.CLOSED) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.message(),
                    BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.code());
        }
        if (exchangeStatus == MallExchangeStatus.PENDING_SHIP && !"refund_only".equals(refundType)) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.message(),
                    BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.code());
        }
        if ((exchangeStatus == MallExchangeStatus.SHIPPED || exchangeStatus == MallExchangeStatus.COMPLETED)
                && !"return_refund".equals(refundType)) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.message(),
                    BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.code());
        }
        if (exchangeStatus != MallExchangeStatus.PENDING_SHIP
                && exchangeStatus != MallExchangeStatus.SHIPPED
                && exchangeStatus != MallExchangeStatus.COMPLETED) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.message(),
                    BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.code());
        }
    }

    private void validateVirtualRefundable(Long exchangeId) {
        if (userCouponLookupMapper.countUsedCouponsByExchangeId(exchangeId) > 0) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_COUPON_USED.message(),
                    BizErrorCode.MALL_AFTERSALE_COUPON_USED.code());
        }
        if (userRightsLookupMapper.countConsumedRightsByExchangeId(exchangeId) > 0) {
            throw new ServiceException(BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.message(),
                    BizErrorCode.MALL_AFTERSALE_NOT_ALLOWED.code());
        }
    }

    private void processCashRefund(Map<String, Object> detail) {
        BigDecimal cashAmount = decimalValue(detail.get("cashAmount"));
        if (cashAmount.compareTo(ZERO_AMOUNT) <= 0) {
            return;
        }
        if (!wxPayEnabled) {
            return;
        }
        WxPayService.RefundRequest request = new WxPayService.RefundRequest();
        request.setRefundNo(stringValue(detail.get("refundNo")));
        request.setOrderNo(stringValue(detail.get("orderNo")));
        request.setOutTradeNo(stringValue(detail.get("thirdPartyNo")));
        request.setRefundCash(cashAmount);
        request.setReason(stringValue(detail.get("reason")));
        WxPayService.RefundResult result = wxPayService.refund(request);
        if (result == null || !result.isSuccess()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.message(),
                    BizErrorCode.JST_ORDER_REFUND_EXECUTE_FAIL.code());
        }
    }

    private void refundPoints(Long userId, Long refundId, Long points, Long operatorId, String operator, Date now) {
        if (points == null || points <= 0) {
            return;
        }
        jstLockTemplate.execute("lock:points:freeze:" + userId, 3, 5, () -> {
            Map<String, Object> snapshot = requirePointsSnapshot(userId);
            Long accountId = longValue(snapshot.get("accountId"));
            Integer version = intValue(snapshot.get("version"));
            Long userAvailable = longValue(snapshot.get("userAvailablePoints"));
            Long userFrozen = longValue(snapshot.get("userFrozenPoints"));
            String ownerType = StringUtils.defaultIfBlank(stringValue(snapshot.get("ownerType")), "user");
            if (accountId != null && pointsAccountLookupMapper.refundPointsAccount(accountId, version, points, operator, now) == 0) {
                throwPointsConflict();
            }
            if (pointsAccountLookupMapper.refundUserPoints(userId, userAvailable, userFrozen, points, operator, now) == 0) {
                throwPointsConflict();
            }
            if (accountId != null) {
                pointsAccountLookupMapper.insertRefundLedger(accountId, ownerType, userId, refundId, points,
                        userAvailable + points, operatorId, operator, now, "商城售后退回积分");
            }
            return null;
        });
    }

    private void handleVirtualRollback(JstMallGoods goods, Long exchangeId, String operator, Date now) {
        if (MallGoodsType.fromDb(goods.getGoodsType()) != MallGoodsType.VIRTUAL) {
            return;
        }
        userCouponLookupMapper.refundCouponsByExchangeId(exchangeId, operator, now);
        userRightsLookupMapper.revokeRightsByExchangeId(exchangeId, operator, now);
    }

    private Map<String, Object> requirePointsSnapshot(Long userId) {
        Map<String, Object> snapshot = pointsAccountLookupMapper.selectPointsSnapshot(userId);
        if (snapshot == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        return snapshot;
    }

    private JstMallExchangeOrder requireExchange(Long exchangeId) {
        JstMallExchangeOrder exchange = jstMallExchangeOrderMapper.selectJstMallExchangeOrderByExchangeId(exchangeId);
        if (exchange == null || !"0".equals(StringUtils.defaultString(exchange.getDelFlag(), "0"))) {
            throw new ServiceException(BizErrorCode.JST_MALL_EXCHANGE_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_EXCHANGE_NOT_FOUND.code());
        }
        return exchange;
    }

    private JstMallGoods requireGoods(Long goodsId) {
        JstMallGoods goods = jstMallGoodsMapper.selectJstMallGoodsByGoodsId(goodsId);
        if (goods == null || !"0".equals(StringUtils.defaultString(goods.getDelFlag(), "0"))) {
            throw new ServiceException(BizErrorCode.JST_MALL_GOODS_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_GOODS_NOT_FOUND.code());
        }
        return goods;
    }

    private AftersaleDetailVO requireDetail(Long refundId) {
        AftersaleDetailVO detail = refundRecordLookupMapper.selectDetail(refundId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.code());
        }
        return detail;
    }

    private Map<String, Object> requireBizDetail(Long refundId) {
        Map<String, Object> detail = refundRecordLookupMapper.selectBizDetail(refundId);
        if (detail == null || detail.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_REFUND_NOT_FOUND.code());
        }
        return detail;
    }

    private List<String> resolveRefundStatuses(String status) {
        AftersaleStatus aftersaleStatus = AftersaleStatus.fromValue(status);
        if (aftersaleStatus != null) {
            return aftersaleStatus.refundStatuses();
        }
        return StringUtils.isBlank(status) ? Collections.emptyList() : List.of(status);
    }

    private void assertOwner(Long userId) {
        if (!Objects.equals(userId, currentUserId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private String mapOrderStatusByExchangeStatus(String exchangeStatus) {
        return switch (StringUtils.defaultString(exchangeStatus)) {
            case "pending_ship", "shipped" -> "paid";
            case "completed" -> "completed";
            default -> "paid";
        };
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }

    private void throwPointsConflict() {
        throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
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

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Long longValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? null : Long.valueOf(text);
    }

    private Integer intValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? 0 : Integer.valueOf(text);
    }

    private long safePoints(Long points) {
        return points == null ? 0L : points;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal decimalValue(Object value) {
        if (value == null) {
            return ZERO_AMOUNT;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
    }
}
