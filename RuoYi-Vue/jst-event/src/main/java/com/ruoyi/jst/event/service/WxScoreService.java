package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.PublicScoreQueryDTO;
import com.ruoyi.jst.event.dto.WxScoreQueryDTO;
import com.ruoyi.jst.event.vo.PublicScoreVO;
import com.ruoyi.jst.event.vo.WxScoreVO;

import java.util.List;

/**
 * Service for mini-program score pages.
 */
public interface WxScoreService {

    List<WxScoreVO> selectMyScores(Long userId, WxScoreQueryDTO query);

    List<PublicScoreVO> queryPublicScores(PublicScoreQueryDTO query);
}
