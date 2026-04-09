package com.ruoyi.jst.marketing.service;

import com.ruoyi.jst.marketing.dto.RightsWriteoffApplyDTO;
import com.ruoyi.jst.marketing.vo.MyRightsVO;
import com.ruoyi.jst.marketing.vo.RightsDetailVO;

import java.util.List;
import java.util.Map;

/**
 * Rights user service.
 */
public interface RightsUserService {

    List<MyRightsVO> selectMyRights(Long userId, String status);

    RightsDetailVO getDetail(Long userId, Long userRightsId);

    Map<String, Object> applyWriteoff(Long userId, Long userRightsId, RightsWriteoffApplyDTO req);
}
