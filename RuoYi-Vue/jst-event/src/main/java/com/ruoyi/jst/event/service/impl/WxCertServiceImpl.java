package com.ruoyi.jst.event.service.impl;

import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.jst.common.support.SchemaInspector;
import com.ruoyi.jst.common.util.MaskUtil;
import com.ruoyi.jst.event.dto.PublicCertVerifyQueryDTO;
import com.ruoyi.jst.event.mapper.WxCertMapper;
import com.ruoyi.jst.event.service.WxCertService;
import com.ruoyi.jst.event.vo.PublicCertVerifyVO;
import com.ruoyi.jst.event.vo.WxCertVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Mini-program certificate service implementation.
 */
@Service
public class WxCertServiceImpl implements WxCertService {

    private static final String CERT_TABLE = "jst_cert_record";
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
                vo.setCertName("获奖证书");
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
            vo.setIssueOrg("竞赛通平台");
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

    private boolean canUseCertVerify() {
        return canUseCertTable() && schemaInspector.hasColumns(CERT_TABLE, "verify_code");
    }

    private String trimToNull(String value) {
        String trimmed = StringUtils.trim(value);
        return StringUtils.isBlank(trimmed) ? null : trimmed;
    }
}
