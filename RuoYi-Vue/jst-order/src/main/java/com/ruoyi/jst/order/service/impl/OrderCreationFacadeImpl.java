package com.ruoyi.jst.order.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.order.api.OrderCreationFacade;
import com.ruoyi.jst.order.domain.JstOrderItem;
import com.ruoyi.jst.order.domain.JstOrderMain;
import com.ruoyi.jst.order.dto.AppointmentOrderCreateReqDTO;
import com.ruoyi.jst.order.enums.OrderRefundStatus;
import com.ruoyi.jst.order.enums.PayMethod;
import com.ruoyi.jst.order.mapper.JstOrderItemMapper;
import com.ruoyi.jst.order.mapper.JstOrderMainMapper;
import com.ruoyi.jst.order.vo.AppointmentOrderCreateResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 预约子订单创建门面实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class OrderCreationFacadeImpl implements OrderCreationFacade {

    private static final BigDecimal ZERO_AMOUNT = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private JstOrderMainMapper jstOrderMainMapper;

    @Autowired
    private JstOrderItemMapper jstOrderItemMapper;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentOrderCreateResVO createAppointmentOrder(AppointmentOrderCreateReqDTO req) {
        if (req == null || req.getParticipantId() == null || req.getContestId() == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        Date now = DateUtils.getNowDate();
        String operator = StringUtils.isBlank(req.getOperator()) ? "system" : req.getOperator();
        BigDecimal amount = safeAmount(req.getListAmount());
        String payMethod = resolvePayMethod(req.getPayMethod(), amount);
        boolean zeroPrice = amount.compareTo(ZERO_AMOUNT) <= 0;

        JstOrderMain order = new JstOrderMain();
        order.setOrderNo(snowflakeIdWorker.nextBizNo("OD"));
        order.setOrderType("appointment");
        order.setBusinessType("appointment");
        order.setUserId(req.getUserId());
        order.setParticipantId(req.getParticipantId());
        order.setChannelId(req.getChannelId());
        order.setContestId(req.getContestId());
        order.setPartnerId(req.getPartnerId());
        order.setTeamAppointmentId(req.getTeamAppointmentId());
        order.setListAmount(amount);
        order.setCouponAmount(ZERO_AMOUNT);
        order.setPointsDeductAmount(ZERO_AMOUNT);
        order.setPointsUsed(0L);
        order.setPlatformBearAmount(ZERO_AMOUNT);
        order.setNetPayAmount(amount);
        order.setServiceFee(ZERO_AMOUNT);
        order.setPayMethod(zeroPrice ? null : payMethod);
        order.setPayInitiator(StringUtils.isBlank(req.getPayInitiator()) ? "channel" : req.getPayInitiator());
        order.setPayInitiatorId(req.getPayInitiatorId());
        order.setPayTime(zeroPrice ? now : null);
        order.setOrderStatus(zeroPrice ? "completed" : "pending_pay");
        order.setRefundStatus(OrderRefundStatus.NONE.dbValue());
        order.setAftersaleDeadline(req.getAftersaleDeadline());
        order.setAllowSelfRefund(0);
        order.setCreateBy(operator);
        order.setCreateTime(now);
        order.setUpdateBy(operator);
        order.setUpdateTime(now);
        order.setDelFlag("0");
        jstOrderMainMapper.insertJstOrderMain(order);

        JstOrderItem item = new JstOrderItem();
        item.setOrderId(order.getOrderId());
        item.setSkuType("appointment_member");
        item.setRefId(req.getRefId());
        item.setItemName(StringUtils.defaultIfBlank(req.getItemName(), "线下预约费"));
        item.setQuantity(1L);
        item.setListAmount(amount);
        item.setCouponShare(ZERO_AMOUNT);
        item.setPointsShare(ZERO_AMOUNT);
        item.setNetPayShare(amount);
        item.setServiceFeeShare(ZERO_AMOUNT);
        item.setRebateShare(ZERO_AMOUNT);
        item.setRefundAmount(ZERO_AMOUNT);
        item.setRefundPoints(0L);
        item.setCreateBy(operator);
        item.setCreateTime(now);
        item.setUpdateBy(operator);
        item.setUpdateTime(now);
        item.setDelFlag("0");
        jstOrderItemMapper.insertJstOrderItem(item);

        AppointmentOrderCreateResVO resVO = new AppointmentOrderCreateResVO();
        resVO.setOrderId(order.getOrderId());
        resVO.setOrderNo(order.getOrderNo());
        resVO.setOrderType(order.getOrderType());
        resVO.setOrderStatus(order.getOrderStatus());
        resVO.setNetPayAmount(order.getNetPayAmount());
        return resVO;
    }

    private String resolvePayMethod(String payMethod, BigDecimal amount) {
        if (amount.compareTo(ZERO_AMOUNT) <= 0) {
            return null;
        }
        if (StringUtils.isBlank(payMethod)) {
            return PayMethod.WECHAT.dbValue();
        }
        return PayMethod.fromDb(payMethod).dbValue();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO_AMOUNT : amount.setScale(2, RoundingMode.HALF_UP);
    }
}
