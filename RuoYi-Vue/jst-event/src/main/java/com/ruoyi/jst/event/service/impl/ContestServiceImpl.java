package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.cache.JstCacheService;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import com.ruoyi.jst.event.domain.JstContestAward;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.domain.JstContestFaq;
import com.ruoyi.jst.event.domain.JstContestSchedule;
import com.ruoyi.jst.event.domain.JstEnrollFormTemplate;
import com.ruoyi.jst.event.domain.JstScoreItem;
import com.ruoyi.jst.event.dto.AuditReqDTO;
import com.ruoyi.jst.event.dto.ContestQueryReqDTO;
import com.ruoyi.jst.event.dto.ContestSaveReqDTO;
import com.ruoyi.jst.event.dto.WxContestQueryDTO;
import com.ruoyi.jst.event.enums.ContestAuditStatus;
import com.ruoyi.jst.event.enums.ContestBizStatus;
import com.ruoyi.jst.event.mapper.ContestMapperExt;
import com.ruoyi.jst.event.mapper.JstAppointmentSlotMapper;
import com.ruoyi.jst.event.mapper.JstContestAwardMapper;
import com.ruoyi.jst.event.mapper.JstContestFaqMapper;
import com.ruoyi.jst.event.mapper.JstContestScheduleMapper;
import com.ruoyi.jst.event.mapper.JstEnrollFormTemplateMapper;
import com.ruoyi.jst.event.mapper.JstScoreItemMapper;
import com.ruoyi.jst.event.service.ContestService;
import com.ruoyi.jst.event.vo.CategoryStatVO;
import com.ruoyi.jst.event.vo.ContestDetailVO;
import com.ruoyi.jst.event.vo.ContestListVO;
import com.ruoyi.jst.event.vo.ContestRecommendCourseVO;
import com.ruoyi.jst.event.vo.ContestRecommendVO;
import com.ruoyi.jst.event.vo.WxContestCardVO;
import com.ruoyi.jst.event.vo.WxContestDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 赛事领域服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class ContestServiceImpl implements ContestService {

    private static final Logger log = LoggerFactory.getLogger(ContestServiceImpl.class);

    private static final String ROLE_PARTNER = "jst_partner";

    private static final String SOURCE_TYPE_PARTNER = "partner";

    @Autowired
    private ContestMapperExt contestMapperExt;

    @Autowired
    private JstEnrollFormTemplateMapper enrollFormTemplateMapper;

    @Autowired
    private JstCacheService jstCacheService;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private JstContestScheduleMapper contestScheduleMapper;

    @Autowired
    private JstContestAwardMapper contestAwardMapper;

    @Autowired
    private JstContestFaqMapper contestFaqMapper;

    @Autowired
    private JstScoreItemMapper scoreItemMapper;

    @Autowired
    private JstAppointmentSlotMapper appointmentSlotMapper;

    /**
     * 赛事方创建赛事草稿。
     *
     * @param req 创建请求
     * @return 新赛事ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_ADD", target = "#{req.contestId}", recordResult = true)
    public Long addContest(ContestSaveReqDTO req) {
        // TX: 创建赛事需在一个事务内完成校验与写库
        validateSaveReq(req);
        Long partnerId = requirePartnerId(req.getPartnerId());
        validateFormTemplate(req.getFormTemplateId());
        normalizeAppointmentDefaults(req);

        Date now = DateUtils.getNowDate();
        JstContest contest = new JstContest();
        contest.setContestName(req.getContestName());
        contest.setSourceType(SOURCE_TYPE_PARTNER);
        contest.setPartnerId(partnerId);
        contest.setCategory(req.getCategory());
        contest.setGroupLevels(req.getGroupLevels());
        contest.setCoverImage(req.getCoverImage());
        contest.setBannerImage(req.getBannerImage());
        contest.setDescription(req.getDescription());
        contest.setEnrollStartTime(req.getEnrollStartTime());
        contest.setEnrollEndTime(req.getEnrollEndTime());
        contest.setEventStartTime(req.getEventStartTime());
        contest.setEventEndTime(req.getEventEndTime());
        contest.setPrice(req.getPrice());
        contest.setSupportChannelEnroll(req.getSupportChannelEnroll());
        contest.setSupportPointsDeduct(req.getSupportPointsDeduct());
        contest.setSupportAppointment(req.getSupportAppointment());
        contest.setTotalQuota(req.getTotalQuota());
        contest.setPerUserLimit(req.getPerUserLimit());
        contest.setAppointmentCapacity(req.getAppointmentCapacity());
        contest.setWriteoffMode(req.getWriteoffMode());
        contest.setWriteoffConfig(req.getWriteoffConfig());
        contest.setNeedSignIn(req.getNeedSignIn());
        contest.setTeamMinSize(req.getTeamMinSize());
        contest.setTeamMaxSize(req.getTeamMaxSize());
        contest.setTeamLeaderFields(req.getTeamLeaderFields());
        contest.setTeamMemberFields(req.getTeamMemberFields());
        contest.setAllowRepeatAppointment(req.getAllowRepeatAppointment());
        contest.setAllowAppointmentRefund(req.getAllowAppointmentRefund());
        contest.setCertRuleJson(req.getCertRuleJson());
        contest.setCertIssueMode(req.getCertIssueMode());
        contest.setScorePublishTime(req.getScorePublishTime());
        contest.setScorePublishMode(req.getScorePublishMode());
        contest.setScoreRuleJson(req.getScoreRuleJson());
        contest.setAftersaleDeadline(req.getAftersaleDeadline());
        contest.setOrganizer(req.getOrganizer());
        contest.setCoOrganizer(req.getCoOrganizer());
        contest.setEventAddress(req.getEventAddress());
        contest.setOrganizerLogo(req.getOrganizerLogo());
        contest.setOrganizerDesc(req.getOrganizerDesc());
        contest.setContactPhone(req.getContactPhone());
        contest.setContactWechat(req.getContactWechat());
        contest.setContactEmail(req.getContactEmail());
        contest.setScheduleJson(req.getScheduleJson());
        contest.setAwardsJson(req.getAwardsJson());
        contest.setFaqJson(req.getFaqJson());
        contest.setRecommendTags(req.getRecommendTags());
        contest.setFormTemplateId(req.getFormTemplateId());
        contest.setAftersaleDays(req.getAftersaleDays());
        contest.setAuditStatus(ContestAuditStatus.DRAFT.dbValue()); // SM-5a
        contest.setStatus(ContestBizStatus.NOT_STARTED.dbValue()); // SM-5b
        contest.setCreatedUserId(SecurityUtils.getUserId());
        contest.setCreateBy(currentOperatorName());
        contest.setCreateTime(now);
        contest.setDelFlag("0");

        int rows = contestMapperExt.insertContest(contest);
        if (rows <= 0 || contest.getContestId() == null) {
            throw new ServiceException("创建赛事失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        // TX: 赛事保存时级联保存子表（先删后插）
        saveContestSubTables(contest.getContestId(), req, now, currentOperatorName());
        log.info("[ContestAdd] 新建赛事成功 contestId={} partnerId={}", contest.getContestId(), partnerId);
        evictContestCache();
        return contest.getContestId();
    }

    /**
     * 编辑赛事草稿或驳回稿。
     *
     * @param req 编辑请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_EDIT", target = "#{req.contestId}")
    public void editContest(ContestSaveReqDTO req) {
        // TX: 编辑赛事需在一个事务内完成校验与更新
        if (req.getContestId() == null) {
            throw new ServiceException("赛事ID不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        validateSaveReq(req);
        validateFormTemplate(req.getFormTemplateId());
        normalizeAppointmentDefaults(req);

        JstContest contest = getRequiredContest(req.getContestId());
        assertContestOwnership(contest);

        ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
        if (currentAudit != ContestAuditStatus.DRAFT && currentAudit != ContestAuditStatus.REJECTED) {
            throw new ServiceException("当前审核状态不允许编辑赛事",
                    BizErrorCode.JST_EVENT_CONTEST_ILLEGAL_TRANSIT.code());
        }

        JstContest update = new JstContest();
        update.setContestId(req.getContestId());
        update.setContestName(req.getContestName());
        update.setCategory(req.getCategory());
        update.setGroupLevels(req.getGroupLevels());
        update.setCoverImage(req.getCoverImage());
        update.setBannerImage(req.getBannerImage());
        update.setDescription(req.getDescription());
        update.setEnrollStartTime(req.getEnrollStartTime());
        update.setEnrollEndTime(req.getEnrollEndTime());
        update.setEventStartTime(req.getEventStartTime());
        update.setEventEndTime(req.getEventEndTime());
        update.setPrice(req.getPrice());
        update.setSupportChannelEnroll(req.getSupportChannelEnroll());
        update.setSupportPointsDeduct(req.getSupportPointsDeduct());
        update.setSupportAppointment(req.getSupportAppointment());
        update.setTotalQuota(req.getTotalQuota());
        update.setPerUserLimit(req.getPerUserLimit());
        update.setAppointmentCapacity(req.getAppointmentCapacity());
        update.setWriteoffMode(req.getWriteoffMode());
        update.setWriteoffConfig(req.getWriteoffConfig());
        update.setNeedSignIn(req.getNeedSignIn());
        update.setTeamMinSize(req.getTeamMinSize());
        update.setTeamMaxSize(req.getTeamMaxSize());
        update.setTeamLeaderFields(req.getTeamLeaderFields());
        update.setTeamMemberFields(req.getTeamMemberFields());
        update.setAllowRepeatAppointment(req.getAllowRepeatAppointment());
        update.setAllowAppointmentRefund(req.getAllowAppointmentRefund());
        update.setCertRuleJson(req.getCertRuleJson());
        update.setCertIssueMode(req.getCertIssueMode());
        update.setScorePublishTime(req.getScorePublishTime());
        update.setScorePublishMode(req.getScorePublishMode());
        update.setScoreRuleJson(req.getScoreRuleJson());
        update.setAftersaleDeadline(req.getAftersaleDeadline());
        update.setOrganizer(req.getOrganizer());
        update.setCoOrganizer(req.getCoOrganizer());
        update.setEventAddress(req.getEventAddress());
        update.setOrganizerLogo(req.getOrganizerLogo());
        update.setOrganizerDesc(req.getOrganizerDesc());
        update.setContactPhone(req.getContactPhone());
        update.setContactWechat(req.getContactWechat());
        update.setContactEmail(req.getContactEmail());
        update.setScheduleJson(req.getScheduleJson());
        update.setAwardsJson(req.getAwardsJson());
        update.setFaqJson(req.getFaqJson());
        update.setRecommendTags(req.getRecommendTags());
        update.setFormTemplateId(req.getFormTemplateId());
        update.setAftersaleDays(req.getAftersaleDays());
        update.setUpdateBy(currentOperatorName());
        update.setUpdateTime(DateUtils.getNowDate());

        int updated = contestMapperExt.updateContestForEdit(update, currentAudit.dbValue());
        if (updated == 0) {
            throw new ServiceException("编辑失败：赛事状态已变更，请刷新后重试",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        // TX: 赛事保存时级联保存子表（先删后插）
        saveContestSubTables(req.getContestId(), req, DateUtils.getNowDate(), currentOperatorName());
        evictContestCache();
    }

    /**
     * 提交赛事审核。
     *
     * @param contestId 赛事ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_SUBMIT", target = "#{contestId}")
    public void submitContest(Long contestId) {
        // TX: 提交审核需在一个事务内完成状态推进
        // LOCK: lock:contest:audit:{contestId}
        jstLockTemplate.execute("lock:contest:audit:" + contestId, 3, 5, () -> {
            JstContest contest = getRequiredContest(contestId);
            assertContestOwnership(contest);

            ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
            // SM-5a
            currentAudit.assertCanTransitTo(ContestAuditStatus.PENDING);

            int updated = contestMapperExt.updateAuditStatus(
                    contestId,
                    currentAudit.dbValue(),
                    ContestAuditStatus.PENDING.dbValue(),
                    null,
                    currentOperatorName(),
                    DateUtils.getNowDate()
            );
            if (updated == 0) {
                throw new ServiceException("提交审核失败：赛事状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return null;
        });
        evictContestCache();
    }

    /**
     * 审核通过赛事。
     *
     * @param contestId 赛事ID
     * @param req       审核请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_AUDIT_APPROVE", target = "#{contestId}")
    public void approveContest(Long contestId, AuditReqDTO req) {
        // TX: 审核通过需在一个事务内完成状态推进
        // LOCK: lock:contest:audit:{contestId}
        jstLockTemplate.execute("lock:contest:audit:" + contestId, 3, 5, () -> {
            JstContest contest = getRequiredContest(contestId);
            ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
            // SM-5a
            currentAudit.assertCanTransitTo(ContestAuditStatus.APPROVED);

            int updated = contestMapperExt.updateAuditStatus(
                    contestId,
                    currentAudit.dbValue(),
                    ContestAuditStatus.APPROVED.dbValue(),
                    safeRemark(req),
                    currentOperatorName(),
                    DateUtils.getNowDate()
            );
            if (updated == 0) {
                throw new ServiceException("审核通过失败：赛事状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return null;
        });
        evictContestCache();
    }

    /**
     * 驳回赛事审核。
     *
     * @param contestId 赛事ID
     * @param req       审核请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_AUDIT_REJECT", target = "#{contestId}")
    public void rejectContest(Long contestId, AuditReqDTO req) {
        // TX: 驳回审核需在一个事务内完成状态推进
        if (StringUtils.isBlank(safeRemark(req))) {
            throw new ServiceException("驳回备注不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        // LOCK: lock:contest:audit:{contestId}
        jstLockTemplate.execute("lock:contest:audit:" + contestId, 3, 5, () -> {
            JstContest contest = getRequiredContest(contestId);
            ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
            // SM-5a
            currentAudit.assertCanTransitTo(ContestAuditStatus.REJECTED);

            int updated = contestMapperExt.updateAuditStatus(
                    contestId,
                    currentAudit.dbValue(),
                    ContestAuditStatus.REJECTED.dbValue(),
                    safeRemark(req),
                    currentOperatorName(),
                    DateUtils.getNowDate()
            );
            if (updated == 0) {
                throw new ServiceException("审核驳回失败：赛事状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return null;
        });
        evictContestCache();
    }

    /**
     * 上线赛事。
     *
     * @param contestId 赛事ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_ONLINE", target = "#{contestId}")
    public void onlineContest(Long contestId) {
        // TX: 上线赛事需在一个事务内同步推进审核状态与业务状态
        // LOCK: lock:contest:status:{contestId}
        jstLockTemplate.execute("lock:contest:status:" + contestId, 3, 5, () -> {
            JstContest contest = getRequiredContest(contestId);
            ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
            // SM-5a
            currentAudit.assertCanTransitTo(ContestAuditStatus.ONLINE);

            Date now = DateUtils.getNowDate();
            boolean shouldTransitBiz = isEnrollOpen(contest, now)
                    && ContestBizStatus.NOT_STARTED.dbValue().equals(contest.getStatus());

            if (shouldTransitBiz) {
                ContestBizStatus currentBiz = ContestBizStatus.fromDb(contest.getStatus());
                // SM-5b
                currentBiz.assertCanTransitTo(ContestBizStatus.ENROLLING);
                int updated = contestMapperExt.updateAuditAndBizStatus(
                        contestId,
                        currentAudit.dbValue(),
                        ContestAuditStatus.ONLINE.dbValue(),
                        currentBiz.dbValue(),
                        ContestBizStatus.ENROLLING.dbValue(),
                        currentOperatorName(),
                        now
                );
                if (updated == 0) {
                    throw new ServiceException("赛事上线失败：赛事状态已变更，请刷新后重试",
                            BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
                }
                return null;
            }

            int updated = contestMapperExt.updateAuditStatus(
                    contestId,
                    currentAudit.dbValue(),
                    ContestAuditStatus.ONLINE.dbValue(),
                    null,
                    currentOperatorName(),
                    now
            );
            if (updated == 0) {
                throw new ServiceException("赛事上线失败：赛事状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return null;
        });
        evictContestCache();
    }

    /**
     * 下线赛事。
     *
     * @param contestId 赛事ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_OFFLINE", target = "#{contestId}")
    public void offlineContest(Long contestId) {
        // TX: 下线赛事需在一个事务内完成状态推进
        // LOCK: lock:contest:audit:{contestId}
        jstLockTemplate.execute("lock:contest:audit:" + contestId, 3, 5, () -> {
            JstContest contest = getRequiredContest(contestId);
            ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
            // SM-5a
            currentAudit.assertCanTransitTo(ContestAuditStatus.OFFLINE);

            int updated = contestMapperExt.updateAuditStatus(
                    contestId,
                    currentAudit.dbValue(),
                    ContestAuditStatus.OFFLINE.dbValue(),
                    null,
                    currentOperatorName(),
                    DateUtils.getNowDate()
            );
            if (updated == 0) {
                throw new ServiceException("赛事下线失败：赛事状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return null;
        });
        evictContestCache();
    }

    /**
     * 删除赛事草稿。
     *
     * @param contestId 赛事ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_DELETE", target = "#{contestId}")
    public void deleteContest(Long contestId) {
        // TX: 删除草稿需在一个事务内完成归属与状态校验
        // LOCK: lock:contest:audit:{contestId}
        jstLockTemplate.execute("lock:contest:audit:" + contestId, 3, 5, () -> {
            JstContest contest = getRequiredContest(contestId);
            assertContestOwnership(contest);
            ContestAuditStatus currentAudit = ContestAuditStatus.fromDb(contest.getAuditStatus());
            if (currentAudit != ContestAuditStatus.DRAFT) {
                throw new ServiceException("仅草稿状态允许删除",
                        BizErrorCode.JST_EVENT_CONTEST_ILLEGAL_TRANSIT.code());
            }
            int updated = contestMapperExt.logicalDeleteByExpectedAudit(
                    contestId,
                    currentAudit.dbValue(),
                    currentOperatorName(),
                    DateUtils.getNowDate()
            );
            if (updated == 0) {
                throw new ServiceException("删除失败：赛事状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            return null;
        });
        evictContestCache();
    }

    /**
     * 复制赛事。
     *
     * @param contestId 原赛事ID
     * @return 新赛事ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "CONTEST_COPY", target = "#{contestId}", recordResult = true)
    public Long copyContest(Long contestId) {
        // TX: 赛事复制需在一个事务内完成主表+子表深拷贝
        JstContest source = getRequiredContest(contestId);
        assertContestOwnership(source);

        Date now = DateUtils.getNowDate();
        String operator = currentOperatorName();
        JstContest copy = new JstContest();
        copy.setContestName(source.getContestName() + "(副本)");
        copy.setSourceType(source.getSourceType());
        copy.setPartnerId(source.getPartnerId());
        copy.setCategory(source.getCategory());
        copy.setGroupLevels(source.getGroupLevels());
        copy.setCoverImage(source.getCoverImage());
        copy.setBannerImage(source.getBannerImage());
        copy.setDescription(source.getDescription());
        copy.setEnrollStartTime(source.getEnrollStartTime());
        copy.setEnrollEndTime(source.getEnrollEndTime());
        copy.setEventStartTime(source.getEventStartTime());
        copy.setEventEndTime(source.getEventEndTime());
        copy.setPrice(source.getPrice());
        copy.setSupportChannelEnroll(source.getSupportChannelEnroll());
        copy.setSupportPointsDeduct(source.getSupportPointsDeduct());
        copy.setSupportAppointment(source.getSupportAppointment());
        copy.setTotalQuota(source.getTotalQuota());
        copy.setPerUserLimit(source.getPerUserLimit());
        copy.setAppointmentCapacity(source.getAppointmentCapacity());
        copy.setWriteoffMode(source.getWriteoffMode());
        copy.setWriteoffConfig(source.getWriteoffConfig());
        copy.setNeedSignIn(source.getNeedSignIn());
        copy.setTeamMinSize(source.getTeamMinSize());
        copy.setTeamMaxSize(source.getTeamMaxSize());
        copy.setTeamLeaderFields(source.getTeamLeaderFields());
        copy.setTeamMemberFields(source.getTeamMemberFields());
        copy.setAllowRepeatAppointment(source.getAllowRepeatAppointment());
        copy.setAllowAppointmentRefund(source.getAllowAppointmentRefund());
        copy.setCertRuleJson(source.getCertRuleJson());
        copy.setCertIssueMode(source.getCertIssueMode());
        copy.setScorePublishTime(source.getScorePublishTime());
        copy.setScorePublishMode(source.getScorePublishMode());
        copy.setScoreRuleJson(source.getScoreRuleJson());
        copy.setAftersaleDeadline(source.getAftersaleDeadline());
        copy.setOrganizer(source.getOrganizer());
        copy.setCoOrganizer(source.getCoOrganizer());
        copy.setEventAddress(source.getEventAddress());
        copy.setOrganizerLogo(source.getOrganizerLogo());
        copy.setOrganizerDesc(source.getOrganizerDesc());
        copy.setContactPhone(source.getContactPhone());
        copy.setContactWechat(source.getContactWechat());
        copy.setContactEmail(source.getContactEmail());
        copy.setScheduleJson(source.getScheduleJson());
        copy.setAwardsJson(source.getAwardsJson());
        copy.setFaqJson(source.getFaqJson());
        copy.setRecommendTags(source.getRecommendTags());
        copy.setFormTemplateId(source.getFormTemplateId());
        copy.setAftersaleDays(source.getAftersaleDays());
        copy.setAuditStatus(ContestAuditStatus.DRAFT.dbValue()); // SM-5a
        copy.setStatus(ContestBizStatus.NOT_STARTED.dbValue());  // SM-5b
        copy.setCreatedUserId(SecurityUtils.getUserId());
        copy.setCreateBy(operator);
        copy.setCreateTime(now);
        copy.setDelFlag("0");

        int rows = contestMapperExt.insertContest(copy);
        if (rows <= 0 || copy.getContestId() == null) {
            throw new ServiceException("复制赛事失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        copyContestSubTables(contestId, copy.getContestId(), now, operator);
        evictContestCache();
        return copy.getContestId();
    }

    /**
     * 查询后台赛事列表。
     *
     * @param query 查询条件
     * @return 列表结果
     */
    @Override
    public List<ContestListVO> selectAdminList(ContestQueryReqDTO query) {
        return contestMapperExt.selectAdminList(query);
    }

    /**
     * 查询后台赛事详情。
     *
     * @param contestId 赛事ID
     * @return 详情
     */
    @Override
    public ContestDetailVO getAdminDetail(Long contestId) {
        JstContest contest = getRequiredContest(contestId);
        assertContestOwnership(contest);
        ContestDetailVO vo = contestMapperExt.selectAdminDetail(contestId);
        if (vo == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        fillContestChildren(vo, contestId);
        return vo;
    }

    /**
     * 查询小程序在线赛事列表（缓存 5 分钟）。
     * <p>
     * 仅对无关键词搜索的分类浏览走缓存，关键词/标签/组别搜索直接查库。
     *
     * @param query 查询条件
     * @return 列表
     */
    @Override
    public List<WxContestCardVO> selectWxList(WxContestQueryDTO query) {
        // 关键词/标签/组别搜索不走缓存（高基数低命中率）
        if (StringUtils.isNotBlank(query.getKeyword()) || StringUtils.isNotBlank(query.getTag())
                || StringUtils.isNotBlank(query.getGroupLevel())) {
            return contestMapperExt.selectWxList(query);
        }
        int pn = query.getPageNum() != null ? query.getPageNum() : 1;
        String key = "cache:contest:list:" + safeKeyPart(query.getCategory()) + ":" + pn;
        return jstCacheService.getOrLoadPage(key, 300, () -> contestMapperExt.selectWxList(query));
    }

    /**
     * 查询小程序在线赛事详情（缓存 10 分钟）。
     *
     * @param contestId 赛事ID
     * @return 详情
     */
    @Override
    public WxContestDetailVO getWxDetail(Long contestId) {
        String key = "cache:contest:detail:" + contestId;
        return jstCacheService.getOrLoad(key, 600, () -> {
            JstContest contest = getRequiredContest(contestId);
            if (!ContestAuditStatus.ONLINE.dbValue().equals(contest.getAuditStatus())) {
                throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.message(),
                        BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.code());
            }
            WxContestDetailVO vo = contestMapperExt.selectWxDetail(contestId);
            if (vo == null) {
                throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.message(),
                        BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.code());
            }
            fillContestChildren(vo, contestId);
            return vo;
        });
    }

    /**
     * 填充赛事详情中与当前用户相关的字段。
     * <p>
     * scorePublished: 赛事级别，查 jst_score_record 是否有 published 记录。
     * hasCert/certImageUrl/totalScore: 用户级别，按 userId 查。
     *
     * @param vo     赛事详情 VO
     * @param userId 当前用户ID，null 表示未登录
     */
    @Override
    public void fillUserFields(WxContestDetailVO vo, Long userId) {
        if (vo == null) {
            return;
        }
        Long contestId = vo.getContestId();
        // 赛事级别：是否有已发布成绩
        vo.setScorePublished(contestMapperExt.countPublishedScores(contestId) > 0);
        if (userId == null) {
            vo.setHasCert(false);
            vo.setCertImageUrl(null);
            vo.setTotalScore(null);
            return;
        }
        // 用户级别：总分
        vo.setTotalScore(contestMapperExt.selectUserTotalScore(contestId, userId));
        // 用户级别：证书
        com.ruoyi.jst.event.domain.JstCertRecord cert = contestMapperExt.selectUserCert(contestId, userId);
        if (cert != null) {
            vo.setHasCert(true);
            vo.setCertImageUrl(cert.getCertFileUrl());
        } else {
            vo.setHasCert(false);
            vo.setCertImageUrl(null);
        }
    }

    /**
     * 查询热门赛事（缓存 5 分钟）。
     *
     * @param limit 限制数量
     * @return 热门赛事列表
     */
    @Override
    public List<WxContestCardVO> selectHotList(Integer limit) {
        int safeLimit = limit == null || limit <= 0 ? 6 : Math.min(limit, 20);
        String key = "cache:contest:hot:" + safeLimit;
        return jstCacheService.getOrLoad(key, 300, () -> contestMapperExt.selectHotList(safeLimit));
    }

    /**
     * 查询赛事详情页推荐内容。
     *
     * @param contestId 赛事ID
     * @return 推荐内容
     */
    @Override
    public ContestRecommendVO getWxRecommend(Long contestId) {
        JstContest contest = getRequiredContest(contestId);
        if (!ContestAuditStatus.ONLINE.dbValue().equals(contest.getAuditStatus())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.code());
        }

        ContestRecommendVO result = new ContestRecommendVO();
        List<ContestListVO> relatedContests = contestMapperExt.selectRelatedContests(
                contestId,
                contest.getCategory(),
                6
        );
        result.setRelatedContests(relatedContests == null ? Collections.emptyList() : relatedContests);

        List<String> tags = splitRecommendTags(contest.getRecommendTags());
        List<ContestRecommendCourseVO> relatedCourses = Collections.emptyList();
        if (!tags.isEmpty()) {
            relatedCourses = contestMapperExt.selectRelatedCoursesByTags(tags, 4);
        }
        if (relatedCourses == null || relatedCourses.isEmpty()) {
            relatedCourses = contestMapperExt.selectHotCourses(4);
        }
        result.setRelatedCourses(relatedCourses == null ? Collections.emptyList() : relatedCourses);
        return result;
    }

    /**
     * 查询赛事分类统计（缓存 60 分钟）。
     *
     * @return 分类统计
     */
    @Override
    public List<CategoryStatVO> selectCategoryStats() {
        return jstCacheService.getOrLoad("cache:contest:categories", 3600,
                () -> contestMapperExt.selectCategoryStats());
    }

    private void fillContestChildren(ContestDetailVO vo, Long contestId) {
        vo.setScheduleList(contestScheduleMapper.selectByContestId(contestId));
        vo.setAwardList(contestAwardMapper.selectByContestId(contestId));
        vo.setFaqList(contestFaqMapper.selectByContestId(contestId));
        vo.setScoreItemList(scoreItemMapper.selectByContestId(contestId));
        vo.setAppointmentSlotList(appointmentSlotMapper.selectByContestId(contestId));
    }

    private void fillContestChildren(WxContestDetailVO vo, Long contestId) {
        vo.setScheduleList(contestScheduleMapper.selectByContestId(contestId));
        vo.setAwardList(contestAwardMapper.selectByContestId(contestId));
        vo.setFaqList(contestFaqMapper.selectByContestId(contestId));
        vo.setScoreItemList(scoreItemMapper.selectByContestId(contestId));
        vo.setAppointmentSlotList(appointmentSlotMapper.selectByContestId(contestId));
    }

    private void saveContestSubTables(Long contestId, ContestSaveReqDTO req, Date now, String operator) {
        contestScheduleMapper.deleteByContestId(contestId);
        if (req.getScheduleList() != null && !req.getScheduleList().isEmpty()) {
            for (JstContestSchedule item : req.getScheduleList()) {
                item.setScheduleId(null);
                item.setContestId(contestId);
                item.setSortOrder(item.getSortOrder() == null ? 0 : item.getSortOrder());
                item.setCreateBy(operator);
                item.setCreateTime(now);
                item.setUpdateBy(operator);
                item.setUpdateTime(now);
                item.setDelFlag("0");
            }
            contestScheduleMapper.batchInsert(req.getScheduleList());
        }

        contestAwardMapper.deleteByContestId(contestId);
        if (req.getAwardList() != null && !req.getAwardList().isEmpty()) {
            for (JstContestAward item : req.getAwardList()) {
                item.setAwardId(null);
                item.setContestId(contestId);
                item.setQuota(item.getQuota() == null ? 0 : item.getQuota());
                item.setSortOrder(item.getSortOrder() == null ? 0 : item.getSortOrder());
                item.setCreateBy(operator);
                item.setCreateTime(now);
                item.setUpdateBy(operator);
                item.setUpdateTime(now);
                item.setDelFlag("0");
            }
            contestAwardMapper.batchInsert(req.getAwardList());
        }

        contestFaqMapper.deleteByContestId(contestId);
        if (req.getFaqList() != null && !req.getFaqList().isEmpty()) {
            for (JstContestFaq item : req.getFaqList()) {
                item.setFaqId(null);
                item.setContestId(contestId);
                item.setSortOrder(item.getSortOrder() == null ? 0 : item.getSortOrder());
                item.setCreateBy(operator);
                item.setCreateTime(now);
                item.setUpdateBy(operator);
                item.setUpdateTime(now);
                item.setDelFlag("0");
            }
            contestFaqMapper.batchInsert(req.getFaqList());
        }

        scoreItemMapper.deleteByContestId(contestId);
        if (req.getScoreItemList() != null && !req.getScoreItemList().isEmpty()) {
            for (JstScoreItem item : req.getScoreItemList()) {
                item.setItemId(null);
                item.setContestId(contestId);
                item.setSortOrder(item.getSortOrder() == null ? 0 : item.getSortOrder());
                item.setCreateBy(operator);
                item.setCreateTime(now);
                item.setUpdateBy(operator);
                item.setUpdateTime(now);
                item.setDelFlag("0");
            }
            scoreItemMapper.batchInsert(req.getScoreItemList());
        }

        appointmentSlotMapper.deleteByContestId(contestId);
        if (req.getAppointmentSlotList() != null && !req.getAppointmentSlotList().isEmpty()) {
            for (JstAppointmentSlot item : req.getAppointmentSlotList()) {
                item.setSlotId(null);
                item.setContestId(contestId);
                item.setCapacity(item.getCapacity() == null ? 0 : item.getCapacity());
                item.setBookedCount(item.getBookedCount() == null ? 0 : item.getBookedCount());
                item.setStatus(item.getStatus() == null ? 1 : item.getStatus());
                item.setCreateBy(operator);
                item.setCreateTime(now);
                item.setUpdateBy(operator);
                item.setUpdateTime(now);
                item.setDelFlag("0");
            }
            appointmentSlotMapper.batchInsert(req.getAppointmentSlotList());
        }
    }

    private void copyContestSubTables(Long sourceContestId, Long targetContestId, Date now, String operator) {
        List<JstContestSchedule> schedules = contestScheduleMapper.selectByContestId(sourceContestId);
        if (schedules != null && !schedules.isEmpty()) {
            List<JstContestSchedule> copies = new ArrayList<>();
            for (JstContestSchedule source : schedules) {
                JstContestSchedule copy = new JstContestSchedule();
                copy.setContestId(targetContestId);
                copy.setStageName(source.getStageName());
                copy.setStartTime(source.getStartTime());
                copy.setEndTime(source.getEndTime());
                copy.setVenue(source.getVenue());
                copy.setDescription(source.getDescription());
                copy.setSortOrder(source.getSortOrder());
                copy.setCreateBy(operator);
                copy.setCreateTime(now);
                copy.setUpdateBy(operator);
                copy.setUpdateTime(now);
                copy.setDelFlag("0");
                copies.add(copy);
            }
            contestScheduleMapper.batchInsert(copies);
        }

        List<JstContestAward> awards = contestAwardMapper.selectByContestId(sourceContestId);
        if (awards != null && !awards.isEmpty()) {
            List<JstContestAward> copies = new ArrayList<>();
            for (JstContestAward source : awards) {
                JstContestAward copy = new JstContestAward();
                copy.setContestId(targetContestId);
                copy.setAwardName(source.getAwardName());
                copy.setAwardLevel(source.getAwardLevel());
                copy.setDescription(source.getDescription());
                copy.setQuota(source.getQuota());
                copy.setSortOrder(source.getSortOrder());
                copy.setCreateBy(operator);
                copy.setCreateTime(now);
                copy.setUpdateBy(operator);
                copy.setUpdateTime(now);
                copy.setDelFlag("0");
                copies.add(copy);
            }
            contestAwardMapper.batchInsert(copies);
        }

        List<JstContestFaq> faqs = contestFaqMapper.selectByContestId(sourceContestId);
        if (faqs != null && !faqs.isEmpty()) {
            List<JstContestFaq> copies = new ArrayList<>();
            for (JstContestFaq source : faqs) {
                JstContestFaq copy = new JstContestFaq();
                copy.setContestId(targetContestId);
                copy.setQuestion(source.getQuestion());
                copy.setAnswer(source.getAnswer());
                copy.setSortOrder(source.getSortOrder());
                copy.setCreateBy(operator);
                copy.setCreateTime(now);
                copy.setUpdateBy(operator);
                copy.setUpdateTime(now);
                copy.setDelFlag("0");
                copies.add(copy);
            }
            contestFaqMapper.batchInsert(copies);
        }

        List<JstScoreItem> scoreItems = scoreItemMapper.selectByContestId(sourceContestId);
        if (scoreItems != null && !scoreItems.isEmpty()) {
            List<JstScoreItem> copies = new ArrayList<>();
            for (JstScoreItem source : scoreItems) {
                JstScoreItem copy = new JstScoreItem();
                copy.setContestId(targetContestId);
                copy.setItemName(source.getItemName());
                copy.setMaxScore(source.getMaxScore());
                copy.setWeight(source.getWeight());
                copy.setSortOrder(source.getSortOrder());
                copy.setCreateBy(operator);
                copy.setCreateTime(now);
                copy.setUpdateBy(operator);
                copy.setUpdateTime(now);
                copy.setDelFlag("0");
                copies.add(copy);
            }
            scoreItemMapper.batchInsert(copies);
        }
    }

    private JstContest getRequiredContest(Long contestId) {
        JstContest contest = contestMapperExt.selectById(contestId);
        if (contest == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        return contest;
    }

    private void validateSaveReq(ContestSaveReqDTO req) {
        if (req.getEnrollEndTime().compareTo(req.getEnrollStartTime()) <= 0) {
            throw new ServiceException("报名结束时间必须晚于报名开始时间",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (req.getEventEndTime().compareTo(req.getEventStartTime()) <= 0) {
            throw new ServiceException("比赛结束时间必须晚于比赛开始时间",
                    BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        BigDecimal price = req.getPrice();
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("报名价格不能小于0", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private void normalizeAppointmentDefaults(ContestSaveReqDTO req) {
        if (req.getTotalQuota() == null) {
            req.setTotalQuota(0);
        }
        if (req.getPerUserLimit() == null || req.getPerUserLimit() < 1) {
            req.setPerUserLimit(1);
        }
        if (req.getAppointmentCapacity() == null) {
            req.setAppointmentCapacity(0);
        }
        if (req.getNeedSignIn() == null) {
            req.setNeedSignIn(0);
        }
        if (req.getTeamMinSize() == null) {
            req.setTeamMinSize(0);
        }
        if (req.getTeamMaxSize() == null) {
            req.setTeamMaxSize(0);
        }
        if (StringUtils.isBlank(req.getWriteoffMode())) {
            req.setWriteoffMode("qr");
        }
        if (StringUtils.isBlank(req.getScorePublishMode())) {
            req.setScorePublishMode("manual");
        }
        if (StringUtils.isBlank(req.getCertIssueMode())) {
            req.setCertIssueMode("manual");
        }
        if (req.getAllowRepeatAppointment() == null) {
            req.setAllowRepeatAppointment(0);
        }
        if (req.getAllowAppointmentRefund() == null) {
            req.setAllowAppointmentRefund(0);
        }
    }

    private void validateFormTemplate(Long formTemplateId) {
        if (formTemplateId == null) {
            return;
        }
        JstEnrollFormTemplate template = enrollFormTemplateMapper.selectJstEnrollFormTemplateByTemplateId(formTemplateId);
        if (template == null || "2".equals(template.getDelFlag())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.message(),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
        }
    }

    private Long requirePartnerId(Long partnerId) {
        if (partnerId != null) {
            return partnerId;
        }
        Long currentPartnerId = JstLoginContext.currentPartnerId();
        if (currentPartnerId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        return currentPartnerId;
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

    private boolean isEnrollOpen(JstContest contest, Date now) {
        return now != null
                && contest.getEnrollStartTime() != null
                && contest.getEnrollEndTime() != null
                && !now.before(contest.getEnrollStartTime())
                && !now.after(contest.getEnrollEndTime());
    }

    private List<String> splitRecommendTags(String recommendTags) {
        if (StringUtils.isBlank(recommendTags)) {
            return Collections.emptyList();
        }
        String normalized = recommendTags.replace('，', ',');
        String[] segments = normalized.split(",");
        Set<String> uniqueTags = new LinkedHashSet<>();
        for (String segment : segments) {
            String tag = segment == null ? null : segment.trim();
            if (StringUtils.isNotBlank(tag)) {
                uniqueTags.add(tag);
            }
        }
        if (uniqueTags.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(uniqueTags);
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private String safeRemark(AuditReqDTO req) {
        return req == null ? null : req.getAuditRemark();
    }

    /**
     * 赛事写操作后清除缓存（列表 + 详情 + 首页推荐）。
     */
    private void evictContestCache() {
        jstCacheService.evictByPrefix("cache:contest:");
        jstCacheService.evictByPrefix("cache:home:");
    }

    private String safeKeyPart(String value) {
        return StringUtils.isBlank(value) ? "_all" : value;
    }
}
