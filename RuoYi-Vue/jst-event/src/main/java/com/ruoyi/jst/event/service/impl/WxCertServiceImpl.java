package com.ruoyi.jst.event.service.impl;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.exception.BizErrorCode;
import com.ruoyi.jst.common.support.SchemaInspector;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.event.dto.PublicCertVerifyQueryDTO;
import com.ruoyi.jst.event.mapper.WxCertMapper;
import com.ruoyi.jst.event.service.WxCertService;
import com.ruoyi.jst.event.vo.PublicCertVerifyVO;
import com.ruoyi.jst.event.vo.WxCertDetailVO;
import com.ruoyi.jst.event.vo.WxCertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mini-program certificate service implementation.
 */
@Service
public class WxCertServiceImpl implements WxCertService {

    private static final String DEFAULT_CERT_NAME = "Certificate";
    private static final String DEFAULT_ISSUE_ORG = "JST Platform";
    private static final String CERT_TABLE = "jst_cert_record";
    private static final String TEMPLATE_TABLE = "jst_cert_template";
    private static final String ENROLL_TABLE = "jst_enroll_record";
    private static final String CONTEST_TABLE = "jst_contest";
    private static final String SCORE_TABLE = "jst_score_record";
    private static final String PARTICIPANT_TABLE = "jst_participant";

    @Autowired
    private WxCertMapper wxCertMapper;

    @Autowired
    private SchemaInspector schemaInspector;

    @Override
    public List<WxCertVO> selectMyCerts(Long userId) {
        if (userId == null || !canUseCertTable()) {
            return Collections.emptyList();
        }
        List<WxCertVO> list = wxCertMapper.selectMyCertList(userId);
        if (list == null) {
            return Collections.emptyList();
        }
        for (WxCertVO vo : list) {
            if (StringUtils.isBlank(vo.getCertName())) {
                vo.setCertName(DEFAULT_CERT_NAME);
            }
            if (vo.getIssueDate() == null) {
                vo.setIssueDate(vo.getIssueTime());
            }
            if (StringUtils.isBlank(vo.getCertImageUrl())) {
                vo.setCertImageUrl(vo.getCertFileUrl());
            }
        }
        return list;
    }

    @Override
    public WxCertDetailVO selectCertDetail(Long userId, Long certId) {
        if (userId == null || certId == null || !canUseCertDetail()) {
            return null;
        }
        WxCertDetailVO vo = wxCertMapper.selectCertDetail(userId, certId);
        if (vo == null) {
            throw new ServiceException(BizErrorCode.JST_EVENT_CERT_NOT_FOUND.message(),
                    BizErrorCode.JST_EVENT_CERT_NOT_FOUND.code());
        }
        if (StringUtils.isBlank(vo.getTemplateName())) {
            vo.setTemplateName(DEFAULT_CERT_NAME);
        }
        if (vo.getIssueDate() == null) {
            vo.setIssueDate(vo.getIssueTime());
        }
        if (StringUtils.isBlank(vo.getCertImageUrl())) {
            vo.setCertImageUrl(vo.getCertFileUrl());
        }
        if (StringUtils.isBlank(vo.getIssueOrg())) {
            vo.setIssueOrg(DEFAULT_ISSUE_ORG);
        }
        if ("issued".equalsIgnoreCase(vo.getIssueStatus())) {
            vo.setIssueStatus("granted");
        }

        Map<String, Object> formData = extractFormData(vo.getFormSnapshotJson());
        vo.setHolderName(firstNonBlank(
                vo.getHolderName(),
                asText(formData.get("name")),
                asText(formData.get("studentName")),
                asText(formData.get("participantName")),
                asText(formData.get("realName"))));
        vo.setVariables(buildVariables(vo, formData));
        return vo;
    }

    @Override
    public PublicCertVerifyVO verifyPublicCert(PublicCertVerifyQueryDTO query) {
        if (query == null || !canUseCertVerify()) {
            return null;
        }
        query.setCertNo(trimToNull(query.getCertNo()));
        query.setVerifyCode(trimToNull(query.getVerifyCode()));
        if (StringUtils.isBlank(query.getCertNo()) && StringUtils.isBlank(query.getVerifyCode())) {
            return null;
        }
        PublicCertVerifyVO vo = wxCertMapper.selectPublicCertVerify(query);
        if (vo == null) {
            return null;
        }
        boolean valid = "issued".equalsIgnoreCase(vo.getIssueStatus())
                || "granted".equalsIgnoreCase(vo.getIssueStatus());
        if (!valid) {
            return null;
        }
        vo.setValid(Boolean.TRUE);
        vo.setHolderName(MaskUtil.realName(vo.getHolderName()));
        if (vo.getIssueDate() == null) {
            vo.setIssueDate(vo.getIssueTime());
        }
        if (StringUtils.isBlank(vo.getIssueOrg())) {
            vo.setIssueOrg(DEFAULT_ISSUE_ORG);
        }
        return vo;
    }

    private boolean canUseCertTable() {
        return schemaInspector.hasColumns(CERT_TABLE,
                "cert_id", "cert_no", "contest_id", "score_id", "user_id",
                "participant_id", "cert_file_url", "issue_status", "issue_time", "del_flag")
                && schemaInspector.hasColumns(CONTEST_TABLE, "contest_id", "contest_name", "category", "group_levels", "del_flag")
                && schemaInspector.hasColumns(PARTICIPANT_TABLE, "participant_id", "name", "del_flag")
                && schemaInspector.hasColumns(SCORE_TABLE, "score_id", "award_level", "del_flag");
    }

    private boolean canUseCertDetail() {
        return schemaInspector.hasColumns(CERT_TABLE,
                "cert_id", "cert_no", "contest_id", "score_id", "enroll_id", "user_id",
                "template_id", "cert_file_url", "issue_status", "issue_time", "create_time", "del_flag")
                && schemaInspector.hasColumns(TEMPLATE_TABLE, "template_id", "template_name", "layout_json", "bg_image", "del_flag")
                && schemaInspector.hasColumns(ENROLL_TABLE, "enroll_id", "form_snapshot_json", "del_flag")
                && schemaInspector.hasColumns(CONTEST_TABLE, "contest_id", "contest_name", "category", "group_levels", "del_flag")
                && schemaInspector.hasColumns(SCORE_TABLE, "score_id", "award_level", "score_value", "del_flag");
    }

    private boolean canUseCertVerify() {
        return canUseCertTable() && schemaInspector.hasColumns(CERT_TABLE, "verify_code");
    }

    private Map<String, String> buildVariables(WxCertDetailVO vo, Map<String, Object> formData) {
        Map<String, String> variables = new LinkedHashMap<>();
        variables.put("name", defaultString(firstNonBlank(
                vo.getHolderName(),
                asText(formData.get("name")),
                asText(formData.get("studentName")),
                asText(formData.get("participantName")),
                asText(formData.get("realName")))));
        variables.put("contestName", defaultString(vo.getContestName()));
        variables.put("awardName", defaultString(vo.getAwardLevel()));
        variables.put("score", formatScore(vo.getScoreValue()));
        variables.put("certNo", defaultString(vo.getCertNo()));
        Date certDate = vo.getIssueDate() != null ? vo.getIssueDate() : vo.getCreateTime();
        variables.put("date", formatChineseDate(certDate));
        variables.put("school", defaultString(firstNonBlank(
                asText(formData.get("school")),
                asText(formData.get("schoolName")))));
        variables.put("groupLevel", defaultString(vo.getGroupLevel()));
        return variables;
    }

    private Map<String, Object> extractFormData(String formSnapshotJson) {
        if (StringUtils.isBlank(formSnapshotJson)) {
            return Collections.emptyMap();
        }
        try {
            Object parsed = JSON.parse(formSnapshotJson);
            if (!(parsed instanceof Map<?, ?> snapshotMap)) {
                return Collections.emptyMap();
            }
            Object formData = snapshotMap.get("formData");
            if (!(formData instanceof Map<?, ?> formDataMap)) {
                return Collections.emptyMap();
            }
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : formDataMap.entrySet()) {
                if (entry.getKey() != null) {
                    result.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }
            return result;
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }

    private String formatChineseDate(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy年M月d日").format(date);
    }

    private String formatScore(BigDecimal scoreValue) {
        if (scoreValue == null) {
            return "";
        }
        return scoreValue.stripTrailingZeros().toPlainString();
    }

    private String defaultString(String text) {
        return StringUtils.defaultString(text);
    }

    private String asText(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        return StringUtils.isBlank(text) ? null : text.trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String trimToNull(String value) {
        String trimmed = StringUtils.trim(value);
        return StringUtils.isBlank(trimmed) ? null : trimmed;
    }
}
