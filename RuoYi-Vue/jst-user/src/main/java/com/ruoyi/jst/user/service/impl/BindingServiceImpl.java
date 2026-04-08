package com.ruoyi.jst.user.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.security.SecurityCheck;
import com.ruoyi.jst.user.domain.JstChannel;
import com.ruoyi.jst.user.domain.JstStudentChannelBinding;
import com.ruoyi.jst.user.domain.JstUser;
import com.ruoyi.jst.user.dto.BindingQueryReqDTO;
import com.ruoyi.jst.user.enums.BindingStatus;
import com.ruoyi.jst.user.mapper.BindingMapperExt;
import com.ruoyi.jst.user.mapper.JstUserMapper;
import com.ruoyi.jst.user.service.BindingService;
import com.ruoyi.jst.user.vo.BindingVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 学生-渠道绑定领域服务实现。
 * <p>
 * 强约束：
 * <ul>
 *   <li>写操作必须加事务与分布式锁</li>
 *   <li>状态变更必须经过 SM-15 校验</li>
 *   <li>状态更新 SQL 必须携带 expectedStatus 乐观锁条件</li>
 *   <li>用户当前绑定渠道必须与 jst_user.bound_channel_id 同步维护</li>
 * </ul>
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class BindingServiceImpl implements BindingService {

    private static final Logger log = LoggerFactory.getLogger(BindingServiceImpl.class);

    @Autowired
    private BindingMapperExt bindingMapperExt;

    @Autowired
    private JstUserMapper jstUserMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    /**
     * 查询当前用户绑定历史。
     *
     * @param userId 当前学生用户ID
     * @return 绑定历史列表
     * @关联表 jst_student_channel_binding / jst_channel / jst_user
     * @关联状态机 SM-15
     * @关联权限 hasRole('jst_student')
     */
    @Override
    public List<BindingVO> selectMyBindings(Long userId) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);
        return bindingMapperExt.selectMyBindingList(userId);
    }

    /**
     * 切换或新建当前用户绑定。
     *
     * @param userId 当前学生用户ID
     * @param channelId 目标渠道方ID
     * @return 新生成的 active 绑定
     * @关联表 jst_student_channel_binding / jst_channel / jst_user
     * @关联状态机 SM-15
     * @关联权限 hasRole('jst_student')
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户", action = "BIND_SWITCH", target = "#{userId}")
    public BindingVO switchBinding(Long userId, Long channelId) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);

        // TX: BindingService.switchBinding
        return jstLockTemplate.execute(lockKey(userId), 3, 5, () -> {
            // LOCK: lock:bind:{userId}
            JstChannel targetChannel = requireApprovedChannel(channelId);
            JstStudentChannelBinding activeBinding = bindingMapperExt.selectActiveBindingByUserId(userId);

            if (activeBinding != null) {
                if (Objects.equals(activeBinding.getChannelId(), channelId)) {
                    throw new ServiceException("已绑定当前渠道，无需重复切换",
                            BizErrorCode.JST_USER_BIND_CONFLICT.code());
                }

                // SM-15
                BindingStatus.fromDb(activeBinding.getStatus()).assertCanTransitTo(BindingStatus.REPLACED);
                int replaced = bindingMapperExt.updateBindingStatus(
                        activeBinding.getBindingId(),
                        BindingStatus.ACTIVE.dbValue(),
                        BindingStatus.REPLACED.dbValue(),
                        userId,
                        null,
                        currentOperatorName()
                );
                if (replaced == 0) {
                    throw new ServiceException("绑定切换失败，原绑定状态已变更",
                            BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
                }
            }

            Date now = new Date();
            JstStudentChannelBinding newBinding = new JstStudentChannelBinding();
            newBinding.setUserId(userId);
            newBinding.setChannelId(targetChannel.getChannelId());
            newBinding.setBindTime(now);
            newBinding.setStatus(BindingStatus.ACTIVE.dbValue());
            newBinding.setOperatorId(userId);
            newBinding.setCreateBy(currentOperatorName());
            newBinding.setCreateTime(now);
            newBinding.setUpdateBy(currentOperatorName());
            newBinding.setUpdateTime(now);
            newBinding.setDelFlag("0");
            bindingMapperExt.insertBinding(newBinding);

            int userUpdated = bindingMapperExt.updateUserBoundChannelId(userId, targetChannel.getChannelId(), currentOperatorName());
            if (userUpdated == 0) {
                throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_NOT_FOUND.code());
            }

            BindingVO bindingVO = bindingMapperExt.selectBindingVoById(newBinding.getBindingId());
            log.info("[BindingSwitch] 切换成功 userId={} channelId={} bindingId={}",
                    userId, channelId, newBinding.getBindingId());
            return bindingVO;
        });
    }

    /**
     * 主动解绑当前 active 绑定。
     *
     * @param userId 当前学生用户ID
     * @param reason 解绑原因
     * @关联表 jst_student_channel_binding / jst_user
     * @关联状态机 SM-15
     * @关联权限 hasRole('jst_student')
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户", action = "BIND_UNBIND", target = "#{userId}")
    public void unbindCurrent(Long userId, String reason) {
        SecurityCheck.assertSameUser(userId);
        assertUserExists(userId);

        // TX: BindingService.unbindCurrent
        jstLockTemplate.execute(lockKey(userId), 3, 5, () -> {
            // LOCK: lock:bind:{userId}
            JstStudentChannelBinding activeBinding = bindingMapperExt.selectActiveBindingByUserId(userId);
            if (activeBinding == null) {
                throw new ServiceException("当前无有效绑定可解绑",
                        BizErrorCode.JST_USER_BIND_NOT_FOUND.code());
            }

            // SM-15
            BindingStatus.fromDb(activeBinding.getStatus()).assertCanTransitTo(BindingStatus.MANUAL_UNBOUND);
            int updated = bindingMapperExt.updateBindingStatus(
                    activeBinding.getBindingId(),
                    BindingStatus.ACTIVE.dbValue(),
                    BindingStatus.MANUAL_UNBOUND.dbValue(),
                    userId,
                    reason,
                    currentOperatorName()
            );
            if (updated == 0) {
                throw new ServiceException("解绑失败，绑定状态已变更",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            int userUpdated = bindingMapperExt.updateUserBoundChannelId(userId, null, currentOperatorName());
            if (userUpdated == 0) {
                throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_NOT_FOUND.code());
            }

            log.info("[BindingUnbind] 解绑成功 userId={} bindingId={}", userId, activeBinding.getBindingId());
        });
    }

    /**
     * 管理后台查询绑定列表。
     *
     * @param query 查询条件
     * @return 绑定列表
     * @关联表 jst_student_channel_binding / jst_channel
     * @关联状态机 SM-15
     * @关联权限 jst:user:binding:list
     */
    @Override
    public List<BindingVO> selectBindingList(BindingQueryReqDTO query) {
        return bindingMapperExt.selectBindingList(query);
    }

    /**
     * 平台强制解绑指定 binding。
     *
     * @param bindingId 绑定ID
     * @param reason 强制解绑原因
     * @关联表 jst_student_channel_binding / jst_user
     * @关联状态机 SM-15
     * @关联权限 jst:user:binding:forceUnbind
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "用户", action = "BIND_FORCE_UNBIND", target = "#{bindingId}")
    public void forceUnbind(Long bindingId, String reason) {
        JstStudentChannelBinding snapshot = requireBinding(bindingId);

        // TX: BindingService.forceUnbind
        jstLockTemplate.execute(lockKey(snapshot.getUserId()), 3, 5, () -> {
            // LOCK: lock:bind:{userId}
            JstStudentChannelBinding binding = requireBinding(bindingId);

            // SM-15
            BindingStatus.fromDb(binding.getStatus()).assertCanTransitTo(BindingStatus.MANUAL_UNBOUND);
            int updated = bindingMapperExt.updateBindingStatus(
                    bindingId,
                    binding.getStatus(),
                    BindingStatus.MANUAL_UNBOUND.dbValue(),
                    SecurityUtils.getUserId(),
                    reason,
                    currentOperatorName()
            );
            if (updated == 0) {
                throw new ServiceException("强制解绑失败，绑定状态已变更",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            int userUpdated = bindingMapperExt.updateUserBoundChannelId(binding.getUserId(), null, currentOperatorName());
            if (userUpdated == 0) {
                throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_NOT_FOUND.code());
            }

            log.info("[BindingForceUnbind] 强制解绑成功 bindingId={} userId={} operatorId={}",
                    bindingId, binding.getUserId(), SecurityUtils.getUserId());
        });
    }

    private String lockKey(Long userId) {
        return "lock:bind:" + userId;
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return username == null ? "system" : username;
    }

    private JstUser assertUserExists(Long userId) {
        JstUser user = jstUserMapper.selectJstUserByUserId(userId);
        if (user == null) {
            throw new ServiceException(BizErrorCode.JST_USER_NOT_FOUND.message(),
                    BizErrorCode.JST_USER_NOT_FOUND.code());
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new ServiceException(BizErrorCode.JST_USER_DISABLED.message(),
                    BizErrorCode.JST_USER_DISABLED.code());
        }
        return user;
    }

    private JstChannel requireApprovedChannel(Long channelId) {
        JstChannel channel = bindingMapperExt.selectApprovedChannelById(channelId);
        if (channel == null) {
            throw new ServiceException("渠道不存在或未通过审核",
                    BizErrorCode.JST_CHANNEL_NOT_AUTHED.code());
        }
        return channel;
    }

    private JstStudentChannelBinding requireBinding(Long bindingId) {
        JstStudentChannelBinding binding = bindingMapperExt.selectBindingById(bindingId);
        if (binding == null) {
            throw new ServiceException("绑定记录不存在",
                    BizErrorCode.JST_USER_BIND_NOT_FOUND.code());
        }
        return binding;
    }
}
