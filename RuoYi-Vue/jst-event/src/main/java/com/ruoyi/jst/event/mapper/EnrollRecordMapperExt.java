package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstEnrollRecord;
import com.ruoyi.jst.event.dto.EnrollQueryReqDTO;
import com.ruoyi.jst.event.vo.EnrollDetailVO;
import com.ruoyi.jst.event.vo.EnrollListVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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
}
