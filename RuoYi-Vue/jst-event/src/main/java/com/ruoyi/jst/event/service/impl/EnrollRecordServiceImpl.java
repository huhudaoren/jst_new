package com.ruoyi.jst.event.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.event.EnrollAuditEvent;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.security.SecurityCheck;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.common.service.BizNoGenerateService;
import com.ruoyi.jst.event.domain.JstAppointmentSlot;
import com.ruoyi.jst.event.domain.JstCertRecord;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.domain.JstEnrollFormTemplate;
import com.ruoyi.jst.event.domain.JstEnrollRecord;
import com.ruoyi.jst.event.domain.JstScoreItem;
import com.ruoyi.jst.event.domain.JstScoreRecord;
import com.ruoyi.jst.event.dto.EnrollAuditReqDTO;
import com.ruoyi.jst.event.dto.EnrollDraftDTO;
import com.ruoyi.jst.event.dto.EnrollQueryReqDTO;
import com.ruoyi.jst.event.dto.EnrollSubmitDTO;
import com.ruoyi.jst.event.dto.EnrollSupplementDTO;
import com.ruoyi.jst.event.dto.ScoreItemReqDTO;
import com.ruoyi.jst.event.dto.TeamEnrollReqDTO;
import com.ruoyi.jst.event.enums.ContestAuditStatus;
import com.ruoyi.jst.event.enums.ContestBizStatus;
import com.ruoyi.jst.event.enums.EnrollAuditStatus;
import com.ruoyi.jst.event.enums.EnrollMaterialStatus;
import com.ruoyi.jst.event.mapper.EnrollRecordMapperExt;
import com.ruoyi.jst.event.mapper.JstAppointmentSlotMapper;
import com.ruoyi.jst.event.mapper.JstCertRecordMapper;
import com.ruoyi.jst.event.mapper.JstContestMapper;
import com.ruoyi.jst.event.mapper.JstEnrollFormTemplateMapper;
import com.ruoyi.jst.event.mapper.JstEnrollRecordMapper;
import com.ruoyi.jst.event.mapper.JstScoreItemMapper;
import com.ruoyi.jst.event.mapper.JstScoreRecordMapper;
import com.ruoyi.jst.event.service.EnrollRecordService;
import com.ruoyi.jst.event.vo.EnrollDetailVO;
import com.ruoyi.jst.event.vo.EnrollListVO;
import com.ruoyi.jst.event.vo.EnrollSubmitResVO;
import com.ruoyi.jst.event.vo.TeamEnrollResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 报名记录领域服务实现。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class EnrollRecordServiceImpl implements EnrollRecordService {

    private static final String ROLE_PARTNER = "jst_partner";
    private static final Set<String> UPLOAD_FIELD_TYPES = Set.of("file", "image", "audio", "video");

    @Autowired private JstEnrollRecordMapper jstEnrollRecordMapper;
    @Autowired private EnrollRecordMapperExt enrollRecordMapperExt;
    @Autowired private JstContestMapper jstContestMapper;
    @Autowired private JstEnrollFormTemplateMapper jstEnrollFormTemplateMapper;
    @Autowired private JstLockTemplate jstLockTemplate;
    @Autowired private SnowflakeIdWorker snowflakeIdWorker;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private JstScoreItemMapper jstScoreItemMapper;
    @Autowired private JstScoreRecordMapper jstScoreRecordMapper;
    @Autowired private JstCertRecordMapper jstCertRecordMapper;
    @Autowired private BizNoGenerateService bizNoGenerateService;
    @Autowired private JstAppointmentSlotMapper jstAppointmentSlotMapper;

    /**
     * 保存报名草稿。
     * @param req 草稿请求
     * @return 保存结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "ENROLL_DRAFT", target = "#{req.enrollId}", recordResult = true)
    public EnrollSubmitResVO saveDraft(EnrollDraftDTO req) {
        // TX: 草稿保存
        Long userId = currentUserId();
        assertParticipantOwned(req.getParticipantId(), userId);
        JstContest contest = requireContest(req.getContestId());
        assertContestOnline(contest);
        JstEnrollFormTemplate template = requireApprovedTemplate(contest.getFormTemplateId());
        JstEnrollRecord record = req.getEnrollId() == null
                ? enrollRecordMapperExt.selectLatestDraft(req.getContestId(), req.getParticipantId(), userId)
                : requireOwnedEnroll(req.getEnrollId(), userId);
        if (record != null) {
            assertSameContestAndParticipant(record, req.getContestId(), req.getParticipantId());
            assertDraftEditable(record);
        }
        if (record == null) {
            record = buildNewRecord(req.getContestId(), req.getParticipantId(), userId);
            record.setMaterialStatus(EnrollMaterialStatus.DRAFT.dbValue());
            record.setAuditStatus(EnrollAuditStatus.PENDING.dbValue());
            record.setCreateBy(currentOperatorName());
            record.setCreateTime(DateUtils.getNowDate());
            record.setUpdateBy(currentOperatorName());
            record.setUpdateTime(DateUtils.getNowDate());
            fillSnapshot(record, template, req.getFormData(), req.getAttachments());
            jstEnrollRecordMapper.insertJstEnrollRecord(record);
        } else {
            fillSnapshot(record, template, req.getFormData(), req.getAttachments());
            record.setUpdateBy(currentOperatorName());
            record.setUpdateTime(DateUtils.getNowDate());
            jstEnrollRecordMapper.updateJstEnrollRecord(record);
        }
        return buildResVO(record);
    }

    /**
     * 提交报名。
     * @param req 提交请求
     * @return 提交结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "ENROLL_SUBMIT", target = "#{req.enrollId}", recordResult = true)
    public EnrollSubmitResVO submit(EnrollSubmitDTO req) {
        // TX: 报名提交
        Long userId = currentUserId();
        assertParticipantOwned(req.getParticipantId(), userId);
        // LOCK: lock:enroll:submit:{userId}:{contestId}
        return jstLockTemplate.execute("lock:enroll:submit:" + userId + ":" + req.getContestId(), 3, 5, () -> {
            JstContest contest = requireContest(req.getContestId());
            assertContestSubmittable(contest);
            JstEnrollFormTemplate template = requireApprovedTemplate(contest.getFormTemplateId());
            validateRequiredFields(template, req.getFormData(), req.getAttachments());
            if (enrollRecordMapperExt.countDuplicatePending(req.getContestId(), req.getParticipantId(), userId, req.getEnrollId()) > 0) {
                throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_DUPLICATE_PENDING.message(),
                        BizErrorCode.JST_EVENT_ENROLL_DUPLICATE_PENDING.code());
            }
            JstEnrollRecord record = req.getEnrollId() == null
                    ? enrollRecordMapperExt.selectLatestDraft(req.getContestId(), req.getParticipantId(), userId)
                    : requireOwnedEnroll(req.getEnrollId(), userId);
            if (record != null) {
                assertSameContestAndParticipant(record, req.getContestId(), req.getParticipantId());
                assertDraftEditable(record);
            }
            if (record == null) {
                record = buildNewRecord(req.getContestId(), req.getParticipantId(), userId);
                record.setCreateBy(currentOperatorName());
                record.setCreateTime(DateUtils.getNowDate());
            }
            fillSnapshot(record, template, req.getFormData(), req.getAttachments());
            record.setMaterialStatus(EnrollMaterialStatus.SUBMITTED.dbValue());
            record.setAuditStatus(EnrollAuditStatus.PENDING.dbValue());
            record.setAuditRemark(null);
            record.setSubmitTime(DateUtils.getNowDate());
            record.setUpdateBy(currentOperatorName());
            record.setUpdateTime(DateUtils.getNowDate());
            if (record.getEnrollId() == null) {
                jstEnrollRecordMapper.insertJstEnrollRecord(record);
            } else {
                jstEnrollRecordMapper.updateJstEnrollRecord(record);
            }
            return buildResVO(record);
        });
    }

    /**
     * 查询小程序报名详情。
     * @param enrollId 报名 ID
     * @return 详情
     */
    @Override
    public EnrollDetailVO getWxDetail(Long enrollId) {
        Long userId = currentUserId();
        JstEnrollRecord record = requireOwnedEnroll(enrollId, userId);
        SecurityCheck.assertSameUser(record.getUserId());
        EnrollDetailVO vo = enrollRecordMapperExt.selectWxDetail(enrollId);
        if (vo == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.code());
        }
        return maskAndParseDetail(vo);
    }

    /**
     * 提交补件。
     * @param enrollId 报名 ID
     * @param req 补件请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "ENROLL_SUPPLEMENT", target = "#{enrollId}")
    public void supplement(Long enrollId, EnrollSupplementDTO req) {
        // TX: 补件提交
        JstEnrollRecord record = requireOwnedEnroll(enrollId, currentUserId());
        EnrollAuditStatus.fromDb(record.getAuditStatus()).assertCanTransitTo(EnrollAuditStatus.PENDING); // SM-6
        if (!EnrollMaterialStatus.SUPPLEMENT.dbValue().equals(record.getMaterialStatus())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.message(),
                    BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
        }
        JstContest contest = requireContest(record.getContestId());
        JstEnrollFormTemplate template = requireApprovedTemplate(contest.getFormTemplateId());
        validateRequiredFields(template, req.getFormData(), req.getAttachments());
        fillSnapshot(record, template, req.getFormData(), req.getAttachments());
        record.setAuditStatus(EnrollAuditStatus.PENDING.dbValue());
        record.setAuditRemark(null);
        record.setSubmitTime(DateUtils.getNowDate());
        record.setSupplementRemark(req.getSupplementRemark());
        record.setRemark(null);
        record.setUpdateBy(currentOperatorName());
        record.setUpdateTime(DateUtils.getNowDate());
        int updated = enrollRecordMapperExt.updateSupplementRecord(record);
        if (updated <= 0) {
            throw new ServiceException(BizErrorCode.JST_COMMON_DATA_TAMPERED.message(),
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    /**
     * 查询后台报名列表。
     * @param query 查询条件
     * @return 列表
     */
    @Override
    public List<EnrollListVO> selectAdminList(EnrollQueryReqDTO query) {
        if (query == null) query = new EnrollQueryReqDTO();
        if (isPartnerUser() && query.getPartnerId() == null) query.setPartnerId(JstLoginContext.currentPartnerId());
        List<EnrollListVO> list = enrollRecordMapperExt.selectAdminList(query);
        for (EnrollListVO vo : list) vo.setGuardianMobileMasked(MaskUtil.mobile(vo.getGuardianMobileMasked()));
        return list;
    }

    /**
     * 小程序-我的报名列表 (从 SecurityUtils 取当前 userId)
     */
    @Override
    public List<EnrollListVO> selectMyList(String auditStatus) {
        Long userId = com.ruoyi.common.utils.SecurityUtils.getUserId();
        if (userId == null) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
        List<EnrollListVO> list = enrollRecordMapperExt.selectMyList(userId, auditStatus);
        for (EnrollListVO vo : list) vo.setGuardianMobileMasked(MaskUtil.mobile(vo.getGuardianMobileMasked()));
        return list;
    }

    /**
     * 查询后台报名详情。
     * @param enrollId 报名 ID
     * @return 详情
     */
    @Override
    public EnrollDetailVO getAdminDetail(Long enrollId) {
        JstEnrollRecord record = requireEnroll(enrollId);
        assertEnrollPartnerOwnership(record);
        EnrollDetailVO vo = enrollRecordMapperExt.selectAdminDetail(enrollId);
        if (vo == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.code());
        }
        return maskAndParseDetail(vo);
    }

    /**
     * 审核报名记录。审核通过时可选传入各成绩项打分，自动写入成绩记录并生成证书编号。
     *
     * @param enrollId 报名 ID
     * @param req 审核请求（含可选 scores）
     * @关联表 jst_enroll_record, jst_score_record, jst_score_item, jst_cert_record
     * @关联状态机 SM-6
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "ENROLL_AUDIT", target = "#{enrollId}")
    public void audit(Long enrollId, EnrollAuditReqDTO req) {
        // TX: 报名审核 + 打分 + 证书编号生成
        // LOCK: lock:enroll:audit:{enrollId}
        jstLockTemplate.execute("lock:enroll:audit:" + enrollId, 3, 5, () -> {
            JstEnrollRecord record = requireEnroll(enrollId);
            assertEnrollPartnerOwnership(record);
            EnrollAuditStatus current = EnrollAuditStatus.fromDb(record.getAuditStatus());
            EnrollAuditStatus target = EnrollAuditStatus.fromDb(req.getResult());
            current.assertCanTransitTo(target); // SM-6
            String materialStatus = target == EnrollAuditStatus.SUPPLEMENT ? EnrollMaterialStatus.SUPPLEMENT.dbValue() : null;
            int updated = enrollRecordMapperExt.updateAuditByExpected(enrollId, current.dbValue(), target.dbValue(),
                    materialStatus, req.getAuditRemark(), currentOperatorName(), DateUtils.getNowDate());
            if (updated == 0) {
                throw new ServiceException("报名审核失败，状态已变更，请刷新后重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }
            // 审核通过 + 有打分 → 写入成绩 + 生成证书编号
            if (target == EnrollAuditStatus.APPROVED && req.getScores() != null && !req.getScores().isEmpty()) {
                handleScoreAndCert(record, req.getScores());
            }
            if (target == EnrollAuditStatus.APPROVED || target == EnrollAuditStatus.REJECTED) {
                JstContest contest = requireContest(record.getContestId());
                Map<String, Object> extraData = new LinkedHashMap<>();
                extraData.put("contestName", contest.getContestName());
                extraData.put("rejectReason", req.getAuditRemark());
                String bizType = target == EnrollAuditStatus.APPROVED ? "enroll_approved" : "enroll_rejected";
                publishAfterCommit(new EnrollAuditEvent(this, record.getUserId(), enrollId, bizType, extraData));
            }
            return null;
        });
    }

    /**
     * 批量审核报名记录。
     *
     * @param enrollIds   报名ID集合
     * @param auditStatus 目标审核状态
     * @param remark      审核备注
     * @param scores      各成绩项统一打分（批量通过时可选）
     * @return 成功处理条数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "ENROLL_BATCH_AUDIT", target = "#{enrollIds}")
    public int batchAudit(List<Long> enrollIds, String auditStatus, String remark, List<ScoreItemReqDTO> scores) {
        // TX: 批量审核整批成功或整批失败
        if (enrollIds == null || enrollIds.isEmpty()) {
            throw new ServiceException("报名ID列表不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        if (StringUtils.isBlank(auditStatus)) {
            throw new ServiceException("审核结果不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
        String normalizedRemark = StringUtils.trim(remark);
        if (StringUtils.isBlank(normalizedRemark)) {
            normalizedRemark = null;
        }
        if ("rejected".equals(auditStatus) && normalizedRemark == null) {
            throw new ServiceException("驳回时必须填写审核备注", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();
        for (Long enrollId : enrollIds) {
            if (enrollId != null) {
                uniqueIds.add(enrollId);
            }
        }
        if (uniqueIds.isEmpty()) {
            throw new ServiceException("报名ID列表不能为空", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }

        int count = 0;
        EnrollAuditReqDTO req = new EnrollAuditReqDTO();
        req.setResult(auditStatus);
        req.setAuditRemark(normalizedRemark);
        req.setScores(scores);
        for (Long enrollId : uniqueIds) {
            audit(enrollId, req);
            count++;
        }
        return count;
    }

    /**
     * 团队报名提交。
     * <p>
     * 1. 校验赛事支持团队报名（team_min_size > 0）
     * 2. 校验团队人数范围
     * 3. 校验手机号不重复
     * 4. 为队长和每个队员创建报名记录
     * 5. 如有 slotId，乐观锁扣减预约时段容量
     *
     * @param req 团队报名请求
     * @return 团队报名结果
     * @关联表 jst_enroll_record, jst_contest, jst_appointment_slot
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "TEAM_ENROLL_SUBMIT", target = "#{req.contestId}", recordResult = true)
    public TeamEnrollResVO submitTeam(TeamEnrollReqDTO req) {
        // TX: 团队报名提交
        Long userId = currentUserId();
        // LOCK: lock:enroll:team:{userId}:{contestId}
        return jstLockTemplate.execute("lock:enroll:team:" + userId + ":" + req.getContestId(), 3, 5, () -> {
            JstContest contest = requireContest(req.getContestId());
            assertContestSubmittable(contest);

            // 校验赛事支持团队报名
            if (contest.getTeamMinSize() == null || contest.getTeamMinSize() <= 0) {
                throw new ServiceException(BizErrorCode.JST_EVENT_TEAM_NOT_SUPPORTED.message(),
                        BizErrorCode.JST_EVENT_TEAM_NOT_SUPPORTED.code());
            }

            // 校验团队人数（队长 + 队员）
            int teamSize = 1 + req.getMembers().size();
            if (teamSize < contest.getTeamMinSize()) {
                throw new ServiceException("团队人数不足，最少需要 " + contest.getTeamMinSize() + " 人",
                        BizErrorCode.JST_EVENT_TEAM_SIZE_INVALID.code());
            }
            if (contest.getTeamMaxSize() != null && contest.getTeamMaxSize() > 0 && teamSize > contest.getTeamMaxSize()) {
                throw new ServiceException("团队人数超出，最多允许 " + contest.getTeamMaxSize() + " 人",
                        BizErrorCode.JST_EVENT_TEAM_SIZE_INVALID.code());
            }

            // 校验手机号不重复
            Set<String> phones = new LinkedHashSet<>();
            phones.add(req.getLeader().getPhone());
            for (TeamEnrollReqDTO.TeamMemberInfo member : req.getMembers()) {
                if (!phones.add(member.getPhone())) {
                    throw new ServiceException("团队成员手机号重复: " + member.getPhone(),
                            BizErrorCode.JST_EVENT_TEAM_PHONE_DUPLICATE.code());
                }
            }

            // 如有 slotId，乐观锁扣减预约时段容量
            if (req.getSlotId() != null) {
                JstAppointmentSlot slot = jstAppointmentSlotMapper.selectBySlotId(req.getSlotId());
                if (slot == null || !Objects.equals(slot.getContestId(), req.getContestId())) {
                    throw new ServiceException("预约时段不存在或不属于当前赛事",
                            BizErrorCode.JST_COMMON_PARAM_INVALID.code());
                }
                int updated = jstAppointmentSlotMapper.incrementBookedCount(req.getSlotId(), teamSize);
                if (updated == 0) {
                    throw new ServiceException(BizErrorCode.JST_EVENT_SLOT_CAPACITY_FULL.message(),
                            BizErrorCode.JST_EVENT_SLOT_CAPACITY_FULL.code());
                }
            }

            String operator = currentOperatorName();
            Date now = DateUtils.getNowDate();
            String teamGroupNo = snowflakeIdWorker.nextBizNo("TM");
            List<Long> enrollIds = new ArrayList<>();

            // 为队长创建报名记录
            JstEnrollRecord leaderRecord = buildTeamMemberRecord(req.getContestId(), userId,
                    req.getLeader(), teamGroupNo, "team_leader", operator, now);
            jstEnrollRecordMapper.insertJstEnrollRecord(leaderRecord);
            enrollIds.add(leaderRecord.getEnrollId());

            // 为每个队员创建报名记录
            for (TeamEnrollReqDTO.TeamMemberInfo member : req.getMembers()) {
                JstEnrollRecord memberRecord = buildTeamMemberRecord(req.getContestId(), userId,
                        member, teamGroupNo, "team_member", operator, now);
                jstEnrollRecordMapper.insertJstEnrollRecord(memberRecord);
                enrollIds.add(memberRecord.getEnrollId());
            }

            TeamEnrollResVO vo = new TeamEnrollResVO();
            vo.setLeaderEnrollId(leaderRecord.getEnrollId());
            vo.setLeaderEnrollNo(leaderRecord.getEnrollNo());
            vo.setEnrollIds(enrollIds);
            vo.setTeamSize(teamSize);
            return vo;
        });
    }

    /**
     * 构建团队成员报名记录。
     */
    private JstEnrollRecord buildTeamMemberRecord(Long contestId, Long userId,
                                                   TeamEnrollReqDTO.TeamMemberInfo member,
                                                   String teamGroupNo, String enrollSource,
                                                   String operator, Date now) {
        JstEnrollRecord record = new JstEnrollRecord();
        record.setEnrollNo(snowflakeIdWorker.nextBizNo("EN"));
        record.setContestId(contestId);
        record.setUserId(userId);
        record.setEnrollSource(enrollSource);
        record.setMaterialStatus(EnrollMaterialStatus.SUBMITTED.dbValue());
        record.setAuditStatus(EnrollAuditStatus.PENDING.dbValue());
        record.setSubmitTime(now);
        record.setDelFlag("0");
        record.setCreateBy(operator);
        record.setCreateTime(now);
        record.setUpdateBy(operator);
        record.setUpdateTime(now);
        // 将成员信息存入 form_snapshot_json
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("teamGroupNo", teamGroupNo);
        snapshot.put("enrollSource", enrollSource);
        Map<String, Object> formData = new LinkedHashMap<>();
        formData.put("name", member.getName());
        formData.put("phone", member.getPhone());
        if (StringUtils.isNotBlank(member.getIdCard())) {
            formData.put("idCard", member.getIdCard());
        }
        if (StringUtils.isNotBlank(member.getSchool())) {
            formData.put("school", member.getSchool());
        }
        snapshot.put("formData", formData);
        record.setFormSnapshotJson(JSON.toJSONString(snapshot));
        record.setRemark("团队报名[" + teamGroupNo + "]");
        return record;
    }

    /**
     * 审核通过时处理打分和证书编号生成。
     * <p>
     * 1. 校验每个 score item 属于该赛事且分数不超过满分
     * 2. 计算加权总分写入 jst_score_record
     * 3. 调用 BizNoGenerateService 生成证书编号写入 jst_cert_record
     *
     * @param record 报名记录
     * @param scores 各成绩项打分
     * @关联表 jst_score_item, jst_score_record, jst_cert_record
     */
    private void handleScoreAndCert(JstEnrollRecord record, List<ScoreItemReqDTO> scores) {
        Long contestId = record.getContestId();
        Long enrollId = record.getEnrollId();
        String operator = currentOperatorName();
        Date now = DateUtils.getNowDate();

        // 1. 加载赛事的成绩项定义
        List<JstScoreItem> items = jstScoreItemMapper.selectByContestId(contestId);
        Map<Long, JstScoreItem> itemMap = new HashMap<>();
        for (JstScoreItem item : items) {
            itemMap.put(item.getItemId(), item);
        }

        // 2. 校验并计算加权总分
        BigDecimal weightedTotal = BigDecimal.ZERO;
        List<Map<String, Object>> scoreDetails = new ArrayList<>();
        for (ScoreItemReqDTO s : scores) {
            JstScoreItem item = itemMap.get(s.getItemId());
            if (item == null) {
                throw new ServiceException("成绩项不存在: itemId=" + s.getItemId(),
                        BizErrorCode.JST_EVENT_SCORE_NOT_FOUND.code());
            }
            if (item.getMaxScore() != null && s.getScore().compareTo(item.getMaxScore()) > 0) {
                throw new ServiceException("成绩项 [" + item.getItemName() + "] 打分超过满分 " + item.getMaxScore(),
                        BizErrorCode.JST_COMMON_PARAM_INVALID.code());
            }
            BigDecimal weight = item.getWeight() != null ? item.getWeight() : BigDecimal.ONE;
            weightedTotal = weightedTotal.add(s.getScore().multiply(weight));
            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("itemId", s.getItemId());
            detail.put("itemName", item.getItemName());
            detail.put("score", s.getScore());
            detail.put("maxScore", item.getMaxScore());
            detail.put("weight", weight);
            scoreDetails.add(detail);
        }
        weightedTotal = weightedTotal.setScale(2, RoundingMode.HALF_UP);

        // 3. 写入成绩记录
        JstScoreRecord scoreRecord = new JstScoreRecord();
        scoreRecord.setContestId(contestId);
        scoreRecord.setEnrollId(enrollId);
        scoreRecord.setUserId(record.getUserId());
        scoreRecord.setParticipantId(record.getParticipantId());
        scoreRecord.setScoreValue(weightedTotal);
        scoreRecord.setAuditStatus("approved");
        scoreRecord.setPublishStatus("unpublished");
        scoreRecord.setRemark(JSON.toJSONString(scoreDetails));
        scoreRecord.setCreateBy(operator);
        scoreRecord.setCreateTime(now);
        scoreRecord.setUpdateBy(operator);
        scoreRecord.setUpdateTime(now);
        scoreRecord.setDelFlag("0");
        jstScoreRecordMapper.insertJstScoreRecord(scoreRecord);

        // 4. 生成证书编号并写入证书记录
        String certNo = bizNoGenerateService.nextNo("cert_no");
        JstCertRecord certRecord = new JstCertRecord();
        certRecord.setCertNo(certNo);
        certRecord.setContestId(contestId);
        certRecord.setScoreId(scoreRecord.getScoreId());
        certRecord.setEnrollId(enrollId);
        certRecord.setUserId(record.getUserId());
        certRecord.setParticipantId(record.getParticipantId());
        certRecord.setIssueStatus("pending");
        certRecord.setCreateBy(operator);
        certRecord.setCreateTime(now);
        certRecord.setUpdateBy(operator);
        certRecord.setUpdateTime(now);
        certRecord.setDelFlag("0");
        jstCertRecordMapper.insertJstCertRecord(certRecord);
    }

    private void publishAfterCommit(Object event) {
        if (event == null) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishEvent(event);
                }
            });
            return;
        }
        eventPublisher.publishEvent(event);
    }

    private JstEnrollRecord buildNewRecord(Long contestId, Long participantId, Long userId) {
        JstEnrollRecord record = new JstEnrollRecord();
        record.setEnrollNo(snowflakeIdWorker.nextBizNo("EN"));
        record.setContestId(contestId);
        record.setUserId(userId);
        record.setParticipantId(participantId);
        record.setEnrollSource("self");
        record.setDelFlag("0");
        return record;
    }

    private void fillSnapshot(JstEnrollRecord record, JstEnrollFormTemplate template, Map<String, Object> formData, List<String> attachments) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("template_id", template.getTemplateId());
        snapshot.put("template_version", toInteger(template.getTemplateVersion()));
        snapshot.put("formData", normalizeFormData(formData));
        snapshot.put("attachments", normalizeAttachments(attachments));
        record.setTemplateId(template.getTemplateId());
        record.setTemplateVersion(template.getTemplateVersion());
        record.setFormSnapshotJson(JSON.toJSONString(snapshot));
    }

    private void validateRequiredFields(JstEnrollFormTemplate template, Map<String, Object> formData, List<String> attachments) {
        Object schema = parseJson(template.getSchemaJson());
        List<String> missing = new ArrayList<>();
        collectMissingRequiredFields(schema, normalizeFormData(formData), normalizeAttachments(attachments), missing);
        if (!missing.isEmpty()) {
            throw new ServiceException("必填字段缺失: " + String.join("、", missing),
                    BizErrorCode.JST_EVENT_FORM_VALIDATION_FAIL.code());
        }
    }

    @SuppressWarnings("unchecked")
    private void collectMissingRequiredFields(Object schema, Map<String, Object> formData, List<String> attachments, List<String> missing) {
        if (schema instanceof Map<?, ?> map) {
            Object fields = map.get("fields");
            if (fields != null) {
                collectMissingRequiredFields(fields, formData, attachments, missing);
                return;
            }
            String key = stringValue(map.get("key"));
            String label = stringValue(map.get("label"));
            String type = stringValue(map.get("type"));
            if (isRequired(map.get("required")) && !hasFieldValue(key, type, formData, attachments)) {
                missing.add(StringUtils.isBlank(label) ? key : label);
            }
            return;
        }
        if (schema instanceof Collection<?> collection) {
            for (Object item : collection) collectMissingRequiredFields(item, formData, attachments, missing);
        }
    }

    private boolean hasFieldValue(String key, String type, Map<String, Object> formData, List<String> attachments) {
        Object value = key == null ? null : formData.get(key);
        if (hasValue(value)) return true;
        return type != null && UPLOAD_FIELD_TYPES.contains(type) && !attachments.isEmpty();
    }

    private boolean hasValue(Object value) {
        if (value == null) return false;
        if (value instanceof String text) return StringUtils.isNotBlank(text);
        if (value instanceof Collection<?> collection) return !collection.isEmpty();
        if (value instanceof Map<?, ?> map) return !map.isEmpty();
        if (value.getClass().isArray()) return Array.getLength(value) > 0;
        return true;
    }

    private boolean isRequired(Object value) {
        if (value instanceof Boolean bool) return bool;
        if (value instanceof String text) return "true".equalsIgnoreCase(text) || "1".equals(text);
        if (value instanceof Number number) return number.intValue() != 0;
        return false;
    }

    private JstEnrollRecord requireEnroll(Long enrollId) {
        JstEnrollRecord record = enrollRecordMapperExt.selectById(enrollId);
        if (record == null) throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.message(),
                BizErrorCode.JST_EVENT_ENROLL_NOT_FOUND.code());
        return record;
    }

    private JstEnrollRecord requireOwnedEnroll(Long enrollId, Long userId) {
        JstEnrollRecord record = requireEnroll(enrollId);
        if (!Objects.equals(record.getUserId(), userId)) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_NOT_OWN.message(),
                    BizErrorCode.JST_EVENT_ENROLL_NOT_OWN.code());
        }
        return record;
    }

    private void assertParticipantOwned(Long participantId, Long userId) {
        if (enrollRecordMapperExt.countOwnedParticipant(participantId, userId) <= 0) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_PARTICIPANT_NOT_OWN.message(),
                    BizErrorCode.JST_EVENT_ENROLL_PARTICIPANT_NOT_OWN.code());
        }
    }

    private void assertDraftEditable(JstEnrollRecord record) {
        if (!EnrollMaterialStatus.DRAFT.dbValue().equals(record.getMaterialStatus())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.message(),
                    BizErrorCode.JST_EVENT_ENROLL_ILLEGAL_TRANSIT.code());
        }
    }

    private void assertSameContestAndParticipant(JstEnrollRecord record, Long contestId, Long participantId) {
        if (!Objects.equals(record.getContestId(), contestId) || !Objects.equals(record.getParticipantId(), participantId)) {
            throw new ServiceException("报名记录与当前赛事/参赛人不匹配", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private JstContest requireContest(Long contestId) {
        JstContest contest = jstContestMapper.selectJstContestByContestId(contestId);
        if (contest == null || !"0".equals(defaultDelFlag(contest.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        return contest;
    }

    private JstEnrollFormTemplate requireApprovedTemplate(Long templateId) {
        if (templateId == null) throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.message(),
                BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.code());
        JstEnrollFormTemplate template = jstEnrollFormTemplateMapper.selectJstEnrollFormTemplateByTemplateId(templateId);
        if (template == null || !"0".equals(defaultDelFlag(template.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.code());
        }
        if (!"approved".equals(template.getAuditStatus()) || template.getStatus() == null || template.getStatus() != 1) {
            throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.message(),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
        }
        return template;
    }

    private void assertContestOnline(JstContest contest) {
        if (!ContestAuditStatus.ONLINE.dbValue().equals(contest.getAuditStatus())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_ONLINE.code());
        }
    }

    private void assertContestSubmittable(JstContest contest) {
        assertContestOnline(contest);
        Date now = DateUtils.getNowDate();
        boolean inWindow = contest.getEnrollStartTime() != null && contest.getEnrollEndTime() != null
                && !now.before(contest.getEnrollStartTime()) && !now.after(contest.getEnrollEndTime());
        if (!ContestBizStatus.ENROLLING.dbValue().equals(contest.getStatus()) || !inWindow) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_ENROLLING.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_ENROLLING.code());
        }
    }

    private void assertEnrollPartnerOwnership(JstEnrollRecord record) {
        if (!isPartnerUser()) return;
        Long partnerId = JstLoginContext.currentPartnerId();
        if (!Objects.equals(requireContest(record.getContestId()).getPartnerId(), partnerId)) {
            throw new ServiceException(BizErrorCode.JST_EVENT_ENROLL_NOT_OWN.message(),
                    BizErrorCode.JST_EVENT_ENROLL_NOT_OWN.code());
        }
    }

    private boolean isPartnerUser() {
        return JstLoginContext.currentPartnerId() != null
                && JstLoginContext.hasRole(ROLE_PARTNER)
                && !JstLoginContext.hasRole("jst_platform_op");
    }

    private Long currentUserId() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        return userId;
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return StringUtils.isBlank(username) ? "system" : username;
    }

    private EnrollSubmitResVO buildResVO(JstEnrollRecord record) {
        EnrollSubmitResVO vo = new EnrollSubmitResVO();
        vo.setEnrollId(record.getEnrollId());
        vo.setEnrollNo(record.getEnrollNo());
        return vo;
    }

    private EnrollDetailVO maskAndParseDetail(EnrollDetailVO vo) {
        vo.setGuardianMobileMasked(MaskUtil.mobile(vo.getGuardianMobileMasked()));
        vo.setFormSnapshotJson(parseJson(vo.getFormSnapshotJson()));
        return vo;
    }

    private Map<String, Object> normalizeFormData(Map<String, Object> formData) {
        return formData == null ? new LinkedHashMap<>() : new LinkedHashMap<>(formData);
    }

    private List<String> normalizeAttachments(List<String> attachments) {
        if (attachments == null || attachments.isEmpty()) return Collections.emptyList();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String attachment : attachments) if (StringUtils.isNotBlank(attachment)) set.add(attachment);
        return new ArrayList<>(set);
    }

    private Object parseJson(Object raw) {
        if (!(raw instanceof String text)) return raw;
        if (StringUtils.isBlank(text)) return null;
        try { return JSON.parse(text); } catch (Exception ex) { return text; }
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String defaultDelFlag(String delFlag) {
        return StringUtils.isBlank(delFlag) ? "0" : delFlag;
    }

    private Integer toInteger(Long value) {
        return value == null ? null : value.intValue();
    }
}
