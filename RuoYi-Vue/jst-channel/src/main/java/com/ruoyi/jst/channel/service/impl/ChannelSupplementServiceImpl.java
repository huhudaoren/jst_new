package com.ruoyi.jst.channel.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.jst.channel.mapper.lookup.ChannelSupplementLookupMapper;
import com.ruoyi.jst.channel.service.ChannelSupplementService;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 渠道方补充接口服务实现（E0-1 新增）
 * <p>
 * 关联表：jst_user_rights / jst_student_channel_binding / jst_enroll_record
 * 关联决策：Q-01 自助解绑
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ChannelSupplementServiceImpl implements ChannelSupplementService {

    private static final Logger log = LoggerFactory.getLogger(ChannelSupplementServiceImpl.class);

    @Autowired
    private ChannelSupplementLookupMapper supplementLookupMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    /**
     * 查询渠道方权益列表
     *
     * @param userId 当前渠道方用户ID
     * @return 权益列表（rightsName/remainQuota/expireTime）
     * @关联表 jst_user_rights / jst_rights_template
     */
    @Override
    public List<Map<String, Object>> selectMyRights(Long userId) {
        List<Map<String, Object>> rights = supplementLookupMapper.selectRightsByUserId(userId);
        return rights == null ? new ArrayList<>() : rights;
    }

    /**
     * 查指定绑定学生的成绩列表
     *
     * @param channelId 渠道方ID
     * @param studentId 学生用户ID
     * @return 成绩列表
     * @关联表 jst_student_channel_binding / jst_enroll_record / jst_contest
     */
    @Override
    public List<Map<String, Object>> selectStudentScores(Long channelId, Long studentId) {
        assertStudentBound(channelId, studentId);
        List<Map<String, Object>> scores = supplementLookupMapper.selectStudentScores(studentId);
        return scores == null ? new ArrayList<>() : scores;
    }

    /**
     * 查指定绑定学生的证书列表
     *
     * @param channelId 渠道方ID
     * @param studentId 学生用户ID
     * @return 证书列表
     * @关联表 jst_student_channel_binding / jst_enroll_record / jst_contest
     */
    @Override
    public List<Map<String, Object>> selectStudentCerts(Long channelId, Long studentId) {
        assertStudentBound(channelId, studentId);
        List<Map<String, Object>> certs = supplementLookupMapper.selectStudentCerts(studentId);
        return certs == null ? new ArrayList<>() : certs;
    }

    /**
     * 渠道方自助解绑学生（Q-01）
     * <p>
     * 解绑后学生可被其他渠道方绑定，不回溯历史返点。
     *
     * @param channelId 渠道方ID
     * @param bindingId 绑定记录ID
     * @关联表 jst_student_channel_binding
     * @关联决策 Q-01
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // TX: unbindStudent
    @OperateLog(module = "渠道", action = "CHANNEL_UNBIND", target = "#{bindingId}")
    public void unbindStudent(Long channelId, Long bindingId) {
        // LOCK: lock:channel:unbind:{bindingId}
        jstLockTemplate.execute("lock:channel:unbind:" + bindingId, 3, 5, () -> {
            Map<String, Object> binding = supplementLookupMapper.selectBindingById(bindingId);
            if (binding == null || binding.isEmpty()) {
                throw new ServiceException(BizErrorCode.JST_USER_BIND_NOT_FOUND.message(),
                        BizErrorCode.JST_USER_BIND_NOT_FOUND.code());
            }

            // 归属校验：只能解绑自己渠道下的学生
            Long bindChannelId = toLong(binding.get("channelId"));
            if (!Objects.equals(channelId, bindChannelId)) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }

            String status = String.valueOf(binding.get("bindingStatus"));
            if (!"active".equals(status)) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_UNBIND_FAILED.message(),
                        BizErrorCode.JST_CHANNEL_UNBIND_FAILED.code());
            }

            Date now = DateUtils.getNowDate();
            int updated = supplementLookupMapper.unbindByBindingId(bindingId, "active",
                    String.valueOf(channelId), now);
            if (updated == 0) {
                throw new ServiceException(BizErrorCode.JST_CHANNEL_UNBIND_FAILED.message(),
                        BizErrorCode.JST_CHANNEL_UNBIND_FAILED.code());
            }

            log.info("[ChannelUnbind] 渠道方自助解绑 channelId={} bindingId={} studentUserId={}",
                    channelId, bindingId, binding.get("userId"));
            return null;
        });
    }

    /**
     * 校验学生是否绑定到指定渠道
     */
    private void assertStudentBound(Long channelId, Long studentUserId) {
        if (supplementLookupMapper.countBindingForChannel(channelId, studentUserId) <= 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private Long toLong(Object val) {
        if (val == null) return null;
        if (val instanceof Number) return ((Number) val).longValue();
        try { return Long.valueOf(String.valueOf(val)); }
        catch (NumberFormatException e) { return null; }
    }
}
