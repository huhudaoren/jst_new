package com.ruoyi.jst.event.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.jst.common.audit.OperateLog;
import com.ruoyi.jst.common.context.JstLoginContext;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.lock.JstLockTemplate;
import com.ruoyi.jst.common.security.SecurityCheck;
import com.ruoyi.jst.event.domain.JstContest;
import com.ruoyi.jst.event.domain.JstEnrollFormTemplate;
import com.ruoyi.jst.event.dto.AuditReqDTO;
import com.ruoyi.jst.event.dto.FormTemplateQueryReqDTO;
import com.ruoyi.jst.event.dto.FormTemplateSaveReqDTO;
import com.ruoyi.jst.event.enums.FormTemplateAuditStatus;
import com.ruoyi.jst.event.mapper.FormTemplateMapperExt;
import com.ruoyi.jst.event.mapper.JstContestMapper;
import com.ruoyi.jst.event.mapper.JstEnrollFormTemplateMapper;
import com.ruoyi.jst.event.service.FormTemplateService;
import com.ruoyi.jst.event.vo.FormTemplateDetailVO;
import com.ruoyi.jst.event.vo.FormTemplateListVO;
import com.ruoyi.jst.event.vo.WxFormTemplateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 动态表单模板领域服务实现。
 * <p>
 * 负责模板保存、提审、审核和小程序报名模板读取。
 *
 * @author jst
 * @since 1.0.0
 */
@Service
public class FormTemplateServiceImpl implements FormTemplateService {

    private static final Logger log = LoggerFactory.getLogger(FormTemplateServiceImpl.class);

    private static final Set<String> ALLOWED_FIELD_TYPES = Set.of(
            "text", "textarea", "radio", "checkbox", "select", "date", "age",
            "number", "image", "audio", "video", "file", "group", "conditional"
    );

    @Autowired
    private JstEnrollFormTemplateMapper jstEnrollFormTemplateMapper;

    @Autowired
    private FormTemplateMapperExt formTemplateMapperExt;

    @Autowired
    private JstContestMapper jstContestMapper;

    @Autowired
    private JstLockTemplate jstLockTemplate;

    /**
     * 创建或更新动态表单模板。
     *
     * @param req 保存请求
     * @return 模板ID
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "FORM_TEMPLATE_SAVE", target = "#{req.templateId}", recordResult = true)
    public Long save(FormTemplateSaveReqDTO req) {
        // TX: FormTemplateService.save
        String normalizedSchema = validateAndNormalizeSchema(req.getSchemaJson());
        Long lockTarget = req.getTemplateId() == null ? 0L : req.getTemplateId();
        return jstLockTemplate.execute(lockKey(lockTarget), 3, 5, () -> {
            // LOCK: lock:form:template:save:{templateId}
            if (req.getTemplateId() == null) {
                return createTemplate(req, normalizedSchema);
            }
            return updateTemplate(req, normalizedSchema);
        });
    }

    /**
     * 提交模板审核。
     *
     * @param templateId 模板ID
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "FORM_TEMPLATE_SUBMIT", target = "#{templateId}")
    public void submit(Long templateId) {
        // TX: FormTemplateService.submit
        JstEnrollFormTemplate template = requireTemplate(templateId);
        assertTemplateOwnership(template);

        // SM-25
        FormTemplateAuditStatus currentStatus = FormTemplateAuditStatus.fromDb(template.getAuditStatus());
        currentStatus.assertCanTransitTo(FormTemplateAuditStatus.PENDING);

        int updated = formTemplateMapperExt.updateAuditStatusByExpected(
                templateId,
                template.getAuditStatus(),
                FormTemplateAuditStatus.PENDING.dbValue(),
                template.getEffectiveTime(),
                currentOperatorName(),
                DateUtils.getNowDate()
        );
        if (updated == 0) {
            throw new ServiceException("提交审核失败，模板状态已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
    }

    /**
     * 审核通过模板。
     *
     * @param templateId 模板ID
     * @param req        审核请求
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:audit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "FORM_TEMPLATE_APPROVE", target = "#{templateId}")
    public void approve(Long templateId, AuditReqDTO req) {
        // TX: FormTemplateService.approve
        SecurityCheck.assertAdmin();
        JstEnrollFormTemplate template = requireTemplate(templateId);

        // SM-25
        FormTemplateAuditStatus currentStatus = FormTemplateAuditStatus.fromDb(template.getAuditStatus());
        currentStatus.assertCanTransitTo(FormTemplateAuditStatus.APPROVED);

        int updated = formTemplateMapperExt.updateAuditStatusByExpected(
                templateId,
                template.getAuditStatus(),
                FormTemplateAuditStatus.APPROVED.dbValue(),
                DateUtils.getNowDate(),
                currentOperatorName(),
                DateUtils.getNowDate()
        );
        if (updated == 0) {
            throw new ServiceException("审核通过失败，模板状态已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        log.info("[FormTemplateApprove] templateId={} remark={}", templateId, req.getAuditRemark());
    }

    /**
     * 审核驳回模板。
     *
     * @param templateId 模板ID
     * @param req        审核请求
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:audit
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(module = "赛事", action = "FORM_TEMPLATE_REJECT", target = "#{templateId}")
    public void reject(Long templateId, AuditReqDTO req) {
        // TX: FormTemplateService.reject
        SecurityCheck.assertAdmin();
        JstEnrollFormTemplate template = requireTemplate(templateId);

        // SM-25
        FormTemplateAuditStatus currentStatus = FormTemplateAuditStatus.fromDb(template.getAuditStatus());
        currentStatus.assertCanTransitTo(FormTemplateAuditStatus.REJECTED);

        int updated = formTemplateMapperExt.updateAuditStatusByExpected(
                templateId,
                template.getAuditStatus(),
                FormTemplateAuditStatus.REJECTED.dbValue(),
                null,
                currentOperatorName(),
                DateUtils.getNowDate()
        );
        if (updated == 0) {
            throw new ServiceException("审核驳回失败，模板状态已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        log.info("[FormTemplateReject] templateId={} remark={}", templateId, req.getAuditRemark());
    }

    /**
     * 查询后台模板列表。
     *
     * @param query 查询条件
     * @return 列表结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit / jst:event:formTemplate:audit
     */
    @Override
    public List<FormTemplateListVO> selectList(FormTemplateQueryReqDTO query) {
        if (query == null) {
            query = new FormTemplateQueryReqDTO();
        }
        if (isPartnerUser()) {
            query.setOwnerType("partner");
            if (query.getOwnerId() == null) {
                query.setOwnerId(JstLoginContext.currentPartnerId());
            }
        }
        return formTemplateMapperExt.selectFormTemplateList(query);
    }

    /**
     * 查询后台模板详情。
     *
     * @param templateId 模板ID
     * @return 详情结果
     * @关联表 jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 jst:event:formTemplate:edit / jst:event:formTemplate:audit
     */
    @Override
    public FormTemplateDetailVO getDetail(Long templateId) {
        JstEnrollFormTemplate template = requireTemplate(templateId);
        assertTemplateOwnership(template);
        return buildDetailVO(template);
    }

    /**
     * 小程序按赛事查询报名模板。
     *
     * @param contestId 赛事ID
     * @return 模板结果
     * @关联表 jst_contest / jst_enroll_form_template
     * @关联状态机 SM-25
     * @关联权限 @Anonymous
     */
    @Override
    public WxFormTemplateVO getWxTemplate(Long contestId) {
        JstContest contest = requireContest(contestId);
        if (!"online".equals(contest.getAuditStatus())) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        if (contest.getFormTemplateId() == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.code());
        }

        JstEnrollFormTemplate template = requireTemplate(contest.getFormTemplateId());
        if (!FormTemplateAuditStatus.APPROVED.dbValue().equals(template.getAuditStatus())
                || template.getStatus() == null
                || template.getStatus() != 1) {
            throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.code());
        }

        WxFormTemplateVO vo = new WxFormTemplateVO();
        vo.setTemplateId(template.getTemplateId());
        vo.setTemplateVersion(toInteger(template.getTemplateVersion()));
        vo.setSchemaJson(parseJsonObject(template.getSchemaJson()));
        return vo;
    }

    private Long createTemplate(FormTemplateSaveReqDTO req, String normalizedSchema) {
        Long ownerId = resolveOwnerId(req.getOwnerType());
        Date now = DateUtils.getNowDate();

        JstEnrollFormTemplate template = new JstEnrollFormTemplate();
        template.setTemplateName(req.getTemplateName());
        template.setTemplateVersion(1L);
        template.setOwnerType(req.getOwnerType());
        template.setOwnerId(ownerId);
        template.setSchemaJson(normalizedSchema);
        template.setAuditStatus(FormTemplateAuditStatus.DRAFT.dbValue());
        template.setStatus(1);
        template.setCreateBy(currentOperatorName());
        template.setCreateTime(now);
        template.setUpdateBy(currentOperatorName());
        template.setUpdateTime(now);
        template.setDelFlag("0");
        jstEnrollFormTemplateMapper.insertJstEnrollFormTemplate(template);
        return template.getTemplateId();
    }

    private Long updateTemplate(FormTemplateSaveReqDTO req, String normalizedSchema) {
        JstEnrollFormTemplate template = requireTemplate(req.getTemplateId());
        assertTemplateOwnership(template);

        Long expectedOwnerId = resolveOwnerId(req.getOwnerType());
        if (!Objects.equals(template.getOwnerType(), req.getOwnerType())
                || !Objects.equals(template.getOwnerId(), expectedOwnerId)) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }

        // SM-25
        FormTemplateAuditStatus currentStatus = FormTemplateAuditStatus.fromDb(template.getAuditStatus());
        currentStatus.assertCanTransitTo(FormTemplateAuditStatus.DRAFT);

        template.setTemplateName(req.getTemplateName());
        template.setSchemaJson(normalizedSchema);
        template.setTemplateVersion(nextVersion(template.getTemplateVersion()));
        template.setAuditStatus(FormTemplateAuditStatus.DRAFT.dbValue());
        template.setEffectiveTime(null);
        template.setUpdateBy(currentOperatorName());
        template.setUpdateTime(DateUtils.getNowDate());

        int updated = formTemplateMapperExt.updateAfterSave(template, currentStatus.dbValue());
        if (updated == 0) {
            throw new ServiceException("模板保存失败，状态已变更",
                    BizErrorCode.JST_COMMON_DATA_TAMPERED.code());
        }
        return template.getTemplateId();
    }

    private String validateAndNormalizeSchema(Object schemaJson) {
        if (schemaJson instanceof List<?> fields) {
            validateFields(fields);
            return JSON.toJSONString(schemaJson);
        }
        if (schemaJson instanceof Map<?, ?> map) {
            Object fields = map.get("fields");
            if (!(fields instanceof List<?> fieldList)) {
                throw new ServiceException("schemaJson 顶层 fields 必须是数组",
                        BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
            }
            validateFields(fieldList);
            return JSON.toJSONString(schemaJson);
        }
        throw new ServiceException("schemaJson 顶层必须是 fields 对象或数组",
                BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
    }

    private void validateFields(List<?> fields) {
        for (Object item : fields) {
            if (!(item instanceof Map<?, ?> fieldMap)) {
                throw new ServiceException("schemaJson 字段项必须为对象",
                        BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
            }
            validateField(fieldMap);
        }
    }

    private void validateField(Map<?, ?> fieldMap) {
        String key = stringValue(fieldMap.get("key"));
        String type = stringValue(fieldMap.get("type"));
        String label = stringValue(fieldMap.get("label"));
        Object required = fieldMap.get("required");

        if (key == null || key.isBlank()
                || type == null || type.isBlank()
                || label == null || label.isBlank()
                || required == null) {
            throw new ServiceException("schemaJson 字段缺少 key/type/label/required",
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
        }
        if (!ALLOWED_FIELD_TYPES.contains(type)) {
            throw new ServiceException("schemaJson 字段类型非法: " + type,
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
        }

        Object children = fieldMap.get("fields");
        if (children != null) {
            if (!(children instanceof List<?> childFields)) {
                throw new ServiceException("schemaJson 子字段 fields 必须是数组",
                        BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
            }
            validateFields(childFields);
        }
    }

    private JstEnrollFormTemplate requireTemplate(Long templateId) {
        JstEnrollFormTemplate template = jstEnrollFormTemplateMapper.selectJstEnrollFormTemplateByTemplateId(templateId);
        if (template == null || !"0".equals(defaultDelFlag(template.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_FORM_TEMPLATE_NOT_FOUND.code());
        }
        return template;
    }

    private JstContest requireContest(Long contestId) {
        JstContest contest = jstContestMapper.selectJstContestByContestId(contestId);
        if (contest == null || !"0".equals(defaultDelFlag(contest.getDelFlag()))) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CONTEST_NOT_FOUND.code());
        }
        return contest;
    }

    private void assertTemplateOwnership(JstEnrollFormTemplate template) {
        if (!isPartnerUser()) {
            return;
        }
        if (!"partner".equals(template.getOwnerType())
                || !Objects.equals(template.getOwnerId(), JstLoginContext.currentPartnerId())) {
            throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                    BizErrorCode.JST_COMMON_AUTH_DENIED.code());
        }
    }

    private boolean isPartnerUser() {
        return JstLoginContext.currentPartnerId() != null
                && JstLoginContext.hasRole("jst_partner")
                && !JstLoginContext.hasRole("jst_platform_op");
    }

    private Long resolveOwnerId(String ownerType) {
        Long currentPartnerId = JstLoginContext.currentPartnerId();
        if ("partner".equals(ownerType)) {
            if (currentPartnerId == null) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }
            return currentPartnerId;
        }
        if ("platform".equals(ownerType)) {
            if (isPartnerUser()) {
                throw new ServiceException(BizErrorCode.JST_COMMON_AUTH_DENIED.message(),
                        BizErrorCode.JST_COMMON_AUTH_DENIED.code());
            }
            return null;
        }
        throw new ServiceException(BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.message(),
                BizErrorCode.JST_EVENT_FORM_TEMPLATE_INVALID.code());
    }

    private FormTemplateDetailVO buildDetailVO(JstEnrollFormTemplate template) {
        FormTemplateDetailVO vo = new FormTemplateDetailVO();
        vo.setTemplateId(template.getTemplateId());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateVersion(toInteger(template.getTemplateVersion()));
        vo.setOwnerType(template.getOwnerType());
        vo.setOwnerId(template.getOwnerId());
        vo.setSchemaJson(parseJsonObject(template.getSchemaJson()));
        vo.setAuditStatus(template.getAuditStatus());
        vo.setStatus(template.getStatus());
        vo.setEffectiveTime(template.getEffectiveTime());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }

    private Object parseJsonObject(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return JSON.parse(json);
        } catch (Exception ex) {
            log.warn("[FormTemplate] schema_json 反序列化失败");
            return json;
        }
    }

    private String lockKey(Long templateId) {
        return "lock:form:template:save:" + templateId;
    }

    private String currentOperatorName() {
        String username = SecurityUtils.getUsername();
        return username == null || username.isBlank() ? "system" : username;
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String defaultDelFlag(String delFlag) {
        return delFlag == null || delFlag.isBlank() ? "0" : delFlag;
    }

    private Long nextVersion(Long currentVersion) {
        long base = currentVersion == null ? 0L : currentVersion;
        return base + 1;
    }

    private Integer toInteger(Long value) {
        return value == null ? null : value.intValue();
    }
}
