package com.ruoyi.jst.message.dto;

/**
 * Query DTO for mini-program message center.
 */
public class WxMessageQueryDTO {

    private String type;
    private Integer readStatus;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }
}
