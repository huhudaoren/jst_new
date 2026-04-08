package com.ruoyi.jst.organizer.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.organizer.domain.JstChannelAuthApply;
import com.ruoyi.jst.organizer.dto.ApproveChannelReqDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.ChannelAuthApplyReqDTO;
import com.ruoyi.jst.organizer.dto.RejectReqDTO;
import com.ruoyi.jst.organizer.enums.ChannelAuthStatus;
import com.ruoyi.jst.organizer.mapper.ChannelAuthApplyMapperExt;
import com.ruoyi.jst.organizer.mapper.SysUserExtMapper;
import com.ruoyi.jst.organizer.service.ChannelAuthApplyService;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplySubmitResVO;
import com.ruoyi.jst.organizer.vo.ChannelAuthApplyVO;
import com.ruoyi.jst.organizer.vo.ChannelAuthApproveResVO;
import com.ruoyi.jst.user.domain.JstChannel;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.service.IJstChannelService;
import com.ruoyi.jst.user.service.IJstUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 渠道认证申请领域服务实现
 * <p>
 * 负责小程序提交申请、后台审核通过/驳回/暂停等 F6 核心流程。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ChannelAuthApplyServiceImpl implements ChannelAuthApplyService {

    private static final Logger log = LoggerFactory.getLogger(ChannelAuthApplyServiceImpl.class);

    private static final String ROLE_KEY_CHANNEL = "jst_channel";

    @Autowired
    private ChannelAuthApplyMapperExt channelAuthApplyMapperExt;

    @Autowired
    private SysUserExtMapper sysUserExtMapper;

    @Autowired
    private IJstUserService jstUserService;

    @Autowired
    private IJstChannelService jstChannelService;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 当前用户提交渠道认证申请
     *
     * @param userId 当前用户ID
     * @param req    申请入参
     * @return 申请结果
     * @关联表 jst_channel_auth_apply / jst_channel / jst_user
     * @关联状态机 SM-3
     * @关联权限 hasRole('jst_student')
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: submitApply
    @OperateLog(module = "渠道认证", action = "APPLY_SUBMIT", target = "#{userId}")
    public ChannelAuthApplySubmitResVO submitApply(Long userId, ChannelAuthApplyReqDTO req) {
        loadRequiredUser(userId);
        if (channelAuthApplyMapperExt.countActiveApplyByUserId(userId) > 0) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_AUTH_APPLY_DUPLICATE.message(),
                    BizErrorCode.JST_CHANNEL_AUTH_APPLY_DUPLICATE.code());
        }
        ensureNotApprovedChannel(userId);

        Date now = DateUtils.getNowDate();
        JstChannelAuthApply apply = new JstChannelAuthApply();
        apply.setApplyNo(snowflakeIdWorker.nextBizNo("CA"));
        apply.setUserId(userId);
        apply.setChannelType(req.getChannelType());
        apply.setApplyName(req.getApplyName());
        apply.setMaterialsJson(req.getMaterialsJson());
        apply.setApplyStatus(ChannelAuthStatus.PENDING.dbValue()); // SM-3
        apply.setSubmitTime(now);
        apply.setCreateBy(String.valueOf(userId));
        apply.setCreateTime(now);
        channelAuthApplyMapperExt.insertApply(apply);

        ChannelAuthApplySubmitResVO vo = new ChannelAuthApplySubmitResVO();
        vo.setApplyId(apply.getApplyId());
        vo.setApplyNo(apply.getApplyNo());
        return vo;
    }

    /**
     * 查询当前用户申请历史
     *
     * @param userId 当前用户ID
     * @return 申请历史
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3
     * @关联权限 hasRole('jst_student')
     */
    @Override
    public List<ChannelAuthApplyVO> listMyApplies(Long userId) {
        loadRequiredUser(userId);
        return channelAuthApplyMapperExt.selectMyApplyList(userId);
    }

    /**
     * 后台查询申请列表
     *
     * @param query 查询条件
     * @return 申请列表
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3
     * @关联权限 jst:organizer:channelApply:list
     */
    @Override
    public List<ChannelAuthApplyVO> listAdminApplies(ChannelAuthApplyQueryDTO query) {
        return channelAuthApplyMapperExt.selectAdminApplyList(query);
    }

    /**
     * 查询申请详情
     *
     * @param applyId 申请ID
     * @return 申请详情
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3
     * @关联权限 jst:organizer:channelApply:detail
     */
    @Override
    public ChannelAuthApplyVO getApplyDetail(Long applyId) {
        ChannelAuthApplyVO detail = channelAuthApplyMapperExt.selectApplyDetail(applyId);
        if (detail == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_AUTH_APPLY_NOT_FOUND.message(),
                    BizErrorCode.JST_CHANNEL_AUTH_APPLY_NOT_FOUND.code());
        }
        return detail;
    }

    /**
     * 审核通过并创建渠道档案
     *
     * @param applyId 申请ID
     * @param req     审核入参
     * @return 审核结果
     * @关联表 jst_channel_auth_apply / jst_channel / sys_user_role / sys_role / jst_user
     * @关联状态机 SM-3
     * @关联权限 jst:organizer:channelApply:approve
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: approve
    @OperateLog(module = "渠道认证", action = "APPLY_APPROVE", target = "#{applyId}")
    public ChannelAuthApproveResVO approve(Long applyId, ApproveChannelReqDTO req) {
        // LOCK: lock:org:channelApply:{applyId}
        return jstLockTemplate.execute(lockKey(applyId), 3, 10, () -> {
            JstChannelAuthApply apply = getRequiredApply(applyId);
            ChannelAuthStatus.fromDb(apply.getApplyStatus()).assertCanTransitTo(ChannelAuthStatus.APPROVED); // SM-3
            ensureNotApprovedChannel(apply.getUserId());

            JstUser user = loadRequiredUser(apply.getUserId());
            if (StringUtils.isBlank(user.getMobile())) {
                throw new ServiceException("申请用户未绑定手机号，无法创建渠道档案",
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }

            Date now = DateUtils.getNowDate();
            Long operatorId = currentOperatorId();
            String operatorName = currentOperatorName();

            JstChannel channel = new JstChannel();
            channel.setUserId(apply.getUserId());
            channel.setChannelType(apply.getChannelType());
            channel.setChannelName(apply.getApplyName());
            channel.setContactMobile(user.getMobile());
            channel.setCertMaterialsJson(apply.getMaterialsJson());
            channel.setAuthStatus(ChannelAuthStatus.APPROVED.dbValue());
            channel.setAuthTime(now);
            channel.setAuthRemark(req.getAuditRemark());
            channel.setTags(req.getTags());
            channel.setStatus(1);
            channel.setRiskFlag(0);
            channel.setCreateBy(operatorName);
            channel.setCreateTime(now);
            channel.setRemark(req.getAuditRemark());
            int insertRows = jstChannelService.insertJstChannel(channel);
            if (insertRows <= 0 || channel.getChannelId() == null) {
                throw new ServiceException("创建渠道档案失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            Long roleId = sysUserExtMapper.selectRoleIdByRoleKey(ROLE_KEY_CHANNEL);
            if (roleId == null) {
                throw new ServiceException("角色 jst_channel 未初始化",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            if (sysUserExtMapper.countUserRole(apply.getUserId(), roleId) == 0) {
                int userRoleRows = sysUserExtMapper.insertUserRole(apply.getUserId(), roleId);
                if (userRoleRows <= 0) {
                    throw new ServiceException("追加渠道角色失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
                }
            }

            int updated = channelAuthApplyMapperExt.updateApplyStatus(
                    applyId,
                    apply.getApplyStatus(),
                    ChannelAuthStatus.APPROVED.dbValue(),
                    channel.getChannelId(),
                    req.getAuditRemark(),
                    operatorId,
                    now,
                    operatorName);
            if (updated == 0) {
                throw new ServiceException("审核通过失败，申请状态已变化，请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            log.info("[ChannelAuthApply] 审核通过 applyId={} userId={} channelId={} mobile={}",
                    applyId, apply.getUserId(), channel.getChannelId(), MaskUtil.mobile(user.getMobile()));
            log.info("[ChannelAuthApply] Mock 短信: userId={} 渠道认证审核已通过", apply.getUserId());

            ChannelAuthApproveResVO vo = new ChannelAuthApproveResVO();
            vo.setChannelId(channel.getChannelId());
            return vo;
        });
    }

    /**
     * 驳回申请
     *
     * @param applyId 申请ID
     * @param req     驳回入参
     * @关联表 jst_channel_auth_apply
     * @关联状态机 SM-3
     * @关联权限 jst:organizer:channelApply:reject
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: reject
    @OperateLog(module = "渠道认证", action = "APPLY_REJECT", target = "#{applyId}")
    public void reject(Long applyId, RejectReqDTO req) {
        // LOCK: lock:org:channelApply:{applyId}
        jstLockTemplate.execute(lockKey(applyId), 3, 10, () -> {
            JstChannelAuthApply apply = getRequiredApply(applyId);
            ChannelAuthStatus.fromDb(apply.getApplyStatus()).assertCanTransitTo(ChannelAuthStatus.REJECTED); // SM-3

            Date now = DateUtils.getNowDate();
            Long operatorId = currentOperatorId();
            int updated = channelAuthApplyMapperExt.updateApplyStatus(
                    applyId,
                    apply.getApplyStatus(),
                    ChannelAuthStatus.REJECTED.dbValue(),
                    apply.getChannelId(),
                    req.getAuditRemark(),
                    operatorId,
                    now,
                    currentOperatorName());
            if (updated == 0) {
                throw new ServiceException("驳回失败，申请状态已变化，请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            log.info("[ChannelAuthApply] 审核驳回 applyId={} userId={} remark={}",
                    applyId, apply.getUserId(), req.getAuditRemark());
            log.info("[ChannelAuthApply] Mock 短信: userId={} 渠道认证审核未通过", apply.getUserId());
            return null;
        });
    }

    /**
     * 暂停已通过的渠道方
     *
     * @param applyId 申请ID
     * @关联表 jst_channel_auth_apply / jst_channel
     * @关联状态机 SM-3
     * @关联权限 jst:organizer:channelApply:suspend
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: suspend
    @OperateLog(module = "渠道认证", action = "APPLY_SUSPEND", target = "#{applyId}")
    public void suspend(Long applyId) {
        // LOCK: lock:org:channelApply:{applyId}
        jstLockTemplate.execute(lockKey(applyId), 3, 10, () -> {
            JstChannelAuthApply apply = getRequiredApply(applyId);
            ChannelAuthStatus.fromDb(apply.getApplyStatus()).assertCanTransitTo(ChannelAuthStatus.SUSPENDED); // SM-3
            if (apply.getChannelId() == null) {
                throw new ServiceException("关联渠道档案不存在，无法暂停",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            JstChannel channel = jstChannelService.selectJstChannelByChannelId(apply.getChannelId());
            if (channel == null) {
                throw new ServiceException("关联渠道档案不存在，无法暂停",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            Date now = DateUtils.getNowDate();
            String operatorName = currentOperatorName();
            Long operatorId = currentOperatorId();

            channel.setAuthStatus(ChannelAuthStatus.SUSPENDED.dbValue());
            channel.setStatus(0);
            channel.setAuthRemark("管理员暂停渠道方");
            channel.setUpdateBy(operatorName);
            channel.setUpdateTime(now);
            int channelRows = jstChannelService.updateJstChannel(channel);
            if (channelRows <= 0) {
                throw new ServiceException("暂停渠道档案失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            int updated = channelAuthApplyMapperExt.updateApplyStatus(
                    applyId,
                    apply.getApplyStatus(),
                    ChannelAuthStatus.SUSPENDED.dbValue(),
                    apply.getChannelId(),
                    "管理员暂停渠道方",
                    operatorId,
                    now,
                    operatorName);
            if (updated == 0) {
                throw new ServiceException("暂停失败，申请状态已变化，请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            log.info("[ChannelAuthApply] 渠道方已暂停 applyId={} channelId={}", applyId, apply.getChannelId());
            return null;
        });
    }

    private String lockKey(Long applyId) {
        return "lock:org:channelApply:" + applyId;
    }

    private Long currentOperatorId() {
        return SecurityUtils.getUserId();
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private JstChannelAuthApply getRequiredApply(Long applyId) {
        JstChannelAuthApply apply = channelAuthApplyMapperExt.selectApplyById(applyId);
        if (apply == null) {
            throw new ServiceException(BizErrorCode.JST_CHANNEL_AUTH_APPLY_NOT_FOUND.message(),
                    BizErrorCode.JST_CHANNEL_AUTH_APPLY_NOT_FOUND.code());
        }
        return apply;
    }

    private JstUser loadRequiredUser(Long userId) {
        JstUser user = jstUserService.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        return user;
    }

    private void ensureNotApprovedChannel(Long userId) {
        JstChannel query = new JstChannel();
        query.setUserId(userId);
        query.setStatus(1);
        query.setAuthStatus(ChannelAuthStatus.APPROVED.dbValue());
        List<JstChannel> channels = jstChannelService.selectJstChannelList(query);
        if (channels != null && !channels.isEmpty()) {
            throw new ServiceException(BizErrorCode.JST_USER_ALREADY_CHANNEL.message(),
                    BizErrorCode.JST_USER_ALREADY_CHANNEL.code());
        }
    }
}
