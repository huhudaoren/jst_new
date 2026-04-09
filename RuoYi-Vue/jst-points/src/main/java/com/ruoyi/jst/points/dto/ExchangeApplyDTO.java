package com.ruoyi.jst.points.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * 商城兑换下单请求。
 *
 * @author jst
 * @since 1.0.0
 */
public class ExchangeApplyDTO {

    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量至少为1")
    private Integer quantity;

    private Long addressId;

    private Map<String, Object> addressSnapshot;

    @NotBlank(message = "支付方式不能为空")
    private String payMethod;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Map<String, Object> getAddressSnapshot() {
        return addressSnapshot;
    }

    public void setAddressSnapshot(Map<String, Object> addressSnapshot) {
        this.addressSnapshot = addressSnapshot;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
}
