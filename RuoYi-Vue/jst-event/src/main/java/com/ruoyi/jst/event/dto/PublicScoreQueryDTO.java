package com.ruoyi.jst.event.dto;

/**
 * Public score query DTO.
 */
public class PublicScoreQueryDTO {

    private String name;
    private String mobile;
    private String idNo;
    private String idCard4;
    private String enrollNo;
    private String contestId;
    private Long contestIdNum;
    private String contestKeyword;
    private String keyword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIdCard4() {
        return idCard4;
    }

    public void setIdCard4(String idCard4) {
        this.idCard4 = idCard4;
    }

    public String getEnrollNo() {
        return enrollNo;
    }

    public void setEnrollNo(String enrollNo) {
        this.enrollNo = enrollNo;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public Long getContestIdNum() {
        return contestIdNum;
    }

    public void setContestIdNum(Long contestIdNum) {
        this.contestIdNum = contestIdNum;
    }

    public String getContestKeyword() {
        return contestKeyword;
    }

    public void setContestKeyword(String contestKeyword) {
        this.contestKeyword = contestKeyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
