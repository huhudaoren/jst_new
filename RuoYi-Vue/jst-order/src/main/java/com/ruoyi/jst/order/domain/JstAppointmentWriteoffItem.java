package com.ruoyi.jst.order.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 核销子项（团队部分核销/到场/礼品/仪式独立流转）对象 jst_appointment_writeoff_item
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstAppointmentWriteoffItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 子项ID */
    private Long writeoffItemId;

    /** 个人预约ID */
    @Excel(name = "个人预约ID")
    private Long appointmentId;

    /** 团队预约ID */
    @Excel(name = "团队预约ID")
    private Long teamAppointmentId;

    /** 子项类型：arrival到场/gift礼品/ceremony仪式/custom自定义 */
    @Excel(name = "子项类型：arrival到场/gift礼品/ceremony仪式/custom自定义")
    private String itemType;

    /** 自定义子项名称 */
    @Excel(name = "自定义子项名称")
    private String itemName;

    /** 独立二维码URL */
    @Excel(name = "独立二维码URL")
    private String qrCode;

    /** 状态：unused/used/expired/voided */
    @Excel(name = "状态：unused/used/expired/voided")
    private String status;

    /** 核销时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "核销时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date writeoffTime;

    /** 核销操作人 */
    @Excel(name = "核销操作人")
    private Long writeoffUserId;

    /** 核销终端 */
    @Excel(name = "核销终端")
    private String writeoffTerminal;

    /** 已核销人数（团队场景累加） */
    @Excel(name = "已核销人数", readConverterExp = "团=队场景累加")
    private Long writeoffQty;

    /** 总人数（团队场景） */
    @Excel(name = "总人数", readConverterExp = "团=队场景")
    private Long totalQty;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setWriteoffItemId(Long writeoffItemId) 
    {
        this.writeoffItemId = writeoffItemId;
    }

    public Long getWriteoffItemId() 
    {
        return writeoffItemId;
    }

    public void setAppointmentId(Long appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() 
    {
        return appointmentId;
    }

    public void setTeamAppointmentId(Long teamAppointmentId) 
    {
        this.teamAppointmentId = teamAppointmentId;
    }

    public Long getTeamAppointmentId() 
    {
        return teamAppointmentId;
    }

    public void setItemType(String itemType) 
    {
        this.itemType = itemType;
    }

    public String getItemType() 
    {
        return itemType;
    }

    public void setItemName(String itemName) 
    {
        this.itemName = itemName;
    }

    public String getItemName() 
    {
        return itemName;
    }

    public void setQrCode(String qrCode) 
    {
        this.qrCode = qrCode;
    }

    public String getQrCode() 
    {
        return qrCode;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setWriteoffTime(Date writeoffTime) 
    {
        this.writeoffTime = writeoffTime;
    }

    public Date getWriteoffTime() 
    {
        return writeoffTime;
    }

    public void setWriteoffUserId(Long writeoffUserId) 
    {
        this.writeoffUserId = writeoffUserId;
    }

    public Long getWriteoffUserId() 
    {
        return writeoffUserId;
    }

    public void setWriteoffTerminal(String writeoffTerminal) 
    {
        this.writeoffTerminal = writeoffTerminal;
    }

    public String getWriteoffTerminal() 
    {
        return writeoffTerminal;
    }

    public void setWriteoffQty(Long writeoffQty) 
    {
        this.writeoffQty = writeoffQty;
    }

    public Long getWriteoffQty() 
    {
        return writeoffQty;
    }

    public void setTotalQty(Long totalQty) 
    {
        this.totalQty = totalQty;
    }

    public Long getTotalQty() 
    {
        return totalQty;
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
            .append("writeoffItemId", getWriteoffItemId())
            .append("appointmentId", getAppointmentId())
            .append("teamAppointmentId", getTeamAppointmentId())
            .append("itemType", getItemType())
            .append("itemName", getItemName())
            .append("qrCode", getQrCode())
            .append("status", getStatus())
            .append("writeoffTime", getWriteoffTime())
            .append("writeoffUserId", getWriteoffUserId())
            .append("writeoffTerminal", getWriteoffTerminal())
            .append("writeoffQty", getWriteoffQty())
            .append("totalQty", getTotalQty())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
