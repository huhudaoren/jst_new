package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.dto.ContestQueryReqDTO;
import com.ruoyi.jst.event.dto.WxContestQueryDTO;
import com.ruoyi.jst.event.vo.CategoryStatVO;
import com.ruoyi.jst.event.vo.ContestDetailVO;
import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.ContestRecommendCourseVO;
import com.ruoyi.jst.event.vo.WxContestCardVO;
import com.ruoyi.jst.event.vo.WxContestDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 赛事扩展 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface ContestMapperExt {

    JstContest selectById(@Param("contestId") Long contestId);

    int insertContest(@Param("contest") JstContest contest);

    int updateContestForEdit(@Param("contest") JstContest contest,
                             @Param("expectedAuditStatus") String expectedAuditStatus);

    int updateAuditStatus(@Param("contestId") Long contestId,
                          @Param("expectedAuditStatus") String expectedAuditStatus,
                          @Param("targetAuditStatus") String targetAuditStatus,
                          @Param("auditRemark") String auditRemark,
                          @Param("updateBy") String updateBy,
                          @Param("updateTime") Date updateTime);

    int updateAuditAndBizStatus(@Param("contestId") Long contestId,
                                @Param("expectedAuditStatus") String expectedAuditStatus,
                                @Param("targetAuditStatus") String targetAuditStatus,
                                @Param("expectedBizStatus") String expectedBizStatus,
                                @Param("targetBizStatus") String targetBizStatus,
                                @Param("updateBy") String updateBy,
                                @Param("updateTime") Date updateTime);

    int logicalDeleteByExpectedAudit(@Param("contestId") Long contestId,
                                     @Param("expectedAuditStatus") String expectedAuditStatus,
                                     @Param("updateBy") String updateBy,
                                     @Param("updateTime") Date updateTime);

    List<ContestListVO> selectAdminList(@Param("query") ContestQueryReqDTO query);

    ContestDetailVO selectAdminDetail(@Param("contestId") Long contestId);

    List<WxContestCardVO> selectWxList(@Param("query") WxContestQueryDTO query);

    WxContestDetailVO selectWxDetail(@Param("contestId") Long contestId);

    List<WxContestCardVO> selectHotList(@Param("limit") Integer limit);

    List<CategoryStatVO> selectCategoryStats();

    List<ContestListVO> selectRelatedContests(@Param("contestId") Long contestId,
                                              @Param("category") String category,
                                              @Param("limit") Integer limit);

    List<ContestRecommendCourseVO> selectRelatedCoursesByTags(@Param("tags") List<String> tags,
                                                              @Param("limit") Integer limit);

    List<ContestRecommendCourseVO> selectHotCourses(@Param("limit") Integer limit);

    /**
     * 查询赛事是否有已发布成绩。
     *
     * @param contestId 赛事ID
     * @return 1=有已发布成绩，0=无
     */
    int countPublishedScores(@Param("contestId") Long contestId);

    /**
     * 查询用户在该赛事的总分。
     *
     * @param contestId 赛事ID
     * @param userId    用户ID
     * @return 总分，无成绩返回 null
     */
    java.math.BigDecimal selectUserTotalScore(@Param("contestId") Long contestId, @Param("userId") Long userId);

    /**
     * 查询用户在该赛事的证书记录。
     *
     * @param contestId 赛事ID
     * @param userId    用户ID
     * @return 证书记录，无则 null
     */
    com.ruoyi.jst.event.domain.JstCertRecord selectUserCert(@Param("contestId") Long contestId, @Param("userId") Long userId);
}
