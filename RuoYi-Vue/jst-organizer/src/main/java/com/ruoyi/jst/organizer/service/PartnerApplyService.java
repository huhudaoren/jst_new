package com.ruoyi.jst.organizer.service;

import com.ruoyi.jst.organizer.dto.ApproveReqDTO;
import com.ruoyi.jst.organizer.dto.PartnerApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.PartnerApplyReqDTO;
import com.ruoyi.jst.organizer.dto.RejectReqDTO;
import com.ruoyi.jst.organizer.dto.SupplementReqDTO;
import com.ruoyi.jst.organizer.vo.PartnerApplyApproveResVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyDetailVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyListVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyStatusResVO;
import com.ruoyi.jst.organizer.vo.PartnerApplySubmitResVO;

import java.util.List;

/**
 * 赛事方入驻申请服务
 * <p>
 * 负责公开提交申请、公开状态查询，以及后台审核列表/详情/通过/驳回/补件。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PartnerApplyService {

    /**
     * 公开提交赛事方入驻申请。
     *
     * @param req 申请请求
     * @return 提交结果
     * @关联表 jst_event_partner_apply
     * @关联状态机 SM-4
     * @关联权限 @Anonymous
     */
    PartnerApplySubmitResVO submitPublicApply(PartnerApplyReqDTO req);

    /**
     * 按申请单号查询公开申请状态。
     *
     * @param applyNo 申请单号
     * @return 状态结果
     * @关联表 jst_event_partner_apply
     * @关联状态机 SM-4
     * @关联权限 @Anonymous
     */
    PartnerApplyStatusResVO queryPublicStatus(String applyNo);

    /**
     * 查询后台审核列表。
     *
     * @param query 查询条件
     * @return 列表结果
     * @关联表 jst_event_partner_apply
     * @关联状态机 SM-4
     * @关联权限 jst:organizer:partnerApply:list
     */
    List<PartnerApplyListVO> selectAdminList(PartnerApplyQueryDTO query);

    /**
     * 查询后台审核详情。
     *
     * @param applyId 申请ID
     * @return 详情结果
     * @关联表 jst_event_partner_apply
     * @关联状态机 SM-4
     * @关联权限 jst:organizer:partnerApply:detail
     */
    PartnerApplyDetailVO getAdminDetail(Long applyId);

    /**
     * 审核通过并创建赛事方账号。
     *
     * @param applyId 申请ID
     * @param req     审核通过请求
     * @return 审核通过结果
     * @关联表 jst_event_partner_apply / jst_event_partner / sys_user / sys_user_role / sys_role
     * @关联状态机 SM-4
     * @关联权限 jst:organizer:partnerApply:approve
     */
    PartnerApplyApproveResVO approve(Long applyId, ApproveReqDTO req);

    /**
     * 驳回赛事方入驻申请。
     *
     * @param applyId 申请ID
     * @param req     驳回请求
     * @return 无
     * @关联表 jst_event_partner_apply
     * @关联状态机 SM-4
     * @关联权限 jst:organizer:partnerApply:reject
     */
    void reject(Long applyId, RejectReqDTO req);

    /**
     * 要求赛事方补充材料。
     *
     * @param applyId 申请ID
     * @param req     补件请求
     * @return 无
     * @关联表 jst_event_partner_apply
     * @关联状态机 SM-4
     * @关联权限 jst:organizer:partnerApply:supplement
     */
    void supplement(Long applyId, SupplementReqDTO req);
}
