package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.domain.JstScoreRecord;
import com.ruoyi.jst.event.dto.ScoreCorrectionApplyReqDTO;
import com.ruoyi.jst.event.dto.ScoreImportRowDTO;
import com.ruoyi.jst.event.dto.ScoreQueryReqDTO;
import com.ruoyi.jst.event.dto.ScoreSaveReqDTO;
import com.ruoyi.jst.event.dto.ScoreSubmitReviewReqDTO;
import com.ruoyi.jst.event.enums.ScoreAuditStatus;
import com.ruoyi.jst.event.enums.ScorePublishStatus;
import com.ruoyi.jst.event.mapper.ContestMapperExt;
import com.ruoyi.jst.event.mapper.PartnerScoreMapper;
import com.ruoyi.jst.event.service.PartnerScoreService;
import com.ruoyi.jst.event.vo.PartnerScoreResVO;
import com.ruoyi.jst.event.vo.ScoreEnrollRefVO;
import com.ruoyi.jst.event.vo.ScoreImportResVO;
import com.ruoyi.jst.event.vo.ScoreStatsResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Partner score management service.
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class PartnerScoreServiceImpl implements PartnerScoreService {

    private static final Logger log = LoggerFactory.getLogger(PartnerScoreServiceImpl.class);
    private static final String ROLE_PARTNER = "jst_partner";
    private static final String ENROLL_AUDIT_APPROVED = "approved";
    private static final String CORRECTION_MARK = "[CORRECTION_REQUESTED]";

    @Autowired
    private PartnerScoreMapper partnerScoreMapper;

    @Autowired
    private ContestMapperExt contestMapperExt;

    /**
     * Imports score rows as drafts.
     *
     * @param contestId contest id
     * @param file Excel file
     * @return import result
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "score", action = "SCORE_IMPORT", target = "#{contestId}", recordResult = true)
    public ScoreImportResVO importScores(Long contestId, MultipartFile file) {
        // TX: score import validates enrollment ownership and writes the batch atomically.
        JstContest contest = getRequiredContest(contestId);
        assertContestOwnership(contest);
        if (file == null || file.isEmpty()) {
            throw new ServiceException("Import file is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        List<ScoreImportRowDTO> rows = readExcelRows(file);
        String importBatchNo = "SC" + DateUtils.dateTimeNow(DateUtils.YYYYMMDDHHMMSS);
        ScoreImportResVO result = new ScoreImportResVO();
        result.setImportBatchNo(importBatchNo);
        result.setTotalCount(rows.size());
        result.setSuccessCount(0);
        List<String> errors = new ArrayList<>();
        Date now = DateUtils.getNowDate();
        for (int i = 0; i < rows.size(); i++) {
            int rowNo = i + 2;
            try {
                ScoreImportRowDTO row = rows.get(i);
                validateImportRow(row);
                ScoreEnrollRefVO enroll = getRequiredEnrollRef(contestId, row.getEnrollId());
                assertEnrollApproved(enroll);
                assertNoDuplicateScore(enroll.getEnrollId(), null);
                JstScoreRecord score = buildScoreFromImport(row, enroll, importBatchNo, now);
                partnerScoreMapper.insertScore(score);
                result.setSuccessCount(result.getSuccessCount() + 1);
            } catch (ServiceException e) {
                errors.add("Row " + rowNo + ": " + e.getMessage());
            } catch (Exception e) {
                log.warn("[ScoreImport] row import failed rowNo={} contestId={}", rowNo, contestId, e);
                errors.add("Row " + rowNo + ": system error");
            }
        }
        result.setErrors(errors);
        result.setFailedCount(errors.size());
        if (result.getSuccessCount() == 0) {
            throw new ServiceException("No valid score rows imported", BizErrorCode.JST_EVENT_SCORE_IMPORT_FAILED.code());
        }
        return result;
    }

    /**
     * Lists partner scores.
     *
     * @param query query DTO carrying PartnerDataScope
     * @return score list
     */
    @Override
    public List<PartnerScoreResVO> listScores(ScoreQueryReqDTO query) {
        if (query == null) {
            query = new ScoreQueryReqDTO();
        }
        List<PartnerScoreResVO> list = partnerScoreMapper.selectScoreList(query);
        for (PartnerScoreResVO vo : list) {
            vo.setGuardianMobileMasked(MaskUtil.mobile(vo.getGuardianMobileMasked()));
            vo.setDisplayStatus(resolveScoreDisplayStatus(vo.getAuditStatus(), vo.getPublishStatus(), vo.getRemark()));
        }
        return list;
    }

    /**
     * Gets score statistics for a contest.
     *
     * @param contestId contest id
     * @return score statistics
     */
    @Override
    public ScoreStatsResVO getStats(Long contestId) {
        JstContest contest = getRequiredContest(contestId);
        assertContestOwnership(contest);
        ScoreStatsResVO stats = partnerScoreMapper.selectScoreStats(contestId, contest.getPartnerId());
        return stats == null ? new ScoreStatsResVO() : stats;
    }

    /**
     * Creates or updates a draft score.
     *
     * @param scoreId score id; 0 means create
     * @param req save request
     * @return score id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "score", action = "SCORE_SAVE", target = "#{scoreId}", recordResult = true)
    public Long saveScore(Long scoreId, ScoreSaveReqDTO req) {
        // TX: save validates enrollment ownership, score uniqueness, and editable state.
        validateScoreContent(req);
        JstContest contest = getRequiredContest(req.getContestId());
        assertContestOwnership(contest);
        ScoreEnrollRefVO enroll = getRequiredEnrollRef(req.getContestId(), req.getEnrollId());
        assertEnrollApproved(enroll);
        if (scoreId == null || scoreId <= 0) {
            assertNoDuplicateScore(req.getEnrollId(), null);
            JstScoreRecord score = buildScoreFromSave(req, enroll, DateUtils.getNowDate());
            int rows = partnerScoreMapper.insertScore(score);
            if (rows <= 0 || score.getScoreId() == null) {
                throw new ServiceException("Score save failed", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return score.getScoreId();
        }
        JstScoreRecord current = getRequiredScore(scoreId);
        assertScoreOwnership(current);
        assertScoreEditable(current);
        assertNoDuplicateScore(req.getEnrollId(), scoreId);
        JstScoreRecord update = buildScoreFromSave(req, enroll, DateUtils.getNowDate());
        update.setScoreId(scoreId);
        int updated = partnerScoreMapper.updateDraftScore(update, current.getAuditStatus());
        if (updated == 0) {
            throw new ServiceException("Score save failed: status changed", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return scoreId;
    }

    /**
     * Submits draft scores for platform review.
     *
     * @param req submit request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "score", action = "SCORE_SUBMIT_REVIEW", target = "#{req.scoreIds}")
    public void submitReview(ScoreSubmitReviewReqDTO req) {
        // TX: submit review checks ownership and optimistic state per score.
        Date now = DateUtils.getNowDate();
        for (Long scoreId : req.getScoreIds()) {
            JstScoreRecord score = getRequiredScore(scoreId);
            assertScoreOwnership(score);
            if (!ScorePublishStatus.UNPUBLISHED.dbValue().equals(score.getPublishStatus())) {
                throw new ServiceException("Published scores cannot be resubmitted by partner",
                        BizErrorCode.JST_EVENT_SCORE_NOT_EDITABLE.code());
            }
            ScoreAuditStatus current = ScoreAuditStatus.fromDb(score.getAuditStatus());
            current.assertCanTransitTo(ScoreAuditStatus.PENDING); // SM-19
            int updated = partnerScoreMapper.updateAuditStatusByExpected(scoreId, current.dbValue(),
                    ScoreAuditStatus.PENDING.dbValue(), currentOperatorName(), now);
            if (updated == 0) {
                throw new ServiceException("Score review submit failed: status changed",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
        }
    }

    /**
     * Lists correction requests.
     *
     * @param query query DTO
     * @return correction list
     */
    @Override
    public List<PartnerScoreResVO> listCorrectionApplications(ScoreQueryReqDTO query) {
        if (query == null) {
            query = new ScoreQueryReqDTO();
        }
        query.setDisplayStatus("correction_requested");
        return listScores(query);
    }

    /**
     * Applies for correction on a published score without changing public score fields.
     *
     * @param req correction request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "score", action = "SCORE_CORRECTION_APPLY", target = "#{req.scoreId}")
    public void applyCorrection(ScoreCorrectionApplyReqDTO req) {
        // TX: published score correction records an application only.
        JstScoreRecord score = getRequiredScore(req.getScoreId());
        assertScoreOwnership(score);
        if (!ScorePublishStatus.PUBLISHED.dbValue().equals(score.getPublishStatus())) {
            throw new ServiceException("Only published scores can request correction",
                    BizErrorCode.JST_EVENT_SCORE_NOT_EDITABLE.code());
        }
        if (StringUtils.isBlank(req.getReason())) {
            throw new ServiceException("Correction reason is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        int updated = partnerScoreMapper.markCorrectionRequested(score.getScoreId(), buildCorrectionRemark(req),
                currentOperatorName(), DateUtils.getNowDate());
        if (updated == 0) {
            throw new ServiceException("Correction apply failed: status changed",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    private List<ScoreImportRowDTO> readExcelRows(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            ExcelUtil<ScoreImportRowDTO> util = new ExcelUtil<>(ScoreImportRowDTO.class);
            List<ScoreImportRowDTO> rows = util.importExcel(inputStream);
            return rows == null ? List.of() : rows;
        } catch (Exception e) {
            log.warn("[ScoreImport] read excel failed filename={}", file.getOriginalFilename(), e);
            throw new ServiceException("Score import failed", BizErrorCode.JST_EVENT_SCORE_IMPORT_FAILED.code());
        }
    }

    private void validateImportRow(ScoreImportRowDTO row) {
        if (row == null || row.getEnrollId() == null) {
            throw new ServiceException("Enrollment id is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (row.getScoreValue() == null && StringUtils.isBlank(row.getAwardLevel())) {
            throw new ServiceException("Score value or award level is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (row.getScoreValue() != null && row.getScoreValue().signum() < 0) {
            throw new ServiceException("Score value must be non-negative", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private void validateScoreContent(ScoreSaveReqDTO req) {
        if (req == null) {
            throw new ServiceException("Score request is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (req.getScoreValue() == null && StringUtils.isBlank(req.getAwardLevel())) {
            throw new ServiceException("Score value or award level is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private JstScoreRecord buildScoreFromImport(ScoreImportRowDTO row, ScoreEnrollRefVO enroll,
                                                String importBatchNo, Date now) {
        JstScoreRecord score = new JstScoreRecord();
        score.setContestId(enroll.getContestId());
        score.setEnrollId(enroll.getEnrollId());
        score.setUserId(enroll.getUserId());
        score.setParticipantId(enroll.getParticipantId());
        score.setScoreValue(row.getScoreValue());
        score.setAwardLevel(trimToNull(row.getAwardLevel()));
        score.setRankNo(row.getRankNo());
        score.setImportBatchNo(importBatchNo);
        score.setAuditStatus(ScoreAuditStatus.DRAFT.dbValue());
        score.setPublishStatus(ScorePublishStatus.UNPUBLISHED.dbValue());
        score.setRemark(trimToNull(row.getRemark()));
        score.setCreateBy(currentOperatorName());
        score.setCreateTime(now);
        score.setUpdateBy(currentOperatorName());
        score.setUpdateTime(now);
        score.setDelFlag("0");
        return score;
    }

    private JstScoreRecord buildScoreFromSave(ScoreSaveReqDTO req, ScoreEnrollRefVO enroll, Date now) {
        JstScoreRecord score = new JstScoreRecord();
        score.setContestId(req.getContestId());
        score.setEnrollId(req.getEnrollId());
        score.setUserId(enroll.getUserId());
        score.setParticipantId(enroll.getParticipantId());
        score.setScoreValue(req.getScoreValue());
        score.setAwardLevel(trimToNull(req.getAwardLevel()));
        score.setRankNo(req.getRankNo());
        score.setImportBatchNo("MANUAL");
        score.setAuditStatus(ScoreAuditStatus.DRAFT.dbValue());
        score.setPublishStatus(ScorePublishStatus.UNPUBLISHED.dbValue());
        score.setRemark(trimToNull(req.getRemark()));
        score.setCreateBy(currentOperatorName());
        score.setCreateTime(now);
        score.setUpdateBy(currentOperatorName());
        score.setUpdateTime(now);
        score.setDelFlag("0");
        return score;
    }

    private JstContest getRequiredContest(Long contestId) {
        if (contestId == null) {
            throw new ServiceException("Contest id is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstContest contest = contestMapperExt.selectById(contestId);
        if (contest == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        return contest;
    }

    private ScoreEnrollRefVO getRequiredEnrollRef(Long contestId, Long enrollId) {
        ScoreEnrollRefVO enroll = partnerScoreMapper.selectEnrollRef(contestId, enrollId);
        if (enroll == null) {
            throw new ServiceException("Enrollment not found for contest", BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.code());
        }
        return enroll;
    }

    private JstScoreRecord getRequiredScore(Long scoreId) {
        if (scoreId == null) {
            throw new ServiceException("Score id is required", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        JstScoreRecord score = partnerScoreMapper.selectById(scoreId);
        if (score == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_SCORE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_SCORE_NOT_FOUND.code());
        }
        return score;
    }

    private void assertEnrollApproved(ScoreEnrollRefVO enroll) {
        if (!ENROLL_AUDIT_APPROVED.equals(enroll.getAuditStatus())) {
            throw new ServiceException("Only approved enrollments can receive scores",
                    BizErrorCode.JST_EVENT_SCORE_NOT_EDITABLE.code());
        }
    }

    private void assertNoDuplicateScore(Long enrollId, Long excludeScoreId) {
        if (partnerScoreMapper.countScoreByEnroll(enrollId, excludeScoreId) > 0) {
            throw new ServiceException("Enrollment already has a score",
                    BizErrorCode.JST_EVENT_SCORE_NOT_EDITABLE.code());
        }
    }

    private void assertScoreEditable(JstScoreRecord score) {
        if (!ScorePublishStatus.UNPUBLISHED.dbValue().equals(score.getPublishStatus())) {
            throw new ServiceException("Published scores cannot be edited by partner",
                    BizErrorCode.JST_EVENT_SCORE_NOT_EDITABLE.code());
        }
        ScoreAuditStatus current = ScoreAuditStatus.fromDb(score.getAuditStatus());
        if (current != ScoreAuditStatus.DRAFT && current != ScoreAuditStatus.REJECTED) {
            throw new ServiceException("Current score status is not editable",
                    BizErrorCode.JST_EVENT_SCORE_NOT_EDITABLE.code());
        }
    }

    private void assertScoreOwnership(JstScoreRecord score) {
        JstContest contest = getRequiredContest(score.getContestId());
        assertContestOwnership(contest);
    }

    private void assertContestOwnership(JstContest contest) {
        if (!JstLoginContext.hasRole(ROLE_PARTNER)) {
            return;
        }
        Long currentPartnerId = JstLoginContext.currentPartnerId();
        if (currentPartnerId == null || !currentPartnerId.equals(contest.getPartnerId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private String resolveScoreDisplayStatus(String auditStatus, String publishStatus, String remark) {
        if (ScorePublishStatus.PUBLISHED.dbValue().equals(publishStatus)
                && remark != null && remark.startsWith(CORRECTION_MARK)) {
            return "correction_requested";
        }
        if (ScorePublishStatus.PUBLISHED.dbValue().equals(publishStatus)) {
            return "published";
        }
        if (ScoreAuditStatus.PENDING.dbValue().equals(auditStatus)) {
            return "pending_review";
        }
        if (ScoreAuditStatus.APPROVED.dbValue().equals(auditStatus)) {
            return "pending_publish";
        }
        return auditStatus;
    }

    private String buildCorrectionRemark(ScoreCorrectionApplyReqDTO req) {
        String text = CORRECTION_MARK
                + " newScore=" + (req.getNewScoreValue() == null ? "" : req.getNewScoreValue())
                + ";newAward=" + trimToEmpty(req.getNewAwardLevel())
                + ";newRank=" + (req.getNewRankNo() == null ? "" : req.getNewRankNo())
                + ";reason=" + trimToEmpty(req.getReason());
        return text.length() > 500 ? text.substring(0, 500) : text;
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private String trimToNull(String text) {
        return StringUtils.isBlank(text) ? null : text.trim();
    }

    private String trimToEmpty(String text) {
        return StringUtils.isBlank(text) ? "" : text.trim();
    }
}
