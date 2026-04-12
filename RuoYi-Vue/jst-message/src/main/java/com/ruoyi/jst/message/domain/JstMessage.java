package com.ruoyi.jst.message.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 站内消息对象 jst_message。
 *
 * @author jst
 * @since 1.0.0
 */
public class JstMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 接收用户ID */
    private Long userId;

    /** 消息类型 system/business */
    private String type;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 已读状态 0未读 1已读 */
    private Integer readStatus;

    /** 业务类型 */
    private String bizType;

    /** 业务ID */
    private Long bizId;

    /** 逻辑删除标记 */
    private String delFlag;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("messageId", getMessageId())
                .append("userId", getUserId())
                .append("type", getType())
                .append("title", getTitle())
                .append("content", getContent())
                .append("readStatus", getReadStatus())
                .append("bizType", getBizType())
                .append("bizId", getBizId())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("delFlag", getDelFlag())
                .toString();
    }
}

