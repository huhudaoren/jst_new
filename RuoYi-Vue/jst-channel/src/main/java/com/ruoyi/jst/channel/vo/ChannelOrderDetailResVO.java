package com.ruoyi.jst.channel.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 渠道订单详情返回对象。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelOrderDetailResVO {

    private Long orderId;
    private String orderNo;
    private Long contestId;
    private String contestName;
    private Long participantId;
    private String participantName;
    private String studentName;
    private String mobileMasked;
    private String orderStatus;
    private String refundStatus;
    private BigDecimal priceOriginal;
    private BigDecimal couponDiscount;
    private BigDecimal pointsDiscount;
    private Long pointsUsed;
    private BigDecimal userNetPay;
    private BigDecimal payAmount;
    private BigDecimal platformFee;
    private BigDecimal rebateBase;
    private BigDecimal rebateRate;
    private BigDecimal rebateAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    private ChannelOwnerVO channelOwner;
    private ParticipantSnapshotVO participantSnapshot;
    private List<TimelineItemVO> timeline;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getContestName() {
        return contestName;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getMobileMasked() {
        return mobileMasked;
    }

    public void setMobileMasked(String mobileMasked) {
        this.mobileMasked = mobileMasked;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public BigDecimal getPriceOriginal() {
        return priceOriginal;
    }

    public void setPriceOriginal(BigDecimal priceOriginal) {
        this.priceOriginal = priceOriginal;
    }

    public BigDecimal getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(BigDecimal couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public BigDecimal getPointsDiscount() {
        return pointsDiscount;
    }

    public void setPointsDiscount(BigDecimal pointsDiscount) {
        this.pointsDiscount = pointsDiscount;
    }

    public Long getPointsUsed() {
        return pointsUsed;
    }

    public void setPointsUsed(Long pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public BigDecimal getUserNetPay() {
        return userNetPay;
    }

    public void setUserNetPay(BigDecimal userNetPay) {
        this.userNetPay = userNetPay;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getRebateBase() {
        return rebateBase;
    }

    public void setRebateBase(BigDecimal rebateBase) {
        this.rebateBase = rebateBase;
    }

    public BigDecimal getRebateRate() {
        return rebateRate;
    }

    public void setRebateRate(BigDecimal rebateRate) {
        this.rebateRate = rebateRate;
    }

    public BigDecimal getRebateAmount() {
        return rebateAmount;
    }

    public void setRebateAmount(BigDecimal rebateAmount) {
        this.rebateAmount = rebateAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public ChannelOwnerVO getChannelOwner() {
        return channelOwner;
    }

    public void setChannelOwner(ChannelOwnerVO channelOwner) {
        this.channelOwner = channelOwner;
    }

    public ParticipantSnapshotVO getParticipantSnapshot() {
        return participantSnapshot;
    }

    public void setParticipantSnapshot(ParticipantSnapshotVO participantSnapshot) {
        this.participantSnapshot = participantSnapshot;
    }

    public List<TimelineItemVO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<TimelineItemVO> timeline) {
        this.timeline = timeline;
    }

    /**
     * 渠道归属信息。
     */
    public static class ChannelOwnerVO {

        private Long channelId;
        private String name;
        private String channelType;

        public Long getChannelId() {
            return channelId;
        }

        public void setChannelId(Long channelId) {
            this.channelId = channelId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getChannelType() {
            return channelType;
        }

        public void setChannelType(String channelType) {
            this.channelType = channelType;
        }
    }

    /**
     * 订单时间轴项。
     */
    public static class TimelineItemVO {

        private String step;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date time;

        public String getStep() {
            return step;
        }

        public void setStep(String step) {
            this.step = step;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }
    }

    /**
     * 参赛人快照兜底对象。
     */
    public static class ParticipantSnapshotVO {

        private String originalParticipantName;

        public String getOriginalParticipantName() {
            return originalParticipantName;
        }

        public void setOriginalParticipantName(String originalParticipantName) {
            this.originalParticipantName = originalParticipantName;
        }
    }
}
