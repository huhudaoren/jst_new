package com.ruoyi.jst.order.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 预约子订单创建入参。
 *
 * @author jst
 * @since 1.0.0
 */
public class AppointmentOrderCreateReqDTO {

    private Long userId;
    private Long participantId;
    private Long channelId;
    private Long contestId;
    private Long partnerId;
    private Long teamAppointmentId;
    private Long refId;
    private BigDecimal listAmount;
    private String itemName;
    private String payMethod;
    private String payInitiator;
    private Long payInitiatorId;
    private Date aftersaleDeadline;
    private String operator;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Long getTeamAppointmentId() {
        return teamAppointmentId;
    }

    public void setTeamAppointmentId(Long teamAppointmentId) {
        this.teamAppointmentId = teamAppointmentId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public BigDecimal getListAmount() {
        return listAmount;
    }

    public void setListAmount(BigDecimal listAmount) {
        this.listAmount = listAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayInitiator() {
        return payInitiator;
    }

    public void setPayInitiator(String payInitiator) {
        this.payInitiator = payInitiator;
    }

    public Long getPayInitiatorId() {
        return payInitiatorId;
    }

    public void setPayInitiatorId(Long payInitiatorId) {
        this.payInitiatorId = payInitiatorId;
    }

    public Date getAftersaleDeadline() {
        return aftersaleDeadline;
    }

    public void setAftersaleDeadline(Date aftersaleDeadline) {
        this.aftersaleDeadline = aftersaleDeadline;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
