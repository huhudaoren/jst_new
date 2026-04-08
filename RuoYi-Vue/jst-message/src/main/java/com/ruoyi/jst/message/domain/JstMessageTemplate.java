package com.ruoyi.jst.message.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息模板对象 jst_message_template
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstMessageTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private Long templateId;

    /** 模板编码 */
    @Excel(name = "模板编码")
    private String templateCode;

    /** 模板名 */
    @Excel(name = "模板名")
    private String templateName;

    /** 通道：inner站内/sms短信/wechat_template微信模板消息 */
    @Excel(name = "通道：inner站内/sms短信/wechat_template微信模板消息")
    private String channel;

    /** 场景：auth_result/withdraw_result/settle_result/points_change/ship/aftersale */
    @Excel(name = "场景：auth_result/withdraw_result/settle_result/points_change/ship/aftersale")
    private String scene;

    /** 模板内容（含 ${var} 变量占位） */
    @Excel(name = "模板内容", readConverterExp = "含=,$={var},变=量占位")
    private String content;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setTemplateId(Long templateId) 
    {
        this.templateId = templateId;
    }

    public Long getTemplateId() 
    {
        return templateId;
    }

    public void setTemplateCode(String templateCode) 
    {
        this.templateCode = templateCode;
    }

    public String getTemplateCode() 
    {
        return templateCode;
    }

    public void setTemplateName(String templateName) 
    {
        this.templateName = templateName;
    }

    public String getTemplateName() 
    {
        return templateName;
    }

    public void setChannel(String channel) 
    {
        this.channel = channel;
    }

    public String getChannel() 
    {
        return channel;
    }

    public void setScene(String scene) 
    {
        this.scene = scene;
    }

    public String getScene() 
    {
        return scene;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("templateId", getTemplateId())
            .append("templateCode", getTemplateCode())
            .append("templateName", getTemplateName())
            .append("channel", getChannel())
            .append("scene", getScene())
            .append("content", getContent())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
