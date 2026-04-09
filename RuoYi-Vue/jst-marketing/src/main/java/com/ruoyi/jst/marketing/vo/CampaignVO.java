package com.ruoyi.jst.marketing.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Campaign topic view.
 */
public class CampaignVO {

    private Long campaignId;
    private String title;
    private String bannerUrl;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Long countdownSeconds;
    private List<Long> linkedCouponTemplateIds;
    private List<ClaimableCouponVO> linkedCoupons;

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getCountdownSeconds() {
        return countdownSeconds;
    }

    public void setCountdownSeconds(Long countdownSeconds) {
        this.countdownSeconds = countdownSeconds;
    }

    public List<Long> getLinkedCouponTemplateIds() {
        return linkedCouponTemplateIds;
    }

    public void setLinkedCouponTemplateIds(List<Long> linkedCouponTemplateIds) {
        this.linkedCouponTemplateIds = linkedCouponTemplateIds;
    }

    public List<ClaimableCouponVO> getLinkedCoupons() {
        return linkedCoupons;
    }

    public void setLinkedCoupons(List<ClaimableCouponVO> linkedCoupons) {
        this.linkedCoupons = linkedCoupons;
    }
}
