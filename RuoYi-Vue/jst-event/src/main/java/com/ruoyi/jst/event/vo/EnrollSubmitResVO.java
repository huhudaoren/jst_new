package com.ruoyi.jst.event.vo;

/**
 * 报名保存/提交结果 VO。
 *
 * @author jst
 * @since 1.0.0
 */
public class EnrollSubmitResVO {

    private Long enrollId;

    private String enrollNo;

    public Long getEnrollId() {
        return enrollId;
    }

    public void setEnrollId(Long enrollId) {
        this.enrollId = enrollId;
    }

    public String getEnrollNo() {
        return enrollNo;
    }

    public void setEnrollNo(String enrollNo) {
        this.enrollNo = enrollNo;
    }
}
