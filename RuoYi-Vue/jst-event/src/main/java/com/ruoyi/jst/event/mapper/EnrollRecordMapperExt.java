package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstEnrollRecord;
import com.ruoyi.jst.event.dto.EnrollQueryReqDTO;
import com.ruoyi.jst.event.vo.EnrollChannelGroupVO;
import com.ruoyi.jst.event.vo.EnrollDetailVO;
import com.ruoyi.jst.event.vo.EnrollListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报名记录扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface EnrollRecordMapperExt {

    JstEnrollRecord selectById(@Param("enrollId") Long enrollId);

    JstEnrollRecord selectLatestDraft(@Param("contestId") Long contestId,
                                      @Param("participantId") Long participantId,
                                      @Param("userId") Long userId);

    int countDuplicatePending(@Param("contestId") Long contestId,
                              @Param("participantId") Long participantId,
                              @Param("userId") Long userId,
                              @Param("excludeEnrollId") Long excludeEnrollId);

    int countOwnedParticipant(@Param("participantId") Long participantId, @Param("userId") Long userId);

    /**
     * 按监护人手机号查询可复用的参赛档案ID。
     *
     * @param phone 监护人手机号
     * @param name  参赛人姓名（用于优先命中）
     * @return 参赛档案ID
     * @关联表 jst_participant
     */
    Long selectParticipantIdByPhone(@Param("phone") String phone, @Param("name") String name);

    /**
     * 新增临时参赛档案。
     *
     * @param participant 参赛档案字段Map（insert后回填 participantId）
     * @return 影响行数
     * @关联表 jst_participant
     */
    int insertTempParticipant(Map<String, Object> participant);

    List<EnrollListVO> selectAdminList(@Param("query") EnrollQueryReqDTO query);

    /**
     * 小程序-我的报名列表 (按 user_id + 可选状态过滤)
     * @param userId 当前用户ID
     * @param auditStatus 可选,审核状态过滤
     */
    List<EnrollListVO> selectMyList(@Param("userId") Long userId,
                                    @Param("auditStatus") String auditStatus);

    EnrollDetailVO selectAdminDetail(@Param("enrollId") Long enrollId);

    EnrollDetailVO selectWxDetail(@Param("enrollId") Long enrollId);

    int updateAuditByExpected(@Param("enrollId") Long enrollId,
                              @Param("expectedAuditStatus") String expectedAuditStatus,
                              @Param("targetAuditStatus") String targetAuditStatus,
                              @Param("materialStatus") String materialStatus,
                              @Param("auditRemark") String auditRemark,
                              @Param("updateBy") String updateBy,
                              @Param("updateTime") Date updateTime);

    int updateSupplementRecord(JstEnrollRecord record);

    /**
     * 按"用户所属渠道"聚合指定赛事的报名数。
     *
     * @param contestId 赛事 ID
     * @return 聚合结果（channelId / channelName / enrollCount）
     * @关联表 jst_enroll_record, jst_user, jst_channel, jst_participant
     * @关联任务 ADMIN-UX-B3 主线 B
     */
    List<EnrollChannelGroupVO> selectChannelGroupsByContest(@Param("contestId") Long contestId);
}
