package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.dto.PublicScoreQueryDTO;
import com.ruoyi.jst.event.vo.PublicScoreVO;
import com.ruoyi.jst.event.vo.WxScoreVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for mini-program score queries.
 */
public interface WxScoreMapper {

    List<WxScoreVO> selectMyScoreList(@Param("userId") Long userId, @Param("contestId") Long contestId);

    List<WxScoreVO> selectMyScoreFallbackList(@Param("userId") Long userId, @Param("contestId") Long contestId);

    List<Long> selectIssuedCertScoreIds(@Param("scoreIds") List<Long> scoreIds);

    List<PublicScoreVO> selectPublicScoreList(PublicScoreQueryDTO query);

    List<PublicScoreVO> selectPublicScoreFallbackList(PublicScoreQueryDTO query);
}
