package com.ruoyi.jst.channel.vo;

import java.math.BigDecimal;

/**
 * 渠道工作台活跃学生返回对象。
 *
 * @author jst
 * @since 1.0.0
 */
public class ChannelTopStudentResVO {

    private String studentName;
    private Integer enrollCount;
    private BigDecimal payAmount;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getEnrollCount() {
        return enrollCount;
    }

    public void setEnrollCount(Integer enrollCount) {
        this.enrollCount = enrollCount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
}
