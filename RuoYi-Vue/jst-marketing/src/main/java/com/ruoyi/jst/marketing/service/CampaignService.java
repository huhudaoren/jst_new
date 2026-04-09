package com.ruoyi.jst.marketing.service;

import com.ruoyi.jst.marketing.vo.CampaignVO;

import java.util.List;

/**
 * Campaign topic service.
 */
public interface CampaignService {

    List<CampaignVO> selectList(Long userId);

    CampaignVO getDetail(Long userId, Long campaignId);
}
