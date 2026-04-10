package com.ruoyi.jst.channel.service;

import java.util.List;
import java.util.Map;

/**
 * 渠道方补充接口服务（E0-1 新增）
 * <p>
 * 关联表：jst_user_rights / jst_student_channel_binding
 *
 * @author jst
 * @since 1.0.0
 */
public interface ChannelSupplementService {

    /**
     * 查询渠道方权益列表
     *
     * @param userId 当前渠道方用户ID
     * @return 权益列表（rightsName/remainQuota/expireTime）
     */
    List<Map<String, Object>> selectMyRights(Long userId);

    /**
     * 查指定绑定学生的成绩列表
     *
     * @param channelId 渠道方ID
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<Map<String, Object>> selectStudentScores(Long channelId, Long studentId);

    /**
     * 查指定绑定学生的证书列表
     *
     * @param channelId 渠道方ID
     * @param studentId 学生ID
     * @return 证书列表
     */
    List<Map<String, Object>> selectStudentCerts(Long channelId, Long studentId);

    /**
     * 渠道方自助解绑学生（Q-01）
     *
     * @param channelId 渠道方ID
     * @param bindingId 绑定记录ID
     */
    void unbindStudent(Long channelId, Long bindingId);
}
