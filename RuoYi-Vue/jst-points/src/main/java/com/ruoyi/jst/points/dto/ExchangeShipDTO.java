package com.ruoyi.jst.points.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 兑换订单发货请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class ExchangeShipDTO {

    @NotBlank(message = "物流公司不能为空")
    @Size(max = 64, message = "物流公司长度不能超过64")
    private String logisticsCompany;

    @NotBlank(message = "物流单号不能为空")
    @Size(max = 64, message = "物流单号长度不能超过64")
    private String logisticsNo;

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo;
    }
}
