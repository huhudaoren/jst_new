package com.ruoyi.jst.user.mapper;

import com.ruoyi.jst.user.domain.Participant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 临时参赛档案 Mapper
 *
 * @author jst
 * @since 1.0.0
 */
@Mapper
public interface ParticipantMapper {

    /** 按主键查询 */
    Participant selectByPk(@Param("participantId") Long participantId);

    /** 按手机号+姓名精确匹配(自动认领核心查询) */
    List<Participant> selectByGuardianMobileAndName(@Param("guardianMobile") String guardianMobile,
                                                    @Param("name") String name);

    /** 按手机号查询(用于多候选检测) */
    List<Participant> selectByGuardianMobile(@Param("guardianMobile") String guardianMobile);

    /** 更新认领状态(必须传当前 status 做乐观校验,避免并发覆盖) */
    int updateClaimStatus(@Param("participantId") Long participantId,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("newStatus") String newStatus,
                          @Param("claimedUserId") Long claimedUserId);
}
