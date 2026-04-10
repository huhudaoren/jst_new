package com.ruoyi.jst.event.service;

import com.ruoyi.jst.event.dto.ScoreCorrectionApplyReqDTO;
import com.ruoyi.jst.event.dto.ScoreQueryReqDTO;
import com.ruoyi.jst.event.dto.ScoreSaveReqDTO;
import com.ruoyi.jst.event.dto.ScoreSubmitReviewReqDTO;
import com.ruoyi.jst.event.vo.PartnerScoreResVO;
import com.ruoyi.jst.event.vo.ScoreImportResVO;
import com.ruoyi.jst.event.vo.ScoreStatsResVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 赛事方成绩领域服务。
 *
 * @author jst
 * @since 1.0.0
 */
public interface PartnerScoreService {

    /**
     * 导入赛事成绩。
     *
     * @param contestId 赛事ID，关联表 jst_contest/jst_score_record，权限 jst:event:score_record:add
     * @param file Excel 文件
     * @return 导入结果
     */
    ScoreImportResVO importScores(Long contestId, MultipartFile file);

    /**
     * 查询赛事方成绩列表。
     *
     * @param query 查询条件，关联表 jst_score_record，权限 jst:event:score_record:list
     * @return 成绩列表
     */
    List<PartnerScoreResVO> listScores(ScoreQueryReqDTO query);

    /**
     * 查询成绩管理统计。
     *
     * @param contestId 赛事ID，关联表 jst_enroll_record/jst_score_record
     * @return 统计信息
     */
    ScoreStatsResVO getStats(Long contestId);

    /**
     * 保存单条成绩。
     *
     * @param scoreId 成绩ID，0 表示新增
     * @param req 保存请求，关联 SM-19 / jst_score_record / jst_enroll_record
     * @return 成绩ID
     */
    Long saveScore(Long scoreId, ScoreSaveReqDTO req);

    /**
     * 批量提交平台审核。
     *
     * @param req 提交请求，关联 SM-19 / jst_score_record
     */
    void submitReview(ScoreSubmitReviewReqDTO req);

    /**
     * 查询成绩更正申请列表。
     *
     * @param query 查询条件，关联表 jst_score_record
     * @return 更正申请列表
     */
    List<PartnerScoreResVO> listCorrectionApplications(ScoreQueryReqDTO query);

    /**
     * 提交成绩更正申请。
     *
     * @param req 更正申请，关联 SM-19 / jst_score_record
     */
    void applyCorrection(ScoreCorrectionApplyReqDTO req);
}
