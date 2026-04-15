package com.ruoyi.jst.user.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.user.dto.ParticipantAdminQueryReqDTO;
import com.ruoyi.jst.user.dto.ParticipantUserMapQueryReqDTO;
import com.ruoyi.jst.user.mapper.ParticipantMapper;
import com.ruoyi.jst.user.mapper.ParticipantUserMapMapper;
import com.ruoyi.jst.user.service.ParticipantAdminService;
import com.ruoyi.jst.user.vo.ParticipantAdminDetailResVO;
import com.ruoyi.jst.user.vo.ParticipantAdminListResVO;
import com.ruoyi.jst.user.vo.ParticipantUserMapAdminResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理端参赛档案查询服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ParticipantAdminServiceImpl implements ParticipantAdminService {

    @Autowired
    private ParticipantMapper participantMapper;

    @Autowired
    private ParticipantUserMapMapper participantUserMapMapper;

    /**
     * 查询参赛档案分页列表（姓名/手机号模糊）。
     *
     * @param query 查询条件
     * @return 参赛档案列表
     * @关联表 jst_participant / jst_user / jst_channel
     * @关联状态机 SM-14
     * @关联权限 jst:participant:list
     */
    @Override
    public List<ParticipantAdminListResVO> selectParticipantList(ParticipantAdminQueryReqDTO query) {
        normalizeQuery(query);
        List<ParticipantAdminListResVO> rows = participantMapper.selectAdminParticipantList(query);
        if (rows == null) {
            return new ArrayList<>();
        }
        rows.forEach(this::maskParticipantRow);
        return rows;
    }

    /**
     * 查询参赛档案详情。
     *
     * @param participantId 参赛档案ID
     * @return 参赛档案详情
     * @关联表 jst_participant / jst_user / jst_channel
     * @关联状态机 SM-14
     * @关联权限 jst:participant:list
     */
    @Override
    public ParticipantAdminDetailResVO selectParticipantDetail(Long participantId) {
        ParticipantAdminDetailResVO detail = participantMapper.selectAdminParticipantDetail(participantId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.code());
        }
        detail.setGuardianMobile(MaskUtil.mobile(detail.getGuardianMobile()));
        detail.setUserName(maskUserName(detail.getUserName()));
        return detail;
    }

    /**
     * 查询参赛档案认领映射分页列表。
     *
     * @param query 查询条件
     * @return 认领映射列表
     * @关联表 jst_participant_user_map / jst_user / jst_participant
     * @关联状态机 SM-14
     * @关联权限 jst:participant:list
     */
    @Override
    public List<ParticipantUserMapAdminResVO> selectParticipantUserMapList(ParticipantUserMapQueryReqDTO query) {
        List<ParticipantUserMapAdminResVO> rows = participantUserMapMapper.selectAdminList(query);
        if (rows == null) {
            return new ArrayList<>();
        }
        rows.forEach(row -> row.setUserName(maskUserName(row.getUserName())));
        return rows;
    }

    private void normalizeQuery(ParticipantAdminQueryReqDTO query) {
        if (query == null) {
            return;
        }
        if (StringUtils.isBlank(query.getGuardianMobile()) && StringUtils.isNotBlank(query.getMobile())) {
            query.setGuardianMobile(query.getMobile());
        }
    }

    private void maskParticipantRow(ParticipantAdminListResVO row) {
        row.setGuardianMobile(MaskUtil.mobile(row.getGuardianMobile()));
        row.setUserName(maskUserName(row.getUserName()));
    }

    private String maskUserName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return userName;
        }
        if (userName.matches("^1\\d{10}$")) {
            return MaskUtil.mobile(userName);
        }
        return MaskUtil.realName(userName);
    }
}
