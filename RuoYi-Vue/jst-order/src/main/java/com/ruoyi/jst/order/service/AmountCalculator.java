package com.ruoyi.jst.order.service;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.order.enums.OrderType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
 * 订单金额计算器。
 * <p>
 * 关联表：jst_order_main / jst_order_item / jst_user_coupon / jst_rebate_rule。
 *
 * @author jst
 * @since 1.0.0
 */
@Component
public class AmountCalculator {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    /**
     * 按任务卡公式重算订单金额。
     *
     * @param input 计算输入
     * @return 计算结果
     */
    public AmountResult calculate(CalcInput input) {
        BigDecimal listAmount = safeAmount(input.getListAmount());
        BigDecimal couponAmount = calculateCouponAmount(listAmount, input);
        BigDecimal pointsDeductAmount = BigDecimal.valueOf(safePoints(input.getPointsToUse()))
                .divide(BigDecimal.valueOf(input.getPointsCashRate()), 2, RoundingMode.HALF_UP);
        BigDecimal netPayAmount = listAmount.subtract(couponAmount).subtract(pointsDeductAmount)
                .setScale(2, RoundingMode.HALF_UP);
        if (netPayAmount.compareTo(ZERO) < 0) {
            throw new ServiceException(BizErrorCode.JST_ORDER_AMOUNT_MISMATCH.message(),
                    BizErrorCode.JST_ORDER_AMOUNT_MISMATCH.code());
        }
        BigDecimal serviceFee = calculateServiceFee(listAmount, input.getServiceFeeMode(), input.getServiceFeeValue());
        BigDecimal rebateBase = listAmount.subtract(serviceFee);
        if (rebateBase.compareTo(ZERO) < 0) {
            rebateBase = ZERO;
        }
        BigDecimal rebateAmount = calculateRebateAmount(rebateBase, input.getRebateMode(), input.getRebateValue());

        AmountResult result = new AmountResult();
        result.setListAmount(listAmount);
        result.setCouponAmount(couponAmount);
        result.setPointsDeductAmount(pointsDeductAmount);
        result.setPointsUsed(safePoints(input.getPointsToUse()));
        result.setPlatformBearAmount(ZERO);
        result.setNetPayAmount(netPayAmount);
        result.setServiceFee(serviceFee);
        result.setRebateBase(rebateBase);
        result.setRebateAmount(rebateAmount);
        result.setOrderType(netPayAmount.compareTo(ZERO) == 0 ? OrderType.ZERO_PRICE.dbValue() : OrderType.NORMAL.dbValue());
        return result;
    }

    private BigDecimal calculateCouponAmount(BigDecimal listAmount, CalcInput input) {
        if (StringUtils.isBlank(input.getCouponType())) {
            return ZERO;
        }
        BigDecimal couponAmount = ZERO;
        BigDecimal faceValue = safeAmount(input.getCouponFaceValue());
        BigDecimal threshold = safeAmount(input.getCouponThresholdAmount());
        BigDecimal discountRate = input.getCouponDiscountRate();
        switch (input.getCouponType()) {
            case "full_reduce":
                if (listAmount.compareTo(threshold) >= 0) {
                    couponAmount = faceValue;
                }
                break;
            case "direct_reduce":
                couponAmount = faceValue;
                break;
            case "discount":
                couponAmount = listAmount.multiply(BigDecimal.ONE.subtract(discountRate))
                        .setScale(2, RoundingMode.HALF_UP);
                break;
            case "contest_specific":
                assertContestSpecificApplicable(input.getApplicableContestIds(), input.getContestId());
                if (discountRate != null) {
                    couponAmount = listAmount.multiply(BigDecimal.ONE.subtract(discountRate))
                            .setScale(2, RoundingMode.HALF_UP);
                } else {
                    couponAmount = faceValue;
                }
                break;
            default:
                throw new ServiceException("未知优惠券类型: " + input.getCouponType(),
                        BizErrorCode.JST_MARKETING_COUPON_INVALID.code());
        }
        if (couponAmount.compareTo(listAmount) > 0) {
            couponAmount = listAmount;
        }
        return couponAmount.setScale(2, RoundingMode.HALF_UP);
    }

    private void assertContestSpecificApplicable(String applicableContestIds, Long contestId) {
        if (StringUtils.isBlank(applicableContestIds) || contestId == null) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_NOT_APPLICABLE.message(),
                    BizErrorCode.JST_MARKETING_COUPON_NOT_APPLICABLE.code());
        }
        boolean matched = Arrays.stream(applicableContestIds.split(","))
                .map(String::trim)
                .anyMatch(id -> id.equals(String.valueOf(contestId)));
        if (!matched) {
            throw new ServiceException(BizErrorCode.JST_MARKETING_COUPON_NOT_APPLICABLE.message(),
                    BizErrorCode.JST_MARKETING_COUPON_NOT_APPLICABLE.code());
        }
    }

    private BigDecimal calculateServiceFee(BigDecimal listAmount, String serviceFeeMode, BigDecimal serviceFeeValue) {
        BigDecimal value = safeAmount(serviceFeeValue);
        if (StringUtils.isBlank(serviceFeeMode) || "none".equals(serviceFeeMode)) {
            return ZERO;
        }
        if ("rate".equals(serviceFeeMode)) {
            return listAmount.multiply(value).setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRebateAmount(BigDecimal rebateBase, String rebateMode, BigDecimal rebateValue) {
        BigDecimal value = safeAmount(rebateValue);
        if (StringUtils.isBlank(rebateMode) || value.compareTo(ZERO) <= 0) {
            return ZERO;
        }
        if ("rate".equals(rebateMode)) {
            return rebateBase.multiply(value).setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? ZERO : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private Long safePoints(Long points) {
        return points == null ? 0L : points;
    }

    /**
     * 金额计算入参。
     */
    public static class CalcInput {
        private BigDecimal listAmount;
        private Long contestId;
        private String couponType;
        private BigDecimal couponFaceValue;
        private BigDecimal couponDiscountRate;
        private BigDecimal couponThresholdAmount;
        private String applicableContestIds;
        private Long pointsToUse;
        private Integer pointsCashRate;
        private String serviceFeeMode;
        private BigDecimal serviceFeeValue;
        private String rebateMode;
        private BigDecimal rebateValue;

        public BigDecimal getListAmount() {
            return listAmount;
        }

        public void setListAmount(BigDecimal listAmount) {
            this.listAmount = listAmount;
        }

        public Long getContestId() {
            return contestId;
        }

        public void setContestId(Long contestId) {
            this.contestId = contestId;
        }

        public String getCouponType() {
            return couponType;
        }

        public void setCouponType(String couponType) {
            this.couponType = couponType;
        }

        public BigDecimal getCouponFaceValue() {
            return couponFaceValue;
        }

        public void setCouponFaceValue(BigDecimal couponFaceValue) {
            this.couponFaceValue = couponFaceValue;
        }

        public BigDecimal getCouponDiscountRate() {
            return couponDiscountRate;
        }

        public void setCouponDiscountRate(BigDecimal couponDiscountRate) {
            this.couponDiscountRate = couponDiscountRate;
        }

        public BigDecimal getCouponThresholdAmount() {
            return couponThresholdAmount;
        }

        public void setCouponThresholdAmount(BigDecimal couponThresholdAmount) {
            this.couponThresholdAmount = couponThresholdAmount;
        }

        public String getApplicableContestIds() {
            return applicableContestIds;
        }

        public void setApplicableContestIds(String applicableContestIds) {
            this.applicableContestIds = applicableContestIds;
        }

        public Long getPointsToUse() {
            return pointsToUse;
        }

        public void setPointsToUse(Long pointsToUse) {
            this.pointsToUse = pointsToUse;
        }

        public Integer getPointsCashRate() {
            return pointsCashRate;
        }

        public void setPointsCashRate(Integer pointsCashRate) {
            this.pointsCashRate = pointsCashRate;
        }

        public String getServiceFeeMode() {
            return serviceFeeMode;
        }

        public void setServiceFeeMode(String serviceFeeMode) {
            this.serviceFeeMode = serviceFeeMode;
        }

        public BigDecimal getServiceFeeValue() {
            return serviceFeeValue;
        }

        public void setServiceFeeValue(BigDecimal serviceFeeValue) {
            this.serviceFeeValue = serviceFeeValue;
        }

        public String getRebateMode() {
            return rebateMode;
        }

        public void setRebateMode(String rebateMode) {
            this.rebateMode = rebateMode;
        }

        public BigDecimal getRebateValue() {
            return rebateValue;
        }

        public void setRebateValue(BigDecimal rebateValue) {
            this.rebateValue = rebateValue;
        }
    }

    /**
     * 金额计算结果。
     */
    public static class AmountResult {
        private BigDecimal listAmount;
        private BigDecimal couponAmount;
        private BigDecimal pointsDeductAmount;
        private Long pointsUsed;
        private BigDecimal platformBearAmount;
        private BigDecimal netPayAmount;
        private BigDecimal serviceFee;
        private BigDecimal rebateBase;
        private BigDecimal rebateAmount;
        private String orderType;

        public BigDecimal getListAmount() {
            return listAmount;
        }

        public void setListAmount(BigDecimal listAmount) {
            this.listAmount = listAmount;
        }

        public BigDecimal getCouponAmount() {
            return couponAmount;
        }

        public void setCouponAmount(BigDecimal couponAmount) {
            this.couponAmount = couponAmount;
        }

        public BigDecimal getPointsDeductAmount() {
            return pointsDeductAmount;
        }

        public void setPointsDeductAmount(BigDecimal pointsDeductAmount) {
            this.pointsDeductAmount = pointsDeductAmount;
        }

        public Long getPointsUsed() {
            return pointsUsed;
        }

        public void setPointsUsed(Long pointsUsed) {
            this.pointsUsed = pointsUsed;
        }

        public BigDecimal getPlatformBearAmount() {
            return platformBearAmount;
        }

        public void setPlatformBearAmount(BigDecimal platformBearAmount) {
            this.platformBearAmount = platformBearAmount;
        }

        public BigDecimal getNetPayAmount() {
            return netPayAmount;
        }

        public void setNetPayAmount(BigDecimal netPayAmount) {
            this.netPayAmount = netPayAmount;
        }

        public BigDecimal getServiceFee() {
            return serviceFee;
        }

        public void setServiceFee(BigDecimal serviceFee) {
            this.serviceFee = serviceFee;
        }

        public BigDecimal getRebateBase() {
            return rebateBase;
        }

        public void setRebateBase(BigDecimal rebateBase) {
            this.rebateBase = rebateBase;
        }

        public BigDecimal getRebateAmount() {
            return rebateAmount;
        }

        public void setRebateAmount(BigDecimal rebateAmount) {
            this.rebateAmount = rebateAmount;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }
    }
}
