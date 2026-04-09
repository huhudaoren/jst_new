package com.ruoyi.jst.marketing.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.marketing.service.CampaignService;
import com.ruoyi.jst.marketing.vo.CampaignVO;
import com.ruoyi.jst.marketing.vo.ClaimableCouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Mock campaign topic service because no jst_campaign table exists in current schema.
 */
@Service
public class CampaignServiceImpl implements CampaignService {

    @Autowired
    private CouponTemplateServiceImpl couponTemplateService;

    @Override
    public List<CampaignVO> selectList(Long userId) {
        List<CampaignVO> campaigns = mockCampaigns();
        Date now = DateUtils.getNowDate();
        List<CampaignVO> list = new ArrayList<>();
        for (CampaignVO campaign : campaigns) {
            if (!campaign.getStartTime().after(now) && !campaign.getEndTime().before(now)) {
                campaign.setCountdownSeconds(Math.max(0L, (campaign.getEndTime().getTime() - now.getTime()) / 1000));
                list.add(campaign);
            }
        }
        return list;
    }

    @Override
    public CampaignVO getDetail(Long userId, Long campaignId) {
        CampaignVO campaign = mockCampaigns().stream()
                .filter(item -> Objects.equals(item.getCampaignId(), campaignId))
                .findFirst()
                .orElse(null);
        if (campaign == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_PARAM_INVALID.message(),
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        List<ClaimableCouponVO> coupons = couponTemplateService.selectClaimableTemplatesByIds(userId, campaign.getLinkedCouponTemplateIds());
        campaign.setLinkedCoupons(coupons);
        Date now = DateUtils.getNowDate();
        campaign.setCountdownSeconds(Math.max(0L, (campaign.getEndTime().getTime() - now.getTime()) / 1000));
        return campaign;
    }

    private List<CampaignVO> mockCampaigns() {
        Date now = DateUtils.getNowDate();
        List<CampaignVO> list = new ArrayList<>();
        list.add(buildCampaign(30001L, "春季报名福利周",
                "https://fixture.example.com/marketing/campaign-1.jpg",
                DateUtils.addDays(now, -1), DateUtils.addDays(now, 7),
                "当前环境缺少 jst_campaign，先用 mock 专题页承接领券中心。",
                Arrays.asList(9761L, 9762L)));
        list.add(buildCampaign(30002L, "课程体验权益月",
                "https://fixture.example.com/marketing/campaign-2.jpg",
                DateUtils.addDays(now, -2), DateUtils.addDays(now, 5),
                "课程与线下活动权益活动专题页 mock 数据。",
                Arrays.asList(9763L)));
        list.add(buildCampaign(30003L, "赛事冲刺直减专场",
                "https://fixture.example.com/marketing/campaign-3.jpg",
                DateUtils.addDays(now, -3), DateUtils.addDays(now, 3),
                "为前端提供固定 3 条进行中 campaign，不阻塞开发。",
                Arrays.asList(9701L, 9761L)));
        return list;
    }

    private CampaignVO buildCampaign(Long campaignId, String title, String bannerUrl, Date startTime,
                                     Date endTime, String description, List<Long> linkedTemplateIds) {
        CampaignVO vo = new CampaignVO();
        vo.setCampaignId(campaignId);
        vo.setTitle(title);
        vo.setBannerUrl(bannerUrl);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        vo.setDescription(description);
        vo.setLinkedCouponTemplateIds(linkedTemplateIds);
        return vo;
    }
}
