package com.ruoyi.jst.organizer.service;

import com.ruoyi.jst.organizer.dto.ApproveChannelReqDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyReqDTO;
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
}
