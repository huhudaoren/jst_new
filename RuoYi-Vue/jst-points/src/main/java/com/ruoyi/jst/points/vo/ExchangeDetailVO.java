package com.ruoyi.jst.points.vo;

/**
 * 兑换订单详情出参。
 *
 * @author jst
 * @since 1.0.0
 */
public class ExchangeDetailVO extends ExchangeListVO {

    private Object addressSnapshot;

    private String aftersaleStatus;

    public Object getAddressSnapshot() {
        return addressSnapshot;
    }

    public void setAddressSnapshot(Object addressSnapshot) {
        this.addressSnapshot = addressSnapshot;
    }

    public String getAftersaleStatus() {
        return aftersaleStatus;
    }

    public void setAftersaleStatus(String aftersaleStatus) {
        this.aftersaleStatus = aftersaleStatus;
    }
}
