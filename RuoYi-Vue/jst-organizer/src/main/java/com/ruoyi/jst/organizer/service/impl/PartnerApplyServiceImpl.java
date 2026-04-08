package com.ruoyi.jst.organizer.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.id.SnowflakeIdWorker;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.organizer.domain.JstEventPartnerApply;
import com.ruoyi.jst.organizer.dto.ApproveReqDTO;
import com.ruoyi.jst.organizer.dto.PartnerApplyQueryDTO;
import com.ruoyi.jst.organizer.dto.PartnerApplyReqDTO;
import com.ruoyi.jst.organizer.dto.RejectReqDTO;
import com.ruoyi.jst.organizer.dto.SupplementReqDTO;
import com.ruoyi.jst.organizer.enums.PartnerApplyStatus;
import com.ruoyi.jst.organizer.mapper.EventPartnerWriteMapper;
import com.ruoyi.jst.organizer.mapper.JstEventPartnerApplyMapper;
import com.ruoyi.jst.organizer.mapper.PartnerApplyMapperExt;
import com.ruoyi.jst.organizer.mapper.SysUserExtMapper;
import com.ruoyi.jst.organizer.service.PartnerApplyService;
import com.ruoyi.jst.organizer.vo.PartnerApplyApproveResVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyDetailVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyListVO;
import com.ruoyi.jst.organizer.vo.PartnerApplyStatusResVO;
import com.ruoyi.jst.organizer.vo.PartnerApplySubmitResVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 赛事方入驻申请服务实现
 * <p>
 * 负责公开提交申请、后台审核列表与详情、审核通过/驳回/补件等核心流程。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class PartnerApplyServiceImpl implements PartnerApplyService {

    private static final Logger log = LoggerFactory.getLogger(PartnerApplyServiceImpl.class);

    private static final String ROLE_KEY_PARTNER = "jst_partner";

    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    private static final int INITIAL_PASSWORD_LENGTH = 12;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Autowired
    private JstEventPartnerApplyMapper jstEventPartnerApplyMapper;

    @Autowired
    private PartnerApplyMapperExt partnerApplyMapperExt;

    @Autowired
    private SysUserExtMapper sysUserExtMapper;

    @Autowired
    private EventPartnerWriteMapper eventPartnerWriteMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 公开提交赛事方入驻申请。
     *
     * @param req 申请请求
     * @return 提交结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事方入驻", action = "APPLY_SUBMIT", recordParams = false, recordResult = true)
    public PartnerApplySubmitResVO submitPublicApply(PartnerApplyReqDTO req) {
        // TX: 公开提交申请需在一个事务内完成重复校验与申请单写入
        long duplicateCount = partnerApplyMapperExt.countDuplicateActiveByMobile(req.getContactMobile());
        if (duplicateCount > 0) {
            throw new ServiceException(BizErrorCode.JST_ORG_APPLY_DUPLICATE.message(),
                    BizErrorCode.JST_ORG_APPLY_DUPLICATE.code());
        }

        Date now = DateUtils.getNowDate();
        String applyNo = snowflakeIdWorker.nextBizNo("PA");

        JstEventPartnerApply apply = new JstEventPartnerApply();
        apply.setApplyNo(applyNo);
        apply.setPartnerName(req.getPartnerName());
        apply.setContactName(req.getContactName());
        apply.setContactMobile(req.getContactMobile());
        apply.setBusinessLicenseNo(req.getBusinessLicenseNo());
        apply.setQualificationJson(req.getQualificationJson());
        apply.setSettlementInfoJson(req.getSettlementInfoJson());
        apply.setInvoiceInfoJson(req.getInvoiceInfoJson());
        apply.setContractFilesJson(req.getContractFilesJson());
        apply.setApplyStatus(PartnerApplyStatus.PENDING.dbValue()); // SM-4: 公开提交直接生成 pending 申请单
        apply.setSubmitTime(now);
        apply.setCreateBy("public_apply");
        apply.setCreateTime(now);

        jstEventPartnerApplyMapper.insertJstEventPartnerApply(apply);

        registerAfterCommit(() -> log.info(
                "[PartnerApply] 申请已提交 applyId={} applyNo={} mobile={}",
                apply.getApplyId(), apply.getApplyNo(), MaskUtil.mobile(req.getContactMobile())));

        PartnerApplySubmitResVO vo = new PartnerApplySubmitResVO();
        vo.setApplyId(apply.getApplyId());
        vo.setApplyNo(apply.getApplyNo());
        return vo;
    }

    /**
     * 按申请单号查询公开申请状态。
     *
     * @param applyNo 申请单号
     * @return 状态结果
     */
    @Override
    public PartnerApplyStatusResVO queryPublicStatus(String applyNo) {
        JstEventPartnerApply apply = getRequiredApplyByNo(applyNo);
        PartnerApplyStatusResVO vo = new PartnerApplyStatusResVO();
        vo.setApplyStatus(apply.getApplyStatus());
        vo.setAuditRemark(resolvePublicRemark(apply));
        vo.setSubmitTime(apply.getSubmitTime());
        vo.setAuditTime(apply.getAuditTime());
        return vo;
    }

    /**
     * 查询后台审核列表。
     *
     * @param query 查询条件
     * @return 列表结果
     */
    @Override
    public List<PartnerApplyListVO> selectAdminList(PartnerApplyQueryDTO query) {
        List<PartnerApplyListVO> list = partnerApplyMapperExt.selectPartnerApplyList(query);
        for (PartnerApplyListVO item : list) {
            item.setContactMobile(MaskUtil.mobile(item.getContactMobile()));
        }
        return list;
    }

    /**
     * 查询后台审核详情。
     *
     * @param applyId 申请ID
     * @return 详情结果
     */
    @Override
    public PartnerApplyDetailVO getAdminDetail(Long applyId) {
        return buildDetailVo(getRequiredApply(applyId));
    }

    /**
     * 审核通过并创建赛事方账号。
     *
     * @param applyId 申请ID
     * @param req     审核通过请求
     * @return 审核通过结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事方入驻", action = "APPLY_APPROVE", target = "#{applyId}", recordResult = true)
    public PartnerApplyApproveResVO approve(Long applyId, ApproveReqDTO req) {
        // TX: 审核通过需在一个大事务内完成建档、建账号、绑角色、状态推进
        return jstLockTemplate.execute("lock:org:apply:" + applyId, 3, 10, () -> { // LOCK: lock:org:apply:{applyId}
            JstEventPartnerApply apply = getRequiredApply(applyId);
            PartnerApplyStatus currentStatus = PartnerApplyStatus.fromDb(apply.getApplyStatus());
            // SM-4
            currentStatus.assertCanTransitTo(PartnerApplyStatus.APPROVED);

            ensureUsernameUnique(req.getUsername());
            Long roleId = getRequiredPartnerRoleId();
            Date now = DateUtils.getNowDate();
            String operatorName = currentOperatorName();
            Long operatorId = currentOperatorId();

            Map<String, Object> partner = buildPartnerInsertData(apply, req, now, operatorName);
            int insertPartnerRows = eventPartnerWriteMapper.insertEventPartner(partner);
            Long partnerId = extractPartnerId(partner.get("partnerId"));
            if (insertPartnerRows <= 0 || partnerId == null) {
                throw new ServiceException("创建赛事方档案失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            String initialPassword = generateInitialPassword();
            SysUser sysUser = buildPartnerSysUser(apply, req, now, operatorName, initialPassword);
            int insertUserRows = sysUserExtMapper.insertSysUser(sysUser);
            if (insertUserRows <= 0 || sysUser.getUserId() == null) {
                throw new ServiceException("创建赛事方账号失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            int insertUserRoleRows = sysUserExtMapper.insertUserRole(sysUser.getUserId(), roleId);
            if (insertUserRoleRows <= 0) {
                throw new ServiceException("绑定赛事方角色失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            int updatePartnerRows = eventPartnerWriteMapper.updatePartnerUserId(
                    partnerId,
                    sysUser.getUserId(),
                    operatorName,
                    now
            );
            if (updatePartnerRows <= 0) {
                throw new ServiceException("回填赛事方账号失败", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            int updateApplyRows = partnerApplyMapperExt.updateApplyStatusByExpectedStatus(
                    applyId,
                    apply.getApplyStatus(),
                    PartnerApplyStatus.APPROVED.dbValue(),
                    req.getAuditRemark(),
                    null,
                    operatorId,
                    now,
                    partnerId,
                    operatorName
            );
            if (updateApplyRows == 0) {
                throw new ServiceException("审核冲突：申请状态已变更，请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            registerAfterCommit(() -> log.info(
                    "[PartnerApply] 审核通过 applyId={} partnerId={} userId={} username={} initPassword={} mobile={}",
                    applyId, partnerId, sysUser.getUserId(), sysUser.getUserName(),
                    initialPassword, MaskUtil.mobile(apply.getContactMobile())));

            PartnerApplyApproveResVO vo = new PartnerApplyApproveResVO();
            vo.setPartnerId(partnerId);
            vo.setUserId(sysUser.getUserId());
            return vo;
        });
    }

    /**
     * 驳回赛事方入驻申请。
     *
     * @param applyId 申请ID
     * @param req     驳回请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事方入驻", action = "APPLY_REJECT", target = "#{applyId}")
    public void reject(Long applyId, RejectReqDTO req) {
        // TX: 驳回操作需在一个事务内完成状态推进与审核信息写入
        jstLockTemplate.execute("lock:org:apply:" + applyId, 3, 10, () -> { // LOCK: lock:org:apply:{applyId}
            JstEventPartnerApply apply = getRequiredApply(applyId);
            PartnerApplyStatus currentStatus = PartnerApplyStatus.fromDb(apply.getApplyStatus());
            // SM-4
            currentStatus.assertCanTransitTo(PartnerApplyStatus.REJECTED);

            int updated = partnerApplyMapperExt.updateApplyStatusByExpectedStatus(
                    applyId,
                    apply.getApplyStatus(),
                    PartnerApplyStatus.REJECTED.dbValue(),
                    req.getAuditRemark(),
                    null,
                    currentOperatorId(),
                    DateUtils.getNowDate(),
                    null,
                    currentOperatorName()
            );
            if (updated == 0) {
                throw new ServiceException("驳回冲突：申请状态已变更，请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            registerAfterCommit(() -> log.info(
                    "[PartnerApply] 审核驳回 applyId={} applyNo={} mobile={} remark={}",
                    applyId, apply.getApplyNo(), MaskUtil.mobile(apply.getContactMobile()), req.getAuditRemark()));
            return null;
        });
    }

    /**
     * 要求赛事方补充材料。
     *
     * @param applyId 申请ID
     * @param req     补件请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事方入驻", action = "APPLY_SUPPLEMENT", target = "#{applyId}")
    public void supplement(Long applyId, SupplementReqDTO req) {
        // TX: 补件操作需在一个事务内完成状态推进与补件备注写入
        jstLockTemplate.execute("lock:org:apply:" + applyId, 3, 10, () -> { // LOCK: lock:org:apply:{applyId}
            JstEventPartnerApply apply = getRequiredApply(applyId);
            PartnerApplyStatus currentStatus = PartnerApplyStatus.fromDb(apply.getApplyStatus());
            // SM-4
            currentStatus.assertCanTransitTo(PartnerApplyStatus.SUPPLEMENT);

            int updated = partnerApplyMapperExt.updateApplyStatusByExpectedStatus(
                    applyId,
                    apply.getApplyStatus(),
                    PartnerApplyStatus.SUPPLEMENT.dbValue(),
                    null,
                    req.getSupplementRemark(),
                    currentOperatorId(),
                    DateUtils.getNowDate(),
                    null,
                    currentOperatorName()
            );
            if (updated == 0) {
                throw new ServiceException("补件冲突：申请状态已变更，请刷新重试",
                        BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
            }

            registerAfterCommit(() -> log.info(
                    "[PartnerApply] 要求补件 applyId={} applyNo={} mobile={} remark={}",
                    applyId, apply.getApplyNo(), MaskUtil.mobile(apply.getContactMobile()), req.getSupplementRemark()));
            return null;
        });
    }

    private JstEventPartnerApply getRequiredApply(Long applyId) {
        JstEventPartnerApply apply = partnerApplyMapperExt.selectByApplyId(applyId);
        if (apply == null) {
            throw new ServiceException(BizErrorCode.JST_ORG_APPLY_NOT_FOUND.message(),
                    BizErrorCode.JST_ORG_APPLY_NOT_FOUND.code());
        }
        return apply;
    }

    private JstEventPartnerApply getRequiredApplyByNo(String applyNo) {
        JstEventPartnerApply apply = partnerApplyMapperExt.selectByApplyNo(applyNo);
        if (apply == null) {
            throw new ServiceException(BizErrorCode.JST_ORG_APPLY_NOT_FOUND.message(),
                    BizErrorCode.JST_ORG_APPLY_NOT_FOUND.code());
        }
        return apply;
    }

    private PartnerApplyDetailVO buildDetailVo(JstEventPartnerApply apply) {
        PartnerApplyDetailVO vo = new PartnerApplyDetailVO();
        vo.setApplyId(apply.getApplyId());
        vo.setApplyNo(apply.getApplyNo());
        vo.setPartnerId(apply.getPartnerId());
        vo.setPartnerName(apply.getPartnerName());
        vo.setContactName(apply.getContactName());
        vo.setContactMobile(MaskUtil.mobile(apply.getContactMobile()));
        vo.setBusinessLicenseNo(apply.getBusinessLicenseNo());
        vo.setQualificationJson(parseJsonValue(apply.getQualificationJson()));
        vo.setSettlementInfoJson(parseSettlementInfo(apply.getSettlementInfoJson()));
        vo.setInvoiceInfoJson(parseJsonValue(apply.getInvoiceInfoJson()));
        vo.setContractFilesJson(parseJsonValue(apply.getContractFilesJson()));
        vo.setApplyStatus(apply.getApplyStatus());
        vo.setSupplementRemark(apply.getSupplementRemark());
        vo.setAuditRemark(apply.getAuditRemark());
        vo.setAuditUserId(apply.getAuditUserId());
        vo.setSubmitTime(apply.getSubmitTime());
        vo.setAuditTime(apply.getAuditTime());
        return vo;
    }

    private Object parseJsonValue(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return JSON.parse(json);
        } catch (Exception ex) {
            log.warn("[PartnerApply] JSON 反序列化失败，按原字符串返回");
            return json;
        }
    }

    private Object parseSettlementInfo(String json) {
        Object parsed = parseJsonValue(json);
        maskSensitiveJson(parsed);
        return parsed;
    }

    private String resolvePublicRemark(JstEventPartnerApply apply) {
        if (PartnerApplyStatus.SUPPLEMENT.dbValue().equals(apply.getApplyStatus())
                && apply.getSupplementRemark() != null && !apply.getSupplementRemark().isBlank()) {
            return apply.getSupplementRemark();
        }
        return apply.getAuditRemark();
    }

    private void maskSensitiveJson(Object value) {
        if (value instanceof JSONObject jsonObject) {
            for (String key : jsonObject.keySet()) {
                Object child = jsonObject.get(key);
                if ("bankAccountNo".equals(key) || "bank_account_no".equals(key)) {
                    if (child instanceof String accountNo) {
                        jsonObject.put(key, MaskUtil.bankAccount(accountNo));
                    }
                    continue;
                }
                maskSensitiveJson(child);
            }
            return;
        }
        if (value instanceof JSONArray jsonArray) {
            for (Object item : jsonArray) {
                maskSensitiveJson(item);
            }
        }
    }

    private void ensureUsernameUnique(String username) {
        Long existedUserId = sysUserExtMapper.selectUserIdByUserName(username);
        if (existedUserId != null) {
            throw new ServiceException("用户名已存在", BizErrorCode.JST_COMMON_PARAM_INVALID.code());
        }
    }

    private Long getRequiredPartnerRoleId() {
        Long roleId = sysUserExtMapper.selectRoleIdByRoleKey(ROLE_KEY_PARTNER);
        if (roleId == null) {
            throw new ServiceException("未找到赛事方角色 jst_partner", BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return roleId;
    }

    private Map<String, Object> buildPartnerInsertData(JstEventPartnerApply apply, ApproveReqDTO req,
                                                       Date now, String operatorName) {
        Map<String, Object> partner = new LinkedHashMap<>();
        partner.put("partnerName", apply.getPartnerName());
        partner.put("contactName", apply.getContactName());
        partner.put("contactMobile", apply.getContactMobile());
        partner.put("businessLicenseNo", apply.getBusinessLicenseNo());
        partner.put("qualificationJson", apply.getQualificationJson());
        partner.put("settlementInfoJson", apply.getSettlementInfoJson());
        partner.put("invoiceInfoJson", apply.getInvoiceInfoJson());
        partner.put("contractFilesJson", apply.getContractFilesJson());
        partner.put("auditStatus", PartnerApplyStatus.APPROVED.dbValue());
        partner.put("auditTime", now);
        partner.put("auditRemark", req.getAuditRemark());
        partner.put("accountStatus", 1);
        partner.put("coopStatus", "active");
        partner.put("createBy", operatorName);
        partner.put("createTime", now);
        partner.put("delFlag", "0");
        return partner;
    }

    private SysUser buildPartnerSysUser(JstEventPartnerApply apply, ApproveReqDTO req,
                                        Date now, String operatorName, String initialPassword) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(req.getUsername());
        sysUser.setNickName(truncate(apply.getPartnerName(), 30));
        sysUser.setEmail(req.getEmail());
        sysUser.setPhonenumber(apply.getContactMobile());
        sysUser.setPassword(SecurityUtils.encryptPassword(initialPassword));
        sysUser.setStatus("0");
        sysUser.setPwdUpdateDate(now);
        sysUser.setCreateBy(operatorName);
        sysUser.setRemark("赛事方入驻审核自动创建");
        return sysUser;
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return username == null || username.isBlank() ? "system" : username;
    }

    private Long currentOperatorId() {
        return SecurityUtils.getUserId();
    }

    private void registerAfterCommit(Runnable runnable) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    runnable.run();
                }
            });
            return;
        }
        runnable.run();
    }

    private String generateInitialPassword() {
        StringBuilder builder = new StringBuilder(INITIAL_PASSWORD_LENGTH);
        for (int i = 0; i < INITIAL_PASSWORD_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(PASSWORD_CHARS.length());
            builder.append(PASSWORD_CHARS.charAt(index));
        }
        return builder.toString();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private Long extractPartnerId(Object partnerId) {
        if (partnerId instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
