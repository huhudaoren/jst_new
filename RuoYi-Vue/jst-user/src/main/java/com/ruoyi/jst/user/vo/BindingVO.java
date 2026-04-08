package com.ruoyi.jst.user.vo;

import java.util.Date;

/**
 * 学生-渠道绑定响应 VO。
 * <p>
 * 强制：仅暴露展示字段，不直接返回 Entity。
 *
 * @author jst
 * @since 1.0.0
 */
public class BindingVO {

    /** 绑定ID。 */
    private Long bindingId;

    /** 渠道方ID。 */
    private Long channelId;

    /** 渠道方名称。 */
    private String channelName;

    /** 渠道方类型。 */
    private String channelType;

    /** 绑定时间。 */
    private Date bindTime;

    /** 解绑时间。 */
    private Date unbindTime;

    /** 当前绑定状态。 */
    private String status;

    public Long getBindingId() {
        return bindingId;
    }

    public void setBindingId(Long bindingId) {
        this.bindingId = bindingId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }

    public Date getUnbindTime() {
        return unbindTime;
    }

    public void setUnbindTime(Date unbindTime) {
        this.unbindTime = unbindTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
