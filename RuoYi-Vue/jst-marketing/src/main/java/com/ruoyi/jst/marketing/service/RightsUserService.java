package com.ruoyi.jst.marketing.service;

import com.ruoyi.jst.marketing.dto.RightsWriteoffApplyDTO;
import com.ruoyi.jst.marketing.vo.MyRightsVO;
import com.ruoyi.jst.marketing.vo.RightsDetailVO;
import com.ruoyi.jst.marketing.vo.RightsWriteoffRecordVO;

import java.util.List;
import java.util.Map;

/**
 * Rights user service.
 */
public interface RightsUserService {

    List<MyRightsVO> selectMyRights(Long userId, String status);

    RightsDetailVO getDetail(Long userId, Long userRightsId);

    Map<String, Object> applyWriteoff(Long userId, Long userRightsId, RightsWriteoffApplyDTO req);

    /**
     * 查询当前用户的核销记录列表（跨权益），分页由 Controller 调 PageHelper 控制。
     *
     * @param userId       当前用户 ID
     * @param rightsId     可选权益 ID 过滤（= user_rights_id）
     * @param status       状态过滤（含 'invalid' 虚拟值 = used+expired+rolled_back）
     * @return 核销记录列表
     */
    List<RightsWriteoffRecordVO> selectMyWriteoffRecords(Long userId, Long rightsId, String status);
}
