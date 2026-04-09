package com.ruoyi.jst.points.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.domain.JstPaymentRecord;
import com.ruoyi.jst.order.pay.WxPayService;
import com.ruoyi.jst.points.domain.JstMallExchangeOrder;
import com.ruoyi.jst.points.domain.JstMallGoods;
import com.ruoyi.jst.points.dto.ExchangeApplyDTO;
import com.ruoyi.jst.points.dto.ExchangeQueryReqDTO;
import com.ruoyi.jst.points.dto.ExchangeShipDTO;
import com.ruoyi.jst.points.enums.MallExchangeStatus;
import com.ruoyi.jst.points.enums.MallGoodsStatus;
import com.ruoyi.jst.points.enums.MallGoodsType;
import com.ruoyi.jst.points.enums.MallVirtualTargetType;
import com.ruoyi.jst.points.mapper.JstMallExchangeOrderMapper;
import com.ruoyi.jst.points.mapper.MallExchangeOrderMapperExt;
import com.ruoyi.jst.points.mapper.MallGoodsMapperExt;
import com.ruoyi.jst.points.mapper.lookup.MallOrderLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.PointsAccountLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.UserCouponLookupMapper;
import com.ruoyi.jst.points.mapper.lookup.UserRightsLookupMapper;
import com.ruoyi.jst.points.service.MallExchangeService;
import com.ruoyi.jst.points.vo.ExchangeApplyResVO;
import com.ruoyi.jst.points.vo.ExchangeDetailVO;
import com.ruoyi.jst.points.vo.ExchangeListVO;
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
 * 商城兑换领域服务实现。
 */
@Service
public class MallExchangeServiceImpl implements MallExchangeService {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired private JstMallExchangeOrderMapper jstMallExchangeOrderMapper;
    @Autowired private MallExchangeOrderMapperExt mallExchangeOrderMapperExt;
    @Autowired private MallGoodsMapperExt mallGoodsMapperExt;
    @Autowired private PointsAccountLookupMapper pointsAccountLookupMapper;
    @Autowired private MallOrderLookupMapper mallOrderLookupMapper;
    @Autowired private UserCouponLookupMapper userCouponLookupMapper;
    @Autowired private UserRightsLookupMapper userRightsLookupMapper;
    @Autowired private JstLockTemplate jstLockTemplate;
    @Autowired private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired private WxPayService wxPayService;

    @Value("${jst.wxpay.enabled:false}")
    private boolean wxPayEnabled;

    /** 查询后台兑换订单列表。 */
    @Override
    public List<ExchangeListVO> selectAdminList(ExchangeQueryReqDTO query) {
        return mallExchangeOrderMapperExt.selectAdminList(query == null ? new ExchangeQueryReqDTO() : query);
    }

    /** 查询我的兑换订单列表。 */
    @Override
    public List<ExchangeListVO> selectMyList(String status) {
        return mallExchangeOrderMapperExt.selectWxList(currentUserId(), status);
    }

    /** 查询后台详情。 */
    @Override
    public ExchangeDetailVO getAdminDetail(Long exchangeId) {
        return requireDetail(exchangeId);
    }

    /** 查询我的详情。 */
    @Override
    public ExchangeDetailVO getMyDetail(Long exchangeId) {
        ExchangeDetailVO detail = requireDetail(exchangeId);
        assertOwner(detail.getUserId());
        return detail;
    }

    /** 兑换下单。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "EXCHANGE_APPLY", target = "#{req.goodsId}", recordResult = true)
    public ExchangeApplyResVO apply(ExchangeApplyDTO req) {
        Long userId = currentUserId();
        // TX: 商城兑换下单事务
        // LOCK: lock:mall:goods:stock:{goodsId}
        return jstLockTemplate.execute("lock:mall:goods:stock:" + req.getGoodsId(), 3, 5,
                () -> doApply(userId, req));
    }

    /** mock 支付成功。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "EXCHANGE_MOCK_PAY", target = "#{exchangeId}")
    public void mockPaySuccess(Long exchangeId) {
        if (wxPayEnabled) {
            throw new ServiceException(BizErrorCode.JST_ORDER_PAY_FAIL.message(),
                    BizErrorCode.JST_ORDER_PAY_FAIL.code());
        }
        JstMallExchangeOrder preview = requireExchange(exchangeId);
        assertOwner(preview.getUserId());
        // LOCK: lock:mall:exchange:{orderId}
        jstLockTemplate.execute("lock:mall:exchange:" + exchangeId, 2, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = operatorName();
            JstMallExchangeOrder exchange = requireExchange(exchangeId);
            assertOwner(exchange.getUserId());
            if (MallExchangeStatus.fromDb(exchange.getStatus()) != MallExchangeStatus.PENDING_PAY) {
                return null;
            }
            JstMallGoods goods = requireGoods(exchange.getGoodsId());
            if (exchange.getOrderId() != null) {
                JstOrderMain order = requireOrder(exchange.getOrderId());
                mallOrderLookupMapper.updateOrderStatusByExpected(order.getOrderId(), "pending_pay", "paid",
                        now, operator, now);
                insertPaymentRecordIfAbsent(order, exchange, now, operator);
            }
            advancePaid(exchange, goods, now, operator);
            return null;
        });
    }

    /** 取消待支付兑换单。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "EXCHANGE_CANCEL", target = "#{exchangeId}")
    public void cancel(Long exchangeId) {
        JstMallExchangeOrder preview = requireExchange(exchangeId);
        assertOwner(preview.getUserId());
        // LOCK: lock:mall:exchange:{orderId}
        jstLockTemplate.execute("lock:mall:exchange:" + exchangeId, 2, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = operatorName();
            JstMallExchangeOrder exchange = requireExchange(exchangeId);
            assertOwner(exchange.getUserId());
            MallExchangeStatus current = MallExchangeStatus.fromDb(exchange.getStatus());
            if (current != MallExchangeStatus.PENDING_PAY) {
                throw new ServiceException(BizErrorCode.JST_MALL_CANCEL_NOT_ALLOWED.message(),
                        BizErrorCode.JST_MALL_CANCEL_NOT_ALLOWED.code());
            }
            current.assertCanTransitTo(MallExchangeStatus.CLOSED); // SM-18
            if (mallExchangeOrderMapperExt.updateStatusByExpected(exchangeId, current.dbValue(),
                    MallExchangeStatus.CLOSED.dbValue(), exchange.getOrderId(), operator, now) == 0) {
                throwDataTampered();
            }
            if (exchange.getOrderId() != null) {
                mallOrderLookupMapper.updateOrderStatusByExpected(exchange.getOrderId(), "pending_pay", "closed",
                        null, operator, now);
            }
            if (safePoints(exchange.getPointsUsed()) > 0) {
                jstLockTemplate.execute("lock:points:freeze:" + exchange.getUserId(), 3, 5, () -> {
                    unfreezePoints(exchange.getUserId(), exchange.getExchangeId(), safePoints(exchange.getPointsUsed()),
                            now, operator, "商城兑换取消解冻积分");
                    return null;
                });
            }
            mallGoodsMapperExt.restoreStock(exchange.getGoodsId(), exchange.getQuantity().intValue(), operator, now);
            return null;
        });
    }

    /** 后台发货。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "EXCHANGE_SHIP", target = "#{exchangeId}")
    public void ship(Long exchangeId, ExchangeShipDTO req) {
        jstLockTemplate.execute("lock:mall:exchange:" + exchangeId, 2, 5, () -> {
            Date now = DateUtils.getNowDate();
            JstMallExchangeOrder exchange = requireExchange(exchangeId);
            MallExchangeStatus.fromDb(exchange.getStatus()).assertCanTransitTo(MallExchangeStatus.SHIPPED);
            if (mallExchangeOrderMapperExt.shipByExpected(exchangeId, exchange.getStatus(),
                    MallExchangeStatus.SHIPPED.dbValue(), req.getLogisticsCompany(), req.getLogisticsNo(),
                    now, operatorName(), now) == 0) {
                throwDataTampered();
            }
            return null;
        });
    }

    /** 后台完成订单。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "积分商城", action = "EXCHANGE_COMPLETE", target = "#{exchangeId}")
    public void complete(Long exchangeId) {
        jstLockTemplate.execute("lock:mall:exchange:" + exchangeId, 2, 5, () -> {
            Date now = DateUtils.getNowDate();
            String operator = operatorName();
            JstMallExchangeOrder exchange = requireExchange(exchangeId);
            MallExchangeStatus.fromDb(exchange.getStatus()).assertCanTransitTo(MallExchangeStatus.COMPLETED);
            if (mallExchangeOrderMapperExt.completeByExpected(exchangeId, exchange.getStatus(),
                    MallExchangeStatus.COMPLETED.dbValue(), now, operator, now) == 0) {
                throwDataTampered();
            }
            if (exchange.getOrderId() != null) {
                mallOrderLookupMapper.updateOrderStatusByExpected(exchange.getOrderId(), "paid", "completed",
                        null, operator, now);
            }
            return null;
        });
    }

    private ExchangeApplyResVO doApply(Long userId, ExchangeApplyDTO req) {
        Date now = DateUtils.getNowDate();
        String operator = operatorName();
        JstMallGoods goods = requireGoodsForSale(req.getGoodsId());
        MallGoodsType goodsType = MallGoodsType.fromDb(goods.getGoodsType());
        validateRole(goods.getRoleLimit());
        validateAddress(goodsType, req);
        validatePayMethod(req.getPayMethod(), goods.getCashPrice());
        long totalPoints = safePoints(goods.getPointsPrice()) * req.getQuantity();
        BigDecimal totalCash = safeAmount(goods.getCashPrice()).multiply(BigDecimal.valueOf(req.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        JstOrderMain order = totalCash.compareTo(ZERO_AMOUNT) > 0 ? buildCashOrder(userId, totalCash, now, operator) : null;
        if (order != null) {
            mallOrderLookupMapper.insertOrderMain(order);
        }
        JstMallExchangeOrder exchange = buildExchangeOrder(userId, goods, req, totalPoints, totalCash, order, now, operator);
        jstMallExchangeOrderMapper.insertJstMallExchangeOrder(exchange);
        if (totalPoints > 0) {
            jstLockTemplate.execute("lock:points:freeze:" + userId, 3, 5, () -> {
                freezePoints(userId, exchange.getExchangeId(), totalPoints, now, operator, "商城兑换冻结积分");
                return null;
            });
        }
        if (mallGoodsMapperExt.decreaseStock(goods.getGoodsId(), req.getQuantity(), operator, now) == 0) {
            throw new ServiceException(BizErrorCode.JST_MALL_STOCK_INSUFFICIENT.message(),
                    BizErrorCode.JST_MALL_STOCK_INSUFFICIENT.code());
        }

        ExchangeApplyResVO vo = new ExchangeApplyResVO();
        vo.setExchangeId(exchange.getExchangeId());
        vo.setExchangeNo(exchange.getExchangeNo());
        vo.setOrderId(exchange.getOrderId());
        vo.setStatus(exchange.getStatus());
        vo.setPointsUsed(exchange.getPointsUsed());
        vo.setCashAmount(exchange.getCashAmount());
        if (totalCash.compareTo(ZERO_AMOUNT) > 0) {
            vo.setWechatPrepay(wxPayService.unifiedOrder(order));
            return vo;
        }
        advancePaid(exchange, goods, now, operator);
        vo.setStatus(requireExchange(exchange.getExchangeId()).getStatus());
        return vo;
    }

    private void advancePaid(JstMallExchangeOrder exchange, JstMallGoods goods, Date now, String operator) {
        if (mallExchangeOrderMapperExt.updateStatusByExpected(exchange.getExchangeId(), MallExchangeStatus.PENDING_PAY.dbValue(),
                MallExchangeStatus.PAID.dbValue(), exchange.getOrderId(), operator, now) == 0) {
            throwDataTampered();
        }
        if (safePoints(exchange.getPointsUsed()) > 0) {
            jstLockTemplate.execute("lock:points:freeze:" + exchange.getUserId(), 3, 5, () -> {
                consumePoints(exchange.getUserId(), exchange.getExchangeId(), safePoints(exchange.getPointsUsed()),
                        now, operator, "商城兑换消费积分");
                return null;
            });
        }
        if (MallGoodsType.fromDb(goods.getGoodsType()) == MallGoodsType.VIRTUAL) {
            deliverVirtual(exchange, goods, now, operator);
            if (mallExchangeOrderMapperExt.updateStatusByExpected(exchange.getExchangeId(), MallExchangeStatus.PAID.dbValue(),
                    MallExchangeStatus.COMPLETED.dbValue(), exchange.getOrderId(), operator, now) == 0) {
                throwDataTampered();
            }
            if (exchange.getOrderId() != null) {
                mallOrderLookupMapper.updateOrderStatusByExpected(exchange.getOrderId(), "paid", "completed",
                        null, operator, now);
            }
            return;
        }
        if (mallExchangeOrderMapperExt.updateStatusByExpected(exchange.getExchangeId(), MallExchangeStatus.PAID.dbValue(),
                MallExchangeStatus.PENDING_SHIP.dbValue(), exchange.getOrderId(), operator, now) == 0) {
            throwDataTampered();
        }
    }

    private void deliverVirtual(JstMallExchangeOrder exchange, JstMallGoods goods, Date now, String operator) {
        Map<String, Object> config = parseVirtualConfig(goods.getRemark());
        MallVirtualTargetType type = MallVirtualTargetType.fromDb(String.valueOf(config.get("virtualTargetType")));
        if (!type.supported()) {
            throwParamInvalid();
        }
        if (type == MallVirtualTargetType.COUPON) {
            Long couponTemplateId = longNullable(config.get("couponTemplateId"));
            Map<String, Object> template = userCouponLookupMapper.selectCouponTemplate(couponTemplateId);
            if (template == null) {
                throwParamInvalid();
            }
            Date validStart = dateValue(template.get("validStart"));
            validStart = validStart == null ? now : validStart;
            Date validEnd = resolveCouponValidEnd(template, now);
            for (int i = 0; i < exchange.getQuantity(); i++) {
                userCouponLookupMapper.insertUserCoupon(couponTemplateId, exchange.getUserId(), exchange.getExchangeNo(),
                        "mall_exchange", "unused", validStart, validEnd, exchange.getExchangeId(),
                        operator, now, "商城兑换发券");
            }
            return;
        }
        Long rightsTemplateId = longNullable(config.get("rightsTemplateId"));
        Map<String, Object> template = userRightsLookupMapper.selectRightsTemplate(rightsTemplateId);
        if (template == null) {
            throwParamInvalid();
        }
        Date validEnd = DateUtils.addDays(now, intValue(template.get("validDays")) > 0 ? intValue(template.get("validDays")) : 3650);
        BigDecimal quota = decimalValue(template.get("quotaValue"));
        for (int i = 0; i < exchange.getQuantity(); i++) {
            userRightsLookupMapper.insertUserRights(rightsTemplateId, "user", exchange.getUserId(),
                    "mall_exchange", exchange.getExchangeId(), quota, now, validEnd,
                    "available", operator, now, "商城兑换发放权益");
        }
    }

    private JstMallExchangeOrder buildExchangeOrder(Long userId, JstMallGoods goods, ExchangeApplyDTO req, long totalPoints,
                                                    BigDecimal totalCash, JstOrderMain order, Date now, String operator) {
        JstMallExchangeOrder exchange = new JstMallExchangeOrder();
        exchange.setExchangeNo(snowflakeIdWorker.nextBizNo("MX"));
        exchange.setUserId(userId);
        exchange.setGoodsId(goods.getGoodsId());
        exchange.setQuantity(req.getQuantity().longValue());
        exchange.setPointsUsed(totalPoints);
        exchange.setCashAmount(totalCash);
        exchange.setOrderId(order == null ? null : order.getOrderId());
        exchange.setAddressSnapshotJson(req.getAddressSnapshot() == null ? null : JSON.toJSONString(req.getAddressSnapshot()));
        exchange.setStatus(MallExchangeStatus.PENDING_PAY.dbValue());
        exchange.setAftersaleStatus("none");
        exchange.setCreateBy(operator);
        exchange.setCreateTime(now);
        exchange.setUpdateBy(operator);
        exchange.setUpdateTime(now);
        exchange.setDelFlag("0");
        return exchange;
    }

    private JstOrderMain buildCashOrder(Long userId, BigDecimal totalCash, Date now, String operator) {
        JstOrderMain order = new JstOrderMain();
        order.setOrderNo(snowflakeIdWorker.nextBizNo("OD"));
        order.setOrderType("exchange");
        order.setBusinessType("mall");
        order.setUserId(userId);
        order.setParticipantId(0L);
        order.setListAmount(totalCash);
        order.setCouponAmount(ZERO_AMOUNT);
        order.setPointsDeductAmount(ZERO_AMOUNT);
        order.setPointsUsed(0L);
        order.setPlatformBearAmount(ZERO_AMOUNT);
        order.setNetPayAmount(totalCash);
        order.setServiceFee(ZERO_AMOUNT);
        order.setPayMethod("wechat");
        order.setPayInitiator("self");
        order.setPayInitiatorId(userId);
        order.setOrderStatus("pending_pay");
        order.setRefundStatus("none");
        order.setAllowSelfRefund(0);
        order.setCreateBy(operator);
        order.setCreateTime(now);
        order.setUpdateBy(operator);
        order.setUpdateTime(now);
        order.setDelFlag("0");
        return order;
    }

    private void freezePoints(Long userId, Long exchangeId, long points, Date now, String operator, String remark) {
        PointsSnapshot snapshot = loadPointsSnapshot(userId);
        if (snapshot.accountId != null && pointsAccountLookupMapper.freezePointsAccount(snapshot.accountId, snapshot.version,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (pointsAccountLookupMapper.freezeUserPoints(userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (snapshot.accountId != null) {
            pointsAccountLookupMapper.insertPointsLedger(snapshot.accountId, snapshot.ownerType, snapshot.userId,
                    "freeze", "exchange", exchangeId, -points, snapshot.userAvailablePoints - points,
                    userId, operator, now, remark);
        }
    }

    private void consumePoints(Long userId, Long exchangeId, long points, Date now, String operator, String remark) {
        PointsSnapshot snapshot = loadPointsSnapshot(userId);
        if (snapshot.accountId != null && pointsAccountLookupMapper.consumePointsAccount(snapshot.accountId, snapshot.version,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (pointsAccountLookupMapper.consumeUserPoints(userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (snapshot.accountId != null) {
            pointsAccountLookupMapper.insertPointsLedger(snapshot.accountId, snapshot.ownerType, snapshot.userId,
                    "spend", "exchange", exchangeId, 0L, snapshot.userAvailablePoints,
                    userId, operator, now, remark);
        }
    }

    private void unfreezePoints(Long userId, Long exchangeId, long points, Date now, String operator, String remark) {
        PointsSnapshot snapshot = loadPointsSnapshot(userId);
        if (snapshot.accountId != null && pointsAccountLookupMapper.unfreezePointsAccount(snapshot.accountId, snapshot.version,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (pointsAccountLookupMapper.unfreezeUserPoints(userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints,
                points, operator, now) == 0) {
            throwPointsConflict();
        }
        if (snapshot.accountId != null) {
            pointsAccountLookupMapper.insertPointsLedger(snapshot.accountId, snapshot.ownerType, snapshot.userId,
                    "unfreeze", "exchange", exchangeId, points, snapshot.userAvailablePoints + points,
                    userId, operator, now, remark);
        }
    }

    private PointsSnapshot loadPointsSnapshot(Long userId) {
        Map<String, Object> map = pointsAccountLookupMapper.selectPointsSnapshot(userId);
        if (map == null || map.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        PointsSnapshot snapshot = PointsSnapshot.fromMap(map);
        if (snapshot.accountId == null) {
            pointsAccountLookupMapper.insertPointsAccount(userId, snapshot.userAvailablePoints, snapshot.userFrozenPoints,
                    operatorName(), DateUtils.getNowDate());
            snapshot = PointsSnapshot.fromMap(pointsAccountLookupMapper.selectPointsSnapshot(userId));
        }
        if (snapshot.userAvailablePoints < 0) {
            throwPointsConflict();
        }
        return snapshot;
    }

    private void insertPaymentRecordIfAbsent(JstOrderMain order, JstMallExchangeOrder exchange, Date now, String operator) {
        JstPaymentRecord existing = mallOrderLookupMapper.selectLatestPayment(order.getOrderId());
        if (existing != null && "success".equals(existing.getPayStatus())) {
            return;
        }
        JstPaymentRecord record = new JstPaymentRecord();
        record.setPaymentNo(snowflakeIdWorker.nextBizNo("PAY"));
        record.setOrderId(order.getOrderId());
        record.setPayMethod(order.getPayMethod());
        record.setCashAmount(safeAmount(exchange.getCashAmount()));
        record.setPointsAmount(ZERO_AMOUNT);
        record.setPointsUsed(0L);
        record.setThirdPartyNo("MOCK_TXN_" + order.getOrderNo());
        record.setPayStatus("success");
        record.setPayTime(now);
        record.setOperatorId(currentUserId());
        record.setCreateBy(operator);
        record.setCreateTime(now);
        record.setUpdateBy(operator);
        record.setUpdateTime(now);
        record.setDelFlag("0");
        mallOrderLookupMapper.insertPaymentRecord(record);
    }

    private JstMallGoods requireGoodsForSale(Long goodsId) {
        JstMallGoods goods = requireGoods(goodsId);
        if (MallGoodsStatus.fromDb(goods.getStatus()) != MallGoodsStatus.ON) {
            throw new ServiceException(BizErrorCode.JST_MALL_GOODS_OFFLINE.message(),
                    BizErrorCode.JST_MALL_GOODS_OFFLINE.code());
        }
        return goods;
    }

    private JstMallGoods requireGoods(Long goodsId) {
        JstMallGoods goods = mallGoodsMapperExt.selectByIdForUpdate(goodsId);
        if (goods == null || !"0".equals(defaultDelFlag(goods.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_MALL_GOODS_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_GOODS_NOT_FOUND.code());
        }
        return goods;
    }

    private JstMallExchangeOrder requireExchange(Long exchangeId) {
        JstMallExchangeOrder exchange = jstMallExchangeOrderMapper.selectJstMallExchangeOrderByExchangeId(exchangeId);
        if (exchange == null || !"0".equals(defaultDelFlag(exchange.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_MALL_EXCHANGE_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_EXCHANGE_NOT_FOUND.code());
        }
        return exchange;
    }

    private ExchangeDetailVO requireDetail(Long exchangeId) {
        ExchangeDetailVO detail = mallExchangeOrderMapperExt.selectDetail(exchangeId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_MALL_EXCHANGE_NOT_FOUND.message(),
                    BizErrorCode.JST_MALL_EXCHANGE_NOT_FOUND.code());
        }
        if (detail.getAddressSnapshot() instanceof String text && StringUtils.isNotBlank(text)) {
            detail.setAddressSnapshot(JSON.parse(text));
        }
        return detail;
    }

    private JstOrderMain requireOrder(Long orderId) {
        JstOrderMain order = mallOrderLookupMapper.selectOrderById(orderId);
        if (order == null || !"0".equals(defaultDelFlag(order.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_ORDER_NOT_FOUND.message(),
                    BizErrorCode.JST_ORDER_NOT_FOUND.code());
        }
        return order;
    }

    private void validateRole(String roleLimit) {
        boolean hasStudent = JstLoginContext.hasRole("jst_student");
        boolean hasChannel = JstLoginContext.hasRole("jst_channel");
        boolean allowed = switch (StringUtils.defaultString(roleLimit)) {
            case "student" -> hasStudent;
            case "channel" -> hasChannel;
            case "both", "" -> hasStudent || hasChannel;
            default -> false;
        };
        if (!allowed) {
            throw new ServiceException(BizErrorCode.JST_MALL_ROLE_NOT_ALLOWED.message(),
                    BizErrorCode.JST_MALL_ROLE_NOT_ALLOWED.code());
        }
    }

    private void validateAddress(MallGoodsType goodsType, ExchangeApplyDTO req) {
        if (goodsType == MallGoodsType.PHYSICAL && (req.getAddressSnapshot() == null || req.getAddressSnapshot().isEmpty())) {
            throwParamInvalid();
        }
    }

    private void validatePayMethod(String payMethod, BigDecimal cashAmount) {
        if (safeAmount(cashAmount).compareTo(ZERO_AMOUNT) > 0 && !"wechat".equals(payMethod)) {
            throwParamInvalid();
        }
        if (safeAmount(cashAmount).compareTo(ZERO_AMOUNT) <= 0 && !"points".equals(payMethod)) {
            throwParamInvalid();
        }
    }

    private void assertOwner(Long userId) {
        if (!Objects.equals(userId, currentUserId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private Date resolveCouponValidEnd(Map<String, Object> template, Date now) {
        int validDays = intValue(template.get("validDays"));
        if (validDays > 0) {
            return DateUtils.addDays(now, validDays);
        }
        Date validEnd = dateValue(template.get("validEnd"));
        return validEnd == null ? DateUtils.addDays(now, 3650) : validEnd;
    }

    private Map<String, Object> parseVirtualConfig(String remark) {
        if (StringUtils.isBlank(remark)) {
            throwParamInvalid();
        }
        return JSON.parseObject(remark);
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

    private long safePoints(Long points) {
        return points == null ? 0L : points;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
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

    private int intValue(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? 0 : Integer.parseInt(text);
    }

    private BigDecimal decimalValue(Object value) {
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

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
    }

    private void throwDataTampered() {
        throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
    }

    private void throwPointsConflict() {
        throw new ServiceException(BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.message(),
                BizErrorCode.JST_POINTS_CONCURRENT_CONFLICT.code());
    }

    private void throwParamInvalid() {
        throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                BizErrorCode.JST_COMMON_PARAM_INVALID.code());
    }

    private static class PointsSnapshot {
        private Long userId;
        private long userAvailablePoints;
        private long userFrozenPoints;
        private Long accountId;
        private Integer version;
        private String ownerType;

        private static PointsSnapshot fromMap(Map<String, Object> map) {
            PointsSnapshot snapshot = new PointsSnapshot();
            snapshot.userId = map.get("userId") == null ? null : ((Number) map.get("userId")).longValue();
            snapshot.userAvailablePoints = map.get("userAvailablePoints") == null ? 0L : ((Number) map.get("userAvailablePoints")).longValue();
            snapshot.userFrozenPoints = map.get("userFrozenPoints") == null ? 0L : ((Number) map.get("userFrozenPoints")).longValue();
            snapshot.accountId = map.get("accountId") == null ? null : ((Number) map.get("accountId")).longValue();
            snapshot.version = map.get("version") == null ? 0 : ((Number) map.get("version")).intValue();
            snapshot.ownerType = map.get("ownerType") == null ? "user" : String.valueOf(map.get("ownerType"));
            return snapshot;
        }
    }
}
