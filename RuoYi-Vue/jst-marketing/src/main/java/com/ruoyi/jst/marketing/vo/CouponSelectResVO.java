package com.ruoyi.jst.marketing.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Coupon select pre-calc result.
 */
public class CouponSelectResVO {

    private Long bestCouponId;
    private BigDecimal bestDiscount;
    private BigDecimal netPayAmount;
    private List<AlternativeVO> alternatives;

    public Long getBestCouponId() {
        return bestCouponId;
    }

    public void setBestCouponId(Long bestCouponId) {
        this.bestCouponId = bestCouponId;
    }

    public BigDecimal getBestDiscount() {
        return bestDiscount;
    }

    public void setBestDiscount(BigDecimal bestDiscount) {
        this.bestDiscount = bestDiscount;
    }

    public BigDecimal getNetPayAmount() {
        return netPayAmount;
    }

    public void setNetPayAmount(BigDecimal netPayAmount) {
        this.netPayAmount = netPayAmount;
    }

    public List<AlternativeVO> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<AlternativeVO> alternatives) {
        this.alternatives = alternatives;
    }

    public static class AlternativeVO {
        private Long couponId;
        private Long couponTemplateId;
        private String couponName;
        private BigDecimal discountAmount;
        private BigDecimal netPayAmount;
        private Boolean applicable;
        private String reason;

        public Long getCouponId() {
            return couponId;
        }

        public void setCouponId(Long couponId) {
            this.couponId = couponId;
        }

        public Long getCouponTemplateId() {
            return couponTemplateId;
        }

        public void setCouponTemplateId(Long couponTemplateId) {
            this.couponTemplateId = couponTemplateId;
        }

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
        }

        public BigDecimal getNetPayAmount() {
            return netPayAmount;
        }

        public void setNetPayAmount(BigDecimal netPayAmount) {
            this.netPayAmount = netPayAmount;
        }

        public Boolean getApplicable() {
            return applicable;
        }

        public void setApplicable(Boolean applicable) {
            this.applicable = applicable;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
