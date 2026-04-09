package com.ruoyi.jst.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户收货地址保存请求
 */
public class UserAddressSaveDTO {

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "收货人手机号格式不正确")
    private String receiverMobile;

    @NotBlank(message = "省不能为空")
    @Size(max = 30, message = "省长度不能超过30")
    private String province;

    @NotBlank(message = "市不能为空")
    @Size(max = 30, message = "市长度不能超过30")
    private String city;

    @NotBlank(message = "区不能为空")
    @Size(max = 30, message = "区长度不能超过30")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200")
    private String addressDetail;

    @Size(max = 10, message = "邮编长度不能超过10")
    private String postalCode;

    @NotNull(message = "是否默认地址不能为空")
    @Min(value = 0, message = "是否默认地址非法")
    @Max(value = 1, message = "是否默认地址非法")
    private Integer isDefault;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}
