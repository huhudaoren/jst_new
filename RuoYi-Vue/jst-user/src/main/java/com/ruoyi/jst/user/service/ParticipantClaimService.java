package com.ruoyi.jst.user.service;

import com.ruoyi.jst.user.dto.SelfCreateParticipantReqDTO;
import com.ruoyi.jst.user.vo.ParticipantClaimResVO;

/**
 * 临时档案-认领领域服务
 * <p>
 * 关联表：jst_participant / jst_participant_user_map
 * 关联状态机：SM-14 ({@link com.ruoyi.jst.user.enums.ClaimStatus})
 * 关联文档：架构设计/12-API边界与服务划分.md §3.5
 * <p>
 * 防孤儿数据规则：底层订单/成绩/证书等流水继续保留 participant_id 快照，
 * 通过 jst_participant_user_map 归集到 user_id，<b>不</b>覆盖原始流水主键
 *
 * @author jst
 * @since 1.0.0
 */
public interface ParticipantClaimService {

    /**
     * 自动认领：用户注册/完善资料后触发
     * <p>
     * 匹配规则：(guardian_mobile + name) 唯一命中 1 条 unclaimed 档案
     * <ul>
     *   <li>无命中：跳过(返回 null)</li>
     *   <li>唯一命中：跃迁 unclaimed → auto_claimed，写 map 表</li>
     *   <li>多候选 / 仅手机号命中：跃迁 unclaimed → pending_manual，等待管理员处理</li>
     * </ul>
     *
     * @param userId         当前注册用户ID
     * @param mobile         监护人手机号(用户注册手机号)
     * @param name           参赛人姓名
     * @return 认领结果(无命中则 null)
     */
    ParticipantClaimResVO claimByAuto(Long userId, String mobile, String name);

    /**
     * 管理员手动认领
     *
     * @param participantId 临时档案ID
     * @param userId        目标正式用户ID
     * @param reason        认领原因(写入 audit_log)
     */
    ParticipantClaimResVO claimByAdmin(Long participantId, Long userId, String reason);

    /**
     * 撤销认领(回到 unclaimed)
     *
     * @param participantId 档案ID
     * @param reason        撤销原因
     */
    void revokeClaim(Long participantId, String reason);

    /**
     * 学生自建参赛档案(创建即认领)
     * <p>
     * 用于"我的档案"页用户自行新增档案。落库时：
     * <ul>
     *   <li>jst_participant: claim_status=manual_claimed, participant_type=registered_participant,
     *       visible_scope=platform, claimed_user_id=userId</li>
     *   <li>jst_participant_user_map: claim_method=self_create, status=active</li>
     * </ul>
     * 防刷：同一 userId 下已存在同名有效档案时拒绝创建。
     *
     * @param userId 当前登录用户ID
     * @param req    档案入参
     * @return 认领结果(含新建的 participantId)
     */
    ParticipantClaimResVO selfCreate(Long userId, SelfCreateParticipantReqDTO req);
}
