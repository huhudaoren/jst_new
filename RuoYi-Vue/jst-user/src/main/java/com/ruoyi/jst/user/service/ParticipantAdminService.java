package com.ruoyi.jst.user.service;

import com.ruoyi.jst.user.dto.ParticipantAdminQueryReqDTO;
import com.ruoyi.jst.user.dto.ParticipantUserMapQueryReqDTO;
import com.ruoyi.jst.user.vo.ParticipantAdminDetailResVO;
import com.ruoyi.jst.user.vo.ParticipantAdminListResVO;
import com.ruoyi.jst.user.vo.ParticipantUserMapAdminResVO;

import java.util.List;

/**
 * 管理端参赛档案查询服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface ParticipantAdminService {

    /**
     * 查询参赛档案分页列表。
     *
     * @param query 查询条件
     * @return 参赛档案列表
     */
    List<ParticipantAdminListResVO> selectParticipantList(ParticipantAdminQueryReqDTO query);

    /**
     * 查询参赛档案详情。
     *
     * @param participantId 参赛档案ID
     * @return 参赛档案详情
     */
    ParticipantAdminDetailResVO selectParticipantDetail(Long participantId);

    /**
     * 查询参赛档案认领映射列表。
     *
     * @param query 查询条件
     * @return 认领映射列表
     */
    List<ParticipantUserMapAdminResVO> selectParticipantUserMapList(ParticipantUserMapQueryReqDTO query);
}
