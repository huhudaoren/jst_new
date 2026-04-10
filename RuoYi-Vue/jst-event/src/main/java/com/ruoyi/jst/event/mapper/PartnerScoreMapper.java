package com.ruoyi.jst.event.mapper;

import com.ruoyi.jst.event.domain.JstScoreRecord;
import com.ruoyi.jst.event.dto.ScoreQueryReqDTO;
import com.ruoyi.jst.event.vo.PartnerScoreResVO;
import com.ruoyi.jst.event.vo.ScoreEnrollRefVO;
import com.ruoyi.jst.event.vo.ScoreStatsResVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 赛事方成绩 Mapper。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PartnerScoreMapper {

    /**
     * 查询赛事方成绩列表。
     *
     * @param query 查询条件，承载 PartnerDataScope
     * @return 成绩列表
     */
    List<PartnerScoreResVO> selectScoreList(ScoreQueryReqDTO query);

    /**
     * 查询成绩管理统计。
     *
     * @param contestId 赛事ID
     * @param partnerId 当前赛事方ID
     * @return 统计信息
     */
    ScoreStatsResVO selectScoreStats(@Param("contestId") Long contestId, @Param("partnerId") Long partnerId);

    /**
     * 按ID查询成绩。
     *
     * @param scoreId 成绩ID
     * @return 成绩实体
     */
    JstScoreRecord selectById(@Param("scoreId") Long scoreId);

    /**
     * 查询报名记录引用。
     *
     * @param contestId 赛事ID
     * @param enrollId 报名ID
     * @return 报名引用
     */
    ScoreEnrollRefVO selectEnrollRef(@Param("contestId") Long contestId, @Param("enrollId") Long enrollId);

    /**
     * 统计报名已有成绩数量。
     *
     * @param enrollId 报名ID
     * @param excludeScoreId 排除的成绩ID
     * @return 数量
     */
    int countScoreByEnroll(@Param("enrollId") Long enrollId, @Param("excludeScoreId") Long excludeScoreId);

    /**
     * 新增成绩。
     *
     * @param score 成绩实体
     * @return 写入行数
     */
    int insertScore(JstScoreRecord score);

    /**
     * 草稿态编辑成绩。
     *
     * @param score 成绩实体
     * @param expectedAuditStatus 期望审核状态
     * @return 更新行数
     */
    int updateDraftScore(@Param("score") JstScoreRecord score, @Param("expectedAuditStatus") String expectedAuditStatus);

    /**
     * 按期望状态更新审核状态。
     *
     * @param scoreId 成绩ID
     * @param expectedAuditStatus 期望审核状态
     * @param targetAuditStatus 目标审核状态
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int updateAuditStatusByExpected(@Param("scoreId") Long scoreId,
                                    @Param("expectedAuditStatus") String expectedAuditStatus,
                                    @Param("targetAuditStatus") String targetAuditStatus,
                                    @Param("updateBy") String updateBy,
                                    @Param("updateTime") Date updateTime);

    /**
     * 写入成绩更正申请备注。
     *
     * @param scoreId 成绩ID
     * @param remark 更正申请备注
     * @param updateBy 更新人
     * @param updateTime 更新时间
     * @return 更新行数
     */
    int markCorrectionRequested(@Param("scoreId") Long scoreId,
                                @Param("remark") String remark,
                                @Param("updateBy") String updateBy,
                                @Param("updateTime") Date updateTime);
}
