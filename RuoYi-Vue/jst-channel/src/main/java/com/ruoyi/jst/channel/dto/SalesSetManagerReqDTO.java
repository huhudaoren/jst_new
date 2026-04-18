package com.ruoyi.jst.channel.dto;

public class SalesSetManagerReqDTO {
    /** null 表示取消主管 */
    private Long managerId;

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long v) { this.managerId = v; }
}
