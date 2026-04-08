package com.ruoyi.jst.user.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.user.domain.Participant;
import com.ruoyi.jst.user.domain.ParticipantUserMap;
import com.ruoyi.jst.user.enums.ClaimStatus;
import com.ruoyi.jst.user.mapper.ParticipantMapper;
import com.ruoyi.jst.user.mapper.ParticipantUserMapMapper;
import com.ruoyi.jst.user.service.ParticipantClaimService;
import com.ruoyi.jst.user.vo.ParticipantClaimResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 临时档案-认领领域服务实现
 * <p>
 * 强约束：
 * <ul>
 *   <li>事务：所有写方法 @Transactional(rollbackFor=Exception.class)</li>
 *   <li>并发：所有写方法持锁 lock:claim:participant:{id}</li>
 *   <li>状态机：必须经 ClaimStatus.assertCanTransitTo 校验</li>
 *   <li>审计：所有写方法标 @OperateLog</li>
 *   <li>越权：本服务不直接暴露 user-id 入参的越权风险，由 Controller 层 SecurityCheck 兜底</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ParticipantClaimServiceImpl implements ParticipantClaimService {

    private static final Logger log = LoggerFactory.getLogger(ParticipantClaimServiceImpl.class);

    @Autowired private ParticipantMapper participantMapper;
    @Autowired private ParticipantUserMapMapper mapMapper;
    @Autowired private JstLockTemplate jstLockTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户", action = "CLAIM_AUTO", target = "#{userId}")
    public ParticipantClaimResVO claimByAuto(Long userId, String mobile, String name) {
        if (userId == null || mobile == null || name == null) {
            return null;
        }

        // 1. 精确匹配 mobile + name
        List<Participant> exact = participantMapper.selectByGuardianMobileAndName(mobile, name);

        if (exact.isEmpty()) {
            // 1.1 无精确命中：检查仅 mobile 命中(多候选)
            List<Participant> mobileOnly = participantMapper.selectByGuardianMobile(mobile);
            if (mobileOnly.isEmpty()) {
                log.info("[ClaimAuto] 无候选档案 userId={} mobile=***", userId);
                return null;
            }
            // 1.2 仅手机号命中 → 全部置 pending_manual
            for (Participant p : mobileOnly) {
                markPendingManual(p.getParticipantId());
            }
            log.info("[ClaimAuto] 仅手机号命中,转人工 userId={} count={}", userId, mobileOnly.size());
            return null;
        }

        if (exact.size() > 1) {
            // 1.3 多个精确命中(同名同手机) → pending_manual
            for (Participant p : exact) {
                markPendingManual(p.getParticipantId());
            }
            log.warn("[ClaimAuto] 多候选,转人工 userId={} count={}", userId, exact.size());
            return null;
        }

        // 1.4 唯一精确命中 → 自动认领
        Participant target = exact.get(0);
        return doClaimWithLock(target.getParticipantId(), userId, "auto_phone_name",
                ClaimStatus.AUTO_CLAIMED, "unclaimed", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户", action = "CLAIM_ADMIN", target = "#{participantId}")
    public ParticipantClaimResVO claimByAdmin(Long participantId, Long userId, String reason) {
        if (participantId == null || userId == null) {
            throw new ServiceException("participantId 与 userId 不能为空",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        Long operatorId = SecurityUtils.getUserId();
        return doClaimWithLock(participantId, userId, "manual_admin",
                ClaimStatus.MANUAL_CLAIMED, null, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户", action = "CLAIM_REVOKE", target = "#{participantId}")
    public void revokeClaim(Long participantId, String reason) {
        jstLockTemplate.execute("lock:claim:participant:" + participantId, 3, 5, () -> {
            Participant p = participantMapper.selectByPk(participantId);
            if (p == null) {
                throw new ServiceException(BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.code());
            }
            // 状态机校验:任何已认领状态都可回到 unclaimed
            ClaimStatus current = ClaimStatus.fromDb(p.getClaimStatus());
            current.assertCanTransitTo(ClaimStatus.UNCLAIMED);

            // 撤销 map
            ParticipantUserMap activeMap = mapMapper.selectActiveByParticipantId(participantId);
            if (activeMap != null) {
                mapMapper.revoke(activeMap.getMapId(), reason);
            }
            // 回滚 participant 状态(乐观锁)
            int updated = participantMapper.updateClaimStatus(participantId,
                    p.getClaimStatus(), ClaimStatus.UNCLAIMED.dbValue(), null);
            if (updated == 0) {
                throw new ServiceException("撤销失败:档案状态已变更,请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            log.info("[ClaimRevoke] 撤销成功 participantId={} reason={}", participantId, reason);
            return null;
        });
    }

    // ========== private helpers ==========

    /**
     * 加锁完成认领的核心逻辑
     */
    private ParticipantClaimResVO doClaimWithLock(Long participantId, Long userId,
                                                   String claimMethod, ClaimStatus targetStatus,
                                                   String forcedExpectedStatus, Long operatorId) {
        return jstLockTemplate.execute("lock:claim:participant:" + participantId, 3, 5, () -> {
            Participant p = participantMapper.selectByPk(participantId);
            if (p == null) {
                throw new ServiceException(BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_PARTICIPANT_NOT_FOUND.code());
            }
            String expectedStatus = forcedExpectedStatus != null ? forcedExpectedStatus : p.getClaimStatus();
            ClaimStatus current = ClaimStatus.fromDb(expectedStatus);

            // 1. 状态机校验
            current.assertCanTransitTo(targetStatus);

            // 2. 写 map (防孤儿:不覆盖原始 participant.user_id,通过 map 关联)
            ParticipantUserMap map = new ParticipantUserMap();
            map.setParticipantId(participantId);
            map.setUserId(userId);
            map.setClaimMethod(claimMethod);
            map.setClaimTime(new Date());
            map.setOperatorId(operatorId);
            map.setStatus("active");
            map.setCreateBy(operatorId == null ? "system" : operatorId.toString());
            mapMapper.insert(map);

            // 3. 乐观锁更新 participant 状态
            int updated = participantMapper.updateClaimStatus(participantId,
                    expectedStatus, targetStatus.dbValue(), userId);
            if (updated == 0) {
                throw new ServiceException("认领冲突:档案状态已变更",
                        BizErrorCode.JST_USER_CLAIM_ALREADY_CLAIMED.code());
            }

            // 4. 组装返回
            ParticipantClaimResVO vo = new ParticipantClaimResVO();
            vo.setParticipantId(participantId);
            vo.setUserId(userId);
            vo.setClaimMethod(claimMethod);
            vo.setClaimTime(map.getClaimTime());
            vo.setResultMessage("认领成功");
            log.info("[Claim] 成功 participantId={} userId={} method={}", participantId, userId, claimMethod);
            return vo;
        });
    }

    /**
     * 标记为待人工(无锁,因为是状态向下滑动,不会冲突)
     */
    private void markPendingManual(Long participantId) {
        Participant p = participantMapper.selectByPk(participantId);
        if (p == null || !"unclaimed".equals(p.getClaimStatus())) return;
        participantMapper.updateClaimStatus(participantId, "unclaimed",
                ClaimStatus.PENDING_MANUAL.dbValue(), null);
    }
}
