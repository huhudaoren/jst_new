package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.dto.ContestQueryReqDTO;
import com.ruoyi.jst.event.dto.WxContestQueryDTO;
import com.ruoyi.jst.event.vo.CategoryStatVO;
import com.ruoyi.jst.event.vo.ContestDetailVO;
import com.ruoyi.jst.event.vo.ContestListVO;
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

    List<ContestListVO> selectAdminList(@Param("query") ContestQueryReqDTO query);

    ContestDetailVO selectAdminDetail(@Param("contestId") Long contestId);

    List<WxContestCardVO> selectWxList(@Param("query") WxContestQueryDTO query);

    WxContestDetailVO selectWxDetail(@Param("contestId") Long contestId);

    List<WxContestCardVO> selectHotList(@Param("limit") Integer limit);

    List<CategoryStatVO> selectCategoryStats();
}
