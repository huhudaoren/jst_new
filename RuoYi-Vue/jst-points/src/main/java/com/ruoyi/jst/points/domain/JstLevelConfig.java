package com.ruoyi.jst.points.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 等级配置对象 jst_level_config
 * 
 * @author ruoyi
 * @date 2026-04-08
 */
public class JstLevelConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 等级ID */
    private Long levelId;

    /** 等级编码 */
    @Excel(name = "等级编码")
    private String levelCode;

    /** 等级名称 */
    @Excel(name = "等级名称")
    private String levelName;

    /** 等级序号（用于排序与升降） */
    @Excel(name = "等级序号", readConverterExp = "用=于排序与升降")
    private Long levelNo;

    /** 成长值门槛 */
    @Excel(name = "成长值门槛")
    private Long growthThreshold;

    /** 等级图标URL */
    @Excel(name = "等级图标URL")
    private String icon;

    /** 适用角色：student/channel/both */
    @Excel(name = "适用角色：student/channel/both")
    private String applicableRole;

    /** 启停：0停 1启 */
    @Excel(name = "启停：0停 1启")
    private Integer status;

    /** 逻辑删除：0存在 2删除 */
    private String delFlag;

    public void setLevelId(Long levelId) 
    {
        this.levelId = levelId;
    }

    public Long getLevelId() 
    {
        return levelId;
    }

    public void setLevelCode(String levelCode) 
    {
        this.levelCode = levelCode;
    }

    public String getLevelCode() 
    {
        return levelCode;
    }

    public void setLevelName(String levelName) 
    {
        this.levelName = levelName;
    }

    public String getLevelName() 
    {
        return levelName;
    }

    public void setLevelNo(Long levelNo) 
    {
        this.levelNo = levelNo;
    }

    public Long getLevelNo() 
    {
        return levelNo;
    }

    public void setGrowthThreshold(Long growthThreshold) 
    {
        this.growthThreshold = growthThreshold;
    }

    public Long getGrowthThreshold() 
    {
        return growthThreshold;
    }

    public void setIcon(String icon) 
    {
        this.icon = icon;
    }

    public String getIcon() 
    {
        return icon;
    }

    public void setApplicableRole(String applicableRole) 
    {
        this.applicableRole = applicableRole;
    }

    public String getApplicableRole() 
    {
        return applicableRole;
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
            .append("levelId", getLevelId())
            .append("levelCode", getLevelCode())
            .append("levelName", getLevelName())
            .append("levelNo", getLevelNo())
            .append("growthThreshold", getGrowthThreshold())
            .append("icon", getIcon())
            .append("applicableRole", getApplicableRole())
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
