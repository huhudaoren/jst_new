package com.ruoyi.jst.channel.service;

import com.ruoyi.jst.channel.vo.ChannelOrderDetailResVO;
import com.ruoyi.jst.channel.vo.ChannelTopContestResVO;
import com.ruoyi.jst.channel.vo.ChannelTopStudentResVO;

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
    /**
     * 查询渠道工作台热门赛事排行榜。
     *
     * @param channelId 渠道方ID
     * @param period 查询周期
     * @param limit 返回条数
     * @return 热门赛事列表
     */
    List<ChannelTopContestResVO> selectTopContests(Long channelId, String period, Integer limit);

    /**
     * 查询渠道工作台活跃学生排行榜。
     *
     * @param channelId 渠道方ID
     * @param limit 返回条数
     * @return 活跃学生列表
     */
    List<ChannelTopStudentResVO> selectTopStudents(Long channelId, Integer limit);

    /**
     * 查询渠道订单详情。
     *
     * @param channelId 渠道方ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    ChannelOrderDetailResVO selectOrderDetail(Long channelId, Long orderId);

    void unbindStudent(Long channelId, Long bindingId);
}
