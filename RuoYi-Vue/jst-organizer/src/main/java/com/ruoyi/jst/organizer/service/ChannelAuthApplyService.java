package com.ruoyi.jst.organizer.service;

import com.ruoyi.jst.organizer.dto.ApproveChannelReqDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyReqDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthResubmitReqDTO;
import com.ruoyi.jst.organizer.dto.RejectReqDTO;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplySubmitResVO;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplyVO;
import com.ruoyi.jst.organizer.vo.ChannelAuthApproveResVO;

import java.util.List;

/**
 * 渠道认证申请领域服务
 * <p>
 * 关联表：jst_channel_auth_apply、jst_channel、sys_user_role
 * 关联状态机：SM-3
 *
 * @author jst
 * @since 1.0.0
 */
public interface ChannelAuthApplyService {

    /**
     * 当前用户提交渠道认证申请
     *
     * @param userId 当前用户ID
     * @param req    申请入参
     * @return 提交结果
     */
    ChannelAuthApplySubmitResVO submitApply(Long userId, ChannelAuthApplyReqDTO req);

    /**
     * 查询当前用户申请历史
     *
     * @param userId 当前用户ID
     * @return 申请历史列表
     */
    List<ChannelAuthApplyVO> listMyApplies(Long userId);

    /**
     * 管理端分页查询申请列表
     *
     * @param query 查询条件
     * @return 申请列表
     */
    List<ChannelAuthApplyVO> listAdminApplies(ChannelAuthApplyQueryDTO query);

    /**
     * 查询申请详情
     *
     * @param applyId 申请ID
     * @return 申请详情
     */
    ChannelAuthApplyVO getApplyDetail(Long applyId);

    /**
     * 审核通过
     *
     * @param applyId 申请ID
     * @param req     审核入参
     * @return 审核结果
     */
    ChannelAuthApproveResVO approve(Long applyId, ApproveChannelReqDTO req);

    /**
     * 审核驳回
     *
     * @param applyId 申请ID
     * @param req     驳回入参
     */
    void reject(Long applyId, RejectReqDTO req);

    /**
     * 暂停已通过的渠道方
     *
     * @param applyId 申请ID
     */
    void suspend(Long applyId);

    /**
     * 查询当前用户最新一条申请（含 rejectCount/lockedForManual）
     *
     * @param userId 当前用户ID
     * @return 最新申请，无则 null
     */
    ChannelAuthApplyVO getMyLatest(Long userId);

    /**
     * 驳回后重提申请（Q-02: rejectCount < 3 且未锁定）
     *
     * @param userId  当前用户ID
     * @param applyId 原申请ID
     * @param req     重提入参
     * @return 新提交结果
     */
    ChannelAuthApplySubmitResVO resubmit(Long userId, Long applyId, ChannelAuthResubmitReqDTO req);

    /**
     * 撤回 pending 状态申请
     *
     * @param userId  当前用户ID
     * @param applyId 申请ID
     */
    void cancelApply(Long userId, Long applyId);

    /**
     * PATCH-5: admin 修正某申请/渠道的 region（省级维度）
     * <p>
     * 同步更新 {@code jst_channel_auth_apply.region}；若该申请已关联 channel，
     * 同步更新 {@code jst_channel.region}，确保热力图口径一致。
     *
     * @param applyId 申请ID
     * @param region  新 region（必须在 jst_region_province 字典内，由 Controller 校验）
     */
    void updateRegion(Long applyId, String region);
}
